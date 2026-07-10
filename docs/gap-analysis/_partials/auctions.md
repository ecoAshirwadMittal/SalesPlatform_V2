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
