-- =============================================================================
-- V2: Identity – Users, Roles, Sessions
-- Source: system$user, system$userrole, system$userroles, system$session,
--         system$session_user, system$language, system$timezone
--
-- Design notes:
--   • BIGINT PKs preserve Mendix ID space for seamless data seeding from prod.
--   • user_type discriminator column replaces Mendix submetaobjectname for STI,
--     keeping the raw value for traceability during migration.
--   • password is nullable — SSO users (AzureAD) authenticate externally.
--   • Timezone and language are normalised into lookup tables.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- Lookup: supported languages
-- Source: system$language
-- ---------------------------------------------------------------------------
CREATE TABLE identity.languages (
    id          BIGINT        PRIMARY KEY,
    code        VARCHAR(20)   NOT NULL,
    description VARCHAR(200)
);

COMMENT ON TABLE  identity.languages IS 'System-supported display languages (system$language)';
COMMENT ON COLUMN identity.languages.code IS 'BCP-47 locale code, e.g. en_US';

-- ---------------------------------------------------------------------------
-- Lookup: timezones
-- Source: system$timezone
-- ---------------------------------------------------------------------------
CREATE TABLE identity.timezones (
    id          BIGINT        PRIMARY KEY,
    code        VARCHAR(50)   NOT NULL,
    description VARCHAR(100),
    raw_offset  INTEGER                    -- milliseconds from UTC
);

COMMENT ON TABLE  identity.timezones IS 'IANA timezone definitions used across the platform (system$timezone)';
COMMENT ON COLUMN identity.timezones.raw_offset IS 'UTC offset in milliseconds, e.g. -18000000 for US/Eastern';

-- ---------------------------------------------------------------------------
-- Core user table (base of Mendix STI hierarchy)
-- Source: system$user
-- Subtypes: System.User / Administration.Account / EcoATM_UserManagement.EcoATMDirectUser
-- ---------------------------------------------------------------------------
CREATE TABLE identity.users (
    id                  BIGINT          PRIMARY KEY,
    -- Mendix STI discriminator — used during migration, maps to Administration.Account
    -- or EcoATM_UserManagement.EcoATMDirectUser for extended profiles
    user_type           VARCHAR(100)    NOT NULL DEFAULT 'System.User',
    name                VARCHAR(100)    NOT NULL,         -- login username / email
    password            VARCHAR(200),                     -- BCrypt hash; NULL for SSO-only users
    last_login          TIMESTAMP,
    blocked             BOOLEAN         NOT NULL DEFAULT false,
    blocked_since       TIMESTAMP,
    active              BOOLEAN         NOT NULL DEFAULT true,
    failed_logins       INTEGER         NOT NULL DEFAULT 0,
    web_service_user    BOOLEAN         NOT NULL DEFAULT false,
    is_anonymous        BOOLEAN         NOT NULL DEFAULT false,
    created_date        TIMESTAMP       NOT NULL DEFAULT NOW(),
    changed_date        TIMESTAMP       NOT NULL DEFAULT NOW(),
    -- Mendix audit columns mapped to application-layer owner tracking
    owner_id            BIGINT,
    changed_by_id       BIGINT
);

COMMENT ON TABLE  identity.users IS 'Base user table matching system$user, root of single-table inheritance hierarchy';
COMMENT ON COLUMN identity.users.user_type IS 'Mendix submetaobjectname discriminator: System.User | Administration.Account | EcoATM_UserManagement.EcoATMDirectUser';
COMMENT ON COLUMN identity.users.password  IS 'BCrypt-hashed password; NULL for pure SSO users who authenticate via AzureAD SAML';

-- ---------------------------------------------------------------------------
-- User ↔ Language (M:1, each user has one preferred language)
-- Source: system$user_language (always one record per user in prod)
-- ---------------------------------------------------------------------------
CREATE TABLE identity.user_languages (
    user_id     BIGINT  NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    language_id BIGINT  NOT NULL REFERENCES identity.languages(id),
    PRIMARY KEY (user_id, language_id)
);

-- ---------------------------------------------------------------------------
-- User ↔ Timezone (M:1, each user has one timezone)
-- Source: system$user_timezone
-- ---------------------------------------------------------------------------
CREATE TABLE identity.user_timezones (
    user_id     BIGINT  NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    timezone_id BIGINT  NOT NULL REFERENCES identity.timezones(id),
    PRIMARY KEY (user_id, timezone_id)
);

-- ---------------------------------------------------------------------------
-- Role definitions
-- Source: system$userrole
-- Known roles in prod: Bidder, SalesOps, ecoAtmDirectAdmin, SalesRep, Administrator, Co-Admin
-- ---------------------------------------------------------------------------
CREATE TABLE identity.user_roles (
    id          BIGINT          PRIMARY KEY,
    model_guid  VARCHAR(36)     NOT NULL UNIQUE,   -- stable GUID from Mendix model
    name        VARCHAR(100)    NOT NULL UNIQUE,
    description VARCHAR(1000)
);

COMMENT ON TABLE  identity.user_roles IS 'Application roles (system$userrole); name matches Mendix role model';
COMMENT ON COLUMN identity.user_roles.model_guid IS 'Immutable GUID from Mendix model, used to correlate roles across environments';

-- ---------------------------------------------------------------------------
-- Role ↔ Role grantability (which roles can assign which roles)
-- Source: system$grantableroles
-- ---------------------------------------------------------------------------
CREATE TABLE identity.grantable_roles (
    grantor_role_id BIGINT  NOT NULL REFERENCES identity.user_roles(id) ON DELETE CASCADE,
    grantee_role_id BIGINT  NOT NULL REFERENCES identity.user_roles(id) ON DELETE CASCADE,
    PRIMARY KEY (grantor_role_id, grantee_role_id)
);

COMMENT ON TABLE identity.grantable_roles IS 'Defines which roles can assign which other roles (system$grantableroles)';

-- ---------------------------------------------------------------------------
-- User ↔ Role assignments
-- Source: system$userroles
-- ---------------------------------------------------------------------------
CREATE TABLE identity.user_role_assignments (
    user_id     BIGINT  NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    role_id     BIGINT  NOT NULL REFERENCES identity.user_roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE identity.user_role_assignments IS 'Many-to-many user-to-role assignment (system$userroles)';

-- ---------------------------------------------------------------------------
-- Sessions
-- Source: system$session
-- ---------------------------------------------------------------------------
CREATE TABLE identity.sessions (
    id                      BIGINT          PRIMARY KEY,
    session_id              VARCHAR(50)     NOT NULL UNIQUE,  -- client-facing session token
    csrf_token              VARCHAR(36),
    last_active             TIMESTAMP,
    long_lived              BOOLEAN         NOT NULL DEFAULT false,
    read_only_hash_key      VARCHAR(36),
    last_action_execution   TIMESTAMP,
    created_date            TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  identity.sessions IS 'Active user sessions (system$session)';
COMMENT ON COLUMN identity.sessions.session_id IS 'Public session identifier sent to the browser';
COMMENT ON COLUMN identity.sessions.long_lived  IS 'True for remember-me / API token sessions';

-- ---------------------------------------------------------------------------
-- Session ↔ User
-- Source: system$session_user
-- ---------------------------------------------------------------------------
CREATE TABLE identity.session_users (
    session_id  BIGINT  NOT NULL REFERENCES identity.sessions(id) ON DELETE CASCADE,
    user_id     BIGINT  NOT NULL REFERENCES identity.users(id)    ON DELETE CASCADE,
    PRIMARY KEY (session_id, user_id)
);

-- ---------------------------------------------------------------------------
-- Indexes for common query patterns
-- ---------------------------------------------------------------------------
CREATE INDEX idx_users_name          ON identity.users(name);
CREATE INDEX idx_users_active        ON identity.users(active);
CREATE INDEX idx_users_user_type     ON identity.users(user_type);
CREATE INDEX idx_sessions_session_id ON identity.sessions(session_id);
CREATE INDEX idx_sessions_last_active ON identity.sessions(last_active);
