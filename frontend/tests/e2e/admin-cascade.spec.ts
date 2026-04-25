import { test, expect, type Page } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture, queryScalar } from './_helpers/seedSql';
import {
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
});
