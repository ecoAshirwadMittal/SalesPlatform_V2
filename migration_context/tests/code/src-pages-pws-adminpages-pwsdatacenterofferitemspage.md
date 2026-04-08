# Page Object: PWS_DataCenter_OfferItemsPage.ts

- **Path**: `src\pages\PWS\AdminPages\PWS_DataCenter_OfferItemsPage.ts`
- **Category**: Page Object
- **Lines**: 28
- **Size**: 1,591 bytes
- **Members**: `class PWS_DataCenter_OfferItemsPage`, `clearOffersFromOfferQueue`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';

export class PWS_DataCenter_OfferItemsPage {


//Under Admin > PWS Data Center
    private readonly pwsOfferItem_button = "//span[text()='PWS Offers and offer items']";
    private readonly pwsOfferItemPage_BuyerCodeFilter = "(//div[@class='filter'])[8]//input";
    private readonly pwsOfferItemPage_trashIconFirstLine = "(//span[@class='mx-icon-lined mx-icon-trash-can'])[1]";
    private readonly pwsOfferItemPage_confirmDeleteButton = "//button[text()='Proceed']";

    constructor(private page: Page) {} 

    async clearOffersFromOfferQueue(buyerCode: string) {
        await this.page.locator(this.pwsOfferItem_button).click();
        await this.page.waitForSelector(this.pwsOfferItemPage_BuyerCodeFilter, { state: 'visible', timeout: 5000 });       
        await this.page.locator(this.pwsOfferItemPage_BuyerCodeFilter).fill('');
        await this.page.locator(this.pwsOfferItemPage_BuyerCodeFilter).type(buyerCode);
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(1500);
        while (await this.page.locator(this.pwsOfferItemPage_trashIconFirstLine).isVisible({ timeout: 2000 }).catch(() => false)) {
            await this.page.locator(this.pwsOfferItemPage_trashIconFirstLine).click();
            await this.page.locator(this.pwsOfferItemPage_confirmDeleteButton).waitFor({ state: 'visible', timeout: 5000 });
            await this.page.locator(this.pwsOfferItemPage_confirmDeleteButton).click();           
            await this.page.waitForTimeout(1000);
        }        
    }
}
```
