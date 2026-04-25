import type { Locator, Page } from '@playwright/test';

/**
 * Round3ReportPage — POM for the Round 3 Bid Report by Buyer admin view
 * at `/admin/auctions-data-center/round3-bid-report`.
 *
 * Mirrors qa-playwright-salesplatform's
 * {@code RoundThreeBidReportPage.{selectSecondWeekFromDropdown,
 * isBuyerCodePresentInReport(code)}}, but selectors target the local DOM
 * (native <select> + <table>), NOT the Mendix mx-name-* hierarchy.
 */
export class Round3ReportPage {
  constructor(private readonly page: Page) {}

  async goto(): Promise<void> {
    await this.page.goto('/admin/auctions-data-center/round3-bid-report');
  }

  /** Page heading — "Round Three Bid Report by Buyer". */
  get heading(): Locator {
    return this.page.getByRole('heading', { name: /Round Three Bid Report by Buyer/i });
  }

  /** Native week dropdown. */
  get weekSelect(): Locator {
    return this.page.getByLabel('Week', { exact: true });
  }

  /**
   * Choose a week by its Mendix-style display value (e.g. "Wk17 / 2026").
   * Falls back to selecting by index when the display string isn't unique.
   */
  async selectWeekByDisplay(display: string): Promise<void> {
    await this.weekSelect.selectOption({ label: display });
  }

  /** Choose the Nth week option (0-indexed, skipping the placeholder). */
  async selectNthWeek(n: number): Promise<void> {
    const options = this.weekSelect.locator('option');
    // option[0] is the "-- Choose a week --" placeholder; skip it.
    const value = await options.nth(n + 1).getAttribute('value');
    if (!value) throw new Error(`No week option at index ${n}`);
    await this.weekSelect.selectOption(value);
  }

  /** Report data grid — only present after a week with data is chosen. */
  get reportGrid(): Locator {
    return this.page.getByRole('table', { name: 'R3 buyer report' });
  }

  /** Empty-state element shown when the chosen week returns 0 rows. */
  get emptyState(): Locator {
    return this.page.getByTestId('r3-empty-state');
  }

  /**
   * True if the report grid contains a row whose "Buyer Code" cell text
   * matches the given code. Mirrors QA's
   * {@code isBuyerCodePresentInReport(code)} semantics.
   */
  async isBuyerCodePresentInReport(code: string): Promise<boolean> {
    return await this.reportGrid
      .getByRole('row')
      .filter({ hasText: new RegExp(`\\b${code}\\b`) })
      .first()
      .isVisible()
      .catch(() => false);
  }
}
