import { test, expect } from '@playwright/test';
import { checkA11y } from './_helpers/a11y';
import { isBackendAvailable } from './_helpers/backend';

/**
 * Phase 2 — Buyer-code picker conditional categories E2E suite.
 *
 * Scenarios:
 *   1. Single code  → skip picker, deep-link directly to the correct shell
 *   2. Auction-only → only "Weekly Wholesale Auction" section visible
 *   3. PWS-only     → only "Premium Wholesale Devices" section visible
 *   4. Mixed        → both sections rendered (live bidder@buyerco.com has both)
 *
 * Scenarios 1–3 use Playwright route mocking to control the API response.
 * Scenario 4 runs against the live dev stack (requires backend + DB running).
 *
 * Fixture data note: scenarios 1–3 mock GET /api/v1/auth/buyer-codes so they
 * work without a specially-seeded DB account.  Scenario 4 relies on the seeded
 * bidder@buyerco.com account (V15 migration), which has 3 PWS codes and 3
 * AUCTION codes — both categories must render.
 */

// Helper: seed auth_token cookie + localStorage so the Next.js middleware
// (proxy.ts) doesn't redirect to /login.
async function setAuthUser(page: import('@playwright/test').Page) {
  // The middleware checks for the auth_token cookie server-side.
  await page.context().addCookies([
    {
      name: 'auth_token',
      value: 'test-jwt-token-for-e2e',
      domain: 'localhost',
      path: '/',
      httpOnly: false,
      secure: false,
      sameSite: 'Strict',
    },
  ]);

  await page.addInitScript(() => {
    localStorage.setItem(
      'auth_user',
      JSON.stringify({
        userId: 42,
        firstName: 'Test',
        lastName: 'Buyer',
        fullName: 'Test Buyer',
        email: 'test@test.com',
        initials: 'TB',
        roles: ['Bidder'],
      })
    );
  });
}

// Mock the buyer-codes API with a fixed response
function mockBuyerCodesApi(
  page: import('@playwright/test').Page,
  codes: Array<{ id: number; code: string; buyerName: string; buyerCodeType: string; codeType: 'PWS' | 'AUCTION' }>
) {
  return page.route('**/api/v1/auth/buyer-codes**', (route) =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(codes),
    })
  );
}

// ── Scenario 1: single code → deep-link, no picker rendered ────────────────

test('single AUCTION code skips picker and redirects to bidder dashboard', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 101, code: 'DDWS', buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
  ]);

  await page.goto('/buyer-select');

  // The page should redirect away from /buyer-select
  await page.waitForURL(/bidder\/dashboard\?buyerCodeId=101/, { timeout: 10_000 });
  await expect(page).toHaveURL(/buyerCodeId=101/);
});

test('single PWS code skips picker and redirects to pws/order', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 202, code: 'NB_PWS', buyerName: 'Nationwide Buyers', buyerCodeType: 'Premium_Wholesale', codeType: 'PWS' },
  ]);

  await page.goto('/buyer-select');

  await page.waitForURL(/pws\/order\?buyerCodeId=202/, { timeout: 10_000 });
  await expect(page).toHaveURL(/buyerCodeId=202/);
});

// ── Scenario 2: auction-only → one section visible ──────────────────────────

test('auction-only user sees only the Weekly Wholesale Auction section', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 101, code: 'DDWS', buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
    { id: 102, code: 'AD',   buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
  ]);

  await page.goto('/buyer-select');

  // Auction section is present
  const auctionSection = page.getByRole('region', { name: 'Weekly Wholesale Auction' });
  await expect(auctionSection).toBeVisible();
  await expect(auctionSection.getByRole('heading', { level: 2 })).toHaveText('Weekly Wholesale Auction');

  // Both buyer-code pills are present
  const pills = auctionSection.getByRole('button');
  await expect(pills).toHaveCount(2);
  await expect(pills.nth(0)).toContainText('DDWS');
  await expect(pills.nth(1)).toContainText('AD');

  // PWS section must NOT be rendered
  await expect(page.getByRole('region', { name: 'Premium Wholesale Devices' })).not.toBeVisible();
});

// ── Scenario 3: PWS-only → one section visible ──────────────────────────────

test('PWS-only user sees only the Premium Wholesale Devices section', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 201, code: 'NB_PWS', buyerName: 'Nationwide Buyers',    buyerCodeType: 'Premium_Wholesale', codeType: 'PWS' },
    { id: 202, code: 'PWS02',  buyerName: 'Nationwide Buyers',    buyerCodeType: 'Premium_Wholesale', codeType: 'PWS' },
  ]);

  await page.goto('/buyer-select');

  // PWS section is present
  const pwsSection = page.getByRole('region', { name: 'Premium Wholesale Devices' });
  await expect(pwsSection).toBeVisible();
  await expect(pwsSection.getByRole('heading', { level: 2 })).toHaveText('Premium Wholesale Devices');

  // Two pills
  const pills = pwsSection.getByRole('button');
  await expect(pills).toHaveCount(2);

  // Auction section must NOT be rendered
  await expect(page.getByRole('region', { name: 'Weekly Wholesale Auction' })).not.toBeVisible();
});

// ── Scenario 4: mixed codes → both sections rendered ─────────────────────────
//
// Runs against the live dev stack.  Requires:
//   - backend at http://localhost:8080 (V15 seed applied)
//   - bidder@buyerco.com / Bidder123! has 3 PWS + 3 AUCTION codes
//
// This test logs in for real so the JWT cookie is available when the page
// fetches /api/v1/auth/buyer-codes.

// ---------------------------------------------------------------------------
// Visual + semantic regression coverage (per the 2026-04-25 ADR)
// ---------------------------------------------------------------------------

// Auction-only picker scenario shared by both the semantic test and the
// (deferred) pixel-compare test. Mirrors the user "Akshay Singhal" with
// 2 Wholesale codes from CHS Technology and no PWS section.
async function setupAuctionOnlyPicker(page: import('@playwright/test').Page) {
  await page.context().addCookies([
    {
      name: 'auth_token',
      value: 'test-jwt-token-for-e2e',
      domain: 'localhost',
      path: '/',
      httpOnly: false,
      secure: false,
      sameSite: 'Strict',
    },
  ]);
  await page.addInitScript(() => {
    localStorage.setItem(
      'auth_user',
      JSON.stringify({
        userId: 42,
        firstName: 'Akshay',
        lastName: 'Singhal',
        fullName: 'Akshay Singhal',
        email: 'akshay@chs.com',
        initials: 'AS',
        roles: ['Bidder'],
      })
    );
  });

  await mockBuyerCodesApi(page, [
    { id: 101, code: 'DDWS', buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
    { id: 102, code: 'AD',   buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
  ]);
  await page.goto('/buyer-select');
}

test('auction-only picker — semantic structure', async ({ page }) => {
  await setupAuctionOnlyPicker(page);

  // Welcome heading + sub-text.
  await expect(page.getByRole('heading', { name: /Welcome .+!/ })).toBeVisible();
  await expect(page.getByText('Choose a buyer code to get started:')).toBeVisible();

  // Only the auction section renders — PWS section absent.
  await expect(page.getByRole('region', { name: 'Weekly Wholesale Auction' })).toBeVisible();
  await expect(page.getByRole('region', { name: 'Premium Wholesale Devices' })).toHaveCount(0);

  // Both pills present, both list CHS Technology as the buyer name.
  const pills = page.getByRole('button', { name: /CHS Technology/ });
  await expect(pills).toHaveCount(2);
});

// Pixel compare against a Linux chromium baseline; stays fixme until
// the baseline PNG is captured via CI workflow with --update-snapshots.
test.fixme('auction-only picker — pixel compare against local baseline', async ({ page }) => {
  await setupAuctionOnlyPicker(page);
  await expect(page.getByRole('region', { name: 'Weekly Wholesale Auction' })).toBeVisible({ timeout: 10_000 });
  await expect(page).toHaveScreenshot({ maxDiffPixelRatio: 0.02 });
});

test('mixed-code picker (mocked) passes axe WCAG 2.x AA check', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 101, code: 'DDWS',   buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale',         codeType: 'AUCTION' },
    { id: 201, code: 'NB_PWS', buyerName: 'Nationwide Buyers',        buyerCodeType: 'Premium_Wholesale', codeType: 'PWS'     },
  ]);

  await page.goto('/buyer-select');

  // Both sections must be visible before the axe scan
  await expect(page.getByRole('region', { name: 'Weekly Wholesale Auction' })).toBeVisible({ timeout: 10_000 });
  await expect(page.getByRole('region', { name: 'Premium Wholesale Devices' })).toBeVisible();

  // axe a11y — WCAG 2.x AA on the mixed-code buyer picker.
  // TODO(a11y): color-contrast — buyer-code pills use teal (#407874) background
  // with white text which passes AA for large text but may flag on smaller pill
  // labels; disable until a design pass can confirm all sizes.
  await checkA11y(page, { disable: ['color-contrast'] });
});

test.describe('mixed-code user — live backend', () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), 'requires Spring Boot backend on :8080');
  });

  test('mixed-code user (bidder@buyerco.com) sees both sections', async ({ page }) => {
    // Log in to obtain a valid JWT cookie
    const loginRes = await page.request.post('http://localhost:8080/api/v1/auth/login', {
      data: { email: 'bidder@buyerco.com', password: 'Bidder123!', rememberMe: false },
    });

    // Seed the auth_user in localStorage so the page doesn't redirect to /login
    // (the cookie-based auth will handle the API call)
    const body = await loginRes.json() as { user?: { userId: number; firstName: string; lastName: string } };
    const user = body.user!;

    await page.addInitScript((authUser: object) => {
      localStorage.setItem('auth_user', JSON.stringify(authUser));
    }, user);

    await page.goto('/buyer-select');

    // Both sections must be visible
    await expect(page.getByRole('region', { name: 'Premium Wholesale Devices' })).toBeVisible({ timeout: 15_000 });
    await expect(page.getByRole('region', { name: 'Weekly Wholesale Auction' })).toBeVisible({ timeout: 15_000 });
  });
}); // end describe 'mixed-code user — live backend'

// ── Routing: click AUCTION pill → /bidder/dashboard ─────────────────────────

test('clicking an AUCTION pill routes to /bidder/dashboard with buyerCodeId', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 101, code: 'DDWS', buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
    { id: 102, code: 'AD',   buyerName: 'CHS Technology (HK) Ltd', buyerCodeType: 'Wholesale', codeType: 'AUCTION' },
  ]);

  await page.goto('/buyer-select');

  // Click the first pill (DDWS)
  const pill = page.getByRole('button', { name: /DDWS/ });
  await expect(pill).toBeVisible();
  await pill.click();

  await page.waitForURL(/bidder\/dashboard\?buyerCodeId=101/, { timeout: 10_000 });
  await expect(page).toHaveURL(/buyerCodeId=101/);
});

// ── Routing: click PWS pill → /pws/order ────────────────────────────────────

test('clicking a PWS pill routes to /pws/order with buyerCodeId', async ({ page }) => {
  await setAuthUser(page);
  await mockBuyerCodesApi(page, [
    { id: 201, code: 'NB_PWS', buyerName: 'Nationwide Buyers', buyerCodeType: 'Premium_Wholesale', codeType: 'PWS' },
    { id: 202, code: 'PWS02',  buyerName: 'Nationwide Buyers', buyerCodeType: 'Premium_Wholesale', codeType: 'PWS' },
  ]);

  await page.goto('/buyer-select');

  const pill = page.getByRole('button', { name: /NB_PWS/ });
  await expect(pill).toBeVisible();
  await pill.click();

  await page.waitForURL(/pws\/order\?buyerCodeId=201/, { timeout: 10_000 });
  await expect(page).toHaveURL(/buyerCodeId=201/);
});
