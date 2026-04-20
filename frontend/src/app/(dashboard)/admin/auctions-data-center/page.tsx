'use client';

import Link from 'next/link';
import s from '../../settings/pws-control-center/admin.module.css';

export default function AuctionsDataCenterPage() {
  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>Auctions Data Center</h2>
      </div>
      <div className={s.card} style={{ padding: 32, textAlign: 'center' }}>
        <p style={{ fontSize: 16, color: '#444' }}>
          Scaffolded stub — parity port of Mendix <em>Admin → Auctions Data Center</em> is scheduled after the PWS Data Center port.
        </p>
        <p style={{ fontSize: 12, color: '#888', marginTop: 16 }}>
          See <code>docs/tasks/pws-data-center-port.md</code> for the sibling plan shape.
        </p>
        <p style={{ marginTop: 24 }}>
          <Link href="/admin/pws-data-center" className={s.backLink}>Go to PWS Data Center →</Link>
        </p>
      </div>
    </div>
  );
}
