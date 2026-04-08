# Page Object: DeposcoOrdersPage.ts

- **Path**: `src\pages\Deposco\DeposcoOrdersPage.ts`
- **Category**: Page Object
- **Lines**: 284
- **Size**: 10,247 bytes
- **Members**: `class DeposcoOrdersPage`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { Logger } from '../../utils/helpers/commonMethods';

/**
 * DeposcoOrdersPage - Handles order-related operations for Deposco WMS
 * Includes navigation to Orders, searching, and order actions
 */
export class DeposcoOrdersPage {
    private readonly page: Page;

    // Selectors
    private readonly selectors = {
        // Navigation
        ordersTab: ".menu-item-link:has-text('Orders')",
        ordersTabAlt: "span:has-text('Orders'), div:has-text('Orders')",
        ordersSubmenu: "a.submenu-item:has-text('Orders')",
        ordersSubmenuFallback: "a:has-text('Orders')",
        submenuItem: ".submenu-item",

        // Filter and Search
        filterButton: "button:has-text('Filter')",
        filterInput: "input[id*='value']",
        filterInputById: "input#value_0_id",
        applyButton: "button:has-text('Apply')",
        applyButtonSecondary: "button.btn-secondary:has-text('Apply')",
        closeButton: "button:has-text('Close')",

        // Actions menu
        actionsButton: "button[title='Actions']",
        actionsButtonText: "button:has-text('Actions')",
        postOrderImportMenuItem: "button[role='menuitem']:has-text('Post Order Import')",
        buildReleaseWaveMenuItem: "button[role='menuitem']:has-text('Build And Release Wave')",
        closeModalButton: "button:has-text('Close'), button:has-text('OK')"
    };

    constructor(page: Page) {
        this.page = page;
    }

    /**
     * Click on Orders tab in the sidebar menu
     */
    async clickOrdersTab(): Promise<void> {
        Logger("Clicking on Orders tab");
        await this.page.waitForTimeout(2000);

        const ordersTab = this.page.locator(this.selectors.ordersTab).first();

        if (await ordersTab.isVisible({ timeout: 5000 }).catch(() => false)) {
            await ordersTab.click();
        } else {
            // Try JavaScript click as fallback
            await this.page.evaluate(() => {
                const menuItems = document.querySelectorAll('.menu-item-link');
                for (const item of menuItems) {
                    if (item.textContent?.includes('Orders')) {
                        (item as HTMLElement).click();
                        break;
                    }
                }
            });
        }
        Logger("Clicked Orders tab");
        await this.page.waitForTimeout(2000);
    }

    /**
     * Click on Orders sub-menu item
     */
    async clickOrdersSubmenu(): Promise<void> {
        Logger("Clicking on Orders sub-menu");
        await this.page.waitForTimeout(1000);

        const submenuOrders = this.page.locator(this.selectors.ordersSubmenu).first();

        if (await submenuOrders.isVisible({ timeout: 5000 }).catch(() => false)) {
            await submenuOrders.click();
        } else {
            // Try JavaScript click as fallback
            await this.page.evaluate(() => {
                const submenuItems = document.querySelectorAll('.submenu-item');
                if (submenuItems.length > 0) {
                    (submenuItems[0] as HTMLElement).click();
                }
            });
        }
        Logger("Clicked Orders sub-menu");
        await this.page.waitForTimeout(3000);

        // Verify Orders list page loaded
        const filterButton = this.page.locator(this.selectors.filterButton);
        await expect(filterButton).toBeVisible({ timeout: 15000 });
        Logger("Orders list page loaded");
    }

    /**
     * Navigate to Orders page (clicks both Orders tab and sub-menu)
     */
    async navigateToOrdersPage(): Promise<void> {
        await this.clickOrdersTab();
        await this.clickOrdersSubmenu();
    }

    /**
     * Search for an order by order number
     * @param orderNumber - The order number to search for
     */
    async searchForOrder(orderNumber: string): Promise<void> {
        Logger(`Searching for order: ${orderNumber}`);

        // Click Filter button
        const filterButton = this.page.locator(this.selectors.filterButton);
        await filterButton.click();
        Logger("Clicked Filter button");

        await this.page.waitForTimeout(2000);

        // Enter order number in filter input
        let filterInput = this.page.locator(this.selectors.filterInputById);
        if (!await filterInput.isVisible({ timeout: 3000 }).catch(() => false)) {
            filterInput = this.page.locator(this.selectors.filterInput).first();
        }
        await expect(filterInput).toBeVisible({ timeout: 10000 });
        await filterInput.click();
        await filterInput.fill(orderNumber);
        Logger(`Entered order number: ${orderNumber}`);

        // Click Apply
        let applyButton = this.page.locator(this.selectors.applyButtonSecondary);
        if (!await applyButton.isVisible({ timeout: 3000 }).catch(() => false)) {
            applyButton = this.page.locator(this.selectors.applyButton);
        }
        await applyButton.click();
        Logger("Clicked Apply button");

        await this.page.waitForTimeout(3000);
        Logger("Search executed");
    }

    /**
     * Click on search result (order row)
     * @param orderNumber - The order number to click on
     */
    async clickOnSearchResult(orderNumber: string): Promise<void> {
        Logger(`Clicking on order ${orderNumber} in search results`);
        await this.page.waitForTimeout(2000);

        // Close filter panel if open
        const closeButton = this.page.locator(this.selectors.closeButton);
        if (await closeButton.isVisible({ timeout: 2000 }).catch(() => false)) {
            await closeButton.click();
            await this.page.waitForTimeout(1000);
        }

        // Find and click on the order
        const orderResult = this.page.locator(`text=${orderNumber}`).first();
        await expect(orderResult).toBeVisible({ timeout: 10000 });
        await orderResult.click();
        Logger(`Clicked on order ${orderNumber}`);

        await this.page.waitForTimeout(3000);

        // Verify order details page loaded
        await expect(this.page.locator(`text=${orderNumber}`).first()).toBeVisible({ timeout: 10000 });
        Logger(`Order ${orderNumber} details loaded`);
    }

    /**
     * Search for order and open it
     * @param orderNumber - The order number to search and open
     */
    async searchAndOpenOrder(orderNumber: string): Promise<void> {
        await this.searchForOrder(orderNumber);
        await this.clickOnSearchResult(orderNumber);
    }

    /**
     * Click Actions button and then Post Order Import
     */
    async executePostOrderImport(): Promise<void> {
        Logger("Executing Post Order Import action");
        await this.page.waitForTimeout(2000);

        // Click Actions button
        await this.clickActionsButton();
        await this.page.waitForTimeout(1000);

        // Wait for popup and click Post Order Import
        const [popup] = await Promise.all([
            this.page.waitForEvent('popup', { timeout: 10000 }).catch(() => null),
            this.page.locator(this.selectors.postOrderImportMenuItem).click()
        ]);

        Logger("Clicked Post Order Import");

        // Handle confirmation popup
        await this.handleActionPopup(popup, "Post Order Import");
    }

    /**
     * Click Actions button and then Build and Release Wave
     */
    async executeBuildAndReleaseWave(): Promise<void> {
        Logger("Executing Build and Release Wave action");
        await this.page.waitForTimeout(2000);

        // Click Actions button
        await this.clickActionsButton();
        await this.page.waitForTimeout(1000);

        // Wait for popup and click Build and Release Wave
        const [popup] = await Promise.all([
            this.page.waitForEvent('popup', { timeout: 10000 }).catch(() => null),
            this.page.locator(this.selectors.buildReleaseWaveMenuItem).click()
        ]);

        Logger("Clicked Build And Release Wave");

        // Handle confirmation popup
        await this.handleActionPopup(popup, "Build and Release Wave");
    }

    /**
     * Click the Actions button
     */
    private async clickActionsButton(): Promise<void> {
        const actionsButton = this.page.locator(this.selectors.actionsButton);
        if (await actionsButton.isVisible({ timeout: 5000 }).catch(() => false)) {
            await actionsButton.click();
        } else {
            const actionsText = this.page.locator(this.selectors.actionsButtonText).first();
            await actionsText.click();
        }
        Logger("Clicked Actions button");
    }

    /**
     * Handle action popup/modal
     * @param popup - The popup page if one opened
     * @param actionName - Name of the action for logging
     */
    private async handleActionPopup(popup: Page | null, actionName: string): Promise<void> {
        if (popup) {
            await popup.waitForLoadState();
            Logger(`${actionName} popup opened`);

            // Wait for operation to complete
            await popup.waitForTimeout(3000);

            // Close the popup
            await popup.close();
            Logger(`Closed ${actionName} confirmation popup`);
        } else {
            // If no popup, wait for any in-page modal
            await this.page.waitForTimeout(3000);
            const closeModal = this.page.locator(this.selectors.closeModalButton).first();
            if (await closeModal.isVisible({ timeout: 2000 }).catch(() => false)) {
                await closeModal.click();
            }
        }

        await this.page.waitForTimeout(2000);
        Logger(`${actionName} action completed`);
    }

    /**
     * Refresh the current page
     */
    async refreshPage(): Promise<void> {
        Logger("Refreshing the page");
        await this.page.reload();
        await this.page.waitForTimeout(3000);
    }

    /**
     * Verify order is visible on the page
     * @param orderNumber - The order number to verify
     */
    async verifyOrderVisible(orderNumber: string): Promise<void> {
        await expect(this.page.locator(`text=${orderNumber}`).first()).toBeVisible({ timeout: 15000 });
        Logger(`Order ${orderNumber} is visible on the page`);
    }
}

```
