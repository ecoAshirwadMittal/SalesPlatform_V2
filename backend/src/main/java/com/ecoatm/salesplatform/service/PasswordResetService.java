package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.PasswordResetToken;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.repository.PasswordResetTokenRepository;
import com.ecoatm.salesplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Handles the two-step password-reset flow:
 * <ol>
 *   <li>{@link #requestReset(String)} — issue a short-lived token for the email (if
 *       an account exists). Always completes without revealing whether the account
 *       exists (enumeration-resistant).</li>
 *   <li>{@link #confirmReset(String, String)} — validate the token, update the
 *       BCrypt hash, mark the token consumed.</li>
 * </ol>
 *
 * <p><b>Email delivery</b>: In this phase the raw token is logged at INFO
 * (prefixed {@code DEV:}) instead of being sent via SMTP. Real email delivery
 * will be wired in a follow-up phase once the SMTP infrastructure is decided.
 * TODO(email-infra): replace the log.info call below with an EmailSender invocation
 * using the same post-commit event pattern from the 2026-04-13 PWS email ADR.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    /** Token TTL in minutes — matches the task spec. */
    private static final int TOKEN_TTL_MINUTES = 30;

    /** Raw token is 32 bytes of SecureRandom, Base64URL-encoded (43 chars). */
    private static final int TOKEN_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Issue a password-reset token for the given email address.
     *
     * <p>If no active user exists for the email, this method returns silently —
     * it never throws or signals that the account is missing (enumeration-resistant).
     *
     * <p>Any previously active (unconsumed) tokens for the user are purged before
     * the new token is persisted, preventing token accumulation.
     */
    @Transactional
    public void requestReset(String email) {
        // Mendix stored the user's email in the `name` field of system$user.
        // UserRepository.findByNameIgnoreCase handles the case-insensitive lookup.
        Optional<User> userOpt = userRepository.findByNameIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            // Enumeration-resistant: log at DEBUG only, return silently.
            log.debug("Password reset requested for unknown email — no action taken");
            return;
        }

        User user = userOpt.get();

        // Remove any stale unconsumed tokens before issuing a fresh one
        tokenRepository.deleteActiveTokensForUser(user.getId());

        String rawToken = generateSecureToken();
        String tokenHash = sha256Hex(rawToken);

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getId());
        token.setTokenHash(tokenHash);
        token.setExpiresAt(Instant.now().plus(TOKEN_TTL_MINUTES, ChronoUnit.MINUTES));
        token.setCreatedAt(Instant.now());
        tokenRepository.save(token);

        // TODO(email-infra): publish a PasswordResetEmailEvent (post-commit, async)
        // and handle it in a listener that calls EmailSender.send() — same pattern
        // as PwsOfferEmailListener from the 2026-04-13 ADR.
        log.info("DEV: password reset email would be sent to={} token={} expiresInMinutes={}",
                email, rawToken, TOKEN_TTL_MINUTES);
    }

    /**
     * Validate the token and update the user's password.
     *
     * @param rawToken    the plain-text token from the reset link (URL parameter)
     * @param newPassword the new plain-text password to BCrypt-encode
     * @throws IllegalArgumentException if the token is invalid, expired, or already used
     */
    @Transactional
    public void confirmReset(String rawToken, String newPassword) {
        String tokenHash = sha256Hex(rawToken);

        PasswordResetToken token = tokenRepository
                .findValidByHash(tokenHash, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        // Update the BCrypt hash — matches what AuthService validates on login
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token consumed — prevents reuse
        token.setConsumedAt(Instant.now());
        tokenRepository.save(token);

        log.info("Password reset completed for userId={}", user.getId());
    }

    // --- helpers ---

    private static String generateSecureToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(bytes);
        // Base64URL without padding — safe in URLs, no padding characters
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256Hex(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the JVM spec — this branch is unreachable
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
