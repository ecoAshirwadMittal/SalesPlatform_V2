package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;

import java.time.Instant;

/**
 * Wire shape returned by {@code POST .../{id}/complete-review}. The
 * caller (admin UI) uses this to flip the page state to "completed" and
 * navigate back to the landing list.
 */
public record CompleteReviewResponse(
        Long id,
        String requestNumber,
        SystemStatus outcome,
        Instant reviewCompletedOn,
        HeaderSummaryDto summary) {
}
