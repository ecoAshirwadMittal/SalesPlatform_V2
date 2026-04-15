import { Page, expect } from '@playwright/test';
import { users, type UserKey } from '../fixtures/test-users';

export class LoginPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/login');
  }

  async loginAs(key: UserKey) {
    const user = users[key];
    if (!user) throw new Error(`Unknown user key: ${key}`);
    await this.goto();
    await this.page.locator('#email').fill(user.email);
    await this.page.locator('#password').fill(user.password);
    await this.page.locator('button[type="submit"]').click();
    await this.page.waitForURL((url) => !url.pathname.includes('/login'), { timeout: 15_000 });

    // Wait for sidebar to be ready if redirected to a PWS page
    if (this.page.url().includes('/pws/')) {
      await this.page.locator('aside').first().waitFor({ state: 'visible', timeout: 15_000 });
    }
  }

  async logout() {
    await this.page.locator('button[aria-label="User menu"]').click();
    await this.page.getByRole('button', { name: 'Logout' }).click();
    await this.page.waitForURL('**/login', { timeout: 10_000 });
  }

  async isOnLoginPage(): Promise<boolean> {
    return this.page.url().includes('/login');
  }
}
