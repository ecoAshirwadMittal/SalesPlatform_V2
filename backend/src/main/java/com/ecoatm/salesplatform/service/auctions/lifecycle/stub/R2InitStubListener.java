package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 5 will replace this with real R2 buyer assignment
 * (per Mendix SUB_AssignRoundTwoBuyers + Sub_ProcessSpecialBuyers).
 */
@Component
public class R2InitStubListener {

    private static final Logger log = LoggerFactory.getLogger(R2InitStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 2) return;
        log.info("[stub] R2 init — would assign R2 buyers + process special buyers auctionId={} weekId={} roundId={}",
                event.auctionId(), event.weekId(), event.roundId());
    }
}
