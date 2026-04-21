package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Row shape for the admin Scheduling Auctions grid (QA parity with
 * {@code Mx_Admin.Pages.Scheduling_Auctions}). Each row is one round
 * of a parent auction; three rounds per scheduled auction.
 */
public record SchedulingAuctionListRow(
        Long id,
        Long auctionId,
        String auctionTitle,
        String auctionWeekYear,
        int round,
        String name,
        Instant startDatetime,
        Instant endDatetime,
        String roundStatus,
        boolean hasRound
) {}
