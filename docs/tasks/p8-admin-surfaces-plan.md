# P8 — Admin Surfaces Master Plan

**Source:** `docs/tasks/qa-wholesale-tests-port-plan.md` (P8 line in §9 Pending).
**Goal:** Build the 6 admin surfaces required to port the remaining ~50 QA admin tests + unblock 6 of 12 hand-on-table behavioral rules (3, 6, 7, 8, 9, 10 in the catalog).
**Strategy:** Plan all 6 first, then execute the independent ones in parallel. Keep dependencies explicit.

---

## 1. The six surfaces, current state, and gaps

For each surface: **Backend** = REST endpoints + service + tests, **Frontend** = page(s) + components + e2e tests. ✅ = exists, ⚠️ = partial, ❌ = missing.

### A. BidAsBidder (admin impersonation)

QA usage: `NavMenuPage.BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin('AA600WHL')` → admin lands on the buyer's dashboard.

| | State | Notes |
|---|---|---|
| Backend | ✅ | `BidderDashboardController` already authorizes `Administrator` (`@PreAuthorize("hasAnyRole('Bidder','Administrator')")`). No new endpoint needed; admins can already call `GET /api/v1/bidder/dashboard?buyerCodeId=…` for any buyer's code. |
| Frontend | ❌ | No "Bid as Bidder" admin page. Need: a page that lists buyer codes with a search/dropdown, navigates to `/bidder/dashboard?buyerCodeId=…` on selection. |
| QA tests unlocked | ~5 | Round-3 admin tests, "Sales can submit on behalf of buyer" |

**Effort:** S. Pure frontend. Picker UI + route wiring; reuse `BuyerCodeChip`.
**Dependencies:** none.

### B. Auction Scheduling (round transitions)

QA usage: `ACC_AuctionSchedulePage.{deleteRecentAuctionWeek, createAuctionAndStartRoundOne, startAuctionRoundBySchedule(2|3), endAuctionRoundBySchedule(1|2|3), ensureRoundOneIsOpen, ensureRoundTwoIsStarted}`

| | State | Notes |
|---|---|---|
| Backend | ⚠️ | `AuctionController` has POST (create), GET, PUT schedule, POST unschedule, DELETE, R1 init. **Missing:** start-round-N (N>1), end-round-N. |
| Frontend | ⚠️ | `/admin/auctions-data-center/auctions/page.tsx` (251 LoC) lists auctions; `schedule-auction/page.tsx` (247 LoC) creates/schedules. **Missing:** UI for start-round-N, end-round-N. |
| QA tests unlocked | ~10 | The orchestration ("close R1", "start R2", "close R2", "start Upsell") that wraps the buyer scenarios in DataGridAllRoundTests + Flow1/4/5. |

**Effort:** M. Backend: 2 new endpoints (`POST /api/v1/admin/auctions/{auctionId}/rounds/{n}/start|end`) + service methods. Frontend: extend the auctions list with action buttons.
**Dependencies:** none for the page itself; the resulting endpoints are used by E. and F. test seeds optionally.

### C. Bid Data Admin (view + remove)

QA usage: `ACC_BidDataPage.{selectBidDataByRoundAndBuyerCode, removeBidByBidPrice, isSelectedBidRemoved, getFilterResultBySubmittedBidAmountGreaterThanZero}`

| | State | Notes |
|---|---|---|
| Backend | ❌ | No admin endpoints for listing or removing bid_data by round + buyer. The bidder's own `PUT /bid-data/{id}` exists but isn't an admin tool. **Missing:** `GET /api/v1/admin/bid-data?round=…&buyerCode=…`, `DELETE /api/v1/admin/bid-data/{id}`. |
| Frontend | ❌ | "Bid Data" tile on `/admin/auctions-data-center/page.tsx:28` exists but routes to a 404. No page built. |
| QA tests unlocked | ~5 | "Sales can remove bids", removed-bid-doesn't-reappear (rule 9), bid-data audit. |

**Effort:** M. Backend: 2 endpoints + admin role guard + audit log entry. Frontend: data-grid filtered by `(round, buyerCode)` with a "Remove" action.
**Dependencies:** none.

### D. Qualified Buyer Codes Admin (qualify/unqualify)

QA usage: `ACC_QualifiedBuyerCodePage.{selectQualifyBuyerListbyWeekAndRound, setBuyerCheckbox(code, "Check"|"Uncheck"), getBuyerCodeQualificationType, isBuyerCodeQualified}`

| | State | Notes |
|---|---|---|
| Backend | ❌ | Schema present (`buyer_mgmt.qualified_buyer_codes` with `included`, `qualification_type`, `is_special_treatment`). **Missing:** `GET /api/v1/admin/qualified-buyer-codes?schedulingAuctionId=…` returning the full list with current state, `PATCH /api/v1/admin/qualified-buyer-codes/{id}` to flip `included` (which auto-sets `qualification_type='Manual'`). |
| Frontend | ❌ | No "Qualified Buyer Codes" page. Add to `/admin/auction-control-center/qualified-buyer-codes/`. |
| QA tests unlocked | ~8 | Manual qualify/unqualify (rules 6, 7), the "After R1 ends, Sales can Qualify/Unqualify" flows. |

**Effort:** M. Backend: 2 endpoints + the qualification-type-flip-on-manual-edit logic. Frontend: dropdown for round, table with checkbox per buyer code.
**Dependencies:** none. R2 Criteria (E.) is conceptually adjacent but operationally independent.

### E. R2 Selection Criteria Admin

QA usage: `ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings('All Buyers' | 'Bid Buyers Only', 'Full Inventory' | 'Inventory With Bids', 'Yes' | 'No')`

| | State | Notes |
|---|---|---|
| Backend | ❌ | Schema present (`auctions.bid_round_selection_filters` — currently empty). **Missing:** `GET /api/v1/admin/round-criteria/{round}` and `PUT /api/v1/admin/round-criteria/{round}`. The bidder's `landingRoute` already reads `bid_round_selection_filters` (referenced in `BidDataCreationRepository.CTE_SQL` as `selection_filter` CTE — but currently a no-op because the table is empty). |
| Frontend | ❌ | No page. Add to `/admin/auction-control-center/r2-criteria/`. |
| QA tests unlocked | ~5 | The "Sales can configure R2 criteria" pre-condition tests + the downstream gating that depends on the selected criteria (rules 4, 5). |

**Effort:** S-M. Backend: 2 endpoints + a `bid_round_selection_filters` upsert. Frontend: 3 dropdowns + Save button.
**Dependencies:** **Soft on D** — the criteria UI is meaningful only if QBCs reflect the criteria choices. If we test E in isolation we can stop at "criteria persists". To test the cascade ("set criteria → R1 ends → buyers eligible per criteria"), we need D wired too.

### F. R3 Bid Report

QA usage: `RoundThreeBidReportPage.{selectSecondWeekFromDropdown, isBuyerCodePresentInReport(code)}`

| | State | Notes |
|---|---|---|
| Backend | ⚠️ | Schema present (`auctions.round3_buyer_data_reports`). **Missing:** `GET /api/v1/admin/round3-reports?weekId=…` returning the report rows. |
| Frontend | ❌ | No "Round 3 Bid Report by Buyer" page. Add to `/admin/auctions-data-center/round3-bid-report/`. |
| QA tests unlocked | ~3 | Sales views qualified buyer list per week in R3. |

**Effort:** S. Backend: 1 read endpoint. Frontend: week dropdown + grid.
**Dependencies:** none.

---

## 2. Dependency graph + parallelism

```
A (BidAsBidder) ─────┐
                     │
B (Scheduling) ──────┤
                     │
C (Bid Data) ────────┼──> all independent ──> can run in parallel
                     │
D (Qualified BC) ────┤
                     │
F (R3 Report) ───────┘

E (R2 Criteria) ─── soft-depends on D for cascade tests; standalone otherwise
```

**No hard dependencies between any two surfaces.** All 6 can be built in 6 parallel branches if we have the agents/people. Practical lanes:

| Lane | Surfaces | Why grouped |
|---|---|---|
| Lane 1 — small frontend ports | A (BidAsBidder), F (R3 Report) | Smallest, lowest risk; same dev can sequence them |
| Lane 2 — round-state machinery | B (Scheduling round transitions) | Touches the scheduling controller in ways that could conflict if anyone else also changes it |
| Lane 3 — bid lifecycle admin | C (Bid Data), D (Qualified Buyer Codes) | Both touch `auctions.bid_data` / `qualified_buyer_codes`; same dev avoids merge thrash |
| Lane 4 — criteria | E (R2 Criteria) | Schema-only addition; isolated; can land last and immediately enable the cascade tests |

3 lanes can run truly in parallel (A+F, B, C+D). Lane 4 (E) starts after D ideally but can also go in parallel if the dev is fine landing the cascade tests in a follow-up.

---

## 3. Per-surface task breakdown (so each lane has a clear PR-sized scope)

### Lane 1A — BidAsBidder (~1-2 days)

**Files**
- `frontend/src/app/(dashboard)/admin/bid-as-bidder/page.tsx` — new admin page
- `frontend/src/app/(dashboard)/admin/bid-as-bidder/BuyerCodePicker.tsx` — searchable dropdown
- `frontend/src/lib/admin/buyerCodes.ts` — `GET /api/v1/admin/buyer-codes?search=…` wrapper (endpoint may already exist; verify)
- `frontend/src/components/chrome/SidebarLink.tsx` — add nav entry "Bid as Bidder" if not present
- `frontend/tests/pages/BidAsBidderPage.ts` — POM
- `frontend/tests/e2e/admin-bid-as-bidder.spec.ts` — 3-5 tests

**Tests**
- Admin lands on Bid as Bidder page; picker renders
- Search "AA600" filters to matching codes
- Selecting a code routes to `/bidder/dashboard?buyerCodeId=…`
- Admin sees the bidder dashboard rendered as the chosen buyer

**Backend touch:** none (admin already authorized on bidder endpoints).

### Lane 1B — R3 Bid Report (~1-2 days)

**Files**
- `backend/.../controller/admin/Round3ReportController.java` — `GET /api/v1/admin/round3-reports?weekId=…`
- `backend/.../service/admin/Round3ReportService.java` + repo + DTO
- `backend/.../tests/...Round3ReportControllerTest|ServiceTest` — JUnit
- `frontend/src/app/(dashboard)/admin/auctions-data-center/round3-bid-report/page.tsx`
- `frontend/tests/pages/Round3ReportPage.ts`
- `frontend/tests/_fixtures/round3-report-seed.sql` — seed a `round3_buyer_data_reports` row
- `frontend/tests/e2e/admin-round3-report.spec.ts` — 2-3 tests

**Tests**
- Page renders with week dropdown
- Selecting a week loads the report; expected buyer codes appear
- Empty week → "No data" state

### Lane 2 — Auction Scheduling round transitions (~2-3 days)

**Files**
- `backend/.../controller/AuctionController.java` (extend) — `POST /api/v1/admin/auctions/{auctionId}/rounds/{n}/start|end`
- `backend/.../service/auctions/SchedulingAuctionService.java` (extend) — `startRound(auctionId, n)`, `endRound(auctionId, n)` with idempotency + validation (can't start a round if previous is still open, etc.)
- `backend/.../tests/...SchedulingAuctionServiceTest` — JUnit cases per state-machine transition
- `frontend/src/app/(dashboard)/admin/auctions-data-center/auctions/page.tsx` (extend) — add Start/End round buttons per row
- `frontend/src/lib/admin/scheduling.ts` — add `startRound`, `endRound` API wrappers
- `frontend/tests/pages/AuctionsListPage.ts`
- `frontend/tests/e2e/admin-auction-scheduling.spec.ts` — 4-6 tests

**Tests**
- Admin starts R1 on a Scheduled auction → R1 status flips to Started
- Admin ends R1 → status flips to Closed; R2 becomes startable
- Admin tries to start R2 while R1 is open → 409
- Admin starts R2 / R3 successfully

**Test-data seed:** new fixture `admin-scheduling-fresh-auction.sql` to set up a Scheduled (not yet started) auction.

### Lane 3A — Bid Data Admin (~2-3 days)

**Files**
- `backend/.../controller/admin/BidDataAdminController.java` — `GET /api/v1/admin/bid-data?bidRoundId=…&buyerCodeId=…`, `DELETE /api/v1/admin/bid-data/{id}`
- `backend/.../service/admin/BidDataAdminService.java` + tests
- `frontend/src/app/(dashboard)/admin/auctions-data-center/bid-data/page.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/bid-data/BidDataAdminGrid.tsx`
- `frontend/tests/pages/BidDataAdminPage.ts`
- `frontend/tests/e2e/admin-bid-data.spec.ts` — 4-6 tests

**Tests**
- Filter by (round, buyerCode); only matching rows appear
- Delete a row by id → row disappears, response 200
- Filter by `submitted_bid_amount > 0` → only submitted rows
- Non-admin gets 403 on the DELETE endpoint

### Lane 3B — Qualified Buyer Codes Admin (~2-3 days)

**Files**
- `backend/.../controller/admin/QualifiedBuyerCodeController.java` — `GET ?schedulingAuctionId=…`, `PATCH /{id}` (toggle `included`, sets `qualification_type='Manual'`)
- `backend/.../service/admin/QualifiedBuyerCodeAdminService.java` + tests
- `frontend/src/app/(dashboard)/admin/auction-control-center/qualified-buyer-codes/page.tsx`
- `frontend/tests/pages/QualifiedBuyerCodesPage.ts`
- `frontend/tests/e2e/admin-qualified-buyer-codes.spec.ts` — 4-6 tests

**Tests**
- List loads QBCs for the chosen scheduling_auction
- Unchecking a buyer flips `included=false`, `qualification_type='Manual'`
- Re-checking flips `included=true`, `qualification_type='Manual'`
- After unchecking, the bidder dashboard for that buyer returns DOWNLOAD mode (cascade with §4 below)

### Lane 4 — R2 Selection Criteria (~1-2 days)

**Files**
- `backend/.../controller/admin/RoundCriteriaController.java` — `GET /api/v1/admin/round-criteria/{round}`, `PUT /api/v1/admin/round-criteria/{round}`
- `backend/.../service/admin/RoundCriteriaService.java` + tests
- `frontend/src/app/(dashboard)/admin/auction-control-center/r2-criteria/page.tsx`
- `frontend/tests/pages/R2CriteriaPage.ts`
- `frontend/tests/e2e/admin-r2-criteria.spec.ts` — 2-4 tests

**Tests**
- Save the three settings (regular qualification, inventory option, STB override) → values persist
- Re-load the page → saved values rendered

**Cascade tests (after Lane 3B lands):**
- Set criteria to `Bid Buyers Only` + run an end-to-end: AA600WHL bids R1, AA999WHL doesn't. After R1 closes + criteria-driven QBC sync, AA999WHL gets DOWNLOAD on R2 dashboard.

---

## 4. Cross-cutting concerns

- **Authorization.** Every new admin endpoint must require `hasAnyRole('Administrator', 'SalesOps')` (or whichever roles QA expects — check `userRole.ADMIN` vs `userRole.SALES`). Existing code uses `@PreAuthorize`; follow the same pattern.
- **Audit logging.** Bid Data DELETE and QBC PATCH should write rows to `buyer_mgmt.buyer_code_change_logs` (or a similar audit table) so admin actions are traceable. Reference `auctions.reserve_bid_audit` pattern from the EB module.
- **Test-data fixtures.** Each new spec gets its own SQL fixture under `frontend/tests/_fixtures/`. Reuse the lighter `UPDATE-only` reset pattern from `wholesale-r1-active.sql` to keep `beforeEach` fast (~30ms).
- **POM convention.** `frontend/tests/pages/*.ts`, exported via `tests/pages/index.ts`. Extend `BidderDashboardPage` only when re-using its locators; otherwise create per-surface POMs.
- **Live-test gate.** Reuse `isBackendAvailable()` + `applyFixture()`. After the actuator health fix, `FORCE_LIVE_TESTS=1` won't be needed for normal runs.
- **Pixel parity.** Admin pages should match QA Mendix layouts where possible — capture screenshots first via Playwright MCP and document the target visual under `docs/qa-reference/`. Token reuse: warm-card `#f7f5f1`, dark-teal `#102e33`, Founders Grotesk font (already loaded in `globals.css`).

---

## 5. Suggested execution order

If only **one** developer / agent at a time:

1. **Lane 1A — BidAsBidder.** Smallest, no backend; gets the muscle going.
2. **Lane 1B — R3 Bid Report.** Same dev, same shape, builds confidence.
3. **Lane 2 — Auction Scheduling round transitions.** Unblocks the orchestration tests in DataGridAllRoundTests.
4. **Lane 3A — Bid Data Admin.** Needed for "Sales can remove bids" flows.
5. **Lane 3B — Qualified Buyer Codes Admin.** Highest leverage — unlocks rules 6, 7, 8 from the hand-on-table catalog.
6. **Lane 4 — R2 Criteria + cascade tests.** Lands last so the cascade tests have D in place.

If **multiple** agents / devs in parallel:

| Agent | Owns | Effort | Notes |
|---|---|---|---|
| Agent 1 | Lane 1A → Lane 1B (sequence) | ~3 days | Easy ramp |
| Agent 2 | Lane 2 alone | ~3 days | Schema-touching; isolated |
| Agent 3 | Lane 3A → Lane 3B (sequence) | ~5 days | Same area; same dev to avoid conflicts |
| Agent 4 | Lane 4 (start after Agent 3 lands D) | ~2 days | Cascade tests follow |

End-state: ~50 new admin E2E tests + 6 backend service test classes + the 6 admin pages.

---

## 6. Risks + mitigations

| Risk | Likelihood | Mitigation |
|---|---|---|
| QA POM expects Mendix-specific HTML structure (e.g., `mx-name-*` classes) | High | Don't port POMs verbatim — read the QA POM to understand intent, write fresh POMs against the local DOM. |
| Admin role authorization gaps (some endpoints accept Bidder by mistake) | Med | Add a one-test-per-endpoint authorization regression — non-admin gets 403. Pattern: see existing `wholesale-buyer-login.spec.ts` for the fixture style. |
| Bid Data DELETE breaks live bid_round state for other tests | Med | Always use the seed fixture's DELETE+CASCADE in admin tests' `beforeEach`. Do NOT share state across describe blocks for admin tests. |
| Lane 3B's cascade tests collide with Lane 4's tests | Low | Both should reset state in `beforeEach`. The cascade test is owned by Lane 4; Lane 3B only tests the local PATCH semantics. |
| Audit logging spec drift (which table, which columns) | Med | Reference `reserve_bid_audit` as the in-repo template; document the choice in a small ADR before implementing. |

---

## 7. Out-of-scope (do not include in P8)

- **Bid Data import/export tooling** — admin convenience features beyond the QA test surface. Defer.
- **Auction recovery / bulk re-init endpoints** — present in legacy Mendix but not exercised by QA tests. Defer.
- **Sales rep / cohort mapping admin pages** — present as tiles on AuctionsDataCenter but no QA test coverage. Defer.
- **Inventory editor** — read-only listing exists; editor is a separate sub-project.
- **Round 3 init job** — covered by `auction-lifecycle-cron-plan.md` (existing sub-project).

---

## 8. Definition of done for P8

- All 6 admin pages reachable from the existing tile menu (no 404s on the QA-tested tiles).
- All admin endpoints authorize at least `Administrator` (and `SalesOps` where Mendix did).
- Each surface has a backend service test class with ≥80% line coverage of the new code.
- Each surface has a Playwright POM in `frontend/tests/pages/` and a spec in `frontend/tests/e2e/admin-*.spec.ts`.
- Cumulative E2E count: ~70 (existing 18 + ~50 new admin).
- `npx playwright test tests/e2e/wholesale-*.spec.ts tests/e2e/admin-*.spec.ts` runs green against a fresh `mvn spring-boot:run` + applied fixtures.
- Plan file `docs/tasks/qa-wholesale-tests-port-plan.md` §9 P8 marked shipped, with a per-lane sub-section noting test counts and any deferred items.
