'use client';

/**
 * BidderSidebar — gradient collapsible sidebar for the auction buyer shell.
 *
 * Renders:
 *   1. A SidebarToggle at the top-right corner of the sidebar
 *   2. Three nav items: Auction, Credit Requests, and Buyer User Guide —
 *      the legacy buyer sidebar order (BDD-P2 / SHELL-P1).
 *
 * Buyer User Guide navigates to the in-app `/buyer-user-guide` stub route
 * (enabled, rendered identically to its siblings — NAV-1). Legacy renders this
 * item enabled; the earlier HEAD-checked backend-PDF link dimmed it whenever no
 * guide was configured, which never matched the legacy evidence.
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
import SidebarIcon from '@/components/chrome/SidebarIcon';
import styles from './bidderSidebar.module.css';

const BUYER_GUIDE_HREF = '/buyer-user-guide';
const CREDIT_REQUESTS_HREF = '/wholesale/partial-credit';

export default function BidderSidebar() {
  const { collapsed, toggle } = useSidebar();
  const pathname = usePathname();

  const isAuctionActive = pathname.startsWith('/bidder/dashboard');
  const isCreditRequestsActive = pathname.startsWith(CREDIT_REQUESTS_HREF);
  const isBuyerGuideActive = pathname.startsWith(BUYER_GUIDE_HREF);

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
              icon={<SidebarIcon name="auction" />}
              label="Auction"
              href="/bidder/dashboard"
              collapsed={collapsed}
              isActive={isAuctionActive}
            />
          </li>
          <li>
            {/* Credit Requests — buyer-shell counterpart of the admin item
                (SHELL-P1 / BDD-P2). Routes to the partial-credit buyer surface. */}
            <BidderSidebarItem
              icon={<SidebarIcon name="credit-requests" />}
              label="Credit Requests"
              href={CREDIT_REQUESTS_HREF}
              collapsed={collapsed}
              isActive={isCreditRequestsActive}
            />
          </li>
          <li>
            {/* Buyer User Guide — enabled, internal link to the in-app stub
                (NAV-1). Legacy renders this item enabled with a plain book glyph,
                identical treatment to its siblings. */}
            <BidderSidebarItem
              icon={<SidebarIcon name="buyer-guide" />}
              label="Buyer User Guide"
              href={BUYER_GUIDE_HREF}
              collapsed={collapsed}
              isActive={isBuyerGuideActive}
            />
          </li>
        </ul>
      </nav>
    </aside>
  );
}
