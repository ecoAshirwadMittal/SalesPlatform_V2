package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Response for {@code GET /api/v1/admin/auctions/{id}/schedule-defaults}.
 *
 * <p>Mirrors the Mendix {@code ACT_LoadScheduleAuction_Helper} +
 * {@code ACT_Create_SchedulingAuction_Helper_Default} pair:
 * <ul>
 *   <li>When the auction already has scheduling rows, start/end come from
 *       the stored rows and {@code roundNActive} reflects the current
 *       {@code has_round} / {@code status} of round 2 and 3.</li>
 *   <li>When the auction has no rounds (fresh schedule), values are computed
 *       from the parent {@code mdm.week} using the Mendix offset math and the
 *       config minute offsets.</li>
 * </ul>
 *
 * <p>{@code round2MinutesOffset} / {@code round3MinutesOffset} are returned
 * so the frontend can recompute R2/R3 {@code From} values locally when the
 * previous round's {@code End} changes, without a round trip.
 */
public record ScheduleDefaultsResponse(
        Instant round1Start,
        Instant round1End,
        Instant round2Start,
        Instant round2End,
        Instant round3Start,
        Instant round3End,
        boolean round2Active,
        boolean round3Active,
        int round2MinutesOffset,
        int round3MinutesOffset) {}
