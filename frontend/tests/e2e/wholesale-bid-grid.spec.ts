/**
 * Phase 13 Part 1 — Wholesale bid grid E2E suite.
 *
 * Covers the 11-column BidGrid shipped in Phase 6A + 6B. All API calls are
 * mocked — no live backend required.
 *
 * Column order: Product Id | Brand | Model | Model Name | Grade | Carrier |
 *               Added | Avail. Qty | Target Price | Price | Qty. Cap
 *
 * Scenarios:
 *  1. All 11 column headers render
 *  2. Sort arrow cycles asc → desc → clear on 3 clicks
 *  3. Text filter on Brand narrows visible rows
 *  4. Numeric filter on Target Price narrows visible rows (exact-match)
 *  5. Footer text reflects filtered subset: "Currently showing 1 of 2"
 *  6. Model Name column hidden at viewport ≤ 1099px
 *  7. Added column formats ISO timestamp as M/D/YYYY
 */

import { test, expect } from '@playwright/test';

// ---------------------------------------------------------------------------
// Shared constants
// ---------------------------------------------------------------------------

const BUYER_CODE_ID = 1;
const BID_ROUND_ID = 9001;

// ---------------------------------------------------------------------------
// Fixture — two rows with distinct Brand / TargetPrice / carrier / grade / added
// ---------------------------------------------------------------------------

function makeRow(
  id: number,
  overrides: Partial<{
    brand: string;
    model: string;
    modelName: string;
    carrier: string;
    mergedGrade: string;
    targetPrice: number;
    added: string;
  }>,
) {
  return {
    id,
    bidRoundId: BID_ROUND_ID,
    ecoid: `SKU-${id}`,
    brand: overrides.brand ?? 'Apple',
    model: overrides.model ?? 'iPhone 14',
    modelName: overrides.modelName ?? 'iPhone 14 Pro',
    carrier: overrides.carrier ?? 'AT&T',
    added: overrides.added ?? '2013-10-10T12:00:00Z',
    mergedGrade: overrides.mergedGrade ?? 'Good',
    buyerCodeType: 'Wholesale',
    bidQuantity: null,
    bidAmount: 0,
    targetPrice: overrides.targetPrice ?? 42.17,
    maximumQuantity: 120,
    payout: 0,
    submittedBidQuantity: null,
    submittedBidAmount: null,
    lastValidBidQuantity: null,
    lastValidBidAmount: null,
    submittedDatetime: null,
    changedDate: '2026-04-22T14:00:00Z',
  };
}

const ROW_APPLE = makeRow(555001, {
  brand: 'Apple',
  model: 'iPhone 14',
  modelName: 'iPhone 14 Pro',
  carrier: 'AT&T',
  mergedGrade: 'Good',
  targetPrice: 42.17,
  // Use noon UTC so the M/D/YYYY display is stable across all UTC-offset timezones.
  added: '2013-10-10T12:00:00Z',
});

const ROW_SAMSUNG = makeRow(555002, {
  brand: 'Samsung',
  model: 'Galaxy S23',
  modelName: 'Galaxy S23 Ultra',
  carrier: 'Verizon',
  mergedGrade: 'Fair',
  targetPrice: 30.00,
  added: '2019-09-12T00:00:00Z',
});

function makeGridResponse(rows = [ROW_APPLE, ROW_SAMSUNG]) {
  return {
    mode: 'GRID',
    auction: {
      id: 301,
      auctionId: 101,
      auctionTitle: 'Auction 2026 / Wk17',
      round: 1,
      roundName: 'Round 1',
      status: 'Started',
    },
    bidRound: {
      id: BID_ROUND_ID,
      schedulingAuctionId: 301,
      round: 1,
      roundStatus: 'Started',
      startDatetime: null,
      endDatetime: null,
      submitted: false,
      submittedDatetime: null,
    },
    rows,
    totals: {
      rowCount: rows.length,
      totalBidAmount: 0,
      totalPayout: 0,
      totalBidQuantity: 0,
    },
    timer: {
      now: '2026-04-22T14:00:00Z',
      startsAt: null,
      endsAt: null,
      secondsUntilStart: 0,
      secondsUntilEnd: 234000,
      active: true,
    },
  };
}

// ---------------------------------------------------------------------------
// Setup helpers
// ---------------------------------------------------------------------------

async function seedAuth(page: import('@playwright/test').Page) {
  // Set a fake auth_token cookie so the Next.js middleware (proxy.ts) does
  // NOT redirect to /login. The value only needs to be non-empty — the
  // middleware checks for presence, not signature validity, and backend
  // calls for buyer-codes are mocked so the JWT is never verified.
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
        userId: 999,
        firstName: 'Test',
        lastName: 'Bidder',
        fullName: 'Test Bidder',
        email: 'bidder@buyerco.com',
        initials: 'TB',
        roles: ['Bidder'],
      }),
    );
    localStorage.setItem(
      'activeBuyerCode',
      JSON.stringify({
        id: 1,
        code: 'BC001',
        buyerName: 'Test Buyer Co',
        buyerCodeType: 'Wholesale',
        codeType: 'AUCTION',
      }),
    );
  });
}

async function mockDashboard(
  page: import('@playwright/test').Page,
  rows = [ROW_APPLE, ROW_SAMSUNG],
) {
  await page.route('**/api/v1/bidder/dashboard**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(makeGridResponse(rows)),
    });
  });
}

async function mockBuyerCodes(page: import('@playwright/test').Page) {
  await page.route('**/api/v1/auth/buyer-codes**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          id: BUYER_CODE_ID,
          code: 'BC001',
          buyerName: 'Test Buyer Co',
          buyerCodeType: 'Wholesale',
          codeType: 'AUCTION',
        },
      ]),
    });
  });
}

/** Navigate to the dashboard and wait for the grid to appear. */
async function gotoGrid(page: import('@playwright/test').Page) {
  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);
  // Wait for at least one column header to confirm the grid rendered.
  await expect(page.getByRole('columnheader', { name: 'Product Id' })).toBeVisible({
    timeout: 15_000,
  });
}

// ---------------------------------------------------------------------------
// Scenario 1 — all 11 column headers render
// ---------------------------------------------------------------------------

test('bid grid renders all 11 column headers', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await gotoGrid(page);

  const expectedHeaders = [
    'Product Id',
    'Brand',
    'Model',
    'Model Name',
    'Grade',
    'Carrier',
    'Added',
    'Avail. Qty',
    'Target Price',
    'Price',
    'Qty. Cap',
  ];

  for (const header of expectedHeaders) {
    await expect(
      page.getByRole('columnheader', { name: new RegExp(header, 'i') }).first(),
    ).toBeVisible();
  }
});

// ---------------------------------------------------------------------------
// Scenario 2 — sort arrow cycles asc → desc → clear on 3 clicks
// ---------------------------------------------------------------------------

test('sort arrow cycles asc -> desc -> clear on Brand column', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await gotoGrid(page);

  const sortBtn = page.getByRole('button', { name: 'Sort by Brand' });
  const brandTh = page.getByRole('columnheader', { name: /Brand/ }).first();

  // Click 1 — should become ascending
  await sortBtn.click();
  await expect(brandTh).toHaveAttribute('aria-sort', 'ascending');

  // Click 2 — should become descending
  await sortBtn.click();
  await expect(brandTh).toHaveAttribute('aria-sort', 'descending');

  // Click 3 — should clear (none)
  await sortBtn.click();
  await expect(brandTh).toHaveAttribute('aria-sort', 'none');
});

// ---------------------------------------------------------------------------
// Scenario 3 — text filter on Brand narrows visible rows
// ---------------------------------------------------------------------------

test('text filter on Brand narrows to matching rows only', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await gotoGrid(page);

  // Both rows should be visible initially
  await expect(page.getByRole('cell', { name: /Apple/ }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: /Samsung/ }).first()).toBeVisible();

  // Type "Apple" into the Brand filter
  const brandFilter = page.getByRole('textbox', { name: 'Filter by brand' });
  await brandFilter.fill('Apple');

  // Only the Apple row should remain; Samsung should disappear from the table body
  await expect(page.getByRole('cell', { name: /Apple/ }).first()).toBeVisible();
  // Samsung data cell should not be visible (filtered out)
  await expect(page.getByRole('cell', { name: /Samsung/ })).not.toBeVisible();
});

// ---------------------------------------------------------------------------
// Scenario 4 — numeric filter on Target Price narrows rows (exact match)
// ---------------------------------------------------------------------------

test('numeric filter on Target Price filters to exact value', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await gotoGrid(page);

  // Both rows initially visible — find them via ecoid cells
  await expect(page.getByRole('cell', { name: /SKU-555001/ }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: /SKU-555002/ }).first()).toBeVisible();

  // Filter by target price 42.17 — only ROW_APPLE matches
  const targetPriceFilter = page.getByRole('spinbutton', { name: 'Filter by targetPrice' });
  await targetPriceFilter.fill('42.17');

  // ROW_APPLE ecoid cell should remain; ROW_SAMSUNG should disappear
  await expect(page.getByRole('cell', { name: /SKU-555001/ }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: /SKU-555002/ })).not.toBeVisible();
});

// ---------------------------------------------------------------------------
// Scenario 5 — footer updates to "Currently showing 1 of 2" after filter
// ---------------------------------------------------------------------------

test('footer updates to "Currently showing 1 of 2" when filter matches a subset', async ({
  page,
}) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await gotoGrid(page);

  // Unfiltered footer — should show "2 of 2"
  await expect(page.getByText(/Currently showing 2 of 2/)).toBeVisible();

  // Filter Brand to "Apple" — only 1 row matches
  const brandFilter = page.getByRole('textbox', { name: 'Filter by brand' });
  await brandFilter.fill('Apple');

  await expect(page.getByText(/Currently showing 1 of 2/)).toBeVisible();
});

// ---------------------------------------------------------------------------
// Scenario 6 — Model Name column hidden at ≤ 1099px viewport
// ---------------------------------------------------------------------------

test('Model Name column is hidden at viewport width ≤ 1099px', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  // Set narrow viewport BEFORE navigation so the CSS media query applies.
  await page.setViewportSize({ width: 1099, height: 800 });
  await gotoGrid(page);

  // The .colModelName CSS class sets display:none at max-width:1100px.
  // getByRole can't find display:none elements so we use a th[class*="colModelName"]
  // CSS selector to retrieve the element directly from the DOM and check its
  // computed display value.
  const modelNameTh = page.locator('th[class*="colModelName"]').first();

  // When CSS modules are active the class name is mangled; fall back to
  // checking whether the column header text is NOT visible.
  // The th element should exist in the DOM (not removed) but be display:none.
  const display = await modelNameTh.evaluate((el) => {
    return window.getComputedStyle(el as HTMLElement).display;
  });
  expect(display).toBe('none');
});

// ---------------------------------------------------------------------------
// Scenario 7 — Added column renders ISO timestamp as M/D/YYYY
// ---------------------------------------------------------------------------

test('Added column renders ISO timestamp as M/D/YYYY', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  // Use a single row with a known "added" date to assert the display format.
  // ROW_APPLE.added = '2013-10-10T00:00:00Z' → should show '10/10/2013'
  await mockDashboard(page, [ROW_APPLE]);
  await gotoGrid(page);

  // The formatted date cell should contain exactly the M/D/YYYY representation.
  await expect(page.getByRole('cell', { name: '10/10/2013' })).toBeVisible();
});
