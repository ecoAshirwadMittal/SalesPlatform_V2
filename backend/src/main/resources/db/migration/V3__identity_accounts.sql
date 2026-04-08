-- =============================================================================
-- V3: Identity – Accounts (Administration Module)
-- Source: administration$account
--
-- Design notes:
--   • Extends identity.users via shared PK (joined-table inheritance).
--   • administration$account is the primary user subtype for internal staff.
--   • fullname and email are the two additional fields beyond base user.
--   • islocaluser distinguishes local-credential vs SSO-only users.
-- =============================================================================

CREATE TABLE identity.accounts (
    -- Joins to identity.users(id) — same PK, no surrogate needed
    user_id         BIGINT          PRIMARY KEY REFERENCES identity.users(id) ON DELETE CASCADE,
    full_name       VARCHAR(200),
    email           VARCHAR(200),
    is_local_user   BOOLEAN         NOT NULL DEFAULT true
);

COMMENT ON TABLE  identity.accounts IS 'Extended profile for Administration.Account users (administration$account). Joined to identity.users on user_id.';
COMMENT ON COLUMN identity.accounts.user_id       IS 'FK to identity.users — same value as system$user.id in Mendix';
COMMENT ON COLUMN identity.accounts.is_local_user IS 'True: local credential auth. False: SSO-only (AzureAD), no password used.';

CREATE INDEX idx_accounts_email ON identity.accounts(email);
