# Page Object: InventoryPage.ts

- **Path**: `src\pages\Auction\AdminPages\InventoryPage.ts`
- **Category**: Page Object
- **Lines**: 143
- **Size**: 7,032 bytes
- **Members**: `class InventoryPage`, `validateDuplication`, `getAdditionalQty`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { navTabs } from '../../../utils/resources/enum';
import { validateDuplication } from '../../../utils/helpers/data_utils';
import { Logger } from '../../../utils/helpers/data_utils';
import { NavMenuPage } from '../../CommonPages/NavMenuPage';
import auction_config from '../../../utils/resources/auction_config.json';

export class InventoryPage {
    private readonly Inventory_AuctionWeekDropdown = "//div[@class='widget-combobox-selected-items']";
    private readonly Inventory_AuctionWeekOptions = "//ul[contains(@class,'widget-combobox-menu-list') and @role='listbox']";
    private readonly downloadInventoryButton = "//button[@type='button' and text()='Download']";
    private readonly filter_DW_Qty_icon = "(//button[@class='btn btn-default filter-selector-button button-icon equal'])[2]";
    private readonly filter_DW_Qty_GreaterThan_option = "((//div[@class='filter-selector-content'])[7]//ul//li[4])";
    private readonly filter_DW_Qty_InputField = "(//input[@class='form-control filter-input'])[7]";

    constructor(private page: Page) { }

    async validateDuplication(nameFile_excel: string, nameFile_csv: string) {
        //   await validateDuplication(this.page, this.downloadInventoryButton, nameFile_excel, nameFile_csv);
    }

    async getDWQtyOfFirstFiveRows(): Promise<number[]> {
        await this.selectAuctionWeek();
        await this.filterDWQtyGreaterThanZero();
        await this.page.waitForTimeout(2000); // Wait for 2 seconds to ensure the filter is applied
        const qtyArray: number[] = [];
        for (let i = 2; i <= 6; i++) {
            // DW Qty column is data-position="6,0" (7th column, 0-based index)
            const qtyCell = this.page.locator(`((//div[@role='row'])[${i}]//span)[7]`);
            await qtyCell.waitFor({ state: 'visible', timeout: 5000 });
            const qtyText = (await qtyCell.textContent())?.replace(/,/g, '').trim() ?? "0";
            const qty = Number(qtyText);
            qtyArray.push(qty);
            Logger(`Row ${i} DW Qty: ${qty}`);
        }
        return qtyArray;
    }

    async getInventoryDataByProductID(productID: string): Promise<Array<{
        productID: string,
        grade: string,
        dwQty: string,
        dwTargetPrice: string,
        totalQty: string,
        targetPrice: string
    }>> {
        await this.selectAuctionWeek();
        // Filter by Product ID
        const productIDFilter = this.page.locator("(//input[@class='form-control filter-input'])[1]");
        await productIDFilter.waitFor({ state: 'visible', timeout: 5000 });
        await productIDFilter.fill(productID);
        await this.page.waitForTimeout(5000);

        const rows = this.page.locator("//div[contains(@class,'widget-datagrid-grid-body')]//div[@role='row']");
        const rowCount = await rows.count();
        const results: Array<{
            productID: string,
            grade: string,
            dwQty: string,
            dwTargetPrice: string,
            totalQty: string,
            targetPrice: string
        }> = [];

        for (let i = 0; i < rowCount; i++) {
            const getCellText = async (colIndex: number) => {
                const cell = this.page.locator(`//div[@role='row']//div[@data-position='${colIndex},${i}']`);
                if (await cell.count() === 0) return "";
                return (await cell.textContent())?.trim() ?? "";
            };

            results.push({
                productID: await getCellText(0),
                grade: await getCellText(1),
                dwQty: await getCellText(6),
                dwTargetPrice: await getCellText(7),
                totalQty: await getCellText(8),
                targetPrice: await getCellText(9)
            });
        }
        return results;
    }

    getAdditionalQty(productID: string, grade: string, buyerType: "DW" | "Non-DW"): number {
        const config = auction_config.additional_qty_config.find(item =>
            item.productID === productID && item.grade === grade
        );        
        if (!config) {
            Logger(`No additional qty config found for productID: ${productID}, grade: ${grade}`);
            return 0;
        }       
        let additionalQty: number;
        if (buyerType === "DW") {
            // DW buyer gets only data_wipe_qty
            additionalQty = Number(config.data_wipe_qty);
            Logger(`Additional qty for DW buyer ${productID} ${grade}: ${config.data_wipe_qty}`);
        } else {
            // Non-DW buyer gets both data_wipe_qty and additional_qty
            additionalQty = Number(config.data_wipe_qty) + Number(config.additional_qty);
            Logger(`Additional qty for Non-DW buyer ${productID} ${grade}: ${config.data_wipe_qty} + ${config.additional_qty} = ${additionalQty}`);
        }        
        return additionalQty;
    }

    private async filterDWQtyGreaterThanZero(): Promise<void> {
        await this.selectAuctionWeek();
        // Click the "=" icon to open the dropdown
        await this.page.waitForTimeout(2000); // Wait for 2 seconds to ensure the page has loaded
        const filterButton = this.page.locator(this.filter_DW_Qty_icon);
        await filterButton.waitFor({ state: 'visible', timeout: 5000 });
        await filterButton.click();
        // Click the "Greater than" option in the dropdown
        const greaterThanOption = this.page.locator(this.filter_DW_Qty_GreaterThan_option);
        await greaterThanOption.waitFor({ state: 'visible', timeout: 5000 });
        await greaterThanOption.click();
        await this.page.waitForTimeout(3000);
        // Input number zero in the filter input field
        const qtyInput = this.page.locator(this.filter_DW_Qty_InputField);
        await qtyInput.waitFor({ state: 'visible', timeout: 5000 });
        await qtyInput.fill('0');
        await qtyInput.press('Enter');
        Logger("Filtered DW Qty to show only rows with quantity greater than zero.");
        await this.page.waitForTimeout(2000);
    }

    private async selectAuctionWeek(): Promise<void> {    // Select the second week from the Auction Week dropdown
        const adminMainPage = new NavMenuPage(this.page);
        await adminMainPage.chooseMainNav(navTabs.Inventory);
        // Click the Auction Week dropdown to open it
        const dropdown = this.page.locator(this.Inventory_AuctionWeekDropdown);
        await dropdown.waitFor({ state: 'visible', timeout: 5000 });
        await dropdown.click();
        // Wait for the dropdown menu to be visible
        const dropdownMenu = this.page.locator(this.Inventory_AuctionWeekOptions);
        await dropdownMenu.waitFor({ state: 'visible', timeout: 5000 });
        // Click the second week option from the top
        const secondWeekOption = dropdownMenu.locator("li").nth(1);
        await secondWeekOption.waitFor({ state: 'visible', timeout: 5000 });
        await secondWeekOption.click();
        await this.page.waitForTimeout(4000);
    }
}

```
