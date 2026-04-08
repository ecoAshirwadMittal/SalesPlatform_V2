# Test Spec: DeposcoShipOrder.spec.ts

- **Path**: `src\tests\SP_WMS_Deposco_Tests\DeposcoShipOrder.spec.ts`
- **Category**: Test Spec
- **Lines**: 73
- **Size**: 2,914 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../BaseTest';
import { Logger } from '../../utils/helpers/commonMethods';
import deposcoTestData from '../../utils/resources/deposco_test_data.json';

/**
 * Deposco Ship Order - Build and Release Wave
 * 
 * Single test that executes the complete workflow:
 * 1. Login to Deposco
 * 2. Navigate to Orders
 * 3. Search and open the order
 * 4. Execute Post Order Import
 * 5. Execute Build and Release Wave
 * 6. Refresh and verify
 */
test.describe("Deposco Ship Order", () => {

    // Dynamic test data - change order number here or in deposco_test_data.json
    const { username, password } = deposcoTestData.deposco.credentials.default;
    const orderNumber = deposcoTestData.orderNumbers.shipOrder_single;

    test(`Build and Release Wave for Order ${orderNumber}`, async ({ browser }) => {
        // Setup
        const page = await browser.newPage();
        const base = new BaseTest(page);

        try {
            // Step 1: Navigate to Deposco and Login
            Logger("Step 1: Navigate to Deposco and Login");
            await base['deposcoLoginPage'].navigateAndLogin(username, password);
            Logger("Successfully logged into Deposco");

            // Step 2: Navigate to Orders page
            Logger("Step 2: Navigate to Orders page");
            await base['deposcoOrdersPage'].navigateToOrdersPage();
            Logger("Orders page loaded");

            // Step 3: Search for the order
            Logger(`Step 3: Search for order ${orderNumber}`);
            await base['deposcoOrdersPage'].searchForOrder(orderNumber);
            Logger("Order search completed");

            // Step 4: Click on the order to open details
            Logger(`Step 4: Open order ${orderNumber}`);
            await base['deposcoOrdersPage'].clickOnSearchResult(orderNumber);
            Logger("Order details page opened");

            // Step 5: Execute Post Order Import
            Logger("Step 5: Execute Post Order Import action");
            await base['deposcoOrdersPage'].executePostOrderImport();
            Logger("Post Order Import completed");

            // Step 6: Execute Build and Release Wave
            Logger("Step 6: Execute Build and Release Wave action");
            await base['deposcoOrdersPage'].executeBuildAndReleaseWave();
            Logger("Build and Release Wave completed");

            // Step 7: Refresh and verify
            Logger("Step 7: Refresh screen and verify order");
            await base['deposcoOrdersPage'].refreshPage();
            await base['deposcoOrdersPage'].verifyOrderVisible(orderNumber);
            Logger("Screen refreshed and order verified");

            Logger(`✅ Successfully completed Build and Release Wave for order ${orderNumber}`);

        } finally {
            // Cleanup
            await page.close();
        }
    });
});

```
