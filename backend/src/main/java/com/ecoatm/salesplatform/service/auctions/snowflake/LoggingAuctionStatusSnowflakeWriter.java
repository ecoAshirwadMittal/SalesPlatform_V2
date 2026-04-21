package com.ecoatm.salesplatform.service.auctions.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default Phase 1 writer: emits a structured INFO line containing every
 * payload field, prefixed with the {@code [deferred-writer]} marker so
 * grep-based monitoring can distinguish logging-only output from the real
 * Snowflake-backed writer that will land in Phase 2.
 */
@Component
public class LoggingAuctionStatusSnowflakeWriter implements AuctionStatusSnowflakeWriter {

    private static final Logger log = LoggerFactory.getLogger(LoggingAuctionStatusSnowflakeWriter.class);

    @Override
    public void push(AuctionStatusPushPayload p) {
        log.info("[deferred-writer] auction-snowflake-push action={} auctionId={} auctionTitle=\"{}\" "
                        + "weekId={} weekDisplay=\"{}\" round={} transitionedAt={} actor={}",
                p.action(), p.auctionId(), p.auctionTitle(),
                p.weekId(), p.weekDisplay(), p.round(),
                p.transitionedAt(), p.actor());
    }
}
