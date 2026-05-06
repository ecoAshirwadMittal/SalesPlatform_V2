package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sub-project 5 — bridges {@link RoundStartedEvent}(round=2) to
 * {@link R2BuyerAssignmentService#run(long)}.
 *
 * <p>Mirrors {@link com.ecoatm.salesplatform.service.auctions.r1init.R1InitListener}:
 * AFTER_COMMIT semantics so a listener throwing does NOT roll back the
 * round transition; {@code @Async("snowflakeExecutor")} so the cron tick
 * thread is not blocked on the per-SA work.
 *
 * <p>Replaces {@code R2InitStubListener} (deleted in Task 12).
 */
@Component
@ConditionalOnProperty(name = "auctions.r2-init.enabled", havingValue = "true", matchIfMissing = true)
public class R2BuyerAssignmentListener {

    private static final Logger log = LoggerFactory.getLogger(R2BuyerAssignmentListener.class);

    private final R2BuyerAssignmentService service;

    public R2BuyerAssignmentListener(R2BuyerAssignmentService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 2) {
            return;
        }
        try {
            service.run(event.roundId());
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("r2-init skipped — already running schedulingAuctionId={}", event.roundId());
        } catch (RuntimeException ex) {
            log.error("r2-init failed schedulingAuctionId={} error={}",
                event.roundId(), ex.toString(), ex);
        }
    }
}
