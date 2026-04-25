import type { Locator, Page } from '@playwright/test';

/**
 * QualifiedBuyerCodesPage — POM for
 * `/admin/auction-control-center/qualified-buyer-codes`.
 *
 * Mirrors qa-playwright-salesplatform's ACC_QualifiedBuyerCodePage:
 * select a scheduling auction, toggle a buyer's `included` checkbox,
 * read the resulting qualification_type label.
 */
export class QualifiedBuyerCodesPage {
  constructor(private readonly page: Page) {}

  async goto(): Promise<void> {
    await this.page.goto('/admin/auction-control-center/qualified-buyer-codes');
  }

  /** Scheduling-auction id input — drives the GET on Apply. */
  get schedulingAuctionFilter(): Locator {
    return this.page.getByLabel('Scheduling Auction Id');
  }

  get applyButton(): Locator {
    return this.page.getByRole('button', { name: 'Apply' });
  }

  /** All visible QBC rows in the grid. */
  get gridRows(): Locator {
    return this.page.getByRole('table').locator('tbody tr');
  }

  /** Row by QBC id (data-testid="qbc-row-<id>"). */
  rowById(id: number): Locator {
    return this.page.getByTestId(`qbc-row-${id}`);
  }

  /** Included checkbox within a specific QBC row. */
  includedCheckbox(id: number): Locator {
    return this.rowById(id).getByRole('checkbox', { name: 'Included' });
  }

  /** Qualification-type cell — shows "Qualified" / "Not_Qualified" / "Manual". */
  qualificationTypeCell(id: number): Locator {
    return this.rowById(id).getByTestId('qualification-type');
  }

  /** Buyer-code cell — shows the human code (e.g. "AA600WHL"). */
  buyerCodeCell(id: number): Locator {
    return this.rowById(id).getByTestId('buyer-code');
  }

  async applyAndWait(): Promise<void> {
    const responsePromise = this.page.waitForResponse(
      (r) =>
        r.url().includes('/api/v1/admin/qualified-buyer-codes') &&
        r.request().method() === 'GET',
    );
    await this.applyButton.click();
    await responsePromise;
  }

  /**
   * Toggle the included checkbox on the given QBC row and wait for the
   * PATCH to complete. Returns the response so callers can assert status.
   */
  async toggleIncludedAndWait(id: number) {
    const patchPromise = this.page.waitForResponse(
      (r) =>
        r.url().includes(`/api/v1/admin/qualified-buyer-codes/${id}`) &&
        r.request().method() === 'PATCH',
    );
    await this.includedCheckbox(id).click();
    return patchPromise;
  }
}
