import { Page } from '@playwright/test';

/**
 * OrderPage — maps to /pws/orders (order history list).
 */
export class OrderPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/pws/orders');
    await this.page.waitForLoadState('domcontentloaded');
  }

  /** Click a tab: All, Recent, In Process, Complete */
  async chooseTab(tab: 'All' | 'Recent' | 'In Process' | 'Complete') {
    const btn = this.page.locator('button').filter({ hasText: tab }).first();
    await btn.waitFor({ state: 'visible', timeout: 10_000 });
    await btn.click();
    await this.page.waitForTimeout(2_000);
  }

  /** Find the first order row with "Shipped" status and click it. Returns the offerId from the URL. */
  async clickFirstShippedOrder(): Promise<string | null> {
    // Look for a row containing a "Shipped" status badge
    const shippedRow = this.page
      .locator('table tbody tr')
      .filter({ has: this.page.locator('span:text-is("Shipped")') })
      .first();

    const isVisible = await shippedRow.isVisible({ timeout: 5_000 }).catch(() => false);
    if (!isVisible) return null;

    await shippedRow.click();
    await this.page.waitForLoadState('domcontentloaded');
    await this.page.waitForTimeout(2_000);

    // Extract offerId from the URL: /pws/orders/{offerId}
    const url = this.page.url();
    const match = url.match(/\/pws\/orders\/(\d+)/);
    return match ? match[1] : null;
  }

  /** Check if any shipped orders exist in the current view */
  async hasShippedOrders(): Promise<boolean> {
    const shippedBadge = this.page
      .locator('table tbody tr')
      .filter({ has: this.page.locator('span:text-is("Shipped")') })
      .first();
    return shippedBadge.isVisible({ timeout: 5_000 }).catch(() => false);
  }
}
