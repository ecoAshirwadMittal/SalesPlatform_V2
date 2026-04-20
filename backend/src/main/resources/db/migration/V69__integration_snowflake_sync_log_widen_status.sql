-- Phase 4 of docs/tasks/aggregated-inventory-snowflake-sync-plan.md
--
-- Widen integration.snowflake_sync_log.status CHECK so the aggregated-inventory
-- sync service can record the plan §3.3 result taxonomy directly. V67 installed
-- the coarse set ('STARTED','SUCCESS','FAILED','SKIPPED'); the service needs
-- to distinguish SKIPPED_LOCKED from SKIPPED_UP_TO_DATE, and uses COMPLETED
-- (not SUCCESS) on the happy path to match AggregatedInventorySyncResult.
--
-- SUCCESS is intentionally dropped — nothing writes it in this code path and
-- future readers would be tempted to map it to COMPLETED ambiguously. Widen
-- cleanly.
--
-- SKIPPED_DISABLED is not included: when snowflake.enabled=false the sync
-- service bean is absent and the caller short-circuits before any log row is
-- written (plan §6 test matrix).

ALTER TABLE integration.snowflake_sync_log
    DROP CONSTRAINT chk_sync_log_status;

ALTER TABLE integration.snowflake_sync_log
    ADD CONSTRAINT chk_sync_log_status
    CHECK (status IN ('STARTED','COMPLETED','FAILED','SKIPPED_LOCKED','SKIPPED_UP_TO_DATE'));
