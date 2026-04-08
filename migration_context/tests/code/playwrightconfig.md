# Config: playwright.config.ts

- **Path**: `playwright.config.ts`
- **Category**: Config
- **Lines**: 50
- **Size**: 1,336 bytes

## Source Code

```typescript
import 'dotenv/config';
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  globalSetup: './global-setup.ts',
  testIgnore: [
    '**/RoundOneFunctionalTests.spec.ts',
    '**/Flow1_AllBuyer_FullInventory_SptON.spec.ts',
    '**/Flow4_BuyerWithR1Bids_FullInventory_SptON.spec.ts',
    '**/Flow5_BuyerWithR1Bids_InventoryR1Bids_SptON.spec.ts'  ,
    '**/DataGrid_Round2.spec.ts',
    '**/DataGrid_Round3.spec.ts',
    '**/DataGridAllRoundsTests.spec.ts'
  ],
  testDir: './src/tests',
  timeout: 600000,
  retries: 0,
  use: {
    browserName: 'chromium',
    headless: false,
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
    trace: 'on-first-retry',
    //viewport: { width: 1280, height: 800 },
    viewport: null,
    actionTimeout: 10000,
    launchOptions: {
      args: ['--start-maximized']
    }
  },
  reporter: [['allure-playwright']],
  /* Configure projects for sequential execution */
  // projects: [
  //   {
  //     name: 'Round1',
  //     testMatch: '**/DataGrid_Round1.spec.ts',
  //   },
  //   {
  //     name: 'Round2',
  //     testMatch: '**/DataGrid_Round2.spec.ts',
  //     dependencies: ['Round1'],
  //   },
  //   {
  //     name: 'Round3',
  //     testMatch: '**/DataGrid_Round3.spec.ts',
  //     dependencies: ['Round2'],
  //   },
  // ],
});

```
