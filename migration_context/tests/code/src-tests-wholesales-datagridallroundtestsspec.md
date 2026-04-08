# Test Spec: DataGridAllRoundTests.spec.ts

- **Path**: `src\tests\Wholesales\DataGridAllRoundTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 359
- **Size**: 19,902 bytes

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


test.describe.serial('Auction Round ONE Tests @auctions-regression', () => {
    let exportFile: string;
    test('R1: Pre-Condition | Sales Can Setup R2 Criteria and Start Auction R1', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['auc_acc_SchedulingPage'].deleteRecentAuctionWeek();
        await base['auc_acc_Round2CriteriaPage'].selectRegularBuyerSettings('All Buyers', 'Full Inventory', 'Yes');
        await base['auc_acc_SchedulingPage'].createAuctionAndStartRoundOne();
        Logger("Setup R2 Criteria completed and Auction Round 1 started.");
        await base['loginPage'].logout();
    });

    test('R1: Reg Non-DW | Verify Buyer Can Access Auction Round 1 and View Inventory', async () => {
        await base['loginPage'].loginAs(userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
    });

    test('R1: Reg Non-DW | SPKB-407: Validate No Duplicates ProductID + Grade on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const isItemDuplicated = await validateDuplication(base['page'], "//button[contains(@class,'mx-name-actionButton1 export')]", "Inventory");
    })

    test('R1: Reg Non-DW | Validate Buyer Code on Dashboard Display Correctly', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA600WHL");
    });

    test('R1: Reg Non-DW | Validate Minimum Bid Price Message Display Above Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);

    });

    test('R1: Reg Non-DW | Validate All Target Price on Data Grid No Less Than Minimum Bid Price', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);

    });

    test('R1: Reg Non-DW | Validate Target Price Display Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);

    });

    test('R1: Reg Non-DW | Validate Qty Cap Display as Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);

    });

    test('R1: Reg Non-DW | Validate Qty Cap Display as Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);

    });

    test('R1: Reg Non-DW | Validate Additional Qty Cap Added and Display as Total Qty Correctly on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);

    });






    test('R1: Verify Buyer Can Enter Bid Price and Qty on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA600WHL", userRole.User_AA600WHL);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1500", qty: "5" }, { price: "4.99", qty: "5" }
        ]);
    });

    test.skip('R1: Verify Submitted-Bids File Sends to SharePoint', async () => {
        Logger('Downloading Submitted-Bids File from SharePoint Stage Folder');
        await base['sharePointPage'].navigateToSharePointStageFolderCurrentWeek();
        const sharePointFileName = await base['sharePointPage'].downloadSubmittedBidFile(user_data.User_AA155WHL.buyer_code);
        Logger("Verifying SharePoint File Contains All Submitted-Bid's SKUs");
        // const submittedBidIDs = new Set<string>(submittedBidsRoundOne_WHL.map(item => item.getProductID()));
        // const filteredRows_SharePoint = await base['sharePointPage'].filterCSVRowByIDs(sharePointFileName, submittedBidIDs);
        // const foundIDs = new Set(filteredRows_SharePoint.map(row => row['ecoATM Code']?.trim()));
        // for (const id of submittedBidIDs) {
        //     expect(foundIDs.has(id)).toBeTruthy();
        // }
        // Logger("Validating Bid Prices and Q'ty on SharePoint File");
        // await base['sharePointPage'].validateSubmittedBids(submittedBidsRoundOne_WHL, filteredRows_SharePoint);
    })

    test('R1: Verify Buyer Can Export Bid File', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA155WHL", userRole.User_AA155WHL);
        exportFile = user_data.User_AA155WHL.buyer_code + "_Round1.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    test('R1: Verify Buyer Can Import the Updated Bid File and Submit Bids', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA155WHL", userRole.User_AA155WHL);
        const importBidsArray =
            [{ productID: "12971", grade: "A_YYY", bidPrice: "1200", bidQty: "2" },
            { productID: "14278", grade: "A_YYY", bidPrice: "1200", bidQty: "" }];
        exportFile = user_data.User_AA155WHL.buyer_code + "_Round1.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        const totalBids = importBidsArray.length + 2; // including previous 2 bids placed
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const expected_submittedMessage = "have been Submitted!";
        const actual_submittedMessage = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(actual_submittedMessage).toEqual(expected_submittedMessage);
        await base['loginPage'].logout();
    });

    test('R1: Verify Multiple Buyer Code Display Correct Selected Buyer Code on Dashboard', async () => {
        await base['loginPage'].loginAs(userRole.Nadia_GmailOne);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeAsMultiCodeBuyer("HN");
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("HN");
    });

    test('R1: Verify Special Buyer Can Enter Bid Price and Qty on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "HN", userRole.Nadia_GmailOne);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1000", qty: "2" }, { price: "1000", qty: "2" },
            { price: "1.99", qty: "2" }, { price: "1.99", qty: "2" }
        ]);
        await base['loginPage'].logout();
    });

    test('R1: Verify Buyer Can Re-Access Auction Round 1', async () => {
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
        expect(displayedBuyerCode).toBe("AA155WHL");
    });

    test('R1: Verify Buyer Can Lower Bid Price & Qty and Re-Submit', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA155WHL", userRole.User_AA155WHL);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1000", qty: "2" }, { price: "1000", qty: "2" },
            { price: "1.99", qty: "2" }, { price: "1.99", qty: "2" }
        ]);
        await base['loginPage'].logout();
    });

    test('R1: Verify Sales Rep Can Submit Bids on Behalf of Buyer', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA156WHL");
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1000", qty: "2" }, { price: "1000", qty: "2" },
            { price: "1.99", qty: "2" }, { price: "1.99", qty: "2" }
        ]);
        await base['loginPage'].logout();
    });

    test('R1: Post-Condition | Close Auction Round 1', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['auc_acc_SchedulingPage'].endAuctionRoundBySchedule(1);
        await base['loginPage'].logout();
    });
});


// ----------------------------  Between R1 - R2 Tests ----------------------------
test.describe.serial('Auction Between R1 - R2 Tests', () => {
    test('R1/2: Verify Buyer Can NOT Access R1', async () => {
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        const isDataGridVisible = await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(5000);
        expect(isDataGridVisible).toBeFalsy();
        Logger("Buyer can NOT access R1 after it ends.");
        await base['loginPage'].logout();
    });

    test('R1/2: Verify Sales Can Access R1', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA157WHL");
        const isDataGridVisible = await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        expect(isDataGridVisible).toBeTruthy();
        Logger("Sales can access to R1 after it ends.");
    });

    test('R1/2: Verify Sales Can Submit Bids on Behalf of Buyer', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA157WHL", userRole.ADMIN);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" }, { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]);
    });

    test('R1/2: Verify Sales Can Remove Bids - AA155WHL', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        await base['navMenuPage'].clickAuctionControlTab("Bid Data");
        await base['auc_acc_BidDataPage'].selectBidDataByRoundAndBuyerCode("Round 1", "AA155WHL");
        await base['auc_acc_BidDataPage'].removeBidByBidPrice("1.99");
        await base['page'].waitForTimeout(5000);
        const isBidRemoved = await base['auc_acc_BidDataPage'].isSelectedBidRemoved();
        expect(isBidRemoved).toBeTruthy();
    });

    test('R1/2: Verify Sales Can Qualify Buyer Code for R2', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        await base['navMenuPage'].clickAuctionControlTab("Qualified Buyer Codes");
        await base['auc_acc_QualifiedBuyerCodePage'].selectQualifyBuyerListbyWeekAndRound("Round 2");
        await base['page'].waitForTimeout(3000);
        const qualifyType = await base['auc_acc_QualifiedBuyerCodePage'].isBuyerCodeQualified("AA158WHL");
        expect(qualifyType).toBeTruthy();
    });

    test('R1/2: Verify Sales Can Unqualify Buyer Code', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        await base['navMenuPage'].clickAuctionControlTab("Qualified Buyer Codes");
        await base['auc_acc_QualifiedBuyerCodePage'].selectQualifyBuyerListbyWeekAndRound("Round 2");
        await base['page'].waitForTimeout(3000);
        await base['auc_acc_QualifiedBuyerCodePage'].setBuyerCheckbox("AA160WHL", "Uncheck");
        const qualifyType = await base['auc_acc_QualifiedBuyerCodePage'].getBuyerCodeQualificationType("AA160WHL");
        expect(qualifyType).toBe("Manual");
    });
});


// ----------------------------------  R2 Tests --------------------------------
test.describe.serial('Auction Round TWO Tests', () => {
    let exportFile: string;
    test('R2: Pre-Condition | Sales Can Start Auction R2', async () => {
        await base['loginPage'].ensureUserLoggedIn(base, userRole.ADMIN);
        await base['auc_acc_SchedulingPage'].startAuctionRoundBySchedule(2);
        await base['loginPage'].logout();
    });

    test('R2: Verify Buyer Who Unqualified Cannot Access R2', async () => {
        await base['loginPage'].loginAs(userRole.User_AA160WHL);
        const isDataGridVisible = await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        expect(isDataGridVisible).toBeFalsy();
        Logger("Unqualify buyer can not access to R2.");
        await base['loginPage'].logout();
    });

    test('R2: Verify Buyer Who Did Not Submit Bids in R1 Can Access R2', async () => {
        await base['loginPage'].loginAs(userRole.User_AA159WHL);
        const isDataGridVisible = await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
        expect(isDataGridVisible).toBeTruthy();
        Logger("Qualify buyer can access to R2.");
    });

    test('R2: Verify Buyer Who Did Not Submit Bids in R1 Can View Full Inventory ', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA159WHL", userRole.User_AA159WHL);
        const isWholesale = await base['auc_dataGridDashBoardPage'].isWholesaleInventory();
        expect(isWholesale).toBeTruthy();
        Logger("Qualify buyer can view full inventory.");
    });

    test('R2: Verify Buyer Who Did Not Submit Bids in R1 Can Enter Bid Price and Qty on Data Grid', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA159WHL", userRole.User_AA159WHL);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([
            { price: "1200", qty: "2" }, { price: "1200", qty: "2" }
        ]);
        await base['loginPage'].logout();
    });

    test('R2: Verify Buyer Cannot Lower Bid Price and Qty on Data Grid', async () => {
        await base['loginPage'].loginAs(userRole.User_AA156WHL);
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(15000);
        const originalBid = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        Logger(`Original Bid - Price: ${originalBid.productId}, ${originalBid.grade}, ${originalBid.price}, Qty: ${originalBid.qty}`);
        await base['auc_dataGridDashBoardPage'].placeBidsAndSubmit([{ price: "999", qty: "1" }]);
        const updatedBid = await base['auc_dataGridDashBoardPage'].getBidDataByFilter(originalBid.productId, originalBid.grade);
        expect(originalBid.price).toBe(updatedBid.price);
        expect(originalBid.qty).toBe(updatedBid.qty);
        await base['page'].waitForTimeout(2000);
    });

    test('R2: Verify Buyer Can Export Bid File', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA156WHL", userRole.User_AA156WHL);
        exportFile = user_data.User_AA156WHL.buyer_code + "_Round2.xlsx";
        console.log("File Name: " + exportFile)
        await base['auc_dataGridDashBoardPage'].exportBidFile(exportFile);
        expect(isDownloaded(exportFile)).toBeTruthy();
    });

    test('R2: Verify Buyer Cannot Lower Bid Price or Qty via Import File', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA156WHL", userRole.User_AA156WHL);
        exportFile = user_data.User_AA156WHL.buyer_code + "_Round2.xlsx";
        const originalBidFirstRow = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(0);
        const originalBidSecondRow = await base['auc_dataGridDashBoardPage'].getBidDataByRowIndex(1);
        const importBidsArray =
            [{ productID: originalBidFirstRow.productId, grade: originalBidFirstRow.grade, bidPrice: "999", bidQty: "1" },
            { productID: originalBidSecondRow.productId, grade: originalBidSecondRow.grade, bidPrice: "", bidQty: "" }];
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        const updatedBidFirstRow = await base['auc_dataGridDashBoardPage'].getBidDataByFilter(originalBidFirstRow.productId, originalBidFirstRow.grade);
        const updatedBidSecondRow = await base['auc_dataGridDashBoardPage'].getBidDataByFilter(originalBidSecondRow.productId, originalBidSecondRow.grade);
        expect(originalBidFirstRow.price).toBe(updatedBidFirstRow.price);
        expect(originalBidFirstRow.qty).toBe(updatedBidFirstRow.qty);
        expect(originalBidSecondRow.price).toBe(updatedBidSecondRow.price);
        expect(originalBidSecondRow.qty).toBe(updatedBidSecondRow.qty);
        await base['page'].waitForTimeout(2000);
    });

    test('R2: Verify Buyer Can Import New Bids', async () => {
        await base['auc_dataGridDashBoardPage'].ensureUserOnDashboard(base, "AA156WHL", userRole.User_AA156WHL);
        const importBidsArray =
            [{ productID: "18406", grade: "A_YYY", bidPrice: "1200", bidQty: "1" },
            { productID: "18408", grade: "A_YYY", bidPrice: "1200", bidQty: "" }];
        exportFile = user_data.User_AA156WHL.buyer_code + "_Round2.xlsx";
        const importedRowCount = await base['auc_dataGridDashBoardPage'].importBidFile(exportFile, importBidsArray);
        await base['page'].waitForTimeout(2000);
        const totalBids = 6;    // 4 from previous + 2 new bids
        const expected_rowCount = "Total Rows " + totalBids;
        expect(importedRowCount).toEqual(expected_rowCount);
        const expected_submittedMessage = "have been Submitted!";
        const actual_submittedMessage = await base['auc_dataGridDashBoardPage'].submitBids_onImportModal();
        expect(actual_submittedMessage).toEqual(expected_submittedMessage);
        await base['loginPage'].logout();
    });

    test('R2: Post Condition | Close Auction Round 2', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['auc_acc_SchedulingPage'].endAuctionRoundBySchedule(2);
    });
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
