package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventorySyncResult;
import com.ecoatm.salesplatform.dto.AggregatedInventorySyncResult.SyncStatus;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.WeekSyncWatermark;
import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.WeekSyncWatermarkRepository;
import com.ecoatm.salesplatform.repository.auctions.AggregatedInventoryRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryReader;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryRow;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AggregatedInventorySnowflakeSyncService}.
 *
 * <p>All collaborators are mocked — no Spring context, no DB. The service's
 * algorithm (lock guard → watermark check → reader loop → upsert → watermark
 * upsert → log update) is verified purely via collaborator interactions.
 *
 * <p>The admin-override test ({@code syncWeek_respectsAdminOverride}) asserts
 * only that the upsert SQL contains the {@code CASE WHEN
 * is_total_quantity_modified} guard — the full behavior (override preserved,
 * other fields refreshed) is exercised against a real Postgres DB in
 * {@code AggregatedInventorySnowflakeSyncServiceIT}.
 *
 * <p>The {@code snowflake.enabled=false} case is gated by
 * {@link org.springframework.boot.autoconfigure.condition.ConditionalOnProperty}
 * and is a Spring-context concern, covered in
 * {@code AggregatedInventorySnowflakeSyncServiceDisabledIT}.
 */
@ExtendWith(MockitoExtension.class)
class AggregatedInventorySnowflakeSyncServiceTest {

    private static final long WEEK_ID = 123L;
    private static final int AUCTION_WEEK = 15;
    private static final int AUCTION_YEAR = 2026;
    private static final String TRIGGERED_BY = "admin@test.com";
    private static final String SOURCE = "SNOWFLAKE_AGG_INVENTORY";

    @Mock private SnowflakeAggInventoryReader reader;
    @Mock private WeekRepository weekRepository;
    @Mock private WeekSyncWatermarkRepository watermarkRepository;
    @Mock private AggregatedInventoryRepository aggregatedInventoryRepository;
    @Mock private SyncLogWriter syncLogWriter;
    @Mock private JdbcTemplate jdbcTemplate;

    private AggregatedInventorySnowflakeSyncService service;

    @BeforeEach
    void setUp() {
        service = new AggregatedInventorySnowflakeSyncService(
                reader,
                weekRepository,
                watermarkRepository,
                aggregatedInventoryRepository,
                syncLogWriter,
                jdbcTemplate,
                Clock.systemUTC());
    }

    // ────────────────────────────────────────────────────────────────────
    // 1. Week not found → EntityNotFoundException (no log row, no reader call)
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("syncWeek throws EntityNotFoundException when the week is absent")
    void syncWeek_missingWeek_throws() {
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.syncWeek(WEEK_ID, TRIGGERED_BY))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(WEEK_ID));

        verifyNoInteractions(reader);
        verifyNoInteractions(syncLogWriter);
        verifyNoInteractions(jdbcTemplate);
    }

    // ────────────────────────────────────────────────────────────────────
    // 2. Week has an auction → SKIPPED_LOCKED (no reader call, lock-log written)
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("syncWeek returns SKIPPED_LOCKED when an auction exists for the week")
    void syncWeek_weekHasAuction_returnsSkippedLocked() {
        stubWeek();
        when(aggregatedInventoryRepository.existsAuctionForWeek(WEEK_ID)).thenReturn(true);
        when(syncLogWriter.writeSkippedLocked(anyString(), anyString())).thenReturn(42L);

        AggregatedInventorySyncResult result = service.syncWeek(WEEK_ID, TRIGGERED_BY);

        assertThat(result.status()).isEqualTo(SyncStatus.SKIPPED_LOCKED);
        assertThat(result.logId()).isEqualTo(42L);
        assertThat(result.rowsRead()).isZero();
        assertThat(result.rowsUpserted()).isZero();
        assertThat(result.source()).isEqualTo(SOURCE);

        verifyNoInteractions(reader);
        verifyNoInteractions(jdbcTemplate);
        verify(watermarkRepository, never()).save(any());
    }

    // ────────────────────────────────────────────────────────────────────
    // 3. Watermark fresh → SKIPPED_UP_TO_DATE (no data read, no upsert)
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("syncWeek returns SKIPPED_UP_TO_DATE when existing watermark is >= snowflake max")
    void syncWeek_watermarkFresh_returnsSkippedUpToDate() {
        stubWeek();
        when(aggregatedInventoryRepository.existsAuctionForWeek(WEEK_ID)).thenReturn(false);

        Instant sfMax = Instant.parse("2026-04-01T00:00:00Z");
        when(reader.readWatermark(AUCTION_WEEK, AUCTION_YEAR)).thenReturn(Optional.of(sfMax));

        when(syncLogWriter.createStarted(anyString(), anyString())).thenReturn(7L);

        WeekSyncWatermark existing = new WeekSyncWatermark();
        existing.setWeekId(WEEK_ID);
        existing.setSource(SOURCE);
        existing.setLastSourceUploadAt(sfMax); // equal counts as fresh
        when(watermarkRepository.findByWeekIdAndSource(WEEK_ID, SOURCE))
                .thenReturn(Optional.of(existing));

        AggregatedInventorySyncResult result = service.syncWeek(WEEK_ID, TRIGGERED_BY);

        assertThat(result.status()).isEqualTo(SyncStatus.SKIPPED_UP_TO_DATE);
        assertThat(result.logId()).isEqualTo(7L);

        // The data cursor must not have been walked.
        verify(reader, never()).iterateAllPages(anyInt(), anyInt());
        verify(reader, never()).readPage(anyInt(), anyInt(), anyInt());
        verifyNoInteractions(jdbcTemplate);
        verify(watermarkRepository, never()).save(any());

        // Log row was created STARTED then flipped to SKIPPED_UP_TO_DATE.
        verify(syncLogWriter).markSkippedUpToDate(eq(7L));
    }

    // ────────────────────────────────────────────────────────────────────
    // 4. Stale/missing watermark, 3 pages → full upsert, watermark saved, COMPLETED
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("syncWeek upserts every page and bumps watermark when stale")
    void syncWeek_watermarkStale_upsertsAllPages() {
        stubWeek();
        when(aggregatedInventoryRepository.existsAuctionForWeek(WEEK_ID)).thenReturn(false);

        Instant sfMax = Instant.parse("2026-04-18T12:00:00Z");
        when(reader.readWatermark(AUCTION_WEEK, AUCTION_YEAR)).thenReturn(Optional.of(sfMax));

        // No prior watermark → treated as stale.
        when(watermarkRepository.findByWeekIdAndSource(WEEK_ID, SOURCE))
                .thenReturn(Optional.empty());

        when(syncLogWriter.createStarted(anyString(), anyString())).thenReturn(99L);

        List<SnowflakeAggInventoryRow> page1 = generatePage(1000, "sku-p1-");
        List<SnowflakeAggInventoryRow> page2 = generatePage(1000, "sku-p2-");
        List<SnowflakeAggInventoryRow> page3 = generatePage(342, "sku-p3-");
        when(reader.iterateAllPages(AUCTION_WEEK, AUCTION_YEAR))
                .thenReturn(List.of(page1, page2, page3));

        // jdbcTemplate.batchUpdate returns int[] of page size (each row upserted).
        when(jdbcTemplate.batchUpdate(anyString(), any(BatchPreparedStatementSetter.class)))
                .thenAnswer(invocation -> {
                    BatchPreparedStatementSetter setter =
                            invocation.getArgument(1, BatchPreparedStatementSetter.class);
                    int n = setter.getBatchSize();
                    int[] affected = new int[n];
                    for (int i = 0; i < n; i++) {
                        affected[i] = 1;
                    }
                    return affected;
                });

        AggregatedInventorySyncResult result = service.syncWeek(WEEK_ID, TRIGGERED_BY);

        assertThat(result.status()).isEqualTo(SyncStatus.COMPLETED);
        assertThat(result.logId()).isEqualTo(99L);
        assertThat(result.rowsRead()).isEqualTo(2342);
        assertThat(result.rowsUpserted()).isEqualTo(2342);

        // jdbcTemplate called once per non-empty page.
        verify(jdbcTemplate, times(3))
                .batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));

        // Watermark saved with sfMax and totalled row count.
        ArgumentCaptor<WeekSyncWatermark> wmCaptor = ArgumentCaptor.forClass(WeekSyncWatermark.class);
        verify(watermarkRepository).save(wmCaptor.capture());
        WeekSyncWatermark saved = wmCaptor.getValue();
        assertThat(saved.getWeekId()).isEqualTo(WEEK_ID);
        assertThat(saved.getSource()).isEqualTo(SOURCE);
        assertThat(saved.getLastSourceUploadAt()).isEqualTo(sfMax);
        assertThat(saved.getRowCount()).isEqualTo(2342);

        // Log row flipped to COMPLETED with final counts.
        verify(syncLogWriter).markCompleted(eq(99L), eq(2342), eq(2342));
    }

    // ────────────────────────────────────────────────────────────────────
    // 5. Snowflake watermark empty → COMPLETED with 0/0 (defensive edge case)
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("syncWeek completes with zero rows when snowflake has no data for the week")
    void syncWeek_watermarkMissing_returnsCompletedZero() {
        stubWeek();
        when(aggregatedInventoryRepository.existsAuctionForWeek(WEEK_ID)).thenReturn(false);
        when(reader.readWatermark(AUCTION_WEEK, AUCTION_YEAR)).thenReturn(Optional.empty());
        when(syncLogWriter.createStarted(anyString(), anyString())).thenReturn(11L);

        AggregatedInventorySyncResult result = service.syncWeek(WEEK_ID, TRIGGERED_BY);

        assertThat(result.status()).isEqualTo(SyncStatus.COMPLETED);
        assertThat(result.rowsRead()).isZero();
        assertThat(result.rowsUpserted()).isZero();
        assertThat(result.logId()).isEqualTo(11L);

        verify(reader, never()).iterateAllPages(anyInt(), anyInt());
        verifyNoInteractions(jdbcTemplate);
        verify(watermarkRepository, never()).save(any());
        verify(syncLogWriter).markCompleted(eq(11L), eq(0), eq(0));
    }

    // ────────────────────────────────────────────────────────────────────
    // 6. Upsert SQL honors admin override flag (text-level guard)
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("upsert SQL contains the CASE-WHEN is_total_quantity_modified guard")
    void syncWeek_respectsAdminOverride() {
        stubWeek();
        when(aggregatedInventoryRepository.existsAuctionForWeek(WEEK_ID)).thenReturn(false);

        Instant sfMax = Instant.parse("2026-04-18T12:00:00Z");
        when(reader.readWatermark(AUCTION_WEEK, AUCTION_YEAR)).thenReturn(Optional.of(sfMax));
        when(watermarkRepository.findByWeekIdAndSource(WEEK_ID, SOURCE)).thenReturn(Optional.empty());
        when(syncLogWriter.createStarted(anyString(), anyString())).thenReturn(1L);

        when(reader.iterateAllPages(AUCTION_WEEK, AUCTION_YEAR))
                .thenReturn(List.of(generatePage(2, "sku-")));

        when(jdbcTemplate.batchUpdate(anyString(), any(BatchPreparedStatementSetter.class)))
                .thenReturn(new int[] {1, 1});

        service.syncWeek(WEEK_ID, TRIGGERED_BY);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).batchUpdate(sqlCaptor.capture(), any(BatchPreparedStatementSetter.class));
        String upsertSql = sqlCaptor.getValue();

        assertThat(upsertSql).contains("ON CONFLICT");
        assertThat(upsertSql).contains("is_total_quantity_modified");
        assertThat(upsertSql)
                .as("admin override preserves total_quantity when flag is true")
                .contains("CASE");
        assertThat(upsertSql).contains("round1_target_price");
        assertThat(upsertSql).contains("round1_target_price_dw");
    }

    // ────────────────────────────────────────────────────────────────────
    // 7. Reader throws → FAILED log row, no rethrow
    // ────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("syncWeek catches SnowflakeReadException and writes FAILED log")
    void syncWeek_snowflakeThrows_logsFailed() {
        stubWeek();
        when(aggregatedInventoryRepository.existsAuctionForWeek(WEEK_ID)).thenReturn(false);
        when(syncLogWriter.createStarted(anyString(), anyString())).thenReturn(55L);

        SnowflakeReadException boom =
                new SnowflakeReadException("snowflake offline", new RuntimeException("underlying"));
        when(reader.readWatermark(AUCTION_WEEK, AUCTION_YEAR)).thenThrow(boom);

        AggregatedInventorySyncResult result = service.syncWeek(WEEK_ID, TRIGGERED_BY);

        assertThat(result.status()).isEqualTo(SyncStatus.FAILED);
        assertThat(result.logId()).isEqualTo(55L);
        assertThat(result.errorMessage()).contains("snowflake offline");

        ArgumentCaptor<String> errCaptor = ArgumentCaptor.forClass(String.class);
        verify(syncLogWriter).markFailed(eq(55L), errCaptor.capture());
        assertThat(errCaptor.getValue()).contains("snowflake offline");
    }

    // ────────────────────────────────────────────────────────────────────
    // helpers
    // ────────────────────────────────────────────────────────────────────
    private void stubWeek() {
        Week week = newWeek(WEEK_ID, AUCTION_WEEK, AUCTION_YEAR);
        when(weekRepository.findById(WEEK_ID)).thenReturn(Optional.of(week));
    }

    private static Week newWeek(long id, int weekNumber, int year) {
        // Week has package-private setters via reflection only — use a
        // lightweight reflection helper here rather than mutating the
        // entity's public surface.
        Week w = new Week();
        try {
            setField(w, "id", id);
            setField(w, "weekNumber", weekNumber);
            setField(w, "year", year);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return w;
    }

    private static void setField(Object target, String name, Object value)
            throws ReflectiveOperationException {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static List<SnowflakeAggInventoryRow> generatePage(int size, String ecoPrefix) {
        if (size == 0) {
            return Collections.emptyList();
        }
        List<SnowflakeAggInventoryRow> rows = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            rows.add(new SnowflakeAggInventoryRow(
                    ecoPrefix + i,                  // ecoId
                    "device-" + i,                  // deviceId
                    "A",                             // mergedGrade
                    false,                           // datawipe
                    Instant.parse("2026-04-18T12:00:00Z"),
                    new BigDecimal("10.00"),        // avgTargetPrice
                    new BigDecimal("9.00"),         // avgPayout
                    new BigDecimal("100.00"),       // totalPayout
                    10,                              // totalQuantity
                    null,                            // dwAvgTargetPrice
                    null,                            // dwAvgPayout
                    null,                            // dwTotalPayout
                    0,                               // dwTotalQuantity
                    new BigDecimal("10.00"),        // round1TargetPrice
                    null,                            // round1TargetPriceDw
                    "Test Device",                   // name
                    "iPhone 14",                     // model
                    "Apple",                         // brand
                    "AT&T",                          // carrier
                    "Phone",                         // category
                    Instant.parse("2026-01-01T00:00:00Z")));
        }
        return rows;
    }
}
