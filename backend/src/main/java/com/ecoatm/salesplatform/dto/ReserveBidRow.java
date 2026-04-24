package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReserveBidRow(
        long id,
        String productId,
        String grade,
        String brand,
        String model,
        BigDecimal bid,
        Instant lastUpdateDatetime,
        BigDecimal lastAwardedMinPrice,
        String lastAwardedWeek,
        String bidValidWeekDate,
        Instant changedDate) {}
