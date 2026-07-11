package com.ecoatm.salesplatform.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Full-representation body for {@code POST /templates} (create) and
 * {@code PUT /templates/{id}} (update) — every editable {@code
 * email.template} (V92) column. Unlike {@link SmtpConfigUpdate}'s
 * null-means-unchanged patch semantics, this is an "upsert" shape: the
 * caller submits the complete template each time, matching the {@code
 * @NotBlank} requirements below (a null-means-unchanged DTO could never
 * require non-null fields).
 *
 * <p><b>{@code templateKey} is accepted but immutable on update</b> — the
 * controller's PUT handler validates it (same DTO shape as POST) but never
 * writes it onto an existing row; the key is a stable identifier senders
 * resolve by (e.g. the Partial-Credit listener), so silently changing it
 * would break sends. See {@code AdminEmailController.updateTemplate}.
 *
 * <p>Size limits mirror the V92 {@code email.template} column widths;
 * {@code templateKey}'s pattern mirrors the table's own CHECK constraint
 * ({@code template_key ~ '^[A-Za-z0-9_]+$'}). {@code @Email} is a no-op on
 * null, so {@code fromAddress}/{@code replyTo} stay optional. Enforced by
 * {@code @Valid} on the controller's {@code @RequestBody}; a violation maps
 * to HTTP 400 via {@code GlobalExceptionHandler.handleValidation}.
 */
public record EmailTemplateUpsert(
        @NotBlank
        @Size(max = 80)
        @Pattern(regexp = "^[A-Za-z0-9_]+$",
                message = "templateKey may contain only letters, digits, and underscores")
        String templateKey,

        @NotBlank @Size(max = 160) String templateName,

        @NotBlank @Size(max = 255) String subject,

        @NotBlank String contentHtml,

        String contentPlain,

        @Email @Size(max = 255) String fromAddress,

        @Size(max = 255) String fromDisplayName,

        @Email @Size(max = 255) String replyTo,

        @Size(max = 2000) String toDefault,

        @Size(max = 2000) String ccDefault,

        @Size(max = 2000) String bccDefault,

        Boolean hasAttachment,

        Boolean enabled,

        @Size(max = 500) String description) {
}
