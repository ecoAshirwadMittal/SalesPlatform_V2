# Golden Rules — Sales Platform Playwright Test Generation

> This document codifies the architecture, patterns, and conventions observed in the
> `qa-playwright-salesplatform` codebase (71 files, 28 page objects,
> 23 test specs). Use this as the reference when generating new tests.

---

## Rule 1: Project Architecture

```
qa-playwright-salesplatform/
├── playwright.config.ts     # Global config (timeout: 600s, chromium, headless: false)
├── global-setup.ts          # AWS Secrets Manager → .env.secrets (runs once before all workers)
├── src/
│   ├── pages/               # Page Object Model (6 domains, 28 classes)
│   │   ├── Auction/         # Admin + Buyer pages for Wholesale Auctions
│   │   ├── CommonPages/     # LoginPage, NavMenuPage, WelcomePage, TempMailPage
│   │   ├── Deposco/         # WMS integration pages
│   │   └── PWS/             # Premium Wholesale Admin + Buyer pages
│   ├── tests/               # Test specs organized by feature
│   │   ├── BaseTest.ts      # Central fixture class
│   │   ├── AccountTests/
│   │   ├── PremiumWholesales/
│   │   ├── Wholesales/
│   │   └── SP_WMS_Deposco_Tests/
│   └── utils/
│       ├── clients/         # SnowflakeClient, SnowflakeSecretsClient
│       ├── helpers/         # commonMethods.ts, data_utils.ts
│       └── resources/       # enum.ts, user_data.json
└── specs/                   # Workflow spec markdown files
```

---

## Rule 2: BaseTest Fixture Pattern

Every test file creates a `BaseTest` instance that provides access to **all** page objects:

```typescript
import { test, expect } from '@playwright/test';
import { BaseTest } from '../../BaseTest';
import { userRole } from '../../../utils/resources/enum';

let base: BaseTest;
test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
});
```

**Rules:**
- `BaseTest` constructor takes a `Page` and instantiates ALL page objects
- Tests access page objects via bracket notation: `base['loginPage']`, `base['pws_CartPage']`
- `base.setup()` clears cookies and navigates to the base URL
- The `base` variable is shared across all tests in a `test.describe` block (serial execution)
- NEVER create page objects directly in tests — always go through `BaseTest`

---

## Rule 3: Page Object Model (POM) Conventions

### Structure
```typescript
import { Page, expect } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';
import { BaseTest } from '../../../tests/BaseTest';
import { userRole } from '../../../utils/resources/enum';
import { waitAndClick, waitAndFill } from '../../../utils/helpers/commonMethods';

export class PWS_ExamplePage {
    // Selectors as private readonly XPath strings
    private readonly someButton = "//button[text()='Submit']";
    private readonly inputField = "//input[@placeholder='Email']";

    constructor(private page: Page) {}

    // Public async methods for page interactions
    async doSomething(): Promise<void> {
        await waitAndClick(this.page, this.someButton);
        Logger("Clicked submit button.");
    }
}
```

### Rules:
- **Selectors**: Private readonly class properties, primarily **XPath** expressions
- **Constructor**: Takes `Page` as the only parameter (except `DeposcoAPI` which is API-only)
- **Methods**: All async, return typed Promises
- **Logging**: Use `Logger()` for all significant actions
- **Naming**: Prefix with domain: `PWS_`, `ACC_`, `AUC_` for page classes

---

## Rule 4: Selector Strategy

**Primary: XPath** — The codebase overwhelmingly uses XPath selectors:

```typescript
// ✅ Standard patterns used in this codebase
"//button[text()='Login']"
"//input[@placeholder='Email']"
"//span[contains(text(),'Your offer')]"
"//div[@role='gridcell']"
"//a[@class='mx-link mx-name-actionButton3 circlebase']"
"(//div[contains(@class,'widget-datagrid')])[2]"
```

**Secondary: Role-based** — Used sparingly as fallback:
```typescript
// Used when XPath causes strict mode violations (multiple matches)
this.page.getByRole('button', { name: 'Cart' });
```

**Rules:**
- Default to XPath selectors
- Use `contains()` for partial class matching on Mendix-generated classes
- Use index-based XPath `(//selector)[N]` when multiple grids exist on the same page
- Escape to `getByRole()` only when XPath causes strict mode violations

---

## Rule 5: Test Structure — Serial `test.describe` with Shared State

Tests are organized as **sequential workflows** within `test.describe` blocks:

```typescript
test.describe('Sales Rep Can Review and Accept the Offer @pws-regression', () => {
    let offerID: string;  // Shared state between tests

    test('Buyer Can Submit Offer', async () => {
        await base['loginPage'].loginAs(userRole.PWS_UserOne);
        offerID = await base['pws_CartPage'].submitOfferBelowListPrice(1, 500, 1);
        await base['loginPage'].logoutFromPWS();
    });

    test('Sales Rep Can Find the Offer', async () => {
        await base['loginPage'].loginAs(userRole.ADMIN_One);
        // ... uses offerID from previous test
    });
});
```

**Rules:**
- Tests within a `describe` block are **serial** and **stateful** — they share variables
- Each `test.describe` represents an end-to-end workflow scenario
- Login/logout happens within individual tests, not in beforeEach
- Tag tests with `@pws-regression`, `@wholesale-regression` etc. in the describe title

---

## Rule 6: Authentication Pattern

Role-based login via the `userRole` enum:

```typescript
// Login as a specific role
await base['loginPage'].loginAs(userRole.ADMIN_One);

// Logout from PWS context
await base['loginPage'].logoutFromPWS();

// Safety check: ensure user is logged in before acting
await base['loginPage'].ensureUserLoggedIn(base, userRole.PWS_UserOne);
```

**User role categories:**
- `ADMIN` / `ADMIN_One` through `ADMIN_Ten` — Sales Rep / Admin accounts
- `PWS_UserOne` through `PWS_UserSeven` — PWS Buyer accounts
- `User_AA###WHL` — Wholesale (non-DW) buyer accounts
- `User_AA###DW` — Direct Wholesale buyer accounts

**Rule:** Each `test.describe` workflow uses a **dedicated** buyer/admin pair to avoid state collisions between parallel suites.

---

## Rule 7: Interaction Helpers — Highlight Then Interact

All interactions go through `commonMethods.ts` helpers that **highlight** elements before acting:

```typescript
// ✅ ALWAYS use these helpers, not raw Playwright methods
await waitAndClick(this.page, this.someButton);      // Highlights → clicks
await waitAndFill(this.page, this.inputField, value); // Highlights → fills
await waitAndGetText(this.page, this.labelSelector);  // Highlights → reads text
```

**Behavior:**
1. Wait for element visibility (configurable timeout, default 5000ms)
2. Apply red box-shadow highlight (`0 0 0 4px #ff0000 inset`)
3. Wait 300ms for highlight to render
4. Perform the action (click/fill/getText)
5. Remove highlight

**Rule:** Raw `page.click()` or `page.fill()` should NOT be used in page objects. Always use the helpers.

---

## Rule 8: Data Grid Interaction Patterns

Grids use column-index-based cell access:

```typescript
private readonly COLUMN_INDEX = {
    List_Price: 9,
    Avl_Qty: 8,
    Offer_Price: 10,
    Offer_Qty: 11,
    Total: 12
};

// Cell editing pattern
await offerPriceCell.dblclick();
await this.page.keyboard.press('Control+A');
await this.page.keyboard.press('Backspace');
await this.page.keyboard.type(priceValue.toString());
await this.page.keyboard.press('Enter');
await this.page.waitForTimeout(500);
```

**Rules:**
- Define column indexes as a constant object in the page class
- Edit cells by: dblclick → select all → delete → type → Enter
- Always `waitForTimeout(500–2000)` after cell edits to let Mendix recalculate
- Parse numeric values with `replace("$", "").replace(",", "")`

---

## Rule 9: Retry & Resilience Patterns

### `ensureUserOnXxxPage()` — State recovery methods
```typescript
async ensureUserOnOfferQueuePage(base: BaseTest, userRole: userRole) {
    try {
        const isOfferHeaderVisible = await this.page
            .locator(this.offersHeader)
            .isVisible()
            .catch(() => false);
        if (isOfferHeaderVisible) {
            Logger("Already on OfferQueue page, no login needed.");
            return;
        }
    } catch (error) {
        Logger("Offer Queue header not displayed, will login and navigate.");
    }
    await base['loginPage'].loginAs(userRole);
    await base['pws_navMenuAsAdmin'].chooseNavMenu("Offer Review");
}
```

### Retry loops for data grid searches
```typescript
let attempts = 0;
while (!found && attempts < 3) {
    await this.page.waitForTimeout(5000);
    const rows = await this.page.locator(selector).all();
    // ... search logic
    attempts++;
}
```

**Rules:**
- Use `.catch(() => false)` on `.isVisible()` checks for safety
- Implement `ensureUserOnXxxPage()` for every major page object
- Use retry loops (max 3 attempts) for searches in dynamic grids
- Log descriptive messages at each step

---

## Rule 10: Naming & Organization Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Page Object class | Domain prefix + PascalCase | `PWS_ShopPage`, `ACC_BidDataPage` |
| Spec file | PascalCase + `.spec.ts` | `OfferFlowTests.spec.ts` |
| Selector property | camelCase, descriptive | `offerIDRows`, `submitButton` |
| Test title | Natural language, action-oriented | `'Buyer Can Submit Offer'` |
| Describe title | Scenario description + tag | `'Sales Rep Can Review @pws-regression'` |
| Helper functions | camelCase verbs | `waitAndClick`, `waitAndFill` |
| User roles | UPPER_SNAKE or PascalCase | `ADMIN_One`, `PWS_UserTwo` |

---

## Rule 11: Global Setup & Secrets Management

```typescript
// global-setup.ts pattern
async function globalSetup() {
    const secretsClient = new SnowflakeSecretsClient();
    const allSecrets = await secretsClient.getSnowflakeSecrets();
    fs.writeFileSync('.env.secrets', envContent, 'utf-8');
    // Also maps SP_SNOWFLAKE_QA_KEY → SNOWFLAKE_PRIVATE_KEY
}
```

**Rules:**
- Global setup runs once before all workers, NOT per-test
- Secrets are persisted to `.env.secrets` file for cross-worker access
- Each BaseTest loads `.env.secrets` via `dotenv.config({ path: '.env.secrets' })`
- AWS credentials are optional; setup gracefully skips if not configured

---

## Rule 12: Mendix-Specific Patterns

The Sales Platform is a **Mendix** application with specific UI patterns:

- **Double login**: Initial login may trigger a secondary Mendix sign-in
- **Class names**: Auto-generated with `mx-name-` prefix (e.g., `mx-name-actionButton3`)
- **Overlays**: Use `.mx-underlay` and `[role="dialog"]` selectors for modals
- **Timeouts**: Mendix recalculations require generous `waitForTimeout()` calls (1000-5000ms)
- **Escape key**: Press Escape as fallback to dismiss remaining overlays
- **Evaluate click**: Use `page.evaluate()` to bypass overlay interception when standard click fails

```typescript
// Standard overlay bypass pattern
await this.page.evaluate((selector) => {
    const btn = document.querySelector(selector) as HTMLElement;
    if (btn) btn.click();
}, this.confirmResetButton);
```
