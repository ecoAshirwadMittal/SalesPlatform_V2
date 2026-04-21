package com.ecoatm.salesplatform.service.auctions.r1init;

public record Round1InitializationResult(
        long schedulingAuctionId,
        long auctionId,
        long weekId,
        int clampedNonDw,
        int clampedDw,
        int qbcsCreated,
        long durationMs
) {}
