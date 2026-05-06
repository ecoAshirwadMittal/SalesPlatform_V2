-- Extra fixture for T7 R2SpecialBuyerRepositoryIT — layered ON TOP of
-- r2-init-seed.sql (loaded first via @Sql). This fixture seeds the
-- special-buyer scenarios for §7.2 special-treatment CTE coverage.
--
-- IDs use the 999110-999199 range, leaving 999100-999109 reserved for the
-- T6 buyers/codes/bid_data graph and avoiding collisions.
--
-- Scenarios:
--   * Buyer A (id=999111, is_special_buyer=TRUE) — 2 DW/WH codes (999111 WH,
--     999112 DW). NEITHER bid in any prior round → both codes are STB-eligible
--     → buyer-A is "all codes STB" → both codes appear in result.
--   * Buyer B (id=999112, is_special_buyer=TRUE) — 2 DW/WH codes (999113 WH,
--     999114 DW). Code 999113 has a submitted R1 bid (via existing R1 SA
--     999101); code 999114 did NOT bid. Per design 3.7's "all codes must
--     qualify" semantics, Buyer B is NOT all-codes-STB → NEITHER 999113 NOR
--     999114 appears in result.
--   * Buyer C (id=999113, is_special_buyer=TRUE) — 1 DW/WH code (999115 WH).
--     Did not bid → STB-eligible → 999115 appears in result.
--   * Regular Buyer D (id=999114, is_special_buyer=FALSE) — 1 WH code
--     (999116). Should NEVER appear in result regardless of bid history.

-- Buyers: 3 special, 1 regular.
INSERT INTO buyer_mgmt.buyers (id, company_name, status, is_special_buyer)
VALUES
  (999111, 'R2 Test Special Buyer A — all codes STB',     'Active', TRUE),
  (999112, 'R2 Test Special Buyer B — mixed codes',       'Active', TRUE),
  (999113, 'R2 Test Special Buyer C — single STB code',   'Active', TRUE),
  (999114, 'R2 Test Regular Buyer D — not special',       'Active', FALSE);

-- Buyer codes — DW + Wholesale only. Status=Active.
INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, status)
VALUES
  (999111, 'R2T-SPEC-A-WH',    'Wholesale', 'Active'),  -- Buyer A code 1
  (999112, 'R2T-SPEC-A-DW',    'Data_Wipe', 'Active'),  -- Buyer A code 2
  (999113, 'R2T-SPEC-B-WH',    'Wholesale', 'Active'),  -- Buyer B code 1 (bid in R1)
  (999114, 'R2T-SPEC-B-DW',    'Data_Wipe', 'Active'),  -- Buyer B code 2 (no bid)
  (999115, 'R2T-SPEC-C-WH',    'Wholesale', 'Active'),  -- Buyer C only code
  (999116, 'R2T-REG-D-WH',     'Wholesale', 'Active');  -- Regular Buyer D

-- M:M links.
INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
VALUES
  (999111, 999111),
  (999112, 999111),
  (999113, 999112),
  (999114, 999112),
  (999115, 999113),
  (999116, 999114);

-- Buyer B code 999113 bid in R1 (against existing R1 SA 999101 from T6 seed).
-- This is the "any prior-round bid disqualifies the whole DW/WH set" trigger.
INSERT INTO auctions.bid_rounds (id, scheduling_auction_id, buyer_code_id, week_id, submitted)
VALUES
  (999113, 999101, 999113, 999100, TRUE);

INSERT INTO auctions.bid_data
  (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name, buyer_code_type,
   submitted_bid_amount, bid_round, week_id)
VALUES
  -- Buyer B's WH code: bid 75 vs target 100 in R1 — value irrelevant for the
  -- STB CTE (it only cares about bid_count > 0).
  (999113, 999113, 999113, 'ECO-X', 'A', 'R2T-SPEC-B-WH',
   'R2 Test Special Buyer B — mixed codes', 'Wholesale', 75.00, 1, 999100);
