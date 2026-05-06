package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Replaces {@code BidRankingStubListener}. Triggers RANKING + TARGET_PRICE
 * on round close for round ∈ {1, 2}.
 *
 * <p>Listener thread is the cron-tick post-commit thread. The orchestrator
 * swallows per-process failures; this listener also catches anything
 * unexpected (wiring bug) so it never propagates to other AFTER_COMMIT
 * listeners on the same event (e.g. {@code R3PreProcessStubListener}).
 */
@Component
public class RecalcRoundClosedListener {

    private static final Logger log = LoggerFactory.getLogger(RecalcRoundClosedListener.class);

    private final RecalcOrchestrator orchestrator;

    public RecalcRoundClosedListener(RecalcOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 1 && event.round() != 2) {
            return;
        }
        try {
            orchestrator.runForClosedRound(event.roundId());
        } catch (RuntimeException ex) {
            log.error("Recalc orchestrator threw unexpectedly roundId={}",
                event.roundId(), ex);
        }
    }
}
