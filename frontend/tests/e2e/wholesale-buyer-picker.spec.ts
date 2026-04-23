import { test, expect } from '@playwright/test';

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

// Helper: seed localStorage so the page doesn't redirect to /login
function setAuthUser(page: import('@playwright/test').Page) {
  return page.addInitScript(() => {
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

test('mixed-code user (bidder@buyerco.com) sees both sections', async ({ page }) => {
  // Log in to obtain a valid JWT cookie
  const loginRes = await page.request.post('http://localhost:8080/api/v1/auth/login', {
    data: { email: 'bidder@buyerco.com', password: 'Bidder123!', rememberMe: false },
  });

  if (!loginRes.ok()) {
    test.skip(true, 'Backend not running or bidder account unavailable — skip live test');
    return;
  }

  // Seed the auth_user in localStorage so the page doesn't redirect to /login
  // (the cookie-based auth will handle the API call)
  const body = await loginRes.json() as { user?: { userId: number; firstName: string; lastName: string } };
  const user = body.user;
  if (!user) {
    test.skip(true, 'Login response missing user field — skip live test');
    return;
  }

  await page.addInitScript((authUser: object) => {
    localStorage.setItem('auth_user', JSON.stringify(authUser));
  }, user);

  await page.goto('/buyer-select');

  // Both sections must be visible
  await expect(page.getByRole('region', { name: 'Premium Wholesale Devices' })).toBeVisible({ timeout: 15_000 });
  await expect(page.getByRole('region', { name: 'Weekly Wholesale Auction' })).toBeVisible({ timeout: 15_000 });
});

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
