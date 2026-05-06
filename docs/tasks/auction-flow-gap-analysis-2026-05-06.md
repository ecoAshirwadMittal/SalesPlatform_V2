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
on `RoundStartedEvent(round=2)`. The remaining stub-listener work is the
**R3 init** and **R3 pre-process** branches (sub-project 6).

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
| R2 bid submission | 🟡 Partial | Submission pipeline works; **but** `BidDataCreationRepository.java:126–137` hardcodes `bid_meets_threshold` and `row_visible` to `TRUE` regardless of R1 rank |
| **R2 close → RANKING + TARGET_PRICE** | ✅ Built (4C) | Same listener handles round 2 → produces `round3_target_price` and `round3_bid_rank` |
| **R3 init (Upsell setup)** | 🔴 Stub | `service/auctions/lifecycle/stub/R3InitStubListener.java:22` — logs "would set Upsell round started" |
| **R3 pre-process (data prep)** | 🔴 Stub | `service/auctions/lifecycle/stub/R3PreProcessStubListener.java:22` — logs on `RoundClosedEvent(round ∈ {2,3})`; no `bid_data` rows seeded for R3 |
| R3 bid submission | 🟡 Partial | Pipeline mechanically supports R3, but R3 has no inventory until pre-process lands |
| R3 close + ranking | 🔴 Not applicable + missing | `RecalcRoundClosedListener` gates on `round ∈ {1,2}` (terminal-round design); no 4C recalc fires for R3 close |
| R3 reports (Round 3 Bid Report by Buyer) | ✅ Built (read-only) | `Round3ReportController` + `Round3ReportService`; `auctions.round3_buyer_data_reports`; frontend at `admin/auctions-data-center/round3-bid-report/page.tsx`. Empty until R3 pre-process lands |
| Buyer notification (R3 start email) | 🔴 Missing | `ACT_Round3_StartNotification` not ported. Schema has `is_start_notification_sent`, `is_end_notification_sent`, `is_reminder_notification_sent` on `SchedulingAuction.java:51–57` but no writes |
| Buyer Award Summary Report | 🔴 Missing | Mendix `SUB_LoadBuyerAwardsSummaryReport` + `EM_BuyerAwardsSummaryReport` — no service, controller, or frontend page exists |
| Order / PWS handoff post-auction | 🔴 Not ported | `ACT_Auction_SendAllBidsToSnowflake_Admin` (manual bulk re-push) absent. `OfferService:662` carries `// TODO: Sync offer data to Snowflake analytics` |

---

## 3. Stub listeners — what each defers

| Stub file | Fires on | Missing production behavior |
|---|---|---|
| `service/auctions/lifecycle/stub/R3InitStubListener.java:22` | `RoundStartedEvent(round=3)` | **R3 (Upsell) init**: `ACT_Round3_SetStarted` / `SUB_InitializeRound3` |
| `service/auctions/lifecycle/stub/R3PreProcessStubListener.java:22` | `RoundClosedEvent(round ∈ {2,3})` | **R3 data pre-processing**: `SUB_Round3_PreProcessRoundData` + `ACT_GenerateRound3_BidDataObjects` (seeds R3 `bid_data` rows + `round3_buyer_data_reports`) |

> `R2InitStubListener` was deleted 2026-05-06 (sub-project 5 shipped).

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
- No PO Excel upload route under `purchase-orders/` (reserve-bids has one; `POExcelParser` exists in backend)

---

## 5. Snowflake sync inventory

| Direction | What | Status |
|---|---|---|
| Pull | `aggregated_inventory` weekly load — `SnowflakeAggInventoryReader` | ✅ Built |
| Pull | `reserve_bid` — `ReserveBidSyncScheduledJob` + `JdbcReserveBidSnowflakeReader` | ✅ Built (4A) |
| Push | `AUCTIONS.RESERVE_BID` — `ReserveBidSnowflakePushListener` | ✅ Built (4A); `// future:` deferred sync-log |
| Push | `AUCTIONS.UPSERT_PURCHASE_ORDER` — `PurchaseOrderSnowflakePushListener` | ✅ Built (4B); same deferral |
| Push | Auction status — `AuctionStatusSnowflakePushListener` | ✅ Built |
| Push | `AUCTIONS.BUYER_BID` rankings — `BidRankingSnowflakePushListener.java:38` | ✅ Built (4C); `// future: write a FAILED row to integration.snowflake_sync_log` |
| Push | `AUCTIONS.TARGET_PRICE_AUDIT` — `TargetPriceSnowflakePushListener.java:38` | ✅ Built (4C); same deferral |
| Missing | Manual "send all bids" admin action (Mendix `ACT_Auction_SendAllBidsToSnowflake_Admin`) | 🔴 Not ported |
| Missing | `syncLogRepo.recordFailure(...)` in the four push-listener catch blocks | 🔴 `SyncLogWriter` + `SnowflakeSyncLogRepository` exist (used by `AggregatedInventoryService`); not injected into the four 4x listeners |

---

## 6. Top 10 ranked pending actions

Ranked by criticality × dependency-blocking factor.

| # | Item | Scope | Rationale |
|---|---|---|---|
| **1** | ~~**Sub-project 5: R2 buyer assignment** — replace `R2InitStubListener` with `SUB_AssignRoundTwoBuyers` + `SUB_GenerateRound2QualifiedBuyerCodes`~~ ✅ **Shipped 2026-05-06** | L | Blocks every live R2 cycle. Without it, zero buyers are scoped to R2 |
| **2** | **Sub-project 6: R3 init + pre-process** — replace `R3InitStubListener` and `R3PreProcessStubListener` with `ACT_Round3_SetStarted` + `SUB_Round3_PreProcessRoundData` / `ACT_GenerateRound3_BidDataObjects` | L | Blocks R3 (Upsell) bidding; populates the already-shipped R3 report page |
| **3** | **Fix `bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository.java:126–137`** | M | Every buyer currently sees every row in R2/R3 regardless of R1 rank — functionally incorrect for production |
| **4** | **Buyer auction email notifications** — port `ACT_Round3_StartNotification`; wire the three notification-sent columns on `SchedulingAuction.java:51–57` | M | Schema slots exist; no service writes them |
| **5** | **Buyer Award Summary Report** — port `SUB_LoadBuyerAwardsSummaryReport` + admin page | M | Finance/ops reporting hole — entirely absent |
| **6** | **Wire `syncLogRepo.recordFailure(...)` in all 4 push-listener catch blocks** (EB, PO, 4C-BidRanking, 4C-TargetPrice) | S | `SyncLogWriter` + `SnowflakeSyncLogRepository` already exist; just inject and call |
| **7** | **Frontend UI for `/re-rank` and `/recalculate-target-price`** | S | REST endpoints shipped in 4C; design deferred UI |
| **8** | **Special-treatment buyer handling** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart` | M | `is_special_treatment` exists on `QualifiedBuyerCode`; `row_visible=TRUE` ignores it |
| **9** | **Admin "send all bids to Snowflake"** — port `ACT_Auction_SendAllBidsToSnowflake_Admin` as a bulk re-push endpoint | S | Ops have no force-resync path today |
| **10** | **PO Excel upload page** — mirror reserve-bids upload route (`POExcelParser` exists in backend) | S | PO creation is one-row-at-a-time; reserve-bids has batch upload |

**Critical path:** items 2 and 3 are the remaining production blockers
(item 1 shipped 2026-05-06 as sub-project 5). Without items 2 and 3, R3
(Upsell) bidding does not function and R2 per-row visibility is
incorrect for non-special buyers. Items 4–10 are non-blocking polish
that can ship in any order.

---

## Appendix: key file references

| File | Role |
|---|---|
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R2InitStubListener.java` | Sub-project 5 placeholder |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3InitStubListener.java` | Sub-project 6 placeholder |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/lifecycle/stub/R3PreProcessStubListener.java` | Sub-project 6 placeholder |
| `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java` lines 126–137 | `bid_meets_threshold` + `row_visible` stubs |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/BidRankingSnowflakePushListener.java` line 38 | Deferred `syncLogRepo` call |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/snowflake/TargetPriceSnowflakePushListener.java` line 38 | Deferred `syncLogRepo` call |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/RecalcOrchestrator.java` | 4C two-process coordinator |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/BidRankingService.java` | DENSE_RANK process |
| `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/recalc/TargetPriceRecalcService.java` | GREATEST CTE process |
| `backend/src/main/java/com/ecoatm/salesplatform/controller/admin/RecalcAdminController.java` | REST-only re-rank + recalculate endpoints |
| `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/SchedulingAuction.java` lines 51–57 | Notification-sent columns (schema present, never written) |
| `migration_context/backend/services/SUB_AssignRoundTwoBuyers.md` | R2 buyer assignment Mendix spec |
| `migration_context/backend/services/SUB_Round3_PreProcessRoundData.md` | R3 pre-process Mendix spec |
| `migration_context/backend/ACT_Round3_StartNotification.md` | Buyer notification Mendix spec |
| `migration_context/backend/services/SUB_LoadBuyerAwardsSummaryReport.md` | Buyer Award Summary Mendix spec |
