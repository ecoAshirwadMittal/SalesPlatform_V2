package com.ecoatm.salesplatform.event;

/**
 * Published AFTER_COMMIT of {@link com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService}
 * once the DENSE_RANK UPDATE succeeds.
 *
 * @param schedulingAuctionId the scheduling_auctions row that just closed
 * @param closedRound         1 or 2 — the round that closed; ranks were
 *                            written to round{closedRound + 1} columns
 * @param weekId              auctions.auctions.week_id (= mdm.week.id)
 * @param auctionId           parent auction id
 */
public record BidRankingUpdatedEvent(
        long schedulingAuctionId,
        int closedRound,
        long weekId,
        long auctionId) {}
