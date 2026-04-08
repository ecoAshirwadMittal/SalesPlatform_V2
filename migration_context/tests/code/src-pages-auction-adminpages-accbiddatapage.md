# Page Object: ACC_BidDataPage.ts

- **Path**: `src\pages\Auction\AdminPages\ACC_BidDataPage.ts`
- **Category**: Page Object
- **Lines**: 91
- **Size**: 4,739 bytes
- **Members**: `class ACC_BidDataPage`

## Source Code

```typescript
import { expect, Locator, Page } from '@playwright/test'
import { Logger } from '../../../utils/helpers/data_utils';


export class ACC_BidDataPage {
    private readonly weekDropdownArrow = "(//div[@class='widget-combobox-down-arrow'])[1]";
    private readonly weekDropdownMenuList = ".widget-combobox-menu-list li";
    private readonly roundInputField = "(//input[@class='widget-combobox-input'])[2]";
    private readonly buyerCodeInputField = "(//input[@class='widget-combobox-input'])[3]";

    private readonly bidAmountFilterInput = "(//input[@class='form-control filter-input'])[6]";
    private readonly deleteSelectedBidsButton = "//button[text()='Delete Selected']";
    private readonly proceedButtonOnModal = "//button[text()='Proceed']";
    private readonly paginationText = "//div[@class='paging-status']";


    constructor(private page: Page) { }

    async selectBidDataByRoundAndBuyerCode(roundText: "Round 1" | "Round 2" | "Upsell Round", buyerCode: string): Promise<void> {
        // Click the dropdown arrow to open the Auction week dropdown
        await this.page.waitForLoadState();
        const dropdownArrow = this.page.locator(this.weekDropdownArrow);
        await dropdownArrow.waitFor({ state: 'visible', timeout: 5000 });
        await dropdownArrow.click();
        // Click the top option in the dropdown menu
        const dropdownMenu = this.page.locator(this.weekDropdownMenuList);
        await dropdownMenu.first().waitFor({ state: 'visible', timeout: 5000 });
        await dropdownMenu.first().click();
        // Input text in the Round field
        await this.page.waitForTimeout(1000);
        const roundInput = this.page.locator(this.roundInputField);
        await roundInput.waitFor({ state: 'visible', timeout: 5000 });
        await roundInput.click();
        await roundInput.type(roundText, { delay: 50 });
        await roundInput.press('Enter');
        // Input buyer code in the Buyer Code field
        await this.page.waitForTimeout(1000);
        const buyerCodeInput = this.page.locator(this.buyerCodeInputField);
        await buyerCodeInput.waitFor({ state: 'visible', timeout: 5000 });
        await buyerCodeInput.click();
        await buyerCodeInput.type(buyerCode, { delay: 50 });
        await buyerCodeInput.press('Enter');
        Logger(`Selected top auction week, entered "${roundText}" in Round field, and entered "${buyerCode}" in Buyer Code field.`);
    }

    async removeBidByBidPrice(bidPrice: string): Promise<void> {
        const bidAmountInput = this.page.locator(this.bidAmountFilterInput);
        await bidAmountInput.waitFor({ state: 'visible', timeout: 5000 });
        await bidAmountInput.click();
        await bidAmountInput.fill('');
        await bidAmountInput.type(bidPrice, { delay: 50 });
        await bidAmountInput.press('Enter');
        Logger(`Entered bid price "${bidPrice}" in Bid Amount filter field.`);
        // Wait for filtered rows to appear
        await this.page.waitForTimeout(2000);
        // Click all checkboxes in the filtered rows
        const checkboxes = this.page.locator("//input[@type='checkbox' and contains(@aria-label,'Select row')]");
        const count = await checkboxes.count();
        for (let i = 0; i < count; i++) {
            await checkboxes.nth(i).click();
        }
        Logger(`Clicked ${count} checkboxes for filtered bid rows.`);
        // Click Delete Selected button
        const deleteButton = this.page.locator(this.deleteSelectedBidsButton);
        await deleteButton.waitFor({ state: 'visible', timeout: 5000 });
        await deleteButton.click();
        Logger("Clicked Delete Selected button to remove filtered bids.");
        const proceedButton = this.page.locator(this.proceedButtonOnModal);
        await proceedButton.waitFor({ state: 'visible', timeout: 10000 });
        await proceedButton.click();
        Logger("Clicked Proceed button on confirmation modal.");
    }

    async isSelectedBidRemoved(): Promise<boolean> {
        for (let i = 0; i < 3; i++) {
            const paginationLocator = this.page.locator(this.paginationText);
            await paginationLocator.waitFor({ state: 'visible', timeout: 5000 });
            const text = await paginationLocator.textContent();
            Logger(`Pagination text displayed: "${text?.trim()}"`);
            if (text?.trim() === "0 to 0 of 0") {
                Logger("Pagination shows 0 to 0 of 0.");
                return true;
            }
            Logger(`Pagination not zero yet (attempt ${i + 1}), waiting 3 seconds...`);
            await this.page.waitForTimeout(3000);
        }
        Logger('Pagination did not show "0 to 0 of 0" after 3 attempts.');
        return false;
    }
}

```
