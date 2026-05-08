package com.ecoatm.salesplatform.dto;

import java.util.List;

/**
 * Response for {@code GET /api/v1/admin/auctions/{id}} and reused as the
 * body for Save Schedule / Unschedule endpoints.
 *
 * <p>{@code rounds} is an empty list when the auction is {@code Unscheduled}
 * (Mendix invariant — no rounds exist yet). Once {@code Scheduled}, exactly
 * three rounds are returned in ascending order.
 *
 * <p>{@code roundStats} (gap H5) is parallel to {@code rounds}: per-round
 * Buyers / Total / DW-Only counts the schedule editor renders inline next
 * to each round header. Empty list when the auction is Unscheduled.
 */
public record AuctionDetailResponse(
        Long id,
        String auctionTitle,
        String auctionStatus,
        Long weekId,
        String weekDisplay,
        List<RoundView> rounds,
        List<RoundStatsView> roundStats) {}
