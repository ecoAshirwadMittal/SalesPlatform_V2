-- Fixture for 4C recalc tests. All IDs use the 999000+ range to avoid
-- colliding with seeded production-style data in dev DB.
--
-- Seeds:
--   • 1 mdm.week (id 999001, week_number 14, year 2026)
--   • 1 auctions.auctions (id 999001) tied to that week
--   • 3 auctions.scheduling_auctions (ids 999001, 999002, 999003) — one per round
--   • 1 bid_round per scheduling_auction (ids 999001/999002/999003)
--   • bid_ranking_config singleton — UPDATE existing V63 row (id 1) for deterministic thresholds
--   • 6 aggregated_inventory rows for 3 ecoid x 2 grade combos
--   • 12 bid_data rows in round 1 across 4 buyer codes (real IDs 1-4 from V18 seed:
--     DW01, SCWC, SCPO, ACCPPO) — covering rank-eligible + below-min-bid + tie-on-max-bid
--   • 2 reserve_bid rows (one matches an ecoid; one stand-alone)
--   • 3 target_price_factors bands + filters for both round 2 and round 3
--   • 1 active purchase_order + 2 po_detail rows (covering week 999001)
--   • 1 inactive purchase_order outside the week range (control)

INSERT INTO mdm.week (id, week_id, year, week_number, week_start_datetime, week_end_datetime, week_display, week_display_short, week_number_string)
VALUES (999001, 999914, 2026, 14, '2026-04-06 00:00:00+00', '2026-04-12 23:59:59+00', '2026 / Wk14', 'Wk14', '14');

INSERT INTO mdm.week (id, week_id, year, week_number, week_start_datetime, week_end_datetime, week_display, week_display_short, week_number_string)
VALUES (999002, 999912, 2026, 12, '2026-03-23 00:00:00+00', '2026-03-29 23:59:59+00', '2026 / Wk12', 'Wk12', '12');

INSERT INTO auctions.auctions (id, auction_title, auction_status, week_id)
VALUES (999001, '4C Test Auction', 'Started', 999001);

INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, ranking_status, target_price_status)
VALUES
  (999001, 999001, 1, 'Closed',      'PENDING', 'PENDING'),
  (999002, 999001, 2, 'Started',     'PENDING', 'PENDING'),
  (999003, 999001, 3, 'Unscheduled', 'PENDING', 'PENDING');

-- Create bid_rounds records (one per buyer_code per round, used to track submission state)
INSERT INTO auctions.bid_rounds (id, scheduling_auction_id, buyer_code_id, week_id)
VALUES
  (999001, 999001, 1, 999001),
  (999002, 999001, 2, 999001),
  (999003, 999001, 3, 999001),
  (999004, 999002, 1, 999001),
  (999005, 999003, 1, 999001);

-- bid_ranking_config singleton expected to exist from V63 seed; tighten thresholds for deterministic tests.
UPDATE auctions.bid_ranking_config
   SET minimum_bid = 100.00,
       display_rank = 1,
       maximum_rank = 5,
       include_reserve_floor = TRUE
 WHERE id = 1;

-- 6 aggregated_inventory rows
INSERT INTO auctions.aggregated_inventory (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity)
VALUES
  (999001, 'ECO-A', 999001, 'A', 100, 50),
  (999002, 'ECO-A', 999001, 'B', 100, 50),
  (999003, 'ECO-B', 999001, 'A', 100, 50),
  (999004, 'ECO-B', 999001, 'B', 100, 50),
  (999005, 'ECO-C', 999001, 'A', 100, 50),
  (999006, 'ECO-C', 999001, 'B', 100, 50);

-- 12 bid_data rows in round 1. Real buyer codes from V18 seed:
--   id=1 → 'DW01'
--   id=2 → 'SCWC'
--   id=3 → 'SCPO'
--   id=4 → 'ACCPPO'
-- STRING_AGG with ORDER BY produces alphabetical output.
INSERT INTO auctions.bid_data
  (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name, buyer_code_type,
   submitted_bid_amount, bid_round, week_id)
VALUES
  -- (ECO-A, A): tie at 500 between DW01 and SCWC, 300 from SCPO, below-min 50 from ACCPPO
  (999001, 999001, 1, 'ECO-A', 'A', 'DW01',   'TestCo1', 'Wholesale', 500.00, 1, 999001),
  (999002, 999001, 2, 'ECO-A', 'A', 'SCWC',   'TestCo2', 'Wholesale', 500.00, 1, 999001),
  (999003, 999002, 3, 'ECO-A', 'A', 'SCPO',   'TestCo3', 'Wholesale', 300.00, 1, 999001),
  (999004, 999003, 4, 'ECO-A', 'A', 'ACCPPO', 'TestCo4', 'Wholesale',  50.00, 1, 999001),
  -- (ECO-A, B): single bidder
  (999005, 999001, 1, 'ECO-A', 'B', 'DW01',   'TestCo1', 'Wholesale', 200.00, 1, 999001),
  -- (ECO-B, A): three rank tiers
  (999006, 999001, 1, 'ECO-B', 'A', 'DW01',   'TestCo1', 'Wholesale', 800.00, 1, 999001),
  (999007, 999002, 2, 'ECO-B', 'A', 'SCWC',   'TestCo2', 'Wholesale', 600.00, 1, 999001),
  (999008, 999003, 3, 'ECO-B', 'A', 'SCPO',   'TestCo3', 'Wholesale', 400.00, 1, 999001),
  -- (ECO-B, B): empty
  -- (ECO-C, A): single bidder, low
  (999009, 999001, 1, 'ECO-C', 'A', 'DW01',   'TestCo1', 'Wholesale', 150.00, 1, 999001),
  -- (ECO-C, B): three bidders, all > min
  (999010, 999001, 1, 'ECO-C', 'B', 'DW01',   'TestCo1', 'Wholesale', 250.00, 1, 999001),
  (999011, 999002, 2, 'ECO-C', 'B', 'SCWC',   'TestCo2', 'Wholesale', 200.00, 1, 999001),
  (999012, 999003, 3, 'ECO-C', 'B', 'SCPO',   'TestCo3', 'Wholesale', 175.00, 1, 999001);

-- reserve_bid rows
INSERT INTO auctions.reserve_bid (id, product_id, grade, brand, model, bid)
VALUES
  (999001, 'ECO-A', 'A', 'BrandX', 'ModelX', 700.0000),
  (999002, 'ECO-D', 'A', 'BrandY', 'ModelY', 999.0000);

-- target_price_factors — three bands matching round 2 and round 3 filters
-- low (0-200) +10%, mid (200-1000) +5 flat, high (1000+) +2%
INSERT INTO auctions.target_price_factors (id, minimum_value, maximum_value, factor_type, factor_amount)
VALUES
  (999001,    0.00,     200.00, 'Percentage_Factor', 10.0000),
  (999002,  200.00,    1000.00, 'Flat_Amount',         5.0000),
  (999003, 1000.00, 9999999.00, 'Percentage_Factor', 2.0000);

-- bid_round_selection_filters — one each for round 2 and round 3
INSERT INTO auctions.bid_round_selection_filters (id, round)
VALUES
  (999002, 2),
  (999003, 3);

-- Apply the three bands to both round 2 and round 3 filters
INSERT INTO auctions.target_price_factor_filters (target_price_factor_id, bid_round_selection_filter_id)
VALUES
  (999001, 999002), (999002, 999002), (999003, 999002),
  (999001, 999003), (999002, 999003), (999003, 999003);

-- Active purchase_order — covers week 999001
INSERT INTO auctions.purchase_order (id, week_from_id, week_to_id, week_range_label, valid_year_week, total_records)
VALUES (999001, 999001, 999001, 'Wk14 2026', TRUE, 2);

INSERT INTO auctions.po_detail (id, purchase_order_id, buyer_code_id, product_id, grade, model_name, price, qty_cap)
VALUES
  -- ECO-A grade A: PO floor 750 → wins over MaxBid+factor (505) AND reserve_bid (700)
  (999001, 999001, 1, 'ECO-A', 'A', 'ModelX', 750.0000, 50),
  -- ECO-B grade A: PO floor 100 (below; MaxBid+factor 805 wins)
  (999002, 999001, 1, 'ECO-B', 'A', 'ModelY', 100.0000, 25);

-- Inactive purchase_order — outside week range; CTE must ignore it
INSERT INTO auctions.purchase_order (id, week_from_id, week_to_id, week_range_label, valid_year_week, total_records)
VALUES (999002, 999002, 999002, 'Wk12 2026 (inactive)', TRUE, 1);

INSERT INTO auctions.po_detail (id, purchase_order_id, buyer_code_id, product_id, grade, model_name, price, qty_cap)
VALUES (999003, 999002, 1, 'ECO-A', 'A', 'ModelX', 9999.0000, 1);
