import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';

test.describe('bidder dashboard (live backend)', () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), 'requires Spring Boot backend on :8080');
  });

  test('bidder can open dashboard, edit, submit, resubmit', async ({ page }) => {
    await page.goto('/login');
    await page.fill('[name="email"]', 'bidder@buyerco.com');
    await page.fill('[name="password"]', 'Bidder123!');
    await page.click('button[type="submit"]');

    // assumes a buyer code id is known in dev seed; use selector bar if available
    await page.goto('/bidder/dashboard?buyerCodeId=1');
    await expect(page.getByRole('columnheader', { name: 'Device' })).toBeVisible();

    // edit first row quantity + amount
    const qtyInput = page.getByLabel('Quantity for AAA1');
    await qtyInput.fill('5');
    const amtInput = page.getByLabel('Amount for AAA1');
    await amtInput.fill('20');

    // wait for debounce + save
    await page.waitForTimeout(700);

    // submit
    await page.getByRole('button', { name: 'Submit' }).click();
    await expect(page.getByText(/Submitted at/)).toBeVisible();

    // resubmit
    await qtyInput.fill('8');
    await page.waitForTimeout(700);
    await page.getByRole('button', { name: 'Submit' }).click();
    await expect(page.getByText(/Submitted at/)).toBeVisible();
  });
}); // end describe 'bidder dashboard (live backend)'
