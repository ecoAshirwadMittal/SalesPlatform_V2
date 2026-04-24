package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface ReserveBidSnowflakeWriter {

    /** Returns the number of rows accepted by the stored proc (best effort). */
    int upsert(ReserveBidSnowflakePayload payload);

    int delete(ReserveBidSnowflakePayload payload);
}
