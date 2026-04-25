import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { LoginPage, R2CriteriaPage } from '../pages';

/**
 * Lane 4 — R2 Selection Criteria admin page (P8 admin-surfaces master plan).
 *
 * Ports the load-bearing assertions from QA POM
 * {@code ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings}: the page
 * loads with defaults when no row exists, persists the three settings on
 * Save, and round-trips the saved values across reload.
 *
 * Cascade tests (criteria → R1 closes → buyers eligible per criteria) are
 * DEFERRED — they require Lane 3B (Qualified Buyer Codes admin) for the
 * end-to-end flow. See docs/tasks/p8-admin-surfaces-plan.md §3 Lane 4.
 *
 * Live tests (`@live`) gate on `isBackendAvailable()` so CI without a
 * running Spring Boot skips cleanly. The escape-hatch env var
 * `FORCE_LIVE_TESTS=1` bypasses the actuator check (parity with the
 * wholesale-* spec convention).
 */
test.describe('Admin — R2 Selection Criteria @admin @regression @live', () => {
  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with admin@test.com seeded',
    );
    // Wipe round 2 row so each test starts from the "no persisted row" state.
    applyFixture('r2-criteria-seed.sql');
  });

  test('loads defaults when no row exists for round 2', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const r2 = new R2CriteriaPage(page);
    await r2.goto();

    // The defaults notice is only rendered when the GET returned 404.
    await expect(r2.defaultsNotice).toBeVisible();

    // Defaults: Bid Buyers Only + Inventory With Bids + override OFF.
    await expect(r2.qualificationBidBuyersOnly).toBeChecked();
    await expect(r2.inventoryWithBids).toBeChecked();
    await expect(r2.stbOverrideToggle).not.toBeChecked();
  });

  test('selecting values + Save persists; reload shows the saved values', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const r2 = new R2CriteriaPage(page);
    await r2.goto();

    // Pick non-default settings on every control so we can prove they round-trip.
    await r2.setSettings('All_Buyers', 'Full_Inventory', true);
    const { status } = await r2.saveAndWait();
    expect(status).toBe(200);

    await expect(r2.successBanner).toBeVisible();
    // After save the defaults notice is gone — row is now persisted.
    await expect(r2.defaultsNotice).toBeHidden();

    // Reload + verify persistence.
    await page.reload();
    await r2.root.waitFor({ state: 'visible' });

    await expect(r2.qualificationAllBuyers).toBeChecked();
    await expect(r2.inventoryFull).toBeChecked();
    await expect(r2.stbOverrideToggle).toBeChecked();
    await expect(r2.defaultsNotice).toBeHidden();
  });

  test('switching values + Save updates correctly (in-place upsert)', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const r2 = new R2CriteriaPage(page);
    await r2.goto();

    // First save: All_Buyers + Full_Inventory + override ON.
    await r2.setSettings('All_Buyers', 'Full_Inventory', true);
    const first = await r2.saveAndWait();
    expect(first.status).toBe(200);

    // Second save: flip everything back. This exercises the UPDATE branch
    // (row exists from the first save).
    await r2.setSettings('Bid_Buyers_Only', 'Inventory_With_Bids', false);
    const second = await r2.saveAndWait();
    expect(second.status).toBe(200);

    await page.reload();
    await r2.root.waitFor({ state: 'visible' });

    await expect(r2.qualificationBidBuyersOnly).toBeChecked();
    await expect(r2.inventoryWithBids).toBeChecked();
    await expect(r2.stbOverrideToggle).not.toBeChecked();
  });
});
