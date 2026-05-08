-- ============================================================================
-- V87: Tighten V86 default thresholds on bid_round_selection_filters
-- ----------------------------------------------------------------------------
-- Purpose:
--   V86 seeded one row per round (2, 3) with NULL `target_percent` and
--   NULL `target_value`. The R2 / R3 qualification CTEs (used to decide which
--   buyer codes are `Qualified` vs `Not_Qualified` after R1 close) treat
--   NULL thresholds as "no buyer ever qualifies" — every `qualified_buyer_codes`
--   row ends up with `included = FALSE`. Downstream:
--     • R2 buyer assignment Phase 4.5 (added in C24 fix) finds 0 included
--       QBCs and seeds 0 bid_rounds.
--     • Bidder dashboard returns BIDROUND_MISSING and renders the empty
--       "Bidding has ended" state for every regular buyer.
--
--   These defaults match the test fixture
--   (src/test/resources/fixtures/auctions/r2-init-seed.sql) which uses
--   target_percent=5 (5%) and target_value=1.00 — the values the R2/R3
--   qualification ITs validate against. They mirror Mendix-spec defaults
--   per migration_context/backend/ACT_Round2AggregatedInventory.md.
--
--   UPDATE-only — V86 rows already exist; we only adjust their threshold
--   columns. A fresh DB applies V86 then immediately V87, ending in the
--   same state.
-- ----------------------------------------------------------------------------

UPDATE auctions.bid_round_selection_filters
   SET target_percent = 5,
       target_value   = 1.00
 WHERE round IN (2, 3)
   AND target_percent IS NULL
   AND target_value   IS NULL;
