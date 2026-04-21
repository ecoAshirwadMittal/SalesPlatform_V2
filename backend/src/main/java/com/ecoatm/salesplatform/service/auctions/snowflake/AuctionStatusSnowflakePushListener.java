package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.Instant;

/**
 * Consumes {@link RoundStartedEvent} / {@link RoundClosedEvent} emitted by
 * {@code AuctionLifecycleService.tick()} after the round-transition tx
 * commits, re-fetches the auction + week aggregate in a short read-only
 * {@code REQUIRES_NEW} tx, and hands a fully-formed
 * {@link AuctionStatusPushPayload} to the {@link AuctionStatusSnowflakeWriter}.
 *
 * <p>Replaces the sub-project 0 {@code SnowflakePushStubListener}. The writer
 * is backed by {@link LoggingAuctionStatusSnowflakeWriter} in Phase 1 — the
 * real Snowflake-bound writer will drop in later without listener changes.
 *
 * <p>Feature flag {@code auctions.snowflake-push.enabled} defaults to {@code
 * false}. When disabled, the listener short-circuits before any DB read.
 *
 * <p>Writer exceptions are caught and logged; they never bubble into Spring's
 * async executor because a failed push must not wedge unrelated events.
 */
@Component
public class AuctionStatusSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(AuctionStatusSnowflakePushListener.class);

    // Cron-driven transitions have no user actor. Mendix microflow populates
    // this field from the session "Username var" — for automated ticks the
    // audit sink gets the literal "system".
    private static final String CRON_ACTOR = "system";

    private final AuctionRepository auctionRepo;
    private final WeekRepository weekRepo;
    private final AuctionStatusSnowflakeWriter writer;
    private final Clock clock;
    private final boolean enabled;

    public AuctionStatusSnowflakePushListener(
            AuctionRepository auctionRepo,
            WeekRepository weekRepo,
            AuctionStatusSnowflakeWriter writer,
            Clock clock,
            @Value("${auctions.snowflake-push.enabled:false}") boolean enabled) {
        this.auctionRepo = auctionRepo;
        this.weekRepo = weekRepo;
        this.writer = writer;
        this.clock = clock;
        this.enabled = enabled;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void onRoundStarted(RoundStartedEvent event) {
        push(AuctionStatusAction.STARTED, event.auctionId(), event.weekId(), event.round());
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void onRoundClosed(RoundClosedEvent event) {
        push(AuctionStatusAction.CLOSED, event.auctionId(), event.weekId(), event.round());
    }

    private void push(AuctionStatusAction action, long auctionId, long weekId, int round) {
        if (!enabled) {
            log.debug("auction-snowflake-push skipped (flag off) action={} auctionId={}", action, auctionId);
            return;
        }
        Auction auction = auctionRepo.findById(auctionId).orElse(null);
        Week week = weekRepo.findById(weekId).orElse(null);
        if (auction == null || week == null) {
            log.warn("auction-snowflake-push aggregate missing action={} auctionId={} weekId={}",
                    action, auctionId, weekId);
            return;
        }
        Instant transitionedAt = clock.instant();
        AuctionStatusPushPayload payload = new AuctionStatusPushPayload(
                auctionId,
                auction.getAuctionTitle(),
                weekId,
                week.getWeekDisplay(),
                round,
                action,
                transitionedAt,
                CRON_ACTOR);
        try {
            writer.push(payload);
        } catch (RuntimeException ex) {
            log.error("auction-snowflake-push writer failed action={} auctionId={}", action, auctionId, ex);
        }
    }
}
