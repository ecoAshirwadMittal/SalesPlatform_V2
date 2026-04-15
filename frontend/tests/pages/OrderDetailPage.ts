import { Page } from '@playwright/test';

/**
 * OrderDetailPage — maps to /pws/orders/[id] (order detail with By SKU / By Device views).
 */
export class OrderDetailPage {
  constructor(private page: Page) {}

  /** Switch to "By Device" view */
  async switchToByDevice() {
    const btn = this.page.locator('button').filter({ hasText: 'By Device' }).first();
    await btn.waitFor({ state: 'visible', timeout: 10_000 });
    await btn.click();
    await this.page.waitForTimeout(2_000);
  }

  /** Switch to "By SKU" view */
  async switchToBySku() {
    const btn = this.page.locator('button').filter({ hasText: 'By SKU' }).first();
    await btn.waitFor({ state: 'visible', timeout: 10_000 });
    await btn.click();
    await this.page.waitForTimeout(2_000);
  }

  /** Get the IMEI from a specific row in the By Device grid (0-indexed). Returns null if no IMEI found. */
  async getImeiByRowIndex(rowIndex: number): Promise<string | null> {
    const rows = this.page.locator('table tbody tr');
    const row = rows.nth(rowIndex);
    const isVisible = await row.isVisible({ timeout: 5_000 }).catch(() => false);
    if (!isVisible) return null;

    // IMEI is the first column in the By Device grid
    const firstCell = row.locator('td').first();
    const text = ((await firstCell.textContent()) || '').trim();
    return text && text !== '---' ? text : null;
  }

  /** Get the first valid IMEI from the By Device grid */
  async getFirstImei(): Promise<string | null> {
    const rows = this.page.locator('table tbody tr');
    const count = await rows.count();
    for (let i = 0; i < count; i++) {
      const imei = await this.getImeiByRowIndex(i);
      if (imei) return imei;
    }
    return null;
  }

  /** Check if the By Device grid has any data rows */
  async hasDeviceData(): Promise<boolean> {
    const rows = this.page.locator('table tbody tr');
    const count = await rows.count();
    if (count === 0) return false;
    // Check if the first row is not an empty-state message
    const firstCell = rows.first().locator('td').first();
    const text = ((await firstCell.textContent()) || '').trim();
    return !text.toLowerCase().includes('no device');
  }

  /** Get the order status badge text */
  async getOrderStatus(): Promise<string> {
    const badge = this.page.locator('[class*="statusBadge"]').first();
    return ((await badge.textContent()) || '').trim();
  }

  /** Click back to order list */
  async goBackToOrders() {
    const backBtn = this.page.locator('button').filter({ hasText: 'Order History' }).first();
    await backBtn.click();
    await this.page.waitForLoadState('domcontentloaded');
    await this.page.waitForTimeout(2_000);
  }
}
