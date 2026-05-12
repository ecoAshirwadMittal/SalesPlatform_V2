package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;

/**
 * Body for {@code POST .../{id}/decision}. Bulk applies {@code decision}
 * to every line across all three sections in one transaction.
 */
public record GlobalDecisionRequest(ReviewDecision decision) {
}
