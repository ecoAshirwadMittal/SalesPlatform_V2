package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * One row in the sales-rep "Submit on behalf — pick a buyer code"
 * dropdown (Sprint 4 chunk 6). Carries the resolved buyer name so the
 * UI can render "20399 — Acme Corp" without a second roundtrip.
 *
 * <p>A buyer code is associated with one or more buyers via
 * {@code buyer_mgmt.buyer_code_buyers}; this projection picks the first
 * matching buyer name (alphabetical) when multiple exist, which is rare
 * in practice and only loses information in the dropdown label.
 */
public record BuyerCodeOption(Long id, String code, String buyerName) {
}
