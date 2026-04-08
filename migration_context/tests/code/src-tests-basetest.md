# Test Fixture: BaseTest.ts

- **Path**: `src\tests\BaseTest.ts`
- **Category**: Test Fixture
- **Lines**: 114
- **Size**: 5,759 bytes
- **Members**: `class BaseTest`, `setup`, `teardown`

## Source Code

```typescript
import * as dotenv from 'dotenv';
dotenv.config({ path: '.env.secrets' }); // Load Snowflake secrets from global-setup
dotenv.config(); // Standard .env for local overrides

import { Page } from '@playwright/test';
// import { AuctionRoundOnePage } from '../pages/buyer-pages/AuctionRoundOnePage';
import { NavMenuPage } from '../pages/CommonPages/NavMenuPage';
import { LoginPage } from '../pages/CommonPages/LoginPage';
import user_data from '../utils/resources/user_data.json';
import { logger } from '../utils/helpers/data_utils';

import { InventoryPage } from '../pages/Auction/AdminPages/InventoryPage';
import { ACC_AuctionSchedulePage } from '../pages/Auction/AdminPages/ACC_AuctionSchedulePage';
import { ACC_BidDataPage } from '../pages/Auction/AdminPages/ACC_BidDataPage';
import { ACC_RoundTwoCriteriaPage } from '../pages/Auction/AdminPages/ACC_RoundTwoCriteriaPage';
import { AUC_DataGridDashBoardPage } from '../pages/Auction/BuyerPages/AUC_DataGridDashBoardPage';
import { ACC_QualifiedBuyerCodePage } from '../pages/Auction/AdminPages/ACC_QualifiedBuyerCodePage';
import { SharePointPage } from '../pages/Auction/AdminPages/SharePointPage';

import { PWS_NavMenu_AsAdmin } from '../pages/PWS/AdminPages/PWS_NavMenu_AsAdmin';
import { PWS_ShopPage } from '../pages/PWS/BuyerPages/PWS_ShopPage';
import { PWS_CartPage } from '../pages/PWS/BuyerPages/PWS_CartPage';
import { PWS_OfferQueuePages } from '../pages/PWS/AdminPages/PWS_OfferQueuePages';
import { PWS_OfferDetailsPage } from '../pages/PWS/AdminPages/PWS_OfferDetailPage';
import { PWS_CounterOfferPage } from '../pages/PWS/BuyerPages/PWS_CounterOfferPage';
import { PWS_DataCenter_OfferItemsPage } from '../pages/PWS/AdminPages/PWS_DataCenter_OfferItemsPage';
import { PWS_DataCenter_DevicesPage as PWS_DataCenter_DevicesPage } from '../pages/PWS/AdminPages/PWS_DataCenter_DevicesPage';
import { DeposcoAPI } from '../pages/PWS/AdminPages/DeposcoAPI';
import { AUC_RoundThreeReportPage } from '../pages/Auction/AdminPages/RoundThreeBidReportPage';
import { PWS_NavMenu_AsBuyer } from '../pages/PWS/BuyerPages/PWS_NavMenu_AsBuyer';
import { PWS_RMAPage } from '../pages/PWS/BuyerPages/PWS_RMAPage';
import { PWS_OrdersPage } from '../pages/PWS/BuyerPages/PWS_OrdersPage';
import { DeposcoLoginPage } from '../pages/Deposco/DeposcoLoginPage';
import { DeposcoOrdersPage } from '../pages/Deposco/DeposcoOrdersPage';
import { WelcomePage } from '../pages/CommonPages/WelcomePage';



export class BaseTest {
    private readonly url: string = user_data.baseURL;
    protected page: Page;
    protected loginPage: LoginPage;
    protected welcomePage: WelcomePage;
    protected navMenuPage: NavMenuPage;

    protected auc_inventoryPage: InventoryPage;
    protected auc_dataGridDashBoardPage: AUC_DataGridDashBoardPage;
    protected auc_RoundThreeReportPage: AUC_RoundThreeReportPage
    protected auc_acc_SchedulingPage: ACC_AuctionSchedulePage;
    protected auc_acc_BidDataPage: ACC_BidDataPage;
    protected auc_acc_Round2CriteriaPage: ACC_RoundTwoCriteriaPage;
    protected auc_acc_QualifiedBuyerCodePage: ACC_QualifiedBuyerCodePage;
    protected sharePointPage: SharePointPage;

    protected pws_navMenuAsAdmin: PWS_NavMenu_AsAdmin;
    protected pws_navMenuAsBuyer: PWS_NavMenu_AsBuyer;
    protected pws_shopPage: PWS_ShopPage;
    protected pws_CartPage: PWS_CartPage;
    protected pws_OfferQueuePage: PWS_OfferQueuePages;
    protected pws_OfferDetailsPage: PWS_OfferDetailsPage;
    protected pws_CounterOfferPage: PWS_CounterOfferPage;
    protected pws_dataCenter_devicesPage: PWS_DataCenter_DevicesPage;
    protected pws_dataCenter_OfferItemsPage: PWS_DataCenter_OfferItemsPage;
    protected deposcoAPI: DeposcoAPI;
    protected deposcoLoginPage: DeposcoLoginPage;
    protected deposcoOrdersPage: DeposcoOrdersPage;
    protected pws_RMAPage: PWS_RMAPage;
    protected pws_OrdersPage: PWS_OrdersPage;


    constructor(page: Page) {
        this.page = page;
        this.loginPage = new LoginPage(page);
        this.welcomePage = new WelcomePage(page);
        this.navMenuPage = new NavMenuPage(page);

        this.auc_dataGridDashBoardPage = new AUC_DataGridDashBoardPage(page);
        this.auc_RoundThreeReportPage = new AUC_RoundThreeReportPage(page);
        this.auc_acc_SchedulingPage = new ACC_AuctionSchedulePage(page);
        this.auc_acc_BidDataPage = new ACC_BidDataPage(page);
        this.auc_acc_Round2CriteriaPage = new ACC_RoundTwoCriteriaPage(page);
        this.auc_acc_QualifiedBuyerCodePage = new ACC_QualifiedBuyerCodePage(page);
        this.auc_inventoryPage = new InventoryPage(page);
        this.sharePointPage = new SharePointPage(page);


        this.pws_navMenuAsAdmin = new PWS_NavMenu_AsAdmin(page);
        this.pws_navMenuAsBuyer = new PWS_NavMenu_AsBuyer(page);
        this.pws_shopPage = new PWS_ShopPage(page);
        this.pws_CartPage = new PWS_CartPage(page);
        this.pws_OfferQueuePage = new PWS_OfferQueuePages(page);
        this.pws_OfferDetailsPage = new PWS_OfferDetailsPage(page);
        this.pws_CounterOfferPage = new PWS_CounterOfferPage(page);
        this.pws_dataCenter_OfferItemsPage = new PWS_DataCenter_OfferItemsPage(page);
        this.pws_dataCenter_devicesPage = new PWS_DataCenter_DevicesPage(page);
        this.deposcoAPI = new DeposcoAPI();
        this.deposcoLoginPage = new DeposcoLoginPage(page);
        this.deposcoOrdersPage = new DeposcoOrdersPage(page);

        this.pws_RMAPage = new PWS_RMAPage(page);
        this.pws_OrdersPage = new PWS_OrdersPage(page);
    }

    async setup() {
        logger.info("TEST START")
        this.page.context().clearCookies;
        await this.page.goto(this.url);
    }

    async teardown() {
        await this.page.close();
    }
}

```
