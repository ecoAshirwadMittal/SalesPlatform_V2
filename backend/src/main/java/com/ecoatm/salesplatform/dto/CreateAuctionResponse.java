package com.ecoatm.salesplatform.dto;

public record CreateAuctionResponse(
        Long id,
        String auctionTitle,
        String auctionStatus,
        Long weekId,
        String weekDisplay) {}
