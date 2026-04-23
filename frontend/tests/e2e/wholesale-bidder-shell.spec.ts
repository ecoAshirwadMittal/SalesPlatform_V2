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

// ---------------------------------------------------------------------------
// Fixture helpers
// ---------------------------------------------------------------------------

const BUYER_CODE_ID = 1;

/** Seed a valid auth_user in localStorage before the page loads. */
async function seedAuth(page: import('@playwright/test').Page) {
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
    // Also seed activeBuyerCode so useActiveBuyerCode resolves from storage
    // on the first pass (before the API call completes).
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
          code: 'BC001',
          buyerName: 'Test Buyer Co',
          buyerCodeType: 'Wholesale',
          codeType: 'AUCTION',
        },
      ]),
    });
  });
}

/**
 * Intercept the bidder dashboard API so the page doesn't need a live auction.
 */
async function mockDashboard(page: import('@playwright/test').Page) {
  await page.route('**/api/v1/bidder/dashboard**', (route) => {
    route.fulfill({
      status: 404,
      contentType: 'application/json',
      body: JSON.stringify({ mode: 'ERROR_AUCTION_NOT_FOUND' }),
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
