package com.ecoatm.salesplatform.service.auctions.snowflake;

/**
 * Abstraction over the Snowflake audit sink for auction round transitions.
 *
 * <p>Phase 1 ships {@link LoggingAuctionStatusSnowflakeWriter} as the default
 * implementation — it logs a structured line with every payload field. Phase 2
 * will add a JDBC-backed implementation; swapping is a single bean override
 * behind a profile or feature flag.
 */
public interface AuctionStatusSnowflakeWriter {

    void push(AuctionStatusPushPayload payload);
}
