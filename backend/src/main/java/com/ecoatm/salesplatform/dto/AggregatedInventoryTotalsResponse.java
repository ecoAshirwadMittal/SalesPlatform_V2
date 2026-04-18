package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AggregatedInventoryTotalsResponse(
        int totalQuantity,
        BigDecimal totalPayout,
        BigDecimal averageTargetPrice,
        int dwTotalQuantity,
        BigDecimal dwTotalPayout,
        BigDecimal dwAverageTargetPrice,
        Instant lastSyncedAt
) {}
