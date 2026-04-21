package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.lifecycle.AuctionLifecycleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end verification that the cron → {@code RoundStartedEvent} →
 * {@link AuctionStatusSnowflakePushListener} → writer chain survives the full
 * Spring context. Drives {@link AuctionLifecycleService#tick()} directly
 * (scheduler disabled) and asserts the captured payload shape.
 *
 * <p>The capturing writer is registered via {@code @TestConfiguration} +
 * {@code @Primary} so it overrides the default {@link LoggingAuctionStatusSnowflakeWriter}.
 * A {@link CountDownLatch} bridges the test thread and the async
 * {@code snowflakeExecutor} — the listener is {@code @Async}, so without the
 * latch the test would assert before the handler ran.
 *
 * <p>Like {@code AuctionLifecycleIT}, this class is intentionally <strong>not</strong>
 * {@code @Transactional}: {@code RoundTransitionService} opens
 * {@code REQUIRES_NEW} inner txs that would not see rows from a test-level tx.
 */
@TestPropertySource(properties = {
        "auctions.lifecycle.enabled=false",
        "auctions.snowflake-push.enabled=true"
})
class AuctionStatusSnowflakePushIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class CaptureConfig {
        @Bean
        @Primary
        AuctionStatusSnowflakeWriter capturingWriter() {
            return new CapturingWriter();
        }
    }

    static class CapturingWriter implements AuctionStatusSnowflakeWriter {
        final List<AuctionStatusPushPayload> payloads = new CopyOnWriteArrayList<>();
        volatile CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void push(AuctionStatusPushPayload payload) {
            payloads.add(payload);
            latch.countDown();
        }

        void reset(int expected) {
            payloads.clear();
            latch = new CountDownLatch(expected);
        }
    }

    @Autowired private AuctionLifecycleService lifecycleService;
    @Autowired private AuctionRepository auctionRepo;
    @Autowired private SchedulingAuctionRepository schedulingRepo;
    @Autowired private WeekRepository weekRepo;
    @Autowired private CapturingWriter writer;

    private final List<Long> createdAuctionIds = new ArrayList<>();

    @BeforeEach
    void resetCapture() {
        writer.reset(1);
    }

    @AfterEach
    void cleanup() {
        for (Long id : createdAuctionIds) {
            try {
                auctionRepo.deleteById(id);
            } catch (Exception ignored) {
                // tolerate cascade races
            }
        }
        createdAuctionIds.clear();
        writer.payloads.clear();
    }

    @Test
    @DisplayName("tick → RoundStartedEvent → push listener writes STARTED payload with aggregate fields")
    void tick_startsRound_pushesStartedPayload() throws InterruptedException {
        long weekId = firstWeekId();
        String expectedWeekDisplay = weekRepo.findById(weekId).orElseThrow().getWeekDisplay();

        Auction a = persistAuction(AuctionStatus.Scheduled, weekId, "IT snowflake-push " + System.nanoTime());
        SchedulingAuction r = persistRound(a.getId(), 1, SchedulingAuctionStatus.Scheduled,
                Instant.now().minusSeconds(10), Instant.now().plusSeconds(3600));

        lifecycleService.tick();

        assertThat(writer.latch.await(5, TimeUnit.SECONDS))
                .as("async push listener should fire within 5s of tick commit")
                .isTrue();
        assertThat(writer.payloads).hasSize(1);
        AuctionStatusPushPayload p = writer.payloads.get(0);
        assertThat(p.action()).isEqualTo(AuctionStatusAction.STARTED);
        assertThat(p.auctionId()).isEqualTo(a.getId());
        assertThat(p.weekId()).isEqualTo(weekId);
        assertThat(p.round()).isEqualTo(1);
        assertThat(p.auctionTitle()).isEqualTo(a.getAuctionTitle());
        assertThat(p.weekDisplay()).isEqualTo(expectedWeekDisplay);
        assertThat(p.actor()).isEqualTo("system");
        assertThat(p.transitionedAt()).isNotNull();

        // Scheduling round status actually flipped — proves the tick landed.
        assertThat(schedulingRepo.findById(r.getId()).orElseThrow().getRoundStatus())
                .isEqualTo(SchedulingAuctionStatus.Started);
    }

    private long firstWeekId() {
        return weekRepo.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("mdm.week is empty — V65 seed didn't run"))
                .getId();
    }

    private Auction persistAuction(AuctionStatus status, long weekId, String title) {
        Auction a = new Auction();
        a.setAuctionTitle(title);
        a.setAuctionStatus(status);
        a.setWeekId(weekId);
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
