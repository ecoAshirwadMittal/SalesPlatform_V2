package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Direct integration coverage for {@link RecalcStatusUpdater}. The class
 * writes raw SQL via {@link JdbcTemplate} so it is most reliably tested
 * against the live schema — exercising column names from V82 (RANKING +
 * TARGET_PRICE) and V83 (R2_INIT) for real.
 *
 * <p>Why @SpringBootTest + PostgresIntegrationTest: {@code markSuccess} and
 * {@code markSkipped} are {@code @Transactional(propagation = MANDATORY)},
 * so the test must drive them from inside an active tx. We use
 * {@link TransactionTemplate} to wrap those calls — the same trick
 * {@code RecalcEndToEndIT} uses to commit publisher events.
 *
 * <p>Fixture range 888xxx avoids colliding with {@code recalc-seed.sql}'s
 * 999xxx range. Cleanup is FK-safe (scheduling_auctions then auctions).
 */
class RecalcStatusUpdaterIT extends PostgresIntegrationTest {

    private static final long AUCTION_ID = 888001L;

    @Autowired RecalcStatusUpdater updater;
    @Autowired JdbcTemplate jdbc;
    @Autowired TransactionTemplate txTemplate;

    @AfterEach
    void cleanup() {
        jdbc.update("DELETE FROM auctions.scheduling_auctions WHERE auction_id = ?", AUCTION_ID);
        jdbc.update("DELETE FROM auctions.auctions WHERE id = ?", AUCTION_ID);
    }

    // ---- R2_INIT (sub-project 5 additions) -------------------------------

    @Test
    @DisplayName("tryFlipToRunning routes R2_INIT to r2_init_status column")
    void flips_r2_init_to_running() {
        long saId = seedSchedulingAuction("r2_init_status", "PENDING");

        boolean flipped = updater.tryFlipToRunning(saId, "R2_INIT");

        assertThat(flipped).isTrue();
        assertThat(currentStatus(saId, "r2_init_status")).isEqualTo("RUNNING");
        assertThat(timestamp(saId, "r2_init_started_at")).isNotNull();
        assertThat(timestamp(saId, "r2_init_finished_at")).isNull();
    }

    @Test
    @DisplayName("markSkipped writes SKIPPED + finished_at + clears error for R2_INIT")
    void marks_skipped_for_r2_init() {
        long saId = seedSchedulingAuction("r2_init_status", "PENDING");
        // Pre-seed an error to verify it gets cleared.
        jdbc.update("UPDATE auctions.scheduling_auctions SET r2_init_error = ? WHERE id = ?",
                "stale", saId);

        // markSkipped is MANDATORY → must run inside a tx.
        txTemplate.executeWithoutResult(status -> updater.markSkipped(saId, "R2_INIT"));

        assertThat(currentStatus(saId, "r2_init_status")).isEqualTo("SKIPPED");
        assertThat(timestamp(saId, "r2_init_finished_at")).isNotNull();
        assertThat(error(saId, "r2_init_error")).isNull();
    }

    // ---- Regression guards: RANKING + TARGET_PRICE still route correctly --

    @Test
    @DisplayName("tryFlipToRunning still routes RANKING to ranking_status")
    void ranking_still_works() {
        long saId = seedSchedulingAuction("ranking_status", "PENDING");

        boolean flipped = updater.tryFlipToRunning(saId, "RANKING");

        assertThat(flipped).isTrue();
        assertThat(currentStatus(saId, "ranking_status")).isEqualTo("RUNNING");
    }

    @Test
    @DisplayName("tryFlipToRunning still routes TARGET_PRICE to target_price_status")
    void target_price_still_works() {
        long saId = seedSchedulingAuction("target_price_status", "PENDING");

        boolean flipped = updater.tryFlipToRunning(saId, "TARGET_PRICE");

        assertThat(flipped).isTrue();
        assertThat(currentStatus(saId, "target_price_status")).isEqualTo("RUNNING");
    }

    @Test
    @DisplayName("Unknown process throws IllegalArgumentException")
    void unknown_process_throws() {
        long saId = seedSchedulingAuction("ranking_status", "PENDING");

        assertThatThrownBy(() -> updater.tryFlipToRunning(saId, "BOGUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BOGUS");
    }

    // ---- R3_PREPROCESS (sub-project 6 additions) ---------------------------

    @Test
    @DisplayName("tryFlipToRunning routes R3_PREPROCESS to r3_preprocess_status column")
    void flips_r3_preprocess_to_running() {
        long saId = seedSchedulingAuction("r3_preprocess_status", "PENDING");

        boolean flipped = updater.tryFlipToRunning(saId, "R3_PREPROCESS");

        assertThat(flipped).isTrue();
        assertThat(currentStatus(saId, "r3_preprocess_status")).isEqualTo("RUNNING");
        assertThat(timestamp(saId, "r3_preprocess_started_at")).isNotNull();
        assertThat(timestamp(saId, "r3_preprocess_finished_at")).isNull();
    }

    @Test
    @DisplayName("markSkipped writes SKIPPED + finished_at + clears error for R3_PREPROCESS")
    void marks_skipped_for_r3_preprocess() {
        long saId = seedSchedulingAuction("r3_preprocess_status", "PENDING");
        // Pre-seed an error to verify it gets cleared.
        jdbc.update("UPDATE auctions.scheduling_auctions SET r3_preprocess_error = ? WHERE id = ?",
                "stale", saId);

        // markSkipped is MANDATORY → must run inside a tx.
        txTemplate.executeWithoutResult(status -> updater.markSkipped(saId, "R3_PREPROCESS"));

        assertThat(currentStatus(saId, "r3_preprocess_status")).isEqualTo("SKIPPED");
        assertThat(timestamp(saId, "r3_preprocess_finished_at")).isNotNull();
        assertThat(error(saId, "r3_preprocess_error")).isNull();
    }

    // ---- R3_INIT (sub-project 6 additions) ---------------------------------

    @Test
    @DisplayName("tryFlipToRunning routes R3_INIT to r3_init_status column")
    void flips_r3_init_to_running() {
        long saId = seedSchedulingAuction("r3_init_status", "PENDING");

        boolean flipped = updater.tryFlipToRunning(saId, "R3_INIT");

        assertThat(flipped).isTrue();
        assertThat(currentStatus(saId, "r3_init_status")).isEqualTo("RUNNING");
        assertThat(timestamp(saId, "r3_init_started_at")).isNotNull();
        assertThat(timestamp(saId, "r3_init_finished_at")).isNull();
    }

    // ---- helpers ---------------------------------------------------------

    /**
     * Inserts a parent auction (idempotent via NOT EXISTS) and a fresh
     * scheduling_auctions row with the given status column initialised to
     * {@code initialStatus}. Returns the new SA id.
     */
    private long seedSchedulingAuction(String statusColumn, String initialStatus) {
        jdbc.update("""
            INSERT INTO auctions.auctions (id, auction_title, auction_status)
            SELECT ?, '4-T4 RecalcStatusUpdater fixture', 'Started'
             WHERE NOT EXISTS (SELECT 1 FROM auctions.auctions WHERE id = ?)
            """, AUCTION_ID, AUCTION_ID);

        // chk_sa_round_positive constrains round to 1..3. @AfterEach
        // wipes both auction + SA, so each test starts fresh and round=1 is safe.
        Long saId = jdbc.queryForObject("""
            INSERT INTO auctions.scheduling_auctions
                (auction_id, round, round_status, %s)
            VALUES (?, 1, 'Closed', ?)
            RETURNING id
            """.formatted(statusColumn), Long.class, AUCTION_ID, initialStatus);
        return saId;
    }

    private String currentStatus(long saId, String column) {
        return jdbc.queryForObject(
                "SELECT " + column + " FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, saId);
    }

    private java.sql.Timestamp timestamp(long saId, String column) {
        return jdbc.queryForObject(
                "SELECT " + column + " FROM auctions.scheduling_auctions WHERE id = ?",
                java.sql.Timestamp.class, saId);
    }

    private String error(long saId, String column) {
        return jdbc.queryForObject(
                "SELECT " + column + " FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, saId);
    }
}
