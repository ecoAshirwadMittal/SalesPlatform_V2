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

## Phase 5 — Dashboard header parity (in progress)


