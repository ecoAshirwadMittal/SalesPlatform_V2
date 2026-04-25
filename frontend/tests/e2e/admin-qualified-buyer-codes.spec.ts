import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { QualifiedBuyerCodesPage } from '../pages/QualifiedBuyerCodesPage';
import { LoginPage } from '../pages/LoginPage';

/**
 * P8 Lane 3B — admin Qualified Buyer Codes view + manual qualify/unqualify.
 *
 * Each test re-applies the QBC fixture to reset the qualification_type back
 * to 'Qualified' so a prior PATCH-to-Manual doesn't leak across runs.
 */

const BACKEND_REASON =
  'requires Spring Boot on :8080 with the QBC admin endpoints + seed applied';

interface SeedContext {
  schedulingAuctionId: number;
  qbcId: number;
  buyerCode: string;
}

async function resolveSeedQbcs(
  request: import('@playwright/test').APIRequestContext,
): Promise<SeedContext> {
  const loginResp = await request.post('http://localhost:8080/api/v1/auth/login', {
    data: { email: 'admin@test.com', password: 'Admin123!', rememberMe: false },
  });
  expect(loginResp.ok()).toBeTruthy();

  // The seed prepares HN, AAWHSL, DS2WHSL against the latest R1
  // scheduling_auction. To resolve the SA id we hit the bid-data admin
  // endpoint and pull bidRoundId → fetch the bid_round → derive SA. Cheaper
  // path: hit the QBC admin endpoint directly with each candidate SA id;
  // we don't have one yet. Instead, walk the auctions admin list and pick
  // the latest. The list ordering may vary, so we filter for one whose QBC
  // list contains buyer code 'HN'.
  // Pragma: try ids in descending order from a small upper bound; the dev
  // DB rarely has > 50 scheduling auctions.
  for (let saId = 2000; saId >= 1; saId--) {
    const probe = await request.get(
      `http://localhost:8080/api/v1/admin/qualified-buyer-codes?schedulingAuctionId=${saId}`,
    );
    if (!probe.ok()) continue;
    const body = await probe.json();
    const hn = body.rows?.find((r: { buyerCode: string }) => r.buyerCode === 'HN');
    if (hn) {
      return { schedulingAuctionId: saId, qbcId: hn.id, buyerCode: hn.buyerCode };
    }
  }
  throw new Error('Could not locate a scheduling_auction with HN QBC seeded');
}

test.describe('Admin Qualified Buyer Codes — view + toggle @auction @admin @live', () => {
  test.beforeAll(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      BACKEND_REASON,
    );
    applyFixture('qbc-admin-seed.sql');
  });

  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      BACKEND_REASON,
    );
    applyFixture('qbc-admin-seed.sql');
  });

  test('list loads QBCs for the chosen scheduling_auction with the joined buyer_code', async ({
    page,
    request,
  }) => {
    const seed = await resolveSeedQbcs(request);

    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const qbcPage = new QualifiedBuyerCodesPage(page);
    await qbcPage.goto();
    await qbcPage.schedulingAuctionFilter.fill(String(seed.schedulingAuctionId));
    await qbcPage.applyAndWait();

    expect(await qbcPage.gridRows.count()).toBeGreaterThanOrEqual(1);
    await expect(qbcPage.buyerCodeCell(seed.qbcId)).toHaveText(seed.buyerCode);
  });

  test('unchecking flips included=false and qualification_type=Manual', async ({
    page,
    request,
  }) => {
    const seed = await resolveSeedQbcs(request);

    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const qbcPage = new QualifiedBuyerCodesPage(page);
    await qbcPage.goto();
    await qbcPage.schedulingAuctionFilter.fill(String(seed.schedulingAuctionId));
    await qbcPage.applyAndWait();

    // Seed leaves the row at Qualified+Included; unchecking flips it.
    await expect(qbcPage.includedCheckbox(seed.qbcId)).toBeChecked();
    await expect(qbcPage.qualificationTypeCell(seed.qbcId)).toHaveText('Qualified');

    const resp = await qbcPage.toggleIncludedAndWait(seed.qbcId);
    expect(resp.status()).toBe(200);

    await expect(qbcPage.includedCheckbox(seed.qbcId)).not.toBeChecked();
    await expect(qbcPage.qualificationTypeCell(seed.qbcId)).toHaveText('Manual');
  });

  test('re-checking flips included=true and qualification_type stays Manual', async ({
    page,
    request,
  }) => {
    const seed = await resolveSeedQbcs(request);

    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const qbcPage = new QualifiedBuyerCodesPage(page);
    await qbcPage.goto();
    await qbcPage.schedulingAuctionFilter.fill(String(seed.schedulingAuctionId));
    await qbcPage.applyAndWait();

    // Uncheck → re-check sequence.
    const r1 = await qbcPage.toggleIncludedAndWait(seed.qbcId);
    expect(r1.status()).toBe(200);
    await expect(qbcPage.qualificationTypeCell(seed.qbcId)).toHaveText('Manual');

    const r2 = await qbcPage.toggleIncludedAndWait(seed.qbcId);
    expect(r2.status()).toBe(200);
    await expect(qbcPage.includedCheckbox(seed.qbcId)).toBeChecked();
    await expect(qbcPage.qualificationTypeCell(seed.qbcId)).toHaveText('Manual');
  });

  test('non-admin (Bidder) gets 403 on the admin GET', async ({ request }) => {
    const loginResp = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: { email: 'bidder@buyerco.com', password: 'Bidder123!', rememberMe: false },
    });
    expect(loginResp.ok()).toBeTruthy();

    const probe = await request.get(
      'http://localhost:8080/api/v1/admin/qualified-buyer-codes?schedulingAuctionId=1',
    );
    expect(probe.status()).toBe(403);
  });

  test('non-admin (Bidder) gets 403 on the PATCH endpoint', async ({ request }) => {
    const loginResp = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: { email: 'bidder@buyerco.com', password: 'Bidder123!', rememberMe: false },
    });
    expect(loginResp.ok()).toBeTruthy();

    const probe = await request.patch(
      'http://localhost:8080/api/v1/admin/qualified-buyer-codes/1',
      { data: { included: false } },
    );
    expect(probe.status()).toBe(403);
  });
});
