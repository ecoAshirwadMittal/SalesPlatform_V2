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
import SidebarIcon from '@/components/chrome/SidebarIcon';
import styles from './dashboard.module.css';

// Sidebar glyphs are the real legacy Mendix nav assets (ICON-1, Pass 5):
// ringed items carry the thin dim ~34px ring baked into the SVG exactly as
// legacy renders it; plain items (Bid as Bidder, Settings, Admin, Buyer User
// Guide) have no ring. See frontend/public/icons/sidebar/*.svg and
// docs/tasks/parity/impl/shell-legacy-parity.md "Pass 5 — bespoke icons".
const navItems: NavItem[] = [
  { label: 'Users', href: '/users', icon: <SidebarIcon name="users" /> },
  { label: 'Buyers', href: '/buyers', icon: <SidebarIcon name="buyers" /> },
  { label: 'Inventory', href: '/admin/auctions-data-center/inventory', icon: <SidebarIcon name="inventory" /> },
  { label: 'Purchase Order', href: '/admin/auctions-data-center/purchase-orders', icon: <SidebarIcon name="purchase-order" /> },
  { label: 'Reserved Bids (EB)', href: '/admin/auctions-data-center/reserve-bids', icon: <SidebarIcon name="reserve-bids" /> },
  { label: 'Auction Scheduling', href: '/admin/auctions-data-center/schedule-auction', icon: <SidebarIcon name="auction" /> },
  { label: 'Bid as Bidder', href: '/bid-as-bidder', icon: <SidebarIcon name="bid-as-bidder" /> },
  { label: 'Auction', href: '/auction', icon: <SidebarIcon name="auction" /> },
  // Credit Requests — legacy admin sidebar item between Auction and Reports
  // (SHELL-P1, ruling 1). Routes to the partial-credit admin surface.
  { label: 'Credit Requests', href: '/admin/auctions-data-center/partial-credit', icon: <SidebarIcon name="credit-requests" /> },
  {
    label: 'Reports', href: '/reports', expandable: true,
    icon: <SidebarIcon name="reports" />,
  },
  {
    label: 'Settings', href: '/settings', expandable: true,
    icon: <SidebarIcon name="settings" />,
    children: [
      { label: 'PWS Control Center', href: '/settings/pws-control-center' },
    ],
  },
  {
    label: 'Admin', href: '/admin', expandable: true,
    icon: <SidebarIcon name="admin" />,
    children: [
      { label: 'Application Control Center', href: '/admin/app-control-center' },
      { label: 'Auction Control Center', href: '/admin/auction-control-center' },
      { label: 'Auctions Data Center', href: '/admin/auctions-data-center' },
      { label: 'PWS Data Center', href: '/admin/pws-data-center' },
    ],
  },
  // M12a — QA admin sidebar exposes a "Buyer User Guide" link at the bottom.
  // The route hosts a stub page until real documentation lands.
  { label: 'Buyer User Guide', href: '/buyer-user-guide', icon: <SidebarIcon name="buyer-guide" /> },
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

  // Mendix SNP_UserInfoDisplay on ecoAtm_Atlas_Default — the SAME layout the
  // legacy bidder dashboard uses (verified: ai_knowledge_base_Release10,
  // ReserveBid_Overview + PG_Bidder_Dashboard_DG2 both cite it). Display
  // name = FullName || Name; initials render inside the green circle. When
  // the account has no display name (the legacy-local admin), the widget
  // degrades to a bare green circle — the "green dot" in the evidence capture
  // is this widget with empty account data, not a distinct admin treatment.
  const displayName = user?.fullName || user?.email || '';
  const initials = user?.initials || (displayName ? displayName.slice(0, 2).toUpperCase() : '');

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
            the content area top-left; the SNP_UserInfoDisplay identity (name +
            green initials circle, click-to-open menu) sits top-right. No white
            bar. Accounts without a display name render a bare green circle —
            exactly what the legacy-local admin capture shows. */}
        <header className={styles.topBar}>
          <div className={styles.topBarLogo}>
            <Image src="/images/ecoatm-direct-logo.png" alt="ecoATM DIRECT" width={119} height={46} priority />
          </div>
          <div className={styles.topBarRight} ref={dropdownRef}>
            {displayName && <span className={styles.userName}>{displayName}</span>}
            <button
              className={styles.userIconWrapper}
              onClick={() => setDropdownOpen(prev => !prev)}
              aria-label={displayName ? `User menu for ${displayName}` : 'User menu'}
              aria-haspopup="true"
              aria-expanded={dropdownOpen}
            >
              {initials && <span aria-hidden="true">{initials}</span>}
            </button>

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
