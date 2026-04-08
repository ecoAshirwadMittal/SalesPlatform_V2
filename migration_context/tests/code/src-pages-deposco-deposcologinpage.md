# Page Object: DeposcoLoginPage.ts

- **Path**: `src\pages\Deposco\DeposcoLoginPage.ts`
- **Category**: Page Object
- **Lines**: 96
- **Size**: 3,249 bytes
- **Members**: `class DeposcoLoginPage`

## Source Code

```typescript
import { Page, expect } from '@playwright/test';
import { Logger, waitAndClick, waitAndFill } from '../../utils/helpers/commonMethods';

/**
 * DeposcoLoginPage - Handles login operations for Deposco WMS
 * Reusable page object for all Deposco authentication needs
 */
export class DeposcoLoginPage {
    private readonly page: Page;

    // Deposco URLs
    private readonly DEPOSCO_LOGIN_URL = 'https://ecoatm-ua.deposco.com/deposco/login/home';

    // Login selectors
    private readonly selectors = {
        usernameField: "input[type='text']",
        usernameFieldById: "input#mat-input-0",
        passwordField: "input[type='password']",
        passwordFieldById: "input#mat-input-1",
        loginButton: "button:has-text('Log In')"
    };

    constructor(page: Page) {
        this.page = page;
    }

    /**
     * Navigate to Deposco login page
     */
    async navigateToLoginPage(): Promise<void> {
        Logger("Navigating to Deposco login page");
        await this.page.goto(this.DEPOSCO_LOGIN_URL);
        await this.page.waitForTimeout(3000);
    }

    /**
     * Verify login page is loaded
     */
    async verifyLoginPageLoaded(): Promise<void> {
        const usernameField = this.page.locator(this.selectors.usernameField).first();
        await expect(usernameField).toBeVisible({ timeout: 15000 });
        Logger("Deposco login page loaded successfully");
    }

    /**
     * Login to Deposco with provided credentials
     * @param username - Deposco username
     * @param password - Deposco password
     */
    async login(username: string, password: string): Promise<void> {
        Logger(`Logging into Deposco with username: ${username}`);

        // Enter username
        const usernameField = this.page.locator(this.selectors.usernameField).first();
        await usernameField.click();
        await usernameField.fill(username);
        Logger("Entered username");

        // Enter password
        const passwordField = this.page.locator(this.selectors.passwordField).first();
        await passwordField.click();
        await passwordField.fill(password);
        Logger("Entered password");

        // Click Login button
        const loginButton = this.page.locator(this.selectors.loginButton);
        await expect(loginButton).toBeVisible({ timeout: 5000 });
        await loginButton.click();
        Logger("Clicked Log In button");

        // Wait for login to complete
        await this.page.waitForTimeout(10000);
        Logger("Login complete - waiting for dashboard");
    }

    /**
     * Complete login flow: navigate to login page and login with credentials
     * @param username - Deposco username
     * @param password - Deposco password
     */
    async navigateAndLogin(username: string, password: string): Promise<void> {
        await this.navigateToLoginPage();
        await this.verifyLoginPageLoaded();
        await this.login(username, password);
    }

    /**
     * Check if user is logged in
     */
    async isLoggedIn(): Promise<boolean> {
        // Check for dashboard element or main menu
        const menuItem = this.page.locator(".menu-item-link").first();
        return await menuItem.isVisible({ timeout: 5000 }).catch(() => false);
    }
}

```
