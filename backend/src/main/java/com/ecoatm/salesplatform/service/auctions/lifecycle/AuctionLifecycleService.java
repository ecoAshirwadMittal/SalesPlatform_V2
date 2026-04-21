package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class AuctionLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(AuctionLifecycleService.class);

    private final SchedulingAuctionRepository schedulingRepo;
    private final RoundTransitionService roundTransitions;
    private final AuctionStatusReconciler statusReconciler;
    private final Clock clock;

    public AuctionLifecycleService(SchedulingAuctionRepository schedulingRepo,
                                   RoundTransitionService roundTransitions,
                                   AuctionStatusReconciler statusReconciler,
                                   Clock clock) {
        this.schedulingRepo = schedulingRepo;
        this.roundTransitions = roundTransitions;
        this.statusReconciler = statusReconciler;
        this.clock = clock;
    }

    /**
     * Single tick of the lifecycle cron. Opens NO transaction itself —
     * each per-row call uses {@code REQUIRES_NEW}.
     */
    public TickResult tick() {
        Instant now = clock.instant();
        TickCounters counters = new TickCounters();

        List<Long> toClose = schedulingRepo.findIdsToClose(now);
        for (Long roundId : toClose) {
            try {
                RoundClosedEvent event = roundTransitions.closeRound(roundId);
                counters.roundsClosed++;
                counters.affectedAuctions.add(event.auctionId());
            } catch (RoundAlreadyTransitionedException e) {
                log.debug("Round {} already transitioned (close), skipping", roundId);
            } catch (Exception e) {
                log.error("Failed to close round {}", roundId, e);
                counters.errorCount++;
            }
        }

        List<Long> toStart = schedulingRepo.findIdsToStart(now);
        for (Long roundId : toStart) {
            try {
                RoundStartedEvent event = roundTransitions.startRound(roundId);
                counters.roundsStarted++;
                counters.affectedAuctions.add(event.auctionId());
            } catch (RoundAlreadyTransitionedException e) {
                log.debug("Round {} already transitioned (start), skipping", roundId);
            } catch (Exception e) {
                log.error("Failed to start round {}", roundId, e);
                counters.errorCount++;
            }
        }

        for (Long auctionId : counters.affectedAuctions) {
            try {
                statusReconciler.reconcile(auctionId);
            } catch (Exception e) {
                log.error("Failed to reconcile auction {}", auctionId, e);
                counters.errorCount++;
            }
        }

        return new TickResult(counters.toJson(), counters.errorCount);
    }
}
