package com.ecoatm.salesplatform.listener.rma;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.service.rma.RmaOracleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Creates the Oracle RMA order once an RMA review commits as
 * {@link RmaReviewOutcome#APPROVED}. This is the event-driven replacement for
 * the synchronous {@code SUB_RMA_PrepareContentAndSendToOracle} call inside the
 * legacy {@code ACT_RMADetails_CompleteReview} — decoupled so the Oracle
 * round-trip runs off the admin request thread and can never roll the review
 * back.
 *
 * <p>Follows the same {@code AFTER_COMMIT} + {@code @Async} shape as
 * {@code ReviewCompletedEmailListener}: it fires only after the review-completion
 * transaction has durably committed, and runs on the dedicated
 * {@link AsyncConfig#ORACLE_EXECUTOR} pool so the admin UI response is not
 * blocked.
 *
 * <p><b>Transaction shape (the readOnly-tx gotcha).</b> The actual
 * {@code oracle_*} column write happens in
 * {@link RmaOracleService#createRmaInOracle}, which is
 * {@code @Transactional(REQUIRES_NEW)} and deliberately NOT {@code readOnly}
 * (it writes). Placing that boundary on the service rather than this listener
 * (a) keeps a single transactional write path shared with the
 * {@code /resubmit-oracle} recovery endpoint, and (b) gives clean exception
 * isolation — the service's own {@code REQUIRES_NEW} transaction commits or
 * rolls back at the service-method boundary, so any failure surfaces here to be
 * logged and swallowed rather than escaping to the async executor.
 *
 * <p>Failure never affects the review: {@link RmaOracleService} records a failed
 * Oracle status ({@code is_successful = false}) rather than throwing, and any
 * exception that does escape (e.g. an infrastructure error) is caught here. The
 * {@code /resubmit-oracle} endpoint recovers a failed create.
 *
 * <p>Gated by {@code rma.oracle-create.enabled} (default {@code true}) so ops
 * can disable auto-create in an environment while leaving the manual resubmit
 * path working. The listener still subscribes when disabled so it can log the
 * intent — flipping the flag needs no bean restart.
 */
@Component
public class RmaOracleCreateListener {

    private static final Logger log = LoggerFactory.getLogger(RmaOracleCreateListener.class);

    private final RmaOracleService rmaOracleService;
    private final boolean enabled;

    public RmaOracleCreateListener(
            RmaOracleService rmaOracleService,
            @Value("${rma.oracle-create.enabled:true}") boolean enabled) {
        this.rmaOracleService = rmaOracleService;
        this.enabled = enabled;
    }

    /**
     * React to an APPROVED {@link RmaReviewCompletedEvent} by creating the RMA
     * in Oracle. Runs on {@link AsyncConfig#ORACLE_EXECUTOR} after the review
     * commits. All exceptions are caught and logged — a failure here must never
     * affect the already-final review.
     */
    @Async(AsyncConfig.ORACLE_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRmaReviewCompleted(RmaReviewCompletedEvent event) {
        try {
            handle(event);
        } catch (Exception ex) {
            log.error("Oracle RMA create failed for rmaId={}: {}",
                    event.rmaId(), ex.getMessage(), ex);
        }
    }

    private void handle(RmaReviewCompletedEvent event) {
        if (!enabled) {
            log.info("[RmaOracleCreateListener] (disabled) would create Oracle RMA for rmaId={}",
                    event.rmaId());
            return;
        }
        if (event.outcome() != RmaReviewOutcome.APPROVED) {
            log.debug("RMA Oracle create skipped: non-approved outcome={} for rmaId={}",
                    event.outcome(), event.rmaId());
            return;
        }
        Long rmaId = event.rmaId();
        if (rmaId == null) {
            log.warn("RmaReviewCompletedEvent published with null rmaId — skipping Oracle create");
            return;
        }
        rmaOracleService.createRmaInOracle(rmaId);
    }
}
