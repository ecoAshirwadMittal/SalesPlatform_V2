import { test, expect, type Page } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture, queryScalar } from './_helpers/seedSql';
import {
  BidAsBidderPage,
  BidDataAdminPage,
  BidderDashboardPage,
  LoginPage,
  QualifiedBuyerCodesPage,
} from '../pages';

/**
 * Cascade tests — exercise BOTH an admin surface (P8 lanes) AND the
 * downstream buyer-side reaction in a single end-to-end flow. These
 * prove the system works as an integrated whole, not just per-layer.
 *
 * Two scenarios captured here:
 *
 *   A. R2 unqualify cascade
 *      Admin unchecks DS2WHSL's Included flag on the QBC page → the
 *      backend flips qualification_type to Manual, included=false →
 *      bidder logged in as DS2WHSL lands on the DOWNLOAD-mode panel
 *      (not GRID), even though R2 is still active.
 *
 *   B. Bid removal cascade
 *      Bidder places + submits $100 on a row → admin removes the row
 *      via the Bid Data admin page → bidder reloads → that row's
 *      bidAmount is back to 0 (or row is gone, depending on
 *      soft-delete semantics).
 *
 * Both gated on a live backend + psql on PATH (for fixture seeding +
 * scalar lookups). Skip silently when either is missing.
 *
 * Live backend MUST have the P8 admin endpoints loaded — restart your
 * Spring Boot if you merged P8 today and haven't bounced yet, otherwise
 * the admin requests return 404 and the cascade tests will fail with
 * confusing toasts about missing endpoints.
 */

async function relogin(page: Page, role: 'ADMIN' | 'BIDDER'): Promise<void> {
  await page.context().clearCookies();
  await page.evaluate(() => {
    try {
      localStorage.clear();
    } catch {
      // localStorage may not be available pre-navigation; safe to ignore.
    }
  });
  await new LoginPage(page).loginAs(role);
}

test.describe.serial('Wholesale cascades — admin action drives buyer behavior @auction @regression @live', () => {
  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with admin endpoints loaded',
    );
  });

  test('R2 unqualify cascade: admin unchecks DS2WHSL → bidder sees DOWNLOAD', async ({
    page,
  }) => {
    // Setup: R2 active. Fixture default is DS2WHSL.included=false, but this
    // test wants it qualified so the admin can unqualify it. Apply the
    // fixture, then flip DS2WHSL's QBC to Qualified+Included via inline SQL.
    applyFixture('wholesale-r2-active.sql');
    const r2SaId = queryScalar(
      "SELECT id FROM auctions.scheduling_auctions WHERE round = 2 ORDER BY auction_id DESC LIMIT 1",
    );
    const ds2WhslBcId = queryScalar(
      "SELECT id FROM buyer_mgmt.buyer_codes WHERE code = 'DS2WHSL'",
    );
    const ds2WhslQbcId = queryScalar(
      `UPDATE buyer_mgmt.qualified_buyer_codes
         SET included = true, qualification_type = 'Qualified', submitted = false
       WHERE scheduling_auction_id = ${r2SaId}
         AND buyer_code_id = ${ds2WhslBcId}
       RETURNING id`,
    );

    // Admin: open QBC page, filter to R2 SA, uncheck DS2WHSL.
    await new LoginPage(page).loginAs('ADMIN');
    const qbc = new QualifiedBuyerCodesPage(page);
    await qbc.goto();
    await qbc.schedulingAuctionFilter.fill(r2SaId);
    await qbc.applyAndWait();

    // Verify the row is rendered with included=true before flipping.
    await expect(qbc.includedCheckbox(Number(ds2WhslQbcId))).toBeChecked();

    const patchResponse = await qbc.toggleIncludedAndWait(Number(ds2WhslQbcId));
    expect(patchResponse.status()).toBe(200);

    // Qualification type cell should now read "Manual" (PATCH side-effect).
    await expect(qbc.qualificationTypeCell(Number(ds2WhslQbcId))).toHaveText(
      /Manual/,
    );

    // Switch to bidder. DS2WHSL should now route to DOWNLOAD because the
    // landingRoute's `activeQbc.isIncluded()` check fails.
    await relogin(page, 'BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('DS2WHSL');

    await expect(
      page.getByRole('heading', { name: /Bidding.*has ended\./ }),
    ).toBeVisible();
    expect(await dash.isGridVisible(2_000)).toBe(false);
  });

  test('Bid removal cascade: admin removes a submitted bid → bidder reload shows the row reset', async ({
    page,
    request,
  }) => {
    applyFixture('wholesale-r1-active.sql');

    // Pre-stage a submitted $100 bid on HN's first available bid_data
    // row, via direct API calls — much faster than the UI flow and removes
    // any race against the autosave debounce.
    const loginRes = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: {
        email: 'bidder@buyerco.com',
        password: 'Bidder123!',
        rememberMe: false,
      },
    });
    expect(loginRes.ok()).toBeTruthy();
    const setCookie = loginRes.headers()['set-cookie'];
    const authToken = /auth_token=([^;]+)/.exec(setCookie)![1];

    const hnBcId = Number(
      queryScalar("SELECT id FROM buyer_mgmt.buyer_codes WHERE code = 'HN'"),
    );

    // Land on the dashboard once via API to lazy-create bid_data rows for
    // HN's R1 bid_round (the GET trigger is what fires
    // BidDataCreationService.ensureRowsExist).
    await request.get(
      `http://localhost:8080/api/v1/bidder/dashboard?buyerCodeId=${hnBcId}`,
      { headers: { Cookie: `auth_token=${authToken}` } },
    );

    const r1SaId = Number(
      queryScalar(
        "SELECT id FROM auctions.scheduling_auctions WHERE round = 1 ORDER BY auction_id DESC LIMIT 1",
      ),
    );
    const bidRoundId = Number(
      queryScalar(
        `SELECT id FROM auctions.bid_rounds
          WHERE scheduling_auction_id = ${r1SaId}
            AND buyer_code_id = ${hnBcId}`,
      ),
    );
    const firstRowId = Number(
      queryScalar(
        `SELECT id FROM auctions.bid_data
          WHERE bid_round_id = ${bidRoundId}
          ORDER BY ecoid, merged_grade
          LIMIT 1`,
      ),
    );

    // Place + submit a $100 bid on that row.
    const putRes = await request.put(
      `http://localhost:8080/api/v1/bidder/bid-data/${firstRowId}`,
      {
        headers: {
          Cookie: `auth_token=${authToken}`,
          'Content-Type': 'application/json',
        },
        data: { bidQuantity: 1, bidAmount: 100 },
      },
    );
    expect(putRes.ok()).toBeTruthy();

    const submitRes = await request.post(
      `http://localhost:8080/api/v1/bidder/bid-rounds/${bidRoundId}/submit?buyerCodeId=${hnBcId}`,
      { headers: { Cookie: `auth_token=${authToken}` } },
    );
    expect(submitRes.ok()).toBeTruthy();

    // Admin: open Bid Data page, filter to (bidRoundId, hnBcId, submittedOnly),
    // click Remove on the only matching row.
    await new LoginPage(page).loginAs('ADMIN');
    const bda = new BidDataAdminPage(page);
    await bda.goto();
    await bda.roundFilter.fill(String(bidRoundId));
    await bda.buyerCodeFilter.fill(String(hnBcId));
    await bda.submittedOnlyToggle.check();
    await bda.applyAndWait();

    // The submitted-only filter should narrow to exactly the one we placed.
    await expect(bda.gridRows).toHaveCount(1);

    const deletePromise = page.waitForResponse(
      (r) =>
        r.url().includes(`/api/v1/admin/bid-data/${firstRowId}`) &&
        r.request().method() === 'DELETE' &&
        r.ok(),
    );
    page.on('dialog', (dlg) => dlg.accept().catch(() => {}));
    await bda.removeButtonForRow(firstRowId).click();
    await deletePromise;

    // Bidder reload — the previously submitted row is no longer surfaced
    // with its $100 value (either soft-deleted out of the grid response,
    // or its bid_amount is reset). Either way the input must NOT show
    // "$ 100.00" anymore.
    await relogin(page, 'BIDDER');
    const dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('HN');
    await dash.isGridVisible();

    const rowInput = page.locator(`input[aria-label="Price for row ${firstRowId}"]`);
    // If soft-delete excludes the row from the grid, the input simply
    // isn't present (count = 0). If hard-delete, same thing. If the row
    // is kept but bid_amount is zeroed, the value is "$ 0.00".
    const count = await rowInput.count();
    if (count > 0) {
      await expect(rowInput).toHaveValue('$ 0.00');
    } else {
      // Row was excluded from the grid response — also a valid removal.
      expect(count).toBe(0);
    }
  });

  test('R2 qualify cascade: admin checks an excluded buyer → that buyer gains GRID access', async ({
    page,
  }) => {
    // Setup: R2 active. Fixture default DS2WHSL.included=false (correct for
    // this test — admin re-includes it below).
    applyFixture('wholesale-r2-active.sql');
    const r2SaId = queryScalar(
      "SELECT id FROM auctions.scheduling_auctions WHERE round = 2 ORDER BY auction_id DESC LIMIT 1",
    );
    const ds2WhslBcId = queryScalar(
      "SELECT id FROM buyer_mgmt.buyer_codes WHERE code = 'DS2WHSL'",
    );
    const ds2WhslQbcId = queryScalar(
      `SELECT id FROM buyer_mgmt.qualified_buyer_codes
        WHERE scheduling_auction_id = ${r2SaId}
          AND buyer_code_id = ${ds2WhslBcId}`,
    );

    // Sanity: bidder hits DOWNLOAD before admin acts.
    await new LoginPage(page).loginAs('BIDDER');
    let dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('DS2WHSL');
    await expect(
      page.getByRole('heading', { name: /Bidding.*has ended\./ }),
    ).toBeVisible();

    // Admin: open QBC page, check DS2WHSL.
    await relogin(page, 'ADMIN');
    const qbc = new QualifiedBuyerCodesPage(page);
    await qbc.goto();
    await qbc.schedulingAuctionFilter.fill(r2SaId);
    await qbc.applyAndWait();

    await expect(qbc.includedCheckbox(Number(ds2WhslQbcId))).not.toBeChecked();
    const patchResponse = await qbc.toggleIncludedAndWait(Number(ds2WhslQbcId));
    expect(patchResponse.status()).toBe(200);
    await expect(qbc.qualificationTypeCell(Number(ds2WhslQbcId))).toHaveText(
      /Manual/,
    );

    // Admin must also create a bid_round for the now-qualified DS2WHSL —
    // landingRoute returns ERROR_BIDROUND_MISSING otherwise. The QBC
    // PATCH alone doesn't create the bid_round (Mendix had a separate
    // job; we simulate it here via direct insert).
    const r2WeekId = queryScalar(
      `SELECT a.week_id::text FROM auctions.auctions a
        JOIN auctions.scheduling_auctions sa ON sa.auction_id = a.id
        WHERE sa.id = ${r2SaId}`,
    );
    queryScalar(
      `WITH ins AS (
         INSERT INTO auctions.bid_rounds (scheduling_auction_id, buyer_code_id, week_id, submitted)
         SELECT ${r2SaId}, ${ds2WhslBcId}, ${r2WeekId}, false
         WHERE NOT EXISTS (
           SELECT 1 FROM auctions.bid_rounds
            WHERE scheduling_auction_id = ${r2SaId}
              AND buyer_code_id = ${ds2WhslBcId}
         )
         RETURNING id
       )
       SELECT COALESCE((SELECT id::text FROM ins),
                       (SELECT id::text FROM auctions.bid_rounds
                         WHERE scheduling_auction_id = ${r2SaId}
                           AND buyer_code_id = ${ds2WhslBcId}
                         LIMIT 1))`,
    );

    // Bidder reload — DS2WHSL now lands on GRID, not DOWNLOAD.
    await relogin(page, 'BIDDER');
    dash = new BidderDashboardPage(page);
    await dash.pickAuctionCode('DS2WHSL');
    await expect(dash.buyerCodeChip).toContainText('DS2WHSL');
    expect(await dash.isGridVisible()).toBe(true);
  });

  test('BidAsBidder cascade: admin submits on behalf → real buyer sees the submitted value', async ({
    page,
    request,
  }) => {
    applyFixture('wholesale-r1-active.sql');

    // Admin: BidAsBidder → impersonate HN → place + submit $750 via UI.
    await new LoginPage(page).loginAs('ADMIN');
    const picker = new BidAsBidderPage(page);
    await picker.goto();
    await picker.chooseBuyerCode('HN');

    const dash = new BidderDashboardPage(page);
    await dash.isGridVisible();

    const priceInput = dash.priceInput(dash.firstGridRow);
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
    await priceInput.pressSequentially('750');
    await priceInput.press('Tab');
    await savePromise;

    const submitPromise = page.waitForResponse(
      (r) => r.url().includes('/submit') && r.request().method() === 'POST' && r.ok(),
    );
    await dash.submitBidsButton.click();
    await submitPromise;
    await expect(dash.bidsSubmittedModal).toBeVisible();

    // Real buyer logs in fresh, picks HN, sees the admin-submitted bid.
    await relogin(page, 'BIDDER');
    const buyerDash = new BidderDashboardPage(page);
    await buyerDash.pickAuctionCode('HN');
    await buyerDash.isGridVisible();

    const reloadedPrice = page.locator(`input[aria-label="Price for row ${rowId}"]`);
    await expect(reloadedPrice).toHaveValue('$ 750.00');
  });
});
