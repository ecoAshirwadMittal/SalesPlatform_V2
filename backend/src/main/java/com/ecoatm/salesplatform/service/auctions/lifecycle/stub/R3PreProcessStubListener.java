package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 6 will replace this with real R3 pre-process logic
 * (per Mendix SUB_Round3_PreProcessRoundData).
 */
@Component
public class R3PreProcessStubListener {

    private static final Logger log = LoggerFactory.getLogger(R3PreProcessStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 2 && event.round() != 3) return;
        log.info("[stub] R3 pre-process — would prep round-3 data auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }
}
