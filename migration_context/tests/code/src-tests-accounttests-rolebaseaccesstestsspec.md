# Test Spec: RoleBaseAccessTests.spec.ts

- **Path**: `src\tests\AccountTests\RoleBaseAccessTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 1310
- **Size**: 81,787 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../BaseTest';
import { navTabs, userRole } from '../../utils/resources/enum';
import { Logger } from '../../utils/helpers/data_utils';

let base: BaseTest;

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
})

test.describe("Buyer Code-Based Access Control Tests @auctions-regression @pws-regression", () => {
    test('SPKB-1403: Verify Admin Landing on Home Page Preference', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN);
        const title = await base['page'].title();
        await base['page'].waitForTimeout(3000);
        expect(title).toBe("ecoATM Direct - Inventory Snowflake");
        await base['loginPage'].logout();
    });

    test('SPKB-1404: Verify 3 Action Columns Not Displaying without Choosing BuyerCode', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_Three);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Inventory");
        const isOfferPriceDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
        expect(isOfferPriceDisplayed).toBeFalsy();
    });

    test('SPKB-1483: Verify Sales Rep See Only PWS BuyerCode Under ChooseBuyerCode Dropdown', async () => {
        ///await base['pws_InventoryPage'].switchBuyerInputField('NBPWS');
        const isOfferPriceDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
        expect(isOfferPriceDisplayed).toBeTruthy();
    });

    test('SPKB-380: Verify PWS Buyer Code Access PWS Page', async () => {
        Logger("Validating PWS Buyer Code Access to PWS Page");
        await base['loginPage'].loginAs(userRole.PWS_UserOne);
        await expect(base['page']).toHaveTitle("ecoATM Direct - Order");
    })
    test('SPKB-690: Verify Non PWS Buyer Code Can\'t Access PWS Page', async () => {
        Logger("Validating Non PWS Buyer Code Access to PWS Page");
        await base['loginPage'].loginAs(userRole.User_AA155WHL);
        await expect(base['page']).not.toHaveTitle('ecoATM Direct - Order');
    })
    test('SPKB-375: Verify SalesRep Can Access PWS Page', async () => {
        Logger("Validatiing SalesRep Access to PWS Page");
        await base['loginPage'].loginAs(userRole.ADMIN_One);
        await base['navMenuPage'].chooseMainNav(navTabs.BidAsBider);
        await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin('AAPW');
        await expect(base['page']).toHaveTitle("ecoATM Direct - Order");
        const isTableVisible = await base['pws_shopPage'].verifyAndLogPwsDataGridTableisplay("SalesRep can see PWS Inventory table");
        expect(isTableVisible).toBeTruthy();
    });
});

test.describe("Welcome Screen and View-As Tests @auctions-regression @pws-regression", () => {
    const AUCTION_DASHBOARD_TITLE = "ecoATM Direct - Bidder Dashboard";
    const singleWHL = { "email": "ecoauc.automation160@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation160", "buyerCodes": "AA160WHL" };
    const singleDW = { "email": "podw01@fexpost.com", "password": "Test100%", "buyerName": "podw01", "buyerCodes": "NB_DW" };
    const singlePWS = { "email": "eco_pws@proton.me", "password": "Pass123!", "buyerName": "eco_pws", "buyerCodes": "1PWS" };
    const singleWHL_singleDW = { "email": "ecoauc.automation167@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation167", "buyerCodes": ["AA167WHL", "AA167DW"] };
    const singleWHL_singlePWS = { "email": "ecoauc.automation164@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation164", "buyerCodes": ["AA164WHL", "AA164PWS"] };
    const singleDW_singlePWS = { "email": "newuser9.2.0@fexpost.com", "password": "Test100%", "buyerName": "newUserTest Release9.2.0", "buyerCodes": ["AA161", "PWS9.2.0"] };
    const singleWHL_singleDW_singlePWS = { "email": "ecoauc.automation169@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation169", "buyerCodes": ["AA169WHL", "AA169DW", "AA169PWS"] };
    const WHL_multipleCodeBuyer = { "email": "ecoauc.automation162@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation162", "buyerCodes": ["AA162WHL", "AA162WHL2"] };
    const DW_multipleCodeBuyer = { "email": "ecoauc.automation163@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation163", "buyerCodes": ["AA163DW", "AA163DW2"] };
    const PWS_multipleCodeBuyer = { "email": "newusertest1@fexpost.com", "password": "Test100%", "buyerName": "New User Testing", "buyerCodes": ["NewPWS01", "NewPWS02"] };
    const singleWHL_multiPWS = { "email": "ecoauc.automation172@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation172", "buyerCodes": ["AA172WHL", "AA172PWS", "AA172PWS2"] };
    const singleWHL_multiDW = { "email": "ecoauc.automation173@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation173", "buyerCodes": ["AA173WHL", "AA173DW", "AA173DW2"] };
    const singleDW_multiPWS = { "email": "ecoauc.automation175@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation175", "buyerCodes": ["AA175DW", "AA175PWS", "AA175PWS2"] };
    const singleDW_multiWHL = { "email": "ecoauc.automation174@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation175", "buyerCodes": ["AA174WHL", "AA174WHL2", "AA174DW"] };
    const singlePWS_multiWHL = { "email": "ecoauc.automation165@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation165", "buyerCodes": ["AA165WHL", "AA165WHL2", "AA165PWS"] };
    const singlePWS_multiDW = { "email": "ecoauc.automation166@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation166", "buyerCodes": ["AA166DW", "AA166DW2", "AA166PWS"] };
    const multiWHL_multiDW = { "email": "ecoauc.automation168@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation168", "buyerCodes": ["AA168WHL", "AA168WHL2", "AA168DW", "AA168DW2"] };
    const multiWHL_multiPWS = { "email": "ecoauc.automation176@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation178", "buyerCodes": ["AA176WHL", "AA176WHL2", "AA176PWS", "AA176PWS2"] };
    const multiDW_multiPWS = { "email": "ecoauc.automation177@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation179", "buyerCodes": ["AA177DW", "AA177DW2", "AA177PWS", "AA177PWS2"] };
    const multiWHL_multiDW_singlePWS = { "email": "ecoauc.automation170@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation170", "buyerCodes": ["AA170WHL", "AA170WHL2", "AA170DW", "AA170DW2", "AA170PWS"] };
    const multiWHL_multiPWS_singleDW = { "email": "ecoauc.automation178@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation178", "buyerCodes": ["AA178WHL", "AA178WHL2", "AA178DW", "AA178PWS", "AA178PWS2"] };
    const multiDW_multiPWS_singleWHL = { "email": "ecoauc.automation179@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation179", "buyerCodes": ["AA179WHL", "AA179DW", "AA179DW2", "AA179PWS", "AA179PWS2"] };
    const multiWHL_multiDW_multiPWS = { "email": "ecoauc.automation171@anything.com", "password": "ecoATM123$", "buyerName": "ecoauc.automation171", "buyerCodes": ["AA171WHL", "AA171WHL2", "AA171DW", "AA171DW2", "AA171PWS", "AA171PWS2"] };


    test.describe('Single Buyer Code', () => {
        test('Single_WHL: Verify Single_WHL Buyer Directly Landing on Auction Dashboard', async () => {
            await base['loginPage'].login(singleWHL.email, singleWHL.password);
            const auctionMenu = base['page'].locator("//a[@title='Auction']");
            await auctionMenu.waitFor({ state: 'visible', timeout: 5000 });
            expect(await auctionMenu.isVisible()).toBeTruthy();
            await base['loginPage'].logout();
        });

        test('Single_DW: Verify Single_DW Buyer Directly Landing on Auction Dashboard', async () => {
            await base['loginPage'].login(singleDW.email, singleDW.password);
            const auctionMenu = base['page'].locator("//a[@title='Auction']");
            await auctionMenu.waitFor({ state: 'visible', timeout: 5000 });
            expect(await auctionMenu.isVisible()).toBeTruthy();
            await base['loginPage'].logout();
        });

        test('Single_PWS: Verify Single_PWS Buyer Directly Landing on Shop Page', async () => {
            await base['loginPage'].login(singlePWS.email, singlePWS.password);
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe("ecoATM Direct - Inventory");
            await base['loginPage'].logoutFromPWS();
        });
    })


    test.describe('SingleWHL_SingleDW', () => {
        test('SingleWHL_SingleDW: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleWHL_singleDW.email, singleWHL_singleDW.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleWHL_singleDW.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('SingleWHL_SingleDW: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW.email, singleWHL_singleDW.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('SingleWHL_SingleDW: Verify PWS Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW.email, singleWHL_singleDW.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeFalsy();
        });

        test('SingleWHL_SingleDW: Verify WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW.email, singleWHL_singleDW.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA167WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleWHL_SingleDW: Verify DW Buyer Code Not Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW.email, singleWHL_singleDW.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA167DW");
            expect(isBuyerCodeExist).toBeFalsy();
        });

        test('SingleWHL_SingleDW: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW.email, singleWHL_singleDW.password);
            await base['welcomePage'].selectBuyerCode("AA167WHL");
            const isAuctionDashboardVisible = await base['welcomePage'].isAuctionDashboardVisible();
            expect(isAuctionDashboardVisible).toBeTruthy();
            await base['loginPage'].logout();
        });

        test('SingleWHL_SingleDW: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(singleWHL_singleDW.email, singleWHL_singleDW.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA167DW");
            const isAuctionDashboardVisible = await base['welcomePage'].isAuctionDashboardVisible();
            expect(isAuctionDashboardVisible).toBeTruthy();
            await base['loginPage'].logout();
        });
    });


    test.describe('SingleWHL_SinglePWS', () => {
        test('SingleWHL_SinglePWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleWHL_singlePWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('SingleWHL_SinglePWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('SingleWHL_SinglePWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('SingleWHL_SinglePWS: Verify WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA164WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleWHL_SinglePWS: Verify PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA164PWS");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleWHL_SinglePWS: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            await base['welcomePage'].selectBuyerCode("AA164WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('SingleWHL_SinglePWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(singleWHL_singlePWS.email, singleWHL_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA164PWS");
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('SingleWHL_SinglePWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singleWHL_singlePWS.email, singleWHL_singlePWS.password, "AA164PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA164PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('SingleDW_SinglePWS', () => {
        test('SingleDW_SinglePWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleDW_singlePWS.email, singleDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleDW_singlePWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('SingleDW_SinglePWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_singlePWS.email, singleDW_singlePWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('SingleDW_SinglePWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_singlePWS.email, singleDW_singlePWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('SingleDW_SinglePWS: Verify DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_singlePWS.email, singleDW_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA161");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleDW_SinglePWS: Verify PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_singlePWS.email, singleDW_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("PWS9.2.0");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleDW_SinglePWS: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_singlePWS.email, singleDW_singlePWS.password);
            await base['welcomePage'].selectBuyerCode("AA161");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('SingleDW_SinglePWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(singleDW_singlePWS.email, singleDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("PWS9.2.0");
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('SingleDW_SinglePWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singleDW_singlePWS.email, singleDW_singlePWS.password, "PWS9.2.0");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("PWS9.2.0");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('SingleWHL_SingleDW_SinglePWS', () => {
        test('SingleWHL_SingleDW_SinglePWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleWHL_singleDW_singlePWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA169WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA169DW");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA169PWS");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            await base['welcomePage'].selectBuyerCode("AA169WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA169DW");
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA169PWS");
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('SingleWHL_SingleDW_SinglePWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singleWHL_singleDW_singlePWS.email, singleWHL_singleDW_singlePWS.password, "AA169PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA169PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('WHL_multipleCodeBuyer', () => {
        test('WHL_multipleCodeBuyer: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(WHL_multipleCodeBuyer.email, WHL_multipleCodeBuyer.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(WHL_multipleCodeBuyer.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('WHL_multipleCodeBuyer: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(WHL_multipleCodeBuyer.email, WHL_multipleCodeBuyer.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('WHL_multipleCodeBuyer: Verify PWS Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(WHL_multipleCodeBuyer.email, WHL_multipleCodeBuyer.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeFalsy();
        });

        test('WHL_multipleCodeBuyer: Verify First WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(WHL_multipleCodeBuyer.email, WHL_multipleCodeBuyer.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA162WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('WHL_multipleCodeBuyer: Verify Second WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(WHL_multipleCodeBuyer.email, WHL_multipleCodeBuyer.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA162WHL2");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('WHL_multipleCodeBuyer: Verify Selecting Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(WHL_multipleCodeBuyer.email, WHL_multipleCodeBuyer.password);
            await base['welcomePage'].selectBuyerCode("AA162WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('DW_multipleCodeBuyer', () => {
        test('DW_multipleCodeBuyer: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(DW_multipleCodeBuyer.email, DW_multipleCodeBuyer.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(DW_multipleCodeBuyer.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('DW_multipleCodeBuyer: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(DW_multipleCodeBuyer.email, DW_multipleCodeBuyer.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('DW_multipleCodeBuyer: Verify PWS Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(DW_multipleCodeBuyer.email, DW_multipleCodeBuyer.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeFalsy();
        });

        test('DW_multipleCodeBuyer: Verify First DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(DW_multipleCodeBuyer.email, DW_multipleCodeBuyer.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA163DW");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('DW_multipleCodeBuyer: Verify Second DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(DW_multipleCodeBuyer.email, DW_multipleCodeBuyer.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA163DW2");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('DW_multipleCodeBuyer: Verify Selecting Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(DW_multipleCodeBuyer.email, DW_multipleCodeBuyer.password);
            await base['welcomePage'].selectBuyerCode("AA163DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('PWS_multipleCodeBuyer', () => {
        test('PWS_multipleCodeBuyer: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(PWS_multipleCodeBuyer.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('PWS_multipleCodeBuyer: Verify Auction Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeFalsy();
        });

        test('PWS_multipleCodeBuyer: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('PWS_multipleCodeBuyer: Verify First PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("NewPWS01");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('PWS_multipleCodeBuyer: Verify Second PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("NewPWS02");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('PWS_multipleCodeBuyer: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password);
            await base['welcomePage'].selectBuyerCode("NewPWS01");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('PWS_multipleCodeBuyer: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, PWS_multipleCodeBuyer.email, PWS_multipleCodeBuyer.password, "NewPWS01");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("NewPWS01");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('singleWHL_multiPWS', () => {
        test('singleWHL_multiPWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleWHL_multiPWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('singleWHL_multiPWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('singleWHL_multiPWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('singleWHL_multiPWS: Verify WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA172WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('singleWHL_multiPWS: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA172PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA172PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('singleWHL_multiPWS: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            await base['welcomePage'].selectBuyerCode("AA172WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('singleWHL_multiPWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(singleWHL_multiPWS.email, singleWHL_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA172PWS");
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('singleWHL_multiPWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singleWHL_multiPWS.email, singleWHL_multiPWS.password, "AA172PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA172PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('singleWHL_multiDW', () => {
        test('singleWHL_multiDW: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleWHL_multiDW.email, singleWHL_multiDW.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleWHL_multiDW.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('singleWHL_multiDW: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiDW.email, singleWHL_multiDW.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('singleWHL_multiDW: Verify PWS Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiDW.email, singleWHL_multiDW.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeFalsy();
        });

        test('singleWHL_multiDW: Verify WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiDW.email, singleWHL_multiDW.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA173WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('singleWHL_multiDW: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiDW.email, singleWHL_multiDW.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA173DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA173DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('singleWHL_multiDW: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleWHL_multiDW.email, singleWHL_multiDW.password);
            await base['welcomePage'].selectBuyerCode("AA173WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('singleWHL_multiDW: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(singleWHL_multiDW.email, singleWHL_multiDW.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA173DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('singleDW_multiPWS', () => {
        test('singleDW_multiPWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleDW_multiPWS.email, singleDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleDW_multiPWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('singleDW_multiPWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiPWS.email, singleDW_multiPWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('singleDW_multiPWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiPWS.email, singleDW_multiPWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('singleDW_multiPWS: Verify DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiPWS.email, singleDW_multiPWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA175DW");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('singleDW_multiPWS: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiPWS.email, singleDW_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA175PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA175PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('singleDW_multiPWS: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiPWS.email, singleDW_multiPWS.password);
            await base['welcomePage'].selectBuyerCode("AA175DW");
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('singleDW_multiPWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(singleDW_multiPWS.email, singleDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA175PWS");
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('singleDW_multiPWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singleDW_multiPWS.email, singleDW_multiPWS.password, "AA175PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA175PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('singleDW_multiWHL', () => {
        test('singleDW_multiWHL: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singleDW_multiWHL.email, singleDW_multiWHL.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singleDW_multiWHL.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('singleDW_multiWHL: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiWHL.email, singleDW_multiWHL.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('singleDW_multiWHL: Verify PWS Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiWHL.email, singleDW_multiWHL.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeFalsy();
        });

        test('singleDW_multiWHL: Verify DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiWHL.email, singleDW_multiWHL.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA174DW");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('singleDW_multiWHL: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiWHL.email, singleDW_multiWHL.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA174WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA174WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('singleDW_multiWHL: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singleDW_multiWHL.email, singleDW_multiWHL.password);
            await base['welcomePage'].selectBuyerCode("AA174DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('singleDW_multiWHL: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(singleDW_multiWHL.email, singleDW_multiWHL.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA174WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('singlePWS_multiWHL', () => {
        test('singlePWS_multiWHL: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singlePWS_multiWHL.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('singlePWS_multiWHL: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('singlePWS_multiWHL: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('singlePWS_multiWHL: Verify PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA165PWS");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('singlePWS_multiWHL: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA165WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA165WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('singlePWS_multiWHL: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            await base['welcomePage'].selectBuyerCode("AA165PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('singlePWS_multiWHL: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singlePWS_multiWHL.email, singlePWS_multiWHL.password, "AA165PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA165PWS");
            await base['loginPage'].logoutFromPWS();
        });

        test('singlePWS_multiWHL: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(singlePWS_multiWHL.email, singlePWS_multiWHL.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA165WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('singlePWS_multiDW', () => {
        test('singlePWS_multiDW: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(singlePWS_multiDW.email, singlePWS_multiDW.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(singlePWS_multiDW.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('singlePWS_multiDW: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiDW.email, singlePWS_multiDW.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('singlePWS_multiDW: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiDW.email, singlePWS_multiDW.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('singlePWS_multiDW: Verify PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiDW.email, singlePWS_multiDW.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA166PWS");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('singlePWS_multiDW: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiDW.email, singlePWS_multiDW.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA166DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA166DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('singlePWS_multiDW: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(singlePWS_multiDW.email, singlePWS_multiDW.password);
            await base['welcomePage'].selectBuyerCode("AA166PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('singlePWS_multiDW: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, singlePWS_multiDW.email, singlePWS_multiDW.password, "AA166PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA166PWS");
            await base['loginPage'].logoutFromPWS();
        });

        test('singlePWS_multiDW: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(singlePWS_multiDW.email, singlePWS_multiDW.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA166DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('multiWHL_multiDW', () => {
        test('multiWHL_multiDW: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiWHL_multiDW.email, multiWHL_multiDW.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiWHL_multiDW.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiWHL_multiDW: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW.email, multiWHL_multiDW.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiDW: Verify PWS Section Not Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW.email, multiWHL_multiDW.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeFalsy();
        });

        test('multiWHL_multiDW: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW.email, multiWHL_multiDW.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA168WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA168WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW.email, multiWHL_multiDW.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA168DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA168DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW.email, multiWHL_multiDW.password);
            await base['welcomePage'].selectBuyerCode("AA168WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiDW: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(multiWHL_multiDW.email, multiWHL_multiDW.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA168DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });
    });


    test.describe('multiWHL_multiPWS', () => {
        test('multiWHL_multiPWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiWHL_multiPWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiWHL_multiPWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiPWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiPWS: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA176WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA176WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiPWS: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA176PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA176PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiPWS: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            await base['welcomePage'].selectBuyerCode("AA178WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiPWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(multiWHL_multiPWS.email, multiWHL_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA176PWS");
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('multiWHL_multiPWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, multiWHL_multiPWS.email, multiWHL_multiPWS.password, "AA176PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA176PWS");
            await base['loginPage'].logoutFromPWS();
        });

    });


    test.describe('multiDW_multiPWS', () => {
        test('multiDW_multiPWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiDW_multiPWS.email, multiDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiDW_multiPWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiDW_multiPWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS.email, multiDW_multiPWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiDW_multiPWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS.email, multiDW_multiPWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('multiDW_multiPWS: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS.email, multiDW_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA177DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA177DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiDW_multiPWS: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS.email, multiDW_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA177PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA177PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiDW_multiPWS: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS.email, multiDW_multiPWS.password);
            await base['welcomePage'].selectBuyerCode("AA177DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiDW_multiPWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(multiDW_multiPWS.email, multiDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA177PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('multiDW_multiPWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, multiDW_multiPWS.email, multiDW_multiPWS.password, "AA177PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA177PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('multiWHL_multiDW_singlePWS', () => {
        test('multiWHL_multiDW_singlePWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiWHL_multiDW_singlePWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA170WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA170WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA170DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA170DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify PWS Buyer Code Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            const isBuyerCodeExist = await base['welcomePage'].isPWSBuyerCodeExist("AA170PWS");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            await base['welcomePage'].selectBuyerCode("AA170WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiDW_singlePWS: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA170DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiDW_singlePWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA170PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('multiWHL_multiDW_singlePWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, multiWHL_multiDW_singlePWS.email, multiWHL_multiDW_singlePWS.password, "AA170PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA170PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('multiWHL_multiPWS_singleDW', () => {
        test('multiWHL_multiPWS_singleDW: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiWHL_multiPWS_singleDW.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA178WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA178WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify DW Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA178DW");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA178PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA178PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            await base['welcomePage'].selectBuyerCode("AA178WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiPWS_singleDW: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA178DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiPWS_singleDW: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA178PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('multiWHL_multiPWS_singleDW: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, multiWHL_multiPWS_singleDW.email, multiWHL_multiPWS_singleDW.password, "AA178PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA178PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('multiDW_multiPWS_singleWHL', () => {
        test('multiDW_multiPWS_singleWHL: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiDW_multiPWS_singleWHL.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify WHL Buyer Code Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            const isBuyerCodeExist = await base['welcomePage'].isAUCBuyerCodeExist("AA179WHL");
            expect(isBuyerCodeExist).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA179DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA179DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA179PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA179PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            await base['welcomePage'].selectBuyerCode("AA179WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiDW_multiPWS_singleWHL: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA179DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiDW_multiPWS_singleWHL: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA179PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('multiDW_multiPWS_singleWHL: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, multiDW_multiPWS_singleWHL.email, multiDW_multiPWS_singleWHL.password, "AA179PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA179PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });


    test.describe('multiWHL_multiDW_multiPWS', () => {
        test('multiWHL_multiDW_multiPWS: Verify Buyer Name Displaying on Welcome Page', async () => {
            await base['loginPage'].login(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            const isBuyerNameDisplaying = await base['welcomePage'].isBuyerNameInWelcomeHeader(multiWHL_multiDW_multiPWS.buyerName);
            expect(isBuyerNameDisplaying).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify Auction Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            const isAuctionSectionVisible = await base['welcomePage'].isAUCSectionVisible();
            expect(isAuctionSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify PWS Section Displaying', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            const isPWSSectionVisible = await base['welcomePage'].isPWSSectionVisible();
            expect(isPWSSectionVisible).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify WHL Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA171WHL");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA171WHL2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify DW Buyer Codes Displaying on Auction Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA171DW");
            const isBuyerCode2Exist = await base['welcomePage'].isAUCBuyerCodeExist("AA171DW2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify PWS Buyer Codes Displaying on PWS Section', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            const isBuyerCode1Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA171PWS");
            const isBuyerCode2Exist = await base['welcomePage'].isPWSBuyerCodeExist("AA171PWS2");
            expect(isBuyerCode1Exist).toBeTruthy();
            expect(isBuyerCode2Exist).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify Selecting WHL Buyer Code Going to Auction Dashboard', async () => {
            await base['welcomePage'].ensureUserOnWelcomePage(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            await base['welcomePage'].selectBuyerCode("AA171WHL");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiDW_multiPWS: Verify Selecting DW Buyer Code Going to Auction Dashboard', async () => {
            await base['loginPage'].login(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA171DW");
            await base['page'].waitForTimeout(3000);
            expect(await base['page'].title()).toBe(AUCTION_DASHBOARD_TITLE);
            await base['loginPage'].logout();
        });

        test('multiWHL_multiDW_multiPWS: Verify Selecting PWS Buyer Code Going to Shop Page', async () => {
            await base['loginPage'].login(multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password);
            await base['page'].waitForTimeout(3000);
            await base['welcomePage'].selectBuyerCode("AA171PWS");
            await base['page'].waitForTimeout(3000);
            const isPWSDataGridDisplayed = await base['pws_shopPage'].isOfferPriceColumnDisplayed();
            expect(isPWSDataGridDisplayed).toBeTruthy();
        });

        test('multiWHL_multiDW_multiPWS: Verify View-As Displaying Correct Buyer Code', async () => {
            await base['pws_shopPage'].ensureUserOnShopPage2(base, multiWHL_multiDW_multiPWS.email, multiWHL_multiDW_multiPWS.password, "AA171PWS");
            const viewAsBuyerCode = await base['pws_shopPage'].getBuyerFromViewAs();
            expect(viewAsBuyerCode).toContain("AA171PWS");
            await base['loginPage'].logoutFromPWS();
        });
    });
});

```
