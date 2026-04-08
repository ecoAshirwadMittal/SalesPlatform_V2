# Test Spec: OrderSubmissionTests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_WorkflowsTests\OrderSubmissionTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 144
- **Size**: 7,311 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { navTabs, userRole } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';
import user_data from '../../../utils/resources/user_data.json';
import { TempMailPage } from '../../../pages/CommonPages/TempMailPage';
import { PWS_NavMenu_AsAdmin } from '../../../pages/PWS/AdminPages/PWS_NavMenu_AsAdmin';

let base: BaseTest;
let tempMailPage: TempMailPage;

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    const context = await browser.newContext();
    tempMailPage = new TempMailPage(context);
    await base.setup();
})

test.describe('PWS Order Submission Tests @pws-regression', () => {
    test.describe.configure({ mode: 'serial' });
    //SPKB-645, SPKB-1255
    test('SPKB-1255: Verify Submit Button Disabled When Cart Empty', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserSix);
        await base['pws_shopPage'].selectMoreActionOption("Reset Offer");
        await base['pws_shopPage'].clickCartButton();
        expect(await base['pws_CartPage'].isCartPageDisplayed()).toBeTruthy();
        let isSubmitButtonOnCartPageEnable = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitButtonOnCartPageEnable).toBeFalsy();
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
    });

    test('SPKB-1255: Verify Submit Button Enabled when Cart Has Items', async () => {
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserSix);
        await base['pws_shopPage'].sortAvlQty("descending");
        await base['pws_shopPage'].enterOfferData(1, 1500, 1);
        await base['pws_shopPage'].clickCartButton();
        let isSubmitButtonOnCartPageEnable = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitButtonOnCartPageEnable).toBeTruthy();
    });

    //SPKB-646, 894, 1282 1556
    test('SPKB-1556: Verify In-Cart Summary - SKUs, Qty, Total on Cart Page', async () => {
        await base['pws_CartPage'].ensureUserOnCartPage(base, userRole.PWS_UserSix);
        const summary = await base['pws_CartPage'].getSummaryOffer()
        const SKU = summary[0];
        const Qty = summary[1];
        const totalAmount = summary[2];
        expect(SKU).toBe("1");
        expect(Qty).toBe("1");
        expect(totalAmount).toBe("1500");
    });

    test('SPKB-1224: Verify Display of Buyer Code and Buyer Name on Cart Page', async () => {
        await base['pws_CartPage'].ensureUserOnCartPage(base, userRole.PWS_UserSix);
        //Buyer Code is Not Displaying for Single BuyerCode User
        const actualBuyerInfo = await base['pws_CartPage'].getBuyerFromViewAs();
        expect(actualBuyerInfo).toBe('');
    });

    //SPKB-1256, SPKB-1258, SPKB-1297, SPKB-1283, SPKB-698
    test('SPKB-1256: Verify Order Confirmation Modal Popup on Submission', async () => {
        await base['pws_CartPage'].ensureUserOnCartPage(base, userRole.PWS_UserSix);
        await base['pws_CartPage'].clickSubmitButton();
        await base['page'].waitForTimeout(2000);
        const isThankYouModalPopup = await base['pws_CartPage'].isSubmittedConfirmationModalDisplayed();
        await base['page'].waitForTimeout(2000);
        expect(isThankYouModalPopup).toBeTruthy();
    });

    test('SPKB-700: Verify Cart is Cleared after Order Submission', async () => {
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserSix);
        await base['pws_shopPage'].clickCartButton();
        expect(await base['pws_CartPage'].isCartPageDisplayed()).toBeTruthy();
        Logger('Verifying Submit Button Should Be Disable')
        let isSubmitButtonOnCartPageEnable = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitButtonOnCartPageEnable).toBeFalsy();
        await base['loginPage'].logoutFromPWS();
    });

    //SPKB-699, SPKB-703, SPKB-1256
    test('SPKB-699: Verify the Order Shows under Offer-Queue ORDERED tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Six);
        await base['page'].waitForTimeout(3000);
        await base['pws_navMenuAsAdmin'].chooseNavMenu('Offer Review');
        await base['page'].waitForTimeout(3000);
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Ordered");
        await base['page'].waitForTimeout(3000);
        const isExist = await base['pws_OfferQueuePage'].isOrderIdExistUnderOrderedTab(user_data.PWS_UserSix.buyer_code, 7);
        await base['page'].waitForTimeout(2000);
        expect(isExist).toBeTruthy();
    });

    //SPKB-1259, SPKB-919
    test('SPKB-1259: Verify Order-Submitted Email Sent to Buyer', async () => {
        const emailSubject = await tempMailPage.getEmailSubjectForCurrentUser("ecopws6");
        expect(emailSubject).toContain("Your Premium Wholesale order is being processed");
    });

    test('SPKB-1450: Validate SKUs, Qty, Total in the Email', async () => {
        const totalAmountInEmail = await tempMailPage.getTotalAmountFromEmail('ecopws6');
        console.log(`Total Amount on Cart Page: 1500, Total Amount in Email: ${totalAmountInEmail}`);
        expect(totalAmountInEmail).toBe("1500");
    });
});


test.describe('PWS Order Submission with EXCEED Avail-Qty Tests', () => {
    test.describe.configure({ mode: 'serial' });
    test('SPKB-1295: Verify Red Hightlighed on Exceed Qty', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserSix);
        await base['pws_shopPage'].selectMoreActionOption("Reset Offer");
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
        await base['pws_shopPage'].sortAvlQty("ascending");
        await base['pws_shopPage'].enterOfferData(1, 1500, 100);
        await base['pws_shopPage'].clickCartButton();
        let isQtyExceedMessageDisplayed = await base['pws_CartPage'].isQtyExceedMessageVisible();
        expect(isQtyExceedMessageDisplayed).toBeTruthy();
    });

    test('SPKB-1296: Verify Submission Blocked on Exceed Qty Order', async () => {
        await base['pws_CartPage'].ensureUserOnCartPage(base, userRole.PWS_UserSix);
        let isSubmitButtonOnCartPageEnable = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitButtonOnCartPageEnable).toBeFalsy();
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
    });

    test('SPKB-1298: Verify Buyer Can Submit Order with Exceed Avail Qty 100+', async () => {
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserSix);
        await base['pws_shopPage'].selectMoreActionOption("Reset Offer");
        await base['pws_shopPage'].sortAvlQty("descending");
        await base['page'].waitForTimeout(2000);
        await base['pws_shopPage'].enterOfferData(1, 9, 10000);
        await base['pws_shopPage'].clickCartButton();
        await base['pws_CartPage'].clickSubmitButton();
        await base['pws_CartPage'].clickAlmostDoneSubmitButton();
    });

    test('SPKB-1451: Verify Adjusted-Qty Sent to Buyer', async () => {
        const emailSubject = await tempMailPage.getEmailSubjectForCurrentUser("ecopws6");
        expect(emailSubject).toContain("We received your Premium Wholesales offer!");
    });
});

```
