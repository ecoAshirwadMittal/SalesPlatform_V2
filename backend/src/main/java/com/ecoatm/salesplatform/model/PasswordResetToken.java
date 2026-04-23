package com.ecoatm.salesplatform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Operational password-reset token row.
 *
 * <p>Stores the SHA-256 hex digest of the raw token sent in the reset link.
 * The raw token is never persisted — only the hash. Tokens expire after 30 min
 * and are one-time-use (consumed_at is set on redemption and checked on reuse).
 *
 * <p>See V75__auth_password_reset_tokens.sql.
 */
@Entity
@Table(name = "password_reset_tokens", schema = "identity")
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
