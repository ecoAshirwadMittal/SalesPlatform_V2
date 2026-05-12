package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.enums.PrologResult;

import java.math.BigDecimal;

/**
 * Body for {@code POST .../{id}/lines/{lineId}/encumbered}. Reviewer-
 * entered Prolog Result + Actual Value on one encumbered line. The
 * Actual Value may be null while the reviewer is still investigating;
 * the credit is then computed against zero on submit.
 */
public record EncumberedFieldsRequest(PrologResult prologResult, BigDecimal actualValue) {
}
