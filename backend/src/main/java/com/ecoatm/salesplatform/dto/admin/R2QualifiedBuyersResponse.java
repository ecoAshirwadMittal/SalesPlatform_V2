package com.ecoatm.salesplatform.dto.admin;

import java.util.List;

/**
 * Response envelope for the R2 qualified-buyer-codes admin view (gap H10).
 *
 * <p>Includes pre-computed counts so the page header can render
 * "Qualified: N / Special: M / Not Qualified: K" without re-summing
 * client-side.
 */
public record R2QualifiedBuyersResponse(
        Long auctionId,
        String auctionTitle,
        Long r2SchedulingAuctionId,
        String r2InitStatus,
        int totalRows,
        int qualifiedCount,
        int specialTreatmentCount,
        int notQualifiedCount,
        List<R2QualifiedBuyerRow> rows
) {}
