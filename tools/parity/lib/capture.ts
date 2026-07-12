// Core two-sided capture: navigate → journey → ready → normalize → masks → PNG.
import * as fs from 'fs';
import * as path from 'path';
import type { Browser, Locator, Page } from '@playwright/test';
import type { ParityPage, Step } from '../schema';
import { contextOptions, FIXED_TIME, normalize, SETTLE_MS } from './context';
import { ensureAuth } from './auth';
import { loadConfig } from './manifest';

export const OUT_DIR = path.join(__dirname, '..', 'out');
type Side = 'legacy' | 'new';

async function runSteps(page: Page, steps: Step[] | undefined): Promise<void> {
  for (const s of steps ?? []) {
    if (s.click) await page.locator(s.click).first().click();
    if (s.fill) await page.locator(s.fill).first().fill(s.value ?? '');
    if (s.waitFor) await page.locator(s.waitFor).first().waitFor({ state: 'visible', timeout: 30_000 });
    if (s.waitMs) await page.waitForTimeout(s.waitMs);
  }
}

async function awaitReady(page: Page, readySelector?: string): Promise<void> {
  await page.waitForLoadState('networkidle', { timeout: 30_000 }).catch(() => {});
  await page
    .evaluate(() => (document as unknown as { fonts?: { ready: Promise<unknown> } }).fonts?.ready)
    .catch(() => {});
  if (readySelector) {
    await page.locator(readySelector).first().waitFor({ state: 'visible', timeout: 60_000 });
  }
}

export async function captureSide(browser: Browser, p: ParityPage, stateId: string, side: Side): Promise<string> {
  const cfg = loadConfig();
  const base = side === 'legacy' ? cfg.legacyBase : cfg.newBase;
  const sideDef = side === 'legacy' ? p.legacy : p.new;
  const role = side === 'legacy' ? p.auth.legacy : p.auth.new;
  const storageState = await ensureAuth(browser, side, role);

  const ctx = await browser.newContext(contextOptions(storageState ? { storageState } : {}));
  const page = await ctx.newPage();
  const outFile = path.join(OUT_DIR, side === 'legacy' ? 'legacy-local' : 'new', `${p.pageId}__${stateId}.png`);
  fs.mkdirSync(path.dirname(outFile), { recursive: true });

  try {
    await page.clock.setFixedTime(FIXED_TIME);
    await page.goto(base + sideDef.path, { waitUntil: 'domcontentloaded' });
    if (side === 'legacy' && sideDef.journey?.length) {
      // Mendix SPA must finish booting before the sidebar is clickable.
      await page.waitForSelector('.mx-page', { timeout: 90_000 });
    }
    await runSteps(page, sideDef.journey);
    const state = p.states.find((s) => s.id === stateId);
    await runSteps(page, side === 'legacy' ? state?.setup?.legacy : state?.setup?.new);
    await awaitReady(page, side === 'legacy' ? p.ready?.legacy : p.ready?.new);
    await normalize(page, side === 'legacy' ? 'legacy' : 'new');
    await page.waitForTimeout(SETTLE_MS);

    const masks: Locator[] = [];
    for (const m of p.masks) {
      const sel = side === 'legacy' ? m.legacy : m.new;
      if (sel) masks.push(page.locator(sel));
    }
    await page.screenshot({ path: outFile, animations: 'disabled', fullPage: p.fullPage ?? false, mask: masks });
    return outFile;
  } catch (e) {
    const shot = path.join(OUT_DIR, 'errors', `${side}-${p.pageId}-${stateId}.png`);
    fs.mkdirSync(path.dirname(shot), { recursive: true });
    await page.screenshot({ path: shot }).catch(() => {});
    throw new Error(`capture failed ${side}/${p.pageId}/${stateId} (error shot: ${shot}): ${e}`);
  } finally {
    await ctx.close();
  }
}
