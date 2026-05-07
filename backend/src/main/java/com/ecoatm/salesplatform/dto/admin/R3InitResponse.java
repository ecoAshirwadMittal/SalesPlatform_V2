package com.ecoatm.salesplatform.dto.admin;

import java.time.OffsetDateTime;

/**
 * Response body for {@code POST /api/v1/admin/auctions/scheduling-auctions/{id}/reinit-r3}.
 */
public record R3InitResponse(
    long schedulingAuctionId,
    String status,
    String error,
    OffsetDateTime startedAt,
    OffsetDateTime finishedAt,
    long durationMs
) {}
