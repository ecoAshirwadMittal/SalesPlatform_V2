package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * Payload for {@code POST /api/v1/buyer/partial-credit/draft}. Carries the
 * order number the buyer picked on wizard Step 1 plus the active
 * buyer_code_id from the BuyerCode switcher. The server resolves the
 * buyer-code string from the id (via {@code buyer_mgmt.buyer_codes.code})
 * before calling the Snowflake reader.
 */
public record CreateDraftRequest(String orderNumber, Long buyerCodeId) {}
