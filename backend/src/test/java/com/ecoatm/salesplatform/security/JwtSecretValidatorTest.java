package com.ecoatm.salesplatform.security;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CR-2 (security review 2026-07-10): the app must refuse to boot in production
 * on a missing/placeholder/weak JWT signing secret rather than silently sign
 * tokens with a git-committed key.
 */
class JwtSecretValidatorTest {

    private static final String PLACEHOLDER =
            "default-dev-secret-key-must-be-at-least-32-bytes-long!!";
    private static final String STRONG =
            "a-very-strong-secret-that-is-well-over-thirty-two-bytes-xyz";
    private static final String SHORT = "too-short"; // < 32 bytes

    private static StandardEnvironment envWithProfiles(String... profiles) {
        StandardEnvironment env = new StandardEnvironment();
        env.setActiveProfiles(profiles);
        return env;
    }

    @Test
    void production_placeholderSecret_refusesToStart() {
        assertThatThrownBy(() ->
                new JwtSecretValidator(PLACEHOLDER, envWithProfiles("production")).validate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void production_shortSecret_refusesToStart() {
        assertThatThrownBy(() ->
                new JwtSecretValidator(SHORT, envWithProfiles("production")).validate())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void production_strongSecret_startsCleanly() {
        assertThatCode(() ->
                new JwtSecretValidator(STRONG, envWithProfiles("production")).validate())
                .doesNotThrowAnyException();
    }

    @Test
    void nonProduction_placeholderSecret_warnsButStarts() {
        // Local/dev convenience: the committed default is tolerated (with a WARN)
        // so `mvn spring-boot:run` keeps working without setting JWT_SECRET.
        assertThatCode(() ->
                new JwtSecretValidator(PLACEHOLDER, envWithProfiles()).validate())
                .doesNotThrowAnyException();
    }
}
