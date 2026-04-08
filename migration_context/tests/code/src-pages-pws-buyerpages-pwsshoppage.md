# Page Object: PWS_ShopPage.ts

- **Path**: `src\pages\PWS\BuyerPages\PWS_ShopPage.ts`
- **Category**: Page Object
- **Lines**: 907
- **Size**: 39,069 bytes
- **Members**: `class PWS_ShopPage`, `ensureUserOnShopPage`, `ensureUserOnShopPage2`, `sortAvlQty`, `clickCartButton`, `selectMoreActionOption`, `getRowData`, `getAndValidateOfferNumber`, `getCaseLotRowData`, `enterCaseOfferData`, `attemptOverPurchase`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';
import { BaseTest } from '../../../tests/BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { waitAndClick } from '../../../utils/helpers/commonMethods';

export class PWS_ShopPage {
    private readonly switchBuyerInputFieldSelector = "//input[@class='widget-combobox-input']";
    private readonly viewAsPlaceholder = "//span[@class='widget-combobox-caption-text']";
    private readonly pwsDataGridTable = "(//div[@class = 'widget-datagrid-grid table'])[1]";
    private readonly gridCells = "//div[@role='gridcell']";
    private readonly filter_SKU = "//div[@title='SKU']//input[@class='form-control']";
    private readonly allRows = "//div[@class='widget-datagrid-grid-body table-content']/div";
    private readonly AvlQty_HeaderColumn = "(//div[@class='column-header clickable align-column-left']/span[text()='Avl. Qty'])[1]";
    private readonly myOfferRows = "//div[contains(@class, 'pws-my-offer-datagrid')]//div[@class='widget-datagrid-grid-body']//div[@role='row']";
    private readonly COLUMN_INDEX = {
        List_Price: 9,
        Avl_Qty: 8,
        Offer_Price: 10,
        Offer_Qty: 11,
        Total: 12
    };
    private readonly moreAction_dotMenu = "//a[@role='button']/img";
    private readonly moreAction_options = "//div[contains(@class,'mx-name-container19 usericon_settings_dropdow')]//a"
    private readonly confirmResetButton = "button.btn.mx-button.mx-name-actionButton1.pws-feedback-button";
    private readonly CartButton = "//span[text()='Cart']";
    private readonly sum_SKUs = "(//div[contains(@class,'pws-inventory-summary')])[1]//span[2]";
    private readonly sum_Qty = "(//div[contains(@class,'pws-inventory-summary')])[3]//span[2]";
    private readonly sum_Total = "(//div[contains(@class,'pws-inventory-summary')])[5]//span[2]";


    constructor(private page: Page) { }

    async ensureUserOnShopPage(base: BaseTest, userRole: userRole) {
        try {
            const isInventoryVisible = await this.page.locator(this.pwsDataGridTable).isVisible().catch(() => false);
            if (isInventoryVisible) {
                Logger("Already on Inventory page, no login needed.");
                return;
            }
        } catch (error) {
            Logger("Inventory table not displayed, will login and navigate.");
        }
        await base['loginPage'].loginAs(userRole);
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
    }

    async ensureUserOnShopPage2(base: BaseTest, username: string, password: string, buyerCode: string) {
        try {
            const isInventoryVisible = await this.page.locator(this.pwsDataGridTable).isVisible().catch(() => false);
            if (isInventoryVisible) {
                Logger("Already on Inventory page, no login needed.");
                return;
            }
        } catch (error) {
            Logger("Inventory table not displayed, will login and navigate.");
        }
        await base['loginPage'].login(username, password);
        await base['welcomePage'].selectBuyerCode(buyerCode);
        await base['page'].waitForTimeout(3000);
    }

    async getBuyerFromViewAs(): Promise<string> {
        const buyerViewAsLocator = this.page.locator(this.viewAsPlaceholder);
        await buyerViewAsLocator.waitFor({ state: 'visible', timeout: 5000 });
        const isVisible = await buyerViewAsLocator.isVisible().catch(() => false);
        if (!isVisible) return '';
        return (await buyerViewAsLocator.textContent())?.trim() || '';
    }

    async selectInventoryTab(tabName: "Functional Devices" | "Functional Case Lots" | "Untested Devices"): Promise<void> {
        // Robust selector using filter
        const tabLocator = this.page.locator('li').filter({ hasText: tabName }).first();
        await tabLocator.waitFor({ state: 'visible', timeout: 10000 });
        await tabLocator.click();
        Logger(`Selected inventory tab: ${tabName}`);
        if (tabName === "Functional Case Lots") {
            const caseLotsText = this.page.locator("//span[contains(text(),'Case lots are sold AS IS')]");
            await caseLotsText.waitFor({ state: 'visible', timeout: 5000 });
            Logger("Verified: Functional Case Lots tab content is displayed.");
        }
        if (tabName === "Untested Devices") {
            const untestedText = this.page.locator("//span[contains(text(),'Untested devices are')]");
            await untestedText.waitFor({ state: 'visible', timeout: 5000 });
            Logger("Verified: Untested Devices tab content is displayed.");
        }
    }

    async sortAvlQty(target: "descending" | "ascending") {
        const isExpectedQty = (text: string): boolean => {
            const cleanedText = text.trim();

            if (target === "descending") {
                // Biggest value is displayed as "100+"
                return cleanedText === "100+" || parseInt(cleanedText.replace(/,/g, "")) >= 100;
            }

            // Ascending → smallest value is 1
            return parseInt(cleanedText.replace(/,/g, "")) === 1;
        };

        for (let attempt = 1; attempt <= 3; attempt++) {
            const rowLocator = this.page.locator(this.allRows);
            await rowLocator.first().waitFor({ state: "visible", timeout: 5000 });

            const rows = await rowLocator.all();
            if (rows.length === 0) {
                throw new Error("No rows found in the inventory table.");
            }

            const firstRow = rows[0];
            const cells = await firstRow.locator(this.gridCells).all();
            const avlQtyCell = cells[this.COLUMN_INDEX.Avl_Qty];

            await avlQtyCell.waitFor({ state: "visible", timeout: 5000 });
            await expect(avlQtyCell).not.toHaveText("", { timeout: 5000 });

            const avlQtyText = (await avlQtyCell.textContent()) ?? "";

            if (isExpectedQty(avlQtyText)) {
                break;
            }

            if (attempt < 3) {
                await this.page.locator(this.AvlQty_HeaderColumn).click();
                await this.page.waitForTimeout(1000);
            }
        }
    }

    async filterBySKU(sku: string): Promise<void> {
        const filterInput = this.page.locator(this.filter_SKU);
        await filterInput.fill('');
        await filterInput.type(sku);
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(1500);
    }

    async enterOfferData(rowIndex: number, priceValue: number, qtyValue: number): Promise<Array<{ price: number, qty: number, total: number }>> {
        await this.page.waitForSelector(this.pwsDataGridTable, { state: 'visible' });
        const cells = await this.page.locator(this.gridCells).all();
        const columnsPerRow = 14;
        if (rowIndex >= Math.floor(cells.length / columnsPerRow)) {
            throw new Error(`Row index ${rowIndex} is out of bounds`);
        }
        const startIdx = rowIndex * columnsPerRow;
        const offerPriceCell = cells[startIdx + this.COLUMN_INDEX.Offer_Price];
        const offerQtyCell = cells[startIdx + this.COLUMN_INDEX.Offer_Qty];
        const totalValueCell = cells[startIdx + this.COLUMN_INDEX.Total];
        // Enter price
        await offerPriceCell.dblclick();
        await this.page.waitForTimeout(100);
        await this.page.keyboard.press('Control+A');
        await this.page.keyboard.press('Backspace');
        await this.page.keyboard.type(priceValue.toString());
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(500);
        // Enter qty
        await offerQtyCell.dblclick();
        await this.page.keyboard.press('Control+A');
        await this.page.keyboard.press('Backspace');
        await this.page.keyboard.type(qtyValue.toString());
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(2000);
        // Get updated values
        const updatedPrice = await offerPriceCell.textContent() || "";
        const updatedQty = await offerQtyCell.textContent() || "";
        const totalValue = await totalValueCell.textContent() || "";
        let priceNum = 0;
        if (updatedPrice && updatedPrice.trim() !== "") {
            priceNum = parseFloat(updatedPrice.replace("$", "").replace(",", ""));
        }
        let qtyNum = 0;
        if (updatedQty && updatedQty.trim() !== "") {
            qtyNum = parseInt(updatedQty.trim());
        }
        let totalNum = 0;
        if (totalValue && totalValue.trim() !== "") {
            totalNum = parseFloat(totalValue.replace("$", "").replace(",", ""));
        }
        Logger(`Row ${rowIndex + 1}: Updated values - Price: "${priceNum}", Qty: "${qtyNum}", Total: "${totalNum}"`);
        return [{ price: priceNum, qty: qtyNum, total: totalNum }];
    }

    async getSummaryOffer(): Promise<string[]> {
        console.log("Getting Summary Offer Details from Shop Page");
        const skuLocator = this.page.locator(this.sum_SKUs);
        const qtyLocator = this.page.locator(this.sum_Qty);
        const totalLocator = this.page.locator(this.sum_Total);
        await skuLocator.waitFor({ state: 'visible', timeout: 5000 });
        await qtyLocator.waitFor({ state: 'visible', timeout: 5000 });
        await totalLocator.waitFor({ state: 'visible', timeout: 5000 });
        const SKUs = (await skuLocator.textContent())?.trim() ?? "0";
        const Qty = (await qtyLocator.textContent())?.trim() ?? "0";
        const totalText = (await totalLocator.textContent())?.trim() ?? "0";
        const Total = totalText.replace('$', "").replace(',', "");
        console.log(`SKUs: ${SKUs}, Qty: ${Qty}, Total: ${Total}`);
        return [SKUs, Qty, Total];
    }

    async clickCartButton() {
        await this.page.waitForTimeout(2000);
        // Use role-based selector to avoid strict mode violation when Cart overlay shows a second "Cart" text
        const cartButton = this.page.getByRole('button', { name: 'Cart' });
        await cartButton.waitFor({ state: 'visible', timeout: 5000 });
        await cartButton.click();
        await this.page.waitForTimeout(2000);
    }

    async selectMoreActionOption(option: "Reset Offer" | "Upload Order" | "Download Offer" | "Download Current View" | "Download All Item") {
        await this.page.locator(this.moreAction_dotMenu).click();
        const options = this.page.locator(this.moreAction_options);
        await options.first().waitFor({ state: 'visible', timeout: 5000 });
        const optionsCount = await options.count();
        for (let i = 0; i < optionsCount; i++) {
            const eachOption = await options.nth(i).textContent();
            if (eachOption?.trim().toLowerCase() === option.toLowerCase()) {
                await options.nth(i).click();
                Logger(`Clicked More Action option: ${option}`);
                if (option === "Reset Offer") {
                    await this.page.waitForTimeout(500);
                    await this.page.locator(this.confirmResetButton).waitFor({ state: 'visible', timeout: 5000 });
                    // Use evaluate to bypass overlay interception
                    await this.page.evaluate((selector) => {
                        const btn = document.querySelector(selector) as HTMLElement;
                        if (btn) btn.click();
                    }, this.confirmResetButton);
                    Logger("Clicked confirm reset button.");
                    // Wait for the reset modal/dialog to fully disappear
                    await this.page.locator('[role="dialog"]').waitFor({ state: 'hidden', timeout: 15000 }).catch(() => { });
                    await this.page.locator('.mx-underlay').waitFor({ state: 'hidden', timeout: 5000 }).catch(() => { });
                    // Press Escape as fallback to dismiss any remaining overlays
                    await this.page.keyboard.press('Escape').catch(() => { });
                    await this.page.waitForTimeout(2000);
                }
                return;
            }
        } throw new Error(`More Action option "${option}" not found.`);
    }

    async getRowData(rowIndex: number): Promise<{ sku: string, price: number, qty: number, total: number }> {
        await this.page.waitForSelector(this.gridCells, { state: 'visible' });
        const cells = await this.page.locator(this.gridCells).all();
        const columnsPerRow = 14;

        if (rowIndex >= Math.floor(cells.length / columnsPerRow)) {
            throw new Error(`Row index ${rowIndex} is out of bounds`);
        }
        const startIdx = rowIndex * columnsPerRow;
        const skuCell = cells[startIdx]; // First column is SKU
        const offerPriceCell = cells[startIdx + this.COLUMN_INDEX.Offer_Price];
        const offerQtyCell = cells[startIdx + this.COLUMN_INDEX.Offer_Qty];
        const totalValueCell = cells[startIdx + this.COLUMN_INDEX.Total];
        const sku = (await skuCell.textContent() || "").trim();
        const offerPrice = await offerPriceCell.textContent() || "";
        const offerQty = await offerQtyCell.textContent() || "";
        const totalValue = await totalValueCell.textContent() || "";
        let priceNum = 0;
        if (offerPrice && offerPrice.trim() !== "") {
            priceNum = parseFloat(offerPrice.replace("$", "").replace(",", ""));
        }
        let qtyNum = 0;
        if (offerQty && offerQty.trim() !== "") {
            qtyNum = parseInt(offerQty.trim());
        }
        let totalNum = 0;
        if (totalValue && totalValue.trim() !== "") {
            totalNum = parseFloat(totalValue.replace("$", "").replace(",", ""));
        }
        return { sku, price: priceNum, qty: qtyNum, total: totalNum };
    }

    async getAndValidateOfferNumber(buyerCode: string): Promise<{
        offerNumber: string,
        isValid: boolean,
        buyerCodeValid: boolean,
        yearValid: boolean,
        sequenceValid: boolean,
        extractedBuyerCode?: string,
        extractedYear?: string,
        extractedSequence?: string
    }> {
        try {
            await this.page.waitForTimeout(1000);
            const offerSubmittedMessage = "//span[contains(text(), 'Your offer has been submitted')]";
            const messageText = await this.page.locator(offerSubmittedMessage).textContent() || "";
            const match = messageText.match(/offer number: (\w+)/);
            let offerNumber = "Unknown";
            if (match && match[1]) {
                offerNumber = match[1];
                const currentYear = new Date().getFullYear() % 100;
                const extractedBuyerCode = offerNumber.substring(0, buyerCode.length);
                const extractedYear = offerNumber.substring(buyerCode.length, buyerCode.length + 2);
                const extractedSequence = offerNumber.substring(buyerCode.length + 2);
                const buyerCodeValid = extractedBuyerCode === buyerCode;
                const yearValid = extractedYear === currentYear.toString().padStart(2, '0');
                const sequenceValid = /^\d{3,}$/.test(extractedSequence); // Sequence should be at least 3 digits
                const isValid = buyerCodeValid && yearValid && sequenceValid;
                return {
                    offerNumber,
                    isValid,
                    buyerCodeValid,
                    yearValid,
                    sequenceValid,
                    extractedBuyerCode,
                    extractedYear,
                    extractedSequence
                };
            }
            return {
                offerNumber,
                isValid: false,
                buyerCodeValid: false,
                yearValid: false,
                sequenceValid: false
            };
        } catch (error) {
            if (error && typeof error === 'object' && 'message' in error) {
                Logger(`Error getting offer number: ${error.message}`);
            } else {
                Logger(`Error getting offer number: ${String(error)}`);
            }
            return {
                offerNumber: "Error",
                isValid: false,
                buyerCodeValid: false,
                yearValid: false,
                sequenceValid: false
            };
        }
    }

    async isOfferPriceColumnDisplayed(): Promise<boolean> {
        const rowLocator = this.page.locator(this.allRows);
        await expect(rowLocator.first()).toBeVisible({ timeout: 8000 });
        const rows = await rowLocator.all();
        if (rows.length === 0) return false;
        const firstRow = rows[0];
        const cells = await firstRow.locator("div[role='gridcell']").all();
        const offerPriceCell = cells[this.COLUMN_INDEX.Total];
        if (!offerPriceCell) return false;
        return await offerPriceCell.isVisible();
    }

    async verifyAndLogPwsDataGridTableisplay(message: string = "PWS Inventory table is displayed"): Promise<boolean> {
        await this.page.waitForLoadState('domcontentloaded');
        Logger(`Checking for inventory table visibility... selector: ${this.pwsDataGridTable}`);

        try {
            // Wait for the grid to be attached and visible
            await this.page.locator(this.pwsDataGridTable).waitFor({ state: 'visible', timeout: 15000 });
            const isVisible = await this.page.locator(this.pwsDataGridTable).isVisible();

            if (isVisible) {
                Logger(message);
                return true;
            } else {
                Logger("PWS Inventory table locator found but reported not visible.");
                return false;
            }
        } catch (error: any) {
            Logger(`PWS Inventory table NOT found or timed out. Error: ${error.message}`);
            // Take a screenshot if possible for debugging (optional)
            return false;
        }
    }

    /**
     * Extract all visible table data for Excel comparison
     * @param maxRows Maximum number of rows to extract (default: 10, use -1 for all)
     * @returns Array of row data objects matching InventoryRowData interface
     */
    async getAllVisibleTableData(maxRows: number = 10): Promise<Array<{
        sku: string;
        category: string;
        brand: string;
        model: string;
        carrier: string;
        capacity: string;
        color: string;
        grade: string;
        avlQty: number;
        price: number;
    }>> {
        await this.page.waitForSelector(this.pwsDataGridTable, { state: 'visible' });
        await this.page.waitForTimeout(1000);

        const rowLocator = this.page.locator(this.allRows);
        await rowLocator.first().waitFor({ state: 'visible', timeout: 5000 });

        const rows = await rowLocator.all();
        const rowCount = maxRows === -1 ? rows.length : Math.min(rows.length, maxRows);
        const tableData: Array<{
            sku: string;
            category: string;
            brand: string;
            model: string;
            carrier: string;
            capacity: string;
            color: string;
            grade: string;
            avlQty: number;
            price: number;
        }> = [];

        // Column indices based on the grid structure
        // SKU, Category, Brand, Model Family, Carrier, Capacity, Color, Grade, Avl. Qty, Price, Offer Price, Cart Qty, Total
        const COLUMN = {
            SKU: 0,
            CATEGORY: 1,
            BRAND: 2,
            MODEL: 3,
            CARRIER: 4,
            CAPACITY: 5,
            COLOR: 6,
            GRADE: 7,
            AVL_QTY: 8,
            PRICE: 9
        };

        for (let i = 0; i < rowCount; i++) {
            const row = rows[i];
            const cells = await row.locator(this.gridCells).all();

            if (cells.length < 10) {
                Logger(`Row ${i + 1} has fewer cells than expected, skipping`);
                continue;
            }

            const skuText = (await cells[COLUMN.SKU].textContent() || "").trim();
            const categoryText = (await cells[COLUMN.CATEGORY].textContent() || "").trim();
            const brandText = (await cells[COLUMN.BRAND].textContent() || "").trim();
            const modelText = (await cells[COLUMN.MODEL].textContent() || "").trim();
            const carrierText = (await cells[COLUMN.CARRIER].textContent() || "").trim();
            const capacityText = (await cells[COLUMN.CAPACITY].textContent() || "").trim();
            const colorText = (await cells[COLUMN.COLOR].textContent() || "").trim();
            const gradeText = (await cells[COLUMN.GRADE].textContent() || "").trim();
            const priceText = (await cells[COLUMN.PRICE].textContent() || "").trim();
            const avlQtyText = (await cells[COLUMN.AVL_QTY].textContent() || "").trim();

            // Parse numeric values
            const price = parseFloat(priceText.replace("$", "").replace(",", "")) || 0;
            const avlQty = avlQtyText === "100+" ? 100 : parseInt(avlQtyText.replace(",", "")) || 0;

            tableData.push({
                sku: skuText,
                category: categoryText,
                brand: brandText,
                model: modelText,
                carrier: carrierText,
                capacity: capacityText,
                color: colorText,
                grade: gradeText,
                avlQty,
                price
            });

            Logger(`Row ${i + 1}: SKU=${skuText}, Model=${modelText}, Grade=${gradeText}, Price=${price}`);
        }

        Logger(`Extracted ${tableData.length} rows from inventory table`);
        return tableData;
    }

    /**
     * Extract all table data across all pages
     * @returns Array of all row data from all pages
     */
    async getAllTableDataAcrossPages(): Promise<Array<{
        sku: string;
        category: string;
        brand: string;
        model: string;
        carrier: string;
        capacity: string;
        color: string;
        grade: string;
        avlQty: number;
        price: number;
    }>> {
        const allData: Array<{
            sku: string;
            category: string;
            brand: string;
            model: string;
            carrier: string;
            capacity: string;
            color: string;
            grade: string;
            avlQty: number;
            price: number;
        }> = [];

        let currentPage = 1;
        let hasNextPage = true;

        while (hasNextPage) {
            Logger(`Extracting data from page ${currentPage}...`);

            // Get all rows on current page
            const pageData = await this.getAllVisibleTableData(-1);
            allData.push(...pageData);

            // Check for next page button
            const nextButton = this.page.locator("button[aria-label='Go to next page']:not([disabled])");
            hasNextPage = await nextButton.isVisible().catch(() => false);

            if (hasNextPage) {
                await nextButton.click();
                await this.page.waitForTimeout(2000);
                currentPage++;
            }
        }

        Logger(`Total rows extracted across ${currentPage} pages: ${allData.length}`);
        return allData;
    }

    /**
     * Download Excel file using More Actions menu
     * @param option Download option to use
     * @returns Path to the downloaded file
     */
    async downloadExcel(option: "Download Current View" | "Download All Item" = "Download Current View"): Promise<string> {
        const path = require('path');
        const testDataDir = "./src/test-data/";

        await this.selectMoreActionOption(option);

        const [download] = await Promise.all([
            this.page.waitForEvent('download'),
            this.page.locator('.mx-name-actionButton20').click()
        ]);

        const fileName = download.suggestedFilename();
        const filePath = path.join(testDataDir, fileName);
        await download.saveAs(filePath);

        Logger(`Downloaded Excel file: ${fileName}`);
        return filePath;
    }

    // =============================================
    // CASE LOT SPECIFIC METHODS
    // =============================================

    /**
     * Column indexes for Case Lot grid (11 columns including Column selector)
     * ["SKU", "Model Family", "Case Pack Qty", "Avl. Cases", "Unit Price", "Case Price", "No. Cases", "Unit Offer", "Case Offer", "Total", "Column selector"]
     * Note: "Unit Offer" (col 7) is the INPUT field for price. "Case Offer" (col 8) is auto-calculated.
     */
    private readonly CASE_LOT_COLUMN_INDEX = {
        SKU: 0,
        Model: 1,
        Units_Per_Case: 2,  // "Case Pack Quantity"
        Avl_Cases: 3,
        Unit_Price: 4,
        Case_Price: 5,
        Offer_Qty: 6,       // "No. Cases" (Input)
        Unit_Offer: 7,      // "Unit Offer" (Input — price per unit)
        Case_Offer: 8,      // "Case Offer" (Calculated — Unit Offer × Case Pack Qty)
        Total: 9,
        Column_Selector: 10
    };

    /**
     * Verify the Case Lots disclaimer is displayed
     * "Case lots are sold AS IS"
     */
    async verifyCaseLotDisclaimer(): Promise<boolean> {
        const disclaimer = this.page.locator("//span[contains(text(),'Case lots are sold AS IS')]");
        try {
            await disclaimer.waitFor({ state: 'visible', timeout: 5000 });
            Logger("Case Lots disclaimer is visible: 'Case lots are sold AS IS'");
            return true;
        } catch {
            Logger("Case Lots disclaimer NOT visible");
            return false;
        }
    }

    /**
     * Get Case Lot row data
     */
    async getCaseLotRowData(rowIndex: number): Promise<{
        sku: string;
        unitsPerCase: number;
        availableCases: number;
        unitPrice: number;       // Per-unit list price (col 4 "Unit Price")
        pricePerCase: number;    // Per-case list price (col 5 "Case Price")
        offerPrice: number;
        offerQty: number;
        total: number;
    }> {
        // Target the visible grid cells specifically
        // Target the second grid (Case Lots) specifically - Index 1 in 0-based, or [2] in XPath
        const gridSelector = "(//div[contains(@class,'widget-datagrid-grid-body')])[2]//div[@role='gridcell']";
        const cells = await this.page.locator(gridSelector).all();
        const columnsPerRow = 11; // 11 columns including Column selector

        if (rowIndex >= Math.floor(cells.length / columnsPerRow)) {
            // Fallback: try finding any visible grid
            Logger(`Warning: Row index ${rowIndex} out of bounds (Cells: ${cells.length}). Grid selector might be wrong or grid empty.`);
        }

        const startIdx = rowIndex * columnsPerRow;
        const COL = this.CASE_LOT_COLUMN_INDEX;

        const getText = async (idx: number) => {
            if (startIdx + idx >= cells.length) return "";
            return (await cells[startIdx + idx]?.textContent() || "").trim();
        };

        const parseNum = (text: string) => parseFloat(text.replace(/[$,]/g, "")) || 0;
        const parseInt_ = (text: string) => {
            const cleaned = text.replace(/[,+]/g, "");
            return cleaned === "" ? 0 : parseInt(cleaned);
        };

        const sku = await getText(COL.SKU);
        const unitsPerCaseText = await getText(COL.Units_Per_Case);
        const avlCasesText = await getText(COL.Avl_Cases);
        const unitPriceText = await getText(COL.Unit_Price);       // Per-unit price
        const pricePerCaseText = await getText(COL.Case_Price);    // Per-case price
        const offerPriceText = await getText(COL.Case_Offer);
        const offerQtyText = await getText(COL.Offer_Qty);
        const totalText = await getText(COL.Total);

        return {
            sku,
            unitsPerCase: parseInt_(unitsPerCaseText),
            availableCases: parseInt_(avlCasesText),
            unitPrice: parseNum(unitPriceText),
            pricePerCase: parseNum(pricePerCaseText),
            offerPrice: parseNum(offerPriceText),
            offerQty: parseInt_(offerQtyText),
            total: parseNum(totalText)
        };
    }

    /**
     * Get available cases for a specific row
     */
    async getAvailableCases(rowIndex: number): Promise<number> {
        const rowData = await this.getCaseLotRowData(rowIndex);
        return rowData.availableCases;
    }

    /**
     * Enter Case Lot offer data (price per case and number of cases)
     */
    async enterCaseOfferData(rowIndex: number, pricePerCase: number, caseQty: number): Promise<{
        sku: string;
        unitsPerCase: number;
        casesRequested: number;
        totalUnits: number;
        pricePerCase: number;
        total: number;
    }> {
        const gridSelector = "(//div[contains(@class,'widget-datagrid-grid-body')])[2]//div[@role='gridcell']";
        const cells = await this.page.locator(gridSelector).all();
        const columnsPerRow = 11; // 11 columns including Column selector
        const startIdx = rowIndex * columnsPerRow;
        const COL = this.CASE_LOT_COLUMN_INDEX;

        // "No. Cases" (col 6) = qty input, "Unit Offer" (col 7) = price input
        // Enter qty first (column order: No. Cases comes before Unit Offer)
        const offerQtyCell = cells[startIdx + COL.Offer_Qty];
        const unitOfferCell = cells[startIdx + COL.Unit_Offer];

        // Enter case quantity (No. Cases)
        await offerQtyCell.dblclick();
        await this.page.waitForTimeout(100);
        await this.page.keyboard.press('Control+A');
        await this.page.keyboard.press('Backspace');
        await this.page.keyboard.type(caseQty.toString());
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(500);

        // Enter unit price (Unit Offer — NOT Case Offer which is auto-calculated)
        await unitOfferCell.dblclick();
        await this.page.waitForTimeout(100);
        await this.page.keyboard.press('Control+A');
        await this.page.keyboard.press('Backspace');
        await this.page.keyboard.type(pricePerCase.toString());
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(2000);

        // Get row data after entry
        const rowData = await this.getCaseLotRowData(rowIndex);

        const result = {
            sku: rowData.sku,
            unitsPerCase: rowData.unitsPerCase,
            casesRequested: caseQty,
            totalUnits: rowData.unitsPerCase * caseQty,
            pricePerCase: rowData.offerPrice || pricePerCase, // Use input if read is empty
            total: rowData.total
        };

        Logger(`Case Lot Row ${rowIndex + 1}: SKU=${result.sku}, Units/Case=${result.unitsPerCase}, Cases=${caseQty}, Total Units=${result.totalUnits}, Total=$${result.total}`);
        return result;
    }

    /**
     * Check if an error message is displayed for over-purchasing cases
     * @returns Error message text if displayed, null otherwise
     */
    async getCaseLotErrorMessage(): Promise<string | null> {
        // Common error message patterns - adjust based on actual UI
        const errorSelectors = [
            "//div[contains(@class, 'alert-danger')]//span",
            "//div[contains(@class, 'error')]//span",
            "//span[contains(@class, 'text-danger')]",
            "//div[contains(text(), 'exceed')]",
            "//div[contains(text(), 'available')]"
        ];

        for (const selector of errorSelectors) {
            try {
                const errorElement = this.page.locator(selector);
                const isVisible = await errorElement.isVisible({ timeout: 2000 }).catch(() => false);
                if (isVisible) {
                    const errorText = await errorElement.textContent();
                    Logger(`Error message found: ${errorText}`);
                    return errorText?.trim() || null;
                }
            } catch {
                // Continue checking other selectors
            }
        }

        Logger("No error message displayed for over-purchase");
        return null;
    }

    /**
     * Attempt to purchase more cases than available and verify error
     * @returns Object with validation result and error message (if any)
     */
    async attemptOverPurchase(rowIndex: number): Promise<{
        availableCases: number;
        attemptedQty: number;
        errorDisplayed: boolean;
        errorMessage: string | null;
    }> {
        const availableCases = await this.getAvailableCases(rowIndex);
        const overQty = availableCases + 1;

        Logger(`Attempting to purchase ${overQty} cases when only ${availableCases} available`);

        // Enter qty that exceeds available
        await this.enterCaseOfferData(rowIndex, 1.00, overQty);

        // Wait a moment for validation to trigger
        await this.page.waitForTimeout(1000);

        const errorMessage = await this.getCaseLotErrorMessage();

        return {
            availableCases,
            attemptedQty: overQty,
            errorDisplayed: errorMessage !== null,
            errorMessage
        };
    }

    // =============================================
    // SEARCH, FILTER, PAGINATION METHODS
    // =============================================

    /**
     * Filter Case Lot grid by SKU using the SKU column search box
     * The Case Lot grid is the 2nd datagrid on the page
     */
    async filterCaseLotBySKU(sku: string): Promise<void> {
        // Case Lots SKU filter is the 2nd SKU filter input on the page
        const skuInputs = this.page.locator("//div[@title='SKU']//input[@class='form-control']");
        const caseLotSKUInput = skuInputs.nth(1); // 2nd grid's SKU filter
        await caseLotSKUInput.fill('');
        await caseLotSKUInput.type(sku);
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(2000);
        Logger(`Filtered Case Lot grid by SKU: "${sku}"`);
    }

    /**
     * Clear the SKU filter on Case Lot grid and restore full results
     */
    async clearCaseLotSKUFilter(): Promise<void> {
        const skuInputs = this.page.locator("//div[@title='SKU']//input[@class='form-control']");
        const caseLotSKUInput = skuInputs.nth(1);
        await caseLotSKUInput.fill('');
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(2000);
        Logger("Cleared Case Lot SKU filter");
    }

    /**
     * Get the pagination info text for Case Lot grid (e.g., "Currently showing 1 to 10 of 10")
     * Returns the text or empty string if not found
     */
    async getCaseLotPaginationInfo(): Promise<string> {
        try {
            // Pagination info for Case Lots grid (2nd pagination on page)
            const paginationLocator = this.page.locator("(//div[contains(@class,'widget-datagrid')])[2]//div[contains(@class,'pagination-text')]");
            if (await paginationLocator.isVisible()) {
                return (await paginationLocator.textContent())?.trim() ?? "";
            }

            // Fallback: look for any "Currently showing" text in the Case Lots tab panel
            const showingText = this.page.locator("div[role='tabpanel'] >> text=/Currently showing/");
            if (await showingText.isVisible().catch(() => false)) {
                return (await showingText.textContent())?.trim() ?? "";
            }
            return "";
        } catch {
            return "";
        }
    }

    /**
     * Count visible Case Lot rows in the grid
     */
    async getCaseLotRowCount(): Promise<number> {
        const gridSelector = "(//div[contains(@class,'widget-datagrid-grid-body')])[2]//div[@role='gridcell']";
        const cells = await this.page.locator(gridSelector).all();
        const columnsPerRow = 11;
        const rowCount = Math.floor(cells.length / columnsPerRow);
        Logger(`Case Lot row count: ${rowCount}`);
        return rowCount;
    }

    /**
     * Filter Case Lot grid by Model Family using the column dropdown filter
     * @param modelFamily The model family text to select (e.g., "iPhone 14")
     */
    async filterCaseLotByModelFamily(modelFamily: string): Promise<void> {
        // The Model Family "Show options" button in Case Lots is the 2nd one on the page
        const showOptionsButtons = this.page.locator(
            "(//div[contains(@class,'widget-datagrid')])[2]//div[@title='Model Family']//button[contains(@aria-label,'Show options') or contains(text(),'Show options')]"
        );
        // Click the dropdown
        await showOptionsButtons.first().click();
        await this.page.waitForTimeout(500);

        // Type into the search combobox and select the option
        const searchInput = this.page.locator(
            "(//div[contains(@class,'widget-datagrid')])[2]//div[@title='Model Family']//input[@role='combobox' or contains(@class,'form-control')]"
        );
        await searchInput.first().fill(modelFamily);
        await this.page.waitForTimeout(1000);

        // Click the matching option in the dropdown
        const option = this.page.locator(`div[role='option']:has-text("${modelFamily}")`);
        if (await option.isVisible().catch(() => false)) {
            await option.click();
        } else {
            // Try pressing Enter as fallback
            await this.page.keyboard.press('Enter');
        }
        await this.page.waitForTimeout(2000);
        Logger(`Filtered Case Lot grid by Model Family: "${modelFamily}"`);
    }

    /**
     * Clear Model Family filter on Case Lot grid
     */
    async clearCaseLotModelFamilyFilter(): Promise<void> {
        const searchInput = this.page.locator(
            "(//div[contains(@class,'widget-datagrid')])[2]//div[@title='Model Family']//input[@role='combobox' or contains(@class,'form-control')]"
        );
        if (await searchInput.first().isVisible().catch(() => false)) {
            await searchInput.first().fill('');
            await this.page.keyboard.press('Enter');
            await this.page.waitForTimeout(2000);
        }
        Logger("Cleared Case Lot Model Family filter");
    }

    /**
     * Get all visible "More Actions" menu option labels
     * @returns Array of option text labels
     */
    async getMoreActionOptions(): Promise<string[]> {
        // Click the More Actions dot menu to open it
        await this.page.locator(this.moreAction_dotMenu).first().click();
        await this.page.waitForTimeout(500);

        // Collect all option labels
        const options = await this.page.locator(this.moreAction_options).all();
        const labels: string[] = [];
        for (const opt of options) {
            const text = (await opt.textContent())?.trim();
            if (text) labels.push(text);
        }
        Logger(`More Actions options: [${labels.join(', ')}]`);

        // Close menu by clicking elsewhere
        await this.page.keyboard.press('Escape');
        await this.page.waitForTimeout(300);

        return labels;
    }
}


```
