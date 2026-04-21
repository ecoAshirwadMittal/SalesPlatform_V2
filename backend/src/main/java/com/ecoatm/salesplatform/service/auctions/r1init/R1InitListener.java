package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "auctions.r1-init.enabled", havingValue = "true", matchIfMissing = true)
public class R1InitListener {

    private static final Logger log = LoggerFactory.getLogger(R1InitListener.class);

    private final Round1InitializationService service;

    public R1InitListener(Round1InitializationService service) {
        this.service = service;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoundStarted(RoundStartedEvent event) {
        if (event.round() != 1) {
            return;
        }
        try {
            service.initialize(event.roundId());
        } catch (SchedulingAuctionNotFoundException ex) {
            log.warn("r1-init skipped auctionId={} schedulingAuctionId={} reason=SCHEDULING_AUCTION_NOT_FOUND",
                    event.auctionId(), event.roundId());
        } catch (RuntimeException ex) {
            log.error("r1-init failed auctionId={} schedulingAuctionId={} weekId={} error={}",
                    event.auctionId(), event.roundId(), event.weekId(), ex.toString(), ex);
        }
    }
}
