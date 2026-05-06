package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.TargetPriceRecalcRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TargetPriceRecalcService {

    private static final Logger log = LoggerFactory.getLogger(TargetPriceRecalcService.class);
    private static final String PROCESS = "TARGET_PRICE";

    private final TargetPriceRecalcRepository repo;
    private final SchedulingAuctionRepository saRepo;
    private final RecalcStatusUpdater statusUpdater;
    private final ApplicationEventPublisher events;

    public TargetPriceRecalcService(TargetPriceRecalcRepository repo,
                                    SchedulingAuctionRepository saRepo,
                                    RecalcStatusUpdater statusUpdater,
                                    ApplicationEventPublisher events) {
        this.repo = repo;
        this.saRepo = saRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    /**
     * Runs TARGET_PRICE for a closed round. Pre-check ordering mirrors
     * {@link BidRankingService#run}: load SA → round-validate → resolve weekId
     * before flipping status, so any pre-check failure leaves status untouched.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RecalcResult run(long schedulingAuctionId) {
        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + schedulingAuctionId));

        if (sa.getRound() != 1 && sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "TARGET_PRICE only valid for closed round 1 or 2; was " + sa.getRound());
        }

        Long weekId = saRepo.findWeekIdById(schedulingAuctionId);
        if (weekId == null) {
            throw new IllegalStateException(
                "Cannot resolve week_id for schedulingAuctionId=" + schedulingAuctionId);
        }

        boolean flipped = statusUpdater.tryFlipToRunning(schedulingAuctionId, PROCESS);
        if (!flipped) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.TARGET_PRICE, schedulingAuctionId);
        }

        long start = System.currentTimeMillis();
        int rows;
        try {
            rows = repo.recalcClosedRound(schedulingAuctionId, sa.getRound());
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(schedulingAuctionId, PROCESS, msg);
            log.error("TARGET_PRICE failed schedulingAuctionId={} round={}",
                schedulingAuctionId, sa.getRound(), ex);
            throw ex;
        }

        statusUpdater.markSuccess(schedulingAuctionId, PROCESS);

        long durationMs = System.currentTimeMillis() - start;
        log.info("TARGET_PRICE success schedulingAuctionId={} round={} rows={} durationMs={}",
            schedulingAuctionId, sa.getRound(), rows, durationMs);

        events.publishEvent(new TargetPriceRecalculatedEvent(
            schedulingAuctionId, sa.getRound(), weekId, sa.getAuctionId()));

        return new RecalcResult(sa.getRound(), rows);
    }

    public RecalcResult recalculate(long schedulingAuctionId) {
        return run(schedulingAuctionId);
    }
}
