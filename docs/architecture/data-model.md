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

## auctions.bid_ranking_config (4C)
4C adds `include_reserve_floor BOOLEAN NOT NULL DEFAULT TRUE` — toggles whether `auctions.reserve_bid` rows participate in DENSE_RANK.
