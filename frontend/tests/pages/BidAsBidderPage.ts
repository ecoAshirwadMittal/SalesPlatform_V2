import type { Locator, Page } from '@playwright/test';

/**
 * BidAsBidderPage — POM for the admin impersonation entry point at
 * `/admin/bid-as-bidder`.
 *
 * Mirrors qa-playwright-salesplatform's
 * `NavMenuPage.BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin('AA600WHL')`.
 * Selectors target the local DOM (ARIA combobox + listbox), NOT the
 * Mendix mx-name-* hierarchy — see docs/tasks/p8-admin-surfaces-plan.md
 * §6 risk row 1.
 */
export class BidAsBidderPage {
  constructor(private readonly page: Page) {}

  async goto(): Promise<void> {
    await this.page.goto('/admin/bid-as-bidder');
  }

  /** Page heading — "Bid as Bidder". */
  get heading(): Locator {
    return this.page.getByRole('heading', { name: 'Bid as Bidder' });
  }

  /** Searchable combobox input. */
  get searchInput(): Locator {
    return this.page.getByRole('combobox');
  }

  /** Listbox containing matching codes. Visible only when the combobox is open. */
  get listbox(): Locator {
    return this.page.getByRole('listbox', { name: 'Buyer codes' });
  }

  /** All option <li> elements currently rendered in the listbox. */
  get options(): Locator {
    return this.listbox.getByRole('option');
  }

  /**
   * Filter the dropdown to entries matching `query` and click the first
   * option whose accessible name starts with the given code. Routes the
   * page to `/bidder/dashboard?buyerCodeId=…` on success.
   */
  async chooseBuyerCode(code: string): Promise<void> {
    await this.searchInput.click();
    await this.searchInput.fill(code);
    const option = this.options.filter({ hasText: new RegExp(`\\b${code}\\b`) }).first();
    await option.click();
    await this.page.waitForURL(/\/bidder\/dashboard\?buyerCodeId=/, { timeout: 15_000 });
  }
}
