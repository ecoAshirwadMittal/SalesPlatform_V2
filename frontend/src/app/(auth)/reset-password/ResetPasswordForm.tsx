'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import styles from './resetPassword.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

/** Eye icon — password visible */
function EyeOpenIcon() {
  return (
    <svg
      className={styles.toggleIcon}
      viewBox="0 0 20 20"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      focusable="false"
    >
      <path
        d="M10 4C5.5 4 1.7 6.9 0 11c1.7 4.1 5.5 7 10 7s8.3-2.9 10-7c-1.7-4.1-5.5-7-10-7z"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinejoin="round"
      />
      <circle cx="10" cy="11" r="3" stroke="currentColor" strokeWidth="1.5" />
    </svg>
  );
}

/** Eye-off icon — password hidden */
function EyeOffIcon() {
  return (
    <svg
      className={styles.toggleIcon}
      viewBox="0 0 20 20"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      focusable="false"
    >
      <path
        d="M2 2l16 16M8.5 5.2A9.8 9.8 0 0110 5c4.5 0 8.3 2.9 10 7a10.1 10.1 0 01-2.5 3.6M13.7 13.8A4 4 0 016.2 8.2M10 5c-4.5 0-8.3 2.9-10 7a10.1 10.1 0 003.3 4.4"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

/**
 * Reset-password form.
 *
 * Reads the `?token=` query parameter from the URL — this is the raw token
 * included in the reset link logged by PasswordResetService (Phase 14 dev flow).
 *
 * Client-side validation ensures the two password fields match before sending
 * the request to /api/v1/auth/reset-password.
 */
export default function ResetPasswordForm() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token') ?? '';

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [confirmError, setConfirmError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setConfirmError(null);

    if (newPassword !== confirmPassword) {
      setConfirmError('Passwords do not match.');
      return;
    }

    if (!token) {
      setError('Reset token is missing. Please use the link from your email.');
      return;
    }

    setLoading(true);

    try {
      const response = await apiFetch(`${API_BASE}/auth/reset-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token, newPassword }),
      });

      if (response.ok) {
        setSubmitted(true);
      } else {
        // Backend returns 400 with { message: "Invalid or expired token" }
        const data = await response.json().catch(() => ({}));
        setError(
          (data as { message?: string }).message ||
            'Unable to reset password. The link may have expired.'
        );
      }
    } catch {
      setError('A network error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (submitted) {
    return (
      <div className={styles.card}>
        <Image
          src="/qa_logo.png"
          alt="ecoATM Logo"
          width={160}
          height={54}
          priority
          className={styles.logo}
        />
        <h1 className={styles.heading}>Password Reset</h1>
        <div className={`${styles.message} ${styles.success}`}>
          Your password has been updated. You can now log in with your new password.
        </div>
        <Link href="/login" className={styles.backLink}>
          Go to Login
        </Link>
      </div>
    );
  }

  return (
    <div className={styles.card}>
      <Image
        src="/qa_logo.png"
        alt="ecoATM Logo"
        width={160}
        height={54}
        priority
        className={styles.logo}
      />

      <h1 className={styles.heading}>Set New Password</h1>
      <p className={styles.subtext}>
        Choose a new password for your account. It must be at least 8 characters
        long.
      </p>

      <form onSubmit={handleSubmit} className={styles.form}>
        {/* New password */}
        <div className={styles.inputWrapper}>
          <input
            id="newPassword"
            type={showNew ? 'text' : 'password'}
            required
            minLength={8}
            className={`${styles.input} ${styles.inputWithToggle}`}
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder="New password"
            autoComplete="new-password"
          />
          <button
            type="button"
            className={styles.passwordToggle}
            onClick={() => setShowNew(!showNew)}
            aria-pressed={showNew}
            aria-label={showNew ? 'Hide password' : 'Show password'}
          >
            {showNew ? <EyeOffIcon /> : <EyeOpenIcon />}
          </button>
        </div>

        {/* Confirm password */}
        <div className={styles.inputWrapper}>
          <input
            id="confirmPassword"
            type={showConfirm ? 'text' : 'password'}
            required
            minLength={8}
            className={`${styles.input} ${styles.inputWithToggle}`}
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm new password"
            autoComplete="new-password"
          />
          <button
            type="button"
            className={styles.passwordToggle}
            onClick={() => setShowConfirm(!showConfirm)}
            aria-pressed={showConfirm}
            aria-label={showConfirm ? 'Hide password' : 'Show password'}
          >
            {showConfirm ? <EyeOffIcon /> : <EyeOpenIcon />}
          </button>
        </div>

        {confirmError && (
          <p className={styles.fieldError}>{confirmError}</p>
        )}

        <button type="submit" className={styles.button} disabled={loading}>
          {loading ? 'Resetting…' : 'Reset Password'}
        </button>

        <Link href="/login" className={styles.backLink}>
          Back to Login
        </Link>

        {error && <div className={`${styles.message} ${styles.error}`}>{error}</div>}
      </form>
    </div>
  );
}
