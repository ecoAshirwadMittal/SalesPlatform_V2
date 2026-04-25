-- P8 Lane 3A — admin Bid Data E2E seed.
-- Idempotent: opens the most recent R1 auction, ensures the test buyer code
-- (HN) has a bid_round + a known set of bid_data rows the spec asserts on.
-- Re-applies cleanly via UPDATE-only on the bid_data table where rows already
-- exist; only INSERTs missing rows.
--
-- After running, an admin sees:
--   • ≥ 2 bid_data rows for the chosen (round, HN) pair
--   • exactly 1 of those rows has submitted_bid_amount > 0 (so the
--     "submitted only" filter narrows from N to 1)
--   • is_deprecated = false on every row (so DELETE tests start clean)
DO $$
DECLARE
  v_sa_id BIGINT;
  v_week_id BIGINT;
  v_buyer_code_id BIGINT;
  v_bid_round_id BIGINT;
  v_count INTEGER;
BEGIN
  SELECT sa.id, a.week_id
    INTO v_sa_id, v_week_id
  FROM auctions.scheduling_auctions sa
  JOIN auctions.auctions a ON a.id = sa.auction_id
  WHERE sa.round = 1
  ORDER BY a.id DESC
  LIMIT 1;

  IF v_sa_id IS NULL THEN
    RAISE EXCEPTION 'No R1 scheduling_auction found in dev DB - cannot apply fixture';
  END IF;

  SELECT id INTO v_buyer_code_id FROM buyer_mgmt.buyer_codes WHERE code = 'HN' LIMIT 1;
  IF v_buyer_code_id IS NULL THEN
    RAISE EXCEPTION 'Buyer code HN not found - V18 data seed missing';
  END IF;

  -- Open R1.
  UPDATE auctions.scheduling_auctions
     SET round_status   = 'Started',
         start_datetime = now() - interval '1 hour',
         end_datetime   = now() + interval '24 hours',
         changed_date   = now()
   WHERE id = v_sa_id;

  -- Ensure HN is qualified+included for this round.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  VALUES (v_sa_id, v_buyer_code_id, 'Qualified', true, false)
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Qualified',
           included           = true,
           submitted          = false;

  -- Ensure HN has a bid_round; reuse existing if present.
  SELECT id INTO v_bid_round_id
    FROM auctions.bid_rounds
   WHERE scheduling_auction_id = v_sa_id
     AND buyer_code_id = v_buyer_code_id
   LIMIT 1;

  IF v_bid_round_id IS NULL THEN
    INSERT INTO auctions.bid_rounds (scheduling_auction_id, buyer_code_id, week_id, submitted)
    VALUES (v_sa_id, v_buyer_code_id, v_week_id, false)
    RETURNING id INTO v_bid_round_id;
  ELSE
    UPDATE auctions.bid_rounds
       SET submitted = false, submitted_datetime = NULL, changed_date = now()
     WHERE id = v_bid_round_id;
  END IF;

  -- Reset is_deprecated on all existing bid_data rows for this (round, code)
  -- so DELETE tests always start with a clean ledger.
  UPDATE auctions.bid_data
     SET is_deprecated = false,
         changed_date  = now()
   WHERE bid_round_id = v_bid_round_id
     AND buyer_code_id = v_buyer_code_id;

  -- Ensure at least 2 bid_data rows exist (insert minimal placeholders if
  -- the table is empty for this round). We use synthetic ecoids so we don't
  -- collide with real device data.
  SELECT count(*) INTO v_count
    FROM auctions.bid_data
   WHERE bid_round_id = v_bid_round_id
     AND buyer_code_id = v_buyer_code_id;

  IF v_count < 2 THEN
    INSERT INTO auctions.bid_data
        (bid_round_id, buyer_code_id, ecoid, merged_grade, buyer_code_type,
         bid_quantity, bid_amount, target_price, maximum_quantity, payout,
         submitted_bid_amount, submitted_bid_quantity)
    VALUES
        (v_bid_round_id, v_buyer_code_id, 'P8-ADMIN-1', 'A', 'Wholesale',
         null, 0, null, 100, null, null, null),
        (v_bid_round_id, v_buyer_code_id, 'P8-ADMIN-2', 'B', 'Wholesale',
         null, 0, null, 100, null, null, null);
  END IF;

  -- Reset all rows to "no submitted bid", then promote exactly one to a
  -- submitted state so the "Submitted only" filter test asserts exactly 1
  -- match. Pick the smallest id deterministically.
  UPDATE auctions.bid_data
     SET submitted_bid_amount   = NULL,
         submitted_bid_quantity = NULL,
         submitted_datetime     = NULL,
         bid_amount             = 0,
         bid_quantity           = NULL
   WHERE bid_round_id = v_bid_round_id
     AND buyer_code_id = v_buyer_code_id;

  UPDATE auctions.bid_data
     SET submitted_bid_amount   = 25.00,
         submitted_bid_quantity = 1,
         submitted_datetime     = now() - interval '1 hour',
         bid_amount             = 25.00,
         bid_quantity           = 1
   WHERE id = (
           SELECT id FROM auctions.bid_data
            WHERE bid_round_id = v_bid_round_id
              AND buyer_code_id = v_buyer_code_id
            ORDER BY id ASC LIMIT 1
         );

  RAISE NOTICE 'Bid data admin seed applied: scheduling_auction_id=%, bid_round_id=%, buyer_code_id=%',
               v_sa_id, v_bid_round_id, v_buyer_code_id;
END $$;
