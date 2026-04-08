# Test Spec: PWS_EmailCommunicationTests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_FunctionalTests\PWS_EmailCommunicationTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 89
- **Size**: 3,752 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';
import { TempMailPage } from '../../../pages/CommonPages/TempMailPage';

let base: BaseTest;
let tempMailPage: TempMailPage;
let offerID: string;
const offerSubmittedEmailSubject = "We received your Premium Wholesale offer!";
const counterOfferEmailSubject = "You have a Premium Wholesale counter offer";
const orderSubmittedEmailSubject = "Your Premium Wholesale order is being processed";

// ===============  NEED TO REVIEW AND FIX --  BUYER CODE NOT AVAILABLE ISSUE =================== //


// add this test file to run in Serial mode
test.beforeEach(async ({ browser }) => {
    const context = await browser.newContext();
    const page = await browser.newPage();
    base = new BaseTest(page);
    tempMailPage = new TempMailPage(context);
    await base.setup();
});

// test('SPKB-919 : Verify "Offer-Submitted" Email Sent to Buyer when Offer Submitted by Buyer ', async () => {
//     await base['loginPage'].loginAs(userRole.PWS_UserSeven);
//     offerID = await base['pws_CartPage'].submitOfferBelowListPrice(1, 0.5, 1);
//     const emailSubject = await tempMailPage.getEmailSubjectForCurrentUser("ecopws7");
//     expect(emailSubject).toContain(offerSubmittedEmailSubject);
// });

// test('SPKB-920, SPKB-1052 : Verify "Counter Offer" Email Sent to Buyer when Offer Countered by Sales Rep', async () => {    
//     await base['loginPage'].loginAs(userRole.ADMIN_Seven);
//     await base['pws_OfferDetailsPage'].navigateToOfferDetailsPage('APWS07', offerID, 7);
//     await base['pws_OfferDetailsPage'].salesActionEachSKU(0, "Counter");
//     await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(0, "2999", "1");
//     await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
//     const emailSubject = await tempMailPage.getEmailSubjectForCurrentUser("ecopws7");
//     expect(emailSubject).toContain(counterOfferEmailSubject);
// });

// test('SPKB-1053 : Verify Action Link on "Counter Offer" Email', async () => {
//     const isButtonWork = await tempMailPage.verifySeeCounterOfferButton("ecopws7");
//     expect(isButtonWork).toBeTruthy();
// });

// test('SPKB-921, SPKB-1054 : Verify "Order-Submitted" Email Sent to Buyer when All SKUs Accepted by Sales Rep', async () => {
//     await base['loginPage'].loginAs(userRole.ADMIN);
//     await base['navMenuPage'].chooseSubNav_UnderAdminMainNav('PWS Data Center');
//     await base['pws_dataCenter_OfferItemsPage'].clearOffersFromOfferQueue('21999');
// });

// test('SPKB-922 : Verify "Order-Submitted" Email Should Not Sent when All SKUs Declined by Sales Rep', async () => {

// });

// test('SPKB-923 : Verify "Order-Submitted" Email Sent to Buyer when At Least One SKU Accepted by Sales Rep', async () => {

// });

// test('SPKB-924 : Verify "Order-Submitted" Email Sent to Buyer when All SKUs Finalized by Sales Rep', async () => {

// });

// test('SPKB-925 : Verify "Order-Submitted" Email Sent to Buyer when At Least One SKU Finalized by Sales Rep', async () => {

// });

// test('SPKB-1450 : Verify "No Adjusted Qty" Order-Submitted Email Sent to Buyer when Submitted Order with All SKUs Below Avail Qty', async () => {

// });

// test('SPKB-1451 : Verify "Adjusted Qty" Order-Submitted Email Sent to Buyer when Submitted Order with Any SKU Above Avail Qty (100+)', async () => {

// });

// test('SPKB-1452 : Validate "Adjusted Qty" in the Email', async () => {

// });

// test('SPKB-1056 : Verify Email Template', async () => {

// });

// test.afterEach(async () => {
//     await base['pws_InventoryPage'].logoutFromPWS();
// });

```
