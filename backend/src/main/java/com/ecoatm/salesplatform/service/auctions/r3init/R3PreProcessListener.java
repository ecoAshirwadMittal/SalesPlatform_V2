package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sub-project 6 — bridges {@link RoundClosedEvent}(round=2) to
 * {@link R3PreProcessService#run(long, long)}.
 *
 * <p>Mirrors {@link com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentListener}:
 * AFTER_COMMIT semantics so a listener throwing does NOT roll back the
 * round transition; {@code @Async("snowflakeExecutor")} so the cron tick
 * thread is not blocked on the per-SA work.
 *
 * <p>Replaces {@code R3PreProcessStubListener} (deleted in Task 14).
 */
@Component
@ConditionalOnProperty(
    name = "auctions.r3-preprocess.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class R3PreProcessListener {

    private static final Logger log = LoggerFactory.getLogger(R3PreProcessListener.class);

    private final R3PreProcessService service;
    private final SchedulingAuctionRepository saRepo;

    public R3PreProcessListener(R3PreProcessService service, SchedulingAuctionRepository saRepo) {
        this.service = service;
        this.saRepo = saRepo;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 2) {
            return;
        }
        long r2SaId = event.roundId();
        var r3SaId = saRepo.findByAuctionIdAndRound(event.auctionId(), 3)
            .map(sa -> sa.getId());
        if (r3SaId.isEmpty()) {
            log.info("R3_PREPROCESS not applicable — auctionId={} has no R3 SA", event.auctionId());
            return;
        }
        try {
            service.run(r2SaId, r3SaId.get());
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("R3_PREPROCESS skipped — already running r3SaId={}", r3SaId.get());
        } catch (RuntimeException ex) {
            log.error("R3_PREPROCESS failed r3SaId={} error={}", r3SaId.get(), ex.toString(), ex);
        }
    }
}
