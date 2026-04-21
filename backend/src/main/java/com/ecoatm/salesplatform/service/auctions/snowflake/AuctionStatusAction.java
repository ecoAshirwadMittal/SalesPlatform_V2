package com.ecoatm.salesplatform.service.auctions.snowflake;

/**
 * Direction of an auction round transition observed by the cron lifecycle
 * service and pushed to the Snowflake audit sink.
 *
 * <p>Mirrors the two Mendix callers of
 * {@code SUB_SendAuctionAndSchedulingActionToSnowflake_async}:
 * {@code ACT_SetAuctionScheduleStarted} and
 * {@code ACT_SetAuctionScheduleClosed}.
 */
public enum AuctionStatusAction {
    STARTED,
    CLOSED
}
