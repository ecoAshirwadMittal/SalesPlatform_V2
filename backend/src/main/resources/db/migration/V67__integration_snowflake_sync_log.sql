-- Audit log for Snowflake sync runs (Theme 2 infra).
-- Used by AggregatedInventorySnowflakeSyncService (Phase 4) and future
-- Snowflake-backed syncs.
CREATE TABLE IF NOT EXISTS integration.snowflake_sync_log (
    id             BIGSERIAL PRIMARY KEY,
    sync_type      VARCHAR(64)  NOT NULL,
    target_key     VARCHAR(128),
    status         VARCHAR(32)  NOT NULL,
    rows_read      INTEGER      NOT NULL DEFAULT 0,
    rows_upserted  INTEGER      NOT NULL DEFAULT 0,
    error_message  TEXT,
    started_at     TIMESTAMP    NOT NULL,
    finished_at    TIMESTAMP,
    CONSTRAINT chk_sync_log_status CHECK (status IN ('STARTED','SUCCESS','FAILED','SKIPPED'))
);

CREATE INDEX IF NOT EXISTS idx_snowflake_sync_log_type_started
    ON integration.snowflake_sync_log (sync_type, started_at DESC);

CREATE INDEX IF NOT EXISTS idx_snowflake_sync_log_target
    ON integration.snowflake_sync_log (sync_type, target_key, started_at DESC);
