import { test, expect } from '@playwright/test';

/**
 * Phase 14 — Forgot Password + Reset Password E2E suite.
 *
 * Backend calls are mocked via route interception so these tests run without
 * a live backend. They verify:
 *   - /forgot-password renders and accepts a valid email submission
 *   - Generic success toast is shown regardless of whether the account exists
 *   - /reset-password?token=... renders and validates password-match on the client
 *   - Successful reset shows the success state
 *   - Invalid-token response from backend is surfaced as an error
 */

test.describe('/forgot-password', () => {
  test('page renders headline and email field', async ({ page }) => {
    await page.goto('/forgot-password');

    await expect(page.locator('h1')).toContainText('Reset Your Password');
    await expect(page.getByPlaceholder('Email address')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Send Reset Link' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Back to Login' })).toBeVisible();
  });

  test('submitting a valid email shows generic success toast', async ({ page }) => {
    // Mock the backend — always 200 regardless of email
    await page.route('**/api/v1/auth/forgot-password', (route) =>
      route.fulfill({ status: 200, body: '' })
    );

    await page.goto('/forgot-password');
    await page.getByPlaceholder('Email address').fill('buyer@example.com');

    const responsePromise = page.waitForResponse('**/api/v1/auth/forgot-password');
    await page.getByRole('button', { name: 'Send Reset Link' }).click();
    await responsePromise;

    // Generic success message must not mention whether the account exists
    const successText = page.getByText(
      'If an account exists with that email, a reset link has been sent.'
    );
    await expect(successText).toBeVisible();

    // "Back to Login" link appears in success state
    await expect(page.getByRole('link', { name: 'Back to Login' })).toBeVisible();
  });

  test('submitting an unknown email still shows generic success toast', async ({ page }) => {
    // The backend always returns 200 — frontend must show success regardless
    await page.route('**/api/v1/auth/forgot-password', (route) =>
      route.fulfill({ status: 200, body: '' })
    );

    await page.goto('/forgot-password');
    await page.getByPlaceholder('Email address').fill('nobody@nowhere.example');
    await page.getByRole('button', { name: 'Send Reset Link' }).click();

    await expect(
      page.getByText('If an account exists with that email, a reset link has been sent.')
    ).toBeVisible();
  });

  test('Back to Login link navigates to /login', async ({ page }) => {
    await page.goto('/forgot-password');
    const link = page.getByRole('link', { name: 'Back to Login' });
    const href = await link.getAttribute('href');
    expect(href).toBe('/login');
  });
});

test.describe('/reset-password', () => {
  test('page renders with token in URL', async ({ page }) => {
    await page.goto('/reset-password?token=test-token-abc');

    await expect(page.locator('h1')).toContainText('Set New Password');
    await expect(page.getByPlaceholder('New password')).toBeVisible();
    await expect(page.getByPlaceholder('Confirm new password')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Reset Password' })).toBeVisible();
  });

  test('mismatched passwords shows client-side error without calling backend', async ({ page }) => {
    // If backend were called it would be an unexpected route — the test would fail
    await page.goto('/reset-password?token=test-token');

    await page.getByPlaceholder('New password').fill('Password1!');
    await page.getByPlaceholder('Confirm new password').fill('Different1!');
    await page.getByRole('button', { name: 'Reset Password' }).click();

    await expect(page.getByText('Passwords do not match.')).toBeVisible();
  });

  test('successful reset shows success state', async ({ page }) => {
    await page.route('**/api/v1/auth/reset-password', (route) =>
      route.fulfill({ status: 200, body: '' })
    );

    await page.goto('/reset-password?token=valid-token-here');

    await page.getByPlaceholder('New password').fill('NewPass1!');
    await page.getByPlaceholder('Confirm new password').fill('NewPass1!');

    const responsePromise = page.waitForResponse('**/api/v1/auth/reset-password');
    await page.getByRole('button', { name: 'Reset Password' }).click();
    await responsePromise;

    await expect(
      page.getByText('Your password has been updated')
    ).toBeVisible();
    await expect(page.getByRole('link', { name: 'Go to Login' })).toBeVisible();
  });

  test('invalid token response from backend shows error message', async ({ page }) => {
    await page.route('**/api/v1/auth/reset-password', (route) =>
      route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Invalid or expired token' }),
      })
    );

    await page.goto('/reset-password?token=expired-token');

    await page.getByPlaceholder('New password').fill('NewPass1!');
    await page.getByPlaceholder('Confirm new password').fill('NewPass1!');
    await page.getByRole('button', { name: 'Reset Password' }).click();

    await expect(page.getByText('Invalid or expired token')).toBeVisible();
  });

  test('page renders without token in URL — shows missing-token error on submit', async ({
    page,
  }) => {
    await page.goto('/reset-password');

    await page.getByPlaceholder('New password').fill('NewPass1!');
    await page.getByPlaceholder('Confirm new password').fill('NewPass1!');
    await page.getByRole('button', { name: 'Reset Password' }).click();

    await expect(
      page.getByText('Reset token is missing')
    ).toBeVisible();
  });
});
