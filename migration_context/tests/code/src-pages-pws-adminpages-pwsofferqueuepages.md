# Page Object: PWS_OfferQueuePages.ts

- **Path**: `src\pages\PWS\AdminPages\PWS_OfferQueuePages.ts`
- **Category**: Page Object
- **Lines**: 125
- **Size**: 5,232 bytes
- **Members**: `class PWS_OfferQueuePages`, `ensureUserOnOfferQueuePage`, `chooseOfferStatusTab`, `findAndClickOfferByID`, `clickFirstRowOfferID`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';
import { BaseTest } from '../../../tests/BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { navTabs } from '../../../utils/resources/enum';
import { NavMenuPage } from '../../CommonPages/NavMenuPage';


export class PWS_OfferQueuePages {

    private readonly offersHeader = "//span[text()='Offers']";
    private readonly offerIDRows = "//div[contains(@class,'pws-small-text')] //div[@class='td-custom-content']/a";
    private readonly offerMenuTabIcon = "(//span[@class='mx-icon-lined mx-icon-hammer'])[1]";
    private readonly offerIDRows_onAllTabs = "//div[contains(@class,'pws-small-text')] //div[@class='td-custom-content']";
    private readonly orderIDRows_underOrderedTab = "//div[@role='row']//div[@role='gridcell'][2]//span";

    constructor(private page: Page) { }


    async ensureUserOnOfferQueuePage(base: BaseTest, userRole: userRole) {
        try {
            const isOfferHeaderVisible = await this.page.locator(this.offersHeader).isVisible().catch(() => false);
            if (isOfferHeaderVisible) {
                Logger("Already on OfferQueue page, no login needed.");
                return;
            }
        } catch (error) {
            Logger("Offer Queue header not displayed, will login and navigate.");
        }
        await base['loginPage'].loginAs(userRole);
        await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
    }


    async chooseOfferStatusTab(tabOption: "Sales Review" | "Buyer Acceptance" | "Ordered" | "Declined" | "Total") {
        const tab = `(//span[contains(text(),'${tabOption}')])[1]`;
        await this.page.locator(tab).click();
    }

    async findAndClickOfferByID(offerId: string, maxRowsToCheck: number) {
        Logger(`Opening details for offer with OfferID: ${offerId}`);
        let found = false;
        let attempts = 0;
        while (!found && attempts < 3) {
            await this.page.waitForTimeout(5000);
            const rows = await this.page.locator(this.offerIDRows).all();
            for (const row of rows.slice(0, maxRowsToCheck)) {
                let cellText = (await row.textContent())?.trim() ?? "";
                let offerIdTrimmed = offerId.replace(/^:\s*/, '').trim();
                console.log(`Comparing cell text: "${cellText}" with offerId: "${offerIdTrimmed}"`);
                if (cellText === offerIdTrimmed) {
                    await row.click();
                    found = true;
                    break;
                }
            }
            if (found) break;
            attempts++;
        }
        if (!found) {
            throw new Error(`OfferID ${offerId} not found in the first ${maxRowsToCheck} rows after 3 attempts.`);
        }
        await this.page.waitForTimeout(5000);
    }

    async isOfferIdExistUnderAnyTab(buyerCode: string, maxRowsToCheck: number): Promise<boolean> {
        Logger(`Checking if OfferID exists in the first ${maxRowsToCheck} rows: ${buyerCode}`);
        let found = false;
        let attempts = 0;
        while (!found && attempts < 3) {
            await this.page.waitForTimeout(5000);
            const rows = await this.page.locator(this.offerIDRows_onAllTabs).all();
            for (const row of rows.slice(0, maxRowsToCheck)) {
                let cellText = (await row.textContent())?.trim() ?? "";
                let buyerCodeTrimmed = buyerCode.trim();
                console.log(`Comparing if cell text: "${cellText}" contains buyerCode: "${buyerCodeTrimmed}"`);
                if (cellText.includes(buyerCodeTrimmed)) {
                    found = true;
                    break;
                }
            }
            if (found) break;
            attempts++;
        }
        return found;
    }

    async clickFirstRowOfferID() {
        const rows = await this.page.locator(this.offerIDRows).all();
        if (rows.length === 0) {
            throw new Error("No offer rows found.");
        }
        await rows[0].click();
        Logger("Clicked the first offer row.");
        await this.page.waitForTimeout(2000);
    }


    //----------------  ORDERED TAB ------------------------------

    async isOrderIdExistUnderOrderedTab(buyerCode: string, maxRowsToCheck: number): Promise<boolean> {
        Logger(`Checking if OrderID exists in the first ${maxRowsToCheck} rows: ${buyerCode}`);
        let found = false;
        let attempts = 0;
        while (!found && attempts < 3) {
            await this.page.waitForTimeout(5000);
            const rows = await this.page.locator(this.offerIDRows_onAllTabs).all();
            for (const row of rows.slice(0, maxRowsToCheck)) {
                let cellText = (await row.textContent())?.trim() ?? "";
                let buyerCodeTrimmed = buyerCode.trim();
                console.log(`Comparing if cell text: "${cellText}" contains buyerCode: "${buyerCodeTrimmed}"`);
                if (cellText.includes(buyerCodeTrimmed)) {
                    found = true;
                    break;
                }
            }
            if (found) break;
            attempts++;
        }
        return found;
    }


}

```
