package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * Wire shape returned by per-line / per-section / encumbered-fields
 * endpoints. Carries the updated line projection plus the recomputed
 * header summary so the frontend can refresh the totals panel without a
 * second round-trip.
 */
public record LineDecisionResponse(AdminLineProjection line, HeaderSummaryDto summary) {
}
