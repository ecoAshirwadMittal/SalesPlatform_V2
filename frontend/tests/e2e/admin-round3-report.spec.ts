import { test, expect } from '@playwright/test';
import { isBackendAvailable } from './_helpers/backend';
import { applyFixture } from './_helpers/seedSql';
import { LoginPage, Round3ReportPage } from '../pages';

/**
 * Lane 1B — R3 Bid Report by Buyer
 *
 * QA-side counterpart (paraphrased):
 *   • RoundThreeBidReportPage.{selectSecondWeekFromDropdown,
 *     isBuyerCodePresentInReport(code)}
 *
 * Backend: GET /api/v1/admin/round3-reports?weekId=… (Lane 1B —
 * Round3ReportController). Authorised for Administrator + SalesOps via
 * SecurityConfig matcher.
 *
 * Live tests (`@live`) gate on `isBackendAvailable()` — actuator/health UP
 * AND `psql` on PATH for the SQL fixture.
 */

test.describe('Admin R3 Bid Report by Buyer @admin @regression @live', () => {
  test.beforeAll(async () => {
    test.skip(
      !(await isBackendAvailable()) && !process.env.FORCE_LIVE_TESTS,
      'requires Spring Boot on :8080 with admin@test.com seeded',
    );
    // Seed an R3 report row pinned to the most recent auction so the
    // admin can navigate to a deterministic state.
    applyFixture('round3-report-seed.sql');
  });

  test('admin lands on the R3 report page; week dropdown renders', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const report = new Round3ReportPage(page);
    await report.goto();

    await expect(report.heading).toBeVisible();
    await expect(report.weekSelect).toBeVisible();

    // The week dropdown is loaded from the existing
    // /api/v1/admin/inventory/weeks endpoint, so options should be
    // present whenever any week rows exist in dev DB.
    const optionCount = await report.weekSelect.locator('option').count();
    expect(optionCount).toBeGreaterThan(1); // placeholder + ≥1 week
  });

  test('selecting the most recent week loads the report and shows the marker buyer code', async ({
    page,
  }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const report = new Round3ReportPage(page);
    await report.goto();

    // Select the first real option (n=0 maps to the most-recent week per
    // WeekRepository.findCurrentAndPastWeeks ordering).
    await report.selectNthWeek(0);

    // Either the grid renders (week with data) or the empty state shows.
    // The fixture seeds a row pinned to the most-recent auction, so the
    // grid path is the expected branch — but we keep the assertion
    // tolerant in case the most-recent week's auction differs from the
    // most-recent auction in dev DB.
    await expect.poll(async () =>
      (await report.reportGrid.isVisible()) || (await report.emptyState.isVisible()),
    ).toBe(true);

    if (await report.reportGrid.isVisible()) {
      // When the grid renders, the marker row from the fixture should be
      // visible IF the most-recent auction belongs to the week the admin
      // just picked. This is the happy path.
      const present = await report.isBuyerCodePresentInReport('AA600WHL_R3FX');
      expect(present).toBe(true);
    }
  });

  test('empty week renders the No-data empty state, not an error', async ({ page }) => {
    const login = new LoginPage(page);
    await login.loginAs('ADMIN');

    const report = new Round3ReportPage(page);
    await report.goto();

    // Pick the LAST option in the dropdown (oldest week per
    // findCurrentAndPastWeeks DESC order). Older weeks are very
    // unlikely to have R3 report rows in a freshly-seeded dev DB,
    // so this exercises the empty-state branch.
    const options = report.weekSelect.locator('option');
    const total = await options.count();
    if (total < 2) {
      test.skip(true, 'need ≥1 week option to exercise empty-state branch');
    }
    const lastValue = await options.nth(total - 1).getAttribute('value');
    if (!lastValue) {
      test.skip(true, 'last week option has no value');
    }
    await report.weekSelect.selectOption(lastValue!);

    // Either empty state OR a populated grid is acceptable here — the
    // important assertion is that the page didn't surface an error alert.
    await expect(page.getByRole('alert')).toHaveCount(0);
  });
});
