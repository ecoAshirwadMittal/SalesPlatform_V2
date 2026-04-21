package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record BidRoundSummary(
        long id,
        long schedulingAuctionId,
        int round,
        String roundStatus,
        Instant startDatetime,
        Instant endDatetime,
        boolean submitted,
        Instant submittedDatetime
) {}
