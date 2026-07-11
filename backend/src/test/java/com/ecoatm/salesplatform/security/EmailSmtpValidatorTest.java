package com.ecoatm.salesplatform.security;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Design decision D2 (docs/tasks/email-management-design-2026-07-10.md): the
 * SMTP password comes only from env ({@code spring.mail.password}, sourced
 * from {@code EMAIL_SMTP_PASSWORD}), never from the DB or API. Mirrors
 * {@link JwtSecretValidatorTest}: refuse to boot in production when email
 * sending is enabled with no password configured; warn (don't block) elsewhere.
 */
class EmailSmtpValidatorTest {

    private static StandardEnvironment envWithProfiles(String... profiles) {
        StandardEnvironment env = new StandardEnvironment();
        env.setActiveProfiles(profiles);
        return env;
    }

    @Test
    void enabled_blankPassword_production_refusesToStart() {
        assertThatThrownBy(() ->
                new EmailSmtpValidator(true, "", envWithProfiles("production")).validate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void enabled_nullPassword_production_refusesToStart() {
        assertThatThrownBy(() ->
                new EmailSmtpValidator(true, null, envWithProfiles("production")).validate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void enabled_blankPassword_nonProduction_warnsButStarts() {
        // Local/dev convenience: matches JwtSecretValidator's non-production tolerance.
        assertThatCode(() ->
                new EmailSmtpValidator(true, "", envWithProfiles()).validate())
                .doesNotThrowAnyException();
    }

    @Test
    void enabled_passwordPresent_startsCleanly() {
        assertThatCode(() ->
                new EmailSmtpValidator(true, "s3cret", envWithProfiles("production")).validate())
                .doesNotThrowAnyException();
    }

    @Test
    void disabled_blankPassword_production_isNoOp() {
        assertThatCode(() ->
                new EmailSmtpValidator(false, "", envWithProfiles("production")).validate())
                .doesNotThrowAnyException();
    }
}
