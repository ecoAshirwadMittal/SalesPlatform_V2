package com.ecoatm.salesplatform.service.auctions.biddata;

public record BidDataCreationResult(
        int rowsCreated,
        boolean skipped,
        long durationMs) {}
