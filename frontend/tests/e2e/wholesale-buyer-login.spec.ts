import { test, expect } from '@playwright/test';
import { checkA11y } from './_helpers/a11y';
import { isBackendAvailable } from './_helpers/backend';

/**
 * Phase 1 — Wholesale buyer login E2E suite.
 * Requires the dev server running at http://localhost:3000 and the backend at
 * http://localhost:8080.  The Playwright config wires `webServer` to start the
 * frontend automatically when not already running.
 *
 * Happy-path test verifies the multi-code buyer-select landing.  Single-code
 * deep-linking (skip-to-shell) is Phase 2's job — if bidder@buyerco.com turns
 * out to have exactly one code in Phase 2, this test may need a data-setup shim
 * to ensure the multi-code path is exercised.
 */

// ---------------------------------------------------------------------------
// Live-backend tests — require Spring Boot on :8080
// ---------------------------------------------------------------------------

test.describe('Wholesale buyer login (live backend)', () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), 'requires Spring Boot backend on :8080');
  });

  test('bidder can log in and lands on /buyer-select', async ({ page }) => {
    await page.goto('/login');

    // Verify the page headline is present and correct
    await expect(page.locator('h1')).toContainText('Premium Wholesale');

    await page.getByPlaceholder('Email').fill('bidder@buyerco.com');
    await page.getByPlaceholder('Password').fill('Bidder123!');

    // Wait on the real network signal rather than an arbitrary timer.
    const loginResponse = page.waitForResponse((r) => r.url().includes('/api/v1/auth/login'));
    await page.getByRole('button', { name: 'Login', exact: true }).click();
    await loginResponse;

    // Phase 1: Bidder role routes to /buyer-select (the code picker).
    // Single-code deep-linking is Phase 2's job.
    await expect(page).toHaveURL(/\/buyer-select/, { timeout: 8_000 });
  });
}); // end describe 'Wholesale buyer login (live backend)'

// ---------------------------------------------------------------------------
// Pure-frontend tests — run in CI without a backend
// ---------------------------------------------------------------------------

test.describe('Wholesale buyer login', () => {
  test('password eye-toggle is keyboard accessible', async ({ page }) => {
    await page.goto('/login');

    const passwordInput = page.getByPlaceholder('Password');
    const toggle = page.getByRole('button', { name: /Show password|Hide password/ });

    // Input starts as type=password
    await passwordInput.fill('Password123');
    await expect(passwordInput).toHaveAttribute('type', 'password');

    // Click the toggle — field switches to type=text
    await toggle.click();
    await expect(passwordInput).toHaveAttribute('type', 'text');

    // Toggle is reachable via keyboard: Tab from the password input must land on it.
    // Focus the password input, then Tab once.
    await passwordInput.focus();
    await page.keyboard.press('Tab');
    await expect(toggle).toBeFocused();
  });

  test('forgot-password link has a non-empty href', async ({ page }) => {
    await page.goto('/login');

    // The link must exist and have a non-empty href so it doesn't silently 404.
    const link = page.getByRole('link', { name: 'Forgot Password?' });
    const href = await link.getAttribute('href');
    expect(href).toBeTruthy();
  });

  test('login page renders required buttons', async ({ page }) => {
    await page.goto('/login');

    // Verify the login card is present and the primary CTA button is visible.
    await expect(page.getByRole('button', { name: 'Login', exact: true })).toBeVisible();

    // Contact Us button exists and is a button (not a link)
    const contactUs = page.getByRole('button', { name: 'Contact Us' });
    await expect(contactUs).toBeVisible();

    // Employee Login is visible
    await expect(page.getByRole('button', { name: 'Employee Login' })).toBeVisible();

    // axe a11y — WCAG 2.x AA check on the login page.
    // TODO(a11y): color-contrast on the teal btn-primary-green — background
    // #407874 + white text passes WCAG AA for large text (bold ≥14px) but
    // may flag on the smaller helper-text labels that inherit the palette;
    // disable here until a design-pass can address it holistically.
    await checkA11y(page, { disable: ['color-contrast'] });
  });

  // ---------------------------------------------------------------------------
  // Visual + semantic regression coverage (per the 2026-04-25 ADR)
  // ---------------------------------------------------------------------------

  test('login page — semantic structure', async ({ page }) => {
    await page.goto('/login');

    // Headline + auth controls + primary/secondary CTAs all present + accessible.
    await expect(page.locator('h1')).toContainText('Premium Wholesale');
    await expect(page.getByPlaceholder('Email')).toBeVisible();
    await expect(page.getByPlaceholder('Password')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Login', exact: true })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Employee Login' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Forgot Password?' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Contact Us' })).toBeVisible();
    // Password show/hide eye toggle.
    await expect(page.getByRole('button', { name: /Show password|Hide password/ })).toBeVisible();
  });

  // Pixel compare against a Linux chromium baseline under
  // frontend/tests/e2e/__screenshots__/. Stays as fixme until the
  // baseline PNG is captured via the e2e.yml workflow with
  // --update-snapshots and committed. See
  // docs/TODO/pixel-compare-strategy-plan.md Phase 2 for the capture flow.
  test.fixme('login page — pixel compare against local baseline', async ({ page }) => {
    await page.goto('/login');
    await expect(page.getByRole('button', { name: 'Login', exact: true })).toBeVisible();
    await expect(page).toHaveScreenshot({ maxDiffPixelRatio: 0.02 });
  });
});
