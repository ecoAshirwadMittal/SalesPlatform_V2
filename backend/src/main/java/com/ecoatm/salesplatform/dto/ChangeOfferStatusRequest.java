package com.ecoatm.salesplatform.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Request for the Administrator-only bulk offer-status change tool — a faithful
 * port of the legacy Mendix {@code ACT_ChangeOfferStatus_Proceed} /
 * {@code ChangeOfferStatusHelper} (gap-analysis 2.3 sub-feature 3). The tool is
 * an ops-correction lever used to fix offer status in bulk after a bad Oracle
 * sync.
 *
 * <p><b>Locked decisions (user):</b> permissive any-&gt;any (NO transition
 * allowlist), Administrator-only, audit-logged, and side-effect-free (no Oracle
 * re-send, email, or inventory reservation). The only guards are the
 * from-status match (when not {@code allPeriod}) and a valid date range.
 *
 * <p>Field semantics (mirroring the Mendix helper attributes):
 * <ul>
 *   <li>{@code allPeriod} — when {@code true}, operate on the explicitly-selected
 *       {@code orderIds} only; when {@code false}, resolve orders by
 *       {@code [startingDate, endingDate]} against {@code order_date}.</li>
 *   <li>{@code startingDate}/{@code endingDate} — inclusive date-range bounds
 *       (compared against the day-truncated {@code order_date}); required when
 *       not {@code allPeriod}, with {@code endingDate > startingDate}.</li>
 *   <li>{@code fromOfferStatus} — the safety guard: when not {@code allPeriod}
 *       and this is a status change, only offers whose current status equals
 *       this value are changed.</li>
 *   <li>{@code toOrderStatus} — the target offer status to apply (required
 *       unless {@code notOrderStatusChange}).</li>
 *   <li>{@code notOrderStatusChange} — when {@code true}, this is a metadata-only
 *       operation (no offer-status change).</li>
 *   <li>{@code hasShipmentDetails} — the metadata flag the legacy tool wrote to
 *       the resolved orders.</li>
 *   <li>{@code orderIds} — the explicitly-selected orders (required when
 *       {@code allPeriod}).</li>
 * </ul>
 *
 * <p>Identity is never taken from this request — the audit caller is derived
 * from the verified JWT at the controller layer.
 */
public record ChangeOfferStatusRequest(
        boolean allPeriod,
        LocalDate startingDate,
        LocalDate endingDate,
        String fromOfferStatus,
        String toOrderStatus,
        boolean notOrderStatusChange,
        boolean hasShipmentDetails,
        List<Long> orderIds) {

    /** Normalise {@code orderIds} to an immutable empty list when absent. */
    public ChangeOfferStatusRequest {
        orderIds = orderIds == null ? List.of() : List.copyOf(orderIds);
    }
}
