package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.BidRankingRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidRankingService {

    private static final Logger log = LoggerFactory.getLogger(BidRankingService.class);
    private static final String PROCESS = "RANKING";

    private final BidRankingRepository repo;
    private final SchedulingAuctionRepository saRepo;
    private final RecalcStatusUpdater statusUpdater;
    private final ApplicationEventPublisher events;

    public BidRankingService(BidRankingRepository repo,
                             SchedulingAuctionRepository saRepo,
                             RecalcStatusUpdater statusUpdater,
                             ApplicationEventPublisher events) {
        this.repo = repo;
        this.saRepo = saRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    /**
     * Runs RANKING for a closed round. Throws
     * {@link RecalcAlreadyRunningException} if status already RUNNING.
     * Throws {@link IllegalArgumentException} for closed round &notin; {1,2}.
     * On any other failure, status flips to FAILED in a sub-tx and the
     * exception is rethrown.
     *
     * <p>{@code REQUIRES_NEW} so each process is independent of orchestrator
     * failure modes.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run(long schedulingAuctionId) {
        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + schedulingAuctionId));

        if (sa.getRound() != 1 && sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "RANKING only valid for closed round 1 or 2; was " + sa.getRound());
        }

        boolean flipped = statusUpdater.tryFlipToRunning(schedulingAuctionId, PROCESS);
        if (!flipped) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.RANKING, schedulingAuctionId);
        }

        long start = System.currentTimeMillis();
        int rows;
        try {
            rows = repo.rankClosedRound(schedulingAuctionId, sa.getRound());
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(schedulingAuctionId, PROCESS, msg);
            log.error("RANKING failed schedulingAuctionId={} round={}",
                schedulingAuctionId, sa.getRound(), ex);
            throw ex;
        }

        statusUpdater.markSuccess(schedulingAuctionId, PROCESS);

        long durationMs = System.currentTimeMillis() - start;
        log.info("RANKING success schedulingAuctionId={} round={} rows={} durationMs={}",
            schedulingAuctionId, sa.getRound(), rows, durationMs);

        Long weekId = saRepo.findWeekIdById(schedulingAuctionId);
        if (weekId == null) {
            throw new IllegalStateException(
                "Cannot resolve week_id for schedulingAuctionId=" + schedulingAuctionId);
        }
        events.publishEvent(new BidRankingUpdatedEvent(
            schedulingAuctionId, sa.getRound(), weekId, sa.getAuctionId()));
    }

    /**
     * Admin re-rank entry point. Same shape as {@link #run} but the caller
     * is a controller; just delegates.
     */
    public void recalculate(long schedulingAuctionId) {
        run(schedulingAuctionId);
    }
}
