// Shared, side-identical browser context options + normalization.
// browser.newContext() does NOT inherit playwright-config `use` options,
// so every context is built from here to guarantee both sides match.
import type { BrowserContextOptions, Page } from '@playwright/test';
import { loadConfig } from './manifest';

const cfg = loadConfig();

export const FIXED_TIME = new Date(cfg.fixedTime);
export const SETTLE_MS = cfg.settleMs;

export function contextOptions(extra: Partial<BrowserContextOptions> = {}): BrowserContextOptions {
  return {
    viewport: cfg.viewport,
    deviceScaleFactor: 1,
    colorScheme: 'light',
    reducedMotion: 'reduce',
    locale: 'en-US',
    timezoneId: 'America/New_York',
    ...extra,
  };
}

const KILL_MOTION_CSS = `
  *, *::before, *::after {
    animation: none !important;
    transition: none !important;
    caret-color: transparent !important;
    scroll-behavior: auto !important;
  }
  html { scrollbar-width: none !important; }
  ::-webkit-scrollbar { display: none !important; }
`;

/** Inject determinism CSS. Re-call after SPA journeys (Mendix re-renders drop styles). */
export async function normalize(page: Page, side: 'legacy' | 'legacy-hosted' | 'new'): Promise<void> {
  await page.addStyleTag({ content: KILL_MOTION_CSS });
  if (side === 'legacy' && cfg.legacyLocalHideCss.trim()) {
    await page.addStyleTag({ content: cfg.legacyLocalHideCss });
  }
}
