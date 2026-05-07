package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3InitCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.ScheduleAuctionInitStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sub-project 6 — R3 init orchestrator.
 *
 * <p>Gated on {@code r3_preprocess_status = SUCCESS}; flips {@code round3_init_status}
 * to {@link ScheduleAuctionInitStatus#Complete} and publishes
 * {@link R3InitCompletedEvent}.
 *
 * <p>{@code REQUIRES_NEW} so each invocation is independent of the caller's
 * transaction context. On {@code RuntimeException}, status flips to FAILED in
 * {@link RecalcStatusUpdater#markFailed} (its own REQUIRES_NEW sub-tx so the row
 * survives parent rollback) and the exception is rethrown.
 */
@Service
public class R3InitService {

    private static final String PROCESS = "R3_INIT";
    private static final Logger log = LoggerFactory.getLogger(R3InitService.class);

    private final SchedulingAuctionRepository saRepo;
    private final RecalcStatusUpdater         statusUpdater;
    private final ApplicationEventPublisher   events;

    public R3InitService(SchedulingAuctionRepository saRepo,
                         RecalcStatusUpdater statusUpdater,
                         ApplicationEventPublisher events) {
        this.saRepo = saRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    /**
     * Runs R3 init for a given R3 scheduling-auction.
     *
     * @param r3SaId  the id of the round-3 scheduling auction
     * @return        duration result
     * @throws EntityNotFoundException        SA id unknown
     * @throws IllegalArgumentException       SA is not round 3
     * @throws IllegalStateException          predecessor r3_preprocess_status is not SUCCESS
     * @throws RecalcAlreadyRunningException  another caller already flipped to RUNNING
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R3InitResult run(long r3SaId) {
        SchedulingAuction sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + r3SaId));

        if (sa.getRound() != 3) {
            throw new IllegalArgumentException(
                "R3 init only valid for round 3; was " + sa.getRound());
        }

        // Predecessor guard — r3_preprocess_status must be SUCCESS
        if (sa.getR3PreprocessStatus() != RecalcStatus.SUCCESS) {
            throw new IllegalStateException(
                "R3 init refused — r3_preprocess_status is " + sa.getR3PreprocessStatus()
                    + " (expected SUCCESS)");
        }

        if (!statusUpdater.tryFlipToRunning(r3SaId, PROCESS)) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_INIT, r3SaId);
        }

        long start = System.currentTimeMillis();
        try {
            sa.setRound3InitStatus(ScheduleAuctionInitStatus.Complete);
            saRepo.save(sa);

            // TODO(gap-analysis #4): R3 start-notification email
            // TODO(gap-analysis #9): SUB_Round3SendAuctionToSnowflake dedicated push

            statusUpdater.markSuccess(r3SaId, PROCESS);
            long durationMs = System.currentTimeMillis() - start;
            log.info("R3_INIT success r3SaId={} ms={}", r3SaId, durationMs);

            events.publishEvent(new R3InitCompletedEvent(r3SaId, sa.getAuctionId()));
            return new R3InitResult(durationMs);
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(r3SaId, PROCESS, msg);
            log.error("R3_INIT failed r3SaId={}", r3SaId, ex);
            throw ex;
        }
    }

    /**
     * Admin-recovery entrypoint — delegates to {@link #run(long)}.
     *
     * <p>{@code REQUIRES_NEW} here ensures an active transaction exists when
     * {@code run()} is called via direct self-invocation (Spring AOP does not
     * proxy self-calls). {@link com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater}
     * methods declared {@code MANDATORY} (e.g. {@code markSuccess}) see this
     * transaction.
     *
     * @param r3SaId  the id of the round-3 scheduling auction
     * @return        duration result
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R3InitResult recalculate(long r3SaId) {
        return run(r3SaId);
    }
}
