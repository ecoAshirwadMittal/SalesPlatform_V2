-- Fixture for sub-project 6 (R3 Init + Pre-process) repository / service / E2E tests.
-- All IDs use the 6XX / 60XXX range to avoid colliding with sub-project 5's
-- r2-init-seed.sql (999100-999109 range) and the 4C recalc-seed.sql
-- (999001-999014 range).
--
-- Seeds the minimum graph needed to exercise every branch of the R3
-- pre-process pipeline:
--
--   Auction 600 ("full path"):
--     SA 6001 round=1 (Closed), SA 6002 round=2 (Closed), SA 6003 round=3 (Started)
--     BidData: ACME-WH R1+R2, ACME-DW R2, BETA-WH R2, GAMMA-WH R2, EPSI-WH R1
--     + two deletion-target rows on SA 6002 (bid_amount=0, bid_amount=0/submitted=false)
--
--   Auction 601 ("no R3"):
--     SA 6011 round=1, SA 6012 round=2 — no R3 SA (listener silent-skip path)
--
--   Auction 602 ("has_round=false R3"):
--     SA 6021 round=1, SA 6022 round=2, SA 6023 round=3 has_round=false (SKIPPED path)
--
-- BRSF rows:
--   id=601  round=2 (defaults the IT starts from; individual tests UPDATE per scenario)
--   id=602  round=3 with bid_percentage_variation=5, bid_amount_variation=1, rank_qualification_limit=3
--
-- Buyer qualification branches exercised by the bid_data rows:
--   ACME-WH  (60001): latest R2 bid 105 vs WH target 100 → pct branch (5%:  100-5=95; 105>=95)
--   ACME-DW  (60002): latest R2 bid 88  vs DW target 90  → pct branch (5%:  90-4.5=85.5; 88>=85.5)
--   BETA-WH  (60003): latest R2 bid 20  vs WH target 50  → rank branch (rank=2 <= limit=3)
--   GAMMA-WH (60004): latest R2 bid 10  vs WH target 75  → NO branch (rank=10 > 3; 10<75-3.75=71.25; 10<75-1=74)
--   EPSI-WH  (60007): only R1 bid (prior bid → Epsilon NOT STB-eligible)
--
-- Deletion targets on R2 SA (bid_amount=0):
--   id=60007  bid_amount=0, submitted=false
--   id=60008  bid_amount=0, submitted=false

-- ─── mdm.week ────────────────────────────────────────────────────────────────
-- week_id is the Mendix business-key (BIGINT NOT NULL UNIQUE, distinct from PK id).
-- week_start_datetime, week_end_datetime, week_display are also NOT NULL in the live DB.
-- Use 60901-60903 as business-key values (week_id) to avoid collision with
-- production rows (Wk01-Wk52 use 1-52 business keys; seeded QA rows use higher values).
INSERT INTO mdm.week (id, week_id, year, week_number,
                      week_start_datetime, week_end_datetime,
                      week_display, week_display_short, week_number_string)
VALUES
    (601, 60901, 2026, 19,
     '2026-05-04 00:00:00+00', '2026-05-10 23:59:59+00',
     '2026 / Wk19', 'Wk19', '19'),
    (602, 60902, 2026, 20,
     '2026-05-11 00:00:00+00', '2026-05-17 23:59:59+00',
     '2026 / Wk20', 'Wk20', '20'),
    (603, 60903, 2026, 21,
     '2026-05-18 00:00:00+00', '2026-05-24 23:59:59+00',
     '2026 / Wk21', 'Wk21', '21')
ON CONFLICT (id) DO NOTHING;

-- ─── auctions.auctions ───────────────────────────────────────────────────────
INSERT INTO auctions.auctions (id, auction_title, auction_status, week_id, created_date, changed_date)
VALUES
    (600, 'R3-Test-Full',       'Started', 601, NOW(), NOW()),
    (601, 'R3-Test-NoR3',       'Started', 602, NOW(), NOW()),
    (602, 'R3-Test-DisabledR3', 'Started', 603, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── auctions.scheduling_auctions ────────────────────────────────────────────
-- Auction 600: R1 (Closed), R2 (Closed), R3 (Started, has_round=true)
INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, has_round, created_date, changed_date)
VALUES
    (6001, 600, 1, 'Closed',  true,  NOW(), NOW()),
    (6002, 600, 2, 'Closed',  true,  NOW(), NOW()),
    (6003, 600, 3, 'Started', true,  NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Auction 601: R1 + R2 only — no R3 SA at all (listener silent-skip path)
INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, has_round, created_date, changed_date)
VALUES
    (6011, 601, 1, 'Closed',  true,  NOW(), NOW()),
    (6012, 601, 2, 'Closed',  true,  NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Auction 602: R3 SA exists but has_round=false (SKIPPED path for pre-process)
INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, has_round, created_date, changed_date)
VALUES
    (6021, 602, 1, 'Closed',  true,  NOW(), NOW()),
    (6022, 602, 2, 'Closed',  true,  NOW(), NOW()),
    (6023, 602, 3, 'Started', false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── auctions.bid_round_selection_filters ────────────────────────────────────
-- Round 2: target_percent=5 (whole-percent, per sub-project 6 alignment), target_value=1.00
--   Individual tests UPDATE these columns per scenario.
-- Round 3: bid_percentage_variation=5, bid_amount_variation=1.00, rank_qualification_limit=3
--   Individual tests UPDATE these to NULL to exercise all-null / single-branch paths.
INSERT INTO auctions.bid_round_selection_filters
    (id, round, target_percent, target_value,
     regular_buyer_qualification, regular_buyer_inventory_options,
     stb_allow_all_buyers_override, stb_include_all_inventory,
     bid_percentage_variation, bid_amount_variation, rank_qualification_limit,
     created_date, changed_date)
VALUES
    (601, 2, 5, 1.00,
     'Only_Qualified', 'InventoryRound1QualifiedBids',
     false, false,
     NULL, NULL, NULL,
     NOW(), NOW()),
    (602, 3, NULL, NULL,
     'Only_Qualified', 'InventoryRound1QualifiedBids',
     false, false,
     5, 1.00, 3,
     NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── auctions.aggregated_inventory ───────────────────────────────────────────
-- 6 (ecoid, grade) rows for week 601.
-- round3_target_price is the R3 qualification threshold used by the CTE.
-- ECO-F is deprecated (is_deprecated=true) — R3 CTE should skip it.
INSERT INTO auctions.aggregated_inventory
    (id, week_id, ecoid2, merged_grade,
     total_quantity, dw_total_quantity,
     avg_target_price, dw_avg_target_price, round3_target_price,
     is_deprecated, created_date, changed_date)
VALUES
    (60001, 601, 'ECO-A', 'Grade_A', 100, 50, 100.00, 90.00, 100.00, false, NOW(), NOW()),
    (60002, 601, 'ECO-B', 'Grade_A', 200, 80,  50.00, 45.00,  50.00, false, NOW(), NOW()),
    (60003, 601, 'ECO-C', 'Grade_B', 150, 60,  75.00, 70.00,  75.00, false, NOW(), NOW()),
    (60004, 601, 'ECO-D', 'Grade_B', 100, 40,  25.00, 22.00,  25.00, false, NOW(), NOW()),
    (60005, 601, 'ECO-E', 'Grade_C',  80, 30,  40.00, 38.00,  40.00, false, NOW(), NOW()),
    (60006, 601, 'ECO-F', 'Grade_C',  60, 20,  60.00, 55.00,  60.00, true,  NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── buyer_mgmt.buyers ───────────────────────────────────────────────────────
-- 5 buyers: 3 regular Active, 2 special (is_special_buyer=true).
-- Delta SpecialA (6004): no prior bid_round rows → STB-eligible.
-- Epsilon SpecialB (6005): EPSI-WH has an R1 bid_round → NOT STB-eligible
--   (any prior bid by any code owned by the buyer disqualifies the whole buyer).
INSERT INTO buyer_mgmt.buyers (id, company_name, status, is_special_buyer, created_date, changed_date)
VALUES
    (6001, 'Acme Corp',        'Active',   false, NOW(), NOW()),
    (6002, 'Beta Inc',         'Active',   false, NOW(), NOW()),
    (6003, 'Gamma LLC',        'Active',   false, NOW(), NOW()),
    (6004, 'Delta SpecialA',   'Active',   true,  NOW(), NOW()),
    (6005, 'Epsilon SpecialB', 'Active',   true,  NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── buyer_mgmt.buyer_codes ──────────────────────────────────────────────────
-- 8 codes: mix of Wholesale and Data_Wipe.
-- DELTA-WH/DW owned by Delta (STB-eligible buyer — no prior bids on any code).
-- EPSI-WH owned by Epsilon (NOT STB-eligible — R1 bid exists on EPSI-WH).
-- EPSI-DW also owned by Epsilon — no bid_round, but Epsilon's STB status is
--   determined at buyer level (EPSI-WH's bid disqualifies the whole buyer).
-- NOTE: column is "soft_delete" (not "soft_deleted") per V8 migration.
INSERT INTO buyer_mgmt.buyer_codes (id, code, buyer_code_type, soft_delete, created_date, changed_date)
VALUES
    (60001, 'ACME-WH',  'Wholesale',  false, NOW(), NOW()),
    (60002, 'ACME-DW',  'Data_Wipe',  false, NOW(), NOW()),
    (60003, 'BETA-WH',  'Wholesale',  false, NOW(), NOW()),
    (60004, 'GAMMA-WH', 'Wholesale',  false, NOW(), NOW()),
    (60005, 'DELTA-WH', 'Wholesale',  false, NOW(), NOW()),
    (60006, 'DELTA-DW', 'Data_Wipe',  false, NOW(), NOW()),
    (60007, 'EPSI-WH',  'Wholesale',  false, NOW(), NOW()),
    (60008, 'EPSI-DW',  'Data_Wipe',  false, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ─── buyer_mgmt.buyer_code_buyers ────────────────────────────────────────────
INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
VALUES
    (60001, 6001), (60002, 6001),           -- Acme owns ACME-WH + ACME-DW
    (60003, 6002),                          -- Beta owns BETA-WH
    (60004, 6003),                          -- Gamma owns GAMMA-WH
    (60005, 6004), (60006, 6004),           -- Delta SpecialA owns DELTA-WH + DELTA-DW
    (60007, 6005), (60008, 6005)            -- Epsilon SpecialB owns EPSI-WH + EPSI-DW
ON CONFLICT (buyer_code_id, buyer_id) DO NOTHING;

-- ─── auctions.bid_rounds ─────────────────────────────────────────────────────
-- R1 bid_rounds for auction 600 (SA 6001): codes that submitted in R1.
-- R2 bid_rounds for auction 600 (SA 6002): codes that submitted in R2.
-- DELTA-WH/DW have NO bid_rounds at all → Delta SpecialA is STB-eligible.
-- EPSI-WH has an R1 bid_round → Epsilon SpecialB is NOT STB-eligible.
-- EPSI-DW has no bid_rounds (but Epsilon is disqualified at buyer level).
INSERT INTO auctions.bid_rounds (id, scheduling_auction_id, buyer_code_id, week_id, submitted, submitted_datetime)
VALUES
    -- R1 rounds (SA 6001)
    (60001, 6001, 60001, 601, true, '2026-05-01 10:00:00+00'),   -- ACME-WH  R1
    (60002, 6001, 60002, 601, true, '2026-05-01 10:00:00+00'),   -- ACME-DW  R1
    (60003, 6001, 60003, 601, true, '2026-05-01 10:00:00+00'),   -- BETA-WH  R1
    (60004, 6001, 60004, 601, true, '2026-05-01 10:00:00+00'),   -- GAMMA-WH R1
    (60005, 6001, 60007, 601, true, '2026-05-01 10:00:00+00'),   -- EPSI-WH  R1 (prior bid → disqualifies STB)
    -- R2 rounds (SA 6002)
    (60006, 6002, 60001, 601, true, '2026-05-02 10:00:00+00'),   -- ACME-WH  R2
    (60007, 6002, 60002, 601, true, '2026-05-02 10:00:00+00'),   -- ACME-DW  R2
    (60008, 6002, 60003, 601, true, '2026-05-02 10:00:00+00'),   -- BETA-WH  R2
    (60009, 6002, 60004, 601, true, '2026-05-02 10:00:00+00')    -- GAMMA-WH R2
    -- Note: DELTA-WH/DW have NO bid_rounds → Delta SpecialA is STB-eligible.
    -- Note: EPSI-DW has NO bid_round row, but buyer 6005 is disqualified via EPSI-WH.
ON CONFLICT (id) DO NOTHING;

-- ─── auctions.bid_data ───────────────────────────────────────────────────────
-- Hits each R3 qualification branch (percentage, amount, rank, no-match)
-- plus the R1-fallback and deletion-target paths.
--
-- Qualification math (BRSF round=3: pct=5, amount=1.00, rank_limit=3):
--   ACME-WH  latest R2 bid=105 vs round3_target_price=100:
--             pct branch: 100 - (100*5/100) = 95; 105 >= 95  → QUALIFIES (pct)
--   ACME-DW  latest R2 bid=88  vs round3_target_price=90 (DW target):
--             pct branch: 90  - (90*5/100)  = 85.5; 88 >= 85.5 → QUALIFIES (pct)
--   BETA-WH  latest R2 bid=20  vs round3_target_price=50:
--             pct branch: 50  - (50*5/100)  = 47.5; 20 < 47.5  → fail
--             amt branch: 50  - 1.00 = 49.0; 20 < 49.0 → fail
--             rank branch: round3_bid_rank=2 <= limit=3 → QUALIFIES (rank)
--   GAMMA-WH latest R2 bid=10  vs round3_target_price=75:
--             pct branch: 75  - (75*5/100)  = 71.25; 10 < 71.25 → fail
--             amt branch: 75  - 1.00 = 74.0; 10 < 74.0 → fail
--             rank branch: round3_bid_rank=10 > limit=3 → fail → DOES NOT QUALIFY
--   EPSI-WH  R1-only bid=15 vs round3_target_price=25:
--             pct branch: 25  - (25*5/100)  = 23.75; 15 < 23.75 → fail
--             amt branch: 25  - 1.00 = 24.0; 15 < 24.0 → fail
--             rank branch: round3_bid_rank=12 > 3 → fail → DOES NOT QUALIFY
--
-- Deletion-target rows (bid_amount=0, submitted=false on R2 SA):
--   id=60007  ACME-WH on ECO-E, bid_amount=0 — pre-process Phase 1 deletes this
--   id=60008  BETA-WH on ECO-E, bid_amount=0 — pre-process Phase 1 deletes this
--
-- Column notes:
--   • bid_amount is NOT NULL DEFAULT 0 in V61 — cannot insert NULL; use 0 for deletion targets.
--   • code / company_name are denormalized VARCHAR columns (not FKs).
--   • bid_round is denormalized INTEGER (1-3).
--   • week_id on bid_data is INTEGER (plain int, not FK per V61 comment).
--   • highest_bid is BOOLEAN NOT NULL DEFAULT false.
--   • created_date / changed_date have NOT NULL DEFAULT NOW() — omitted to use DB default.
INSERT INTO auctions.bid_data
    (id, bid_round_id, buyer_code_id, aggregated_inventory_id,
     ecoid, merged_grade, code, company_name, buyer_code_type,
     bid_amount, submitted_bid_amount, submitted_datetime,
     bid_quantity, target_price, bid_round, week_id,
     round3_bid_rank, highest_bid)
VALUES
    -- ACME-WH R1 (bid_round_id=60001, SA 6001): bid=80 vs WH target=100
    (60001, 60001, 60001, 60001,
     'ECO-A', 'Grade_A', 'ACME-WH', 'Acme Corp', 'Wholesale',
     80.00, 80.00, '2026-05-01 10:00:00+00',
     10, 100.00, 1, 601,
     NULL, false),

    -- ACME-WH R2 (bid_round_id=60006, SA 6002): bid=105 — latest bid, qualifies via pct
    (60002, 60006, 60001, 60001,
     'ECO-A', 'Grade_A', 'ACME-WH', 'Acme Corp', 'Wholesale',
     105.00, 105.00, '2026-05-02 10:00:00+00',
     10, 100.00, 2, 601,
     5, false),

    -- ACME-DW R2 (bid_round_id=60007, SA 6002): bid=88 vs DW target=90 → qualifies via pct
    (60003, 60007, 60002, 60001,
     'ECO-A', 'Grade_A', 'ACME-DW', 'Acme Corp', 'Data_Wipe',
     88.00, 88.00, '2026-05-02 10:00:00+00',
     5, 90.00, 2, 601,
     4, false),

    -- BETA-WH R2 (bid_round_id=60008, SA 6002): bid=20 vs target=50, rank=2 → qualifies via rank
    (60004, 60008, 60003, 60002,
     'ECO-B', 'Grade_A', 'BETA-WH', 'Beta Inc', 'Wholesale',
     20.00, 20.00, '2026-05-02 10:00:00+00',
     5, 50.00, 2, 601,
     2, false),

    -- GAMMA-WH R2 (bid_round_id=60009, SA 6002): bid=10 vs target=75, rank=10 → DOES NOT QUALIFY
    (60005, 60009, 60004, 60003,
     'ECO-C', 'Grade_B', 'GAMMA-WH', 'Gamma LLC', 'Wholesale',
     10.00, 10.00, '2026-05-02 10:00:00+00',
     3, 75.00, 2, 601,
     10, false),

    -- EPSI-WH R1 (bid_round_id=60005, SA 6001): bid=15 vs target=25 → DOES NOT QUALIFY
    -- Also marks Epsilon SpecialB as NOT STB-eligible (has a prior bid_round).
    (60006, 60005, 60007, 60004,
     'ECO-D', 'Grade_B', 'EPSI-WH', 'Epsilon SpecialB', 'Wholesale',
     15.00, 15.00, '2026-05-01 10:00:00+00',
     2, 25.00, 1, 601,
     12, false),

    -- Deletion target 1: ACME-WH on ECO-E, bid_amount=0 (pre-process Phase 1 deletes this)
    (60007, 60006, 60001, 60005,
     'ECO-E', 'Grade_C', 'ACME-WH', 'Acme Corp', 'Wholesale',
     0.00, NULL, NULL,
     0, 40.00, 2, 601,
     NULL, false),

    -- Deletion target 2: BETA-WH on ECO-E, bid_amount=0 (pre-process Phase 1 deletes this)
    (60008, 60008, 60003, 60005,
     'ECO-E', 'Grade_C', 'BETA-WH', 'Beta Inc', 'Wholesale',
     0.00, NULL, NULL,
     0, 40.00, 2, 601,
     NULL, false)

ON CONFLICT (id) DO NOTHING;
