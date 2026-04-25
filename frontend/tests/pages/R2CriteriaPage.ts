import type { Locator, Page } from '@playwright/test';

/**
 * R2CriteriaPage — POM for the Lane 4 admin "Round 2 Selection Criteria"
 * surface. Mirrors the QA POM
 * {@code ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings(qual, inv, override)}
 * via three controls plus the Save button.
 *
 * Selectors prefer {@code data-testid} attributes anchored on the page; the
 * page is freshly built (no Mendix DOM heritage) so testids are the
 * canonical hook. Form labels (Bid Buyers Only / All Buyers, etc.) carry
 * accessible text but are not the primary lookup — testids survive copy
 * tweaks.
 */
export class R2CriteriaPage {
  constructor(private readonly page: Page) {}

  async goto(): Promise<void> {
    await this.page.goto('/admin/auction-control-center/r2-criteria');
    // Wait for either the form or the load-error banner so we never assert
    // on a half-mounted page.
    await Promise.race([
      this.root.waitFor({ state: 'visible', timeout: 15_000 }),
      this.loadErrorBanner.waitFor({ state: 'visible', timeout: 15_000 }),
    ]);
  }

  // ── Top-level containers ───────────────────────────────────────────────

  get root(): Locator {
    return this.page.getByTestId('r2-criteria-page');
  }

  get loadingState(): Locator {
    return this.page.getByTestId('r2-criteria-loading');
  }

  get loadErrorBanner(): Locator {
    return this.page.getByTestId('r2-criteria-load-error');
  }

  // ── Banner messages ────────────────────────────────────────────────────

  get successBanner(): Locator {
    return this.page.getByTestId('r2-criteria-success-banner');
  }

  get errorBanner(): Locator {
    return this.page.getByTestId('r2-criteria-error-banner');
  }

  /**
   * "No saved criteria yet — showing defaults" notice. Visible only when
   * the GET endpoint returned 404 (no row persisted yet).
   */
  get defaultsNotice(): Locator {
    return this.page.getByTestId('r2-criteria-defaults-notice');
  }

  // ── Radio: Regular Buyer Qualification ─────────────────────────────────

  get qualificationBidBuyersOnly(): Locator {
    return this.page.getByTestId('r2-qualification-bid-buyers-only');
  }

  get qualificationAllBuyers(): Locator {
    return this.page.getByTestId('r2-qualification-all-buyers');
  }

  // ── Radio: Regular Buyer Inventory Options ─────────────────────────────

  get inventoryWithBids(): Locator {
    return this.page.getByTestId('r2-inventory-with-bids');
  }

  get inventoryFull(): Locator {
    return this.page.getByTestId('r2-inventory-full');
  }

  // ── Toggle: STB allow-all-buyers override ──────────────────────────────

  get stbOverrideToggle(): Locator {
    return this.page.getByTestId('r2-stb-override-toggle');
  }

  // ── Action buttons ─────────────────────────────────────────────────────

  get saveButton(): Locator {
    return this.page.getByTestId('r2-criteria-save');
  }

  get resetButton(): Locator {
    return this.page.getByTestId('r2-criteria-reset');
  }

  // ── High-level verbs (mirror the QA POM) ───────────────────────────────

  /**
   * Equivalent to QA POM's
   * {@code selectRegularBuyerSettings(qual, inv, override)}. Picks the
   * three controls and clicks Save, waiting for the PUT response to settle
   * before returning. The caller still gets the success/error banner to
   * assert against.
   */
  async setSettings(
    qualification: 'Bid_Buyers_Only' | 'All_Buyers',
    inventory: 'Inventory_With_Bids' | 'Full_Inventory',
    stbOverride: boolean,
  ): Promise<void> {
    if (qualification === 'Bid_Buyers_Only') {
      await this.qualificationBidBuyersOnly.check();
    } else {
      await this.qualificationAllBuyers.check();
    }
    if (inventory === 'Inventory_With_Bids') {
      await this.inventoryWithBids.check();
    } else {
      await this.inventoryFull.check();
    }
    const isCurrentlyOn = await this.stbOverrideToggle.isChecked();
    if (isCurrentlyOn !== stbOverride) {
      await this.stbOverrideToggle.click();
    }
  }

  /**
   * Click Save and wait for the PUT to land. Returns the parsed response
   * so callers can pin specific assertions on the persisted projection.
   */
  async saveAndWait(): Promise<{ status: number; body: unknown }> {
    const responsePromise = this.page.waitForResponse(
      (r) =>
        r.url().includes('/api/v1/admin/round-criteria/') &&
        r.request().method() === 'PUT',
    );
    await this.saveButton.click();
    const response = await responsePromise;
    const body = await response.json().catch(() => null);
    return { status: response.status(), body };
  }
}
