package com.ecoatm.salesplatform.dto;

import java.time.Instant;

/**
 * Row shape for the admin Auctions grid (QA parity with
 * {@code Mx_Admin.Pages.Auctions}). {@code roundCount} is 0 for
 * Unscheduled auctions and 3 once Scheduled/Started/Closed.
 */
public record AuctionListRow(
        Long id,
        String auctionTitle,
        String auctionStatus,
        Long weekId,
        String weekDisplay,
        Instant createdDate,
        Instant changedDate,
        String createdBy,
        String updatedBy,
        int roundCount
) {}
