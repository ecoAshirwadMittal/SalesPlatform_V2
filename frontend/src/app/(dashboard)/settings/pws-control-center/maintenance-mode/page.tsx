'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/maintenance-mode';

interface MaintenanceMode {
  is_enabled: boolean;
  banner_start_time: string | null;
  start_time: string | null;
  end_time: string | null;
  banner_title: string;
  banner_message: string;
  page_title: string;
  page_header: string;
  page_message: string;
}

function toLocalInput(ts: string | null): string {
  if (!ts) return '';
  try {
    const d = new Date(ts);
    return d.toISOString().slice(0, 16);
  } catch {
    return '';
  }
}

export default function MaintenanceModePage() {
  const [data, setData] = useState<MaintenanceMode | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  async function load() {
    try {
      const res = await apiFetch(API);
      setData(await res.json());
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function handleSave() {
    if (!data) return;
    setSaving(true);
    setBanner(null);
    try {
      await apiFetch(API, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          isEnabled: data.is_enabled,
          bannerStartTime: data.banner_start_time || null,
          startTime: data.start_time || null,
          endTime: data.end_time || null,
          bannerTitle: data.banner_title,
          bannerMessage: data.banner_message,
          pageTitle: data.page_title,
          pageHeader: data.page_header,
          pageMessage: data.page_message,
        }),
      });
      setBanner({ type: 'success', message: 'Maintenance mode settings saved.' });
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setSaving(false);
    }
  }

  if (loading || !data) return <div className={s.loading}>Loading maintenance mode...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>Maintenance Mode</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.formCard}>
        <div className={s.toggleRow}>
          <label className={s.toggleLabel}>Maintenance Mode Enabled</label>
          <button
            className={`${s.toggleSwitch} ${data.is_enabled ? s.toggleSwitchOn : ''}`}
            onClick={() => setData({ ...data, is_enabled: !data.is_enabled })}
          />
          <span className={s.toggleStatusText}>{data.is_enabled ? 'ON' : 'OFF'}</span>
        </div>

        <h3 className={s.sectionTitle}>Schedule</h3>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Banner Start Time</label>
          <input className={s.formInput} type="datetime-local" value={toLocalInput(data.banner_start_time)} onChange={e => setData({ ...data, banner_start_time: e.target.value || null })} />
          <div className={s.formHint}>When the warning banner appears to users</div>
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Maintenance Start Time</label>
          <input className={s.formInput} type="datetime-local" value={toLocalInput(data.start_time)} onChange={e => setData({ ...data, start_time: e.target.value || null })} />
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Maintenance End Time</label>
          <input className={s.formInput} type="datetime-local" value={toLocalInput(data.end_time)} onChange={e => setData({ ...data, end_time: e.target.value || null })} />
        </div>

        <h3 className={s.sectionTitle}>Banner Content</h3>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Banner Title</label>
          <input className={s.formInput} value={data.banner_title || ''} onChange={e => setData({ ...data, banner_title: e.target.value })} />
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Banner Message</label>
          <textarea className={s.formTextarea} value={data.banner_message || ''} onChange={e => setData({ ...data, banner_message: e.target.value })} />
        </div>

        <h3 className={s.sectionTitle}>Maintenance Page Content</h3>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Page Title</label>
          <input className={s.formInput} value={data.page_title || ''} onChange={e => setData({ ...data, page_title: e.target.value })} />
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Page Header</label>
          <input className={s.formInput} value={data.page_header || ''} onChange={e => setData({ ...data, page_header: e.target.value })} />
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Page Message</label>
          <textarea className={s.formTextarea} value={data.page_message || ''} onChange={e => setData({ ...data, page_message: e.target.value })} />
        </div>

        <div className={s.actionsRow}>
          <button className={s.saveBtn} onClick={handleSave} disabled={saving}>
            {saving ? 'Saving...' : 'Save'}
          </button>
        </div>
      </div>
    </div>
  );
}
