import type { Page } from '@playwright/test';

export class QualifiedBuyerCodesPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/admin/auctions-data-center/qualified-buyer-codes');
  }

  get saIdFilter() {
    return this.page.locator('#saIdFilter');
  }

  get buyerCodeIdFilter() {
    return this.page.locator('#buyerCodeIdFilter');
  }

  get table() {
    return this.page.locator('table');
  }

  get tableRows() {
    return this.page.locator('table tbody tr');
  }

  get emptyMessage() {
    return this.page.locator('td[class*="empty"]');
  }

  qualifyButtonForRow(id: number) {
    return this.page.getByRole('button', { name: `Manually qualify QBC ${id}` });
  }
}
