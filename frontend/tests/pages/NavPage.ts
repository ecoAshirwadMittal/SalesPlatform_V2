import { Page } from '@playwright/test';

type PwsNavLabel =
  | 'Inventory' | 'Offer Review' | 'RMA Review' | 'Pricing'
  | 'Shop' | 'Counters' | 'Orders' | 'RMAs' | "FAQ's" | 'Grading';

const ROUTE_MAP: Record<PwsNavLabel, string> = {
  Inventory: '/pws/inventory',
  'Offer Review': '/pws/offer-review',
  'RMA Review': '/pws/rma-review',
  Pricing: '/pws/pricing',
  Shop: '/pws/order',
  Counters: '/pws/counter-offers',
  Orders: '/pws/orders',
  RMAs: '/pws/rma-requests',
  "FAQ's": '/pws/faqs',
  Grading: '/pws/grading',
};

export class NavPage {
  constructor(private page: Page) {}

  async chooseNavMenu(label: PwsNavLabel) {
    const route = ROUTE_MAP[label];

    // If not already inside the PWS layout, navigate directly by URL
    if (!this.page.url().includes('/pws/')) {
      await this.page.goto(route, { waitUntil: 'domcontentloaded' });
    } else {
      // Wait for sidebar to be rendered, then find and click the nav link
      await this.page.locator('aside').first().waitFor({ state: 'visible', timeout: 15_000 });
      const navLink = this.page.locator('aside a').filter({
        has: this.page.locator(`span:text-is("${label}")`),
      }).first();
      await navLink.waitFor({ state: 'visible', timeout: 10_000 });
      await navLink.click();
    }

    // Wait for PWS sidebar + main content to be ready
    await this.page.locator('aside').first().waitFor({ state: 'visible', timeout: 15_000 });
    await this.page.locator('main').first().waitFor({ state: 'visible', timeout: 15_000 });
  }

  async selectBuyerCode(code: string) {
    const input = this.page.locator('input[aria-label="Search or select buyer code"]');
    await input.click();
    await input.fill(code);
    await this.page.locator('button').filter({ hasText: code }).first().click();
    await this.page.waitForLoadState('domcontentloaded');
  }

  async getCurrentBuyerCode(): Promise<string> {
    const input = this.page.locator('input[aria-label="Search or select buyer code"]');
    return (await input.inputValue()) || '';
  }
}
