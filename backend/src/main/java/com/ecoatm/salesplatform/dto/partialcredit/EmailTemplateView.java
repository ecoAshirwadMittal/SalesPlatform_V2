package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.EmailTemplate;

import java.time.Instant;

/**
 * Response projection of a {@link EmailTemplate} row for the admin
 * email-templates editor. Mirrors every cosmetic field — the editor
 * needs all of them so the Preview tab can render with the same body
 * the listener would use at send time.
 */
public record EmailTemplateView(
        Long id,
        String templateKey,
        String subject,
        String bodyHtml,
        String bodyText,
        Boolean enabled,
        String description,
        Instant changedDate,
        Long changedById) {

    public static EmailTemplateView from(EmailTemplate t) {
        return new EmailTemplateView(
                t.getId(),
                t.getTemplateKey(),
                t.getSubject(),
                t.getBodyHtml(),
                t.getBodyText(),
                t.getEnabled(),
                t.getDescription(),
                t.getChangedDate(),
                t.getChangedById());
    }
}
