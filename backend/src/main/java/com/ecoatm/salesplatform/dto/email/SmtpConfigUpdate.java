package com.ecoatm.salesplatform.dto.email;

/**
 * Patch payload for {@code SmtpConfigService.update} — {@code PUT
 * /api/v1/admin/email/smtp}. Nullable fields mean "leave unchanged" (mirrors
 * {@code EmailTemplateUpdate}/{@code StatusConfigPatch}) — the admin SMTP
 * settings form only needs to submit the fields the admin actually edited.
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
        String serverHost,
        Integer serverPort,
        String protocol,
        String fromAddress,
        String fromDisplayName,
        String replyTo,
        Boolean useSsl,
        Boolean useTls,
        Boolean enabled,
        Integer maxRetryAttempts,
        Integer timeoutMs) {
}
