-- =============================================================================
-- V94: Reserve Bid (EB) parity fixes
-- Source findings: docs/tasks/parity/findings.md — RBL-D1, RBL-D2, RBL-P1
-- Impl notes:      docs/tasks/parity/impl/reserve-bids-data-fixes.md
-- =============================================================================
--
-- RBL-D2 (this migration) — product_id: VARCHAR(100) -> BIGINT
--   Legacy ecoatm_eb$reservebid.productid is INTEGER. The V76 modern schema
--   stored it as VARCHAR(100) "to match auctions.bid_data.ecoid", which made the
--   admin grid sort/filter lexicographic ('1','10206','10211',…,'73') and broke
--   numeric comparators (73 < 100). Every value in the seeded table is numeric
--   (verified: 0 rows fail '^[0-9]+$'; range 1..30002466, max 8 digits — well
--   within BIGINT), so the cast is total and lossless.
--
--   The device-identity JOIN key across the rest of the auctions domain stays
--   VARCHAR (bid_data.ecoid, aggregated_inventory.ecoid2, po_detail.product_id).
--   reserve_bid.product_id is projected AS ecoid into the 4C recalc CTEs; those
--   4 projection sites now cast product_id::text (BidRankingRepository x2,
--   TargetPriceRecalcRepository x2) so the UNION / USING joins stay string-typed
--   and byte-for-byte unchanged.
--
--   The ALTER automatically rebuilds the two dependent indexes
--   (idx_rb_product_grade, and the unique index backing
--   uq_reserve_bid_product_grade) — no manual DROP/CREATE needed. No view or
--   generated column depends on the column (verified via pg_depend).
--
-- RBL-P1 (no DDL) — default grid order is fixed in the query layer, not here:
--   ReserveBidRepositoryImpl now defaults an unsorted list to
--   `ORDER BY legacy_id ASC NULLS LAST, id ASC` (Mendix object/insert-order
--   proxy; first screen = product 73,76,78,79,496 as in legacy). The frontend
--   stopped forcing a product_id sort.
--
-- RBL-D1 (no DDL — catalogued expected delta):
--   New count 14,657 vs legacy 14,659 == exactly the 2 duplicate (productid,
--   grade) business-key pairs the UNIQUE(product_id, grade) constraint
--   legitimately collapses:
--     • product 13038 / F_NYN/H_NNN — legacy ids ...864305 & ...864786, both
--       bid = 25.01. Survivor kept: ...864786 (later created_date).
--     • product 16456 / E_YYN       — legacy ids ...504291 & ...504528, both
--       bid = 9.78. Survivor kept: ...504291 (later last_update_datetime).
--   In BOTH pairs the two rows carry an IDENTICAL bid, and the surviving row is
--   the most-recent one, so no price information is lost and the survivor value
--   is already correct. No data correction is warranted — do NOT re-insert the
--   dropped twins (they would violate the unique business key). See the impl doc
--   for the full evidence.
-- =============================================================================

ALTER TABLE auctions.reserve_bid
    ALTER COLUMN product_id TYPE BIGINT USING product_id::bigint;

COMMENT ON COLUMN auctions.reserve_bid.product_id IS
    'Numeric device product id (BIGINT, RBL-D2/V94). Legacy '
    'ecoatm_eb$reservebid.productid was INTEGER. Projected ::text when joined to '
    'the VARCHAR ecoid domain (bid_data.ecoid) in the 4C recalc CTEs.';
