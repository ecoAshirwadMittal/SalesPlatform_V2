-- P8 Lane 3B — admin Qualified Buyer Codes E2E seed.
-- Idempotent: opens the most recent R1 auction and ensures three known
-- wholesale buyer codes (HN, AAWHSL, DS2WHSL) have a qualified_buyer_codes
-- row with `qualification_type='Qualified'` and `included=true`. Re-running
-- resets the qualification_type so cascade tests don't see leaked Manual
-- state from prior runs.
DO $$
DECLARE
  v_sa_id BIGINT;
BEGIN
  SELECT sa.id
    INTO v_sa_id
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

  -- Insert/refresh QBC rows for the 3 test codes. The reset clause forces
  -- qualification_type=Qualified so a prior PATCH-to-Manual doesn't leak
  -- across test runs.
  INSERT INTO buyer_mgmt.qualified_buyer_codes
        (scheduling_auction_id, buyer_code_id, qualification_type, included, submitted)
  SELECT v_sa_id, bc.id, 'Qualified', true, false
    FROM buyer_mgmt.buyer_codes bc
   WHERE bc.code IN ('HN', 'AAWHSL', 'DS2WHSL')
  ON CONFLICT (scheduling_auction_id, buyer_code_id) DO UPDATE
       SET qualification_type = 'Qualified',
           included           = true,
           submitted          = false,
           changed_date       = now();

  RAISE NOTICE 'QBC admin seed applied: scheduling_auction_id=%', v_sa_id;
END $$;
