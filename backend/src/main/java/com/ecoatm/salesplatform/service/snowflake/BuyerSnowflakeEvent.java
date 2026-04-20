package com.ecoatm.salesplatform.service.snowflake;

public sealed interface BuyerSnowflakeEvent {

    long buyerId();

    record BuyerSaved(long buyerId) implements BuyerSnowflakeEvent {}

    record AllBuyersSync() implements BuyerSnowflakeEvent {
        @Override
        public long buyerId() { return -1; }
    }
}
