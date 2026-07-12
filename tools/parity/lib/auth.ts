// Per-side login → storageState files under tools/parity/auth/ (gitignored).
// States are reused within a freshness window so a full-suite run logs in once
// per (side, role). Credentials live in auth/creds.local.json — never in the
// committed manifests.
import * as fs from 'fs';
import * as path from 'path';
import type { Browser } from '@playwright/test';
import { contextOptions } from './context';
import { loadConfig } from './manifest';

const AUTH_DIR = path.join(__dirname, '..', 'auth');
const FRESH_MS = 20 * 60 * 1000; // Mendix session idle-expiry is ~30 min; stay under it

interface Cred {
  user: string;
  pass: string;
  loginPath?: string;
}
interface CredFile {
  legacy: Record<string, Cred>;
  new: Record<string, Cred>;
}

function creds(): CredFile {
  const p = path.join(AUTH_DIR, 'creds.local.json');
  if (!fs.existsSync(p)) {
    throw new Error(`Missing ${p} — create it (see tools/parity/README.md, gitignored)`);
  }
  return JSON.parse(fs.readFileSync(p, 'utf8')) as CredFile;
}

function isFresh(p: string): boolean {
  return fs.existsSync(p) && Date.now() - fs.statSync(p).mtimeMs < FRESH_MS;
}

/** Returns a storageState path for (side, role), logging in only when stale. */
export async function ensureAuth(browser: Browser, side: 'legacy' | 'new', role: string): Promise<string | undefined> {
  if (role === 'none') return undefined;
  const statePath = path.join(AUTH_DIR, `${side}-${role}.json`);
  if (isFresh(statePath)) return statePath;

  const cfg = loadConfig();
  const cred = creds()[side]?.[role];
  if (!cred) throw new Error(`No credentials for ${side}/${role} in creds.local.json`);
  fs.mkdirSync(AUTH_DIR, { recursive: true });

  const ctx = await browser.newContext(contextOptions());
  const page = await ctx.newPage();
  try {
    if (side === 'new') {
      await page.goto(cfg.newBase + '/login', { waitUntil: 'domcontentloaded' });
      // Next.js hydration wipes values filled too early — settle first, then
      // fill and verify the value stuck before submitting.
      await page.waitForLoadState('networkidle', { timeout: 20_000 }).catch(() => {});
      const email = page.getByPlaceholder('Email').first();
      const pass = page.getByPlaceholder('Password').first();
      for (let attempt = 0; attempt < 3; attempt++) {
        await email.fill(cred.user);
        await pass.fill(cred.pass);
        if ((await email.inputValue()) === cred.user && (await pass.inputValue()) === cred.pass) break;
        await page.waitForTimeout(1000);
      }
      await page.getByRole('button', { name: 'Login', exact: true }).click();
      await page.waitForURL((u) => !u.pathname.startsWith('/login'), { timeout: 30_000 });
    } else if ((cred.loginPath ?? '/login.html') === '/login.html') {
      // Classic Mendix admin login page
      await page.goto(cfg.legacyBase + '/login.html', { waitUntil: 'domcontentloaded' });
      await page.locator('input[name="username"], #usernameInput, input[type="text"]').first().fill(cred.user);
      await page.locator('input[name="password"], #passwordInput, input[type="password"]').first().fill(cred.pass);
      await page.locator('#loginButton, button[type="submit"], input[type="submit"], button').first().click();
      await page.waitForSelector('.mx-page', { timeout: 90_000 });
    } else {
      // Marketing buyer login (/p/login/web). NOTE: this page is itself a Mendix
      // page, so `.mx-page` is NOT a login-success signal — require the login
      // form to disappear (navigation away) before trusting the session.
      await page.goto(cfg.legacyBase + cred.loginPath, { waitUntil: 'domcontentloaded' });
      await page.waitForLoadState('networkidle', { timeout: 20_000 }).catch(() => {});
      const lEmail = page.getByPlaceholder('Email').first();
      const lPass = page.getByPlaceholder('Password').first();
      for (let attempt = 0; attempt < 3; attempt++) {
        await lEmail.fill(cred.user);
        await lPass.fill(cred.pass);
        if ((await lEmail.inputValue()) === cred.user && (await lPass.inputValue()) === cred.pass) break;
        await page.waitForTimeout(1000);
      }
      await page.getByRole('button', { name: 'Login', exact: true }).click();
      await page.locator('input[placeholder="Email"]').first().waitFor({ state: 'detached', timeout: 90_000 });
      await page.waitForSelector('.mx-page', { timeout: 90_000 });
    }
    await ctx.storageState({ path: statePath });
  } catch (e) {
    const shot = path.join(AUTH_DIR, `login-failure-${side}-${role}.png`);
    await page.screenshot({ path: shot }).catch(() => {});
    throw new Error(`Login failed for ${side}/${role} (screenshot: ${shot}): ${e}`);
  } finally {
    await ctx.close();
  }
  return statePath;
}
