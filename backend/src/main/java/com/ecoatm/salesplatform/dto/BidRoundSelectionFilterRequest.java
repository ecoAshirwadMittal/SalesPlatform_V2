package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.RegularBuyerInventoryOption;
import com.ecoatm.salesplatform.model.auctions.RegularBuyerQualification;

import java.math.BigDecimal;

/**
 * Request body for {@code PUT /api/v1/admin/auctions/round-filters/{round}}.
 * Only writable fields are included — {@code id}, {@code legacyId},
 * {@code round}, {@code createdDate}, and {@code changedDate} are
 * managed by the service layer.
 *
 * <p>Nullable fields map to Mendix-typed optional attributes. The two
 * enum fields are non-null and validated against the CHECK constraints
 * in V59 via {@code EnumType.STRING}.
 */
public record BidRoundSelectionFilterRequest(
        BigDecimal targetPercent,
        BigDecimal targetValue,
        BigDecimal totalValueFloor,
        String mergedGrade1,
        String mergedGrade2,
        String mergedGrade3,
        Boolean stbAllowAllBuyersOverride,
        Boolean stbIncludeAllInventory,
        RegularBuyerQualification regularBuyerQualification,
        RegularBuyerInventoryOption regularBuyerInventoryOptions) {}
