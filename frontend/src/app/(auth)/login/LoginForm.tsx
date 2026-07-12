'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import styles from './login.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

/**
 * Password-toggle eye — the exact legacy asset
 * (AuctionUI$Image_collection$font_awesome_icon_2.svg, 23×20, fill #7D7B7A).
 * Legacy wires this SAME svg for both the shown and hidden states
 * (Login_New toggleShowPassword1 eyeOpen == eyeClosed), so we do too;
 * the toggled state is communicated via aria-pressed/aria-label instead.
 */
function LegacyEyeIcon() {
  return (
    <svg
      className={styles.toggleIcon}
      width="23"
      height="20"
      viewBox="0 0 23 20"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      focusable="false"
    >
      <path
        d="M11.5 3.125C8.92188 3.125 6.85156 4.29688 5.25 5.78125C3.72656 7.1875 2.71094 8.82812 2.16406 10C2.71094 11.1719 3.72656 12.8516 5.25 14.2578C6.85156 15.7422 8.92188 16.875 11.5 16.875C14.0391 16.875 16.1094 15.7422 17.7109 14.2578C19.2344 12.8516 20.2891 11.1719 20.7969 10C20.2891 8.82812 19.2344 7.1875 17.75 5.78125C16.1094 4.29688 14.0391 3.125 11.5 3.125ZM3.96094 4.41406C5.79688 2.69531 8.33594 1.25 11.5 1.25C14.625 1.25 17.1641 2.69531 19 4.41406C20.8359 6.13281 22.0469 8.125 22.6328 9.53125C22.75 9.84375 22.75 10.1953 22.6328 10.5078C22.0469 11.875 20.8359 13.9062 19 15.625C17.1641 17.3438 14.625 18.75 11.5 18.75C8.33594 18.75 5.79688 17.3438 3.96094 15.625C2.125 13.9062 0.914062 11.875 0.328125 10.5078C0.210938 10.1953 0.210938 9.84375 0.328125 9.53125C0.914062 8.125 2.125 6.09375 3.96094 4.41406ZM11.5 13.125C13.2188 13.125 14.625 11.7578 14.625 10C14.625 8.28125 13.2188 6.875 11.5 6.875C11.4609 6.875 11.4219 6.875 11.4219 6.875C11.4609 7.10938 11.5 7.30469 11.5 7.5C11.5 8.90625 10.3672 10 9 10C8.76562 10 8.57031 10 8.375 9.92188C8.375 9.96094 8.375 10 8.375 10C8.375 11.7578 9.74219 13.125 11.5 13.125ZM11.5 5C13.2578 5 14.8984 5.97656 15.7969 7.5C16.6953 9.0625 16.6953 10.9766 15.7969 12.5C14.8984 14.0625 13.2578 15 11.5 15C9.70312 15 8.0625 14.0625 7.16406 12.5C6.26562 10.9766 6.26562 9.0625 7.16406 7.5C8.0625 5.97656 9.70312 5 11.5 5Z"
        fill="#7D7B7A"
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

  const handleContactUs = () => {
    // Legacy Login_New "Contact Us" is an openLink (web schema) to the ecoATM
    // B2B site — see EcoATM_UserManagement.Login_New.actionButtonSignIn1.
    window.open(
      'https://www.ecoatmb2b.com/wholesale-devices#contact-us',
      '_blank',
      'noopener,noreferrer',
    );
  };

  return (
    <>
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
        {/* Legacy renders the logo at its natural 200×40 (byte-identical asset;
            measured glyph box 195×34 @1080p). 180×60 distorted the box. */}
        <Image
          src="/qa_logo.png"
          alt="ecoATM Logo"
          width={200}
          height={40}
          priority
          className={styles.loginLogoImage}
        />

        <h1 className={styles.mainHeaderText}>
          Premium Wholesale &<br />Weekly Auctions
        </h1>

        <form onSubmit={handleSubmit} className={styles.formBody}>
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
                  <LegacyEyeIcon />
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

          {/* Footer Contacts Section (legacy .contact-us-container) */}
          <div className={styles.divider}></div>
          <span className={styles.subHeaderText}>Interested but don&rsquo;t have an account?</span>
          <button
            type="button"
            onClick={handleContactUs}
            className={`${styles.loginbutton} ${styles.loginbuttonNoMargin}`}
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

      {/* Footer (legacy .policy-footer) — sibling below the card, on the gradient.
          Copyright year is the current year, mirroring the legacy Mendix
          expression: "© " + formatDateTime(currentDateTime, "yyyy") + " …". */}
      <footer className={styles.policyFooter}>
        <a
          className={styles.policyText}
          href="https://www.ecoatm.com/pages/privacy-policy"
          target="_blank"
          rel="noopener noreferrer"
        >
          Privacy Policy
        </a>
        <span className={styles.copyrightText}>
          © {new Date().getFullYear()} ecoATM, LLC. All Rights Reserved.
        </span>
      </footer>
    </>
  );
}
