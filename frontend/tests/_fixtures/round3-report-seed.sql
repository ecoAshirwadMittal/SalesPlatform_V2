-- Idempotent E2E fixture for the R3 Bid Report by Buyer admin view.
--
-- Targets dev DB (`salesplatform_dev`). Re-applies cleanly on every run:
--   • Picks the most recent auction (highest auction_id wins) so the
--     resulting report row is reachable from the admin's "current week"
--     dropdown selection.
--   • UPSERTs a fixed marker row keyed by buyer_code = 'AA600WHL_R3FX'
--     and that auction_id. Marker code keeps the fixture out of the way
--     of any production-shape data the dev DB may already hold.
--   • Updates the marker's totals every run so beforeEach state is
--     deterministic.
--
-- After running, navigating to /admin/auctions-data-center/round3-bid-report,
-- selecting the auction's week, and asserting the marker buyer_code is
-- present in the grid is a deterministic round-trip.
DO $$
DECLARE
  v_auction_id BIGINT;
  v_week_id    BIGINT;
BEGIN
  SELECT a.id, a.week_id
    INTO v_auction_id, v_week_id
    FROM auctions.auctions a
   ORDER BY a.id DESC
   LIMIT 1;

  IF v_auction_id IS NULL THEN
    RAISE EXCEPTION 'No auction rows found in dev DB - cannot apply R3 report fixture';
  END IF;

  -- Mendix R3 report row — marker buyer_code keeps fixture data isolated.
  INSERT INTO auctions.round3_buyer_data_reports
        (auction_id, buyer_code, company_name, total_quantity, total_payout,
         submitted_datetime, created_date, changed_date)
  SELECT v_auction_id, 'AA600WHL_R3FX', 'Round 3 Fixture Buyer', 250,
         3750.00, now(), now(), now()
  WHERE NOT EXISTS (
    SELECT 1 FROM auctions.round3_buyer_data_reports
     WHERE buyer_code = 'AA600WHL_R3FX' AND auction_id = v_auction_id
  );

  UPDATE auctions.round3_buyer_data_reports
     SET total_quantity     = 250,
         total_payout       = 3750.00,
         submitted_datetime = now(),
         changed_date       = now()
   WHERE buyer_code = 'AA600WHL_R3FX'
     AND auction_id = v_auction_id;

  RAISE NOTICE 'R3 report fixture applied: auction_id=%, week_id=%, marker=AA600WHL_R3FX',
    v_auction_id, v_week_id;
END $$;
