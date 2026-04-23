'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import styles from './login.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

/** Eye icon — "show password" state (eye open) */
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

/** Eye-off icon — "hide password" state (eye with slash) */
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

export default function LoginForm() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isInternalUser = email.toLowerCase().endsWith('@ecoatm.com');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (isInternalUser) {
      window.location.href = '/api/v1/auth/sso?target=azuread';
      return;
    }

    if (!password) {
      setError('Please enter your password.');
      return;
    }

    try {
      const response = await apiFetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, rememberMe }),
      });

      if (response.ok) {
        const data = await response.json();
        // The JWT is delivered as an HttpOnly cookie by AuthController and
        // is not readable by JS. We only cache the user profile locally
        // for top-bar display (SNP_UserInfoDisplay) — never the token.
        if (data.user) localStorage.setItem('auth_user', JSON.stringify(data.user));

        // Redirect to returnTo if present, otherwise route by role
        const params = new URLSearchParams(window.location.search);
        const returnTo = params.get('returnTo');
        if (returnTo && returnTo.startsWith('/') && !returnTo.startsWith('//')) {
          router.push(returnTo);
        } else {
          const roles: string[] = data.user?.roles ?? [];
          const isBuyerOnly = roles.length > 0 && roles.every(r => r === 'Bidder');
          // Bidder users pick a buyer code at /buyer-select; the picker handles
          // skip-to-shell routing for single-code users (Phase 2 completes this).
          router.push(isBuyerOnly ? '/buyer-select' : '/users');
        }
      } else {
        const data = await response.json();
        setError(data.message || 'Incorrect email or password');
      }
    } catch {
      setError('A network error occurred. Please try again.');
    }
  };

  const handleEmployeeLogin = () => {
    window.location.href = '/api/v1/auth/sso?target=azuread';
  };

  return (
    <div className={styles.loginCard}>
      {/* Left Column Background Image */}
      <div className={styles.leftSide}>
        <Image
          src="/qa_side_image.png"
          alt="ecoATM Device Sorting"
          fill
          priority
          className={styles.leftImage}
        />
      </div>

      {/* Right Column Form */}
      <div className={styles.rightSide}>
        <Image
          src="/qa_logo.png"
          alt="ecoATM Logo"
          width={180}
          height={60}
          priority
          className={styles.loginLogoImage}
        />

        <h1 className={styles.mainHeaderText}>
          Premium Wholesale &<br />Weekly Auctions
        </h1>

        <form onSubmit={handleSubmit} style={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <div className={styles.formGroup}>
            <div className={styles.inputWrapper}>
              <input
                id="email"
                type="email"
                required
                className={styles.input}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Email"
              />
            </div>
          </div>

          {!isInternalUser && (
            <div className={styles.formGroup}>
              <div className={styles.inputWrapper}>
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  required={!isInternalUser}
                  className={`${styles.input} ${styles.inputWithToggle}`}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
                />
                {/* Keyboard-accessible eye toggle — tabIndex default (0) so Tab reaches it.
                    Uses aria-pressed to communicate toggled state to screen readers. */}
                <button
                  type="button"
                  className={styles.passwordToggle}
                  onClick={() => setShowPassword(!showPassword)}
                  aria-pressed={showPassword}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? <EyeOffIcon /> : <EyeOpenIcon />}
                </button>
              </div>
            </div>
          )}

          {!isInternalUser && (
            <div className={styles.actions}>
              <div className={styles.checkboxContainer}>
                <input
                  id="remember"
                  type="checkbox"
                  className={styles.checkbox}
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                />
                <label htmlFor="remember" className={styles.checkboxLabel}>
                  Remember me?
                </label>
              </div>
              <Link href="/forgot-password" className={styles.forgotPassword}>
                Forgot Password?
              </Link>
            </div>
          )}

          <button type="submit" className={styles.loginbutton}>
            {isInternalUser ? 'Login with ecoATM SSO' : 'Login'}
          </button>

          {!isInternalUser && (
            <button
              type="button"
              onClick={handleEmployeeLogin}
              className={styles.loginbuttonemployee}
            >
              Employee Login
            </button>
          )}

          {/* Footer Contacts Section */}
          <div className={styles.divider}></div>
          <span className={styles.subHeaderText}>Interested but don&apos;t have an account?</span>
          {/* Contact URL TBD — no-op on click, matching QA behaviour */}
          <button
            type="button"
            className={styles.loginbutton}
            style={{ marginBottom: 0 }}
            onClick={(e) => e.preventDefault()}
          >
            Contact Us
          </button>

          {error && <div className={`${styles.message} ${styles.error}`}>{error}</div>}
          {isInternalUser && (
            <div className={`${styles.message} ${styles.neutral}`}>
              You will be redirected to Azure AD to sign in.
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
