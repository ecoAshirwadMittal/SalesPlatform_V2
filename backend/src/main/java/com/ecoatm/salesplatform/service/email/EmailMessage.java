package com.ecoatm.salesplatform.service.email;

import java.util.List;

/**
 * Immutable email payload handed to an {@link EmailSender}.
 *
 * <p>{@code to} is required and non-empty. {@code cc} and {@code bcc} are
 * always non-null lists (empty when not supplied). {@code from} and
 * {@code replyTo} are optional per-message overrides — when {@code from} is
 * null the sender falls back to its configured default address, and when
 * {@code replyTo} is null no {@code Reply-To} header is set. {@code htmlBody}
 * is required; {@code textBody} is an optional plain-text alternative for
 * clients that cannot render HTML.
 */
public record EmailMessage(
        List<String> to,
        List<String> cc,
        List<String> bcc,
        String from,
        String replyTo,
        String subject,
        String htmlBody,
        String textBody) {

    public EmailMessage {
        if (to == null || to.isEmpty()) {
            throw new IllegalArgumentException("EmailMessage.to must contain at least one recipient");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("EmailMessage.subject must not be blank");
        }
        if (htmlBody == null || htmlBody.isBlank()) {
            throw new IllegalArgumentException("EmailMessage.htmlBody must not be blank");
        }
        to = List.copyOf(to);
        cc = cc == null ? List.of() : List.copyOf(cc);
        bcc = bcc == null ? List.of() : List.copyOf(bcc);
    }

    /**
     * Back-compat factory for the original 5-field shape (no bcc/from/replyTo).
     * Lets existing callers that only need to/cc/subject/htmlBody/textBody
     * keep compiling against the 8-field canonical constructor.
     */
    public static EmailMessage of(
            List<String> to, List<String> cc, String subject, String htmlBody, String textBody) {
        return new EmailMessage(to, cc, List.of(), null, null, subject, htmlBody, textBody);
    }
}
