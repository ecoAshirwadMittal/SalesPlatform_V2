/**
 * E2E: Partial Credit — admin review surface
 *
 * Three scenarios (Sprint 3 chunk 8):
 *   1. Admin lists requests — the landing page mounts, the four status
 *      counter chips render with numeric values, the request table
 *      renders (or the empty-state copy renders, when no rows seed). No
 *      console errors fire during load.
 *   2. Open + complete-review happy path — SKIPPED in dev. The buyer
 *      submit path in Sprint 2 fails with ORDER_NOT_FOUND under the
 *      LOGGING Snowflake reader, so no `PENDING_APPROVAL` row can be
 *      seeded against admin@test.com without a JDBC reader + a real
 *      Snowflake manifest. The spec keeps the scaffolding so that
 *      flipping the reader to `jdbc` in staging unlocks the full flow.
 *   3. Status config edit + colour propagation — admin edits the DRAFT
 *      row's colorHex from `#888888` to `#999999`, asserts the swatch
 *      reflects the new colour, then restores `#888888` so other tests
 *      see the seeded baseline.
 *
 * Requires:
 *   - frontend dev server on http://localhost:3000 (playwright.config.ts
 *     auto-starts `npm run dev`)
 *   - Spring Boot backend on http://localhost:8080 with the seeded
 *     admin@test.com user + V89 status seed
 *
 * Auth strategy:
 *   - log in via the login form using `loginAs()` (same pattern as
 *     admin-purchase-orders.spec.ts and the rest of `/admin/**` E2Es).
 *     Admin pages do their own permission gate inside the controller via
 *     `@PreAuthorize`, so no localStorage seeding is required.
 */

import { test, expect, type Page } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';

const ADMIN_EMAIL = 'admin@test.com';
const ADMIN_PASSWORD = 'Admin123!';

/** Standard admin login — matches the pattern used by partial-credit-submit.spec.ts. */
async function loginAs(page: Page, email: string, password: string): Promise<void> {
  await page.goto('/login');
  await page.getByPlaceholder('Email').fill(email);
  await page.getByPlaceholder('Password').fill(password);
  const loginResponse = page.waitForResponse(
    (r) => r.url().includes('/api/v1/auth/login') && r.request().method() === 'POST',
    { timeout: 30_000 },
  );
  await page.getByRole('button', { name: 'Login', exact: true }).click();
  const resp = await loginResponse;
  if (!resp.ok()) {
    throw new Error(`login failed: HTTP ${resp.status()}`);
  }
  await page.waitForURL((u) => !u.pathname.includes('/login'), { timeout: 30_000 });
}

test.describe('Admin → Partial Credit review', () => {
  test.beforeAll(async () => {
    test.skip(
      !(await isBackendAvailable()),
      'requires Spring Boot backend on :8080 with admin@test.com seeded',
    );
  });

  // ─── 1. Admin lists requests ──────────────────────────────────────────────

  test('admin lists partial credit requests', async ({ page }) => {
    await loginAs(page, ADMIN_EMAIL, ADMIN_PASSWORD);

    // Hook the console error listener AFTER login navigation so we only
    // catch errors that originate from the partial-credit landing — the
    // shared layout polls a few unrelated endpoints during route
    // transitions which can surface "Failed to fetch users" noise that
    // isn't relevant to this surface.
    const consoleErrors: string[] = [];
    page.on('console', (msg) => {
      if (msg.type() !== 'error') return;
      const text = msg.text();
      // Filter to errors that look related to the partial-credit page.
      // Any error containing "partial-credit" or "listAdmin" is on us;
      // everything else is shared-layout noise.
      if (text.includes('partial-credit') || text.includes('listAdmin') || text.includes('AdminCreditRequest')) {
        consoleErrors.push(text);
      }
    });

    await page.goto('/admin/auctions-data-center/partial-credit');

    // Heading is the load-bearing render assertion.
    await expect(page.getByRole('heading', { name: 'Partial Credit Requests' })).toBeVisible({
      timeout: 15_000,
    });

    // Status counter chips — all four chips render, each with a numeric
    // counter. The numbers can be 0 on a fresh DB; the test only asserts
    // that the chips render and the counter text is numeric.
    for (const label of ['Pending Approval', 'Approved', 'Declined', 'All']) {
      const chip = page.getByRole('button', { name: new RegExp(`^${label}:`) });
      await expect(chip).toBeVisible();
      const text = await chip.textContent();
      expect(text).toMatch(/\d+/);
    }

    // Either a request table OR the empty-state copy must render — never
    // both, never neither. We accept both outcomes because dev seeds
    // don't reliably produce a row (Sprint 2 submit fails under the
    // LOGGING Snowflake reader). Wait for whichever lands first; the
    // raw `isVisible()` check would race the React loading state.
    const tableHeader = page.locator('table thead tr').first();
    const emptyState = page.getByText('No partial credit requests match your filters');
    await Promise.any([tableHeader.waitFor({ state: 'visible', timeout: 15_000 }), emptyState.waitFor({ state: 'visible', timeout: 15_000 })]);
    const tableVisible = await tableHeader.isVisible().catch(() => false);
    const emptyStateVisible = await emptyState.isVisible().catch(() => false);
    expect(tableVisible || emptyStateVisible).toBe(true);

    // No client-side errors during load — listAdmin() fetching the four
    // counters + page rows must not surface a console.error in dev.
    expect(consoleErrors, `console errors: ${consoleErrors.join(' | ')}`).toEqual([]);
  });

  // ─── 2. Open + complete-review happy path (dev SKIP) ─────────────────────

  test.skip(
    'open + complete-review happy path requires JDBC Snowflake reader (CI/staging only)',
    async ({ page }) => {
      // Why this is skipped in dev:
      //
      // Sprint 2's buyer-submit path rejects with ORDER_NOT_FOUND under
      // the LOGGING reader (CreditRequestSnowflakeReader -> Logging
      // impl). Without a real Snowflake manifest behind the buyer
      // submit, no PENDING_APPROVAL row exists for admin@test.com to
      // open + complete-review.
      //
      // The full happy path — open a request, transition to UNDER_REVIEW,
      // accept lines, click Complete Review, confirm modal, assert status
      // pill flips to "Approved" — requires:
      //   1. `partial-credit.snowflake.reader: jdbc` (or a test seed
      //      that bypasses the validator)
      //   2. a buyer-side credit request that submitted cleanly
      //
      // Keep the scaffolding here so the test runs once staging flips
      // the reader. The first test (`admin lists`) is the load-bearing
      // dev assertion.
      await loginAs(page, ADMIN_EMAIL, ADMIN_PASSWORD);
      await page.goto('/admin/auctions-data-center/partial-credit');

      const firstRow = page.locator('tbody tr').first();
      await expect(firstRow).toBeVisible();
      await firstRow.locator('button[aria-label^="Open"]').click();

      await expect(page).toHaveURL(/\/admin\/auctions-data-center\/partial-credit\/\d+$/);
      await expect(page.getByRole('button', { name: /Complete Review/ })).toBeVisible();
    },
  );

  // ─── 3. Status config edit + colour propagation ──────────────────────────

  test('admin edits status config and verifies the live colour change', async ({ page }) => {
    await loginAs(page, ADMIN_EMAIL, ADMIN_PASSWORD);
    await page.goto('/admin/auctions-data-center/partial-credit/statuses');

    await expect(
      page.getByRole('heading', { name: 'Partial Credit — Status Configuration' }),
    ).toBeVisible({ timeout: 15_000 });

    // Locate the DRAFT row (seeded with system_status = 'DRAFT' and
    // color_hex = '#888888' in V89).
    const draftRow = page.locator('tbody tr', { hasText: 'DRAFT' });
    await expect(draftRow).toBeVisible();

    // Capture the original colour so we can restore it after the test.
    // The colour-cell hex span renders inside the row's color cell —
    // selecting it via the literal `#888888` string keeps the locator
    // resilient to swatch-rendering changes.
    const originalHex = '#888888';
    const newHex = '#999999';

    await draftRow.getByRole('button', { name: 'Edit' }).click();

    // The hex <input> for the row in edit mode — match the placeholder.
    const hexInput = draftRow.locator('input[placeholder="#RRGGBB"]');
    await expect(hexInput).toBeVisible();
    await hexInput.fill(newHex);

    // Save and wait for the PATCH round-trip to land before asserting
    // the swatch — Playwright's networkidle would be flaky here because
    // unrelated polling can keep the network alive.
    const patchResponse = page.waitForResponse(
      (r) =>
        r.url().includes('/api/v1/admin/partial-credit/statuses/') &&
        r.request().method() === 'PATCH',
      { timeout: 15_000 },
    );
    await draftRow.getByRole('button', { name: 'Save' }).click();
    const resp = await patchResponse;
    expect(resp.ok()).toBe(true);

    // After save, the swatch span uses `background-color` inline style.
    // Re-query the swatch element after exit-edit-mode rerender and
    // assert the computed colour matches the new hex.
    const swatch = draftRow.locator('span[aria-label*="Color swatch"]');
    await expect(swatch).toBeVisible();
    await expect(swatch).toHaveCSS(
      'background-color',
      // CSS engines normalise hex to rgb()
      'rgb(153, 153, 153)',
    );

    // Restore the original colour so this test doesn't leak state to
    // other suites. Click Edit again, paste the original hex, Save.
    await draftRow.getByRole('button', { name: 'Edit' }).click();
    const hexInputAgain = draftRow.locator('input[placeholder="#RRGGBB"]');
    await hexInputAgain.fill(originalHex);
    const restoreResponse = page.waitForResponse(
      (r) =>
        r.url().includes('/api/v1/admin/partial-credit/statuses/') &&
        r.request().method() === 'PATCH',
      { timeout: 15_000 },
    );
    await draftRow.getByRole('button', { name: 'Save' }).click();
    const restoreResp = await restoreResponse;
    expect(restoreResp.ok()).toBe(true);
  });
});
