# Test Spec: DataGrid_Round3.spec.ts

- **Path**: `src\tests\Wholesales\DataGrid_Round3.spec.ts`
- **Category**: Test Spec
- **Lines**: 41
- **Size**: 1,861 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../BaseTest';
import { userRole } from '../../utils/resources/enum';
import { validateDuplication, isDownloaded, Logger, modifyBidsExcelSheet } from '../../utils/helpers/data_utils';
import user_data from '../../utils/resources/user_data.json';

let base: BaseTest;
test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
});


// ----------------------------  Between R2 - R3 Tests ----------------------------
test.describe.serial('Auction Between R2 - R3 Tests', () => {
    test('R2/3: Verify Buyer Can NOT Access R1', async () => {
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        const isDataGridVisible = await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(5000);
        expect(isDataGridVisible).toBeFalsy();
        Logger("Buyer can NOT access R1 after it ends.");
        await base['loginPage'].logout();
    });

    test('R2/3: Verify Sales Can Access R1', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA157WHL");
        const isDataGridVisible = await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        expect(isDataGridVisible).toBeTruthy();
        Logger("Sales can access to R1 after it ends.");
    });

    test('R2/3: Verify Sales Can Submit Bids on Behalf of Buyer', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA154WHL", userRole.ADMIN);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" },
            { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]);
    });
});

```
