# Test Spec: DeposcoIntegration.spec.ts

- **Path**: `src\tests\SP_WMS_Deposco_Tests\DeposcoIntegration.spec.ts`
- **Category**: Test Spec
- **Lines**: 49
- **Size**: 1,760 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../BaseTest';
import { Logger } from '../../utils/helpers/commonMethods';
import deposcoTestData from '../../utils/resources/deposco_test_data.json';

/**
 * Deposco Integration Test
 * 
 * Single test that navigates to Deposco and opens an order:
 * 1. Login to Deposco
 * 2. Navigate to Orders
 * 3. Search and open the order
 */
test.describe("Deposco Integration", () => {

    // Dynamic test data - change order number in deposco_test_data.json
    const { username, password } = deposcoTestData.deposco.credentials.default;
    const orderNumber = deposcoTestData.orderNumbers.integration_single;

    test(`Navigate to Order ${orderNumber}`, async ({ browser }) => {
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

            // Step 3: Search and open the order
            Logger(`Step 3: Search and open order ${orderNumber}`);
            await base['deposcoOrdersPage'].searchAndOpenOrder(orderNumber);
            Logger(`Order ${orderNumber} details loaded`);

            Logger(`✅ Successfully navigated to order ${orderNumber}`);

        } finally {
            // Cleanup
            await page.close();
        }
    });
});

```
