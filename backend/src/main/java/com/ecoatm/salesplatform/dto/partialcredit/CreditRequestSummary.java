package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Row shape for the buyer's landing-page grid. Matches the Figma
 * "Credit Requests Landing" frame columns.
 */
public record CreditRequestSummary(
        Long id,
        String requestNumber,
        String orderNumber,
        SystemStatus systemStatus,
        String displayStatus,
        Instant requestDate,
        Instant submittedDate,
        boolean hasMissingDevice,
        boolean hasWrongDevice,
        boolean hasEncumberedDevice,
        Integer totalDevices,
        BigDecimal requestedTotal) {

    public static CreditRequestSummary from(CreditRequest cr, SystemStatus systemStatus, String displayStatus) {
        return new CreditRequestSummary(
                cr.getId(),
                cr.getRequestNumber(),
                cr.getOrderNumber(),
                systemStatus,
                displayStatus,
                cr.getRequestDate(),
                cr.getSubmittedDate(),
                Boolean.TRUE.equals(cr.getHasMissingDevice()),
                Boolean.TRUE.equals(cr.getHasWrongDevice()),
                Boolean.TRUE.equals(cr.getHasEncumberedDevice()),
                cr.getTotalDevices(),
                cr.getRequestedTotal());
    }
}
