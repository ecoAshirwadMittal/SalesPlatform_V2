-- =============================================================================
-- V1: Create PostgreSQL Schemas
-- Maps Mendix modules to PG namespaces for clean domain separation.
-- =============================================================================

-- Core identity: system$user, system$userrole, administration$account
CREATE SCHEMA IF NOT EXISTS identity;

-- ecoATM user management: extended user profiles
CREATE SCHEMA IF NOT EXISTS user_mgmt;

-- Buyer management: buyers, buyer codes, sales reps, qualified codes
CREATE SCHEMA IF NOT EXISTS buyer_mgmt;

-- SSO & authentication: SAML2/AzureAD config, forgot password
CREATE SCHEMA IF NOT EXISTS sso;

-- Comments for documentation
COMMENT ON SCHEMA identity   IS 'Core user identity: users, roles, sessions, accounts (system + administration modules)';
COMMENT ON SCHEMA user_mgmt  IS 'ecoATM direct user profiles, statuses, and user-buyer associations (ecoatm_usermanagement module)';
COMMENT ON SCHEMA buyer_mgmt IS 'Buyers, buyer codes, sales reps, qualified buyer codes and auction links (ecoatm_buyermanagement module)';
COMMENT ON SCHEMA sso        IS 'SAML 2.0 SSO configuration, request/response audit, password reset (saml20 + forgotpassword modules)';
