package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.auctions.BidRoundSelectionFilter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response body for {@code GET/PUT /api/v1/admin/auctions/round-filters/{round}}.
 * Projects every column of {@link BidRoundSelectionFilter} so the admin UI
 * can display the full round-qualification record. Enum values are
 * serialized as their {@code .name()} (Mendix-parity strings).
 */
public record BidRoundSelectionFilterResponse(
        Long id,
        Long legacyId,
        Integer round,
        BigDecimal targetPercent,
        BigDecimal targetValue,
        BigDecimal totalValueFloor,
        String mergedGrade1,
        String mergedGrade2,
        String mergedGrade3,
        Boolean stbAllowAllBuyersOverride,
        Boolean stbIncludeAllInventory,
        String regularBuyerQualification,
        String regularBuyerInventoryOptions,
        Instant createdDate,
        Instant changedDate) {

    public static BidRoundSelectionFilterResponse from(BidRoundSelectionFilter entity) {
        return new BidRoundSelectionFilterResponse(
                entity.getId(),
                entity.getLegacyId(),
                entity.getRound(),
                entity.getTargetPercent(),
                entity.getTargetValue(),
                entity.getTotalValueFloor(),
                entity.getMergedGrade1(),
                entity.getMergedGrade2(),
                entity.getMergedGrade3(),
                entity.getStbAllowAllBuyersOverride(),
                entity.getStbIncludeAllInventory(),
                entity.getRegularBuyerQualification() != null
                        ? entity.getRegularBuyerQualification().name() : null,
                entity.getRegularBuyerInventoryOptions() != null
                        ? entity.getRegularBuyerInventoryOptions().name() : null,
                entity.getCreatedDate(),
                entity.getChangedDate());
    }
}
