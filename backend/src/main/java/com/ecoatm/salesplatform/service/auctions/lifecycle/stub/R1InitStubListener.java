package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 3 will replace this with the real Round 1 init logic
 * (per Mendix SUB_InitializeRound1). Until then, this listener only logs.
 */
@Component
public class R1InitStubListener {

    private static final Logger log = LoggerFactory.getLogger(R1InitStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 1) return;
        log.info("[stub] R1 init — would snapshot inventory auctionId={} weekId={} roundId={}",
                event.auctionId(), event.weekId(), event.roundId());
    }
}
