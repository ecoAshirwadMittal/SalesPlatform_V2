package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * One row in the admin landing list. Strict superset of the buyer-side
 * {@code CreditRequestSummary} — adds party_name + buyer_code + status
 * color hex so the admin landing can render columns the buyer page
 * doesn't need.
 *
 * <p>The buyer code is rendered as a string label here (not an id) to
 * avoid forcing the frontend to round-trip through buyer-code lookup;
 * the {@code CreditRequest.buyerCodeId} is available too in case the
 * frontend wants to deep-link to the buyer-detail page.
 */
public record AdminCreditRequestRow(
        Long id,
        String requestNumber,
        String orderNumber,
        Long buyerCodeId,
        String buyerCode,
        String partyName,
        SystemStatus systemStatus,
        String displayStatus,
        String statusColorHex,
        Instant requestDate,
        Instant submittedDate,
        boolean hasMissingDevice,
        boolean hasWrongDevice,
        boolean hasEncumberedDevice,
        Integer totalDevices,
        BigDecimal requestedTotal) {

    /**
     * Builds a row from the JPA entities. {@code buyerCode} is null when
     * the caller didn't pre-resolve it — the admin landing tolerates a
     * blank buyer-code cell rather than failing the whole list render.
     */
    public static AdminCreditRequestRow from(
            CreditRequest cr,
            CreditRequestStatus statusRow,
            String buyerCode) {
        return new AdminCreditRequestRow(
                cr.getId(),
                cr.getRequestNumber(),
                cr.getOrderNumber(),
                cr.getBuyerCodeId(),
                buyerCode,
                cr.getPartyName(),
                statusRow.getSystemStatus(),
                statusRow.getExternalStatusText(),
                statusRow.getColorHex(),
                cr.getRequestDate(),
                cr.getSubmittedDate(),
                Boolean.TRUE.equals(cr.getHasMissingDevice()),
                Boolean.TRUE.equals(cr.getHasWrongDevice()),
                Boolean.TRUE.equals(cr.getHasEncumberedDevice()),
                cr.getTotalDevices(),
                cr.getRequestedTotal());
    }
}
