-- =============================================================================
-- V100: Make buyer_mgmt.buyer_code_change_logs insertable by the modern app.
--
-- The compliance buyer-code-type-change audit (legacy
-- SUB_LogBuyerCodeTypeChange_Compliance / BCO_LogBuyerCodeChange, which writes
-- the EcoATM_BuyerManagement.BuyerCodeChangeLog entity) targets the EXISTING
-- buyer_mgmt.buyer_code_change_logs table (created in V8, seeded in V18). We do
-- NOT create a new table — that table already carries the exact legacy fields
-- (old_buyer_code_type / new_buyer_code_type / edited_by / edited_on /
-- changed_by_id / owner_id) plus historical rows.
--
-- Blocker: its `id` is a plain BIGINT PRIMARY KEY with NO auto-generation
-- (V18 inserts explicit ids), so the app cannot INSERT new audit rows. Mirror
-- the V66 pattern (buyers / buyer_codes): add a sequence starting past the max
-- existing id and wire it as the column DEFAULT so the JPA IDENTITY strategy
-- (same as BuyerCode) fills it on insert.
--
-- Idempotent (IF NOT EXISTS) so a re-run against the shared dev DB is safe.
-- =============================================================================

DO $$
DECLARE
    max_log_id BIGINT;
BEGIN
    SELECT COALESCE(MAX(id), 0) INTO max_log_id FROM buyer_mgmt.buyer_code_change_logs;
    EXECUTE format(
        'CREATE SEQUENCE IF NOT EXISTS buyer_mgmt.buyer_code_change_logs_id_seq START WITH %s INCREMENT BY 1',
        max_log_id + 1);
END $$;

ALTER TABLE buyer_mgmt.buyer_code_change_logs
    ALTER COLUMN id SET DEFAULT nextval('buyer_mgmt.buyer_code_change_logs_id_seq');
ALTER SEQUENCE buyer_mgmt.buyer_code_change_logs_id_seq
    OWNED BY buyer_mgmt.buyer_code_change_logs.id;

-- Audit-lookup indexes (mirror auctions.qualified_buyer_code_audit's idx_qbca_*).
CREATE INDEX IF NOT EXISTS idx_bccl_buyer_code
    ON buyer_mgmt.buyer_code_change_logs(buyer_code_id);
CREATE INDEX IF NOT EXISTS idx_bccl_edited_on
    ON buyer_mgmt.buyer_code_change_logs(edited_on DESC);
