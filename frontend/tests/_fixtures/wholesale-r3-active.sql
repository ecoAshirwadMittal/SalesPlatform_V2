-- Idempotent E2E fixture: transition to R3 (Started) for the most recent
-- auction. Closes R1 + R2 and opens R3. QBC layout matches the R2 fixture:
--
--   • HN, AAWHSL → Qualified + Included in R3 (qualified path)
--   • DS2WHSL    → Included=false in R3 (DOWNLOAD path, BUYER_NOT_INCLUDED)
--
-- bid_rounds are created for the qualified codes only — DS2WHSL never
-- reaches the bid_round lookup because the included=false guard fires
-- first in BidderDashboardService.landingRoute.
--
-- Re-applies cleanly in `beforeEach` so submit-mutating R3 tests reset
-- between runs.
DO $$
DECLARE
  v_auction_id BIGINT;
  v_week_id BIGINT;
  v_r1_id BIGINT;
  v_r2_id BIGINT;
  v_r3_id BIGINT;
BEGIN
  SELECT a.id, a.week_id INTO v_auction_id, v_week_id
  FROM auctions.auctions a
  ORDER BY a.id DESC
  LIMIT 1;

  IF v_auction_id IS NULL THEN
    RAISE EXCEPTION 'No auction found in dev DB - cannot apply fixture';
  END IF;

  SELECT id INTO v_r1_id FROM auctions.scheduling_auctions
   WHERE auction_id = v_auction_id AND round = 1 LIMIT 1;
  SELECT id INTO v_r2_id FROM auctions.scheduling_auctions
   WHERE auction_id = v_auction_id AND round = 2 LIMIT 1;
  SELECT id INTO v_r3_id FROM auctions.scheduling_auctions
   WHERE auction_id = v_auction_id AND round = 3 LIMIT 1;

  IF v_r3_id IS NULL THEN
    RAISE EXCEPTION 'No R3 scheduling_auction found for auction_id=% - schema seed missing', v_auction_id;
  END IF;

  -- Close R1 + R2 if open.
  IF v_r1_id IS NOT NULL THEN
    UPDATE auctions.scheduling_auctions
       SET round_status = 'Closed', changed_date = now()
     WHERE id = v_r1_id;
  END IF;
  IF v_r2_id IS NOT NULL THEN
    UPDATE auctions.scheduling_auctions
       SET round_status = 'Closed', changed_date = now()
     WHERE id = v_r2_id;
  END IF;

  -- Open R3 with a future end_datetime.
  UPDATE auctions.scheduling_auctions
     SET round_status   = 'Started',
         start_datetime = now() - interval '1 hour',
         end_datetime   = now() + interval '24 hours',
         changed_date   = now()
   WHERE id = v_r3_id;

  -- Qualified codes — Included=true.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  SELECT v_r3_id, bc.id, 'Qualified', true, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL')
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Qualified',
           included           = true,
           submitted          = false;

  -- Unqualified code — Included=false; landingRoute returns DOWNLOAD.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  SELECT v_r3_id, bc.id, 'Not_Qualified', false, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('DS2WHSL')
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Not_Qualified',
           included           = false,
           submitted          = false;

  -- Ensure bid_round for the qualified codes (idempotent insert; no DELETE).
  INSERT INTO auctions.bid_rounds (scheduling_auction_id, buyer_code_id, week_id, submitted)
  SELECT v_r3_id, bc.id, v_week_id, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL')
     AND NOT EXISTS (
           SELECT 1 FROM auctions.bid_rounds br
            WHERE br.scheduling_auction_id = v_r3_id
              AND br.buyer_code_id         = bc.id
         );

  -- Reset state so the test sees a fresh round each time.
  UPDATE auctions.bid_rounds
     SET submitted          = false,
         submitted_datetime = NULL,
         changed_date       = now()
   WHERE scheduling_auction_id = v_r3_id;

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
           SELECT id FROM auctions.bid_rounds
            WHERE scheduling_auction_id = v_r3_id
         );

  RAISE NOTICE 'Wholesale R3 seed applied: r3_id=%, r2_id=%, r1_id=%, week_id=%',
               v_r3_id, v_r2_id, v_r1_id, v_week_id;
END $$;
