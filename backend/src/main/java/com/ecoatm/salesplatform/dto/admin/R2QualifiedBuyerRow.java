package com.ecoatm.salesplatform.dto.admin;

/**
 * One row in the R2 qualified-buyer-codes admin grid (gap H10).
 *
 * <p>Maps a {@code qualified_buyer_codes} row joined to its buyer code +
 * buyer (company) details. Rendered read-only — admins inspect WHO the
 * R2 buyer-assignment service decided to qualify for a given auction.
 */
public record R2QualifiedBuyerRow(
        Long buyerCodeId,
        String code,
        String buyerCodeType,
        String companyName,
        String qualificationType,
        boolean included,
        boolean isSpecialTreatment
) {}
