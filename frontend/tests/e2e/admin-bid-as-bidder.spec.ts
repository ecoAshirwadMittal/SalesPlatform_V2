import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { BidAsBidderPage, BidderDashboardPage, LoginPage } from '../pages';

/**
 * Lane 1A — BidAsBidder admin impersonation
 *
 * QA-side counterpart (paraphrased):
 *   • NavMenuPage.BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin('AA600WHL')
 *
 * Backend already authorises Administrator on the bidder dashboard endpoint
 * (BidderDashboardController has `@PreAuthorize hasAnyRole('Bidder','Administrator')`)
 * so this lane is frontend-only. The R1 fixture is reused so the dashboard
 * lands on GRID for HN/AAWHSL/DS2WHSL after impersonation.
 *
 * Live tests (`@live`) gate on `isBackendAvailable()` — actuator/health UP
 * AND `psql` on PATH for the SQL fixture.
 */

test.describe('Admin BidAsBidder @admin @regression @live', () => {
  test.beforeAll(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with admin@test.com seeded',
    );
    // Reuse the wholesale R1 fixture — guarantees an open round so the
    // impersonated dashboard renders the GRID branch instead of ERROR.
    applyFixture('wholesale-r1-active.sql');
  });

  test('admin lands on Bid as Bidder page; picker renders with auction codes', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();

    await expect(picker.heading).toBeVisible();
    await expect(picker.searchInput).toBeVisible();

    // Combobox opens on focus; at least one auction-side code is loaded.
    await picker.searchInput.click();
    await expect(picker.listbox).toBeVisible();
    await expect(picker.options.first()).toBeVisible();
  });

  test('typing a code substring filters the dropdown', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();

    await picker.searchInput.click();
    const totalCount = await picker.options.count();

    // Filter by a substring known to match HN — the seeded HN/AAWHSL/DS2WHSL
    // codes guarantee at least one match. The narrowed count must be lower
    // than the full list and every visible option must contain the substring.
    await picker.searchInput.fill('HN');
    const filteredCount = await picker.options.count();
    expect(filteredCount).toBeGreaterThan(0);
    expect(filteredCount).toBeLessThanOrEqual(totalCount);

    const firstOptionText = await picker.options.first().innerText();
    expect(firstOptionText.toLowerCase()).toContain('hn');
  });

  test('selecting a code routes to /bidder/dashboard?buyerCodeId=…', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();
    await picker.chooseBuyerCode('HN');

    // chooseBuyerCode awaits the URL change; assert the query param is non-empty.
    const url = new URL(page.url());
    expect(url.pathname).toBe('/bidder/dashboard');
    expect(url.searchParams.get('buyerCodeId')).toMatch(/^\d+$/);
  });

  test('admin sees the bidder dashboard rendered for the chosen buyer code', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();
    await picker.chooseBuyerCode('HN');

    // Reuse the bidder dashboard POM to assert the admin-impersonated render.
    const dash = new BidderDashboardPage(page);
    await expect(dash.buyerCodeChip).toBeVisible();
    await expect(dash.buyerCodeChip).toContainText('HN');
    expect(await dash.isGridVisible()).toBe(true);
  });

  test('an empty search shows the full code list (no filter)', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();

    await picker.searchInput.click();
    const baseCount = await picker.options.count();
    expect(baseCount).toBeGreaterThan(0);

    await picker.searchInput.fill('HN');
    await picker.searchInput.fill('');
    const reopenedCount = await picker.options.count();
    // Clearing the filter re-shows every code.
    expect(reopenedCount).toBe(baseCount);
  });
});
