-- =============================================================================
-- V7: User Management – ecoATM Direct Users
-- Source: ecoatm_usermanagement$ecoatmdirectuser,
--         ecoatm_usermanagement$ecoatmdirectuser_buyer,
--         ecoatm_usermanagement$ecoatmdirectuser_userstatus,
--         ecoatm_usermanagement$userstatus
--
-- Design notes:
--   • EcoATMDirectUser extends identity.users (joined-table inheritance, same PK).
--   • 1,088 active users in prod — all external buyer-side users.
--   • userstatus is a Mendix lookup entity (currently 0 rows, dynamically populated).
--   • landing_page_preference enum covers Wholesale_Auction and Premium_Wholesale.
--   • Validation/display helper columns EXCLUDED:
--       userbuyerdisplay, userrolesdisplay, entityowner, entitychanger
--       (computed/display values, not domain data)
--   • overall_user_status is a computed status — kept for data migration parity.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- User status lookup (ecoatm_usermanagement$userstatus)
-- ---------------------------------------------------------------------------
CREATE TABLE user_mgmt.user_statuses (
    id           BIGINT        PRIMARY KEY,
    user_status  VARCHAR(8),   -- Active | Disabled | Inactive
    status_text  VARCHAR(200)
);

COMMENT ON TABLE user_mgmt.user_statuses IS 'User status lookup values (ecoatm_usermanagement$userstatus)';

-- ---------------------------------------------------------------------------
-- ecoATM Direct User extended profile
-- Source: ecoatm_usermanagement$ecoatmdirectuser
-- Joined to identity.users on the shared PK.
-- ---------------------------------------------------------------------------
CREATE TABLE user_mgmt.ecoatm_direct_users (
    -- Joins to identity.users(id) — same PK, no surrogate needed
    user_id                 BIGINT          PRIMARY KEY REFERENCES identity.users(id) ON DELETE CASCADE,

    -- Legacy Mendix submission ID (assigned sequentially by Mendix on object creation)
    submission_id           BIGINT,

    -- Personal info
    first_name              VARCHAR(200),
    last_name               VARCHAR(200),

    -- Invitation / activation lifecycle timestamps
    invited_date            TIMESTAMP,
    last_invite_sent        TIMESTAMP,
    activation_date         TIMESTAMP,

    -- Temporary fields for password-change UX (should remain NULL post-activation)
    password_tmp            VARCHAR(200),
    password_confirm_tmp    VARCHAR(200),

    -- Role flag for simplified role check at login
    is_buyer_role           BOOLEAN         NOT NULL DEFAULT false,

    -- User status
    user_status             VARCHAR(8),     -- Active | Disabled (direct field from Mendix)
    inactive                BOOLEAN         NOT NULL DEFAULT false,
    overall_user_status     VARCHAR(8),     -- Active | Disabled | Inactive (computed field, kept for data parity)

    -- Landing page preference after login
    landing_page_preference VARCHAR(17),    -- Wholesale_Auction | Premium_Wholesale

    -- Terms of service / user agreement acknowledgement
    acknowledgement         BOOLEAN         NOT NULL DEFAULT false,

    -- Audit
    created_date            TIMESTAMP       NOT NULL DEFAULT NOW(),
    changed_date            TIMESTAMP       NOT NULL DEFAULT NOW(),
    owner_id                BIGINT          REFERENCES identity.users(id),
    changed_by_id           BIGINT          REFERENCES identity.users(id)
);

COMMENT ON TABLE  user_mgmt.ecoatm_direct_users IS 'Extended profile for external buyer-facing users (ecoatm_usermanagement$ecoatmdirectuser). Joined to identity.users.';
COMMENT ON COLUMN user_mgmt.ecoatm_direct_users.submission_id IS 'Sequential Mendix object creation ID — used for data migration traceability';
COMMENT ON COLUMN user_mgmt.ecoatm_direct_users.is_buyer_role IS 'Denormalized flag: true when user has at least one Bidder role assignment — avoids role join on login';
COMMENT ON COLUMN user_mgmt.ecoatm_direct_users.landing_page_preference IS 'Where to redirect after login: Wholesale_Auction | Premium_Wholesale';
COMMENT ON COLUMN user_mgmt.ecoatm_direct_users.password_tmp IS 'Temporary field for password-change UX flow; should always be NULL outside of that flow';
COMMENT ON COLUMN user_mgmt.ecoatm_direct_users.overall_user_status IS 'Mendix-computed aggregate status (Active/Disabled/Inactive); retained for migration parity';

-- ---------------------------------------------------------------------------
-- Direct User ↔ Buyer (M:M — one user can represent multiple companies)
-- Source: ecoatm_usermanagement$ecoatmdirectuser_buyer
-- FK to buyer_mgmt.buyers added in V11 (after buyer_mgmt schema is created)
-- ---------------------------------------------------------------------------
CREATE TABLE user_mgmt.user_buyers (
    user_id     BIGINT  NOT NULL REFERENCES user_mgmt.ecoatm_direct_users(user_id) ON DELETE CASCADE,
    buyer_id    BIGINT  NOT NULL,  -- FK to buyer_mgmt.buyers added in V11
    PRIMARY KEY (user_id, buyer_id)
);

COMMENT ON TABLE user_mgmt.user_buyers IS 'Associates a direct user with one or more buyer companies (ecoatm_usermanagement$ecoatmdirectuser_buyer)';

-- ---------------------------------------------------------------------------
-- Direct User ↔ UserStatus (M:M — currently unused in prod, 0 rows)
-- Source: ecoatm_usermanagement$ecoatmdirectuser_userstatus
-- ---------------------------------------------------------------------------
CREATE TABLE user_mgmt.user_status_assignments (
    user_id         BIGINT  NOT NULL REFERENCES user_mgmt.ecoatm_direct_users(user_id) ON DELETE CASCADE,
    user_status_id  BIGINT  NOT NULL REFERENCES user_mgmt.user_statuses(id)            ON DELETE CASCADE,
    PRIMARY KEY (user_id, user_status_id)
);

COMMENT ON TABLE user_mgmt.user_status_assignments IS 'M:M user-to-status assignments (ecoatm_usermanagement$ecoatmdirectuser_userstatus); 0 rows in legacy prod';

-- Indexes
CREATE INDEX idx_edu_user_status    ON user_mgmt.ecoatm_direct_users(user_status);
CREATE INDEX idx_edu_overall_status ON user_mgmt.ecoatm_direct_users(overall_user_status);
CREATE INDEX idx_edu_buyer_role     ON user_mgmt.ecoatm_direct_users(is_buyer_role);
CREATE INDEX idx_edu_activation     ON user_mgmt.ecoatm_direct_users(activation_date);
