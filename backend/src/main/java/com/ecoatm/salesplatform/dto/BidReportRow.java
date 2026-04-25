package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Single row in the R3 bid report. Carries bid_data columns plus the parent
 * auction / scheduling_auction identifiers so the UI can show context.
 */
public record BidReportRow(
        long id,
        long bidRoundId,
        Long auctionId,
        long schedulingAuctionId,
        long buyerCodeId,
        String ecoid,
        String mergedGrade,
        String buyerCodeType,
        Integer bidQuantity,
        BigDecimal bidAmount,
        Integer submittedBidQuantity,
        BigDecimal submittedBidAmount,
        BigDecimal targetPrice,
        Integer maximumQuantity,
        Instant submittedDatetime,
        Instant changedDate
) {}
