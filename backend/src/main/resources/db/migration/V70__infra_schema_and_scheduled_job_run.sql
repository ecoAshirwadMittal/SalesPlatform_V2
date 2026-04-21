-- Infra schema for platform-level cross-cutting tables (job audit, locks, etc.)
CREATE SCHEMA IF NOT EXISTS infra;

CREATE TABLE infra.scheduled_job_run (
    id              BIGSERIAL PRIMARY KEY,
    job_name        VARCHAR(80)  NOT NULL,
    started_at      TIMESTAMPTZ  NOT NULL,
    finished_at     TIMESTAMPTZ,
    status          VARCHAR(20)  NOT NULL,
    node_id         VARCHAR(120) NOT NULL,
    duration_ms     INTEGER,
    error_message   TEXT,
    counters        JSONB,
    CONSTRAINT chk_sjr_status
      CHECK (status IN ('RUNNING','OK','FAILED','SKIPPED_LOCKED'))
);

CREATE INDEX idx_sjr_job_started
    ON infra.scheduled_job_run (job_name, started_at DESC);

COMMENT ON TABLE infra.scheduled_job_run IS
  'One row per cron tick. status RUNNING -> OK|FAILED|SKIPPED_LOCKED.';
COMMENT ON COLUMN infra.scheduled_job_run.counters IS
  'JSONB free-form per-job payload, e.g. {"roundsStarted":2,"roundsClosed":1,...}';
