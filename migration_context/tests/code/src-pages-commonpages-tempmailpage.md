# Page Object: TempMailPage.ts

- **Path**: `src\pages\CommonPages\TempMailPage.ts`
- **Category**: Page Object
- **Lines**: 79
- **Size**: 3,742 bytes
- **Members**: `class TempMailPage`

## Source Code

```typescript
import { BrowserContext, Page, expect } from '@playwright/test';
import { userRole } from '../../utils/resources/enum';
import user_data from '../../utils/resources/user_data.json';
import { Logger } from '../../utils/helpers/data_utils';

export class TempMailPage {
    constructor(private context: BrowserContext) { }

    private readonly emailSubject = "//div[@class='subject mb-20']";
    private readonly counterEmail_SeeConterOfferButton = "//a[contains(text(),'See Counter Offer')]";
    private readonly totalAmount = "(//tbody//tr)[3]//span[2]";

    async getEmailSubjectForCurrentUser(emailName: string): Promise<string> {
        const tempMailPage = await this.gotoFirstEmail(emailName);
        const emailSubject = tempMailPage.locator(this.emailSubject);
        await emailSubject.waitFor({ state: 'visible', timeout: 10000 });
        const emailSubjectText = (await emailSubject.textContent())?.trim() || '';
        await tempMailPage.close();
        return emailSubjectText;
    }

    async verifySeeCounterOfferButton(emailName: string): Promise<string> {
        const tempMailPage = await this.gotoFirstEmail(emailName);
        Logger(`Navigated to temp mail page for email: ${emailName}`);
        const seeCounterOfferBtn = tempMailPage.locator(this.counterEmail_SeeConterOfferButton);
        const isBtnVisible = await seeCounterOfferBtn.isVisible({ timeout: 10000 });
        if (!isBtnVisible) {
            await tempMailPage.close();
            return '';
        }
        const [newPage] = await Promise.all([
            tempMailPage.context().waitForEvent('page'),
            seeCounterOfferBtn.click()
        ]);
        await newPage.waitForLoadState('domcontentloaded');
        // Wait for the title to be available
        await newPage.waitForFunction(() => document.title.length > 0, null, { timeout: 10000 });
        const title = await newPage.title();
        await newPage.close();
        await tempMailPage.close();
        return title;
    }

    async gotoFirstEmail(emailName: string): Promise<Page> {
        const tempMailPage = await this.context.newPage();
        await tempMailPage.goto('https://tempmail.plus/en/#!');
        await tempMailPage.waitForLoadState('domcontentloaded');
        const emailInput = tempMailPage.locator("//input[@id='pre_button']");
        await emailInput.waitFor({ state: 'visible', timeout: 5000 });
        await emailInput.fill('');
        await emailInput.type(emailName);
        await tempMailPage.locator("//button[@id='domain']").click();
        const domainOptions = tempMailPage.locator("//div[@class='dropdown-menu show']/button");
        const count = await domainOptions.count();
        for (let i = 0; i < count; i++) {
            const text = (await domainOptions.nth(i).textContent())?.trim();
            if (text?.toLowerCase() === "fexpost.com") {
                await domainOptions.nth(i).click();
                break;
            }
        }
        await tempMailPage.waitForTimeout(5000);
        const firstMail = tempMailPage.locator("//div[@class='mail'][1]");
        await firstMail.waitFor({ state: 'visible', timeout: 10000 });
        await firstMail.click();
        await tempMailPage.waitForTimeout(2000);
        return tempMailPage;
    }

    async getTotalAmountFromEmail(emailName: string): Promise<string> {
        const tempMailPage = await this.gotoFirstEmail(emailName);
        const totalAmountLocator = tempMailPage.locator(this.totalAmount);
        await totalAmountLocator.waitFor({ state: 'visible', timeout: 10000 });
        const totalAmountText = (await totalAmountLocator.textContent())?.trim() || '';
        await tempMailPage.close();
        return totalAmountText;
    }

}
```
