import { test, expect, Page } from '@playwright/test';
import { LoginPage, NavPage, ShopPage, CartPage, OfferReviewPage, OfferDetailPage, CounterOfferPage } from '../pages';

/**
 * Offer Flow E2E Tests — ported from Mendix PWS_WorkflowsTests/OfferFlowTests.spec.ts
 *
 * Tests the complete offer lifecycle:
 *   Buyer submits offer → SalesRep reviews → Accept/Decline/Finalize/Counter
 *   Counter flows: Buyer Accept / Buyer Decline / Buyer Cancel / Mixed Accept+Decline
 *
 * Original: 7 describe blocks, 47 tests
 * Modern rewrite: same business logic, modern app selectors
 */

let page: Page;
let loginPage: LoginPage;
let navPage: NavPage;
let shopPage: ShopPage;
let cartPage: CartPage;
let offerReviewPage: OfferReviewPage;
let offerDetailPage: OfferDetailPage;
let counterOfferPage: CounterOfferPage;

test.beforeAll(async ({ browser }) => {
  page = await browser.newPage();
  loginPage = new LoginPage(page);
  navPage = new NavPage(page);
  shopPage = new ShopPage(page);
  cartPage = new CartPage(page);
  offerReviewPage = new OfferReviewPage(page);
  offerDetailPage = new OfferDetailPage(page);
  counterOfferPage = new CounterOfferPage(page);
});

test.afterAll(async () => {
  await page.close();
});

// ────────────────────────────────────────────────────────────
// Flow 1: Accept All
// Mendix: Sales Rep Can Review and Accept the Offer
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Accept All @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;

  test('Buyer can submit offer below list price', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(1, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep can find offer under Sales Review tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 7);
    expect(found).toBeTruthy();
  });

  test('SalesRep can Accept All SKUs', async () => {
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await offerDetailPage.moreActionOption('Accept All');
    const action = await offerDetailPage.getSalesActionStatusByRowIndex(0);
    expect(action).toBe('Accept');
  });

  test('SalesRep can complete review of accepted offer', async () => {
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
  });

  test('Accepted offer appears in Ordered tab', async () => {
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Ordered');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// Flow 2: Decline All
// Mendix: Sales Rep Can Review and Decline the Offer
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Decline All @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;

  test('Buyer can submit offer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(1, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep can find offer under Sales Review tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 7);
    expect(found).toBeTruthy();
  });

  test('SalesRep can Decline All SKUs', async () => {
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await offerDetailPage.moreActionOption('Decline All');
    const action = await offerDetailPage.getSalesActionStatusByRowIndex(0);
    expect(action).toBe('Decline');
  });

  test('SalesRep can complete review of declined offer', async () => {
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
  });

  test('Declined offer appears in Declined tab', async () => {
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Declined');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// Flow 3: Finalize All
// Mendix: Sales Rep Can Review and Finalize the Offer
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Finalize All @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;

  test('Buyer can submit offer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(1, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep can find offer under Sales Review tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 7);
    expect(found).toBeTruthy();
  });

  test('SalesRep can Finalize All SKUs', async () => {
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await offerDetailPage.moreActionOption('Finalize All');
    const action = await offerDetailPage.getSalesActionStatusByRowIndex(0);
    expect(action).toBe('Finalize');
  });

  test('SalesRep can complete review of finalized offer', async () => {
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
  });

  test('Finalized offer appears in Ordered tab', async () => {
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Ordered');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// Flow 4: Counter → Buyer Accept
// Mendix: Sales Rep Can Counteroffer --> Buyer Can Accept the Offer
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Counter → Buyer Accept @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;
  const numberOfSKUs = 1;

  test('Buyer can submit offer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(numberOfSKUs, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep can find offer under Sales Review tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 7);
    expect(found).toBeTruthy();
  });

  test('SalesRep can select Counter for each SKU', async () => {
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    for (let i = 0; i < numberOfSKUs; i++) {
      await offerDetailPage.salesActionEachSKU(i, 'Counter');
      await offerDetailPage.enterCounterPriceAndQty(i, '2999', '1');
    }
  });

  test('SalesRep can complete review of counteroffer', async () => {
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
  });

  test('Counteroffer appears in Buyer Acceptance tab', async () => {
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Buyer Acceptance');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
    await loginPage.logout();
  });

  test('Buyer can view the counteroffer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Counters');
    await counterOfferPage.findAndClickOfferByID(offerID);
  });

  test('Buyer can accept the counteroffer', async () => {
    await counterOfferPage.selectCounterActionByRowIndex(0, 'Accept');
    await counterOfferPage.clickSubmitResponseButton();
    await counterOfferPage.closeOfferResponseSubmittedModal();
    await loginPage.logout();
  });

  test('Accepted counteroffer appears in Ordered tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Ordered');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// Flow 5: Counter → Buyer Decline
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Counter → Buyer Decline @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;

  test('Buyer can submit offer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(1, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep counters the offer', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await offerDetailPage.salesActionEachSKU(0, 'Counter');
    await offerDetailPage.enterCounterPriceAndQty(0, '2999', '1');
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
    await loginPage.logout();
  });

  test('Buyer can decline the counteroffer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Counters');
    await counterOfferPage.findAndClickOfferByID(offerID);
    await counterOfferPage.selectCounterActionByRowIndex(0, 'Decline');
    await counterOfferPage.clickSubmitResponseButton();
    await counterOfferPage.closeOfferResponseSubmittedModal();
    await loginPage.logout();
  });

  test('Declined counteroffer appears in Declined tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Declined');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// Flow 6: Counter → Buyer Cancel
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Counter → Buyer Cancel @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;

  test('Buyer can submit offer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(1, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep counters the offer', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await offerDetailPage.salesActionEachSKU(0, 'Counter');
    await offerDetailPage.enterCounterPriceAndQty(0, '2999', '1');
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
    await loginPage.logout();
  });

  test('Buyer can cancel the counteroffer', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Counters');
    await counterOfferPage.findAndClickOfferByID(offerID);
    await counterOfferPage.moreActionOption('Cancel Order');
    await counterOfferPage.cancelOrderModalAction('yes');
    await counterOfferPage.closeOfferResponseSubmittedModal();
    await loginPage.logout();
  });

  test('Canceled counteroffer appears in Declined tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Declined');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// Flow 7: Counter → Buyer Accept + Decline Mixed SKUs
// ────────────────────────────────────────────────────────────
test.describe('Offer Flow: Counter → Mixed Accept + Decline @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  let offerID: string;
  const numberOfSKUs = 2;

  test('Buyer can submit offer with multiple SKUs', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Shop');
    await shopPage.selectMoreActionOption('Reset');
    await shopPage.confirmReset();
    await shopPage.sortAvlQty('descending');
    await shopPage.enterOfferData(numberOfSKUs, 0.5, 1);
    await shopPage.clickCartButton();
    offerID = await cartPage.submitAndCaptureOrderID();
    expect(offerID).toBeTruthy();
    await loginPage.logout();
  });

  test('SalesRep counters all SKUs', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    for (let i = 0; i < numberOfSKUs; i++) {
      await offerDetailPage.salesActionEachSKU(i, 'Counter');
      await offerDetailPage.enterCounterPriceAndQty(i, '2999', '1');
    }
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();
    await loginPage.logout();
  });

  test('Buyer can accept first SKU and decline second', async () => {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Counters');
    await counterOfferPage.findAndClickOfferByID(offerID);
    await counterOfferPage.selectCounterActionByRowIndex(0, 'Accept');
    await counterOfferPage.selectCounterActionByRowIndex(1, 'Decline');
    await counterOfferPage.clickSubmitResponseButton();
    await counterOfferPage.closeOfferResponseSubmittedModal();
    await loginPage.logout();
  });

  test('Mixed counteroffer appears in Ordered tab', async () => {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await offerReviewPage.chooseOfferStatusTab('Ordered');
    const found = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(found).toBeTruthy();
  });
});
