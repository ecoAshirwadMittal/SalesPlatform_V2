# Phase 13 — E2E + Pixel QA Handoff

**Status (2026-04-25 update):** Pixel-compare strategy switched — see ADR
"Pixel-compare strategy: local baselines + semantic assertions" in
`docs/architecture/decisions.md`. Each of the 8 prior pixel-compare
fixmes now has a passing **semantic-structure** test alongside it that
catches regressions today; the pixel compares stay fixme until Linux
chromium baselines are captured via `--update-snapshots` in CI and
committed under `frontend/tests/e2e/__screenshots__/`. Implementation
plan: `docs/TODO/pixel-compare-strategy-plan.md` (Phase 2 — baseline
capture — is the only remaining step).

E2E live infrastructure: shipped — `.github/workflows/e2e-live.yml`
runs the wholesale + admin + cascade specs against containerized
Postgres + Spring Boot on a nightly cron + manual dispatch.

**Original deferred status (preserved for context):** Required live dev + backend stack to execute.

## What's Already Done

E2E specs shipped in prior phases (all under `frontend/tests/e2e/`):

| Spec | Phase | Scenarios |
|---|---|---|
| `wholesale-buyer-login.spec.ts` | Phase 1 | Login redirect, eye-toggle a11y, forgot-password href, button visibility |
| `wholesale-buyer-picker.spec.ts` | Phase 2 | 7 tests: single-code skip, category gating, mixed, routing |
| `wholesale-bidder-shell.spec.ts` | Phase 4 | 6 tests: shell + sidebar toggle persistence + popover |
| `wholesale-submit-bids.spec.ts` | Phase 8 | 4 tests: empty-state modal, success modal, resubmit, edited amount |

All use `page.route()` mocks; none require a running backend.

## Phase 13 Acceptance Criteria — What Still Needs Doing

### 1. Pixel-compare fixtures against QA reference

- Add Playwright `toHaveScreenshot` comparisons in each spec against the corresponding `docs/qa-reference/qa-*.png` file
- Set viewport to 1280×720 (QA capture resolution)
- Tolerance: ≤2% pixel delta, resolution-aware (`maxDiffPixelRatio: 0.02`)
- Capture new baselines via `npx playwright test --update-snapshots` after review

Mapping (use these exact filenames):
- `wholesale-buyer-login.spec.ts` → `docs/qa-reference/qa-01-login.png`
- `wholesale-buyer-picker.spec.ts` → `docs/qa-reference/qa-02-buyer-code-picker.png`
- `wholesale-bidder-shell.spec.ts` → `docs/qa-reference/qa-03-bidder-dashboard-ad.png` (expanded) + `qa-07-sidebar-collapsed.png` (collapsed)
- `wholesale-submit-bids.spec.ts` → `docs/qa-reference/qa-08-submit-success-modal.png` + `qa-10-submit-no-bids-modal.png`

### 2. Additional E2E specs (not yet written)

- `wholesale-carryover.spec.ts` — click Carryover → mock 0-copied response → empty-state modal → Escape closes (Phase 9 didn't ship a dedicated spec)
- `wholesale-import-export.spec.ts` — click Export (verify download fires) → click Import → upload fixture xlsx → verify success modal (Phase 10 didn't ship)
- `wholesale-bid-grid.spec.ts` — sort arrows cycle, filter inputs filter rows, footer updates (Phase 6A has unit tests but no E2E)

### 3. Accessibility

- Add `@axe-core/playwright` dev dependency
- Add per-spec a11y assertions: `await expect(page).toPassA11y()`
- Keyboard navigation test: Tab through the grid, ensure focus visible on Price/Qty inputs, Enter/Escape close modals
- Sidebar toggle visible focus ring (currently has `:focus-visible` but not tested)

### 4. CI integration

- Update `.github/workflows/` to run `npx playwright test` against the dev server
- Containerize dev + backend for CI using `docker compose up -d` + `npm run test:e2e`
- Upload Playwright report as a workflow artifact on failure

### 5. Backend dependency for full coverage

- Phase 6B blocker (brand/model/modelName/carrier/added DTO expansion) lands first — grid E2E can't truly verify 11-column parity until those fields are in the API response
- Phase 11 blocker (DOWNLOAD endpoint) lands first — DOWNLOAD-mode E2E requires the new `/bidder/download-round-1` endpoint

## Running Locally (current state)

```bash
# Backend (required for live E2E; not for mock-based suites)
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm run dev

# Run existing mock-based E2E (no backend needed)
cd frontend && npx playwright test

# Run with UI mode for debugging
cd frontend && npx playwright test --ui
```

## Estimated effort

- Pixel-compare integration + baselines: ~4h
- 3 new E2E specs: ~6h
- axe-core + keyboard tests: ~3h
- CI workflow: ~2h
- **Total: ~15h of focused work** once Phase 6B and Phase 11 DOWNLOAD endpoint are in place.
