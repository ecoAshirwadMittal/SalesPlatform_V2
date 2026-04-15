import { Page, expect } from '@playwright/test';

/**
 * CartPage — maps to /pws/cart.
 * Replaces Mendix PWS_CartPage selectors with modern React DOM selectors.
 */
export class CartPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/pws/cart');
    await this.page.waitForLoadState('domcontentloaded');
  }

  async isCartPageDisplayed(): Promise<boolean> {
    try {
      await this.page.getByRole('heading', { name: /My Offer/i })
        .waitFor({ state: 'visible', timeout: 10_000 });
      return true;
    } catch {
      return false;
    }
  }

  async isSubmitButtonEnabled(): Promise<boolean> {
    const submitBtn = this.page.getByRole('button', { name: /submit/i }).first();
    try {
      await submitBtn.waitFor({ state: 'visible', timeout: 10_000 });
    } catch {
      return false;
    }
    return !(await submitBtn.isDisabled());
  }

  async clickSubmitButton() {
    await this.page.getByRole('button', { name: /submit/i }).first().click();
    await this.page.waitForTimeout(1_000);
  }

  /** "Submit Offer" button on the "Almost Done!" modal (below-list-price offers) */
  async clickAlmostDoneSubmitButton() {
    const btn = this.page.getByRole('button', { name: /submit offer/i });
    await btn.waitFor({ state: 'visible', timeout: 15_000 });
    await btn.click();
    await this.page.waitForTimeout(2_000);
  }

  async isAlmostDoneModalVisible(): Promise<boolean> {
    try {
      await this.page.locator('h3').filter({ hasText: /almost done/i }).first()
        .waitFor({ state: 'visible', timeout: 5_000 });
      return true;
    } catch {
      return false;
    }
  }

  async isSubmittedConfirmationModalDisplayed(): Promise<boolean> {
    // Wait for either "Offer submitted" or "Thank you for your order!" heading
    try {
      await this.page.locator('h3').filter({ hasText: /offer submitted|thank you for your order/i }).first()
        .waitFor({ state: 'visible', timeout: 30_000 });
      return true;
    } catch {
      return false;
    }
  }

  async getOfferIdFromConfirmation(): Promise<string> {
    // Sales review path: "Your offer has been submitted, offer number: 456."
    const offerParagraph = this.page.locator('p').filter({ hasText: /offer number:/i }).first();
    if (await offerParagraph.isVisible().catch(() => false)) {
      const text = (await offerParagraph.textContent()) || '';
      const match = text.match(/offer number:\s*(\S+)/i);
      return match ? match[1].replace(/[.,]$/, '') : '';
    }

    // Direct order path: "Order Number: <strong>789</strong>"
    const orderParagraph = this.page.locator('p').filter({ hasText: /order number:/i }).first();
    if (await orderParagraph.isVisible().catch(() => false)) {
      const strong = orderParagraph.locator('strong').first();
      if (await strong.isVisible().catch(() => false)) {
        return (await strong.textContent())?.trim() || '';
      }
      const text = (await orderParagraph.textContent()) || '';
      const match = text.match(/order number:\s*(\S+)/i);
      return match ? match[1].replace(/[.,]$/, '') : '';
    }

    return '';
  }

  async closeConfirmationModal() {
    // Try close/ok/continue button, then Escape
    const closeBtn = this.page.getByRole('button', { name: /close|ok|done|continue/i }).first();
    try {
      await closeBtn.waitFor({ state: 'visible', timeout: 5_000 });
      await closeBtn.click();
    } catch {
      await this.page.keyboard.press('Escape');
    }
    await this.page.waitForTimeout(500);
  }

  async isQtyExceedMessageVisible(): Promise<boolean> {
    const warning = this.page.locator('text=/exceed|exceeds|over.*available/i');
    return warning.isVisible({ timeout: 5_000 }).catch(() => false);
  }

  async getSummaryOffer(): Promise<{ skus: string; qty: string; total: string }> {
    // Cart summary uses CSS module classes: cartSummary > cartStat > cartStatLabel + cartStatValue
    const statValues = this.page.locator('[class*="cartStatValue"]');
    const count = await statValues.count();

    if (count >= 3) {
      return {
        skus: ((await statValues.nth(0).textContent()) || '0').trim(),
        qty: ((await statValues.nth(1).textContent()) || '0').trim(),
        total: ((await statValues.nth(2).textContent()) || '0').replace(/[$,]/g, '').trim(),
      };
    }

    // Fallback: find value span adjacent to each label within the summary container
    const summary = this.page.locator('[class*="cartSummary"], [class*="Summary"]').first();
    const getValue = async (label: string): Promise<string> => {
      const stat = summary.locator(`[class*="cartStat"]`).filter({ hasText: label }).first();
      const value = stat.locator('span').nth(1);
      try {
        return ((await value.textContent({ timeout: 3_000 })) || '0').trim();
      } catch {
        return '0';
      }
    };

    return {
      skus: await getValue('SKUs'),
      qty: await getValue('Qty'),
      total: (await getValue('Total')).replace(/[$,]/g, ''),
    };
  }

  async getBuyerFromViewAs(): Promise<string> {
    const input = this.page.locator('input[aria-label="Search or select buyer code"]');
    if (await input.isVisible({ timeout: 3_000 }).catch(() => false)) {
      return (await input.inputValue()) || '';
    }
    return '';
  }

  /**
   * Full submit flow: handles both above-list (direct order) and below-list (sales review) paths.
   * Returns the offer/order ID.
   */
  async submitAndCaptureOrderID(): Promise<string> {
    await this.clickSubmitButton();

    const almostDone = await this.isAlmostDoneModalVisible();
    if (almostDone) {
      await this.clickAlmostDoneSubmitButton();
    }

    await this.isSubmittedConfirmationModalDisplayed();
    const offerId = await this.getOfferIdFromConfirmation();
    await this.closeConfirmationModal();
    return offerId;
  }

  /**
   * Full convenience method: navigates to shop, resets, enters offer data, goes to cart, and submits.
   * Equivalent to Mendix PWS_CartPage.submitOfferBelowListPrice().
   */
  async submitOfferBelowListPrice(
    page: Page,
    numberOfSKUs: number,
    priceFactor: number,
    qty: number,
  ): Promise<string> {
    const { ShopPage } = await import('./ShopPage');
    const shop = new ShopPage(page);
    const nav = (await import('./NavPage')).NavPage;
    const navPage = new nav(page);

    await navPage.chooseNavMenu('Shop');
    await shop.selectMoreActionOption('Reset');
    await shop.confirmReset();
    await shop.sortAvlQty('descending');
    await shop.enterOfferData(numberOfSKUs, priceFactor, qty);
    await shop.clickCartButton();
    return this.submitAndCaptureOrderID();
  }
}
