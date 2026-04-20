'use client';

import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';

interface ComingSoonProps {
  title: string;
  phase: string;
  consolidates: string[];
}

export function ComingSoon({ title, phase, consolidates }: ComingSoonProps) {
  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <Link href="/admin/pws-data-center" className={s.backLink}>← PWS Data Center</Link>
        <h2 className={s.pageTitle}>{title}</h2>
      </div>

      <div className={s.card} style={{ padding: 32, textAlign: 'center' }}>
        <p style={{ fontSize: 16, color: '#444', marginBottom: 12 }}>
          This screen is planned for <strong>{phase}</strong>.
        </p>
        <p style={{ fontSize: 14, color: '#666', marginBottom: 20 }}>
          Consolidates these legacy Mendix entity grids:
        </p>
        <ul style={{ listStyle: 'none', padding: 0, fontSize: 14, color: '#444' }}>
          {consolidates.map((c) => (
            <li key={c} style={{ padding: '4px 0' }}>• {c}</li>
          ))}
        </ul>
        <p style={{ fontSize: 12, color: '#888', marginTop: 24 }}>
          Tracked in <code>docs/tasks/pws-data-center-port.md</code>.
        </p>
      </div>
    </div>
  );
}
