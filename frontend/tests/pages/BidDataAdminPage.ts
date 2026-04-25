import type { Locator, Page } from '@playwright/test';

/**
 * BidDataAdminPage — POM for `/admin/auctions-data-center/bid-data`.
 *
 * Mirrors the operations in qa-playwright-salesplatform's
 * ACC_BidDataPage that are reachable in the local app today
 * (filter by round + buyerCode, remove a row by price, count submitted-only).
 * Selector strategy: text + ARIA roles, never CSS classes — the layout
 * is still under pixel-parity work, so class-based selectors are brittle.
 */
export class BidDataAdminPage {
  constructor(private readonly page: Page) {}

  async goto(): Promise<void> {
    await this.page.goto('/admin/auctions-data-center/bid-data');
  }

  /** Bid round id input — the admin grid filters server-side on bidRoundId. */
  get roundFilter(): Locator {
    return this.page.getByLabel('Bid Round Id');
  }

  /** Buyer code id input — server-side filter on buyerCodeId. */
  get buyerCodeFilter(): Locator {
    return this.page.getByLabel('Buyer Code Id');
  }

  /**
   * "Submitted only" checkbox — when checked, the request is sent with
   * `submittedBidAmountGt=1` so the backend narrows to rows where
   * `submitted_bid_amount > 0`.
   */
  get submittedOnlyToggle(): Locator {
    return this.page.getByLabel('Submitted only');
  }

  /** Apply / Refresh button — re-fires the GET with the current filters. */
  get applyButton(): Locator {
    return this.page.getByRole('button', { name: 'Apply' });
  }

  /** All visible body rows in the bid_data grid. */
  get gridRows(): Locator {
    return this.page.getByRole('table').locator('tbody tr');
  }

  /** "No rows" empty-state copy — surfaces when the filter returns nothing. */
  get emptyState(): Locator {
    return this.page.getByText(/No bid data/i);
  }

  /**
   * Row by bid_data id — the rendered row uses
   * `data-testid="bid-data-row-<id>"`.
   */
  rowById(id: number): Locator {
    return this.page.getByTestId(`bid-data-row-${id}`);
  }

  /** "Remove" button inside the row scope. */
  removeButtonForRow(id: number): Locator {
    return this.rowById(id).getByRole('button', { name: 'Remove' });
  }

  /**
   * Apply the filters then wait for the resulting GET so the test doesn't
   * race against React's render flush.
   */
  async applyAndWait(): Promise<void> {
    const responsePromise = this.page.waitForResponse(
      (r) => r.url().includes('/api/v1/admin/bid-data') && r.request().method() === 'GET',
    );
    await this.applyButton.click();
    await responsePromise;
  }
}
