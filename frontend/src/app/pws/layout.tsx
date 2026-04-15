'use client';

import { usePathname, useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { useState, useEffect, useRef } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';
import { getAuthUser, getSelectedBuyerCode, type AuthUser } from '@/lib/session';
import type { NavItem } from '@/lib/types';
import styles from './pws.module.css';

/**
 * PWS Layout — matches Mendix EcoATM_PWS_Sales layout
 * Validated against QA: https://buy-qa.ecoatmdirect.com
 *
 * Sidebar has two sections (SALES / BUYERS) with nav items from PWS_Menu.
 * Top bar: dark background, "ecoATM" logo + "Premium Wholesale" teal text.
 * User dropdown shows name, email, "Switch to Wholesale", "Submit Feedback", "Logout".
 */

// SALES section nav items
const salesNavItems: NavItem[] = [
  {
    label: 'Inventory',
    href: '/pws/inventory',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
        <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
        <line x1="12" y1="22.08" x2="12" y2="12"/>
      </svg>
    ),
  },
  {
    label: 'Offer Review',
    href: '/pws/offer-review',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
        <polyline points="14 2 14 8 20 8"/>
        <line x1="12" y1="18" x2="12" y2="12"/>
        <line x1="9" y1="15" x2="15" y2="15"/>
      </svg>
    ),
  },
  {
    label: 'RMA Review',
    href: '/pws/rma-review',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/>
        <rect x="8" y="2" width="8" height="4" rx="1" ry="1"/>
        <path d="M9 14l2 2 4-4"/>
      </svg>
    ),
  },
  {
    label: 'Pricing',
    href: '/pws/pricing',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="12" y1="1" x2="12" y2="23"/>
        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
      </svg>
    ),
  },
];

// BUYERS section nav items
const buyersNavItems: NavItem[] = [
  {
    label: 'Shop',
    href: '/pws/order',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
      </svg>
    ),
  },
  {
    label: 'Counters',
    href: '/pws/counter-offers',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M17 1l4 4-4 4"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/>
        <path d="M7 23l-4-4 4-4"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/>
      </svg>
    ),
  },
  {
    label: 'Orders',
    href: '/pws/orders',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
        <polyline points="14 2 14 8 20 8"/>
        <line x1="16" y1="13" x2="8" y2="13"/>
        <line x1="16" y1="17" x2="8" y2="17"/>
        <polyline points="10 9 9 9 8 9"/>
      </svg>
    ),
  },
  {
    label: 'RMAs',
    href: '/pws/rma-requests',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <polyline points="1 4 1 10 7 10"/>
        <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/>
      </svg>
    ),
  },
  {
    label: "FAQ's",
    href: '/pws/faqs',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="10"/>
        <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
        <line x1="12" y1="17" x2="12.01" y2="17"/>
      </svg>
    ),
  },
  {
    label: 'Grading',
    href: '/pws/grading',
    icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="10"/>
        <line x1="12" y1="16" x2="12" y2="12"/>
        <line x1="12" y1="8" x2="12.01" y2="8"/>
      </svg>
    ),
  },
];

interface BuyerCodeOption {
  id: number;
  code: string;
  buyerName?: string;
  buyerCodeType?: string;
}

export default function PwsLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [user, setUser] = useState<AuthUser | null>(() => getAuthUser());
  const [buyerCodes, setBuyerCodes] = useState<BuyerCodeOption[]>([]);
  const [selectedBuyerCode, setSelectedBuyerCode] = useState<BuyerCodeOption | null>(
    () => getSelectedBuyerCode() as BuyerCodeOption | null
  );
  const [buyerDropdownOpen, setBuyerDropdownOpen] = useState(false);
  const [buyerSearch, setBuyerSearch] = useState('');
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [buyerCodeError, setBuyerCodeError] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const buyerDropdownRef = useRef<HTMLDivElement>(null);

  // Load buyer codes for the logged-in user
  useEffect(() => {
    if (!user) return;
    async function loadBuyerCodes() {
      try {
        setBuyerCodeError(null);
        const res = await apiFetch(`${API_BASE}/auth/buyer-codes?userId=${user!.userId}`);
        if (!res.ok) {
          setBuyerCodeError('Failed to load buyer codes');
          return;
        }
        const codes: BuyerCodeOption[] = await res.json();
        const pwsCodes = codes.filter(c =>
          c.buyerCodeType === 'Premium_Wholesale' || c.buyerCodeType === 'Wholesale'
        );
        setBuyerCodes(pwsCodes);

        // Auto-select first code if none selected
        const stored = getSelectedBuyerCode();
        if (!stored && pwsCodes.length > 0) {
          const first = { id: pwsCodes[0].id, code: pwsCodes[0].code };
          setSelectedBuyerCode(first);
          sessionStorage.setItem('selectedBuyerCode', JSON.stringify(first));
          window.dispatchEvent(new Event('buyerCodeChanged'));
        }
      } catch {
        setBuyerCodeError('Failed to load buyer codes');
      }
    }
    loadBuyerCodes();
  }, [user]);

  // Re-read buyer code when it changes (other pages may also write to sessionStorage)
  useEffect(() => {
    function handleStorage() {
      const bc = getSelectedBuyerCode();
      if (bc) setSelectedBuyerCode(bc as BuyerCodeOption);
    }
    window.addEventListener('buyerCodeChanged', handleStorage);
    return () => window.removeEventListener('buyerCodeChanged', handleStorage);
  }, []);

  // Close dropdowns on outside click
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
      if (buyerDropdownRef.current && !buyerDropdownRef.current.contains(e.target as Node)) {
        setBuyerDropdownOpen(false);
        setBuyerSearch('');
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  function handleBuyerCodeSwitch(bc: BuyerCodeOption) {
    const val = { id: bc.id, code: bc.code };
    setSelectedBuyerCode(val);
    sessionStorage.setItem('selectedBuyerCode', JSON.stringify(val));
    window.dispatchEvent(new Event('buyerCodeChanged'));
    setBuyerDropdownOpen(false);
    setBuyerSearch('');
  }

  const filteredBuyerCodes = buyerCodes.filter(bc =>
    bc.code.toLowerCase().includes(buyerSearch.toLowerCase()) ||
    (bc.buyerName && bc.buyerName.toLowerCase().includes(buyerSearch.toLowerCase()))
  );

  // Bidder-only users see only BUYERS nav; admin/salesops/etc see both sections
  const BUYER_ONLY_ROLES = ['Bidder'];
  const isBuyerOnly = user?.roles != null
    && user.roles.length > 0
    && user.roles.every(r => BUYER_ONLY_ROLES.includes(r));

  const displayName = user?.fullName || user?.email || 'User';
  const initials = user?.initials || displayName.slice(0, 2).toUpperCase();

  const handleLogout = async () => {
    // Expire the HttpOnly auth_token cookie server-side, then clear the
    // cached user profile. The token itself is never touched from JS.
    try {
      await apiFetch(`${API_BASE}/auth/logout`, { method: 'POST' });
    } catch {
      // Best-effort: even if the logout call fails, clear local state and
      // navigate to /login so the user isn't stranded on a protected page.
    }
    localStorage.removeItem('auth_user');
    // Broadcast so other open tabs also return to /login.
    try { new BroadcastChannel('auth').postMessage('logout'); } catch { /* unsupported */ }
    router.push('/login');
  };

  function renderNavItem(item: NavItem) {
    const isActive = pathname === item.href || pathname.startsWith(item.href + '/');
    return (
      <Link
        key={item.href}
        href={item.href}
        className={`${styles.navItem} ${isActive ? styles.navItemActive : ''}`}
      >
        <span className={styles.navIcon}>{item.icon}</span>
        <span className={styles.navLabel}>{item.label}</span>
      </Link>
    );
  }

  return (
    <div className={styles.pwsContainer}>
      {/* Top bar — spans full width above sidebar + content */}
      <header className={styles.topBar}>
          <div className={styles.topBarLeft}>
            <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={143} height={29} />
            <span className={styles.topBarBranding}>Premium Wholesale</span>
          </div>
          {buyerCodes.length > 0 && (
            <div className={styles.topBarCenter}>
              <div className={styles.buyerCodeWrapper} ref={buyerDropdownRef}>
                <span className={styles.buyerCodeLabel}>View As</span>
                <div className={styles.buyerCodeButton}>
                  <input
                    className={styles.buyerCodeInput}
                    type="text"
                    value={buyerDropdownOpen ? buyerSearch : (selectedBuyerCode?.code || '')}
                    placeholder="Select Buyer Code"
                    onChange={e => { setBuyerSearch(e.target.value); if (!buyerDropdownOpen) setBuyerDropdownOpen(true); }}
                    onFocus={() => { setBuyerSearch(''); setBuyerDropdownOpen(true); }}
                    aria-label="Search or select buyer code"
                  />
                  <button
                    type="button"
                    className={styles.buyerCodeChevron}
                    onClick={() => { setBuyerDropdownOpen(prev => !prev); setBuyerSearch(''); }}
                    tabIndex={-1}
                    aria-label="Toggle buyer code list"
                  >
                    <svg width="16" height="16" viewBox="0 0 32 32" fill="currentColor">
                      <path d="M16 23.41L4.29004 11.71L5.71004 10.29L16 20.59L26.29 10.29L27.71 11.71L16 23.41Z"/>
                    </svg>
                  </button>
                </div>
                {buyerDropdownOpen && (
                  <div className={styles.buyerCodeDropdown}>
                    <div className={styles.buyerCodeList}>
                      {filteredBuyerCodes.map(bc => (
                        <button
                          key={bc.id}
                          className={`${styles.buyerCodeItem} ${selectedBuyerCode?.id === bc.id ? styles.buyerCodeItemActive : ''}`}
                          onClick={() => handleBuyerCodeSwitch(bc)}
                        >
                          <span className={styles.buyerCodeItemCode}>{bc.code}</span>
                        </button>
                      ))}
                      {filteredBuyerCodes.length === 0 && (
                        <div className={styles.buyerCodeEmpty}>No matching codes</div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
          <div className={styles.topBarRight} ref={dropdownRef}>
            <button
              className={styles.userIconWrapper}
              onClick={() => setDropdownOpen(prev => !prev)}
              aria-label="User menu"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </button>

            {dropdownOpen && (
              <div className={styles.userDropdown}>
                <div className={styles.userDropdownHeader}>
                  <div className={styles.userDropdownName}>{displayName}</div>
                  {user?.email && <div className={styles.userDropdownEmail}>{user.email}</div>}
                </div>
                <button className={styles.userDropdownItem} onClick={() => { setDropdownOpen(false); router.push('/buyer-select'); }}>
                  Switch to Wholesale
                </button>
                <button className={styles.userDropdownItem} onClick={() => setDropdownOpen(false)}>
                  Submit Feedback
                </button>
                <button className={styles.userDropdownItem} onClick={handleLogout}>
                  Logout
                </button>
              </div>
            )}
          </div>
      </header>

      {/* Body row: sidebar + content below the top bar */}
      <div className={styles.bodyRow}>
        <aside className={styles.sidebar}>
          {!isBuyerOnly && (
            <>
              <div className={styles.sectionHeader}>Sales</div>
              {salesNavItems.map(renderNavItem)}
              <div className={styles.sectionHeader}>Buyers</div>
            </>
          )}
          {buyersNavItems.map(renderNavItem)}
        </aside>

        <main className={styles.contentArea}>
          {buyerCodeError && (
            <div style={{ padding: '8px 16px', background: '#fef2f2', color: '#991b1b', borderBottom: '1px solid #fecaca', fontSize: '14px' }}>
              {buyerCodeError}
            </div>
          )}
          {children}
        </main>
      </div>
    </div>
  );
}
