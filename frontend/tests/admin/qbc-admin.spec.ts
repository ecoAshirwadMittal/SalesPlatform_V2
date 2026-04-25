import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages';
import { QualifiedBuyerCodesPage } from '../pages/QualifiedBuyerCodesPage';

test.describe('Qualified Buyer Codes admin page', () => {
  test('admin can view QBC page', async ({ page }) => {
    const login = new LoginPage(page);
    await login.goto();
    await login.loginAs('ADMIN');

    const qbcPage = new QualifiedBuyerCodesPage(page);
    await qbcPage.goto();

    await expect(page.getByRole('heading', { name: 'Qualified Buyer Codes' })).toBeVisible();
    await expect(qbcPage.table).toBeVisible();
  });

  test('filter controls are present', async ({ page }) => {
    const login = new LoginPage(page);
    await login.goto();
    await login.loginAs('ADMIN');

    const qbcPage = new QualifiedBuyerCodesPage(page);
    await qbcPage.goto();

    await expect(qbcPage.saIdFilter).toBeVisible();
    await expect(qbcPage.buyerCodeIdFilter).toBeVisible();
  });

  test('SalesOps can view QBC page', async ({ page }) => {
    const login = new LoginPage(page);
    await login.goto();
    await login.loginAs('SALESOPS');

    const qbcPage = new QualifiedBuyerCodesPage(page);
    await qbcPage.goto();

    await expect(page.getByRole('heading', { name: 'Qualified Buyer Codes' })).toBeVisible();
  });
});
