package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;

/**
 * Body for {@code POST .../{id}/sections/{kind}/decision}. Bulk applies
 * {@code decision} to every line of {@code {kind}} on the request.
 */
public record SectionDecisionRequest(ReviewDecision decision) {
}
