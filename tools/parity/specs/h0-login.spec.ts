// H0 proof: capture the login surfaces on all three apps with identical
// normalization, so reg-cli can diff (a) legacy-local vs legacy-hosted
// (certification / noise floor) and (b) new vs legacy-local (real parity).
// No auth needed on any target — that's why H0 lives on the login pages.
import { test, type Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

const OUT = path.join(__dirname, '..', 'out');
// setFixedTime (not clock.install): freezes Date/now but keeps timers running —
// clock.install pauses timer advancement, which can stall the Mendix client boot.
const FIXED_TIME = new Date('2026-07-11T12:00:00-05:00');

const SIDES: Record<string, string> = {
  'legacy-hosted': 'https://buy-qa.ecoatmdirect.com',
  'legacy-local': 'http://localhost:8082',
  'new': 'http://localhost:3000',
};

// Both legacy login entry points map onto the new app's single /login.
const PAGES: Array<{ id: string; paths: Record<string, string> }> = [
  {
    id: 'login-admin',
    paths: { 'legacy-hosted': '/login.html', 'legacy-local': '/login.html', 'new': '/login' },
  },
  {
    id: 'login-buyer',
    paths: { 'legacy-hosted': '/p/login/web', 'legacy-local': '/p/login/web', 'new': '/login' },
  },
];

async function normalize(page: Page): Promise<void> {
  await page.addStyleTag({
    content: `
      *, *::before, *::after {
        animation: none !important;
        transition: none !important;
        caret-color: transparent !important;
        scroll-behavior: auto !important;
      }
      html { scrollbar-width: none !important; }
      ::-webkit-scrollbar { display: none !important; }
    `,
  });
}

async function capture(page: Page, url: string, file: string): Promise<void> {
  await page.clock.setFixedTime(FIXED_TIME);
  await page.goto(url, { waitUntil: 'domcontentloaded' });
  await page.waitForLoadState('networkidle', { timeout: 30_000 }).catch(() => {});
  await page.evaluate(() => (document as unknown as { fonts?: { ready: Promise<unknown> } }).fonts?.ready).catch(() => {});
  await normalize(page);
  await page.waitForTimeout(750);
  await page.screenshot({ path: file, animations: 'disabled', fullPage: false });
}

test('H0: capture login surfaces on all three apps', async ({ page }) => {
  for (const [side, base] of Object.entries(SIDES)) {
    const dir = path.join(OUT, side);
    fs.mkdirSync(dir, { recursive: true });
    for (const p of PAGES) {
      await capture(page, base + p.paths[side], path.join(dir, `${p.id}.png`));
      console.log(`captured ${side}/${p.id}`);
    }
  }
});
