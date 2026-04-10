'use client';

import { useRouter } from 'next/navigation';
import styles from '../../settings/pws-control-center/controlCenter.module.css';

/**
 * Application Control Center — mirrors Mendix Admin > Application Control Center.
 * Contains admin-level tools: Email Admin, SSO Config, etc.
 */

interface ControlItem {
  label: string;
  href?: string;
}

const items: ControlItem[] = [
  { label: 'Email Admin', href: '/admin/app-control-center/email-admin' },
  { label: 'SSO Configuration', href: '/admin/app-control-center/sso-config' },
  { label: 'Authentication', href: '/admin/app-control-center/authentication' },
  { label: 'Authorization', href: '/admin/app-control-center/authorization' },
];

export default function AppControlCenterPage() {
  const router = useRouter();

  return (
    <div className={styles.pageContainer}>
      <h2 className={styles.pageTitle}>Application Control Center</h2>

      <div className={styles.cardContainer}>
        <div className={styles.buttonGrid}>
          {items.map((item) => (
            <button
              key={item.label}
              className={styles.controlButton}
              onClick={() => {
                if (item.href) router.push(item.href);
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
