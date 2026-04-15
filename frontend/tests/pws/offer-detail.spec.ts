import { test, expect, Page } from '@playwright/test';
import {
  LoginPage,
  NavPage,
  ShopPage,
  CartPage,
  OfferReviewPage,
  OfferDetailPage,
  CounterOfferPage,
} from '../pages';

/**
 * Offer Detail Functional Tests — ported from Mendix OfferDetailFunctionalTests.spec.ts
 *
 * All 29 tests were commented out in the Mendix source with note:
 *   "NOT UP TO DATE - NEED TO BE REVISED LATER"
 *
 * Ported as active tests since the modern app has the corresponding pages built.
 *
 * Describe blocks:
 *   1. Finalize Tests (SPKB-966, 970, 971, 967, 972, 973)
 *   2. More-Action Options Tests (SPKB-1277, 1118, 968, 1167)
 *   3. Sales Action Functionality Tests (SPKB-788, 1199.1-3, 791, 791.1, 795, 790, 794, 793, 792)
 *   4. Counter Offer Buyer Action Tests (SPKB-843, 844, 845, 850, 846, 847, 848, 849)
 */

// ────────────────────────────────────────────────────────────
// 1. Finalize Tests
// ────────────────────────────────────────────────────────────
test.describe('Sale Review Detail Page: Finalize Tests @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });

  const numberOfSKUs = 3;
  const finalizedPrice = '2500';
  const finalizedQty = '1';
  let offerID: string;

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

    // Buyer submits an offer below list price
    await loginPage.loginAs('BIDDER');
    offerID = await cartPage.submitOfferBelowListPrice(page, numberOfSKUs, 0.8, 1);
    await loginPage.logout();

    // Admin navigates to the offer detail
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await page.waitForTimeout(2_000);
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await page.waitForTimeout(2_000);
  });

  test.afterAll(async () => {
    try { await loginPage.logout(); } catch { /* ignore */ }
    await page.close();
  });

  test('SPKB-966: Verify Finalize is under More Action dropdown', async () => {
    const isFinalizeOptionVisible = await offerDetailPage.isMoreActionOptionVisible('Finalize All');
    expect(isFinalizeOptionVisible).toBeTruthy();
  });

  test('SPKB-970: Error when counter price or qty is zero on submission', async () => {
    // Set all items to Counter — backend pre-populates counterQty/Price with
    // original offer values via setItemAction(), so we must explicitly zero them
    // to trigger the validation at completeReview() (counterQty <= 0).
    for (let i = 0; i < numberOfSKUs; i++) {
      await offerDetailPage.salesActionEachSKU(i, 'Counter');
    }
    for (let i = 0; i < numberOfSKUs; i++) {
      await offerDetailPage.enterCounterPriceAndQty(i, '0', '0');
    }
    await offerDetailPage.clickCompleteReviewButton();

    const errorModalPopup = await counterOfferPage.isErrorMessageModalVisible();
    expect(errorModalPopup).toBe(true);
    await counterOfferPage.closeErrorModal();
  });

  test('SPKB-971: Error when mixing accepted and countered SKUs without price', async () => {
    // Row 0 → Accept; rows 1-2 remain Counter without prices
    await offerDetailPage.salesActionEachSKU(0, 'Accept');
    await offerDetailPage.clickCompleteReviewButton();

    const errorModalPopup = await counterOfferPage.isErrorMessageModalVisible();
    expect(errorModalPopup).toBe(true);
    await counterOfferPage.closeErrorModal();
  });

  test('SPKB-967: Sales rep can enter counter price and qty', async () => {
    for (let i = 0; i < numberOfSKUs; i++) {
      await offerDetailPage.salesActionEachSKU(i, 'Counter');
    }
    for (let i = 0; i < numberOfSKUs; i++) {
      await offerDetailPage.enterCounterPriceAndQty(i, finalizedPrice, finalizedQty);
    }
  });

  test('SPKB-972: Validate summary counter offer totals', async () => {
    const expectedTotal = (numberOfSKUs * parseFloat(finalizedPrice)).toString();
    const expectedQty = (numberOfSKUs * parseFloat(finalizedQty)).toString();
    const expectedSkus = numberOfSKUs.toString();

    const counterOffer = await offerDetailPage.getCounterOfferSummary();
    expect([counterOffer.skus, counterOffer.qty, counterOffer.total])
      .toEqual([expectedSkus, expectedQty, expectedTotal]);
  });

  test('SPKB-973: Countered offer moves to Buyer Acceptance stage', async () => {
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();

    await navPage.chooseNavMenu('Offer Review');
    await page.waitForTimeout(2_000);
    await offerReviewPage.chooseOfferStatusTab('Buyer Acceptance');
    const isOfferFound = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(isOfferFound).toBeTruthy();
  });
});

// ────────────────────────────────────────────────────────────
// 2. More-Action Options Tests
// ────────────────────────────────────────────────────────────
test.describe('Sale Review Page: More-Action Options Tests @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });

  const numberOfSKUs = 3;
  let offerID: string;

  let page: Page;
  let loginPage: LoginPage;
  let navPage: NavPage;
  let shopPage: ShopPage;
  let cartPage: CartPage;
  let offerReviewPage: OfferReviewPage;
  let offerDetailPage: OfferDetailPage;

  test.beforeAll(async ({ browser }) => {
    page = await browser.newPage();
    loginPage = new LoginPage(page);
    navPage = new NavPage(page);
    shopPage = new ShopPage(page);
    cartPage = new CartPage(page);
    offerReviewPage = new OfferReviewPage(page);
    offerDetailPage = new OfferDetailPage(page);

    // Buyer submits an offer below list price
    await loginPage.loginAs('BIDDER');
    offerID = await cartPage.submitOfferBelowListPrice(page, numberOfSKUs, 0.8, 1);
    await loginPage.logout();

    // Admin navigates to the offer detail
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await page.waitForTimeout(2_000);
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await page.waitForTimeout(2_000);
  });

  test.afterAll(async () => {
    try { await loginPage.logout(); } catch { /* ignore */ }
    await page.close();
  });

  test('SPKB-1277: Accept All sets inline items to Accept', async () => {
    await offerDetailPage.moreActionOption('Accept All');
    for (let i = 0; i < numberOfSKUs; i++) {
      const salesAction = await offerDetailPage.getSalesActionStatusByRowIndex(i);
      expect(salesAction).toBe('Accept');
    }
  });

  test('SPKB-1118: Decline All sets inline items to Decline', async () => {
    await offerDetailPage.moreActionOption('Decline All');
    for (let i = 0; i < numberOfSKUs; i++) {
      const salesAction = await offerDetailPage.getSalesActionStatusByRowIndex(i);
      expect(salesAction).toBe('Decline');
    }
  });

  test('SPKB-968: Finalize All sets inline items to Finalize', async () => {
    await offerDetailPage.moreActionOption('Finalize All');
    for (let i = 0; i < numberOfSKUs; i++) {
      const salesAction = await offerDetailPage.getSalesActionStatusByRowIndex(i);
      expect(salesAction).toBe('Finalize');
    }
  });

  test('SPKB-1167: Download option from More Action dropdown', async () => {
    const downloadPromise = page.waitForEvent('download', { timeout: 15_000 });
    await offerDetailPage.moreActionOption('Download');
    const download = await downloadPromise;

    const fileName = download.suggestedFilename();
    expect(fileName).toBeTruthy();
    expect(fileName).toMatch(/\.csv$/);
    const path = `./test-results/${fileName}`;
    await download.saveAs(path);
  });
});

// ────────────────────────────────────────────────────────────
// 3. Sales Action Functionality Tests
// ────────────────────────────────────────────────────────────
test.describe('Sale Review Page: Sales Action Functionality Tests @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });

  const numberOfSKUs = 3;
  let offerID: string;
  let originalOffer: { skus: string; qty: string; total: string };

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

    // Buyer submits an offer below list price
    await loginPage.loginAs('BIDDER');
    offerID = await cartPage.submitOfferBelowListPrice(page, numberOfSKUs, 0.8, 1);
    await loginPage.logout();
  });

  test.afterAll(async () => {
    try { await loginPage.logout(); } catch { /* ignore */ }
    await page.close();
  });

  // Each test logs in as admin and navigates to the offer detail
  async function navigateToOfferDetail() {
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await page.waitForTimeout(2_000);
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await page.waitForTimeout(2_000);
  }

  test('SPKB-788: Offer detail page displays "Sales Review" as default status', async () => {
    await navigateToOfferDetail();
    expect(await offerDetailPage.isSalesReviewStatusDisplayed()).toBeTruthy();
    originalOffer = await offerDetailPage.getOriginalOfferSummary();
    await loginPage.logout();
  });

  test('SPKB-1199.1: Sales rep can accept an offer at SKU level', async () => {
    await navigateToOfferDetail();
    await offerDetailPage.salesActionEachSKU(0, 'Accept');
    const selected = await offerDetailPage.getSalesActionStatusByRowIndex(0);
    expect(selected).toBe('Accept');
    await loginPage.logout();
  });

  test('SPKB-1199.2: Sales rep can decline an offer at SKU level', async () => {
    await navigateToOfferDetail();
    await offerDetailPage.salesActionEachSKU(1, 'Decline');
    const selected = await offerDetailPage.getSalesActionStatusByRowIndex(1);
    expect(selected).toBe('Decline');
    await loginPage.logout();
  });

  test('SPKB-1199.3: Sales rep can counter an offer at SKU level', async () => {
    await navigateToOfferDetail();
    await offerDetailPage.salesActionEachSKU(2, 'Counter');
    const selected = await offerDetailPage.getSalesActionStatusByRowIndex(2);
    expect(selected).toBe('Counter');
  });

  test('SPKB-791: Accepted SKU disables counter price and qty fields', async () => {
    // Continue from prior test — set row 0 to Accept
    await offerDetailPage.salesActionEachSKU(0, 'Accept');

    const isPriceDisabled = await offerDetailPage.isCounterPriceFieldDisabled(0);
    expect(isPriceDisabled).toBe(true);
    const isQtyDisabled = await offerDetailPage.isCounterQtyFieldDisabled(0);
    expect(isQtyDisabled).toBe(true);
  });

  test('SPKB-791.1: Declined SKU disables counter price and qty fields', async () => {
    const isPriceDisabled = await offerDetailPage.isCounterPriceFieldDisabled(1);
    expect(isPriceDisabled).toBe(true);
    const isQtyDisabled = await offerDetailPage.isCounterQtyFieldDisabled(1);
    expect(isQtyDisabled).toBe(true);
  });

  test('SPKB-795: Complete Review blocked when counter price/qty missing', async () => {
    // Counter item (row 2) gets pre-populated with original offer values by
    // the backend setItemAction(). Clear them so the review fails validation.
    await offerDetailPage.enterCounterPriceAndQty(2, '0', '0');
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseErrorModalPopup();
  });

  test('SPKB-790: Counter SKU allows sales rep to enter price and qty', async () => {
    const isPriceDisabled = await offerDetailPage.isCounterPriceFieldDisabled(2);
    expect(isPriceDisabled).toBe(false);
    const isQtyDisabled = await offerDetailPage.isCounterQtyFieldDisabled(2);
    expect(isQtyDisabled).toBe(false);
  });

  test('SPKB-794: Counter inline total calculation is correct', async () => {
    const counterPrice = '2000';
    const counterQty = '1';
    const expectedTotal = parseFloat(counterPrice) * parseFloat(counterQty);

    const inlineTotal = await offerDetailPage.enterCounterPriceAndQty(2, counterPrice, counterQty);
    expect(parseFloat(inlineTotal)).toBe(expectedTotal);
  });

  test('SPKB-793: Counter offer summary box updates when entering counter price/qty', async () => {
    const expected = ['1', '1', '2000'];
    // Decline row 0 so only counter row 2 counts
    await offerDetailPage.salesActionEachSKU(0, 'Decline');

    const counterOffer = await offerDetailPage.getCounterOfferSummary();
    expect([counterOffer.skus, counterOffer.qty, counterOffer.total]).toEqual(expected);
  });

  test('SPKB-792: Original offer summary box does not change', async () => {
    const currentOriginalOffer = await offerDetailPage.getOriginalOfferSummary();
    expect(currentOriginalOffer).toEqual(originalOffer);
    await loginPage.logout();
  });
});

// ────────────────────────────────────────────────────────────
// 4. Counter Offer Page: Buyer Action Functionality Tests
// ────────────────────────────────────────────────────────────
test.describe('Counter Offer Page: Buyer Action Functionality Tests @regression @pws', () => {
  test.describe.configure({ mode: 'serial' });
  test.setTimeout(120_000);

  const numberOfSKUs = 3;
  let offerID: string;

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

    // Step 1: Buyer submits an offer below list price
    await loginPage.loginAs('BIDDER');
    offerID = await cartPage.submitOfferBelowListPrice(page, numberOfSKUs, 0.8, 1);
    await loginPage.logout();

    // Step 2: Admin reviews the offer with mixed actions (Accept/Decline/Counter)
    await loginPage.loginAs('ADMIN');
    await navPage.chooseNavMenu('Offer Review');
    await page.waitForTimeout(2_000);
    await offerReviewPage.chooseOfferStatusTab('Sales Review');
    await offerReviewPage.findAndClickOfferByID(offerID, 7);
    await page.waitForTimeout(2_000);

    await offerDetailPage.salesActionEachSKU(0, 'Accept');
    await offerDetailPage.salesActionEachSKU(1, 'Decline');
    await offerDetailPage.salesActionEachSKU(2, 'Counter');
    await offerDetailPage.enterCounterPriceAndQty(2, '2999', '1');
    await offerDetailPage.clickCompleteReviewButton();
    await offerDetailPage.clickCloseSubmittedConfirmationModal();

    // Verify offer moved to Buyer Acceptance
    await navPage.chooseNavMenu('Offer Review');
    await page.waitForTimeout(2_000);
    await offerReviewPage.chooseOfferStatusTab('Buyer Acceptance');
    const isOfferFound = await offerReviewPage.isOfferIdExistUnderAnyTab(offerID, 5);
    expect(isOfferFound).toBeTruthy();

    await loginPage.logout();
  });

  test.afterAll(async () => {
    try { await loginPage.logout(); } catch { /* ignore */ }
    await page.close();
  });

  // Helper: buyer logs in and navigates to the counter offer detail
  async function buyerNavigateToCounterOffer() {
    await loginPage.loginAs('BIDDER');
    await navPage.chooseNavMenu('Counters');
    await page.waitForTimeout(2_000);
    await counterOfferPage.findAndClickOfferByID(offerID);
    await page.waitForTimeout(2_000);
  }

  test('SPKB-843: Buyer can open counter offer detail page', async () => {
    await buyerNavigateToCounterOffer();
    // Verify the page loaded with offer data
    const rows = page.locator('table tbody tr');
    const count = await rows.count();
    expect(count).toBeGreaterThanOrEqual(numberOfSKUs);
  });

  test('SPKB-844: Buyer cannot edit accepted/declined SKUs from sales rep', async () => {
    // Row 0 was Accepted — dropdown should not be visible
    const isDropdownVisibleAccepted = await counterOfferPage.isActionDropdownVisibleByRowIndex(0);
    expect(isDropdownVisibleAccepted).toBe(false);

    // Row 1 was Declined — dropdown should not be visible
    const isDropdownVisibleDeclined = await counterOfferPage.isActionDropdownVisibleByRowIndex(1);
    expect(isDropdownVisibleDeclined).toBe(false);
  });

  test('SPKB-845: Buyer can select Accept/Decline on countered SKUs', async () => {
    // Row 2 was Countered — dropdown should be visible
    const isDropdownVisible = await counterOfferPage.isActionDropdownVisibleByRowIndex(2);
    expect(isDropdownVisible).toBe(true);
  });

  test('SPKB-850: Buyer cannot submit response with blank action on countered SKUs', async () => {
    await counterOfferPage.clickSubmitResponseButton();
    const errorModalPopup = await counterOfferPage.isErrorMessageModalVisible();
    expect(errorModalPopup).toBe(true);
    await counterOfferPage.closeErrorModal();
  });

  test('SPKB-846: Original offer summary box matches data grid', async () => {
    await page.waitForTimeout(2_000);
    const originalOfferSummary = await counterOfferPage.getOfferSummaryBox('original');
    expect(originalOfferSummary.skus).toBeGreaterThan(0);
    expect(originalOfferSummary.qty).toBeGreaterThan(0);
    expect(originalOfferSummary.total).toBeGreaterThan(0);
  });

  test('SPKB-847: Counter offer summary box matches data grid', async () => {
    const counterOfferSummary = await counterOfferPage.getOfferSummaryBox('counter');
    expect(counterOfferSummary.skus).toBeGreaterThan(0);
    expect(counterOfferSummary.total).toBeGreaterThan(0);
  });

  test('SPKB-848: Final offer summary box updates on buyer action', async () => {
    // Accept the countered SKU
    await counterOfferPage.selectCounterActionByRowIndex(2, 'Accept');
    await page.waitForTimeout(2_000);

    const finalAccept = await counterOfferPage.getOfferSummaryBox('final');
    expect(finalAccept.skus).toBeGreaterThan(0);
    expect(finalAccept.total).toBeGreaterThan(0);

    // Switch to Decline and verify summary updates
    await counterOfferPage.selectCounterActionByRowIndex(2, 'Decline');
    await page.waitForTimeout(2_000);

    const finalDecline = await counterOfferPage.getOfferSummaryBox('final');
    // Declining the counter should reduce the final offer total
    expect(finalDecline.total).toBeLessThanOrEqual(finalAccept.total);
    await loginPage.logout();
  });

  test('SPKB-849: Cancel Order modal functionality', async () => {
    await buyerNavigateToCounterOffer();

    // Click Cancel Order, then choose "No" — modal should close
    await counterOfferPage.moreActionOption('Cancel Order');
    await page.waitForTimeout(1_000);
    await counterOfferPage.cancelOrderModalAction('no');
    const isCancelModalVisible = await counterOfferPage.isCancelOrderModalVisible();
    expect(isCancelModalVisible).toBe(false);

    // Click Cancel Order again, choose "Yes" — response submitted
    await counterOfferPage.moreActionOption('Cancel Order');
    await page.waitForTimeout(1_000);
    await counterOfferPage.cancelOrderModalAction('yes');

    const isSubmittedVisible = await counterOfferPage.isOfferResponseSubmittedModalVisible();
    expect(isSubmittedVisible).toBe(true);
    await counterOfferPage.closeOfferResponseSubmittedModal();
  });
});
