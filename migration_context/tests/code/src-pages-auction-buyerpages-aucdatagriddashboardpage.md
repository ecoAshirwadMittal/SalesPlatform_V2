# Page Object: AUC_DataGridDashBoardPage.ts

- **Path**: `src\pages\Auction\BuyerPages\AUC_DataGridDashBoardPage.ts`
- **Category**: Page Object
- **Lines**: 387
- **Size**: 21,341 bytes
- **Members**: `class AUC_DataGridDashBoardPage`, `exportBidFile`, `downloadRound1Bids_AfterEnded`, `getBidDataByFilter`, `getBidDataByRowIndex`

## Source Code

```typescript
import { expect, Locator, Page } from '@playwright/test';
import { Logger, downloadFile, uploadFile, modifyBidsExcelSheet } from '../../../utils/helpers/data_utils';
import { BaseTest } from '../../../tests/BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { waitAndClick, waitAndFill } from '../../../utils/helpers/commonMethods';

export class AUC_DataGridDashBoardPage {
    //---- Dashboard Info -------
    private readonly dataGrid = "//div[@class='widget-datagrid-grid table infinite-loading']";
    private readonly buyerCodeDisplay = "//span[contains(@class,'BuyerSelection_Code')]";
    private readonly minimumBidDisplay = "//div[@class='mx-name-container12']/span[2]";
    private readonly totalRowsDisplay = "(//div[contains(@class,'paging-status')])[2]";

    //---- DataGrid Cells ----
    private readonly productIDColumnHeader = "//div[@class='column-header clickable']/span[text()='Product Id']";
    private readonly targetPriceColumnHeader = "//div[@class='column-header clickable align-column-center']/span[text()='Target Price']";
    private readonly availQtyColumnHeader = "//div[@class='column-header clickable align-column-center']/span[text()='Avail. Qty']";
    private readonly priceColumnHeader = "//div[@class='column-header clickable align-column-center']/span[text()='Price']";
    private readonly qtyCapColumnHeader = "//div[@class='column-header clickable align-column-center']/span[text()='Qty. Cap']";
    private readonly productIDCellDataGrid = (row: number) => `//div[@class='widget-datagrid-grid-body table-content']//div[@role='row'][${row + 1}]//div[@data-position='0,${row}']`;
    private readonly gradeCellDataGrid = (row: number) => `//div[@class='widget-datagrid-grid-body table-content']//div[@role='row'][${row + 1}]//div[@data-position='4,${row}']`;
    private readonly availQtyCellDataGrid = (row: number) => `//div[@class='widget-datagrid-grid-body table-content']//div[@role='row'][${row + 1}]//div[@data-position='7,${row}']`;
    private readonly targetPriceCellDataGrid = (row: number) => `//div[@class='widget-datagrid-grid-body table-content']//div[@role='row'][${row + 1}]//div[@data-position='8,${row}']`;
    private readonly priceCellDataGrid = (row: number) => `//div[@class='widget-datagrid-grid-body table-content']//div[@role='row'][${row + 1}]//div[@data-position='9,${row}']`;
    private readonly qtyCellDataGrid = (row: number) => `//div[@class='widget-datagrid-grid-body table-content']//div[@role='row'][${row + 1}]//div[@data-position='10,${row}']`;

    //---- Submit via Data Grid ----
    private readonly submitButton_buyerView = "//button[contains(@class,'mx-name-actionButton3')]";
    private readonly noBidModelCloseButton = "//button[contains(@data-button-id,'BidsSubmittedConfirmation_NoBids')]"
    private readonly submittedMessage = "//span[@class='confirmationheader'][2]"
    private readonly minimumBidValidationMessage = "//span[@class='mx-text mx-name-text2 spacing-outer-top text-center d-block']";

    //---- Import/Export ----
    private readonly exportButton = "//button[contains(@class,'mx-name-actionButton1 export')]";
    private readonly importButton = "(//button[contains(@class,'mx-name-actionButton5')])[2]";
    private readonly importFileField = "//input[@type='file']";
    private readonly importModal_importButton = "//button[contains(@class,'mx-name-microflowButton1')]";
    private readonly importModal_totalRowCount = "//span[contains(@class,'mx-text mx-name-text5 bidupload_successfilename')]";
    private readonly importModal_SubmitButton = "//div[@class='mx-name-container8 col-center']/button";

    private readonly downloadYourRoundOneButton = "//button[contains(.,'Download your Round 1 Bids')]";

    //---- Admin View Xpaths ----
    private readonly submitButton_adminView = "//button[@title='On behalf of bidder']";
    private readonly inputFieldOnModal_adminView = "textarea.mx-textarea-input";
    private readonly submitButtonOnModal_adminView = "//div[@class='mx-name-container5 col-center']/button";
    private readonly submittedModalCloseButton = "//button[contains(@class,'mx-name-actionButtonSubmit')]";


    constructor(private page: Page) { }

    async ensureUserOnDashboard(base: BaseTest, buyerCode: string, userRole: userRole): Promise<void> {
        try {
            await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(2000);
            const displayedBuyerCode = await base['auc_dataGridDashBoardPage'].getBuyerCodeDisplay();
            if (displayedBuyerCode === buyerCode) {
                Logger(`Already on dashboard for buyer code: ${buyerCode}`);
                return;
            }
        } catch (error) {
            Logger("Buyer code not displayed or dashboard not loaded, will logout and login.");
        }
        await base['loginPage'].loginAs(userRole);
        await base['page'].waitForTimeout(2000);
        // Check if user lands on welcome page (multiple buyer codes) or dashboard (single buyer code)
        const isOnWelcomePage = await base['welcomePage'].isAUCSectionVisible().catch(() => false);

        if (isOnWelcomePage) {
            Logger(`User has multiple buyer codes, selecting buyer code: ${buyerCode} from welcome page`);
            await base['welcomePage'].selectBuyerCode(buyerCode);
        } else {
            Logger(`User has single buyer code, landed directly on dashboard`);
        }
        if (userRole === "ADMIN") {
            await base['navMenuPage'].BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin(buyerCode);
        }
        await base['auc_dataGridDashBoardPage'].waitForDataGridLoaded(10000);
    }

    async waitForDataGridLoaded(timeout: number): Promise<boolean> {
        try {
            await this.page.locator(this.dataGrid).waitFor({ state: 'visible', timeout });
            const start = Date.now();
            while (Date.now() - start < timeout) {
                const rowCount = await this.page.locator(this.priceCellDataGrid(1)).count();
                if (rowCount > 0) {
                    Logger("Data grid is fully loaded.");
                    return true;
                }
                await this.page.waitForTimeout(500);
            }
            Logger("Data grid did not load any rows within the expected time.");
            return false;
        } catch (error) {
            Logger("Data grid did not load within the expected time.");
            return false;
        }
    }

    async isWholesaleInventory(): Promise<boolean> {
        const paginationStatus = this.page.locator(this.totalRowsDisplay);
        await paginationStatus.waitFor({ state: 'visible', timeout: 5000 });
        const text = await paginationStatus.textContent();
        Logger(`Pagination status text: "${text?.trim()}"`);
        const match = text?.match(/of\s+([\d,]+)/i);
        if (match && match[1]) {
            const total = Number(match[1].replace(/,/g, ''));
            Logger(`Total inventory rows: ${total}`);
            return total > 9999;  //if more than 9999 rows, it is wholesale inventory  
        }
        Logger("Could not determine total inventory rows from pagination status.");
        return false;
    }

    async placeBids(bidRows: Array<{ price: string, qty: string }>): Promise<void> {
        for (let i = 0; i < bidRows.length; i++) {
            const { price, qty } = bidRows[i];
            await waitAndClick(this.page, this.priceCellDataGrid(i));
            await this.page.keyboard.type(price, { delay: 50 });
            await waitAndClick(this.page, this.qtyCellDataGrid(i));
            await this.page.keyboard.type(qty, { delay: 50 });
            await this.page.keyboard.press('Tab');
            await this.page.waitForTimeout(1000);
        }
    }

    // This method will place bids and submit, then return the number of bids that are below minimum if the validation modal appears
    async placeBidsAndSubmit(bidRows: Array<{ price: string, qty: string }>): Promise<number> {
        for (let i = 0; i < bidRows.length; i++) {
            const { price, qty } = bidRows[i];
            await waitAndClick(this.page, this.priceCellDataGrid(i));
            await this.page.keyboard.type(price, { delay: 50 });
            await waitAndClick(this.page, this.qtyCellDataGrid(i));
            await this.page.keyboard.type(qty, { delay: 50 });
            await this.page.keyboard.press('Tab');
            await this.page.waitForTimeout(1000);
        }
        await waitAndClick(this.page, this.submitButton_buyerView);
        let bidsBlowMinimum = 0;
        try {
            const validationMessageLocator = this.page.locator(this.minimumBidValidationMessage);
            await validationMessageLocator.waitFor({ state: 'visible', timeout: 3000 });
            const messageText = await validationMessageLocator.textContent();
            Logger(`Validation message: ${messageText}`);
            const match = messageText?.match(/(\d+)\s+of\s+your\s+bids\s+below\s+minimum/i);
            if (match && match[1]) {
                bidsBlowMinimum = Number(match[1]);
                Logger(`Number of bids below minimum: ${bidsBlowMinimum}`);
            }
        } catch (error) {
            Logger("No minimum bid validation message appeared - all bids are valid.");
        }
        try {
            await waitAndFill(this.page, this.inputFieldOnModal_adminView, 'test');
            await waitAndClick(this.page, this.submitButtonOnModal_adminView);
            Logger("Submitted bids and confirmed in modal.");
            await this.page.waitForTimeout(2000);
        } catch (error) {
            Logger("Sales Rep modal did not appear, continuing as normal buyer.");
        }
        await waitAndClick(this.page, this.submittedModalCloseButton);
        await this.page.waitForTimeout(5000);
        return bidsBlowMinimum;
    }

    async exportBidFile(fileName: string) {
        await downloadFile(this.page, this.exportButton, fileName);
    }

    async importBidFile(fileName: string, modifyBids: Array<{ productID: string, grade: string, bidPrice: string, bidQty: string }>): Promise<string> {
        await this.page.waitForTimeout(10000);
        await modifyBidsExcelSheet(fileName, modifyBids);
        // click import button on dashboard
        await waitAndClick(this.page, this.importButton);
        // upload file
        const importFileFieldLocator = this.page.locator(this.importFileField);
        await importFileFieldLocator.waitFor({ state: 'visible', timeout: 10000 });
        await uploadFile(this.page, this.importFileField, fileName);
        // click import button on modal
        await waitAndClick(this.page, this.importModal_importButton);
        // wait for total row count to be visible        
        const totalRowCountLocator = this.page.locator(this.importModal_totalRowCount);
        await totalRowCountLocator.waitFor({ state: 'visible', timeout: 15000 });
        return await totalRowCountLocator.textContent() ?? "";
    }

    // This method return minimum bid validation message if appears after submission, otherwise return empty string
    async submitBids_onImportModal(): Promise<number> {
        await waitAndClick(this.page, this.importModal_SubmitButton);
        let bidsBlowMinimum = 0;
        try {
            const validationMessageLocator = this.page.locator(this.minimumBidValidationMessage);
            await validationMessageLocator.waitFor({ state: 'visible', timeout: 3000 });
            const messageText = await validationMessageLocator.textContent();
            Logger(`Validation message: ${messageText}`);
            const match = messageText?.match(/(\d+)\s+of\s+your\s+bids\s+below\s+minimum/i);
            if (match && match[1]) {
                bidsBlowMinimum = Number(match[1]);
                Logger(`Number of bids below minimum: ${bidsBlowMinimum}`);
            }
        } catch (error) {
            Logger("No minimum bid validation message appeared - all bids are valid.");
        }
        await waitAndClick(this.page, this.submittedModalCloseButton);
        await this.page.waitForTimeout(5000);
        return bidsBlowMinimum;
    }

    async isNoBidModalAppeared(): Promise<boolean> {
        await waitAndClick(this.page, this.submitButton_buyerView);
        try {
            const noBidModalButton = this.page.locator(this.noBidModelCloseButton);
            await noBidModalButton.waitFor({ state: 'visible', timeout: 3000 });
            Logger("No bid modal appeared, clicking close button");
            await waitAndClick(this.page, this.noBidModelCloseButton);
            return true;
        } catch (error) {
            Logger("No bid modal did not appear");
            return false;
        }
    }

    async downloadRound1Bids_AfterEnded() {
        await waitAndClick(this.page, this.downloadYourRoundOneButton);
    }

    async getBuyerCodeDisplay(): Promise<string> {
        const buyerCodeSpan = this.page.locator(this.buyerCodeDisplay);
        await buyerCodeSpan.waitFor({ state: 'visible', timeout: 4000 });
        const displayedBuyerCode = (await buyerCodeSpan.textContent())?.trim();
        return displayedBuyerCode ?? '';
    }

    async getMinimumBidDisplay(): Promise<string> {
        const minimumBidSpan = this.page.locator(this.minimumBidDisplay);
        await minimumBidSpan.waitFor({ state: 'visible', timeout: 4000 });
        const displayedMinimumBid = (await minimumBidSpan.textContent())?.trim();
        return displayedMinimumBid ?? '';
    }

    async getBidDataByFilter(productID: string, grade: string): Promise<{ price: string, qty: string, availQty: string, targetPrice: string }> {
        await this.filterByProductIDAndGrade(productID, grade);
        const priceInput = this.page.locator(this.priceCellDataGrid(0) + "//input");
        const qtyInput = this.page.locator(this.qtyCellDataGrid(0) + "//input");
        const availQtyCell = this.page.locator(this.availQtyCellDataGrid(0));
        const targetPriceCell = this.page.locator(this.targetPriceCellDataGrid(0));
        await priceInput.waitFor({ state: 'visible', timeout: 5000 });
        await qtyInput.waitFor({ state: 'visible', timeout: 5000 });
        await availQtyCell.waitFor({ state: 'visible', timeout: 5000 });
        await targetPriceCell.waitFor({ state: 'visible', timeout: 5000 });
        const price = await priceInput.inputValue();
        const qty = await qtyInput.inputValue();
        const availQty = (await availQtyCell.textContent())?.trim() ?? '';
        const targetPrice = (await targetPriceCell.textContent())?.trim() ?? '';
        return { price, qty, availQty, targetPrice };
    }

    async getBidDataByRowIndex(rowIndex: number): Promise<{ productId: string, grade: string, price: string, qty: string, availQty: string, targetPrice: string }> {
        await this.clearProductFilter();
        const productIdCell = this.page.locator(this.productIDCellDataGrid(rowIndex));
        const gradeCell = this.page.locator(this.gradeCellDataGrid(rowIndex));
        const availQtyCell = this.page.locator(this.availQtyCellDataGrid(rowIndex));
        const targetPriceCell = this.page.locator(this.targetPriceCellDataGrid(rowIndex));
        await productIdCell.waitFor({ state: 'visible', timeout: 5000 });
        await gradeCell.waitFor({ state: 'visible', timeout: 5000 });
        await availQtyCell.waitFor({ state: 'visible', timeout: 5000 });
        await targetPriceCell.waitFor({ state: 'visible', timeout: 5000 });
        const productId = (await productIdCell.textContent())?.trim() ?? '';
        const grade = (await gradeCell.textContent())?.trim() ?? '';
        const availQty = (await availQtyCell.textContent())?.trim() ?? '';
        const targetPrice = (await targetPriceCell.textContent())?.trim() ?? '';
        const priceInput = this.page.locator(this.priceCellDataGrid(rowIndex) + "//input");
        const qtyInput = this.page.locator(this.qtyCellDataGrid(rowIndex) + "//input");
        await priceInput.waitFor({ state: 'visible', timeout: 5000 });
        await qtyInput.waitFor({ state: 'visible', timeout: 5000 });
        const price = await priceInput.inputValue();
        const qty = await qtyInput.inputValue();
        return { productId, grade, price, qty, availQty, targetPrice };
    }

    async sortByTargetPrice(order: 'ascending' | 'descending'): Promise<boolean> {
        try {
            const targetPriceHeader = this.page.locator(this.targetPriceColumnHeader);
            await targetPriceHeader.waitFor({ state: 'visible', timeout: 5000 });
            // Get current sort state
            const currentSortAttr = await targetPriceHeader.getAttribute('aria-sort');
            Logger(`Current sort state: ${currentSortAttr}`);
            if (order === 'ascending') {
                if (currentSortAttr !== 'ascending') {
                    await targetPriceHeader.click();
                    await this.page.waitForTimeout(2000);
                    // Check if we got ascending, if not, click again
                    const newSortAttr = await targetPriceHeader.getAttribute('aria-sort');
                    if (newSortAttr === 'descending') {
                        await targetPriceHeader.click();
                        await this.page.waitForTimeout(2000);
                    }
                } Logger("Sorted Target Price in ascending order");
            }
            else if (order === 'descending') {
                if (currentSortAttr !== 'descending') {
                    await targetPriceHeader.click();
                    await this.page.waitForTimeout(2000);
                    // Check if we got descending, if not, click again
                    const newSortAttr = await targetPriceHeader.getAttribute('aria-sort');
                    if (newSortAttr === 'ascending') {
                        await targetPriceHeader.click();
                        await this.page.waitForTimeout(2000);
                    }
                } Logger("Sorted Target Price in descending order");
            }
            return true;
        } catch (error) {
            Logger(`Failed to sort Target Price: ${error}`);
            return false;
        }
    }

    async sortByProductID(order: 'ascending' | 'descending'): Promise<boolean> {
        try {
            const productIDHeader = this.page.locator(this.productIDColumnHeader);
            await productIDHeader.waitFor({ state: 'visible', timeout: 5000 });

            // Get current sort state
            const currentSortAttr = await productIDHeader.getAttribute('aria-sort');
            Logger(`Current sort state: ${currentSortAttr}`);

            // Click to sort ascending (first click if unsorted or descending)
            if (order === 'ascending') {
                if (currentSortAttr !== 'ascending') {
                    await productIDHeader.click();
                    await this.page.waitForTimeout(2000);
                    // Check if we got ascending, if not, click again
                    const newSortAttr = await productIDHeader.getAttribute('aria-sort');
                    if (newSortAttr === 'descending') {
                        await productIDHeader.click();
                        await this.page.waitForTimeout(2000);
                    }
                }
                Logger("Sorted Product ID in ascending order");
            }
            // Click to sort descending
            else if (order === 'descending') {
                if (currentSortAttr !== 'descending') {
                    await productIDHeader.click();
                    await this.page.waitForTimeout(2000);
                    // Check if we got descending, if not, click again
                    const newSortAttr = await productIDHeader.getAttribute('aria-sort');
                    if (newSortAttr === 'ascending') {
                        await productIDHeader.click();
                        await this.page.waitForTimeout(2000);
                    }
                }
                Logger("Sorted Product ID in descending order");
            }
            return true;
        } catch (error) {
            Logger(`Failed to sort Product ID: ${error}`);
            return false;
        }
    }

    async filterByProductIDAndGrade(productID: string, grade: string): Promise<boolean> {
        try {
            await waitAndFill(this.page, "(//input[@aria-label='Search'])[1]", productID);
            await this.page.keyboard.press('Tab');
            await this.page.waitForTimeout(1000);
            await waitAndFill(this.page, "(//input[@aria-label='Search'])[5]", grade);
            await this.page.keyboard.press('Tab');
            await this.page.waitForTimeout(1000);
            return true;
        } catch (error) {
            Logger(`Failed to filter by ProductID and Grade: ${error}`);
            return false;
        }
    }

    private async clearProductFilter(): Promise<void> {
        await waitAndFill(this.page, "(//input[@aria-label='Search'])[1]", "");
        await this.page.keyboard.press('Tab');
        await this.page.waitForTimeout(500);
        await waitAndFill(this.page, "(//input[@aria-label='Search'])[5]", "");
        await this.page.keyboard.press('Tab');
        await this.page.waitForTimeout(500);
    }
}

```
