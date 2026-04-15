package com.ecoatm.salesplatform.service.email;

import java.util.List;

/**
 * Immutable email payload handed to an {@link EmailSender}.
 *
 * <p>{@code to} and {@code cc} are always non-null lists (may be empty for cc).
 * {@code htmlBody} is required; {@code textBody} is an optional plain-text
 * alternative for clients that cannot render HTML.
 */
public record EmailMessage(
        List<String> to,
        List<String> cc,
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
    }
}
