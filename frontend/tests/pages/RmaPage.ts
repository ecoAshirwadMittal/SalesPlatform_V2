import { Page, expect } from '@playwright/test';

/**
 * RmaPage — maps to /pws/rma-requests and /pws/rma-requests/[rmaId].
 * Replaces Mendix PWS_RMAPage selectors.
 */
export class RmaPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/pws/rma-requests');
    await this.page.waitForLoadState('domcontentloaded');
  }

  async isRMAPageDisplayed(): Promise<boolean> {
    const header = this.page.locator('h1, h2, span').filter({ hasText: /RMA/i }).first();
    return header.isVisible({ timeout: 10_000 }).catch(() => false);
  }

  /** Click the "Request an RMA" button */
  async clickRequestRMAButton() {
    await this.page.getByRole('button', { name: /request.*rma/i }).click();
    await this.page.waitForTimeout(1_000);
  }

  /** Download the RMA template from the request modal */
  async downloadRMATemplate(): Promise<string> {
    const [download] = await Promise.all([
      this.page.waitForEvent('download', { timeout: 15_000 }),
      this.page.getByRole('button', { name: /download.*template/i }).click(),
    ]);
    const path = `./test-results/rma-template-${Date.now()}.xlsx`;
    await download.saveAs(path);
    return path;
  }

  /** Upload an RMA file */
  async uploadRMAFile(filePath: string) {
    const fileInput = this.page.locator('input[type="file"]');
    await fileInput.setInputFiles(filePath);
    await this.page.waitForTimeout(1_000);
  }

  /** Click submit on the RMA request modal */
  async submitRMA() {
    await this.page.getByRole('button', { name: /submit.*rma/i }).click();
    await this.page.waitForTimeout(2_000);
  }

  /** Verify RMA submission success message */
  async verifyRMASubmissionSuccess(): Promise<boolean> {
    try {
      await this.page.locator('text=/submitted.*successfully|success/i')
        .waitFor({ state: 'visible', timeout: 15_000 });
      return true;
    } catch {
      return false;
    }
  }

  /** Close any open modal */
  async closeModal() {
    const closeBtn = this.page.getByRole('button', { name: /close|ok|×/i }).first();
    if (await closeBtn.isVisible({ timeout: 3_000 }).catch(() => false)) {
      await closeBtn.click();
    } else {
      await this.page.keyboard.press('Escape');
    }
    await this.page.waitForTimeout(500);
  }

  /** Open 3-dot menu and click option */
  async clickMenuOption(option: string): Promise<boolean> {
    const moreBtn = this.page.locator('button').filter({ hasText: '⋮' }).first()
      .or(this.page.locator('button[class*="more"]').first());
    await moreBtn.click();
    await this.page.waitForTimeout(300);

    const optionBtn = this.page.locator('button, a').filter({ hasText: option }).first();
    if (await optionBtn.isVisible({ timeout: 3_000 }).catch(() => false)) {
      await optionBtn.click();
      await this.page.waitForTimeout(500);
      return true;
    }
    await this.page.keyboard.press('Escape');
    return false;
  }

  async clickMenuRMAPolicy(): Promise<boolean> {
    return this.clickMenuOption('Return Policy');
  }

  async clickMenuRMAInstructions(): Promise<boolean> {
    return this.clickMenuOption('RMA Instructions');
  }

  async clickMenuDownload(): Promise<string> {
    const [download] = await Promise.all([
      this.page.waitForEvent('download', { timeout: 15_000 }),
      this.clickMenuOption('Download'),
    ]);
    const path = `./test-results/rma-download-${Date.now()}.xlsx`;
    await download.saveAs(path);
    return path;
  }

  /** Get the total RMA count from pagination */
  async getPaginationCount(): Promise<number> {
    const pagination = this.page.locator('text=/of\\s+\\d+|total.*\\d+|\\d+\\s+results/i').first();
    if (await pagination.isVisible({ timeout: 3_000 }).catch(() => false)) {
      const text = (await pagination.textContent()) || '';
      const match = text.match(/(\d+)/);
      return match ? parseInt(match[1], 10) : 0;
    }
    // Fallback: count rows
    return this.page.locator('table tbody tr').count();
  }

  /** Click first row and verify RMA details */
  async clickFirstRowAndVerifyDetails(expectedImei: string, expectedReason: string) {
    const firstRow = this.page.locator('table tbody tr').first();
    const link = firstRow.locator('a').first();
    if (await link.isVisible().catch(() => false)) {
      await link.click();
    } else {
      await firstRow.click();
    }
    await this.page.waitForLoadState('domcontentloaded');

    const pageText = await this.page.locator('main').textContent() || '';
    const foundImei = pageText.includes(expectedImei);

    return {
      imei: foundImei ? expectedImei : null,
      returnReason: pageText.includes(expectedReason) ? expectedReason : null,
      status: null,
      success: foundImei,
    };
  }
}
