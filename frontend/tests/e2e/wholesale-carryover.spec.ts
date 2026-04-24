/**
 * Phase 13 Part 1 — Carryover flow E2E suite.
 *
 * Covers the Phase 9 Carryover button + CarryoverResultModal. All API calls
 * are mocked via page.route() — no live backend required.
 *
 * Scenarios:
 *  1. Carryover with 0-copied response → empty-state modal renders correct copy
 *  2. Escape key closes the modal
 *  3. Carryover with success response (copied=3, prevWeek) → success copy renders
 */

import { test, expect } from '@playwright/test';

// ---------------------------------------------------------------------------
// Shared constants
// ---------------------------------------------------------------------------

const BUYER_CODE_ID = 1;
const BID_ROUND_ID = 9001;

// ---------------------------------------------------------------------------
// Fixture builders
// ---------------------------------------------------------------------------

const GRID_ROW = {
  id: 555001,
  bidRoundId: BID_ROUND_ID,
  ecoid: 'SKU-001',
  brand: 'Apple',
  model: 'iPhone 14',
  modelName: 'iPhone 14 Pro',
  carrier: 'AT&T',
  added: '2026-04-01T00:00:00Z',
  mergedGrade: 'Good',
  buyerCodeType: 'Wholesale',
  bidQuantity: null,
  bidAmount: 0,
  targetPrice: 42.17,
  maximumQuantity: 120,
  payout: 0,
  submittedBidQuantity: null,
  submittedBidAmount: null,
  lastValidBidQuantity: null,
  lastValidBidAmount: null,
  submittedDatetime: null,
  changedDate: '2026-04-22T14:00:00Z',
};

function makeGridResponse() {
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
    rows: [GRID_ROW],
    totals: {
      rowCount: 1,
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
  // Inject a fake auth_token cookie so the Next.js middleware (proxy.ts)
  // does NOT redirect to /login for protected routes.
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

async function setupCommonRoutes(page: import('@playwright/test').Page) {
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

  await page.route('**/api/v1/bidder/dashboard**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(makeGridResponse()),
    });
  });
}

/** Navigate to the dashboard and wait for the bid grid column header. */
async function gotoGrid(page: import('@playwright/test').Page) {
  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);
  await expect(page.getByRole('columnheader', { name: 'Product Id' })).toBeVisible({
    timeout: 15_000,
  });
}

// ---------------------------------------------------------------------------
// Scenario 1 — 0-copied response shows empty-state modal
// ---------------------------------------------------------------------------

test('carryover with 0 copied shows empty-state modal', async ({ page }) => {
  await seedAuth(page);
  await setupCommonRoutes(page);

  await page.route('**/api/v1/bidder/bid-rounds/*/carryover**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ copied: 0, notFound: 0, prevWeek: null }),
    });
  });

  await gotoGrid(page);

  // Click the Carryover button in the dashboard sub-header
  const carryoverBtn = page.getByRole('button', { name: /Carryover/i });
  await expect(carryoverBtn).toBeVisible();
  await carryoverBtn.click();

  // Empty-state modal should appear
  const modal = page.getByRole('dialog');
  await expect(modal).toBeVisible({ timeout: 10_000 });
  await expect(modal).toContainText("You don't have bids from last week to carry over.");
});

// ---------------------------------------------------------------------------
// Phase 13 Part 2 — pixel-compare against QA reference
// ---------------------------------------------------------------------------

// TODO(phase-13-pixel): Carryover empty-state modal vs qa-04-carryover-empty-modal.png.
// The QA reference shows the "no previous bids" empty state with the Mendix
// modal chrome.  Local rendering differences in the modal overlay, icon,
// and copy layout are expected to produce a meaningful diff until a dedicated
// pixel-match pass aligns the modal styling.
// Tracking issue: Phase 13 follow-up — carryover empty-state modal pixel parity.
test.fixme('carryover empty-state modal pixel-compare vs QA reference (qa-04)', async ({ page }) => {
  await seedAuth(page);
  await setupCommonRoutes(page);

  await page.route('**/api/v1/bidder/bid-rounds/*/carryover**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ copied: 0, notFound: 0, prevWeek: null }),
    });
  });

  await gotoGrid(page);

  await page.getByRole('button', { name: /Carryover/i }).click();
  await expect(page.getByRole('dialog')).toBeVisible({ timeout: 10_000 });

  await expect(page).toHaveScreenshot('qa-04-carryover-empty-modal.png', {
    maxDiffPixelRatio: 0.02,
  });
});

// ---------------------------------------------------------------------------
// Scenario 2 — Escape closes the modal
// ---------------------------------------------------------------------------

test('Escape key closes the carryover modal', async ({ page }) => {
  await seedAuth(page);
  await setupCommonRoutes(page);

  await page.route('**/api/v1/bidder/bid-rounds/*/carryover**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ copied: 0, notFound: 0, prevWeek: null }),
    });
  });

  await gotoGrid(page);

  // Open the modal
  await page.getByRole('button', { name: /Carryover/i }).click();
  await expect(page.getByRole('dialog')).toBeVisible({ timeout: 10_000 });

  // Press Escape — modal should close
  await page.keyboard.press('Escape');
  await expect(page.getByRole('dialog')).not.toBeVisible();
});

// ---------------------------------------------------------------------------
// Scenario 3 — success response shows count + week
// ---------------------------------------------------------------------------

test('carryover success shows copied count and prevWeek label', async ({ page }) => {
  await seedAuth(page);
  await setupCommonRoutes(page);

  await page.route('**/api/v1/bidder/bid-rounds/*/carryover**', (route) => {
    void route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ copied: 3, notFound: 0, prevWeek: '2026 / Wk15' }),
    });
  });

  await gotoGrid(page);

  await page.getByRole('button', { name: /Carryover/i }).click();

  const modal = page.getByRole('dialog');
  await expect(modal).toBeVisible({ timeout: 10_000 });

  // CarryoverResultModal success text: "Carried over 3 bids from Week 2026 / Wk15."
  await expect(modal).toContainText('Carried over 3 bids');
  await expect(modal).toContainText('2026 / Wk15');
});
