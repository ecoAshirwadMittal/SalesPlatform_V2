package com.ecoatm.salesplatform.dto;

public record AggregatedInventoryUpdateRequest(
        String mergedGrade,
        boolean datawipe,
        int totalQuantity,
        int dwTotalQuantity
) {}
