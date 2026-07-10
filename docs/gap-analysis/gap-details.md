# Gap Analysis — Details (per capability)

> **What this is.** SalesPlatform_Modern (Next.js + Spring Boot) vs. the legacy Mendix app, whose intended behavior is captured in `../Rebuilding/rebuild_graph.json` (2336 nodes / 3298 edges). Each capability below was analyzed against the graph's **reachable** surface (dead legacy flows excluded) by comparing decisions/reads/writes/integrations — not names. Verdicts: **IMPLEMENTED** (behavior matches) · **PARTIAL** (branches/writes/integrations missing) · **MISSING** (no modern equivalent) · **DIVERGENT** (built differently). Every row cites the spec node id + the modern file/symbol.
>
> **Generated:** 2026-07-10 by 5 parallel analysis agents; the PO overlap-guard and the dead `/cohort-mapping` route were spot-verified against the code.

## Contents
1. Auctions  2. PWS  3. Buyers & Users  4. RMA  5. Credit Requests  6. Purchase Orders  7. Integration  8. Platform  9. Reports  10. The `(none)` bucket (obsolete library code)

---

# Auctions — gap analysis

**Rollup:** Implemented 33 · Partial 12 · Missing 18 · Divergent 2  (of 65 significant entries assessed) · spec surface: 89 pages / 4 batches / 329 reachable flows

> Method note: the Auctions capability is huge (583 nodes; 301 key flows). This
> assesses the ~53 distinct use-cases / screens / jobs (not every helper flow).
> `PO` is a *separate* capability in the graph and is excluded here except where
> the reserve-floor path touches it. Evidence corroborated against the repo's own
> `docs/tasks/auction-flow-gap-analysis-2026-05-06.md`.

## State (2-3 sentences)
The **core auction lifecycle is fully rebuilt**: create → 3-round schedule →
cron-driven round transitions (event-driven) → R1 seed/submit → 4C bid-ranking &
target-price recalc → R2 buyer-assignment → R3 init/pre-process/reports, plus the
EB/reserve-bid module (upload, Excel export, Snowflake push+pull). Bidder dashboard
(grid, import/export, carry-forward, timer, submit) is complete. **Three whole
legacy sub-domains are absent**: the entire Device Allocation module (`EcoATM_DA` —
Round-3 winner allocation, external-DB pull, SharePoint publish), *all* buyer
auction email notifications (start/reminder/confirmation), and the Buyer Award
Summary report — plus SharePoint bid-file publishing and a cluster of admin
config/QA-diagnostic screens. Post-submit side-effects (per-buyer bid→Snowflake
push, confirmation email, SharePoint file send) were dropped from the submit path.

## Entry points, screens & flows

### Scheduling
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `AuctionUI.Create_Auction` (page) · `ACT_Create_Auction` (flow) | IMPLEMENTED | `service/auctions/AuctionService.java`, `AuctionController.java`; `admin/auctions-data-center/inventory` + `CreateAuctionModal` | Stands up Auction from a week; default round scheduling seeded. |
| `AuctionUI.ScheduleAuction_Confirm` · `ACT_SaveScheduleAuction` (flow) | IMPLEMENTED | `service/auctions/AuctionScheduleService.java`, `controller/SchedulingAuctionController.java`; `auctions/[auctionId]/schedule/page.tsx` | Creates R1/R2/R3 `scheduling_auctions`; double-schedule guard present. |
| `AuctionUI.SchedulingAuction_NewEdit` / `_Overview*` · `ACT_SaveScheduleAuction_Admin` | IMPLEMENTED | `SchedulingAuctionController`; `admin/auctions-data-center/scheduling-auctions` + `schedule-auction` pages | Admin edit of round start/end times. |
| `AuctionUI.Inventory_Auction_Overview` · `ACT_UnscheduleAuction`, `ACT_EditAuctionName` | PARTIAL | `AuctionScheduleService` / `AuctionListService`; `inventory`/`auctions` pages | Schedule/reschedule built; **un-schedule revert** ("blocked if a round already Started") not confirmed as a wired endpoint. |
| `AuctionUI.Round2DefaultCriteria`, `PG_Round3Criteria` · `ACT_SaveRound2Criteria`/`ACT_SaveRound3Criteria`, `VAL_TargetPriceFactors`, `VAL_Round3Criteria` | PARTIAL | `controller/admin/RoundCriteriaController.java`, `service/auctions/BidRoundSelectionFilterService.java`; `auctions/round-filters/[round]` + `auction-control-center/r2-criteria` | Criteria persist/read work. **TargetPriceFactor infinity-row/overlap validation and the Round-3 numeric-field regex validation** (`VAL_TargetPriceFactors`, `VAL_Round3Criteria`) not verified as ported — likely thinner. |
| batch `Scheduled_event_ScheduleAuctionStatus` (Disabled) · `ACT_SetAuctionScheduleStarted`/`ACT_SetAuctionScheduleClosed` | IMPLEMENTED (divergent arch) | `service/auctions/lifecycle/AuctionLifecycleScheduler.java` (60 s, ShedLock) → `AuctionLifecycleService` + `RoundTransitionService` → emits `RoundStartedEvent`/`RoundClosedEvent` | Behavior matches (open due rounds, close elapsed rounds). Rebuilt as a ShedLock cron + Spring events rather than a Mendix heartbeat microflow. |

### R1 / bid submission
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `ACT_CreateBidData` / `ACT_CreateBidDataOptimized` (flow) | IMPLEMENTED | `service/auctions/biddata/BidDataCreationService.java`, `repository/auctions/BidDataCreationRepository.java`; `r1init/R1InitListener` | Seeds the round's bid sheet from aggregated inventory (server-side SQL, matching the "optimized" variant). |
| `AuctionUI.BidsSubmittedConfirmation` · `ACT_SubmitBidData` (flow) | PARTIAL | `service/auctions/biddata/BidDataSubmissionService.java#submit`; `bidder/dashboard/BidsSubmittedModal` | Core submit (slide submitted_* values, mark round submitted, per-buyer scope) works. **Missing all four post-submit branches**: `SUB_SendBidDataToSnowflake` per-buyer push, `CreateExcelBidExport`, `SendFilesToSharepointOnSubmit`, and `SUB_SendSubmitBidConfirmationEmail`. No event/email/Snowflake call in the submit path (grep-confirmed). |
| `EcoATM_BidData.*` bid Excel import · `SUB_BidDataImport_NoRank`, `BidData_TransformAndCommit`, `ACT_BidData_Import_ClientController` | IMPLEMENTED | `service/auctions/biddata/BidImportService.java`; `bidder/dashboard/ImportBidsModal` + `ImportBidsButton` | Parse → validate (non-neg price/qty) → merge by EcoID+grade. Generic sheet path covered. |
| Rank-annotated import variants · `SUB_BidDataImport_Round2BidRank`/`_Round3BidRank`, `ACT_BidData_ExcelImport` (sheet-name router) | PARTIAL | `BidImportService` | No `Round3`/`BidDataRankRound2/3Export` sheet-name branch found in `BidImportService` — the three-format router (`BidDataExport` / `RankRound2` / `RankRound3`) is not evident. |
| `EcoATM_BidData.PG_BidCarryover_Confirmation` · `NF_Start_CarryOverBids_JA`, `JA_CarryForwardBids_UpdateOnly_OQL`, `PG_CarryForwardTest` | IMPLEMENTED | `service/auctions/biddata/BidCarryoverService.java`; `bidder/dashboard/CarryoverButton` + `CarryoverResultModal` | Bulk copy of prior-week/round bids ported. |
| Bid sheet download · `ACT_DownloadRound3File` (BidDataExport template), buyer export | IMPLEMENTED (generic) / PARTIAL (R3) | `service/auctions/biddata/BidExportService.java`; `bidder/dashboard/ExportBidsButton` | Streams xlsx bid slice with import round-trip. The **R3 pre-filled offline template** (`ACT_DownloadRound3File` + `SUB_BidDataCustomExcelExport_...PreRound3`) is not a distinct route. |
| `ACT_SaveBidData_Handson` (spreadsheet grid bulk upsert) | IMPLEMENTED | `BidDataSubmissionService` save path; `bidder/dashboard/BidGrid` + `BidGridRow` | Hands-on-table grid edit/save. |
| `SUB_ValidateZeroBids` (zero-qty submit guard) | PARTIAL | `bidder/dashboard/SubmitBidsEmptyStateModal` | An empty-state modal exists; the explicit "zero-quantity lines" confirmation gate (`BidsSubmittedConfirmation_ZeroQuantityBids`) is not clearly ported as a blocking check. |

### R2
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `ACT_Round2AggregatedInventory` (R2 eligibility engine) | IMPLEMENTED | `repository/auctions/BidDataCreationRepository.java` R2 5-branch cascade (sub-project 5b) | Per-row R2 visibility (Only_Qualified / All_Buyers / DW / prior-bid gate) ported; see `docs/tasks/auction-r2-r3-row-visibility-design.md`. |
| `SUB_GenerateQualifiedBuyerCodes_Round2` + `SUB_AssignRoundTwoBuyers` | IMPLEMENTED (divergent source) | `service/auctions/r2init/R2BuyerAssignmentService.java` + `R2BuyerAssignmentListener` (on `RoundStartedEvent(round=2)`); V83 status cols | Qualification computed **in-DB via CTE**, not pulled from Snowflake/PWS (`JA_RetrieveQuery`) as legacy did. Gate `calculate_round2_buyer_participation`. |
| `Sub_ProcessSpecialBuyers` / `SUB_CreateBidDataForAllAE` / `SUB_IsSpecialTreatmentBuyer` | IMPLEMENTED | `service/auctions/r2init/BidDataForAllAEService.java`; `qualified_buyer_codes.is_special_treatment` (V72) | Special/VIP buyers auto-seeded across all AE. |
| `AuctionUI.RoundTwoSelectedBuyers` · `ACT_RemoveRound2BuyerCode` | PARTIAL | `controller/admin/R2QualifiedBuyersController.java` + `QualifiedBuyerCodeAdminController.java`; `auctions/[auctionId]/r2-qualified-buyers` + `auction-control-center/qualified-buyer-codes` | Result **view** is built (closes the doc's earlier gap). **Manual remove-a-buyer-code-from-R2** (`ACT_RemoveRound2BuyerCode`) not confirmed as a wired mutation. |

### R3
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `SUB_GenerateQualifiedBuyerCodes_Round3` + R3 pre-process (`SUB_Round3_PreProcessRoundData`) | IMPLEMENTED | `service/auctions/r3init/R3PreProcessService.java` + `R3PreProcessListener` (on `RoundClosedEvent(round=2)`); V84/V85 | 5-phase: delete unsubmitted R2 bids, regular CTE, STB CTE, QBC bulk INSERT, round3 reports. Divergent source (local CTE, not Snowflake pull). |
| R3 init (`ACT_Round3_SetStarted`, `SUB_InitializeRound3`) | IMPLEMENTED | `service/auctions/r3init/R3InitService.java` + `R3InitListener`; predecessor guard (`r3_preprocess_status = SUCCESS`) | |
| `ACT_Round3_SubmitBidData` | IMPLEMENTED (via generic submit) | `BidDataSubmissionService` | Same submit engine; same missing post-submit side-effects as R1. |
| R3 reports · `ACT_Generate_RoundThreeQualifiedBuyersReport`, `SUB_ListRoundThreeBuyersDataForQualifiedBuyers`, `DS_Round3ValidBidsForBuyer`, `PG_RoundThreeBidsReportByBuyer` | PARTIAL | `controller/admin/Round3ReportController.java` (GET list + by-auction), `Round3ReportService`; `admin/auctions-data-center/round3-bid-report` + `round3_buyer_data_reports` (V85) | **Read-only report only.** The per-buyer "export valid R3 bids to Excel" (rebuild-then-download, `ACT_DownloadRound3ValidBidsForBuyer` via `XLSReport.GenerateExcelDoc`) and the OQL valid-bids recompute are not ported. |
| R3 buyer file exchange · `PG_Round3DataFiles`, `PG_Round3_BidData_XMLUpload_BidRound`, `ACT_Round3_BidData_Import_ClientController_PreProcess`, `SUB_Round3_ExcelImport_PreProcess` | MISSING | — (searched `BidImportService`, `Round3ReportController` — no `Accept_Max_Bid_YN` preprocess, no bulk per-buyer template publish/upload) | The buyer-facing offline Round-3 template download + bulk publish + rank-annotated re-upload preprocess pipeline has no modern equivalent. |

### Reserve bids / EB
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_EB.ReserveBid_File_Upload` · `ACT_UploadReserveBidFile` | IMPLEMENTED | `service/auctions/reservebid/ReserveBidService.java` + `ReserveBidExcelParser`; `controller/admin/ReserveBidController.java`; `reserve-bids/upload` | Parse → match by ProductId+Grade → audit-on-change → update/create; `reserve_bid_audit` (V76). |
| `SUB_RefreshEBPrice` / `SCH_UpdateEBPrice` (Snowflake→Mendix pull) | IMPLEMENTED | `reservebid/ReserveBidSyncScheduledJob.java` (30 min ShedLock) + `snowflake/JdbcReserveBidSnowflakeReader.java` | Watermark via `reserve_bid_sync`. |
| `SUB_ReserveBidData_UpdateSnowflake` (push) | IMPLEMENTED | `snowflake/ReserveBidSnowflakePushListener.java` + `JdbcReserveBidSnowflakeWriter.java` | FAILED sync-log wired (gap-analysis #6). |
| `EcoATM_EB.ReserveBid_Overview` · `ACT_DownloadReserveBids` (EBPrice xlsx) | IMPLEMENTED | `reservebid/ReserveBidExcelWriter.java`; `reserve-bids` list page | |
| `ReserveBid_Admin_Overview`/`_NewEdit` · `ACT_DeleteAll`, admin CRUD | IMPLEMENTED | `ReserveBidController`; `reserve-bids/[id]` + `/new` | Full admin surface. |

### Bid ranking / target price (4C)
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `ACT_TriggerBidRankingCalculation` (DENSE_RANK at round close) | IMPLEMENTED | `service/auctions/recalc/BidRankingService.java` + `RecalcRoundClosedListener` + `RecalcOrchestrator`; V82 status cols; `bid_ranking_config.include_reserve_floor` | Fires on `RoundClosedEvent(round∈{1,2})`. |
| `SUB_UpdateAETargetPriceMaxBid` + `ACT_UpdateRound1TargetPrice_MinBid` (target-price recalc + floors) | IMPLEMENTED | `service/auctions/recalc/TargetPriceRecalcService.java` (GREATEST CTE: reserve floor + PO floor) | Min-allowed-bid floor folded into the recalc CTE. |
| Snowflake pushes · `AUCTIONS.BUYER_BID`, `AUCTIONS.TARGET_PRICE_AUDIT` | IMPLEMENTED | `snowflake/BidRankingSnowflakePushListener.java`, `TargetPriceSnowflakePushListener.java` (+ Jdbc/Logging writers) | |
| Admin recovery · re-rank / recalculate-target-price | IMPLEMENTED | `controller/admin/RecalcAdminController.java`; buttons in `schedule-auction/page.tsx` | |
| `AuctionUI.PG_BidRankingConfiguration` (singleton config page) | PARTIAL | `model/auctions/BidRankingConfig.java` + V82/V86/V87 seed | Config **table** exists and is consumed; no dedicated admin **UI page** to edit it (seeded via migration). |
| R3 close → ranking | MISSING (by design) | `RecalcRoundClosedListener` gates on `round∈{1,2}` | Terminal-round design; documented non-gap. |

### Inventory
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `SUB_LoadAggregatedInventory` / `SUB_LoadAggregatedInventoryTotals` (rebuild) | IMPLEMENTED (divergent arch) | `snowflake/SnowflakeAggInventoryReader.java` + `service/auctions/AggregatedInventoryService.java` + `AggregatedInventorySnowflakeSyncService.java` | Modern **pulls** agg inventory from Snowflake rather than the legacy `ExecuteDatabaseQuery`→XML-roundtrip rebuild. Net effect (materialize AggregatedInventory + Totals per week) matches. |
| `SUB_BuildAggregatedInventoryFilters` (Brand/Carrier/Model facets) | IMPLEMENTED | `service/auctions/inventory/InventoryFilterRequest.java`; `AggregatedInventoryController.java` | |
| `PG_AggregatedInventory` · `ACT_GetAggregateInventoryforWeek` + grid export | IMPLEMENTED | `AggregatedInventoryController` + `AggregatedInventoryExcelExporter.java`; `admin/auctions-data-center/inventory` | |
| Agg-inventory per-row admin CRUD · `AggregatedInventory_NewEdit`/`_Overview`, `ACT_AggregateInventory_UpdateByAdmin`/`_DeleteByAdmin` | PARTIAL | `AggregatedInventoryController` | Grid + export present; per-row admin edit/delete-with-JSON-audit not confirmed wired. |
| `PG_AggInventory_TargetPriceView` (target-price columns) | PARTIAL | target-price columns on `aggregated_inventory` (recalc) | No dedicated target-price view page; data lives on the inventory grid. |
| batch `SE_GetAggregatedInventory` (Disabled) · `SUB_UploadInventoryForCurrentWeek` | DIVERGENT | (no cron) — pull-on-demand via `SnowflakeAggInventoryReader` | No week-close cron; refresh is pull-based. Legacy batch was Disabled. |

### Device Allocation (EcoATM_DA — Round 3 winner allocation)
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_DA.PG_DeviceAllocation*`, `DeviceAllocation_Accept`/`_Reject`/`_Finalize`/`_ChangeEB`, `SUB_DeviceBuyer_SetAwardedQty` | MISSING | none — grep `DeviceAllocation|DeviceBuyer|DAWeek` in `backend/src/main/java` → **0 files**; no frontend dir | Entire winner-award review/accept/reject/finalize workflow absent. |
| `SUB_LoadDAData` / `SUB_GetDADataFromExternalDB` (external Processing-Portal DB pull) | MISSING | none | No external-DB device-allocation pull. |
| `SUB_TransferDADataToSharepoint` (publish allocation results) | MISSING | none | See SharePoint row below. |
| `EcoATM_DA.DAWeek_*` CRUD, `SUB_DeviceAllocation_CreateDeviceBuyers` | MISSING | none | No `DAWeek`/`DeviceBuyer` model or migration. |
| batch `SCE_LoadDAData` (Disabled) · `SE_LoadDAData` | MISSING | none | Legacy batch Disabled; module never ported. |

### Reports & notifications
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| Buyer Award Summary (`SUB_LoadBuyerAwardsSummaryReport`) | MISSING | none (grep `BuyerAward|AwardSummary` → 0) | Finance/ops reporting hole (also flagged in the repo's own gap doc #5). |
| batch `Scheduled_event_Inventory_Notification` (Disabled) · `SUB_InvenNotificationProcessDateTime` + `SUB_InventoryNotificationEmailStart/Reminder_Generic`/`_Round_2_3` | MISSING | `model/auctions/SchedulingAuction.java` has `is_start/end/reminder_notification_sent` cols + `ReminderEmails.java` enum, but **no writer/service** (grep-confirmed) | All auction open/reminder emails (R1/R2/R3, active vs inactive-user activation URL) unported. Schema slots dormant. |
| `ACT_Round3_StartNotification` | MISSING | none | R3 start-email; notification-sent columns never written. |
| `SUB_SendSubmitBidConfirmationEmail` (bidder + on-behalf admin templates) | MISSING | none in submit path | Buyers never get a "bids submitted" email. |

### SharePoint & cross-cutting Snowflake
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| SharePoint bid-file publishing · `ACT_SendBidstoSharepoint_perBuyerCode_Admin`, `SUB_AllBids_ExportExcel_PerBuyerCode`, zero-qty-cap file (`Auction_Overview`/`Auction_SubmittedBuyerCodes`), `PG_SharepointMethodConfiguration`, `Sharepoint_Overview`, `MicrosoftGraph_Overview` | MISSING | only a `sharepoint`-named flag on `model/buyermgmt/AuctionsFeatureConfig.java`; no writer/`DriveItem`/Graph client (grep-confirmed) | No SharePoint/MS-Graph integration at all — bid-file drops, zero-quantity-cap files, and SharePoint config pages are absent. |
| Round-config Snowflake pull · `Async_Snowflake_QualifiedBuyerCodes`/`_SelectionCriteria`/`_TargetPriceFactors`/`_TargetSync` | DIVERGENT | R2/R3 qualification computed locally (`R2BuyerAssignmentService`, `R3PreProcessService` CTEs) | Legacy pulled qualified codes/criteria from Snowflake via `JA_RetrieveQuery`/`JA_SnowflakeDML`; modern derives them in-DB. Deliberate re-architecture. |
| `SUB_SendBidDataToSnowflake` (per-buyer submitted bids push) | MISSING | none in submit path | Only round-close **rankings** (BUYER_BID) push exists — the per-buyer submitted-bid store-proc push at submit time is gone. |
| `ACT_SendWeekDataToSnowflake` (week reference push) + auction-status push | PARTIAL / IMPLEMENTED | `model/auctions/WeekSyncWatermark.java`; `snowflake/AuctionStatusSnowflakePushListener.java` + `AuctionSnowflakeResyncService.java` | Auction-status push + manual resync built; a standalone "push all weeks" action is thinner (watermark model present). |

### Admin data-management / CRUD / config / QA
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `Auction_Overview`/`Auction_Edit` · `ACT_Auction_UpdateByAdmin`, `SUB_Auction_DeleteByAdmin` | IMPLEMENTED | `AuctionController.java` + `AuctionListService.java`; `admin/auctions-data-center/auctions` | Auction list + admin edit/delete. |
| `BidData_Overview`/`_NewEdit` · `ACT_BidData_UpdateByAdmin`, admin delete (+Snowflake sync) | PARTIAL | `controller/admin/BidDataAdminController.java`; `admin/auctions-data-center/bid-data` | Grid present; per-row edit/delete-with-Snowflake-resync depth not verified. |
| `BidDataTotalQuantityConfig_*` · `MF_BidDataTotalQuantityConfig_Import`, `VAL_BidDataTotalQuantityConfig` | MISSING | none | Total-quantity threshold config (Excel full-replace import + EcoID/Grade uniqueness) not ported. |
| `HistoricalBidData_Overview`/`_Upload` · `HistoricalBidData_ImportExcel` | MISSING | none (grep `HistoricalBidData` → 0) | Historical bid archive import absent. |
| QA/purge tooling · `Auction_Overview_QA_Testing`/`ACT_PurgeBidData`, `BidData_Overview_QA_Testing`, `BidDataDeleteHelper_NewEdit`/`MF_CleanupUsingStoredProcedure`, `ACT_CleanupDataForAWeek`, `Sub_PerformRestoreActivity`/`ACT_ConfirmBackupRestoreActivity` | MISSING (mostly obsolete) | none | Destructive QA/reset utilities — intentionally not carried into a fresh schema; see "likely-dead". |
| `IdleTimeoutConfiguration` | MISSING | none | Session-idle config page; likely superseded by JWT/session-cleanup (`SessionCleanupTask`). |
| `PG_UserHelperGuide` / `UserHelperGuide_NewEdit` · `ACT_SaveDocument` | IMPLEMENTED | `controller/BuyerUserGuideController.java`; `auction-control-center/userguide-configuration` (V74) | |
| Admin control-center hubs · `Admin_ControlCenter`, `Business_Auctions_ControlCenter`, `AuctionControl_Tech_Overview`, `Admin_Auctions_Data`, `Admin_PWS_Data` | IMPLEMENTED (divergent) | Next.js route tree under `admin/auction-control-center` + `admin/auctions-data-center` | Nav hubs → route structure; behavior (fan-out links) preserved. |

### Auth / session (AuctionUI module, mostly Platform-capability)
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `Login_Custom_Web` · `ACT_Login_Client`, `ACT_Set_ShowLoginPassword`, remember-me | IMPLEMENTED | `controller/AuthController.java`; `(auth)/login` | @ecoatm.com → SSO branch; local password reveal. (Cross-listed under Platform.) |
| `Step1_ForgotPassword` (+ `GenerateActivationUIDAndURL`, `ACT_CreateAndSendEmail`) | IMPLEMENTED | `AuthController` + V75 `password_reset_tokens`; `(auth)/forgot-password` + `reset-password` | |
| `PG_UserAgreement` · `ACT_AcknowledgeClicked` (mandatory ToS gate) | MISSING | none (no agreement gate page found) | Logged-in acknowledgement gate + decline-signs-out flow not ported. |
| EcoATMDirectUser / Buyer / BuyerCode admin CRUD (`ACT_SaveNewUser`, `VAL_EcoAtmUser`, `DTA_Save_New_Buyer`, `ACT_CheckUnCommittedCodes`, Snowflake user sync) | IMPLEMENTED (defer) | `controller/DirectUserController.java`, `BuyerOverviewController.java` | In-scope of the **BuyersUsers** capability analysis; present here only because they live in the AuctionUI module. |

## Biggest gaps (named, with spec node ids)
1. **Device Allocation module — entirely missing** (`EcoATM_DA.PG_DeviceAllocation`, `SUB_LoadDAData`, `SUB_GetDADataFromExternalDB`, `SUB_DeviceBuyer_SetAwardedQty`, `SUB_TransferDADataToSharepoint`, `DeviceAllocation_Accept`/`_Reject`/`_Finalize`, batch `SCE_LoadDAData`). The whole Round-3 winner-award review/accept/finalize + external-DB pull + SharePoint publish domain has zero modern code (0 files on grep).
2. **All buyer auction email notifications — missing** (batch `Scheduled_event_Inventory_Notification`, `SUB_InvenNotificationProcessDateTime`, `SUB_InventoryNotificationEmailStart_*`/`Reminder_*`, `ACT_Round3_StartNotification`, `SUB_SendSubmitBidConfirmationEmail`). `SchedulingAuction` has the three `*_notification_sent` columns but nothing writes them.
3. **SharePoint / MS-Graph integration — missing** (`ACT_SendBidstoSharepoint_perBuyerCode_Admin`, `SUB_AllBids_ExportExcel_PerBuyerCode`, zero-quantity-cap file drop, `PG_SharepointMethodConfiguration`, `MicrosoftGraph_Overview`). Only a config flag survives; no bid-file publishing.
4. **Post-submit side-effects dropped from `ACT_SubmitBidData`** — per-buyer bid→Snowflake push (`SUB_SendBidDataToSnowflake`), Excel bid export, SharePoint file send, and confirmation email are all absent; modern `BidDataSubmissionService.submit` is a pure DB update.
5. **Round-3 buyer file exchange + per-buyer valid-bids export** (`PG_Round3DataFiles`, `ACT_DownloadRound3File`, `ACT_Round3_BidData_Import_ClientController_PreProcess`, `SUB_Round3_ExcelImport_PreProcess`, `ACT_DownloadRound3ValidBidsForBuyer`) — the offline R3 template download / bulk-publish / rank-annotated re-upload pipeline and per-buyer Excel export are not ported; only a read-only R3 report exists.

## Net-new modern behavior (not in legacy)
- **ShedLock-guarded distributed cron + Spring event bus** (`AuctionLifecycleScheduler`, `RoundStartedEvent`/`RoundClosedEvent`) replacing the single-node Mendix heartbeat microflow — enables safe multi-instance deploys.
- **Explicit recalc status columns + admin recovery endpoints** (V82–V85: `ranking_status`, `target_price_status`, `r2_init_status`, `r3_preprocess_status`, `r3_init_status` with error/started/finished timestamps; `RecalcAdminController`, `R2BuyerAssignmentAdminController`, `R3LifecycleAdminController`) — observable lifecycle with re-run buttons, absent in Mendix.
- **In-DB CTE qualification** (R2/R3 qualified-buyer-code computation local, replacing Snowflake `JA_RetrieveQuery` pull) with a `qualified_buyer_codes` flattened table (V72).
- **`AuctionSnowflakeResyncService`** — manual per-auction "re-sync closed rounds to Snowflake" endpoint + button (no direct legacy analog beyond the admin bulk-push).
- **Bid rate limiting + upload rate limiting** (`BidRateLimiter`, `UploadRateLimiter`) — infra hardening with no Mendix equivalent.

## Likely-dead / obsolete legacy (don't port — cite reachable=false or Disabled)
- **All 4 batches are `status: Disabled`** in the graph (`Scheduled_event_ScheduleAuctionStatus`, `SE_GetAggregatedInventory`, `Scheduled_event_Inventory_Notification`, `SCE_LoadDAData`). The lifecycle heartbeat was nonetheless re-implemented (prod-critical); the other three map to missing features (agg-inventory pull is on-demand instead).
- **QA/test-only utilities**: `Auction_Overview_QA_Testing` + `ACT_PurgeBidData`, `BidData_Overview_QA_Testing`, `BidDataDeleteHelper_NewEdit` (`MF_CleanupUsingStoredProcedure`, `ACT_ExecuteAdhocQuery`), `Sub_PerformRestoreActivity` / `ACT_ConfirmBackupRestoreActivity` (prod-DB copy-down reset, gated to qa/dev/localhost), `ACT_CreateNewUsers_TestEnvironment`, `PG_ErrorTestPage`, `PG_CarryForwardTest`, `BidData_XMLUpload`/`PG_BidData_XMLUpload` admin diagnostic import, `Test_GetBidDataDoc`. Destructive/diagnostic tooling tied to the Mendix runtime — deliberately not recreated in the fresh schema.
- **Buyer-code select-helper session plumbing** (`Buyer_Code_Select`, `Buyer_Code_Select_Search*`, tab/session helper objects) — Mendix session-object mechanics superseded by JWT + React state; no 1:1 port needed.
- **Legacy Round-3 email path** `SUB_InventoryNotificationEmailStart_Round_3_Legacy` (kept only for `LegacyRoundThree` configs) — superseded even in legacy; do not port.

---

# PWS (Premium Wholesale) — gap analysis

**Rollup:** Implemented 33 · Partial 6 · Missing 12 · Divergent 4 (of 55 assessed) · spec surface: 63 pages / 1 batch / 289 reachable flows

## State (2-3 sentences)
The buyer offer→counter→order lifecycle is the most faithfully ported surface in the whole app: cart CRUD, submit-offer, submit-order with the real Oracle Create-Order integration (token + POST + 3-way response branch), sales line-by-line review, buyer counter-response, inventory reservation, order history/details, pricing, MDM device/master-data CRUD, and the Deposco ATP inventory sync all have strong behavioral parity backed by real endpoints and JPA models. The clear gaps cluster in three places: (1) **Snowflake offer-status sync is stubbed** (`SUB_Offer_UpdateSnowflake` is a TODO — no per-offer upsert and no manual resync page), (2) **scheduled/automation flows are manual or absent** (the every-minute `SE_SetSLATag` batch is a manual admin button; counter-offer reminder emails have config but no sender job), and (3) **ops recovery levers are missing** (`Resubmit-to-Oracle`, bulk change-order-status, Deposco order-number/shipment lookup, and buyer Excel offer upload). A handful of legacy behaviors are deliberately divergent (concurrent-edit `EcoATM_Lock` → buyer-code ownership guard; Oracle toggle-off returns simulated success instead of a pending-order error).

## Entry points, screens & flows

### Shop / cart / browse
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWSOrder_PE` / `PWSOrder_PE_Dashboard` (page) — buyer store browse + grid search | IMPLEMENTED | `frontend/src/app/pws/order/page.tsx`, `pws/inventory/page.tsx`; `InventoryController` `GET /api/v1/inventory/devices` (`listActiveDevices`, `listFilteredDevices` itemType/excludeGrade/minAtpQty), `PwsInventoryService` | ATP-aware device browse + filter present. |
| `EcoATM_PWS.DS_GetOrCreateOrderItem` / `_CaseLot` (flow) — add/edit cart line, case-lot aware, CSS style class | IMPLEMENTED | `OfferController` `PUT /pws/offers/cart/items` → `OfferService.upsertCartItem` (case-lot size multiply, `caseLotRepository`) | Legacy `CAL_BuyerOfferItem_CSSStyle` red/orange price-vs-list styling is presentational; modern computes client-side. |
| `EcoATM_PWS.PWS_MyOffer` (page) — cart review | IMPLEMENTED | `pws/cart/page.tsx`; `OfferController` `GET /cart`, `DELETE /cart/items/{sku}` | — |
| `EcoATM_PWS.ACT_ResetOrder` / `PWS_ResetConfirmation` (flow/page) — discard cart edits | IMPLEMENTED | `OfferController` `DELETE /pws/offers/cart` → `OfferService.resetCart` | — |
| `EcoATM_PWS.PWS_DeviceView` (page) — device detail + build-offer / similar-SKU drawer | PARTIAL | browse/add wired via store + cart; no dedicated device-detail route located | Device detail drawer + "similar SKUs" offer-builder not found as a distinct screen (searched `pws/**`, no `device-view`/`[sku]` route). |
| `EcoATM_PWS.PWS_AlmostDone` (page) — final checkout confirmation popup | PARTIAL | folded into `POST /cart/submit` (`OfferService.submitCart`) | No standalone confirmation step; single submit call. Minor. |
| `EcoATM_PWS.BuyerOffer_Step1_SelectExcelFile` / `Step2_LoadExcelFile` (page) — buyer bulk **Excel offer upload** wizard (`NAN_Buyer_UploadOfferExcel`) | MISSING | not found — buyer offer endpoints are per-item only | `PricingController /devices/upload` is admin future-price CSV, not buyer offer upload. Confirmed-absent buyer bulk-offer ingest. |

### Offers / offer submission
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.ACT_Offer_SubmitOffer` (flow) — BuyerOffer→Offer, TotalPrice>0 filter, reserve, confirmation email | IMPLEMENTED | `OfferController` `POST /{offerId}/submit-offer` + `/cart/submit` → `OfferService.submitOffer` (Sales_Review drawer + `reserveDeviceQuantity`); `PwsOfferEmailEvent.OfferConfirmation` | Strong parity incl. TotalPrice>0 filter and post-commit email. |
| `EcoATM_PWS.ACT_Offer_SubmitOrder` (flow) — BuyerOffer→Offer+Order+Oracle, 3-way branch on ReturnCode | IMPLEMENTED | `OfferController` `POST /{offerId}/submit-order` → `OfferService.submitOrder` → `OracleOrderClient.submitOrder` → `handleOracleResponse` (no-response / `'00'` / other) | 3 outcome branches present (pending email / ordered+confirmation / failure). |
| `EcoATM_PWS.ACr_UpdateOfferID` (flow) — sequential zero-padded per-buyer-code Offer ID on save | IMPLEMENTED | `service/OfferNumberGenerator`; `V27__offer_id_sequence.sql` | — |

### Oracle order integration
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_Order_SendOrderToOracle` + `CWS_PostToken` + `EcoATM_PWSIntegration.CWS_PostCreateOrder` — token auth + POST create-order, `IsOracleCreateOrderAPIOn` toggle | IMPLEMENTED | `service/OracleOrderClient` (`fetchOracleToken` client_credentials, `postCreateOrder` Bearer POST to `config.createOrderPath`, parses camel+Pascal keys); `OracleConfigController` `POST /admin/oracle-config/test-auth`; `model/integration/OracleConfig`, `V14` | Real HTTP client, timeout, audit logs. **Divergent toggle-off** — see Divergent table. |
| `EcoATM_PWS.SUB_Offer_PrepareOraclePayload` (flow) — build OrderRequest/OrderLineItem JSON from accepted/countered-accept/finalize items | IMPLEMENTED | `OfferService.prepareOraclePayload` (accepted-item filter, case-lot qty) | — |
| `EcoATM_PWS.SUB_CreateOrderResponse_ManageResult` (flow) — process response, pending/adjusted/standard confirmation emails | IMPLEMENTED | `OfferService.handleOracleResponse` + `PWSEmailService` (`sendOrderConfirmationEmail`, `sendPendingOrderEmail`) | Adjusted-quantity email folded into pending email — see Divergent. |
| `EcoATM_PWS.ACT_Order_ReSubmitToOracle` (flow) + `Order_detail` "Resubmit to Oracle" (page) — manual retry of a failed order | MISSING | no `resubmit`/`re-submit` endpoint (searched all controllers/services) | Confirmed-absent ops recovery lever for stuck/failed Oracle orders. |

### Counter-offers (buyer side)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.NAV_PWSCounterOffers` / `SUB_NavigateToCounterOffers` (flow) — route single-vs-multiple pending, acquire lock | IMPLEMENTED | `CounterOfferController` `GET /pws/counter-offers` + `/{offerId}`; `pws/counter-offers/(page|[offerId])` | Routing present. Lock behavior **divergent** (see below). |
| `EcoATM_PWS.ACT_Offer_BuyerAcceptAllCounters` (flow) — bulk accept counters, case-lot aware | IMPLEMENTED | `POST /{offerId}/accept-all` → `CounterOfferService.acceptAllCounters` (`caseLotRepository`) | — |
| `EcoATM_PWS.ACT_Offer_BuyerSubmitCounterResponse` (flow) — accept/decline → if `Ordered` create Order + send to Oracle + Snowflake | IMPLEMENTED | `POST /{offerId}/submit` → `CounterOfferService.submitCounterResponse` → `offerService.submitOrder` when ordering; else all-declined path | Order placement present; Snowflake sync stubbed (see Snowflake). |
| `EcoATM_PWS.VAL_Offer_IsCounterOfferReadyForSubmit` (flow) — gate submit until all counters answered | IMPLEMENTED | `submitCounterResponse` guard: "All countered SKUs must be either Accepted or Rejected" | — |
| `EcoATM_PWS.ACT_Offer_EditCounterOfferByBuyer` (flow) — reopen submitted counter, re-split by grade/case-lot, lock bounce | PARTIAL | `PUT /{offerId}/items/{itemId}/action` (`setBuyerItemAction`) | Per-item accept/reject present; the grade / `A_YYY` / case-lot bucket re-split and the read-only lock bounce are not reproduced. |

### Offer review (sales / admin)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWSOffers` / `PWSOffer_OfferItems` / `PWS_OfferItemView` (page) — central grid + line-by-line accept/decline/counter/finalize | IMPLEMENTED | `OfferReviewController` (summary, list, paged, `GET /{offerId}`, `PUT items/{itemId}/action`, `.../counter`, accept-all, decline-all, finalize-all, complete-review); `pws/offer-review/(page|[offerId])` | Full review workspace. |
| `EcoATM_PWS.ACT_Offer_SalesFinalizeAll` + `VAL_Offer_Finalize` (flow) — finalize whole offer, guard mid-negotiation items | IMPLEMENTED | `POST /{offerId}/finalize-all`; `OfferReviewService.finalizeAll` | — |
| `EcoATM_PWS.SUB_CalculateCounterOfferSummary` + `SUB_Offer_DefineFinalOfferStatus` (flow) — counter summary totals; Ordered-vs-Declined terminal status | IMPLEMENTED | `OfferReviewService.completeReview` (counter-item → `Buyer_Acceptance` + summary totals; none → `offerService.submitOrder`); over-ATP guard on complete | Matches "any accept → Ordered else Declined" intent. |
| `EcoATM_PWS.ACT_UpdateOfferMasterHelper_HasItems` / `ACT_ChangeOfferStatus` (flow) — per-status tab counts / highlight | IMPLEMENTED | `OfferReviewController` `GET /summary` (`getStatusSummaries`), `GET /counts` | Status-bucket counters present. |
| `EcoATM_PWS.SUB_UpdateOfferDrawerStatus` (flow) — central status state-machine: reserve qty per Device/CaseLot **and** Snowflake sync on every status change | PARTIAL | drawer statuses set in `submitOffer`/`completeReview`; `reserveDeviceQuantity` present | Inventory reservation present; the **Snowflake push half is stubbed** (see Snowflake row). |

### Inventory / devices / reservation
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_ReserveQuantityForDevice` / `ForCaseLot` + `SUB_UpdateReservedQuanityPerDevice` (flow) — reserved = min(ordered sum, avail); atp = avail − reserved | IMPLEMENTED | `OfferService.reserveDeviceQuantity`; `AtpSyncService.updateReservedQuantities` (`sumReservedQtyByDeviceId`) | Formula matches legacy. |
| `EcoATM_PWSMDM.Device_Overview` / `Device_Edit` (page) | IMPLEMENTED | `admin/pws-data-center/devices`; `InventoryController /devices`, `PricingController /pricing/devices`; `model/mdm/Device`, `V13` | — |
| `EcoATM_PWSMDM.{Brand,Capacity,Carrier,Category,Color,Model,Grade,Note,CaseLot}_Overview/NewEdit` (page) — master-data CRUD + JSON audit-on-save | IMPLEMENTED | `admin/pws-data-center/master-data` (tabbed) → `AdminMasterDataController /admin/master-data/{type}`; `V13`, `V31` case_lots; audit → `V56 pws_admin_audit` | Consolidated 8 grids → 1 tabbed screen (per `docs/tasks/pws-data-center-port.md`); JSON-warning-log audit → audit table (divergent-minor, intentional). |
| `EcoATM_PWS.PropertiesUtility_Update` (page) — bulk device-property merge/mass-edit tool | MISSING | not found (searched controllers/services) | Likely-obsolete admin power tool; see Likely-dead. |

### Orders / history / status
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWS_OrderHistory` (page) + `DS_GetOrCreateOrderHistoryHelper` + `SUB_CalculateOrderHistoryTabTotals` — 4 tabs (All/Recent/In-Process/Complete) | IMPLEMENTED | `OrderHistoryController` `GET /pws/orders` + `/counts`; `OrderHistoryService`; `model/pws/OrderHistoryView`, `V37/V40 order_history_view`; `pws/orders/page.tsx` | Tab counts present. |
| `EcoATM_PWS.PWS_OrderDetails` (page) — by-SKU / by-device toggle | IMPLEMENTED | `GET /{offerId}/details/by-sku` + `/by-device`; `pws/orders/[id]`; `V38 order_detail_columns` | — |
| `EcoATM_PWS.Order_Overview` / `Offer_Overview` (page) — admin order/offer grids | IMPLEMENTED | `admin/pws-data-center/offers/page.tsx` | Consolidates Offers/OfferItems/Orders/OfferID/BuyerOffers per port doc. |
| `EcoATM_PWS.OrderStatus_Overview` / `OrderStatus_NewEdit` (page) — status reference CRUD | IMPLEMENTED | `settings/pws-control-center/order-status`; `PWSAdminController` `/admin/order-status` (list/create/update/delete) | — |
| `EcoATM_PWS.ChangeOrderStatus_Select` + `ACT_ChangeOfferStatus_Proceed` + `VAL_ChargeOfferStatusHelper_IsValid` (page/flow) — **bulk order/offer status migration** (date-range or selected orders, from/to safety guard) | MISSING | no `change-status`/bulk-status endpoint (searched) | Confirmed-absent. Legacy tool for correcting bad Oracle syncs in bulk. |
| `EcoATM_PWS.ManageFileDocument_SelectOrderStatusFile` (page) — import OrderStatus lookup from Oracle spreadsheet | MISSING | order-status CRUD is manual only | Minor; file-import path absent. |
| `EcoATM_PWS.PWS_TrackOrder` (page) — open external Oracle tracking link | PARTIAL | order detail present; no explicit external-tracking-link action located | Minor; may be embedded in order detail. |

### Pricing / future price
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWS_Pricing` (page) — review device pricing + inventory, grid search | IMPLEMENTED | `PricingController` `GET /pws/pricing/devices` (paged); `pws/pricing/page.tsx` | — |
| `EcoATM_PWS.Page2_UploadData` (page) + `EcoATM_PWSMDM.JA_UpdateDevicePrices` — future-price Excel upload + scheduled price update + config | IMPLEMENTED | `PricingController` `POST /devices/upload`, `PUT /devices/{id}`, `/devices/bulk`; `FuturePriceConfigController` `GET/PUT /pws/pricing/config`; `V36 future_price_config` | — |
| `EcoATM_PWSMDM.PriceHistory_Overview` / `NewEdit` (page) | IMPLEMENTED (read) | `GET /pws/pricing/devices/{id}/history` | Read path present; dedicated edit form not separately verified (low value). |

### Admin / control-center / integration config
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWSIntegration.DeposcoConfig_Overview` / `PWSConfiguration_Edit` (page) — Deposco config + test connection | IMPLEMENTED | `settings/pws-control-center/deposco`; `OracleConfigController /admin/oracle-config` (+`test-auth`); `V14 integration`, `V24 config` | Oracle test-auth present. Deposco-specific ping — see Partial below. |
| `EcoATM_PWSIntegration.ACT_TestDeposcoAPI` (flow) — Deposco credential/reachability ping | PARTIAL | Oracle `test-auth` exists; no Deposco `TestString` GET located | `ACT_TestDeposcoAPI` (GET `DeposcoConfig/TestString`) not reproduced as its own endpoint. |
| `EcoATM_PWS.PWSResponseConfig_Overview` (page) — error-code → friendly message map | IMPLEMENTED | `settings/pws-control-center/error-messages`; `PWSAdminController /admin/error-messages` (CRUD) | — |
| `EcoATM_PWS.PWSConstants_Overview` (page) — global config incl. reminder-hour thresholds, SLA days | IMPLEMENTED (storage) | `settings/pws-control-center/pws-constants`; `PWSAdminController` `/admin/pws-constants` (`sla_days`, `send_first/second_reminder`, `hours_first/second_counter_reminder`) | Config persists; **reminder sender job missing** (see Missing). |
| `EcoATM_PWS.MaintenanceMode_NewEdit` (page) | IMPLEMENTED | `settings/pws-control-center/maintenance-mode`; `PWSAdminController /admin/maintenance-mode` | — |
| `EcoATM_PWSIntegration.ManageFileDocument_ChooseFile` (page) — import error-mapping JSON | MISSING | error-messages CRUD manual only | Minor; file-import absent. |
| `EcoATM_PWS.PWSUserPersonalization_Overview` (page) — user personalization/idle-timeout | MISSING | not located | Minor; couldn't-locate. |
| `EcoATM_PWSIntegration.SUB_Oracle_Configuration` / `SUB_Oracle_ErrorMessage` (flow) — **inbound** Oracle push replacing PWSConfiguration / error table | MISSING | config edited via admin UI, no inbound XML-import endpoint | Likely-obsolete inbound integration (Oracle→Mendix XML push). |

### Deposco inventory sync (integration)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_LoadPWSInventory_Deposco` + `SUB_FetchItemsFromDeposco` + `SUB_LoadPWSInventory_Task_Deposco` — paged Deposco inventory pull, match Device by SKU, update AvailableQty, delta-warn, recalc reserved/facets | IMPLEMENTED | `AtpSyncService.fullInventorySync` → `fetchAllDeposcoInventory` (token + paged `/inventory` GET) → `applyAtpUpdates` (SKU match, availableQty/atpQty update, lastSyncTime) → `updateReservedQuantities`; `AtpSyncController` `POST /inventory/sync/full`, `/sync/simulate`, `GET /sync/logs`; `shipments/page.tsx` | Real HTTP paged fetch. Manual-trigger (legacy `ACT_FullInventorySync` was also button-triggered per port doc). |
| `EcoATM_PWSIntegration.ACT_GenerateDeposcoV2Token` / `SUB_GenerateDeposcoPassword` (flow) — Deposco OAuth token / Basic-auth header | IMPLEMENTED | `AtpSyncService.obtainDeposcoToken` (POST `/auth/token`) | Token caching (legacy cached AccessToken row) not reproduced — re-auths per sync; low risk. |
| `EcoATM_PWSIntegration.SUB_FetchDeposcoOrderNumber` (flow) — per-order Deposco order-number lookup (precursor to shipment history) | MISSING | shipments page uses sync logs; no Deposco order-search call | Confirmed-absent per-order Deposco order/shipment lookup. |

### Snowflake sync (integration)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_Offer_UpdateSnowflake` (flow) — serialize Offer → `JA_SnowflakeStoreProc` upsert after every accept/counter/decline/order | MISSING (stubbed) | `OfferService.java:671-672` `// SUB_Offer_UpdateSnowflake (stubbed) TODO: Sync offer data to Snowflake analytics` | Confirmed stub. Legacy is fire-and-log (non-error-propagating), so low functional risk, but analytics warehouse never receives PWS offer state. (`AggregatedInventorySnowflakeSyncService` exists but is auctions-inventory, a different capability.) |
| `EcoATM_PWS.ACT_Offers_UpdateOfferStatusSnowflake` / `Offer_UpdateSnowflake` (flow/page) — admin manual date-range Snowflake resync | MISSING | no resync endpoint/page | Follows from the stub above. |

### Emails
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `SUB_SendPWSOfferConfirmationEmail` / `OrderConfirmationEmail` / `PendingOrderEmail` / `CounterOfferEmail` (flow) | IMPLEMENTED | `PWSEmailService` (4 methods) + `PwsOfferEmailListener` (`@TransactionalEventListener AFTER_COMMIT`) + `PwsOfferEmailEvent`; counter email fired from `OfferReviewService.completeReview:477` | 4 buyer emails wired via post-commit events. |
| `EcoATM_PWS.ACT_SendCounterOfferReminderEmails` / `SUB_SendCounterOfferReminderEmail` (flow) — first/second reminder cadence from `PWSConstants` thresholds, one-shot flags | MISSING | thresholds stored (`pws_constants.hours_first/second_counter_reminder`) but no scheduled sender; no `@Scheduled` PWS job (only Auth/Upload/Bid rate-limiters, AuctionLifecycle, ReserveBidSync, SessionCleanup) | Confirmed-absent automation — config exists but nothing sends the nag emails. |
| `SUB_SendPWSAdjustedQuantityOrderConfirmationEmail` (flow) — distinct adjusted-qty confirmation | DIVERGENT | mapped to `sendPendingOrderEmail` (per `PWSEmailService` javadoc) | Distinct adjusted-qty template collapsed into pending-order email. |

### Batch
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `batch:SE_SetSLATag` — **every-minute** job tagging `Sales_Review`/`Buyer_Acceptance` offers past SLA cutoff | DIVERGENT | `PWSAdminController` `POST setSLATags` / `removeSLATags` (manual admin buttons, jdbc `UPDATE`); no `@Scheduled` | Behavior exists but as a manual lever, not an automatic 1-min cron — overdue offers are not auto-flagged. |

### RMA-adjacent (separate capability, noted for completeness)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| RMA surface (RMA is its own capability; **no RMA pages in the PWS 63**) | (net-new here) | `RmaController /api/v1/pws/rma` (submit/summary/reasons/template/detail/approve-all/decline-all/item-status/complete-review); `pws/rma-requests`, `pws/rma-review`, `admin/pws-data-center/rma`, `settings/.../rma-status`+`rma-template`; `V33 rma_tables`, `V34 data_rma` | Full modern RMA surface exists under the PWS route tree — assess against the **RMA** capability partial, not counted in PWS totals. |

## Biggest gaps (named, with spec node ids)
1. **Snowflake offer-status sync stubbed** — `EcoATM_PWS.SUB_Offer_UpdateSnowflake` is a literal TODO in `OfferService`; the entire per-offer warehouse upsert and the admin manual resync (`ACT_Offers_UpdateOfferStatusSnowflake` / `Offer_UpdateSnowflake` page) are absent. PWS offer/order state never reaches Snowflake analytics.
2. **Counter-offer reminder automation missing** — `EcoATM_PWS.ACT_SendCounterOfferReminderEmails` / `SUB_SendCounterOfferReminderEmail`: thresholds are stored in `pws_constants` and editable in the UI, but there is no scheduled job that sends first/second reminder emails, so idle counter-offers are never nudged.
3. **`Resubmit to Oracle` recovery lever missing** — `EcoATM_PWS.ACT_Order_ReSubmitToOracle` (+ `Order_detail` action): no endpoint to re-send a failed/pending Oracle order. Ops cannot retry stuck orders from the modern app.
4. **Bulk change-order/offer-status tool missing** — `EcoATM_PWS.ChangeOrderStatus_Select` / `ACT_ChangeOfferStatus_Proceed` / `VAL_ChargeOfferStatusHelper_IsValid`: the date-range/selected-orders status-migration tool (with the from-status safety guard) used to correct bad Oracle syncs has no modern equivalent.
5. **SLA tagging is manual, not the 1-minute batch** — `batch:SE_SetSLATag`: modern exposes `setSLATags`/`removeSLATags` as admin buttons but ships no scheduler, so offers sitting too long in review/acceptance are not automatically flagged for sales.

Secondary: buyer **Excel offer upload** wizard (`BuyerOffer_Step1/Step2`), **Deposco per-order lookup** (`SUB_FetchDeposcoOrderNumber`), and the **device detail / build-offer drawer** (`PWS_DeviceView`) are absent/partial.

## Net-new modern behavior (not in legacy)
- **Full RMA module** under `/api/v1/pws/rma` + 4 frontend routes + `V33/V34` (its own capability; legacy PWS had no RMA pages in these 63).
- **Consolidated admin surface** — 19 legacy PWS-Data-Center datagrids collapsed to 5 screens (`docs/tasks/pws-data-center-port.md`); master-data 8 grids → 1 tabbed screen.
- **Audited soft-delete + reason** on device/master-data deletes (`V56 pws_admin_audit`) replacing legacy raw JSON-warning-log deletes; string filters default to `contains` not `=`.
- **Async sync with status drawer + `sync/logs`** endpoint and a `/sync/simulate` dev path for Deposco ATP (legacy exposed long-running batch as a bare button).
- **Feature-flag / error-message / ranks-config admin CRUD** surfaced as first-class settings pages.

## Likely-dead / obsolete legacy (don't port)
- `EcoATM_PWSIntegration.SUB_Oracle_Configuration` / `SUB_Oracle_ErrorMessage` — inbound Oracle→Mendix XML config/error-table push; modern edits config via admin UI, so the inbound replace-singleton endpoints are obsolete.
- `EcoATM_PWS.PropertiesUtility_Update` — bulk device-property merge/mass-edit power tool; not ported, likely superseded by audited master-data CRUD.
- `EcoATM_PWS.DS_GetEcoATMCounterOffers` / `DS_GetFinalOfferForCounterOffers` / `DS_GetOriginalOfferForCounterOffers` — the graph itself flags these as unfinished **stub** data sources (return a blank `OffersUiHelper`); do not port as-is — confirm intent with product.
- `*_Test` Deposco flows (`SUB_FetchItemsFromDeposco_Test`, `SUB_LoadPWSInventory_Task_Deposco_Test`) — sandbox validation harnesses; modern `/sync/simulate` covers the equivalent.
- `EcoATM_PWSMDM.Grade_Overview.ACT_CloneCaseLotDisplayNames` — one-off maintenance utility.

## Divergent behaviors (behavior differs, not just naming)
1. **Oracle toggle-off path** — legacy `SUB_Order_SendOrderToOracle`: API disabled → returns a *generic error* `OracleResponse` (routes to Pending_Order). Modern `OracleOrderClient.submitOrder`: config inactive/missing → returns a *simulated success* (`returnCode="00"`, `orderNumber="SIM-…"`) routing to **Ordered**. Different terminal offer status when the integration is off.
2. **Concurrent-edit lock** — legacy `EcoATM_Lock.JA_ExtractObjectInfo` soft-lock (another user holds it → bounce to read-only review page) replaced by `security/PwsOwnershipGuard` buyer-code **ownership** authorization. No concurrent-edit contention handling / read-only bounce.
3. **Adjusted-quantity confirmation email** — collapsed into the pending-order email (`PWSEmailService` javadoc), losing the distinct legacy template branch in `SUB_CreateOrderResponse_ManageResult`.
4. **Master-data delete audit** — legacy writes a JSON snapshot to the warning log; modern soft-deletes into a dedicated audit table with actor + reason (intentional, per port doc anti-pattern #2).

---

# BuyersUsers — gap analysis

**Rollup:** Implemented 12 · Partial 5 · Missing 13 · Divergent 2 (of 32 assessed) · spec surface: 25 pages / 1 batch / 81 reachable flows

## State (2-3 sentences)
Core buyer/company + buyer-code CRUD, buyer↔user + role associations, per-save and bulk Snowflake buyer sync, external local login, forgot/reset password, and the bidder dashboard (bid entry, submit, carryover, export/import) are ported and behaviorally close. The largest holes are on the *administrative periphery*: Sales Representative management is read-only (no create/edit/delete/sync), the manual buyer-code qualification override persists the toggle but omits every legacy side-effect (round-status guard, bid-data seeding, Snowflake override sync, notification email), and the user-lifecycle tail (account activation, public self-service signup, user/login→Snowflake sync, grantable-role gating, compliance change-audit) is absent. Internal `@ecoatm.com` SSO is an explicit hardcoded stub.

## Entry points, screens & flows
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_UserManagement.Login_New` + `ACT_Login_ExternalUser` / `VAL_Login` (flow) | IMPLEMENTED | `controller/AuthController.loginExternalUser` (`POST /api/v1/auth/login`), `service/AuthService.authenticateLocalUser`; `(auth)/login/LoginForm.tsx`; JWT cookie + `AuthRateLimiter` | External local-auth + email-domain branch present; adds rate-limit + HttpOnly cookie. |
| `ACT_Login_InternalUser` (flow) · internal SSO redirect | DIVERGENT | `AuthController.handleSSORedirect` (`GET /api/v1/auth/sso`) | Hardcoded `login.microsoftonline.com/...-stub`; TODO(Theme 3) real SAML. Internal users cannot actually SSO-authenticate yet. |
| `Login_ForgotPassword` + `ACT_SendPasswordResetEmail` (flow) | PARTIAL | `AuthController.forgotPassword`, `service/PasswordResetService.requestReset` | Enumeration-safe always-200 matches legacy generic-confirmation intent, but email delivery is *logged only* (TODO email-infra) — no reset email actually sent. |
| `Login_ResetPassword` + `ACT_SubmitNewPassword` (flow) | IMPLEMENTED | `AuthController.resetPassword`, `PasswordResetService.confirmReset`; `(auth)/reset-password` | Token validate + password update + generic failure. |
| `Login_ActivateUser` / `Activate_User` + `ACT_ActivateNewUser` (flow) · account activation | MISSING | not found (searched `AuthController`, `service/*Activat*`, migrations) — no emailed-link activation endpoint; only admin-create + reset-password exist | Legacy: set initial password, delete activation token, sync user to Snowflake, land on Activated page. Confirmed absent as a distinct flow. |
| `Step1_SignupEnterInfo` + `MB_Step2_SendConfirmationEmail` · public self-service signup | MISSING | not found — users are Administrator-created only via `DirectUserController.createDirectUser` | No public signup endpoint (confirmed-absent). |
| `SUB_SendUserToSnowflake` / `SUB_SendUserLoginToSnowflake` / `SUB_SendBuyerUserToSnowflake` (flow) | MISSING | not found — only `service/snowflake/BuyerSnowflakeSyncService` (buyers) exists; no user/login→Snowflake | User master + login-event warehouse sync not ported. |
| `EcoATM_MDM.Buyer_New` + `ACT_Buyer_NewSave` (flow) | IMPLEMENTED | `BuyerOverviewController.create` (`POST /api/v1/admin/buyers`), `service/BuyerEditService.create` | Admin-gated; publishes `BuyerSnowflakeEvent.BuyerSaved`. |
| `Buyer_Edit` + `ACT_Buyer_EditSave_Admin` / `VAL_CheckIfBuyerIsDisabled` (flow) | IMPLEMENTED | `BuyerEditService.update` + `validateDisable` (counts active `user_mgmt.user_buyers`) → `BuyerDisableException` (409) | Disable-blocked-by-active-users guard present. |
| `ACT_Buyer_EditSave_Compliance` + `SUB_LogBuyerCodeTypeChange_Compliance` (flow) | PARTIAL | `BuyerEditService` (`BuyerPermissions.forAdmin/forCompliance`; type change gated `if (admin && ...)`) | Admin/compliance permission split exists, but **no `BuyerCodeChangeLog` audit row** is written on `BuyerCodeType` change — compliance audit trail missing. |
| `VAL_Buyer` / `VAL_BuyerCode` (flow) · company-name + code global uniqueness | PARTIAL | `repository/BuyerCodeRepository.existsByCodeIgnoreCaseAndNotSoftDeleted` (surfaces a `duplicate` flag in `BuyerCodeDetail`) | Uniqueness is *displayed* but `create/update` do not reject on duplicate company name or code — legacy hard-blocks the save. |
| `ACT_BuyerCode_Create` / `ACT_SoftDelete_BuyerCode` / `NF_ValidateBuyerCodeBudget` (flow) | IMPLEMENTED | `BuyerEditService.updateBuyerCodes` (add + `softDelete=true`), typed `BigDecimal budget` | Soft-delete (not hard) preserved; numeric budget via typed field. |
| `SUB_Buyer_Save` + `SUB_SendBuyerToSnowflake` (flow) · per-save sync | IMPLEMENTED | event `BuyerSnowflakeEvent.BuyerSaved` → `BuyerSnowflakeSyncService` | Save choke-point + downstream sync present. |
| `ACT_SendAllBuyerstoSnowflake` + `SUB_SendAllBuyersToSnowflake` (flow) · bulk sync | IMPLEMENTED | `BuyerOverviewController.syncAllToSnowflake` (`POST /snowflake-sync`) → `BuyerSnowflakeEvent.AllBuyersSync` | Admin-gated bulk re-sync. |
| `ACT_Buyer_CreateNewFromUser` / `ACT_Buyer_SaveBuyerFromUser` (flow) | MISSING | not found — buyer and user creation are separate surfaces | "Create Buyer for this user" wiring + auto user↔buyer link absent. |
| `QualifiedBuyerCodes_Overview` + `NF_OnIncludedChanged` (flow) · manual qualify persist | IMPLEMENTED | `controller/admin/QualifiedBuyerCodeAdminController.updateIncluded` (`PATCH /api/v1/admin/qualified-buyer-codes/{id}`), `service/admin/QualifiedBuyerCodeAdminService` | Persists `included`, forces `qualification_type=Manual`, writes audit row. |
| `NF_OnIncludedChanged_New` / `_Legacy` (flow) · side-effects | MISSING / DIVERGENT | `QualifiedBuyerCodeAdminService.updateIncluded` (persist + audit only) | Omits: "Round cannot be modified if Closed"/R3-started guard, `SUB_CreateBidDataForAllAE` seeding on include, `SUB_Snowflake_BuyerQuaificationOverride`, and `SUB_SendManualQualificationEmail` (mid-round notify). |
| `QualifiedBuyerCodes_Edit` + `ACT_OverrideQualificationBuyerCodes` (flow) | IMPLEMENTED | same PATCH endpoint (covers the override-persist behavior) | Direct commit of admin override — covered. |
| `SalesRepresentative_NewEdit` + `Act_SaveSaleRep` (flow) | MISSING | not found — only `BuyerOverviewController.listSalesReps` (`GET .../sales-representatives`, read-only) | No create/edit, no case-insensitive duplicate-name guard, no Snowflake sales-rep push. |
| `SalesRepresentative_Overview` + `ACT_DeleteSalesRep` (flow) · guarded delete | MISSING | not found | No delete endpoint; legacy blocks delete when Offers reference the rep. |
| `ACT_FixAllSalesRepNames` (flow) · backfill repair | MISSING | not found | One-off data-repair not ported (likely-dead — see below). |
| `NAN_SalesRepresentative_SynchronizeToSnowflake` + `SUB_SendAllSalesRepresentativeToSnowflake` (flow) | MISSING | not found | Manual sales-rep→Snowflake re-sync absent. |
| `PG_MasterDataInventory` + `ACT_LoadMasterDataInventory` / `SCH_UpdateMasterDeviceInventory` (batch `SCH_UpdateDeviceInventory`) | MISSING (unconfirmed) | `controller/AdminMasterDataController`, `AtpSyncController`/`AtpSyncService` exist; no `MasterDeviceInventory`/`JA_SnowflakeToMendix` incremental device-catalog sync found | mdm.device seeded once via V21; live incremental Snowflake→app device sync + manual "refresh now" not evidenced. |
| `CompanyHoliday_Overview` (+ `CompanyHoliday_NewEdit`) (page) | MISSING | not found (searched `CompanyHoliday`) | Company-holiday master data not ported (confirmed-absent). |
| `PG_CreateTestUsers` + `ACT_CreateTestUsersStart` (flow) · bulk QA test users | MISSING | not found (searched `TestUser`) | QA disposable-Bidder bulk-create not ported (likely-dead). |
| `PG_Bidder_Dashboard_DG2` + `OCH_ValidateAndSaveBidData` (flow) · bid entry + no-regression autosave | IMPLEMENTED | `controller/BidderDashboardController` (`GET /dashboard`, `PUT /bid-data/{id}`, `POST /bid-rounds/{id}/submit`) | Bid entry/save/submit present (overlaps Auctions capability). |
| `ACT_BidDataDoc_ExportExcel` + `_SubmittedBidSheet_Round1/2/3` / `SUB_BidDataCustomExcelExport` (flow) | PARTIAL | `BidderDashboardController` `GET /bid-rounds/{id}/export`, `GET /download-round-1` | Base export + Round-1 download exist; per-round rank templates (`BidDataRankRound2/3Export`) and on-demand Round-3 generate not clearly replicated. |
| `Nanoflow` (generate bid data) + `SUB_CreateBidDataForAllAE` (flow) | IMPLEMENTED | `service/auctions/r1init/Round1InitializationService`, `service/auctions/r2init/R2BuyerAssignmentService`, `repository/auctions/BidDataForAllAERepository` | Bid-data seeding per AE ported in the auctions init services. |
| `BidDownloadOnSubmit` / `BidDownloadOnBuyerCodeSelect` + `ACT_GetSubmittedBidRounds` (flow) | PARTIAL | `BidderDashboardController` export/download; `dto/BidderDashboardResponse` | Post-submit redownload partially covered by `/export`; per-round submitted-sheet routing helper not fully replicated. |
| `PG_BidDataTotalQuantityConfig_Upload` + `MF_BidDataTotalQuantityConfig_Import` (destructive full-replace) | MISSING | not found (searched `BidDataTotalQuantity`) | Per-buyer total-quantity threshold Excel re-import not ported. |
| `PG_BuyerSubmitConfig` + `ACT_SaveAuctionConfiguration` / `Act_GetOrCreateBuyerCodeSubmitConfig` (flow) | IMPLEMENTED | `model/buyermgmt/AuctionsFeatureConfig`, `service/buyermgmt/AuctionsFeatureConfigService`, `PWSAdminController` feature-flags; `settings/pws-control-center/feature-flags` | Feature-config read/save present. |
| grantable-roles enforcement (V16 `identity.grantable_roles` seed) | MISSING | table seeded (V2 DDL + V16 data) but **zero Java references** (`grep grantable` → none); `DirectUserController` gates on Administrator-only | Legacy grantor→grantee role-assignment restriction not enforced; any role can be granted by an Administrator. |

## Biggest gaps (named, with spec node ids)
1. **Sales Representative management is read-only.** `SalesRepresentative_NewEdit`/`Act_SaveSaleRep`, `SalesRepresentative_Overview`/`ACT_DeleteSalesRep` (Offer-referenced guard), and `NAN_SalesRepresentative_SynchronizeToSnowflake`/`SUB_SendAllSalesRepresentativeToSnowflake` are absent — only `GET /api/v1/admin/buyers/sales-representatives` exists. No create/edit/duplicate-guard/delete/Snowflake push.
2. **Manual qualification override drops all side-effects.** `NF_OnIncludedChanged_New`/`NF_OnIncludedChanged_Legacy`: modern `QualifiedBuyerCodeAdminService.updateIncluded` persists `included`+audit but omits the round-status modify guard, `SUB_CreateBidDataForAllAE` seeding, `SUB_Snowflake_BuyerQuaificationOverride`, and `SUB_SendManualQualificationEmail`.
3. **User lifecycle tail + governance.** Account activation (`ACT_ActivateNewUser` / `Login_ActivateUser`), public signup (`Step1_SignupEnterInfo`), user/login→Snowflake sync (`SUB_SendUserToSnowflake` / `SUB_SendUserLoginToSnowflake`), grantable-roles gating, and compliance `SUB_LogBuyerCodeTypeChange_Compliance` (BuyerCodeChangeLog) are all missing.

## Net-new modern behavior (not in legacy)
- **Direct-user admin CRUD** (`DirectUserController` `/api/v1/users/direct-users` + `DirectUserService`): filtered listing, detail, create/update with role-assignment + `user_buyers` association replacement, roles/buyers lookups — a consolidated user-admin surface not represented as a page node in this capability slice.
- **Security hardening:** JWT HttpOnly `Strict` cookie + `AuthRateLimiter` on login/forgot/reset; `DirectUserController` Administrator-only class gate (documented privilege-escalation guard); open-redirect guard on the future `/sso` `target`.
- **Qualified-buyer-code audit table** (`QualifiedBuyerCodeAudit`) capturing old/new included + qualification-type per manual change (richer than the legacy inline override).

## Likely-dead / obsolete legacy (don't port)
- `PG_CreateTestUsers` / `ACT_CreateTestUsers(Start)` — QA-only disposable Bidder generator; superseded by Flyway `V15__seed_dev_roles_and_users` seed accounts.
- `ACT_FixAllSalesRepNames` — one-off historical data-repair/backfill after a migration; not a recurring behavior.
- `PG_Bidder_Dashboard_HOT_Deprecated` — explicitly `_Deprecated` in the spec (superseded by `PG_Bidder_Dashboard_DG2`).
- `Act_GetOrCreateBuyerCodeSubmitConfig` `LegacyBidDataCreation` / `LegacyManualQualification` flags — legacy/optimized dual-path toggles that exist only to bridge the Mendix migration; the modern app has a single path.

---

# RMA — gap analysis

**Rollup:** Implemented 11 · Partial 5 · Missing 11 · Divergent 2 (of 29 assessed) · spec surface: 10 pages / 0 batches / 52 reachable flows

## State (2-3 sentences)
The RMA *read + review skeleton* is ported: buyers submit via file upload, sales review per-item / bulk approve-decline / complete-review, and status-grouped overview lists + summary KPI cards render (with buyer-code ownership scoping added as a security improvement). But the two integration-heavy pillars are entirely absent: **Oracle** order creation/resubmit on approval and **Deposco** status polling — the `Rma` entity even carries `oracle_*` columns that are never written. The buyer-facing emails, Snowflake sync, templated Excel exports, return-label PDF, downloadable invalid-IMEI report, and (critically) the submit-time OfferItem/device-ownership validation are all missing, so `deviceId`/SKU/sales-total roll-ups never populate.

## Entry points, screens & flows
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_RMA.ACT_RequestRMA` + `DS_CreateRMAFile` (flow) · start draft | IMPLEMENTED | `controller/RmaController.submitRma` creates the `Rma` at commit time; `pws/rma-requests` UI | Draft-then-fill collapsed into a single CSV submit (no separate uncommitted-draft step). |
| `RequestRMA` + `ACT_SubmitRMAFile` / `SUB_ProcessFileUpload` (flow) · file-upload submit | IMPLEMENTED · DIVERGENT | `RmaController` `POST /api/v1/pws/rma/submit` (multipart), `service/RmaService.submitRmaRequest` + `parseCsv` | Modern accepts **CSV** (`text/csv`/`plain`/`ms-excel`), legacy parsed a structured **Excel** template via `ImportExcelData`. Adds size cap + rate-limit + buyer-code ownership check. |
| `VAL_RMARequestFile` (flow) · authoritative device gate | PARTIAL | `RmaService.submitRmaRequest` (reason-in-active-set + dup-IMEI in-file + `RmaItemRepository.findDuplicateImeis`) | **Missing the OfferItem ownership check** — legacy requires each IMEI to match an `EcoATM_PWS.OfferItem` for the buyer code (device must belong to a real prior offer/order) and pulls `Device`/sale price. Modern never matches a device → `deviceId`, SKUs, sales total stay empty. |
| `SUB_CheckEmptyRecords` / `SUB_ValidateRecords` (flow) · empty + field validation | IMPLEMENTED | `RmaService.submitRmaRequest` per-row: empty IMEI, empty/invalid reason, row-numbered errors | Empty-IMEI and reason validation present (returned as JSON error list). |
| `SUB_FinalizeRMASubmission` (flow) · commit + number | IMPLEMENTED · (DIVERGENT numbering) | `RmaService` `generateRmaNumber` = `RMA{code}{YY}{seq}`, sets `Submitted` status | Legacy used a per-buyer-code `RMAId` sequence entity; modern derives seq from `countByBuyerCode`. Functionally equivalent. |
| `SUB_CalculateRMARequestSummary` (flow) · qty/SKU/sales total | PARTIAL | `RmaService.submitRmaRequest` sets `requestQty=rows`, `requestSkus=rows`, `requestSalesTotal=ZERO` | SKUs forced = qty (no device de-dup), sales total hardcoded 0 — direct consequence of the missing OfferItem/device match. |
| `SUB_SendEmail_RMASubmitted` (flow) · "we received your RMA" email | MISSING | not found — no `Email_Connector`/`SendEmailWithTemplate` for RMA submit | Buyer submission-confirmation email not sent. |
| `SUB_SendRMADetailsToSnowflake` / `SUB_SetRMAOwnerAndChanger` (flow) · submit Snowflake + owner stamp | MISSING | not found — no RMA→Snowflake `JA_SnowflakeStoreProc` push; no owner/changer stamping | Warehouse sync + audit-owner metadata absent at submit. |
| `ACT_DownloadRMATemplate` (flow) · active DB template, date-stamped | DIVERGENT | `RmaController.downloadTemplate` (`GET /template`) emits static `"IMEI/Serial,Return Reason\n"` CSV | Ignores the configured active `pws.rma_template` row (`PWSAdminController` stores it) and the `RMA_Request_yyyyMMdd.xlsx` date-stamp rename. |
| `ACT_DownloadInvalidIMEIs` + `SUB_InvalidRMA_GenerateReport` (flow) · invalid-IMEI Excel | MISSING | not found — modern returns `RmaSubmitResponse.failure(errors)` JSON inline | No downloadable "fix these bad rows" Excel (`InvalidRMA_Message_View` flow). Errors surfaced as JSON only. |
| `OCH_RMAItem_Action` (flow) · per-item decision | IMPLEMENTED | `RmaController.updateItemStatus` (`PUT /items/{itemId}/status`), `RmaService.updateItemStatus` + `recalculateApprovedValues` | Approve/Decline single item + roll-up recompute. |
| `ACT_RMAItem_SalesApproveAll` / `SalesDeclineAll` (flow) · bulk | IMPLEMENTED | `RmaController` `PUT /{rmaId}/items/approve-all` · `decline-all`; `pws/rma-review/[rmaId]` buttons | Bulk approve/decline present. |
| `SUB_CalculateApprovedRMAValues` / `SUB_RMA_SetAllRMAItemsValid` (flow) · roll-up | IMPLEMENTED | `RmaService.recalculateApprovedValues` (approved/declined counts, approved SKUs by deviceId) | Logic present but **approvedSkus is always 0** because `deviceId` is never set (submit gap). |
| `PWS_RMADetails_Sales` + `ACT_RMADetails_CompleteReview` (flow) · finalize | PARTIAL · (DIVERGENT) | `RmaController.completeReview` (`PUT /{rmaId}/complete-review`), `RmaService.completeReview` | Sets `Approved`/`Declined` status + reviewer + dates only. **Missing Oracle send + approval email + Snowflake sync.** Also *mixed* items → `Approved` (legacy leaves mixed as pending/empty). |
| `SUB_RMA_PrepareContentAndSendToOracle` / `SUB_RMA_SendRMAToOracle` / `CWS_PostCreateRMA` (flow) · Oracle create | MISSING | not found — `model/pws/Rma` has `oracle_number`/`oracle_id`/`oracle_http_code`/`oracle_json_response`/`oracle_rma_status` columns but **no code writes them** | No Oracle RMA order creation, no `IsOracleCreateRMAAPIOn` gate / bearer-token / REST POST. Schema is scaffolded, behavior absent. |
| `RMA_Detail` + `ACT_RMA_ReSubmitToOracle` (flow) · Oracle retry | MISSING | not found | No resubmit-to-Oracle path / error-message surfacing / `ReturnCode='00'` handling. |
| `SUB_SendEmail_RMAApproved` (flow) · approval email | MISSING | not found | Buyer approval notification (per `EcoATMDirectUser` on the buyer) not sent. |
| `RMA_Overview` + `ACT_UpdateRMAFromDeposco` / `SUB_SyncRMAStatus` (flow) · Deposco status poll | MISSING | not found for RMA — `AtpSyncController`/`AtpSyncService` cover inventory ATP, not RMA reverse-logistics | No polling of Deposco for RMAs with `oracleNumber` → statuses never auto-advance to `Received`; no V2-token/retry resilience. |
| `DS_GetRMAsByStatus` (flow) · list + status filter | IMPLEMENTED | `RmaController.getRmas` (`GET /`), `RmaService.getRmasByBuyerCode`/`getAllRmas` + `findByStatusGroupedTo`; `admin/pws-data-center/rma`, `pws/rma-review` | Buyer-scoped + all-view with `statusGroupedTo` filter; adds ownership scoping (security review CR-3). |
| `DS_GetRMASummaryByStatus` + `DS_CreateRMAMasterHelper` (flow) · summary tabs/KPIs | IMPLEMENTED | `RmaController.getSummary` (`GET /summary`), `RmaService.getSummary`/`getAllSummary` (count/price/SKU/qty + `Total` card) | Tab counters present; price/SKU accuracy weak (device match gap). |
| `ACT_ChangeFilterStatusToApproved` / `ToDeclined` (flow) · filter tabs | IMPLEMENTED | `status` query param + client-side filter in `admin/pws-data-center/rma/page.tsx` | Approved/Declined (and more) filtering supported. |
| RMA status config (`RMA_RequestsOverview_Sales` context) | IMPLEMENTED | `PWSAdminController` `/rma-status` GET/POST/PUT/DELETE (sort_order, system_status, internal/external text, group, default); `settings/pws-control-center/rma-status` | DB-driven status config CRUD present. |
| RMA reason config + `getReturnReasons` | IMPLEMENTED | `PWSAdminController` `/rma-reasons` CRUD; `RmaController.getReasons` (`GET /reasons`), `RmaReasonRepository.findByIsActiveTrue...` | Active-reason config + read used in submit validation. |
| RMA template config (`ACT_DownloadRMATemplate` consumer) | PARTIAL | `PWSAdminController` `/rma-templates` list/update; `settings/pws-control-center/rma-template` | Template row (name/active/file_name) is stored but **not consumed** by `/template` download (see DIVERGENT row above). |
| `ACT_RMADetailSales_Export` (+`_PendingApproval`) / `ACT_RMADetailsBidder_Export` (flow) · templated Excel detail | PARTIAL · DIVERGENT | client-side CSV builder in `pws/rma-review/[rmaId]/page.tsx` (`sales-details.csv`, `pending-approval.csv`) | Browser CSV, not the server `XLSReport.GenerateExcelDoc` templated Excel (`PWSRMADetailsSales`/`...PendingApproval`/`PWSRMADetailsBidder`). |
| `ACT_ExportRMAExcelFile` (buyer) / `ACT_DownloadRMAFile` (sales by status) (flow) · server exports | MISSING | not found — no server export endpoints | Buyer "export all my RMAs" and sales "download RMAs by status" not ported (only the review-page client CSV exists). |
| `ACT_DownloadRMALabel` (flow) · return-label PDF | MISSING | not found — no `DocumentGeneration.JA_GenerateDocument` | Printable shipping/return label absent. |
| `RMA_RequestsOverview_Sales` + `ACT_ChangRMAStatus` (flow) · manual status override | MISSING | not found — no per-RMA ad-hoc status override endpoint | Admin manual status override (outside approve/decline) not ported. |

## Biggest gaps (named, with spec node ids)
1. **Complete-review has no downstream effects.** `ACT_RMADetails_CompleteReview` in modern (`RmaService.completeReview`) only stamps status — the legacy Oracle order creation (`SUB_RMA_PrepareContentAndSendToOracle` → `SUB_RMA_SendRMAToOracle` → `CWS_PostCreateRMA`), the approval email (`SUB_SendEmail_RMAApproved`), and Snowflake sync (`SUB_SendRMADetailsToSnowflake`) are all absent. The `Rma.oracle_*` columns exist but are never written; there is no Oracle resubmit (`ACT_RMA_ReSubmitToOracle`).
2. **Submit-time device/ownership validation is skipped.** `VAL_RMARequestFile` requires each IMEI to resolve to an `EcoATM_PWS.OfferItem` for the buyer code (and pulls the device + sale price); `RmaService.submitRmaRequest` validates only reason + duplicate IMEI, so `deviceId`/SKUs/`requestSalesTotal` never populate and every approved-SKU/money roll-up reads 0. Submission email + Snowflake sync also missing.
3. **Deposco status polling absent.** `ACT_UpdateRMAFromDeposco` / `SUB_SyncRMAStatus` have no modern counterpart (the existing `AtpSyncService` is inventory ATP, not RMA), so RMAs with an `oracleNumber` never auto-advance to `Received`; no token-refresh/retry resilience.

## Net-new modern behavior (not in legacy)
- **Buyer-code ownership scoping + role split on every RMA read/mutation** (`RmaController.ownsRma`/`hasInternalRole`, `BuyerCodeService.isUserAuthorizedForBuyerCode`): buyers only see their own codes; approve/decline/complete are internal-only — a documented tightening (security review 2026-07-10, CR-3/C6) over the legacy bare-auth surface.
- **Upload hardening:** `UploadRateLimiter` + 10 MB size ceiling + Content-Type allowlist on `/submit`, and reviewer/submitter identity taken from the JWT rather than a client-supplied param.

## Likely-dead / obsolete legacy (don't port)
- `ACT_CopyText` / `EcoATM_RMA.CopyText` — trivial clipboard JS helper; a UI concern, not backend behavior.
- `InvalidRMA_Message_View_OLD` — explicitly the superseded `_OLD` variant of `InvalidRMA_Message_View`.
- `SUB_SetRMAOwnerAndChanger` / `SUB_SetRMAItemOwnerAndChanger` re-resolution — Mendix `System.owner`/`changer` bookkeeping; the modern app uses `submittedByUserId`/`reviewedByUserId` + `created/updated_date` columns instead (port the intent, not the flow).
- `NAV_SubmitRMA_Sales` — a mislabeled navigation nanoflow that just routes to the buyer overview page; pure navigation.

---

# CreditRequests — gap analysis

**Rollup:** Implemented 22 · Partial 1 · Missing 6 · Divergent 1 (of 30 assessed) · spec surface: 15 pages / 0 batches / 41 reachable flows (of 46)

## State (2-3 sentences)
Partial Credit is the repo's most complete module (Phase 1 done, Sprint 4, 124/124 backend tests green). The entire buyer wizard (5 steps, three reason branches), admin review (per-line / per-section / global decisions, live totals, complete-review + async buyer email), status config, email-template editor, photos, xlsx export, and the sales-rep on-behalf flow are all faithfully rebuilt across three controllers (`BuyerPartialCreditController`, `AdminPartialCreditController`, `OnBehalfPartialCreditController`) and `service/partialcredit/*`. The behavioral gaps are narrow and mostly intentional deferrals: the KPI/reports dashboard, the buyer "we received your request" submission email, the internal accounting-notification email, per-request Excel packet, draft deletion, and the automated Prolog encumbrance check (Phase 2).

## Entry points, screens & flows

| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `PG_CreditRequests` · sales/buyer landing, start new · page | IMPLEMENTED | `(dashboard)/wholesale/partial-credit/page.tsx`; `BuyerPartialCreditController.list/createDraft`; `POST /draft` → `CreditRequestService.createDraft` | Buyer-code selection is via API param + on-behalf modal instead of Mendix `NP_BuyerCodeSelect_Helper`. |
| `PG_CreditRequest_Sales` · status-filtered grids, admin queue Excel · page | IMPLEMENTED | `admin/.../partial-credit/page.tsx`; `AdminPartialCreditController.list` (status/buyerCode/reason/date filters + counters) + `GET /export.xlsx` | Status counters chips + filtered list = parity; export is broader than legacy (filtered vs pending-only). |
| `CreditRequest_SubmitNew` · wizard entry, flag issue, dup guard · page | IMPLEMENTED | `wholesale/partial-credit/new/page.tsx`; `PATCH /{id}` (reason flags), `VAL_NewCreditRequest` dup guard → `CreditRequestValidator.validateOrderEligibility` (`findActiveByOrderAndBuyer` excl. DECLINED) | |
| `CreditRequest_DeviceUpload` · paste/type barcodes popup · page | IMPLEMENTED | `POST /{id}/{missing\|wrong\|encumbered}-lines`; `POST /parse-barcodes` (`CreditRequestFileDropParser`, xlsx/csv/docx) | Modern file-drop supports csv+docx too (legacy was Excel-only via `JA_ImportBarcodesFromExcel`). |
| `CreditRequest_MissingDevice` · review missing lines, damage Q, Next · page | IMPLEMENTED | `new/missing/page.tsx`; `replaceMissingLines`; damage-answer gate in `CreditRequestValidator.validateDamageAnswer` (`damageNotAnswered` / `damageRequiresPhoto`) | `VAL_MissingDevices` shipment-damage gate ported as submit-time validation. |
| `CreditRequest_WrongDevice` · review wrong-model lines · page | IMPLEMENTED | `new/wrong/page.tsx`; `replaceWrongLines`; expected/actual pairs on `WrongDeviceLine` | |
| `CreditRequest_EncumberedDevices` · review encumbered lines · page | IMPLEMENTED | `new/encumbered/page.tsx`; `replaceEncumberedLines` | See Prolog gap below — barcodes captured; encumbrance verification manual. |
| `CreditRequest_Summary` · final review + submit · page | IMPLEMENTED | `new/summary/page.tsx`; `POST /{id}/submit` → `CreditRequestService.submit` (full validator + denormalise + flip PENDING_APPROVAL) | |
| `CreditRequest_PhotoUpload` / `_MissingDevice` · photo popups + count badge · page | IMPLEMENTED | `POST/GET/DELETE /{id}/photos`, `/photos/{id}/blob`; `CreditRequestPhotoService` (5 MB, 5/line cap, image allowlist, freeze-when-final); `ACE_CalculateImageCount` → V91 wrong-photo-count map in `toDetail` | |
| `CreditRequest_Detail` · full detail to sales/admin · page | IMPLEMENTED | buyer `[id]/page.tsx` + admin `[id]/page.tsx`; `CreditRequestDetail.from` | Buyer detail reveals per-line decisions post-finalisation. |
| `PG_CreditRequest_Review` · per-line/bulk decisions, live totals · page | IMPLEMENTED | admin `[id]/page.tsx`; `POST /{id}/lines/{lineId}/decision`, `/sections/{kind}/decision`, `/decision`, `/lines/{lineId}/encumbered`; `SUB_CalculateTotals` → `CreditCalculationService.computeHeaderSummary` recomputed per decision | Wrong-device received-pricing (`SUB_CalculateReceivedDeviceAmt`) → `ResolveReceivedDeviceService` + `MaxSubmittedBidLookup`. |
| `Admin_CreditRequest` · admin overview → drill to detail · page | IMPLEMENTED | admin landing + `GET /{id}` | |
| `PG_Admin_PartialCreditStatus` · status config · page | IMPLEMENTED | `admin/.../partial-credit/statuses/page.tsx`; `GET /statuses`, `PATCH /statuses/{id}`; `StatusConfigService` (5 seeded rows, cosmetic-only, immutable `system_status`) | |
| `ACT_CreditRequest_CompleteReview` · terminal APPROVED/DECLINED + email · flow | IMPLEMENTED | `POST /{id}/complete-review` → `AdminCreditRequestService.completeReview` publishes `ReviewCompletedEvent`; `ReviewCompletedEmailListener` (`@TransactionalEventListener` AFTER_COMMIT, `@Async`) → `EmailTemplateService` + `EmailAuditService` | Adds `UNDER_REVIEW` intermediate state + `open-for-review` (net-new honest verb). |
| barcode manifest reconciliation (`SUB_GenerateValidBarcodesList`) · flow | PARTIAL | `CreditRequestValidator.reconcileBarcodes` + `validateOrderForBuyer` via `CreditRequestSnowflakeReader.getOrderLines` | Logic complete (dedupe/Valid/NotInOrder + banner), but the live `JdbcCreditRequestSnowflakeReader` is gated behind `partial-credit.snowflake.reader=jdbc`; default is `Logging` stub and the JDBC path is unproven in staging (one e2e test `.skip`'d). |
| `PG_CreditRequest_Reports` + `SUB_ComputeCRKPIs`/`ACT_RefreshCRKPIs`/`ACT_Open_CRKPIDashboard` · KPI dashboard · page+flows | MISSING | not found (searched `frontend/**/partial-credit`, backend `service/partialcredit/*` for KPI/CRSummary) | No reports/KPI surface at all — confirmed-absent. |
| `SUB_SendCreditRequestSubmittedEmail` · buyer "we received your request" email · flow | MISSING | not found — `submit()` only flips status + saves, no event/email; `ReviewCompletedEmailListener` only fires on complete-review | Email infra exists but no submit-time buyer confirmation. Confirmed-absent. |
| `ACT_SendCreditRequestAccountingEmail` · internal accounting-review notification · flow | MISSING | not found — only `ReviewCompleted_Approved/_Declined` + `PhotoUploadRequested` templates seeded (V90); listener resolves buyer recipients only | No internal accounting-team notification. Confirmed-absent. |
| encumbered submit-time re-pricing / automated Prolog check (`ACT_SubmitCreditRequest` `EncumberedDevicesPrices` re-derive) · flow | MISSING | admin enters `prologResult`+`actualValue` manually via `POST /{id}/lines/{lineId}/encumbered`; `PrologResult` enum exists but no automated check | Deferred to Phase 2 (documented in `partial-credit.md` §"NOT in Phase 1"). Credit = manual `actualValue`. |
| `NF_DeleteDraftCreditRequest` / `ACT_DeleteCreditRequest` · delete unsubmitted draft · flow | MISSING | not found — `BuyerPartialCreditController` has no `DELETE /{id}` (only `DELETE /photos/{photoId}`); `CreditRequestService` has no delete-request method | Buyer cannot delete a draft. Confirmed-absent. |
| `ACT_DownloadCreditRequest` · per-request review-packet Excel · flow | MISSING | not found — only the two-sheet list export (`PartialCreditExcelExportService.export`) exists | Single-request POI packet not ported (low value; list export supersedes). |
| `SUB_NavigateCreditRequests` / `NAV_CreditRequests` / `ACT_NAV_CreditRequests` · role-based buyer-code routing · flow | DIVERGENT | on-behalf modal + `?buyerCodeId=` param; role gating via `@PreAuthorize` | Mendix `NP_BuyerCodeSelect_Helper` / `CreditRequestWizard` / `CreditRequest_ImportHelper` session-state machinery is obsolete under a stateless REST wizard — intentional re-architecture, not a regression. |

## Biggest gaps (named, with spec node ids)
1. **KPI/Reports dashboard fully absent** — `PG_CreditRequest_Reports` + `SUB_ComputeCRKPIs` + `ACT_RefreshCRKPIs` + `ACT_Open_CRKPIDashboard`. No submitted-volume-by-Internal/External metrics surface exists. (MISSING)
2. **Buyer submission-confirmation email not sent** — `SUB_SendCreditRequestSubmittedEmail`. Legacy emailed the buyer on submit; modern only emails on review completion. Buyer gets no "we received it" acknowledgement. (MISSING)
3. **Automated encumbrance (Prolog) check + submit-time encumbered re-pricing deferred** — `ACT_SubmitCreditRequest` encumbered branch. Modern relies on a reviewer manually entering `prologResult`+`actualValue`; the automated block-list/lock verification and DB-query re-pricing are Phase 2. (MISSING/deferred)

Secondary: internal accounting-notification email (`ACT_SendCreditRequestAccountingEmail`), draft deletion (`NF_DeleteDraftCreditRequest`), per-request Excel packet (`ACT_DownloadCreditRequest`).

## Net-new modern behavior (not in legacy)
- **`UNDER_REVIEW` state + explicit `POST /{id}/open-for-review`** — legacy had Draft→Pending→Approved/Declined; modern adds an honest intermediate review-open transition (replaces the spec's implied state-mutating GET).
- **Action-recommendation engine** — `ActionRecommendationService` computes ACCEPT/DECLINE defaults for wrong-device lines (brand allowlist, no-power, capacity-vs-grade diff, price floor); a formalised decision tree the reviewer can override.
- **Formalised sales-rep on-behalf flow** — `OnBehalfPartialCreditController` (`/api/v1/salesrep/partial-credit/**`): pick buyer-code → buyer-user → order#, `is_on_behalf` stamping. Legacy only tracked `SubmittedByType` Internal/External.
- **`email_audit` trail** (V90) — one row per send attempt with success/error; `ON DELETE SET NULL`. No legacy equivalent.
- **DB-driven status pills + admin-editable email templates** — `credit_request_statuses` cosmetic config + `email_templates` `{{var}}` renderer with HTML-escape default and `{{!var}}` raw opt-out.
- **Hardened uploads** — `UploadRateLimiter`, 5 MB/5-per-line photo caps, magic-byte image validation, `nosniff` attachment streaming, xlsx 5,000-row cap → 413.

## Likely-dead / obsolete legacy (don't port)
- **`CreditRequest_ImportHelper` / `CreditRequestWizard` / `NP_BuyerCodeSelect_Helper` session objects** and their reset flows (`ACT_ViewCreditRequest`, `ACT_CreditRequest_BackButton`) — Mendix client-state scaffolding; obsolete under a stateless REST wizard where each step is an idempotent PATCH/POST.
- **`DS_CreateUIHelper`, `DS_CurrentPageName`, `NF_CreditRequest_Sales_SwitchTab`, `NAV_CreditRequests` progress-spinner wrappers** — pure Mendix UI-state plumbing; React owns tab/nav/loading state.
- **`SUB_CR_LogInfo` / `SUB_CR_LogWarning` / Custom_Logging timers** — replaced by slf4j + the `email_audit` table.
- **`NF_DownloadCreditRequests_Sales`** (unreachable in legacy) — superseded by the admin `export.xlsx`.

---

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

---

# Integration — gap analysis

**Rollup:** Implemented 0 · Partial 1 · Missing 8 · Divergent 0 (of 9 assessed behavior-groups, +1 obsolete) · spec surface: 1 page / 0 batches / 36 reachable flows (50 total)

## State (2-3 sentences)
The entire Integration capability is a single legacy behavior: the `EcoATM_Direct_Sharepoint` module's "AllBids by BuyerCode" Excel-generation-and-SharePoint-upload pipeline (plus a "ZeroQtyCap" variant), built on Microsoft Graph OAuth + the Mendix `Sharepoint`/`XLSReport`/`OQL` marketplace connectors. **None of it is ported** — the modern backend has no SharePoint or Microsoft Graph client at all (grep for `sharepoint|microsoftgraph|CreateDriveItem|GetDrives|webhook|/subscriptions|documentgeneration` in `backend/src/main/java` returns only schema-column names, never client code). The legacy intent "publish every buyer's bids to a downstream system" is instead served, divergently, by the modern Snowflake push path (`BidExportService` / `AuctionSnowflakeResyncService`), so the data leaves the app — just not as an Excel file on a SharePoint drive.

## Entry points & flows
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate` · SharePoint upload primitive · flow | **MISSING** | not found (searched backend/src for `CreateDriveItem`, `GetDrives`, `driveitem`, `Sharepoint.*`) — confirmed-absent | The shared "push a FileDocument into the Documents drive, gated by `SharepointBidRoundEnabled`" primitive has no modern equivalent. |
| `EcoATM_Direct_Sharepoint.ACT_GetAuthorization` · Graph OAuth token lookup · flow | **MISSING** | not found (searched `MicrosoftGraph`, `Authorization`, `accessToken`, `refreshToken`, OAuth) — confirmed-absent | No Graph OAuth/authorization record or token-refresh in the modern app. |
| `EcoATM_Direct_Sharepoint.SUB_createSharepointFile_OQLQuery` · OQL AllBids build+export · flow | **MISSING** | not found — confirmed-absent | OQL-bulk build → `XLSReport.GenerateExcelDoc` → upload not reproduced. |
| `EcoATM_Direct_Sharepoint.SUB_createSharepointFile_OLD` / `SUB_CreateBidDataDownload_NonDW` / `_DW` · legacy per-entity AllBids build · flow | **MISSING** | not found — confirmed-absent | Row-by-row `AllBidDownload` build for DW + non-DW tracks not reproduced. |
| 32× `SUB_AllBidDownload_Create_*` / `SUB_Change_ExistingDevice_*` (A–H grade truth table, DW + non-DW) · flow | **MISSING** | not found (searched `AllBidDownload`, `allbid`) — confirmed-absent | The 8-way (BidData/Quantity/Amount/Qty>0) × create/update × DW/non-DW helper matrix has no modern port; `AuctionUI.AllBidDownload` entity is not migrated. |
| `EcoATM_Direct_Sharepoint.SUB_SendZeroQtyCapFileToSharepoint` / `_allBuyerCodes` / `ACT_SendZeroQtyCapFileToSharepoint` · ZeroQtyCap export+upload · flow | **MISSING** | not found — confirmed-absent | Per-buyer + bulk "your quota dropped to zero" Excel alert to SharePoint not reproduced. |
| `EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint` · per-submit flag-gated push · flow | **MISSING** | `AuctionsFeatureConfig.sendFilesToSharepointOnSubmit` column migrated (V8/V18) but **no consumer** | The flag that would trigger a per-submit SharePoint push exists in schema; nothing reads it. |
| `EcoATM_Direct_Sharepoint.SUB_GetSharepointTemplate` + `AuctionUI.SharePointMethod` (OQL vs OLD) · template/method switch · flow | **PARTIAL** | `auctions.sharepoint_method_config` (`use_api_upload`, `oql_endpoint`, `api_endpoint`) migrated (V59/V63); `bid_submit_log` has `Push_To_Sharepoint` action; `sp_retry_count` column | **Config/state schema migrated, zero runtime consumer.** Data model preserves the OQL-vs-API toggle and retry counter but no code uploads or switches on it. |
| `EcoATM_Direct_Sharepoint.DS_GetParticipatedBuyerCodes` · participated-buyer-codes query · flow | **MISSING** | not found — confirmed-absent | The "buyers who placed a non-zero bid" set (input to the bulk ZeroQtyCap job) is not reproduced for this purpose. |
| `EcoATM_Direct_Sharepoint.BidDataUploadPOC` (page) + `Test_UploadBidDataDoc` / `Test_GetBidDataDoc` · flow/page | **Obsolete — don't port** | n/a | Developer POC/diagnostic surface; explicitly "not a production end-user page." |

## Biggest gaps (named, with spec node ids)
1. **No SharePoint / Microsoft Graph client whatsoever** — the whole capability (`ACT_CreateDriveItemCreate`, `ACT_GetAuthorization`, `Sharepoint.CreateDriveItem`/`GetDrives`) is confirmed-absent. Any requirement to deliver bid workbooks to a SharePoint drive is unmet.
2. **AllBids-by-BuyerCode Excel export** (`SUB_createSharepointFile_OQLQuery`/`_OLD`, `SUB_CreateBidDataDownload_NonDW`/`_DW`, 32 `AllBidDownload` helpers) — the entire per-buyer spreadsheet generator is gone; `AuctionUI.AllBidDownload` was never migrated.
3. **ZeroQtyCap buyer-alert files** (`SUB_SendZeroQtyCapFileToSharepoint*`) — no modern path notifies a buyer their allowed quantity was capped to zero via a file.
4. **Config migrated without a consumer** (`sharepoint_method_config`, `send_files_to_sharepoint_on_submit`, `sp_retry_count`, `bid_submit_log.Push_To_Sharepoint`) — dead configuration that will mislead unless either wired up or documented as intentionally dropped.

## Net-new modern behavior (not in legacy)
- **Snowflake bid publication replaces SharePoint export (divergent re-platforming of the same intent).** `service/auctions/biddata/BidExportService`, `controller/admin/AuctionSnowflakeResyncAdminController` + `service/auctions/snowflake/AuctionSnowflakeResyncService` explicitly "Port Mendix `ACT_Auction_SendAllBidsToSnowflake_Admin`" — all-bids data now flows to Snowflake (`Jdbc*SnowflakeWriter`), not to an Excel file on a drive.
- **Broad modern integration layer for other capabilities** (not this one's behavior): `service/snowflake` + `service/auctions/snowflake` (Jdbc/Logging writers+readers for auction-status, bid-ranking, target-price, reserve-bid, PO, aggregated-inventory, credit-request, buyer), `service/email` (`EmailSender`/`SmtpEmailSender`/`LoggingEmailSender` + templates), `OracleOrderClient`/`OracleConfigController`, Deposco (`AtpSyncController`/`AtpSyncService`/`DeposcoInventoryDto`).

## Likely-dead / obsolete legacy (don't port)
- `BidDataUploadPOC` page + `Test_UploadBidDataDoc` / `Test_GetBidDataDoc` — developer POC/diagnostics.
- The Microsoft Graph **change-notification subscription** machinery behind SharePoint (`MicrosoftGraph.SUB_Subscription_*`, `SCE_Subscription_Renew` daily job, `SE_AccessToken_Refresh` — the latter already **Disabled** in legacy) — these are marketplace-connector plumbing (see the (none) bucket), obsolete unless SharePoint integration is deliberately revived.

### SharePoint / REST / access-token verdict (explicit)
- **SharePoint drive upload** (`Sharepoint.CreateDriveItem` / `GetDrives`, `ACT_CreateDriveItemCreate`): **CONFIRMED ABSENT** in the modern repo (grep of `backend/src/main/java` for `sharepoint|CreateDriveItem|GetDrives|driveitem` matches only migration-SQL column names, never client code).
- **Microsoft Graph REST + OAuth access token** (`MicrosoftGraph` verbs, `ACT_GetAuthorization`, subscription renewal): **CONFIRMED ABSENT** (grep for `microsoftgraph|graph.microsoft|webhook|subscription.?renew|change.?notification|/subscriptions|OAuth|accessToken|refreshToken` in backend Java is empty).
- **DocumentGeneration access-token refresh** (`SE_AccessToken_Refresh`): **CONFIRMED ABSENT** and moot — the legacy job is Disabled and never runs.

---

# Platform — gap analysis

**Rollup:** Implemented 2 · Partial 2 · Missing 2 · Divergent 2 (of 8 assessed behavior-groups; ShedLock infra counted as net-new) · spec surface: 4 pages / 1 batch / 25 reachable flows (42 total)

## State (2-3 sentences)
The admin-facing configuration half of the legacy platform layer is ported well: the modern `PWSAdminController` (`/api/v1/admin/**`) is a faithful, broad "PWS Control Center" — feature flags, error messages, PWS constants, order/RMA status, navigation menu, ranks, maintenance mode — backed by the `settings/pws-control-center/*` Next.js pages. The runtime/session half is mostly re-platformed away: the custom **record-lock framework** (`EcoATM_Lock`) and the **multi-tab idle-timeout/keep-alive** mechanism have no modern equivalent (a stateless JWT REST API doesn't need them), and **admin-configurable dashboard tiles** became hardcoded arrays. Distributed **ShedLock** exists but solves job-leader-election, not the legacy per-record locking it superficially resembles.

## Entry points & flows
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `Eco_Core.FeatureFlag_Overview` (page) · `Eco_Core.ACT_FeatureFlag_RetrieveOrCreate` · flow · `Eco_Core.PWSFeatureFlag` | **IMPLEMENTED** | `PWSAdminController` `/feature-flags` GET/POST/PUT/DELETE over `pws.feature_flag`; `frontend/src/app/(dashboard)/settings/pws-control-center/feature-flags` | Storage + admin toggle fully ported. Gap: the legacy **self-registration** side (`RetrieveOrCreate` auto-inserts a default-off flag the first time code references an unknown name) is **not** reproduced — no `isFeatureEnabled`/`retrieveOrCreate` service (grep empty); flags are admin-seeded only. |
| `EcoATM_Direct_Theme.DS_PWSSiteBanner` · maintenance gate · flow (+ `MaintenanceMode`) | **IMPLEMENTED** | `PWSAdminController` `/maintenance-mode` GET(auto-seed)/PUT over `pws.maintenance_mode` (banner_start/start/end + banner & page copy); `settings/pws-control-center/maintenance-mode` page | Two-stage "advance-warning banner → full lockout" **data model + admin surface** ported. Parity check: confirm the **buyer PWS site actually enforces** the window at runtime (redirect to lockout / show banner) — that gate wasn't located on the buyer shell. |
| `EcoATM_Lock.Lock_Overview` (page) · `EcoATM_Lock.ACT_Lock_Release` · `EcoATM_Lock.SUB_Lock_ReleaseInactivePage` · `batch:SE_Lock_Release` | **MISSING** | not found — no `Lock` entity/service (find `*lock*` → only `ClockConfig.java`). `net.javacrumbs.shedlock` present but is **job**-election, not record locks | The optimistic record-locking framework (prevent two users editing one record; auto-expire stale locks every minute) has no port. `ShedLock` (`SchedulingConfig`, `@SchedulerLock` on `AuctionLifecycleScheduler`/`ReserveBidSyncScheduledJob`) is a different concept — do not conflate. |
| `EcoATM_Direct_Theme.IdleTimeoutAlert` (page) · `ACT_ContinueSession` · `DS_CreateTimerHelper` · `DS_LastUserActivity` · `DS_GetOrCreateIdleTimeout` · flows | **MISSING / DIVERGENT** | `SessionCleanupTask` (`@Scheduled`); `application.yml server ... idle-timeout: 300000` (Tomcat conn timeout, not app popup); JWT expiry in `AuthController`/security | The multi-tab "session about to expire" popup, client countdown `TimerHelper`, keep-alive ping (`JSA_ClickElement`), and per-session `IdleTimeout` freshest-activity logic are not ported. Replaced by stateless JWT token expiry — behavior (interactive "stay signed in") is gone. |
| `Eco_Core.Tile_NewEdit` (page) · `Eco_Core.ACT_Tile_Create` · flow (`Eco_Core.Tile`) | **DIVERGENT** | `frontend/.../admin/auctions-data-center/page.tsx` — `const tiles: Tile[]` **hardcoded** launcher array (incl. a `Cohort Mapping` tile) | Launcher tiles exist but are compiled-in, not admin-CRUD `Tile` records with image/Deeplink resolution (`CommunityCommons.GetApplicationUrl`). No `Tile_NewEdit` equivalent; admins can't add/edit tiles. |
| `EcoATM_Direct_Theme.SUB_GetOrSetBuyerCode_SessionAndTabHelper` · `DS_GetBuyerCode_SessionAndTabHelper` (per-tab, `JSA_GetTabIndex` polling) · flows | **PARTIAL** | `PWSSessionController` `/activate` (resolve + authorize a single active buyer code, count pending counter-offers); `BuyerPortalChrome` "Switch Buyer Code"; `buyer_mgmt.buyer_code_session_helpers` table migrated (V10) | Session-level active-buyer-code selection + switching is ported. The **per-browser-tab isolation** (tab-id polling so two tabs hold different codes) is simplified to one active code per session; the migrated helper table is largely unused for that purpose. |
| `batch:SE_SetSLATag` · SLA-breach tagging job (PWS cap; task-grouped here) | **PARTIAL** | `PWSAdminController` `/sla-tags/set` (+ net-new `/sla-tags/remove`): same 2-day cutoff + `Sales_Review`/`Buyer_Acceptance` filter on `pws.offer.offer_beyond_sla`; `pws_constants.sla_days` | Tagging **logic** ported, but as **manual admin buttons**. The legacy **every-minute automated scheduled job is absent** (no SLA entry in the `@Scheduled` sweep) — offers won't auto-flag as overdue without a manual trigger. |
| `Custom_Logging.SUB_Log_*` (Info/Warning/Error/Critical/Debug/HttpError/StartTimer/EndTimer) → `JA_EcoATMErrorLog` / `JA_EcoATMSimpleLog` · flows | **DIVERGENT** | slf4j (`log.warn`/`log.error` pervasive) + domain audit tables: `pws.admin_audit_log` (`PricingService`), `partial_credit.email_audit`, `sso.sso_audit_log`, `infra.scheduledjob` (`ScheduledJobRunRecorder`) | Logging intent preserved via standard framework + targeted audit trails (arguably richer). But the generic DB-backed `EcoATMErrorLog` entity and the `StartTimer/EndTimer` perf-timer log pattern are **obsolete/not reproduced**; `SUB_Log_HttpError`'s bundled `HttpResponse` capture for integration failures is now ad-hoc slf4j. |

## Biggest gaps (named, with spec node ids)
1. **Automated SLA tagging job** (`batch:SE_SetSLATag`) — the modern app can set/clear SLA flags on demand but never on a schedule; overdue offers silently won't surface until someone clicks. Highest-impact functional gap.
2. **Record-lock framework** (`EcoATM_Lock.Lock`, `ACT_Lock_Release`, `SE_Lock_Release`, `Lock_Overview`) — no concurrent-edit protection at the record level. Acceptable for a stateless REST design (optimistic concurrency / DB tx), but confirm no admin workflow depended on the manual Lock_Overview release UI.
3. **Feature-flag self-registration** (`ACT_FeatureFlag_RetrieveOrCreate`) — code that references a not-yet-seeded flag will get no auto-created default-off row; every flag must be pre-seeded or it 500s/returns empty.
4. **Idle-timeout keep-alive UX** (`IdleTimeoutAlert`, `ACT_ContinueSession`) — no interactive "stay signed in" prompt; users hit hard JWT expiry instead.

## Net-new modern behavior (not in legacy)
- **ShedLock distributed job locking** (`config/SchedulingConfig` `@EnableSchedulerLock`, `infra.shedlock` table V71, `@SchedulerLock` on the auction-lifecycle + reserve-bid-sync jobs) — replaces the Mendix `TaskQueueScheduler`/`ScE_SchedulerQueues` tick with framework-native multi-instance leader election.
- **`ScheduledJobRun` audit infra** (`infra/scheduledjob/*`) — first-class scheduled-job run recording (no legacy analog).
- **Broader Control-Center scope**: `/error-messages`, `/order-status`, `/rma-status|templates|reasons`, `/ranks-config`, `/deposco`, `/sla-tags/remove` — several are richer than / additive to the legacy platform pages.
- **Rate limiters** (`AuthRateLimiter`, `UploadRateLimiter`, `BidRateLimiter`) — net-new platform guardrails.

## Likely-dead / obsolete legacy (don't port)
- `StartTimer`/`EndTimer` perf-timer logging wrappers — superseded by APM/slf4j timing; noise if ported literally.
- The `EcoATM_Lock` machinery and multi-tab `IdleTimeout`/`TimerHelper`/`BuyerCode_SessionAndTabHelper` per-tab plumbing — artifacts of Mendix's stateful page-session model; a JWT SPA doesn't need them (keep only the session-level buyer-code selection already built).
- Mendix `TaskQueueScheduler` internals (see (none) bucket) — replaced wholesale by Spring `@Scheduled` + ShedLock.

---

# Reports — gap analysis

**Rollup:** Implemented 0 · Partial 0 · Missing 4 · Divergent 0 (of 4 assessed report families) · spec surface: 9 pages / 0 batches / 17 reachable flows (23 total)

## State (2-3 sentences)
The legacy `EcoATM_Reports` module is three analytics report families — **Buyer Award Summary**, **Cohort Mapping / EB Calibration**, and **Buyer Bid Summary/Detail** — and **none of them are ported**: there are no backend report endpoints and no frontend pages for any of the three (the admin launcher even links to a `/admin/auctions-data-center/cohort-mapping` route that does not exist — a dead stub). The modern app's reporting surfaces (`Round3ReportController`, `BidDataAdminController`, `BuyerOverviewController`) are net-new and cover different data (R3 buyer data, bid-data grid, buyer overview), not these families. This matches the repo's own note that the Buyer Award Summary report is outstanding.

## Entry points & flows
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_Reports.PG_BuyerAwardsSummaryReport` / `PG_BuyerAwardSummaryReportReview` (pages) · `SUB_LoadBuyerAwardsSummaryReport` · `SUB_GetBuyerAwardSummaryrReportForWeek` · `NAV_BuyerAwardsSummaryReportsLandingPage` · `SUB_DeleteBuyerAwardSummaryReportForWeek` · `SUB_BuyerAwardSummaryTotals_GetOrCreate` · `ACT_BuyerAwardSummaryReport_ShowPage` · `SUB_LoadBuyerDetails` · `SUB_CalculateBuyerDetailsTotal` | **MISSING** | not found (searched backend/frontend for `award.?summary`, `BuyerSummary`, `BuyerDetail`, `AwardSummary` — only an unrelated "calibration" comment in `AdminCreditRequestService`) — confirmed-absent | The per-week buyer-award recompute (delete-then-`ExecuteDatabaseQuery`/XML round-trip into `EcoATM_DA.BuyerSummary`/`BuyerDetail`, totals roll-up, MDM device enrichment, drill-down review page) has no backend or frontend equivalent. |
| `EcoATM_Reports.PG_CohortMapping` / `PG_CohortMapping_2` / `CohortMappingDoc_NewEdit` (pages) · `SUB_UploadCohortMapping` · `SUB_DeleteCohortMappingData` · `ACT_CreateNewCohortMapping` · `ACT_SendCohortMappingToSnowflake` · `NF_DownloadCohortMapping` | **MISSING** | **Stub only**: `admin/auctions-data-center/page.tsx` lists a `Cohort Mapping` tile → `/admin/auctions-data-center/cohort-mapping`, but **no such route exists** (find `*cohort*` under `frontend/src/app` empty); no backend cohort endpoint | Excel upload → full-table replace → per-row recreate → Snowflake push (`DatabaseConnector.ExecuteStatement`) is entirely unported. The dashboard link is a dead end. |
| `EcoATM_Reports.PG_EB_Calibration_Report` (page) · `NF_DownloadEBCalibrationReport` | **MISSING** | not found (searched `calibration`, `EB.?calibration`) — confirmed-absent | The EB Calibration Excel export (per-reporting-week filename) has no port. Related "reserve bid / EB" admin surfaces exist (`ReserveBidController`) but not this calibration report. |
| `EcoATM_Reports.BuyerSummaryReportOverviewNew` / `BidDetailReport_BuyerCodeSelect` / `BidDetailReport_BuyerCodeSelect_SwitchBuyerCode` (pages) · `ACT_RetrieveBuyerSummaryReport` (session-cached OQL) · `ACT_GetBuyerBidDetailReport_fromSummary` · `SUB_RetrieveLatestSummaryReportBySession` | **MISSING** | not found — confirmed-absent | The two-auction OQL bid/lots aggregation, session-level report caching (`ReportsSessionCachingInMinutes`), buyer-code switcher popup, and summary→detail drill-through are unported. `BuyerOverviewController` is a buyer-management grid, not this bid summary/detail report. |

## Biggest gaps (named, with spec node ids)
1. **Buyer Award Summary Report** (`SUB_LoadBuyerAwardsSummaryReport`, `PG_BuyerAwardsSummaryReport`, `EcoATM_DA.BuyerSummary`/`BuyerDetail`) — the flagship weekly award report and its per-buyer device-level drill-down; fully absent and explicitly outstanding per repo docs.
2. **Cohort Mapping upload + Snowflake push** (`SUB_UploadCohortMapping`, `ACT_SendCohortMappingToSnowflake`) — plus a **misleading dead dashboard link** to a nonexistent `/cohort-mapping` route that should be removed or built.
3. **Buyer Bid Summary/Detail Report** (`ACT_RetrieveBuyerSummaryReport`, `ACT_GetBuyerBidDetailReport_fromSummary`) — the cached two-auction bid aggregation + drill-through; absent.
4. **EB Calibration Report** (`NF_DownloadEBCalibrationReport`) — absent.

## Net-new modern behavior (not in legacy)
- **Round 3 buyer-data report** — `controller/admin/Round3ReportController` (`/api/v1/admin/round3-reports`, GET + `/by-auction`) + `frontend/.../auctions-data-center/round3-bid-report` page, backed by `auctions.round3_buyer_data_reports` (sub-project 6). Sourced from the AuctionUI/R3 lifecycle, **not** the `EcoATM_Reports` families.
- **Bid-data admin grid** — `controller/admin/BidDataAdminController` (`/api/v1/admin/bid-data`, list + delete).
- **Buyer overview** — `controller/BuyerOverviewController` (`/api/v1/admin/buyers`, grid + CRUD + `/snowflake-sync` + `/sales-representatives`).

## Likely-dead / obsolete legacy (don't port)
- `BidDetailReport_BuyerCodeSelect_SwitchBuyerCode` and the duplicate `PG_CohortMapping` vs `PG_CohortMapping_2` pages are UI-helper/older-variant artifacts — if these reports are rebuilt, collapse to one page each; the `BuyerCodeSelectSearchHelper` "create a fresh empty object per open" pattern is a Mendix page-datasource idiom with no modern analog.
- Nothing here is obsolete at the **behavior** level — all three report families are legitimate missing features, not dead code. They should be scoped as net-new work if the business still needs them.

---

# (none) bucket — obsolescence assessment

**Do NOT classify or port these 895 flows.** The `(none)`-capability bucket is 902 nodes but only **74 flows are reachable (~8%)** — the other ~828 are dead internal branches of vendored code. Every reachable node belongs to a **Mendix marketplace / library module**, not to an orphaned business feature: `MicrosoftGraph` (25), `ForgotPassword` (15), `TaskQueueScheduler` (13), `SAML20` (9), `Email_Connector` (5), `Sharepoint` (3), and singletons for `Encryption`, `Custom_Excel_Import`, `ExternalDatabaseConnector`, `TaskQueueHelpers`. These are cross-cutting plumbing (OAuth/HTTP verbs, SAML metadata sync, password-reset email, task-queue ticks, symmetric crypto, Excel import), which is exactly why the capability tagger left them unlabeled — they support many capabilities and own none. The modern app deliberately **replaces this plumbing with framework-native equivalents** rather than porting the connector internals: Spring Security SAML (SSO — `sso` schema is migrated), Spring Mail `SmtpEmailSender`, Spring `@Scheduled` + ShedLock (in place of `TaskQueueScheduler`/`ScE_SchedulerQueues`), Java/JWT crypto, and Apache-POI-style Excel handling. The one genuine feature-bearing subset — `MicrosoftGraph`/`Sharepoint` (subscriptions, `CreateDriveItem`, resumable upload, token refresh) — is confirmed-absent in the modern repo and is the same SharePoint gap tracked under the Integration capability, not a new finding. Net: this bucket is obsolete library/plumbing code; blindly porting it would re-implement things Spring already provides. The low reachable ratio (74/902) is itself the tell that it is scaffolding, not product.

**Example reachable node ids:** `MicrosoftGraph.SUB_Authorization_RefreshAccessToken`, `MicrosoftGraph.SCE_Subscription_RenewAll`, `Sharepoint.CreateDriveItem`, `SAML20.SE_Sync_IDPMetadata`, `SAML20.RetrieveIdPMetadata`, `ForgotPassword.SF_CreateAndSendEmailForReset`, `ForgotPassword.SF_DeleteExpiredSignInRecords`, `TaskQueueScheduler.Sub_HandleScheduleQueues`, `Email_Connector.SUB_EmailAccount_CheckServerConnection`, `Encryption.Decrypt`.
