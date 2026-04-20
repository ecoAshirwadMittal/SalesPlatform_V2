package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Paginated JDBC reader for the Snowflake aggregated-inventory query.
 *
 * <p>Phase 3 of {@code docs/tasks/aggregated-inventory-snowflake-sync-plan.md}.
 * This component is read-only — it never writes to Snowflake. Gated on
 * {@code snowflake.enabled=true} so the application continues to boot in
 * environments without Snowflake configured (matches the
 * {@code SnowflakeDataSourceConfig} gate).
 *
 * <p>Two queries back the reader:
 * <ul>
 *   <li>{@link #WATERMARK_QUERY} — the latest {@code Upload_Time} for the
 *       requested auction week/year; used by the sync service to decide
 *       whether a new run is needed.</li>
 *   <li>{@link #DATA_QUERY} — a single page of aggregated rows, ordered
 *       deterministically and sliced with {@code OFFSET / FETCH NEXT} so
 *       pagination is stable across calls.</li>
 * </ul>
 *
 * <p>The data query deliberately omits the legacy {@code UNION ALL} totals
 * block. Per ADR 2026-04-17 the Postgres layer recomputes totals on read, so
 * storing them would introduce drift. The {@code DEVICE_ID='Total'} filter in
 * {@link #readPage} is kept as a defensive measure in case the upstream SQL
 * is ever restored to include a totals row.
 *
 * <p>Timestamp handling: Snowflake returns {@code TIMESTAMP_NTZ} values which
 * have no zone information. We materialize them as {@link Instant} using a
 * UTC {@link Calendar} so behavior is independent of JVM default timezone.
 */
@Component
@ConditionalOnProperty(name = "snowflake.enabled", havingValue = "true")
public final class SnowflakeAggInventoryReader {

    private static final Logger log = LoggerFactory.getLogger(SnowflakeAggInventoryReader.class);

    /**
     * Page size matches Mendix {@code CONST_SF_QueryPageSize} so sync
     * behavior is predictable for anyone comparing legacy and migrated
     * runs. Exposed for the service so it can assert invariants (e.g.
     * "short page means we're done").
     */
    public static final int PAGE_SIZE = 1000;

    private static final String DW_MARKER = "DW";
    private static final String TOTAL_DEVICE_ID = "Total";

    private static final Calendar UTC_CALENDAR =
            Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    /**
     * Parameter order (6 params):
     *   1 = auctionWeek  (MaxUploadInventory CTE)
     *   2 = auctionYear  (MaxUploadInventory CTE)
     *   3 = auctionYear  (AggregatedData filter)
     *   4 = auctionWeek  (AggregatedData filter)
     *   5 = offset       (OFFSET n ROWS)
     *   6 = fetch        (FETCH NEXT n ROWS ONLY)
     *
     * <p>Callers MUST use {@link #bindDataQueryParams} so the order stays
     * consistent with the SQL above. Do NOT inline parameter setters — the
     * duplicated week/year pair is a common mis-bind.
     */
    private static final String DATA_QUERY = """
            WITH MaxUploadInventory AS (
                SELECT MAX(Upload_Time) AS MaxUploadTime
                FROM Master_Inventory_List_Snapshot
                WHERE Auction_Week = ? AND Auction_Year = ?
            ),
            AggregatedData AS (
                SELECT
                    current_device_ecoatm_code AS ecoID,
                    MG,
                    CASE WHEN SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN 1 ELSE 0 END) > 0
                         THEN 'DW' ELSE '' END AS DataWipe,
                    (SELECT MaxUploadTime FROM MaxUploadInventory) AS MaxUploadTime,
                    COALESCE(
                        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN target_price END), 2),
                        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END), 2)
                    ) AS AvgTargetPrice,
                    COALESCE(
                        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = '' THEN payout END), 2),
                        ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2)
                    ) AS AvgPayout,
                    COALESCE(
                        ROUND(SUM(CASE WHEN Data_WIPE_GOOD = '' THEN payout END), 2),
                        ROUND(SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2)
                    ) AS TotalPayout,
                    COUNT(*) AS TotalQuantity,
                    ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN target_price END), 2) AS DWAvgTargetPrice,
                    ROUND(AVG(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2) AS DWAvgPayout,
                    ROUND(SUM(CASE WHEN Data_WIPE_GOOD = 'DW' THEN payout END), 2) AS DWTotalPayout,
                    COUNT(CASE WHEN Data_WIPE_GOOD = 'DW' THEN 1 END) AS DWTotalQuantity
                FROM Master_Inventory_List_Snapshot
                WHERE Auction_Year = ? AND Auction_Week = ?
                  AND Upload_Time = (SELECT MaxUploadTime FROM MaxUploadInventory)
                GROUP BY current_device_ecoatm_code, MG
            ),
            DimDeviceFiltered AS (
                SELECT DISTINCT
                    DEVICE_ID, ecoatm_code, name,
                    device_brand AS brand, device_category AS category,
                    device_carrier AS carrier,
                    CREATED_AT::DATE AS created_at,
                    device_model AS model
                FROM dim_device
            )
            SELECT
                agg.ecoID, dd.DEVICE_ID, agg.MG, agg.DataWipe, agg.MaxUploadTime,
                agg.AvgTargetPrice, agg.AvgPayout, agg.TotalPayout, agg.TotalQuantity,
                agg.DWAvgTargetPrice, agg.DWAvgPayout, agg.DWTotalPayout, agg.DWTotalQuantity,
                agg.AvgTargetPrice AS ROUND1TARGETPRICE,
                agg.DWAvgTargetPrice AS ROUND1TARGETPRICE_DW,
                dd.name, dd.model, dd.brand, dd.carrier, dd.category, dd.created_at
            FROM AggregatedData agg
            LEFT OUTER JOIN DimDeviceFiltered dd ON agg.ecoID = dd.ecoatm_code
            ORDER BY ecoID, DEVICE_ID, MG, DataWipe
            OFFSET ? ROWS
            FETCH NEXT ? ROWS ONLY
            """;

    private static final String WATERMARK_QUERY = """
            SELECT MAX(Upload_Time) AS MAX_UPLOAD_TIME
            FROM Master_Inventory_List_Snapshot
            WHERE Auction_Week = ? AND Auction_Year = ?
            """;

    private final DataSource snowflakeDataSource;

    public SnowflakeAggInventoryReader(
            @Qualifier("snowflakeDataSource") DataSource snowflakeDataSource) {
        this.snowflakeDataSource = snowflakeDataSource;
    }

    /**
     * Return Snowflake's max {@code Upload_Time} for the given auction week/year,
     * or {@link Optional#empty()} when no snapshot exists yet.
     */
    public Optional<Instant> readWatermark(int auctionWeek, int auctionYear) {
        try (Connection conn = snowflakeDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(WATERMARK_QUERY)) {
            ps.setInt(1, auctionWeek);
            ps.setInt(2, auctionYear);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    log.debug("readWatermark week={} year={} result={}", auctionWeek, auctionYear, null);
                    return Optional.empty();
                }
                Instant watermark = readUtcTimestamp(rs, 1);
                Optional<Instant> result = Optional.ofNullable(watermark);
                log.debug("readWatermark week={} year={} result={}",
                        auctionWeek, auctionYear, result.orElse(null));
                return result;
            }
        } catch (SQLException ex) {
            throw new SnowflakeReadException(
                    "Failed to read watermark for week=" + auctionWeek
                            + " year=" + auctionYear, ex);
        }
    }

    /**
     * Read a single page of aggregated rows starting at {@code offset}. Returns
     * up to {@link #PAGE_SIZE} rows. A result size strictly less than
     * {@code PAGE_SIZE} signals end-of-cursor to the caller.
     *
     * <p>Rows where {@code DEVICE_ID='Total'} are filtered defensively — the
     * current DATA_QUERY does not emit them, but future SQL tweaks might.
     */
    public List<SnowflakeAggInventoryRow> readPage(int auctionWeek, int auctionYear, int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0, got " + offset);
        }
        try (Connection conn = snowflakeDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DATA_QUERY)) {
            bindDataQueryParams(ps, auctionWeek, auctionYear, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<SnowflakeAggInventoryRow> rows = new ArrayList<>(PAGE_SIZE);
                while (rs.next()) {
                    String deviceId = rs.getString("DEVICE_ID");
                    if (TOTAL_DEVICE_ID.equals(deviceId)) {
                        continue;
                    }
                    rows.add(mapRow(rs));
                }
                log.debug("readPage week={} year={} offset={} rows={}",
                        auctionWeek, auctionYear, offset, rows.size());
                return rows;
            }
        } catch (SQLException ex) {
            throw new SnowflakeReadException(
                    "Failed to read page offset=" + offset
                            + " week=" + auctionWeek + " year=" + auctionYear, ex);
        }
    }

    /**
     * Iterate all pages for the given auction week/year, yielding one page
     * per {@link Iterator#next()} call. The iterator is <em>lazy</em> — each
     * {@code next()} opens a new connection, runs one page query, closes the
     * connection. The caller batches rows into downstream JDBC upserts and
     * never needs to hold more than one page (~1k rows) in memory.
     *
     * <p>Stops when a page comes back with fewer than {@link #PAGE_SIZE} rows
     * (after the {@code DEVICE_ID='Total'} filter is applied). An empty first
     * page yields an iterator with a single empty list — callers that care
     * about "no rows at all" should check {@code isEmpty()} on the first page.
     */
    public Iterable<List<SnowflakeAggInventoryRow>> iterateAllPages(int auctionWeek, int auctionYear) {
        return () -> new PageIterator(auctionWeek, auctionYear);
    }

    /**
     * Lazy page iterator over {@link #readPage} results.
     *
     * <p><strong>End-of-data detection.</strong> The iterator stops on the
     * first short page (a page with fewer than {@link #PAGE_SIZE} rows
     * after the {@code DEVICE_ID='Total'} filter). At exact multiples of
     * {@code PAGE_SIZE} this means one trailing zero-row query is made to
     * detect end-of-data — there is no cheaper way to know the last full
     * page was actually the last without issuing a {@code COUNT(*)} on
     * Snowflake first, which is slower than a bounded OFFSET/FETCH probe.
     * This trailing empty page is <em>intentional</em> and is suppressed
     * from the iterator's output — {@code hasNext()} returns {@code false}
     * and callers never observe a phantom iteration.
     *
     * <p><strong>Empty first page.</strong> When the very first page comes
     * back empty ({@code offset == 0} and no rows), the iterator yields a
     * single empty list. Callers that want to distinguish "no rows at all"
     * from "consumed all rows" should inspect the first element returned
     * by {@link Iterator#next()}.
     */
    private final class PageIterator implements Iterator<List<SnowflakeAggInventoryRow>> {
        private final int auctionWeek;
        private final int auctionYear;
        private int offset = 0;
        private boolean exhausted = false;
        private List<SnowflakeAggInventoryRow> buffered;

        PageIterator(int auctionWeek, int auctionYear) {
            this.auctionWeek = auctionWeek;
            this.auctionYear = auctionYear;
        }

        @Override
        public boolean hasNext() {
            if (buffered != null) {
                return true;
            }
            if (exhausted) {
                return false;
            }
            List<SnowflakeAggInventoryRow> page = readPage(auctionWeek, auctionYear, offset);
            buffered = page;
            // Short page (< PAGE_SIZE) signals end-of-cursor. A full-sized
            // page may still be the last if the total row count is an exact
            // multiple of PAGE_SIZE — in that case the next call returns an
            // empty list, which we treat as hasNext=false.
            if (page.size() < PAGE_SIZE) {
                exhausted = true;
            } else {
                offset += PAGE_SIZE;
            }
            if (page.isEmpty() && offset > 0) {
                // Trailing empty page at an exact PAGE_SIZE boundary — the
                // previous iteration already returned the final non-empty
                // page. See class Javadoc: this zero-row query is the
                // unavoidable cost of end-of-data detection without a
                // COUNT(*), and it must NOT be surfaced to the caller.
                buffered = null;
                return false;
            }
            return true;
        }

        @Override
        public List<SnowflakeAggInventoryRow> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            List<SnowflakeAggInventoryRow> out = buffered;
            buffered = null;
            return out;
        }
    }

    // ---- helpers ----

    private static void bindDataQueryParams(
            PreparedStatement ps, int auctionWeek, int auctionYear, int offset)
            throws SQLException {
        ps.setInt(1, auctionWeek);
        ps.setInt(2, auctionYear);
        ps.setInt(3, auctionYear);
        ps.setInt(4, auctionWeek);
        ps.setInt(5, offset);
        ps.setInt(6, PAGE_SIZE);
    }

    private static SnowflakeAggInventoryRow mapRow(ResultSet rs) throws SQLException {
        String ecoId = rs.getString("ecoID");
        String deviceId = rs.getString("DEVICE_ID");
        String mergedGrade = rs.getString("MG");
        boolean datawipe = DW_MARKER.equals(rs.getString("DataWipe"));
        Instant maxUploadTime = readUtcTimestamp(rs, "MaxUploadTime");

        BigDecimal avgTargetPrice = rs.getBigDecimal("AvgTargetPrice");
        BigDecimal avgPayout = rs.getBigDecimal("AvgPayout");
        BigDecimal totalPayout = rs.getBigDecimal("TotalPayout");
        int totalQuantity = rs.getInt("TotalQuantity");
        BigDecimal dwAvgTargetPrice = rs.getBigDecimal("DWAvgTargetPrice");
        BigDecimal dwAvgPayout = rs.getBigDecimal("DWAvgPayout");
        BigDecimal dwTotalPayout = rs.getBigDecimal("DWTotalPayout");
        int dwTotalQuantity = rs.getInt("DWTotalQuantity");
        BigDecimal round1TargetPrice = rs.getBigDecimal("ROUND1TARGETPRICE");
        BigDecimal round1TargetPriceDw = rs.getBigDecimal("ROUND1TARGETPRICE_DW");

        String name = rs.getString("name");
        String model = rs.getString("model");
        String brand = rs.getString("brand");
        String carrier = rs.getString("carrier");
        String category = rs.getString("category");
        Instant createdAt = readUtcTimestamp(rs, "created_at");

        return new SnowflakeAggInventoryRow(
                ecoId, deviceId, mergedGrade, datawipe, maxUploadTime,
                avgTargetPrice, avgPayout, totalPayout, totalQuantity,
                dwAvgTargetPrice, dwAvgPayout, dwTotalPayout, dwTotalQuantity,
                round1TargetPrice, round1TargetPriceDw,
                name, model, brand, carrier, category, createdAt);
    }

    private static Instant readUtcTimestamp(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName, (Calendar) UTC_CALENDAR.clone());
        return ts == null ? null : ts.toInstant();
    }

    private static Instant readUtcTimestamp(ResultSet rs, int columnIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnIndex, (Calendar) UTC_CALENDAR.clone());
        return ts == null ? null : ts.toInstant();
    }
}
