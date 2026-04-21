package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Per-round projection returned inside {@link AuctionDetailResponse}.
 * Mirrors the {@code SchedulingAuction} row with the small set of fields
 * the scheduling page consumes.
 *
 * <p>{@code hasRound} reflects the Mendix {@code HasRound} flag — admin
 * toggles for round 2/3 translate to this; round 1 is always {@code true}.
 */
public record RoundView(
        Long id,
        int round,
        String name,
        Instant startDatetime,
        Instant endDatetime,
        String roundStatus,
        boolean hasRound) {}
