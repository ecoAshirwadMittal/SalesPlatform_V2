# Page Object: NavMenuPage.ts

- **Path**: `src\pages\CommonPages\NavMenuPage.ts`
- **Category**: Page Object
- **Lines**: 161
- **Size**: 8,039 bytes
- **Members**: `class NavMenuPage`, `chooseMainNav`, `chooseSubNav_Reports`, `chooseSubNav_UnderAdminMainNav`, `chooseSubNav_UnderSettingMainNav`, `BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin`, `BidAsBidderPage_chooseBuyerCodeAsMultiCodeBuyer`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { navTabs } from '../../utils/resources/enum';
import { logger, Logger } from '../../utils/helpers/data_utils';
//import { AuctionRoundOnePage } from '../Auction/BuyerPages/AuctionRoundOnePage';
import { click, waitAndClick, waitAndFill } from '../../utils/helpers/commonMethods';
import { cli } from 'cypress';

export class NavMenuPage {
    private readonly navXpath = "(//ul[@role='menu'])[1]/li";
    private readonly inventoryPageTitle = "//h2[contains(text(),'Inventory')]";
    private readonly adminSubNav = "//a[@title='Admin']/following-sibling::ul[@role='menu']//li";
    private readonly settingsSubNav = "//a[@title='Settings']/following-sibling::ul[@role='menu']//li";
    private readonly reportsSubNav = "//a[@title='Reports']/following-sibling::ul[@role='menu']//li";
    private readonly PAGE_TITLES = {
        [navTabs.Users]: "ecoATM Direct - Users",
        [navTabs.Buyers]: "ecoATM Direct - Buyers",
        [navTabs.Inventory]: "ecoATM Direct - Inventory Snowflake",
        [navTabs.PurchaseOrder]: "ecoATM Direct - Create PO",
        [navTabs.ReservedBids]: "ecoATM Direct - Reserved Price Overview",
        [navTabs.AuctionScheduling]: "ecoATM Direct - Auction Scheduling",
        [navTabs.BidAsBider]: "ecoATM Direct - Bid as Bidder",
        [navTabs.DeviceAllocation]: "ecoATM Direct - Device Allocation",
        [navTabs.Admin]: "ecoATM Direct - Admin"
    };
    private readonly chooseBuyerCodeOptions = "//span[@class='mx-text mx-name-text2 BuyerSelection_Code']";

    
    constructor(private page: Page) { }

    async chooseMainNav(selectedNavTab: navTabs) {
        Logger(`Navigating to ${selectedNavTab} page`)
        const navTabsElement = this.page.locator(this.navXpath);
        try {
            for (let i = 0; i < await navTabsElement.count(); i++) {
                const eachTab = await navTabsElement.nth(i).textContent();
                if (eachTab?.trim() === selectedNavTab) {
                    await click(this.page, navTabsElement.nth(i));
                    break;
                }
            }
        } catch (error) {
            logger.error(`Navigation Tab ${selectedNavTab} Doesn't Exist:`, error);
        }
        await this.page.waitForTimeout(3000);
        await this.page.waitForLoadState();
        const actualTitle = await this.page.title();
        const expectedTitle = this.PAGE_TITLES[selectedNavTab];
        // expect(actualTitle).toBe(expectedTitle);
    }

    async chooseSubNav_Reports(subNavTabName: 'Buyer Bid Summary Report' | 'Round Target Price' | 'Round Three Bid Report by Buyer') {
        const reportsNav = this.page.locator("//a[@title='Reports']");
        await reportsNav.waitFor({ state: 'visible', timeout: 5000 });
        await reportsNav.click();
        const subNavElements = this.page.locator(this.reportsSubNav);
        const count = await subNavElements.count();
        for (let i = 0; i < count; i++) {
            const text = (await subNavElements.nth(i).textContent())?.trim();
            if (text === subNavTabName) {
                await click(this.page, subNavElements.nth(i));
                Logger(`Clicked subnav under Reports: ${subNavTabName}`);
                return;
            }
        }
        throw new Error(`SubNavTab "${subNavTabName}" not found under Reports`);
    }

    async chooseSubNav_UnderAdminMainNav(subNavTabName: 'Application Control Center' | 'Auction Data Center' | 'PWS Data Center') {
        const adminTab = this.page.locator("//a[@title='Admin']");
        await adminTab.waitFor({ state: 'visible', timeout: 5000 });
        await adminTab.click();
        const subNavElements = this.page.locator(this.adminSubNav);
        const count = await subNavElements.count();
        for (let i = 0; i < count; i++) {
            const text = (await subNavElements.nth(i).textContent())?.trim();
            if (text === subNavTabName) {
                await click(this.page, subNavElements.nth(i));
                Logger(`Clicked subnav: ${subNavTabName}`);
                return;
            }
        }
        throw new Error(`SubNavTab "${subNavTabName}" not found under Admin`);
    }

    async chooseSubNav_UnderSettingMainNav(subNavTabName: 'Auctions Control Center' | 'PWS Control Center') {
        await this.page.reload();
        await this.page.waitForTimeout(2000);
        await waitAndClick(this.page, "//a[@title='Settings']");
        // Locate subnav options under Settings
        const subNavElements = this.page.locator(this.settingsSubNav);
        const count = await subNavElements.count();
        for (let i = 0; i < count; i++) {
            const text = (await subNavElements.nth(i).textContent())?.trim();
            console.log(`Found subnav option: ${text}`); // Debug log
            if (text === subNavTabName) {
                await click(this.page, subNavElements.nth(i));
                Logger(`Clicked subnav under Settings: ${subNavTabName}`);
                return;
            }
        }
        throw new Error(`SubNavTab "${subNavTabName}" not found under Settings`);
    }

    // ---------- Auction Control Center Page --------------

    async clickAuctionControlTab(tabName: "Auctions" | "Scheduled Auctions" | "Bid Data" | "Round 2 Selection Criteria" |
        "UserGuide Configuration" | "Bid Data Total Configuration" | "Auctions Feature Flags" |
        "Round 3 Selection Criteria" | "Error Test Page" | "Qualified Buyer Codes"): Promise<void> {
        await this.chooseSubNav_UnderSettingMainNav("Auctions Control Center");
        await this.page.waitForTimeout(2000);
        const tabMenuLocator = this.page.locator("//div[@class='row']//span[contains(@class,'mx-text')]");
        await tabMenuLocator.first().waitFor({ state: 'visible', timeout: 5000 });
        const count = await tabMenuLocator.count();
        for (let i = 0; i < count; i++) {
            const text = (await tabMenuLocator.nth(i).innerText())?.trim();
            if (text === tabName) {
                await click(this.page, tabMenuLocator.nth(i));
                Logger(`Clicked Auction Control Center tab: ${tabName}`);
                return;
            }
        }
        throw new Error(`Tab "${tabName}" not found on Auction Control Center page`);
    }

    // ---------- Bid As Bider Page --------------

    async BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin(buyerCode: string) {
        await this.chooseMainNav(navTabs.BidAsBider);
        await this.chooseMainNav(navTabs.BidAsBider);
        await this.page.waitForTimeout(2000);
        await waitAndClick(this.page, '.widget-combobox-input');
        await waitAndFill(this.page, '.widget-combobox-input', buyerCode);
        const locator = this.page.locator('.widget-combobox-input');       
        await this.page.waitForTimeout(2000);
        await locator.press('Enter');
        await this.page.waitForTimeout(5000);
    }

    async BidAsBidderPage_chooseBuyerCodeAsMultiCodeBuyer(buyerCode: string) {
        await this.chooseMainNav(navTabs.BidAsBider);
        await this.chooseMainNav(navTabs.BidAsBider);
        await this.page.waitForTimeout(2000);
        // Wait for buyer code options to be visible
        await this.page.waitForSelector(this.chooseBuyerCodeOptions, { state: 'visible', timeout: 5000 });
        // Find and click the option that matches the buyerCode
        const optionLocator = this.page.locator(this.chooseBuyerCodeOptions);
        const count = await optionLocator.count();
        for (let i = 0; i < count; i++) {
            const text = (await optionLocator.nth(i).textContent())?.trim();
            if (text === buyerCode) {
                await click(this.page, optionLocator.nth(i));
                
                Logger(`Clicked buyer code option: ${buyerCode}`);
                await this.page.waitForTimeout(5000);
                return;
            }
        }
        throw new Error(`Buyer code "${buyerCode}" not found in options`);
    }
}

```
