package com.ecoatm.salesplatform.service.email;

/**
 * Abstraction over the mail transport used by PWS notifications.
 *
 * <p>Two implementations exist:
 * <ul>
 *   <li>{@link LoggingEmailSender} — default, logs full payload. Active when
 *       {@code pws.email.enabled=false} (or unset).</li>
 *   <li>{@link SmtpEmailSender} — real SMTP via {@code JavaMailSender}.
 *       Active when {@code pws.email.enabled=true}.</li>
 * </ul>
 */
public interface EmailSender {

    /**
     * Deliver the message. Implementations may block and may throw on transport
     * failure; callers must ensure this runs on a background thread and must
     * catch exceptions so email failures never affect the originating
     * business transaction.
     */
    void send(EmailMessage message);
}
