-- Idempotent E2E fixture: open the most recent Round 1 auction so the
-- bidder dashboard renders in GRID mode for the wholesale buyer codes
-- associated to bidder@buyerco.com (HN, AAWHSL, DS2WHSL).
--
-- Targets dev DB (`salesplatform_dev`). Re-applies cleanly on every run:
--   • UPDATE: set R1.round_status=Started, end_datetime=now()+24h
--   • UPSERT: ensure QBC for each code is Qualified+Included, submitted=false
--   • INSERT (if missing): bid_rounds for each code; UPDATE submitted=false
--   • UPDATE: reset bid_amount/bid_quantity/submitted_* on existing
--     bid_data rows. Avoids DELETE+CASCADE so re-application is fast
--     (~30ms vs ~5s for full row regeneration).
--
-- After running, the bidder dashboard for HN should land on GRID. On the
-- first run for a fresh DB, BidDataCreationService lazily generates
-- bid_data rows on the next dashboard fetch; subsequent runs reuse those
-- rows with reset values.
DO $$
DECLARE
  v_sa_id BIGINT;
  v_week_id BIGINT;
BEGIN
  -- The R1 of the most recent auction (highest auction_id wins).
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

  UPDATE auctions.scheduling_auctions
     SET round_status   = 'Started',
         start_datetime = now() - interval '1 hour',
         end_datetime   = now() + interval '24 hours',
         changed_date   = now()
   WHERE id = v_sa_id;

  -- Ensure each wholesale buyer code is qualified for this round.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  SELECT v_sa_id, bc.id, 'Qualified', true, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL', 'DS2WHSL')
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Qualified',
           included           = true,
           submitted          = false;

  -- Ensure a bid_round exists per code (insert only if missing — keeps
  -- previously generated bid_data intact for fast re-application).
  INSERT INTO auctions.bid_rounds (scheduling_auction_id, buyer_code_id, week_id, submitted)
  SELECT v_sa_id, bc.id, v_week_id, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL', 'DS2WHSL')
     AND NOT EXISTS (
           SELECT 1 FROM auctions.bid_rounds br
            WHERE br.scheduling_auction_id = v_sa_id
              AND br.buyer_code_id         = bc.id
         );

  -- Reset bid_round + bid_data state without dropping rows. This makes
  -- the fixture safe for `beforeEach` between mutating tests.
  UPDATE auctions.bid_rounds
     SET submitted          = false,
         submitted_datetime = NULL,
         changed_date       = now()
   WHERE scheduling_auction_id = v_sa_id
     AND buyer_code_id IN (
           SELECT id FROM buyer_mgmt.buyer_codes
            WHERE code IN ('HN', 'AAWHSL', 'DS2WHSL')
         );

  UPDATE auctions.bid_data
     SET bid_amount             = 0,
         bid_quantity           = NULL,
         submitted_bid_amount   = NULL,
         submitted_bid_quantity = NULL,
         last_valid_bid_amount  = NULL,
         last_valid_bid_quantity = NULL,
         submitted_datetime     = NULL,
         changed_date           = now()
   WHERE bid_round_id IN (
           SELECT br.id
             FROM auctions.bid_rounds br
            WHERE br.scheduling_auction_id = v_sa_id
              AND br.buyer_code_id IN (
                    SELECT id FROM buyer_mgmt.buyer_codes
                     WHERE code IN ('HN', 'AAWHSL', 'DS2WHSL')
                  )
         );

  RAISE NOTICE 'Wholesale R1 seed applied: scheduling_auction_id=%, week_id=%', v_sa_id, v_week_id;
END $$;
