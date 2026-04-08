# Test Spec: RMATests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_WorkflowsTests\RMATests.spec.ts`
- **Category**: Test Spec
- **Lines**: 464
- **Size**: 19,196 bytes

## Source Code

```typescript
import { test, expect, Page } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { Logger, readRMAExcel } from '../../../utils/helpers/data_utils';
import * as fs from 'fs';

/**
 * RMA (Return Merchandise Authorization) Tests
 * 
 * Test Order (optimized to avoid modal interference):
 * 1. Login and navigate to RMAs page
 * 2. Column visibility toggle (no state change)
 * 3. 3-dot menu tests (Download, Instructions, Policy)
 * 4. RMA Upload/Submit test (last - creates modal)
 */

// Use describe.serial to run tests sequentially and share state
test.describe.serial("RMA Tests @pws-regression", () => {

    // Test data - using valid IMEI from shipped order 5008587
    const testData = {
        // IMEI from shipped order: Galaxy S25 Edge 512GB (SKU: PWS10013442)
        imei: "171120252000021",
        // Return reason must match a valid dropdown value in column B
        returnReason: "Defective Camera",
        buyerCode: "22379"  // From Nadia_GmailOne user data
    };

    let page: Page;
    let base: BaseTest;

    test.beforeAll(async ({ browser }) => {
        page = await browser.newPage();
        base = new BaseTest(page);
        await base.setup();

        // Login and navigate to RMAs page (shared setup for all tests)
        Logger("BeforeAll: Logging in with Nadia_GmailOne credentials");
        await base['loginPage'].loginAs(userRole.Nadia_GmailOne);
        await base['page'].waitForTimeout(3000);
        Logger("Login successful");

        Logger(`BeforeAll: Selecting buyer code ${testData.buyerCode}`);
        await base['welcomePage'].selectBuyerCode(testData.buyerCode);
        await base['page'].waitForTimeout(3000);
        Logger("Buyer code selected");

        Logger("BeforeAll: Navigating to RMAs page");
        await base['pws_navMenuAsBuyer'].chooseNavMenu("RMAs");
        await base['page'].waitForTimeout(2000);
        Logger("Navigated to RMAs page");
    });

    test.afterAll(async () => {
        if (base) {
            // Logout before teardown
            try {
                Logger("AfterAll: Logging out");
                await base['loginPage'].logout();
                Logger("Logout successful");
            } catch (error) {
                Logger(`Logout failed: ${error}`);
            }
            await base.teardown();
        }
    });

    // ============================================
    // UI TESTS - No state changes (run first)
    // ============================================

    test.skip('RMA-002: Toggle column visibility', async () => {
        /**
         * Test the column show/hide functionality (eye icon)
         * Run early - no state changes, no modals
         */
        Logger("Starting RMA-002: Column visibility toggle");

        const targetColumn = "Company";

        // Verify column is initially visible
        const initiallyVisible = await base['pws_RMAPage'].isColumnVisible(targetColumn);
        Logger(`Column "${targetColumn}" initially visible: ${initiallyVisible}`);
        expect(initiallyVisible).toBeTruthy();

        // Open column selector and hide the column
        await base['pws_RMAPage'].openColumnSelector();
        await base['pws_RMAPage'].toggleColumn(targetColumn);
        await base['pws_RMAPage'].closeColumnSelector();

        // Verify column is now hidden
        await base['page'].waitForTimeout(1000);
        const afterHide = await base['pws_RMAPage'].isColumnVisible(targetColumn);
        Logger(`Column "${targetColumn}" after hiding: ${afterHide}`);
        expect(afterHide).toBeFalsy();

        // Re-open column selector and show the column again
        await base['pws_RMAPage'].openColumnSelector();
        await base['pws_RMAPage'].toggleColumn(targetColumn);
        await base['pws_RMAPage'].closeColumnSelector();

        // Verify column is visible again
        await base['page'].waitForTimeout(1000);
        const afterShow = await base['pws_RMAPage'].isColumnVisible(targetColumn);
        Logger(`Column "${targetColumn}" after showing: ${afterShow}`);
        expect(afterShow).toBeTruthy();

        Logger(`✅ RMA-002: Column visibility toggle test passed`);
    });

    test.skip('RMA-003: Download RMA data from 3-dot menu', async () => {
        /**
         * Test the Download option in the 3-dot menu (top right)
         * Downloads data, no persistent state change
         */
        Logger("Starting RMA-003: Download from 3-dot menu");

        const downloadPath = await base['pws_RMAPage'].clickMenuDownload();

        expect(downloadPath).toBeTruthy();

        const fs = require('fs');
        expect(fs.existsSync(downloadPath)).toBeTruthy();

        Logger(`✅ RMA-003: Downloaded file to ${downloadPath}`);
    });

    test.skip('RMA-004: Verify RMA Instructions from 3-dot menu', async () => {
        /**
         * Test the RMA Instructions option - opens modal
         * Modal is closed by the method
         */
        Logger("Starting RMA-004: RMA Instructions modal");

        const success = await base['pws_RMAPage'].clickMenuRMAInstructions();

        expect(success).toBeTruthy();

        Logger(`✅ RMA-004: RMA Instructions modal verified`);
    });

    test('RMA-005: Verify RMA Policy from 3-dot menu', async () => {
        /**
         * Test the RMA Policy option
         * Opens a side drawer which needs to be closed with close button
         */
        Logger("Starting RMA-005: RMA Policy verification");

        // Click 3-dot menu and RMA Policy
        const success = await base['pws_RMAPage'].clickMenuRMAPolicy();

        expect(success).toBeTruthy();

        // Close the side drawer/modal if open using the specific close button
        Logger("Closing the RMA Policy side drawer");
        const closeButton = base['page'].locator('.rma-instructiondrawer-closebtn');
        if (await closeButton.isVisible({ timeout: 3000 }).catch(() => false)) {
            await closeButton.click();
            await base['page'].waitForTimeout(1000);
            Logger("Side drawer closed");
        }

        Logger(`✅ RMA-005: RMA Policy verified`);
    });

    // ============================================
    // UPLOAD TEST - State changing (run last)
    // ============================================

    test('RMA-006: Request RMA with IMEI upload', async () => {
        /**
         * RMA submission flow:
         * 1. Get initial count
         * 2. Submit RMA
         * 3. Verify count increased
         * 4. Verify first record has our IMEI
         * 5. Download Excel and verify IMEI
         */

        // Get initial RMA count before submission
        Logger("Step 0: Getting initial RMA count");
        const initialCount = await base['pws_RMAPage'].getPaginationCount();
        Logger(`Initial RMA count: ${initialCount}`);

        Logger("Step 1: Clicking Request RMA button");
        await base['pws_RMAPage'].clickRequestRMAButton();
        await base['page'].waitForTimeout(2000);
        Logger("Request RMA modal opened");

        Logger("Step 2: Downloading RMA template");
        const templatePath = await base['pws_RMAPage'].downloadRMATemplate();
        expect(templatePath).toBeTruthy();
        Logger(`Template downloaded to: ${templatePath}`);

        Logger(`Step 3: Filling template with IMEI: ${testData.imei}`);
        const filledPath = await base['pws_RMAPage'].fillRMATemplate(
            templatePath,
            testData.imei,
            testData.returnReason
        );
        expect(filledPath).toBeTruthy();
        Logger(`Template filled and saved to: ${filledPath}`);

        Logger("Step 4: Uploading filled RMA template");
        await base['pws_RMAPage'].uploadRMAFile(filledPath);
        await base['page'].waitForTimeout(2000);
        Logger("Template uploaded");

        Logger("Step 5: Submitting RMA request");
        await base['pws_RMAPage'].submitRMA();
        await base['page'].waitForTimeout(3000);
        Logger("RMA submitted");

        const isSuccess = await base['pws_RMAPage'].verifyRMASubmissionSuccess();
        Logger(`RMA submission success: ${isSuccess}`);
        expect(isSuccess).toBeTruthy();

        // Step 6: Close the popup - try multiple methods
        Logger("Step 6: Closing success popup");
        await base['page'].keyboard.press('Escape');
        await base['page'].waitForTimeout(1000);

        // Step 7: Refresh the page and navigate back to RMAs if needed
        Logger("Step 7: Refreshing page");
        await base['page'].reload();
        await base['page'].waitForTimeout(3000);

        // Check if we need to re-navigate to RMAs tab (reload might go to default page)
        const currentUrl = base['page'].url();
        Logger(`Current URL after reload: ${currentUrl}`);
        if (!currentUrl.includes('/RMA')) {
            Logger("Re-navigating to RMAs tab...");
            await base['page'].getByRole('button', { name: /RMAs/i }).click();
            await base['page'].waitForTimeout(2000);
        }

        // Wait for the Mendix datagrid to be visible
        await base['page'].waitForSelector('.pws-rma-returns-datagrid', { timeout: 15000 });
        Logger("Page refreshed and RMA datagrid visible");

        // Step 7b: Verify RMA count increased
        Logger("Step 7b: Verifying RMA count increased");
        const newCount = await base['pws_RMAPage'].getPaginationCount();
        Logger(`New RMA count: ${newCount}, Expected: ${initialCount + 1}`);
        if (initialCount > 0 && newCount > 0) {
            expect(newCount).toBeGreaterThan(initialCount);
            Logger(`✓ RMA count increased from ${initialCount} to ${newCount}`);
        }

        // Step 8: Click first row and verify details on RMA Details page
        Logger("Step 8: Clicking first row to verify RMA details");
        const verifyResult = await base['pws_RMAPage'].clickFirstRowAndVerifyDetails(
            testData.imei,
            testData.returnReason
        );

        Logger(`Verification result: IMEI=${verifyResult.imei}, Reason=${verifyResult.returnReason}, Status=${verifyResult.status}`);
        Logger(`Success: ${verifyResult.success}`);

        // Log what we got vs expected for debugging
        if (verifyResult.imei) {
            Logger(`Found IMEI: ${verifyResult.imei}`);
            Logger(`Expected IMEI: ${testData.imei}`);
            Logger(`IMEI contains expected: ${verifyResult.imei.includes(testData.imei)}`);
        }

        // Main assertion
        expect(verifyResult.success, `IMEI verification failed. Expected ${testData.imei}, got ${verifyResult.imei}`).toBeTruthy();
        Logger(`✓ IMEI verified on details page: ${verifyResult.imei}`);
        Logger(`Return Reason: ${verifyResult.returnReason}`);
        Logger(`Status: ${verifyResult.status}`);

        // Step 9: Download Excel from RMA Details page and verify IMEI
        Logger("Step 9: Downloading Excel from RMA Details page");
        const excelPath = await base['pws_RMAPage'].downloadRMADetailsExcel();
        expect(fs.existsSync(excelPath)).toBeTruthy();
        Logger(`Excel downloaded to: ${excelPath}`);

        // Parse and verify Excel contains the IMEI
        Logger("Step 10: Verifying IMEI in downloaded Excel");
        const excelData = readRMAExcel(excelPath);
        expect(excelData.length).toBeGreaterThan(0);
        Logger(`Parsed ${excelData.length} rows from Excel`);

        const foundInExcel = excelData.some(row => row.imei.includes(testData.imei) || testData.imei.includes(row.imei));
        expect(foundInExcel, `IMEI ${testData.imei} not found in Excel`).toBeTruthy();
        Logger(`✓ IMEI verified in Excel: ${testData.imei}`);

        Logger("✅ RMA-006: Completed with count verification and Excel download");
    });

    // ============================================
    // VALIDATION TEST - Error scenarios
    // ============================================

    test('RMA-008: Verify validation errors for invalid IMEI and reason code', async () => {
        /**
         * Validation test scenarios:
         * 1. Invalid IMEI should show error popup
         * 2. No/invalid reason code should show error popup
         */

        // Test Case 1: Invalid IMEI
        Logger("Test Case 1: Testing invalid IMEI");

        Logger("Step 1a: Opening RMA request modal");
        await base['pws_RMAPage'].clickRequestRMAButton();
        await base['page'].waitForTimeout(2000);

        Logger("Step 1b: Downloading template");
        const templatePath1 = await base['pws_RMAPage'].downloadRMATemplate();
        expect(templatePath1).toBeTruthy();

        Logger("Step 1c: Filling template with INVALID IMEI");
        const invalidImei = "INVALID123456789";
        const filledPath1 = await base['pws_RMAPage'].fillRMATemplate(
            templatePath1,
            invalidImei,
            testData.returnReason
        );
        expect(filledPath1).toBeTruthy();

        Logger("Step 1d: Uploading and submitting with invalid IMEI");
        await base['pws_RMAPage'].uploadRMAFile(filledPath1);
        await base['page'].waitForTimeout(2000);
        await base['pws_RMAPage'].submitRMA();
        await base['page'].waitForTimeout(3000);

        // Check for error message using specific RMA error popup selectors
        Logger("Step 1e: Checking for error popup");
        const errorContainer1 = base['page'].locator('.rma-file-upload-error');
        const errorHeading1 = base['page'].locator('text=Your file is incomplete');
        const invalidFileText1 = base['page'].locator('text=Invalid file');

        const hasError1 = await errorContainer1.isVisible({ timeout: 5000 }).catch(() => false)
            || await errorHeading1.isVisible({ timeout: 3000 }).catch(() => false)
            || await invalidFileText1.isVisible({ timeout: 3000 }).catch(() => false);

        Logger(`Invalid IMEI error shown: ${hasError1}`);
        expect(hasError1, "Expected error message for invalid IMEI").toBeTruthy();

        // Click "Start Over" button to close the error popup
        const startOverBtn = base['page'].getByRole('button', { name: 'Start Over' });
        if (await startOverBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
            await startOverBtn.click();
            await base['page'].waitForTimeout(1000);
        } else {
            await base['page'].keyboard.press('Escape');
            await base['page'].waitForTimeout(1000);
        }
        Logger("✓ Test Case 1 passed: Invalid IMEI shows error");

        // Refresh page for next test
        await base['page'].reload();
        await base['page'].waitForTimeout(3000);

        // Navigate back to RMAs if needed
        const currentUrl = base['page'].url();
        if (!currentUrl.includes('/RMA')) {
            await base['page'].getByRole('button', { name: /RMAs/i }).click();
            await base['page'].waitForTimeout(2000);
        }

        // Test Case 2: Invalid/Random reason code
        Logger("Test Case 2: Testing invalid reason code");

        Logger("Step 2a: Opening RMA request modal");
        await base['pws_RMAPage'].clickRequestRMAButton();
        await base['page'].waitForTimeout(2000);

        Logger("Step 2b: Downloading template");
        const templatePath2 = await base['pws_RMAPage'].downloadRMATemplate();
        expect(templatePath2).toBeTruthy();

        Logger("Step 2c: Filling template with valid IMEI but INVALID reason");
        const invalidReason = "RANDOM_INVALID_REASON_12345";
        const filledPath2 = await base['pws_RMAPage'].fillRMATemplate(
            templatePath2,
            testData.imei,
            invalidReason
        );
        expect(filledPath2).toBeTruthy();

        Logger("Step 2d: Uploading and submitting with invalid reason");
        await base['pws_RMAPage'].uploadRMAFile(filledPath2);
        await base['page'].waitForTimeout(2000);
        await base['pws_RMAPage'].submitRMA();
        await base['page'].waitForTimeout(3000);

        // Check for error message using specific RMA error popup selectors
        Logger("Step 2e: Checking for error popup");
        const errorContainer2 = base['page'].locator('.rma-file-upload-error');
        const errorHeading2 = base['page'].locator('text=Your file is incomplete');
        const invalidFileText2 = base['page'].locator('text=Invalid file');

        const hasError2 = await errorContainer2.isVisible({ timeout: 5000 }).catch(() => false)
            || await errorHeading2.isVisible({ timeout: 3000 }).catch(() => false)
            || await invalidFileText2.isVisible({ timeout: 3000 }).catch(() => false);

        Logger(`Invalid reason error shown: ${hasError2}`);
        expect(hasError2, "Expected error message for invalid reason code").toBeTruthy();

        // Click "Start Over" button to close the error popup
        const startOverBtn2 = base['page'].getByRole('button', { name: 'Start Over' });
        if (await startOverBtn2.isVisible({ timeout: 2000 }).catch(() => false)) {
            await startOverBtn2.click();
            await base['page'].waitForTimeout(1000);
        } else {
            await base['page'].keyboard.press('Escape');
            await base['page'].waitForTimeout(1000);
        }
        Logger("✓ Test Case 2 passed: Invalid reason code shows error");

        Logger("✅ RMA-008: Validation tests completed");
    });

    // ============================================
    // SKIPPED TESTS (TODO: Fix later)
    // ============================================

    test.skip('RMA-007: Request RMA with dynamic IMEI from shipped order', async () => {
        /**
         * Dynamic IMEI retrieval flow - currently skipped
         * Needs navigation fixes
         */

        Logger("Starting RMA-007: Dynamic IMEI retrieval flow");

        const orderData = await base['pws_RMAPage'].getIMEIFromShippedOrder();

        expect(orderData).not.toBeNull();
        expect(orderData?.imei).toBeTruthy();
        expect(orderData?.orderNumber).toBeTruthy();

        Logger(`Retrieved IMEI: ${orderData?.imei} from order ${orderData?.orderNumber}`);

        await base['pws_RMAPage'].clickRequestRMAButton();
        await base['page'].waitForTimeout(2000);

        const templatePath = await base['pws_RMAPage'].downloadRMATemplate();
        expect(templatePath).toBeTruthy();

        const filledPath = await base['pws_RMAPage'].fillRMATemplate(
            templatePath,
            orderData!.imei,
            testData.returnReason
        );
        expect(filledPath).toBeTruthy();

        await base['pws_RMAPage'].uploadRMAFile(filledPath);
        await base['page'].waitForTimeout(2000);

        await base['pws_RMAPage'].submitRMA();
        await base['page'].waitForTimeout(3000);

        const isSuccess = await base['pws_RMAPage'].verifyRMASubmissionSuccess();
        Logger(`RMA submission success: ${isSuccess}`);

        await base['pws_RMAPage'].closeModal();

        Logger(`✅ RMA-007 completed: Submitted RMA for IMEI ${orderData!.imei} from order ${orderData!.orderNumber}`);
    });
});

```
