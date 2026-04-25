-- Idempotent E2E fixture for the Lane 4 R2 Selection Criteria admin page.
--
-- Wipes any existing row for round 2 in auctions.bid_round_selection_filters
-- so the test can start from the "no row exists → defaults shown" state. The
-- service layer GET endpoint returns 404 in that state; the page renders
-- defaults locally. Subsequent PUTs upsert the row, and re-applying the
-- fixture between tests resets the slate.
--
-- Round 3 is left untouched: other lanes (or the broader Phase D form) may
-- depend on it, and Lane 4's page only edits round 2.
--
-- Targets dev DB (`salesplatform_dev`). Re-applies cleanly on every run.

DELETE FROM auctions.bid_round_selection_filters
 WHERE round = 2;
