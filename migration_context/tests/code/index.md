# Sales Platform Playwright Code — Index

> **71** source files crawled from `qa-playwright-salesplatform`.

## Summary

| Category | Files | Total Lines |
|----------|-------|-------------|
| Config | 3 | 100 |
| Global Setup | 1 | 92 |
| Page Object | 28 | 5,486 |
| Test Fixture | 1 | 114 |
| Test Spec | 23 | 8,625 |
| Utility | 10 | 1,776 |
| Workflow Spec | 5 | 591 |

## Config

| File | Path | Lines | Members |
|------|------|-------|---------|
| [package.json](./package.md) | `package.json` | 36 |  |
| [playwright.config.ts](./playwrightconfig.md) | `playwright.config.ts` | 50 |  |
| [tsconfig.json](./tsconfig.md) | `tsconfig.json` | 14 |  |

## Global Setup

| File | Path | Lines | Members |
|------|------|-------|---------|
| [global-setup.ts](./global-setup.md) | `global-setup.ts` | 92 | globalSetup |

## Page Object

| File | Path | Lines | Members |
|------|------|-------|---------|
| [ACC_AuctionSchedulePage.ts](./src-pages-auction-adminpages-accauctionschedulepage.md) | `src\pages\Auction\AdminPages\ACC_AuctionSchedulePage.ts` | 290 | class ACC_AuctionSchedulePage, createAuctionAndStartRoundOne, formatDate, formatTime |
| [ACC_BidDataPage.ts](./src-pages-auction-adminpages-accbiddatapage.md) | `src\pages\Auction\AdminPages\ACC_BidDataPage.ts` | 91 | class ACC_BidDataPage |
| [ACC_QualifiedBuyerCodePage.ts](./src-pages-auction-adminpages-accqualifiedbuyercodepage.md) | `src\pages\Auction\AdminPages\ACC_QualifiedBuyerCodePage.ts` | 105 | class ACC_QualifiedBuyerCodePage |
| [ACC_RoundTwoCriteriaPage.ts](./src-pages-auction-adminpages-accroundtwocriteriapage.md) | `src\pages\Auction\AdminPages\ACC_RoundTwoCriteriaPage.ts` | 44 | class ACC_RoundTwoCriteriaPage |
| [InventoryPage.ts](./src-pages-auction-adminpages-inventorypage.md) | `src\pages\Auction\AdminPages\InventoryPage.ts` | 143 | class InventoryPage, validateDuplication, getAdditionalQty |
| [RoundThreeBidReportPage.ts](./src-pages-auction-adminpages-roundthreebidreportpage.md) | `src\pages\Auction\AdminPages\RoundThreeBidReportPage.ts` | 41 | class AUC_RoundThreeReportPage |
| [SharePointPage.ts](./src-pages-auction-adminpages-sharepointpage.md) | `src\pages\Auction\AdminPages\SharePointPage.ts` | 141 | class SharePointPage, navigateToSharePointStageFolderCurrentWeek, validateSubmittedBids, navigateToSharePointSite, loginToSharePoint (+1 more) |
| [AUC_DataGridDashBoardPage.ts](./src-pages-auction-buyerpages-aucdatagriddashboardpage.md) | `src\pages\Auction\BuyerPages\AUC_DataGridDashBoardPage.ts` | 387 | class AUC_DataGridDashBoardPage, exportBidFile, downloadRound1Bids_AfterEnded, getBidDataByFilter, getBidDataByRowIndex |
| [LoginPage.ts](./src-pages-commonpages-loginpage.md) | `src\pages\CommonPages\LoginPage.ts` | 234 | class LoginPage, loginAs, login, handlePossibleSecondSignIn, clickLink (+3 more) |
| [NavMenuPage.ts](./src-pages-commonpages-navmenupage.md) | `src\pages\CommonPages\NavMenuPage.ts` | 161 | class NavMenuPage, chooseMainNav, chooseSubNav_Reports, chooseSubNav_UnderAdminMainNav, chooseSubNav_UnderSettingMainNav (+2 more) |
| [TempMailPage.ts](./src-pages-commonpages-tempmailpage.md) | `src\pages\CommonPages\TempMailPage.ts` | 79 | class TempMailPage |
| [WelcomePage.ts](./src-pages-commonpages-welcomepage.md) | `src\pages\CommonPages\WelcomePage.ts` | 116 | class WelcomePage, ensureUserOnWelcomePage |
| [DeposcoLoginPage.ts](./src-pages-deposco-deposcologinpage.md) | `src\pages\Deposco\DeposcoLoginPage.ts` | 96 | class DeposcoLoginPage |
| [DeposcoOrdersPage.ts](./src-pages-deposco-deposcoorderspage.md) | `src\pages\Deposco\DeposcoOrdersPage.ts` | 284 | class DeposcoOrdersPage |
| [index.ts](./src-pages-deposco-index.md) | `src\pages\Deposco\index.ts` | 7 |  |
| [DeposcoAPI.ts](./src-pages-pws-adminpages-deposcoapi.md) | `src\pages\PWS\AdminPages\DeposcoAPI.ts` | 134 | class DeposcoAPI |
| [PWS_DataCenter_DevicesPage.ts](./src-pages-pws-adminpages-pwsdatacenterdevicespage.md) | `src\pages\PWS\AdminPages\PWS_DataCenter_DevicesPage.ts` | 59 | class PWS_DataCenter_DevicesPage, goToPWSDevicesPage, getATPAndReservedQtyBySKU |
| [PWS_DataCenter_OfferItemsPage.ts](./src-pages-pws-adminpages-pwsdatacenterofferitemspage.md) | `src\pages\PWS\AdminPages\PWS_DataCenter_OfferItemsPage.ts` | 28 | class PWS_DataCenter_OfferItemsPage, clearOffersFromOfferQueue |
| [PWS_NavMenu_AsAdmin.ts](./src-pages-pws-adminpages-pwsnavmenuasadmin.md) | `src\pages\PWS\AdminPages\PWS_NavMenu_AsAdmin.ts` | 48 | class PWS_NavMenu_AsAdmin, chooseNavMenu |
| [PWS_OfferDetailPage.ts](./src-pages-pws-adminpages-pwsofferdetailpage.md) | `src\pages\PWS\AdminPages\PWS_OfferDetailPage.ts` | 235 | class PWS_OfferDetailsPage, ensureUserOnOfferDetailsPage, navigateToOfferDetailsPage, getOriginalOfferSummary, getCounterOfferSummary (+7 more) |
| [PWS_OfferQueuePages.ts](./src-pages-pws-adminpages-pwsofferqueuepages.md) | `src\pages\PWS\AdminPages\PWS_OfferQueuePages.ts` | 125 | class PWS_OfferQueuePages, ensureUserOnOfferQueuePage, chooseOfferStatusTab, findAndClickOfferByID, clickFirstRowOfferID |
| [PWS_PricingPage.ts](./src-pages-pws-adminpages-pwspricingpage.md) | `src\pages\PWS\AdminPages\PWS_PricingPage.ts` | 1 |  |
| [PWS_CartPage.ts](./src-pages-pws-buyerpages-pwscartpage.md) | `src\pages\PWS\BuyerPages\PWS_CartPage.ts` | 236 | class PWS_CartPage, ensureUserOnCartPage, clickSubmitButton, clickAlmostDoneSubmitButton, clickAlmostDoneGoBackLink (+1 more) |
| [PWS_CounterOfferPage.ts](./src-pages-pws-buyerpages-pwscounterofferpage.md) | `src\pages\PWS\BuyerPages\PWS_CounterOfferPage.ts` | 276 | class PWS_CounterOfferPage, clickFirstRowOfferID, findAndClickOfferByID, selectCounterActionByRowIndex, clickSubmitResponseButton (+4 more) |
| [PWS_NavMenu_AsBuyer.ts](./src-pages-pws-buyerpages-pwsnavmenuasbuyer.md) | `src\pages\PWS\BuyerPages\PWS_NavMenu_AsBuyer.ts` | 43 | class PWS_NavMenu_AsBuyer, chooseNavMenu |
| [PWS_OrdersPage.ts](./src-pages-pws-buyerpages-pwsorderspage.md) | `src\pages\PWS\BuyerPages\PWS_OrdersPage.ts` | 98 | class PWS_OrdersPage, ensureUserOnOrdersPage, getOrderRowDetails |
| [PWS_RMAPage.ts](./src-pages-pws-buyerpages-pwsrmapage.md) | `src\pages\PWS\BuyerPages\PWS_RMAPage.ts` | 1077 | class PWS_RMAPage, getIMEIFromShippedOrder, getFirstRMARowData, clickFirstRowAndVerifyDetails, completeRMAFlowWithDynamicIMEI |
| [PWS_ShopPage.ts](./src-pages-pws-buyerpages-pwsshoppage.md) | `src\pages\PWS\BuyerPages\PWS_ShopPage.ts` | 907 | class PWS_ShopPage, ensureUserOnShopPage, ensureUserOnShopPage2, sortAvlQty, clickCartButton (+6 more) |

## Test Fixture

| File | Path | Lines | Members |
|------|------|-------|---------|
| [BaseTest.ts](./src-tests-basetest.md) | `src\tests\BaseTest.ts` | 114 | class BaseTest, setup, teardown |

## Test Spec

| File | Path | Lines | Members |
|------|------|-------|---------|
| [LoginTests.spec.ts](./src-tests-accounttests-logintestsspec.md) | `src\tests\AccountTests\LoginTests.spec.ts` | 132 |  |
| [RoleBaseAccessTests.spec.ts](./src-tests-accounttests-rolebaseaccesstestsspec.md) | `src\tests\AccountTests\RoleBaseAccessTests.spec.ts` | 1310 |  |
| [ATP_Tests.spec.ts](./src-tests-premiumwholesales-pwsfunctionaltests-atptestsspec.md) | `src\tests\PremiumWholesales\PWS_FunctionalTests\ATP_Tests.spec.ts` | 55 |  |
| [InventoryAndCartFunctionalTests.spec.ts](./src-tests-premiumwholesales-pwsfunctionaltests-inventoryandcartfunctionaltestsspec.md) | `src\tests\PremiumWholesales\PWS_FunctionalTests\InventoryAndCartFunctionalTests.spec.ts` | 362 |  |
| [OfferDetailFunctionalTests.spec.ts](./src-tests-premiumwholesales-pwsfunctionaltests-offerdetailfunctionaltestsspec.md) | `src\tests\PremiumWholesales\PWS_FunctionalTests\OfferDetailFunctionalTests.spec.ts` | 376 |  |
| [PWS_EmailCommunicationTests.spec.ts](./src-tests-premiumwholesales-pwsfunctionaltests-pwsemailcommunicationtestsspec.md) | `src\tests\PremiumWholesales\PWS_FunctionalTests\PWS_EmailCommunicationTests.spec.ts` | 89 |  |
| [ATPInventoryTests.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-atpinventorytestsspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\ATPInventoryTests.spec.ts` | 408 |  |
| [CaseLotTests.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-caselottestsspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\CaseLotTests.spec.ts` | 760 |  |
| [InventoryExcelDownloadTests.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-inventoryexceldownloadtestsspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\InventoryExcelDownloadTests.spec.ts` | 207 |  |
| [OfferFlowTests.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-offerflowtestsspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\OfferFlowTests.spec.ts` | 390 |  |
| [OrderSubmissionTests.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-ordersubmissiontestsspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\OrderSubmissionTests.spec.ts` | 144 |  |
| [RMATests.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-rmatestsspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\RMATests.spec.ts` | 464 |  |
| [SnowflakeConnectivityTest.spec.ts](./src-tests-premiumwholesales-pwsworkflowstests-snowflakeconnectivitytestspec.md) | `src\tests\PremiumWholesales\PWS_WorkflowsTests\SnowflakeConnectivityTest.spec.ts` | 80 |  |
| [DeposcoIntegration.spec.ts](./src-tests-spwmsdeposcotests-deposcointegrationspec.md) | `src\tests\SP_WMS_Deposco_Tests\DeposcoIntegration.spec.ts` | 49 |  |
| [DeposcoShipOrder.spec.ts](./src-tests-spwmsdeposcotests-deposcoshiporderspec.md) | `src\tests\SP_WMS_Deposco_Tests\DeposcoShipOrder.spec.ts` | 73 |  |
| [DataGridAllRoundTests.spec.ts](./src-tests-wholesales-datagridallroundtestsspec.md) | `src\tests\Wholesales\DataGridAllRoundTests.spec.ts` | 359 |  |
| [DataGrid_Round1.spec.ts](./src-tests-wholesales-datagridround1spec.md) | `src\tests\Wholesales\DataGrid_Round1.spec.ts` | 2102 |  |
| [DataGrid_Round2.spec.ts](./src-tests-wholesales-datagridround2spec.md) | `src\tests\Wholesales\DataGrid_Round2.spec.ts` | 171 |  |
| [DataGrid_Round3.spec.ts](./src-tests-wholesales-datagridround3spec.md) | `src\tests\Wholesales\DataGrid_Round3.spec.ts` | 41 |  |
| [Flow1_AllBuyer_FullInventory_SptON.spec.ts](./src-tests-wholesales-handontabletests-flow1allbuyerfullinventorysptonspec.md) | `src\tests\Wholesales\HandOnTableTests\Flow1_AllBuyer_FullInventory_SptON.spec.ts` | 242 |  |
| [Flow4_BuyerWithR1Bids_FullInventory_SptON.spec.ts](./src-tests-wholesales-handontabletests-flow4buyerwithr1bidsfullinventorysptonspec.md) | `src\tests\Wholesales\HandOnTableTests\Flow4_BuyerWithR1Bids_FullInventory_SptON.spec.ts` | 280 |  |
| [Flow5_BuyerWithR1Bids_InventoryR1Bids_SptON.spec.ts](./src-tests-wholesales-handontabletests-flow5buyerwithr1bidsinventoryr1bidssptonspec.md) | `src\tests\Wholesales\HandOnTableTests\Flow5_BuyerWithR1Bids_InventoryR1Bids_SptON.spec.ts` | 280 |  |
| [seed.spec.ts](./src-tests-seedspec.md) | `src\tests\seed.spec.ts` | 251 |  |

## Utility

| File | Path | Lines | Members |
|------|------|-------|---------|
| [SnowflakeClient.ts](./src-utils-clients-snowflakeclient.md) | `src\utils\clients\SnowflakeClient.ts` | 227 | class SnowflakeClient, isConnected, getSnowflakeClient |
| [SnowflakeSecretsClient.ts](./src-utils-clients-snowflakesecretsclient.md) | `src\utils\clients\SnowflakeSecretsClient.ts` | 128 | class SnowflakeSecretsClient |
| [index.ts](./src-utils-clients-index.md) | `src\utils\clients\index.ts` | 9 |  |
| [commonMethods.ts](./src-utils-helpers-commonmethods.md) | `src\utils\helpers\commonMethods.ts` | 83 | Logger |
| [data_utils.ts](./src-utils-helpers-datautils.md) | `src\utils\helpers\data_utils.ts` | 553 | downloadFile, clickLocatorAnddownloadFile, modifyBidsExcelSheet, uploadFile, excelToCSV (+5 more) |
| [deposcoTestDataHelper.ts](./src-utils-helpers-deposcotestdatahelper.md) | `src\utils\helpers\deposcoTestDataHelper.ts` | 74 | getDeposcoCredentials, getDeposcoUrl, getTestScenario, getBatchTestOrders |
| [auction_config.json](./src-utils-resources-auctionconfig.md) | `src\utils\resources\auction_config.json` | 47 |  |
| [deposco_test_data.json](./src-utils-resources-deposcotestdata.md) | `src\utils\resources\deposco_test_data.json` | 46 |  |
| [enum.ts](./src-utils-resources-enum.md) | `src\utils\resources\enum.ts` | 111 |  |
| [user_data.json](./src-utils-resources-userdata.md) | `src\utils\resources\user_data.json` | 498 |  |

## Workflow Spec

| File | Path | Lines | Members |
|------|------|-------|---------|
| [case-lot-workflow.md](./specs-case-lot-workflow.md) | `specs\case-lot-workflow.md` | 147 |  |
| [inventory-cart-functional.md](./specs-inventory-cart-functional.md) | `specs\inventory-cart-functional.md` | 115 |  |
| [offer-review-flow.md](./specs-offer-review-flow.md) | `specs\offer-review-flow.md` | 112 |  |
| [order-submission-flow.md](./specs-order-submission-flow.md) | `specs\order-submission-flow.md` | 109 |  |
| [rma-request-flow.md](./specs-rma-request-flow.md) | `specs\rma-request-flow.md` | 108 |  |
