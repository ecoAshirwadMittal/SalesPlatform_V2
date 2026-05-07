package com.ecoatm.salesplatform.event;

public record R3PreProcessCompletedEvent(
    long schedulingAuctionId,
    long auctionId,
    int qualifiedCount,
    int specialTreatmentCount,
    int reportRowCount
) {}
