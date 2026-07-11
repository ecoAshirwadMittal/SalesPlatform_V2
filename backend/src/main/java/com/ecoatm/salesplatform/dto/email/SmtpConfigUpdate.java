package com.ecoatm.salesplatform.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Patch payload for {@code SmtpConfigService.update} — {@code PUT
 * /api/v1/admin/email/smtp}. Nullable fields mean "leave unchanged" — the
 * admin SMTP settings form only needs to submit the fields the admin
 * actually edited, and a {@code null} coalesces to the current value rather
 * than nulling a NOT-NULL column (server_port, protocol, …).
 *
 * <p><b>Validation:</b> every constraint below passes {@code null} (JSR-380
 * constraints are no-ops on null), so an omitted field is always accepted;
 * a <em>present</em> field is bounded to the V92 column widths and to
 * semantically valid ranges. Two are load-bearing beyond "don't 500":
 * {@code maxRetryAttempts} flows straight into {@code EmailRetryWorker}, so
 * a {@code 0}/negative value would silently disable retry for EVERY module
 * writing to {@code email.log} — {@code @Min(1)} blocks that; and
 * {@code serverPort} out of {@code 1..65535} is rejected here as a 400
 * rather than surfacing as a Hibernate 500 on save. Enforced by {@code @Valid}
 * on the controller's {@code @RequestBody}; a violation is mapped to HTTP 400
 * by {@code GlobalExceptionHandler.handleValidation}.
 *
 * <p><b>Design decision D2 — password never accepted:</b> the SMTP password
 * is env-only ({@code spring.mail.password}) and is never read from a
 * request. This record deliberately has NO {@code password}/
 * {@code encryptedPassword} component. If a caller's JSON body includes
 * either field anyway, Jackson silently drops it during deserialization
 * because this record has no matching component to bind into — there is no
 * code path anywhere that could persist it.
 */
public record SmtpConfigUpdate(
        @Size(max = 255) String serverHost,
        @Min(1) @Max(65535) Integer serverPort,
        @Size(max = 20) String protocol,
        @Email @Size(max = 255) String fromAddress,
        @Size(max = 255) String fromDisplayName,
        @Email @Size(max = 255) String replyTo,
        Boolean useSsl,
        Boolean useTls,
        Boolean enabled,
        @Min(1) Integer maxRetryAttempts,
        @Positive Integer timeoutMs) {
}
