package com.ecoatm.salesplatform.event;

/**
 * Published AFTER_COMMIT of {@link com.ecoatm.salesplatform.service.auctions.recalc.TargetPriceRecalcService}
 * once the GREATEST UPDATE succeeds.
 */
public record TargetPriceRecalculatedEvent(
        long schedulingAuctionId,
        int closedRound,
        long weekId,
        long auctionId) {}
