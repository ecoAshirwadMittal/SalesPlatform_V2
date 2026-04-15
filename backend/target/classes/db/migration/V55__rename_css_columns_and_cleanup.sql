-- =============================================================================
-- V55: Rename hex_code columns and remove dead code
--
-- Finding 24: internal_hex_code / external_hex_code are actually CSS classes
-- Finding 21: api_log.legacy_id is dead code from Mendix migration
-- =============================================================================

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 24: Rename misleading column names
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE pws.order_status_config
  RENAME COLUMN internal_hex_code TO internal_css_class;

ALTER TABLE pws.order_status_config
  RENAME COLUMN external_hex_code TO external_css_class;

-- ══════════════════════════════════════════════════════════════════════════════
-- Finding 21: Remove dead legacy_id column from api_log
-- ══════════════════════════════════════════════════════════════════════════════

ALTER TABLE integration.api_log DROP CONSTRAINT IF EXISTS api_log_legacy_id_key;
ALTER TABLE integration.api_log DROP COLUMN IF EXISTS legacy_id;
