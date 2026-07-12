package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReserveBidRequest(
        @NotNull @Positive Long productId,
        @NotBlank String grade,
        String brand,
        String model,
        @NotNull @DecimalMin("0") BigDecimal bid,
        BigDecimal lastAwardedMinPrice,
        String lastAwardedWeek,
        String bidValidWeekDate) {}
