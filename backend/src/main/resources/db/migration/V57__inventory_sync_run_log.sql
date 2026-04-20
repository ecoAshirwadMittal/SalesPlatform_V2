-- ==============================================================================
-- V57: Inventory sync run log — Phase 3 of the PWS Data Center port.
--   Persists every ATP / Deposco inventory sync execution so the admin
--   Shipments & Sync page can show last run, duration, counts, failures.
--   Distinct from the planned integration.snowflake_sync_log (Theme 2);
--   this table records the inventory (Deposco) path only.
-- ==============================================================================

CREATE TABLE IF NOT EXISTS integration.sync_run_log (
    id                    BIGSERIAL PRIMARY KEY,
    sync_type             VARCHAR(32)  NOT NULL,
    status                VARCHAR(16)  NOT NULL,
    start_time            TIMESTAMPTZ  NOT NULL,
    end_time              TIMESTAMPTZ,
    total_items_received  INTEGER      NOT NULL DEFAULT 0,
    devices_updated       INTEGER      NOT NULL DEFAULT 0,
    devices_missing       INTEGER      NOT NULL DEFAULT 0,
    error_message         TEXT,
    triggered_by          VARCHAR(200),
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sync_run_log_start_time
    ON integration.sync_run_log (start_time DESC);

CREATE INDEX IF NOT EXISTS idx_sync_run_log_type_status
    ON integration.sync_run_log (sync_type, status);
