-- Idempotent E2E fixture: transition from R1 (Closed) → R2 (Started) for
-- the most recent auction. Sets up two QBC states for the wholesale
-- buyer codes associated to bidder@buyerco.com:
--
--   • HN, AAWHSL → Qualified + Included in R2 (qualified path)
--   • DS2WHSL    → Included=false in R2 (DOWNLOAD path; landing returns
--                  BUYER_NOT_INCLUDED)
--
-- bid_rounds are created for the qualified codes only — DS2WHSL never
-- reaches the bid_round lookup because the included=false guard fires
-- first in BidderDashboardService.landingRoute.
--
-- Re-application is idempotent and fast (UPDATE-only on existing rows
-- where possible). Intended for `beforeEach` of the wholesale-r2 spec.
DO $$
DECLARE
  v_auction_id BIGINT;
  v_week_id BIGINT;
  v_r1_id BIGINT;
  v_r2_id BIGINT;
BEGIN
  -- Most recent auction (highest id wins)
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

  IF v_r2_id IS NULL THEN
    RAISE EXCEPTION 'No R2 scheduling_auction found for auction_id=% - schema seed missing', v_auction_id;
  END IF;

  -- Close R1 if still open (no-op if already closed).
  IF v_r1_id IS NOT NULL THEN
    UPDATE auctions.scheduling_auctions
       SET round_status = 'Closed', changed_date = now()
     WHERE id = v_r1_id;
  END IF;

  -- Open R2 with a future end_datetime.
  UPDATE auctions.scheduling_auctions
     SET round_status   = 'Started',
         start_datetime = now() - interval '1 hour',
         end_datetime   = now() + interval '24 hours',
         changed_date   = now()
   WHERE id = v_r2_id;

  -- Qualified codes — Included=true.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  SELECT v_r2_id, bc.id, 'Qualified', true, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL')
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Qualified',
           included           = true,
           submitted          = false;

  -- Unqualified code — Included=false; landingRoute returns DOWNLOAD.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  SELECT v_r2_id, bc.id, 'Not_Qualified', false, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('DS2WHSL')
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Not_Qualified',
           included           = false,
           submitted          = false;

  -- Ensure bid_round for the qualified codes (no DELETE — idempotent insert).
  INSERT INTO auctions.bid_rounds (scheduling_auction_id, buyer_code_id, week_id, submitted)
  SELECT v_r2_id, bc.id, v_week_id, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL')
     AND NOT EXISTS (
           SELECT 1 FROM auctions.bid_rounds br
            WHERE br.scheduling_auction_id = v_r2_id
              AND br.buyer_code_id         = bc.id
         );

  -- Reset state so the test sees a fresh round each time.
  UPDATE auctions.bid_rounds
     SET submitted          = false,
         submitted_datetime = NULL,
         changed_date       = now()
   WHERE scheduling_auction_id = v_r2_id;

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
            WHERE scheduling_auction_id = v_r2_id
         );

  RAISE NOTICE 'Wholesale R2 seed applied: r2_id=%, r1_id=%, week_id=%',
               v_r2_id, v_r1_id, v_week_id;
END $$;
