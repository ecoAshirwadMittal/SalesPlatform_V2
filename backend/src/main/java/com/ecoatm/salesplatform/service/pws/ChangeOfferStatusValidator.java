package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.dto.ChangeOfferStatusRequest;
import org.springframework.stereotype.Component;

/**
 * Faithful port of the Mendix {@code VAL_ChargeOfferStatusHelper_IsValid}
 * microflow — the input guard for the bulk offer-status change tool. The deeply
 * nested legacy decision tree distils to these rules:
 *
 * <ul>
 *   <li><b>All Period</b> ({@code allPeriod == true}): an explicitly-selected
 *       order is required ({@code orderIds} non-empty). When this is a status
 *       change (not {@code notOrderStatusChange}) a target status
 *       ({@code toOrderStatus}) is required. No {@code fromOfferStatus} is
 *       required in this branch.</li>
 *   <li><b>Date range</b> ({@code allPeriod == false}): both dates are required
 *       and {@code endingDate} must be strictly after {@code startingDate}. When
 *       this is a status change, both {@code toOrderStatus} <i>and</i>
 *       {@code fromOfferStatus} (the safety guard) are required.</li>
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
        boolean statusChange = !req.notOrderStatusChange();

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

        if (statusChange) {
            if (isBlank(req.toOrderStatus())) {
                throw new IllegalArgumentException(
                        "A target order status is required for a status change.");
            }
            // The from-status safety guard only applies (and is only required)
            // on the date-range path — legacy skips it entirely when AllPeriod.
            if (!req.allPeriod() && isBlank(req.fromOfferStatus())) {
                throw new IllegalArgumentException(
                        "A from offer status is required for a date-range status change.");
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
