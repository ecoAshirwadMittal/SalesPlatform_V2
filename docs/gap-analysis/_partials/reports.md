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
