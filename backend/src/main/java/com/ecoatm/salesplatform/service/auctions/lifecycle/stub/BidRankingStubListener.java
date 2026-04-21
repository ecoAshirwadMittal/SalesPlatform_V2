package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 4 will replace this with real bid ranking + target
 * price calc (per Mendix ACT_TriggerBidRankingCalculation_TryCatch and
 * ACT_CalculateTargetPrice).
 */
@Component
public class BidRankingStubListener {

    private static final Logger log = LoggerFactory.getLogger(BidRankingStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        if (event.round() != 1 && event.round() != 2) return;
        log.info("[stub] Bid ranking — would rank bids + calc target price auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }
}
