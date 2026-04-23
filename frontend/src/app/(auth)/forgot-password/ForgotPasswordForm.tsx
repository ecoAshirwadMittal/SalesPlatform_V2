'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import styles from './forgotPassword.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

/**
 * Forgot-password form.
 *
 * On submit, POSTs to /api/v1/auth/forgot-password. The backend always returns
 * 200 regardless of whether the email maps to an account (enumeration-resistant),
 * so we always show the same generic success message to the user.
 */
export default function ForgotPasswordForm() {
  const [email, setEmail] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await apiFetch(`${API_BASE}/auth/forgot-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });
      // Always show generic success — do NOT reveal whether the email exists
      setSubmitted(true);
    } catch {
      setError('A network error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

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

      <h1 className={styles.heading}>Reset Your Password</h1>
      <p className={styles.subtext}>
        Enter your email address and we&apos;ll send you a link to reset your
        password.
      </p>

      {submitted ? (
        <>
          <div className={`${styles.message} ${styles.success}`}>
            If an account exists with that email, a reset link has been sent.
          </div>
          <Link href="/login" className={styles.backLink}>
            Back to Login
          </Link>
        </>
      ) : (
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.inputWrapper}>
            <input
              id="email"
              type="email"
              required
              className={styles.input}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email address"
              autoComplete="email"
            />
          </div>

          <button type="submit" className={styles.button} disabled={loading}>
            {loading ? 'Sending…' : 'Send Reset Link'}
          </button>

          <Link href="/login" className={styles.backLink}>
            Back to Login
          </Link>

          {error && <div className={`${styles.message} ${styles.error}`}>{error}</div>}
        </form>
      )}
    </div>
  );
}
