# Page Object: PWS_CartPage.ts

- **Path**: `src\pages\PWS\BuyerPages\PWS_CartPage.ts`
- **Category**: Page Object
- **Lines**: 236
- **Size**: 11,110 bytes
- **Members**: `class PWS_CartPage`, `ensureUserOnCartPage`, `clickSubmitButton`, `clickAlmostDoneSubmitButton`, `clickAlmostDoneGoBackLink`, `moreActionOption`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { PWS_ShopPage } from './PWS_ShopPage';
import { BaseTest } from '../../../tests/BaseTest';
import { Logger } from '../../../utils/helpers/data_utils';
import { userRole } from '../../../utils/resources/enum';
import { waitAndClick } from '../../../utils/helpers/commonMethods';
import { PWS_NavMenu_AsBuyer } from './PWS_NavMenu_AsBuyer';

export class PWS_CartPage {
    private readonly cartPage_header = "//span[text()='Cart']";
    private readonly sum_SKUs = "(//div[contains(@class,'pws-inventory-summary')])[1]//h5";
    private readonly sum_Qty = "(//div[contains(@class,'pws-inventory-summary')])[2]//h5";
    private readonly sum_Total = "(//div[contains(@class,'pws-inventory-summary')])[3]//h5";
    private readonly viewAsPlaceholder = "//span[@class='widget-combobox-caption-text']";

    private readonly moreAction_dotMenu = "//a[@role='button']/img";
    private readonly moreAction_options = "//div[contains(@class,'settings_dropdown')]//a"

    private readonly qtyExceedMessage = "//span[@class='mx-text mx-name-text1 pws-warning-color spacing-outer-left']";
    private readonly submitButton = "//span[text()='Submit']";
    private readonly disableSubmitButton = "//div[contains(@class,'pws-inventory-cart-disabled')]";
    private readonly almostDoneModal_submitButton = "//button[text()='Submit Offer for Review']";
    private readonly almostDoneModal_goBackLink = "//a[text()='Go back and edit my offer']";
    private readonly offerSubmittedModal_closeButton = "//button[contains(@class,'modal-no-title-close-btn')]";
    private readonly offerID = "//span[@class='mx-text mx-name-text4 text-secondary']"

    constructor(private page: Page) { }

    async ensureUserOnCartPage(base: BaseTest, userRole: userRole) {
        try {
            const isCartHeaderVisible = await this.page.locator(this.cartPage_header).isVisible().catch(() => false);
            if (isCartHeaderVisible) {
                Logger("Already on Cart page, no login needed.");
                return;
            }
        } catch (error) {
            Logger("Cart not displayed, will login and navigate.");
        }
        await base['loginPage'].loginAs(userRole);
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Shop");
        await base['pws_shopPage'].clickCartButton();
    }

    async isCartPageDisplayed(): Promise<boolean> {
        await this.page.waitForTimeout(2000);
        return await this.page.locator(this.cartPage_header).isVisible();
    }

    async clickSubmitButton() {
        await this.page.waitForTimeout(1000);
        await waitAndClick(this.page, this.submitButton);
    }

    async isSubmitButtonEnabled(): Promise<boolean> {
        const disabledButton = this.page.locator(this.disableSubmitButton);
        await disabledButton.waitFor({ state: 'visible', timeout: 5000 }).catch(() => { });
        const isDisabledVisible = await disabledButton.isVisible().catch(() => false);
        return !isDisabledVisible;
    }

    async clickAlmostDoneSubmitButton() {
        // Wait longer for the "Almost Done" modal to appear — it can be slow
        await this.page.locator(this.almostDoneModal_submitButton).waitFor({ state: 'visible', timeout: 30000 });
        await waitAndClick(this.page, this.almostDoneModal_submitButton);
        await this.page.waitForTimeout(2000);
    }

    async clickAlmostDoneGoBackLink() {
        await waitAndClick(this.page, this.almostDoneModal_goBackLink);
        await this.page.waitForTimeout(1000);
    }

    async isAlmostDoneModalPopup(): Promise<boolean> {
        await this.page.waitForTimeout(1000);
        try {
            return await this.page.locator(this.almostDoneModal_submitButton).isVisible({ timeout: 2000 });
        } catch {
            return false;
        }
    }

    async isSubmittedConfirmationModalDisplayed(): Promise<boolean> {
        try {
            await this.page.locator(this.offerSubmittedModal_closeButton).waitFor({ state: 'visible', timeout: 30000 });
            return true;
        } catch {
            return false;
        }
    }

    async isQtyExceedMessageVisible(): Promise<boolean> {
        try {
            await this.page.locator(this.qtyExceedMessage).waitFor({ state: 'visible', timeout: 5000 });
            return true;
        } catch {
            return false;
        }
    }

    async getSummaryOffer(): Promise<string[]> {
        console.log("Getting Summary Offer Details from Cart Page");
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

    async getBuyerFromViewAs(): Promise<string> {
        // Adjust the selector to match the element that displays the Buyer Code on the cart page
        const buyerViewAsLocator = this.page.locator(this.viewAsPlaceholder);
        await buyerViewAsLocator.waitFor({ state: 'visible', timeout: 5000 });
        const isVisible = await buyerViewAsLocator.isVisible().catch(() => false);
        if (!isVisible) return '';
        return (await buyerViewAsLocator.textContent())?.trim() || '';
    }

    //More Action Options:  Passing option name as parameter
    async moreActionOption(moreActionOption: "Finalize Order" | "Download Offer" | "Reset Offer") {
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

    async getOfferID_afterSubmit(): Promise<string> {
        // Wait for the confirmation modal with offer ID to appear (can take 15-30s)
        await this.page.locator(this.offerID).waitFor({ state: 'visible', timeout: 30000 });
        const submitted_fullText = await this.page.locator(this.offerID).textContent() ?? "";
        const offerID = submitted_fullText.substring(submitted_fullText.indexOf(':')).trim();

        // Try to close the confirmation modal — it may auto-dismiss on its own
        try {
            await waitAndClick(this.page, this.offerSubmittedModal_closeButton, 5000);
        } catch {
            Logger("Confirmation modal auto-dismissed before close button could be clicked — proceeding");
        }
        await this.page.waitForTimeout(3000);
        return offerID;
    }

    async submitOfferBelowListPrice(nubmerOfSKUs: number, offerPrice: number, offerQty: number): Promise<string> {
        const inventoryPage = new PWS_ShopPage(this.page);
        const pws_navMenuAsBuyer = new PWS_NavMenu_AsBuyer(this.page);
        await pws_navMenuAsBuyer.chooseNavMenu('Shop');
        await inventoryPage.selectMoreActionOption('Reset Offer');
        await inventoryPage.sortAvlQty('descending');
        await inventoryPage.enterOfferData(nubmerOfSKUs, offerPrice, offerQty);
        await inventoryPage.clickCartButton();
        await this.clickSubmitButton();
        await this.clickAlmostDoneSubmitButton();
        const offerID = await this.getOfferID_afterSubmit();
        const offerIDTrimmed = offerID.replace(/^:\s*/, '').trim();
        return offerIDTrimmed;
    }

    async submitOrderAboveListPrice(nubmerOfSKUs: number, offerPrice: number, offerQty: number): Promise<string> {
        const inventoryPage = new PWS_ShopPage(this.page);
        const pws_navMenuAsBuyer = new PWS_NavMenu_AsBuyer(this.page);
        await pws_navMenuAsBuyer.chooseNavMenu('Shop');
        await inventoryPage.selectMoreActionOption('Reset Offer');
        await inventoryPage.sortAvlQty('descending');
        await inventoryPage.enterOfferData(nubmerOfSKUs, offerPrice, offerQty);
        await inventoryPage.clickCartButton();
        await this.clickSubmitButton();
        const offerID = await this.getOfferID_afterSubmit();
        return offerID;
    }

    async submitOrderbySKU(sku: string, offerPrice: number, offerQty: number): Promise<string> {
        const inventoryPage = new PWS_ShopPage(this.page);
        const pws_navMenuAsBuyer = new PWS_NavMenu_AsBuyer(this.page);
        await pws_navMenuAsBuyer.chooseNavMenu('Shop');
        await inventoryPage.filterBySKU(sku);
        await inventoryPage.enterOfferData(1, offerPrice, offerQty);
        await inventoryPage.clickCartButton();
        await this.clickSubmitButton();
        const offerID = await this.getOfferID_afterSubmit();
        return offerID;
    }

    async isQtyExceedMessageDisplayed(): Promise<boolean> {
        await this.page.waitForTimeout(1000);
        return await this.page.locator(this.qtyExceedMessage).isVisible();
    }

    /**
     * Smart submit that handles both above-list and below-list price flows:
     * - Above/at list price: Submit → Confirmation popup (order #) → Close
     * - Below list price: Submit → "Almost Done" modal → Submit Offer for Review → Confirmation popup → Close
     * 
     * @returns The captured Order ID string
     */
    async submitAndCaptureOrderID(): Promise<string> {
        Logger("Submitting order...");

        // Step 1: Click Submit
        await this.clickSubmitButton();

        // Step 2: Check if "Almost Done" modal appears (below-list offers only)
        const almostDoneVisible = await this.isAlmostDoneModalPopup();
        if (almostDoneVisible) {
            Logger("Almost Done modal detected — offer is below list price, clicking Submit Offer for Review");
            await this.clickAlmostDoneSubmitButton();
        } else {
            Logger("No Almost Done modal — offer is at/above list price, proceeding to confirmation");
        }

        // Step 3: Wait for confirmation popup with Order ID
        const isConfirmed = await this.isSubmittedConfirmationModalDisplayed();
        if (!isConfirmed) {
            throw new Error("Order submission failed: confirmation modal did not appear within 30s");
        }

        // Step 4: Capture Order ID and close the popup
        const offerID = await this.getOfferID_afterSubmit();
        const cleanOfferID = offerID.replace(/^:\s*/, '').trim();
        Logger(`Order submitted successfully. Order ID: ${cleanOfferID}`);
        return cleanOfferID;
    }
}


```
