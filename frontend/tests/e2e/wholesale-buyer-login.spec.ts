import { test, expect } from '@playwright/test';

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

test.describe('Wholesale buyer login', () => {
  test('bidder can log in and lands on /buyer-select', async ({ page }) => {
    await page.goto('/login');

    // Verify the page headline is present and correct
    await expect(page.locator('h1')).toContainText('Premium Wholesale');

    await page.getByPlaceholder('Email').fill('bidder@buyerco.com');
    await page.getByPlaceholder('Password').fill('Bidder123!');
    await page.getByRole('button', { name: 'Login', exact: true }).click();

    // Phase 1: Bidder role routes to /buyer-select (the code picker).
    // Single-code deep-linking is Phase 2's job.
    await expect(page).toHaveURL(/\/buyer-select/, { timeout: 15_000 });
  });

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

  test('login page renders with correct visual tokens (smoke)', async ({ page }) => {
    await page.goto('/login');

    // Verify the login card is present and the primary CTA button is visible.
    await expect(page.getByRole('button', { name: 'Login', exact: true })).toBeVisible();

    // Contact Us button exists and is a button (not a link)
    const contactUs = page.getByRole('button', { name: 'Contact Us' });
    await expect(contactUs).toBeVisible();

    // Employee Login is visible
    await expect(page.getByRole('button', { name: 'Employee Login' })).toBeVisible();
  });
});
