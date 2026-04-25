import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { BidDataAdminPage } from '../pages/BidDataAdminPage';
import { LoginPage } from '../pages/LoginPage';

/**
 * P8 Lane 3A — admin Bid Data view + remove.
 *
 * Live tests gate on `isBackendAvailable()` AND the absence of
 * `FORCE_LIVE_TESTS` (the QA convention is "skip when offline unless the
 * caller forces it"; the env var is the override switch). Each test
 * re-applies the SQL fixture so prior runs' DELETEs don't shrink the seed.
 *
 * The fixture seeds:
 *   • A bid_round for buyer code HN against the latest R1 scheduling_auction
 *   • ≥ 2 bid_data rows attached to that round
 *   • exactly 1 row with submitted_bid_amount > 0 (so the "Submitted only"
 *     filter narrows from N to 1).
 */

const BACKEND_REASON =
  'requires Spring Boot on :8080 with the bid-data admin endpoints + seed applied';

interface SeedContext {
  bidRoundId: number;
  buyerCodeId: number;
}

/**
 * Read the seed identifiers back out of the DB so the spec doesn't have to
 * hardcode them. Uses the admin endpoint + login to resolve the round/code
 * for "HN" against the most-recent R1.
 */
async function resolveSeedIds(request: import('@playwright/test').APIRequestContext): Promise<SeedContext> {
  // The bid-data admin endpoint accepts buyerCodeId as an optional filter,
  // so we first need to know HN's id. Re-use the existing buyer-codes admin
  // path that resolves a code → id; if that's not available we fall back to
  // the qualified-buyer-codes endpoint which JOINs the same table.
  // For minimal coupling we hit the QBC list and pick HN.
  const loginResp = await request.post('http://localhost:8080/api/v1/auth/login', {
    data: { email: 'admin@test.com', password: 'Admin123!', rememberMe: false },
  });
  expect(loginResp.ok()).toBeTruthy();

  // The fixture targets the latest R1, but we need its scheduling_auction_id
  // to query the QBC list. Hit the auctions admin list endpoint.
  const auctionsResp = await request.get(
    'http://localhost:8080/api/v1/admin/auctions?page=0&size=1',
  );
  // Defensive: if the auctions endpoint isn't available, fall back to a
  // direct SQL probe via psql is not feasible in-test; instead error.
  expect(auctionsResp.ok(), 'admin auctions list must be reachable to resolve seed').toBeTruthy();

  // Fall back: rely on the bid-data admin GET with no filters returning at
  // least one row that we seeded. The first row's bidRoundId + buyerCodeId
  // are the IDs the seed prepared.
  const bdResp = await request.get('http://localhost:8080/api/v1/admin/bid-data');
  expect(bdResp.ok()).toBeTruthy();
  const body = await bdResp.json();
  const seedRow = body.rows.find((r: { ecoid: string }) => r.ecoid?.startsWith('P8-ADMIN-'));
  expect(seedRow, 'seed bid_data row P8-ADMIN-* must exist').toBeTruthy();
  return { bidRoundId: seedRow.bidRoundId, buyerCodeId: seedRow.buyerCodeId };
}

test.describe('Admin Bid Data — view + remove @auction @admin @live', () => {
  test.beforeAll(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      BACKEND_REASON,
    );
    applyFixture('bid-data-admin-seed.sql');
  });

  test.beforeEach(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      BACKEND_REASON,
    );
    // Per-test re-apply so prior DELETEs don't shrink the seed.
    applyFixture('bid-data-admin-seed.sql');
  });

  test('admin can filter by (round, buyerCode) and only matching rows render', async ({
    page,
    request,
  }) => {
    const seed = await resolveSeedIds(request);

    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const bdPage = new BidDataAdminPage(page);
    await bdPage.goto();
    await bdPage.roundFilter.fill(String(seed.bidRoundId));
    await bdPage.buyerCodeFilter.fill(String(seed.buyerCodeId));
    await bdPage.applyAndWait();

    const count = await bdPage.gridRows.count();
    expect(count).toBeGreaterThanOrEqual(2);
  });

  test('admin can soft-delete a bid_data row and it disappears from the grid', async ({
    page,
    request,
  }) => {
    const seed = await resolveSeedIds(request);

    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const bdPage = new BidDataAdminPage(page);
    await bdPage.goto();
    await bdPage.roundFilter.fill(String(seed.bidRoundId));
    await bdPage.buyerCodeFilter.fill(String(seed.buyerCodeId));
    await bdPage.applyAndWait();

    const beforeCount = await bdPage.gridRows.count();
    expect(beforeCount).toBeGreaterThan(0);

    // Capture the first row's id from data-testid.
    const firstTestId = await bdPage.gridRows.first().getAttribute('data-testid');
    const id = Number(firstTestId!.replace('bid-data-row-', ''));

    const deletePromise = page.waitForResponse(
      (r) =>
        r.url().includes(`/api/v1/admin/bid-data/${id}`) &&
        r.request().method() === 'DELETE',
    );
    await bdPage.removeButtonForRow(id).click();
    const resp = await deletePromise;
    expect([200, 204]).toContain(resp.status());

    // The page re-applies the filter after Remove resolves. The deleted row
    // has been soft-deleted, so the next list response excludes it.
    await expect(bdPage.rowById(id)).toHaveCount(0);
  });

  test('"Submitted only" filter narrows to rows where submitted_bid_amount > 0', async ({
    page,
    request,
  }) => {
    const seed = await resolveSeedIds(request);

    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const bdPage = new BidDataAdminPage(page);
    await bdPage.goto();
    await bdPage.roundFilter.fill(String(seed.bidRoundId));
    await bdPage.buyerCodeFilter.fill(String(seed.buyerCodeId));
    await bdPage.applyAndWait();

    const allCount = await bdPage.gridRows.count();
    expect(allCount).toBeGreaterThanOrEqual(2);

    await bdPage.submittedOnlyToggle.check();
    await bdPage.applyAndWait();

    // Seed guarantees exactly 1 submitted row.
    expect(await bdPage.gridRows.count()).toBe(1);
  });

  test('non-admin (Bidder) gets 403 on the admin GET endpoint', async ({ request }) => {
    const loginResp = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: { email: 'bidder@buyerco.com', password: 'Bidder123!', rememberMe: false },
    });
    expect(loginResp.ok()).toBeTruthy();

    const probe = await request.get('http://localhost:8080/api/v1/admin/bid-data');
    expect(probe.status()).toBe(403);
  });

  test('non-admin (Bidder) gets 403 on the admin DELETE endpoint', async ({ request }) => {
    const loginResp = await request.post('http://localhost:8080/api/v1/auth/login', {
      data: { email: 'bidder@buyerco.com', password: 'Bidder123!', rememberMe: false },
    });
    expect(loginResp.ok()).toBeTruthy();

    // ID 1 is fine — the security filter rejects before the controller runs.
    const probe = await request.delete('http://localhost:8080/api/v1/admin/bid-data/1');
    expect(probe.status()).toBe(403);
  });
});
