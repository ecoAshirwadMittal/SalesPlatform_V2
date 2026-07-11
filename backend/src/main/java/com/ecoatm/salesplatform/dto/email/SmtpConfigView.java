package com.ecoatm.salesplatform.dto.email;

import com.ecoatm.salesplatform.model.email.SmtpConfig;

import java.time.Instant;

/**
 * Admin-facing projection of {@link SmtpConfig} for {@code GET
 * /api/v1/admin/email/smtp}.
 *
 * <p><b>Design decision D2 — password never exposed:</b> the SMTP password
 * is env-only ({@code spring.mail.password}); {@code email.smtp_config}
 * (V92) has no password column. This record deliberately has NO
 * {@code password}/{@code encryptedPassword} component — there is no field
 * to populate, so a password can never leave this endpoint.
 */
public record SmtpConfigView(
        Long id,
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
        Integer timeoutMs,
        Instant changedDate) {

    public static SmtpConfigView from(SmtpConfig config) {
        return new SmtpConfigView(
                config.getId(),
                config.getServerHost(),
                config.getServerPort(),
                config.getProtocol(),
                config.getFromAddress(),
                config.getFromDisplayName(),
                config.getReplyTo(),
                config.getUseSsl(),
                config.getUseTls(),
                config.getEnabled(),
                config.getMaxRetryAttempts(),
                config.getTimeoutMs(),
                config.getChangedDate());
    }
}
