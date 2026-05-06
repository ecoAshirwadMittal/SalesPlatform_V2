package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "bid-ranking-writer",
                       havingValue = "logging", matchIfMissing = true)
public class LoggingBidRankingSnowflakeWriter implements BidRankingSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingBidRankingSnowflakeWriter.class);

    @Override
    public void pushBidRankings(long weekId, int targetRound) {
        log.info("[snowflake-bid-ranking] LOGGING IMPL — would push weekId={} round={}", weekId, targetRound);
    }
}
