package com.ecoatm.salesplatform.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Startup guard for the SMTP password used by {@code SmtpEmailSender}.
 *
 * <p>Why: design decision D2
 * ({@code docs/tasks/email-management-design-2026-07-10.md}) requires the SMTP
 * password to come only from the environment ({@code spring.mail.password},
 * sourced from {@code EMAIL_SMTP_PASSWORD}) — never from the database or an
 * API payload. If email sending is ever enabled in a real deployment with no
 * password configured, the app would otherwise boot "successfully" and then
 * fail silently (or worse, connect unauthenticated) on every send attempt.
 * This bean fails startup in production (and warns elsewhere) rather than
 * fail open — mirrors {@link JwtSecretValidator} (CR-2,
 * {@code docs/tasks/security-review-and-remediation-plan-2026-07-10.md}).
 */
@Component
public class EmailSmtpValidator {

    private static final Logger log = LoggerFactory.getLogger(EmailSmtpValidator.class);

    private final boolean enabled;
    private final String password;
    private final Environment environment;

    public EmailSmtpValidator(
            // T4/T7: Task 4/7 may repoint this to smtp_config.enabled once SmtpConfigService lands
            @Value("${pws.email.enabled:false}") boolean enabled,
            @Value("${spring.mail.password:}") String password,
            Environment environment) {
        this.enabled = enabled;
        this.password = password;
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        if (!enabled) {
            return;
        }
        boolean missingPassword = password == null || password.isBlank();
        if (!missingPassword) {
            return;
        }
        if (Arrays.asList(environment.getActiveProfiles()).contains("production")) {
            throw new IllegalStateException(
                    "spring.mail.password must be set via the EMAIL_SMTP_PASSWORD environment "
                            + "variable when email sending is enabled in production; refusing to "
                            + "start with delivery enabled and no SMTP password.");
        }
        log.warn("Email sending is enabled but spring.mail.password is blank. Set "
                + "EMAIL_SMTP_PASSWORD before enabling email delivery in production — this MUST "
                + "NOT reach production unset.");
    }
}
