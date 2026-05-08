# Architecture Decisions Log

Running log of notable technical decisions. Each entry is lightweight
ADR-style: context, decision, consequences. Newest first.

---

## ADR — Reserve Bids: intentional divergences from QA Mendix (2026-05-08)

**Status:** Accepted.

**Context:** The 2026-05-08 QA-vs-local audit on `/admin/auctions-data-center/reserve-bids` flagged three "differences from QA" that are deliberate, not gaps. Recording them here so future audits stop re-flagging them and so reviewers know they were considered.

**Decisions:**

1. **Local Admin sub-nav does not match QA's flat menu** (gap RB-25). QA's Mendix sidebar exposes `Settings` and `Admin` as inline items at the top level. Local groups Mendix's `Admin` page set into a collapsible Admin section with four sub-routes (Application Control Center, Auction Control Center, Auctions Data Center, PWS Data Center). The local sub-routes do not exist in QA — they are net-new admin surfaces in this rebuild. We keep the divergence: collapsing the four into the legacy QA shape would either (a) bury the new pages in unrelated menus or (b) require a global nav redesign. Reserve Bids itself remains a top-level item identical to QA.

2. **Local exposes user identity in the top bar** (gap RB-26). QA renders only a status indicator. Local renders "Admin User" + an avatar pill + a "User menu" button. This is a strict improvement (admins routinely need to confirm whose session they're in before performing destructive ops like Delete) and ships across every admin page, not just Reserve Bids. We keep the divergence.

3. **No breadcrumbs on either app — local stays parity, deferred** (gap RB-27). The Reserve Bids list lives at `/admin/auctions-data-center/reserve-bids` — a long URL that benefits from breadcrumbs. Neither QA nor local has them today, and adding them only on Reserve Bids would create cross-page inconsistency. Defer until a global breadcrumb pattern is decided across the admin shell.

**Consequences:**

- Future QA-vs-local audits that walk the Reserve Bids surface should reference this ADR and skip RB-25 / RB-26 / RB-27.
- If the admin nav is ever reshaped (e.g. flattening the four Control Centers), revisit (1).
- If global breadcrumbs land, retrofit Reserve Bids first since its URL is among the deepest.

**Out of scope:** RB-3 (whether to keep the `/new` manual-create route) is a separate decision and remains open — it ties to the Mendix invariant "EB authored only via Excel," which a future ADR or product call should resolve.

**References:**
- Walkthrough: `docs/tasks/qa-vs-local-reserve-bids-walkthrough-2026-05-08.md` §9, §10
- Styling spec: `docs/tasks/qa-vs-local-reserve-bids-styling-spec-2026-05-08.md` §11

---

<<<<<<< HEAD
## ADR — Sub-project 5b: R2/R3 row visibility correctness (2026-05-07)

**Status:** Accepted

**Context:** `BidDataCreationRepository.generate(...)` had two `TRUE`-constant stubs
for `bid_meets_threshold` and `row_visible` since sub-project 4. Every R2/R3 buyer
saw every AE row regardless of R1 rank, R1 bid amount, or whether they had a prior
bid at all. Functionally incorrect for production. Sub-project 5b replaces both
stubs with the real predicate cascades:

- **R2** ports the per-AE predicate from `SUB_Round2AggregatedInventorySingleItem`
  (5 branches with DW/Wholesale split, with whole-percent unit alignment).
- **R3** uses the per-row form of sub-project 6's R3 selection rule (4 branches:
  all-NULL fallthrough + pct + amt + rank).
- **STB shortcut**: `row_visible = is_special_treatment OR bid_meets_threshold` —
  STB buyers see every row regardless of threshold (covers gap-analysis #8's
  basic STB-sees-all rule; full STB rerun semantics remain a 5c task).

**Decisions:** see `docs/tasks/auction-r2-r3-row-visibility-design.md` §3 for the
14 numbered decisions. Highlights:
- One method, one CTE_SQL constant, branched on `params.round` (decision 3.1)
- Round-agnostic `prior_round_biddata` via `DISTINCT ON` (decision 3.6)
- `prior_scheduling_auction` CTE deleted (subsumed)
- Orphaned `selection_filter` CTE deleted as part of the refactor
- LEFT JOIN refactor for "missing QBC" deferred (cosmetic only)

**Implementation notes:**
- `BidDataScenario` test builder gained 7 primitives (qbc, brsfR2, brsfR3,
  priorBid, priorBidWithRank, explicit-round priorBid overload, r3TargetPrice)
  to author R2/R3/STB scenarios fluently.
- The builder's `insertExtendedPriorBid` helper now UPDATEs `submitted=TRUE`
  on existing prior bid_round rows when reused, because the new
  `prior_round_biddata` CTE filters `submitted = TRUE`.
- 20 new IT cases in `BidDataCreationRepositoryIT` (10 R2 tests: 7 Only_Qualified branches + 1 DW + 1 All_Buyers + 1 noPriorBid_invisible; 7 R3 tests; 2 STB; 1 R1 regression).

**Consequences:**
- Bidder dashboard now correctly filters AEs per-buyer for R2 and R3.
- Existing R1-init behaviour preserved (R1 ELSE branch returns TRUE).
- `BidDataCreationService` signature + concurrency contract unchanged.

**Out of scope:**
- Sub-project 5c: `SUB_HandleSpecialTreatmentBuyerOnRoundStart` (STB rerun)
- LEFT JOIN refactor for "missing QBC" distinction

---

## 2026-05-07 — Sub-project 6: R3 Init + Pre-process

**Status:** Accepted.

### Context
Two stub listeners (`R3InitStubListener`, `R3PreProcessStubListener`) deferred all R3 (Upsell) bidding logic. While they existed, the R3 round had zero `qualified_buyer_codes` rows — bidders saw an empty inventory dashboard — and `auctions.round3_buyer_data_reports` was never written, leaving the already-shipped Round 3 Bid Report admin page permanently empty. Sub-project 6 ports Mendix `SUB_Round3_PreProcessRoundData` (minus BidData generation) and `ACT_Round3_SetStarted` / `SUB_InitializeRound3`. The R3 qualification rule was supplied directly by the product owner as a three-branch SQL predicate, replacing Mendix's legacy "any nonzero bid qualifies" semantics.

### Decisions
See `docs/tasks/auction-r3-init-preprocess-design.md` §3 for the full list of 14 numbered decisions. Key highlights:

1. **Two sibling services, not an orchestrator** — `R3PreProcessService` (triggered by `RoundClosedEvent(round=2)`) and `R3InitService` (triggered by `RoundStartedEvent(round=3)`) are independent. They fire at different lifecycle points, do non-overlapping work, and have independent recovery semantics (decision 3.1).
2. **Predecessor guard in code, not constraint** — `R3InitService.run` rejects with 422 unless `r3_preprocess_status = SUCCESS` on the same R3 SA row (decision 3.2).
3. **`has_round = false` → SKIPPED** — pre-process gates on the R3 SA's `has_round` flag; `FALSE` produces a `SKIPPED` terminal state with no row writes (decision 3.3).
4. **No BidData generation in pre-process** — R3 BidData flows through the existing on-demand `BidDataCreationService` path from the bidder dashboard; `ACT_GenerateRound3_BidDataObjects` and `Sub_ProcessSpecialBuyers` are intentionally not ported (decision 3.5).
5. **New R3 qualification rule** — per (ecoid, grade, buyer_code) take latest bid across rounds 1+2, evaluate against three filter knobs (`bid_percentage_variation`, `bid_amount_variation`, `rank_qualification_limit`). All three NULL → fall-through qualify. Replaces `SUB_GenerateRound3QualifiedBuyerCodes` step 5 (decision 3.6).
6. **Whole-percent convention everywhere** — R3 stores `bid_percentage_variation = 5` for 5%. R2's `target_percent` was stored as 0.05 in V59/sub-project 5; sub-project 6 normalises R2 to whole-percent by updating the CTE formula and test fixtures (decision 3.8).
7. **STB CTE retained for R3 QBC writes** — `is_special_treatment = TRUE` rows are written to `qualified_buyer_codes` for the R3 SA; `BidDataCreationService` reads this flag for STB all-AE visibility (decision 3.9).
8. **`ACT_ChangeSavedBidsToPreviouslySubmitted` not ported** — schema tracks submission as `bid_rounds.submitted` boolean; no per-row state flip needed (decision 3.12).

### Implementation deviations (discovered during build)

- **V85 migration** — the design assumed `auctions.round3_buyer_data_reports` (V62) already had `scheduling_auction_id` and `buyer_codes` columns. In practice it did not. `V85__auctions_r3_reports_scheduling_auction.sql` adds them. Phase 5 of `R3PreProcessService` writes these columns via `bulkInsertForSchedulingAuction`.
- **`R3LifecycleValidationException` (HTTP 422)** — existing `GlobalExceptionHandler` mappings route `IllegalArgumentException` → 400 and `IllegalStateException` → 409. Neither matched the desired 422 for service-layer guard failures (predecessor check, wrong-round validation). A new exception class wraps these and is mapped to 422. Admin endpoints translate service exceptions via this wrapper.
- **`spring.jpa.open-in-view: false` added project-wide** — Hibernate's L1 cache was masking JDBC-written status updates in admin response paths: the controller read the SA entity back to build the response DTO, but the cache served the pre-JDBC-write snapshot. Disabling OSIV forces a fresh load on every request. This is the recommended production setting for a pure REST API.
- **`@Transactional(REQUIRES_NEW)` on both `recalculate()` methods** — Spring's CGLIB proxy does not intercept self-calls. `recalculate()` calls `run()` inside the same class; `run()` declares `@Transactional(MANDATORY)` via the status-updater repos, which would throw `IllegalTransactionStateException` if no outer tx existed. Annotating `recalculate()` itself with `REQUIRES_NEW` starts the tx that the inner methods join. **Future admin-recovery wrappers should follow this same pattern.**
- **`R3InitService` JPA/JDBC interleave fix** — `saRepo.save(sa)` followed by JDBC `markSuccess` causes Hibernate's flush at tx commit to overwrite the JDBC-written final status. Fixed via `em.refresh(sa)` after `tryFlipToRunning` (which performs a `REQUIRES_NEW` sub-tx) and `saveAndFlush(sa)` before `markSuccess()`. This ensures the Hibernate snapshot reflects the current DB state before the JDBC write lands.

### Consequences
- R3 BidData stays empty until the first bidder-dashboard view triggers `BidDataCreationService` on-demand creation (per decision 3.5).
- Predecessor-guard failure leaves the R3 SA in `PENDING` init state — admin must rerun `/preprocess-r3` first.
- OSIV=false is now the global setting; lazy-load-outside-tx patterns would fail. The `R3LifecycleAdminControllerIT` sweep (and all other ControllerITs) validates no such patterns exist today.
- **Follow-up risk (latent bug in R2):** `R2BuyerAssignmentService.recalculate()` has the same self-call AOP bypass as R3 had before the fix. The existing `R2BuyerAssignmentAdminControllerIT` is a `@WebMvcTest` slice with `@MockBean service`, so the bug is never exercised by the test suite. Production `/reassign-r2-buyers` would fail with `IllegalTransactionStateException`. Fix: annotate `R2BuyerAssignmentService.recalculate()` with `@Transactional(propagation = REQUIRES_NEW)`.
- Schema migrations V84 + V85 are purely additive; existing rows pick up `r3_preprocess_status = 'PENDING'` and `r3_init_status = 'PENDING'` via column defaults.

### References
- `docs/tasks/auction-r3-init-preprocess-design.md` — full spec (14 decisions, schema, SQL contracts)
- `docs/tasks/auction-r3-init-preprocess-plan.md` — task-by-task implementation plan
- `docs/business-logic/r3-init-and-preprocess.md` — narrative business logic
- Schema: `V84__auctions_r3_lifecycle_status.sql`, `V85__auctions_r3_reports_scheduling_auction.sql`
- Mendix sources: `migration_context/backend/ACT_Round3_SetStarted.md`, `migration_context/backend/services/SUB_Round3_PreProcessRoundData.md`, `migration_context/backend/services/SUB_GenerateRound3QualifiedBuyerCodes.md`
- Related ADRs: 2026-05-06 (5), 2026-04-30 (4C)

---

## 2026-05-06 — Sub-project 5: R2 Buyer Assignment

**Status:** Accepted.

### Context
`R2InitStubListener` previously logged "would assign R2 buyers + process special buyers" on `RoundStartedEvent(round=2)` and did nothing — every live R2 cycle saw zero qualified buyers, blocking R2 bid submission entirely. This was the most critical end-to-end gap remaining after sub-project 4C. The Mendix flow splits into buyer-code qualification (`SUB_AssignRoundTwoBuyers` + `SUB_GenerateRound2QualifiedBuyerCodes`) and special-buyer bid-data seeding (`Sub_ProcessSpecialBuyers` + `SUB_CreateBidDataForAllAE`). Migration **V72** (already shipped) flattened the legacy Mendix M:M junctions `qbc_buyer_codes` + `qbc_scheduling_auctions` into direct FK columns on `qualified_buyer_codes`; the original three-step write design collapsed to a single bulk INSERT.

### Decisions
1. **Single process** `R2_INIT` (not two like 4C) — phases 4 (QBC write) and 5 (special bid_data seed) share row-level dependencies; one tx + admin re-fire is simpler than orchestrating partial commits.
2. **Status sub-tx pattern reused** — FAILED writes via `RecalcStatusUpdater.markFailed(REQUIRES_NEW)` survive the parent rollback (same idiom as 4C decision 3.8).
3. **`calculate_round2_buyer_participation = FALSE` short-circuits to `SKIPPED`** — distinct terminal state so admins can tell "didn't run" from "ran with empty result".
4. **Set-based qualification CTE — no Java loops** — Mendix's per-buyer-per-AE microflow loop expressed as one Postgres CTE (mirrors 4C decision 3.7).
5. **DENSE_RANK is not used** — qualification is a per-(buyer_code, ecoid, grade) pass/fail predicate, no ranking to compute.
6. **CTE matches new-stack enums** — `RegularBuyerQualification ∈ {All_Buyers, Only_Qualified}` and `RegularBuyerInventoryOption ∈ {InventoryRound1QualifiedBids, ShowAllInventory}` per V59; legacy three-value Mendix enums are excluded by CHECK constraints.
7. **Buyer-code type filter is `('Wholesale','Data_Wipe')` only** — Purchasing_Order variants do not participate in R2 buyer qualification (separate PO-commitment flow).
8. **Three-set QBC write is one bulk INSERT** — qualified, not-qualified, and special-treatment sets unioned in a single SQL with derived `qualification_type` / `is_special_treatment` columns.
9. **Special-treatment is computed independently** — a buyer with `is_special_buyer = TRUE` AND every DW/WH code passing STB check (override OR zero prior bids) is added to qualified set regardless of `Only_Qualified` exclusion.
10. **Special-buyer bid_data uses a bulk INSERT, not the existing `BidDataCreationRepository.generate(...)`** — that method short-circuits on the QBC `included` gate; STBs need every-AE-row semantics.
11. **Admin endpoint rejects 409 when `r2_init_status = 'RUNNING'`** — single-statement guard via state-flip UPDATE (same as 4C decision 3.6).
12. **Listener is `@Async("snowflakeExecutor")`** — same pattern as `R1InitListener`; offloads the bulk CTE off the cron-tick post-commit thread.

### Consequences
- One R2 listener invocation produces a deterministic snapshot — DELETE-then-INSERT plus one bulk bid_data SQL — bounded by ~600 buyer codes × ~30k AEs.
- Schema migration V83 is purely additive (4 columns + CHECK constraint); existing rows pick up `r2_init_status = 'PENDING'` via column default.
- Admin endpoint `POST /api/v1/admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers` is the recovery path; no frontend UI today (REST-only, mirrors 4C).
- **No Snowflake push** — legacy never synced QBC rows to Snowflake. If/when reporting needs it, model after `BidRankingSnowflakePushListener`.
- **Follow-up: sub-project 5b** — `bid_meets_threshold` / `row_visible` stubs in `BidDataCreationRepository` are still hardcoded `TRUE`. Sub-project 5 unblocks R2 by writing QBCs; 5b makes per-row visibility correct for non-special bidders.
- **Follow-up: sub-project 5c** — `SUB_HandleSpecialTreatmentBuyerOnRoundStart` refines STB row-visibility post-seed.

### References
- `docs/tasks/auction-r2-buyer-assignment-design.md` — full spec (decisions, schema, SQL contracts)
- `docs/business-logic/r2-buyer-assignment.md` — narrative business logic
- Schema: `V83__auctions_r2_init_status.sql`
- Mendix sources: `migration_context/backend/services/SUB_AssignRoundTwoBuyers.md`, `SUB_GenerateRound2QualifiedBuyerCodes.md`, `SUB_IsSpecialTreatmentBuyer.md`, `Sub_ProcessSpecialBuyers.md`, `SUB_CreateBidDataForAllAE.md`
- Related ADRs: 2026-04-30 (4C), 2026-04-22 (R1 init + V72 QBC flattening), 2026-04-20 (cron skeleton + event contract)

---

## 2026-05-07 — Inventory KPI cards: keep bordered-card layout (vs QA inline)

**Status:** Accepted (logged divergence — no code change).

### Context
QA renders the inventory KPIs (`/admin/auctions-data-center/inventory`)
as inline label-then-value text in a single horizontal row. Local
renders the same six metrics as bordered cards in a 6-column grid
(`.kpiStrip` + `.kpiCard` in `inventory.module.css`). Both surfaces
show the same numbers from the same backend; the divergence is
purely presentational, but it is a divergence and was flagged in
`docs/tasks/qa-vs-local-pixel-walkthrough-2026-05-07.md` §1.3.

### Decision
Keep the bordered-card layout. Rationale:
- Already shipped and stable; reverting to inline labels is rework
  with no functional gain.
- Bordered cards scale better on narrow viewports — the inline row
  collapses ungracefully below ~1100px while the grid reflows.
- Cards make the metric labels more scannable: each label sits above
  its value with consistent leading, instead of being inlined with
  `Label: value` punctuation that the eye has to parse.

### Consequences
- The QA-vs-local walkthrough item M13a is closed by this decision,
  not by a code change.
- Any future "match QA exactly" mandate would need to revisit this.
- Layout choice is locked in `inventory.module.css` `.kpiStrip` /
  `.kpiCard`; future contributors should not switch back to inline
  text without re-opening this ADR.

### References
- `docs/tasks/qa-vs-local-implementation-plan-2026-05-07.md` Bundle M13
- `docs/tasks/qa-vs-local-pixel-walkthrough-2026-05-07.md` §1.3
- Local impl: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`

---

## 2026-05-07 — Sidebar color: keep teal gradient (per CLAUDE.md spec) over QA's legacy solid green

**Status:** Accepted (logged divergence — no code change).

### Context
The local admin sidebar uses the teal palette documented in
`CLAUDE.md` (#407874 teal, #112d32 dark). QA's legacy admin still
ships the older solid green sidebar from the pre-rebrand Mendix theme.
Walkthrough item L21 noted the divergence.

### Decision
Local stays on teal. `CLAUDE.md` is the single source of truth for
brand tokens for the rebuild, and the teal token (#407874) is the
documented spec. QA is the stale surface, not local.

### Consequences
- L21 is closed by this decision; no CSS change.
- If/when QA is updated to the rebrand palette, this divergence
  disappears organically.

### References
- `CLAUDE.md` "Color tokens" section (#407874 teal)
- `docs/tasks/qa-vs-local-implementation-plan-2026-05-07.md` Bundle M14, L21

---

## 2026-04-30 — Sub-project 4C: Bid Ranking + Target-Price Recalc

**Status:** Accepted.

### Context
Mendix `ACT_TriggerBidRankingCalculation` + `ACT_CalculateTargetPrice` are the two heaviest queries on round close. Sub-project 3 stubbed them via `BidRankingStubListener`. 4A (EB) and 4B (PO) shipped the data inputs; 4C ports the queries themselves.

### Decisions
1. Two independent processes (RANKING + TARGET_PRICE), each in its own `REQUIRES_NEW` tx.
2. Reserve-floor inclusion in ranking is configurable on `bid_ranking_config.include_reserve_floor`.
3. Synchronous on the cron-tick listener thread; ShedLock on the cron prevents parallel ticks.
4. Two admin recovery endpoints (re-rank, recalculate-target-price), each rejecting 409 when status=`RUNNING`.
5. Per-process Snowflake events; full week refresh per push; bounded payload (~few-thousand rows).
6. Status writes that survive rollback live in a `REQUIRES_NEW` sub-tx (`RecalcStatusUpdater`).

### Consequences
- One-process failure does not stop the other; ops can fix and re-fire each independently.
- Cron ticks see deterministic ordering — RANKING then TARGET_PRICE per closed round.
- Schema migration V82 is purely additive.

### References
- `docs/tasks/auction-bid-ranking-design.md` — full spec
- `docs/tasks/auction-bid-ranking-plan.md` — implementation plan
- `docs/tasks/auction-sub4-umbrella-design.md` — parent decomposition

---

## 2026-04-25 — Pixel-compare strategy: local baselines + semantic assertions

**Status:** Accepted. Supersedes the Phase-13 `compare-against-QA-reference`
strategy documented in the 2026-04-23 wholesale-buyer-portal ADR (visual
parity section is amended; the surrounding shell/routing decisions stand).

### Context

Phase 13 wired `toHaveScreenshot()` to compare Playwright screenshots
against QA reference PNGs committed under `docs/qa-reference/`. The
follow-up pixel-parity sprint tried to flip 8 `test.fixme()` compares to
passing `test()` calls and got **0/8** — a structural blocker:

1. The QA PNGs were captured from **live Mendix production** with real DB
   data (actual ECO IDs, target prices, row counts visible in the grid).
   `page.route()` mocks cannot reproduce row-level exactness.
2. Persistent environmental rendering deltas — macOS P3 vs Windows/Linux
   sRGB, GPU gradient differences, `font-display: swap` flicker, browser
   minor version skew.
3. Tightening tolerance to force passes hides real regressions; loosening
   beyond 2% makes the compares uninformative.

The original goal is **catching styling regressions we introduce** — not
enforcing byte-for-byte parity with a 6-month-old production snapshot.
The strategy needs to match that goal.

### Decision

- **Pixel-compare runs against local Playwright baselines** under
  `frontend/tests/e2e/__screenshots__/`, captured on a Linux chromium
  runner (CI or Docker) for OS-reproducibility.
  - `playwright.config.ts` no longer overrides `snapshotPathTemplate`.
  - `toHaveScreenshot()` uses Playwright's auto-naming (no filename arg).
  - Baselines committed to git; intentional style changes regenerate them
    in the same PR via `--update-snapshots`.
- **Each of the 8 prior compare scenarios gains a semantic-assertion test**
  (role, text, visible state) alongside the pixel compare. Catches
  regressions that pixel-compare misses — aria attribute drift, copy
  changes, element re-ordering that still renders pixel-identical.
- **QA PNGs in `docs/qa-reference/` stay as manual design references only**,
  not automated fixtures. README updated to reflect the change.

### Consequences

- Pixel-compare catches *our* styling drift, not Mendix parity — the test
  signal matches the test intent.
- Semantic assertions improve diagnostic clarity:
  `Expected text "Login" but found "Log In"` is far better than
  `0.3% of pixels differ`.
- Intentional style changes require a `--update-snapshots` pass in the
  same PR. That's a feature, not a bug.
- QA parity work still happens — via manual comparison against
  `docs/qa-reference/` during implementation — but isn't gated by CI.
- The `test.fixme('… pixel compare …')` blocks stay until the first
  baseline capture lands; they flip to `test()` in the same PR that
  commits the baseline PNGs.

### Implementation status (this commit)

- Phase 1: `playwright.config.ts` `snapshotPathTemplate` override
  removed. Done.
- Phase 3: 8 new semantic-structure tests added across the 6 wholesale
  spec files (`login`, `picker`, `bidder-shell` × 2, `submit-bids` × 2,
  `carryover`, `import-export`). Done.
- Phase 4: README + this ADR. Done.
- Phase 2 (Linux baseline capture): **deferred** — needs a CI run with
  `--update-snapshots` and the resulting PNGs committed back. Tracked in
  `docs/TODO/pixel-compare-strategy-plan.md`. The 8 `test.fixme()` pixel
  compares stay fixme until then.

### References

- `docs/TODO/pixel-compare-strategy-plan.md` — implementation plan
- Continuous run log: `docs/tasks/wholesale-buyer-continuous-run-log.md`
  sections on the pixel-parity sprint + CI cleanup
- Related ADR: 2026-04-23 wholesale-buyer portal (visual-parity section,
  now amended)

---

## 2026-04-23 — Wholesale buyer portal: two-shell routing + parity baselines

**Status:** Accepted (wholesale-buyer parity closeout, Phases 0-14 except 13).

### Context

Porting the Mendix wholesale buyer portal (`https://buy-qa.ecoatmdirect.com`)
to Next.js surfaced that a Mendix user with mixed code types (auction +
PWS) never sees a merged menu — the shell is chosen by the currently
selected buyer code's type. Auction-only users see Auction + Buyer User
Guide + the bidder dashboard grid. PWS users see Orders / Offers /
Pricing / Profile / Notifications + the Shop page. Switching between
shells requires returning to the buyer-code picker via a "Switch Buyer
Code" link.

The 14 phases documented in `docs/tasks/wholesale-buyer-parity-plan.md`
implemented the auction-code-selected shell end to end. The PWS shell
parity is deferred to a separate walkthrough because the QA account
used (`akshay+4@learnfastthinkslow.com`) only holds auction codes.

### Decision

- **Two sibling shells gated on the active buyer code's type, not on the
  user's portfolio.** `BidderShell` (auction codes) and `PwsShell` (PWS
  codes) share only the top-bar chrome (logo + Switch Buyer Code link +
  user avatar popover) and the login/picker flow. The active code is
  persisted to `localStorage['activeBuyerCode']`; picker routing sets
  this key and redirects to the appropriate shell's default landing.
  Single-code users bypass the picker via `router.replace()`.
- **Chrome primitives live in `frontend/src/components/chrome/`** —
  `BuyerPortalChrome`, `BuyerCodeChip` (framed + filled variants),
  `UserAvatarPopover` (injectable items for shell-specific menus),
  `SidebarToggle`, `SidebarContext`. Both shells consume these.
- **Backend surface for bidder:** `GET/PUT/POST /api/v1/bidder/**` plus
  new endpoints from Phases 9/10/12/14 — `carryover`, `export`,
  `import`, `buyer-user-guide`, `forgot-password`, `reset-password`.
  Admin surface added: `POST /api/v1/admin/buyer-user-guide` for PDF
  upload.
- **Visual parity source of truth:** ten QA screenshots persisted in
  `docs/qa-reference/` (captured 2026-04-22). Every frontend phase
  compares against the corresponding file in that directory. Pixel-diff
  tolerance (≤2% delta) + resolution-aware comparison are Phase 13
  deferred work, but the reference bitmaps are committed.
- **Key copy decisions** (verbatim from QA walkthrough):
  - Round 3 displays as `Round 3`, not `"Upsell Round"` (reverses the
    2026-04-20 ADR — see below).
  - Submit Bids has no pre-submit confirmation. The empty-state modal
    ("No Bids to Submit") fires client-side when no bids are entered;
    the success modal ("Your Bids have been Submitted!") fires
    post-commit. Both use verbatim Mendix copy including the double
    space in `Bids  have` and the `3.Upload your file here` artifact
    in the Import modal's step 3.
  - The "Minimum starting bid - $2.50" label is advisory, not a hard
    gate — the backend accepts $2.50 without complaint.
  - The 4-step Import instructions preserve the Mendix typo where
    step 3 has no space between `3.` and `Upload`.
- **Layout strategy:** `bidder/layout.tsx` sits inside the shared
  `(dashboard)` route group and uses `position: fixed; inset: 0;` to
  visually override the inherited admin chrome. A cleaner route-group
  restructure (move `bidder/` into its own `(buyer-portal)` group) is
  technically viable but deferred — requires moving 8+ files for
  marginal gain while Phase 6B is still pending.
- **Grid footer:** `Currently showing N of M`. No totals bar
  (`SubmitBar` was removed in Phase 5 — QA has no bottom totals row).
  Submit Bids lives in the header.
- **Bid cell inputs:** `PriceCell` (dollar-formatted, strip-on-focus,
  reformat-on-blur) and `QtyCapCell` (integer-only, `null` = no-cap
  sentinel, `0` = zero-unit bid). Debounced save via the existing
  `useAutoSaveBid` hook (500 ms). Three error paths surface through a
  shared `BidToast` with a 5-second auto-clear: `RateLimitedError`,
  `RoundClosedError`, `VersionConflictError`.

### Deferred follow-ups (tracked in
`docs/tasks/wholesale-buyer-continuous-run-log.md`)

- **Phase 6B — backend DTO expansion.** `BidDataRow` only exposes
  `ecoid` + `mergedGrade`. QA shows Brand / Model / Model Name /
  Carrier / Added alongside. Needs a SELECT joining
  `auctions.aggregated_inventory` → `mdm.device` + related reference
  tables. The export endpoint duplicates this join today; when 6B
  ships, the two queries should share a helper.
- **Phase 13 — E2E + pixel QA.** Specs exist per-phase (25 tests
  across 5 files). Pixel-compare fixtures, axe-core, and CI workflow
  are documented in `docs/tasks/phase-13-e2e-handoff.md`.
- **DOWNLOAD mode button** has no live endpoint — it requires a new
  `GET /bidder/download-round-1?buyerCodeId=X` backend. `onClick` is
  currently a no-op with a TODO.
- **ALL_ROUNDS_DONE / ERROR_AUCTION_NOT_FOUND / Carryover success**
  copy is provisional. Needs a live QA walkthrough when those states
  are reachable.
- **Brandon Grotesque license** confirmation and any additional weights
  (Phase 0 ships 400 + 500; 700 is commented out).
- **PWS shell parity walkthrough** — the other half of the buyer
  portal. Separate plan stub needed; no QA capture yet.
- **Email SMTP for password reset** — `PasswordResetService` logs the
  raw token at INFO with `DEV:` prefix. Real delivery follows the
  2026-04-13 PWS email-delivery ADR pattern.
- **Buyer User Guide storage** lives on local disk in Phase 12. Multi-
  instance deploys need shared storage (S3 or equivalent).

### Consequences

- Future buyer-portal work consumes the same chrome primitives and
  `useActiveBuyerCode` hook; PWS shell slot-in is structural.
- The `BidderModal` primitive (Phase 8) serves Phase 9 (Carryover) and
  Phase 10 (Import) — future modal-driven bidder features reuse it.
- Design tokens in `globals.css` now have a stable surface (Phase 0 +
  phase additions) documented in `docs/frontend/ui-primitives.md` —
  future components should read from the table before adding new
  tokens.
- Rate-limiter bucket `(userId, bidRoundId)` is shared across Save +
  Import. If abuse patterns diverge across these endpoints, split the
  buckets.
- The 2026-04-22 QA screenshots are the parity ground truth. Any QA-
  visible drift on the Mendix side that changes bidder behavior requires
  a re-capture (new dated file under `docs/qa-reference/`, don't
  overwrite existing ones).

### References

- Plan: `docs/tasks/wholesale-buyer-parity-plan.md`
- Analysis: `docs/tasks/wholesale-buyer-qa-analysis.md`
- Continuous run log: `docs/tasks/wholesale-buyer-continuous-run-log.md`
- Phase 13 handoff: `docs/tasks/phase-13-e2e-handoff.md`
- QA references: `docs/qa-reference/README.md` + 10 PNGs
- UI primitives: `docs/frontend/ui-primitives.md`
- Related prior ADRs: 2026-04-23 (Round 3 label reversal), 2026-04-23
  (Bidder dashboard + bid_data generation), 2026-04-13 (email-delivery
  event pattern), 2026-04-20 (auction lifecycle cron), 2026-04-19
  (admin security matcher ordering).

---

## 2026-04-23 — Round 3 displays as "Round 3" (not "Upsell Round") — reverses 2026-04-20 ADR

**Status:** Accepted. Reverses the display-name portion of the 2026-04-20
ADR ("Auction lifecycle: Create persists only the auction row; Schedule
persists the rounds"). The enum-as-varchar, dedicated-endpoint, and
method-security decisions from that ADR remain in force.

### Context

The Mendix source (`migration_context/backend/ACT_SaveScheduleAuction.md`)
and the original 2026-04-20 ADR both specified that Round 3's
customer-facing label should be `"Upsell Round"`, matching the string
persisted in the `auctions.scheduling_auctions.name` column. This string
appeared in the scheduling UI, the scheduling-auctions list grid, and —
under the original plan — the bidder-facing dashboard round header.

During the 2026-04-22 QA walkthrough with the wholesale buyer user,
the product owner directed that **all bidder-facing UI should render
`Round {n}` uniformly** — including Round 3. The rationale: wholesale
buyers should not be confused by a different label for the third round;
the "Upsell" framing is internal operations language and has no meaning
to the buyer.

The QA capture at `docs/qa-reference/qa-03-bidder-dashboard-ad.png`
confirms the expected bidder-facing panel reads "Round 3".

### Decision

- **Frontend derives the round label from the numeric `round` field.**
  Every consumer of a `SchedulingAuctionSummary` or equivalent round
  shape should render `` `Round ${round}` `` regardless of the value in
  the `name` or `roundName` fields.
- **Backend `name` column is preserved.** `auctions.scheduling_auctions.name`
  retains `"Upsell Round"` for Round 3. The value is still returned in
  the API response (`GET /admin/auctions/{id}` and the
  `SchedulingAuctionSummary` DTO) for audit and historical parity with
  Mendix. No migration needed.
- **Admin-facing surfaces (scheduling grid, auction detail) may choose
  to display the `name` field** — this ADR restricts only the
  bidder-facing UI.

### Alternatives considered

- **Rename the DB column value to `"Round 3"`.** Rejected: breaks
  historical parity with the Mendix source and removes the ability to
  surface the Upsell label on admin surfaces or in Snowflake push
  payloads.
- **Add a separate `displayName` field computed server-side.**
  Rejected: over-engineered for a single-string override; the frontend
  derivation rule is trivially expressed and has no edge cases.

### Consequences

- Simpler bidder UI — all round headers are uniform `"Round N"` copy.
- Audit consumers (admin grids, Snowflake, reporting) can still query
  the original Mendix `"Upsell Round"` value from the `name` column.
- Any future bidder-facing feature that wants to surface the Upsell
  meaning must opt in explicitly (i.e., read `name` rather than
  deriving from `round`).
- The scheduling-auctions admin list grid currently renders the `name`
  column; that is unchanged by this ADR.

### References

- Reversed decision: 2026-04-20 ADR ("Round 3 name is `Upsell Round`"
  section), archived in `decisions-archive.md`.
- QA capture: `docs/qa-reference/qa-03-bidder-dashboard-ad.png`.
- Mendix source: `migration_context/backend/ACT_SaveScheduleAuction.md`.

---

## 2026-04-23 — Bidder dashboard + bid_data generation

**Status:** Accepted (sub-project 3 of
`docs/tasks/auction-lifecycle-cron-design.md`; plan at
`docs/tasks/auction-bid-data-create-plan.md`).

### Context

Mendix `ACT_OpenBidderDashboard` is a landing-route microflow that (1)
picks the week's scheduling auction, (2) resolves which round's grid to
show — or which "bidding closed" copy to render — and (3) on the
`Started` branch calls `ACT_CreateBidData` to materialize one
`BidData` row per `(aggregated_inventory, bid_round, buyer_code)` tuple.
Rows are not pre-computed at round start: generation happens lazily the
first time the bidder opens the page. Subsequent opens short-circuit
because the rows already exist. Submit (`ACT_SubmitBidRound`) is a
copy-forward: `bid_*` → `submitted_*`, prior `submitted_*` →
`last_valid_*`. The round stays re-submittable until its status flips
to `Closed`.

Porting this verbatim surfaced four decisions worth recording.

### Decision

- **Synchronous generation on dashboard open.** The `GET /dashboard`
  handler, when the landing result is `GRID`, calls
  `BidDataCreationService.ensureRowsExist(buyerCodeId, bidRoundId, userId)`
  **inside the request**. First open for a `(buyer_code, round)` pair
  pays the write; all subsequent opens are a cheap existence check.
  Matches Mendix latency on the legacy flow (~1–3s for ~580 rows) and
  avoids a batch job that would have to fan out across every qualified
  buyer code at round start.
- **Single-CTE `INSERT ... SELECT` guarded by a Postgres advisory
  lock.** All 500–600 rows land in one statement. The advisory lock is
  keyed on `(bid_round_id, buyer_code_id)` so two concurrent tabs for
  the same bidder serialize inside Postgres instead of racing at the
  ORM layer. The CTE selects from `auctions.aggregated_inventory`
  joined to the QBC set flattened in V72, filtered to non-DW rows for
  Round 1. R2/R3 threshold filtering
  (`bid_meets_threshold = TRUE`) is **stubbed** in this sub-project —
  sub-project 4 will replace the placeholder with the real per-round
  qualification logic.
- **`bid_quantity` nullability is the no-cap sentinel.** `NULL` means
  "I accept any quantity up to `maximum_quantity`"; `0` means "price
  me in but ship zero units" (valid, unusual, used for hedging).
  Storing a sentinel instead of a separate flag column keeps the save
  path one UPDATE and keeps the grid's JSON body one-to-one with the
  row. Reject negative quantities with `INVALID_QUANTITY` at the
  service boundary — the frontend also clamps but the backend is the
  authority.
- **Submit is a re-callable copy-forward, not a state transition.**
  `POST /bid-rounds/{id}/submit?buyerCodeId=…` flips every row in the
  `(round, buyer_code)` slice. Calling it twice is safe: the second
  call rewrites `submitted_*` from the latest `bid_*` and stores the
  prior `submitted_*` into `last_valid_*`. The response's
  `resubmit: true` flag lets the UI show "resubmitted" confirmation
  copy. The round's own status stays `Started` until the cron closes
  it; the submit endpoint does not mutate `bid_round.round_status`.
- **Rate limit: 60 req/min per `(user, bid_round)`.** Enforced by
  `BidRateLimiter` **before** the DB write on `PUT /bid-data/{id}`.
  The limiter never touches the database on a denied request —
  important because the realistic abuse shape is a stuck auto-save
  loop firing hundreds of writes/min. Bucket scope is
  `(user, round)` not `(user, buyer_code, round)` because a single
  user + single round with N open buyer codes is not a realistic
  workload on the Mendix-parity UI; re-scoping is a one-line change
  if the assumption breaks.
- **Admin bypass at the role level, not per-endpoint.**
  `BidderDashboardController` is annotated
  `@PreAuthorize("hasAnyRole('Bidder','Administrator')")` at the class
  level. The Administrator bypass mirrors the existing admin-write
  pattern in `AuctionController` — admins can act on behalf of any
  bidder for diagnostic / recovery purposes. The `SecurityConfig`
  matcher at `/api/v1/bidder/**` must list both roles; a mismatch
  here fails closed at the filter chain before method security runs
  (same ordering pitfall as the 2026-04-19 SalesOps matcher ADR).
- **Error codes are carried in a typed field, not parsed from the
  message.** `BidDataValidationException` and
  `BidDataSubmissionException` expose a `code` getter
  (`INVALID_QUANTITY`, `INVALID_AMOUNT`, `NOT_YOUR_BID_DATA`,
  `BID_DATA_NOT_FOUND`, `ROUND_CLOSED`, `NOT_YOUR_BID_ROUND`,
  `BID_ROUND_NOT_FOUND`). `GlobalExceptionHandler` maps them to HTTP
  statuses; the frontend's typed error classes
  (`VersionConflictError`, `RoundClosedError`, `RateLimitedError`)
  check on these codes, not on substrings.

### Alternatives considered

- **Pre-generate all rows at Round 1 start.** Rejected: the R1 init
  cron listener would have to fan out across every qualified buyer
  code (~580 in prod), multiplying the listener's tx size by two
  orders of magnitude. Lazy generation distributes the write across
  the window when bidders actually open the page.
- **Row-level lock (`SELECT ... FOR UPDATE`) instead of advisory lock.**
  Rejected: there is no single row to lock — the whole point is to
  serialize the `INSERT ... SELECT` against itself. An advisory lock
  keyed on the pair is exactly the right primitive.
- **Store a `no_cap` boolean alongside `bid_quantity INT NOT NULL`.**
  Rejected: doubles the null-safety surface (both columns must agree)
  and the save path becomes a branch. `NULL` as the sentinel is
  idiomatic Postgres and one less column.
- **Return `409 Conflict` with `Retry-After` on the rate limit.**
  Rejected in favor of `429 Too Many Requests` (empty body) — the
  standard code for the scenario. The frontend's
  `RateLimitedError.retryAfterMs` is a client-side backoff and
  doesn't depend on a server header.
- **Transition `bid_round.round_status` to a synthetic `Submitted`
  value on submit.** Rejected: Mendix keeps `round_status` as
  lifecycle-only (`Scheduled | Started | Closed | Unscheduled`) and
  tracks submission on the `BidRound.submitted` flag. Adding a
  synthetic status would diverge from the cron's transition matrix.

### Consequences

- First-open latency scales with the row count for the bidder's
  buyer-code slice (~500–600 rows → ~1s in dev Postgres). Subsequent
  opens are one SELECT.
- Two concurrent tabs for the same `(bidder, round, buyer_code)`
  serialize at the advisory lock — the second tab waits for the
  first to commit, then short-circuits because rows now exist.
- Submit can be called repeatedly until the cron flips the round to
  `Closed`. An admin recovery of a stuck submit is a plain HTTP
  call, not a DB surgery.
- Rate limiter state is in-process. A horizontally-scaled backend
  would over-permit (N instances × 60/min) — acceptable for current
  single-node deploys; revisit if/when we scale out.
- The R2/R3 threshold stub (`bid_meets_threshold = TRUE`) is a known
  gap. Sub-project 4 will add the real per-round qualification
  predicates; the frontend contract (rows filtered to "qualified
  only" on R2/R3) does not change.

### References

- Plan: `docs/tasks/auction-bid-data-create-plan.md`
- Spec: `docs/tasks/auction-bid-data-create-design.md`
- Schema: `V73__bid_data_docs_and_submit_columns.sql`
- Related ADRs: 2026-04-22 (R1 init + QBC flattening),
  2026-04-20 (cron skeleton + event contract),
  2026-04-19 (admin security matcher ordering)
- Mendix source:
  `migration_context/backend/ACT_OpenBidderDashboard.md`,
  `ACT_CreateBidData.md`, `ACT_SubmitBidRound.md`

---

## 2026-04-22 — Sub-project 4A: EB module port

**Decision:** Ported the Mendix `ecoatm_eb` module into `auctions.reserve_bid`
+ `reserve_bid_audit` + `reserve_bid_sync` with full bidirectional
Snowflake sync (push on write + 30-min pull cron).

**Rationale:** Sub-project 4C's target-price CTE joins against reserve
bids; that table must exist and be kept in sync with the external
pricing engine's authoritative Snowflake data.

**Key choices:**
- Schema lives in `auctions` (not a new `exchange_bid` namespace) — EB
  is consumed solely by auctions
- `product_id` as `VARCHAR(100)` to match `bid_data.ecoid` join key
- Dropped Delete-All bulk button (operational risk > parity value)
- Pull path uses delete-all + re-insert (matches Mendix); explicitly
  does NOT publish `ReserveBidChangedEvent` to prevent echo loops
- Feature guard via `eb.sync.enabled` Spring property (modern lacks a
  generic feature-flag service)

**Consequences:**
- Admin UI at `/admin/auctions-data-center/reserve-bids/**`
- New Flyway V74 (schema) + V75 (data load, 14,657 rows)
- Sub-project 4C unblocked on the EB dimension

---

## 2026-04-22 — Auction R1 init: listener + admin endpoint + QBC schema flattening

**Status:** Accepted (sub-project 2 of `docs/tasks/auction-lifecycle-cron-design.md`).

### Context

Mendix `SUB_InitializeRound1` fires when a Round 1 scheduling auction
transitions `Scheduled → Started`. It performs three effects:
(1) clamp aggregated-inventory non-DW `avg_target_price` below the
`minimum_allowed_bid` floor; (2) clamp DW `dw_avg_target_price` the
same way; (3) rewrite the Qualified Buyer Codes set — delete any
existing QBCs for the scheduling auction, insert one fresh QBC per
active wholesale/data-wipe buyer code with
`qualification_type='Qualified'`, `included=true`,
`is_special_treatment=false`. Ports `ACT_UpdateRound1TargetPrice_MinBid`
+ `SUB_CreateQualifiedBuyersEntity` + `SUB_ClearQualifiedBuyerList`
into our listener + service model, skipping
`SUB_HandleSpecialTreatmentBuyerOnRoundStart` (deferred).

### Decision

- **Post-commit listener + admin recovery endpoint.** `R1InitListener`
  is a `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`
  consumer of `RoundStartedEvent`, gated by `auctions.r1-init.enabled`
  via `@ConditionalOnProperty`. Admin endpoint
  `POST /api/v1/admin/auctions/{id}/rounds/1/init` (Administrator only,
  synchronous) calls the same `Round1InitializationService.initialize`
  entry point and is never gated.
- **Single `@Transactional(REQUIRES_NEW, timeout=30)` method owns all
  three effects.** All-or-nothing: any failure rolls back the clamp
  and QBC rewrite together. The round transition itself has already
  committed by the time this runs, so a failure here does not undo
  Started status — matches the 2026-04-20 cron ADR.
- **QBC schema flattening (V72).** Mendix modeled SA ↔ QBC and
  BuyerCode ↔ QBC as M:N junctions (`qbc_scheduling_auctions`,
  `qbc_buyer_codes`). Each QBC row in practice belongs to exactly one
  SA and one BuyerCode. V72 adds `scheduling_auction_id` +
  `buyer_code_id` directly on `qualified_buyer_codes`, backfills from
  the junctions, drops both junction tables, and enforces
  `UNIQUE(scheduling_auction_id, buyer_code_id)`. Enables one-hop
  delete on the SA rewrite (`SUB_ClearQualifiedBuyerList` parity) and
  eliminates a degenerate 2-hop join for "which auction is this QBC
  for".
- **Direct SA ↔ BuyerCode association (`SchedulingAuction_QualifiedBuyers`)
  dropped.** The QBC graph already encodes the same set; the Mendix
  association was historical modeling duplication.
- **Dev-first rollout.** `auctions.r1-init.enabled=true` in dev
  `application.yml`; `false` in `application-test.yml` so unit/IT
  suites stay deterministic (the full-chain IT opts in via
  `@TestPropertySource`). QA enables via env var after one full
  manual dev cycle + intentional failure drill.

### Alternatives considered

- **Split into three services** (clamp, clear QBCs, create QBCs).
  Rejected — YAGNI. The three effects are cohesive by contract and
  Mendix treats them as one microflow. Nothing else in the port needs
  any step in isolation.
- **Per-listener audit table** (e.g., `auctions.round_init_run`).
  Deferred — logs are the audit surface for Phase 1, consistent with
  sub-project 1's `[deferred-writer]` stance. `integration.scheduled_job_run`
  already captures the tick-level aggregate.
- **Keep the Mendix `SchedulingAuction_QualifiedBuyers` association as
  a parallel junction.** Rejected — duplicates state that the
  flattened QBC row already encodes; a QBC rewrite would have to stay
  in sync with two tables.
- **Rename `snowflakeExecutor` → `auctionDownstreamExecutor`.** YAGNI
  until 2–3 more listeners share the pool.

### Consequences

- One R1 listener invocation performs at most two UPDATEs + one DELETE
  + N INSERTs (one per active wholesale/data-wipe buyer code — ~580
  in prod). All within a single tx bounded by 30s.
- Listener failures are logged at ERROR and swallowed — the async
  executor thread is not poisoned. Monitoring must watch the
  `r1-init failed ...` log line.
- Admin endpoint is the recovery path. Running it a second time
  against the same auction is a no-op on clamp counts (nothing below
  floor anymore) but fully rewrites the QBC set.
- Unique constraint `uq_qbc_sa_bc` means a concurrent double-fire
  (listener + admin) would throw and roll back the later run — safe
  by construction.
- Special-treatment buyer handling (Mendix
  `SUB_HandleSpecialTreatmentBuyerOnRoundStart`) is deferred to a
  later sub-project.

### References

- Plan: `docs/tasks/auction-r1-init-plan.md`
- Spec: `docs/tasks/auction-r1-init-design.md`
- Schema: `V72__buyer_mgmt_qbc_flatten.sql`
- Related ADRs: 2026-04-21 (Snowflake push pattern + executor),
  2026-04-20 (cron + event contract), 2026-04-19 (admin security
  matcher ordering, entity-less FK)
- Mendix source:
  `migration_context/backend/services/SUB_InitializeRound1.md`,
  `ACT_UpdateRound1TargetPrice_MinBid.md`,
  `services/SUB_CreateQualifiedBuyersEntity.md`,
  `services/SUB_ClearQualifiedBuyerList.md`,
  `Act_GetOrCreateBuyerCodeSubmitConfig.md`

---

## 2026-04-21 — Auction status Snowflake push: listener wired, writer deferred

**Status:** Accepted (Phase 1 of sub-project 1,
`docs/tasks/auction-status-snowflake-push-plan.md`).

### Context

Mendix `SUB_SendAuctionAndSchedulingActionToSnowflake_async` pushes one row
to Snowflake on every round Start/Close transition. The microflow docs
describe the call shape (export XML → resolve username → `ExecuteDatabaseQuery`)
but the target Snowflake table schema is opaque — we don't yet know the
column set or type mapping. Blocking sub-project 1 on that archaeology
would starve the listener contract the other five sub-projects (R1/R2/R3
init, bid ranking, R3 pre-process) need to co-exist with on the event bus.

### Decision

- **Ship the listener, payload record, writer interface, and feature flag
  now.** `AuctionStatusSnowflakePushListener` consumes
  `RoundStartedEvent` / `RoundClosedEvent` via
  `@TransactionalEventListener(AFTER_COMMIT)` + `@Async("snowflakeExecutor")`,
  re-fetches the auction + week aggregate in a REQUIRES_NEW read-only tx,
  and hands an `AuctionStatusPushPayload` to an
  `AuctionStatusSnowflakeWriter`.
- **Default writer is logging-only.** `LoggingAuctionStatusSnowflakeWriter`
  emits a single structured INFO line prefixed with `[deferred-writer]`
  containing every field the real writer will eventually persist
  (`action`, `auctionId`, `auctionTitle`, `weekId`, `weekDisplay`,
  `round`, `transitionedAt`, `actor`). The marker makes it trivial to
  grep logs for unshipped payloads once the real writer lands.
- **Feature flag at the listener, not the writer.**
  `auctions.snowflake-push.enabled=false` short-circuits inside the
  listener before any DB read, so the feature is fully inert in dev/CI.
  Flipping it on in QA/prod is a config change, no redeploy.
- **Swallow writer failures.** The listener catches `RuntimeException`
  from the writer and logs at ERROR — matches the "downstream failures
  never undo the round transition" contract from the 2026-04-20 cron ADR
  (listeners fire post-commit; their failures cannot roll back the
  transaction that already committed).

### Alternatives considered

- **Port the real Snowflake `INSERT` now.** Rejected: target table is
  unknown; guessing would bake in a shape we'd have to migrate off.
- **Pull the feature flag from `BuyerCodeSubmitConfig.SendAuctionDataToSnowflake`
  (Mendix parity).** Rejected: the `AuctionsFeatureConfig` entity sits on
  a parallel branch; coupling the two is avoided by using yml. When that
  entity lands, swapping the flag source is a one-line change.
- **Let writer exceptions propagate to fail the round transition.**
  Rejected: the transaction has already committed by the time the
  `AFTER_COMMIT` listener runs — propagating would at best log a second
  stack trace, at worst poison the `snowflakeExecutor` thread.

### Consequences

- The six-listener contract from the 2026-04-20 cron ADR is unbroken:
  push is live, consuming events, on `snowflakeExecutor`.
- Flipping to real Snowflake delivery is a single-class drop-in
  (`@Primary` override or a second `@ConditionalOnProperty`-gated bean).
  The payload shape is part of the contract and must not change without
  a writer migration.
- The INFO log line is the current audit surface.
  `integration.snowflake_sync_log` will be wired when the real writer
  lands — the agg-inventory sync already uses it, so the table is ready.
- Test coverage: 3 unit test classes (`AuctionStatusPushPayloadTest`,
  `LoggingAuctionStatusSnowflakeWriterTest`,
  `AuctionStatusSnowflakePushListenerTest` — 5 branches) + 1 IT
  (`AuctionStatusSnowflakePushIT`) exercising the full
  cron → event → listener → capturing-writer chain across the async
  executor boundary. All green.

### References

- Plan: `docs/tasks/auction-status-snowflake-push-plan.md`
- Mendix source: `migration_context/backend/services/SUB_SendAuctionAndSchedulingActionToSnowflake_async.md`,
  `ACT_SetAuctionScheduleStarted.md`, `ACT_SetAuctionScheduleClosed.md`
- Related ADRs: 2026-04-20 (cron skeleton, event contract),
  2026-04-18 (agg-inventory sync — same post-commit + @Async executor
  pattern, different writer surface).

---

## 2026-04-20 — Auction lifecycle cron: per-row tx, ShedLock leader election, event-driven downstream

**Status:** Accepted (sub-project 0 of `docs/tasks/auction-lifecycle-cron-design.md`).

### Context

Mendix `AuctionUI.ACT_ScheduleAuctionCheckStatus` runs every minute and
transitions auction rounds (`Scheduled→Started`, `Started→Closed`),
fanning out into Round 1/2/3 init, bid ranking, target price calc,
special-buyer processing, and Snowflake push. The downstream tree is the
entire Mendix bid engine. We split the port into seven sub-projects;
this ADR covers sub-project 0 — the cron skeleton and event contract
that the other six will subscribe to.

### Decision

- **Multi-instance safety via ShedLock + `infra.shedlock`.** Spring
  `@Scheduled` runs on every JVM by default; ShedLock's
  `@SchedulerLock(name="auctionLifecycle", lockAtLeastFor=10s,
  lockAtMostFor=55s)` ensures only one node executes per tick.
  `lockAtMostFor < poll-ms` so a crashed leader is reclaimable on the
  next minute.
- **Per-row REQUIRES_NEW transactions.** The orchestrator
  (`AuctionLifecycleService.tick()`) opens no tx itself; each row
  transition runs in its own short tx via `RoundTransitionService`,
  guarded by a `SELECT ... FOR UPDATE` re-check. One bad row logs at
  ERROR and is skipped; others proceed. Trades the legacy Mendix
  all-or-nothing atomicity for forward-progress under partial failure.
- **Hybrid event contract.** Cron-driven transitions emit
  `RoundStartedEvent` / `RoundClosedEvent` (post-commit). The existing
  `AuctionScheduledEvent` / `AuctionUnscheduledEvent` continue to fire
  from the admin HTTP flows. Downstream listeners filter on `round`
  field instead of subscribing to a generic transition event.
- **Reusable `infra` schema.** `infra.scheduled_job_run` records every
  tick (status, duration, counters JSONB, node id) and is reusable for
  any future cron job. `infra.shedlock` is the ShedLock JDBC table.
  Audit shape is *lightweight + payload counters* — same write cost as
  status-only, ~10× more useful for forensic debugging.
- **Six stub listeners ship with this PR.** They log "would do X" and
  define the contract sub-projects 1-6 must preserve when they replace
  each stub with real logic (Snowflake push, R1/R2/R3 init, bid
  ranking, special-buyer processing, R3 pre-process).

### Alternatives considered

- **Postgres advisory lock** (`pg_try_advisory_lock`) — simpler than
  ShedLock, no new dependency. Rejected because the user explicitly
  anticipates many more cron jobs, and ShedLock provides per-job naming
  and TTL semantics out of the box.
- **Single tx for the whole tick** (Mendix shape) — preserves legacy
  atomicity. Rejected because one bad row would block all others on
  every tick until manually cleared; per-row tx prefers liveness.
- **Reuse `AuctionScheduleService` for the cron entry point.**
  Rejected — it conflates user-driven actions (HTTP, validates against
  bids/started rounds) with system-driven actions (cron, no validation,
  just transitions). Different invariants, different audit needs,
  different test surface.

### Consequences

- A round picked up by the selector but flipped between selector and
  `FOR UPDATE` re-check throws `RoundAlreadyTransitionedException`,
  which the orchestrator treats as benign and logs at DEBUG only.
- Listener failures do NOT roll back the round transition (they fire
  post-commit). Sub-projects 1-6 must each handle their own retry /
  DLQ semantics.
- The `counters` JSONB allows future jobs to add their own keys without
  schema migrations — but the existing four (`roundsStarted`,
  `roundsClosed`, `auctionsAffected`, `errorCount`) are part of the
  audit contract and cannot be renamed.
- Tests disable the cron via `application-test.yml`; integration tests
  drive `service.tick()` (or `scheduler.runTick()`) directly.
- Coverage target on `service/auctions/lifecycle/**` and
  `infra/scheduledjob/**` is 90%+ — central code, must stay tested.

### References

- Plan: `docs/tasks/auction-lifecycle-cron-plan.md`
- Spec: `docs/tasks/auction-lifecycle-cron-design.md`
- Schema: `V70__infra_schema_and_scheduled_job_run.sql`,
  `V71__infra_shedlock.sql`
- Mendix source: `migration_context/backend/ACT_ScheduleAuctionCheckStatus.md`,
  `ACT_SetAuctionScheduleClosed.md`, `ACT_SetAuctionScheduleStarted.md`,
  `services/SUB_SetAuctionStatus.md`

---

## 2026-04-20 — Auction lifecycle: Create persists only the auction row; Schedule persists the rounds (partial supersession of 2026-04-19)

**Status:** Accepted. Partially supersedes the 2026-04-19 ADR
("Create Auction: dedicated endpoint + in-tx round creation +
enum-as-varchar"). The enum-as-varchar, dedicated-endpoint, and
method-security decisions from the prior ADR remain in force.

### Context

Porting the Mendix Auction Scheduling + Confirm flow
(`docs/tasks/auction-scheduling-plan.md`) surfaced three divergences
between the 2026-04-19 implementation and the Mendix source of truth:

1. **Title example in the ADR is wrong.** The narrative used
   `"Auction Week 17 2026"`, but `Week.weekDisplay` has shape
   `"YYYY / WkNN"` (see `V58__create_auctions_schema_and_core.sql:29`
   and `V65__seed_mdm_week.sql:42`). Existing legacy rows in
   `auctions.auctions` use titles like `"Auction 2026 / Wk04"`. The
   runtime code in `AuctionService.buildAuctionTitle` is already
   correct — only the ADR example was misleading.
2. **Round-creation timing is wrong (architectural).** The 2026-04-19
   ADR stated that `POST /admin/auctions` writes the auction row plus
   three `SchedulingAuction` rows in one tx. Mendix
   `ACT_Create_Auction` only writes the Auction row plus a transient
   helper; the three rounds are persisted later by
   `ACT_SaveScheduleAuction` when the admin clicks **Confirm** on the
   scheduling page. Our current endpoint conflates the two Mendix
   steps.
3. **Round 3 display name is wrong.** The ADR and current code emit
   `"Round 3"`. Mendix `ACT_SaveScheduleAuction` uses the verbatim
   string `"Upsell Round"`, which surfaces in the scheduling UI and
   in email templates.

### Decision

- **Title example correction.** The canonical title is
  `"Auction " + Week.weekDisplay`, e.g. `"Auction 2026 / Wk04"`. No
  code change — this is a narrative fix only.
- **Split Create from Schedule.** `POST /admin/auctions` will persist
  **only** the `auctions.auctions` row (status `Unscheduled`). A new
  `PUT /admin/auctions/{id}/schedule` will persist the three
  `auctions.scheduling_auctions` rows and flip the auction to
  `Scheduled` in a single tx. The lifecycle invariant is explicit:
  - `Unscheduled` → exactly 0 rounds.
  - `Scheduled` / `Started` / `Closed` → exactly 3 rounds.
- **Round 3 name.** Round 3's display name is `"Upsell Round"`, not
  `"Round 3"`. Rounds 1 and 2 keep their numeric labels. The name is
  a presentation concern; the underlying `round` column stays `1|2|3`.
- **No data migration.** No auctions have been created through the
  new-app endpoint in any real environment yet, so refactoring does
  not require backfilling or splitting existing rows.

### Consequences

- `AuctionService.createAuction` will be refactored in Phase B of
  `docs/tasks/auction-scheduling-plan.md` to stop writing rounds.
  A new `AuctionService.scheduleAuction(id, offsets)` will own the
  round persistence and status flip.
- `docs/api/rest-endpoints.md` will be updated: the `rounds[]` block
  drops out of the `POST /admin/auctions` `201` response, and a new
  `PUT /admin/auctions/{id}/schedule` section is added.
- The 2026-04-19 ADR stays in place as historical context — its
  decisions on enum storage (`VARCHAR(20)` + `@Enumerated(STRING)`),
  the dedicated `/api/v1/admin/auctions` controller, the explicit
  `SecurityConfig.requestMatchers` rule for SalesOps, and the two
  distinct 409 error shapes (duplicate title vs. duplicate week)
  remain in force and are not revisited here.
- The existing atomicity argument from the 2026-04-19 ADR
  (auction + rounds must commit together) still applies — but now it
  applies to the `PUT .../schedule` transaction, not to `POST`. An
  `Unscheduled` auction with zero rounds is a valid, expected state.
- Tests: the seven `AuctionServiceTest` cases and four
  `AuctionControllerTest` cases will be re-shaped in Phase B. No
  test is deleted; assertions on round creation move to the new
  schedule-service tests.

### References

- Plan: `docs/tasks/auction-scheduling-plan.md`
- Amended ADR: 2026-04-19 — Create Auction (archived in `decisions-archive.md`)
- Mendix source:
  `migration_context/backend/ACT_Create_Auction.md`,
  `migration_context/backend/ACT_SaveScheduleAuction.md`,
  `migration_context/backend/ACT_Create_SchedulingAuction_Helper_Default.md`
- Schema: `backend/src/main/resources/db/migration/V58__create_auctions_schema_and_core.sql`,
  `V65__seed_mdm_week.sql`


## Older entries

ADRs dated 2026-04-19 and earlier have been moved to
[`decisions-archive.md`](./decisions-archive.md) to keep this file
compact. They remain authoritative for the decisions they record and
are cross-referenced from newer entries above.

Archived entries:

- 2026-04-19 — Create Auction: dedicated endpoint + in-tx round creation + enum-as-varchar (partially superseded by the 2026-04-20 Schedule split above)
- 2026-04-18 — Aggregated Inventory: non-DW KPI totals must filter DW-only groups
- 2026-04-18 — Aggregated Inventory: Snowflake sync via post-commit event + watermark-based incremental pull
- 2026-04-17 — Aggregated Inventory: compute totals at read time + keep quantity override flag
- 2026-04-13 — Auth token moved from localStorage to HttpOnly cookie
- 2026-04-13 — PWS email delivery: post-commit event + async + feature flag

## 2026-04-25 — Sub-project 4B: PO module port

**Status:** Accepted.

**Context:** Mendix `ecoatm_po` ships PurchaseOrder + PODetail authored
via Excel upload, with push-only Snowflake sync (no pull cron,
porefreshtimestamp watermark). Sub-project 4C's target-price CTE joins
`po_detail.price` into its `GREATEST(...)` term, so 4B must port the
schema + admin surface before 4C can compute.

**Decision:** Port `purchase_order` + `po_detail` only (drop `weekly_po`,
`week_period`, `purchase_order_doc`, `pohelper`). Push-only Snowflake
sync. Lifecycle state derived from week range. Wipe-and-replace upload
with strict-rejection error posture. `Administrator` + `SalesOps` role
gate.

**Consequences:** 4C unblocks. Snowflake recovery = admin re-upload (same
as Mendix). No fulfillment-tracker port — if `weekly_po` becomes an ops
ask later, it's a follow-up. `temp_buyer_code` column carried forward
for Snowflake payload parity; can be dropped after QA confirms the proc
doesn't read it.

**Spec / Plan:**
- `docs/tasks/auction-po-module-design.md`
- `docs/tasks/auction-po-module-plan.md`
