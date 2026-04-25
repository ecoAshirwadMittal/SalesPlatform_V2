package com.ecoatm.salesplatform.dto;

import java.time.LocalDateTime;

/**
 * DTO for a single qualified_buyer_codes row, used by the admin QBC list page.
 */
public record QualifiedBuyerCodeRow(
        long id,
        long schedulingAuctionId,
        long buyerCodeId,
        String qualificationType,
        boolean included,
        boolean submitted,
        LocalDateTime submittedDatetime,
        boolean specialTreatment,
        LocalDateTime createdDate,
        LocalDateTime changedDate
) {}
