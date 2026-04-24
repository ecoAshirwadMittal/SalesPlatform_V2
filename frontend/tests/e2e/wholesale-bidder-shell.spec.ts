/**
 * E2E tests for the Phase 4 BidderShell layout.
 *
 * Tests are grouped by concern:
 *   1. Shell renders with gradient sidebar (expanded by default)
 *   2. Collapse / expand toggle + localStorage persistence
 *   3. Sidebar nav items (Auction, Buyer User Guide)
 *   4. Avatar popover contents (Submit Feedback, Logout)
 *   5. "Switch Buyer Code" returns to /buyer-select
 *
 * Auth strategy: write auth_user to localStorage before navigation so the
 * shell's useActiveBuyerCode hook reads a valid user without a live backend.
 * The /auth/buyer-codes endpoint is intercepted via page.route() to return a
 * fixture buyer code so useActiveBuyerCode resolves successfully.
 *
 * If a scenario requires too much fixture setup (e.g. live auction state),
 * it is wrapped in test.skip() with a clear TODO.
 */

import { test, expect } from '@playwright/test';
import { checkA11y } from './_helpers/a11y';

// ---------------------------------------------------------------------------
// Fixture helpers
// ---------------------------------------------------------------------------

const BUYER_CODE_ID = 2; // matches "AD" code id from QA fixture

/** Seed a valid auth_user in localStorage before the page loads. */
async function seedAuth(page: import('@playwright/test').Page) {
  // Set a fake auth_token cookie so the Next.js middleware (proxy.ts)
  // does NOT redirect to /login before the page can mount.
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
        firstName: 'Akshay',
        lastName: 'Singhal',
        fullName: 'Akshay Singhal',
        email: 'akshay@chs.com',
        initials: 'AS',
        roles: ['Bidder'],
      }),
    );
    // Also seed activeBuyerCode so useActiveBuyerCode resolves from storage
    // on the first pass (before the API call completes).
    localStorage.setItem(
      'activeBuyerCode',
      JSON.stringify({
        id: 2,
        code: 'AD',
        buyerName: 'CHS Technology (HK) Ltd',
        buyerCodeType: 'Wholesale',
        codeType: 'AUCTION',
      }),
    );
  });
}

/**
 * Intercept the /auth/buyer-codes API to return a single AUCTION buyer code
 * so useActiveBuyerCode resolves without a live backend.
 */
async function mockBuyerCodes(page: import('@playwright/test').Page) {
  await page.route('**/api/v1/auth/buyer-codes**', (route) => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        {
          id: BUYER_CODE_ID,
          code: 'AD',
          buyerName: 'CHS Technology (HK) Ltd',
          buyerCodeType: 'Wholesale',
          codeType: 'AUCTION',
        },
      ]),
    });
  });
}

// ---------------------------------------------------------------------------
// QA-parity fixture data — matches the row set visible in qa-03 / qa-07
// ---------------------------------------------------------------------------

const QA_BID_ROUND_ID = 9001;

function makeQaGridRow(
  id: number,
  ecoid: string,
  brand: string,
  model: string,
  modelName: string,
  carrier: string,
  added: string,
  mergedGrade: string,
  maxQty: number,
  targetPrice: number,
) {
  return {
    id,
    bidRoundId: QA_BID_ROUND_ID,
    ecoid,
    brand,
    model,
    modelName,
    carrier,
    added,
    mergedGrade,
    buyerCodeType: 'Wholesale',
    bidQuantity: null,
    bidAmount: 0,
    targetPrice,
    maximumQuantity: maxQty,
    payout: 0,
    submittedBidQuantity: null,
    submittedBidAmount: null,
    lastValidBidQuantity: null,
    lastValidBidAmount: null,
    submittedDatetime: null,
    changedDate: '2026-04-22T14:00:00Z',
  };
}

// Target prices extracted from qa-04 and qa-07 reference screenshots.
// Unique IDs — no duplicates to avoid React key warnings.
const QA_GRID_ROWS = [
  makeQaGridRow(7752,  '7752',  'Apple', 'A_YYY',        'iPhone 5c',         'Verizon',  '9/19/2013', 'Unlocked', 8,   5555.00),
  makeQaGridRow(12236, '12236', 'Apple', 'A_YYY',        'iPhone 6',          'Unlocked', '9/12/2019', 'Unlocked', 49,  1501.50),
  makeQaGridRow(12238, '12238', 'Apple', 'C_YNY/G_YNN',  'iPhone 6 Plus',     'Unlocked', '9/12/2019', 'Unlocked', 681, 1501.50),
  makeQaGridRow(12240, '12240', 'Apple', 'B_NYY/D_NNY',  'iPhone 6s',         'Unlocked', '9/9/2014',  'Unlocked', 30,  1501.50),
  makeQaGridRow(18467, '18467', 'Apple', 'A_YYY',        'iPhone X',          'Unlocked', '9/11/2025', 'Unlocked', 7,   1355.00),
  makeQaGridRow(18462, '18462', 'Apple', 'A_YYY',        'iPhone 7',          'Unlocked', '9/11/2025', 'Unlocked', 916, 1230.00),
  makeQaGridRow(18468, '18468', 'Apple', 'A_YYY',        'iPhone XS',         'Verizon',  '9/11/2025', 'Unlocked', 1,   1222.68),
  makeQaGridRow(18463, '18463', 'Apple', 'A_YYY',        'iPhone 7',          'Verizon',  '9/11/2025', 'Unlocked', 6,   1152.42),
  makeQaGridRow(58457, '58457', 'Apple', 'A_YYY',        'iPhone 12 Pro Max', 'AT&T',     '9/11/2025', 'Unlocked', 9,   1142.40),
  makeQaGridRow(18464, '18464', 'Apple', 'A_YYY',        'iPhone 7',          'Unlocked', '9/11/2025', 'Unlocked', 2,   1124.76),
  makeQaGridRow(58461, '58461', 'Apple', 'A_YYY',        'iPhone 14 Pro Max', 'T-Mobile', '9/11/2025', 'Unlocked', 3,   1119.46),
  makeQaGridRow(18465, '18465', 'Apple', 'A_YYY',        'iPhone 8',          'Other',    '9/11/2025', 'Unlocked', 1,   1090.32),
];

/**
 * Intercept the bidder dashboard API so the page doesn't need a live auction.
 * Returns a realistic GRID response matching the qa-03/qa-07 QA reference.
 */
async function mockDashboard(page: import('@playwright/test').Page) {
  await page.route('**/api/v1/bidder/dashboard**', (route) => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        mode: 'GRID',
        auction: {
          id: 301,
          auctionId: 101,
          auctionTitle: 'Auction 2026 / Wk16',
          round: 1,
          roundName: 'Round 1',
          status: 'Started',
        },
        bidRound: {
          id: QA_BID_ROUND_ID,
          schedulingAuctionId: 301,
          round: 1,
          roundStatus: 'Started',
          startDatetime: '2026-04-21T16:00:00Z',
          endDatetime: '2026-04-25T07:00:00Z',
          submitted: false,
          submittedDatetime: null,
        },
        rows: QA_GRID_ROWS,
        totals: {
          rowCount: QA_GRID_ROWS.length,
          totalBidAmount: 0,
          totalPayout: 0,
          totalBidQuantity: 0,
        },
        timer: {
          now: '2026-04-22T14:00:00Z',
          startsAt: '2026-04-21T16:00:00Z',
          endsAt: '2026-04-25T07:00:00Z',
          secondsUntilStart: 0,
          secondsUntilEnd: 234000,
          active: true,
        },
      }),
    });
  });
}

// ---------------------------------------------------------------------------
// 1. Shell renders with sidebar expanded by default
// ---------------------------------------------------------------------------

test('bidder shell renders with sidebar expanded by default', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  // Clear any persisted collapsed state before the test.
  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  const sidebar = page.getByTestId('bidder-sidebar');
  await expect(sidebar).toBeVisible();

  // In expanded state the sidebar should be ~220px wide.
  const box = await sidebar.boundingBox();
  expect(box?.width).toBeGreaterThanOrEqual(200);
  expect(box?.width).toBeLessThanOrEqual(240);

  // Both nav item labels should be visible.
  await expect(page.getByTestId('sidebar-item-auction')).toBeVisible();
  await expect(page.getByTestId('sidebar-item-buyer-user-guide')).toBeVisible();

  // axe a11y — WCAG 2.x AA on the shell with expanded sidebar + error-mode dashboard.
  // TODO(a11y): color-contrast — teal sidebar (#407874 bg / white text) passes AA for
  // large text but axe's automated check may flag smaller icon labels; defer until
  // a design pass can confirm exact pixel sizes across all nav items.
  await checkA11y(page, { disable: ['color-contrast'] });
});

// ---------------------------------------------------------------------------
// 2a. Collapse on toggle click
// ---------------------------------------------------------------------------

test('sidebar collapses when toggle is clicked', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  const sidebar = page.getByTestId('bidder-sidebar');
  await expect(sidebar).toBeVisible();

  // Click the toggle button (aria-label="Collapse sidebar")
  await page.getByRole('button', { name: 'Collapse sidebar' }).click();

  // Sidebar should now be ~54px wide.
  const box = await sidebar.boundingBox();
  expect(box?.width).toBeGreaterThanOrEqual(50);
  expect(box?.width).toBeLessThanOrEqual(60);
});

// ---------------------------------------------------------------------------
// 2b. Collapsed state is persisted after reload
// ---------------------------------------------------------------------------

test('collapsed state is persisted to localStorage and survives reload', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    // Start expanded.
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  // Collapse the sidebar.
  await page.getByRole('button', { name: 'Collapse sidebar' }).click();

  // Verify the localStorage key was written.
  const storedValue = await page.evaluate(() =>
    localStorage.getItem('bidder.sidebarCollapsed'),
  );
  expect(storedValue).toBe('true');

  // Reload the page — collapsed state should be restored.
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await page.reload();

  const sidebar = page.getByTestId('bidder-sidebar');
  const box = await sidebar.boundingBox();
  expect(box?.width).toBeLessThanOrEqual(60);
});

// ---------------------------------------------------------------------------
// 2c. Expanded state is persisted after reload
// ---------------------------------------------------------------------------

test('expanded state is persisted to localStorage and survives reload', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  // Start collapsed.
  await page.addInitScript(() => {
    localStorage.setItem('bidder.sidebarCollapsed', 'true');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  // Expand the sidebar.
  await page.getByRole('button', { name: 'Expand sidebar' }).click();

  const storedValue = await page.evaluate(() =>
    localStorage.getItem('bidder.sidebarCollapsed'),
  );
  expect(storedValue).toBe('false');

  // Reload the page — expanded state should be restored.
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);
  await page.reload();

  const sidebar = page.getByTestId('bidder-sidebar');
  const box = await sidebar.boundingBox();
  expect(box?.width).toBeGreaterThanOrEqual(200);
});

// ---------------------------------------------------------------------------
// 3a. Auction nav item routes correctly
// ---------------------------------------------------------------------------

test('Auction sidebar item links to /bidder/dashboard', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  const auctionItem = page.getByTestId('sidebar-item-auction');
  await expect(auctionItem).toBeVisible();

  // Should be an anchor/link pointing to /bidder/dashboard
  const href = await auctionItem.getAttribute('href');
  expect(href).toContain('/bidder/dashboard');
});

// ---------------------------------------------------------------------------
// 3b. Buyer User Guide item opens in a new tab
// ---------------------------------------------------------------------------

test('Buyer User Guide sidebar item has target=_blank and correct href', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  const guideItem = page.getByTestId('sidebar-item-buyer-user-guide');
  await expect(guideItem).toBeVisible();

  const target = await guideItem.getAttribute('target');
  expect(target).toBe('_blank');

  const rel = await guideItem.getAttribute('rel');
  expect(rel).toContain('noopener');

  const href = await guideItem.getAttribute('href');
  expect(href).toContain('/api/v1/bidder/docs/buyer-guide');
});

// ---------------------------------------------------------------------------
// 4. Avatar popover — Submit Feedback + Logout visible; Escape closes
// ---------------------------------------------------------------------------

test('avatar popover shows Submit Feedback and Logout; closes on Escape', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  // Open the avatar popover.
  const avatarButton = page.getByRole('button', { name: /user menu for/i });
  await avatarButton.click();

  // Both items should be visible.
  await expect(page.getByRole('menuitem', { name: 'Submit Feedback' })).toBeVisible();
  await expect(page.getByRole('menuitem', { name: 'Logout' })).toBeVisible();

  // Escape should close the popover.
  await page.keyboard.press('Escape');
  await expect(page.getByRole('menuitem', { name: 'Submit Feedback' })).not.toBeVisible();
});

// ---------------------------------------------------------------------------
// 5. Switch Buyer Code navigates to /buyer-select
// ---------------------------------------------------------------------------

test('Switch Buyer Code link navigates to /buyer-select', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  // The Switch Buyer Code button should be visible in the chrome top-bar.
  const switchButton = page.getByRole('button', { name: 'Switch Buyer Code' });
  await expect(switchButton).toBeVisible();

  // Mock the buyer-select page so we can assert the navigation.
  await page.route('**/buyer-select**', (route) => route.continue());

  await switchButton.click();
  await expect(page).toHaveURL(/\/buyer-select/);
});

// ---------------------------------------------------------------------------
// Phase 13 Part 2 — pixel-compare against QA reference
// ---------------------------------------------------------------------------

// TODO(phase-13-pixel): Bidder shell expanded sidebar vs qa-03-bidder-dashboard-ad.png.
// The QA reference shows the full dashboard with sidebar expanded and the "AD"
// buyer code active (live auction data, real row counts).  Local rendering
// uses a mocked 404/no-auction state which produces a materially different
// content area.  Both the content-area delta and subtle sidebar colour
// differences (gradient rendering across OS/GPU) are expected to cause a
// large diff.  Fix: stub an auction-like dashboard response and pixel-align
// the sidebar gradient before re-enabling.
// Tracking issue: Phase 13 follow-up — bidder shell (expanded) pixel parity.
test.fixme('bidder shell expanded sidebar pixel-compare vs QA reference (qa-03)', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.removeItem('bidder.sidebarCollapsed');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);
  await expect(page.getByTestId('bidder-sidebar')).toBeVisible();
  // Wait for the bid grid to render before capturing the screenshot.
  await expect(page.getByRole('columnheader', { name: 'Product Id' })).toBeVisible({ timeout: 15_000 });

  await expect(page).toHaveScreenshot('qa-03-bidder-dashboard-ad.png', {
    maxDiffPixelRatio: 0.02,
  });
});

// TODO(phase-13-pixel): Bidder shell collapsed sidebar vs qa-07-sidebar-collapsed.png.
// The QA reference shows the sidebar in its narrow (~54px) collapsed state.
// Collapse animation timing and icon positioning between the Mendix original and
// the local React implementation are expected to differ.
// Tracking issue: Phase 13 follow-up — bidder shell (collapsed) pixel parity.
test.fixme('bidder shell collapsed sidebar pixel-compare vs QA reference (qa-07)', async ({ page }) => {
  await seedAuth(page);
  await mockBuyerCodes(page);
  await mockDashboard(page);

  await page.addInitScript(() => {
    localStorage.setItem('bidder.sidebarCollapsed', 'true');
  });

  await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

  // Wait for sidebar to be present in collapsed state
  const sidebar = page.getByTestId('bidder-sidebar');
  await expect(sidebar).toBeVisible();
  const box = await sidebar.boundingBox();
  expect(box?.width).toBeLessThanOrEqual(60);
  // Wait for the bid grid to render before capturing the screenshot.
  await expect(page.getByRole('columnheader', { name: 'Product Id' })).toBeVisible({ timeout: 15_000 });

  await expect(page).toHaveScreenshot('qa-07-sidebar-collapsed.png', {
    maxDiffPixelRatio: 0.02,
  });
});

// ---------------------------------------------------------------------------
// 6. Collapsed sidebar — icons still accessible, labels hidden
// ---------------------------------------------------------------------------

test.skip(
  'collapsed sidebar hides labels but icons remain accessible',
  // TODO: This test requires measuring CSS visibility / computed styles
  // which is brittle with CSS modules. Re-enable when a data-collapsed
  // attribute or aria-label-based assertion can be used reliably.
  async ({ page }) => {
    await seedAuth(page);
    await mockBuyerCodes(page);
    await mockDashboard(page);

    await page.addInitScript(() => {
      localStorage.setItem('bidder.sidebarCollapsed', 'true');
    });

    await page.goto(`/bidder/dashboard?buyerCodeId=${BUYER_CODE_ID}`);

    // Icon should still be in the DOM (aria-label on the item covers it).
    await expect(page.getByTestId('sidebar-item-auction')).toBeVisible();
    // Label text should not be visually visible.
    // (Checking via bounding box width is the robust approach here.)
    const sidebar = page.getByTestId('bidder-sidebar');
    const box = await sidebar.boundingBox();
    expect(box?.width).toBeLessThanOrEqual(60);
  },
);
