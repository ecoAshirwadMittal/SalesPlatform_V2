-- =============================================================================
-- V11: Cross-Schema Foreign Keys, Deferred Constraints & Performance Indexes
--
-- This migration adds FK constraints that couldn't be defined inline because
-- they cross schema creation order boundaries (circular refs or schema ordering).
--
-- Also adds:
--   • FK from user_mgmt.user_buyers to buyer_mgmt.buyers (circular between V7/V8)
--   • Composite indexes for common join patterns in reports and APIs
--   • Partial indexes for "active only" query patterns
-- =============================================================================

-- =============================================================================
-- CROSS-SCHEMA FOREIGN KEY: user_mgmt.user_buyers → buyer_mgmt.buyers
-- Could not be defined in V7 because buyer_mgmt.buyers was created in V8
-- =============================================================================
ALTER TABLE user_mgmt.user_buyers
    ADD CONSTRAINT fk_user_buyers_buyer_id
    FOREIGN KEY (buyer_id) REFERENCES buyer_mgmt.buyers(id) ON DELETE CASCADE;

COMMENT ON CONSTRAINT fk_user_buyers_buyer_id ON user_mgmt.user_buyers
    IS 'Cross-schema FK: user_mgmt.user_buyers → buyer_mgmt.buyers, deferred to V11 due to schema creation order';

-- =============================================================================
-- COMPOSITE INDEXES FOR REPORT/API QUERY PATTERNS
-- =============================================================================

-- Buyer management: qualified code lookup by inclusion + type (auction dashboard query)
CREATE INDEX idx_qbc_included_type
    ON buyer_mgmt.qualified_buyer_codes(included, qualification_type)
    WHERE included = true;

-- Buyer management: active buyer codes with type (for round qualification queries)
CREATE INDEX idx_buyer_codes_active_type
    ON buyer_mgmt.buyer_codes(buyer_code_type, status)
    WHERE soft_delete = false AND status = 'Active';

-- Buyer management: buyer + status (admin listing page)
CREATE INDEX idx_buyers_status_created
    ON buyer_mgmt.buyers(status, created_date DESC);

-- Identity: user lookup by email (login flow — most common lookup)
CREATE INDEX idx_accounts_email_lower
    ON identity.accounts(LOWER(email));  -- case-insensitive email lookup

-- Identity: active users by type (role-scoped list pages)
CREATE INDEX idx_users_active_type
    ON identity.users(user_type, active)
    WHERE active = true;

-- User mgmt: users with buyer role for auction dashboard access check
CREATE INDEX idx_edu_active_buyer_role
    ON user_mgmt.ecoatm_direct_users(is_buyer_role, overall_user_status)
    WHERE is_buyer_role = true;

-- SSO: find SAML requests by returned principal (post-login lookup)
CREATE INDEX idx_saml_req_principal_created
    ON sso.saml_requests(returned_principal, sso_config_id);

-- SSO: password reset tokens by expiry (cleanup job + redemption flow)
CREATE INDEX idx_prt_account_valid
    ON sso.password_reset_tokens(account_id, valid_until);

-- Buyer management: buyer-salesrep join (sales rep management page)
CREATE INDEX idx_buyer_sales_reps_rep
    ON buyer_mgmt.buyer_sales_reps(sales_rep_id);

-- =============================================================================
-- ENUM-STYLE CHECK CONSTRAINTS (data quality guards)
-- =============================================================================

ALTER TABLE buyer_mgmt.qualified_buyer_codes
    ADD CONSTRAINT chk_qbc_qualification_type
    CHECK (qualification_type IN ('Qualified', 'Not_Qualified', 'Manual'));

ALTER TABLE buyer_mgmt.buyers
    ADD CONSTRAINT chk_buyer_status
    CHECK (status IN ('Active', 'Disabled'));

ALTER TABLE buyer_mgmt.buyer_codes
    ADD CONSTRAINT chk_buyer_code_status
    CHECK (status IN ('Active', 'Inactive'));

ALTER TABLE buyer_mgmt.buyer_codes
    ADD CONSTRAINT chk_buyer_code_type
    CHECK (buyer_code_type IN (
        'Wholesale', 'Data_Wipe',
        'Purchasing_Order_Data_Wipe', 'Purchasing_Order',
        'Premium_Wholesale'
    ));

ALTER TABLE user_mgmt.ecoatm_direct_users
    ADD CONSTRAINT chk_edu_user_status
    CHECK (user_status IN ('Active', 'Disabled') OR user_status IS NULL);

ALTER TABLE user_mgmt.ecoatm_direct_users
    ADD CONSTRAINT chk_edu_overall_status
    CHECK (overall_user_status IN ('Active', 'Disabled', 'Inactive') OR overall_user_status IS NULL);

ALTER TABLE user_mgmt.ecoatm_direct_users
    ADD CONSTRAINT chk_edu_landing_page
    CHECK (landing_page_preference IN ('Wholesale_Auction', 'Premium_Wholesale') OR landing_page_preference IS NULL);

ALTER TABLE sso.sso_audit_log
    ADD CONSTRAINT chk_sso_log_result
    CHECK (logon_result IN ('Success', 'Failure'));

ALTER TABLE identity.users
    ADD CONSTRAINT chk_user_type
    CHECK (user_type IN (
        'System.User',
        'Administration.Account',
        'EcoATM_UserManagement.EcoATMDirectUser'
    ));

-- =============================================================================
-- TRIGGER: auto-update changed_date on row updates
-- Applied to the tables most frequently updated in business logic
-- =============================================================================
CREATE OR REPLACE FUNCTION update_changed_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.changed_date = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to high-churn tables
DO $$
DECLARE
    t RECORD;
BEGIN
    FOR t IN VALUES
        ('identity',   'users'),
        ('identity',   'accounts'),
        ('identity',   'sessions'),
        ('user_mgmt',  'ecoatm_direct_users'),
        ('buyer_mgmt', 'buyers'),
        ('buyer_mgmt', 'buyer_codes'),
        ('buyer_mgmt', 'qualified_buyer_codes'),
        ('buyer_mgmt', 'sales_representatives'),
        ('sso',        'sso_configurations'),
        ('sso',        'sso_audit_log'),
        ('sso',        'password_reset_tokens')
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_update_changed_date
             BEFORE UPDATE ON %I.%I
             FOR EACH ROW EXECUTE FUNCTION update_changed_date()',
            t.column1, t.column2
        );
    END LOOP;
END $$;
