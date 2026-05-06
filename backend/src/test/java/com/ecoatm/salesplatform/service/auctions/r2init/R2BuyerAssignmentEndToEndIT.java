package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.R2BuyerAssignmentCompletedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end IT for sub-project 5 (R2 Buyer Assignment).
 *
 * <p>Publishes a {@link RoundStartedEvent}(round=2) on the event publisher
 * inside a committed sub-tx and asserts the full chain:
 * <ol>
 *   <li>{@link R2BuyerAssignmentListener#onRoundStarted} fires
 *       ({@code @TransactionalEventListener(AFTER_COMMIT)} + {@code @Async}).</li>
 *   <li>{@link R2BuyerAssignmentService#run} executes against real Postgres.</li>
 *   <li>{@code qualified_buyer_codes} rows are written for the SA — qualified,
 *       special-treatment, and not-qualified all present.</li>
 *   <li>Special-buyer {@code bid_data} rows are seeded.</li>
 *   <li>{@code scheduling_auctions.r2_init_status} flips to {@code SUCCESS}
 *       with timestamps populated and error null.</li>
 *   <li>{@link R2BuyerAssignmentCompletedEvent} is published.</li>
 * </ol>
 *
 * <p>Mirrors {@link com.ecoatm.salesplatform.integration.R1InitializationIT}'s
 * pattern (the closest precedent — async listener triggered by
 * {@code RoundStartedEvent}). Not {@code @Transactional}: the listener opens
 * its own {@code REQUIRES_NEW} tx and would not see rows committed by a
 * surrounding test tx.
 *
 * <p>{@link #cleanup} DELETEs fixture rows after each test in reverse-FK
 * order so the next run can re-apply {@code @Sql} cleanly. The service uses
 * {@code REQUIRES_NEW} and commits independently — DB state survives without
 * explicit rollback. Disables the lifecycle scheduler so a stray cron tick
 * does not race with the test publishing its own event.
 */
@Sql(scripts = {
        "/fixtures/auctions/r2-init-seed.sql",
        "/fixtures/auctions/r2-special-buyer-extra-seed.sql"
})
@TestPropertySource(properties = {
        "auctions.r2-init.enabled=true",
        "auctions.lifecycle.enabled=false"
})
@Import(R2BuyerAssignmentEndToEndIT.TestConfig.class)
class R2BuyerAssignmentEndToEndIT extends PostgresIntegrationTest {

    private static final long R2_SA_ID = 999102L;
    private static final long R1_SA_ID = 999101L;
    private static final long AUCTION_ID = 999100L;
    private static final long WEEK_ID = 999100L;

    @TestConfiguration
    static class TestConfig {
        @Bean
        EventCapture eventCapture() {
            return new EventCapture();
        }
    }

    @Autowired ApplicationEventPublisher publisher;
    @Autowired JdbcTemplate jdbc;
    @Autowired EventCapture capture;
    @Autowired TransactionTemplate tx;
    @Autowired SchedulingAuctionRepository saRepo;
    @Autowired QualifiedBuyerCodeRepository qbcRepo;

    @AfterEach
    void cleanup() {
        // Reverse-FK order. All fixture IDs are in the 999100-999199 range.
        // The service writes QBCs + bid_data + bid_rounds + bid_data_docs
        // through a REQUIRES_NEW tx so they are committed independently of
        // the test thread; we have to delete them explicitly here.
        //
        // Important: deleting scheduling_auctions cascades to
        // qualified_buyer_codes (V72 FK ON DELETE CASCADE) and bid_rounds,
        // but bid_data → bid_rounds is also cascade so we drop those by
        // way of bid_round deletion. We still nuke bid_data first by
        // week_id to catch anything pinned to AE rows we created.
        jdbc.update("DELETE FROM auctions.bid_data WHERE week_id = ? OR id BETWEEN 999100 AND 999199",
                (int) WEEK_ID);
        // bid_data_docs has FKs to both buyer_code_id AND week_id — the service
        // creates docs against the seeded week 999100 for all 20 special-treatment
        // codes (including production-seed buyer codes from V18, not just our
        // 999100-range fixture codes), so scope cleanup by week_id, not code id.
        jdbc.update("DELETE FROM auctions.bid_data_docs WHERE week_id = ?", WEEK_ID);
        jdbc.update("DELETE FROM auctions.aggregated_inventory WHERE id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM auctions.bid_rounds WHERE id BETWEEN 999100 AND 999199 "
                + "OR scheduling_auction_id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM auctions.bid_round_selection_filters WHERE id BETWEEN 999100 AND 999199");
        // QBCs are CASCADEd by SA deletion; explicit DELETE first is harmless.
        jdbc.update("DELETE FROM buyer_mgmt.qualified_buyer_codes WHERE scheduling_auction_id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM auctions.scheduling_auctions WHERE id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM auctions.auctions WHERE id = ?", AUCTION_ID);
        jdbc.update("DELETE FROM buyer_mgmt.buyer_code_buyers WHERE buyer_code_id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM buyer_mgmt.buyer_codes WHERE id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM buyer_mgmt.buyers WHERE id BETWEEN 999100 AND 999199");
        jdbc.update("DELETE FROM mdm.week WHERE id = ?", WEEK_ID);
        capture.clear();
    }

    @Test
    @DisplayName("RoundStartedEvent(round=2) → QBCs + special bid_data + SUCCESS status")
    void roundStartedEvent_triggersR2BuyerAssignmentEndToEnd() {
        // Pre-condition sanity: fixtures seeded an R2 SA in PENDING state.
        SchedulingAuction sa = saRepo.findById(R2_SA_ID).orElseThrow();
        assertThat(sa.getRound()).isEqualTo(2);
        assertThat(sa.getR2InitStatus()).isEqualTo(RecalcStatus.PENDING);

        // Publish inside a committing tx — AFTER_COMMIT listeners are a no-op
        // outside an active transaction (fallbackExecution=false default).
        tx.executeWithoutResult(s ->
                publisher.publishEvent(new RoundStartedEvent(R2_SA_ID, 2, AUCTION_ID, WEEK_ID)));

        // The listener is @Async("snowflakeExecutor") — poll until status flips.
        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> {
                    SchedulingAuction reread = saRepo.findById(R2_SA_ID).orElseThrow();
                    assertThat(reread.getR2InitStatus()).isEqualTo(RecalcStatus.SUCCESS);
                });

        // QBCs written for the R2 SA — all three classes present.
        List<QualifiedBuyerCode> qbcs = qbcRepo.findBySchedulingAuctionId(R2_SA_ID);
        assertThat(qbcs).as("QBCs written for R2 SA").isNotEmpty();
        assertThat(qbcs)
                .as("at least one Qualified non-special row (the regular bid-above-target buyer)")
                .anySatisfy(qbc -> {
                    assertThat(qbc.getQualificationType()).isEqualTo(QualificationType.Qualified);
                    assertThat(qbc.isSpecialTreatment()).isFalse();
                });
        assertThat(qbcs)
                .as("at least one special-treatment Qualified row")
                .anySatisfy(qbc -> {
                    assertThat(qbc.getQualificationType()).isEqualTo(QualificationType.Qualified);
                    assertThat(qbc.isSpecialTreatment()).isTrue();
                });
        assertThat(qbcs)
                .as("at least one Not_Qualified row (e.g. WH-ZERO bid)")
                .anySatisfy(qbc ->
                        assertThat(qbc.getQualificationType()).isEqualTo(QualificationType.Not_Qualified));

        // Special-buyer bid_data seeded — count > 0 for round=2 against this SA.
        Integer bidDataCount = jdbc.queryForObject("""
                SELECT COUNT(*)::int
                  FROM auctions.bid_data bd
                  JOIN auctions.bid_rounds br ON br.id = bd.bid_round_id
                 WHERE br.scheduling_auction_id = ?
                """, Integer.class, R2_SA_ID);
        assertThat(bidDataCount).as("R2 special-buyer bid_data rows").isGreaterThan(0);

        // Status timestamps populated and error cleared.
        SchedulingAuction finalSa = saRepo.findById(R2_SA_ID).orElseThrow();
        assertThat(finalSa.getR2InitStartedAt()).isNotNull();
        assertThat(finalSa.getR2InitFinishedAt()).isNotNull();
        assertThat(finalSa.getR2InitError()).isNull();

        // Completion event published exactly once.
        assertThat(capture.completed)
                .as("R2BuyerAssignmentCompletedEvent published once")
                .hasSize(1);
        R2BuyerAssignmentCompletedEvent event = capture.completed.get(0);
        assertThat(event.schedulingAuctionId()).isEqualTo(R2_SA_ID);
        assertThat(event.auctionId()).isEqualTo(AUCTION_ID);
        assertThat(event.weekId()).isEqualTo(WEEK_ID);
        assertThat(event.qualifiedCount()).isPositive();
        assertThat(event.specialTreatmentCount()).isPositive();
    }

    /**
     * Captures {@link R2BuyerAssignmentCompletedEvent} so the test can assert
     * the service published exactly once on the SUCCESS path. Mirrors the
     * EventCapture pattern in {@code RecalcEndToEndIT}.
     */
    static class EventCapture {
        final List<R2BuyerAssignmentCompletedEvent> completed = new ArrayList<>();

        @EventListener
        void onCompleted(R2BuyerAssignmentCompletedEvent e) {
            completed.add(e);
        }

        void clear() {
            completed.clear();
        }
    }
}
