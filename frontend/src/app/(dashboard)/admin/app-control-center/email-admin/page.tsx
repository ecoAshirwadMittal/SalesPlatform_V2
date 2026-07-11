'use client';

import { useState } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import e from './emailAdmin.module.css';
import { SmtpConfigTab } from './SmtpConfigTab';
import { TemplatesTab } from './TemplatesTab';
import { EmailLogTab } from './EmailLogTab';
import type { Banner } from './shared';

/**
 * Task 10 — Email Admin: SMTP config / templates / delivery log.
 *
 * Thin shell: tab bar + banner state only. Each tab owns its own data
 * fetching (via `@/lib/adminEmailClient`) and is split into its own file
 * (`SmtpConfigTab.tsx`, `TemplatesTab.tsx`, `EmailLogTab.tsx`) to keep this
 * page cohesive — see those files for the wiring to `/api/v1/admin/email/**`
 * (Tasks 7-9) and the M-3 DOMPurify sanitization of server-derived HTML.
 */
type TabId = 'smtp' | 'templates' | 'log';

export default function EmailAdminPage() {
  const [activeTab, setActiveTab] = useState<TabId>('smtp');
  const [banner, setBanner] = useState<Banner | null>(null);

  const tabs: { id: TabId; label: string }[] = [
    { id: 'smtp', label: 'SMTP Settings' },
    { id: 'templates', label: 'Email Templates' },
    { id: 'log', label: 'Email Log' },
  ];

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>Email Admin</h2>
        <Link href="/admin/app-control-center" className={s.backLink}>
          ← Back to Application Control Center
        </Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={e.tabBar}>
        {tabs.map((tab) => (
          <button
            key={tab.id}
            className={`${e.tab} ${activeTab === tab.id ? e.tabActive : ''}`}
            onClick={() => { setActiveTab(tab.id); setBanner(null); }}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <div className={s.card}>
        {activeTab === 'smtp' && <SmtpConfigTab onBanner={setBanner} />}
        {activeTab === 'templates' && <TemplatesTab onBanner={setBanner} />}
        {activeTab === 'log' && <EmailLogTab onBanner={setBanner} />}
      </div>
    </div>
  );
}
