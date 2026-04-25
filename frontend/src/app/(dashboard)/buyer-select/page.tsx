'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import styles from './buyerSelect.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';
import {
  setActiveBuyerCode,
  resolveShellRoute,
  type ActiveBuyerCode,
} from '@/lib/activeBuyerCode';

/**
 * Buyer Code Select page — pixel-match of legacy Mendix Buyer_Code_Select.
 *
 * Phase 2 changes:
 *   - Category sections are conditional: PWS section only if user has PWS codes;
 *     Auction section only if user has AUCTION codes. Pure-auction users see
 *     exactly one section — matches qa-02-buyer-code-picker.png.
 *   - Single-code short-circuit: if the user has exactly one code, skip the
 *     picker and deep-link directly to the appropriate shell.
 *   - Storage migrated from sessionStorage → localStorage (key: activeBuyerCode).
 *   - Routing: AUCTION → /bidder/dashboard?buyerCodeId=…
 *              PWS    → /pws/order?buyerCodeId=…
 *   - codeType field from backend API drives all category and routing logic.
 *
 * Phase 3 changes:
 *   - setActiveBuyerCode and resolveShellRoute extracted to lib/activeBuyerCode.ts.
 *   - console.error replaced with a proper loadError state rendered in the UI.
 *   - Image priority: PWS image gets priority only when it's the sole section
 *     (no auction section); auction image gets priority only when it's the
 *     sole section (no PWS section). When both sections coexist neither is
 *     above-the-fold, so priority=false on both avoids unnecessary preloads.
 *   - Non-interactive userIconWrapper div: aria-label="User menu" dropped
 *     because the div is not interactive. UserAvatarPopover (Phase 4) will
 *     own that a11y role when it is wired into the top-bar.
 */

// Re-export the BuyerCode shape from activeBuyerCode so local code
// continues to reference a concrete type without re-declaring it.
type BuyerCode = ActiveBuyerCode;

interface UserInfo {
  userId: number;
  firstName: string;
  lastName: string;
}

function getAuthUser(): UserInfo | null {
  if (typeof window === 'undefined') return null;
  try {
    const stored = localStorage.getItem('auth_user');
    if (stored) return JSON.parse(stored) as UserInfo;
  } catch {
    /* ignore malformed stored value */
  }
  return null;
}

export default function BuyerSelectPage() {
  const router = useRouter();
  const [user, setUser] = useState<UserInfo | null>(null);
  const [pwsCodes, setPwsCodes] = useState<BuyerCode[]>([]);
  const [auctionCodes, setAuctionCodes] = useState<BuyerCode[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    loadBuyerCodes();
    // loadBuyerCodes is defined below and stable for the lifetime of this render.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function loadBuyerCodes() {
    try {
      const authUser = getAuthUser();
      if (!authUser) {
        router.push('/login');
        return;
      }
      setUser(authUser);

      const res = await apiFetch(`${API_BASE}/auth/buyer-codes?userId=${authUser.userId}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const codes: BuyerCode[] = await res.json();

      // Single-code fast-path: skip the picker entirely and deep-link to the shell.
      // router.replace() keeps the picker out of the history stack so Back doesn't
      // return here.
      if (codes.length === 1) {
        setActiveBuyerCode(codes[0]);
        router.replace(resolveShellRoute(codes[0]));
        return;
      }

      // Split by the codeType field computed on the backend.
      // Mapping: Premium_Wholesale → PWS; everything else → AUCTION.
      setPwsCodes(codes.filter(c => c.codeType === 'PWS'));
      setAuctionCodes(codes.filter(c => c.codeType === 'AUCTION'));
    } catch (err) {
      // Surface the error in the UI instead of silently logging to the console.
      const message = err instanceof Error ? err.message : 'Unknown error';
      setLoadError(`Unable to load buyer codes: ${message}. Please refresh or contact support.`);
    } finally {
      setLoading(false);
    }
  }

  function handleCodeSelect(code: BuyerCode): void {
    setActiveBuyerCode(code);
    router.push(resolveShellRoute(code));
  }

  if (loading) {
    return (
      <div className={styles.pageWrapper}>
        <header className={styles.header}>
          <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={120} height={28} className={styles.logo} />
          {/* User icon placeholder — UserAvatarPopover will replace this in Phase 4 */}
          <div className={styles.userIconWrapper}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              width="18"
              height="18"
              fill="none"
              stroke="#2a8a7a"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <circle cx="12" cy="8" r="4" />
              <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
            </svg>
          </div>
        </header>
        <div className={styles.contentArea}>
          <p className={styles.loadingText}>Loading...</p>
        </div>
      </div>
    );
  }

  // Show the error state if the buyer-code fetch failed.
  if (loadError) {
    return (
      <div className={styles.pageWrapper}>
        <header className={styles.header}>
          <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={120} height={28} className={styles.logo} />
        </header>
        <div className={styles.contentArea}>
          <p className={styles.errorState} role="alert">{loadError}</p>
        </div>
      </div>
    );
  }

  const hasPws = pwsCodes.length > 0;
  const hasAuction = auctionCodes.length > 0;

  // Image priority: when only one section is visible its hero image is
  // above-the-fold and warrants a preload (priority=true). When both sections
  // coexist both images sit below the welcome heading and neither is the
  // single most important LCP candidate, so both get priority=false to avoid
  // unnecessary preload contention.
  const pwsImagePriority = hasPws && !hasAuction;
  const auctionImagePriority = hasAuction && !hasPws;

  return (
    <div className={styles.pageWrapper}>
      {/* Header — dark teal bar, ecoATM wordmark left, user avatar right.
          The user icon div is presentational only — UserAvatarPopover (Phase 4)
          will replace it with an interactive button. aria-label is intentionally
          absent here because the element is non-interactive. */}
      <header className={styles.header}>
        <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={120} height={28} className={styles.logo} />
        <div className={styles.userIconWrapper}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            width="18"
            height="18"
            fill="none"
            stroke="#2a8a7a"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
          >
            <circle cx="12" cy="8" r="4" />
            <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
          </svg>
        </div>
      </header>

      {/* Content */}
      <main className={styles.contentArea}>
        <h1 className={styles.welcomeHeading}>
          Welcome {user?.firstName} {user?.lastName}!
        </h1>
        <p className={styles.subtitle}>Choose a buyer code to get started:</p>

        <div className={styles.sectionsWrapper}>
          {/* PWS Section — only rendered when the user has at least one PWS code */}
          {hasPws && (
            <section className={styles.categorySection} aria-label="Premium Wholesale Devices">
              <Image
                src="/images/pws_card.png"
                alt=""
                width={460}
                height={200}
                className={styles.heroImage}
                priority={pwsImagePriority}
              />
              <div className={styles.sectionBody}>
                <h2 className={styles.sectionTitle}>Premium Wholesale Devices</h2>
                <p className={styles.sectionTagline}>Shop consumer-ready, fully functional devices.</p>
                <ul className={styles.codeList} role="list">
                  {pwsCodes.map((bc) => (
                    <li key={bc.id}>
                      <button
                        className={styles.codePill}
                        onClick={() => handleCodeSelect(bc)}
                        type="button"
                      >
                        <span className={styles.codeInfo}>
                          <span className={styles.codeLabel}>{bc.code}</span>
                          <span className={styles.companyName}>{bc.buyerName}</span>
                        </span>
                        <span className={styles.arrowIcon} aria-hidden="true">→</span>
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            </section>
          )}

          {/* Auction Section — only rendered when the user has at least one AUCTION code */}
          {hasAuction && (
            <section className={styles.categorySection} aria-label="Weekly Wholesale Auction">
              <Image
                src="/images/auction_card.png"
                alt=""
                width={460}
                height={200}
                className={styles.heroImage}
                priority={auctionImagePriority}
                unoptimized
              />
              <div className={styles.sectionBody}>
                <h2 className={styles.sectionTitle}>Weekly Wholesale Auction</h2>
                <p className={styles.sectionTagline}>Bid on devices ready for uplift, repair, &amp; resale.</p>
                <ul className={styles.codeList} role="list">
                  {auctionCodes.map((bc) => (
                    <li key={bc.id}>
                      <button
                        className={styles.codePill}
                        onClick={() => handleCodeSelect(bc)}
                        type="button"
                      >
                        <span className={styles.codeInfo}>
                          <span className={styles.codeLabel}>{bc.code}</span>
                          <span className={styles.companyName}>{bc.buyerName}</span>
                        </span>
                        <span className={styles.arrowIcon} aria-hidden="true">→</span>
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            </section>
          )}
        </div>

        {/* Empty-state fallback — shown when the API returns no codes at all */}
        {!hasPws && !hasAuction && (
          <p className={styles.emptyState}>
            No active buyer codes are assigned to your account. Please contact your
            sales representative.
          </p>
        )}
      </main>
    </div>
  );
}
