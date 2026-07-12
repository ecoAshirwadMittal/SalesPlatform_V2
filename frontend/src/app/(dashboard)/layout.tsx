'use client';

import { usePathname, useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { useState, useEffect, useRef } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';
import { getAuthUser, type AuthUser } from '@/lib/session';
import type { NavItem } from '@/lib/types';
import SidebarToggle from '@/components/chrome/SidebarToggle';
import styles from './dashboard.module.css';

const navItems: NavItem[] = [
  {
    label: 'Users', href: '/users',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
        <circle cx="9" cy="7" r="4"/>
        <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
      </svg>
    ),
  },
  {
    label: 'Buyers', href: '/buyers',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/>
        <path d="M16 3h-8l-2 4h12z"/>
      </svg>
    ),
  },
  {
    label: 'Inventory', href: '/admin/auctions-data-center/inventory',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
        <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
        <line x1="12" y1="22.08" x2="12" y2="12"/>
      </svg>
    ),
  },
  {
    label: 'Purchase Order', href: '/admin/auctions-data-center/purchase-orders',
    icon: <span className={styles.textBadge}>PO</span>,
  },
  {
    label: 'Reserved Bids (EB)', href: '/admin/auctions-data-center/reserve-bids',
    icon: <span className={styles.textBadge}>RB</span>,
  },
  {
    label: 'Auction Scheduling', href: '/admin/auctions-data-center/schedule-auction',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
        <line x1="16" y1="2" x2="16" y2="6"/>
        <line x1="8" y1="2" x2="8" y2="6"/>
        <line x1="3" y1="10" x2="21" y2="10"/>
        <circle cx="12" cy="16" r="2"/>
      </svg>
    ),
  },
  {
    label: 'Bid as Bidder', href: '/bid-as-bidder',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>
      </svg>
    ),
  },
  {
    label: 'Auction', href: '/auction',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="10"/>
        <polyline points="12 6 12 12 16 14"/>
      </svg>
    ),
  },
  // Credit Requests — legacy admin sidebar item between Auction and Reports
  // (SHELL-P1, ruling 1). Routes to the partial-credit admin surface. The
  // reply/return arrow mirrors the legacy Mendix glyph; rendered as a plain
  // stroke icon to match the sibling nav items' style.
  {
    label: 'Credit Requests', href: '/admin/auctions-data-center/partial-credit',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <polyline points="9 10 4 15 9 20"/>
        <path d="M20 4v7a4 4 0 0 1-4 4H4"/>
      </svg>
    ),
  },
  {
    label: 'Reports', href: '/reports', expandable: true,
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="18" y1="20" x2="18" y2="10"/>
        <line x1="12" y1="20" x2="12" y2="4"/>
        <line x1="6" y1="20" x2="6" y2="14"/>
      </svg>
    ),
  },
  {
    label: 'Settings', href: '/settings', expandable: true,
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="3"/>
        <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
      </svg>
    ),
    children: [
      { label: 'PWS Control Center', href: '/settings/pws-control-center' },
    ],
  },
  {
    label: 'Admin', href: '/admin', expandable: true,
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 2 4 6v6c0 5 3.5 9.5 8 10 4.5-.5 8-5 8-10V6l-8-4z"/>
      </svg>
    ),
    children: [
      { label: 'Application Control Center', href: '/admin/app-control-center' },
      { label: 'Auction Control Center', href: '/admin/auction-control-center' },
      { label: 'Auctions Data Center', href: '/admin/auctions-data-center' },
      { label: 'PWS Data Center', href: '/admin/pws-data-center' },
    ],
  },
  // M12a — QA admin sidebar exposes a "Buyer User Guide" link at the bottom.
  // The route hosts a stub page until real documentation lands.
  {
    label: 'Buyer User Guide', href: '/buyer-user-guide',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
      </svg>
    ),
  },
];

/**
 * Every navigable leaf href — top-level (non-expandable) items plus every
 * submenu child. The active nav item is the leaf whose href is the *longest*
 * prefix of the current path. This guarantees a single highlight: on
 * `/admin/auctions-data-center/reserve-bids` the "Reserved Bids (EB)" leaf
 * (len 40) wins over the "Auctions Data Center" submenu child (len 27), so the
 * page item and the submenu entry never light up together (SHELL-P1, ruling 3).
 */
function collectLeafHrefs(): string[] {
  const leaves: string[] = [];
  for (const item of navItems) {
    if (item.expandable && item.children && item.children.length > 0) {
      for (const child of item.children) leaves.push(child.href);
    } else {
      leaves.push(item.href);
    }
  }
  return leaves;
}

function computeActiveHref(pathname: string): string | null {
  const matches = collectLeafHrefs()
    .filter((href) => pathname === href || pathname.startsWith(href + '/'))
    .sort((a, b) => b.length - a.length);
  return matches[0] ?? null;
}

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  // Initialize as null so SSR and the first client render agree; load the
  // real user from localStorage only after mount to avoid hydration mismatch.
  const [user, setUser] = useState<AuthUser | null>(null);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  // Sidebar sections start collapsed and open only on explicit click (ruling 3).
  // No auto-expand from the current path.
  const [expandedMenus, setExpandedMenus] = useState<Set<string>>(() => new Set());
  // Whole-sidebar collapse — mirrors the legacy chrome's top-of-sidebar toggle.
  // Session-only (no persistence) to keep SSR hydration simple.
  const [collapsed, setCollapsed] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setUser(getAuthUser());
  }, []);

  const activeHref = computeActiveHref(pathname);

  function toggleMenu(label: string) {
    setExpandedMenus(prev => {
      const next = new Set(prev);
      if (next.has(label)) next.delete(label);
      else next.add(label);
      return next;
    });
  }

  // Close dropdown on outside click
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const handleLogout = async () => {
    try {
      await apiFetch(`${API_BASE}/auth/logout`, { method: 'POST' });
    } catch {
      // best effort — still clear local state and redirect
    }
    localStorage.removeItem('auth_user');
    try { new BroadcastChannel('auth').postMessage('logout'); } catch { /* unsupported */ }
    router.push('/login');
  };

  return (
    <div className={styles.dashboardContainer}>
      {/* Sidebar */}
      <aside className={`${styles.sidebar} ${collapsed ? styles.sidebarCollapsed : ''}`}>
        {/* Legacy chrome: a collapse toggle sits at the top of the sidebar
            (no logo — the logo lives in the content area, ruling 2). */}
        <div className={styles.sidebarHeader}>
          <SidebarToggle collapsed={collapsed} onToggle={() => setCollapsed(prev => !prev)} />
        </div>
        <nav className={styles.sidebarNav}>
          {navItems.map((item) => {
            const hasChildren = item.expandable && item.children && item.children.length > 0;
            const isExpanded = !collapsed && expandedMenus.has(item.label);
            // Only leaf items highlight, and only the single longest-prefix match.
            const isActive = !hasChildren && item.href === activeHref;

            if (hasChildren) {
              return (
                <div key={item.label}>
                  <button
                    className={`${styles.navItem} ${styles.navItemButton}`}
                    onClick={() => toggleMenu(item.label)}
                    aria-expanded={isExpanded}
                    title={collapsed ? item.label : undefined}
                  >
                    <span className={styles.navIcon}>{item.icon}</span>
                    <span className={styles.navLabel}>{item.label}</span>
                    <span className={`${styles.chevron} ${isExpanded ? styles.chevronOpen : ''}`}>
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
                    </span>
                  </button>
                  {isExpanded && (
                    <div className={styles.subMenu}>
                      {item.children!.map(child => (
                        <Link
                          key={child.href}
                          href={child.href}
                          className={`${styles.subMenuItem} ${child.href === activeHref ? styles.subMenuItemActive : ''}`}
                        >
                          {child.label}
                        </Link>
                      ))}
                    </div>
                  )}
                </div>
              );
            }

            return (
              <Link
                key={item.href + item.label}
                href={item.href}
                className={`${styles.navItem} ${isActive ? styles.navItemActive : ''}`}
                title={collapsed ? item.label : undefined}
              >
                <span className={styles.navIcon}>{item.icon}</span>
                <span className={styles.navLabel}>{item.label}</span>
                {item.expandable && (
                  <span className={styles.chevron}>
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
                  </span>
                )}
              </Link>
            );
          })}
        </nav>
      </aside>

      {/* Main content */}
      <div className={styles.mainArea}>
        {/* Top chrome — legacy parity (ruling 2): the ecoATM DIRECT logo sits in
            the content area top-left; a green status dot sits top-right. No
            white bar. The dot doubles as the (click-to-open) user menu so
            logout stays reachable — legacy shows no name/initials for admins. */}
        <header className={styles.topBar}>
          <div className={styles.topBarLogo}>
            <Image src="/images/ecoatm-direct-logo.png" alt="ecoATM DIRECT" width={119} height={46} priority />
          </div>
          <div className={styles.topBarRight} ref={dropdownRef}>
            <button
              className={styles.statusDot}
              onClick={() => setDropdownOpen(prev => !prev)}
              aria-label={user ? `User menu for ${user.fullName || user.email}` : 'User menu'}
              aria-haspopup="true"
              aria-expanded={dropdownOpen}
            />

            {/* Mendix: .usericon_settings_dropdown — appears on click */}
            {dropdownOpen && (
              <div className={styles.userDropdown} role="menu">
                <button className={styles.userDropdownItem} onClick={() => { setDropdownOpen(false); router.push('/pws/order'); }}>
                  Switch to Premium
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

        {/* Page content */}
        <main className={styles.contentArea}>
          {children}
        </main>
      </div>
    </div>
  );
}
