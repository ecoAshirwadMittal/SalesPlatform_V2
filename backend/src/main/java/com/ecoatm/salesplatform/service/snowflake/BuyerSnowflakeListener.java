package com.ecoatm.salesplatform.service.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "buyers.snowflake.enabled", havingValue = "true")
public class BuyerSnowflakeListener {

    private static final Logger log = LoggerFactory.getLogger(BuyerSnowflakeListener.class);

    private final BuyerSnowflakeSyncService syncService;

    public BuyerSnowflakeListener(BuyerSnowflakeSyncService syncService) {
        this.syncService = syncService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBuyerSaved(BuyerSnowflakeEvent.BuyerSaved event) {
        log.debug("Buyer saved event received: buyerId={}", event.buyerId());
        syncService.syncBuyer(event.buyerId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAllBuyersSync(BuyerSnowflakeEvent.AllBuyersSync event) {
        log.debug("All buyers sync event received");
        syncService.syncAllBuyers();
    }
}
