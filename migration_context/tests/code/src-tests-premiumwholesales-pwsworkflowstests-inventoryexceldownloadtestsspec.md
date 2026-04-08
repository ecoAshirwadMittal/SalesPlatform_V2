# Test Spec: InventoryExcelDownloadTests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_WorkflowsTests\InventoryExcelDownloadTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 207
- **Size**: 8,685 bytes

## Source Code

```typescript
import { test, expect, Page } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { Logger, readInventoryExcel, InventoryRowData } from '../../../utils/helpers/data_utils';
import * as fs from 'fs';

/**
 * Inventory Excel Download Verification Tests
 * 
 * INV-001: Quick smoke test - validates first 2 pages (for CI)
 * INV-002: Full test - validates ALL pages (skip by default, run manually)
 */

test.describe.serial("Inventory Excel Download Tests @pws-regression", () => {
    let page: Page;
    let base: BaseTest;

    test.beforeAll(async ({ browser }) => {
        page = await browser.newPage();
        base = new BaseTest(page);
        await base.setup();
    });

    test.afterAll(async () => {
        if (base) {
            await base.teardown();
        }
    });

    /**
     * QUICK SMOKE TEST - First 2 pages only, validates all fields
     * Suitable for CI/regular test runs
     */
    test('INV-001: Quick smoke test - Download Excel and verify first 2 pages', async () => {
        Logger("Step 1: Logging in with Nadia_GmailOne credentials");
        await base['loginPage'].loginAs(userRole.Nadia_GmailOne);
        await base['page'].waitForTimeout(3000);
        Logger("Login successful");

        // Select buyer code (required for Nadia_GmailOne)
        Logger("Step 1b: Selecting buyer code");
        await base['welcomePage'].selectBuyerCode("22379");
        await base['page'].waitForTimeout(3000);
        Logger("Buyer code selected");

        // Navigate to Shop page
        Logger("Step 2: Navigating to Shop page");
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
        await base['page'].waitForTimeout(3000);
        Logger("Navigated to Shop page");

        // Verify inventory table is displayed
        Logger("Step 3: Verifying inventory table is displayed");
        const isTableVisible = await base['pws_shopPage'].verifyAndLogPwsDataGridTableisplay();
        expect(isTableVisible).toBeTruthy();

        // Extract table data from first 2 pages only (10 rows each = 20 rows max)
        Logger("Step 4: Extracting table data (first 2 pages only)");
        const tableData = await base['pws_shopPage'].getAllVisibleTableData(20);
        expect(tableData.length).toBeGreaterThan(0);
        Logger(`Extracted ${tableData.length} rows from first page(s)`);

        // Download Excel file - Current View
        Logger("Step 5: Downloading Excel file (Current View)");
        const excelPath = await base['pws_shopPage'].downloadExcel("Download Current View");
        expect(fs.existsSync(excelPath)).toBeTruthy();
        Logger(`Excel downloaded to: ${excelPath}`);

        // Parse Excel file
        Logger("Step 6: Parsing downloaded Excel file");
        const excelData = readInventoryExcel(excelPath);
        expect(excelData.length).toBeGreaterThan(0);
        Logger(`Parsed ${excelData.length} rows from Excel`);

        // Row count check
        Logger(`Table rows: ${tableData.length}, Excel rows: ${excelData.length}`);
        expect(excelData.length).toBeGreaterThanOrEqual(tableData.length);

        // Validate sample rows (first 10 from table)
        Logger("Step 7: Validating sample rows (field comparison)");
        let matchCount = 0;
        let mismatchCount = 0;
        const sampleSize = Math.min(tableData.length, 10);

        for (let i = 0; i < sampleSize; i++) {
            const tableRow = tableData[i];
            const excelRow = excelData.find(e => e.sku === tableRow.sku);

            if (excelRow) {
                let rowMatches = true;
                const issues: string[] = [];

                // Validate all key fields
                if (excelRow.model !== tableRow.model) { issues.push(`Model`); rowMatches = false; }
                if (excelRow.grade !== tableRow.grade) { issues.push(`Grade`); rowMatches = false; }
                if (Math.abs(excelRow.price - tableRow.price) > 0.01) { issues.push(`Price`); rowMatches = false; }
                if (excelRow.category !== tableRow.category) { issues.push(`Category`); rowMatches = false; }
                if (excelRow.brand !== tableRow.brand) { issues.push(`Brand`); rowMatches = false; }

                if (rowMatches) {
                    matchCount++;
                    Logger(`✓ SKU ${tableRow.sku}: All fields match`);
                } else {
                    mismatchCount++;
                    Logger(`✗ SKU ${tableRow.sku}: Mismatch in ${issues.join(', ')}`);
                }
            } else {
                mismatchCount++;
                Logger(`⚠ SKU ${tableRow.sku}: Not found in Excel`);
            }
        }

        Logger(`\n========== SMOKE TEST SUMMARY ==========`);
        Logger(`Rows sampled: ${sampleSize}`);
        Logger(`✓ Matches: ${matchCount}`);
        Logger(`✗ Mismatches: ${mismatchCount}`);
        Logger(`=========================================\n`);

        expect(matchCount).toBeGreaterThan(0);
        expect(mismatchCount).toBe(0);

        Logger("✅ INV-001: Quick smoke test passed");
    });

    /**
     * FULL VALIDATION TEST - All pages
     * Skipped by default - run manually with: npx playwright test --grep "INV-002"
     */
    test('INV-002: Full test - Download Excel and verify ALL pages (manual run)', async () => {
        Logger("Step 1: Logging in with PWS_UserOne credentials");
        await base['loginPage'].loginAs(userRole.PWS_UserOne);
        await base['page'].waitForTimeout(3000);
        Logger("Login successful");

        // Navigate to Shop page
        Logger("Step 2: Navigating to Shop page");
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
        await base['page'].waitForTimeout(3000);
        Logger("Navigated to Shop page");

        // Verify inventory table is displayed
        Logger("Step 3: Verifying inventory table is displayed");
        const isTableVisible = await base['pws_shopPage'].verifyAndLogPwsDataGridTableisplay();
        expect(isTableVisible).toBeTruthy();

        // Extract table data from ALL pages
        Logger("Step 4: Extracting table data across ALL pages");
        const tableData = await base['pws_shopPage'].getAllTableDataAcrossPages();
        expect(tableData.length).toBeGreaterThan(0);
        Logger(`Extracted ${tableData.length} total rows from table`);

        // Download Excel file with ALL items
        Logger("Step 5: Downloading Excel file (All Items)");
        const excelPath = await base['pws_shopPage'].downloadExcel("Download All Item");
        expect(fs.existsSync(excelPath)).toBeTruthy();
        Logger(`Excel downloaded to: ${excelPath}`);

        // Parse Excel file
        Logger("Step 6: Parsing downloaded Excel file");
        const excelData = readInventoryExcel(excelPath);
        expect(excelData.length).toBeGreaterThan(0);
        Logger(`Parsed ${excelData.length} rows from Excel`);

        // Row count must match
        Logger(`Table rows: ${tableData.length}, Excel rows: ${excelData.length}`);
        expect(excelData.length).toBe(tableData.length);

        // Full validation
        Logger("Step 7: Full field validation across ALL rows");
        let matchCount = 0;
        let mismatchCount = 0;

        for (const tableRow of tableData) {
            const excelRow = excelData.find(e => e.sku === tableRow.sku);

            if (excelRow) {
                let rowMatches = true;
                if (excelRow.model !== tableRow.model) rowMatches = false;
                if (excelRow.grade !== tableRow.grade) rowMatches = false;
                if (Math.abs(excelRow.price - tableRow.price) > 0.01) rowMatches = false;
                if (excelRow.category !== tableRow.category) rowMatches = false;
                if (excelRow.brand !== tableRow.brand) rowMatches = false;

                if (rowMatches) matchCount++;
                else {
                    mismatchCount++;
                    Logger(`✗ SKU ${tableRow.sku}: Field mismatch`);
                }
            } else {
                mismatchCount++;
                Logger(`⚠ SKU ${tableRow.sku}: Not found in Excel`);
            }
        }

        Logger(`\n========== FULL VALIDATION SUMMARY ==========`);
        Logger(`Total rows: ${tableData.length}`);
        Logger(`✓ Matches: ${matchCount}`);
        Logger(`✗ Mismatches: ${mismatchCount}`);
        Logger(`=============================================\n`);

        expect(matchCount).toBe(tableData.length);
        expect(mismatchCount).toBe(0);

        Logger("✅ INV-002: Full validation test passed");
    });
});

```
