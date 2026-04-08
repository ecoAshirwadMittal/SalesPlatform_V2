# Test Spec: DataGrid_Round1.spec.ts

- **Path**: `src\tests\Wholesales\DataGrid_Round1.spec.ts`
- **Category**: Test Spec
- **Lines**: 2102
- **Size**: 116,794 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../BaseTest';
import { navTabs, userRole } from '../../utils/resources/enum';
import { validateDuplication, isDownloaded, Logger, modifyBidsExcelSheet } from '../../utils/helpers/data_utils';
import user_data from '../../utils/resources/user_data.json';
import auction_config from '../../utils/resources/auction_config.json';

let base: BaseTest;
let inventoryData: Array<{
    productID: string,
    grade: string,
    dwQty: string,
    dwTargetPrice: string,
    totalQty: string,
    targetPrice: string
}>;

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
});

test.describe.serial('R1: Pre-Conditions', () => {
    test('R1: Pre-Condition | Admin Can Delete Current Auction Week', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['auc_acc_SchedulingPage'].deleteRecentAuctionWeek();
    });

    test('R1: Pre-Condition | Admin Can Configure R2 Selection Criteria', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        await base['auc_acc_Round2CriteriaPage'].selectRegularBuyerSettings(
            "Buyer with Target Qualification", "Inventory Based on Target Qualification", 'Yes');
    });

    test('R1: Pre-Condition | Admin Can Configure Minimum Bid Price', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);

    });

    test('R1: Pre-Condition | Admin Can Configure Bid Ranking', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);

    });

    test('R1: Pre-Condition | Admin Can Configure Additional Bid Quantity', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);

    });

    test('R1: Pre-Condition | Store Inventory Data from Inventory Page', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
        console.log("Inventory Data:", inventoryData);
    });

    test('R1: Pre-Condition | Admin Can Schedule Auction Week', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        await base['auc_acc_SchedulingPage'].createAuctionAndStartRoundOne();
    });

    test('R1: Pre-Condition | Round 1 is Open', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        const isRoundOneOpen = await base['auc_acc_SchedulingPage'].ensureRoundOneIsOpen();
        expect(isRoundOneOpen).toBeTruthy();
    });
})


test.describe.serial('R1: Regular Non-DW Buyer Tests', () => {
    let exportFile: string;

    test('R1: Reg Non-DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    //SPKB-407
    test('R1: Reg Non-DW | Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    //SPKB-2470
    test('R1: Reg Non-DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA600WHL");
    });

    //SPKB-2470
    test('R1: Reg Non-DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const displayedMinimumBid = await base['auc_dataGridDashBoardPage'].getMinimumBidDisplay();
        expect(displayedMinimumBid).toBe(auction_config.minimum_bid_price);
    });

    //SPKB-3211
    test('R1: Reg Non-DW | Verify Non-DW Buyer Can Sort by Target Price or Avail. Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('descending');
        await base['page'].waitForTimeout(2000);
        expect(isSorted).toBeTruthy();
    });

    //SPKB-2884
    test('R1: Reg Non-DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('ascending');
        await base['page'].waitForTimeout(1000);
        expect(isSorted).toBeTruthy();
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        for (let i = 0; i < 3; i++) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(i);
            const targetPrice = Number(bidData.targetPrice.replace('$', ''));
            Logger(`Row ${i}: Target Price = ${targetPrice}, Minimum Bid Price = ${minimumBidPrice}`);
            expect(targetPrice).toBeGreaterThanOrEqual(minimumBidPrice);
        }
    });

    //SPKB-3212
    test('R1: Reg Non-DW | Verify Non-DW Buyer Can Filter by ProductID and Grade', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const filterResult = await base['auc_dataGridDashBoardPage'].filterByProductIDAndGrade("12238", "F_NYN/H_NNN");
        expect(filterResult).toBeTruthy();
    });

    //SPKB-2452
    test('R1: Reg Non-DW | Validate Total Qty Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        expect(bidData.availQty).toBe(matchingInventory?.totalQty);
    });

    //SPKB-2883
    test('R1: Reg Non-DW | Validate Additional Qty Added to Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        // Validate grade A_YYY
        const bidDataA = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "A_YYY");
        const matchingInventoryA = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "A_YYY"
        );
        const additionalQtyA = base['auc_inventoryPage'].getAdditionalQty("12238", "A_YYY", "Non-DW");
        const expectedAvailQtyA = String(Number(matchingInventoryA?.totalQty || 0) + additionalQtyA);
        expect(bidDataA.availQty).toBe(expectedAvailQtyA);
        // Validate grade B_NYY/D_NNY
        const bidDataB = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "B_NYY/D_NNY");
        const matchingInventoryB = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "B_NYY/D_NNY"
        );
        const additionalQtyB = base['auc_inventoryPage'].getAdditionalQty("12238", "B_NYY/D_NNY", "Non-DW");
        const expectedAvailQtyB = String(Number(matchingInventoryB?.totalQty || 0) + additionalQtyB);
        expect(bidDataB.availQty).toBe(expectedAvailQtyB);
    });

    //SPKB-2453
    test('R1: Reg Non-DW | Verify Non-DW Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(bidData.targetPrice).toBe(matchingInventory?.targetPrice);
        }
    });

    //SPKB-3210
    test('R1: Reg Non-DW | Verify Non-DW Buyer Cannot Submit Zero Bid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const noBidModalAppeared = await base['auc_dataGridDashBoardPage'].isNoBidModalAppeared();
        expect(noBidModalAppeared).toBe(true);
    });

    //SPKB-3208
    test('R1: Reg Non-DW | Verify Non-DW Buyer Cannot Enter Negative Values for Price and Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "-100", qty: "-5" }]);
        await base['page'].waitForTimeout(1000);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidData.price).toBe("0.00"); // Price should remain default
        expect(bidData.qty).toBe(""); // Qty should remain blank
        Logger(`After entering negative values - Price: ${bidData.price}, Qty: ${bidData.qty}`);
    });

    //SPKB-3209
    test('R1: Reg Non-DW | Verify Non-DW Buyer Cannot Enter Decimal Values for Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "100", qty: "5.5" }]);
        await base['page'].waitForTimeout(1000);
        const alertLocator = base['page'].locator("//div[@class='alert alert-danger mx-validation-message']");
        await expect(alertLocator).toBeVisible();
        const alertMessage = await alertLocator.textContent();
        expect(alertMessage?.toLowerCase()).toContain("invalid number");
        Logger(`Validation message displayed for decimal qty: ${alertMessage}`);
    });

    //SPKB-2447, SPKB-2523
    test('R1: Reg Non-DW | Validate Minimum Bid Validation Modal Popup on Submission via Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "6" }, { price: "1500", qty: "6" }, { price: "1500", qty: "6" },
            { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }
        ]);
        expect(bidsBlowMinimum).toBe(3);
    });

    test.skip('R1: Reg Non-DW | Verify Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2448
    test('R1: Reg Non-DW | Verify Buyer Can Lower Bids and Re-Submit on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1000", qty: "5" }, { price: "1000", qty: "5" }, { price: "1000", qty: "5" },
            { price: "1000", qty: "5" }, { price: "1000", qty: "5" }, { price: "1000", qty: "5" },
            { price: "1000", qty: "5" }, { price: "1000", qty: "5" }, { price: "999.99", qty: " " }
        ]);
        expect(bidsBlowMinimum).toBe(0);
        await base['loginPage'].logout();
    });

    test.skip('R1: Reg Non-DW | Verify Re-Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2449
    test('R1: Reg Non-DW | Verify Buyer Can Re-Access Auction without Losing Unsubmitted Bids', async () => {
        await base['loginPage'].loginAs(userRole.User_AA602WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        await base['auc_dataGridDashBoardPage'].placeBids([
            { price: "1200", qty: "5" }, { price: "1200", qty: "5" }
        ]);
        await base['loginPage'].logout();
        await base['page'].waitForTimeout(2000);
        await base['loginPage'].loginAs(userRole.User_AA602WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const bidDataRow0 = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidDataRow0.price).toBe("1200");
        expect(bidDataRow0.qty).toBe("5");
        Logger(`Unsubmitted bid on row 0: Price = ${bidDataRow0.price}, Qty = ${bidDataRow0.qty}`);
        await base['loginPage'].logout();
    });

    //SPKB-
    test('R1: Reg Non-DW | Verify Buyer Can Export Bid Data', async () => {
        await base['loginPage'].loginAs(userRole.User_AA601WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    //SPKB-2533
    test('R1: Reg Non-DW | Validate Excel Sheet Format and Column Headers', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        }
        const { validateExcelColumns } = await import('../../utils/helpers/data_utils');
        const { headers, hasValidFormat } = validateExcelColumns(exportFile);
        const expectedColumns = [
            "ProductId", "Brand", "Model", "Model_Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price", "Qty. Cap"
        ];
        for (const expectedColumn of expectedColumns) {
            expect(headers).toContain(expectedColumn);
        }
        expect(hasValidFormat).toBeTruthy();
        Logger(`Validated Excel columns: ${headers.join(', ')}`);
    });

    //SPKB-2452
    test('R1: Reg Non-DW | Validate Total Qty Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const excelData = getExcelRowData(exportFile, "12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        console.log("Excel Data:", excelData);
        console.log("Matching Inventory:", matchingInventory);
        expect(excelData).not.toBeNull();
        expect(excelData?.availQty).toBe(matchingInventory?.totalQty);
        Logger(`Excel Avail.Qty: ${excelData?.availQty}, Inventory TotalQty: ${matchingInventory?.totalQty}`);
    });

    //SPKB-2883
    test('R1: Reg Non-DW | Validate Additional Qty Added to Total Qty Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            const additionalQty = base['auc_inventoryPage'].getAdditionalQty("12238", grade, "Non-DW");
            const expectedAvailQty = String(Number(matchingInventory?.totalQty || 0) + additionalQty);
            expect(excelData).not.toBeNull();
            expect(excelData?.availQty).toBe(expectedAvailQty);
            Logger(`Grade ${grade} - Excel Avail.Qty: ${excelData?.availQty}, Expected: ${expectedAvailQty}`);
        }
    });

    //SPKB-2453
    test('R1: Reg Non-DW | Verify Non-DW Target Price Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(excelData).not.toBeNull();
            expect(excelData?.targetPrice).toBe(matchingInventory?.targetPrice);
            Logger(`Grade ${grade} - Excel Target Price: ${excelData?.targetPrice}, Inventory: ${matchingInventory?.targetPrice}`);
        }
    });

    //SPKB-2884
    test('R1: Reg Non-DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        }
        const { getAllTargetPricesFromExcel } = await import('../../utils/helpers/data_utils');
        const targetPrices = getAllTargetPricesFromExcel(exportFile);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        expect(targetPrices.length).toBeGreaterThan(0);
        for (const price of targetPrices) {
            expect(price).toBeGreaterThanOrEqual(minimumBidPrice);
        }
        Logger(`Validated ${targetPrices.length} target prices - all >= ${minimumBidPrice}`);
    });

    //SPKB-2450, SPKB-2523
    test('R1: Reg Non-DW | Validate Submitted Bids Row Count and Validate Minimum Bid Validation Message', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA601WHL", userRole.User_AA601WHL);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(bidsBlowMinimum).toBe(2);
        await base['loginPage'].logout();
    });

    // test.skip('R1: Reg Non-DW | Verify Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

    // SPKB-2451, SPKB-2455
    test('R1: Reg Non-DW | Verify Buyer Can Lower/Higher Bids and Re-Submit Bids via Excel Import', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA601WHL", userRole.User_AA601WHL);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1000", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1000", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "1000", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "1000", bidQty: "5" }];
        exportFile = user_data.User_AA601WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
    });

    // test.skip('R1: Reg Non-DW | Verify Re-Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })
});


test.describe.serial('R1: Regular DW Buyer Tests', () => {
    let exportFile: string;

    test('R1: Reg DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA700DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    //SPKB-407
    test('R1: Reg DW | Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    //SPKB-2528
    test('R1: Reg DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA700DW");
    });

    //SPKB-2528
    test('R1: Reg DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const displayedMinimumBid = await base['auc_dataGridDashBoardPage'].getMinimumBidDisplay();
        expect(displayedMinimumBid).toBe(auction_config.minimum_bid_price);
    });

    //SPKB-3217
    test('R1: Reg DW | Verify DW Buyer Can Sort by Target Price or Avail. Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('descending');
        await base['page'].waitForTimeout(2000);
        expect(isSorted).toBeTruthy();
    });

    //SPKB-2888
    test('R1: Reg DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        await base['auc_dataGridDashBoardPage'].sortByTargetPrice('ascending');
        await base['page'].waitForTimeout(2000);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        for (let i = 0; i < 3; i++) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(i);
            const targetPrice = Number(bidData.targetPrice.replace('$', ''));
            Logger(`Row ${i}: Target Price = ${targetPrice}, Minimum Bid Price = ${minimumBidPrice}`);
            expect(targetPrice).toBeGreaterThanOrEqual(minimumBidPrice);
        }
    });

    //SPKB-3217
    test('R1: Reg DW | Verify DW Buyer Can Filter by ProductID and Grade', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const filterResult = await base['auc_dataGridDashBoardPage'].filterByProductIDAndGrade("12238", "F_NYN/H_NNN");
        expect(filterResult).toBeTruthy();
    });

    //SPKB-2466
    test('R1: Reg DW | Validate Total Qty Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        expect(bidData.availQty).toBe(matchingInventory?.dwQty);
    });

    //SPKB-2887
    test('R1: Reg DW | Validate Additional Qty Added to Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        // Validate grade A_YYY
        const bidDataA = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "A_YYY");
        const matchingInventoryA = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "A_YYY"
        );
        const additionalQtyA = base['auc_inventoryPage'].getAdditionalQty("12238", "A_YYY", "DW");
        const expectedAvailQtyA = String(Number(matchingInventoryA?.dwQty || 0) + additionalQtyA);
        expect(bidDataA.availQty).toBe(expectedAvailQtyA);
        // Validate grade B_NYY/D_NNY
        const bidDataB = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "B_NYY/D_NNY");
        const matchingInventoryB = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "B_NYY/D_NNY"
        );
        const additionalQtyB = base['auc_inventoryPage'].getAdditionalQty("12238", "B_NYY/D_NNY", "DW");
        const expectedAvailQtyB = String(Number(matchingInventoryB?.dwQty || 0) + additionalQtyB);
        expect(bidDataB.availQty).toBe(expectedAvailQtyB);
    });

    //SPKB-2467
    test('R1: Reg DW | Verify DW Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(bidData.targetPrice).toBe(matchingInventory?.dwTargetPrice);
        }
    });

    //SPKB-3216
    test('R1: Reg DW | Verify DW Buyer Cannot Submit Zero Bid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        const noBidModalAppeared = await base['auc_dataGridDashBoardPage'].isNoBidModalAppeared();
        expect(noBidModalAppeared).toBe(true);
    });

    //SPKB-3214
    test('R1: Reg Non-DW | Verify Non-DW Buyer Cannot Enter Negative Values for Price and Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "-100", qty: "-5" }]);
        await base['page'].waitForTimeout(1000);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidData.price).toBe("0.00"); // Price should remain default
        expect(bidData.qty).toBe(""); // Qty should remain blank
        Logger(`After entering negative values - Price: ${bidData.price}, Qty: ${bidData.qty}`);
    });

    //SPKB-3215
    test('R1: Reg Non-DW | Verify Non-DW Buyer Cannot Enter Decimal Values for Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "100", qty: "5.5" }]);
        await base['page'].waitForTimeout(1000);
        const alertLocator = base['page'].locator("//div[@class='alert alert-danger mx-validation-message']");
        await expect(alertLocator).toBeVisible();
        const alertMessage = await alertLocator.textContent();
        expect(alertMessage?.toLowerCase()).toContain("invalid number");
        Logger(`Validation message displayed for decimal qty: ${alertMessage}`);
    });

    //SPKB-2460, SPKB-2532
    test('R1: Reg DW | Validate Minimum Bid Validation Modal Popup on Submission via Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "6" }, { price: "1500", qty: "6" }, { price: "1500", qty: "6" },
            { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }
        ]);
        expect(bidsBlowMinimum).toBe(3);
    });

    test.skip('R1: Reg DW | Verify Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2461
    test('R1: Reg DW | Verify Buyer Can Lower Bids and Re-Submit on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA700DW", userRole.User_AA700DW);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "999", qty: "5" }, { price: "999", qty: "5" }, { price: "999", qty: "5" },
            { price: "999", qty: "5" }, { price: "999", qty: "5" }, { price: "999", qty: "5" },
            { price: "999", qty: "5" }, { price: "999", qty: "5" }, { price: "998.99", qty: " " }
        ]);
        expect(bidsBlowMinimum).toBe(0);
        await base['loginPage'].logout();
    });

    test.skip('R1: Reg DW | Verify Re-Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2462
    test('R1: Reg DW | Verify Buyer Can Re-Access Auction without Losing Unsubmitted Bids', async () => {
        await base['loginPage'].loginAs(userRole.User_AA702DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        await base['auc_dataGridDashBoardPage'].placeBids([
            { price: "1200", qty: "5" }, { price: "1200", qty: "5" }
        ]);
        await base['loginPage'].logout();
        await base['page'].waitForTimeout(2000);
        await base['loginPage'].loginAs(userRole.User_AA702DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const bidDataRow0 = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidDataRow0.price).toBe("1200");
        expect(bidDataRow0.qty).toBe("5");
        Logger(`Unsubmitted bid on row 0: Price = ${bidDataRow0.price}, Qty = ${bidDataRow0.qty}`);
        await base['loginPage'].logout();
    });

    //SPKB-
    test('R1: Reg DW | Verify Buyer Can Export Bid Data', async () => {
        await base['loginPage'].loginAs(userRole.User_AA701DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    //SPKB-2535
    test('R1: Reg DW | Validate Excel Sheet Format and Column Headers', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        }
        const { validateExcelColumns } = await import('../../utils/helpers/data_utils');
        const { headers, hasValidFormat } = validateExcelColumns(exportFile);
        const expectedColumns = [
            "ProductId", "Brand", "Model", "Model_Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price", "Qty. Cap"
        ];
        for (const expectedColumn of expectedColumns) {
            expect(headers).toContain(expectedColumn);
        }
        expect(hasValidFormat).toBeTruthy();
        Logger(`Validated Excel columns: ${headers.join(', ')}`);
    });

    //SPKB-2466
    test('R1: Reg DW | Validate Total Qty Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const excelData = getExcelRowData(exportFile, "12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        console.log("Excel Data:", excelData);
        console.log("Matching Inventory:", matchingInventory);
        expect(excelData).not.toBeNull();
        expect(excelData?.availQty).toBe(matchingInventory?.dwQty);
        Logger(`Excel Avail.Qty: ${excelData?.availQty}, Inventory TotalQty: ${matchingInventory?.dwQty}`);
    });

    //SPKB-2887
    test('R1: Reg DW | Validate Additional Qty Added to Total Qty Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            const additionalQty = base['auc_inventoryPage'].getAdditionalQty("12238", grade, "DW");
            const expectedAvailQty = String(Number(matchingInventory?.dwQty || 0) + additionalQty);
            expect(excelData).not.toBeNull();
            expect(excelData?.availQty).toBe(expectedAvailQty);
            Logger(`Grade ${grade} - Excel Avail.Qty: ${excelData?.availQty}, Expected: ${expectedAvailQty}`);
        }
    });

    //SPKB-2467
    test('R1: Reg DW | Verify DW Target Price Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(excelData).not.toBeNull();
            expect(excelData?.targetPrice).toBe(matchingInventory?.dwTargetPrice);
            Logger(`Grade ${grade} - Excel Target Price: ${excelData?.targetPrice}, Inventory: ${matchingInventory?.dwTargetPrice}`);
        }
    });

    //SPKB-2888
    test('R1: Reg DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        }
        const { getAllTargetPricesFromExcel } = await import('../../utils/helpers/data_utils');
        const targetPrices = getAllTargetPricesFromExcel(exportFile);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        expect(targetPrices.length).toBeGreaterThan(0);
        for (const price of targetPrices) {
            expect(price).toBeGreaterThanOrEqual(minimumBidPrice);
        }
        Logger(`Validated ${targetPrices.length} target prices - all >= ${minimumBidPrice}`);
    });

    //SPKB-2463, SPKB-2532
    test('R1: Reg DW | Validate Submitted Bids Row Count and Validate Minimum Bid Validation Message', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA701DW", userRole.User_AA701DW);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(bidsBlowMinimum).toBe(2);
        await base['loginPage'].logout();
    });

    // test.skip('R1: Reg DW | Verify Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

    // SPKB-2464, SPKB-2465
    test('R1: Reg DW | Verify Buyer Can Lower/Higher Bids and Re-Submit Bids via Excel Import', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA701DW", userRole.User_AA701DW);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "999", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "999", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "999", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "999", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA701DW.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
    });

    // test.skip('R1: Reg DW | Verify Re-Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })
});


test.describe.serial('R1: Special Non-DW Buyer Tests', () => {
    let exportFile: string;

    test('R1: Special Non-DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA800WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    //SPKB-407
    test('R1: Special Non-DW | Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    //SPKB-2578
    test('R1: Special Non-DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA800WHL");
    });

    //SPKB-2578
    test('R1: Special Non-DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const displayedMinimumBid = await base['auc_dataGridDashBoardPage'].getMinimumBidDisplay();
        expect(displayedMinimumBid).toBe(auction_config.minimum_bid_price);
    });

    //SPKB-3223
    test('R1: Special Non-DW | Verify Non-DW Buyer Can Sort by Target Price or Avail. Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('descending');
        await base['page'].waitForTimeout(2000);
        expect(isSorted).toBeTruthy();
    });

    //SPKB-2892
    test('R1: Special Non-DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('ascending');
        await base['page'].waitForTimeout(1000);
        expect(isSorted).toBeTruthy();
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        for (let i = 0; i < 3; i++) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(i);
            const targetPrice = Number(bidData.targetPrice.replace('$', ''));
            Logger(`Row ${i}: Target Price = ${targetPrice}, Minimum Bid Price = ${minimumBidPrice}`);
            expect(targetPrice).toBeGreaterThanOrEqual(minimumBidPrice);
        }
    });

    //SPKB-3224
    test('R1: Special Non-DW | Verify Non-DW Buyer Can Filter by ProductID and Grade', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const filterResult = await base['auc_dataGridDashBoardPage'].filterByProductIDAndGrade("12238", "F_NYN/H_NNN");
        expect(filterResult).toBeTruthy();
    });

    //SPKB-2580
    test('R1: Special Non-DW | Validate Total Qty Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        expect(bidData.availQty).toBe(matchingInventory?.totalQty);
    });

    //SPKB-2891
    test('R1: Special Non-DW | Validate Additional Qty Added to Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        // Validate grade A_YYY
        const bidDataA = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "A_YYY");
        const matchingInventoryA = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "A_YYY"
        );
        const additionalQtyA = base['auc_inventoryPage'].getAdditionalQty("12238", "A_YYY", "Non-DW");
        const expectedAvailQtyA = String(Number(matchingInventoryA?.totalQty || 0) + additionalQtyA);
        expect(bidDataA.availQty).toBe(expectedAvailQtyA);
        // Validate grade B_NYY/D_NNY
        const bidDataB = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "B_NYY/D_NNY");
        const matchingInventoryB = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "B_NYY/D_NNY"
        );
        const additionalQtyB = base['auc_inventoryPage'].getAdditionalQty("12238", "B_NYY/D_NNY", "Non-DW");
        const expectedAvailQtyB = String(Number(matchingInventoryB?.totalQty || 0) + additionalQtyB);
        expect(bidDataB.availQty).toBe(expectedAvailQtyB);
    });

    //SPKB-2581
    test('R1: Special Non-DW | Verify Non-DW Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(bidData.targetPrice).toBe(matchingInventory?.targetPrice);
        }
    });

    //SPKB-3222
    test('R1: Special Non-DW | Verify Non-DW Buyer Cannot Submit Zero Bid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        const noBidModalAppeared = await base['auc_dataGridDashBoardPage'].isNoBidModalAppeared();
        expect(noBidModalAppeared).toBe(true);
    });

    //SPKB-3220
    test('R1: Special Non-DW | Verify Non-DW Buyer Cannot Enter Negative Values for Price and Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "-100", qty: "-5" }]);
        await base['page'].waitForTimeout(1000);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidData.price).toBe("0.00"); // Price should remain default
        expect(bidData.qty).toBe(""); // Qty should remain blank
        Logger(`After entering negative values - Price: ${bidData.price}, Qty: ${bidData.qty}`);
    });

    //SPKB-3221
    test('R1: Special Non-DW | Verify Non-DW Buyer Cannot Enter Decimal Values for Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "100", qty: "5.5" }]);
        await base['page'].waitForTimeout(1000);
        const alertLocator = base['page'].locator("//div[@class='alert alert-danger mx-validation-message']");
        await expect(alertLocator).toBeVisible();
        const alertMessage = await alertLocator.textContent();
        expect(alertMessage?.toLowerCase()).toContain("invalid number");
        Logger(`Validation message displayed for decimal qty: ${alertMessage}`);
    });

    //SPKB-2583, SPKB-2584
    test('R1: Special Non-DW | Validate Minimum Bid Validation Modal Popup on Submission via Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "6" }, { price: "1500", qty: "6" }, { price: "1500", qty: "6" },
            { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }
        ]);
        expect(bidsBlowMinimum).toBe(3);
    });

    test.skip('R1: Special Non-DW | Verify Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2585
    test('R1: Special Non-DW | Verify Buyer Can Lower Bids and Re-Submit on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA800WHL", userRole.User_AA800WHL);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "998", qty: "5" }, { price: "998", qty: "5" }, { price: "998", qty: "5" },
            { price: "998", qty: "5" }, { price: "998", qty: "5" }, { price: "998", qty: "5" },
            { price: "998", qty: "5" }, { price: "998", qty: "5" }, { price: "997.99", qty: " " }
        ]);
        expect(bidsBlowMinimum).toBe(0);
        await base['loginPage'].logout();
    });

    test.skip('R1: Special Non-DW | Verify Re-Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2586
    test('R1: Special Non-DW | Verify Buyer Can Re-Access Auction without Losing Unsubmitted Bids', async () => {
        await base['loginPage'].loginAs(userRole.User_AA802WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        await base['auc_dataGridDashBoardPage'].placeBids([
            { price: "1200", qty: "5" }, { price: "1200", qty: "5" }
        ]);
        await base['loginPage'].logout();
        await base['page'].waitForTimeout(2000);
        await base['loginPage'].loginAs(userRole.User_AA802WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const bidDataRow0 = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidDataRow0.price).toBe("1200");
        expect(bidDataRow0.qty).toBe("5");
        Logger(`Unsubmitted bid on row 0: Price = ${bidDataRow0.price}, Qty = ${bidDataRow0.qty}`);
        await base['loginPage'].logout();
    });

    //SPKB-
    test('R1: Special Non-DW | Verify Buyer Can Export Bid Data', async () => {
        await base['loginPage'].loginAs(userRole.User_AA801WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    //SPKB-2579
    test('R1: Special Non-DW | Validate Excel Sheet Format and Column Headers', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        }
        const { validateExcelColumns } = await import('../../utils/helpers/data_utils');
        const { headers, hasValidFormat } = validateExcelColumns(exportFile);
        const expectedColumns = [
            "ProductId", "Brand", "Model", "Model_Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price", "Qty. Cap"
        ];
        for (const expectedColumn of expectedColumns) {
            expect(headers).toContain(expectedColumn);
        }
        expect(hasValidFormat).toBeTruthy();
        Logger(`Validated Excel columns: ${headers.join(', ')}`);
    });

    //SPKB-2580
    test('R1: Special Non-DW | Validate Total Qty Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const excelData = getExcelRowData(exportFile, "12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        console.log("Excel Data:", excelData);
        console.log("Matching Inventory:", matchingInventory);
        expect(excelData).not.toBeNull();
        expect(excelData?.availQty).toBe(matchingInventory?.totalQty);
        Logger(`Excel Avail.Qty: ${excelData?.availQty}, Inventory TotalQty: ${matchingInventory?.totalQty}`);
    });

    //SPKB-2891
    test('R1: Special Non-DW | Validate Additional Qty Added to Total Qty Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            const additionalQty = base['auc_inventoryPage'].getAdditionalQty("12238", grade, "Non-DW");
            const expectedAvailQty = String(Number(matchingInventory?.totalQty || 0) + additionalQty);
            expect(excelData).not.toBeNull();
            expect(excelData?.availQty).toBe(expectedAvailQty);
            Logger(`Grade ${grade} - Excel Avail.Qty: ${excelData?.availQty}, Expected: ${expectedAvailQty}`);
        }
    });

    //SPKB-2581
    test('R1: Special Non-DW | Verify Non-DW Target Price Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(excelData).not.toBeNull();
            expect(excelData?.targetPrice).toBe(matchingInventory?.targetPrice);
            Logger(`Grade ${grade} - Excel Target Price: ${excelData?.targetPrice}, Inventory: ${matchingInventory?.targetPrice}`);
        }
    });

    //SPKB-2892
    test('R1: Special Non-DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        }
        const { getAllTargetPricesFromExcel } = await import('../../utils/helpers/data_utils');
        const targetPrices = getAllTargetPricesFromExcel(exportFile);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        expect(targetPrices.length).toBeGreaterThan(0);
        for (const price of targetPrices) {
            expect(price).toBeGreaterThanOrEqual(minimumBidPrice);
        }
        Logger(`Validated ${targetPrices.length} target prices - all >= ${minimumBidPrice}`);
    });

    //SPKB-2587, SPKB-2584
    test('R1: Special Non-DW | Validate Submitted Bids Row Count and Validate Minimum Bid Validation Message', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA801WHL", userRole.User_AA801WHL);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(bidsBlowMinimum).toBe(2);
        await base['loginPage'].logout();
    });

    // test.skip('R1: Special Non-DW | Verify Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

    // SPKB-2588, SPKB-2589
    test('R1: Special Non-DW | Verify Buyer Can Lower/Higher Bids and Re-Submit Bids via Excel Import', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA801WHL", userRole.User_AA801WHL);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "998", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "998", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "998", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "998", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA801WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
    });

    // test.skip('R1: Special Non-DW | Verify Re-Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

});


test.describe.serial('R1: Special DW Buyer Tests', () => {
    let exportFile: string;

    test('R1: Special DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA900DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    //SPKB-407
    test('R1: Special DW | Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    //SPKB-2633
    test('R1: Special DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA900DW");
    });

    //SPKB-2633
    test('R1: Special DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const displayedMinimumBid = await base['auc_dataGridDashBoardPage'].getMinimumBidDisplay();
        expect(displayedMinimumBid).toBe(auction_config.minimum_bid_price);
    });

    //SPKB-3229
    test('R1: Special DW | Verify DW Buyer Can Sort by Target Price or Avail. Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('descending');
        await base['page'].waitForTimeout(2000);
        expect(isSorted).toBeTruthy();
    });

    //SPKB-2896
    test('R1: Special DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        await base['auc_dataGridDashBoardPage'].sortByTargetPrice('ascending');
        await base['page'].waitForTimeout(2000);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        for (let i = 0; i < 3; i++) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(i);
            const targetPrice = Number(bidData.targetPrice.replace('$', ''));
            Logger(`Row ${i}: Target Price = ${targetPrice}, Minimum Bid Price = ${minimumBidPrice}`);
            expect(targetPrice).toBeGreaterThanOrEqual(minimumBidPrice);
        }
    });

    //SPKB-3230
    test('R1: Special DW | Verify DW Buyer Can Filter by ProductID and Grade', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const filterResult = await base['auc_dataGridDashBoardPage'].filterByProductIDAndGrade("12238", "F_NYN/H_NNN");
        expect(filterResult).toBeTruthy();
    });

    //SPKB-2635
    test('R1: Special DW | Validate Total Qty Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        expect(bidData.availQty).toBe(matchingInventory?.dwQty);
    });

    //SPKB-2895
    test('R1: Special DW | Validate Additional Qty Added to Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        // Validate grade A_YYY
        const bidDataA = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "A_YYY");
        const matchingInventoryA = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "A_YYY"
        );
        const additionalQtyA = base['auc_inventoryPage'].getAdditionalQty("12238", "A_YYY", "DW");
        const expectedAvailQtyA = String(Number(matchingInventoryA?.dwQty || 0) + additionalQtyA);
        expect(bidDataA.availQty).toBe(expectedAvailQtyA);
        // Validate grade B_NYY/D_NNY
        const bidDataB = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "B_NYY/D_NNY");
        const matchingInventoryB = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "B_NYY/D_NNY"
        );
        const additionalQtyB = base['auc_inventoryPage'].getAdditionalQty("12238", "B_NYY/D_NNY", "DW");
        const expectedAvailQtyB = String(Number(matchingInventoryB?.dwQty || 0) + additionalQtyB);
        expect(bidDataB.availQty).toBe(expectedAvailQtyB);
    });

    //SPKB-2636
    test('R1: Special DW | Verify DW Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(bidData.targetPrice).toBe(matchingInventory?.dwTargetPrice);
        }
    });

    //SPKB-3228
    test('R1: Special DW | Verify DW Buyer Cannot Submit Zero Bid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const noBidModalAppeared = await base['auc_dataGridDashBoardPage'].isNoBidModalAppeared();
        expect(noBidModalAppeared).toBe(true);
    });

    //SPKB-3226
    test('R1: Special Non-DW | Verify Non-DW Buyer Cannot Enter Negative Values for Price and Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "-100", qty: "-5" }]);
        await base['page'].waitForTimeout(1000);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidData.price).toBe("0.00"); // Price should remain default
        expect(bidData.qty).toBe(""); // Qty should remain blank
        Logger(`After entering negative values - Price: ${bidData.price}, Qty: ${bidData.qty}`);
    });

    //SPKB-3227
    test('R1: Special Non-DW | Verify Non-DW Buyer Cannot Enter Decimal Values for Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "100", qty: "5.5" }]);
        await base['page'].waitForTimeout(1000);
        const alertLocator = base['page'].locator("//div[@class='alert alert-danger mx-validation-message']");
        await expect(alertLocator).toBeVisible();
        const alertMessage = await alertLocator.textContent();
        expect(alertMessage?.toLowerCase()).toContain("invalid number");
        Logger(`Validation message displayed for decimal qty: ${alertMessage}`);
    });

    //SPKB-2638, SPKB-2639
    test('R1: Special DW | Validate Minimum Bid Validation Modal Popup on Submission via Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "6" }, { price: "1500", qty: "6" }, { price: "1500", qty: "6" },
            { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }
        ]);
        expect(bidsBlowMinimum).toBe(3);
    });

    test.skip('R1: Special DW | Verify Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2640
    test('R1: Special DW | Verify Buyer Can Lower Bids and Re-Submit on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "997", qty: "5" }, { price: "997", qty: "5" }, { price: "997", qty: "5" },
            { price: "997", qty: "5" }, { price: "997", qty: "5" }, { price: "997", qty: "5" },
            { price: "997", qty: "5" }, { price: "997", qty: "5" }, { price: "996.99", qty: " " }
        ]);
        expect(bidsBlowMinimum).toBe(0);
        await base['loginPage'].logout();
    });

    test.skip('R1: Special DW | Verify Re-Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2641
    test('R1: Special DW | Verify Buyer Can Re-Access Auction without Losing Unsubmitted Bids', async () => {
        await base['loginPage'].loginAs(userRole.User_AA902DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        await base['auc_dataGridDashBoardPage'].placeBids([
            { price: "1200", qty: "5" }, { price: "1200", qty: "5" }
        ]);
        await base['loginPage'].logout();
        await base['page'].waitForTimeout(2000);
        await base['loginPage'].loginAs(userRole.User_AA902DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const bidDataRow0 = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidDataRow0.price).toBe("1200");
        expect(bidDataRow0.qty).toBe("5");
        Logger(`Unsubmitted bid on row 0: Price = ${bidDataRow0.price}, Qty = ${bidDataRow0.qty}`);
        await base['loginPage'].logout();
    });

    //SPKB-
    test('R1: Special DW | Verify Buyer Can Export Bid Data', async () => {
        await base['loginPage'].loginAs(userRole.User_AA901DW);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    //SPKB-2634
    test('R1: Special DW | Validate Excel Sheet Format and Column Headers', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        }
        const { validateExcelColumns } = await import('../../utils/helpers/data_utils');
        const { headers, hasValidFormat } = validateExcelColumns(exportFile);
        const expectedColumns = [
            "ProductId", "Brand", "Model", "Model_Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price", "Qty. Cap"
        ];
        for (const expectedColumn of expectedColumns) {
            expect(headers).toContain(expectedColumn);
        }
        expect(hasValidFormat).toBeTruthy();
        Logger(`Validated Excel columns: ${headers.join(', ')}`);
    });

    //SPKB-2635
    test('R1: Special DW | Validate Total Qty Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const excelData = getExcelRowData(exportFile, "12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        console.log("Excel Data:", excelData);
        console.log("Matching Inventory:", matchingInventory);
        expect(excelData).not.toBeNull();
        expect(excelData?.availQty).toBe(matchingInventory?.dwQty);
        Logger(`Excel Avail.Qty: ${excelData?.availQty}, Inventory TotalQty: ${matchingInventory?.dwQty}`);
    });

    //SPKB-2895
    test('R1: Special DW | Validate Additional Qty Added to Total Qty Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            const additionalQty = base['auc_inventoryPage'].getAdditionalQty("12238", grade, "DW");
            const expectedAvailQty = String(Number(matchingInventory?.dwQty || 0) + additionalQty);
            expect(excelData).not.toBeNull();
            expect(excelData?.availQty).toBe(expectedAvailQty);
            Logger(`Grade ${grade} - Excel Avail.Qty: ${excelData?.availQty}, Expected: ${expectedAvailQty}`);
        }
    });

    //SPKB-2636
    test('R1: Special DW | Verify DW Target Price Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(excelData).not.toBeNull();
            expect(excelData?.targetPrice).toBe(matchingInventory?.dwTargetPrice);
            Logger(`Grade ${grade} - Excel Target Price: ${excelData?.targetPrice}, Inventory: ${matchingInventory?.dwTargetPrice}`);
        }
    });

    //SPKB-2896
    test('R1: Special DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        }
        const { getAllTargetPricesFromExcel } = await import('../../utils/helpers/data_utils');
        const targetPrices = getAllTargetPricesFromExcel(exportFile);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        expect(targetPrices.length).toBeGreaterThan(0);
        for (const price of targetPrices) {
            expect(price).toBeGreaterThanOrEqual(minimumBidPrice);
        }
        Logger(`Validated ${targetPrices.length} target prices - all >= ${minimumBidPrice}`);
    });

    //SPKB-2642, SPKB-2639
    test('R1: Special DW | Validate Submitted Bids Row Count and Validate Minimum Bid Validation Message', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA901DW", userRole.User_AA901DW);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(bidsBlowMinimum).toBe(2);
        await base['loginPage'].logout();
    });

    // test.skip('R1: Reg DW | Verify Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

    // SPKB-2643, SPKB-2644
    test('R1: Special DW | Verify Buyer Can Lower/Higher Bids and Re-Submit Bids via Excel Import', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA901DW", userRole.User_AA901DW);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "997", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "997", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "997", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "997", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA901DW.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
    });

    // test.skip('R1: Reg DW | Verify Re-Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })
});


test.describe.serial('R1: Special(Multi) Non-DW Buyer Tests', () => {
    let exportFile: string;

    test('R1: Special(Multi) Non-DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA1000WHL);
        await base['welcomePage'].selectBuyerCode("AA1000WHL");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    //SPKB-407
    test('R1: Special(Multi) Non-DW | Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    //SPKB-2797
    test('R1: Special(Multi) Non-DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA1000WHL");
    });

    //SPKB-2797
    test('R1: Special(Multi) Non-DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const displayedMinimumBid = await base['auc_dataGridDashBoardPage'].getMinimumBidDisplay();
        expect(displayedMinimumBid).toBe(auction_config.minimum_bid_price);
    });

    //SPKB-3235
    test('R1: Special(Multi) Non-DW | Verify Non-DW Buyer Can Sort by Target Price or Avail. Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('descending');
        await base['page'].waitForTimeout(2000);
        expect(isSorted).toBeTruthy();
    });

    //SPKB-2912
    test('R1: Special(Multi) Non-DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('ascending');
        await base['page'].waitForTimeout(1000);
        expect(isSorted).toBeTruthy();
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        for (let i = 0; i < 3; i++) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(i);
            const targetPrice = Number(bidData.targetPrice.replace('$', ''));
            Logger(`Row ${i}: Target Price = ${targetPrice}, Minimum Bid Price = ${minimumBidPrice}`);
            expect(targetPrice).toBeGreaterThanOrEqual(minimumBidPrice);
        }
    });

    //SPKB-3236
    test('R1: Special(Multi) Non-DW | Verify Non-DW Buyer Can Filter by ProductID and Grade', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const filterResult = await base['auc_dataGridDashBoardPage'].filterByProductIDAndGrade("12238", "F_NYN/H_NNN");
        expect(filterResult).toBeTruthy();
    });

    //SPKB-2799
    test('R1: Special(Multi) Non-DW | Validate Total Qty Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        expect(bidData.availQty).toBe(matchingInventory?.totalQty);
    });

    //SPKB-2911
    test('R1: Special(Multi) Non-DW | Validate Additional Qty Added to Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        // Validate grade A_YYY
        const bidDataA = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "A_YYY");
        const matchingInventoryA = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "A_YYY"
        );
        const additionalQtyA = base['auc_inventoryPage'].getAdditionalQty("12238", "A_YYY", "Non-DW");
        const expectedAvailQtyA = String(Number(matchingInventoryA?.totalQty || 0) + additionalQtyA);
        expect(bidDataA.availQty).toBe(expectedAvailQtyA);
        // Validate grade B_NYY/D_NNY
        const bidDataB = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "B_NYY/D_NNY");
        const matchingInventoryB = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "B_NYY/D_NNY"
        );
        const additionalQtyB = base['auc_inventoryPage'].getAdditionalQty("12238", "B_NYY/D_NNY", "Non-DW");
        const expectedAvailQtyB = String(Number(matchingInventoryB?.totalQty || 0) + additionalQtyB);
        expect(bidDataB.availQty).toBe(expectedAvailQtyB);
    });

    //SPKB-2800
    test('R1: Special(Multi) Non-DW | Verify Non-DW Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(bidData.targetPrice).toBe(matchingInventory?.targetPrice);
        }
    });

    //SPKB-3234
    test('R1: Special(Multi) Non-DW | Verify Non-DW Buyer Cannot Submit Zero Bid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        const noBidModalAppeared = await base['auc_dataGridDashBoardPage'].isNoBidModalAppeared();
        expect(noBidModalAppeared).toBe(true);
    });

    //SPKB-3232
    test('R1: Special(Multi) Non-DW | Verify Non-DW Buyer Cannot Enter Negative Values for Price and Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "-100", qty: "-5" }]);
        await base['page'].waitForTimeout(1000);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidData.price).toBe("0.00"); // Price should remain default
        expect(bidData.qty).toBe(""); // Qty should remain blank
        Logger(`After entering negative values - Price: ${bidData.price}, Qty: ${bidData.qty}`);
    });

    //SPKB-3233
    test('R1: Special(Multi) Non-DW | Verify Non-DW Buyer Cannot Enter Decimal Values for Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "100", qty: "5.5" }]);
        await base['page'].waitForTimeout(1000);
        const alertLocator = base['page'].locator("//div[@class='alert alert-danger mx-validation-message']");
        await expect(alertLocator).toBeVisible();
        const alertMessage = await alertLocator.textContent();
        expect(alertMessage?.toLowerCase()).toContain("invalid number");
        Logger(`Validation message displayed for decimal qty: ${alertMessage}`);
    });

    //SPKB-2802, SPKB-2803
    test('R1: Special(Multi) Non-DW | Validate Minimum Bid Validation Modal Popup on Submission via Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "6" }, { price: "1500", qty: "6" }, { price: "1500", qty: "6" },
            { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }
        ]);
        expect(bidsBlowMinimum).toBe(3);
    });

    test.skip('R1: Special(Multi) Non-DW | Verify Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2804
    test('R1: Special(Multi) Non-DW | Verify Buyer Can Lower Bids and Re-Submit on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1000WHL", userRole.User_AA1000WHL);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "996", qty: "5" }, { price: "996", qty: "5" }, { price: "996", qty: "5" },
            { price: "996", qty: "5" }, { price: "996", qty: "5" }, { price: "996", qty: "5" },
            { price: "996", qty: "5" }, { price: "996", qty: "5" }, { price: "995.99", qty: " " }
        ]);
        expect(bidsBlowMinimum).toBe(0);
        await base['loginPage'].logout();
    });

    test.skip('R1: Special(Multi) Non-DW | Verify Re-Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2805
    test('R1: Special(Multi) Non-DW | Verify Buyer Can Re-Access Auction without Losing Unsubmitted Bids', async () => {
        await base['loginPage'].loginAs(userRole.User_AA1002WHL);
        await base['welcomePage'].selectBuyerCode("AA1002WHL");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        await base['auc_dataGridDashBoardPage'].placeBids([
            { price: "1200", qty: "5" }, { price: "1200", qty: "5" }
        ]);
        await base['loginPage'].logout();
        await base['page'].waitForTimeout(2000);
        await base['loginPage'].loginAs(userRole.User_AA1002WHL);
        await base['welcomePage'].selectBuyerCode("AA1002WHL");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const bidDataRow0 = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidDataRow0.price).toBe("1200");
        expect(bidDataRow0.qty).toBe("5");
        Logger(`Unsubmitted bid on row 0: Price = ${bidDataRow0.price}, Qty = ${bidDataRow0.qty}`);
        await base['loginPage'].logout();
    });

    //SPKB-
    test('R1: Special(Multi) Non-DW | Verify Buyer Can Export Bid Data', async () => {
        await base['loginPage'].loginAs(userRole.User_AA1001WHL);
        await base['welcomePage'].selectBuyerCode("AA1001WHL");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    //SPKB-2798
    test('R1: Special(Multi) Non-DW | Validate Excel Sheet Format and Column Headers', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        }
        const { validateExcelColumns } = await import('../../utils/helpers/data_utils');
        const { headers, hasValidFormat } = validateExcelColumns(exportFile);
        const expectedColumns = [
            "ProductId", "Brand", "Model", "Model_Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price", "Qty. Cap"
        ];
        for (const expectedColumn of expectedColumns) {
            expect(headers).toContain(expectedColumn);
        }
        expect(hasValidFormat).toBeTruthy();
        Logger(`Validated Excel columns: ${headers.join(', ')}`);
    });

    //SPKB-2799
    test('R1: Special(Multi) Non-DW | Validate Total Qty Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const excelData = getExcelRowData(exportFile, "12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        console.log("Excel Data:", excelData);
        console.log("Matching Inventory:", matchingInventory);
        expect(excelData).not.toBeNull();
        expect(excelData?.availQty).toBe(matchingInventory?.totalQty);
        Logger(`Excel Avail.Qty: ${excelData?.availQty}, Inventory TotalQty: ${matchingInventory?.totalQty}`);
    });

    //SPKB-2911
    test('R1: Special(Multi) Non-DW | Validate Additional Qty Added to Total Qty Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            const additionalQty = base['auc_inventoryPage'].getAdditionalQty("12238", grade, "Non-DW");
            const expectedAvailQty = String(Number(matchingInventory?.totalQty || 0) + additionalQty);
            expect(excelData).not.toBeNull();
            expect(excelData?.availQty).toBe(expectedAvailQty);
            Logger(`Grade ${grade} - Excel Avail.Qty: ${excelData?.availQty}, Expected: ${expectedAvailQty}`);
        }
    });

    //SPKB-2800
    test('R1: Special(Multi) Non-DW | Verify Non-DW Target Price Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(excelData).not.toBeNull();
            expect(excelData?.targetPrice).toBe(matchingInventory?.targetPrice);
            Logger(`Grade ${grade} - Excel Target Price: ${excelData?.targetPrice}, Inventory: ${matchingInventory?.targetPrice}`);
        }
    });

    //SPKB-2912
    test('R1: Special(Multi) Non-DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        }
        const { getAllTargetPricesFromExcel } = await import('../../utils/helpers/data_utils');
        const targetPrices = getAllTargetPricesFromExcel(exportFile);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        expect(targetPrices.length).toBeGreaterThan(0);
        for (const price of targetPrices) {
            expect(price).toBeGreaterThanOrEqual(minimumBidPrice);
        }
        Logger(`Validated ${targetPrices.length} target prices - all >= ${minimumBidPrice}`);
    });

    //SPKB-2806, SPKB-2803
    test('R1: Special(Multi) Non-DW | Validate Submitted Bids Row Count and Validate Minimum Bid Validation Message', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1001WHL", userRole.User_AA1001WHL);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(bidsBlowMinimum).toBe(2);
        await base['loginPage'].logout();
    });

    // test.skip('R1: Special Non-DW | Verify Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

    // SPKB-2807, SPKB-2808
    test('R1: Special(Multi) Non-DW | Verify Buyer Can Lower/Higher Bids and Re-Submit Bids via Excel Import', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA1001WHL", userRole.User_AA1001WHL);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "996", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "996", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "996", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "996", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA1001WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
    });

    // test.skip('R1: Special Non-DW | Verify Re-Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })
});


test.describe.serial('R1: Special(Multi) DW Buyer Tests', () => {
    let exportFile: string;

    test('R1: Special DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA2000DW);
        await base['welcomePage'].selectBuyerCode("AA2000DW");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    //SPKB-407
    test('R1: Special DW | Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA900DW", userRole.User_AA900DW);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    //SPKB-2956
    test('R1: Special DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA2000DW");
    });

    //SPKB-2956
    test('R1: Special DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const displayedMinimumBid = await base['auc_dataGridDashBoardPage'].getMinimumBidDisplay();
        expect(displayedMinimumBid).toBe(auction_config.minimum_bid_price);
    });

    //SPKB-3241
    test('R1: Special DW | Verify DW Buyer Can Sort by Target Price or Avail. Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const isSorted = await base['auc_dataGridDashBoardPage'].sortByTargetPrice('descending');
        await base['page'].waitForTimeout(2000);
        expect(isSorted).toBeTruthy();
    });

    //SPKB-2961
    test('R1: Special DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        await base['auc_dataGridDashBoardPage'].sortByTargetPrice('ascending');
        await base['page'].waitForTimeout(2000);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        for (let i = 0; i < 3; i++) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(i);
            const targetPrice = Number(bidData.targetPrice.replace('$', ''));
            Logger(`Row ${i}: Target Price = ${targetPrice}, Minimum Bid Price = ${minimumBidPrice}`);
            expect(targetPrice).toBeGreaterThanOrEqual(minimumBidPrice);
        }
    });

    //SPKB-3242
    test('R1: Special DW | Verify DW Buyer Can Filter by ProductID and Grade', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const filterResult = await base['auc_dataGridDashBoardPage'].filterByProductIDAndGrade("12238", "F_NYN/H_NNN");
        expect(filterResult).toBeTruthy();
    });

    //SPKB-2958
    test('R1: Special DW | Validate Total Qty Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        expect(bidData.availQty).toBe(matchingInventory?.dwQty);
    });

    //SPKB-2959
    test('R1: Special DW | Validate Additional Qty Added to Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        // Validate grade A_YYY
        const bidDataA = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "A_YYY");
        const matchingInventoryA = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "A_YYY"
        );
        const additionalQtyA = base['auc_inventoryPage'].getAdditionalQty("12238", "A_YYY", "DW");
        const expectedAvailQtyA = String(Number(matchingInventoryA?.dwQty || 0) + additionalQtyA);
        expect(bidDataA.availQty).toBe(expectedAvailQtyA);
        // Validate grade B_NYY/D_NNY
        const bidDataB = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", "B_NYY/D_NNY");
        const matchingInventoryB = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "B_NYY/D_NNY"
        );
        const additionalQtyB = base['auc_inventoryPage'].getAdditionalQty("12238", "B_NYY/D_NNY", "DW");
        const expectedAvailQtyB = String(Number(matchingInventoryB?.dwQty || 0) + additionalQtyB);
        expect(bidDataB.availQty).toBe(expectedAvailQtyB);
    });

    //SPKB-2960
    test('R1: Special DW | Verify DW Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByFilter("12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(bidData.targetPrice).toBe(matchingInventory?.dwTargetPrice);
        }
    });

    //SPKB-3240
    test('R1: Special DW | Verify Buyer Cannot Submit Zero Bid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        const noBidModalAppeared = await base['auc_dataGridDashBoardPage'].isNoBidModalAppeared();
        expect(noBidModalAppeared).toBe(true);
    });

    //SPKB-3238
    test('R1: Special DW | Verify DW Buyer Cannot Enter Negative Values for Price and Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "-100", qty: "-5" }]);
        await base['page'].waitForTimeout(1000);
        const bidData = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidData.price).toBe("0.00"); // Price should remain default
        expect(bidData.qty).toBe(""); // Qty should remain blank
        Logger(`After entering negative values - Price: ${bidData.price}, Qty: ${bidData.qty}`);
    });

    //SPKB-3239
    test('R1: Special DW | Verify DW Buyer Cannot Enter Decimal Values for Qty', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        await base['auc_dataGridDashBoardPage'].placeBids([{ price: "100", qty: "5.5" }]);
        await base['page'].waitForTimeout(1000);
        const alertLocator = base['page'].locator("//div[@class='alert alert-danger mx-validation-message']");
        await expect(alertLocator).toBeVisible();
        const alertMessage = await alertLocator.textContent();
        expect(alertMessage?.toLowerCase()).toContain("invalid number");
        Logger(`Validation message displayed for decimal qty: ${alertMessage}`);
    });

    //SPKB-2963, SPKB-2964
    test('R1: Special DW | Validate Minimum Bid Validation Modal Popup on Submission via Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "6" }, { price: "1500", qty: "6" }, { price: "1500", qty: "6" },
            { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }, { price: "0.99", qty: "5" }
        ]);
        expect(bidsBlowMinimum).toBe(3);
    });

    test.skip('R1: Special DW | Verify Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2965
    test('R1: Special DW | Verify Buyer Can Lower Bids and Re-Submit on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2000DW", userRole.User_AA2000DW);
        await base['auc_dataGridDashBoardPage'].sortByProductID("ascending");
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "995", qty: "5" }, { price: "995", qty: "5" }, { price: "995", qty: "5" },
            { price: "995", qty: "5" }, { price: "995", qty: "5" }, { price: "995", qty: "5" },
            { price: "995", qty: "5" }, { price: "995", qty: "5" }, { price: "994.99", qty: " " }
        ]);
        expect(bidsBlowMinimum).toBe(0);
        await base['loginPage'].logout();
    });

    test.skip('R1: Special DW | Verify Re-Submitted-Bids Data Sends to SharePoint', async () => {
    })

    //SPKB-2966
    test('R1: Special DW | Verify Buyer Can Re-Access Auction without Losing Unsubmitted Bids', async () => {
        await base['loginPage'].loginAs(userRole.User_AA2004DW);
        await base['welcomePage'].selectBuyerCode("AA2004DW");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        await base['auc_dataGridDashBoardPage'].placeBids([
            { price: "1200", qty: "5" }, { price: "1200", qty: "5" }
        ]);
        await base['loginPage'].logout();
        await base['page'].waitForTimeout(2000);
        await base['loginPage'].loginAs(userRole.User_AA2004DW);
        await base['welcomePage'].selectBuyerCode("AA2004DW");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const bidDataRow0 = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        expect(bidDataRow0.price).toBe("1200");
        expect(bidDataRow0.qty).toBe("5");
        Logger(`Unsubmitted bid on row 0: Price = ${bidDataRow0.price}, Qty = ${bidDataRow0.qty}`);
        await base['loginPage'].logout();
    });

    //SPKB-
    test('R1: Special DW | Verify Buyer Can Export Bid Data', async () => {
        await base['loginPage'].loginAs(userRole.User_AA2003DW);
        await base['welcomePage'].selectBuyerCode("AA2003DW");
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    //SPKB-2957
    test('R1: Special DW | Validate Excel Sheet Format and Column Headers', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        }
        const { validateExcelColumns } = await import('../../utils/helpers/data_utils');
        const { headers, hasValidFormat } = validateExcelColumns(exportFile);
        const expectedColumns = [
            "ProductId", "Brand", "Model", "Model_Name", "Grade",
            "Carrier", "Added", "Avail. Qty", "Target Price", "Price", "Qty. Cap"
        ];
        for (const expectedColumn of expectedColumns) {
            expect(headers).toContain(expectedColumn);
        }
        expect(hasValidFormat).toBeTruthy();
        Logger(`Validated Excel columns: ${headers.join(', ')}`);
    });

    //SPKB-2958
    test('R1: Special DW | Validate Total Qty Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const excelData = getExcelRowData(exportFile, "12238", "F_NYN/H_NNN");
        const matchingInventory = inventoryData.find(item =>
            item.productID === "12238" && item.grade === "F_NYN/H_NNN"
        );
        console.log("Excel Data:", excelData);
        console.log("Matching Inventory:", matchingInventory);
        expect(excelData).not.toBeNull();
        expect(excelData?.availQty).toBe(matchingInventory?.dwQty);
        Logger(`Excel Avail.Qty: ${excelData?.availQty}, Inventory TotalQty: ${matchingInventory?.dwQty}`);
    });

    //SPKB-2959
    test('R1: Special DW | Validate Additional Qty Added to Total Qty Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            const additionalQty = base['auc_inventoryPage'].getAdditionalQty("12238", grade, "DW");
            const expectedAvailQty = String(Number(matchingInventory?.dwQty || 0) + additionalQty);
            expect(excelData).not.toBeNull();
            expect(excelData?.availQty).toBe(expectedAvailQty);
            Logger(`Grade ${grade} - Excel Avail.Qty: ${excelData?.availQty}, Expected: ${expectedAvailQty}`);
        }
    });

    //SPKB-2960
    test('R1: Special DW | Verify DW Target Price Display Correctly on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        }
        if (!inventoryData || inventoryData.length === 0) {
            await base['loginPage'].loginAs(userRole.ADMIN);
            inventoryData = await base['auc_inventoryPage'].getInventoryDataByProductID("12238");
            await base['loginPage'].logout();
        }
        const { getExcelRowData } = await import('../../utils/helpers/data_utils');
        const gradesToValidate = ["A_YYY", "B_NYY/D_NNY", "C_YNY/G_YNN"];
        for (const grade of gradesToValidate) {
            const excelData = getExcelRowData(exportFile, "12238", grade);
            const matchingInventory = inventoryData.find(item =>
                item.productID === "12238" && item.grade === grade
            );
            expect(excelData).not.toBeNull();
            expect(excelData?.targetPrice).toBe(matchingInventory?.dwTargetPrice);
            Logger(`Grade ${grade} - Excel Target Price: ${excelData?.targetPrice}, Inventory: ${matchingInventory?.dwTargetPrice}`);
        }
    });

    //SPKB-2961
    test('R1: Special DW | Validate Target Price Display Cannot Be Less Than Minimum Bid Price on Excel Sheet', async () => {
        if (!exportFile) {
            exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        }
        const { getAllTargetPricesFromExcel } = await import('../../utils/helpers/data_utils');
        const targetPrices = getAllTargetPricesFromExcel(exportFile);
        const minimumBidPrice = Number(auction_config.minimum_bid_price);
        expect(targetPrices.length).toBeGreaterThan(0);
        for (const price of targetPrices) {
            expect(price).toBeGreaterThanOrEqual(minimumBidPrice);
        }
        Logger(`Validated ${targetPrices.length} target prices - all >= ${minimumBidPrice}`);
    });

    //SPKB-2967, SPKB-2961
    test('R1: Special DW | Validate Submitted Bids Row Count and Validate Minimum Bid Validation Message', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2003DW", userRole.User_AA2003DW);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "1200", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "0.99", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const bidsBlowMinimum = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(bidsBlowMinimum).toBe(2);
        await base['loginPage'].logout();
    });

    // test.skip('R1: Reg DW | Verify Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })

    // SPKB-2968, SPKB-2969
    test('R1: Special DW | Verify Buyer Can Lower/Higher Bids and Re-Submit Bids via Excel Import', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA2003DW", userRole.User_AA2003DW);
        const importBidsArray =
            [{ productID: "12238", grade: "A_YYY", bidPrice: "995", bidQty: "5" },
            { productID: "12238", grade: "B_NYY/D_NNY", bidPrice: "995", bidQty: "5" },
            { productID: "12238", grade: "C_YNY/G_YNN", bidPrice: "995", bidQty: "5" },
            { productID: "12238", grade: "E_YYN", bidPrice: "995", bidQty: "5" },
            { productID: "12238", grade: "F_NYN/H_NNN", bidPrice: "0.99", bidQty: "5" }];
        exportFile = user_data.User_AA2003DW.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length;
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
    });

    // test.skip('R1: Reg DW | Verify Re-Submitted-Bids Data via Import Sends to SharePoint', async () => {
    // })
});

```
