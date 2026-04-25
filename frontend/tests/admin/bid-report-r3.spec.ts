import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages';
import { BidReportPage } from '../pages/BidReportPage';

test.describe('R3 Bid Report admin page', () => {
  test('admin can view R3 bid report page', async ({ page }) => {
    const login = new LoginPage(page);
    await login.goto();
    await login.loginAs('ADMIN');

    const reportPage = new BidReportPage(page);
    await reportPage.goto();

    await expect(page.getByRole('heading', { name: 'R3 Bid Report' })).toBeVisible();
    await expect(reportPage.table).toBeVisible();
  });

  test('SalesOps can access R3 bid report', async ({ page }) => {
    const login = new LoginPage(page);
    await login.goto();
    await login.loginAs('SALESOPS');

    const reportPage = new BidReportPage(page);
    await reportPage.goto();

    await expect(page.getByRole('heading', { name: 'R3 Bid Report' })).toBeVisible();
  });

  test('auction ID filter is visible', async ({ page }) => {
    const login = new LoginPage(page);
    await login.goto();
    await login.loginAs('ADMIN');

    const reportPage = new BidReportPage(page);
    await reportPage.goto();

    await expect(reportPage.auctionIdFilter).toBeVisible();
  });
});
