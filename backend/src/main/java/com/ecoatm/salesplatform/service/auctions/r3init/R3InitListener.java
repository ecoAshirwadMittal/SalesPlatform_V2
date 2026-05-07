package com.ecoatm.salesplatform.service.auctions.r3init;

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
 * Sub-project 6 — bridges {@link RoundStartedEvent}(round=3) to
 * {@link R3InitService#run(long)}.
 *
 * <p>Mirrors {@link com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentListener}:
 * AFTER_COMMIT semantics so a listener throwing does NOT roll back the
 * round transition; {@code @Async("snowflakeExecutor")} so the cron tick
 * thread is not blocked on the per-SA work.
 *
 * <p>Replaces {@code R3InitStubListener} (deleted in Task 15).
 */
@Component
@ConditionalOnProperty(
    name = "auctions.r3-init.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class R3InitListener {

    private static final Logger log = LoggerFactory.getLogger(R3InitListener.class);

    private final R3InitService service;

    public R3InitListener(R3InitService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 3) {
            return;
        }
        long r3SaId = event.roundId();
        try {
            service.run(r3SaId);
        } catch (RecalcAlreadyRunningException ex) {
            log.warn("R3_INIT skipped — already running r3SaId={}", r3SaId);
        } catch (IllegalStateException ex) {
            log.error("R3_INIT refused (predecessor guard) r3SaId={} — {}", r3SaId, ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("R3_INIT failed r3SaId={} error={}", r3SaId, ex.toString(), ex);
        }
    }
}
