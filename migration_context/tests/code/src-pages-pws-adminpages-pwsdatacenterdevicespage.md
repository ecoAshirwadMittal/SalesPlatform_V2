# Page Object: PWS_DataCenter_DevicesPage.ts

- **Path**: `src\pages\PWS\AdminPages\PWS_DataCenter_DevicesPage.ts`
- **Category**: Page Object
- **Lines**: 59
- **Size**: 2,835 bytes
- **Members**: `class PWS_DataCenter_DevicesPage`, `goToPWSDevicesPage`, `getATPAndReservedQtyBySKU`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { NavMenuPage } from '../../CommonPages/NavMenuPage';
import { Logger } from '../../../utils/helpers/data_utils';

export class PWS_DataCenter_DevicesPage {
    private readonly PWS_devices_button = "//span[text()='PWS Devices']";
    private readonly sum_SKUs = "//span[text()='SKUs']/following-sibling::h5";
    private readonly sku_filter_input = "(//input[@type='text'])[1]";
    private readonly ATP_qty_firstRow = "(//div[@role='row'])[2]//div[5]";
    private readonly Reserved_qty_firstRow = "(//div[@role='row'])[2]//div[6]";

    constructor(private page: Page) { }

    private async goToPWSDevicesPage() {
        const adminMainPage = new NavMenuPage(this.page);
        await adminMainPage.chooseSubNav_UnderAdminMainNav('PWS Data Center');
        await this.page.locator(this.PWS_devices_button).click();
        await this.page.waitForTimeout(3000);
        Logger('Navigated to PWS Devices Page via PWS Data Center subnav');
    }

    async getATPQtyBySKU(sku: string): Promise<number> {
        await this.goToPWSDevicesPage();
        const skuInput = this.page.locator(this.sku_filter_input);
        await skuInput.waitFor({ state: 'visible', timeout: 5000 });
        await skuInput.fill('');
        await skuInput.type(sku);
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(2000);
        const atpQtyCell = this.page.locator(this.ATP_qty_firstRow);
        await atpQtyCell.waitFor({ state: 'visible', timeout: 5000 });
        const atpQtyText = (await atpQtyCell.textContent())?.trim() || '';
        const atpQty = Number(atpQtyText);
        Logger(`ATPQty for SKU ${sku}: ${atpQty}`);
        return atpQty;
    }

    async getATPAndReservedQtyBySKU(sku: string): Promise<{ atp: number; reserved: number }> {
        await this.goToPWSDevicesPage();
        const skuInput = this.page.locator(this.sku_filter_input);
        await skuInput.waitFor({ state: 'visible', timeout: 5000 });
        await skuInput.fill('');
        await skuInput.type(sku);
        await this.page.keyboard.press('Enter');
        await this.page.waitForTimeout(2000);
        // Get ATP
        const atpQtyCell = this.page.locator(this.ATP_qty_firstRow);
        await atpQtyCell.waitFor({ state: 'visible', timeout: 5000 });
        const atpQtyText = (await atpQtyCell.textContent())?.trim() || '';
        const atpQty = Number(atpQtyText);
        // Get Reserved
        const reservedQtyCell = this.page.locator(this.Reserved_qty_firstRow);
        const reservedQtyText = (await reservedQtyCell.textContent())?.trim() || '';
        const reservedQty = Number(reservedQtyText);
        Logger(`For SKU ${sku}: ATP = ${atpQty}, Reserved = ${reservedQty}`);
        return { atp: atpQty, reserved: reservedQty };
    }
}

```
