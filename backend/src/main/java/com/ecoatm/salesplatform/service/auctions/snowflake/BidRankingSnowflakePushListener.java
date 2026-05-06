package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BidRankingSnowflakePushListener {

    private static final Logger log = LoggerFactory.getLogger(BidRankingSnowflakePushListener.class);

    private final BidRankingSnowflakeWriter writer;
    private final Environment env;

    public BidRankingSnowflakePushListener(BidRankingSnowflakeWriter writer, Environment env) {
        this.writer = writer;
        this.env = env;
    }

    @Async("snowflakeExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBidRankingUpdated(BidRankingUpdatedEvent event) {
        if (!Boolean.TRUE.equals(env.getProperty("snowflake.enabled", Boolean.class, false))) {
            log.info("[snowflake] disabled; skipping bid-ranking push for event={}", event);
            return;
        }
        try {
            writer.pushBidRankings(event.weekId(), event.closedRound() + 1);
            log.info("[snowflake] bid-ranking pushed weekId={} targetRound={}",
                event.weekId(), event.closedRound() + 1);
        } catch (RuntimeException ex) {
            log.error("[snowflake] bid-ranking push failed for event={}", event, ex);
            // future: write a FAILED row to integration.snowflake_sync_log
        }
    }
}
