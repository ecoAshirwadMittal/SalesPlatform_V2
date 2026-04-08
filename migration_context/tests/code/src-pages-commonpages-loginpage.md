# Page Object: LoginPage.ts

- **Path**: `src\pages\CommonPages\LoginPage.ts`
- **Category**: Page Object
- **Lines**: 234
- **Size**: 10,656 bytes
- **Members**: `class LoginPage`, `loginAs`, `login`, `handlePossibleSecondSignIn`, `clickLink`, `logout`, `switchToPWS`, `switchToWholesale`

## Source Code

```typescript
import { Page } from '@playwright/test';
import { userRole } from '../../utils/resources/enum';
import user_data from '../../utils/resources/user_data.json';
import { Logger, waitAndClick, waitAndFill } from '../../utils/helpers/commonMethods';
import { BaseTest } from '../../tests/BaseTest';


export class LoginPage {
    private readonly emailField = "//input[@placeholder='Email']";
    private readonly passwordField = "//input[@placeholder='Password']";
    private readonly loginButton = "//button[text()='Login']";
    private readonly employeeLoginLink = "//button[text()='Employee Login']";
    private readonly contactUsLink = "//button[text()='Contact Us']";
    private readonly forgotPasswordLink = "//a[text()='Forgot Password?']";
    private readonly privacyPolicyLink = "//a[text()='Privacy Policy']";

    private readonly mendixLoginUsernameField = "//input[@id='usernameInput']";
    private readonly mendixLoginPasswordField = "//input[@id='passwordInput']";
    private readonly mendixLoginSignInButton = "//button[@id='loginButton']";

    private readonly userAccountIcon = "//a[@class='mx-link mx-name-actionButton3 circlebase usericon_settings']";
    private readonly logoutButton = "//button[text()='Logout']";
    private readonly switchToPremiumButton = "//a[text()='Switch to Premium']";

    private readonly welcomePage_userAcctionIcon = "//a[@class='mx-link mx-name-actionButton6 circlebase usericon_settings pws-usericon-bgcolor']";
    private readonly welcomePage_logoutButton = "//a[text()='Logout']";

    private readonly pws_userAccountIcon = "//span[@class='mx-icon-lined mx-icon-user']";
    private readonly pws_logoutButton = "//a[text()='Logout']";
    private readonly pws_switchToAuctionButton = "//a[text()='Switch to Wholesale']";


    constructor(private page: Page) { }

    async loginAs(role: userRole) {
        const userMap: Record<string, { username: string; password: string }> = {
            ADMIN: user_data.ADMIN,
            ADMIN_One: user_data.ADMIN_One,
            ADMIN_Two: user_data.ADMIN_Two,
            ADMIN_Three: user_data.ADMIN_Three,
            ADMIN_Four: user_data.ADMIN_Four,
            ADMIN_Five: user_data.ADMIN_Five,
            ADMIN_Six: user_data.ADMIN_Six,
            ADMIN_Seven: user_data.ADMIN_Seven,
            ADMIN_Eight: user_data.ADMIN_Eight,
            ADMIN_Nine: user_data.ADMIN_Nine,
            ADMIN_Ten: user_data.ADMIN_Ten,
            User_AA155WHL: user_data.User_AA155WHL,
            User_AA156WHL: user_data.User_AA156WHL,
            User_AA157WHL: user_data.User_AA157WHL,
            User_AA158WHL: user_data.User_AA158WHL,
            User_AA159WHL: user_data.User_AA159WHL,
            User_AA160WHL: user_data.User_AA160WHL,
            User_AA161WHL: user_data.User_AA161WHL,
            User_AA600WHL: user_data.User_AA600WHL,
            User_AA601WHL: user_data.User_AA601WHL,
            User_AA602WHL: user_data.User_AA602WHL,
            User_AA603WHL: user_data.User_AA603WHL,
            User_AA604WHL: user_data.User_AA604WHL,
            User_AA605WHL: user_data.User_AA605WHL,
            User_AA700DW: user_data.User_AA700DW,
            User_AA701DW: user_data.User_AA701DW,
            User_AA702DW: user_data.User_AA702DW,
            User_AA703DW: user_data.User_AA703DW,
            User_AA704DW: user_data.User_AA704DW,
            User_AA705DW: user_data.User_AA705DW,
            User_AA800WHL: user_data.User_AA800WHL,
            User_AA801WHL: user_data.User_AA801WHL,
            User_AA802WHL: user_data.User_AA802WHL,
            User_AA803WHL: user_data.User_AA803WHL,
            User_AA804WHL: user_data.User_AA804WHL,
            User_AA805WHL: user_data.User_AA805WHL,
            User_AA900DW: user_data.User_AA900DW,
            User_AA901DW: user_data.User_AA901DW,
            User_AA902DW: user_data.User_AA902DW,
            User_AA903DW: user_data.User_AA903DW,
            User_AA904DW: user_data.User_AA904DW,
            User_AA905DW: user_data.User_AA905DW,
            User_AA1000WHL: user_data.User_AA1000WHL,
            User_AA1000DW: user_data.User_AA1000DW,
            User_AA1001WHL: user_data.User_AA1001WHL,
            User_AA1001DW: user_data.User_AA1001DW,
            User_AA1002WHL: user_data.User_AA1002WHL,
            User_AA1002DW: user_data.User_AA1002DW,
            User_AA1003WHL: user_data.User_AA1003WHL,
            User_AA1003DW: user_data.User_AA1003DW,
            User_AA1004WHL: user_data.User_AA1004WHL,
            User_AA1004DW: user_data.User_AA1004DW,
            User_AA1005WHL: user_data.User_AA1005WHL,
            User_AA1005DW: user_data.User_AA1005DW,     
            User_AA2000WHL: user_data.User_AA2000WHL,
            User_AA2000DW: user_data.User_AA2000DW,
            User_AA2003WHL: user_data.User_AA2003WHL,
            User_AA2003DW: user_data.User_AA2003DW,
            User_AA2004WHL: user_data.User_AA2004WHL,
            User_AA2004DW: user_data.User_AA2004DW,
            User_AA2005WHL: user_data.User_AA2005WHL,
            User_AA2005DW: user_data.User_AA2005DW,
            



            PWS_UserOne: user_data.PWS_UserOne,
            PWS_UserTwo: user_data.PWS_UserTwo,
            PWS_UserThree: user_data.PWS_UserThree,
            PWS_UserFour: user_data.PWS_UserFour,
            PWS_UserFive: user_data.PWS_UserFive,
            PWS_UserSix: user_data.PWS_UserSix,
            PWS_UserSeven: user_data.PWS_UserSeven,
            Nadia_GmailOne: user_data.Nadia_GmailOne,
        };
        const user = userMap[role];
        if (user) {
            await this.login(user.username, user.password);
        } else {
            throw new Error(`Unknown user role: ${role}`);
        }
    }

    async ensureUserLoggedIn(base: BaseTest, userRole: userRole): Promise<void> {
        try {
            const userIcon = this.page.locator(this.pws_userAccountIcon);
            if (await userIcon.isVisible({ timeout: 1500 }).catch(() => false)) {
                Logger("User already logged in — no action needed.");
                return;
            }
            const loginField = this.page.locator(this.emailField);
            const loginButton = this.page.locator(this.loginButton);
            let isLoginPage =
                (await loginField.isVisible({ timeout: 1500 }).catch(() => false)) ||
                (await loginButton.isVisible({ timeout: 1500 }).catch(() => false));
            if (!isLoginPage) {
                Logger("Login page not detected — waiting 5 seconds to recheck...");
                await this.page.waitForTimeout(5000);
                isLoginPage =
                    (await loginField.isVisible({ timeout: 1500 }).catch(() => false)) ||
                    (await loginButton.isVisible({ timeout: 1500 }).catch(() => false));
            }
            if (isLoginPage) {
                Logger("Login page detected — performing login.");
                await this.loginAs(userRole);
                return;
            }
            Logger("User Logged In — no action required.");
            return;
        } catch (error) {
            Logger("Error while checking login status — no login performed.");
            return;
        }
    }

    async login(username: string, password: string) {
        Logger(`Loggin into account`);
        await waitAndFill(this.page, this.emailField, username);
        await waitAndFill(this.page, this.passwordField, password);
        await waitAndClick(this.page, this.loginButton);
        await this.page.waitForTimeout(2000);
        await this.handlePossibleSecondSignIn(username, password);
    }

    private async handlePossibleSecondSignIn(username: string, password: string) {
        try {
            const locator = this.page.locator(this.mendixLoginUsernameField);
            const isVisible = await locator.waitFor({ state: 'visible', timeout: 5000 }).then(() => true).catch(() => false);
            console.log(`Is second sign-in visible: ${isVisible}`);
            if (isVisible) {
                await this.page.waitForTimeout(2000);
                await waitAndFill(this.page, this.mendixLoginUsernameField, username);
                await this.page.waitForTimeout(2000);
                await waitAndFill(this.page, this.mendixLoginPasswordField, password);
                await this.page.waitForTimeout(2000);
                await waitAndClick(this.page, this.mendixLoginSignInButton);
                await this.page.waitForTimeout(2000);
            }
        } catch (error) {
            Logger(`no secondary sign-in step detected.`);
        }
    }

    async clickLink(link: string | "EmployeeLogin" | "ContactUs" | "PrivacyPolicy" | "ForgotPassword") {
        switch (link) {
            case "EmployeeLogin":
                await waitAndClick(this.page, this.employeeLoginLink);
                break;
            case "ContactUs":
                await waitAndClick(this.page, this.contactUsLink);
                break;
            case "PrivacyPolicy":
                await waitAndClick(this.page, this.privacyPolicyLink);
                break;
            case "ForgotPassword":
                await waitAndClick(this.page, this.forgotPasswordLink);
                break;
            default:
                throw new Error(`Link ${link} not implemented`);
        }
    }

    async logout() {
        Logger(`Loggin out`);
        await waitAndClick(this.page, this.userAccountIcon);
        await waitAndClick(this.page, this.logoutButton);
        await this.page.waitForTimeout(2000);
    }

    async logoutFromPWS(): Promise<void> {
        await this.page.waitForTimeout(2000);
        await waitAndClick(this.page, this.pws_userAccountIcon);
        await waitAndClick(this.page, this.pws_logoutButton);
        await this.page.waitForTimeout(3000);
    }

    async logoutFromWelcomePage(): Promise<void> {
        await this.page.waitForTimeout(2000);
        await waitAndClick(this.page, this.welcomePage_userAcctionIcon);
        await waitAndClick(this.page, this.welcomePage_logoutButton);
        await this.page.waitForTimeout(3000);
    }

    async switchToPWS() {
        Logger(`Switching to PWS`);
        await waitAndClick(this.page, this.pws_userAccountIcon);
        await waitAndClick(this.page, this.pws_switchToAuctionButton);
        await this.page.waitForTimeout(2000);
    }

    async switchToWholesale() {
        Logger(`Switching to Wholesale`);
        await waitAndClick(this.page, this.pws_userAccountIcon);
        await waitAndClick(this.page, this.pws_switchToAuctionButton);
        await this.page.waitForTimeout(2000);
    }
}

```
