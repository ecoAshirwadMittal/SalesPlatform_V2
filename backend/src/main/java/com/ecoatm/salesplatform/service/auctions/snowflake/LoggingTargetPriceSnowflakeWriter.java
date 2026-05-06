package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(prefix = "recalc.snowflake", name = "target-price-writer",
                       havingValue = "logging", matchIfMissing = true)
public class LoggingTargetPriceSnowflakeWriter implements TargetPriceSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingTargetPriceSnowflakeWriter.class);

    @Override
    public void pushTargetPrices(long weekId, int targetRound) {
        log.info("[snowflake-target-price] LOGGING IMPL — would push weekId={} round={}", weekId, targetRound);
    }
}
