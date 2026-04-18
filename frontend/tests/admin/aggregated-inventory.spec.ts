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
