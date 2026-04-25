import type { Locator, Page } from '@playwright/test';

/**
 * BidderDashboardPage — POM for the wholesale bidder dashboard.
 *
 * Mirrors the operations in qa-playwright-salesplatform's
 * AUC_DataGridDashBoardPage / NavMenuPage that are reachable in the local app
 * today. Operations gated on features not yet ported live (round-access
 * gating, sales-as-bidder dropdown, hand-on-table widget) are NOT included
 * here — add them as the underlying features land per
 * docs/tasks/qa-wholesale-tests-port-plan.md.
 *
 * Selector strategy: prefer text content + ARIA roles over CSS classes so
 * the POM survives the ongoing pixel-parity work that touches class names.
 */
export class BidderDashboardPage {
  constructor(private readonly page: Page) {}

  // ── Buyer-code picker (/buyer-select) ──────────────────────────────────
  // The picker is technically not the dashboard, but the bidder lands here
  // immediately after login when they hold > 1 code, so the dashboard POM
  // owns the "pick a code and proceed" verb.

  /**
   * Click the auction-side pill matching the given buyer code (e.g. 'HN').
   * Buyers with multiple auction codes will have multiple pills; this
   * targets the first one whose accessible name starts with the code.
   */
  async pickAuctionCode(code: string): Promise<void> {
    // Pills are <button> elements with accessible name composed from
    // "{code} {buyerName}" — anchor the regex with ^ to avoid matching
    // a buyer name that happens to contain the code as a substring.
    const pill = this.page.getByRole('button', { name: new RegExp(`^${code}\\b`) });
    await pill.first().click();
    await this.page.waitForURL(/\/bidder\/dashboard\?buyerCodeId=/, { timeout: 15_000 });
  }

  // ── Top-bar chrome (rendered by BuyerPortalChrome) ─────────────────────

  /** Wraps the ecoATM logo + Switch Buyer Code + buyer-code chip + avatar. */
  get chrome(): Locator {
    return this.page.getByRole('banner');
  }

  /**
   * The buyer-code chip in the chrome top-bar. Contains the active code
   * (bold, e.g. "HN") and the company name (e.g. "Nadia Boonnayanont").
   * Matches the legacy QA test's `getBuyerCodeDisplay()`.
   */
  get buyerCodeChip(): Locator {
    return this.page.locator('[class*="chipFramed"]');
  }

  /** "Switch Buyer Code" link in the chrome top-bar. */
  get switchBuyerCodeLink(): Locator {
    return this.page.getByRole('button', { name: 'Switch Buyer Code' });
  }

  // ── Dashboard header ───────────────────────────────────────────────────

  /** "Minimum starting bid - $2.50" label, red text, sub-header row. */
  get minimumBidLabel(): Locator {
    return this.page.getByText(/Minimum starting bid/);
  }

  /** "Submit Bids" button; disabled until the bidder has dirty rows. */
  get submitBidsButton(): Locator {
    return this.page.getByRole('button', { name: 'Submit Bids' });
  }

  /** Carryover button — opens the carryover modal. */
  get carryoverButton(): Locator {
    return this.page.getByRole('button', { name: /Carryover/i });
  }

  // ── Grid ───────────────────────────────────────────────────────────────

  /**
   * Wait until the bid grid has rendered. Returns true if the grid table
   * is visible, false if the page rendered an empty / no-active-auction
   * state instead.
   */
  async isGridVisible(timeoutMs = 5_000): Promise<boolean> {
    try {
      await this.grid.waitFor({ state: 'visible', timeout: timeoutMs });
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Grid container — the bid <table> element. Plain HTML tables have an
   * implicit role of `table` (not `grid`); BidGrid.tsx uses <table> with
   * no explicit role override.
   */
  get grid(): Locator {
    return this.page.getByRole('table');
  }

  /** All visible body rows in the grid. */
  get gridRows(): Locator {
    return this.grid.locator('tbody tr');
  }

  /** Footer summary text — e.g. "Currently showing 11956 of 11956". */
  get gridFooter(): Locator {
    return this.page.getByText(/Currently showing/i);
  }

  /**
   * Sort button inside a column header. The button's accessible name is
   * `Sort by <label>` per BidGrid.HeaderCell.
   */
  sortButton(columnLabel: string): Locator {
    return this.page.getByRole('button', { name: `Sort by ${columnLabel}` });
  }

  /**
   * Header cell <th> with aria-sort attribute — used to assert the sort
   * direction after clicking sortButton().
   */
  headerCell(columnLabel: string): Locator {
    return this.page.getByRole('columnheader').filter({ hasText: columnLabel }).first();
  }

  /**
   * Filter input by column key (matches aria-label="Filter by <key>"
   * inside BidGrid). Keys are camelCase per the FilterState type.
   */
  filterInput(columnKey: string): Locator {
    return this.page.getByLabel(`Filter by ${columnKey}`);
  }

  /**
   * Auction title rendered in the dashboard header (e.g. "Auction 2026 / Wk17").
   * Two h2 elements with `roundLabel` form the title group; this targets the
   * first (the auction title) rather than the round label.
   */
  get auctionTitle(): Locator {
    return this.page.getByRole('heading', { name: /^Auction \d+ \/ Wk\d+$/ });
  }

  /** Round label heading — "Round 1", "Round 2", "Round 3" per parity ADR. */
  roundLabel(roundNumber: number): Locator {
    return this.page.getByRole('heading', { name: `Round ${roundNumber}` });
  }

  /** First visible body row in the grid (most-relevant row for placement tests). */
  get firstGridRow(): Locator {
    return this.gridRows.first();
  }

  /**
   * Price input within a specific row locator. PriceCell renders an
   * `<input aria-label="Price for row <id>">` — the row-relative
   * locator avoids relying on a hardcoded id.
   */
  priceInput(row: Locator): Locator {
    return row.locator('input[aria-label^="Price for row"]');
  }

  /** Qty Cap input within a specific row locator. */
  qtyCapInput(row: Locator): Locator {
    return row.locator('input[aria-label^="Qty Cap for row"]');
  }

  /**
   * "Bids submitted" success modal. BidderModal applies the title as the
   * dialog's `aria-label` (not a visible heading), so target the dialog
   * role rather than a heading.
   */
  get bidsSubmittedModal(): Locator {
    return this.page.getByRole('dialog', { name: 'Bids submitted' });
  }

  /** "No Bids to Submit" empty-state modal. */
  get noBidsModal(): Locator {
    return this.page.getByRole('dialog', { name: 'No Bids to Submit' });
  }
}
