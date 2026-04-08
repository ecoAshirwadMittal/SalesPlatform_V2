'use client';

import { usePathname, useRouter } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { useState, useEffect, useRef } from 'react';
import styles from './dashboard.module.css';

interface SubNavItem {
  label: string;
  href: string;
}

interface NavItem {
  label: string;
  href: string;
  /** SVG icon as JSX, or a short text badge (e.g. "PO", "RB") */
  icon: React.ReactNode;
  /** If true, shows a chevron and can expand sub-items */
  expandable?: boolean;
  /** Sub-menu items shown when expanded */
  children?: SubNavItem[];
}

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
    label: 'Inventory', href: '/inventory',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
        <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
        <line x1="12" y1="22.08" x2="12" y2="12"/>
      </svg>
    ),
  },
  {
    label: 'Purchase Order', href: '/purchase-order',
    icon: <span className={styles.textBadge}>PO</span>,
  },
  {
    label: 'Reserved Bids (EB)', href: '/reserved-bids',
    icon: <span className={styles.textBadge}>RB</span>,
  },
  {
    label: 'Auction Scheduling', href: '/auction-scheduling',
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
];

interface AuthUser {
  userId: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  initials: string;
}

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [collapsed] = useState(false);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [expandedMenus, setExpandedMenus] = useState<Set<string>>(() => new Set());
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Load user from localStorage (set during login — mirrors ACT_GetCurrentUser)
  useEffect(() => {
    const stored = localStorage.getItem('auth_user');
    if (stored) {
      try { setUser(JSON.parse(stored)); } catch { /* ignore */ }
    }
  }, []);

  // Auto-expand menus whose children or parent href match the current path
  useEffect(() => {
    navItems.forEach(item => {
      if (item.expandable && item.children) {
        const match = item.children.some(child => pathname.startsWith(child.href))
          || pathname.startsWith(item.href);
        if (match) {
          setExpandedMenus(prev => new Set(prev).add(item.label));
        }
      }
    });
  }, [pathname]);

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

  // Mendix SNP_UserInfoDisplay: display name = FullName || Name
  const displayName = user?.fullName || user?.email || 'User';
  const initials = user?.initials || displayName.slice(0, 2).toUpperCase();

  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    router.push('/login');
  };

  return (
    <div className={styles.dashboardContainer}>
      {/* Sidebar */}
      <aside className={styles.sidebar}>
        <div className={styles.sidebarHeader}>
          <Image src="/images/ecoatm_logo.svg" alt="ecoATM" width={120} height={28} />
        </div>
        <nav className={styles.sidebarNav}>
          {navItems.map((item) => {
            const hasChildren = item.expandable && item.children && item.children.length > 0;
            const isExpanded = expandedMenus.has(item.label);
            const isActive = hasChildren
              ? item.children!.some(c => pathname.startsWith(c.href))
              : pathname.startsWith(item.href);

            if (hasChildren) {
              return (
                <div key={item.label}>
                  <button
                    className={`${styles.navItem} ${styles.navItemButton} ${isActive ? styles.navItemActive : ''}`}
                    onClick={() => toggleMenu(item.label)}
                  >
                    <span className={styles.navIcon}>{item.icon}</span>
                    {!collapsed && (
                      <>
                        <span className={styles.navLabel}>{item.label}</span>
                        <span className={`${styles.chevron} ${isExpanded ? styles.chevronOpen : ''}`}>
                          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
                        </span>
                      </>
                    )}
                  </button>
                  {isExpanded && !collapsed && (
                    <div className={styles.subMenu}>
                      {item.children!.map(child => (
                        <Link
                          key={child.href}
                          href={child.href}
                          className={`${styles.subMenuItem} ${pathname.startsWith(child.href) ? styles.subMenuItemActive : ''}`}
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
              >
                <span className={styles.navIcon}>{item.icon}</span>
                {!collapsed && (
                  <>
                    <span className={styles.navLabel}>{item.label}</span>
                    {item.expandable && (
                      <span className={styles.chevron}>
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
                      </span>
                    )}
                  </>
                )}
              </Link>
            );
          })}
        </nav>
      </aside>

      {/* Main content */}
      <div className={styles.mainArea}>
        {/* Top bar — SNP_UserInfoDisplay: username + initials circle + dropdown */}
        <header className={styles.topBar}>
          <div className={styles.topBarRight} ref={dropdownRef}>
            <span className={styles.userName}>{displayName}</span>
            <button
              className={styles.userIconWrapper}
              onClick={() => setDropdownOpen(prev => !prev)}
              aria-label="User menu"
            >
              <span className={styles.userIcon}>{initials}</span>
            </button>

            {/* Mendix: .usericon_settings_dropdown — appears on hover/focus */}
            {dropdownOpen && (
              <div className={styles.userDropdown}>
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
