package com.ecoatm.salesplatform.dto;

import java.time.Instant;
import java.util.List;

public record CreateAuctionResponse(
        Long id,
        String auctionTitle,
        String auctionStatus,
        Long weekId,
        String weekDisplay,
        List<Round> rounds) {

    public record Round(
            Long id,
            int round,
            Instant startDatetime,
            Instant endDatetime,
            String status) {}
}
