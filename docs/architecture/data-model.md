# Data Model Overview

Key tables and entities across the SalesPlatform database.

---

## auctions.reserve_bid / reserve_bid_audit / reserve_bid_sync
ExchangeBid reserve floor prices, price-change audit trail, and Snowflake sync watermark singleton. See `docs/tasks/auction-eb-module-design.md` for schema.

---

## auctions.purchase_order / po_detail
PO header + line items. `purchase_order.week_from_id` / `week_to_id`
reference `mdm.week`. `po_detail.buyer_code_id` references
`buyer_mgmt.buyer_codes`. Lifecycle is derived (`DRAFT`/`ACTIVE`/`CLOSED`)
from `today` vs. week range — never stored. See
`docs/tasks/auction-po-module-design.md` §5 for full schema.

---

## auctions.scheduling_auctions (4C status fields)
4C adds 8 status columns: `ranking_status` / `target_price_status` (`PENDING`/`RUNNING`/`SUCCESS`/`FAILED`), each with `_error` (TEXT), `_started_at`, `_finished_at` (TIMESTAMPTZ).

## auctions.scheduling_auctions (5 R2-init status fields)
Sub-project 5 (V83) adds 4 columns for R2 buyer-assignment lifecycle:
`r2_init_status` (`PENDING`/`RUNNING`/`SUCCESS`/`FAILED`/`SKIPPED`),
`r2_init_error` (TEXT), `r2_init_started_at`, `r2_init_finished_at`
(TIMESTAMPTZ). `SKIPPED` is the terminal state when
`auctions_feature_config.calculate_round2_buyer_participation = FALSE`
(no QBC or bid_data rows written). Default `'PENDING'` for existing rows.

## auctions.bid_ranking_config (4C)
4C adds `include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE` — toggles whether `auctions.reserve_bid` rows participate in DENSE_RANK.

## auctions.scheduling_auctions (R3-lifecycle status fields, V84)

Sub-project 6 (V84) adds eight columns for the R3 lifecycle:
- `r3_preprocess_status` (5-state: `PENDING`/`RUNNING`/`SUCCESS`/`FAILED`/`SKIPPED`)
- `r3_preprocess_error` (TEXT)
- `r3_preprocess_started_at`, `r3_preprocess_finished_at` (TIMESTAMPTZ)
- `r3_init_status` (4-state: `PENDING`/`RUNNING`/`SUCCESS`/`FAILED` — no `SKIPPED`)
- `r3_init_error` (TEXT)
- `r3_init_started_at`, `r3_init_finished_at` (TIMESTAMPTZ)

Both column groups live on the R3 SA row. `SKIPPED` for pre-process means `has_round = false` on the R3 SA — no QBCs or reports are written. Init has no `SKIPPED` state because it is gated by the predecessor guard (refuses unless `r3_preprocess_status = SUCCESS`).

## auctions.bid_round_selection_filters (3 R3 columns + R2 re-alignment, V84)

V84 adds three nullable R3-qualification knobs (whole-percent convention):
- `bid_percentage_variation NUMERIC(10, 4)` — 5 = 5%; branch active when NOT NULL
- `bid_amount_variation NUMERIC(14, 2)` — flat amount; branch active when NOT NULL
- `rank_qualification_limit INTEGER` — rank ceiling; branch active when NOT NULL

All three NULL → all-buyers fall-through qualify (default Mendix behavior).

V84 also normalises `target_percent` to whole-percent convention (was stored as decimal 0.05 in V59/sub-project 5; sub-project 6 updates the R2 CTE formula to divide by 100 and updates test fixtures). No data migration needed — no production BRSF rows had been loaded.

## auctions.round3_buyer_data_reports (V85, sub-project 6)

V85 adds two columns to the V62 base table:
- `scheduling_auction_id BIGINT NOT NULL REFERENCES auctions.scheduling_auctions(id) ON DELETE CASCADE`
- `buyer_codes VARCHAR(1000)` — comma-joined codes per company

V62 created the table for the legacy Mendix report; V85 wires it to the R3 SA and adds the codes column. Populated by `R3PreProcessService` Phase 5 (`Round3BuyerDataReportRepository.bulkInsertForSchedulingAuction`).

## buyer_mgmt.qualified_buyer_codes (V72-flattened)
Migration **V72** dropped the Mendix M:M junctions
`buyer_mgmt.qbc_buyer_codes` + `buyer_mgmt.qbc_scheduling_auctions` and
added direct FK columns `scheduling_auction_id` + `buyer_code_id`
directly on `qualified_buyer_codes`, plus
`UNIQUE(scheduling_auction_id, buyer_code_id)`. Each row now encodes one
(SA, BuyerCode) pair with `qualification_type` (`'Qualified'` /
`'Not_Qualified'`), `included` (boolean gate for bid_data generation),
and `is_special_treatment` (boolean — STB override). Sub-project 5
writes this table in two phases: the qualification + special-treatment
CTEs compute the qualified-code id sets in Java, then a single bulk
INSERT writes one row per active WH/DW code with derived
`qualification_type` and `is_special_treatment` columns. The legacy
junctions no longer exist.
