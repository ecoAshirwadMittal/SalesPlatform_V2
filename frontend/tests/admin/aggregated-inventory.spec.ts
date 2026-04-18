import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';

/**
 * Task 8.3 — Aggregated Inventory E2E.
 *
 * Asserts the page skeleton, KPI strip, grid headers, pagination readout, and
 * a simple filter round-trip. Follows the existing LoginPage/test-users
 * fixture convention already used by other specs in this repo.
 */
test.describe('Aggregated Inventory', () => {
  test.beforeEach(async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAs('ADMIN');
    await page.goto('/admin/auctions-data-center/inventory');
  });

  test('shows KPI strip, grid, and paginates', async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'Inventory' })).toBeVisible();
    await expect(page.getByText('Total Quantity').first()).toBeVisible();
    await expect(page.getByText('DW Average Target Price')).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Product ID/ })).toBeVisible();
    await expect(page.getByText(/Currently showing \d+ to \d+ of/)).toBeVisible();
  });

  test('filters by grade (contains)', async ({ page }) => {
    await page.getByPlaceholder('Ab').first().fill('A_YYY');
    await expect(page.getByText(/Currently showing/)).toBeVisible({ timeout: 10_000 });
  });
});

/**
 * Phase 8 — Snowflake sync trigger + banner.
 *
 * Mocks only the two new sync endpoints (POST trigger + GET status) so the
 * live backend continues to serve the grid/KPI requests. The mocked GET
 * returns PENDING on the first call and COMPLETED on the second, so the
 * banner appears briefly then clears after the 3s poll tick — matching the
 * real backend's async completion path without depending on Snowflake.
 */
test.describe('Aggregated Inventory — Snowflake sync banner', () => {
  test('shows syncing banner while backend status is PENDING, then clears after COMPLETED', async ({ page }) => {
    let statusCalls = 0;

    await page.route('**/api/v1/admin/inventory/weeks/*/sync', async route => {
      if (route.request().method() !== 'POST') {
        await route.fallback();
        return;
      }
      await route.fulfill({
        status: 202,
        contentType: 'application/json',
        body: JSON.stringify({ status: 'ACCEPTED', source: 'SNOWFLAKE_AGG_INVENTORY' }),
      });
    });

    await page.route('**/api/v1/admin/inventory/weeks/*/sync/status', async route => {
      statusCalls += 1;
      const body = statusCalls === 1
        ? { status: 'PENDING', lastSyncedAt: null, rowsUpserted: null, errorMessage: null }
        : { status: 'COMPLETED', lastSyncedAt: '2026-04-18T10:00:00Z', rowsUpserted: 1234, errorMessage: null };
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(body),
      });
    });

    const loginPage = new LoginPage(page);
    await loginPage.loginAs('ADMIN');
    await page.goto('/admin/auctions-data-center/inventory');

    // Wait for the page skeleton so the week-change effect has fired.
    await expect(page.getByRole('heading', { name: 'Inventory' })).toBeVisible();

    // Banner is driven by the PENDING response on the first status poll.
    await expect(page.getByText('Syncing from Snowflake…')).toBeVisible({ timeout: 10_000 });

    // After ~3s the poll fires again and the COMPLETED response clears it.
    await expect(page.getByText('Syncing from Snowflake…')).toBeHidden({ timeout: 10_000 });

    expect(statusCalls).toBeGreaterThanOrEqual(2);
  });
});
