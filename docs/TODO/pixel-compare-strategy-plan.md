# Pixel-Compare Strategy: Local Baselines + Semantic Assertions (Option A + D)

**Status:** Planned — not yet executed.
**Owner:** TBD (open)
**Estimated effort:** ~5-6h focused work across 3 phases.
**Unblocks:** flipping 8 `test.fixme()` compares to passing `test()` calls; CI pixel-regression detection; removes the brittle dependency on Mendix production DB state.

---

## Context

Phase 13 shipped pixel-compare infrastructure pointing `toHaveScreenshot` at the QA reference PNGs in `docs/qa-reference/`. The pixel-parity sprint that followed tried to flip the 8 `test.fixme()` compares to passing tests and got **0/8** flipped — a structural blocker surfaced:

1. The QA reference PNGs were captured from **live Mendix production** with real DB data (actual ECO IDs, target prices, row counts visible in the grid). `page.route()` mocks cannot reproduce row-level exactness.
2. Environmental rendering deltas persist regardless of data:
   - macOS P3 colour space (QA capture env) vs Windows / Linux sRGB (local dev + CI)
   - GPU gradient rendering differences
   - `font-display: swap` flicker during Brandon Grotesque load
   - Browser minor version skew
3. Tightening tolerance to force passes would hide real regressions; loosening beyond 2% would make the compares uninformative.

The original goal of pixel-compare is **catching styling regressions we introduce** — not enforcing byte-for-byte parity with a 6-month-old production snapshot. The strategy needs to match that goal.

---

## Decision

Adopt a combined strategy:

### Option A — Local-baseline pixel-compare

- Drop the `snapshotPathTemplate` override; Playwright's default (`frontend/tests/e2e/__screenshots__/{spec}-{name}-chromium-linux.png`) becomes the baseline location.
- Capture baselines on a **Linux chromium runner** (CI or a container locally) for consistency — never capture on macOS/Windows locally and commit those.
- First run auto-generates baselines via `--update-snapshots`; subsequent runs diff against the checked-in PNGs.
- The baselines are **committed to git** under `frontend/tests/e2e/__screenshots__/`.
- When an intentional style change lands, the author regenerates baselines in the same PR.

### Option D — Semantic (role/component) assertions

- Each of the 8 fixme'd compare scenarios gets a **structural assertion block** alongside the pixel compare — tests the *intent* of the UI (key roles, text, visible states) rather than exact pixels.
- Catches regressions that pixel-compare misses (e.g. aria attribute drift, text copy change, element re-ordering that still renders pixel-identical).
- Not a replacement for pixel-compare, a complement.

### Keep QA PNGs as design references only

- `docs/qa-reference/` stays as-is — still the manual visual reference for anyone porting a surface.
- `docs/qa-reference/README.md` gets a clarifying note: "Design references, not automated test fixtures."

---

## Success criteria

1. All 8 prior `test.fixme()` pixel compares flip to passing `test()` calls against **local baselines**.
2. All 8 scenarios have **role/component assertion blocks** added (structural intent checks).
3. `frontend/tests/e2e/__screenshots__/` is committed with Linux chromium baselines — stable across machines.
4. `docs/qa-reference/README.md` explains the design-reference-only purpose.
5. An ADR records the strategy change.
6. CI stays green (no new failures).

---

## Phased execution

### Phase 1 — Strategy switch (0.5h)

**Files:**
- `frontend/playwright.config.ts`

**Steps:**
1. Remove the `snapshotPathTemplate: '../docs/qa-reference/{arg}{ext}'` override. Let Playwright default to `frontend/tests/e2e/__screenshots__/`.
2. Keep the viewport (`1280×720`) and tolerance (`maxDiffPixelRatio: 0.02`, `threshold: 0.2`, `animations: 'disabled'`) — they're still the right values for detecting drift against local baselines.
3. Verify the dev server `webServer` block still uses `reuseExistingServer: !process.env.CI` for the local+CI split.

### Phase 2 — Capture baselines (1h)

**Environment:** Linux chromium runner for reproducibility. Options:
- **Primary:** Run `npx playwright test --update-snapshots` inside the GitHub Actions workflow we already wired (`.github/workflows/e2e.yml`) and commit the resulting PNGs from the CI artifact to git locally.
- **Fallback:** Use a local Docker container (`mcr.microsoft.com/playwright:v1.47.0-jammy` or similar) to run `--update-snapshots` on Linux chromium.

**Files:**
- Flip `test.fixme()` → `test()` in all 8 spec locations listed below.
- Add `frontend/tests/e2e/__screenshots__/**/*.png` baselines (committed to git).

**Spec locations of the 8 compares:**

| # | Spec file | Test name |
|---|---|---|
| 1 | `wholesale-buyer-login.spec.ts` | `pixel-compare against QA login` |
| 2 | `wholesale-buyer-picker.spec.ts` | `pixel-compare against QA picker` |
| 3 | `wholesale-bidder-shell.spec.ts` | `pixel-compare against QA expanded shell` |
| 4 | `wholesale-bidder-shell.spec.ts` | `pixel-compare against QA collapsed shell` |
| 5 | `wholesale-submit-bids.spec.ts` | `pixel-compare against QA success modal` |
| 6 | `wholesale-submit-bids.spec.ts` | `pixel-compare against QA no-bids modal` |
| 7 | `wholesale-carryover.spec.ts` | `pixel-compare against QA carryover-empty` |
| 8 | `wholesale-import-export.spec.ts` | `pixel-compare against QA import modal` |

(Use the first half of the name column as the `toHaveScreenshot` argument — spec files reference the exact names.)

**Update screenshot filenames:**
- Previous references: `qa-01-login.png` / `qa-02-buyer-code-picker.png` / etc.
- New baselines under Playwright's default layout: **let Playwright generate them** with its default `{spec}-{title}-chromium-linux.png` naming. The new snapshots live under `__screenshots__/`, not the `docs/qa-reference/` tree. Drop the explicit argument entirely:
  ```ts
  // Before:
  await expect(page).toHaveScreenshot('qa-01-login.png', { ... });
  // After (let Playwright name it automatically):
  await expect(page).toHaveScreenshot({ maxDiffPixelRatio: 0.02 });
  ```

### Phase 3 — Add semantic assertions (3-4h)

For each of the 8 scenarios, add a **structural assertion block** *before* the pixel compare (so the semantic assertions fail first — faster to diagnose than a visual diff). Suggested assertion set per target:

#### 1. Login (`wholesale-buyer-login.spec.ts`)
```ts
await expect(page.getByRole('heading')).toContainText('Premium Wholesale & Weekly Auctions');
await expect(page.getByPlaceholder('Email')).toBeVisible();
await expect(page.getByPlaceholder('Password')).toBeVisible();
await expect(page.getByRole('button', { name: 'Login', exact: true })).toBeVisible();
await expect(page.getByRole('button', { name: 'Employee Login' })).toBeVisible();
await expect(page.getByRole('link', { name: 'Forgot Password?' })).toBeVisible();
await expect(page.getByRole('button', { name: 'Contact Us' })).toBeVisible();
await expect(page.getByLabel(/Show password|Hide password/)).toBeVisible(); // eye toggle
```

#### 2. Buyer-code picker (`wholesale-buyer-picker.spec.ts`)
```ts
// Assumes akshay+4-equivalent mock: 2 auction codes, no PWS
await expect(page.getByRole('heading', { name: /Welcome .+!/ })).toBeVisible();
await expect(page.getByText('Choose a buyer code to get started:')).toBeVisible();
await expect(page.getByRole('heading', { name: 'Weekly Wholesale Auction' })).toBeVisible();
await expect(page.queryByRole('heading', { name: 'Premium Wholesale Devices' })).toBeNull(); // no PWS section
const pills = page.getByRole('button', { name: /CHS Technology/ });
await expect(pills).toHaveCount(2);
```

#### 3. Bidder shell — expanded (`wholesale-bidder-shell.spec.ts`)
```ts
// Sidebar expanded: width ~220px, both menu items visible with labels
const sidebar = page.locator('[data-testid="bidder-sidebar"]'); // add data-testid if missing
await expect(sidebar).toBeVisible();
await expect(sidebar).toHaveCSS('width', /21\d|220/); // 210-220px range
await expect(page.getByRole('menuitem', { name: 'Auction' })).toBeVisible();
await expect(page.getByRole('menuitem', { name: 'Buyer User Guide' })).toBeVisible();
await expect(page.getByRole('link', { name: 'Switch Buyer Code' })).toBeVisible();
// Active-code chip in top bar
await expect(page.getByText('AD')).toBeVisible(); // buyer code
await expect(page.getByText('CHS Technology')).toBeVisible();
// User avatar button
await expect(page.getByRole('button', { name: /initials|user menu/i })).toBeVisible();
```

#### 4. Bidder shell — collapsed
```ts
// After clicking the toggle
const sidebar = page.locator('[data-testid="bidder-sidebar"]');
await expect(sidebar).toHaveCSS('width', /5[0-5]/); // ~54px
// Labels hidden; icons still visible
await expect(page.getByRole('menuitem', { name: 'Auction' })).toBeVisible(); // still has aria-label
// But the label span should be hidden (use .locator with a CSS selector)
await expect(sidebar.locator('.labelSpan')).toBeHidden(); // adapt selector name
```

#### 5. Submit success modal (`wholesale-submit-bids.spec.ts`)
```ts
const dialog = page.getByRole('dialog');
await expect(dialog).toBeVisible();
await expect(dialog.getByRole('heading', { level: 2 })).toContainText('Your');
await expect(dialog.getByRole('heading', { level: 2 })).toContainText('Bids');
await expect(dialog.getByRole('heading', { level: 2 })).toContainText('have been Submitted!');
await expect(dialog).toContainText('Please review your updated bids, quantity caps and resubmit for any changes.');
await expect(dialog.getByRole('button', { name: 'Close' })).toBeVisible();
// Verify the green "Bids" span uses the expected color
const bidsSpan = dialog.locator('.confirmationheadercolor');
await expect(bidsSpan).toHaveCSS('color', 'rgb(20, 172, 54)');
```

#### 6. No-bids modal
```ts
const dialog = page.getByRole('dialog');
await expect(dialog).toBeVisible();
await expect(dialog.getByRole('heading')).toContainText('No Bids to Submit');
await expect(dialog).toContainText('Please add Bids by');
// 3-item list, verbatim copy (including the "3.Use" artifact)
const list = dialog.getByRole('list');
await expect(list.getByRole('listitem')).toHaveCount(3);
await expect(list).toContainText('Entering bids in the screen');
await expect(list).toContainText('Use Export, add bids in the downloaded excel and import the file');
await expect(list).toContainText('Use Carryover function to carry bids from last week and make necessary changes');
await expect(dialog.getByRole('button', { name: 'Close' })).toBeVisible();
```

#### 7. Carryover empty-state (`wholesale-carryover.spec.ts`)
```ts
const dialog = page.getByRole('dialog');
await expect(dialog).toBeVisible();
await expect(dialog).toContainText("You don't have bids from last week to carry over.");
// No explicit heading visible; no primary CTA, just close (×)
const closeBtn = dialog.getByRole('button', { name: /close/i });
await expect(closeBtn).toBeVisible();
```

#### 8. Import modal (`wholesale-import-export.spec.ts`)
```ts
const dialog = page.getByRole('dialog');
await expect(dialog).toBeVisible();
await expect(dialog.getByRole('heading', { name: 'Import Your Bids' })).toBeVisible();
await expect(dialog).toContainText('To bulk import your bids:');
const steps = dialog.getByRole('list').getByRole('listitem');
await expect(steps).toHaveCount(4);
await expect(steps.nth(0)).toHaveText('Export your bid sheet');
await expect(steps.nth(1)).toHaveText('Update your bids and qty caps in the excel sheet');
// Preserve the Mendix "3.Upload" artifact verbatim
await expect(steps.nth(2)).toHaveText(/^3\.Upload your file here$/);
await expect(steps.nth(3)).toContainText('Please review your updated bids');
await expect(dialog).toContainText('Supported format: .xlsx');
await expect(dialog.getByRole('button', { name: 'Import' })).toBeVisible();
```

### Phase 4 — Documentation (0.5h)

1. **Update `docs/qa-reference/README.md`** — add a heading "### Purpose: design references, not automated fixtures" that explains the QA PNGs are for manual comparison, not pixel-compare. Point readers to the spec files for the actual automated checks.
2. **Update `docs/tasks/phase-13-e2e-handoff.md`** — mark the pixel-compare infrastructure as complete; note the strategy change.
3. **Add ADR** in `docs/architecture/decisions.md` — see the draft below.

---

## ADR draft (to be added at top of `docs/architecture/decisions.md`)

```
## 2026-04-NN — Pixel-compare strategy: local baselines + semantic assertions

**Status:** Accepted. Supersedes the Phase 13 compare-against-QA-reference
strategy documented in the 2026-04-23 wholesale-buyer-portal ADR.

### Context

The initial Phase 13 approach compared Playwright screenshots against QA
reference PNGs committed to `docs/qa-reference/`. The pixel-parity sprint
tried to flip 8 `test.fixme()` compares to passing `test()` calls and got
0/8 — the QA PNGs were captured from live Mendix production with real DB
state, and mock-driven Playwright specs can't reproduce row-level exactness.
Environmental rendering deltas (macOS P3 vs sRGB, GPU gradients,
font-display flicker) are persistent regardless of data.

### Decision

- Pixel-compare runs against **local Playwright baselines** under
  `frontend/tests/e2e/__screenshots__/`, captured on a Linux chromium runner
  for reproducibility.
- Each of the 8 prior compare scenarios gains a **semantic-assertion block**
  (role, text, state) ahead of the pixel-compare — catches regressions that
  pixel-compare misses (aria attrs, copy drift, reordering).
- QA PNGs in `docs/qa-reference/` stay as **manual design references only**,
  not automated fixtures.

### Consequences

- Pixel-compare catches *our* styling drift, not Mendix parity — the test
  signal matches the test intent.
- Semantic assertions improve diagnostic clarity (`Expected text "Login"
  but found "Log In"` is far better than "0.3% of pixels differ").
- Intentional style changes require a `--update-snapshots` pass in the same
  PR. That's a feature, not a bug.
- QA parity work still happens — via manual comparison against
  `docs/qa-reference/` during implementation — but isn't gated by CI.

### References

- `docs/TODO/pixel-compare-strategy-plan.md` — implementation plan
- Continuous run log: `docs/tasks/wholesale-buyer-continuous-run-log.md`
  sections on pixel-parity sprint + CI cleanup
- Related ADR: 2026-04-23 wholesale-buyer portal (visual parity section —
  now amended)
```

---

## Files touched (summary)

- `frontend/playwright.config.ts` — drop `snapshotPathTemplate` override
- `frontend/tests/e2e/wholesale-buyer-login.spec.ts` — semantic assertions + flip fixme
- `frontend/tests/e2e/wholesale-buyer-picker.spec.ts` — same
- `frontend/tests/e2e/wholesale-bidder-shell.spec.ts` — same (2 compares)
- `frontend/tests/e2e/wholesale-submit-bids.spec.ts` — same (2 compares)
- `frontend/tests/e2e/wholesale-carryover.spec.ts` — same
- `frontend/tests/e2e/wholesale-import-export.spec.ts` — same
- `frontend/tests/e2e/__screenshots__/**/*.png` — committed baselines (new)
- `docs/qa-reference/README.md` — design-reference note
- `docs/tasks/phase-13-e2e-handoff.md` — mark complete
- `docs/architecture/decisions.md` — new ADR entry

---

## Open decisions before starting

1. **Who captures baselines?** Recommend: GitHub Actions CI run with `--update-snapshots`, download the resulting screenshots as an artifact, check them into git. Keeps capture environment consistent across the team.
2. **Regeneration cadence?** Whenever a style/copy change is intentional. Enforced by the snapshot diff — anyone pushing a style change will see test failures and must regen + commit in the same PR. Document this in the test README.
3. **Do we also regenerate baselines when Brandon Grotesque changes weights/licensing (Q1 deferral)?** Yes — it's a visual change. Note in the ADR that font swaps trigger a baseline refresh.
4. **Should semantic assertions live in a shared `_helpers/assertions.ts` file?** Recommend yes — it's the same 4-8 lines per scenario, easy to extract. Saves LoC and makes the intent grep-able.

---

## Risks

1. **Baseline drift from non-deterministic renders.** Font-display swap, animation frames, or network-stubbed response timing could produce flaky baselines. Mitigation: `animations: 'disabled'` is already set; add `page.evaluate('document.fonts.ready')` before the snapshot if font-flash causes flakes; use `waitForLoadState('networkidle')`.
2. **Linux chromium on macOS author's machine.** Without Docker, local iteration is flaky. Document the Docker run command clearly. Alternative: always run via CI and download artifacts.
3. **Semantic assertions could ossify the DOM structure.** Use accessible queries (`getByRole`, `getByLabel`) rather than CSS selectors where possible — survives restyling.

---

## Non-goals

- **Re-capturing QA PNGs from Mendix production** — out of scope. QA PNGs remain as-is for manual reference.
- **Migrating the whole suite to visual-regression-as-a-service** (Percy / Chromatic) — considered but excluded. Local baselines + semantic assertions cover the core need; a hosted service is a separate decision.
- **Testing responsive breakpoints** — 1280×720 only. Responsive coverage is a separate Phase 13 follow-up.
