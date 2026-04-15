import { test as base, expect, Page } from '@playwright/test';
import { users, type TestUser, type UserKey } from './test-users';

/**
 * Login helper — authenticates via the modern app's JWT login form.
 * Sets localStorage & cookie exactly as LoginForm.tsx does.
 */
export async function login(page: Page, user: TestUser): Promise<void> {
  await page.goto('/login');
  await page.locator('#email').fill(user.email);
  await page.locator('#password').fill(user.password);
  await page.locator('button[type="submit"]').click();

  // Wait for redirect away from login page
  await page.waitForURL((url) => !url.pathname.includes('/login'), { timeout: 15_000 });
}

export async function loginAs(page: Page, key: UserKey): Promise<void> {
  const user = users[key];
  if (!user) throw new Error(`Unknown user key: ${key}`);
  await login(page, user);
}

export async function logout(page: Page): Promise<void> {
  // Click user icon in PWS top bar then "Logout"
  await page.locator('button[aria-label="User menu"]').click();
  await page.getByRole('button', { name: 'Logout' }).click();
  await page.waitForURL('**/login', { timeout: 10_000 });
}

/**
 * Navigate to a PWS sidebar page by clicking the nav label.
 */
export async function navigateTo(
  page: Page,
  label: 'Inventory' | 'Offer Review' | 'RMA Review' | 'Pricing' | 'Shop' | 'Counters' | 'Orders' | 'RMAs' | "FAQ's" | 'Grading',
): Promise<void> {
  const navLink = page.locator(`nav a, aside a`).filter({ hasText: label }).first();
  // fallback: locate by the navLabel span text
  const fallback = page.locator('a').filter({ has: page.locator(`span:text-is("${label}")`) }).first();

  const target = (await navLink.count()) > 0 ? navLink : fallback;
  await target.click();
  await page.waitForLoadState('domcontentloaded');
}

/**
 * Select a buyer code from the "View As" dropdown in the PWS top bar.
 */
export async function selectBuyerCode(page: Page, code: string): Promise<void> {
  const input = page.locator('input[aria-label="Search or select buyer code"]');
  await input.click();
  await input.fill(code);
  await page.getByRole('button', { name: code }).first().click();
  await page.waitForLoadState('domcontentloaded');
}

/**
 * Extended test fixture that pre-provides login helpers.
 */
export const test = base.extend<{
  loginAs: (key: UserKey) => Promise<void>;
  logout: () => Promise<void>;
  navigateTo: (label: string) => Promise<void>;
}>({
  loginAs: async ({ page }, use) => {
    await use(async (key: UserKey) => loginAs(page, key));
  },
  logout: async ({ page }, use) => {
    await use(async () => logout(page));
  },
  navigateTo: async ({ page }, use) => {
    await use(async (label: string) => navigateTo(page, label as any));
  },
});

export { expect };
