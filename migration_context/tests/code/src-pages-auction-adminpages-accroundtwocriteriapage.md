# Page Object: ACC_RoundTwoCriteriaPage.ts

- **Path**: `src\pages\Auction\AdminPages\ACC_RoundTwoCriteriaPage.ts`
- **Category**: Page Object
- **Lines**: 44
- **Size**: 2,431 bytes
- **Members**: `class ACC_RoundTwoCriteriaPage`

## Source Code

```typescript
import { expect, Locator, Page } from '@playwright/test'
import { NavMenuPage } from '../../CommonPages/NavMenuPage';
import { navTabs } from '../../../utils/resources/enum';
import { Logger } from '../../../utils/helpers/data_utils';


export class ACC_RoundTwoCriteriaPage {

    private readonly buyerQualificationDropdown = "select[id*='dropDown2']";
    private readonly inventoryQualificationDropdown = "select[id*='dropDown1']";
    private readonly saveButton = "button.btn-success:has-text('Save')";


    constructor(private page: Page) { }

    async selectRegularBuyerSettings(
        qualification: "All Buyers" | "Any Buyer with Round-1 Bids" | "Buyer with Target Qualification",       
        inventoryOption: "Full Inventory" | "Inventory with Round-1 Bids" | "Inventory Based on Target Qualification", 
        specialTreatment: 'Yes' | 'No'): Promise<void> {
        // Go to Round 2 Selection Criteria tab
        const navigationBarPages = new NavMenuPage(this.page);
        await navigationBarPages.clickAuctionControlTab("Round 2 Selection Criteria");
        await this.page.waitForTimeout(2000);
        // Select Regular Buyer Qualification
        const qualificationDropdown = this.page.locator(this.buyerQualificationDropdown);
        await qualificationDropdown.waitFor({ state: 'visible', timeout: 5000 });
        await qualificationDropdown.selectOption({ label: qualification });
        // Select Regular Buyer Inventory Options
        const inventoryDropdown = this.page.locator(this.inventoryQualificationDropdown);
        await inventoryDropdown.waitFor({ state: 'visible', timeout: 5000 });
        await inventoryDropdown.selectOption({ label: inventoryOption });
        // Select Special Treatment Buyer (radio button)
        const specialTreatmentValue = specialTreatment === 'Yes' ? 'true' : 'false';
        const specialTreatmentRadio = this.page.locator(`input[type='radio'][value='${specialTreatmentValue}']`);
        await specialTreatmentRadio.waitFor({ state: 'visible', timeout: 5000 });
        await specialTreatmentRadio.check();
        // Click Save button
        const saveButton = this.page.locator(this.saveButton);
        await saveButton.waitFor({ state: 'visible', timeout: 5000 });
        await saveButton.click();
        Logger(`Selected: Qualification='${qualification}', Inventory='${inventoryOption}', SpecialTreatment='${specialTreatment}'`);
    }
}

```
