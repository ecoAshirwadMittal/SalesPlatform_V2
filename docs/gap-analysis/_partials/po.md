# PO (Purchase Orders) — gap analysis

**Rollup:** Implemented 10 · Partial 2 · Missing 2 · Divergent 1 (of 15 assessed) · spec surface: 5 pages / 1 batch / 16 reachable flows (of 21)

## State (2-3 sentences)
The authoring half of the PO module — create/edit a PO with a From/To week range, Excel import (create + full-replace), buyer-code validation, Excel export, and one-way Snowflake push — is cleanly rebuilt in `PurchaseOrderController` (8 endpoints) + `service/auctions/purchaseorder/*` + migrations V80/V81, and the 4C target-price recalc correctly consumes `po_detail` as a `GREATEST(...)` floor. The **fulfillment-reconciliation half** (on-demand + weekly pack-out sync that populated `WeeklyPO`) is deliberately dropped: V80 documents `ecoatm_po$weeklypo` as "4C unused," and the legacy weekly batch was already Disabled. Two smaller gaps remain — the create-time week-range **overlap guard** is not enforced, and the PO-module `Inventory_Overview` page has no counterpart here.

## Entry points, screens & flows

| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `PurchaseOrder_Main` · create PO, pick week range, export, on-demand sync, save edits · page | PARTIAL | `admin/auctions-data-center/purchase-orders/new` + `[id]`; `PurchaseOrderController.create/update`, `GET /{id}/details/download` | Create / week-range / export / edit all present; the page's **`ACT_OnDemandSync`** button has no modern counterpart (see below). |
| `PurchaseOrder_Overview` · list POs, open/new · page | IMPLEMENTED | `purchase-orders/page.tsx`; `GET /` (`list` w/ week+year filters), `GET /by-range` (0/1/2+ cardinality) | |
| `PODetail_Overview` · browse PODetail records · page | IMPLEMENTED | `[id]` detail; `GET /{id}/details` (`PODetailService.list`), `POST /{id}/details/upload` | Legacy `DeleteAll_PO` on this page is a test-reset util → obsolete (see below). |
| `Inventory_Overview` · review listed inventory for a Week, export Excel · page | MISSING | not found under `purchase-orders/*` | No PO-module inventory page. May be subsumed by the separate Auctions inventory surface (`/admin/auctions-data-center/inventory`) — out of this capability's scope, but no direct port. |
| `ACT_CreateNewPO` / `NAV_CreatePO` / `SUB_GetOrCreatePOHelper` · start PO + landing · flow | IMPLEMENTED | `POST /` → `PurchaseOrderService.create`; `PurchaseOrderChangedEvent.UPSERT` | Per-user `POHelper` staging singleton is obsolete (stateless REST); create takes `weekFromId/weekToId` directly. |
| `SUB_ImportCreatePODetails` · create PO from Excel + validation · flow | IMPLEMENTED | `POST /{id}/details/upload` → `PODetailService.upload`; `POExcelParser`; `PurchaseOrderValidator.requireBuyerCodes` | |
| `SUB_ImportUpdatePODetails` · update PO from Excel (full replace) + re-push · flow | IMPLEMENTED | `PODetailService.upload` replaces detail rows; `PurchaseOrderChangedEvent` → Snowflake push | Full-replace semantics preserved. |
| `SUB_CreatePODetail` · per-row PODetail factory · flow | IMPLEMENTED | `POExcelParser` + `PODetail` rows (buyer_code_id FK, product_id, grade, price) | |
| `VAL_BuyerCode_PO` · buyer-code existence validation · flow | IMPLEMENTED | `PurchaseOrderValidator.requireBuyerCodes` → `BuyerCodeRepository.findCodesIn`; `MISSING_BUYER_CODE` blocks import | Note: verify Active-flag filtering matches legacy "Active BuyerCode" lookup — modern resolves by code set, may not gate on active status. |
| `VAL_WeekRange_PO` · week range valid + no overlap with existing PO · flow | PARTIAL | `PurchaseOrderValidator.resolveWeekRange` enforces `from ≤ to` (`INVALID_WEEK_RANGE`) | **Overlap guard missing** — legacy blocked a range already claimed by another `WeekPeriod` ("no two POs cover the same week"); modern drops `WeekPeriod` (V80) and does not re-check overlap at create/update. `findByExactWeekRange` is a landing lookup, not a create-time guard. |
| `SUB_UploadPOToSnowFlake` · push PO snapshot to Snowflake · flow | IMPLEMENTED | `PurchaseOrderSnowflakePushListener` + `JdbcPurchaseOrderSnowflakeWriter` (UPSERT_PURCHASE_ORDER) / `Logging` default; `po.sync.*` config | Push-only, event-driven on UPSERT/DELETE — parity. |
| `ACT_ExportPOtoExcel` · export PO grid to Excel · flow | IMPLEMENTED | `GET /{id}/details/download` → `POExcelBuilder.write` | |
| `ACT_UpdatePO` · save PO edits · flow | IMPLEMENTED | `PUT /{id}` → `PurchaseOrderService.update` | |
| 4C floor consumption (`po_detail` as target-price floor) · flow | IMPLEMENTED | `TargetPriceRecalcRepository` joins `auctions.po_detail`→`purchase_order`, `GREATEST(MaxBid+factor, EB, PO)` for round2/round3 | Confirms modules.md claim; the load-bearing downstream consumer works. |
| `ACT_OnDemandSync` (+ `SUB_UpdatePOFromPackOut`, `SUB_QuerySnowflakeOnDemand`) · reconcile PO fulfillment vs Snowflake sales, write `WeeklyPO` · flow | MISSING | not found — searched backend for `WeeklyPO`/`PackOut`/`OnDemandSync`/`DIM_PACKOUT`/`VW_SALE_ORDER_PO`/`fulfillment` → 0 hits | Intentionally dropped: V80 header drops `ecoatm_po$weeklypo` ("12,384 rows — fulfillment tracker, 4C unused"). Real legacy capability not rebuilt. |
| Lifecycle derivation (DRAFT/ACTIVE/CLOSED) · derived state | DIVERGENT | `PurchaseOrderLifecycleState.derive(today, weekFrom, weekTo)` → **ACTIVE/CLOSED only** | Modern intentionally collapses to two states ("DRAFT was over-modelling"); `data-model.md` still says DRAFT/ACTIVE/CLOSED. Derived-not-stored parity holds; the DRAFT state is gone by design. |

## Biggest gaps (named, with spec node ids)
1. **Week-range overlap guard not enforced** — `VAL_WeekRange_PO`. Legacy blocked creating a PO whose weeks overlap an existing PO's `WeekPeriod`; modern only checks `from ≤ to`. Two POs can now cover the same week, which would double-count the `GREATEST(...)` PO floor into 4C target-price. **Highest-severity functional gap.** (PARTIAL)
2. **Fulfillment reconciliation dropped** — `ACT_OnDemandSync` + `SUB_UpdatePOFromPackOut` + `Update_POFromPackOut_Weekly` batch. No `WeeklyPO`/pack-out tracking against Snowflake sales. Intentional (V80 "4C unused") but it *is* a legacy behavior with no modern equivalent. (MISSING)
3. **`Inventory_Overview` page absent** — no PO-module inventory review/export page; likely re-homed to the Auctions inventory surface but not ported here. (MISSING)

## Net-new modern behavior (not in legacy)
- **`GET /by-range` cardinality envelope** — exact week-range lookup returning a `matches[]` array so the landing branches 0→empty / 1→load / 2+→error.
- **Event-driven Snowflake push** — `PurchaseOrderChangedEvent` (UPSERT/DELETE) decouples the write from the warehouse sync via a listener + pluggable `logging`/`jdbc` writer, with `po.sync.*` timeout/toggle config.
- **Upload hardening** — `UploadRateLimiter` on `/{id}/details/upload` (per-client-IP, 429).
- **Two-state lifecycle enum** — `PurchaseOrderLifecycleState` (ACTIVE/CLOSED) as first-class derived state with a clean `derive(today, from, to)` function.

## Likely-dead / obsolete legacy (don't port)
- **`Update_POFromPackOut_Weekly` (batch)** — **Disabled in legacy** (status captured in extract); the WeeklyPO fulfillment tracker it fed is dropped in V80. Do not port.
- **`DeleteAll_PO` / `ACT_WeeklyPODELETE`** — destructive, unfiltered test/QA data-reset utilities (`ACT_WeeklyPODELETE` is hardcoded to ProductID 16687 / BuyerCode 'ADPO' and doesn't even delete). Never rebuild as-is.
- **`EcoATM_PO.Page` (Device Allocation landing) + `ACT_DeleteDA`** — a Device-Allocation (`EcoATM_DA.DAWeek`) reset tool that happens to live in the PO module; belongs to the DA domain (not in scope) and is a blunt "delete every DAWeek" QA util.
- **`POHelper` / `POHelper_Account` per-user staging singleton, `SUB_GetOrCreatePOHelper`, `NAV_PurchaseOrder`/`NAV_CreatePO` spinner wrappers, `DS_FromWeekPO`/`DS_ToWeekPO`, `DS_GetOrCreatePODoc`, `PurchaseOrderDoc` blob container** — Mendix client-state + file-blob scaffolding; React owns UI state, week pickers hit `GET /weeks`, and Excel streams through the controller (V80 drops `purchaseorderdoc`, `weekperiod`, `pohelper`).
- **`ACT_GETWeeklyPO`** (unreachable) — WeeklyPO report datasource; moot once WeeklyPO is dropped.
