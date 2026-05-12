package com.ecoatm.salesplatform.dto.partialcredit;

/**
 * Patch payload for {@code EmailTemplateService.update}. Nullable fields
 * mean "leave unchanged" — the admin UI sends only the fields the user
 * touched. {@code templateKey} is not patchable post-seed (listener code
 * references it directly; changing it would break the listener silently),
 * so it is intentionally absent from this record.
 */
public record EmailTemplateUpdate(
        String subject,
        String bodyHtml,
        String bodyText,
        Boolean enabled,
        String description) {
}
