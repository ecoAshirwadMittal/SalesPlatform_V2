-- V85: Sub-project 6 Task 11 — R3 reports scheduling_auction_id + buyer_codes
-- Additive only: adds two columns to support bulk insert + delete operations.

-- ─── R3 reports FK + buyer_codes on round3_buyer_data_reports ────────────────
-- Add scheduling_auction_id: FK to track which R3 SA generated each report.
-- Add buyer_codes: comma-joined codes (created by bulkInsertForSchedulingAuction).
ALTER TABLE auctions.round3_buyer_data_reports
    ADD COLUMN scheduling_auction_id    BIGINT
       REFERENCES auctions.scheduling_auctions(id) ON DELETE CASCADE,
    ADD COLUMN buyer_codes             VARCHAR(1000);

COMMENT ON COLUMN auctions.round3_buyer_data_reports.scheduling_auction_id IS
    '6: Parent R3 scheduling auction. Populated by R3PreProcessService phase 5 (bulkInsertForSchedulingAuction).';
COMMENT ON COLUMN auctions.round3_buyer_data_reports.buyer_codes IS
    '6: Comma-joined buyer codes (R3-generated rows). Legacy Mendix rows use buyer_code (singular) instead.';

CREATE INDEX idx_r3rep_scheduling_auction ON auctions.round3_buyer_data_reports(scheduling_auction_id);
