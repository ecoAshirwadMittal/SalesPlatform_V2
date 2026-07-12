# Buyer Award Summary Report — Implementation Plan (gap-analysis #5)

> Execute with superpowers:subagent-driven-development (Opus agents + review gate).

**Date:** 2026-07-11 · Source: `docs/gap-analysis/_partials/reports.md` gap #1, `implementation-plan.md` §3.1. Legacy nodes: `PG_BuyerAwardsSummaryReport` / `SUB_LoadBuyerAwardsSummaryReport` / `SUB_GetBuyerAwardSummaryReportForWeek` / `PG_BuyerAwardSummaryReportReview` (drill-down) / `JSON_BuyerAwardsSummaryReport`.

**Goal:** A weekly, per-buyer award-summary report (backend endpoint + admin page) — the single most-cited missing report — plus removal of the dead `/cohort-mapping` launcher link.

**Key design decision (locked with the human 2026-07-11):** the legacy report sourced from `EcoATM_DA.BuyerSummary`/`BuyerDetail` (the Device-Allocation domain, **0-files / not ported**, gate open-Q2). The modern report **re-sources from the R3 award data**. Scope = **core now, defer `WeeklyBudget`** (its modern home — likely PO weekly commitments — is unconfirmed; add it as a fast-follow).

## Legacy report shape (`JSON_BuyerAwardsSummaryReport`, per-buyer weekly row)
`BuyerCode, BuyerName, SalesQty, Amount, WeeklyBudget, PreviousWeekSalesQty, PreviousWeekAmount, PreviousWeekWeeklyBudget, CurrentEcoATMGradeDetails`.
**Build now:** `BuyerCode, BuyerName, SalesQty, Amount, GradeDetails, PreviousWeekSalesQty, PreviousWeekAmount`. **Defer:** `WeeklyBudget` + `PreviousWeekWeeklyBudget`.

## Modern data source (research the exact joins during build)
- **Award qty + amount:** `auctions.round3_buyer_data_reports` (`total_quantity`, `total_payout`, `buyer_code`, `company_name`, `auction_id`) — the existing `Round3ReportService`/`Round3BuyerDataReportRepository` (net-new R3 buyer-data report) is the closest analog; the award summary is a **weekly aggregation** of it, not per-auction. Confirm how a `scheduling_auction`/`auction_id` maps to a **week** (the reporting week — check `scheduling_auctions` for a week/date reference) and aggregate per (buyerCode, week). If round3_buyer_data_reports doesn't fully carry awarded qty/$, fall back to `auctions.bid_data` winners for the R3 round.
- **BuyerName / company:** buyer-code → company (`buyer_mgmt` / `round3_buyer_data_reports.company_name`).
- **GradeDetails (`CurrentEcoATMGradeDetails`):** per-buyer grade breakdown from MDM (`mdm.grade`) joined through the awarded devices/bid_data — research `SUB_LoadBuyerDetails` for the exact grade roll-up; a compact grade-summary string per the legacy sample.
- **Previous-week:** re-run the same aggregation for `week − 1`; join on buyerCode to produce the prior-week columns (0 when the buyer had no prior-week awards).

## Tasks
### Task 1 — Backend: report service + endpoint
**Files:** `service/admin/BuyerAwardSummaryService.java`, `dto/BuyerAwardSummaryRow.java` (+ `BuyerAwardSummaryResponse` if paged), a query on the R3 repo (or a new `BuyerAwardSummaryRepository`), `controller/admin/BuyerAwardSummaryController.java`, `security/SecurityConfig.java` (matcher). Test: service test + `BuyerAwardSummaryIT` (real Postgres, seed R3 rows across two weeks).
- `GET /api/v1/admin/reports/buyer-award-summary?week=<weekId>` → `List<BuyerAwardSummaryRow>` (or paged). `week` optional → default latest reporting week; `SUB_GetBuyerAwardSummaryReportForWeek` semantics.
- Aggregate per (buyerCode, week): sum awarded qty → `salesQty`, sum payout → `amount`, grade breakdown → `gradeDetails`; join the prior week for `previousWeekSalesQty`/`previousWeekAmount`. `weeklyBudget` omitted (deferred — leave the DTO field out or null with a doc note).
- **Authz:** `hasAnyRole('Administrator','SalesOps')` (match the sibling admin auction/report surfaces) — explicit `SecurityConfig` matcher + class `@PreAuthorize`; identity from JWT.
- **Acceptance:** seed two weeks of R3 awards for a buyer → the endpoint returns one row per buyer for the queried week with correct qty/amount + non-zero prior-week columns; empty week → empty list; wrong role → 403 (IT).

### Task 2 — Frontend: admin page + kill the dead link
**Files:** new `frontend/src/app/(dashboard)/admin/auctions-data-center/buyer-award-summary/page.tsx` (+ client/table components), `frontend/.../admin/auctions-data-center/page.tsx:45` (remove/repoint the dead `Cohort Mapping → /cohort-mapping` tile). Test: RTL.
- Admin page: week selector + a table of the rows (buyer, sales qty, amount, grade details, prev-week qty/amount), matching the existing admin-data-center table styling (reuse the Round3 report page's patterns). camelCase fields per the backend contract (no snake_case — the app serializes camelCase).
- **Remove the dead `/cohort-mapping` launcher tile** (`page.tsx:45`) — it 404s (Cohort Mapping report is a separate, deferred family). Either delete the tile or repoint it to a "not yet built" state; deleting is cleanest.
- **Acceptance:** page renders the report from the endpoint (mock fetch in RTL); no dead `/cohort-mapping` link remains; `npm run build` green.

## Deferred (fast-follow, documented)
- `WeeklyBudget` + `PreviousWeekWeeklyBudget` — once the modern weekly-budget source is confirmed (candidate: sum of `po_detail.price`×commitment for the buyer's POs covering that week — ties to gap 0.1's PO week model). Add the column to the DTO + query + page then.
- The other three legacy report families (Cohort Mapping, EB Calibration, Buyer Bid Summary/Detail) remain out of scope (gap open-Q8 — confirm still-used before building).

## Execution
Two tasks, sequential (backend → frontend), fresh Opus SDD agents + review gate; then merge + push. Update `docs/api/rest-endpoints.md`, `docs/app-metadata/modules.md` (reports), `docs/testing/coverage.md`.
