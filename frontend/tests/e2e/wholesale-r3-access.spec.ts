import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { BidderDashboardPage, LoginPage } from '../pages';

/**
 * P5 ports from qa-playwright-salesplatform's
 * Wholesales/DataGrid_Round3.spec.ts. R3 (Upsell Round) buyer-side access
 * + qualification gating.
 *
 * QA-side counterparts:
 *   • DataGrid_Round3.spec.ts has 3 tests; only the buyer-facing scenario
 *     ("R2/3: Verify Buyer Can NOT Access R1") is portable today. The
 *     other two ("Sales Can Access R1", "Sales Can Submit on Behalf of
 *     Buyer") require the BidAsBidder admin surface, which the local app
 *     doesn't yet have — see docs/tasks/qa-wholesale-tests-port-plan.md
 *     §5 item #10.
 *
 * Per the 2026-04-22 parity ADR (Q5 in `wholesale-buyer-parity-plan.md`),
 * the dashboard displays `Round ${round}` regardless of the backend's
 * `roundName` — so R3 renders "Round 3", not "Upsell Round".
 *
 * Seed: `wholesale-r3-active.sql` closes R1+R2 + opens R3 of the most
 * recent auction, with the same QBC layout as the R2 fixture (HN+AAWHSL
 * qualified, DS2WHSL excluded).
 */

test.describe.serial('Wholesale R3 — buyer access @auction @regression @live', () => {
  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with bidder@buyerco.com seeded',
    );
    applyFixture('wholesale-r3-active.sql');
  });

  test('qualified buyer (HN) lands on R3 dashboard with grid + Round 3 label', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    await expect(dash.buyerCodeChip).toContainText('HN');
    // Frontend ignores the backend's `roundName: "Upsell Round"` and
    // renders `Round ${round}` — verify the parity ADR is honoured.
    await expect(dash.roundLabel(3)).toBeVisible();
    expect(await dash.isGridVisible()).toBe(true);
    await expect(dash.minimumBidLabel).toContainText('Minimum starting bid');
  });

  test('unqualified buyer (DS2WHSL) sees DOWNLOAD-mode end-of-bidding panel', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('DS2WHSL');

    await expect(page.getByRole('heading', { name: /Bidding.*has ended\./ })).toBeVisible();
    await expect(page.getByText(/Your bids from round 1 can be found below/)).toBeVisible();
    await expect(page.getByRole('button', { name: 'Download your Round 1 Bids' })).toBeVisible();

    expect(await dash.isGridVisible(2_000)).toBe(false);
  });

  test('qualified buyer can submit bids in R3', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    const row = dash.firstGridRow;
    const priceInput = dash.priceInput(row);
    const ariaLabel = await priceInput.getAttribute('aria-label');
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
    await priceInput.pressSequentially('300');
    await priceInput.press('Tab');
    await savePromise;

    const submitPromise = page.waitForResponse(
      (r) => r.url().includes('/submit') && r.request().method() === 'POST' && r.ok(),
    );
    await dash.submitBidsButton.click();
    await submitPromise;

    await expect(dash.bidsSubmittedModal).toBeVisible();
  });
});
