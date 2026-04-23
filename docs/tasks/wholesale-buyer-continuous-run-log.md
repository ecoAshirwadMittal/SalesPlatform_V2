# Wholesale Buyer Parity — Continuous Run Log

**Started:** 2026-04-23
**Mode:** Continuous execution across phases; defer only Minor issues / questions.

Phases completed + notes. Anything in **NEEDS REVIEW** should be surfaced to the user at end of run.

## Phase 0 — Foundation ✅
- Commits: `9410b16`, `71854cc`, `f546b31`, `fa26361`
- Reviews passed after 2 revisions.

## Phase 1 — Login parity ✅
- Commits: `2170496`, `6116481`, `5c2f64c`
- Reviews passed after 2 revisions.
- **NEEDS REVIEW:** 3 Minor items deferred to Phase 13 polish pass (card padding, button width, logo size).

## Phase 2 — Buyer-code picker conditional categories ✅
- Commits: `7ebefa3`, `50acc30`
- Reviews passed after 1 fix pass.
- **KEY FIX:** caught category-filter bug that would have routed AD/DDWS (Wholesale type) to PWS.
- **NEEDS REVIEW:** auction_card.png asset shows product imagery, not QA's warehouse-shelves photo. Image swap deferred.

## Phase 3 — Shared chrome primitives ✅
- Commits: `3b56d14`, `ce703d4`, `8fd9792`
- 4 chrome components + hook + lib + 129 passing tests.
- Combined spec + code-quality review; 4 Important fixes applied.
- **NEEDS REVIEW (deferred Minor items):**
  - Stale `// @vitest-environment jsdom` directive at top of `useActiveBuyerCode.ts:1` — harmless but should be removed when hook gets its own test file (Phase 4 scope).
  - `UserAvatarPopover` full-name `<span>` has `aria-hidden="true"` — screen readers hear only the avatar button label. Verify this is intentional vs. QA expectation.
  - `BuyerCodeChip` cross-feature import of `BriefcaseIcon` from `@/app/(dashboard)/buyer-select/BriefcaseIcon`. Promote to `components/icons/` when Phase 4 shell lands.
  - `useActiveBuyerCode` itself has no unit tests (deferred per reviewer — "acceptable as Phase 4 scope since the hook has no Phase 3 consumers yet").

## Phase 4 — BidderShell layout ✅
- Commit: `96ed504`
- **Layout strategy decision (Option B visual overlay over Option A route restructure):** Phase 4 wraps the bidder in `position: fixed; inset: 0; z-index: 500` to cover the inherited admin shell. Route-group restructuring to cleanly isolate layouts is deferred — would require moving 8+ files.
- `BidderShell` with gradient sidebar (54/220px), top-bar chrome, `useActiveBuyerCode` redirects, persistent collapse state.
- E2E: 6 active + 1 skipped.
- **NEEDS REVIEW:**
  - Skipped e2e test with TODO for CSS-visibility assertion requiring fixture work.
  - Layout strategy (Option B): reconsider Option A (route-group restructure) once other pages stabilize — could be a Phase 13+ cleanup.
  - `Buyer User Guide` sidebar link currently 404s; resolves when Phase 12 PDF endpoint ships.

## Phase 5 — Dashboard header parity ✅
- Commit: `475639a`
- Executed directly (subagent hit rate limit mid-phase; mechanical work finished manually).
- Created: `BidderTimer` + test (5 tests pass), `MinimumStartingBidLabel`, `CarryoverButton`, `ExportBidsButton`, `ImportBidsButton`, `dashboardHeader.module.css`.
- Rewrote `DashboardHeader.tsx` with two-H2 title, inline Export/Import, timer, green Submit Bids pill, min-bid label, Carryover.
- **Removed `SubmitBar.tsx` entirely** — QA has no bottom totals bar (plan §4.5). Totals display is now gone; Submit Bids moved into the header.
- Removed `lastSubmittedAt` state + display (Phase 8 replaces with modal).
- Lint clean; 5 new tests pass.
- **NEEDS REVIEW:**
  - SubmitBar removal: totals (rowCount, totalBidQuantity, totalBidAmount, totalPayout) are no longer displayed. QA doesn't show them, but check if any stakeholder expected them elsewhere.
  - Post-submit confirmation is currently silent (no toast, no stamp). Phase 8 adds the success modal — interim silent state is intentional.

## Phase 6A — BidGrid sort/filter/footer + Round-3 ADR ✅
- Commit: `fb6f87a`
- Scoped subset of full Phase 6 per the spec, since backend fields + virtualization libs were missing.
- Shipped: sort arrows (asc/desc/clear cycle) on all 7 existing columns, text/numeric column filters, sticky header, `Currently showing X of Y` footer, Round-3 reversal ADR in `docs/architecture/decisions.md`.
- 17 new BidGrid tests pass (9 sort, 5 filter, 3 footer).
- **NEEDS REVIEW (deferred as Phase 6B):**
  - **Backend DTO expansion required** — BidDataRow only exposes `ecoid` + `mergedGrade`. QA grid has 11 columns: Product Id, Brand, Model, Model Name, Grade, Carrier, Added, Avail Qty, Target Price, Price, Qty Cap. The missing 4 fields (brand, model, modelName, carrier, added date) need backend work: BidData JPA entity + DTO + the SELECT joining `mdm.device`/`mdm.brand`/`mdm.model`/`mdm.carrier`. Frontend grid can then render the full column set.
  - **Virtualization** — no TanStack Table or react-virtuoso installed. Current grid renders all rows (up to ~10k) as a plain HTML `<table>`. Defer addition until real-world perf measures bad, or when Phase 13 hardens the UI.
  - **`totalRowCount` passed as `rows.length` from client** — client computes locally instead of reading a backend-provided count. Fine for Phase 6A; revisit if Phase 6B introduces server-side pagination.

## Phase 7 — Bid cell inputs ✅
- Commit: `0d72670`
- `PriceCell` (dollar-format, strip-on-focus/reformat-on-blur), `QtyCapCell` (integer-only, null/0 sentinel distinction), `BidToast` (aria-live, no library).
- 18 new tests passing (8 PriceCell + 10 QtyCapCell); 168 total.
- Mendix CSS classes applied for grep parity (`auction-price`, `auction-qtycap`, `text-dollar`, `only-numbers`, `textbox-select-all`).
- Toast surfaces 3 error states (429 / 409 RoundClosed / VersionConflict) + auto-clears after 5s.

## Phase 8 — Submit Bids flow ✅
- Commit: `423f304`
- Shared `BidderModal` primitive + `SubmitBidsEmptyStateModal` + `BidsSubmittedModal`; verbatim QA copy (green `Bids` span, double-space preserved, "3.Use Carryover" artifact).
- 8 new tests passing (184 total).
- Client-side no-bids guard before POST; no pre-submit confirmation (matches Mendix).

## Phase 9 — Carryover flow ✅
- Commit: `d06908c`
- Real backend SQL (not stub): walks auction chain for most-recent prior `bid_round` with same `(buyer_code, round)`, single `UPDATE ... FROM` with advisory lock.
- `CarryoverResultModal` renders both empty-state ("You don't have bids from last week to carry over.") and success state.
- 5 backend + 8 frontend tests passing.
- **NEEDS REVIEW:**
  - Success-state copy `"Carried over N bids from Week X."` is **provisional** — needs QA verification when carryover has real prior-week data.
  - Rate limiting skipped on carryover endpoint (only advisory lock serializes). TODO logged in controller.
  - R2/R3 threshold filtering is stubbed from Phase sub-project 4 — carryover source set changes automatically when that ships; no special handling needed here.

## Phase 10 — Import / Export xlsx ✅
- Commit: `ccd58b6`
- Backend: Apache POI (SXSSFWorkbook streaming for export), `GET /bid-rounds/{id}/export` + `POST /bid-rounds/{id}/import`; 11-column xlsx matching full grid (includes missing Phase 6B columns via joining `mdm.device` at query time — export got its own copy of the join).
- Frontend: `ImportBidsModal` with idle→uploading→result state machine; verbatim Mendix `"3.Upload your file here"` (no space).
- Import shares the existing 60 req/min rate-limit bucket.
- 9 backend + 11 frontend tests passing.
- **NEEDS REVIEW:** Export join logic for brand/model/modelName/carrier/added is duplicated here because Phase 6B backend DTO expansion is deferred. When 6B lands, the DTO query and the export query should share a helper.

## Phase 11 — End-of-bidding mode polish ✅
- Commit: `945bd90`
- Migrated `EndOfBiddingPanel.tsx` off `'Trebuchet MS'` + hardcoded hexes onto design tokens; DOWNLOAD button remains a no-op TODO.
- **NEEDS REVIEW:**
  - DOWNLOAD mode button onClick still no-op — the mode returns without `bidRound` context so the existing `/bid-rounds/{id}/export` endpoint can't be used. New backend endpoint `GET /bidder/download-round-1?buyerCodeId=X` needed (finds the closed Round 1 of the active week). Documented in code comment.
  - Copy for all three modes (DOWNLOAD / ALL_ROUNDS_DONE / ERROR_AUCTION_NOT_FOUND) remains placeholder. Per Q4 decision, defer to live QA walkthrough when states are reachable.

## Phase 12 — Buyer User Guide admin upload ✅
- Commit: `84c0488`
- V74 migration + `admin.buyer_user_guide` table with partial unique index guaranteeing one active file.
- Backend: service (PDF validation, magic bytes, 20 MB cap, soft-delete) + controller (4 endpoints) + 29 tests.
- Frontend admin UI: Auction Control Center → Userguide Configuration page with upload + history.
- Bidder sidebar HEAD-checks `/bidder/docs/buyer-guide`; disables link with tooltip if no active guide.
- 11 new frontend Vitest tests; 208 total passing (1 pre-existing apiFetch-guard failure unchanged).
- **NEEDS REVIEW:** S3 storage deferred; files live on local disk under `backend/uploads/buyer-user-guide/` (gitignored). Multi-instance deploys will need shared storage.

## Phase 13 — E2E + pixel QA ⚠️ DEFERRED
- Full execution requires live dev + backend + CI setup and is blocked on Phase 6B (DTO expansion) + Phase 11 DOWNLOAD endpoint.
- E2E specs ARE shipped from prior phases: `wholesale-buyer-login`, `wholesale-buyer-picker` (7), `wholesale-bidder-shell` (6), `wholesale-submit-bids` (4).
- Handoff document: `docs/tasks/phase-13-e2e-handoff.md` with acceptance criteria, missing specs (carryover/import-export/grid), axe-core setup notes, CI integration, and estimated effort (~15h).
- **NEEDS REVIEW:** whole phase deferred — flag to user for scheduling.

## Phase 14 — /forgot-password route ✅
- Commit: `1281c95`
- V75 migration + `identity.password_reset_tokens` table (separate from V6's `sso` table).
- Backend: `PasswordResetService` (secure random + SHA-256 hash + 30-min TTL + one-time-use) + two endpoints; enumeration-resistant (always 200 on request).
- Frontend: `/forgot-password` + `/reset-password?token=X` pages, token-copy-from-log dev flow.
- 5 service tests + 10 controller tests + 10 E2E; 28 total auth controller tests, all pass.
- **NEEDS REVIEW:**
  - **Email delivery NOT wired** — `PasswordResetService.requestReset` only logs the token at INFO with `DEV:` prefix. Real SMTP integration deferred (follow the 2026-04-13 PWS email-delivery ADR pattern). Until then, dev/QA flow is: click "Forgot password?", grab the token from the backend log, navigate to `/reset-password?token=<rawToken>`.

## Phase 15 — ADR + docs closeout ✅
- Commit: `cda6ed2`
- Closeout ADR added to `docs/architecture/decisions.md` summarizing two-shell routing, chrome primitives, backend surface, copy decisions, deferred follow-ups.
- Handoff doc created at `docs/tasks/phase-13-e2e-handoff.md` for the deferred Phase 13 work.
- This run log is the narrative complement; the ADR is the durable record.

---

## End-of-run summary

**Completed:** Phases 0–12, 14, 15 (+ interim tasks for QA reference persistence and plan updates).
**Deferred:** Phase 6B (backend DTO expansion), Phase 13 (E2E + pixel QA), PWS shell parity, email SMTP wiring, DOWNLOAD endpoint wiring.

Full list of NEEDS REVIEW items lives inline above under each phase. ADR at `docs/architecture/decisions.md` (2026-04-23 closeout entry) is the canonical reference.











