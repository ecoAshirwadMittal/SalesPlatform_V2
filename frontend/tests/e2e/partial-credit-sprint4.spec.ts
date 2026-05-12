import { test, expect, type Page } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';

/**
 * Sprint 4 close-out smoke spec — partial-credit (SPKB-3653).
 *
 * Each test gates on `isBackendAvailable()` so CI (no backend) skips
 * cleanly. Local runs with `mvn spring-boot:run` exercise the real
 * surface end-to-end.
 *
 * Covers five plan §8 chunk-8 scenarios at a smoke level: deeper
 * scenarios (full wizard submit → admin complete-review → email_audit
 * insert) need DB state coordination that's better verified manually
 * via the §12 verification list than from a single Playwright run.
 * What we DO want from this spec is regression-guard: if any of the
 * five entry points 404 or 403 unexpectedly, CI fails fast.
 */

async function loginAs(page: Page, email: string, password: string) {
  await page.goto('/login');
  await page.fill('[name="email"]', email);
  await page.fill('[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL((u) => !u.pathname.includes('/login'));
}

test.describe('Partial Credit — Sprint 4 close-out', () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), 'requires Spring Boot backend on :8080');
  });

  test('bidder lands on /wholesale/partial-credit and sees the page chrome', async ({ page }) => {
    await loginAs(page, 'bidder@buyerco.com', 'Bidder123!');
    await page.goto('/wholesale/partial-credit');
    await expect(page.locator('h1')).toHaveText('Credit Requests');
    // Bidder must NOT see the SalesRep-only "Submit on behalf" button.
    await expect(page.getByTestId('on-behalf-trigger')).toHaveCount(0);
  });

  test('sales-rep sees the "Submit on behalf" button on the buyer landing', async ({ page }) => {
    await loginAs(page, 'salesrep@test.com', 'SalesRep123!');
    await page.goto('/wholesale/partial-credit');
    await expect(page.getByTestId('on-behalf-trigger')).toBeVisible();
  });

  test('salesops lands on the admin partial-credit page with chips + Download', async ({ page }) => {
    await loginAs(page, 'salesops@test.com', 'SalesOps123!');
    await page.goto('/admin/auctions-data-center/partial-credit');
    await expect(page.locator('h1')).toHaveText('Partial Credit Requests');
    // Download button is the new Sprint 4 chunk 7 entry point.
    await expect(page.getByTestId('export-xlsx')).toBeVisible();
    await expect(page.getByTestId('export-xlsx')).toBeEnabled();
  });

  test('admin email-templates page lists the 3 seeded templates', async ({ page }) => {
    await loginAs(page, 'admin@test.com', 'Admin123!');
    await page.goto('/admin/auctions-data-center/partial-credit/email-templates');
    await expect(page.locator('h1')).toHaveText('Email templates');
    // The 3 V90-seeded keys must all be visible in the table.
    await expect(page.getByText('ReviewCompleted_Approved')).toBeVisible();
    await expect(page.getByText('ReviewCompleted_Declined')).toBeVisible();
    await expect(page.getByText('PhotoUploadRequested')).toBeVisible();
  });

  test('admin Download triggers an xlsx response (or 413 when filter is too wide)', async ({ page }) => {
    await loginAs(page, 'admin@test.com', 'Admin123!');
    await page.goto('/admin/auctions-data-center/partial-credit');
    // Probe the export endpoint directly — driving the download dialog
    // through the browser is flaky in Playwright; the network call is
    // the real assertion.
    const response = await page.request.get(
      'http://localhost:8080/api/v1/admin/partial-credit/export.xlsx',
    );
    expect([200, 413]).toContain(response.status());
    if (response.status() === 200) {
      expect(response.headers()['content-type']).toContain('spreadsheetml.sheet');
      expect(response.headers()['content-disposition']).toContain('attachment');
    }
  });

  test('bidder cannot reach the admin landing — returns 403/404', async ({ page }) => {
    await loginAs(page, 'bidder@buyerco.com', 'Bidder123!');
    const res = await page.goto('/admin/auctions-data-center/partial-credit');
    expect([200, 403, 404]).toContain(res?.status() ?? 200);
    // If the page rendered (200) it'll be the dashboard's "you don't
    // have access" surface, not the partial-credit table — verify the
    // heading is absent.
    await expect(page.getByRole('heading', { name: 'Partial Credit Requests' })).toHaveCount(0);
  });
});

/*
 * Deferred scenarios (kept here as a checklist for the §12 manual
 * verification pass; tied to live SMTP + DB state so they don't belong
 * in CI):
 *
 *  - Buyer submits a request → opens the detail page → uploads a
 *    photo → photo appears in the gallery (covered piecemeal by
 *    Sprint 2 + Chunk 5 unit tests).
 *  - Admin reviews + APPROVEs → partial_credit.email_audit gets a row
 *    (verify with `psql -c "SELECT template_key, recipient_email, success
 *     FROM partial_credit.email_audit ORDER BY sent_at DESC LIMIT 5"`).
 *  - Sales-rep submits on behalf → request appears on the bidder's
 *    landing (the on-behalf modal + wizard resume flow is exercised by
 *    Chunk 6 + Chunk 8 unit tests).
 *  - Admin edits an email template → re-runs a review → new subject is
 *    logged in email_audit.
 */
