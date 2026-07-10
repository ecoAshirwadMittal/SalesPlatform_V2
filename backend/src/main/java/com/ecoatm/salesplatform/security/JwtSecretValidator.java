package com.ecoatm.salesplatform.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Startup guard for the JWT signing secret.
 *
 * <p>Why: {@code JwtService} accepts a git-committed dev fallback
 * ({@code app.jwt.secret} default). If {@code JWT_SECRET} is ever unset in a real
 * deployment the app would otherwise boot silently and sign every token with a
 * publicly-known key — letting anyone forge an {@code Administrator} JWT. This
 * bean fails startup in production (and warns elsewhere) rather than fail open.
 * See {@code docs/tasks/security-review-and-remediation-plan-2026-07-10.md} (CR-2).
 */
@Component
public class JwtSecretValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtSecretValidator.class);

    /** The git-committed dev fallback — must never sign tokens in production. */
    private static final String DEV_PLACEHOLDER_SECRET =
            "default-dev-secret-key-must-be-at-least-32-bytes-long!!";
    private static final int MIN_SECRET_BYTES = 32;

    private final String secret;
    private final Environment environment;

    public JwtSecretValidator(
            @Value("${app.jwt.secret:default-dev-secret-key-must-be-at-least-32-bytes-long!!}") String secret,
            Environment environment) {
        this.secret = secret;
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        boolean weak = secret == null
                || secret.isBlank()
                || secret.equals(DEV_PLACEHOLDER_SECRET)
                || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES;
        if (!weak) {
            return;
        }
        if (Arrays.asList(environment.getActiveProfiles()).contains("production")) {
            throw new IllegalStateException(
                    "app.jwt.secret must be set to a strong (>= " + MIN_SECRET_BYTES
                            + " byte) value via the JWT_SECRET environment variable in production; "
                            + "refusing to start with a missing or default signing key.");
        }
        log.warn("app.jwt.secret is using a weak or default value. Set JWT_SECRET to a strong "
                + "secret before deploying — this MUST NOT reach production.");
    }
}
