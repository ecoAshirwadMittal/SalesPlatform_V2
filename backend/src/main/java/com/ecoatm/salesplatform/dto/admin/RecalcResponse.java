package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

public record RecalcResponse(
        long schedulingAuctionId,
        int closedRound,
        String status,        // "SUCCESS"
        String error,         // always null on success path; FAILED is reflected via 5xx mapping
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        int rowsAffected,
        long durationMs) {}
