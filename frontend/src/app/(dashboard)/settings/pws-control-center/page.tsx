'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import styles from './controlCenter.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API_BASE = '/api/v1/admin';

interface Banner {
  type: 'success' | 'error' | 'info';
  message: string;
}

interface ControlItem {
  label: string;
  href?: string;
  action?: () => Promise<void>;
}

export default function PWSControlCenterPage() {
  const router = useRouter();
  const [banner, setBanner] = useState<Banner | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  async function runAction(endpoint: string, successMsg: string) {
    setActionLoading(true);
    setBanner(null);
    try {
      const res = await apiFetch(`${API_BASE}${endpoint}`, { method: 'POST' });
      const data = await res.json();
      if (data.success !== false) {
        setBanner({ type: 'success', message: data.message || successMsg });
      } else {
        setBanner({ type: 'error', message: data.message || 'Action failed.' });
      }
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setActionLoading(false);
    }
  }

  const items: ControlItem[] = [
    { label: 'PWS Feature Flags', href: '/settings/pws-control-center/feature-flags' },
    { label: 'PWS Ranks Configuration', href: '/settings/pws-control-center/ranks-config' },
    { label: 'PWS Error Messages', href: '/settings/pws-control-center/error-messages' },
    { label: 'Oracle', href: '/settings/oracle-config' },
    { label: 'Set SLA Tags', action: () => runAction('/sla-tags/set', 'SLA tags updated for overdue offers.') },
    { label: 'Remove SLA Tags', action: () => runAction('/sla-tags/remove', 'SLA tags removed from all offers.') },
    { label: 'PWS Constants', href: '/settings/pws-control-center/pws-constants' },
    { label: 'Deposco', href: '/settings/pws-control-center/deposco' },
    { label: 'Order Status', href: '/settings/pws-control-center/order-status' },
    { label: 'Maintenance Mode', href: '/settings/pws-control-center/maintenance-mode' },
    { label: 'RMA Status', href: '/settings/pws-control-center/rma-status' },
    { label: 'RMA Template', href: '/settings/pws-control-center/rma-template' },
    { label: 'PWS Navigation Menu', href: '/settings/pws-control-center/navigation-menu' },
    { label: 'Snowflake Offer/Order Status', action: () => runAction('/snowflake/sync', 'Snowflake sync triggered.') },
  ];

  return (
    <div className={styles.pageContainer}>
      <h2 className={styles.pageTitle}>PWS Control Center</h2>

      {banner && (
        <div className={`${styles.banner} ${
          banner.type === 'success' ? styles.bannerSuccess :
          banner.type === 'error' ? styles.bannerError : styles.bannerInfo
        }`}>
          {banner.message}
        </div>
      )}

      <div className={styles.cardContainer}>
        <div className={styles.buttonGrid}>
          {items.map((item) => (
            <button
              key={item.label}
              className={styles.controlButton}
              disabled={actionLoading}
              onClick={() => {
                if (item.href) {
                  router.push(item.href);
                } else if (item.action) {
                  item.action();
                }
              }}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
