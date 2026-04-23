'use client';

/**
 * BidderSidebar — gradient collapsible sidebar for the auction buyer shell.
 *
 * Renders:
 *   1. A SidebarToggle at the top-right corner of the sidebar
 *   2. Two nav items: Auction (gavel icon) and Buyer User Guide (book icon)
 *
 * The Buyer User Guide link is wired to the Phase 12 backend endpoint.
 * On mount a HEAD request checks whether a guide has been configured; if not,
 * the link is rendered disabled with a tooltip "No guide configured yet."
 *
 * Collapse state is read from SidebarContext (populated by SidebarProvider
 * in the parent layout). The gradient background is applied to this container
 * — NOT to individual items — per the Phase 4 spec.
 *
 * QA references: qa-03-bidder-dashboard-ad.png (expanded), qa-07-sidebar-collapsed.png (collapsed)
 */

import { useEffect, useState } from 'react';
import { usePathname } from 'next/navigation';
import SidebarToggle from '@/components/chrome/SidebarToggle';
import { useSidebar } from '@/components/chrome/SidebarContext';
import { apiFetch } from '@/lib/apiFetch';
import BidderSidebarItem from './BidderSidebarItem';
import { GavelIcon, BookIcon } from './BidderSidebarIcons';
import styles from './bidderSidebar.module.css';

const BUYER_GUIDE_HREF = '/api/v1/bidder/docs/buyer-guide';

export default function BidderSidebar() {
  const { collapsed, toggle } = useSidebar();
  const pathname = usePathname();

  /**
   * null  = not yet checked (link is enabled optimistically while checking)
   * true  = guide exists
   * false = guide does not exist (404)
   */
  const [guideAvailable, setGuideAvailable] = useState<boolean | null>(null);

  useEffect(() => {
    // HEAD request — no body read, minimal overhead.
    apiFetch(BUYER_GUIDE_HREF, { method: 'HEAD' })
      .then((res) => setGuideAvailable(res.ok))
      .catch(() => setGuideAvailable(false));
  }, []);

  const isAuctionActive = pathname.startsWith('/bidder/dashboard');

  // Disable the link while we haven't checked yet (null) OR when we know there's no guide.
  const guideDisabled = guideAvailable === false;

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
            {/* External PDF — opens in a new tab via the Phase 12 backend endpoint. */}
            <BidderSidebarItem
              icon={<BookIcon />}
              label="Buyer User Guide"
              href={BUYER_GUIDE_HREF}
              external={!guideDisabled}
              disabled={guideDisabled}
              tooltip={guideDisabled ? 'No guide configured yet.' : undefined}
              collapsed={collapsed}
              isActive={false}
            />
          </li>
        </ul>
      </nav>
    </aside>
  );
}
