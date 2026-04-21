package com.ecoatm.salesplatform.service.auctions.lifecycle.stub;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * STUB — sub-project 1 will replace this with the real Snowflake push
 * (per Mendix SUB_SendAuctionAndSchedulingActionToSnowflake_async).
 */
@Component
public class SnowflakePushStubListener {

    private static final Logger log = LoggerFactory.getLogger(SnowflakePushStubListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        log.info("[stub] Snowflake push (started) — would push auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundClosed(RoundClosedEvent event) {
        log.info("[stub] Snowflake push (closed) — would push auctionId={} weekId={} round={}",
                event.auctionId(), event.weekId(), event.round());
    }
}
