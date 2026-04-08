# Test Spec: seed.spec.ts

- **Path**: `src\tests\seed.spec.ts`
- **Category**: Test Spec
- **Lines**: 251
- **Size**: 8,742 bytes

## Source Code

```typescript
// /**
//  * SEED SPEC - Gold Standard Test Pattern
//  *
//  * This file demonstrates the recommended patterns for PWS UI automation:
//  * 1. UI-first approach (no API dependencies)
//  * 2. BaseTest + Page Object Model usage
//  * 3. Role-based login with userRole enum
//  * 4. Serial test execution for state-dependent flows
//  * 5. Proper logging throughout
//  * 6. Clean setup/teardown
//  *
//  * Reference: .agent/rules/playwright-agent-standards.md
//  * Spec: specs/[workflow-name].md
//  */

// import { test, expect } from '@playwright/test';
// import { BaseTest } from './BaseTest';
// import { userRole } from '../utils/resources/enum';
// import { Logger } from '../utils/helpers/data_utils';

// // ============================================
// // TEST CONFIGURATION
// // ============================================

// let base: BaseTest;

// /**
//  * Pattern: Single browser instance shared across all tests
//  * Use beforeAll for login + navigation setup
//  */
// test.beforeAll(async ({ browser }) => {
//     Logger("=== SEED SPEC: Test Suite Starting ===");
//     const page = await browser.newPage();
//     base = new BaseTest(page);
//     await base.setup();
// });

// /**
//  * Pattern: Clean teardown with logout
//  */
// test.afterAll(async () => {
//     if (base) {
//         try {
//             Logger("=== SEED SPEC: Cleaning up ===");
//             await base['loginPage'].logout();
//         } catch (e) {
//             Logger("Logout skipped - may have already logged out");
//         }
//         await base.teardown();
//     }
// });

// // ============================================
// // TEST SUITES
// // ============================================

// /**
//  * Pattern: Use describe.configure({ mode: 'serial' }) for state-dependent tests
//  * Tests in this block share browser state and run in order
//  */
// test.describe('PWS Buyer - Shop Page Smoke Tests', () => {
//     test.describe.configure({ mode: 'serial' });

//     /**
//      * Login Test Pattern:
//      * - Use loginAs() with userRole enum
//      * - Log the action clearly
//      * - Verify successful navigation
//      */
//     test('SEED-001: Buyer can login successfully', async () => {
//         Logger("Test: Verifying buyer login");

//         await base['loginPage'].loginAs(userRole.PWS_UserOne);

//         // Verify: Shop page should load after login
//         const isTableVisible = await base['pws_shopPage'].verifyAndLogPwsDataGridTableisplay();
//         expect(isTableVisible).toBeTruthy();

//         Logger("✅ Login successful - Shop page visible");
//     });

//     /**
//      * Inventory Interaction Pattern:
//      * - Sort data for predictable results
//      * - Enter offer data using POM methods
//      * - Verify calculations
//      */
//     test('SEED-002: Buyer can enter offer data and verify totals', async () => {
//         Logger("Test: Entering offer data and verifying totals");

//         // Sort to ensure we have items with available quantity
//         await base['pws_shopPage'].sortAvlQty("descending");

//         // Enter offer for 1 SKU
//         const offerData = await base['pws_shopPage'].enterOfferData(1, 100, 1);
//         Logger(`Entered offer: Price=${offerData[0].price}, Qty=${offerData[0].qty}`);

//         // Wait for UI to update
//         await base['page'].waitForTimeout(500);

//         // Verify total calculation
//         const rowData = await base['pws_shopPage'].getRowData(0);
//         const expectedTotal = offerData[0].price * offerData[0].qty;

//         expect(rowData.total).toBe(expectedTotal);
//         Logger(`✅ Total calculated correctly: ${rowData.total} = ${offerData[0].price} × ${offerData[0].qty}`);
//     });

//     /**
//      * Cart Navigation Pattern:
//      * - Use clickCartButton() to navigate
//      * - Verify page transition
//      */
//     test('SEED-003: Buyer can navigate to cart', async () => {
//         Logger("Test: Navigating to cart");

//         await base['pws_shopPage'].clickCartButton();

//         const isCartDisplayed = await base['pws_CartPage'].isCartPageDisplayed();
//         expect(isCartDisplayed).toBeTruthy();

//         Logger("✅ Cart page displayed");
//     });

//     /**
//      * Cart Summary Verification Pattern:
//      * - Use getSummaryOffer() to extract cart totals
//      * - Verify SKU count, quantity, and amount
//      */
//     test('SEED-004: Cart summary shows correct values', async () => {
//         Logger("Test: Verifying cart summary");

//         const summary = await base['pws_CartPage'].getSummaryOffer();
//         const [skuCount, totalQty, totalAmount] = summary;

//         Logger(`Cart Summary: SKUs=${skuCount}, Qty=${totalQty}, Total=${totalAmount}`);

//         expect(parseInt(skuCount)).toBeGreaterThan(0);
//         expect(parseInt(totalQty)).toBeGreaterThan(0);
//         expect(parseInt(totalAmount)).toBeGreaterThan(0);

//         Logger("✅ Cart summary verified");
//     });

//     /**
//      * Reset/Cleanup Pattern:
//      * - Use Reset Offer to clear cart
//      * - Navigate back to Shop for clean state
//      */
//     test('SEED-005: Buyer can reset offer', async () => {
//         Logger("Test: Resetting offer");

//         await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
//         await base['pws_shopPage'].selectMoreActionOption("Reset Offer");

//         // Verify cart is now empty
//         await base['pws_shopPage'].clickCartButton();
//         const isSubmitEnabled = await base['pws_CartPage'].isSubmitButtonEnabled();
//         expect(isSubmitEnabled).toBeFalsy();

//         Logger("✅ Offer reset - cart is empty");
//     });

//     /**
//      * Logout Pattern:
//      * - Logout before switching roles
//      * - This enables the next describe block to use a different user
//      */
//     test('SEED-006: Buyer can logout', async () => {
//         Logger("Test: Logging out buyer");

//         await base['loginPage'].logoutFromPWS();

//         Logger("✅ Logout successful");
//     });
// });

// /**
//  * Pattern: Separate describe block for Admin role
//  * Demonstrates role switching within same browser
//  */
// test.describe('PWS Admin - Offer Queue Smoke Tests', () => {
//     test.describe.configure({ mode: 'serial' });

//     test('SEED-007: Admin can login and view Offer Queue', async () => {
//         Logger("Test: Admin login and Offer Queue navigation");

//         await base['loginPage'].loginAs(userRole.ADMIN_One);
//         await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");

//         // Verify we're on the Offer Queue page
//         // The page should have status tabs
//         const page = base['page'];
//         await page.waitForTimeout(2000);

//         Logger("✅ Admin logged in and viewing Offer Queue");
//     });

//     test('SEED-008: Admin can switch between Offer Status tabs', async () => {
//         Logger("Test: Switching Offer Status tabs");

//         const tabs = ["Sales Review", "Buyer Acceptance", "Ordered", "Declined"];

//         for (const tab of tabs) {
//             Logger(`Clicking tab: ${tab}`);
//             await base['pws_OfferQueuePage'].chooseOfferStatusTab(tab);
//             await base['page'].waitForTimeout(500);
//         }

//         Logger("✅ All tabs accessible");
//     });
// });

// // ============================================
// // PATTERNS REFERENCE
// // ============================================

// /**
//  * PATTERN SUMMARY:
//  *
//  * 1. LOGIN:
//  *    await base['loginPage'].loginAs(userRole.XXX);
//  *
//  * 2. NAVIGATION:
//  *    await base['pws_navMenuAsBuyer'].chooseNavMenu("Tab Name");
//  *    await base['pws_navMenuAsAdmin'].chooseNavMenu("Tab Name");
//  *
//  * 3. SHOP PAGE:
//  *    await base['pws_shopPage'].enterOfferData(numberOfSkus, priceMultiplier, qty);
//  *    await base['pws_shopPage'].sortAvlQty("ascending" | "descending");
//  *    await base['pws_shopPage'].selectMoreActionOption("Reset Offer");
//  *
//  * 4. CART PAGE:
//  *    await base['pws_CartPage'].getSummaryOffer();
//  *    await base['pws_CartPage'].isSubmitButtonEnabled();
//  *    await base['pws_CartPage'].clickSubmitButton();
//  *
//  * 5. LOGGING:
//  *    Logger("Descriptive message about current action");
//  *    Logger("✅ Success indicator");
//  *
//  * 6. WAITS:
//  *    await base['page'].waitForTimeout(ms);  // Use sparingly, prefer waitFor* methods
//  *    await base['page'].waitForLoadState('domcontentloaded');
//  *
//  * 7. ASSERTIONS:
//  *    expect(value).toBeTruthy();
//  *    expect(value).toBe(expected);
//  *    expect(value).toContain("substring");
//  */

```
