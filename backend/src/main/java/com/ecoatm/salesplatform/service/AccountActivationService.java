package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.EcoATMDirectUser;
import com.ecoatm.salesplatform.model.PasswordResetToken;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.PasswordResetTokenRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * Activates a newly-provisioned buyer-side user from an emailed activation link.
 *
 * <p>Modern port of the legacy {@code ACT_ActivateNewUser} microflow: given a
 * one-time activation token (the same operational token machinery the
 * password-reset flow uses — see {@link PasswordResetService}), set the user's
 * BCrypt password and flip the {@link EcoATMDirectUser} business status to
 * Active, then consume the token so it cannot be replayed.
 *
 * <p><b>Public-endpoint security invariants</b> (repo Security Rules):
 * <ul>
 *   <li>The target user is derived <em>only</em> from the token — never from any
 *       request field.</li>
 *   <li>Every token failure (unknown / expired / already-consumed / dangling
 *       user row) returns a single generic error, so the endpoint can never be
 *       used to enumerate accounts.</li>
 *   <li>The token is single-use and time-limited — enforced by
 *       {@link PasswordResetTokenRepository#findValidByHash} (filters consumed +
 *       expired) plus marking {@code consumed_at} on redemption.</li>
 *   <li>The password policy (ported from {@code ACT_CheckPasswordRequirements_activation})
 *       is enforced before any DB work; the raw token and password are never
 *       logged.</li>
 * </ul>
 *
 * <p><b>Deferred</b>: the legacy {@code SUB_SendUserToSnowflake} push is out of
 * scope here — user replication moves to the scheduled Snowflake batch.
 */
@Service
@RequiredArgsConstructor
public class AccountActivationService {

    private static final Logger log = LoggerFactory.getLogger(AccountActivationService.class);

    /** Single generic message for every token-failure mode (no account enumeration). */
    private static final String GENERIC_TOKEN_ERROR = "Invalid or expired activation link";

    /** Status string stored in ecoatm_direct_users.user_status / overall_user_status. */
    private static final String STATUS_ACTIVE = "Active";

    // Legacy password policy from ACT_CheckPasswordRequirements_activation:
    // min 8 chars + at least one uppercase letter + at least one special character.
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern SPECIAL_CHARACTER = Pattern.compile("[!@#$%^&*()<>]");
    private static final String PASSWORD_POLICY_MESSAGE =
            "Password must be at least 8 characters and include an uppercase letter "
                    + "and a special character (!@#$%^&*()<>).";

    private final UserRepository userRepository;
    private final EcoATMDirectUserRepository directUserRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    /**
     * Activate the user behind {@code rawToken} by setting {@code rawPassword} and
     * flipping their status to Active.
     *
     * @param rawToken    the plain activation token from the emailed link
     * @param rawPassword the new plain-text password to BCrypt-encode
     * @throws IllegalArgumentException with a generic message when the token is
     *         invalid/expired/consumed (400, no enumeration), or with the policy
     *         message when the password is too weak (400)
     */
    @Transactional
    public void activate(String rawToken, String rawPassword) {
        // Policy first — fail fast without any DB work or state change on a weak password.
        validatePasswordPolicy(rawPassword);

        // Resolve the token (findValidByHash already excludes expired + consumed rows).
        PasswordResetToken token = tokenRepository
                .findValidByHash(sha256Hex(rawToken), Instant.now(clock))
                .orElseThrow(() -> new IllegalArgumentException(GENERIC_TOKEN_ERROR));

        // Target user is derived from the token — never from a request field.
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(GENERIC_TOKEN_ERROR));
        EcoATMDirectUser directUser = directUserRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(GENERIC_TOKEN_ERROR));

        // Set the BCrypt hash where login validates it (identity.users).
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        // Flip the EcoATMDirectUser business status (legacy ACT_ActivateNewUser).
        directUser.setUserStatus(STATUS_ACTIVE);
        directUser.setOverallUserStatus(STATUS_ACTIVE);
        directUser.setInactive(false);
        directUser.setActivationDate(LocalDateTime.now(clock));
        directUserRepository.save(directUser);

        // Consume the token (single-use) — mirrors PasswordResetService.confirmReset.
        token.setConsumedAt(Instant.now(clock));
        tokenRepository.save(token);

        log.info("Account activated for userId={}", user.getId());
    }

    private static void validatePasswordPolicy(String password) {
        boolean valid = password != null
                && password.length() >= MIN_PASSWORD_LENGTH
                && UPPERCASE.matcher(password).find()
                && SPECIAL_CHARACTER.matcher(password).find();
        if (!valid) {
            throw new IllegalArgumentException(PASSWORD_POLICY_MESSAGE);
        }
    }

    /**
     * SHA-256 hex digest — mirrors {@link PasswordResetService}'s hashing so a
     * token issued through the shared reset-token machinery resolves here.
     */
    private static String sha256Hex(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the JVM spec — unreachable.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
