import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { BidderDashboardPage, LoginPage } from '../pages';

/**
 * P1 ports from qa-playwright-salesplatform's
 * Wholesales/DataGrid_Round1.spec.ts. Demonstrates the porting pattern
 * documented in docs/tasks/qa-wholesale-tests-port-plan.md §6.
 *
 * QA-side counterparts (paraphrased):
 *   • "R1: Reg Non-DW | Verify Buyer Can Access Auction Round 1 and View Inventory"
 *   • "R1: Reg Non-DW | Validate Buyer Code on Dashboard Display Correctly"
 *   • "R1: Reg Non-DW | Validate Minimum Bid Price Message Display Above Data Grid"
 *
 * Live tests (`@live`) gate on `isBackendAvailable()` — actuator/health UP
 * AND `psql` on PATH for the SQL fixture. The escape-hatch env var
 * `FORCE_LIVE_TESTS=1` bypasses the actuator check (useful when a
 * non-critical health indicator like mail is DOWN but auction APIs work).
 *
 * Test data:
 *   • Bidder = `bidder@buyerco.com` (seeded V15) with wholesale buyer
 *     codes HN, AAWHSL, DS2WHSL associated via `user_buyers`.
 *   • Active R1 auction guaranteed by `_fixtures/wholesale-r1-active.sql`
 *     applied in `beforeAll`. The fixture is idempotent — re-running
 *     resets bid_rounds and reopens the most recent R1.
 */

test.describe('Wholesale R1 — buyer access @auction @regression @live', () => {
  test.beforeAll(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with bidder@buyerco.com seeded',
    );
    // Idempotent SQL — opens the latest R1 + ensures HN/AAWHSL/DS2WHSL are
    // Qualified+Included. Safe to re-run between tests.
    applyFixture('wholesale-r1-active.sql');
  });

  test('bidder can pick auction code HN and lands on the dashboard with chip', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');

    // bidder@buyerco.com holds multiple buyer codes → the post-login route
    // is the picker, not the dashboard. Confirm before proceeding.
    await expect(page).toHaveURL(/\/buyer-select/);

    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    // Dashboard chrome rendered, buyer-code chip shows the picked code.
    await expect(dash.buyerCodeChip).toBeVisible();
    await expect(dash.buyerCodeChip).toContainText('HN');
    await expect(dash.buyerCodeChip).toContainText('Nadia Boonnayanont');
  });

  test('Switch Buyer Code link returns the bidder to the picker', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    await dash.switchBuyerCodeLink.click();
    await expect(page).toHaveURL(/\/buyer-select/);

    // Picker shows the auction-side pills again.
    await expect(
      page.getByRole('button', { name: /^HN\b/ }).first(),
    ).toBeVisible();
  });

  test('bidder can pick a different auction code and the chip updates', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);

    await dash.pickAuctionCode('HN');
    await expect(dash.buyerCodeChip).toContainText('HN');

    await dash.switchBuyerCodeLink.click();
    await expect(page).toHaveURL(/\/buyer-select/);
    await dash.pickAuctionCode('AAWHSL');
    await expect(dash.buyerCodeChip).toContainText('AAWHSL');
    await expect(dash.buyerCodeChip).toContainText('Andrei Aliasiuk');
  });

  test('dashboard renders min-bid label and grid for HN', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    await expect(dash.minimumBidLabel).toContainText('Minimum starting bid');
    expect(await dash.isGridVisible()).toBe(true);
  });

  test('dashboard header shows auction title + Round 1 label', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');

    await expect(dash.auctionTitle).toBeVisible();
    await expect(dash.auctionTitle).toContainText(/^Auction \d+ \/ Wk\d+$/);
    await expect(dash.roundLabel(1)).toBeVisible();
  });

  test('grid footer shows the Currently-showing count and matches row total', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    await expect(dash.gridFooter).toBeVisible();
    // QA seed (week 544) gives HN ≥ 100 inventory rows. Loose lower bound
    // keeps the test stable across re-seeds while still catching empty-grid
    // regressions.
    const text = await dash.gridFooter.innerText();
    const match = text.match(/Currently showing ([\d,]+) of ([\d,]+)/);
    expect(match, 'footer should match the Currently-showing pattern').not.toBeNull();
    const matchCount = Number(match![1].replace(/,/g, ''));
    const total = Number(match![2].replace(/,/g, ''));
    expect(total).toBeGreaterThan(100);
    expect(matchCount).toBe(total);
  });

  test('sort by Brand cycles aria-sort: none → ascending → descending', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    const brandHeader = dash.headerCell('Brand');
    // Default state — aria-sort is "none" (no active sort yet).
    await expect(brandHeader).toHaveAttribute('aria-sort', 'none');

    await dash.sortButton('Brand').click();
    await expect(brandHeader).toHaveAttribute('aria-sort', 'ascending');

    await dash.sortButton('Brand').click();
    await expect(brandHeader).toHaveAttribute('aria-sort', 'descending');
  });

  test('filter by Brand narrows the grid to fewer rows', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    const before = (await dash.gridFooter.innerText()).match(
      /Currently showing ([\d,]+) of/,
    );
    expect(before, 'footer should be present before filter').not.toBeNull();
    const beforeCount = Number(before![1].replace(/,/g, ''));

    // Read the brand value out of the first visible body row so the filter
    // is guaranteed to match at least one row but exclude rows with other
    // brands. Column index: ecoid(0), brand(1).
    const firstRow = dash.gridRows.first();
    const firstBrand = (await firstRow.locator('td').nth(1).innerText()).trim();
    expect(firstBrand).not.toBe('');

    await dash.filterInput('brand').fill(firstBrand);

    // Footer is aria-live polite; waitFor handles the React render flush.
    await expect
      .poll(async () => {
        const m = (await dash.gridFooter.innerText()).match(
          /Currently showing ([\d,]+) of/,
        );
        return m ? Number(m[1].replace(/,/g, '')) : null;
      })
      .toBeLessThan(beforeCount);
  });
});

// P3 — bid placement + submission. Mutates state, so beforeEach re-applies
// the seed (the lighter UPDATE-only fixture is fast enough — no
// DELETE/CASCADE penalty per test).
test.describe.serial('Wholesale R1 — bid placement @auction @regression @live', () => {
  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080',
    );
    applyFixture('wholesale-r1-active.sql');
  });

  test('Submit Bids with no bids placed shows the empty-state modal', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    // Click Submit immediately — no bidAmount > 0 anywhere, so the client-side
    // guard should fire and show the empty-state modal without POSTing.
    await dash.submitBidsButton.click();
    await expect(dash.noBidsModal).toBeVisible();
  });

  /**
   * Helper — types a price value into the cell using the same sequence the
   * user would: focus (click) → clear → type → blur (Tab). Avoids the
   * Playwright `.fill()` quirk where the focus event's setDisplay("0.00")
   * race with handleChange's setDisplay("42.50") can leave the autosave
   * payload at the cleared 0.
   *
   * Returns when the autosave PUT for this row has resolved.
   */
  async function placePrice(page: import('@playwright/test').Page, row: import('@playwright/test').Locator, value: string): Promise<number> {
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
    // Select-all then type the new value, finishing with Tab to blur and
    // commit. The blur fires the autosave's flush; the 500ms debounce kicks
    // in immediately because Tab + blur = no further keystrokes.
    await priceInput.press('ControlOrMeta+a');
    await priceInput.press('Delete');
    await priceInput.pressSequentially(value);
    await priceInput.press('Tab');
    await savePromise;
    return rowId;
  }

  test('placing a price + clicking Submit shows the success modal', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    await placePrice(page, dash.firstGridRow, '100');

    const submitPromise = page.waitForResponse(
      (r) => r.url().includes('/submit') && r.request().method() === 'POST' && r.ok(),
    );
    await dash.submitBidsButton.click();
    await submitPromise;

    await expect(dash.bidsSubmittedModal).toBeVisible();
  });

  test('submitted bid amount persists after page reload', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    const rowId = await placePrice(page, dash.firstGridRow, '42.50');

    const submitPromise = page.waitForResponse(
      (r) => r.url().includes('/submit') && r.request().method() === 'POST' && r.ok(),
    );
    await dash.submitBidsButton.click();
    await submitPromise;
    await expect(dash.bidsSubmittedModal).toBeVisible();

    await page.reload();
    await dash.isGridVisible();

    // PriceCell formats stored bid_amount with formatDollar → "$ 42.50".
    // Backend's submit copies bid_amount → submitted_bid_amount but does
    // NOT zero bid_amount, so the value persists across reload.
    const reloadedPrice = page.locator(`input[aria-label="Price for row ${rowId}"]`);
    await expect(reloadedPrice).toHaveValue('$ 42.50');
  });

  // Port of qa-playwright-salesplatform's
  // "R2: Verify Buyer Cannot Lower Bid Price and Qty on Data Grid".
  // Anchored in R1 here because the lowering protection applies to any
  // previously submitted bid, regardless of round — and R1 setup is faster.
  // Backend logic: BidDataSubmissionService.validateNotLoweringSubmittedBid
  // throws BidDataSubmissionException("BID_LOWERED") → 409 via
  // GlobalExceptionHandler.
  test('lowering a previously submitted bid is rejected with 409 and the value persists', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    // Submit a baseline bid of $1000.
    const rowId = await placePrice(page, dash.firstGridRow, '1000');
    const submitPromise = page.waitForResponse(
      (r) => r.url().includes('/submit') && r.request().method() === 'POST' && r.ok(),
    );
    await dash.submitBidsButton.click();
    await submitPromise;
    await expect(dash.bidsSubmittedModal).toBeVisible();
    await page.getByRole('button', { name: 'Close' }).first().click();

    // Now try to lower it to $50. Expect a 409, NOT a 200, on the autosave PUT.
    const lowerAttempt = page.waitForResponse(
      (r) =>
        r.url().includes(`/api/v1/bidder/bid-data/${rowId}`) &&
        r.request().method() === 'PUT',
    );
    const priceInput = dash.priceInput(dash.firstGridRow);
    await priceInput.click();
    await priceInput.press('ControlOrMeta+a');
    await priceInput.press('Delete');
    await priceInput.pressSequentially('50');
    await priceInput.press('Tab');
    const resp = await lowerAttempt;
    expect(resp.status()).toBe(409);

    // BidLoweredError is mapped to a user-facing toast in BidderDashboardClient.
    await expect(
      page.getByText('You cannot lower a previously submitted bid.'),
    ).toBeVisible();

    // After reload, the persisted bid_amount is still 1000 — the 409 above
    // means the backend did not accept the lower value.
    await page.reload();
    await dash.isGridVisible();
    const reloadedPrice = page.locator(`input[aria-label="Price for row ${rowId}"]`);
    await expect(reloadedPrice).toHaveValue('$ 1000.00');
  });
});
