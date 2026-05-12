package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineKind;

/**
 * Body for {@code POST .../{id}/lines/{lineId}/decision}. Reviewer flips
 * one line to ACCEPTED or DECLINED; the {@code kind} disambiguates which
 * of the three line tables to look in.
 */
public record LineDecisionRequest(LineKind kind, ReviewDecision decision) {
}
