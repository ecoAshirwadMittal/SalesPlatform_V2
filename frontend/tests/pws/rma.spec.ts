import { test, expect, Page } from '@playwright/test';
import { LoginPage, NavPage, RmaPage, OrderPage, OrderDetailPage } from '../pages';

/**
 * RMA Tests — ported from Mendix PWS_WorkflowsTests/RMATests.spec.ts
 *
 * Active tests:
 *   RMA-005: RMA Policy from 3-dot menu
 *   RMA-006: Request RMA with IMEI upload
 *   RMA-007: Dynamic IMEI from shipped order (cross-page flow)
 *   RMA-008: Validation errors for invalid IMEI
 *
 * Skipped (ported as test.skip):
 *   RMA-002: Toggle column visibility
 *   RMA-003: Download RMA data
 *   RMA-004: RMA Instructions modal
 */

const testData = {
  imei: `1711${Date.now().toString().slice(-11)}`,
  returnReason: 'Defective Camera',
};

let page: Page;
let loginPage: LoginPage;
let navPage: NavPage;
let rmaPage: RmaPage;
let orderPage: OrderPage;
let orderDetailPage: OrderDetailPage;

test.beforeAll(async ({ browser }) => {
  page = await browser.newPage();
  loginPage = new LoginPage(page);
  navPage = new NavPage(page);
  rmaPage = new RmaPage(page);
  orderPage = new OrderPage(page);
  orderDetailPage = new OrderDetailPage(page);

  await loginPage.loginAs('BIDDER');
  await navPage.chooseNavMenu('RMAs');
  await page.waitForTimeout(2_000);
});

test.afterAll(async () => {
  try { await loginPage.logout(); } catch { /* ignore */ }
  await page.close();
});

test.describe.serial('RMA Tests @regression @pws', () => {

  // ── UI tests (no state changes) ──

  test.skip('RMA-002: Toggle column visibility', async () => {
    // Original was skipped. Requires column selector UI to exist in modern app.
  });

  test.skip('RMA-003: Download RMA data from 3-dot menu', async () => {
    // Original was skipped. Requires download functionality.
  });

  test.skip('RMA-004: RMA Instructions from 3-dot menu', async () => {
    // Original was skipped. Requires modal.
  });

  test('RMA-005: Verify RMA Policy from 3-dot menu', async () => {
    const success = await rmaPage.clickMenuRMAPolicy();
    expect(success).toBeTruthy();

    // Close the policy drawer/modal
    const closeBtn = page.locator('button').filter({ hasText: /close|×/i }).first();
    if (await closeBtn.isVisible({ timeout: 3_000 }).catch(() => false)) {
      await closeBtn.click();
      await page.waitForTimeout(500);
    } else {
      await page.keyboard.press('Escape');
    }
  });

  // ── Upload test (state changing) ──

  test('RMA-006: Request RMA with IMEI upload', async () => {
    const initialCount = await rmaPage.getPaginationCount();

    await rmaPage.clickRequestRMAButton();
    await page.waitForTimeout(1_000);

    // Download template
    const templatePath = await rmaPage.downloadRMATemplate();
    expect(templatePath).toBeTruthy();

    // In the modern app, the template is an XLSX file.
    // We need to fill it with test data and upload.
    // For E2E, we create a minimal CSV/XLSX with IMEI + reason.
    const fs = await import('fs');
    const testFilePath = `./test-results/rma-test-${Date.now()}.csv`;
    fs.writeFileSync(testFilePath, `IMEI,Return Reason\n${testData.imei},${testData.returnReason}\n`);

    await rmaPage.uploadRMAFile(testFilePath);
    await page.waitForTimeout(1_000);

    await rmaPage.submitRMA();
    await page.waitForTimeout(2_000);

    const isSuccess = await rmaPage.verifyRMASubmissionSuccess();
    expect(isSuccess).toBeTruthy();

    await rmaPage.closeModal();

    // Refresh and verify count increased
    await page.reload();
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(2_000);

    const newCount = await rmaPage.getPaginationCount();
    if (initialCount > 0 && newCount > 0) {
      expect(newCount).toBeGreaterThan(initialCount);
    }
  });

  // ── Validation tests ──

  test('RMA-008: Validation errors for invalid IMEI', async () => {
    await rmaPage.clickRequestRMAButton();
    await page.waitForTimeout(1_000);

    // Upload CSV with an invalid return reason to guarantee backend validation error
    const fs = await import('fs');
    const testImei = `VAL${Date.now().toString().slice(-12)}`;
    const invalidFilePath = `./test-results/rma-invalid-${Date.now()}.csv`;
    fs.writeFileSync(invalidFilePath,
      `IMEI,Return Reason\n${testImei},INVALID_REASON_XYZ\n`);

    await rmaPage.uploadRMAFile(invalidFilePath);

    // Wait for the Submit RMA button to be enabled (file selected)
    const submitBtn = page.getByRole('button', { name: /submit.*rma/i });
    await submitBtn.waitFor({ state: 'visible', timeout: 5_000 });
    await expect(submitBtn).toBeEnabled({ timeout: 5_000 });
    await submitBtn.click();

    // Wait for "Validation Errors" heading or error list to appear
    const errorTitle = page.locator('text=/validation error/i');
    await errorTitle.first().waitFor({ state: 'visible', timeout: 15_000 });

    const hasError = await errorTitle.first().isVisible();
    expect(hasError).toBeTruthy();

    // Close error and cleanup
    await rmaPage.closeModal();
  });

  test('RMA-007: Request RMA with dynamic IMEI from shipped order', async () => {
    // Step 1: Navigate to Orders and find a shipped order
    await navPage.chooseNavMenu('Orders');
    await page.waitForTimeout(2_000);
    await orderPage.chooseTab('Complete');

    const hasShipped = await orderPage.hasShippedOrders();
    if (!hasShipped) {
      test.skip(true, 'No shipped orders available for IMEI extraction');
      return;
    }

    const offerId = await orderPage.clickFirstShippedOrder();
    expect(offerId).toBeTruthy();

    // Step 2: Switch to By Device view and extract IMEI
    await orderDetailPage.switchToByDevice();
    const hasDevices = await orderDetailPage.hasDeviceData();
    if (!hasDevices) {
      test.skip(true, 'Shipped order has no device/IMEI data');
      return;
    }

    const dynamicImei = await orderDetailPage.getFirstImei();
    expect(dynamicImei).toBeTruthy();

    // Step 3: Navigate to RMAs and submit RMA with the extracted IMEI
    await navPage.chooseNavMenu('RMAs');
    await page.waitForTimeout(2_000);

    const initialCount = await rmaPage.getPaginationCount();

    await rmaPage.clickRequestRMAButton();
    await page.waitForTimeout(1_000);

    // Create a CSV file with the dynamic IMEI
    const fs = await import('fs');
    const testFilePath = `./test-results/rma-dynamic-${Date.now()}.csv`;
    fs.writeFileSync(testFilePath, `IMEI,Return Reason\n${dynamicImei},Defective Camera\n`);

    await rmaPage.uploadRMAFile(testFilePath);
    await page.waitForTimeout(1_000);

    await rmaPage.submitRMA();
    await page.waitForTimeout(2_000);

    const isSuccess = await rmaPage.verifyRMASubmissionSuccess();
    expect(isSuccess).toBeTruthy();

    await rmaPage.closeModal();

    // Step 4: Verify RMA count increased
    await page.reload();
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(2_000);

    const newCount = await rmaPage.getPaginationCount();
    if (initialCount > 0 && newCount > 0) {
      expect(newCount).toBeGreaterThan(initialCount);
    }
  });
});
