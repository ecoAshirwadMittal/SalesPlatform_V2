package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record AggregatedInventoryRow(
        Long id,
        String ecoid2,
        String mergedGrade,
        String brand,
        String model,
        String name,
        String carrier,
        int dwTotalQuantity,
        BigDecimal dwAvgTargetPrice,
        int totalQuantity,
        BigDecimal avgTargetPrice
) {}
