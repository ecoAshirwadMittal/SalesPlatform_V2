import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';

/**
 * Theme 3, Phase 3.3 — HttpOnly auth cookie E2E verification.
 *
 * Asserts the three exit criteria from docs/tasks/todos-resolution-plan.md:
 *   3.3.1 login → protected page → logout → protected page redirects to /login
 *   3.3.2 `document.cookie` does NOT expose `auth_token` (HttpOnly is honored)
 *   3.3.3 `localStorage.auth_token` never gets set
 *
 * Requires both servers running:
 *   backend:  cd backend && mvn spring-boot:run
 *   frontend: cd frontend && npm run dev  (Playwright webServer auto-starts this)
 */
test.describe('auth cookie migration (Theme 3)', () => {
  test('HttpOnly cookie is set on login and not readable from JS', async ({ page, context }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAs('ADMIN');

    // 3.3.1 — successful redirect off /login
    await expect(page).not.toHaveURL(/\/login/);

    // 3.3.2 — the cookie is present server-side but NOT visible to JS.
    const jsCookie = await page.evaluate(() => document.cookie);
    expect(jsCookie).not.toContain('auth_token');

    const browserCookies = await context.cookies();
    const authCookie = browserCookies.find((c) => c.name === 'auth_token');
    expect(authCookie, 'backend must set auth_token cookie on login').toBeDefined();
    expect(authCookie?.httpOnly).toBe(true);
    expect(authCookie?.sameSite).toBe('Strict');
    // In local dev secure=false so http://localhost works; in CI over HTTPS this should flip.
    // We only assert HttpOnly + SameSite here; the Secure flag is environment-dependent.

    // 3.3.3 — no token anywhere in localStorage
    const lsToken = await page.evaluate(() => window.localStorage.getItem('auth_token'));
    expect(lsToken).toBeNull();
  });

  test('logout clears the cookie and protected routes bounce to /login', async ({ page, context }) => {
    const loginPage = new LoginPage(page);
    await loginPage.loginAs('ADMIN');
    await expect(page).not.toHaveURL(/\/login/);

    // Hit the logout endpoint directly — this mirrors what both layouts now do.
    const res = await page.request.post('/api/v1/auth/logout');
    expect(res.status()).toBe(204);

    // Cookie jar should no longer hold a usable auth_token.
    const cookiesAfter = await context.cookies();
    const stillPresent = cookiesAfter.find((c) => c.name === 'auth_token' && c.value.length > 0);
    expect(stillPresent, 'logout must expire auth_token cookie').toBeUndefined();

    // Visiting a protected page now bounces to /login via next middleware.
    await page.goto('/users');
    await expect(page).toHaveURL(/\/login/);
  });
});
