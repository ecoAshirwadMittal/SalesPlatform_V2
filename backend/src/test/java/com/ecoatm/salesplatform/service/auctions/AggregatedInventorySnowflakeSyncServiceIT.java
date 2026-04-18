package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.dto.AggregatedInventorySyncResult;
import com.ecoatm.salesplatform.dto.AggregatedInventorySyncResult.SyncStatus;
import com.ecoatm.salesplatform.model.auctions.WeekSyncWatermark;
import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.repository.WeekSyncWatermarkRepository;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryReader;
import com.ecoatm.salesplatform.service.auctions.snowflake.SnowflakeAggInventoryRow;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * End-to-end integration test for
 * {@link AggregatedInventorySnowflakeSyncService} against a real PostgreSQL DB.
 *
 * <p>Validates the admin-override guarantee that the unit test can only check
 * textually — when {@code is_total_quantity_modified=true}, the UPSERT must
 * leave {@code total_quantity} and {@code dw_total_quantity} alone while still
 * refreshing every other column. This is the Mendix-parity bit the Phase 4
 * plan specifies (§3.2).
 *
 * <p>The Snowflake reader is mocked out; all Postgres plumbing (Flyway
 * migrations V1..V69, entity persistence, JdbcTemplate upsert, watermark
 * repository, audit log repository) is real.
 *
 * <p>The class-level {@code @Transactional} rolls back everything at the end
 * of each test — including the seeded {@code aggregated_inventory} row — so
 * the shared local/Testcontainers DB stays clean.
 */
@TestPropertySource(properties = {
        "snowflake.enabled=true",
        "snowflake.username=dummy-test-user",
        "snowflake.password=dummy-test-pwd",
        "snowflake.jdbc-url=jdbc:snowflake://it-test-unused.snowflakecomputing.com/?db=ECO_DEV"
})
@Transactional
class AggregatedInventorySnowflakeSyncServiceIT extends PostgresIntegrationTest {

    private static final String SOURCE = AggregatedInventorySnowflakeSyncService.SOURCE;

    @TestConfiguration
    static class TestReaderConfig {
        @Bean
        @Primary
        public SnowflakeAggInventoryReader testReader() {
            return Mockito.mock(SnowflakeAggInventoryReader.class);
        }
    }

    @Autowired private AggregatedInventorySnowflakeSyncService service;
    @Autowired private WeekSyncWatermarkRepository watermarkRepository;
    @Autowired private SnowflakeSyncLogRepository logRepository;
    @Autowired private EntityManager em;

    @Autowired private SnowflakeAggInventoryReader reader;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(reader);
    }

    @Test
    @DisplayName("syncWeek preserves admin-edited total_quantity while refreshing every other column")
    void syncWeek_preservesAdminOverrideButRefreshesOtherFields() {
        // Arrange — pick a seeded week and seed an admin-overridden row on it.
        Object[] weekRow = pickSeedWeek();
        long weekId = ((Number) weekRow[0]).longValue();
        int weekNumber = ((Number) weekRow[1]).intValue();
        int year = ((Number) weekRow[2]).intValue();

        String ecoid = "IT_OVERRIDE_" + System.nanoTime();
        // Seed a pre-existing row with an admin edit: total_quantity=99 (would
        // ordinarily be 10 from the source), and stale avg_target_price that
        // MUST be refreshed.
        em.createNativeQuery("""
                INSERT INTO auctions.aggregated_inventory (
                    ecoid2, merged_grade, datawipe, week_id,
                    total_quantity, dw_total_quantity,
                    avg_target_price, avg_payout, total_payout,
                    dw_avg_target_price, dw_avg_payout, dw_total_payout,
                    round1_target_price, round1_target_price_dw,
                    name, model, brand, carrier, category, device_id,
                    is_total_quantity_modified, is_deprecated,
                    created_date, changed_date
                )
                VALUES (:e, 'A', false, :w,
                        99, 77,
                        :stalePrice, :stalePayout, :stalePayout,
                        :stalePrice, :stalePayout, :stalePayout,
                        :stalePrice, :stalePrice,
                        'Stale Name', 'Stale Model', 'Stale Brand', 'Stale Carrier', 'Stale Category', 'stale-device',
                        true, false,
                        NOW(), NOW())
                """)
            .setParameter("e", ecoid)
            .setParameter("w", weekId)
            .setParameter("stalePrice", new BigDecimal("1.00"))
            .setParameter("stalePayout", new BigDecimal("1.00"))
            .executeUpdate();
        em.flush();

        // Stub the Snowflake reader with a fresh watermark and a single row
        // carrying the NEW data. total_quantity=500 would stomp 99 if the
        // CASE WHEN guard is wrong — that is the bug this test catches.
        Instant sfMax = Instant.now().plusSeconds(3600);
        when(reader.readWatermark(anyInt(), anyInt())).thenReturn(Optional.of(sfMax));

        SnowflakeAggInventoryRow page1Row = new SnowflakeAggInventoryRow(
                ecoid,
                "new-device",
                "A",
                false,
                sfMax,
                new BigDecimal("25.50"),        // avgTargetPrice (must refresh)
                new BigDecimal("22.75"),        // avgPayout (must refresh)
                new BigDecimal("12750.00"),     // totalPayout (must refresh)
                500,                             // totalQuantity (must NOT overwrite 99)
                new BigDecimal("30.00"),        // dwAvgTargetPrice (must refresh)
                new BigDecimal("28.00"),        // dwAvgPayout (must refresh)
                new BigDecimal("2156.00"),      // dwTotalPayout (must refresh)
                400,                             // dwTotalQuantity (must NOT overwrite 77)
                new BigDecimal("27.00"),        // round1TargetPrice (must refresh)
                new BigDecimal("32.00"),        // round1TargetPriceDw (must refresh)
                "New Name",
                "New Model",
                "New Brand",
                "New Carrier",
                "New Category",
                Instant.now());

        when(reader.iterateAllPages(weekNumber, year))
                .thenReturn(List.of(List.of(page1Row)));

        // Act
        AggregatedInventorySyncResult result = service.syncWeek(weekId, "it-test");

        // Assert — status + audit log + watermark + row state.
        assertThat(result.status()).isEqualTo(SyncStatus.COMPLETED);
        assertThat(result.rowsRead()).isEqualTo(1);
        assertThat(result.rowsUpserted()).isEqualTo(1);
        assertThat(result.logId()).isNotNull();

        SnowflakeSyncLog log = logRepository.findById(result.logId()).orElseThrow();
        assertThat(log.getStatus()).isEqualTo("COMPLETED");
        assertThat(log.getRowsUpserted()).isEqualTo(1);

        Optional<WeekSyncWatermark> wm = watermarkRepository.findByWeekIdAndSource(weekId, SOURCE);
        assertThat(wm).isPresent();
        assertThat(wm.get().getLastSourceUploadAt()).isEqualTo(sfMax);

        // The actual persisted row must keep admin overrides on the two
        // quantity columns AND have every other column refreshed from the
        // Snowflake payload. This is the Mendix-parity behavior.
        Object[] persisted = (Object[]) em.createNativeQuery("""
                SELECT total_quantity,
                       dw_total_quantity,
                       avg_target_price,
                       avg_payout,
                       total_payout,
                       name,
                       brand,
                       device_id,
                       is_total_quantity_modified
                FROM auctions.aggregated_inventory
                WHERE ecoid2 = :e
                  AND merged_grade = 'A'
                  AND datawipe = false
                  AND week_id = :w
                  AND is_deprecated = false
                """)
            .setParameter("e", ecoid)
            .setParameter("w", weekId)
            .getSingleResult();

        assertThat(((Number) persisted[0]).intValue())
                .as("admin-edited total_quantity preserved")
                .isEqualTo(99);
        assertThat(((Number) persisted[1]).intValue())
                .as("admin-edited dw_total_quantity preserved")
                .isEqualTo(77);
        assertThat(((BigDecimal) persisted[2]))
                .as("avg_target_price refreshed from Snowflake")
                .isEqualByComparingTo("25.50");
        assertThat(((BigDecimal) persisted[3]))
                .as("avg_payout refreshed from Snowflake")
                .isEqualByComparingTo("22.75");
        assertThat(((BigDecimal) persisted[4]))
                .as("total_payout refreshed from Snowflake")
                .isEqualByComparingTo("12750.00");
        assertThat((String) persisted[5])
                .as("name refreshed from Snowflake")
                .isEqualTo("New Name");
        assertThat((String) persisted[6]).isEqualTo("New Brand");
        assertThat((String) persisted[7]).isEqualTo("new-device");
        assertThat((Boolean) persisted[8])
                .as("override flag stays true after a sync that did not overwrite")
                .isTrue();
    }

    @Test
    @DisplayName("syncWeek inserts a new row when no match exists, flag defaults to false")
    void syncWeek_insertsFreshRow() {
        Object[] weekRow = pickSeedWeek();
        long weekId = ((Number) weekRow[0]).longValue();
        int weekNumber = ((Number) weekRow[1]).intValue();
        int year = ((Number) weekRow[2]).intValue();

        String ecoid = "IT_INSERT_" + System.nanoTime();
        Instant sfMax = Instant.now().plusSeconds(3600);
        when(reader.readWatermark(anyInt(), anyInt())).thenReturn(Optional.of(sfMax));

        SnowflakeAggInventoryRow row = new SnowflakeAggInventoryRow(
                ecoid, "device-x", "B", true, sfMax,
                new BigDecimal("50.00"),
                new BigDecimal("48.00"),
                new BigDecimal("480.00"),
                10,
                new BigDecimal("52.00"),
                new BigDecimal("50.00"),
                new BigDecimal("500.00"),
                10,
                new BigDecimal("55.00"),
                new BigDecimal("57.00"),
                "Fresh Name",
                "Fresh Model",
                "Fresh Brand",
                "Fresh Carrier",
                "Fresh Category",
                Instant.now());
        when(reader.iterateAllPages(weekNumber, year))
                .thenReturn(List.of(List.of(row)));

        AggregatedInventorySyncResult result = service.syncWeek(weekId, "it-test");

        assertThat(result.status()).isEqualTo(SyncStatus.COMPLETED);

        Object[] persisted = (Object[]) em.createNativeQuery("""
                SELECT total_quantity,
                       is_total_quantity_modified,
                       datawipe,
                       merged_grade,
                       name
                FROM auctions.aggregated_inventory
                WHERE ecoid2 = :e
                """)
            .setParameter("e", ecoid)
            .getSingleResult();

        assertThat(((Number) persisted[0]).intValue()).isEqualTo(10);
        assertThat((Boolean) persisted[1])
                .as("fresh inserts default the override flag to false")
                .isFalse();
        assertThat((Boolean) persisted[2]).isTrue();
        assertThat((String) persisted[3]).isEqualTo("B");
        assertThat((String) persisted[4]).isEqualTo("Fresh Name");
    }

    /** Pick any seeded (id, week_number, year) row from {@code mdm.week}. */
    private Object[] pickSeedWeek() {
        return (Object[]) em.createNativeQuery("""
                SELECT id, week_number, year
                FROM mdm.week
                ORDER BY id
                LIMIT 1
                """).getSingleResult();
    }
}
