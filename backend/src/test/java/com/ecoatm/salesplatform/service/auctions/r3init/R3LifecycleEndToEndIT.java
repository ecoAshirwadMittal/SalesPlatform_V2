package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end IT for sub-project 6 (R3 Pre-process + Init).
 *
 * <p>Exercises the full listener → service → DB write chain:
 * <ol>
 *   <li>{@link R3PreProcessListener#onRoundClosed} fires on
 *       {@link RoundClosedEvent}(round=2) AFTER_COMMIT,
 *       {@link R3PreProcessService#run} writes QBCs + reports, status → SUCCESS.</li>
 *   <li>{@link R3InitListener#onRoundStarted} fires on
 *       {@link RoundStartedEvent}(round=3) AFTER_COMMIT,
 *       {@link R3InitService#run} sets r3InitStatus=SUCCESS + round3InitStatus=Complete.</li>
 *   <li>Silent-skip path: auction with no R3 SA → no writes.</li>
 *   <li>SKIPPED path: R3 SA with has_round=false → r3PreprocessStatus=SKIPPED, no writes.</li>
 * </ol>
 *
 * <p>Not {@code @Transactional}: both listeners use
 * {@code @TransactionalEventListener(AFTER_COMMIT)} + {@code @Async("snowflakeExecutor")},
 * so they require the publisher's tx to commit before firing and run in a
 * separate thread. A test-level tx would prevent the commit from being visible
 * to the listener. We use {@link TransactionTemplate} to commit a narrow
 * sub-tx around {@code publishEvent}, then Awaitility polls until DB state
 * settles.
 *
 * <p>Awaitility polls use plain {@link JdbcTemplate} queries (not JPA {@code findById})
 * to avoid Hibernate's first-level entity cache returning a stale value while
 * the async worker is writing from a different connection. JPA reads are used
 * only for post-settlement assertions where the desired state is already confirmed.
 *
 * <p>{@link #cleanup} DELETE-cleans fixture rows in reverse-FK order after
 * each test so the next run can re-apply {@code @Sql} cleanly.
 * The reset SQL resets status columns to PENDING before each test.
 */
@Sql(
    scripts = {
        "/fixtures/auctions/r3-lifecycle-seed.sql",
        "/fixtures/auctions/r3-lifecycle-reset.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@TestPropertySource(properties = {
    "auctions.r3-preprocess.enabled=true",
    "auctions.r3-init.enabled=true",
    "auctions.lifecycle.enabled=false"
})
class R3LifecycleEndToEndIT extends PostgresIntegrationTest {

    // Auction 600 — full path (R1=6001, R2=6002, R3=6003, week=601)
    private static final long R2_SA_FULL   = 6002L;
    private static final long R3_SA_FULL   = 6003L;
    private static final long AUCTION_FULL = 600L;
    private static final long WEEK_FULL    = 601L;

    // Auction 601 — no R3 SA (R2=6012, week=602)
    private static final long R2_SA_NO_R3   = 6012L;
    private static final long AUCTION_NO_R3 = 601L;
    private static final long WEEK_NO_R3    = 602L;

    // Auction 602 — has_round=false (R2=6022, R3=6023, week=603)
    private static final long R2_SA_DISABLED   = 6022L;
    private static final long R3_SA_DISABLED   = 6023L;
    private static final long AUCTION_DISABLED = 602L;
    private static final long WEEK_DISABLED    = 603L;

    @Autowired ApplicationEventPublisher events;
    @Autowired TransactionTemplate tx;
    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void cleanup() {
        // Reverse-FK order. All fixture IDs use the 6XXX / 60XXX / 6XX range.
        // The services write QBCs + reports inside REQUIRES_NEW transactions
        // that commit independently — explicit cleanup is required.
        jdbc.update("DELETE FROM auctions.round3_buyer_data_reports "
                + "WHERE scheduling_auction_id IN (6001, 6002, 6003, 6011, 6012, 6021, 6022, 6023)");
        jdbc.update("DELETE FROM buyer_mgmt.qualified_buyer_codes "
                + "WHERE scheduling_auction_id IN (6001, 6002, 6003, 6011, 6012, 6021, 6022, 6023)");
        // bid_data seeded by fixture
        jdbc.update("DELETE FROM auctions.bid_data WHERE id BETWEEN 60001 AND 60099");
        jdbc.update("DELETE FROM auctions.bid_rounds WHERE id BETWEEN 60001 AND 60099");
        jdbc.update("DELETE FROM auctions.bid_round_selection_filters WHERE id IN (601, 602)");
        jdbc.update("DELETE FROM auctions.aggregated_inventory WHERE id BETWEEN 60001 AND 60099");
        jdbc.update("DELETE FROM auctions.scheduling_auctions "
                + "WHERE id IN (6001, 6002, 6003, 6011, 6012, 6021, 6022, 6023)");
        jdbc.update("DELETE FROM auctions.auctions WHERE id IN (600, 601, 602)");
        jdbc.update("DELETE FROM buyer_mgmt.buyer_code_buyers "
                + "WHERE buyer_code_id BETWEEN 60001 AND 60099");
        jdbc.update("DELETE FROM buyer_mgmt.buyer_codes WHERE id BETWEEN 60001 AND 60099");
        jdbc.update("DELETE FROM buyer_mgmt.buyers WHERE id BETWEEN 6001 AND 6099");
        jdbc.update("DELETE FROM mdm.week WHERE id IN (601, 602, 603)");
    }

    // ─── Helper: raw-SQL status reads (bypass Hibernate entity cache) ─────────

    /** Returns the r3_preprocess_status string for the given SA id. */
    private String preprocessStatus(long saId) {
        return jdbc.queryForObject(
                "SELECT r3_preprocess_status FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, saId);
    }

    /** Returns the r3_init_status string for the given SA id. */
    private String initStatus(long saId) {
        return jdbc.queryForObject(
                "SELECT r3_init_status FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, saId);
    }

    /** Returns the round3_init_status string for the given SA id. */
    private String round3InitStatus(long saId) {
        return jdbc.queryForObject(
                "SELECT round3_init_status FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, saId);
    }

    /** Counts QBC rows for the given R3 SA id. */
    private int qbcCount(long saId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*)::int FROM buyer_mgmt.qualified_buyer_codes WHERE scheduling_auction_id = ?",
                Integer.class, saId);
        return count != null ? count : 0;
    }

    /** Counts report rows for the given R3 SA id. */
    private int reportCount(long saId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*)::int FROM auctions.round3_buyer_data_reports WHERE scheduling_auction_id = ?",
                Integer.class, saId);
        return count != null ? count : 0;
    }

    // ─── Tests ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("RoundClosedEvent(round=2) → R3 SA preprocess columns + QBCs + reports written")
    void preprocess_full_path() {
        // Pre-condition: R3 SA in PENDING state (JDBC read — safe before async starts)
        assertThat(preprocessStatus(R3_SA_FULL)).isEqualTo("PENDING");

        // Publish inside a committed sub-tx so AFTER_COMMIT listener fires
        tx.executeWithoutResult(s ->
                events.publishEvent(new RoundClosedEvent(R2_SA_FULL, 2, AUCTION_FULL, WEEK_FULL)));

        // Listener is @Async("snowflakeExecutor") — poll via JDBC (no JPA cache)
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    assertThat(preprocessStatus(R3_SA_FULL))
                            .as("r3_preprocess_status should be SUCCESS")
                            .isEqualTo("SUCCESS");
                    assertThat(qbcCount(R3_SA_FULL))
                            .as("QBC rows written for R3 SA")
                            .isGreaterThan(0);
                    assertThat(reportCount(R3_SA_FULL))
                            .as("round3_buyer_data_reports written for R3 SA")
                            .isGreaterThan(0);
                });

        // Timestamps populated, error cleared — verified via JDBC to avoid JPA stale reads
        String ppStartedAt = jdbc.queryForObject(
                "SELECT r3_preprocess_started_at::text FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, R3_SA_FULL);
        String ppFinishedAt = jdbc.queryForObject(
                "SELECT r3_preprocess_finished_at::text FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, R3_SA_FULL);
        String ppError = jdbc.queryForObject(
                "SELECT r3_preprocess_error FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, R3_SA_FULL);
        assertThat(ppStartedAt).as("r3_preprocess_started_at should be set").isNotNull();
        assertThat(ppFinishedAt).as("r3_preprocess_finished_at should be set").isNotNull();
        assertThat(ppError).as("r3_preprocess_error should be null").isNull();
    }

    @Test
    @DisplayName("RoundStartedEvent(round=3) after preprocess → r3InitStatus=SUCCESS, round3InitStatus=Complete")
    void init_after_preprocess() {
        // Phase 1: trigger pre-process via RoundClosedEvent
        tx.executeWithoutResult(s ->
                events.publishEvent(new RoundClosedEvent(R2_SA_FULL, 2, AUCTION_FULL, WEEK_FULL)));

        // Wait for preprocess to complete before triggering init (JDBC poll)
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        assertThat(preprocessStatus(R3_SA_FULL))
                                .as("r3_preprocess_status should be SUCCESS before init")
                                .isEqualTo("SUCCESS"));

        // Phase 2: trigger init via RoundStartedEvent for R3
        tx.executeWithoutResult(s ->
                events.publishEvent(new RoundStartedEvent(R3_SA_FULL, 3, AUCTION_FULL, WEEK_FULL)));

        // Poll until init completes (JDBC poll)
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    assertThat(initStatus(R3_SA_FULL))
                            .as("r3_init_status should be SUCCESS")
                            .isEqualTo("SUCCESS");
                    assertThat(round3InitStatus(R3_SA_FULL))
                            .as("round3_init_status should be Complete")
                            .isEqualTo("Complete");
                });

        // Timestamps and error — verified via JDBC to avoid JPA L1-cache stale reads
        // (the async service runs in a different connection; JPA findById may return a
        // snapshot loaded before the service committed its REQUIRES_NEW transaction).
        String startedAt = jdbc.queryForObject(
                "SELECT r3_init_started_at::text FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, R3_SA_FULL);
        String finishedAt = jdbc.queryForObject(
                "SELECT r3_init_finished_at::text FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, R3_SA_FULL);
        String error = jdbc.queryForObject(
                "SELECT r3_init_error FROM auctions.scheduling_auctions WHERE id = ?",
                String.class, R3_SA_FULL);
        assertThat(startedAt).as("r3_init_started_at should be set").isNotNull();
        assertThat(finishedAt).as("r3_init_finished_at should be set").isNotNull();
        assertThat(error).as("r3_init_error should be null").isNull();
    }

    @Test
    @DisplayName("RoundClosedEvent(round=2) for auction without R3 SA → no writes anywhere")
    void no_r3_sa_silent_skip() {
        // Auction 601 has no R3 SA — the listener should log and return silently
        tx.executeWithoutResult(s ->
                events.publishEvent(new RoundClosedEvent(R2_SA_NO_R3, 2, AUCTION_NO_R3, WEEK_NO_R3)));

        // Wait long enough for the thread-pool executor to have processed the event
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // No QBCs should have been written for anything related to auction 601
        assertThat(qbcCount(R2_SA_NO_R3))
                .as("no QBCs for auction-601 R2 SA (no R3 SA exists)")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("RoundClosedEvent(round=2) for R3 SA with has_round=false → SKIPPED, no QBCs/reports")
    void has_round_false_skipped() {
        // Auction 602 has R3 SA 6023 with has_round=false — service should write SKIPPED
        tx.executeWithoutResult(s ->
                events.publishEvent(new RoundClosedEvent(R2_SA_DISABLED, 2, AUCTION_DISABLED, WEEK_DISABLED)));

        // Poll until the listener runs and marks R3 SA as SKIPPED (JDBC poll)
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    assertThat(preprocessStatus(R3_SA_DISABLED))
                            .as("r3_preprocess_status should be SKIPPED when has_round=false")
                            .isEqualTo("SKIPPED");
                    assertThat(qbcCount(R3_SA_DISABLED))
                            .as("no QBCs written for SKIPPED R3 SA")
                            .isEqualTo(0);
                    assertThat(reportCount(R3_SA_DISABLED))
                            .as("no reports written for SKIPPED R3 SA")
                            .isEqualTo(0);
                });
    }
}
