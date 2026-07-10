package com.ecoatm.salesplatform.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Startup guard for the database password.
 *
 * <p>Why: {@code spring.datasource.password} carries a weak dev fallback
 * ({@code ${DB_PASSWORD:salesplatform}} in {@code application.yml}). If
 * {@code DB_PASSWORD} is ever unset in a real deployment the app would otherwise
 * boot silently and connect to Postgres with a publicly-known credential. This
 * bean fails startup in production (and warns elsewhere) rather than fail open —
 * the same class of defect as the JWT-secret fallback, mirroring
 * {@link com.ecoatm.salesplatform.security.JwtSecretValidator}. See
 * {@code docs/tasks/security-review-and-remediation-plan-2026-07-10.md} (M-5).
 */
@Component
public class DbPasswordValidator {

    private static final Logger log = LoggerFactory.getLogger(DbPasswordValidator.class);

    /** The dev fallback from application.yml — must never authenticate in production. */
    private static final String DEV_DEFAULT_PASSWORD = "salesplatform";

    private final String password;
    private final Environment environment;

    public DbPasswordValidator(
            @Value("${spring.datasource.password:salesplatform}") String password,
            Environment environment) {
        this.password = password;
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        boolean weak = password == null
                || password.isBlank()
                || password.equals(DEV_DEFAULT_PASSWORD);
        if (!weak) {
            return;
        }
        if (Arrays.asList(environment.getActiveProfiles()).contains("production")) {
            throw new IllegalStateException(
                    "spring.datasource.password must be set to a non-default value via the "
                            + "DB_PASSWORD environment variable in production; refusing to start "
                            + "with a missing or dev-default database credential.");
        }
        log.warn("spring.datasource.password is using a weak or default value. Set DB_PASSWORD to a "
                + "strong secret before deploying — this MUST NOT reach production.");
    }
}
