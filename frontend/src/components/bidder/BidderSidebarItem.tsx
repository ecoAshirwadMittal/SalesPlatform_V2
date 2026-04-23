'use client';

/**
 * BidderSidebarItem — a single navigation item inside the bidder sidebar.
 *
 * When collapsed, only the icon is visible (label hidden via CSS).
 * When expanded, icon + label are shown in a horizontal row.
 *
 * External items (Buyer User Guide) open in a new tab.
 * Internal items use Next.js <Link> for client-side navigation.
 */

import Link from 'next/link';
import styles from './bidderSidebar.module.css';

export interface BidderSidebarItemProps {
  /** 20×20 icon element */
  icon: React.ReactNode;
  /** Human-readable label; hidden when sidebar is collapsed */
  label: string;
  /** Navigation href */
  href: string;
  /** When true, renders as an <a> with target="_blank" rel="noopener" */
  external?: boolean;
  /** Sidebar collapsed state — drives label visibility */
  collapsed: boolean;
  /** Whether this item represents the current page */
  isActive?: boolean;
}

export default function BidderSidebarItem({
  icon,
  label,
  href,
  external = false,
  collapsed,
  isActive = false,
}: BidderSidebarItemProps) {
  const itemClass = [
    styles.navItem,
    isActive ? styles.navItemActive : '',
  ]
    .filter(Boolean)
    .join(' ');

  const content = (
    <>
      <span className={styles.navIcon} aria-hidden="true">
        {icon}
      </span>
      {/* Label is always in the DOM for accessibility but visually hidden
          when collapsed so the focus target retains its full width. */}
      <span className={collapsed ? styles.navLabelHidden : styles.navLabel}>
        {label}
      </span>
    </>
  );

  if (external) {
    return (
      <a
        href={href}
        className={itemClass}
        target="_blank"
        rel="noopener noreferrer"
        aria-label={collapsed ? label : undefined}
        data-testid={`sidebar-item-${label.toLowerCase().replace(/\s+/g, '-')}`}
      >
        {content}
      </a>
    );
  }

  return (
    <Link
      href={href}
      className={itemClass}
      aria-label={collapsed ? label : undefined}
      aria-current={isActive ? 'page' : undefined}
      data-testid={`sidebar-item-${label.toLowerCase().replace(/\s+/g, '-')}`}
    >
      {content}
    </Link>
  );
}
