package com.ecoatm.salesplatform.service.partialcredit;

/**
 * Thrown by {@link AccountingEmailService#sendAccountingEmail(Long)} when the
 * target credit request is not in the {@code APPROVED} system status.
 *
 * <p>The accounting-notification email (legacy
 * {@code ACT_SendCreditRequestAccountingEmail} / template
 * {@code CreditRequestSalesApproved}) is the <em>sales-approved</em> summary:
 * its body carries the approved-only snapshot ({@code approvedQty} /
 * {@code approvedTotal}), and the legacy button lived on the review page only
 * for already-approved requests. Sending it for a request that is still under
 * review — or was declined — would email accounting a "sales-approved" figure
 * that was never approved, so the action is refused.
 *
 * <p>Extends {@link IllegalStateException} so the controller's existing handler
 * maps it to HTTP {@code 409} (a conflict with the resource's current state),
 * matching the brief's "409 if not in the required state" and the in-repo
 * {@code RoundClosedException} precedent.
 */
public class CreditRequestNotApprovedException extends IllegalStateException {

    public CreditRequestNotApprovedException(String message) {
        super(message);
    }
}
