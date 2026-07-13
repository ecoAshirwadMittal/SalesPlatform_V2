package com.ecoatm.salesplatform.service.partialcredit;

/**
 * Thrown by {@link AccountingEmailService#sendAccountingEmail(Long)} when the
 * {@code partial-credit.accounting-email.recipients} config is unset or empty.
 *
 * <p>Locked decision (user, 2026-07-12): the accounting distribution list has
 * <b>no shipped default</b> (the address is not in any migrated source), so an
 * unconfigured environment must <em>fail safe</em> — never silently drop the
 * send, never fall back to a hard-coded address. Extends
 * {@link IllegalStateException} so {@code AdminPartialCreditController}'s
 * existing handler maps it to HTTP {@code 409} with a clear message, rather
 * than surfacing a scary {@code 500} for what is really an ops-config gap.
 */
public class AccountingRecipientsNotConfiguredException extends IllegalStateException {

    public AccountingRecipientsNotConfiguredException(String message) {
        super(message);
    }
}
