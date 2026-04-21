package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration test for {@link AuctionLifecycleService}, exercising
 * the full Spring context against a real PostgreSQL DB (Testcontainers in
 * Docker, local PG fallback otherwise — see {@link PostgresIntegrationTest}).
 *
 * <p>Drives {@code service.tick()} directly with the @Scheduled disabled so
 * the orchestration is deterministic. Validates the three core round-state
 * transitions and the parent-status reconciliation. Audit-row coverage lives
 * in {@link AuctionLifecycleSchedulerIT}, which exercises the scheduler entry
 * point that wraps {@code tick()} with {@code ScheduledJobRunRecorder}.
 *
 * <p>This class is intentionally <strong>not</strong> {@code @Transactional}.
 * {@link RoundTransitionService} uses {@code Propagation.REQUIRES_NEW}, which
 * suspends the caller's tx — a class-level {@code @Transactional} would hide
 * the seed rows from the inner tx and the tick would find nothing to do.
 * Each seed row is committed in its own tx and deleted in {@link #cleanup()}
 * via the V58 {@code ON DELETE CASCADE} on
 * {@code auctions.scheduling_auctions.auction_id}.
 */
@TestPropertySource(properties = {
        "auctions.lifecycle.enabled=false"
})
class AuctionLifecycleIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class CaptureConfig {
        @Bean
        TestEventCapture eventCapture() { return new TestEventCapture(); }
    }

    static class TestEventCapture {
        final List<Object> events = new ArrayList<>();

        @EventListener
        void onStarted(RoundStartedEvent e) { events.add(e); }

        @EventListener
        void onClosed(RoundClosedEvent e) { events.add(e); }
    }

    @Autowired private AuctionLifecycleService service;
    @Autowired private SchedulingAuctionRepository schedulingRepo;
    @Autowired private AuctionRepository auctionRepo;
    @Autowired private TestEventCapture capture;

    private final List<Long> createdAuctionIds = new ArrayList<>();

    @BeforeEach
    void clearCapturedEvents() {
        capture.events.clear();
    }

    @AfterEach
    void cleanup() {
        // ON DELETE CASCADE on scheduling_auctions.auction_id wipes child rounds.
        for (Long id : createdAuctionIds) {
            try {
                auctionRepo.deleteById(id);
            } catch (Exception ignored) {
                // Tolerate cascade races / already-deleted rows so cleanup
                // never masks the real test failure.
            }
        }
        createdAuctionIds.clear();
        capture.events.clear();
    }

    @Test
    @DisplayName("tick closes Started rounds whose end is in the past")
    void tick_closesStartedRoundsPastEnd() {
        Auction a = persistAuction(AuctionStatus.Started);
        SchedulingAuction r = persistRound(a.getId(), 1,
                SchedulingAuctionStatus.Started,
                Instant.now().minusSeconds(7200),
                Instant.now().minusSeconds(60));

        service.tick();

        SchedulingAuction reloaded = schedulingRepo.findById(r.getId()).orElseThrow();
        assertThat(reloaded.getRoundStatus()).isEqualTo(SchedulingAuctionStatus.Closed);
        assertThat(capture.events).anyMatch(e -> e instanceof RoundClosedEvent);
    }

    @Test
    @DisplayName("tick starts Scheduled rounds whose start time has arrived")
    void tick_startsScheduledRoundsAtTime() {
        Auction a = persistAuction(AuctionStatus.Scheduled);
        SchedulingAuction r = persistRound(a.getId(), 1,
                SchedulingAuctionStatus.Scheduled,
                Instant.now().minusSeconds(10),
                Instant.now().plusSeconds(3600));

        service.tick();

        assertThat(schedulingRepo.findById(r.getId()).orElseThrow().getRoundStatus())
                .isEqualTo(SchedulingAuctionStatus.Started);
        assertThat(capture.events).anyMatch(e -> e instanceof RoundStartedEvent);
    }

    @Test
    @DisplayName("tick reconciles parent to Closed when every round is Closed")
    void tick_reconcilesParentToClosedWhenAllRoundsClosed() {
        Auction a = persistAuction(AuctionStatus.Started);
        persistRound(a.getId(), 1, SchedulingAuctionStatus.Closed,
                Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600));
        persistRound(a.getId(), 2, SchedulingAuctionStatus.Closed,
                Instant.now().minusSeconds(3600), Instant.now().minusSeconds(1800));
        // Round 3 is Started but past end → tick will close it, then reconciler
        // should see all 3 rounds Closed and flip parent to Closed.
        persistRound(a.getId(), 3, SchedulingAuctionStatus.Started,
                Instant.now().minusSeconds(1800), Instant.now().minusSeconds(60));

        service.tick();

        assertThat(auctionRepo.findById(a.getId()).orElseThrow().getAuctionStatus())
                .isEqualTo(AuctionStatus.Closed);
    }

    private Auction persistAuction(AuctionStatus status) {
        Auction a = new Auction();
        a.setAuctionTitle("IT lifecycle " + System.nanoTime());
        a.setAuctionStatus(status);
        a.setWeekId(1L);
        a.setCreatedDate(Instant.now());
        a.setChangedDate(Instant.now());
        a.setCreatedBy("it");
        a.setUpdatedBy("it");
        Auction saved = auctionRepo.save(a);
        createdAuctionIds.add(saved.getId());
        return saved;
    }

    private SchedulingAuction persistRound(Long auctionId, int round,
                                           SchedulingAuctionStatus status,
                                           Instant start, Instant end) {
        SchedulingAuction r = new SchedulingAuction();
        r.setAuctionId(auctionId);
        r.setRound(round);
        r.setRoundStatus(status);
        r.setStartDatetime(start);
        r.setEndDatetime(end);
        r.setCreatedDate(Instant.now());
        r.setChangedDate(Instant.now());
        r.setCreatedBy("it");
        r.setUpdatedBy("it");
        return schedulingRepo.save(r);
    }
}
