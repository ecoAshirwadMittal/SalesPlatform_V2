package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ReserveBidRequest(
        @NotBlank String productId,
        @NotBlank String grade,
        String brand,
        String model,
        @NotNull @DecimalMin("0") BigDecimal bid,
        BigDecimal lastAwardedMinPrice,
        String lastAwardedWeek,
        String bidValidWeekDate) {}
