# Test Spec: LoginTests.spec.ts

- **Path**: `src\tests\AccountTests\LoginTests.spec.ts`
- **Category**: Test Spec
- **Lines**: 132
- **Size**: 5,515 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../BaseTest';
import { navTabs, userRole } from '../../utils/resources/enum';
import { Logger } from '../../utils/helpers/data_utils';
import { waitAndClick, waitAndFill, waitAndGetText } from '../../utils/helpers/commonMethods';


let base: BaseTest;
const emailInput = "//input[@placeholder='Email']";
const passwordInput = "//input[@placeholder='Password']";
const loginButton = "//button[text()='Login']";

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
})

test.describe("Login Page Tests @auctions-regression @pws-regression", () => {
    test('SPKB-2231 : Verify Displaying Match Figma Design', async () => {
        const headerText = await waitAndGetText(base['page'], "//span[contains(@class,'mainheadertext')]", 5000);
        const normalizedHeader = headerText.replace(/\s+/g, ' ').trim();
        expect(normalizedHeader).toBe('Premium Wholesale & Weekly Auctions');
        const copyrightText = await waitAndGetText(base['page'], "//span[contains(@class,'copyright-text')]", 5000);
        expect(copyrightText?.trim()).toBe('© 2026 ecoATM, LLC. All Rights Reserved.');
    });

    test('SPKB-2232 : Verify Madatory Fields on Login Page', async () => {
        const emailField = base['page'].locator(emailInput);
        await expect(emailField).toBeVisible();
        await emailField.fill('testuser@domain.com');
        expect(await emailField.inputValue()).toBe('testuser@domain.com');
        const passwordField = base['page'].locator(passwordInput);
        await expect(passwordField).toBeVisible();
        await passwordField.fill('testpassword');
        expect(await passwordField.inputValue()).toBe('testpassword');
    });

    test('SPKB-2233 : Verify External User Can Successfully Login', async () => {
        await base['loginPage'].loginAs(userRole.Nadia_GmailOne);
        const welcomeText = await waitAndGetText(base['page'], "//span[@class='mx-text mx-name-text1 bcs-header']", 10000);
        expect(welcomeText?.trim()).toBe('Welcome Nadia GmailOne !');
        await base['loginPage'].logoutFromWelcomePage();
    });

    test('SPKB-2234 : Verify Invalid Credential Handling', async () => {
        await waitAndFill(base['page'], emailInput, 'nadia.ecoatm@gmail.com');
        await waitAndFill(base['page'], passwordInput, 'InvalidPassword');
        await waitAndClick(base['page'], loginButton);
        const errorPasswordMessage = await waitAndGetText(base['page'], "//span[@class='mx-text mx-name-text7 login-validation-message pull-left']", 5000);
        expect(errorPasswordMessage?.trim()).toBe('Incorrect Password');
        await waitAndFill(base['page'], emailInput, 'InvalidEmail');
        await waitAndFill(base['page'], passwordInput, 'Test100%');
        await waitAndClick(base['page'], loginButton);
        const errorEmailMessage = await waitAndGetText(base['page'], "//span[@class='mx-text mx-name-text6 login-validation-message pull-left']", 5000);
        expect(errorEmailMessage?.trim()).toBe('No account with this email');
    });

    test('SPKB-2235 : Verify RememberMe Checkbox is Clickable', async () => {
        const rememberMeCheckbox = base['page'].locator("//input[@type='checkbox']");
        await expect(rememberMeCheckbox).toBeVisible();
        // Click the checkbox to check it
        await rememberMeCheckbox.click();
        expect(await rememberMeCheckbox.isChecked()).toBe(true);
        // Click again to uncheck
        await rememberMeCheckbox.click();
        expect(await rememberMeCheckbox.isChecked()).toBe(false);
    });

    test('SPKB-2236 : Verify Employee Can Login Via Microsoft SSO', async () => {
        await base['loginPage'].clickLink("EmployeeLogin")
        await base['page'].waitForTimeout(5000);
        await expect(base['page']).toHaveTitle("Sign in to your account");
    });

    test('SPKB-2237 : Verify Forgot-Password Link', async () => {
        await base['loginPage'].clickLink("ForgotPassword");
        await base['page'].waitForTimeout(5000);
        await expect(base['page']).toHaveTitle("ecoATM Direct - Forgot Password?");
    });

    test('SPKB-2238 : Verify Contact-Us Link', async () => {
        // Listen for new page (tab) event
        const [newPage] = await Promise.all([
            base['page'].context().waitForEvent('page'),
            base['loginPage'].clickLink("ContactUs")
        ]);
        await newPage.waitForLoadState();
        await expect(newPage).toHaveURL("https://www.ecoatmb2b.com/wholesale-devices#contact-us");
        await newPage.close();
    });

    test('SPKB-2238 : Verify Privacy-Policy Link', async () => {
        const [newPage] = await Promise.all([
            base['page'].context().waitForEvent('page'),
            base['loginPage'].clickLink("PrivacyPolicy")
        ]);
        await newPage.waitForLoadState();
        await expect(newPage).toHaveURL("https://www.ecoatm.com/pages/privacy-policy");
        await newPage.close();
    });
});

// test.describe("Forgot and Reset Password Tests ", () => {
//     test('SPKB- :  () => {

//     });

//     test('SPKB- : ', async () => {

//     });

//     test('SPKB- : ', async () => {

//     });

//     test('SPKB- : ', async () => {

//     });

//     test('SPKB- : ', async () => {

//     });

//     test('SPKB- : ', async () => {

//     });

//     test('SPKB- : ', async () => {

//     });
// });
```
