-- =============================================================================
-- V6: SSO – Forgot Password / Signup Invitation Flow
-- Source: forgotpassword$forgotpassword, forgotpassword$configuration
--
-- Design notes:
--   • 34,181 forgotpassword records in prod — this is a high-volume table.
--   • forgotpasswdguid/url stored as AES-encrypted values in Mendix;
--     the new stack will store them as hashed tokens (bcrypt/SHA-256).
--   • is_signup=true records are buyer invitation emails (not password resets).
--   • Configuration is a singleton (one row in prod), modeled as such.
--   • Configuration's deeplink / email template references are stored as
--     text identifiers since those modules aren't migrated yet.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- Password reset / invitation tokens
-- Source: forgotpassword$forgotpassword
-- ---------------------------------------------------------------------------
CREATE TABLE sso.password_reset_tokens (
    id                  BIGINT        PRIMARY KEY,
    email_address       VARCHAR(200)  NOT NULL,
    username            VARCHAR(200)  NOT NULL,
    user_full_name      VARCHAR(200),
    -- Mendix stored AES-encrypted GUIDs; new stack stores plain UUID hashed on redemption
    token_value         VARCHAR(200),          -- was: forgotpasswordguid (AES-encrypted)
    reset_url           VARCHAR(500),          -- was: forgotpasswordurl (AES-encrypted)
    valid_until         TIMESTAMP     NOT NULL,
    is_signup           BOOLEAN       NOT NULL DEFAULT false,  -- true = buyer invitation
    created_date        TIMESTAMP     NOT NULL DEFAULT NOW(),
    -- FK to the account this token was issued for
    account_id          BIGINT        REFERENCES identity.accounts(user_id) ON DELETE CASCADE
);

COMMENT ON TABLE  sso.password_reset_tokens IS 'Password reset and buyer invitation tokens (forgotpassword$forgotpassword)';
COMMENT ON COLUMN sso.password_reset_tokens.is_signup IS 'true = buyer onboarding invite; false = standard password reset';
COMMENT ON COLUMN sso.password_reset_tokens.token_value IS 'Encrypted/hashed token; legacy Mendix values are AES3-encrypted';
COMMENT ON COLUMN sso.password_reset_tokens.valid_until IS 'Token expiry timestamp — expired tokens are cleaned up by scheduled job';

-- Anon user access (Mendix needed anonymous-user FK for deeplink resolution)
-- Mapped as a simple reference since anonymous sessions are transient
CREATE TABLE sso.password_reset_anon_access (
    token_id BIGINT NOT NULL REFERENCES sso.password_reset_tokens(id) ON DELETE CASCADE,
    user_id  BIGINT NOT NULL REFERENCES identity.users(id)            ON DELETE CASCADE,
    PRIMARY KEY (token_id, user_id)
);

COMMENT ON TABLE sso.password_reset_anon_access IS 'Anon user access grants for password reset deeplinks (forgotpassword$forgotpassword_anon_user_access)';

-- ---------------------------------------------------------------------------
-- Forgot Password Module Configuration (singleton)
-- Source: forgotpassword$configuration
-- ---------------------------------------------------------------------------
CREATE TABLE sso.forgot_password_config (
    id                      BIGINT  PRIMARY KEY,
    -- References to deeplink and email template modules (not yet migrated)
    -- Stored as text identifiers to enable future FK once those modules land
    deeplink_identifier     VARCHAR(200),   -- forgotpassword$configuration_deeplink
    reset_email_template    VARCHAR(200),   -- email template logical name
    signup_email_template   VARCHAR(200)    -- optional signup/invite template
);

COMMENT ON TABLE  sso.forgot_password_config IS 'Singleton config for forgot-password module (forgotpassword$configuration). One row only.';
COMMENT ON COLUMN sso.forgot_password_config.deeplink_identifier IS 'Logical name of the deeplink used in reset emails; FK added when deeplink module is migrated';

-- Performance indexes
CREATE INDEX idx_prt_email        ON sso.password_reset_tokens(email_address);
CREATE INDEX idx_prt_valid_until  ON sso.password_reset_tokens(valid_until);
CREATE INDEX idx_prt_is_signup    ON sso.password_reset_tokens(is_signup);
