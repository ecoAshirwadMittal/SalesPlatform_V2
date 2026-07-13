package com.ecoatm.salesplatform.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Shared SHA-256 hex hashing for operational auth tokens (password-reset +
 * account-activation), which both persist and look up the digest of a raw token
 * in {@code identity.password_reset_tokens}.
 *
 * <p>This exists as a single implementation on purpose: account activation
 * deliberately redeems tokens issued by the password-reset flow through the same
 * table and {@code findValidByHash} query, so the two call sites MUST hash
 * byte-identically (same algorithm, charset, and encoding) or a token issued by
 * one would silently fail to resolve in the other. Centralising the digest makes
 * that coupling invariant impossible to drift.
 */
public final class TokenHasher {

    private TokenHasher() {
    }

    /** @return the lowercase hex SHA-256 digest of {@code input} (UTF-8 bytes). */
    public static String sha256Hex(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the JVM spec — this branch is unreachable.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
