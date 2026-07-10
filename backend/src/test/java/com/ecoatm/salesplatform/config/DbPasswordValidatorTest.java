package com.ecoatm.salesplatform.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * M-5 (security review 2026-07-10): the app must refuse to boot in production on
 * the missing / dev-default database password rather than silently connect to
 * Postgres with a publicly-known credential. Mirrors {@code JwtSecretValidatorTest}.
 */
class DbPasswordValidatorTest {

    private static final String DEV_DEFAULT = "salesplatform";
    private static final String STRONG = "a-strong-db-password-from-the-secret-manager";

    private static StandardEnvironment envWithProfiles(String... profiles) {
        StandardEnvironment env = new StandardEnvironment();
        env.setActiveProfiles(profiles);
        return env;
    }

    @Test
    void production_devDefaultPassword_refusesToStart() {
        assertThatThrownBy(() ->
                new DbPasswordValidator(DEV_DEFAULT, envWithProfiles("production")).validate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void production_blankPassword_refusesToStart() {
        assertThatThrownBy(() ->
                new DbPasswordValidator("   ", envWithProfiles("production")).validate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void production_strongPassword_startsCleanly() {
        assertThatCode(() ->
                new DbPasswordValidator(STRONG, envWithProfiles("production")).validate())
                .doesNotThrowAnyException();
    }

    @Test
    void nonProduction_devDefaultPassword_warnsButStarts() {
        // Local/dev convenience: the committed default is tolerated (with a WARN)
        // so `mvn spring-boot:run` keeps working without setting DB_PASSWORD.
        assertThatCode(() ->
                new DbPasswordValidator(DEV_DEFAULT, envWithProfiles()).validate())
                .doesNotThrowAnyException();
    }
}
