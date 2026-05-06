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
