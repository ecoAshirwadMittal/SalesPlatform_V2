import { Page, expect } from '@playwright/test';

/**
 * OfferDetailPage — maps to /pws/offer-review/[offerId] (admin offer detail).
 * Replaces Mendix PWS_OfferDetailsPage selectors.
 */
export class OfferDetailPage {
  constructor(private page: Page) {}

  async gotoOffer(offerId: number) {
    await this.page.goto(`/pws/offer-review/${offerId}`);
    await this.page.waitForLoadState('domcontentloaded');
  }

  /** Check if the status badge shows "Sales Review" */
  async isSalesReviewStatusDisplayed(): Promise<boolean> {
    const badge = this.page.locator('[class*="status"], [class*="badge"]')
      .filter({ hasText: /sales.*review/i }).first();
    return badge.isVisible({ timeout: 5_000 }).catch(() => false);
  }

  /** Set sales action for a SKU row: Accept, Decline, Counter, Finalize */
  async salesActionEachSKU(rowIndex: number, action: string) {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const row = rows.nth(rowIndex);
    const select = row.locator('select').first();
    await select.waitFor({ state: 'visible', timeout: 15_000 });
    await select.selectOption({ label: action });
    await this.page.waitForTimeout(500);
  }

  /** Get the currently selected sales action for a row */
  async getSalesActionStatusByRowIndex(rowIndex: number): Promise<string> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const row = rows.nth(rowIndex);
    const select = row.locator('select').first();
    await select.waitFor({ state: 'visible', timeout: 15_000 });
    const value = await select.inputValue();
    const option = select.locator(`option[value="${value}"]`);
    return ((await option.textContent()) || value).trim();
  }

  /** Enter counter price and qty for a SKU row */
  async enterCounterPriceAndQty(rowIndex: number, price: string, qty: string): Promise<string> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const row = rows.nth(rowIndex);
    const inputs = row.locator('input');

    // DOM column order: Qty (first input), Counter Price (second input)
    const qtyInput = inputs.first();
    const priceInput = inputs.nth(1);

    await qtyInput.fill(qty);
    await this.page.waitForTimeout(1_000); // let qty API call settle before price
    await priceInput.fill(price);
    await priceInput.press('Tab');
    await this.page.waitForTimeout(1_000);

    // Read the total column (last cell with a numeric value)
    const totalCell = row.locator('td, div').last();
    const totalText = (await totalCell.textContent())?.replace(/[^0-9.]/g, '') || '0';
    return totalText;
  }

  /** Click "Complete Review" button */
  async clickCompleteReviewButton() {
    await this.page.getByRole('button', { name: /complete.*review/i }).click();
    await this.page.waitForTimeout(2_000);
  }

  /** Close the confirmation modal after successful review */
  async clickCloseSubmittedConfirmationModal() {
    const closeBtn = this.page.getByRole('button', { name: /close|ok|done|×/i }).first();
    if (await closeBtn.isVisible({ timeout: 5_000 }).catch(() => false)) {
      await closeBtn.click();
    } else {
      await this.page.keyboard.press('Escape');
    }
    await this.page.waitForTimeout(500);
  }

  /** Close the error modal popup */
  async clickCloseErrorModalPopup() {
    await this.clickCloseSubmittedConfirmationModal();
  }

  /** Open the "More Actions" menu and click an option */
  async moreActionOption(option: 'Accept All' | 'Finalize All' | 'Decline All' | 'Download') {
    // Wait for the detail page data grid to be loaded
    await this.page.locator('table[class*="dataGrid"] tbody tr').first().waitFor({ state: 'visible', timeout: 15_000 });

    // Click the three-dot menu button (&#8943; horizontal ellipsis or class*="more")
    const moreBtn = this.page.locator('button[class*="more"], button[class*="More"]').first();
    await moreBtn.waitFor({ state: 'visible', timeout: 10_000 });
    await moreBtn.click();
    await this.page.waitForTimeout(300);

    await this.page.locator('button').filter({ hasText: option }).first().click();
    await this.page.waitForTimeout(2_000);
  }

  /** Check if a More Action option is visible */
  async isMoreActionOptionVisible(option: string): Promise<boolean> {
    const moreBtn = this.page.locator('button[class*="more"], button[class*="More"]').first();
    await moreBtn.waitFor({ state: 'visible', timeout: 10_000 });
    await moreBtn.click();
    await this.page.waitForTimeout(300);

    const optionEl = this.page.locator('button, a').filter({ hasText: option }).first();
    const visible = await optionEl.isVisible().catch(() => false);
    // Close the menu by clicking the toggle button again (no Escape handler)
    await moreBtn.click();
    await this.page.waitForTimeout(300);
    return visible;
  }

  /** Get original offer summary box values (SKUs, Qty, Price from the summary table) */
  async getOriginalOfferSummary(): Promise<{ skus: string; qty: string; total: string }> {
    const title = this.page.locator('text=Original Offer');
    await title.waitFor({ state: 'visible', timeout: 10_000 });
    const box = title.locator('..');
    const cells = box.locator('table tbody td');

    return {
      skus: ((await cells.nth(0).textContent()) || '0').trim(),
      qty: ((await cells.nth(1).textContent()) || '0').trim(),
      total: ((await cells.nth(2).textContent()) || '0').replace(/[$,]/g, '').trim(),
    };
  }

  /** Get counter offer summary — computed from counter input values in the data grid */
  async getCounterOfferSummary(): Promise<{ skus: string; qty: string; total: string }> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const rowCount = await rows.count();
    let skus = 0, qty = 0, total = 0;

    for (let i = 0; i < rowCount; i++) {
      const row = rows.nth(i);
      const qtyInput = row.locator('input').first();
      if (await qtyInput.isVisible().catch(() => false)) {
        const priceInput = row.locator('input').nth(1);
        const qtyVal = parseInt(await qtyInput.inputValue() || '0', 10);
        const priceVal = parseFloat(await priceInput.inputValue() || '0');
        if (qtyVal > 0 || priceVal > 0) {
          skus++;
          qty += qtyVal;
          total += qtyVal * priceVal;
        }
      }
    }

    return {
      skus: skus.toString(),
      qty: qty.toString(),
      total: total.toString(),
    };
  }

  /** Check if price field is disabled (for accepted/declined items) */
  async isCounterPriceFieldDisabled(rowIndex: number): Promise<boolean> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    // DOM column order: Qty (first input), Counter Price (second input)
    const input = rows.nth(rowIndex).locator('input').nth(1);
    const isVisible = await input.isVisible().catch(() => false);
    if (!isVisible) return true;
    return input.isDisabled();
  }

  /** Check if qty field is disabled */
  async isCounterQtyFieldDisabled(rowIndex: number): Promise<boolean> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    // DOM column order: Qty (first input), Counter Price (second input)
    const input = rows.nth(rowIndex).locator('input').first();
    const isVisible = await input.isVisible().catch(() => false);
    if (!isVisible) return true;
    return input.isDisabled();
  }
}
