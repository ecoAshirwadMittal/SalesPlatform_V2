'use client';

/**
 * BidderSidebar — gradient collapsible sidebar for the auction buyer shell.
 *
 * Renders:
 *   1. A SidebarToggle at the top-right corner of the sidebar
 *   2. Two nav items: Auction (gavel icon) and Buyer User Guide (book icon)
 *
 * Collapse state is read from SidebarContext (populated by SidebarProvider
 * in the parent layout). The gradient background is applied to this container
 * — NOT to individual items — per the Phase 4 spec.
 *
 * QA references: qa-03-bidder-dashboard-ad.png (expanded), qa-07-sidebar-collapsed.png (collapsed)
 */

import { usePathname } from 'next/navigation';
import SidebarToggle from '@/components/chrome/SidebarToggle';
import { useSidebar } from '@/components/chrome/SidebarContext';
import BidderSidebarItem from './BidderSidebarItem';
import { GavelIcon, BookIcon } from './BidderSidebarIcons';
import styles from './bidderSidebar.module.css';

/**
 * Phase 12 will wire this to a real backend endpoint that streams the PDF.
 * For now it points to the planned endpoint path so the link is present
 * in the DOM; it will 404 until Phase 12 ships.
 */
const BUYER_GUIDE_HREF = '/api/v1/bidder/docs/buyer-guide';

export default function BidderSidebar() {
  const { collapsed, toggle } = useSidebar();
  const pathname = usePathname();

  const isAuctionActive = pathname.startsWith('/bidder/dashboard');

  return (
    <aside
      className={`${styles.sidebar} ${collapsed ? styles.sidebarCollapsed : ''}`}
      aria-label="Bidder navigation"
      data-testid="bidder-sidebar"
    >
      {/* Toggle button — top-right of sidebar */}
      <div className={styles.toggleRow}>
        <SidebarToggle collapsed={collapsed} onToggle={toggle} />
      </div>

      {/* Navigation items */}
      <nav>
        <ul className={styles.navList} role="list">
          <li>
            <BidderSidebarItem
              icon={<GavelIcon />}
              label="Auction"
              href="/bidder/dashboard"
              collapsed={collapsed}
              isActive={isAuctionActive}
            />
          </li>
          <li>
            {/* External PDF — opens in a new tab. Phase 12 wires the backend endpoint. */}
            <BidderSidebarItem
              icon={<BookIcon />}
              label="Buyer User Guide"
              href={BUYER_GUIDE_HREF}
              external
              collapsed={collapsed}
              isActive={false}
            />
          </li>
        </ul>
      </nav>
    </aside>
  );
}
