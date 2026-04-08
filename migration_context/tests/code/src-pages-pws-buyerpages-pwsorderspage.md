# Page Object: PWS_OrdersPage.ts

- **Path**: `src\pages\PWS\BuyerPages\PWS_OrdersPage.ts`
- **Category**: Page Object
- **Lines**: 98
- **Size**: 4,027 bytes
- **Members**: `class PWS_OrdersPage`, `ensureUserOnOrdersPage`, `getOrderRowDetails`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { BaseTest } from '../../../tests/BaseTest';
import { Logger } from '../../../utils/helpers/data_utils';
import { userRole } from '../../../utils/resources/enum';

export class PWS_OrdersPage {
    private readonly pageTitleSelector = "//div[contains(@class,'page-header')]//h3[contains(text(),'Orders')]";
    private readonly orderGrid = ".widget-datagrid-grid-body"; // Assuming standard grid
    private readonly searchInput = "//input[contains(@class,'form-control')]"; // Generic search if available

    constructor(private page: Page) { }

    async ensureUserOnOrdersPage(base: BaseTest, userRole: userRole) {
        try {
            const isVisible = await this.page.locator(this.pageTitleSelector).isVisible().catch(() => false);
            if (isVisible) {
                Logger("Already on Orders page.");
                return;
            }
        } catch {
            Logger("Orders page not visible, navigating...");
        }

        // If not logged in or wrong page
        // Check if we are checking nav menu first?
        // Safest is generic ensure login then navigate
        await base['loginPage'].loginAs(userRole);
        await base['pws_navMenuAsBuyer'].chooseNavMenu("Orders");
        await this.page.waitForSelector(this.pageTitleSelector, { state: 'visible', timeout: 10000 });
    }

    /**
     * Verify if a specific Order ID exists in the list
     * @param orderID The Order ID (formatted as per UI, usually Offer #)
     */
    async verifyOrderExists(orderID: string): Promise<boolean> {
        Logger(`Verifying Order ID ${orderID} exists in Orders tab...`);

        // Sanitize ID (remove leading colon if present which PWS_CartPage might return)
        const cleanID = orderID.replace(":", "").trim();

        // Wait for grid to load
        await this.page.waitForSelector(this.orderGrid, { state: 'visible', timeout: 10000 });

        // Look for the ID in the grid
        // Start by simple text search in the grid
        const orderLocator = this.page.locator(this.orderGrid).filter({ hasText: cleanID });

        // Wait for it to appear (might take a moment to sync)
        try {
            await orderLocator.waitFor({ state: 'visible', timeout: 10000 });
            return true;
        } catch (e) {
            Logger(`Order ID ${cleanID} not found in grid.`);
            // Dump grid text for debug
            const gridText = await this.page.locator(this.orderGrid).textContent();
            Logger(`Grid Content: ${gridText?.substring(0, 200)}...`);
            return false;
        }
    }

    /**
     * Get the details of a specific order row by its Order ID.
     * Returns all visible cell text from the matching row.
     * @param orderID The Order ID to find
     */
    async getOrderRowDetails(orderID: string): Promise<{ found: boolean; cells: string[] }> {
        const cleanID = orderID.replace(":", "").trim();
        Logger(`Fetching order row details for Order ID: ${cleanID}`);

        await this.page.waitForSelector(this.orderGrid, { state: 'visible', timeout: 10000 });

        // Find the row containing this order ID
        const row = this.page.locator(`${this.orderGrid} div[role='row']`).filter({ hasText: cleanID });

        try {
            await row.first().waitFor({ state: 'visible', timeout: 10000 });
        } catch {
            Logger(`Order row for ID ${cleanID} not found.`);
            return { found: false, cells: [] };
        }

        // Extract all cell text from the matching row
        const cellLocators = row.first().locator("div[role='gridcell']");
        const cellCount = await cellLocators.count();
        const cells: string[] = [];

        for (let i = 0; i < cellCount; i++) {
            const text = (await cellLocators.nth(i).textContent() || '').trim();
            cells.push(text);
        }

        Logger(`Order ${cleanID} row cells: ${JSON.stringify(cells)}`);
        return { found: true, cells };
    }
}


```
