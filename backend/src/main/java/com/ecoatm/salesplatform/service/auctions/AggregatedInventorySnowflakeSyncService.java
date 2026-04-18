package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventorySyncResult;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.WeekSyncWatermark;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.WeekSyncWatermarkRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryReader;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryRow;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeReadException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates a single pull of Snowflake's aggregated-inventory snapshot into
 * {@code auctions.aggregated_inventory}.
 *
 * <p>Phase 4 of {@code docs/tasks/aggregated-inventory-snowflake-sync-plan.md}.
 * Algorithm (plan §3.3 / §6 test matrix):
 * <ol>
 *   <li>Look up the week → fail fast if missing.</li>
 *   <li>Lock guard: if {@code auctions.auctions} already has a row for this
 *       week, skip (log {@code SKIPPED_LOCKED}) so active bid rounds see
 *       stable numbers.</li>
 *   <li>Open a {@code STARTED} audit row.</li>
 *   <li>Watermark: ask Snowflake for {@code MAX(Upload_Time)}. If absent →
 *       write {@code COMPLETED 0/0} and return. If our stored watermark is
 *       already ≥ that, write {@code SKIPPED_UP_TO_DATE}.</li>
 *   <li>Otherwise iterate pages (1k rows each) and upsert with the admin-
 *       override-preserving SQL below.</li>
 *   <li>Persist the new watermark and mark the log {@code COMPLETED}.</li>
 * </ol>
 *
 * <p><strong>Log durability on failure.</strong> Log writes use
 * {@link SyncLogWriter} whose methods run with {@code REQUIRES_NEW} so a
 * rolled-back outer sync tx still leaves a {@code FAILED} row behind. If the
 * write path throws, the catch in {@link #syncWeek} flips the log and returns
 * {@code FAILED} — the exception is <em>not</em> rethrown, so callers always
 * get a result object.
 *
 * <p><strong>Feature flag.</strong> Gated on {@code snowflake.enabled=true}
 * (same gate as the reader). When disabled, the bean is absent and the
 * controller short-circuits with {@link AggregatedInventorySyncResult#skippedDisabled()}
 * before any code here is reached. Plan §6.
 */
@Service
@ConditionalOnProperty(name = "snowflake.enabled", havingValue = "true")
public class AggregatedInventorySnowflakeSyncService {

    private static final Logger log =
            LoggerFactory.getLogger(AggregatedInventorySnowflakeSyncService.class);

    public static final String SOURCE = "SNOWFLAKE_AGG_INVENTORY";
    public static final String SYNC_TYPE = "SNOWFLAKE_AGG_INVENTORY";

    /**
     * Canonical upsert. The {@code CASE WHEN is_total_quantity_modified} guard
     * is the Mendix-parity bit: admin edits to {@code total_quantity} /
     * {@code dw_total_quantity} are preserved even when Snowflake reports a
     * different value. Every other non-identity column is refreshed from
     * {@code EXCLUDED}. Rows marked {@code is_deprecated=true} are ignored by
     * both the unique partial index (see V68) and the WHERE clause.
     *
     * <p>Parameter order (20 positional parameters — {@code created_date} /
     * {@code changed_date} are NOW()) must stay in lockstep with
     * {@link #bindRow}. Mis-binding is the most likely regression here.
     */
    static final String UPSERT_SQL = """
            INSERT INTO auctions.aggregated_inventory (
                ecoid2, merged_grade, datawipe, week_id,
                name, model, brand, carrier, category, device_id,
                avg_target_price, avg_payout, total_payout, total_quantity,
                round1_target_price, round1_target_price_dw,
                dw_avg_target_price, dw_avg_payout, dw_total_payout, dw_total_quantity,
                created_date, changed_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                      ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            ON CONFLICT (ecoid2, merged_grade, datawipe, week_id) WHERE is_deprecated = false
            DO UPDATE SET
                name = EXCLUDED.name,
                model = EXCLUDED.model,
                brand = EXCLUDED.brand,
                carrier = EXCLUDED.carrier,
                category = EXCLUDED.category,
                device_id = EXCLUDED.device_id,
                avg_target_price = EXCLUDED.avg_target_price,
                avg_payout = EXCLUDED.avg_payout,
                total_payout = EXCLUDED.total_payout,
                total_quantity = CASE
                    WHEN auctions.aggregated_inventory.is_total_quantity_modified THEN auctions.aggregated_inventory.total_quantity
                    ELSE EXCLUDED.total_quantity
                END,
                dw_total_quantity = CASE
                    WHEN auctions.aggregated_inventory.is_total_quantity_modified THEN auctions.aggregated_inventory.dw_total_quantity
                    ELSE EXCLUDED.dw_total_quantity
                END,
                round1_target_price = EXCLUDED.round1_target_price,
                round1_target_price_dw = EXCLUDED.round1_target_price_dw,
                dw_avg_target_price = EXCLUDED.dw_avg_target_price,
                dw_avg_payout = EXCLUDED.dw_avg_payout,
                dw_total_payout = EXCLUDED.dw_total_payout,
                changed_date = NOW()
            WHERE auctions.aggregated_inventory.is_deprecated = false
            """;

    private final SnowflakeAggInventoryReader reader;
    private final WeekRepository weekRepository;
    private final WeekSyncWatermarkRepository watermarkRepository;
    private final AggregatedInventoryRepository aggregatedInventoryRepository;
    private final SyncLogWriter syncLogWriter;
    private final JdbcTemplate jdbcTemplate;

    public AggregatedInventorySnowflakeSyncService(
            SnowflakeAggInventoryReader reader,
            WeekRepository weekRepository,
            WeekSyncWatermarkRepository watermarkRepository,
            AggregatedInventoryRepository aggregatedInventoryRepository,
            SyncLogWriter syncLogWriter,
            JdbcTemplate jdbcTemplate) {
        this.reader = reader;
        this.weekRepository = weekRepository;
        this.watermarkRepository = watermarkRepository;
        this.aggregatedInventoryRepository = aggregatedInventoryRepository;
        this.syncLogWriter = syncLogWriter;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Run one sync for the given week. See class Javadoc for the algorithm;
     * see {@code docs/tasks/aggregated-inventory-snowflake-sync-plan.md} §6
     * for the test matrix this method satisfies.
     *
     * @param weekId PK of {@code mdm.week}
     * @param triggeredBy free-form label threaded into logs — typically the
     *                    email of the admin who triggered the run, or the
     *                    scheduler name
     * @return a {@link AggregatedInventorySyncResult} capturing the outcome —
     *         exceptions from Snowflake or the DB are caught and surfaced as
     *         {@code FAILED}, never rethrown
     * @throws EntityNotFoundException if {@code weekId} does not exist in
     *         {@code mdm.week}
     */
    @Transactional
    public AggregatedInventorySyncResult syncWeek(long weekId, String triggeredBy) {
        long startNs = System.nanoTime();
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("week", weekId));

        int auctionWeek = week.getWeekNumber();
        int auctionYear = week.getYear();
        String targetKey = String.valueOf(weekId);

        // 1. Lock guard — no log row created by the service, writeSkippedLocked
        // creates a single terminal row.
        if (aggregatedInventoryRepository.existsAuctionForWeek(weekId)) {
            Long logId = syncLogWriter.writeSkippedLocked(SYNC_TYPE, targetKey);
            log.info("syncWeek weekId={} SKIPPED_LOCKED triggeredBy={}", weekId, triggeredBy);
            return AggregatedInventorySyncResult.skippedLocked(logId);
        }

        // 2. STARTED audit row — guarantees a breadcrumb even if we crash
        // before reaching the terminal mark* call.
        Long logId = syncLogWriter.createStarted(SYNC_TYPE, targetKey);

        try {
            return runSync(weekId, auctionWeek, auctionYear, triggeredBy, logId, startNs);
        } catch (SnowflakeReadException | DataAccessException ex) {
            return handleFailure(logId, ex, startNs);
        } catch (RuntimeException ex) {
            // Defensive: anything unexpected still gets an audit row.
            return handleFailure(logId, ex, startNs);
        }
    }

    private AggregatedInventorySyncResult runSync(
            long weekId, int auctionWeek, int auctionYear,
            String triggeredBy, Long logId, long startNs) {
        // 3. Watermark. If Snowflake has no snapshot yet, there is nothing
        // to do — mark COMPLETED with zero counts so the audit trail is
        // correct and the caller can show "in sync".
        Optional<Instant> sfMaxOpt = reader.readWatermark(auctionWeek, auctionYear);
        if (sfMaxOpt.isEmpty()) {
            syncLogWriter.markCompleted(logId, 0, 0);
            log.info("syncWeek weekId={} COMPLETED (no snowflake watermark) triggeredBy={}",
                    weekId, triggeredBy);
            return AggregatedInventorySyncResult.completed(0, 0, elapsedMs(startNs), logId);
        }
        Instant sfMax = sfMaxOpt.get();

        Optional<WeekSyncWatermark> existing =
                watermarkRepository.findByWeekIdAndSource(weekId, SOURCE);
        if (existing.isPresent()
                && !existing.get().getLastSourceUploadAt().isBefore(sfMax)) {
            // Our watermark is ≥ Snowflake's → no new data.
            syncLogWriter.markSkippedUpToDate(logId);
            log.info("syncWeek weekId={} SKIPPED_UP_TO_DATE triggeredBy={}", weekId, triggeredBy);
            return AggregatedInventorySyncResult.skippedUpToDate(logId, elapsedMs(startNs));
        }

        // 4. Iterate pages and batch-upsert. weekId is bound on each row —
        // the Snowflake rows carry (auctionWeek, auctionYear) but not the
        // Postgres FK, so the service threads it through.
        int rowsRead = 0;
        int rowsUpserted = 0;
        for (List<SnowflakeAggInventoryRow> page : reader.iterateAllPages(auctionWeek, auctionYear)) {
            if (page.isEmpty()) {
                continue;
            }
            rowsRead += page.size();
            rowsUpserted += upsertPage(page, weekId);
        }

        // 5. Persist the new watermark (insert or update — rely on JPA merge
        // semantics via save()).
        persistWatermark(weekId, sfMax, rowsUpserted, existing);

        // 6. Terminal audit row update.
        syncLogWriter.markCompleted(logId, rowsRead, rowsUpserted);
        log.info("syncWeek weekId={} COMPLETED rowsRead={} rowsUpserted={} triggeredBy={}",
                weekId, rowsRead, rowsUpserted, triggeredBy);
        return AggregatedInventorySyncResult.completed(
                rowsRead, rowsUpserted, elapsedMs(startNs), logId);
    }

    private int upsertPage(List<SnowflakeAggInventoryRow> page, long weekId) {
        int[] affected = jdbcTemplate.batchUpdate(UPSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                bindRow(ps, page.get(index), weekId);
            }

            @Override
            public int getBatchSize() {
                return page.size();
            }
        });
        return sumAffected(affected, page.size());
    }

    /**
     * Sum the return values from {@code batchUpdate}. Postgres returns
     * {@code 1} per upserted row in the common case, but drivers may return
     * {@code Statement.SUCCESS_NO_INFO} (-2) when they can't count. In that
     * case the most honest thing is to treat the page as fully written —
     * {@code rowsUpserted} is an audit counter, not an ACK, and a short
     * count here would misrepresent a successful batch.
     */
    private static int sumAffected(int[] affected, int pageSize) {
        int sum = 0;
        boolean sawUnknown = false;
        for (int n : affected) {
            if (n == PreparedStatement.SUCCESS_NO_INFO || n == -2) {
                sawUnknown = true;
                continue;
            }
            if (n > 0) {
                sum += n;
            }
        }
        return sawUnknown && sum < pageSize ? pageSize : sum;
    }

    private void persistWatermark(
            long weekId, Instant sfMax, int rowsUpserted,
            Optional<WeekSyncWatermark> existing) {
        WeekSyncWatermark wm = existing.orElseGet(WeekSyncWatermark::new);
        wm.setWeekId(weekId);
        wm.setSource(SOURCE);
        wm.setLastSourceUploadAt(sfMax);
        wm.setLastSyncedAt(Instant.now());
        wm.setRowCount(rowsUpserted);
        watermarkRepository.save(wm);
    }

    private AggregatedInventorySyncResult handleFailure(
            Long logId, Exception ex, long startNs) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        log.error("syncWeek logId={} FAILED: {}", logId, message, ex);
        // REQUIRES_NEW ensures this write commits even though the outer tx
        // rolls back. Without this the failure would be silent.
        syncLogWriter.markFailed(logId, message);
        return AggregatedInventorySyncResult.failed(logId, elapsedMs(startNs), message);
    }

    private static long elapsedMs(long startNs) {
        return (System.nanoTime() - startNs) / 1_000_000L;
    }

    /**
     * Bind one {@link SnowflakeAggInventoryRow} to the 20 positional
     * parameters in {@link #UPSERT_SQL}. Order matches the VALUES clause
     * exactly — do not reorder without updating the SQL. Null {@code BigDecimal}
     * fields are bound via {@code setNull(..., NUMERIC)} because
     * {@link PreparedStatement#setBigDecimal} on a null value maps to
     * {@code setObject(..., null)} which some drivers reject for typed
     * columns. {@code weekId} is threaded through from the service because the
     * Snowflake row carries auction-week/year context but not the Postgres
     * FK.
     */
    static void bindRow(PreparedStatement ps, SnowflakeAggInventoryRow row, long weekId)
            throws SQLException {
        int i = 1;
        ps.setString(i++, row.ecoId());
        setStringNullable(ps, i++, row.mergedGrade());
        ps.setBoolean(i++, row.datawipe());
        ps.setLong(i++, weekId);
        setStringNullable(ps, i++, row.name());
        setStringNullable(ps, i++, row.model());
        setStringNullable(ps, i++, row.brand());
        setStringNullable(ps, i++, row.carrier());
        setStringNullable(ps, i++, row.category());
        setStringNullable(ps, i++, row.deviceId());
        setDecimalNullable(ps, i++, row.avgTargetPrice());
        setDecimalNullable(ps, i++, row.avgPayout());
        setDecimalNullable(ps, i++, row.totalPayout());
        ps.setInt(i++, row.totalQuantity());
        setDecimalNullable(ps, i++, row.round1TargetPrice());
        setDecimalNullable(ps, i++, row.round1TargetPriceDw());
        setDecimalNullable(ps, i++, row.dwAvgTargetPrice());
        setDecimalNullable(ps, i++, row.dwAvgPayout());
        setDecimalNullable(ps, i++, row.dwTotalPayout());
        ps.setInt(i, row.dwTotalQuantity());
    }

    private static void setStringNullable(PreparedStatement ps, int index, String value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }
    }

    private static void setDecimalNullable(PreparedStatement ps, int index, BigDecimal value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.NUMERIC);
        } else {
            ps.setBigDecimal(index, value);
        }
    }
}
