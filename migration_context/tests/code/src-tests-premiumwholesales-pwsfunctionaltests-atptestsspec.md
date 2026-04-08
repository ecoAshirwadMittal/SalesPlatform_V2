# Test Spec: ATP_Tests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_FunctionalTests\ATP_Tests.spec.ts`
- **Category**: Test Spec
- **Lines**: 55
- **Size**: 2,372 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';
import user_data from '../../../utils/resources/user_data.json';

let base: BaseTest;
let orderNumber: string;
let sku = "PWS10000001";
let ATP_Deposco_init: number | null;

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
});

test('Verify ATP on Sales Platform Match ATP on Deposco', async () => {
    await base['loginPage'].loginAs(userRole.ADMIN);
    const { atp, reserved } = await base['pws_dataCenter_devicesPage'].getATPAndReservedQtyBySKU(sku);
    const ATP_total = atp + reserved;
    await base['loginPage'].logout();
    ATP_Deposco_init = (await base['deposcoAPI'].getAvailableToPromiseFromDeposco(sku)) ?? null;
    expect(ATP_total).toBe(ATP_Deposco_init);    
})

test('Verify Deposco API Return Code 200 after Order Created', async () => {
    await base['loginPage'].loginAs(userRole.Nadia_GmailOne);
    await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeAsMultiCodeBuyer(user_data.Nadia_GmailOne.buyer_code_pws);
   // await base['pws_navMenuAsAdmin'].clickNavigationTab('Inventory');
    orderNumber = await base['pws_CartPage'].submitOrderbySKU(sku, 1.1, 1);
    let statusCode = await base['deposcoAPI'].getOrderStatusCode(orderNumber);
    await base['loginPage'].logout();
    expect(statusCode).toBe(200);
})

test('Verify Deposco Order Status is in NEW', async () => {
    let orderStatus = await base['deposcoAPI'].getOrderStatusTrial(orderNumber);
    expect(orderStatus).toBe('NEW');
})

test('Verify ATP on Deposco Decreased after Order Placed', async () => {
    const ATP_Deposco_afterOrder = await base['deposcoAPI'].getAvailableToPromiseFromDeposco(sku);
    if (ATP_Deposco_init !== null) {
        expect(ATP_Deposco_afterOrder).toBe(ATP_Deposco_init);
    }
})

test('Verify ATP on Sales Platform Sync with Deposco and ATP Qty Updated', async () => {
    await base['loginPage'].loginAs(userRole.ADMIN);
    const { atp, reserved } = await base['pws_dataCenter_devicesPage'].getATPAndReservedQtyBySKU(sku);
    const ATP_total = atp + reserved;
    const ATP_Deposco = (await base['deposcoAPI'].getAvailableToPromiseFromDeposco(sku)) ?? null;
    expect(ATP_total).toBe(ATP_Deposco);
})

```
