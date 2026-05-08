'use client';

/**
 * Buyer User Guide — placeholder stub.
 *
 * QA admin sidebar exposes a "Buyer User Guide" entry; local was missing
 * the entry entirely (gap M12a). This page is the navigation target so the
 * sidebar link doesn't 404. Real documentation content will replace the
 * placeholder body when the buyer-facing KB is published.
 */

import styles from './buyer-user-guide.module.css';

export default function BuyerUserGuidePage(): React.ReactElement {
  return (
    <div className={styles.page}>
      <h1 className={styles.title}>Buyer User Guide</h1>
      <p className={styles.body}>Documentation coming soon.</p>
    </div>
  );
}
