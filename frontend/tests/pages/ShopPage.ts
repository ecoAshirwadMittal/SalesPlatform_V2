import { Page, expect } from '@playwright/test';

/**
 * ShopPage — maps to /pws/order (buyer-facing inventory + cart builder).
 * Replaces Mendix PWS_ShopPage selectors with modern React DOM selectors.
 */
export class ShopPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/pws/order');
    await this.page.waitForLoadState('domcontentloaded');
  }

  async isInventoryTableVisible(): Promise<boolean> {
    try {
      await this.page.locator('table').waitFor({ state: 'visible', timeout: 15_000 });
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Sort the Avl. Qty column by clicking its header.
   * Clicks once for ascending, twice for descending.
   */
  async sortAvlQty(direction: 'ascending' | 'descending') {
    const header = this.page.locator('th').filter({ hasText: 'Avl. Qty' }).first();
    await header.click();
    await this.page.waitForTimeout(500);
    // Check current sort direction by aria or class, click again if needed
    if (direction === 'descending') {
      await header.click();
      await this.page.waitForTimeout(500);
    }
  }

  /**
   * Enter offer price and quantity for rows starting at startRowIndex.
   * Returns array of { price, qty } entered.
   *
   * IMPORTANT: The Offer Price input only renders when cartQty > 0.
   * Before any qty is entered, only the Cart Qty input (step="1") exists.
   * After filling qty, React re-renders and the Offer Price input (step="0.01") appears.
   * The app auto-defaults offerPrice to listPrice when qty is first set.
   */
  async enterOfferData(
    numberOfSkusOrStartIndex: number,
    priceOrFactor: number,
    qty: number,
  ): Promise<Array<{ price: number; qty: number }>> {
    const result: Array<{ price: number; qty: number }> = [];
    const rows = this.page.locator('table tbody tr');
    const rowCount = await rows.count();
    const count = Math.min(numberOfSkusOrStartIndex, rowCount);

    for (let i = 0; i < count; i++) {
      const row = rows.nth(i);

      // Read list price from column 9 (always visible text) before entering data
      let actualPrice = priceOrFactor;
      if (priceOrFactor < 10) {
        const priceCell = row.locator('td').nth(9); // Price column
        const priceText = (await priceCell.textContent())?.replace(/[^0-9.]/g, '') || '0';
        actualPrice = Math.floor(parseFloat(priceText) * priceOrFactor);
        if (actualPrice <= 0) actualPrice = 1;
      }

      // 1. Fill Cart Qty first — the only input before any data is entered
      const qtyInput = row.locator('input[step="1"]');
      await qtyInput.fill(String(qty));
      await qtyInput.press('Tab');

      // 2. Wait for Offer Price input to appear (React re-renders after cartQty > 0)
      const priceInput = row.locator('input[step="0.01"]');
      await priceInput.waitFor({ state: 'visible', timeout: 5_000 });

      // 3. Clear auto-defaulted price and fill desired offer price
      await priceInput.fill(String(actualPrice));
      await priceInput.press('Tab');
      await this.page.waitForTimeout(300);

      result.push({ price: actualPrice, qty });
    }
    return result;
  }

  async getRowData(rowIndex: number): Promise<{ sku: string; price: number; qty: number; total: number }> {
    const row = this.page.locator('table tbody tr').nth(rowIndex);

    const sku = (await row.locator('td').first().textContent())?.trim() || '';

    // Offer Price input (step="0.01") only exists when cartQty > 0
    const priceInput = row.locator('input[step="0.01"]');
    const priceVisible = await priceInput.isVisible().catch(() => false);
    const price = priceVisible ? parseFloat((await priceInput.inputValue()) || '0') : 0;

    // Cart Qty input (step="1") is always present
    const qtyInput = row.locator('input[step="1"]');
    const qty = parseInt((await qtyInput.inputValue()) || '0', 10);

    const totalCell = row.locator('td').last();
    const totalText = (await totalCell.textContent())?.replace(/[^0-9.]/g, '') || '0';
    const total = parseFloat(totalText);

    return { sku, price, qty, total };
  }

  /** Click the "Cart" button in the bottom summary bar */
  async clickCartButton() {
    await this.page.locator('button:text-is("Cart")').click();
    await this.page.waitForLoadState('domcontentloaded');
  }

  /** Open the three-dot "More Actions" menu and select an option */
  async selectMoreActionOption(option: string) {
    // Click the three-dot menu button
    const moreBtn = this.page.locator('button').filter({ hasText: '⋮' }).first()
      .or(this.page.locator('button[class*="moreActions"], button[class*="more"]').first());
    await moreBtn.click();
    await this.page.waitForTimeout(300);

    // Click the option
    await this.page.locator('button, a').filter({ hasText: option }).first().click();
    await this.page.waitForTimeout(500);
  }

  /** Handle the Reset Offer confirmation modal */
  async confirmReset() {
    await this.page.getByRole('button', { name: /yes|confirm|reset/i }).click();
    await this.page.waitForTimeout(500);
  }

  async getCartSummary(): Promise<{ skus: number; qty: number; total: number }> {
    const skuText = await this.page.locator('span:text-is("SKUs") + span, span:text-is("SKUs") ~ span').first().textContent() || '0';
    const qtyText = await this.page.locator('span:text-is("Qty") + span, span:text-is("Qty") ~ span').first().textContent() || '0';
    const totalText = await this.page.locator('span:text-is("Total") + span, span:text-is("Total") ~ span').first().textContent() || '$0';

    return {
      skus: parseInt(skuText.replace(/[^0-9]/g, ''), 10) || 0,
      qty: parseInt(qtyText.replace(/[^0-9]/g, ''), 10) || 0,
      total: parseFloat(totalText.replace(/[^0-9.]/g, '')) || 0,
    };
  }
}
