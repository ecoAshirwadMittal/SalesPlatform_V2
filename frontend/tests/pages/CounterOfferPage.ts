import { Page, expect } from '@playwright/test';

/**
 * CounterOfferPage — maps to /pws/counter-offers and /pws/counter-offers/[offerId].
 * Replaces Mendix PWS_CounterOfferPage selectors.
 */
export class CounterOfferPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/pws/counter-offers');
    await this.page.waitForLoadState('domcontentloaded');
  }

  async gotoOffer(offerId: number | string) {
    await this.page.goto(`/pws/counter-offers/${offerId}`);
    await this.page.waitForLoadState('domcontentloaded');
  }

  /** Find and click an offer row by ID in the counter offers list */
  async findAndClickOfferByID(offerId: string) {
    const cleanId = offerId.replace(/^:\s*/, '').trim();

    // Wait for page to settle (may auto-redirect if only 1 counter offer)
    await this.page.waitForTimeout(2_000);

    // Already on detail page?
    if (/\/counter-offers\/\d+/.test(this.page.url())) {
      await this.page.locator('table[class*="dataGrid"] tbody tr').first()
        .waitFor({ state: 'visible', timeout: 10_000 });
      return;
    }

    // On list page — find the offer row
    const rows = this.page.locator('table tbody tr');

    // Wait for rows to appear if the list is still loading
    if (await rows.count() === 0) {
      await rows.first().waitFor({ state: 'visible', timeout: 10_000 }).catch(() => {});
    }

    // Find the matching row and extract the data-offer-id for direct navigation
    const rowCount = await rows.count();
    let matched = false;
    for (let i = 0; i < Math.min(rowCount, 10); i++) {
      const text = (await rows.nth(i).textContent())?.trim() || '';
      if (text.includes(cleanId)) {
        const dbOfferId = await rows.nth(i).getAttribute('data-offer-id');
        matched = true;

        if (dbOfferId) {
          await this.page.goto(`/pws/counter-offers/${dbOfferId}`, { waitUntil: 'domcontentloaded' });
        } else {
          await rows.nth(i).locator('td').first().click();
          await this.page.waitForURL(/counter-offers\/\d+/, { timeout: 10_000 });
        }
        break;
      }
    }

    // Wait for the data grid to be visible on the detail page
    await this.page.locator('table[class*="dataGrid"] tbody tr').first()
      .waitFor({ state: 'visible', timeout: 15_000 });
  }

  /** Get the counter status text for a row (Accept, Decline, Counter) */
  async getCounterStatusByRowIndex(rowIndex: number): Promise<string> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const row = rows.nth(rowIndex);
    const statusCell = row.locator('[class*="status"], td').filter({ hasText: /Accept|Decline|Counter/i }).first();
    return ((await statusCell.textContent()) || '').trim();
  }

  /** Check if the buyer action dropdown is visible for a row */
  async isActionDropdownVisibleByRowIndex(rowIndex: number): Promise<boolean> {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const row = rows.nth(rowIndex);
    const dropdown = row.locator('select');
    return dropdown.isVisible().catch(() => false);
  }

  /** Select buyer counter action (Accept / Decline) for a row */
  async selectCounterActionByRowIndex(rowIndex: number, action: 'Accept' | 'Decline') {
    const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
    const row = rows.nth(rowIndex);
    const dropdown = row.locator('select').first();
    await dropdown.waitFor({ state: 'visible', timeout: 10_000 });
    await dropdown.selectOption({ label: action });
    await this.page.waitForTimeout(500);
  }

  /** Click the "Submit Response" button and confirm the modal */
  async clickSubmitResponseButton() {
    await this.page.getByRole('button', { name: /submit.*response/i }).click();
    // Wait for confirm modal "Yes" button and click it
    const yesBtn = this.page.getByRole('button', { name: /^yes$/i }).first();
    await yesBtn.waitFor({ state: 'visible', timeout: 5_000 });
    await yesBtn.click();
    // Wait for submission to complete (success banner + auto-redirect)
    await this.page.waitForTimeout(3_000);
  }

  /** Check if error message modal or error banner is visible */
  async isErrorMessageModalVisible(): Promise<boolean> {
    const modal = this.page.locator('text=/error|required|please select/i');
    const banner = this.page.locator('[class*="messageBannerError"], [class*="modalHeaderError"]');
    return modal.or(banner).first().isVisible({ timeout: 5_000 }).catch(() => false);
  }

  async closeErrorModal() {
    const closeBtn = this.page.getByRole('button', { name: /close|ok|×/i }).first();
    if (await closeBtn.isVisible({ timeout: 3_000 }).catch(() => false)) {
      await closeBtn.click();
    } else {
      await this.page.keyboard.press('Escape');
    }
    await this.page.waitForTimeout(500);
  }

  /** Check if the submitted/canceled confirmation banner is visible */
  async isOfferResponseSubmittedModalVisible(): Promise<boolean> {
    const textMatch = this.page.locator('text=/submitted|success|canceled|response.*sent/i');
    const banner = this.page.locator('[class*="messageBannerSuccess"]');
    return textMatch.or(banner).first().isVisible({ timeout: 10_000 }).catch(() => false);
  }

  async closeOfferResponseSubmittedModal() {
    await this.closeErrorModal();
  }

  /** Open More Actions and click an option */
  async moreActionOption(option: 'Accept All Counters' | 'Cancel Order') {
    // The more button uses ⋮ (vertical ellipsis) with class moreMenuButton
    const moreBtn = this.page.locator('button[class*="moreMenu"], button[class*="more"]').first();
    await moreBtn.waitFor({ state: 'visible', timeout: 10_000 });
    await moreBtn.click();
    await this.page.waitForTimeout(500);

    // Map test-facing names to actual button text on the page
    const labelMap: Record<string, RegExp> = {
      'Accept All Counters': /accept all/i,
      'Cancel Order': /cancel.*offer/i,
    };
    const pattern = labelMap[option] || new RegExp(option, 'i');
    const menuBtn = this.page.locator('[class*="moreMenu"] button, [class*="Dropdown"] button')
      .filter({ hasText: pattern }).first();
    await menuBtn.waitFor({ state: 'visible', timeout: 5_000 });
    await menuBtn.click();
    await this.page.waitForTimeout(500);
  }

  /** Handle the Cancel Order confirmation modal */
  async cancelOrderModalAction(choice: 'yes' | 'no') {
    if (choice === 'yes') {
      await this.page.getByRole('button', { name: /yes|confirm/i }).click();
    } else {
      await this.page.getByRole('button', { name: /no|cancel/i }).click();
    }
    await this.page.waitForTimeout(1_000);
  }

  async isCancelOrderModalVisible(): Promise<boolean> {
    const modal = this.page.locator('text=/cancel.*order|are you sure/i');
    return modal.isVisible({ timeout: 3_000 }).catch(() => false);
  }

  /** Get summary box values for original, counter, or final offer */
  async getOfferSummaryBox(type: 'original' | 'counter' | 'final'): Promise<{ skus: number; qty: number; total: number }> {
    const labelMap: Record<string, string> = {
      original: 'Original Offer',
      counter: "EcoATM's Counter Offer",
      final: 'Final Offer',
    };
    const title = this.page.locator('[class*="summaryCardTitle"]')
      .filter({ hasText: labelMap[type] });
    await title.waitFor({ state: 'visible', timeout: 10_000 });
    const card = title.locator('..');
    const cells = card.locator('table tbody td');

    const skusText = ((await cells.nth(0).textContent()) || '0').replace(/[^0-9]/g, '');
    const qtyText = ((await cells.nth(1).textContent()) || '0').replace(/[^0-9]/g, '');
    const totalText = ((await cells.nth(2).textContent()) || '0').replace(/[^0-9.]/g, '');

    return {
      skus: parseInt(skusText, 10) || 0,
      qty: parseInt(qtyText, 10) || 0,
      total: parseFloat(totalText) || 0,
    };
  }
}
