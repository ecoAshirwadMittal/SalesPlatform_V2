'use client';

import { useRouter } from 'next/navigation';
import styles from '../../settings/pws-control-center/controlCenter.module.css';

/**
 * Auctions Data Center — mirrors Mendix Admin > Auctions Data Center.
 *
 * QA layout is a 4-column grid of 19 green tiles (see
 * qa-auctions-data-center.png reference). Tiles whose target page
 * has been ported link to the live route; the rest will 404 until
 * their respective ports land. Order preserves the QA layout so
 * admins can navigate from muscle memory.
 */

interface Tile {
  label: string;
  href: string;
}

const tiles: Tile[] = [
  // Row 1
  { label: 'Auctions', href: '/admin/auctions-data-center/auctions' },
  { label: 'Direct Users', href: '/admin/auctions-data-center/direct-users' },
  { label: 'Aggregated Inventory Totals', href: '/admin/auctions-data-center/aggregated-inventory-totals' },
  { label: 'Aggregated Inventory', href: '/admin/auctions-data-center/inventory' },
  // Row 2
  { label: 'Bid Data', href: '/admin/auctions-data-center/bid-data' },
  { label: 'Bid Round', href: '/admin/auctions-data-center/bid-round' },
  { label: 'Bidder File Management', href: '/admin/auctions-data-center/bidder-file-management' },
  { label: 'Schedule Auction Overview - Buyer Codes', href: '/admin/auctions-data-center/schedule-auction-overview' },
  // Row 3
  { label: 'Schedule Auction', href: '/admin/auctions-data-center/schedule-auction' },
  { label: 'Reserve Bid', href: '/admin/auctions-data-center/reserve-bid' },
  { label: 'User Status', href: '/admin/auctions-data-center/user-status' },
  { label: 'Buyer Code Management', href: '/admin/auctions-data-center/buyer-code-management' },
  // Row 4
  { label: 'DA Week', href: '/admin/auctions-data-center/da-week' },
  { label: 'PO Detail', href: '/admin/auctions-data-center/po-detail' },
  { label: 'Purchase Order', href: '/admin/auctions-data-center/purchase-order' },
  { label: 'Weeks', href: '/admin/auctions-data-center/weeks' },
  // Row 5
  { label: 'Sales Rep Management', href: '/admin/auctions-data-center/sales-rep-management' },
  { label: 'Company Holiday', href: '/admin/auctions-data-center/company-holiday' },
  { label: 'Cohort Mapping', href: '/admin/auctions-data-center/cohort-mapping' },
];

export default function AuctionsDataCenterPage() {
  const router = useRouter();

  return (
    <div className={styles.pageContainer}>
      <h2 className={styles.pageTitle}>Auctions Data Center</h2>

      <div className={styles.cardContainer}>
        <div className={styles.buttonGrid}>
          {tiles.map((tile) => (
            <button
              key={tile.label}
              type="button"
              className={styles.controlButton}
              onClick={() => router.push(tile.href)}
            >
              {tile.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
