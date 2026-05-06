-- Fixture for sub-project 5 (R2 Buyer Assignment) repository tests.
-- All IDs use the 999100-999199 range to avoid colliding with the 4C
-- recalc-seed.sql range (999001-999014) and any V18 production-style data
-- (1-700ish for buyer codes / buyers).
--
-- Seeds the minimum graph needed to exercise every branch of
-- R2BuyerQualificationRepository.CTE_SQL:
--   * mdm.week id=999100 (year 2026)
--   * auctions.auctions id=999100 tied to that week
--   * 2 scheduling_auctions: id=999101 (R1, Closed), id=999102 (R2, Started)
--   * 1 bid_round_selection_filters row for round=2 with the *defaults the
--     plan tests start from* — Only_Qualified + InventoryRound1QualifiedBids,
--     target_percent=0.05 (5%), target_value=1.00. Tests UPDATE the row
--     to flip qual_mode / inv_mode for individual scenarios.
--   * 3 buyers (2 Active, 1 Disabled)
--   * 9 buyer_codes covering every dimension the CTE filters on:
--       - 999101 Wholesale  / Active   — bid above target  → qualifies
--       - 999102 Wholesale  / Active   — bid -<5% target   → qualifies (pct band)
--       - 999103 Wholesale  / Active   — bid -<$1 target   → qualifies (flat band)
--       - 999104 Wholesale  / Active   — bid far below     → fallback territory
--       - 999105 Data_Wipe  / Active   — bid above DW target (DW branch)
--       - 999106 Wholesale  / Active   — bid_amount = 0    → never qualifies
--       - 999107 Purchasing_Order      → must be excluded by type filter
--       - 999108 Wholesale  / DISABLED → must be excluded by buyer.status
--       - 999109 Wholesale  / Active   — never bid in R1 (no_bid_universe)
--   * 1 bid_round per (R1 SA, buyer_code) with submitted=TRUE — only buyer
--     codes that actually bid in R1 get a bid_round row, mirroring how R1
--     submission flow writes them.
--   * aggregated_inventory rows for ECO-X (target=100), ECO-Y (target=0),
--     covering the divide-by-zero branch.
--   * 5 R1 bid_data rows hitting each predicate branch.

-- mdm.week
INSERT INTO mdm.week (id, week_id, year, week_number, week_start_datetime, week_end_datetime, week_display, week_display_short, week_number_string)
VALUES (999100, 999919, 2026, 19, '2026-05-04 00:00:00+00', '2026-05-10 23:59:59+00', '2026 / Wk19', 'Wk19', '19');

-- auctions.auctions
INSERT INTO auctions.auctions (id, auction_title, auction_status, week_id)
VALUES (999100, 'R2-init Test Auction', 'Started', 999100);

-- scheduling_auctions: R1 (Closed) + R2 (Started)
INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, r2_init_status)
VALUES
  (999101, 999100, 1, 'Closed',  'PENDING'),
  (999102, 999100, 2, 'Started', 'PENDING');

-- bid_round_selection_filters for round=2.
-- Defaults the IT starts from. Individual tests UPDATE these per scenario.
INSERT INTO auctions.bid_round_selection_filters
  (id, round, target_percent, target_value, regular_buyer_qualification, regular_buyer_inventory_options)
VALUES
  (999102, 2, 0.05, 1.00, 'Only_Qualified', 'InventoryRound1QualifiedBids');

-- Buyers (V8 schema: id is BIGINT, not BIGSERIAL — must be explicit)
INSERT INTO buyer_mgmt.buyers (id, company_name, status, is_special_buyer)
VALUES
  (999101, 'R2 Test Active Buyer A', 'Active',   false),
  (999102, 'R2 Test Active Buyer B', 'Active',   false),
  (999103, 'R2 Test Disabled Buyer', 'Disabled', false);

-- Buyer codes spanning every dimension the qualification CTE cares about.
INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, status)
VALUES
  (999101, 'R2T-WH-ABOVE',    'Wholesale',        'Active'),
  (999102, 'R2T-WH-PCT',      'Wholesale',        'Active'),
  (999103, 'R2T-WH-FLAT',     'Wholesale',        'Active'),
  (999104, 'R2T-WH-FAR',      'Wholesale',        'Active'),
  (999105, 'R2T-DW',          'Data_Wipe',        'Active'),
  (999106, 'R2T-WH-ZERO',     'Wholesale',        'Active'),
  (999107, 'R2T-PO',          'Purchasing_Order', 'Active'),  -- excluded by type filter
  (999108, 'R2T-WH-DISABLED', 'Wholesale',        'Active'),  -- excluded by buyer.status
  (999109, 'R2T-WH-NOBID',    'Wholesale',        'Active');  -- never bid in R1

-- Buyer-code ↔ buyer M:M links. 999108 belongs to disabled buyer 999103.
-- Code 999101 is intentionally linked to TWO active buyers (999101 + 999102)
-- to exercise the GROUP BY bc.id collapse of the M:M fan-out in
-- QualifiedBuyerCodeRepositoryImpl#bulkInsertForR2. Buyer 999102 is Active
-- and is_special_buyer=FALSE, so this extra link does not affect T6/T7
-- qualification or special-treatment assertions (buyer 999102 stays out of
-- the special_buyers CTE; code 999101 is asserted via contains() in T6).
INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
VALUES
  (999101, 999101),
  (999101, 999102),  -- M:M fan-out: code 999101 linked to a second active buyer
  (999102, 999101),
  (999103, 999101),
  (999104, 999101),
  (999105, 999102),
  (999106, 999102),
  (999107, 999102),
  (999108, 999103),  -- disabled buyer
  (999109, 999102);

-- bid_rounds — one per (R1 SA, buyer_code) for codes that actually bid in R1.
-- submitted=TRUE so the qualification CTE's br.submitted filter passes.
-- Per design 3.5c (PO codes don't participate in R2 auction-bid flow) and
-- buyer.status='Disabled' (account can't log in), the fixture mirrors legacy
-- data by NOT writing bid_round / bid_data rows for PO codes (999107) or
-- Disabled-buyer codes (999108). The exclusion tests verify these never show
-- up in the result via the active_codes universe path.
INSERT INTO auctions.bid_rounds (id, scheduling_auction_id, buyer_code_id, week_id, submitted)
VALUES
  (999101, 999101, 999101, 999100, TRUE),
  (999102, 999101, 999102, 999100, TRUE),
  (999103, 999101, 999103, 999100, TRUE),
  (999104, 999101, 999104, 999100, TRUE),
  (999105, 999101, 999105, 999100, TRUE),
  (999106, 999101, 999106, 999100, TRUE);

-- aggregated_inventory rows.
-- ECO-X grade A: avg_target_price=100, dw_avg_target_price=200
--   → Wholesale threshold = 95 (pct), 99 (flat)
--   → Data_Wipe threshold = 190 (pct), 199 (flat)
-- ECO-Y grade A: avg_target_price=0 — divide-by-zero branch
INSERT INTO auctions.aggregated_inventory
  (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity, avg_target_price, dw_avg_target_price)
VALUES
  (999101, 'ECO-X', 999100, 'A', 100, 50, 100.0000, 200.0000),
  (999102, 'ECO-Y', 999100, 'A', 100, 50,   0.0000,   0.0000);

-- R1 bid_data — hits every predicate branch.
-- buyer_code_type column is denormalized; the CTE reads it directly from bid_data.
-- All rows are bid_round=1 against the R1 SA. submitted_bid_amount is the
-- field the CTE compares to r1_target_price.
INSERT INTO auctions.bid_data
  (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name, buyer_code_type,
   submitted_bid_amount, bid_round, week_id)
VALUES
  -- 999101: WH ABOVE — bid 120 vs target 100 → qualifies (Only_Qualified strict)
  (999101, 999101, 999101, 'ECO-X', 'A', 'R2T-WH-ABOVE',    'R2 Test Active Buyer A', 'Wholesale', 120.00, 1, 999100),
  -- 999102: WH PCT — bid 96 vs target 100 (96/100 = 0.96 ≥ 1 - 0.05) → qualifies pct band
  (999102, 999102, 999102, 'ECO-X', 'A', 'R2T-WH-PCT',      'R2 Test Active Buyer A', 'Wholesale',  96.00, 1, 999100),
  -- 999103: WH FLAT — bid 99.50 vs target 100 (100 - 99.50 = 0.50 ≤ 1.00) → qualifies flat band
  (999103, 999103, 999103, 'ECO-X', 'A', 'R2T-WH-FLAT',     'R2 Test Active Buyer A', 'Wholesale',  99.50, 1, 999100),
  -- 999104: WH FAR — bid 50 vs target 100 → fails strict (50/100=0.5; gap=50)
  -- → qualifies under InventoryRound1QualifiedBids (bid>0) and ShowAllInventory.
  (999104, 999104, 999104, 'ECO-X', 'A', 'R2T-WH-FAR',      'R2 Test Active Buyer A', 'Wholesale',  50.00, 1, 999100),
  -- 999105: DW above DW target — bid 210 vs dw target 200 → qualifies
  (999105, 999105, 999105, 'ECO-X', 'A', 'R2T-DW',          'R2 Test Active Buyer B', 'Data_Wipe', 210.00, 1, 999100),
  -- 999106: WH ZERO — submitted_bid_amount=0 → CTE filters it out at r1_bids (bid_amount > 0).
  (999106, 999106, 999106, 'ECO-X', 'A', 'R2T-WH-ZERO',     'R2 Test Active Buyer B', 'Wholesale',   0.00, 1, 999100);
-- Note: no bid_data rows for 999107 (PO) or 999108 (Disabled buyer's code) —
-- those codes never reach R1 submission in legacy data shape (see design 3.5c).
