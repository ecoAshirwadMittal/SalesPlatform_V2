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

  // snapshotPathTemplate points at docs/qa-reference/ so toHaveScreenshot()
  // compares against the committed QA reference files rather than generating
  // its own snapshot store inside the test tree.
  // {arg} is the filename passed to toHaveScreenshot() — e.g. 'qa-01-login.png'.
  snapshotPathTemplate: '../docs/qa-reference/{arg}',

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
