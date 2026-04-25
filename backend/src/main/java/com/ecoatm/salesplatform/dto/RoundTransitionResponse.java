package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Response shape for admin round-transition endpoints
 * (POST /admin/scheduling-auctions/{id}/start and /close).
 */
public record RoundTransitionResponse(
        Long id,
        int round,
        String roundStatus,
        Instant changedDate
) {}
