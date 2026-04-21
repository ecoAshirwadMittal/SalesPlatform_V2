package com.ecoatm.salesplatform.dto;

public record SchedulingAuctionSummary(
        long id,
        long auctionId,
        String auctionTitle,
        int round,
        String roundName,
        String status
) {}
