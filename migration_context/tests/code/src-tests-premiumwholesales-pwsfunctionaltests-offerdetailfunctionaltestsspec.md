# Test Spec: OfferDetailFunctionalTests.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_FunctionalTests\OfferDetailFunctionalTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 376
- **Size**: 21,464 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';

// =======  NOT UP TO DATE -  NEED TO BE REVISED LATER 
// ==== OFFER_ID FORMAT CHANGED AND BUYER_CODE NOT AVAILABLE ISSUES  ====

// let base: BaseTest;

// test.beforeAll(async ({ browser }) => {
//     const page = await browser.newPage();
//     base = new BaseTest(page);
//     await base.setup();
// });

// test.describe('Sale Review Detail Page : Finalize Tests', () => {
//     const numberOfSKUs = 3;
//     const finalizedPrice = "2500";
//     const finalizedQty = "1";
//     let offerID: string;   
//     test.beforeAll(async () => {
//         await base['loginPage'].loginAs(userRole.PWS_UserSix);        
//         offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 0.8, 1);
//         await base['pws_InventoryPage'].logoutFromPWS();                          
//         await base['loginPage'].loginAs(userRole.ADMIN);
//         await base['pws_OfferDetailsPage'].navigateToOfferDetailsPage('APWS06', offerID, 7);
//     });

//     test('SPKB-966: Verify Finalize is Under MoreAction Dropdown', async () => {
//         Logger('Verify Finalize is Under MoreAction Dropdown');
//         const isFinalizeOptionVisible = await base['pws_OfferDetailsPage'].isMoreActionOptionVisible('Finalize All');  
//         expect(isFinalizeOptionVisible).toBeTruthy();
//     });

//     test('SPKB-970: Verify Error Handling When Missing Finalized Price and Qty on Submission', async () => {
//         Logger('Verify Error Handling When Missing Finalized Price and Qty on Submission');
//         await base['pws_OfferDetailsPage'].moreActionOption('Finalize All');
//         await base['pws_OfferDetailsPage'].clickCompleteReviewButton();   
//         const errorModalPopup = await base['pws_CounterOfferPage'].isErrorMessageModalVisible();
//         expect(errorModalPopup).toBe(true)
//         await base['pws_CounterOfferPage'].closeErrorModal();
//     });

//     test('SPKB-971: Verify Error Handling When Accepted and Finalized SKUs on Submission', async () => {
//         Logger('Verify Error Handling When Accepted and Finalized SKUs on Submission');
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(0,'Accept');   
//         await base['pws_OfferDetailsPage'].clickCompleteReviewButton();   
//         const errorModalPopup = await base['pws_CounterOfferPage'].isErrorMessageModalVisible();
//         expect(errorModalPopup).toBe(true)
//         await base['pws_CounterOfferPage'].closeErrorModal();  
//     });

//     test('SPKB-967: Verify Sales Rep Can Enter Price and Qty', async () => {
//         Logger('Verify Sales Rep Can Enter Price and Qty');
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(0,'Finalize');
//         for (let i = 0; i < numberOfSKUs; i++) {
//             await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(i, finalizedPrice, finalizedQty);
//         }
//     });    

//     test('SPKB-972: Validate Summary Final Offer', async () => {
//         Logger('Validate Summary Final Offer');
//         const sumInlinePrice = (numberOfSKUs * parseFloat(finalizedPrice)).toString();
//         const sumInlineQty = (numberOfSKUs * parseFloat(finalizedQty)).toString();
//         const numberOfSKUString = numberOfSKUs.toString();
//         const sumInlineArray = [numberOfSKUString, sumInlineQty, sumInlinePrice];  
//         const counterOffer = await base['pws_OfferDetailsPage'].getCounterOfferSummary();
//         expect([counterOffer.skus, counterOffer.qty, counterOffer.total]).toEqual(sumInlineArray);
//     });

//     test('SPKB-973: Verify the Finalized Offer Send to Ordered Stage', async () => {
//         // Logger('Verify the Finalized Offer Send to Ordered Stage');
//         // await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
//         // await base['pws_OfferDetailsPage'].clickCloseSubmittedConfirmationModal();
//         // await base['pws_OfferQueuePage'].clickNavTabOption("Ordered");
//         // const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderOfferTab(offerID,3);
//         // expect(isOfferFound).toBeTruthy();   
//     });

//     test.afterAll(async () => {
//             try {
//                 await base['pws_InventoryPage'].logoutFromPWS();
//             } catch (error) {            
//                     console.warn('Logout failed in afterEach:', error);            
//             }   
//         });
// });


// test.describe('Sale Review Page : More-Action Options Tests', () => {
//     const numberOfSKUs = 3;
//     let offerID: string;   
//     test.beforeAll(async () => {
//         await base['loginPage'].loginAs(userRole.PWS_UserTwo);        
//         offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 0.8, 1);
//         await base['pws_InventoryPage'].logoutFromPWS();              
//         await base['loginPage'].loginAs(userRole.ADMIN);
//         await base['pws_OfferDetailsPage'].navigateToOfferDetailsPage('APWS02', offerID, 7);
//     });

//     test('SPKB-1277: Verify Accept-All set Inline Items to Accept', async () => { 
//         Logger('Verify Accept-All set Inline Items to Accept');
//         await base['pws_OfferDetailsPage'].moreActionOption('Accept All');
//         for (let i = 0; i < numberOfSKUs; i++) {
//             const salesAction = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(i);
//             expect(salesAction).toBe('Accept');
//         }
//     });

//     test('SPKB-1118: Verify Decline-All set Inline Items to Decline', async () => { 
//         Logger('Verify Decline-All set Inline Items to Decline');
//         await base['pws_OfferDetailsPage'].moreActionOption('Decline All');
//         for (let i = 0; i < numberOfSKUs; i++) {
//             const salesAction = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(i);
//             expect(salesAction).toBe('Decline');
//         }
//     });

//     test('SPKB-968: Verify Finalize-All set Inline Items to Finalize', async () => { 
//         Logger('Verify Finalize-All set Inline Items to Finalize');
//         await base['pws_OfferDetailsPage'].moreActionOption('Finalize All');
//         for (let i = 0; i < numberOfSKUs; i++) {
//             const salesAction = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(i);
//             expect(salesAction).toBe('Finalize');
//         }
//     });

//     test('SPKB-1167: Verify Download Option from More-Action Dropdown', async () => { 
//         Logger('Verify Download Option from More-Action Dropdown');
//         await base['pws_OfferDetailsPage'].moreActionOption('Download');        
//         const dataGrid: Array<{sku: string, action: string}> = [];
//         for (let i = 0; i < 3; i++) {
//             dataGrid.push(await base['pws_OfferDetailsPage'].getItemInfoByRowIndex(i));
//             await base['page'].waitForTimeout(1000);
//         }
//         const fileName = 'MoreActionDownload.xlsx';
//         const excelData = await base['pws_OfferDetailsPage'].getSkuAndActionFromExcel(fileName);    
//         const sortBySku = (a: {sku: string}) => a.sku;
//         dataGrid.sort((a, b) => sortBySku(a).localeCompare(sortBySku(b)));
//         excelData.sort((a, b) => sortBySku(a).localeCompare(sortBySku(b)));
//         expect(dataGrid.length).toBe(excelData.length);
//         for (let i = 0; i < dataGrid.length; i++) {
//             expect(dataGrid[i].sku).toBe(excelData[i].sku);
//             expect(dataGrid[i].action).toBe(excelData[i].action);
//         }   
//     });    

//         test.afterAll(async () => {
//             try {
//                 await base['pws_InventoryPage'].logoutFromPWS();
//             } catch (error) {            
//                     console.warn('Logout failed in afterEach:', error);            
//             }   
//         });
// });   

// test.describe('Sale Review Page : Sales Action Functionality Tests', () => {
//     let offerID: string;  
//     const nubmerOfSKUs = 3;
//     let originalOffer: { skus: string; qty: string; total: string };
//     test.beforeAll(async () => {       
//         await base['loginPage'].loginAs(userRole.PWS_UserTwo);        
//         offerID = await base['pws_CartPage'].submitOfferBelowListPrice(nubmerOfSKUs, 0.8, 1);
//         await base['pws_InventoryPage'].logoutFromPWS();    
//     });    

//     test.beforeEach(async() => {
//         await base['loginPage'].loginAs(userRole.ADMIN_Two);
//         await base['pws_OfferDetailsPage'].navigateToOfferDetailsPage('APWS02', offerID, 7);
//     })
    
//     test('SPKB-788: Verify Offer Detail Page Display "Sales Review" as Default Status', async () => {
//         Logger('Verifying Offer Detail Page Display "Sales Review" as Default Status');
//         expect(await base['pws_OfferDetailsPage'].isSalesReviewStatusDisplayed()).toBeTruthy();
//         originalOffer = await base['pws_OfferDetailsPage'].getOriginalOfferSummary();       
//     });
       
//     test('SPKB-1199.1: Verify Sales Rep Can Accept an Offer at SKU Level', async () => {
//         Logger('Verifying Sales Rep Can Accept an Offer at SKU Level');
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(0,"Accept");
//         const selected = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(0);
//         expect(selected).toBe("Accept");
//     });
    
//     test('SPKB-1199.2: Verify Sales Rep Can Reject an Offer at SKU Level', async () => {
//         Logger('Verifying Sales Rep Can Reject an Offer at SKU Level');
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(1,"Decline");
//         const selected = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(1);
//         expect(selected).toBe("Decline");
//     });
    
//     test('SPKB-1199.3: Verify Sales Rep Can Counter an Offer at SKU Level', async () => {
//         Logger('Verifying Sales Rep Can Counter an Offer at SKU Level');
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(2,"Counter");
//         const selected = await base['pws_OfferDetailsPage'].getSalesActionStatusByRowIndex(2);
//         expect(selected).toBe("Counter");
//     });
    
//     test('SPKB-791: Verify Accept SKU Not Allows Sales Rep to Enter Price, Qty', async () => {
//         Logger('Verifying Accept SKU Not Allows Sales Rep to Enter Price, Qty');
//         const isPriceFieldDisabled = await base['pws_OfferDetailsPage'].isCounterPriceFieldDisabled(0); 
//         expect(isPriceFieldDisabled).toBe(true);
//         const isQtyFieldDisabled = await base['pws_OfferDetailsPage'].isCounterQtyFieldDisabled(0); 
//         expect(isQtyFieldDisabled).toBe(true);
//     });

//     test('SPKB-791.1: Verify Reject Items Not Allows Sales Rep to Enter Price, Qty', async () => {
//         Logger('Verifying Reject Items Not Allows Sales Rep to Enter Price, Qty');
//         const isPriceFieldDisabled = await base['pws_OfferDetailsPage'].isCounterPriceFieldDisabled(1); 
//          expect(isPriceFieldDisabled).toBe(true);
//         const isQtyFieldDisabled = await base['pws_OfferDetailsPage'].isCounterQtyFieldDisabled(1); 
//         expect(isQtyFieldDisabled).toBe(true);
//     });

//     test('SPKB-795: Verify Complete-Review Submission Not Allow If Counter Price/Qty Missing', async () => {
//         Logger('Verifying Complete-Review Submission Not Allow If Counter Price/Qty Missing');
//         await base['pws_OfferDetailsPage'].clickCompleteReviewButton();  
//         await base['pws_OfferDetailsPage'].clickCloseErrorModalPopup();    
//     }); 
    
//     test('SPKB-790: Verify Counter-Offer Items Allows Sales Rep to Enter Price, Qty', async () => {
//         Logger('Verifying Counter-Offer Items Allows Sales Rep to Enter Price, Qty');
//         const isPriceFieldDisabled = await base['pws_OfferDetailsPage'].isCounterPriceFieldDisabled(2); 
//         expect(isPriceFieldDisabled).toBe(false);
//         const isQtyFieldDisabled = await base['pws_OfferDetailsPage'].isCounterQtyFieldDisabled(2); 
//         expect(isQtyFieldDisabled).toBe(false);
//     });

//     test('SPKB-794: Verify Calculation of Counter Inline Total, Counter Summary Box', async () => {
//         Logger('Verifying Calculation of Counter Inline Total, Counter Summary Box');
//         const counterPrice = "2000";
//         const counterQty = "1";
//         const expectedTotal = (parseFloat(counterPrice) * parseFloat(counterQty)).toString();
//         const inlineTotal = await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(2, counterPrice, counterQty);
//         expect(inlineTotal).toBe(expectedTotal);
//     }); 

//     test('SPKB-793: Verify Counteroffer Summary Box Updating When Sales Rep Enter Counter-Offer Price, Qty', async () => {  
//         Logger('Verifying Counteroffer Summary Box Updating When Sales Rep Enter Counter-Offer Price, Qty');      
//         const expected = ["1", "1", "2000"];
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(0,"Decline");
//         const counterOffer = await base['pws_OfferDetailsPage'].getCounterOfferSummary();
//         expect([counterOffer.skus, counterOffer.qty, counterOffer.total]).toEqual(expected);
//     });

//     test('SPKB-792: Verify Original Offer Summary Box Not Change at Anytime ', async () => {
//         Logger('Verifying Original Offer Summary Box Not Change at Anytime');
//         const currentOriginalOffer = await base['pws_OfferDetailsPage'].getOriginalOfferSummary();
//         expect(currentOriginalOffer).toEqual(originalOffer);
//     });   

//     test.afterEach(async () => {
//         try {
//             await base['pws_InventoryPage'].logoutFromPWS();
//         } catch (error) {            
//                 console.warn('Logout failed in afterEach:', error);            
//         }   
//     });
// });

// test.describe('Conter Offer Page : Buyer Action Functionality Tests', () => { 
//     test.setTimeout(0);
//     const numberOfSKUs = 3;
//     let offerID: string;
//     test.beforeAll(async () => {
//         await base['loginPage'].loginAs(userRole.PWS_UserFour);    
//     // need to insert test on this step --> SPKB-1257: Verify Order Status after Submit Any Item Below List Price    
//         offerID = await base['pws_CartPage'].submitOfferBelowListPrice(numberOfSKUs, 0.8, 1);
//         await base['pws_InventoryPage'].logoutFromPWS();              
//         await base['loginPage'].loginAs(userRole.ADMIN_Four);
//         await base['pws_OfferDetailsPage'].navigateToOfferDetailsPage('APWS04', offerID, 7);
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(0,"Accept");
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(1,"Decline");
//         await base['pws_OfferDetailsPage'].salesActionEachSKU(2,"Counter");
//         await base['pws_OfferDetailsPage'].enterCounterPriceAndQty(2, "2999", "1");
//         await base['pws_OfferDetailsPage'].clickCompleteReviewButton();
//         await base['pws_OfferQueuePage'].chooseOfferStatusTab("Buyer Acceptance");
//         const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerID, 5);
//         expect(isOfferFound).toBeTruthy();
//         await base['pws_InventoryPage'].logoutFromPWS();    
//     });

//     test('SPKB-1260: Verify Reserved Qty after Order Submission', async () => {
//             Logger('Verifying Avail Qty Decreased on Inventory Page after Order Submission')
//             await base['pws_InventoryPage'].clickOnInventoryNavTab();
//            // const isSkuExist = await base['pws_InventoryPage'].isThisSkuExist(sku);
//           //  expect(isSkuExist).toBeFalsy();
//         });

//     test.beforeEach(async () => {
//         await base['loginPage'].loginAs(userRole.PWS_UserFour);
//     });

//     test('SPKB-843 : Verify Buyer Can Open Counter Offer Detail Page', async () => {     
//         Logger('Verifying Buyer Can Open Counter Offer Detail Page');
//        // await expect(base['page']).toHaveTitle("ecoATM Direct - Counter Offer");    
//     });

//     test('SPKB-844 : Verify Buyer Not Able to Edit Accepted SKU by Sales Rep', async () => {
//         Logger('Verifying Buyer Not Able to Edit Accepted SKU by Sales Rep');
//         const statusAcceptedRow = await base['pws_CounterOfferPage'].getCounterStatusByRowIndex(0);
//         const isDropdownVisibleAcceptedRow = await base['pws_CounterOfferPage'].isActionDropdownVisibleByRowIndex(0,statusAcceptedRow);
//         expect(isDropdownVisibleAcceptedRow).toBe(false);
//         const statusDeclinedRow = await base['pws_CounterOfferPage'].getCounterStatusByRowIndex(1);
//         const isDropdownVisibleDeclinedRow = await base['pws_CounterOfferPage'].isActionDropdownVisibleByRowIndex(1,statusDeclinedRow);
//         expect(isDropdownVisibleDeclinedRow).toBe(false);        
//     });

//     test('SPKB-845 : Verify Buyer Can Select Accept/Reject on Countered SKUs', async () => {
//         Logger('Verifying Buyer Can Select Accept/Reject on Countered SKUs');
//         const statusCounteredRow = await base['pws_CounterOfferPage'].getCounterStatusByRowIndex(2);
//         const isDropdownVisibleCounteredRow = await base['pws_CounterOfferPage'].isActionDropdownVisibleByRowIndex(2,statusCounteredRow);
//         expect(isDropdownVisibleCounteredRow).toBe(true);
//     });

//     test('SPKB-850 : Verify Buyer Not Able to Submit Response with Action Blank on Countered SKUs', async () => {
//         Logger('Verifying Buyer Not Able to Submit Response with Action Blank on Countered SKUs');
//         await base['pws_CounterOfferPage'].clickSubmitResponseButton();
//         const errorModalPopup = await base['pws_CounterOfferPage'].isErrorMessageModalVisible();
//         expect(errorModalPopup).toBe(true)
//         await base['pws_CounterOfferPage'].closeErrorModal();
//     });

//     test('SPKB-846 : Verify Original Offer Summary Box', async () => {
//         Logger('Verifying Original Offer Summary Box Matching Data Grid');
//         await base['page'].waitForTimeout(2000);
//         const originalOfferDataGrid = await base['pws_CounterOfferPage'].getOriginalOfferSummaryDataGrid();
//         const originalOfferSummaryBox = await base['pws_CounterOfferPage'].getOfferSummaryBox("original");
//         expect(originalOfferDataGrid).toEqual(originalOfferSummaryBox);        
//     });

//     test('SPKB-847 : Verify Counter Offer Summary Box', async () => {
//         Logger('Verifying Counter Offer Summary Box Matching Data Grid');
//         const counterOfferDataGrid = await base['pws_CounterOfferPage'].getCounterOfferSummaryDataGrid();
//         const counterOfferSummaryBox = await base['pws_CounterOfferPage'].getOfferSummaryBox("counter");
//         expect(counterOfferDataGrid).toEqual(counterOfferSummaryBox);
//     });

//     test('SPKB-848 : Verify Final Offer Summary Box', async () => {
//         Logger('Verifying Final Offer Summary Box Matching Data Grid');
//         await base['pws_CounterOfferPage'].selectCounterActionByRowIndex(2, "Accept");
//         await base['page'].waitForTimeout(2000);
//         const finalOfferDataGrid = await base['pws_CounterOfferPage'].getFinalOfferSummaryDataGrid();
//         const finalOfferSummaryBox = await base['pws_CounterOfferPage'].getOfferSummaryBox("final");
//         expect(finalOfferDataGrid).toEqual(finalOfferSummaryBox);
//         await base['pws_CounterOfferPage'].selectCounterActionByRowIndex(2, "Decline");
//         await base['page'].waitForTimeout(2000);
//         const finalOfferDataGridDecline = await base['pws_CounterOfferPage'].getFinalOfferSummaryDataGrid();
//         const finalOfferSummaryBoxDecline = await base['pws_CounterOfferPage'].getOfferSummaryBox("final");
//         expect(finalOfferDataGridDecline).toEqual(finalOfferSummaryBoxDecline);
//     });

//     test('SPKB-849 : Verify Cancel Order Functionality', async () => {
//         Logger('Verifying Cancel Order Modal Functionality');
//         await base['pws_CounterOfferPage'].moreActionOption("Cancel Order");
//         await base['page'].waitForTimeout(1000);
//         await base['pws_CounterOfferPage'].cancelOrderModalAction('no');
//         const isCancelOrderModalVisible = await base['pws_CounterOfferPage'].isCancelOrderModalVisible();
//         expect(isCancelOrderModalVisible).toBe(false);       
//         await base['pws_CounterOfferPage'].moreActionOption("Cancel Order");
//         await base['page'].waitForTimeout(1000);        
//         await base['pws_CounterOfferPage'].cancelOrderModalAction('yes');
//         const isSubmittedModalVisible = await base['pws_CounterOfferPage'].isOfferResponseSubmittedModalVisible();
//         expect(isSubmittedModalVisible).toBe(true);
//         await base['pws_CounterOfferPage'].closeOfferResponseSubmittedModal();
//     });    

//     test.afterEach(async () => {
//         try {
//             await base['pws_InventoryPage'].logoutFromPWS();
//         } catch (error) {            
//                 console.warn('Logout failed in afterEach:', error);            
//         }        
//     });
// });
```
