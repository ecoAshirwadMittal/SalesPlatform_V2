package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 6 will replace this with the real R3 (Upsell) init
 * (per Mendix ACT_Round3_SetStarted).
 */
@Component
public class R3InitStubListener {

    private static final Logger log = LoggerFactory.getLogger(R3InitStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 3) return;
        log.info("[stub] R3 init — would set Upsell round started auctionId={} weekId={} roundId={}",
                event.auctionId(), event.weekId(), event.roundId());
    }
}
