-- =============================================================================
-- V47: Add expiry enforcement for password_tmp columns
-- Security fix: temporary passwords must have an expiry timestamp and be
-- cleaned up automatically.
-- =============================================================================

-- Add expiry timestamp column
ALTER TABLE user_mgmt.ecoatm_direct_users
  ADD COLUMN IF NOT EXISTS password_tmp_expires_at TIMESTAMP;

-- Clear any stale temporary passwords from migrated data
UPDATE user_mgmt.ecoatm_direct_users
SET password_tmp = NULL, password_confirm_tmp = NULL, password_tmp_expires_at = NULL
WHERE password_tmp IS NOT NULL;

-- Enforce: if password_tmp is set, expiry must also be set (and vice versa)
ALTER TABLE user_mgmt.ecoatm_direct_users
  ADD CONSTRAINT chk_password_tmp_requires_expiry
  CHECK (
    (password_tmp IS NULL AND password_tmp_expires_at IS NULL)
    OR (password_tmp IS NOT NULL AND password_tmp_expires_at IS NOT NULL)
  );
