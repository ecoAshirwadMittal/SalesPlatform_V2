package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineKind;

/**
 * Wire shape returned by {@code POST .../sections/{kind}/decision}.
 * {@code updatedCount} is the number of lines that were flipped to
 * the new decision (Sprint 3 bulk applies to ALL lines, so this is
 * always the section's line count).
 */
public record SectionDecisionResponse(
        LineKind kind,
        int updatedCount,
        HeaderSummaryDto summary) {
}
