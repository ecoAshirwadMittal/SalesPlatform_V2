# Auction Flow Gap Analysis — 2026-05-06

**Snapshot date:** 2026-05-06
**Branch:** `main`
**Most recent feature merged:** Sub-project 4C (bid ranking + target-price recalc)

This doc inventories what is shipped vs missing across the end-to-end
auction lifecycle in SalesPlatform_Modern, with file/line evidence for every
gap. It is a snapshot in time — re-run the analysis when sub-projects 5 or 6
land, or whenever the stub listeners under
`backend/.../service/auctions/lifecycle/stub/` change.

---

## 1. Sub-project 4 status

| Sub | Name | Shipped? | Task doc | Key remaining gaps |
|---|---|---|---|---|
| **4A** | EB module (reserve bids) | **Yes** — V76, V77; `auctions.reserve_bid`, `reserve_bid_audit`, `reserve_bid_sync`; full admin CRUD + Snowflake push/pull; frontend pages at `admin/auctions-data-center/reserve-bids/**` | `docs/tasks/auction-eb-module-design.md` / `-plan.md` | `snowflake_sync_log` FAILED-row write deferred (`// future:` in push listener catch) |
| **4B** | PO module | **Yes** — V80, V81; `auctions.purchase_order`, `auctions.po_detail`; full admin CRUD + Snowflake push; frontend pages at `admin/auctions-data-center/purchase-orders/**` | `docs/tasks/auction-po-module-design.md` / `-plan.md` | Same `syncLogRepo` gap; no PO Excel upload UI |
| **4C** | Bid ranking + target-price recalc | **Yes** — V82; `RecalcRoundClosedListener` (replaced `BidRankingStubListener`); two services + orchestrator; admin POST endpoints; both Snowflake push listeners; full test suite | `docs/tasks/auction-bid-ranking-design.md` / `-plan.md` | (a) `syncLogRepo.recordFailure(...)` deferred in both 4C listeners; (b) no UI for re-rank / recalculate-target-price |

**Observation:** Sub-project 4 (reserve floor + PO floor + R1/R2 close-time
math) is now functionally complete. **Sub-project 5 (R2 buyer assignment) shipped 2026-05-06** —
`R2InitStubListener` has been replaced by `R2BuyerAssignmentListener` +
`R2BuyerAssignmentService`; QBC + special-buyer bid_data writes now run
on `RoundStartedEvent(round=2)`. **Sub-project 6 (R3 init + pre-process) shipped 2026-05-07** —
both R3 stub listeners have been deleted and replaced by production services; V84 + V85 migrations
apply the new status columns and reports-table wiring. The stub directory is now empty.

---

## 2. End-to-end auction lifecycle gaps

| Stage | Status | Evidence |
|---|---|---|
| Auction creation | ✅ Built | `AuctionService` + `AuctionController`; `CreateAuctionModal.tsx` |
| Scheduling (start/end times per round) | ✅ Built | `AuctionScheduleService` + `SchedulingAuctionController`; frontend at `admin/auctions-data-center/auctions/[auctionId]/schedule/page.tsx` and `schedule-auction/page.tsx` |
| Cron tick — round transitions | ✅ Built | `AuctionLifecycleScheduler` + `AuctionLifecycleService` + `RoundTransitionService`; ShedLock-guarded; emits `RoundStartedEvent` / `RoundClosedEvent` |
| R1 init (aggregated inventory seeding) | ✅ Built | `Round1InitializationService` + `R1InitListener` (on `RoundStartedEvent(round=1)`) |
| R1 bid submission | ✅ Built | `BidDataCreationService`, `BidDataSubmissionService`, `BidderDashboardService`; `BidImportService` (Excel upload); `BidCarryoverService`; frontend at `bidder/dashboard/**` |
| **R1 close → RANKING + TARGET_PRICE** | ✅ Built (4C) | `RecalcRoundClosedListener` → `RecalcOrchestrator` → `BidRankingService` / `TargetPriceRecalcService`; status columns on `scheduling_auctions` from V82 |
| **R2 init (buyer assignment)** | ✅ Built (5) | `R2BuyerAssignmentListener` + `R2BuyerAssignmentService` (replaced `R2InitStubListener` 2026-05-06); QBC three-set write + special-buyer bid_data seed; status columns on `scheduling_auctions` from V83 |
| R2 bid submission | ✅ Built (5b) | `BidDataCreationRepository` now applies the R2 5-branch cascade + STB shortcut (sub-project 5b, 2026-05-07). Per-row visibility correct for non-special and STB bidders. |
| **R2 close → RANKING + TARGET_PRICE** | ✅ Built (4C) | Same listener handles round 2 → produces `round3_target_price` and `round3_bid_rank` |
| **R3 init (Upsell setup)** | ✅ Built (6) — V84/V85; replaced both stubs; full test suite | `R3InitListener` + `R3InitService`; predecessor guard requires `r3_preprocess_status = SUCCESS` |
| **R3 pre-process (data prep)** | ✅ Built (6) — V84/V85; replaced both stubs; full test suite | `R3PreProcessListener` + `R3PreProcessService`; 5 phases — delete unsubmitted R2 bids, regular CTE, STB CTE, QBC bulk INSERT, round3 reports |
| R3 bid submission | ✅ Built (5b/6) | R3 pre-process (sub-project 6) populates QBCs + reports; R3 cascade in `BidDataCreationRepository` (sub-project 5b) gates per-row visibility on the buyer's R1+R2 latest bid. |
| R3 close + ranking | 🔴 Not applicable + missing | `RecalcRoundClosedListener` gates on `round ∈ {1,2}` (terminal-round design); no 4C recalc fires for R3 close |
| R3 reports (Round 3 Bid Report by Buyer) | ✅ Built (read-only) | `Round3ReportController` + `Round3ReportService`; `auctions.round3_buyer_data_reports`; frontend at `admin/auctions-data-center/round3-bid-report/page.tsx`. Empty until R3 pre-process lands |
| Buyer notification (R3 start email) | 🔴 Missing | `ACT_Round3_StartNotification` not ported. Schema has `is_start_notification_sent`, `is_end_notification_sent`, `is_reminder_notification_sent` on `SchedulingAuction.java:51–57` but no writes |
| Buyer Award Summary Report | 🔴 Missing | Mendix `SUB_LoadBuyerAwardsSummaryReport` + `EM_BuyerAwardsSummaryReport` — no service, controller, or frontend page exists |
| Order / PWS handoff post-auction | 🔴 Not ported | `ACT_Auction_SendAllBidsToSnowflake_Admin` (manual bulk re-push) absent. `OfferService:662` carries `// TODO: Sync offer data to Snowflake analytics` |

---

## 3. Stub listeners — what each defers

> **All stub listeners have been deleted.** Sub-project 5 deleted `R2InitStubListener` (2026-05-06); sub-project 6 deleted both `R3InitStubListener` and `R3PreProcessStubListener` (2026-05-07). The `service/auctions/lifecycle/stub/` directory is now empty.

There are no remaining stub listeners.

---

## 4. Frontend gaps

**Built** (auction admin):
- `admin/auctions-data-center/inventory/page.tsx` — aggregated inventory + create-auction modal
- `admin/auctions-data-center/auctions/page.tsx` — auction list
- `admin/auctions-data-center/auctions/[auctionId]/schedule/page.tsx` — schedule form
- `admin/auctions-data-center/schedule-auction/page.tsx` — scheduling auction list
- `admin/auctions-data-center/round-filters/[round]/page.tsx` — R2 criteria
- `admin/auctions-data-center/bid-data/page.tsx` — bid data grid
- `admin/auctions-data-center/reserve-bids/**` — 4A full surface (list, detail, new, upload, audit)
- `admin/auctions-data-center/purchase-orders/**` — 4B list, new, detail
- `admin/auctions-data-center/round3-bid-report/page.tsx`
- `bidder/dashboard/**` — bidder dashboard

**Missing**:
- No "Re-rank" / "Recalculate Target Price" UI buttons (4C deferred to REST-only by design — endpoints exist on `RecalcAdminController`)
- No Buyer Award Summary Report page
- No R2 qualified-buyer-code result view (criteria-config exists; assignment-result view does not)
- ~~No PO Excel upload route under `purchase-orders/`~~ ✅ Shipped (5b → #10) — `purchase-orders/[id]/upload/page.tsx`

---

## 5. Snowflake sync inventory

| Direction | What | Status |
|---|---|---|
| Pull | `aggregated_inventory` weekly load — `SnowflakeAggInventoryReader` | ✅ Built |
| Pull | `reserve_bid` — `ReserveBidSyncScheduledJob` + `JdbcReserveBidSnowflakeReader` | ✅ Built (4A) |
| Push | `AUCTIONS.RESERVE_BID` — `ReserveBidSnowflakePushListener` | ✅ Built (4A); FAILED sync-log wired in gap-analysis #6 (2026-05-07) |
| Push | `AUCTIONS.UPSERT_PURCHASE_ORDER` — `PurchaseOrderSnowflakePushListener` | ✅ Built (4B); FAILED sync-log wired in gap-analysis #6 (2026-05-07) |
| Push | Auction status — `AuctionStatusSnowflakePushListener` | ✅ Built |
| Push | `AUCTIONS.BUYER_BID` rankings — `BidRankingSnowflakePushListener` | ✅ Built (4C); FAILED sync-log wired in gap-analysis #6 (2026-05-07) |
| Push | `AUCTIONS.TARGET_PRICE_AUDIT` — `TargetPriceSnowflakePushListener` | ✅ Built (4C); FAILED sync-log wired in gap-analysis #6 (2026-05-07) |
| Missing | Manual "send all bids" admin action (Mendix `ACT_Auction_SendAllBidsToSnowflake_Admin`) | 🔴 Not ported |
| ~~Missing~~ | ~~`syncLogRepo.recordFailure(...)` in the four push-listener catch blocks~~ | ✅ Wired in gap-analysis #6 (2026-05-07) via `SyncLogWriter.writeFailed(...)` |

---

## 6. Top 10 ranked pending actions

Ranked by criticality × dependency-blocking factor.

| # | Item | Scope | Rationale |
|---|---|---|---|
| **1** | ~~**Sub-project 5: R2 buyer assignment** — replace `R2InitStubListener` with `SUB_AssignRoundTwoBuyers` + `SUB_GenerateRound2QualifiedBuyerCodes`~~ ✅ **Shipped 2026-05-06** | L | Blocks every live R2 cycle. Without it, zero buyers are scoped to R2 |
| **2** | ~~**Sub-project 6: R3 init + pre-process** — replace `R3InitStubListener` and `R3PreProcessStubListener` with `ACT_Round3_SetStarted` + `SUB_Round3_PreProcessRoundData`~~ ✅ **Shipped 2026-05-07** | L | Both stubs deleted; V84 + V85 migrations; `R3PreProcessService` + `R3InitService` + full test suite |
| **3** | ~~**Fix `bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository.java:126–137`**~~ ✅ **Shipped 2026-05-07 (sub-project 5b)** | M | R2 + R3 cascades + STB shortcut; 20 new IT cases; design at `docs/tasks/auction-r2-r3-row-visibility-design.md` |
| **4** | **Buyer auction email notifications** — port `ACT_Round3_StartNotification`; wire the three notification-sent columns on `SchedulingAuction.java:51–57` | M | Schema slots exist; no service writes them |
| **5** | **Buyer Award Summary Report** — port `SUB_LoadBuyerAwardsSummaryReport` + admin page | M | Finance/ops reporting hole — entirely absent |
| **6** | ~~**Wire `syncLogRepo.recordFailure(...)` in all 4 push-listener catch blocks**~~ ✅ **Shipped 2026-05-07** | S | New `SyncLogWriter.writeFailed(syncType, targetKey, errorMessage)` method (single-shot REQUIRES_NEW); 4 listeners now record FAILED rows on push exceptions |
| **7** | **Frontend UI for `/re-rank` and `/recalculate-target-price`** | S | REST endpoints shipped in 4C; design deferred UI |
| **8** | **Special-treatment buyer handling** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart` | M | `is_special_treatment` exists on `QualifiedBuyerCode`; `row_visible=TRUE` ignores it |
| **9** | **Admin "send all bids to Snowflake"** — port `ACT_Auction_SendAllBidsToSnowflake_Admin` as a bulk re-push endpoint | S | Ops have no force-resync path today |
| **10** | ~~**PO Excel upload page** — mirror reserve-bids upload route (`POExcelParser` exists in backend)~~ ✅ **Shipped 2026-05-07** | S | New `purchase-orders/[id]/upload/page.tsx` route mirrors the reserve-bids upload UX (counts + errors table + back navigation). Backend `POST /{id}/details/upload` was already wired. |

**Critical path:** items 1, 2, and 3 are all shipped. Items 4–10 are non-blocking
polish that can ship in any order.

---

## 7. Follow-up risks

### ~~Latent bug: `R2BuyerAssignmentService.recalculate()` self-call AOP bypass~~ — **fixed 2026-05-07**

**Status:** Fixed — commit `3a096b1` annotates `R2BuyerAssignmentService.recalculate()`
with `@Transactional(propagation = REQUIRES_NEW)`, matching the pattern applied
to both R3 services in sub-project 6.

**Original bug:** `recalculate()` called `this.run()` on the same bean instance.
Spring's CGLIB proxy intercepts `recalculate()` but not the self-call, so the
`@Transactional(REQUIRES_NEW)` annotation on `run()` was never applied. Without
a surrounding transaction, the inner `MANDATORY` calls (e.g.,
`RecalcStatusUpdater.markSuccess`) threw `IllegalTransactionStateException`.
Production `POST /api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers`
would have failed with a 500.

The bug was silent in the test suite because `R2BuyerAssignmentAdminControllerIT`
is a `@WebMvcTest` slice with `@MockBean R2BuyerAssignmentService` — the real
service body was never invoked.

---

## Appendix: key file references

| File | Role |
|---|---|
| ~~`backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R2InitStubListener.java`~~ | Deleted (sub-project 5 shipped 2026-05-06) |
| ~~`backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3InitStubListener.java`~~ | Deleted (sub-project 6 shipped 2026-05-07) |
| ~~`backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3PreProcessStubListener.java`~~ | Deleted (sub-project 6 shipped 2026-05-07) |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3PreProcessService.java` | R3 pre-process implementation (sub-project 6) |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/r3init/R3InitService.java` | R3 init implementation with predecessor guard (sub-project 6) |
| `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/R3LifecycleAdminController.java` | REST-only /preprocess-r3 + /reinit-r3 endpoints (sub-project 6) |
| ~~`backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java` lines 126–137~~ | ~~`bid_meets_threshold` + `row_visible` stubs~~ (resolved by sub-project 5b) |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakePushListener.java` | `// future:` comment removed; `writeFailed` wired (#6, 2026-05-07) |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakePushListener.java` | `// future:` comment removed; `writeFailed` wired (#6, 2026-05-07) |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcOrchestrator.java` | 4C two-process coordinator |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/BidRankingService.java` | DENSE_RANK process |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/TargetPriceRecalcService.java` | GREATEST CTE process |
| `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/RecalcAdminController.java` | REST-only re-rank + recalculate endpoints |
| `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java` lines 51–57 | Notification-sent columns (schema present, never written) |
| `migration_context/backend/services/SUB_AssignRoundTwoBuyers.md` | R2 buyer assignment Mendix spec |
| `migration_context/backend/services/SUB_Round3_PreProcessRoundData.md` | R3 pre-process Mendix spec |
| `migration_context/backend/ACT_Round3_StartNotification.md` | Buyer notification Mendix spec |
| `migration_context/backend/services/SUB_LoadBuyerAwardsSummaryReport.md` | Buyer Award Summary Mendix spec |
