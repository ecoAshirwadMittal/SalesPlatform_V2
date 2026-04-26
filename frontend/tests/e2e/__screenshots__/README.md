# Playwright Snapshot Baselines

Pixel-compare baselines for the wholesale + admin E2E suite.

**Captured on:** Linux chromium (in CI or via Docker), 1280×720 viewport.
**Strategy:** local baselines + semantic assertions — see ADR
"2026-04-25 — Pixel-compare strategy" in `docs/architecture/decisions.md`.
**Plan doc:** `docs/TODO/pixel-compare-strategy-plan.md`.

## How baselines are captured

Run the `e2e` GitHub Actions workflow with `update_snapshots: true`:

```
gh workflow run e2e.yml -f update_snapshots=true
```

The job runs `npx playwright test --update-snapshots`, generates the PNGs
under this directory, and uploads them as the `playwright-snapshots`
artifact. Download the artifact, place its contents here, and commit.

**Why CI and not local?** Reproducibility — Linux chromium renders
deterministically across machines, but macOS/Windows captures introduce
sub-pixel deltas (P3 vs sRGB, font hinting differences) that make
baselines flap unpredictably between contributors.

If you can't use CI, run locally inside a Linux Docker container:

```
docker run --rm -v "$(pwd):/work" -w /work mcr.microsoft.com/playwright:v1.47.0-jammy \
  bash -c "cd frontend && npm ci && npx playwright test --update-snapshots"
```

## Updating baselines after an intentional UI change

Any PR that changes pixel-rendered UI must update the baselines in the
same commit. Workflow:

1. Make the UI change locally.
2. Trigger CI with `update_snapshots=true` — OR run the Docker command above.
3. Download / capture the resulting PNG diff for review.
4. Commit the new baselines under this directory alongside the UI change.
5. CI on the next push runs `playwright test` (no update flag), comparing
   against the committed baselines. Green = the UI change is exactly
   what you intended to ship.

## What goes here

Playwright auto-names snapshot files using:

```
<spec-stem>-<test-title>-chromium-linux.png
```

So `wholesale-buyer-login.spec.ts` running the test
`"login page — pixel compare against local baseline"` produces:

```
__screenshots__/wholesale-buyer-login-login-page-pixel-compare-against-local-baseline-chromium-linux.png
```

Don't rename the files — Playwright resolves them by computed name.

## Status

This directory is **empty** until Phase 2 of the pixel-compare plan
runs. The 8 `test.fixme('… pixel compare against local baseline')`
blocks across the 6 wholesale specs stay fixme until baselines land
here. The 8 sibling `test('… semantic structure')` blocks already run
on every PR and catch structural regressions in the meantime.
