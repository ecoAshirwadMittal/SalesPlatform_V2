-- ============================================================================
-- V86: Seed default rows in auctions.bid_round_selection_filters
-- ----------------------------------------------------------------------------
-- Purpose:
--   Mendix lazily creates a BidRoundSelectionFilter row the first time an
--   admin opens the Round 2 or Round 3 Selection Criteria page (see
--   migration_context/backend/ACT_Open_Round_2_Selection_Criteria.md and
--   ACT_Open_Round_3_Selection_Criteria.md). On a fresh local DB this row
--   never exists, which causes:
--     (a) /admin/auctions-data-center/auctions/round-filters/{2,3} to 500
--         with "BidRoundSelectionFilter not found"
--     (b) R2 buyer assignment (R2BuyerAssignmentService.findByRound(2)) to
--         fail at first R2 init
--     (c) R3 pre-process (R3PreProcessService.findByRound(3)) to fail at
--         first R2 close
--
--   Pre-creating one row per round with column defaults matches Mendix
--   behavior: only `round` is set explicitly; every other column relies on
--   the V59/V83/V84 column DEFAULTs (regular_buyer_qualification =
--   'Only_Qualified', regular_buyer_inventory_options =
--   'InventoryRound1QualifiedBids', stb_*_override = false, the three R3
--   knobs NULL → all-buyers fall-through). Admins still use the criteria
--   page to tune target_percent / target_value / R3 knobs per business need.
-- ----------------------------------------------------------------------------
-- Idempotent: WHERE NOT EXISTS guard keeps reruns safe.
-- ============================================================================

INSERT INTO auctions.bid_round_selection_filters (round)
SELECT 2
WHERE NOT EXISTS (
    SELECT 1 FROM auctions.bid_round_selection_filters WHERE round = 2
);

INSERT INTO auctions.bid_round_selection_filters (round)
SELECT 3
WHERE NOT EXISTS (
    SELECT 1 FROM auctions.bid_round_selection_filters WHERE round = 3
);
