package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface TargetPriceSnowflakeWriter {
    void pushTargetPrices(long weekId, int targetRound);
}
