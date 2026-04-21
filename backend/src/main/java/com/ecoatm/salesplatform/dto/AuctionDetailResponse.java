package com.ecoatm.salesplatform.dto;

import java.util.List;

/**
 * Response for {@code GET /api/v1/admin/auctions/{id}} and reused as the
 * body for Save Schedule / Unschedule endpoints.
 *
 * <p>{@code rounds} is an empty list when the auction is {@code Unscheduled}
 * (Mendix invariant — no rounds exist yet). Once {@code Scheduled}, exactly
 * three rounds are returned in ascending order.
 */
public record AuctionDetailResponse(
        Long id,
        String auctionTitle,
        String auctionStatus,
        Long weekId,
        String weekDisplay,
        List<RoundView> rounds) {}
