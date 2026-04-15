-- =============================================================================
-- V46: Remove credential columns from integration and email config tables
-- Security fix: credentials must come from environment variables / secrets
-- manager, not from database columns.
-- =============================================================================

-- Oracle config: remove username and password_hash
ALTER TABLE integration.oracle_config DROP COLUMN IF EXISTS username;
ALTER TABLE integration.oracle_config DROP COLUMN IF EXISTS password_hash;

-- Deposco config: remove username and password_hash
ALTER TABLE integration.deposco_config DROP COLUMN IF EXISTS username;
ALTER TABLE integration.deposco_config DROP COLUMN IF EXISTS password_hash;

-- Email SMTP config: remove username and encrypted_password
ALTER TABLE email.smtp_config DROP COLUMN IF EXISTS username;
ALTER TABLE email.smtp_config DROP COLUMN IF EXISTS encrypted_password;
