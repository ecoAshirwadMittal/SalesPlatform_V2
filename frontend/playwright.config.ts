import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: [['html', { open: 'never' }], ['list']],
  timeout: 60_000,

  // Per-assertion timeout (non-screenshot). Screenshot comparisons have their
  // own timeout governed by the overall test timeout.
  expect: {
    timeout: 10_000,
    toHaveScreenshot: {
      // ≤2% of pixels may differ — small rendering deltas (sub-pixel AA,
      // font hinting) are tolerated; structural regressions are caught.
      maxDiffPixelRatio: 0.02,
      // Per-pixel color tolerance (0–1 scale).  0.2 absorbs minor colour
      // drift between OS/GPU rendering environments.
      threshold: 0.2,
      // Disable CSS animations so frames are deterministic.
      animations: 'disabled',
    },
  },

  // Snapshot baselines live under Playwright's default location:
  // frontend/tests/e2e/__screenshots__/{spec}-{name}-chromium-linux.png.
  // Captured on a Linux chromium runner (CI or Docker) for reproducibility
  // across OS — committed to git, regenerated when intentional style
  // changes land. See ADR "Pixel-compare strategy: local baselines +
  // semantic assertions" (2026-04-25) and
  // docs/TODO/pixel-compare-strategy-plan.md.
  //
  // The QA PNGs in docs/qa-reference/ are now manual design references
  // only — not automated test fixtures. This avoids the "live Mendix
  // production data" reproducibility problem that made the prior
  // strategy hit 0/8 on the pixel-compare flip attempt.

  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    actionTimeout: 15_000,
    navigationTimeout: 30_000,
    // Capture at the same resolution as the QA reference screenshots.
    viewport: { width: 1280, height: 720 },
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],

  // webServer:
  //   CI  → start a fresh Next.js dev server (no existing server to reuse)
  //   local → reuse whatever is already running on :3000 (fast iteration)
  webServer: {
    command: 'npm run dev',
    port: 3000,
    reuseExistingServer: !process.env.CI,
    timeout: 120_000,
  },
});
