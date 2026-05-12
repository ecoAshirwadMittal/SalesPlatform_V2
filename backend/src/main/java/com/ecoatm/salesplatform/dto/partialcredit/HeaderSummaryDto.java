package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.service.partialcredit.CreditCalculationService.HeaderSummary;

import java.math.BigDecimal;

/**
 * Wire shape for the six header counter fields displayed on the admin
 * review detail (Requested Credit / Total Credit panels). Wraps
 * {@link HeaderSummary} so the JSON shape stays detached from the
 * service-layer record.
 */
public record HeaderSummaryDto(
        int requestedSkus,
        int requestedQty,
        BigDecimal requestedTotal,
        int approvedSkus,
        int approvedQty,
        BigDecimal approvedTotal) {

    public static HeaderSummaryDto from(HeaderSummary s) {
        return new HeaderSummaryDto(
                s.requestedSkus(), s.requestedQty(), s.requestedTotal(),
                s.approvedSkus(), s.approvedQty(), s.approvedTotal());
    }
}
