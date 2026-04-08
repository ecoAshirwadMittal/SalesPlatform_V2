# Page Object: ACC_QualifiedBuyerCodePage.ts

- **Path**: `src\pages\Auction\AdminPages\ACC_QualifiedBuyerCodePage.ts`
- **Category**: Page Object
- **Lines**: 105
- **Size**: 5,820 bytes
- **Members**: `class ACC_QualifiedBuyerCodePage`

## Source Code

```typescript
import { Locator, Page } from '@playwright/test'
import { NavMenuPage } from '../../CommonPages/NavMenuPage';
import { navTabs } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';
import { waitAndClick } from '../../../utils/helpers/commonMethods';


export class ACC_QualifiedBuyerCodePage {
    private readonly weekDropdownArrow = "(//div[@class='widget-combobox-down-arrow'])[1]";
    private readonly weekDropdownMenuList = "//ul[contains(@class,'widget-combobox-menu-list')]//li";
    private readonly roundInputField = "(//input[@class='widget-combobox-input'])[2]";
    private readonly codeFilterInputField = "(//input[@aria-label='Search'])[2]";
    private readonly filterResultFirstRow_checkbox = "//input[@type='checkbox']";
    private readonly filterResultFirstRow_qualificationType = "(//div[@class='tr'])[2]/div[7]/span";
    private readonly savingBuyerCodeModal = "//div[@aria-live='assertive']";

    
    constructor(private page: Page) { }

    async selectQualifyBuyerListbyWeekAndRound(roundText: "Round 1" | "Round 2" | "Upsell Round"): Promise<void> {
        // Click the week dropdown arrow to open the dropdown
        const dropdownArrow = this.page.locator(this.weekDropdownArrow);
        await dropdownArrow.waitFor({ state: 'visible', timeout: 5000 });
        await dropdownArrow.click();
        // Wait for the dropdown menu options to be visible
        const dropdownOptions = this.page.locator(this.weekDropdownMenuList);
        await dropdownOptions.first().waitFor({ state: 'visible', timeout: 5000 });
        // Click the first option (top week)
        await dropdownOptions.first().click();
        // Enter the round text into the round input field
        const roundInput = this.page.locator(this.roundInputField);
        await roundInput.waitFor({ state: 'visible', timeout: 5000 });
        await roundInput.click();
        await roundInput.fill('');
        await roundInput.type(roundText, { delay: 50 });
        await roundInput.press('Enter');
        Logger(`Selected the first auction week and entered "${roundText}" in the round input field.`);
    }

    async isBuyerCodeQualified(buyerCode: string): Promise<boolean> {
        // Input buyer code in the Code column filter
        const codeFilterInput = this.page.locator(this.codeFilterInputField);
        await codeFilterInput.waitFor({ state: 'visible', timeout: 5000 });
        await codeFilterInput.fill('');
        await codeFilterInput.type(buyerCode, { delay: 50 });
        await this.page.waitForTimeout(1000);
        // Validate the first column checkbox is checked
        const includedCheckbox = this.page.locator(this.filterResultFirstRow_checkbox);
        await includedCheckbox.waitFor({ state: 'visible', timeout: 5000 });
        const isChecked = await includedCheckbox.isChecked();
        // Validate Qualification Type column displays "Qualified"
        const qualificationTypeCell = this.page.locator(this.filterResultFirstRow_qualificationType);
        const qualificationText = (await qualificationTypeCell.textContent())?.trim();
        Logger(`Buyer code "${buyerCode}" checkbox checked: ${isChecked}, qualification type: "${qualificationText}"`);
        if (isChecked && qualificationText === "Qualified") {
            return true;
        } else {
            return false;
        }
    }

    async getBuyerCodeQualificationType(buyerCode: string): Promise<string> {
        // Input buyer code in the Code column filter
        const codeFilterInput = this.page.locator(this.codeFilterInputField);
        await codeFilterInput.waitFor({ state: 'visible', timeout: 5000 });
        await codeFilterInput.fill('');
        await codeFilterInput.type(buyerCode, { delay: 80 });
        await this.page.waitForTimeout(1000);
        // Get the Qualification Type text for the filtered row
        const qualificationTypeCell = this.page.locator(this.filterResultFirstRow_qualificationType);
        await qualificationTypeCell.waitFor({ state: 'visible', timeout: 5000 });
        const qualificationText = (await qualificationTypeCell.textContent())?.trim() ?? '';
        Logger(`Buyer code "${buyerCode}" qualification type: "${qualificationText}"`);
        return qualificationText;
    }

    async setBuyerCheckbox(buyerCode: string, shouldBeChecked: "Check" | "Uncheck"): Promise<void> {
        // Input buyer code in the Code column filter
        const codeFilterInput = this.page.locator(this.codeFilterInputField);
        await codeFilterInput.waitFor({ state: 'visible', timeout: 5000 });
        await codeFilterInput.fill('');
        await codeFilterInput.type(buyerCode, { delay: 80 });
        await this.page.waitForTimeout(1000);
        // Locate the checkbox for the filtered row
        const includedCheckbox = this.page.locator(this.filterResultFirstRow_checkbox);
        await includedCheckbox.waitFor({ state: 'visible', timeout: 5000 });
        const isChecked = await includedCheckbox.isChecked();
        // Click to change state if needed
        if (isChecked !== (shouldBeChecked === "Check")) {
            await includedCheckbox.click();
            Logger(`Checkbox for buyer "${buyerCode}" changed to ${shouldBeChecked === "Check" ? "checked" : "unchecked"}.`);
        } else {
            Logger(`Checkbox for buyer "${buyerCode}" already ${shouldBeChecked === "Check" ? "checked" : "unchecked"}.`);
        }
        // Loop to wait for "Saving Buyer Code" modal to disappear (max 15 seconds)
        for (let i = 0; i < 15; i++) {
            const isModalVisible = await this.page.locator(this.savingBuyerCodeModal).isVisible();
            if (!isModalVisible) {
                break;
            }
            await this.page.waitForTimeout(1000);
        }
    }
}  

```
