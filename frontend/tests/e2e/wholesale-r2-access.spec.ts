import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { BidderDashboardPage, LoginPage } from '../pages';

/**
 * P4 ports from qa-playwright-salesplatform's
 * Wholesales/DataGrid_Round2.spec.ts. R2 transition + qualification gating.
 *
 * QA-side counterparts (paraphrased):
 *   • "R2: Verify Buyer Who Unqualified Cannot Access R2"
 *   • "R2: Verify Buyer Who Did Not Submit Bids in R1 Can Access R2"
 *   • "R2: Verify Buyer Can Submit Bids in R2"
 *
 * NOT ported here (backend gap — see docs/tasks/qa-wholesale-tests-port-plan.md §5):
 *   • "R2: Verify Buyer Cannot Lower Bid Price and Qty on Data Grid"
 *     The local backend's BidDataSubmissionService.validateAmountAndQuantity
 *     only checks bidAmount >= 0 and bidQuantity <= maximumQuantity. There
 *     is no comparison against last_valid_bid_amount / submitted_bid_amount
 *     to reject downward moves. Adding this is a backend feature item.
 *
 * Seed: `wholesale-r2-active.sql` closes R1 + opens R2 of the most recent
 * auction; HN+AAWHSL get Qualified+Included QBCs, DS2WHSL gets Not_Qualified
 * (included=false) so it lands in DOWNLOAD mode. Re-applies in beforeEach
 * so submit-mutating tests reset cleanly.
 *
 * NOTE: Running this spec leaves the dev DB with R1=Closed and R2=Started.
 * To revert to R1=Started, re-apply `wholesale-r1-active.sql`. The two
 * fixtures are siblings — neither is cumulative on top of the other.
 */

test.describe.serial('Wholesale R2 — buyer access @auction @regression @live', () => {
  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with bidder@buyerco.com seeded',
    );
    applyFixture('wholesale-r2-active.sql');
  });

  test('qualified buyer (HN) lands on R2 dashboard with grid + Round 2 label', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    await expect(dash.buyerCodeChip).toContainText('HN');
    await expect(dash.roundLabel(2)).toBeVisible();
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

    // EndOfBiddingPanel renders for DOWNLOAD mode. Heading text "Bidding has
    // ended." is split across two spans, so target it with a regex.
    await expect(page.getByRole('heading', { name: /Bidding.*has ended\./ })).toBeVisible();
    await expect(page.getByText(/Your bids from round 1 can be found below/)).toBeVisible();
    await expect(page.getByRole('button', { name: 'Download your Round 1 Bids' })).toBeVisible();

    // Grid must NOT be present in DOWNLOAD mode.
    expect(await dash.isGridVisible(2_000)).toBe(false);
  });

  test('qualified buyer can submit bids in R2', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    // Same placePrice pattern from P3 (avoids the .fill() controlled-input
    // race documented in wholesale-r1-access.spec.ts).
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
    await priceInput.pressSequentially('200');
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
