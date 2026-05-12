package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;

/**
 * Body for {@code POST .../{id}/complete-review}. Reviewer picks the
 * terminal outcome — must be {@link SystemStatus#APPROVED} or
 * {@link SystemStatus#DECLINED}; the service rejects all other values
 * with an INVALID_OUTCOME validation issue.
 */
public record CompleteReviewRequest(SystemStatus outcome) {
}
