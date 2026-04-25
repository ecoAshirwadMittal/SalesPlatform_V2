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
