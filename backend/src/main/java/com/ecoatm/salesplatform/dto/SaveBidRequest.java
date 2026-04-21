package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record SaveBidRequest(
        Integer bidQuantity,
        BigDecimal bidAmount
) {}
