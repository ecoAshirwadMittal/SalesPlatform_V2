# Page Object: PWS_NavMenu_AsAdmin.ts

- **Path**: `src\pages\PWS\AdminPages\PWS_NavMenu_AsAdmin.ts`
- **Category**: Page Object
- **Lines**: 48
- **Size**: 2,054 bytes
- **Members**: `class PWS_NavMenu_AsAdmin`, `chooseNavMenu`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';

export class PWS_NavMenu_AsAdmin {

    constructor(private page: Page) { }


    async chooseNavMenu(menuName: "Inventory" | "Offer Review" | "RMA Review" | "Pricing" | "Shop" | "Counters" | "Orders" | "RMAs" | "FAQ's" | "Grading") {
        const titleMap: Record<string, string> = {
            "Inventory": "Order",
            "Offer Review": "Offers",
            "RMA Review": "RMA Returns Overview",
            "Pricing": "Bid Report",

            "Shop": "Inventory",
            "Counters": "Counter Offers",
            "Orders": "Orders",
            "RMAs": "RMA Returns Overview",
            "FAQ's": "FAQ's",
            "Grading": "Grading",            
        };
        const expectedTitle = titleMap[menuName];
        const navBarMenu = this.page.locator("//div[@class='mx-name-container3 pws-navbar-position']");
        await navBarMenu.waitFor({ state: 'visible', timeout: 10000 });
        await this.page.waitForTimeout(3000);
        const navBarMenuItems = this.page.locator("//div[@class='mx-name-container3 pws-navbar-position']//li//span[2]");
        const count = await navBarMenuItems.count();
        console.log("Nav Bar Menu Item Count: ", count);
        for (let i = 0; i < count; i++) {
            const text = await navBarMenuItems.nth(i).textContent();
            console.log("Nav Bar Menu Item Text: ", text);
            if (text === menuName) {
                await navBarMenuItems.nth(i).click();
                Logger(`Clicked nav bar menu: ${menuName}`);
                await this.page.waitForFunction(
                    (expected) => document.title.toLowerCase().includes(expected.toLowerCase()),
                    expectedTitle,
                    { timeout: 5000 }
                );
                Logger(`Verified page title contains: ${expectedTitle}`);
                return;
            }
        }
        throw new Error(`Nav bar menu "${menuName}" not found.`);
    }
}

```
