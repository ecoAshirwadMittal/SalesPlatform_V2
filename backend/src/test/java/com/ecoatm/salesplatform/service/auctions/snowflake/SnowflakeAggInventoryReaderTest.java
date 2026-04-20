package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SnowflakeAggInventoryReader}.
 *
 * <p>Real Snowflake is not available in CI; the test stubs the entire JDBC
 * stack with Mockito. A {@code RowSetProvider} alternative was considered but
 * rejected — Snowflake returns {@code TIMESTAMP_NTZ} values via the
 * {@code getTimestamp(col, Calendar)} overload and the default {@code CachedRowSet}
 * does not honor per-call calendars, which would obscure the UTC conversion
 * contract we specifically want to verify.
 */
class SnowflakeAggInventoryReaderTest {

    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private SnowflakeAggInventoryReader reader;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        reader = new SnowflakeAggInventoryReader(dataSource);
    }

    // --- helpers ---

    /**
     * Configure {@link #resultSet} to iterate through the supplied rows in
     * order. The row cursor advances on each {@code next()} call rather than
     * on a particular column accessor so the stub is independent of column
     * access order inside {@code mapRow}.
     */
    private void stubRows(List<RowStub> rows) throws SQLException {
        final RowStub[] currentRow = new RowStub[1];
        final int[] index = new int[] {-1};

        when(resultSet.next()).thenAnswer(inv -> {
            index[0]++;
            if (index[0] < rows.size()) {
                currentRow[0] = rows.get(index[0]);
                return true;
            }
            currentRow[0] = null;
            return false;
        });

        when(resultSet.getString("DEVICE_ID")).thenAnswer(inv -> currentRow[0].deviceId);
        when(resultSet.getString("ecoID")).thenAnswer(inv -> currentRow[0].ecoId);
        when(resultSet.getString("MG")).thenAnswer(inv -> currentRow[0].mergedGrade);
        when(resultSet.getString("DataWipe")).thenAnswer(inv -> currentRow[0].datawipe);
        when(resultSet.getString("name")).thenAnswer(inv -> currentRow[0].name);
        when(resultSet.getString("model")).thenAnswer(inv -> currentRow[0].model);
        when(resultSet.getString("brand")).thenAnswer(inv -> currentRow[0].brand);
        when(resultSet.getString("carrier")).thenAnswer(inv -> currentRow[0].carrier);
        when(resultSet.getString("category")).thenAnswer(inv -> currentRow[0].category);
        when(resultSet.getBigDecimal("AvgTargetPrice")).thenAnswer(inv -> currentRow[0].avgTargetPrice);
        when(resultSet.getBigDecimal("AvgPayout")).thenAnswer(inv -> currentRow[0].avgPayout);
        when(resultSet.getBigDecimal("TotalPayout")).thenAnswer(inv -> currentRow[0].totalPayout);
        when(resultSet.getInt("TotalQuantity")).thenAnswer(inv -> currentRow[0].totalQuantity);
        when(resultSet.getBigDecimal("DWAvgTargetPrice")).thenAnswer(inv -> currentRow[0].dwAvgTargetPrice);
        when(resultSet.getBigDecimal("DWAvgPayout")).thenAnswer(inv -> currentRow[0].dwAvgPayout);
        when(resultSet.getBigDecimal("DWTotalPayout")).thenAnswer(inv -> currentRow[0].dwTotalPayout);
        when(resultSet.getInt("DWTotalQuantity")).thenAnswer(inv -> currentRow[0].dwTotalQuantity);
        when(resultSet.getBigDecimal("ROUND1TARGETPRICE")).thenAnswer(inv -> currentRow[0].round1TargetPrice);
        when(resultSet.getBigDecimal("ROUND1TARGETPRICE_DW")).thenAnswer(inv -> currentRow[0].round1TargetPriceDw);
        when(resultSet.getTimestamp(eq("MaxUploadTime"), any(Calendar.class)))
                .thenAnswer(inv -> currentRow[0].maxUploadTime);
        when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class)))
                .thenAnswer(inv -> currentRow[0].createdAt);
    }

    private static Timestamp ts(String iso) {
        return Timestamp.from(LocalDateTime.parse(iso).toInstant(ZoneOffset.UTC));
    }

    // --- tests ---

    @Test
    @DisplayName("readPage maps a row's columns into the record correctly")
    void readPage_parsesRowCorrectly() throws SQLException {
        RowStub row = new RowStub();
        row.ecoId = "eco-1";
        row.deviceId = "dev-1";
        row.mergedGrade = "A";
        row.datawipe = "DW";
        row.maxUploadTime = ts("2026-04-10T12:00:00");
        row.avgTargetPrice = new BigDecimal("10.50");
        row.avgPayout = new BigDecimal("8.25");
        row.totalPayout = new BigDecimal("82.50");
        row.totalQuantity = 10;
        row.dwAvgTargetPrice = new BigDecimal("11.00");
        row.dwAvgPayout = new BigDecimal("9.00");
        row.dwTotalPayout = new BigDecimal("90.00");
        row.dwTotalQuantity = 10;
        row.round1TargetPrice = new BigDecimal("10.50");
        row.round1TargetPriceDw = new BigDecimal("11.00");
        row.name = "iPhone X 64GB";
        row.model = "iPhone X";
        row.brand = "Apple";
        row.carrier = "UNLOCKED";
        row.category = "Phone";
        row.createdAt = ts("2024-01-01T00:00:00");

        stubRows(List.of(row));

        List<SnowflakeAggInventoryRow> page = reader.readPage(15, 2026, 0);

        assertThat(page).hasSize(1);
        SnowflakeAggInventoryRow out = page.get(0);
        assertThat(out.ecoId()).isEqualTo("eco-1");
        assertThat(out.deviceId()).isEqualTo("dev-1");
        assertThat(out.mergedGrade()).isEqualTo("A");
        assertThat(out.datawipe()).isTrue();
        assertThat(out.maxUploadTime()).isEqualTo(Instant.parse("2026-04-10T12:00:00Z"));
        assertThat(out.avgTargetPrice()).isEqualByComparingTo("10.50");
        assertThat(out.avgPayout()).isEqualByComparingTo("8.25");
        assertThat(out.totalPayout()).isEqualByComparingTo("82.50");
        assertThat(out.totalQuantity()).isEqualTo(10);
        assertThat(out.dwAvgTargetPrice()).isEqualByComparingTo("11.00");
        assertThat(out.dwAvgPayout()).isEqualByComparingTo("9.00");
        assertThat(out.dwTotalPayout()).isEqualByComparingTo("90.00");
        assertThat(out.dwTotalQuantity()).isEqualTo(10);
        assertThat(out.round1TargetPrice()).isEqualByComparingTo("10.50");
        assertThat(out.round1TargetPriceDw()).isEqualByComparingTo("11.00");
        assertThat(out.name()).isEqualTo("iPhone X 64GB");
        assertThat(out.model()).isEqualTo("iPhone X");
        assertThat(out.brand()).isEqualTo("Apple");
        assertThat(out.carrier()).isEqualTo("UNLOCKED");
        assertThat(out.category()).isEqualTo("Phone");
        assertThat(out.createdAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("readPage filters DEVICE_ID='Total' rows defensively")
    void readPage_filtersTotalRow() throws SQLException {
        RowStub real = new RowStub();
        real.ecoId = "eco-1";
        real.deviceId = "dev-1";
        real.datawipe = "";
        real.name = "iPhone 13 128GB";
        real.model = "iPhone 13";
        real.brand = "Apple";
        real.carrier = "VERIZON";
        real.category = "Phone";

        RowStub totals = new RowStub();
        totals.ecoId = "eco-total";
        totals.deviceId = "Total";
        totals.datawipe = "";

        stubRows(List.of(real, totals));

        List<SnowflakeAggInventoryRow> page = reader.readPage(15, 2026, 0);

        assertThat(page).hasSize(1);
        SnowflakeAggInventoryRow surviving = page.get(0);
        assertThat(surviving.deviceId()).isEqualTo("dev-1");
        assertThat(surviving.datawipe()).isFalse();
        assertThat(surviving.name()).isEqualTo("iPhone 13 128GB");
        assertThat(surviving.model()).isEqualTo("iPhone 13");
        assertThat(surviving.brand()).isEqualTo("Apple");
        assertThat(surviving.carrier()).isEqualTo("VERIZON");
        assertThat(surviving.category()).isEqualTo("Phone");
    }

    @Test
    @DisplayName("readPage binds the 6 query parameters in the documented order")
    void readPage_passesPagingParams() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        reader.readPage(15, 2026, 2000);

        // Parameter order: week, year, year, week, offset, fetch
        verify(preparedStatement).setInt(1, 15);
        verify(preparedStatement).setInt(2, 2026);
        verify(preparedStatement).setInt(3, 2026);
        verify(preparedStatement).setInt(4, 15);
        verify(preparedStatement).setInt(5, 2000);
        verify(preparedStatement).setInt(6, SnowflakeAggInventoryReader.PAGE_SIZE);
        verify(preparedStatement, times(6)).setInt(anyInt(), anyInt());
    }

    @Test
    @DisplayName("readPage rejects negative offset")
    void readPage_rejectsNegativeOffset() {
        assertThatThrownBy(() -> reader.readPage(15, 2026, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("offset");
    }

    @Test
    @DisplayName("readPage wraps SQLException in SnowflakeReadException")
    void readPage_wrapsSqlException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("boom"));

        assertThatThrownBy(() -> reader.readPage(15, 2026, 0))
                .isInstanceOf(SnowflakeReadException.class)
                .hasMessageContaining("week=15")
                .hasMessageContaining("year=2026")
                .hasMessageContaining("offset=0")
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    @DisplayName("readWatermark returns an Instant when a timestamp is present")
    void readWatermark_returnsInstant() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getTimestamp(eq(1), any(Calendar.class)))
                .thenReturn(ts("2026-04-10T12:00:00"));

        Optional<Instant> watermark = reader.readWatermark(15, 2026);

        assertThat(watermark).contains(Instant.parse("2026-04-10T12:00:00Z"));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).contains("MAX(Upload_Time)");
        verify(preparedStatement).setInt(1, 15);
        verify(preparedStatement).setInt(2, 2026);
    }

    @Test
    @DisplayName("readWatermark returns empty when the row is missing")
    void readWatermark_returnsEmptyWhenNoRow() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        Optional<Instant> watermark = reader.readWatermark(15, 2026);

        assertThat(watermark).isEmpty();
    }

    @Test
    @DisplayName("readWatermark returns empty when the value is SQL NULL")
    void readWatermark_returnsEmptyWhenNullTimestamp() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

        Optional<Instant> watermark = reader.readWatermark(15, 2026);

        assertThat(watermark).isEmpty();
    }

    @Test
    @DisplayName("iterateAllPages stops after a short page")
    void iterateAllPages_stopsOnShortPage() throws SQLException {
        // We can't easily stub three separate ResultSets on the same mocks.
        // Instead exercise the iterator against a reader where PAGE_SIZE is
        // effectively large enough that the stub's single "non-Total" row
        // becomes a short page and ends iteration immediately.
        RowStub real = new RowStub();
        real.ecoId = "eco-1";
        real.deviceId = "dev-1";
        real.datawipe = "";
        stubRows(List.of(real));

        Iterable<List<SnowflakeAggInventoryRow>> pages = reader.iterateAllPages(15, 2026);
        Iterator<List<SnowflakeAggInventoryRow>> it = pages.iterator();

        assertThat(it.hasNext()).isTrue();
        List<SnowflakeAggInventoryRow> first = it.next();
        assertThat(first).hasSize(1);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    @DisplayName("iterateAllPages at exact PAGE_SIZE boundary emits one page and stops")
    void iterateAllPages_atExactPageSizeBoundary_emitsOnePageAndStops() throws SQLException {
        // Simulate a week whose row count is exactly PAGE_SIZE. The iterator
        // must yield that single full page, then — on the mandatory trailing
        // zero-row probe that detects end-of-data — suppress the empty page
        // and report hasNext()=false.
        List<RowStub> fullPage = new java.util.ArrayList<>(SnowflakeAggInventoryReader.PAGE_SIZE);
        for (int i = 0; i < SnowflakeAggInventoryReader.PAGE_SIZE; i++) {
            RowStub r = new RowStub();
            r.ecoId = "eco-" + i;
            r.deviceId = "dev-" + i;
            r.datawipe = "";
            fullPage.add(r);
        }
        // After the first readPage drains this list, the shared rs.next()
        // stub keeps returning false, which is exactly what the trailing
        // zero-row probe needs to see.
        stubRows(fullPage);

        Iterable<List<SnowflakeAggInventoryRow>> pages = reader.iterateAllPages(15, 2026);
        Iterator<List<SnowflakeAggInventoryRow>> it = pages.iterator();

        assertThat(it.hasNext()).isTrue();
        List<SnowflakeAggInventoryRow> page = it.next();
        assertThat(page).hasSize(SnowflakeAggInventoryReader.PAGE_SIZE);
        assertThat(it.hasNext()).isFalse();
        // Sanity: a second hasNext() remains idempotently false, not an
        // exception — guards against the iterator accidentally re-firing
        // the probe.
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    @DisplayName("iterateAllPages with no rows yields a single empty list")
    void iterateAllPages_withNoRows_yieldsSingleEmptyList() throws SQLException {
        // Documented behavior (see PageIterator Javadoc): an empty first
        // page yields a single empty list so callers can distinguish
        // "no rows at all" from "consumed all rows". The `page.isEmpty()
        // && offset > 0` guard in hasNext() requires offset > 0 to
        // suppress, so the first empty page IS surfaced.
        when(resultSet.next()).thenReturn(false);

        Iterable<List<SnowflakeAggInventoryRow>> pages = reader.iterateAllPages(15, 2026);
        Iterator<List<SnowflakeAggInventoryRow>> it = pages.iterator();

        assertThat(it.hasNext()).isTrue();
        List<SnowflakeAggInventoryRow> page = it.next();
        assertThat(page).isEmpty();
        assertThat(it.hasNext()).isFalse();
    }

    // --- row stub used by the mocked ResultSet ---

    /**
     * Mutable row holder. Mockito lambdas are more readable when they point
     * at a field rather than a per-row {@code Map<String, Object>} — any
     * mis-typed column name fails at compile time.
     */
    private static final class RowStub {
        String ecoId;
        String deviceId;
        String mergedGrade;
        String datawipe;
        Timestamp maxUploadTime;
        BigDecimal avgTargetPrice;
        BigDecimal avgPayout;
        BigDecimal totalPayout;
        int totalQuantity;
        BigDecimal dwAvgTargetPrice;
        BigDecimal dwAvgPayout;
        BigDecimal dwTotalPayout;
        int dwTotalQuantity;
        BigDecimal round1TargetPrice;
        BigDecimal round1TargetPriceDw;
        String name;
        String model;
        String brand;
        String carrier;
        String category;
        Timestamp createdAt;
    }
}
