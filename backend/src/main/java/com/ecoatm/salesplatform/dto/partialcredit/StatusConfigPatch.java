package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * PATCH body for the status-config admin endpoint (SPKB-3664).
 *
 * <p>All fields are nullable — null means "leave the existing value
 * alone", consistent with PATCH semantics. The five fields cover the
 * editable surface; {@code system_status}, {@code id}, {@code is_default},
 * and {@code status_grouped_to} are explicitly NOT in this DTO and cannot
 * be modified through this endpoint.
 */
public record StatusConfigPatch(
        String internalStatusText,
        String externalStatusText,
        String colorHex,
        Integer sortOrder,
        Boolean showInUserCounters) {
}
