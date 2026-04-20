'use client';

import { useRouter } from 'next/navigation';
import styles from '../../settings/pws-control-center/controlCenter.module.css';

/**
 * PWS Data Center — mirrors Mendix Admin > PWS Data Center.
 *
 * Consolidates the 19 raw entity buttons from the legacy hub into 5
 * functional screens. Phase 1 ships Devices + Offers read-only; the
 * other three are scaffolded with a "Coming soon" banner so the hub
 * is fully navigable. See docs/tasks/pws-data-center-port.md.
 */

interface ControlItem {
  label: string;
  href: string;
}

const items: ControlItem[] = [
  { label: 'PWS Devices', href: '/admin/pws-data-center/devices' },
  { label: 'PWS Offers & Orders', href: '/admin/pws-data-center/offers' },
  { label: 'Master Data', href: '/admin/pws-data-center/master-data' },
  { label: 'Shipments & Sync', href: '/admin/pws-data-center/shipments' },
  { label: 'RMA', href: '/admin/pws-data-center/rma' },
];

export default function PwsDataCenterPage() {
  const router = useRouter();

  return (
    <div className={styles.pageContainer}>
      <h2 className={styles.pageTitle}>PWS Data Center</h2>

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
