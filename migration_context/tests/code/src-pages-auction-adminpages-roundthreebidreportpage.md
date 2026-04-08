# Page Object: RoundThreeBidReportPage.ts

- **Path**: `src\pages\Auction\AdminPages\RoundThreeBidReportPage.ts`
- **Category**: Page Object
- **Lines**: 41
- **Size**: 2,059 bytes
- **Members**: `class AUC_RoundThreeReportPage`

## Source Code

```typescript
import { expect, Locator, Page } from '@playwright/test'
import { Logger } from '../../../utils/helpers/data_utils';

export class AUC_RoundThreeReportPage {
    private readonly weeklyDropdownIcon = "//div[@class='widget-combobox-selected-items']";
    private readonly weeklydropdownOptions = "//ul[contains(@class,'widget-combobox-menu-list') and @role='listbox']";
    private readonly codeFilterInputField = "(//input[@aria-label='Search'])[2]";


    constructor(private page: Page) { }

    async selectSecondWeekFromDropdown(): Promise<void> {
        await this.page.waitForTimeout(2000);
        const dropdown = this.page.locator(this.weeklyDropdownIcon);
        await dropdown.waitFor({ state: 'visible', timeout: 5000 });
        await dropdown.click();
        const dropdownMenu = this.page.locator(this.weeklydropdownOptions);
        await dropdownMenu.waitFor({ state: 'visible', timeout: 5000 });
        const secondWeekOption = dropdownMenu.locator("li").nth(1);
        await secondWeekOption.waitFor({ state: 'visible', timeout: 5000 });
        await secondWeekOption.click();
        Logger("Selected the second week from the dropdown.");
        await this.page.waitForTimeout(2000);
    }

    async isBuyerCodePresentInReport(buyerCode: string): Promise<boolean> {
        // Enter buyer code into the filter input field
        const buyerCodeFilterInput = this.page.locator(this.codeFilterInputField);
        await buyerCodeFilterInput.waitFor({ state: 'visible', timeout: 5000 });
        await buyerCodeFilterInput.fill('');
        await buyerCodeFilterInput.type(buyerCode, { delay: 50 });
        await this.page.waitForTimeout(1000);

        // Check if the buyer code appears in the first row of the report table
        const buyerCodeCell = this.page.locator("//div[@role='row']//span[contains(@class,'td-text') and text()='" + buyerCode + "']");
        const isPresent = await buyerCodeCell.isVisible();
        Logger(`Buyer code "${buyerCode}" present in report: ${isPresent}`);
        return isPresent;
    }
}

```
