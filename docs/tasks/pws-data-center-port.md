# PWS Data Center — Admin Screen Port

**Status:** Phase 3 complete
**Owner:** Ashirwad Mittal
**Created:** 2026-04-15
**Legacy source:** Mendix QA `https://buy-qa.ecoatmdirect.com` → `Admin` → `PWS Data Center`

## Why

Mendix exposes 19 raw entity datagrids under `Admin → PWS Data Center`.
The modern app has no equivalent admin surface — ops has no way to
inspect PWS devices, offers, orders, RMAs, or shipment sync status from
the new UI. Today they must still log into QA Mendix for day-to-day
triage, blocking Mendix sunset.

## Parity reference (live QA audit, 2026-04-15)

| QA button | Backing entity | Key columns |
|---|---|---|
| PWS Orders | `pws.orders` | order #, status, ship date, qty, total |
| **PWS Offers and offer items** | `pws.offers` | offer id, status, sku count, qty, total, offer date, sales rep, buyer code, update date, changed by |
| Offer Items | `pws.offer_items` | – |
| PWS Offer ID | id generator | – |
| PWS Device {Colors, Brands, Capacities, Carriers, Grades, Models, Categories} | `mdm.*` | lookup CRUD |
| PWS Case Lots | `mdm.case_lots` | – |
| **PWS Devices** | `mdm.device` | SKU, ItemType, DeviceDescription, AvailableQty, ATPQty, ReservedQty, Last Sync Time, CurrentListPrice, IsActive, createdDate, changedDate |
| PWS Integration | `integration.deposco_config` | – |
| PWS Shipment Data | `pws.shipment_details` | – |
| PWS Full Inventory Sync Report | sync log | – |
| RMA / RMA Items / Request Files | RMA schema | – |
| PWS Device Notes | device notes | – |
| PWS Buyer Offers and Offer items | buyer-scoped offers | – |
| PWS Idle Timeout → Users | session config | – |

### Legacy anti-patterns found in QA (do NOT port 1:1)
1. `ACT Fix updated date` and `ACT Fix Order Status (friendly display)`
   buttons on the Offers grid — raw microflow data-repair levers exposed
   to admins with no audit trail. Port as scripted, audited admin
   endpoints with a reason field.
2. `PWS Devices` trash delete — single Proceed modal, no audit write.
   Modern port must soft-delete into `buyer_mgmt.change_logs` (or an
   equivalent audit table) with actor and reason.
3. Duplicate `changedDate` column on Devices grid.
4. `Sync Search Attr For Devices` / `Sync Inventory to Snowflake` are
   long-running batch jobs exposed as buttons with no progress UI.
   Port as async jobs with a status drawer.
5. `Changed By` column leaks raw admin emails. Show display name with
   email tooltip; keep email in audit log only.
6. `Remove SLA Tag` per-row action on Offers — not yet located in
   `migration_context/`. Needs grep pass before Offers admin ships.
7. Default `=` filters on string columns — swap to `contains` in the
   port.

## Consolidation

The 19 QA buttons collapse to **5 functional screens** in the modern
app. Pure master-data CRUD (Colors/Brands/Capacities/Carriers/Grades/
Models/Categories/Case Lots) lives under a single tabbed Master Data
screen rather than one route per table.

| Screen | Route | Consolidates |
|---|---|---|
| **Devices** | `/admin/pws-data-center/devices` | PWS Devices, PWS Device Notes |
| **Offers & Orders** | `/admin/pws-data-center/offers` | PWS Offers and offer items, Offer Items, PWS Orders, PWS Offer ID, PWS Buyer Offers |
| **Master Data** | `/admin/pws-data-center/master-data` | Colors, Brands, Capacities, Carriers, Grades, Models, Categories, Case Lots (tabs) |
| **Shipments & Sync** | `/admin/pws-data-center/shipments` | PWS Shipment Data, PWS Integration, PWS Full Inventory Sync Report |
| **RMA** | `/admin/pws-data-center/rma` | RMA, RMA Items, RMA Request Files |

## Phases

### Phase 1 — Hub + read-only skeleton ✅
- [x] Plan doc (this file)
- [x] Add `Admin` expandable group to sidebar with 3 children
  (Application Control Center, Auctions Data Center, PWS Data Center).
  Parity with QA hierarchy — today `app-control-center` exists as a
  route but is not reachable from the sidebar.
- [x] `/admin/pws-data-center` hub landing — 5-card grid reusing
  `controlCenter.module.css` (already styled to match QA's green
  button hub).
- [x] `/admin/pws-data-center/devices` read-only device overview —
  reuses existing `GET /api/v1/pws/pricing/devices` (returns SKU,
  category, brand, model, currentListPrice — enough for a v1 grid).
  Columns: SKU, Description, Category, Brand, Model, AvailableQty,
  CurrentListPrice, IsActive.
- [x] `/admin/pws-data-center/offers` read-only offer overview —
  reuses existing `GET /api/v1/pws/offer-review` (returns
  `OfferListItem`: offerId, offerNumber, status, buyerCode,
  salesRepName, totalSkus, totalQty, totalPrice, submissionDate,
  updatedDate). Columns match QA's Offer Overview minus `Changed By`
  and `Remove SLA Tag` (both gated on Phase 2).
- [x] Stub routes for `master-data`, `shipments`, `rma` with "Coming
  soon" banner so the hub is fully navigable and the plan is visible
  in the UI.

### Phase 2 — Audit + safe mutations ✅
- [x] Add `changedBy` to `OfferListItem` (JWT principal name).
  Backed by new `pws.offer.changed_by` column (V56) and stamped from
  `CurrentPrincipal.displayName()` on every OfferReviewService
  mutation.
- [x] Row actions on Devices admin: edit future price (reuses
  `PUT /pws/pricing/devices/{id}`), soft-delete via new
  `DELETE /pws/pricing/devices/{id}` with required reason modal.
  Audit row written to `pws.admin_audit_log` (V56).
- [x] Server-side pagination on both grids. Devices already pages via
  `/pws/pricing/devices`; Offers admin grid now uses the new
  `/pws/offer-review/paged` endpoint.
- [x] Swap default `=` filters for `contains` on string columns
  (PricingService category/brand/model/carrier/capacity/color/grade).

### Phase 3 — Sync status + master data ✅
- [x] `/admin/pws-data-center/shipments` — reuses `AtpSyncController`,
  persists every sync run to `integration.sync_run_log` (V57 table),
  and exposes `GET /api/v1/inventory/sync/logs` for the admin grid.
  Surfaces last run card (status, duration, items, updated, missing)
  plus paginated run history. Full-sync button triggers the existing
  `POST /api/v1/inventory/sync/full`; controller stamps the run with
  `CurrentPrincipal.displayName()` and records SUCCESS/FAILED plus
  the truncated error message.
- [x] `/admin/pws-data-center/master-data` — tabbed CRUD over the
  seven MDM lookup tables (brands, models, categories, capacities,
  carriers, colors, grades). Backed by new `AdminMasterDataService`
  + `AdminMasterDataController` at `/api/v1/admin/master-data/{type}`
  with list/create/update/soft-delete. Delete flips `is_enabled` to
  false rather than dropping rows — devices reference these by FK.
  Tabs switch client-side and reset page + filters; name-contains
  filter is client-side against the current page. SecurityConfig
  already gates `/api/v1/admin/**` to the Administrator role.
- [x] `/admin/pws-data-center/rma` — read-only list reusing
  `RmaController` list endpoint. Server-side status filter,
  client-side pagination and number-contains filter.
- [x] Security hardening: `/api/v1/inventory/sync/**` endpoints now
  gated to `Administrator` role in SecurityConfig (was only
  `.authenticated()` — any logged-in user could trigger a full sync).

#### Phase 3 — not in scope
- **Case Lots** tab deferred: `pws.case_lot` has FK to `mdm.device`
  plus its own quantity/price fields, so it does not fit the generic
  BaseLookup CRUD. Track as a follow-up with a dedicated endpoint.
- **Deposco config UI** deferred: `integration.deposco_config` is
  still managed through settings/pws-control-center; Shipments &
  Sync only surfaces sync outcomes.



## Access control

All routes under `/admin/pws-data-center/**` must require one of:
`Administrator`, `ecoAtmDirectAdmin`. Frontend checks via
`getAuthUser()` + role claim; backend enforces via Spring Security
method-level `@PreAuthorize("hasAnyRole('ADMIN','DIRECT_ADMIN')")`
once the role mapping is confirmed. Buyer tokens must 403.

## References

- Live QA audit screenshots: `pws-data-center-landing.png`,
  `pws-devices.png`, `pws-offers-grid.png` (repo root)
- Mendix source: `migration_context/tests/code/src-pages-pws-adminpages-pwsdatacenterdevicespage.md`,
  `src-pages-pws-adminpages-pwsdatacenterofferitemspage.md`
- Existing admin scaffolding:
  `frontend/src/app/(dashboard)/admin/app-control-center/`,
  `frontend/src/app/(dashboard)/settings/pws-control-center/`
- Backend reused APIs: `PricingController` (`/pws/pricing/devices`),
  `OfferReviewController` (`/pws/offer-review`), `PWSAdminController`
  (`/admin/sla-tags/*`)
