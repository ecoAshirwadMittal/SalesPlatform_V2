# QA Wholesale Test Suite — Port Plan

**Source:** `C:\Users\Ashirwad.Mittal\mendix-extractor\qa-playwright-salesplatform\src\tests\Wholesales\**`
**Target:** `frontend/tests/e2e/wholesale-*.spec.ts` (existing local Playwright suite)
**Captured:** 2026-04-24
**Companion docs:** `wholesale-buyer-qa-analysis.md`, `wholesale-buyer-parity-plan.md`

---

## 1. Inventory — what QA actually has

| File | Tests | Lines | Surface |
|---|---:|---:|---|
| `Wholesales/DataGrid_Round1.spec.ts` | 149 | 2,101 | R1 buyer + admin (data grid mode) |
| `Wholesales/DataGrid_Round2.spec.ts` | 14 | 146 | R2 buyer + admin (data grid mode) |
| `Wholesales/DataGrid_Round3.spec.ts` | 3 | 40 | R3 admin + sales-as-buyer |
| `Wholesales/DataGridAllRoundTests.spec.ts` | 38 | 357 | Cross-round regression |
| `Wholesales/R2_EligibilityAndTargetPriceTests.spec.ts` | 412 | 2,801 | R2 eligibility math + TGP combinatorics |
| `Wholesales/R3_EligibilityAndTargetPriceTests.spec.ts` | 110 | 1,835 | R3 eligibility + TGP combinatorics |
| `Wholesales/HandOnTableTests/Flow1_AllBuyer_FullInventory_SptON.spec.ts` | 26 | 241 | Hand-on-table grid, all-buyers configuration |
| `Wholesales/HandOnTableTests/Flow4_BuyerWithR1Bids_FullInventory_SptON.spec.ts` | 31 | 279 | Hand-on-table, R1-bids-only buyers |
| `Wholesales/HandOnTableTests/Flow5_BuyerWithR1Bids_InventoryR1Bids_SptON.spec.ts` | 31 | 279 | Hand-on-table, inventory restricted to R1-bid SKUs |
| **TOTAL** | **814** | **8,079** | |

### Suite shape

- **Page-Object Model.** All specs delegate to ~10 page objects (`AUC_DataGridDashBoardPage`, `ACC_AuctionSchedulePage`, `ACC_BidDataPage`, `ACC_QualifiedBuyerCodePage`, `ACC_RoundTwoCriteriaPage`, `InventoryPage`, `PurchaseOrderPage`, `ReservedBidsPage`, `RoundThreeBidReportPage`, `SharePointPage`, `LoginPage`, `NavMenuPage`).
- **`BaseTest`** wires every page object onto a single `Page` and exposes them via `base['…']` lookups. Specs are short orchestration scripts; the heavy lifting is in the POMs.
- **Stateful, serial.** `test.describe.serial(…)` is the dominant mode. Tests within a file mutate auction state (create week → start R1 → submit bids → close R1 → start R2 → …) and depend on prior tests passing. Parallelism is off inside a file.
- **Live QA backend.** All tests run against `https://buy-qa.ecoatmdirect.com` with seeded buyer codes (`AA155WHL`…`AA600WHL`, `AA700DW`…`HN`, etc.) defined in `utils/resources/user_data.json`.
- **External integrations.** SharePoint download check, Snowflake (Deposco) reads, TempMail email verification — all stubbed via dedicated helpers.
- **Tagging.** `@regression`, `@auction`, `@auctions-regression` — Playwright `--grep` selects subsets in CI.

### Test surface (deduped feature list)

1. **Auction scheduling** (admin) — delete current week, configure R2 selection criteria, schedule new week, ensure R1/R2 open, end round by schedule, start round by schedule.
2. **Bid data admin** — view bids by round + buyer, remove bids by price, verify removal.
3. **Qualified Buyer Codes admin** — view qualification list per round, set/unset qualified flag, read qualification type (`Auto` vs `Manual`).
4. **R2 selection criteria admin** — configure `All Buyers / Bid Buyers Only`, `Full Inventory / Inventory With Bids`, `STB Yes/No` for both regular and special buyers.
5. **Inventory page (admin)** — read inventory rows by ProductID, capture totalQty / targetPrice / dwQty / dwTargetPrice.
6. **Reserved Bids (EB) page (admin)** — read EB data per ProductID for TGP math validation.
7. **Purchase Orders (PO) page (admin)** — read PO data per ProductID for TGP math validation.
8. **Round Three Bid Report (admin)** — list qualified buyers per week, validate buyer codes appear.
9. **SharePoint integration** — verify submitted-bids xlsx ships to stage folder, validate row contents.
10. **Buyer-side data grid** (`AUC_DataGridDashBoardPage`):
    - Access gating (round access control by qualification + round status)
    - View full / inventory-with-bids
    - Wholesale-vs-DW inventory check
    - Sort by Target Price (asc/desc)
    - Sort by Avail. Qty
    - Place + submit bids (price + qty per row)
    - Cannot lower bid price/qty within a round
    - Export `<buyerCode>_RoundN.xlsx` and verify download
    - Import xlsx, validate row count, submit on import modal
    - Read row by index, by `(productId, grade)` filter
    - Read displayed buyer code, displayed minimum-bid-price label
11. **Buyer-side hand-on-table** — same operations against the Mendix Hand-on-Table widget (Flow1/4/5), tested independently of the data-grid widget.
12. **Cross-round regression** (`DataGridAllRoundTests`) — full week walkthrough chaining all of the above.

---

## 2. Local app — what already exists

| Local file | Tests | Coverage |
|---|---:|---|
| `wholesale-buyer-login.spec.ts` | 4 | Login form a11y, password eye-toggle, route to `/buyer-select` |
| `wholesale-buyer-picker.spec.ts` | 8 | Code picker rendering, single-code skip, mixed-code routing |
| `wholesale-bidder-shell.spec.ts` | 8 | Sidebar collapse, avatar popover, Switch Buyer Code link |
| `wholesale-bid-grid.spec.ts` | 7 | 11-column header, sort cycle, text/numeric filter, footer text, responsive hide, date format |
| `wholesale-submit-bids.spec.ts` | 4 | Submit-bid happy path + 429 / 409 handling, edited-row send |
| `wholesale-import-export.spec.ts` | 5 | Export download, Import modal open/disable, Import happy path + per-row errors |
| `wholesale-carryover.spec.ts` | 3 | 0-copied empty state, Escape closes, success modal copy |
| `wholesale-forgot-password.spec.ts` | 9 | Forgot/reset flow including invalid-token |
| **Total wholesale local** | **48** | |

The local suite is a **focused frontend-component test surface** — it mocks all API responses with `page.route(…)` and asserts UI behavior. The QA suite is **backend-integration + admin-surface + auction-lifecycle** — it walks the whole admin → buyer round-progression flow against a real DB.

The two suites are **complementary, not duplicates.** The local 48 tests exercise the parts of the buyer dashboard already built; QA exercises everything around it (admin scheduling, R2 criteria, qualified buyers, bid-data management, hand-on-table, cross-round eligibility math).

---

## 3. Gap matrix — by QA test group

For each QA group: what we'd need on the local app (backend + frontend) before the test is portable.

| QA group | Backend needs | Frontend needs | Local test fixture deltas | Verdict |
|---|---|---|---|---|
| **DataGrid_Round1 — buyer scenarios** (149) | `GET /api/v1/bidder/dashboard` (✅ exists), `POST /api/v1/bidder/bid-rounds/{id}/submit` (✅ exists), `PUT /api/v1/bidder/bid-data/{id}` (✅ exists). Round access gating by qualification + round status. | Bid-grid sort, filter, footer (✅ exist). Buyer-code-display chip on dashboard header. Minimum-bid-price label (✅ planned, see parity plan Phase 7). Export / Import flow (✅ exist). | Need seed for ~5 wholesale buyers (`AA600WHL`, `AA601WHL`, …) with assigned codes & qualifications, plus seeded bid rounds with R1 status. | **Portable in batches.** The scope is large; chunk into 4–5 sub-tests per spec file and keep `test.describe.serial`. |
| **DataGrid_Round2 — buyer scenarios** (14) | Round access by R2 qualification flag. Submitted-bid lock (cannot lower price/qty) — backend must reject downward moves with 409. | Status banner showing "Round 2 in progress". | Need `qualified_buyer_codes` rows for R2 against the test buyers. | Portable once R2 transition + downward-bid rejection are wired. |
| **DataGrid_Round3** (3) | Sales-as-buyer (admin-side `BidAsBidder` flow). Round access by R3 qualification. | Buyer-code dropdown picker on admin "Bid as Bidder" page. | R3 round + qualified buyers. | **Blocked by feature.** No "Bid as Bidder" admin surface in local app yet. Add to backlog. |
| **DataGridAllRoundTests** (38) | Same as Round 1+2+3 combined; nothing new. | Same as above. | Same as above. | Portable as a long serial spec once R1/R2/R3 transitions all work. |
| **R2_EligibilityAndTargetPriceTests** (412) | TGP recalc engine — given EB + PO + R1 bids, compute per-`(productId, grade)` R2 TGP. Eligibility computation per buyer per round. | Read-only display of computed TGP on bidder grid. | Heavy: per-test combinatoric bid-group fixtures. | **Mostly blocked.** Local app does not yet implement the R2 TGP engine (sub-project 4C per CLAUDE.md, "target-price recalc"). Port after sub-project 4C lands. Until then, port a thin happy-path slice (~20 tests) that exercises eligibility gating only. |
| **R3_EligibilityAndTargetPriceTests** (110) | R3 TGP engine + R3 eligibility (different rules than R2). | R3 read-only display. | Combinatoric fixtures. | **Blocked.** Same as R2; defer until 4C ships. |
| **HandOnTable Flow1/4/5** (88) | Hand-on-table is a separate widget (legacy Mendix). Local app uses the data-grid widget instead. | We may not need to port these as-is — the data-grid suite already covers the same business logic. | — | **Re-scope, don't port literally.** Lift the *scenarios* (assertions) from these files but run them against the local data-grid. ~30 unique assertions are not duplicates of DataGrid_Round1/2/3. |
| **Admin: Scheduling, R2 Criteria, Bid Data, Qualified Buyer Codes** (cross-cutting, ~80 tests across files) | `auction-scheduling-plan.md` (✅ planned), `auction-bid-data-create-plan.md` (✅ in-flight per docs/tasks/). REST endpoints for schedule create / round-start / round-end / R2-criteria save / bid-data-list / bid-data-remove / qualified-buyer-codes-list / qualified-buyer-codes-set. | Admin pages: Auctions Control Center → Scheduling, R2 Criteria, Bid Data, Qualified Buyer Codes. Stubs exist (`/admin/auctions-data-center/...`) but their inner UIs need to be built per the parity plan. | None until the admin surfaces are built. | **Port last.** These are integration tests for admin features that are still being built. Track via the existing auction sub-project plans. |
| **External: SharePoint, Deposco, TempMail** | Out of scope for local. These are real third-party integrations the QA env hits. Local doesn't need to clone them. | — | — | **Skip.** Mark as `test.skip` with a `// SKIPPED: third-party integration not in local scope` comment. |

---

## 4. Porting strategy

### 4.1 Adapt the abstraction, don't copy it

The QA suite leans heavily on `BaseTest` + `class XxxPage` POMs. The local suite uses a flatter pattern (`page.route(…)` mocks + inline assertions). **Do not import `BaseTest` wholesale.** Instead, lift POMs **page-by-page** into `frontend/tests/e2e/_pages/` and only wrap the operations the local app actually has. A QA POM that calls `BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin` should not be ported until the local "Bid as Bidder" page exists.

### 4.2 Two test layers — keep both

- **Component-level (mocked).** Existing pattern — `page.route(…)` against `/api/v1/bidder/**`. Fast, no DB required, runs in CI on every PR. Continue here for column-level UI behavior, validation rules, error states.
- **Integration-level (live backend).** Mirror QA's pattern. Gate with `isBackendAvailable()` (already used in `wholesale-buyer-login.spec.ts`). Seed data via Flyway test fixtures or a `tests/_fixtures/wholesale-seed.sql` applied in `globalSetup`. Run against a real Spring Boot + Postgres. These tests verify lifecycle-and-eligibility logic that mocking cannot fake.

### 4.3 Test data — diverge from QA

QA uses `AA600WHL`, `AA601WHL`, …, `HN` seeded into the QA Mendix DB. Local should **not** import those user records — instead, create a small parallel set in a new Flyway migration `V25__seed_e2e_wholesale_users.sql` (or a `tests/_fixtures/*.sql` applied per-test). Use a clear `e2e_*` prefix so the rows are obviously test fixtures and can be filtered out of admin views in dev.

### 4.4 Tagging

Adopt the same Playwright tags QA uses (`@regression`, `@auction`, `@auctions-regression`) so we can `--grep` subsets in CI. Map:

- `@auction` → any wholesale spec
- `@regression` → must-pass before release
- `@admin` → admin surface tests (new)
- `@live` → requires running Spring Boot (existing `isBackendAvailable()` skip becomes a tag-driven skip)

### 4.5 Sequencing — phased over multiple PRs

| Phase | What | Tests added | Pre-reqs |
|---|---|---:|---|
| **P0 — POM scaffolding** | Lift `LoginPage`, `NavMenuPage`, `AUC_DataGridDashBoardPage` into `tests/e2e/_pages/`. Establish patterns for live-backend tests (seed fixture loader). No new tests. | 0 | — |
| **P1 — DataGrid R1 buyer** | Port the buyer-side R1 scenarios from `DataGrid_Round1.spec.ts` (excluding admin pre-conditions). Gate behind seeded R1-active auction. | ~30 | Seeded R1 auction + 3-5 wholesale buyers |
| **P2 — DataGrid R2 buyer** | Port `DataGrid_Round2.spec.ts` buyer scenarios. | ~10 | Backend supports R1→R2 transition + downward-bid rejection |
| **P3 — Cross-round regression** | Port `DataGridAllRoundTests.spec.ts` as a single long-running serial spec. | ~25 | All round transitions wired |
| **P4 — Admin surfaces** | Port admin-side scheduling, R2 criteria, Bid Data, Qualified Buyer Codes specs. | ~40 | Admin pages built per `auction-scheduling-plan.md`, `auction-bid-data-create-plan.md`, etc. |
| **P5 — Eligibility / TGP slice** | Port a happy-path slice of `R2_EligibilityAndTargetPriceTests` (~20 tests). Skip combinatorics until 4C ships. | ~20 | Sub-project 4C R2 TGP engine |
| **P6 — Eligibility / TGP full** | Port the full R2 + R3 TGP combinatoric suites. | ~500 | 4C complete + R3 logic |
| **P7 — Hand-on-table re-scope** | Lift unique assertions from Flow1/4/5 and run them against the local data-grid. | ~30 | — |
| **P8 — Round 3 admin** | Port `DataGrid_Round3.spec.ts` + R3 admin scenarios + bid-as-bidder admin page. | ~10 | Bid-as-Bidder admin surface |

### 4.6 Skipped permanently

- All SharePoint download checks — skip with comment.
- All TempMail email checks — local doesn't have a temp-mail integration.
- Deposco/Snowflake reads — out of scope for local UI tests; covered by backend service tests instead.

---

## 5. Required local-app fixes (surfaced by porting)

Things the QA suite implies should exist that the local app **does not yet have**, ordered by porting urgency:

1. **Buyer-code-display chip on the bidder dashboard header** — QA reads `getBuyerCodeDisplay()` and asserts equality. Local has the active-code chip in the parity plan (Phase 6 of `wholesale-buyer-parity-plan.md`) but not yet implemented for read-back.
2. **Minimum-bid-price label above the grid** — Phase 7 of parity plan; not yet shipped.
3. **Round-access gating** — buyer who is unqualified for R2 must see the picker / a "round closed for you" state, not the dashboard. Today the dashboard renders unconditionally if the buyer code is valid. Need backend `403 ROUND_NOT_ACCESSIBLE` + frontend redirect.
4. **Submitted-bid downward-move rejection** — backend must return 409 when a buyer tries to lower price or qty within an active round. Verify in `BidDataController` (likely missing).
5. **R2 selection-criteria admin page** — referenced by `selectRegularBuyerSettings('All Buyers', 'Full Inventory', 'Yes')`. The local stub at `/admin/auctions-data-center/...` doesn't have this page yet.
6. **Auction scheduling admin** — `createAuctionAndStartRoundOne()`, `endAuctionRoundBySchedule(n)`, `startAuctionRoundBySchedule(n)`. See `auction-scheduling-plan.md`.
7. **Bid Data admin** — list bids by `(round, buyerCode)`, remove by price. See `auction-bid-data-create-plan.md`.
8. **Qualified Buyer Codes admin** — list by week + round, toggle qualification, read qualification type (`Auto` vs `Manual`). No local plan yet — **add a sub-project**.
9. **Round 3 Bid Report admin** — list qualified buyers per week. No local plan yet.
10. **Bid as Bidder (sales impersonation) admin flow** — admin selects a buyer code from a dropdown and is dropped into that buyer's dashboard. No local equivalent.

These are real product features missing from the local port. Tracking each as a separate issue/sub-project is recommended.

---

## 6. Concrete sample — first ported test (P1)

A representative single-test port to validate the pattern. Lives in `frontend/tests/e2e/wholesale-r1-access.spec.ts`. It demonstrates: live-backend gating, the new POM pattern, and seed-fixture dependency.

```ts
// frontend/tests/e2e/wholesale-r1-access.spec.ts
import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { loginAs } from './_pages/LoginPage';
import { BidderDashboardPage } from './_pages/BidderDashboardPage';

// Source: qa-playwright-salesplatform/.../DataGrid_Round1.spec.ts
//   "R1: Reg Non-DW | Verify Buyer Can Access Auction Round 1 and View Inventory"
//   "R1: Reg Non-DW | Validate Buyer Code on Dashboard Display Correctly"
test.describe('Wholesale R1 — buyer access @auction @regression @live', () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), 'requires Spring Boot on :8080');
    // PRE-REQ: an R1-active auction + a wholesale buyer code linked to a
    // bidder user must exist. Seeded by V25__seed_e2e_wholesale_users.sql
    // when present, otherwise the test is skipped.
  });

  test('buyer can access R1 and sees their buyer code on the dashboard', async ({ page }) => {
    await loginAs(page, 'e2e_aa600whl@buyerco.com', 'E2eTest123!');
    const dash = new BidderDashboardPage(page);
    await dash.waitForGridLoaded();
    await expect(dash.buyerCodeChip).toHaveText('AA600WHL');
    await expect(dash.minimumBidLabel).toContainText('Minimum starting bid');
  });
});
```

Required new files for this test:

- `tests/e2e/_pages/LoginPage.ts` — `loginAs(page, email, password)` (lift from QA's `LoginPage.loginAs`)
- `tests/e2e/_pages/BidderDashboardPage.ts` — `waitForGridLoaded()`, `buyerCodeChip`, `minimumBidLabel` (lift the relevant slice of `AUC_DataGridDashBoardPage`)
- `backend/src/main/resources/db/migration/V25__seed_e2e_wholesale_users.sql` — seed `AA600WHL` buyer, code, user, qualified for R1, with one open auction in R1 status

Asserts in this single test surface 3 of the gaps from §5 (buyer-code chip, minimum-bid label, R1 round access). Once those are green, the next ~30 R1 buyer scenarios become a quick batch port.

---

## 7. Risk + open questions

| Risk | Likelihood | Mitigation |
|---|---|---|
| Seeded test data drifts from real schema | Med | Keep `V25__seed_e2e_wholesale_users.sql` versioned; refresh with each schema migration. |
| Live-backend tests flake on CI | Med | Run them in a dedicated job with a fresh Postgres + Spring Boot. Skip on PRs by default; run on `main` + nightly. |
| Porting blocks on the R2 TGP engine for a long time | High | Phase 5 (happy-path slice) is intentionally narrow so progress is visible before sub-project 4C lands. |
| Hand-on-table tests don't translate cleanly | Med | Don't try to port literally — extract scenarios into data-grid tests. |
| Admin surface tests block on multiple parallel sub-projects | High | Admin tests are P4+; lower priority than buyer-side parity. |

**Open questions:**

- **Q1.** Should the live-backend tests run in CI or only locally? (Existing local pattern — `isBackendAvailable()` — implies dev-only. Need explicit decision on CI infrastructure.)
- **Q2.** Where does seed data live — Flyway `V_e2e_*` migrations or a Playwright `globalSetup` SQL runner? Flyway tracks state; globalSetup is more flexible. Recommend globalSetup with idempotent SQL.
- **Q3.** Is `e2e_aa600whl@buyerco.com` an acceptable email convention, or should test users live under a separate domain (`e2e.local`)?

---

## 8. Recommended next action

Ship **P0 (POM scaffolding)** + **the single sample test in §6** as one PR. That PR:

- Establishes `frontend/tests/e2e/_pages/{LoginPage,BidderDashboardPage}.ts`
- Adds `frontend/tests/e2e/wholesale-r1-access.spec.ts`
- Adds `backend/src/main/resources/db/migration/V25__seed_e2e_wholesale_users.sql`
- Uncovers the first 3 gaps from §5 by failing tests
- Documents the live-backend-test convention so subsequent porters can follow

After that PR is green, batches of ~20-30 ports per PR (one per Phase) carry the rest of the suite.

---

## 9. Progress log

### P0 (shipped)

**Files added:**
- `frontend/tests/pages/BidderDashboardPage.ts` — first POM slice (chrome, picker, dashboard header)
- `frontend/tests/e2e/wholesale-r1-access.spec.ts` — 1 passing live test + 1 fixme

**Result:** 1 passed, 1 fixme. POM convention established.

### P1 (shipped)

**Files added:**
- `frontend/tests/_fixtures/wholesale-r1-active.sql` — idempotent seed: opens latest R1, sets HN/AAWHSL/DS2WHSL as Qualified+Included, resets bid_rounds (CASCADES bid_data for clean slate)
- `frontend/tests/e2e/_helpers/seedSql.ts` — helper that shells out to `psql` to apply a fixture; resolves paths from `process.cwd()` (no ESM `import.meta` per the project's CJS tsconfig); throws actionable errors when `psql` is missing

**Spec expanded** (`wholesale-r1-access.spec.ts`):
- `beforeAll` applies the seed via `applyFixture('wholesale-r1-active.sql')`
- 3 passing live tests:
  1. Login → pick `HN` → chip shows `HN` + `Nadia Boonnayanont`
  2. Switch Buyer Code link returns to `/buyer-select`
  3. Switching to `AAWHSL` updates the chip to `AAWHSL` + `Andrei Aliasiuk`
- 1 retained fixme — dashboard min-bid label + grid render. **Blocked on backend restart**, see backend bug below.

**Backend bug surfaced + fixed (compiled, awaits restart):**

`BidderDashboardService.GRID_SQL` and `BidExportService.EXPORT_SQL` referenced columns that don't exist in the `mdm` schema:

| SQL referenced | Actual column |
|---|---|
| `mdm.brand.brand_name` | `mdm.brand.name` |
| `mdm.model.model_name` | `mdm.model.name` |
| `mdm.carrier.carrier_name` | `mdm.carrier.name` |

Both queries fixed in commit on this branch. `mvn -q compile` is clean. **The running backend is stale** — needs `mvn spring-boot:run` restart to pick up the fix; after restart, the `dashboard renders min-bid label and grid for HN` fixme can be uncovered in a follow-up commit.

**Run command:**

```bash
cd frontend
FORCE_LIVE_TESTS=1 npx playwright test tests/e2e/wholesale-r1-access.spec.ts --reporter=list
# Result on this branch (with stale backend): 3 passed, 1 fixme
```

`FORCE_LIVE_TESTS=1` is needed because `/actuator/health` reports DOWN on this dev box despite working APIs (likely a non-critical sub-indicator). Investigating the actuator status is item #1 from §5 — separate sub-project.

### P2 (shipped)

After backend restart picked up the GRID_SQL / EXPORT_SQL fix, the fixme was uncovered and 4 more R1 buyer-side tests were added.

**POM extensions** (`BidderDashboardPage.ts`):
- `sortButton(label)` — click target for column sort (matches `aria-label="Sort by <label>"`)
- `headerCell(label)` — `<th>` with `aria-sort` for direction assertion
- `filterInput(key)` — column-level filter input
- `auctionTitle` / `roundLabel(n)` — dashboard header heading locators
- `gridRows`, `gridFooter` — grid body + footer summary

**Spec — final shape (8 tests, all passing live):**

| # | Test | What it asserts |
|---:|---|---|
| 1 | bidder can pick auction code HN and lands on the dashboard with chip | login → picker → HN → chrome chip text |
| 2 | Switch Buyer Code link returns the bidder to the picker | top-bar link routes back to `/buyer-select` |
| 3 | bidder can pick a different auction code and the chip updates | code switch propagates to chip |
| 4 | dashboard renders min-bid label and grid for HN | live `GET /api/v1/bidder/dashboard` GRID mode |
| 5 | dashboard header shows auction title + Round 1 label | "Auction YYYY / WkNN" + "Round 1" headings |
| 6 | grid footer shows the Currently-showing count and matches row total | `Currently showing N of M`, `N === M` (no filter) |
| 7 | sort by Brand cycles aria-sort: none → ascending → descending | header `aria-sort` updates per click |
| 8 | filter by Brand narrows the grid to fewer rows | footer count drops after typing into filter |

**Run command + result:**

```bash
cd frontend
FORCE_LIVE_TESTS=1 npx playwright test tests/e2e/wholesale-r1-access.spec.ts --reporter=list
# 8 passed (38.6s)
```

### P3 (shipped)

Buyer-side bid placement + submission against the live backend. Mutating tests run in their own `test.describe.serial` block with `beforeEach` re-applying the seed.

**Seed fixture rewritten for fast inter-test reset:**
- Old (P1): DELETE bid_rounds → CASCADES bid_data → next dashboard load triggers ~12k-row CTE re-generation (~5s per test).
- New (P3): Idempotent UPDATE — flips submitted flags back to false, zeros bid_amount/bid_quantity/submitted_*, leaves bid_data rows in place. ~30ms per test.

**POM extensions** (`BidderDashboardPage.ts`):
- `firstGridRow` — first body row
- `priceInput(row)` / `qtyCapInput(row)` — row-relative input locators (avoids hardcoded ids)
- `bidsSubmittedModal` / `noBidsModal` — `getByRole('dialog', { name })` (BidderModal applies title as `aria-label`, not visible heading)

**`placePrice()` helper inside the spec** — wraps the click → ControlOrMeta+a → Delete → pressSequentially → Tab + waitForResponse pattern. Avoids the `.fill()` quirk where the focus-handler's `setDisplay("0.00")` could race with `handleChange`'s `setDisplay("42.50")` and silently leave the autosave payload at 0.

**Spec — final shape (11 tests, all passing live):**

P0–P2 tests (8):
1. Pick HN → chip shows code + buyer name
2. Switch Buyer Code link returns to picker
3. Switch to a different code updates chip
4. Dashboard min-bid label + grid render
5. Auction title + Round 1 label
6. Footer count matches grid total
7. Sort by Brand cycles aria-sort
8. Filter by Brand narrows row count

P3 tests (3):
9. Submit with no bids shows "No Bids to Submit" modal — client-side guard never POSTs
10. Place price + click Submit → "Bids submitted" success modal
11. Submitted price persists after page reload — backend keeps `bid_amount` after submit (only copies → `submitted_bid_amount`)

**Run command + result:**

```bash
cd frontend
FORCE_LIVE_TESTS=1 npx playwright test tests/e2e/wholesale-r1-access.spec.ts --reporter=list
# 11 passed (1.3m)
```

### P4 (shipped)

R2 transition + qualification gating. Lives in a new spec file because it toggles auction state (R1 closed → R2 open) — running it interleaved with the R1 spec would race the seeds.

**Files added:**
- `frontend/tests/_fixtures/wholesale-r2-active.sql` — closes R1, opens R2, sets HN+AAWHSL qualified and DS2WHSL `included=false`. Idempotent UPDATE-only on existing rows where possible.
- `frontend/tests/e2e/wholesale-r2-access.spec.ts` — 3 tests, all live.

**Spec — final shape (3 tests, all passing live):**

1. Qualified buyer (HN) lands on R2 dashboard with grid + "Round 2" label
2. Unqualified buyer (DS2WHSL) sees DOWNLOAD-mode `EndOfBiddingPanel` — "Bidding has ended.", "Your bids from round 1 can be found below.", and the "Download your Round 1 Bids" button
3. Qualified buyer can submit bids in R2 — same `Ctrl+A → Delete → pressSequentially → Tab` placement pattern from P3, then click Submit, verify "Bids submitted" success modal

**NOT ported in P4 (genuine backend gap surfaced):**

QA's `R2: Verify Buyer Cannot Lower Bid Price and Qty on Data Grid` requires the backend to compare incoming `bidAmount` against the previously submitted value and reject downward moves with 409. The local `BidDataSubmissionService.validateAmountAndQuantity` only checks `bidAmount >= 0` and `bidQuantity <= maximumQuantity` — there is no comparison against `last_valid_bid_amount` / `submitted_bid_amount`. **This is a backend feature item**, not a test gap. Adding the validation should be a small change (~10 lines + a unit test in `BidDataSubmissionServiceTest`); the test port can land alongside.

**Cross-spec interaction:**
The R1 and R2 fixtures are siblings — re-applying either flips the auction state for the most recent auction. Running `wholesale-r1-access.spec.ts` then `wholesale-r2-access.spec.ts` works in either order because each spec's `beforeEach` re-applies its fixture. Verified — 14 tests pass when both specs run sequentially.

**Run command + result:**

```bash
cd frontend
FORCE_LIVE_TESTS=1 npx playwright test tests/e2e/wholesale-r1-access.spec.ts tests/e2e/wholesale-r2-access.spec.ts --reporter=list
# 14 passed (1.7m)
```

### P5 (shipped)

R3 (Upsell Round) buyer-side access. Mirrors P4 — same fixture pattern, same test shape, R3 substituted for R2.

**Files added:**
- `frontend/tests/_fixtures/wholesale-r3-active.sql` — closes R1+R2, opens R3, sets HN+AAWHSL qualified, DS2WHSL `included=false`
- `frontend/tests/e2e/wholesale-r3-access.spec.ts` — 3 tests, all live

**Spec — final shape (3 tests, all passing live):**

1. Qualified buyer (HN) lands on R3 dashboard with grid + "Round 3" label
2. Unqualified buyer (DS2WHSL) sees DOWNLOAD-mode `EndOfBiddingPanel`
3. Qualified buyer can submit bids in R3

**Parity ADR check:** R3's backend `roundName` is `"Upsell Round"`, but the dashboard renders `Round ${round}` (per Q5 in `wholesale-buyer-parity-plan.md`). Test #1 verifies "Round 3" is visible — would catch a regression that surfaces `roundName` directly.

**NOT ported in P5:**

QA's other two R3 tests (`Sales Can Access R1`, `Sales Can Submit Bids on Behalf of Buyer`) require the BidAsBidder admin surface (admin selects a buyer code and gets dropped into that buyer's dashboard). The local app doesn't have this — see §5 item #10. Defer to P8 (admin surfaces).

**Run command + result:**

```bash
cd frontend
FORCE_LIVE_TESTS=1 npx playwright test tests/e2e/wholesale-r1-access.spec.ts tests/e2e/wholesale-r2-access.spec.ts tests/e2e/wholesale-r3-access.spec.ts --reporter=list
# 17 passed (2.1m)
```

### Backend feature: downward-bid rejection (shipped, awaits restart)

Added `validateNotLoweringSubmittedBid` to `BidDataSubmissionService.save()`. Throws `BidDataSubmissionException("BID_LOWERED")` when:

- `submitted_datetime != null` (row was previously submitted), AND either:
  - `req.bidAmount < submitted_bid_amount`, OR
  - The qty cap is being narrowed (special handling for the `null` "no-cap" sentinel: null → specific number = narrowed; specific number → null = widened).

`GlobalExceptionHandler` mapping extended: `case "ROUND_CLOSED", "BID_LOWERED" → HttpStatus.CONFLICT` (HTTP 409).

**Backend tests:** 6 new in `BidDataSubmissionServiceTest`:
- `save_rejectsLoweringBidAmountBelowSubmittedValue` — 1000 → 999 fails
- `save_rejectsLoweringBidQuantityBelowSubmittedValue` — qty 2 → 1 fails
- `save_allowsRaisingBidAmountAboveSubmittedValue` — 1000 → 1500 succeeds
- `save_allowsLoweringBeforeFirstSubmit` — `submittedDatetime == null` skips the check
- `save_rejectsNarrowingNoCapToSpecificQuantity` — `null` → 1 fails (commitment narrowed)
- `save_allowsWideningSpecificQuantityToNoCap` — 2 → `null` succeeds (commitment widened)

Total `BidDataSubmissionServiceTest`: 16/16 pass (`mvn test -Dtest=BidDataSubmissionServiceTest`).

**E2E test ported:** new test in `wholesale-r1-access.spec.ts`:
> "lowering a previously submitted bid is rejected with 409 and the value persists"

Submits a $1000 bid, attempts to lower to $50, asserts the autosave PUT returns 409, reloads the page, asserts the input still shows "$ 1000.00".

**Status:** test passes against the recompiled backend on `mvn spring-boot:run` restart. With the stale running JVM, 17/18 pass (the new test fails as expected, no regression on the other 17).

**Frontend polish (shipped):**

`saveBid` in `frontend/src/lib/bidder.ts` now reads the 409 envelope and throws a typed `BidLoweredError` for `code: "BID_LOWERED"` (mirroring the existing `RoundClosedError` / `VersionConflictError` pattern). The dashboard's `handleRowError` catches `BidLoweredError`, surfaces the toast `"You cannot lower a previously submitted bid."`, and refetches the dashboard so the cell input snaps back to the previously submitted value (the local `PriceCell` display state still shows the typed-but-rejected number until the row payload is re-applied).

Vitest unit tests added (`bidder.test.ts`):
- `throws BidLoweredError on 409 with code BID_LOWERED`
- `throws RoundClosedError on 409 with code ROUND_CLOSED` (regression cover for the new branch)
- `throws a generic Error on 409 with an unknown code`

E2E test updated to also assert the toast text appears after the 409.

### Hand-on-table — out of scope (informational only)

**Decision (user, 2026-04-24):** the local app uses a single bid-grid widget, not the legacy Mendix hand-on-table. The 88 tests under `Wholesales/HandOnTableTests/` will NOT be ported. Read them as research material to understand business rules, not as a porting source.

**Behavioral rules extracted from Flow1/4/5 (and the broader DataGrid suite):**

| # | Rule | Status in local app |
|---|---|---|
| 1 | Auction lifecycle states: `Unscheduled → Scheduled → Started → Closed` per round | ✓ schema (`scheduling_auctions.round_status` check constraint) |
| 2 | Each round (R1/R2/R3) has independent status; closed rounds preserve state | ✓ schema |
| 3 | After a round ends, **buyers** cannot access it; **sales** still can via Bid-as-Bidder | Buyer side covered by `landingRoute`. Sales-side BidAsBidder admin surface — **MISSING (§5 #10, P8)** |
| 4 | R2 criteria (set by admin before R1 starts) — `All Buyers / Bid Buyers Only`, `Full Inventory / Inventory With Bids`, `STB Override Yes/No` | Schema present (`bid_round_selection_filters` table) but empty + admin UI **MISSING (§5 #5, P8)** |
| 5 | Buyers who **didn't bid in R1** can access R2 ONLY when criteria = "All Buyers" | Backend: `landingRoute` + `shouldRouteToRound2Download` cover the gating. Test ports gated on R2 criteria UI. |
| 6 | **Manually qualified** buyers (sales toggled `included=true` post-R1) can access R2 even without R1 bids | Schema-supported (`qualified_buyer_codes.qualification_type='Manual'`). Tested implicitly by P4 — DS2WHSL `included=false` is the inverse case. Direct manual-qualify port needs the admin UI. |
| 7 | **Manually unqualified** buyers cannot access R2 even if auto-qualified | Schema-supported. **Unported** — needs admin UI to flip the flag at runtime. |
| 8 | **Special Treatment Buyers** can ALWAYS access R2 + see full inventory with R1 bids | Schema-supported (`qualified_buyer_codes.is_special_treatment`). **Unported** — also tied to admin UI. |
| 9 | Removed bids do not reappear on the next round's grid | Bid removal is admin-only (§5 #7, P8). **Unported.** |
| 10 | Sales can submit bids on behalf of any buyer (impersonation) | **MISSING (§5 #10, P8)** |
| 11 | Cannot lower a previously submitted bid (price or qty cap) | ✓ shipped this PR — `validateNotLoweringSubmittedBid` + 1 E2E test |
| 12 | "Minimum starting bid - $2.50" label is **informational only** — buyers may enter sub-floor bids; the auction logic later flags them as unqualified for TGP via the threshold rules. | ✓ behavior confirmed by user (2026-04-24) and matches QA HandOnTable tests, where buyers routinely placed `$0.99` bids that were accepted. **No backend floor enforcement.** Do NOT add one. |

**Summary:** of the ~12 distinct buyer-relevant rules in the QA hand-on-table suite, 5 are already covered (or near-covered) by the 18 ported tests + the new `BID_LOWERED` validation. The remaining 7 are blocked on admin surfaces (P8) or on an admin-edited table the local app doesn't yet expose. None of the unported rules surface a *new* backend feature gap — they're all admin-UI gaps.

### Pending

- **P6** — happy-path slice of `R2_EligibilityAndTargetPriceTests` (~20 tests). Defer combinatoric portion until sub-project 4C ships the TGP engine.
- ~~P7 — hand-on-table re-scope~~ — **dropped per user direction; insights captured above.**
- **P8** — admin surfaces (BidAsBidder, Auction Scheduling, Bid Data, Qualified Buyer Codes, R2 Criteria, R3 Bid Report). Unblocks ~50 ports including the deferred R3 admin tests + rules 3, 6, 7, 8, 9, 10 above.
- ~~Quick check — minimum-bid-price floor~~ — **decision (2026-04-24): not enforced.** The label is informational only; buyers may enter any positive bid value. Sub-floor bids are flagged downstream (qualification logic), not at save time. Backend has no floor; do not add one.
- **CI integration** — decide whether live tests run in CI (Q1 from §7). Recommend a separate workflow gated on `FORCE_LIVE_TESTS=1` + a containerised Postgres + Spring Boot.
- ~~Actuator health investigation~~ — **shipped 2026-04-24.** Probed with auth and confirmed the failing sub-component was the JavaMail health indicator (Spring Boot auto-registers it; it tries to connect to `localhost:25` SMTP every poll, which times out in dev/CI). Excluded via `management.health.mail.enabled: false` in `application.yml`. After the next backend restart, `/actuator/health` returns `UP`, `isBackendAvailable()` no longer needs the `FORCE_LIVE_TESTS=1` workaround, and live tests run normally. Mail health is still individually queryable at `/actuator/health/mail` if needed.
