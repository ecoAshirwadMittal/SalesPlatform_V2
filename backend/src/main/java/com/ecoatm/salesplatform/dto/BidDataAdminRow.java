package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Compact bid_data row for the admin Bid Data list. Excludes the MDM display
 * fields (brand/model/etc.) used by the bidder grid — admins filter by
 * (round, buyerCode) and only need the bid columns + the soft-delete flag.
 */
public record BidDataAdminRow(
        long id,
        long bidRoundId,
        long buyerCodeId,
        String ecoid,
        String mergedGrade,
        Integer bidQuantity,
        BigDecimal bidAmount,
        Integer submittedBidQuantity,
        BigDecimal submittedBidAmount,
        Instant submittedDatetime,
        Instant changedDate,
        boolean deprecated
) {}
