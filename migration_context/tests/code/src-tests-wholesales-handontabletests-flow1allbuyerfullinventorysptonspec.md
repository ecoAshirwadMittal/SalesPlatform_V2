# Test Spec: Flow1_AllBuyer_FullInventory_SptON.spec.ts

- **Path**: `src\tests\Wholesales\HandOnTableTests\Flow1_AllBuyer_FullInventory_SptON.spec.ts`
- **Category**: Test Spec
- **Lines**: 242
- **Size**: 13,377 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';

let base: BaseTest;
test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
    await base['loginPage'].loginAs(userRole.ADMIN);
});

// Details of all flows are in Confluence here: https://gazelle.atlassian.net/wiki/spaces/~7120209847cc53ce024443ae6408dfacf2ddaf/pages/4565139457/Auction+Automation

test.describe('SPKB-1856: Sales Can Setup R2 Criteria | Flow - All Buyers, Full Inventory, STB Flag ON @auctions-regression', () => {

    test('Sales Can Setup R2 Criteria and Start Auction R1', async () => {
        await base['auc_acc_Round2CriteriaPage'].selectRegularBuyerSettings('All Buyers', 'Full Inventory', 'Yes');
        await base['auc_acc_SchedulingPage'].createAuctionAndStartRoundOne();
        Logger("Setup R2 Criteria completed and Auction Round 1 started.");
    });

    test('R1 Live | Buyers Can Place Bids on Hand-On Table and Submit Bids', async () => {
        Logger("Buyer One: AA155WHL - All Bids Qualify for TGP");
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA155WHL");  // AA155WHL
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" }, { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]); await base['page'].waitForTimeout(3000);
        Logger("Buyer Two: AA156WHL - Some Bids Qualify for TGP, Some Do Not Qualify for TGP");
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA156WHL");   // AA156WHL
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" }, { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]); await base['page'].waitForTimeout(3000);
        Logger("Buyer Three: AA157WHL - Some Bids Qualify for TGP, Some Do Not Qualify for TGP");
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA157WHL");   // AA157WHL
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "0.99", qty: "1" }, { price: "0.99", qty: "1" }
        ]); await base['page'].waitForTimeout(3000);
        Logger("Buyer Three: AA158WHL - Some Bids Qualify for TGP, Some Do Not Qualify for TGP");
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA158WHL");   // AA158WHL
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "0.99", qty: "1" }, { price: "0.99", qty: "1" }
        ]); await base['page'].waitForTimeout(3000);
        Logger("SPT Buyer One: HN - All Bids Qualify for TGP");
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("HN");     // HN
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" }, { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]); await base['page'].waitForTimeout(3000);
    });

    test('Close Auction Round 1', async () => {
        await base['auc_acc_SchedulingPage'].endAuctionRoundBySchedule(1);
    });

    //============================= Between R1 and R2 =============================//

    test('After R1 Ends | Sales Can Access R1', async () => {
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA159WHL");
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeTruthy();
        Logger("Sales can access to R1 after it ends.");
    });

    test('After R1 Ends | Sales Can Submit Bids on Behalf of Buyer', async () => {
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA159WHL");
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" }, { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]); await base['page'].waitForTimeout(3000);
    });

    test('After R1 Ends | Buyer Can NOT Access R1', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeFalsy();
        Logger("Buyer can NOT access R1 after it ends.");
    });

    test('After R1 Ends | Sales Can Remove Bids - AA155WHL', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].clickAuctionControlTab("Bid Data");
        await base['auc_acc_BidDataPage'].selectBidDataByRoundAndBuyerCode("Round 1", "AA155WHL");
        await base['auc_acc_BidDataPage'].removeBidByBidPrice("0.99");
        await base['page'].waitForTimeout(5000);
        const isBidRemoved = await base['auc_acc_BidDataPage'].isSelectedBidRemoved();
        expect(isBidRemoved).toBeTruthy();
    });

    test('After R1 Ends | Sales Can Qualify Buyer Code for R2', async () => {
        await base['navMenuPage'].clickAuctionControlTab("Qualified Buyer Codes");
        await base['auc_acc_QualifiedBuyerCodePage'].selectQualifyBuyerListbyWeekAndRound("Round 2");
        await base['page'].waitForTimeout(3000);
        const qualifyType = await base['auc_acc_QualifiedBuyerCodePage'].isBuyerCodeQualified("AA157WHL");
        expect(qualifyType).toBeTruthy();
    });

    test('After R1 Ends | Sales Can Unqualify Buyer Code', async () => {
        await base['navMenuPage'].clickAuctionControlTab("Qualified Buyer Codes");
        await base['auc_acc_QualifiedBuyerCodePage'].selectQualifyBuyerListbyWeekAndRound("Round 2");
        await base['page'].waitForTimeout(3000);
        await base['auc_acc_QualifiedBuyerCodePage'].setBuyerCheckbox("AA158WHL", "Uncheck");
        const qualifyType = await base['auc_acc_QualifiedBuyerCodePage'].getBuyerCodeQualificationType("AA158WHL");
        expect(qualifyType).toBe("Manual");
    });

    //========================================= R2 TESTS =============================//

    test('Start Auction Round 2', async () => {
        await base['auc_acc_SchedulingPage'].startAuctionRoundBySchedule(2);
    });

    test('R2 Live | Buyer Who Did Not Submit Bids in R1 Can Access R2', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA159WHL);
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeTruthy();
        Logger("Qualify buyer can access to R2.");
    });

    test('R2 Live | Buyer Who Did Not Submit Bids in R1 Can View Full Inventory ', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA159WHL);
        const isFullInventory = await base['auc_dashBoardPage'].isFullInventory();
        expect(isFullInventory).toBeTruthy();
    });

    test('R2 Live | Buyer Who Did Not Submit Bids in R1 Can Submit Bids in R2', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA159WHL);
        await base['auc_dashBoardPage'].placeBidsOnRoundOneTableAndSubmit([
            { price: "1000", qty: "1" }, { price: "1000", qty: "1" }, { price: "0.99", qty: "1" }, { price: "0.99", qty: "" }
        ]); await base['page'].waitForTimeout(3000);
    });

    test('R2 Live | Manually Unqualified Buyer Can NOT Access R2 ', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA158WHL);
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeFalsy();
        Logger("Unqualify buyer can NOT access to R2.");
    });

    test('R2 Live | Qualified Buyer Who Submitted Bids in R1 Can Access R2', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA156WHL);
        const isFullInventory = await base['auc_dashBoardPage'].isFullInventory();
        expect(isFullInventory).toBeTruthy();
    });

    test('R2 Live | Qualified Buyer Who Submitted Bids in R1 Can See Full Inventory with R1 Bids', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA156WHL);
        const isQualifiedBidPresent = await base['auc_dashBoardPage'].isBidPricePresentInTopRows("1000", 6);
        expect(isQualifiedBidPresent).toBeTruthy();
        const isUnqualifiedBidPresent = await base['auc_dashBoardPage'].isBidPricePresentInTopRows("0.99", 6);
        expect(isUnqualifiedBidPresent).toBeTruthy();
    });

    test('R2 Live | Removed Bids Not Reappear on Hand-on Table in R2 - AA155WHL', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA155WHL");
        const isBidPresent = await base['auc_dashBoardPage'].isBidPricePresentInTopRows("0.99", 6);
        expect(isBidPresent).toBeFalsy();
    });

    test('R2 Live | Sales Can Unqualify Buyers During R2 is Live', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].clickAuctionControlTab("Qualified Buyer Codes");
        await base['auc_acc_QualifiedBuyerCodePage'].selectQualifyBuyerListbyWeekAndRound("Round 2");
        await base['page'].waitForTimeout(3000);
        await base['auc_acc_QualifiedBuyerCodePage'].setBuyerCheckbox("AA160WHL", "Uncheck");
        const qualifyType = await base['auc_acc_QualifiedBuyerCodePage'].getBuyerCodeQualificationType("AA160WHL");
        expect(qualifyType).toBe("Manual");
    });

    test('R2 Live | Manually Unqualify Buyer Can NOT Access R2', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA160WHL);
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeFalsy();
        Logger("Unqualify buyer can NOT access to R2.");
    });

    test('Close Auction Round 2', async () => {
        await base['auc_acc_SchedulingPage'].endAuctionRoundBySchedule(2);
    });

    //============================ Between R2 and R3 =============================//

    test('After R2 Ends | Sales Can Access R2', async () => {
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin("AA155WHL");
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeTruthy();
        Logger("Sales can access to R2 after it ends.");
    });

    test('After R2 Ends | Buyer Can NOT Access R2', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        const isHandOnTableVisible = await base['auc_dashBoardPage'].isHandOnTableDisplayed();
        expect(isHandOnTableVisible).toBeFalsy();
        Logger("Buyer can NOT access R2 after it ends.");
    });

    test('After R2 Ends | Sales Can Remove Bids - AA159WHL', async () => {
        await base['loginPage'].logout();
        await base['loginPage'].loginAs(userRole.ADMIN);
        await base['navMenuPage'].clickAuctionControlTab("Bid Data");
        await base['auc_acc_BidDataPage'].selectBidDataByRoundAndBuyerCode("Round 2", "AA159WHL");
        await base['auc_acc_BidDataPage'].removeBidByBidPrice("0.99");
        await base['page'].waitForTimeout(5000);
        const isBidRemoved = await base['auc_acc_BidDataPage'].isSelectedBidRemoved();
        expect(isBidRemoved).toBeTruthy();
    });

    test('After R2 Ends | Sales Can Qualify Buyer Code for R3', async () => {
        await base['navMenuPage'].clickAuctionControlTab("Qualified Buyer Codes");
        await base['auc_acc_QualifiedBuyerCodePage'].selectQualifyBuyerListbyWeekAndRound("Upsell Round");
        await base['page'].waitForTimeout(3000);
        await base['auc_acc_QualifiedBuyerCodePage'].setBuyerCheckbox("QASI", "Check");
        const qualifyType = await base['auc_acc_QualifiedBuyerCodePage'].getBuyerCodeQualificationType("QASI");
        expect(qualifyType).toBe("Manual");
    });

    //========================================= R3 TESTS =============================//

    test('Start Auction Upsell Round', async () => {
        await base['auc_acc_SchedulingPage'].startAuctionRoundBySchedule(3);
    });

    test('R3 Live | Sales Can View Qualified Buyer List', async () => {
        await base['navMenuPage'].chooseSubNav_Reports('Round Three Bid Report by Buyer');
        await base['auc_RoundThreeReportPage'].selectSecondWeekFromDropdown();
        const isBuyerCodePresent = await base['auc_RoundThreeReportPage'].isBuyerCodePresentInReport("QASI");
        expect(isBuyerCodePresent).toBeTruthy();
    });
});

```
