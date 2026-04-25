# Application Modules

Inventory of major modules and their primary entities.

---

## Exchange Bid (EB)
- Source module: `ecoatm_eb`
- Primary tables: `auctions.reserve_bid`, `auctions.reserve_bid_audit`, `auctions.reserve_bid_sync`
- Purpose: per-(product_id, grade) reserve floor prices consumed by sub-project 4C target-price recalc
- Admin surface: `/admin/auctions-data-center/reserve-bids/**`

## Purchase Order (PO)
- Source module: `ecoatm_po`
- Primary tables: `auctions.purchase_order`, `auctions.po_detail`
- Purpose: weekly PO commitments authored via Excel upload, consumed by
  sub-project 4C target-price recalc as `GREATEST(...)` floor input
- Admin surface: `/admin/auctions-data-center/purchase-orders/**`
- Snowflake sync: push-only via `AUCTIONS.UPSERT_PURCHASE_ORDER`
