-- ==============================================================================
-- V56: PWS admin audit — Phase 2 of the PWS Data Center port.
--   1. Add pws.offer.changed_by (email / principal) so the Offers admin grid
--      can show Mendix-parity "Changed By" without leaking raw admin emails
--      in every mutation log line.
--   2. Create pws.admin_audit_log — generic audit trail for admin mutations
--      (device soft-delete, future-price overrides, etc.). Replaces Mendix's
--      untracked trash-delete behaviour documented in
--      docs/tasks/pws-data-center-port.md → "Legacy anti-patterns".
-- ==============================================================================

ALTER TABLE pws.offer
    ADD COLUMN IF NOT EXISTS changed_by VARCHAR(200);

CREATE TABLE IF NOT EXISTS pws.admin_audit_log (
    id              BIGSERIAL PRIMARY KEY,
    entity_type     VARCHAR(64)  NOT NULL,
    entity_id       BIGINT       NOT NULL,
    action          VARCHAR(32)  NOT NULL,
    reason          VARCHAR(1000),
    actor           VARCHAR(200) NOT NULL,
    before_state    TEXT,
    after_state     TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_admin_audit_entity
    ON pws.admin_audit_log (entity_type, entity_id);

CREATE INDEX IF NOT EXISTS idx_admin_audit_created_at
    ON pws.admin_audit_log (created_at DESC);
