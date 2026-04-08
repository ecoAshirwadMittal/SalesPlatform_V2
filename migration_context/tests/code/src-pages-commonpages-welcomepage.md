# Page Object: WelcomePage.ts

- **Path**: `src\pages\CommonPages\WelcomePage.ts`
- **Category**: Page Object
- **Lines**: 116
- **Size**: 4,749 bytes
- **Members**: `class WelcomePage`, `ensureUserOnWelcomePage`

## Source Code

```typescript
import { Page } from '@playwright/test';
import { userRole } from '../../utils/resources/enum';
import user_data from '../../utils/resources/user_data.json';
import { Logger, waitAndClick, waitAndFill } from '../../utils/helpers/commonMethods';
import { BaseTest } from '../../tests/BaseTest';
import { LoginPage } from '../../pages/CommonPages/LoginPage';


export class WelcomePage {
    private readonly welcomeHeader = "//span[@class='mx-text mx-name-text1 bcs-header']";
    private readonly PWS_SectionHeader = "//span[@class='mx-text mx-name-text3 bcs-card-header']";
    private readonly AUC_SectionHeader = "//span[@class='mx-text mx-name-text7 bcs-card-header']";
    private readonly buyerCodeList = "//div[contains(@class,'bcs-buyercode-list')]//li";
    private readonly PWS_BuyerCodes = "//span[@class='mx-text mx-name-text5 bcs-buyer-code']";
    private readonly AUC_BuyerCodes = "//span[@class='mx-text mx-name-text9 bcs-buyer-code']";
    private readonly AuctionDashboard = "//a[@title='Auction']";

    constructor(private page: Page) { }

    async ensureUserOnWelcomePage(userName: string, password: string) {
        try {
            const isWelcomeVisible = await this.page.locator(this.welcomeHeader).isVisible().catch(() => false);
            if (isWelcomeVisible) {
                Logger("Already on Welcome page, no login needed.");
                return;
            }
        } catch (error) {
            Logger("Welcome page not displayed, will login.");
        }
        const loginPage = new LoginPage(this.page);
        await loginPage.login(userName, password);
    }



    async isPWSSectionVisible(): Promise<boolean> {
        const locator = this.page.locator(this.PWS_SectionHeader);
        try {
            await locator.waitFor({ state: 'visible', timeout: 5000 });
            return true;
        } catch {
            return false;
        }
    }

    async isAUCSectionVisible(): Promise<boolean> {
        const locator = this.page.locator(this.AUC_SectionHeader);
        try {
            await locator.waitFor({ state: 'visible', timeout: 5000 });
            return true;
        } catch {
            return false;
        }
    }

    async isPWSBuyerCodeExist(buyerCode: string): Promise<boolean> {
        const buyerCodesFromPage = this.page.locator(this.PWS_BuyerCodes);
        await buyerCodesFromPage.first().waitFor({ state: 'visible', timeout: 5000 });
        const count = await buyerCodesFromPage.count();
        for (let i = 0; i < count; i++) {
            const code = await buyerCodesFromPage.nth(i).textContent();
            console.log(code)
            if (code && code.trim().toLowerCase() === buyerCode.trim().toLowerCase()) {
                return true;
            }
        }
        return false;
    }

    async isAUCBuyerCodeExist(buyerCode: string): Promise<boolean> {
        const buyerCodesFromPage = this.page.locator(this.AUC_BuyerCodes);
        await buyerCodesFromPage.first().waitFor({ state: 'visible', timeout: 5000 });
        const count = await buyerCodesFromPage.count();
        for (let i = 0; i < count; i++) {
            const code = await buyerCodesFromPage.nth(i).textContent();
            if (code && code.trim().toLowerCase() === buyerCode.trim().toLowerCase()) {
                return true;
            }
        }
        return false;
    }

    async isBuyerNameInWelcomeHeader(buyerName: string): Promise<boolean> {
        const header = this.page.locator(this.welcomeHeader);
        await header.waitFor({ state: 'visible', timeout: 5000 });
        const text = await header.textContent();
        return text ? text.toLowerCase().includes(buyerName.toLowerCase()) : false;
    }

    async selectBuyerCode(buyerCode: string): Promise<void> {
        const options = this.page.locator(this.buyerCodeList);
        await options.first().waitFor({ state: 'visible', timeout: 5000 });
        const count = await options.count();
        for (let i = 0; i < count; i++) {
            const optionText = await options.nth(i).textContent();
            if (optionText && optionText.toLowerCase().includes(buyerCode.trim().toLowerCase())) {
                await waitAndClick(this.page, options.nth(i));
                await this.page.waitForTimeout(2000);
                return;
            }
        }
        throw new Error(`Buyer code '${buyerCode}' not found in the list.`);
    }

    //Auction Page
    async isAuctionDashboardVisible(): Promise<boolean> {
        const auctionDashboard = this.page.locator(this.AuctionDashboard);
        try {
            await auctionDashboard.waitFor({ state: 'visible', timeout: 5000 });
            return true;
        } catch {
            return false;
        }
    }
}

```
