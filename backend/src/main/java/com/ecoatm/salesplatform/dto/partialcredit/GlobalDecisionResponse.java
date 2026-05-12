package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * Wire shape returned by {@code POST .../{id}/decision}. Per-section
 * update counts plus the recomputed header summary.
 */
public record GlobalDecisionResponse(
        int missingUpdated,
        int wrongUpdated,
        int encumberedUpdated,
        HeaderSummaryDto summary) {
}
