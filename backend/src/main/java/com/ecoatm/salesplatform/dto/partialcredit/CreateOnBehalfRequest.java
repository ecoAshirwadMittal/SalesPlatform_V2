package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * POST body for the sales-rep on-behalf draft-creation endpoint
 * (Sprint 4 chunk 6 — SPKB-3659). The endpoint creates a partial-credit
 * draft and stamps {@code is_on_behalf=TRUE} + the two on-behalf FKs in
 * a single transaction — the modal can then redirect the rep into the
 * standard wizard for step 2 onwards.
 */
public record CreateOnBehalfRequest(
        String orderNumber,
        Long buyerCodeId,
        Long onBehalfOfUserId) {
}
