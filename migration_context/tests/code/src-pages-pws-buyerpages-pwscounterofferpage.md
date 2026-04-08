# Page Object: PWS_CounterOfferPage.ts

- **Path**: `src\pages\PWS\BuyerPages\PWS_CounterOfferPage.ts`
- **Category**: Page Object
- **Lines**: 276
- **Size**: 12,745 bytes
- **Members**: `class PWS_CounterOfferPage`, `clickFirstRowOfferID`, `findAndClickOfferByID`, `selectCounterActionByRowIndex`, `clickSubmitResponseButton`, `closeErrorModal`, `closeOfferResponseSubmittedModal`, `moreActionOption`, `cancelOrderModalAction`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';

export class PWS_CounterOfferPage {
    private readonly offerIDRows = "//div[@class='td-custom-content']//a";

    private readonly summaryBox_originalOffer = "(//div[@class='row'])[7]//span[contains(@class,'text-semibold')]";
    private readonly summaryBox_counterOffer = "(//div[@class='row'])[8]//span[contains(@class,'text-semibold')]";
    private readonly summaryBox_finalOffer = "(//div[@class='row'])[9]//span[contains(@class,'text-semibold')]";

    private readonly itemRows = "//div[@class='widget-datagrid-grid-body table-content']/div";
    private readonly offerID = "//div[@class='mx-name-container8']/span";

    private readonly moreAction_dotMenu = "//a[@role='button']/img";
    private readonly moreAction_options = "//div[contains(@class,'settings_dropdown')]//a"
    private readonly cancelOrderModal = "mxui_widget_ConfirmationDialog_0";
    private readonly yesButton_cancelOrderModal = "//button[text()='YES']";
    private readonly noButton_cancelOrderModal = "//button[text()='NO']";

    private readonly submitResponseButton = "//button[text()='Submit Response']";
    private readonly offerResponSubmittedModal = "//div[@class='col-lg-12 col-md col spacing-inner-left-none spacing-inner-right-none']";
    private readonly closeSubmittedResponseModalButton = "//button[contains(@class,'modal-no-title-close')]";

    constructor(private page: Page) { }

    async clickFirstRowOfferID() {
        await this.page.waitForSelector(this.offerIDRows, { timeout: 5000 });
        const firstRow = this.page.locator(this.offerIDRows).first();
        const offerIdText = await firstRow.textContent();
        await firstRow.click();
        await this.page.waitForTimeout(2000);
    }

    async findAndClickOfferByID(offerId: string) {
        Logger(`Opening details for offer with OfferID: ${offerId}`);
        try {
            // Check if offerIDRows are present
            const offerRowsCount = await this.page.locator(this.offerIDRows).count();

            if (offerRowsCount === 0) {
                Logger("No offer list displayed, likely only one counteroffer present. Skipping find and click.");
                return;
            }
            let found = false;
            let attempts = 0;
            while (!found && attempts < 3) {
                await this.page.waitForTimeout(5000);
                const rows = await this.page.locator(this.offerIDRows).all();
                console.log(`Attempt ${attempts + 1}: Number of rows found: ${rows.length}`);
                // Only check the first 7 rows
                for (const row of rows.slice(0, 7)) {
                    let cellText = (await row.textContent())?.trim() ?? "";
                    console.log(`Comparing cellText: ${cellText} with offerId: ${offerId}`);
                    if (cellText === offerId) {
                        await row.click();
                        found = true;
                        break;
                    }
                }
                if (found) break;
                attempts++;
            }
            if (!found) {
                throw new Error(`OfferID ${offerId} not found in the first 7 rows after 3 attempts.`);
            }
            await this.page.waitForTimeout(5000);
        } catch (error) {
            Logger("Skipping find & click OfferID");
        }
    }

    //=========== Counteroffer Details Page =============

    async getCounterStatusByRowIndex(rowIndex: number): Promise<string> {
        const row = this.page.locator(this.itemRows).nth(rowIndex);
        const statusCell = row.locator('div').nth(11);
        const statusText = await statusCell.textContent();
        return statusText?.trim() ?? "";
    }

    async isActionDropdownVisibleByRowIndex(rowIndex: number, status: string): Promise<boolean> {
        const row = this.page.locator(this.itemRows).nth(rowIndex);
        const actionDropdown = row.locator('div').nth(15).locator('select');
        console.log("==== actionDropdown: ", actionDropdown);
        return await actionDropdown.isVisible();
    }

    async selectCounterActionByRowIndex(rowIndex: number, buyerAction: "Accept" | "Decline") {
        const row = this.page.locator(this.itemRows).nth(rowIndex);
        const actionDropdown = row.locator('div').nth(15).locator('select');
        await actionDropdown.selectOption({ label: buyerAction });
        await this.page.waitForTimeout(3000);
    }

    async clickSubmitResponseButton() {
        await this.page.locator(this.submitResponseButton).click();
        await this.page.waitForTimeout(3000);
    }

    async isErrorMessageModalVisible(): Promise<boolean> {
        const errorModal = this.page.locator(this.closeSubmittedResponseModalButton);
        const isVisible = await errorModal.isVisible().catch(() => false);
        if (isVisible) {
            return true;
        }
        return false;
    }

    async closeErrorModal() {
        await this.page.locator(this.closeSubmittedResponseModalButton).click();
        await this.page.waitForTimeout(2000);
    }

    async isOfferResponseSubmittedModalVisible(): Promise<boolean> {
        const submittedModal = this.page.locator(this.offerResponSubmittedModal);
        return await submittedModal.isVisible().catch(() => false);
    }

    async closeOfferResponseSubmittedModal() {
        await this.page.locator(this.closeSubmittedResponseModalButton).click();
        await this.page.waitForTimeout(2000);
    }

    async moreActionOption(moreActionOption: "Accept All Counters" | "Cancel Order") {
        await this.page.waitForTimeout(1000);
        await this.page.locator(this.moreAction_dotMenu).click();
        const options = this.page.locator(this.moreAction_options);
        const optionsCount = await options.count();
        for (let i = 0; i < optionsCount; i++) {
            const eachOption = await options.nth(i).textContent();
            if (eachOption?.trim().toLowerCase() === moreActionOption.trim().toLowerCase()) {
                await options.nth(i).click();
                break;
            }
        }
    }

    async cancelOrderModalAction(string: "yes" | "no") {
        if (string.toLowerCase() === "yes") {
            await this.page.locator(this.yesButton_cancelOrderModal).click();
        } else if (string.toLowerCase() === "no") {
            await this.page.locator(this.noButton_cancelOrderModal).click();
        } else {
            throw new Error(`Invalid action: ${string}. Expected 'yes' or 'no'.`);
        }
        await this.page.waitForTimeout(2000);
    }

    async isCancelOrderModalVisible(): Promise<boolean> {
        const submittedModal = this.page.locator(this.cancelOrderModal);
        return await submittedModal.isVisible().catch(() => false);
    }

    async getOfferSummaryBox(type: "original" | "counter" | "final"): Promise<[number, number, number]> {
        let summarySelector: string;
        switch (type) {
            case "original":
                summarySelector = this.summaryBox_originalOffer;
                break;
            case "counter":
                summarySelector = this.summaryBox_counterOffer;
                break;
            case "final":
                summarySelector = this.summaryBox_finalOffer;
                break;
            default:
                throw new Error(`Invalid summary type: ${type}`);
        }
        const summarySpans = this.page.locator(summarySelector);
        const count = await summarySpans.count();
        if (count < 3) {
            throw new Error(`Expected at least 3 summary spans, but found ${count}`);
        }
        const skusText = (await summarySpans.nth(0).textContent())?.trim() ?? "0";
        const qtyText = (await summarySpans.nth(1).textContent())?.trim() ?? "0";
        const priceText = (await summarySpans.nth(2).textContent())?.trim() ?? "0";
        const skus = parseInt(skusText.replace(/,/g, ""), 10) || 0;
        const qty = parseInt(qtyText.replace(/,/g, ""), 10) || 0;
        const price = parseFloat(priceText.replace(/[^0-9.]/g, "")) || 0;
        return [skus, qty, price];
    }

    async getOriginalOfferSummaryDataGrid(): Promise<[number, number, number]> {
        const rows = await this.page.locator(this.itemRows).all();
        const skuCount = rows.length;
        let totalQty = 0;
        let totalPrice = 0;
        for (const row of rows) {
            const qtyText = (await row.locator('div').nth(8).textContent())?.trim() ?? "0";
            const qty = parseInt(qtyText.replace(/,/g, ""), 10) || 0;
            totalQty += qty;
            const priceText = (await row.locator('div').nth(10).textContent())?.trim() ?? "0";
            const price = parseFloat(priceText.replace(/[^0-9.]/g, "")) || 0;
            totalPrice += price;
        }
        return [skuCount, totalQty, totalPrice];
    }

    async getCounterOfferSummaryDataGrid(): Promise<[number, number, number]> {
        const rows = await this.page.locator(this.itemRows).all();
        let skuCount = 0;
        let totalQty = 0;
        let totalPrice = 0;
        for (const row of rows) {
            const statusText = (await row.locator('div').nth(11).textContent())?.trim() ?? "";
            if (statusText === "Decline") {
                continue; // Skip declined rows
            }
            skuCount += 1;
            let qty = 0;
            let price = 0;
            if (statusText === "Counter") {
                const counterQtyText = (await row.locator('div').nth(13).textContent())?.trim() ?? "0";
                qty = parseInt(counterQtyText.replace(/,/g, ""), 10) || 0;
                const counterTotalText = (await row.locator('div').nth(14).textContent())?.trim() ?? "0";
                price = parseFloat(counterTotalText.replace(/[^0-9.]/g, "")) || 0;
            } else {
                const offerQtyText = (await row.locator('div').nth(8).textContent())?.trim() ?? "0";
                qty = parseInt(offerQtyText.replace(/,/g, ""), 10) || 0;
                const offerTotalText = (await row.locator('div').nth(10).textContent())?.trim() ?? "0";
                price = parseFloat(offerTotalText.replace(/[^0-9.]/g, "")) || 0;
            }
            totalQty += qty;
            totalPrice += price;
        }
        return [skuCount, totalQty, totalPrice];
    }

    async getFinalOfferSummaryDataGrid(): Promise<[number, number, number]> {
        const rows = await this.page.locator(this.itemRows).all();
        let skuCount = 0;
        let totalQty = 0;
        let totalPrice = 0;
        for (let i = 0; i < rows.length; i++) {
            const [sku, qty, price] = await this.getInlineSummaryByRowIndex(i);
            skuCount += sku;
            totalQty += qty;
            totalPrice += price;
        }
        return [skuCount, totalQty, totalPrice];
    }

    private async getInlineSummaryByRowIndex(rowIndex: number): Promise<[number, number, number]> {
        const row = this.page.locator(this.itemRows).nth(rowIndex);
        const saleStatus = (await row.locator('div').nth(11).textContent())?.trim().toLowerCase() ?? "";
        if (saleStatus === "decline") {
            return [0, 0, 0];
        }
        if (saleStatus === "accept") {
            const offerQtyText = (await row.locator('div').nth(8).textContent())?.trim() ?? "0";
            const qty = parseInt(offerQtyText.replace(/,/g, ""), 10) || 0;
            const offerTotalText = (await row.locator('div').nth(10).textContent())?.trim() ?? "0";
            const price = parseFloat(offerTotalText.replace(/[^0-9.]/g, "")) || 0;
            return [1, qty, price];
        }
        if (saleStatus === "counter") {
            const selectLocator = row.locator('div').nth(15).locator('select');
            let buyerActionStatus = "";
            buyerActionStatus = (await selectLocator.inputValue()).trim().toLowerCase();
            console.log("==== buyerActionStatus: ", buyerActionStatus);
            if (buyerActionStatus === "decline") {
                return [0, 0, 0];
            }
            if (buyerActionStatus === "accept") {
                const counterQtyText = (await row.locator('div').nth(13).textContent())?.trim() ?? "0";
                const qty = parseInt(counterQtyText.replace(/,/g, ""), 10) || 0;
                const counterTotalText = (await row.locator('div').nth(14).textContent())?.trim() ?? "0";
                const price = parseFloat(counterTotalText.replace(/[^0-9.]/g, "")) || 0;
                return [1, qty, price];
            }
        }
        // If none of the above, do not count this row
        return [0, 0, 0];
    }
}

```
