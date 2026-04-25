package com.ecoatm.salesplatform.dto;

/**
 * Per-row admin view of {@code buyer_mgmt.qualified_buyer_codes}. Includes
 * the human-readable {@code buyerCode} (e.g. "AA600WHL") joined from
 * {@code buyer_codes.code} so the admin grid can render without a follow-up
 * lookup, plus the toggle source-of-truth fields ({@code included},
 * {@code qualificationType}, {@code specialTreatment}).
 */
public record QualifiedBuyerCodeAdminRow(
        long id,
        long schedulingAuctionId,
        long buyerCodeId,
        String buyerCode,
        String qualificationType,
        boolean included,
        boolean specialTreatment
) {}
