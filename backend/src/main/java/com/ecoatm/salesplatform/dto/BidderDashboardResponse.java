package com.ecoatm.salesplatform.dto;

import java.util.List;

public record BidderDashboardResponse(
        String mode,
        SchedulingAuctionSummary auction,
        BidRoundSummary bidRound,
        List<BidDataRow> rows,
        BidDataTotals totals,
        RoundTimerState timer
) {}
