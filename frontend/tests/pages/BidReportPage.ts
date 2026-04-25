import type { Page } from '@playwright/test';

export class BidReportPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/admin/auctions-data-center/bid-report');
  }

  get auctionIdFilter() {
    return this.page.locator('#auctionIdFilter');
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

  get refreshButton() {
    return this.page.getByRole('button', { name: 'Refresh' });
  }

  async filterByAuctionId(auctionId: number) {
    await this.auctionIdFilter.fill(String(auctionId));
  }
}
