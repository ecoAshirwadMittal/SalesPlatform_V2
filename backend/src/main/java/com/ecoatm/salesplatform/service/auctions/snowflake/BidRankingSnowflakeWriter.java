package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface BidRankingSnowflakeWriter {
    /**
     * Push the full {@code (weekId, targetRound)} slice of bid ranks to
     * Snowflake AUCTIONS.BUYER_BID. Implementations decide how — JDBC MERGE
     * for prod; logging-only for dev/test.
     */
    void pushBidRankings(long weekId, int targetRound);
}
