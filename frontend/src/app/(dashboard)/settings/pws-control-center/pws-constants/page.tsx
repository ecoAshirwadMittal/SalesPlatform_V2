'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/pws-constants';

interface PWSConstants {
  sla_days: number;
  sales_email: string;
  send_first_reminder: boolean;
  send_second_reminder: boolean;
  hours_first_counter_reminder: number;
  hours_second_counter_reminder: number;
}

export default function PWSConstantsPage() {
  const [data, setData] = useState<PWSConstants | null>(null);
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
          slaDays: data.sla_days,
          salesEmail: data.sales_email,
          sendFirstReminder: data.send_first_reminder,
          sendSecondReminder: data.send_second_reminder,
          hoursFirstCounterReminder: data.hours_first_counter_reminder,
          hoursSecondCounterReminder: data.hours_second_counter_reminder,
        }),
      });
      setBanner({ type: 'success', message: 'PWS constants saved.' });
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setSaving(false);
    }
  }

  if (loading || !data) return <div className={s.loading}>Loading PWS constants...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>PWS Constants</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.formCard}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>SLA Days</label>
          <input className={s.formInputShort} type="number" value={data.sla_days} onChange={e => setData({ ...data, sla_days: Number(e.target.value) })} />
          <div className={s.formHint}>Number of days before an offer is flagged as beyond SLA</div>
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Sales Email</label>
          <input className={s.formInput} type="email" value={data.sales_email || ''} onChange={e => setData({ ...data, sales_email: e.target.value })} />
          <div className={s.formHint}>Email address for sales team notifications</div>
        </div>

        <div className={s.toggleRow}>
          <label className={s.toggleLabel}>Send First Reminder</label>
          <button
            className={`${s.toggleSwitch} ${data.send_first_reminder ? s.toggleSwitchOn : ''}`}
            onClick={() => setData({ ...data, send_first_reminder: !data.send_first_reminder })}
          />
          <span className={s.toggleStatusText}>{data.send_first_reminder ? 'On' : 'Off'}</span>
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Hours Until First Counter Reminder</label>
          <input className={s.formInputShort} type="number" value={data.hours_first_counter_reminder} onChange={e => setData({ ...data, hours_first_counter_reminder: Number(e.target.value) })} />
        </div>

        <div className={s.toggleRow}>
          <label className={s.toggleLabel}>Send Second Reminder</label>
          <button
            className={`${s.toggleSwitch} ${data.send_second_reminder ? s.toggleSwitchOn : ''}`}
            onClick={() => setData({ ...data, send_second_reminder: !data.send_second_reminder })}
          />
          <span className={s.toggleStatusText}>{data.send_second_reminder ? 'On' : 'Off'}</span>
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Hours Until Second Counter Reminder</label>
          <input className={s.formInputShort} type="number" value={data.hours_second_counter_reminder} onChange={e => setData({ ...data, hours_second_counter_reminder: Number(e.target.value) })} />
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
