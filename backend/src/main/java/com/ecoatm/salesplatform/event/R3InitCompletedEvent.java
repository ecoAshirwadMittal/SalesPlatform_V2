package com.ecoatm.salesplatform.event;

public record R3InitCompletedEvent(
    long schedulingAuctionId,
    long auctionId
) {}
