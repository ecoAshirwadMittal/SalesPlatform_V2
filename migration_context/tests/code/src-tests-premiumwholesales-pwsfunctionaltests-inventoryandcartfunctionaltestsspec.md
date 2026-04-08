# Test Spec: InventoryAndCartFunctionalTests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_FunctionalTests\InventoryAndCartFunctionalTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 362
- **Size**: 17,862 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { navTabs, userRole } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';
import user_data from '../../../utils/resources/user_data.json';
import { uploadFile } from '../../../utils/helpers/data_utils';

let base: BaseTest;

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    const context = await browser.newContext();
    await base.setup();
})

test.describe('Inventory Page Functional Tests @pws-regression', () => {
    test.describe.configure({ mode: 'serial' });
    let offerID: string;
    let downloadedOfferFilePath: string; // <-- define variable in test scope
    let downloadedFileName: string;
    let expectedRowsData: Array<{ price: number, qty: number, total: number }>;

    test('SPKB-1082,SPKB-965: Verify PWS Buyer Can Create Offer and Total is Calculated Correctly/OfferId validation', async () => {
        Logger("Validating PWS Buyer Can Create Offer and Total is Calculated Correctly");
        await base['loginPage'].loginAs(userRole.PWS_UserOne);
        // Wait for the page to be in a ready state
        await base['page'].waitForLoadState('domcontentloaded');
        await base['page'].waitForTimeout(2000);
        // Verify the inventory table is displayed
        const isTableVisible = await base['pws_shopPage'].verifyAndLogPwsDataGridTableisplay();
        Logger("PWS Inventory table is displayed");
        expect(isTableVisible).toBeTruthy();
        // Enter offer price and quantity for multiple SKUs
        const numberOfSkus = 3;
        const offerData = await base['pws_shopPage'].enterOfferData(numberOfSkus, 0.8, 1);
        Logger(`Entered offer data for ${offerData.length} SKUs`);
        // Wait for the total to be calculated
        await base['page'].waitForTimeout(1000);
        // Verify each row
        for (let i = 0; i < numberOfSkus; i++) {
            // Get the row data to verify the total
            const rowData = await base['pws_shopPage'].getRowData(i);
            Logger(`Row ${i + 1} data: Price=${rowData.price}, Qty=${rowData.qty}, Total=${rowData.total}`);
            // Calculate expected total
            const expectedTotal = offerData[i].price * offerData[i].qty;
            Logger(`Row ${i + 1} expected total: ${expectedTotal} (${offerData[i].price} × ${offerData[i].qty})`);
            // Verify that the total is calculated correctly
            expect(rowData.total).toBe(expectedTotal);
            Logger(`Row ${i + 1} total value is correct: ${rowData.total}`);
        }
        Logger("PWS Buyer successfully created offers and totals were calculated correctly");
        // Click My Offer button
        await base['pws_shopPage'].clickCartButton();
        Logger("Clicked My Offer button");
        await base['pws_CartPage'].clickAlmostDoneSubmitButton();
        // Wait for the My Offer page to load
        await base['page'].waitForTimeout(1000);
        // Click Submit Offer button
        await base['pws_CartPage'].clickSubmitButton();
        Logger("Clicked Submit Offer button");
        // Wait for the offer submission to complete
        await base['page'].waitForTimeout(2000);
        const buyerCode = user_data.PWS_UserOne.buyer_code;
        // Get and validate the offer number
        const offerValidation = await base['pws_shopPage'].getAndValidateOfferNumber(buyerCode);
        Logger(`Offer submitted with number: ${offerValidation.offerNumber}`);
        Logger(`Validation details: 
        - Buyer code: Expected "${buyerCode}", got "${offerValidation.extractedBuyerCode}"
        - Year: Expected current year, got "${offerValidation.extractedYear}"
        - Sequence: Got "${offerValidation.extractedSequence}"`);
        // Verify that a valid offer number was generated
        expect(offerValidation.offerNumber).not.toBe("Unknown");
        expect(offerValidation.isValid).toBeTruthy();
        expect(offerValidation.buyerCodeValid).toBeTruthy();
        expect(offerValidation.yearValid).toBeTruthy();
        expect(offerValidation.sequenceValid).toBeTruthy();
        Logger(`Offer number ${offerValidation.offerNumber} successfully validated with pattern: ${buyerCode}<yy><SeqNumberXXX>`);
    });

    test('SPKB-648: Verify PWS Buyer is able to download offer file', async () => {
        Logger("Validating PWS Buyer is able to download offer file");
        await base['loginPage'].loginAs(userRole.PWS_UserOne);
        const numberOfRows = 3;
        const offerData = await base['pws_shopPage'].enterOfferData(numberOfRows, 0.8, 5);
        Logger(`Entered offer data for ${offerData.length} SKUs`);
        // Store all rows data for verification in the next test
        const rowsData = [];
        for (let i = 0; i < numberOfRows; i++) {
            const rowData = await base['pws_shopPage'].getRowData(i);
            rowsData.push(rowData);
            Logger(`Row ${i + 1} data: Price=${rowData.price}, Qty=${rowData.qty}, Total=${rowData.total}`);
        }
        expectedRowsData = rowsData;
        await base['pws_shopPage'].selectMoreActionOption('Download Offer');
        // Capture the actual filename from download
        const [download] = await Promise.all([
            base['page'].waitForEvent('download'),
            base['page'].locator('.mx-name-actionButton20').click()
        ]);
        downloadedFileName = download.suggestedFilename();
        const filePath = `./src/test-data/${downloadedFileName}`;
        await download.saveAs(filePath);
        Logger(`PWS Buyer successfully downloaded offer file: ${downloadedFileName}`);
        // Log out to clean up session
        await base['loginPage'].logout();
    });

    test('SPKB-528: Verify SalesRep can upload valid offer file', async () => {
        test.skip(!downloadedFileName, 'Download test failed, skipping upload test');
        Logger("Validating SalesRep can upload valid offer file");
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].chooseMainNav(navTabs.BidAsBider);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin('AAPW');
        // await base['pws_InventoryPage'].selectMoreActionOption('');
        // await base['pws_InventoryPage'].clickUploadOfferButton();
        // Use uploadFile from data_utils
        await uploadFile(base['page'], "//input[@type='file']", downloadedFileName);
        // Click upload button after file selection
        await base['page'].locator("button.btn.mx-button.mx-name-actionButton1.pws-button-primary.btn-lg.btn-block.btn-primary").click();
        await base['page'].waitForTimeout(3000);
        // Verify success message is displayed
        const successMessage = await base['page'].locator("text=Your file was successfully uploaded!").waitFor({ state: 'visible', timeout: 10000 });
        Logger("Upload success message verified");
        // Close the popup
        await base['page'].locator("span.mx-icon-lined.mx-icon-remove").click();
        await base['page'].waitForTimeout(5000);
        Logger("Closed the success message popup");
        const isTableVisible = await base['pws_shopPage'].verifyAndLogPwsDataGridTableisplay("SalesRep can see PWS Inventory table");
        expect(isTableVisible).toBeTruthy();
        // Verify all rows were updated correctly
        for (let i = 0; i < expectedRowsData.length; i++) {
            const offerData = await base['pws_shopPage'].getRowData(i);
            Logger(`Row ${i + 1} data: Price=${offerData.price}, Qty=${offerData.qty}, Total=${offerData.total}`);
            expect(offerData.price).toBe(expectedRowsData[i].price);
            expect(offerData.qty).toBe(expectedRowsData[i].qty);
            expect(offerData.total).toBe(expectedRowsData[i].total);
        }
        Logger("All uploaded data verified successfully");
        // Reset the offer as the final step
        await base['pws_shopPage'].selectMoreActionOption('Reset Offer');
        Logger("Offer has been reset successfully");
    });
});


test.describe('Cart Page Functional Tests', () => {
    test.describe.configure({ mode: 'serial' });
    // test('SPKB-647: Verify Buyer Can Reset Offer from Cart Page', async () => {
    //     await base['pws_navMenuAsAdmin'].clickPwsNavBarMenu('Inventory');
    //     await base['pws_InventoryPage'].moreActionResetOption();
    //     await base['pws_InventoryPage'].clickCartButton();
    //     const summary = await base['pws_CartPage'].getSummaryOffer()
    //     const SKU = summary[0];
    //     const Qty = summary[1];
    //     expect(SKU).toBe("0");
    //     expect(Qty).toBe("0");
    // });

    test('SPKB-648: Verify Buyer Can Download Offer', async () => {
        Logger('SPKB-648: Verifying Buyer Can Download Offer from Shop page');

        // 1. Navigate to Shop page and reset
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserOne);
        await base['page'].waitForTimeout(2000);
        await base['pws_shopPage'].selectMoreActionOption('Reset Offer');
        await base['page'].waitForTimeout(1000);

        // 2. Sort by Avl. Qty descending so first rows have stock
        await base['pws_shopPage'].sortAvlQty('descending');

        // 3. Enter offer data on first row with a valid integer price
        const offerData = await base['pws_shopPage'].enterOfferData(0, 50, 1);
        Logger(`Entered offer data: Price=${offerData[0].price}, Qty=${offerData[0].qty}`);

        // 4. Download Offer via More Actions then confirmation button
        await base['pws_shopPage'].selectMoreActionOption('Download Offer');
        await base['page'].waitForTimeout(2000);

        const downloadButton = base['page'].locator('.mx-name-actionButton20');
        await downloadButton.waitFor({ state: 'visible', timeout: 10000 });

        const [download] = await Promise.all([
            base['page'].waitForEvent('download', { timeout: 30000 }),
            downloadButton.click()
        ]);

        const fileName = download.suggestedFilename();
        Logger(`Download triggered, file: ${fileName}`);

        // 5. Save and verify file exists
        const filePath = `./src/test-data/${fileName}`;
        await download.saveAs(filePath);

        const fs = require('fs');
        expect(fs.existsSync(filePath), `Downloaded file should exist at ${filePath}`).toBeTruthy();
        Logger(`SPKB-648 PASSED: Offer downloaded as "${fileName}"`);

        // Cleanup
        fs.unlinkSync(filePath);
    });

    test('SPKB-1296: Verify Buyer Can Not Submit Offer Above Avail-Qty', async () => {
        Logger('SPKB-1296: Verifying offer above available qty is blocked');

        // 1. Navigate to Shop page and reset
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserOne);
        await base['page'].waitForTimeout(2000);
        await base['pws_shopPage'].selectMoreActionOption('Reset Offer');
        await base['page'].waitForTimeout(1000);

        // 2. Sort ascending to find a SKU with LOW avl qty (easy to exceed)
        await base['pws_shopPage'].sortAvlQty('ascending');
        await base['page'].waitForTimeout(1000);

        // 3. Read the available qty of the first row
        const firstRowData = await base['pws_shopPage'].getRowData(0);
        Logger(`First row SKU: ${firstRowData.sku}`);

        const cells = await base['page'].locator("//div[@role='gridcell']").all();
        const avlQtyText = (await cells[8].textContent() || '0').trim().replace(/[,+]/g, '');
        const avlQty = parseInt(avlQtyText) || 1;
        Logger(`Available qty for first SKU: ${avlQty}`);

        // 4. Enter offer qty that EXCEEDS available qty (row index 0)
        const exceedQty = avlQty + 5;
        await base['pws_shopPage'].enterOfferData(0, 0.8, exceedQty);
        Logger(`Entered offer qty ${exceedQty} (exceeds avl qty ${avlQty})`);

        // 5. Navigate to Cart
        await base['pws_shopPage'].clickCartButton();
        await base['page'].waitForTimeout(2000);

        // 6. Verify qty exceed warning is displayed
        const isWarningVisible = await base['pws_CartPage'].isQtyExceedMessageVisible();
        expect(isWarningVisible, 'Qty exceed warning should be displayed').toBeTruthy();
        Logger('Qty exceed warning is displayed as expected');

        // 7. Verify Submit button is disabled
        const isSubmitEnabled = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitEnabled, 'Submit button should be disabled when qty exceeds available').toBeFalsy();
        Logger('SPKB-1296 PASSED: Submit blocked when offer qty exceeds available qty');
    });

    test('SPKB-1297: Verify Buyer Can Submit Offer Below Avail-Qty', async () => {
        Logger('SPKB-1297: Verifying offer below available qty can be submitted');

        // 1. Navigate to Shop page and reset
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserTwo);
        await base['page'].waitForTimeout(2000);
        await base['pws_shopPage'].selectMoreActionOption('Reset Offer');
        await base['page'].waitForTimeout(1000);

        // 2. Sort descending so first row has high avl qty
        await base['pws_shopPage'].sortAvlQty('descending');
        await base['page'].waitForTimeout(1000);

        // 3. Enter offer with qty = 1 on first row (always below avl qty for high-qty items)
        await base['pws_shopPage'].enterOfferData(0, 0.8, 1);
        Logger('Entered offer: row 0, price=0.8, qty=1');

        // 4. Navigate to Cart
        await base['pws_shopPage'].clickCartButton();
        await base['page'].waitForTimeout(2000);

        // 5. Verify NO qty exceed warning
        const isWarningVisible = await base['pws_CartPage'].isQtyExceedMessageDisplayed();
        expect(isWarningVisible, 'No qty exceed warning should be displayed').toBeFalsy();
        Logger('No qty exceed warning — as expected for qty below available');

        // 6. Verify Submit button is enabled
        const isSubmitEnabled = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitEnabled, 'Submit button should be enabled').toBeTruthy();

        // 7. Submit the offer
        const offerID = await base['pws_CartPage'].submitAndCaptureOrderID();
        expect(offerID).toBeTruthy();
        Logger(`SPKB-1297 PASSED: Offer submitted successfully. Order ID: ${offerID}`);
    });

    test('SPKB-1298: Verify Buyer Can Submit Offer Above Avail-Qty > 100', async () => {
        Logger('SPKB-1298: Verifying offer above 100 qty is accepted when avl qty is "100+"');

        // 1. Navigate to Shop page and reset
        await base['pws_shopPage'].ensureUserOnShopPage(base, userRole.PWS_UserOne);
        await base['page'].waitForTimeout(2000);
        await base['pws_shopPage'].selectMoreActionOption('Reset Offer');
        await base['page'].waitForTimeout(1000);

        // 2. Sort descending — first row should show "100+" in Avl. Qty
        await base['pws_shopPage'].sortAvlQty('descending');
        await base['page'].waitForTimeout(1000);

        // 3. Verify the first row actually shows "100+" or ≥100
        const cells = await base['page'].locator("//div[@role='gridcell']").all();
        const avlQtyText = (await cells[8].textContent() || '').trim();
        Logger(`First row Avl. Qty text: "${avlQtyText}"`);
        expect(avlQtyText).toContain('100');

        // 4. Enter offer qty of 101 on first row (above displayed "100" but within actual stock)
        await base['pws_shopPage'].enterOfferData(0, 0.8, 101);
        Logger('Entered offer: row 0, price=0.8, qty=101');

        // 5. Navigate to Cart
        await base['pws_shopPage'].clickCartButton();
        await base['page'].waitForTimeout(2000);

        // 6. Verify NO qty exceed warning (the actual avl qty is > 100)
        const isWarningVisible = await base['pws_CartPage'].isQtyExceedMessageDisplayed();
        expect(isWarningVisible, 'No qty exceed warning for "100+" items').toBeFalsy();
        Logger('No qty exceed warning — as expected for "100+" items');

        // 7. Verify Submit button is enabled and submit
        const isSubmitEnabled = await base['pws_CartPage'].isSubmitButtonEnabled();
        expect(isSubmitEnabled, 'Submit button should be enabled').toBeTruthy();

        const offerID = await base['pws_CartPage'].submitAndCaptureOrderID();
        expect(offerID).toBeTruthy();
        Logger(`SPKB-1298 PASSED: Offer with qty=101 submitted for "100+" item. Order ID: ${offerID}`);
    });

    test('SPKB-1279: Verify Remove Item by Clicking X Icon', async () => {
        Logger('Verifying Buyer Can Remove SKU by Clicking X Icon');
        await base['loginPage'].loginAs(userRole.PWS_UserTwo);
        await base['pws_shopPage'].selectMoreActionOption('Reset Offer');
        await base['pws_shopPage'].sortAvlQty("descending");
        await base['pws_shopPage'].enterOfferData(3, 0.8, 1);
        await base['pws_shopPage'].clickCartButton();
    });

    test('SPKB-1280: Verify Remove Item by Update Qty to Zero', async () => {

    });

    test('SPKB-1281: Verify Removed Items Not Reappear after Page Reload', async () => {

    });

    test('SPKB-1282: Verify Summary SKU, Qty, Price Updating when Removing Item', async () => {

    });

    test('SPKB-1283: Verify Buyer Can Submit Order Successful', async () => {

    });

    test('SPKB-1315: Sales Rep Can Finalize Order with All SKUs Above List Price', async () => {

    });

    test('SPKB-1316: Sales Rep Can Finalize Order with All SKUs Below List Price', async () => {

    });

    test('SPKB-1317: Sales Rep Can Finalize Order with Some SKU Below List Price', async () => {


    });


});

```
