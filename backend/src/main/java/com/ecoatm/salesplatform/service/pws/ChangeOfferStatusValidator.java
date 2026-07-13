package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.dto.ChangeOfferStatusRequest;
import org.springframework.stereotype.Component;

/**
 * Faithful port of the Mendix {@code VAL_ChargeOfferStatusHelper_IsValid}
 * microflow — the input guard for the bulk offer-status change tool. The deeply
 * nested legacy decision tree distils to these rules:
 *
 * <ul>
 *   <li><b>Metadata-only</b> ({@code notOrderStatusChange == true}): rejected
 *       outright. The legacy tool wrote {@code HasShipmentDetails} to the
 *       resolved orders, but the modern {@code pws.order} has no such column and
 *       this feature ships no migration — so the flag write is impossible.
 *       Rather than pretend success (the old {@code applyMetadata} path bumped
 *       {@code updated_date} and returned 200), the request is refused so the
 *       gap stays visible until a schema-prep migration lands.</li>
 *   <li><b>All Period</b> ({@code allPeriod == true}): an explicitly-selected
 *       order is required ({@code orderIds} non-empty) plus a target status
 *       ({@code toOrderStatus}). No {@code fromOfferStatus} is required in this
 *       branch.</li>
 *   <li><b>Date range</b> ({@code allPeriod == false}): both dates are required
 *       and {@code endingDate} must be strictly after {@code startingDate}, plus
 *       both {@code toOrderStatus} <i>and</i> {@code fromOfferStatus} (the safety
 *       guard).</li>
 * </ul>
 *
 * <p>On any failure this throws {@link IllegalArgumentException}, which
 * {@code GlobalExceptionHandler} maps to HTTP 400 with the message. Stateless —
 * no repository access (mirrors the legacy pure-validation microflow).
 */
@Component
public class ChangeOfferStatusValidator {

    /**
     * @throws IllegalArgumentException with a clear message when the request is
     *         not a valid bulk change (→ HTTP 400)
     */
    public void validate(ChangeOfferStatusRequest req) {
        // Metadata-only is unsupported on the modern schema: pws.order has no
        // has_shipment_details / legacy_order column and this feature adds no
        // migration, so the requested flag cannot be persisted. Reject at the
        // earliest point — before any order resolution or updated_date bump —
        // instead of pretending success.
        if (req.notOrderStatusChange()) {
            throw new IllegalArgumentException(
                    "Metadata-only bulk update (notOrderStatusChange) is not supported "
                    + "until pws.order has a has_shipment_details column");
        }

        if (req.allPeriod()) {
            if (req.orderIds() == null || req.orderIds().isEmpty()) {
                throw new IllegalArgumentException(
                        "An explicitly-selected order is required when All Period is set.");
            }
        } else {
            if (req.startingDate() == null || req.endingDate() == null) {
                throw new IllegalArgumentException(
                        "A starting date and an ending date are required.");
            }
            if (!req.endingDate().isAfter(req.startingDate())) {
                throw new IllegalArgumentException(
                        "The ending date must be after the starting date.");
            }
        }

        // Past the metadata guard this is always a status change, so a target
        // status is always required.
        if (isBlank(req.toOrderStatus())) {
            throw new IllegalArgumentException(
                    "A target order status is required for a status change.");
        }
        // The from-status safety guard only applies (and is only required) on the
        // date-range path — legacy skips it entirely when AllPeriod.
        if (!req.allPeriod() && isBlank(req.fromOfferStatus())) {
            throw new IllegalArgumentException(
                    "A from offer status is required for a date-range status change.");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
