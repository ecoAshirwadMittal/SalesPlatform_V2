-- =============================================================================
-- V75: Password Reset Tokens (operational table)
-- Source: Phase 14 — /forgot-password route implementation
--
-- Design notes:
--   • V6__forgot_password.sql created sso.password_reset_tokens for migrated
--     Mendix legacy rows (AES-encrypted GUIDs, high-volume historical data).
--   • This new table, identity.password_reset_tokens, is the operational table
--     used by the live reset flow. It stores SHA-256 token hashes, enforces
--     short TTL (30 min), and is one-time-use (consumed_at sentinel).
--   • Kept separate from sso.password_reset_tokens to avoid coupling new logic
--     to the Mendix migration shape (different FK targets, different expiry
--     semantics, different token format).
-- =============================================================================

CREATE TABLE identity.password_reset_tokens (
    id           BIGSERIAL     PRIMARY KEY,
    user_id      BIGINT        NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    token_hash   VARCHAR(64)   NOT NULL,
    expires_at   TIMESTAMPTZ   NOT NULL,
    consumed_at  TIMESTAMPTZ,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

COMMENT ON TABLE  identity.password_reset_tokens IS 'Operational password-reset tokens. Short-lived (30 min), one-time-use. Separate from sso.password_reset_tokens (Mendix migration data).';
COMMENT ON COLUMN identity.password_reset_tokens.token_hash IS 'SHA-256 hex digest of the raw token sent in the reset link.';
COMMENT ON COLUMN identity.password_reset_tokens.consumed_at IS 'Non-null once the token has been used; subsequent use is rejected.';

-- Unique index ensures no two live tokens share the same hash
CREATE UNIQUE INDEX uq_prt_token_hash
    ON identity.password_reset_tokens (token_hash);

-- Partial index for fast "find valid token for user" lookups
CREATE INDEX idx_prt_user_active
    ON identity.password_reset_tokens (user_id)
    WHERE consumed_at IS NULL;
