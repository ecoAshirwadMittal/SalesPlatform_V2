'use client';

import { useRouter } from 'next/navigation';
import styles from '../../../settings/pws-control-center/controlCenter.module.css';

/**
 * Auction Control Center — admin hub for cross-cutting auction tools.
 * Currently contains the Userguide Configuration page (Phase 12).
 */

interface ControlItem {
  label: string;
  href: string;
}

const items: ControlItem[] = [
  {
    label: 'Userguide Configuration',
    href: '/admin/auction-control-center/userguide-configuration',
  },
];

export default function AuctionControlCenterPage() {
  const router = useRouter();

  return (
    <div className={styles.pageContainer}>
      <h2 className={styles.pageTitle}>Auction Control Center</h2>

      <div className={styles.cardContainer}>
        <div className={styles.buttonGrid}>
          {items.map((item) => (
            <button
              key={item.label}
              type="button"
              className={styles.controlButton}
              onClick={() => router.push(item.href)}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
