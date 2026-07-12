// Parity harness config — deliberately separate from frontend/playwright.config.ts:
// no webServer auto-start, strict determinism flags, sign-off viewport (1920).
// Run from frontend/ so @playwright/test resolves:
//   npx playwright test -c ../tools/parity/playwright.parity.config.ts
import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './specs',
  testMatch: ['**/run.spec.ts'],
  timeout: 240_000,
  fullyParallel: false,
  workers: 1,
  retries: 0,
  reporter: [['list']],
  use: {
    viewport: { width: 1920, height: 1080 },
    deviceScaleFactor: 1,
    colorScheme: 'light',
    reducedMotion: 'reduce',
    locale: 'en-US',
    timezoneId: 'America/New_York',
    launchOptions: {
      args: [
        // Both sides render in this one binary with one color profile —
        // kills the P3-vs-sRGB and AA-engine noise that sank the 0/8 attempt.
        '--force-color-profile=srgb',
        '--disable-lcd-text',
        '--font-render-hinting=none',
        '--hide-scrollbars',
      ],
    },
  },
  projects: [{ name: 'chromium', use: { browserName: 'chromium' } }],
});
