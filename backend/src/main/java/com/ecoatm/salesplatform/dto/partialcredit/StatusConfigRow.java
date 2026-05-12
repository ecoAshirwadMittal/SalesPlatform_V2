package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;

/**
 * Read shape for the status-config admin grid (SPKB-3664).
 *
 * <p>Surfaces all editable + read-only fields on the
 * {@code partial_credit.credit_request_statuses} table. The
 * {@code systemStatus} is read-only — it's the enum key consumed by both
 * the buyer landing color/text rendering and the admin domain logic.
 */
public record StatusConfigRow(
        Long id,
        String systemStatus,
        String internalStatusText,
        String externalStatusText,
        String colorHex,
        Integer sortOrder,
        Boolean showInUserCounters,
        Boolean isDefault,
        String statusGroupedTo) {

    public static StatusConfigRow from(CreditRequestStatus row) {
        return new StatusConfigRow(
                row.getId(),
                row.getSystemStatus() == null ? null : row.getSystemStatus().name(),
                row.getInternalStatusText(),
                row.getExternalStatusText(),
                row.getColorHex(),
                row.getSortOrder(),
                row.getShowInUserCounters(),
                row.getIsDefault(),
                row.getStatusGroupedTo());
    }
}
