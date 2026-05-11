'use client';

/**
 * QA-parity entry point for the left-nav "Auction Scheduling" item.
 *
 * QA's menu lands directly on the most recent auction's schedule editor
 * (round fieldsets + inventory preview), not on a list of scheduling
 * rows. This page mirrors that: it looks up the newest auction
 * (`listAuctions`, sorted `created_date DESC, id DESC` server-side) and
 * `router.replace`s into its editor. If no auction exists, the
 * `NoActiveAuctionModal` informs the admin and routes them to the
 * inventory page where the Create Auction button lives.
 *
 * The per-round operational list (Start / Close / Re-rank / Recalc TP)
 * has moved off-menu to /admin/auctions-data-center/scheduling-auctions.
 * See docs/tasks/auction-scheduling-menu-redirect-plan.md.
 */

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { listAuctions } from '@/lib/auctions';
import styles from './list.module.css';
import { NoActiveAuctionModal } from './NoActiveAuctionModal';

const INVENTORY_PATH = '/admin/auctions-data-center/inventory';

export default function AuctionSchedulingEntryPage(): React.ReactElement {
  const router = useRouter();
  const [showNoAuctionModal, setShowNoAuctionModal] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    listAuctions({ page: 0, pageSize: 1 })
      .then((res) => {
        if (cancelled) return;
        const newest = res.content[0];
        if (newest) {
          router.replace(`/admin/auctions-data-center/auctions/${newest.id}/schedule`);
          return;
        }
        setShowNoAuctionModal(true);
      })
      .catch((err: unknown) => {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Failed to load auctions.');
      });
    return () => {
      cancelled = true;
    };
  }, [router]);

  return (
    <div className={styles.page}>
      {error ? (
        <div className={styles.error} role="alert">
          {error}
        </div>
      ) : (
        <div className={styles.empty}>Loading…</div>
      )}
      {showNoAuctionModal && (
        <NoActiveAuctionModal
          onAcknowledge={() => {
            setShowNoAuctionModal(false);
            router.push(INVENTORY_PATH);
          }}
        />
      )}
    </div>
  );
}
