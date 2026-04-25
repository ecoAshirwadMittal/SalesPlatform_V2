import { test, expect, type Locator, type Page } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { BidAsBidderPage, BidderDashboardPage, LoginPage } from '../pages';

/**
 * R3 admin port — finishes the two QA tests deferred during P5 because
 * the BidAsBidder admin surface didn't exist yet:
 *
 *   • DataGrid_Round3.spec.ts: "R2/3: Verify Sales Can Access R1"
 *   • DataGrid_Round3.spec.ts: "R2/3: Verify Sales Can Submit Bids on Behalf of Buyer"
 *
 * Plus the inverse buyer-side check from the same file:
 *   • "R2/3: Verify Buyer Can NOT Access R1"
 *
 * The QA fixture closes R1 + opens R3; once an admin is impersonating a
 * buyer code, the bidder dashboard's `landingRoute` finds R3 as the
 * latest Started round and renders the GRID for it. So "Sales can access
 * R1" really means "sales can act on behalf of any buyer post-R1-close
 * via BidAsBidder, and the dashboard renders the current active round".
 *
 * Live tests gate on backend availability + psql for fixture seeding.
 * Backend MUST have BidAsBidder + admin endpoints loaded.
 */

async function placePrice(page: Page, row: Locator, value: string): Promise<number> {
  const dash = new BidderDashboardPage(page);
  const priceInput = dash.priceInput(row);
  const ariaLabel = await priceInput.getAttribute('aria-label');
  expect(ariaLabel).toBeTruthy();
  const rowId = Number(ariaLabel!.match(/\d+/)![0]);

  const savePromise = page.waitForResponse(
    (r) =>
      r.url().includes(`/api/v1/bidder/bid-data/${rowId}`) &&
      r.request().method() === 'PUT' &&
      r.ok(),
  );
  await priceInput.click();
  await priceInput.press('ControlOrMeta+a');
  await priceInput.press('Delete');
  await priceInput.pressSequentially(value);
  await priceInput.press('Tab');
  await savePromise;
  return rowId;
}

test.describe.serial('Wholesale R3 — admin BidAsBidder + buyer access @auction @regression @live', () => {
  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with admin BidAsBidder endpoints loaded',
    );
    applyFixture('wholesale-r3-active.sql');
  });

  test('admin can access the bidder dashboard for any qualified code post-R1-close', async ({
    page,
  }) => {
    // R3 is the active Started round; R1 + R2 are Closed.
    await new LoginPage(page).loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();
    await picker.chooseBuyerCode('HN');

    const dash = new BidderDashboardPage(page);
    await expect(dash.buyerCodeChip).toContainText('HN');
    // Frontend renders Round ${round} regardless of backend `roundName`
    // ("Upsell Round" for R3) per the parity ADR. The visible label here
    // confirms the impersonated dashboard targets the latest Started round.
    await expect(dash.roundLabel(3)).toBeVisible();
    expect(await dash.isGridVisible()).toBe(true);
  });

  test('admin can submit bids on behalf of a buyer (BidAsBidder + place + submit)', async ({
    page,
  }) => {
    await new LoginPage(page).loginAs('ADMIN');

    const picker = new BidAsBidderPage(page);
    await picker.goto();
    await picker.chooseBuyerCode('HN');

    const dash = new BidderDashboardPage(page);
    await dash.isGridVisible();

    // Place + submit a $500 bid as the admin-impersonating-HN.
    await placePrice(page, dash.firstGridRow, '500');

    const submitPromise = page.waitForResponse(
      (r) => r.url().includes('/submit') && r.request().method() === 'POST' && r.ok(),
    );
    await dash.submitBidsButton.click();
    await submitPromise;

    await expect(dash.bidsSubmittedModal).toBeVisible();
  });

  test('plain bidder (no admin role) lands on R3 — never sees the closed R1 grid', async ({
    page,
  }) => {
    await new LoginPage(page).loginAs('BIDDER');

    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    // Round label = 3 — confirms landingRoute returned R3 (the latest
    // Started round), not R1 (Closed). Even though the buyer participated
    // in R1, that round's data is no longer reachable from the dashboard.
    await expect(dash.roundLabel(3)).toBeVisible();
    expect(await dash.isGridVisible()).toBe(true);
    // R1 label MUST NOT be visible.
    await expect(dash.roundLabel(1)).toHaveCount(0);
  });
});
