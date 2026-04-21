package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Body for {@code PUT /api/v1/admin/auctions/{id}/schedule}. Ports the
 * Mendix {@code SchedulingAuction_Helper} transient object.
 *
 * <p>Round 1 is always active (Mendix: {@code Round1_Status=Scheduled},
 * no toggle). Rounds 2 and 3 carry an active flag that maps to
 * {@code has_round} on the persisted row; inactive rounds still save their
 * computed start/end times so the round-edit modal can reveal them later.
 */
public record ScheduleAuctionRequest(
        Instant round1Start,
        Instant round1End,
        Instant round2Start,
        Instant round2End,
        boolean round2Active,
        Instant round3Start,
        Instant round3End,
        boolean round3Active) {}
