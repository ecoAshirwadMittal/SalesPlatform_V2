package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record BidDataTotals(
        int rowCount,
        BigDecimal totalBidAmount,
        BigDecimal totalPayout,
        int totalBidQuantity
) {}
