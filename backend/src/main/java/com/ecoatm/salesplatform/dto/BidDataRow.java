package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BidDataRow(
        long id,
        long bidRoundId,
        String ecoid,
        String mergedGrade,
        String buyerCodeType,
        Integer bidQuantity,
        BigDecimal bidAmount,
        BigDecimal targetPrice,
        Integer maximumQuantity,
        BigDecimal payout,
        Integer submittedBidQuantity,
        BigDecimal submittedBidAmount,
        Integer lastValidBidQuantity,
        BigDecimal lastValidBidAmount,
        Instant submittedDatetime,
        Instant changedDate
) {}
