package com.ecoatm.salesplatform.service.auctions.r3init;

public record R3PreProcessResult(
    int qualifiedCount,
    int specialTreatmentCount,
    int notQualifiedCount,
    int reportRowCount,
    int deletedBidsCount,
    long durationMs,
    boolean skipped
) {}
