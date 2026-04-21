package com.ecoatm.salesplatform.service.auctions.snowflake;

import java.time.Instant;

/**
 * Immutable payload handed to {@link AuctionStatusSnowflakeWriter} whenever
 * an auction round transitions Scheduled→Started or Started→Closed.
 *
 * <p>This is the full contract the real Snowflake writer will consume; the
 * current {@link LoggingAuctionStatusSnowflakeWriter} just renders every
 * field so the Phase 2 swap is a drop-in replacement.
 *
 * @param auctionId      parent auction primary key
 * @param auctionTitle   human-readable auction title (e.g. "Auction 2026 / Wk17")
 * @param weekId         parent week primary key
 * @param weekDisplay    human-readable week label (e.g. "2026 / Wk17")
 * @param round          1 / 2 / 3
 * @param action         STARTED or CLOSED
 * @param transitionedAt instant the listener observed the transition (UTC)
 * @param actor          identity that drove the change; "system" for cron-driven flips
 */
public record AuctionStatusPushPayload(
        long auctionId,
        String auctionTitle,
        long weekId,
        String weekDisplay,
        int round,
        AuctionStatusAction action,
        Instant transitionedAt,
        String actor) {
}
