# Test Spec: OfferFlowTests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_WorkflowsTests\OfferFlowTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 390
- **Size**: 19,782 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { userRole } from '../../../utils/resources/enum';
import { BaseTest } from '../../BaseTest';

let base: BaseTest;
test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
});

test.describe('Sales Rep Can Review and Accept the Offer @pws-regression', () => {   //UserOne - 21839
    let offerID: string;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserOne);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(1, 500, 1);
        console.log(`Submitted Offer ID: ${offerID}`);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_One);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Accept All SKUs', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_One);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        await base['pws_OfferDetailsPage'].moreActionOption("Accept All");
        for (let i = 0; i < 1; i++) {
            const selected = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(i);
            expect(selected).toBe("Accept");
        }
    });

    test('Sales Rep Can Complete Review the Accepted Offer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_One, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Accepted Offer Should Appear in Ordered Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_One);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Ordered");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});


test.describe('Sales Rep Can Review and Decline the Offer', () => {  //UserFour - APWS04
    let offerID: string;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserFour);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(1, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Four);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Decline All SKUs', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Four);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        await base['pws_OfferDetailsPage'].moreActionOption("Decline All");
        for (let i = 0; i < 1; i++) {
            const selected = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(i);
            expect(selected).toBe("Decline");
        }
    });

    test('Sales Rep Can Complete Review the Declined Offer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_Four, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Declined Offer Should Appear in Declined Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Four);
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Declined");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});


test.describe('Sales Rep Can Review and Finalize the Offer', () => {  //UserTwo - 25199
    let offerID: string;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserTwo);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(1, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Two);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Finalize All SKUs', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Two);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        await base['pws_OfferDetailsPage'].moreActionOption("Finalize All");
        for (let i = 0; i < 1; i++) {
            const selected = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(i);
            expect(selected).toBe("Finalize");
        }
    });

    test('Sales Rep Can Complete Review the Finalized Offer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_Two, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Finalized Offer Should Appear in Ordered Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Two);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Ordered");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});


test.describe('Sales Rep Can Counteroffer --> Buyer Can Accept the Offer', () => {    //UserThree - 24763
    let offerID: string;
    let numberOfSKUs = 1;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserThree);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Three);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Counteroffer', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Three);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        for (let i = 0; i < numberOfSKUs; i++) {
            await base['pws_OfferDetailsPage'].salesActionEachSKU(i, "Counter");
            await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(i, "2999", "1");
        }
    });

    test('Sales Rep Can Complete Review the Counteroffer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_Three, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Counteroffer Should Appear in Buyer Acceptance Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Three);
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Buyer Acceptance");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
        await base['loginPage'].logoutFromPWS();
    });

    test('Buyer Can View the Counteroffer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserThree);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Counters");
        await base['pws_CounterOfferPage'].findAndClickOfferByID(offerID);
    });

    test('Buyer Can Accept the Counteroffer', async () => {
        await base['pws_CounterOfferPage'].selectCounterActionByRowIndex(0, "Accept");
        await base['pws_CounterOfferPage'].clickSubmitResponseButton();
        await base['pws_CounterOfferPage'].closeOfferResponseSubmittedModal();
        await base['loginPage'].logoutFromPWS();
    });

    test('The Accepted Counteroffer Should Appear in Ordered Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Three);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Ordered");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});


test.describe('Sales Rep Can Counteroffer --> Buyer Can Decline the Offer', () => {    //UserSix - APWS06
    let offerID: string;
    let numberOfSKUs = 1;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserSix);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Six);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Counteroffer', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Six);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        for (let i = 0; i < numberOfSKUs; i++) {
            await base['pws_OfferDetailsPage'].salesActionEachSKU(i, "Counter");
            await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(i, "2999", "1");
        }
    });

    test('Sales Rep Can Complete Review the Counteroffer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_Six, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Counteroffer Should Appear in Buyer Acceptance Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Six);
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Buyer Acceptance");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
        await base['loginPage'].logoutFromPWS();
    });

    test('Buyer Can View the Counteroffer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserSix);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Counters");
        await base['pws_CounterOfferPage'].findAndClickOfferByID(offerID);
    });

    test('Buyer Can Decline the Counteroffer', async () => {
        await base['pws_CounterOfferPage'].selectCounterActionByRowIndex(0, "Decline");
        await base['pws_CounterOfferPage'].clickSubmitResponseButton();
        await base['pws_CounterOfferPage'].closeOfferResponseSubmittedModal();
        await base['loginPage'].logoutFromPWS();
    });

    test('The Declined Counteroffer Should Appear in Ordered Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Six);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Declined");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});


test.describe('Sales Rep Can Counteroffer --> Buyer Can Cancel the Offer', () => {    //UserSeven - APWS07
    let offerID: string;
    let numberOfSKUs = 1;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserSeven);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Seven);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Counteroffer', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Seven);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        for (let i = 0; i < numberOfSKUs; i++) {
            await base['pws_OfferDetailsPage'].salesActionEachSKU(i, "Counter");
            await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(i, "2999", "1");
        }
    });

    test('Sales Rep Can Complete Review the Counteroffer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_Seven, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Counteroffer Should Appear in Buyer Acceptance Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Seven);
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Buyer Acceptance");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
        await base['loginPage'].logoutFromPWS();
    });

    test('Buyer Can View the Counteroffer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserSeven);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Counters");
        await base['pws_CounterOfferPage'].findAndClickOfferByID(offerID);
    });

    test('Buyer Can Cancel the Counteroffer', async () => {
        await base['pws_CounterOfferPage'].moreActionOption("Cancel Order");
        await base['page'].waitForTimeout(1000);
        await base['pws_CounterOfferPage'].cancelOrderModalAction('yes');
        await base['pws_CounterOfferPage'].closeOfferResponseSubmittedModal();
        await base['loginPage'].logoutFromPWS();
    });

    test('The Canceled Counteroffer Should Appear in Declined Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Seven);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Declined");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});


test.describe('Sales Rep Can Counteroffer --> Buyer Can Accept SKUs and Decline SKUs', () => {  //UserFive - 21999
    let offerID: string;
    let numberOfSKUs = 2;
    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserFive);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer Under Sales Review Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Five);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 7);
        expect(isOfferFound).toBeTruthy();
    });

    test('Sales Rep Can Select Counteroffer', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Five);
        await base['pws_OfferQueuePage'].findAndClickOfferByID(offerID, 7);
        for (let i = 0; i < numberOfSKUs; i++) {
            await base['pws_OfferDetailsPage'].salesActionEachSKU(i, "Counter");
            await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(i, "2999", "1");
        }
    });

    test('Sales Rep Can Complete Review the Counteroffer', async () => {
        await base['pws_OfferDetailsPage'].ensureUserOnOfferDetailsPage(base, userRole.ADMIN_Five, offerID);
        await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
        await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
    });

    test('Counteroffer Should Appear in Buyer Acceptance Tab', async () => {
        await base['pws_OfferQueuePage'].ensureUserOnOfferQueuePage(base, userRole.ADMIN_Five);
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Buyer Acceptance");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
        await base['loginPage'].logoutFromPWS();
    });

    test('Buyer Can View the Counteroffer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserFive);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Counters");
        await base['pws_CounterOfferPage'].findAndClickOfferByID(offerID);
    });

    test('Buyer Can Accept and Decline SKUs on the Counteroffer', async () => {
        await base['pws_CounterOfferPage'].selectCounterActionByRowIndex(0, "Accept");
        await base['pws_CounterOfferPage'].selectCounterActionByRowIndex(0, "Decline");
        await base['pws_CounterOfferPage'].clickSubmitResponseButton();
        await base['pws_CounterOfferPage'].closeOfferResponseSubmittedModal();
        await base['loginPage'].logoutFromPWS();
    });

    test('The Accepted/Declined SKUs Counteroffer Should Appear in Ordered Tab', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Five);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Ordered");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
        expect(isOfferFound).toBeTruthy();
    });
});

```
