# Page Object: PWS_OfferDetailPage.ts

- **Path**: `src\pages\PWS\AdminPages\PWS_OfferDetailPage.ts`
- **Category**: Page Object
- **Lines**: 235
- **Size**: 11,606 bytes
- **Members**: `class PWS_OfferDetailsPage`, `ensureUserOnOfferDetailsPage`, `navigateToOfferDetailsPage`, `getOriginalOfferSummary`, `getCounterOfferSummary`, `salesActionEachSKU`, `getItemInfoByRowIndex`, `clickCompleteReviewButton`, `clickBackToAllOffersLink`, `clickCloseSubmittedConfirmationModal`, `clickCloseErrorModalPopup`, `moreActionOption`

## Source Code

```typescript
import { Page } from '@playwright/test';
import { clickLocatorAnddownloadFile, Logger } from '../../../utils/helpers/data_utils';
import * as XLSX from 'xlsx';
import * as path from 'path';
import { PWS_OfferQueuePages } from './PWS_OfferQueuePages';
import { BaseTest } from '../../../tests/BaseTest';
import { userRole } from '../../../utils/resources/enum';

export class PWS_OfferDetailsPage {

    private readonly dashborad_displayStatus = "//span[@class='mx-text mx-name-text6 text-semibold text-large']";
    private readonly backToAllOfferLink = "//span[@class='mx-icon-lined mx-icon-chevron-left']";
    private readonly tableGrid = "//div[contains(@class,'widget-datagrid-grid-body table-content')]/div[@class='tr']";

    private readonly sumOriginal_SKUs = "//span[@class='mx-text mx-name-text5 text-semibold']";
    private readonly sumOriginal_Qty = "//span[@class='mx-text mx-name-text10 text-semibold']";
    private readonly sumOriginal_Total = "//span[@class='mx-text mx-name-text7 text-semibold']";
    private readonly sumOffer_SKUs = "//span[@class='mx-text mx-name-text19 text-semibold']";
    private readonly sumOffer_Qty = "//span[@class='mx-text mx-name-text21 text-semibold']";
    private readonly sumOffer_Total = "//span[@class='mx-text mx-name-text23 text-semibold']";

    private readonly actionDropdownRows = "//div[@class='col-lg-9 col-md col']";
    private readonly counterPriceFieldRows = "//div[contains(@class,'pws-offer-price-column')]";
    private readonly counterQtyFieldRows = "//div[contains(@class,'pws-offer-qty-column')]";
    private readonly counterTotalFieldRows = "//div[contains(@class,'pws-offer-total-column')]";

    private readonly completeReviewButton = "//button[contains(@class,'pws-button-primary-complete_review')]";
    private readonly closeSubmittedConfirmationModal = "//button[contains(@class,'modal-no-title-close-btn')]";

    private readonly errorModal_message = "//span[@class='mx-text mx-name-text4 text-secondary']";
    private readonly errorModal_closeButton = "//button[contains(@class,'modal-no-title-close-btn')]";

    private readonly moreAction_dotMenu = "//a[@role='button']/img";
    private readonly moreAction_options = "//div[contains(@class,'settings_dropdown')]//a"

    constructor(private page: Page) { }

    async ensureUserOnOfferDetailsPage(base: BaseTest, userRole: userRole, offerId: string) {
        try {
            const isOfferStatusVisible = await this.page.locator(this.dashborad_displayStatus).isVisible().catch(() => false);
            if (isOfferStatusVisible) {
                Logger("Already on OfferDetails page, no login needed.");
                return;
            }
        } catch (error) {
            Logger("Offer Details header not displayed, will login and navigate.");
        }
        await base['loginPage'].loginAs(userRole);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
        await base['pws_OfferQueuePage'].chooseOfferStatusTab("Sales Review");
        const isOfferFound = await base['pws_OfferQueuePage'].isOfferIdExistUnderAnyTab(offerId, 7);
    }

    async isSalesReviewStatusDisplayed(): Promise<boolean> {
        Logger("Validating Sales Review Status");
        const status = await this.page.locator(this.dashborad_displayStatus).textContent();
        if (status === "Sales Review") {
            Logger("Sales Review Status is displayed");
            return true;
        } else {
            Logger("Sales Review Status is not displayed");
            return false;
        }
    }

    async navigateToOfferDetailsPage(offerId: string, maxRowCount: number) {
        const offerQueuePage = new PWS_OfferQueuePages(this.page);
        await offerQueuePage.chooseOfferStatusTab("Sales Review");
        await offerQueuePage.findAndClickOfferByID(offerId, maxRowCount);
    }

    async getOriginalOfferSummary(): Promise<{ skus: string; qty: string; total: string }> {
        const skus = (await this.page.locator(this.sumOriginal_SKUs).textContent())?.trim() ?? "";
        const qty = (await this.page.locator(this.sumOriginal_Qty).textContent())?.trim() ?? "";
        const totalRaw = (await this.page.locator(this.sumOriginal_Total).textContent())?.trim() ?? "";
        const total = totalRaw.replace(/\$|,/g, "");
        return { skus, qty, total };
    }

    async getCounterOfferSummary(): Promise<{ skus: string; qty: string; total: string }> {
        const skus = (await this.page.locator(this.sumOffer_SKUs).textContent())?.trim() ?? "";
        const qty = (await this.page.locator(this.sumOffer_Qty).textContent())?.trim() ?? "";
        const totalRaw = (await this.page.locator(this.sumOffer_Total).textContent())?.trim() ?? "";
        const total = totalRaw.replace(/\$|,/g, "");
        return { skus, qty, total };
    }

    async salesActionEachSKU(rowIndex: number, actionOption: string) {
        Logger(`Sales Rep ${actionOption} offer at index ${rowIndex}`);
        const actionDiv = this.page.locator(this.actionDropdownRows).nth(rowIndex);
        await actionDiv.click();
        const select = actionDiv.locator('select');
        await select.selectOption({ value: actionOption });
        await actionDiv.click();
        await this.page.waitForTimeout(2000);
    }

    async getSalesActionStatusByRowIndex(rowIndex: number): Promise<string> {
        const actionDiv = this.page.locator(this.actionDropdownRows).nth(rowIndex);
        const select = actionDiv.locator('select');
        const selectedOption = await select.locator('option:checked').textContent();
        return selectedOption?.trim() ?? "";
    }

    async getItemInfoByRowIndex(rowIndex: number): Promise<{ sku: string, action: string }> {
        const rowLocator = this.page.locator(this.tableGrid).nth(rowIndex);
        const sku = (await rowLocator.locator('div').nth(2).textContent())?.trim() ?? "";
        const action = await this.getSalesActionStatusByRowIndex(rowIndex);
        return { sku, action };
    }

    async getSkuAndActionFromExcel(fileName: string): Promise<Array<{ sku: string, action: string }>> {
        const filePath = path.join("./src/test-data/", fileName);
        const workbook = XLSX.readFile(filePath);
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 }) as string[][];
        const header = rows[0];
        const skuIndex = header.findIndex(col => col?.toString().toLowerCase() === 'sku');
        const actionIndex = header.findIndex(col => col?.toString().toLowerCase() === 'action');
        if (skuIndex === -1 || actionIndex === -1) {
            throw new Error('SKU or Action column not found in Excel file');
        }
        const result: Array<{ sku: string, action: string }> = [];
        for (let i = 1; i < rows.length; i++) {
            const row = rows[i];
            if (row[skuIndex] && row[actionIndex]) {
                result.push({ sku: row[skuIndex].toString().trim(), action: row[actionIndex].toString().trim() });
            }
        }
        return result;
    }

    async enterCounterPriceAndQty(rowIndex: number, price: string, qty: string): Promise<string> {
        const priceCell = this.page.locator(this.counterPriceFieldRows).nth(rowIndex);
        await priceCell.click();
        const priceInput = priceCell.locator('input');
        await priceInput.waitFor({ state: 'visible', timeout: 2000 });
        await priceInput.fill(price);

        const qtyCell = this.page.locator(this.counterQtyFieldRows).nth(rowIndex);
        await qtyCell.click();
        const qtyInput = qtyCell.locator('input');
        await qtyInput.waitFor({ state: 'visible', timeout: 2000 });
        await qtyInput.fill(qty);

        const totalCell = this.page.locator(this.counterTotalFieldRows).nth(rowIndex);
        await totalCell.click();
        await this.page.waitForTimeout(2000);
        const totalValueRaw = (await totalCell.textContent())?.trim() ?? "";
        const totalValue = totalValueRaw.replace(/\$|,/g, "");
        return totalValue;
    }

    async clickCompleteReviewButton() {
        Logger("Clicking Complete Review button");
        await this.page.waitForTimeout(2000);
        await this.page.locator(this.completeReviewButton).click();
        await this.page.waitForTimeout(3000);
        Logger("Complete Review button clicked");
    }

    async clickBackToAllOffersLink() {
        Logger("Clicking Back to All Offers link");
        await this.page.locator(this.backToAllOfferLink).click();
        Logger("Back to All Offers link clicked");
    }

    async clickCloseSubmittedConfirmationModal() {
        Logger("Clicking close button on submitted confirmation modal");
        await this.page.locator(this.closeSubmittedConfirmationModal).click();
        Logger("Close button on submitted confirmation modal clicked");
    }

    async clickCloseErrorModalPopup() {
        Logger("Clicking close button on submitted confirmation modal");
        await this.page.locator(this.errorModal_closeButton).click();
        Logger("Close button on submitted confirmation modal clicked");
    }

    async isCounterPriceFieldDisabled(rowIndex: number): Promise<boolean> {
        const priceCell = this.page.locator(this.counterPriceFieldRows).nth(rowIndex);
        await priceCell.click().catch(() => { });
        const priceInput = priceCell.locator('input');
        const isVisible = await priceInput.isVisible().catch(() => false);
        if (!isVisible) {
            return true;
        }
        return await priceInput.isDisabled();
    }

    async isCounterQtyFieldDisabled(rowIndex: number): Promise<boolean> {
        const qtyCell = this.page.locator(this.counterQtyFieldRows).nth(rowIndex);
        await qtyCell.click().catch(() => { });
        const qtyInput = qtyCell.locator('input');
        const isVisible = await qtyInput.isVisible().catch(() => false);
        if (!isVisible) {
            return true;
        }
        return await qtyInput.isDisabled();
    }

    async moreActionOption(moreActionOption: "Accept All" | "Finalize All" | "Decline All" | "Download") {
        await this.page.waitForTimeout(1000);
        await this.page.locator(this.moreAction_dotMenu).click();
        const options = this.page.locator(this.moreAction_options);
        const optionsCount = await options.count();
        const downloadFileName = "MoreActionDownload.xlsx";
        for (let i = 0; i < optionsCount; i++) {
            const eachOption = await options.nth(i).textContent();
            if (eachOption?.trim().toLowerCase() === moreActionOption.trim().toLowerCase()) {
                if (moreActionOption === "Download") {
                    await clickLocatorAnddownloadFile(this.page, options.nth(i), downloadFileName);
                } else {
                    await options.nth(i).click();
                }
                break;
            }
        }
        await this.page.locator(this.dashborad_displayStatus).click(); // Close the dropdown        
    }

    async isMoreActionOptionVisible(moreActionOption: string): Promise<boolean> {
        const options = this.page.locator(this.moreAction_options);
        const optionsCount = await options.count();
        for (let i = 0; i < optionsCount; i++) {
            const eachOption = await options.nth(i).textContent();
            if (eachOption?.trim().toLowerCase() === moreActionOption.trim().toLowerCase()) {
                return true;
            }
        }
        return false;
    }
}

```
