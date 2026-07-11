package com.ecoatm.salesplatform.dto.email;

import com.ecoatm.salesplatform.model.email.EmailTemplate;

import java.time.Instant;

/**
 * Admin-facing projection of {@link EmailTemplate} (unified {@code
 * email.template} store, V92) for the Task 8 template-management endpoints.
 * Mirrors every editable + audit column — the admin editor's Preview tab
 * needs the raw {@code contentHtml}/{@code contentPlain} to render against,
 * exactly like the partial-credit-module precedent
 * ({@code dto.partialcredit.EmailTemplateView}).
 */
public record EmailTemplateView(
        Long id,
        String templateKey,
        String templateName,
        String subject,
        String contentHtml,
        String contentPlain,
        String fromAddress,
        String fromDisplayName,
        String replyTo,
        String toDefault,
        String ccDefault,
        String bccDefault,
        Boolean hasAttachment,
        Boolean enabled,
        String description,
        Instant createdDate,
        Instant changedDate) {

    public static EmailTemplateView from(EmailTemplate t) {
        return new EmailTemplateView(
                t.getId(),
                t.getTemplateKey(),
                t.getTemplateName(),
                t.getSubject(),
                t.getContentHtml(),
                t.getContentPlain(),
                t.getFromAddress(),
                t.getFromDisplayName(),
                t.getReplyTo(),
                t.getToDefault(),
                t.getCcDefault(),
                t.getBccDefault(),
                t.getHasAttachment(),
                t.getEnabled(),
                t.getDescription(),
                t.getCreatedDate(),
                t.getChangedDate());
    }
}
