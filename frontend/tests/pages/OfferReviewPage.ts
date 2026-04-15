import { Page, expect } from '@playwright/test';

/**
 * OfferReviewPage — maps to /pws/offer-review (admin offer queue list).
 * Replaces Mendix PWS_OfferQueuePages selectors.
 */
export class OfferReviewPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/pws/offer-review');
    await this.page.waitForLoadState('domcontentloaded');
  }

  /** Click a status tab: "Sales Review", "Buyer Acceptance", "Ordered", "Declined", "Total" */
  async chooseOfferStatusTab(tab: string) {
    const tabBtn = this.page.locator('button, [role="tab"], span')
      .filter({ hasText: new RegExp(`^\\s*${tab}`, 'i') })
      .first();
    await tabBtn.click();
    await this.page.waitForTimeout(1_000);
  }

  /** Check if an offer ID / offer number exists in the current tab's table rows */
  async isOfferIdExistUnderAnyTab(offerId: string, maxRowsToCheck: number): Promise<boolean> {
    const cleanId = offerId.replace(/^:\s*/, '').trim();
    let found = false;
    let attempts = 0;

    while (!found && attempts < 3) {
      await this.page.waitForTimeout(2_000);
      const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
      const count = Math.min(await rows.count(), maxRowsToCheck);

      for (let i = 0; i < count; i++) {
        const text = (await rows.nth(i).textContent())?.trim() || '';
        if (text.includes(cleanId)) {
          found = true;
          break;
        }
      }
      if (found) break;
      attempts++;
    }
    return found;
  }

  /** Find and click on a specific offer row by its ID */
  async findAndClickOfferByID(offerId: string, maxRowsToCheck: number) {
    const cleanId = offerId.replace(/^:\s*/, '').trim();
    let found = false;
    let attempts = 0;

    while (!found && attempts < 3) {
      await this.page.waitForTimeout(2_000);
      const rows = this.page.locator('table[class*="dataGrid"] tbody tr');
      const count = Math.min(await rows.count(), maxRowsToCheck);

      for (let i = 0; i < count; i++) {
        const text = (await rows.nth(i).textContent())?.trim() || '';
        if (text.includes(cleanId)) {
          // Click the offer ID link (may be <a> or clickable <span>)
          const link = rows.nth(i).locator('a, span[class*="link"], span[class*="Link"]').first();
          if (await link.isVisible().catch(() => false)) {
            await link.click();
          } else {
            // Fallback: click the first cell's text element
            await rows.nth(i).locator('td').first().click();
          }
          found = true;
          break;
        }
      }
      if (found) break;
      attempts++;
    }

    if (!found) throw new Error(`OfferID ${offerId} not found in ${maxRowsToCheck} rows after 3 attempts`);
    await this.page.waitForLoadState('domcontentloaded');
  }
}
