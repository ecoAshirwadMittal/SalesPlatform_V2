'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import s from '../admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/deposco';

interface DeposcoConfig {
  id?: number;
  base_url: string;
  username: string;
  timeout_ms: number;
  is_active: boolean;
}

export default function DeposcoPage() {
  const [data, setData] = useState<DeposcoConfig>({ base_url: '', username: '', timeout_ms: 30000, is_active: true });
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  async function load() {
    try {
      const res = await apiFetch(API);
      const json = await res.json();
      if (json.config && json.config.id) {
        setData(json.config);
      }
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function handleSave() {
    setSaving(true);
    setBanner(null);
    try {
      await apiFetch(API, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          baseUrl: data.base_url,
          username: data.username,
          password: password || null,
          timeoutMs: data.timeout_ms,
          isActive: data.is_active,
        }),
      });
      setBanner({ type: 'success', message: 'Deposco configuration saved.' });
      setPassword('');
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <div className={s.loading}>Loading Deposco config...</div>;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>Deposco Configuration</h2>
        <Link href="/settings/pws-control-center" className={s.backLink}>&larr; Back to Control Center</Link>
      </div>

      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      <div className={s.formCard}>
        <div className={s.toggleRow}>
          <label className={s.toggleLabel}>Active</label>
          <button
            className={`${s.toggleSwitch} ${data.is_active ? s.toggleSwitchOn : ''}`}
            onClick={() => setData({ ...data, is_active: !data.is_active })}
          />
          <span className={s.toggleStatusText}>{data.is_active ? 'Enabled' : 'Disabled'}</span>
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Base URL</label>
          <input className={s.formInput} value={data.base_url || ''} onChange={e => setData({ ...data, base_url: e.target.value })} placeholder="https://api.deposco.com" />
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Username</label>
          <input className={s.formInput} value={data.username || ''} onChange={e => setData({ ...data, username: e.target.value })} />
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Password</label>
          <input className={s.formInput} type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Leave blank to keep current" />
          <div className={s.formHint}>Only updated if a new value is provided</div>
        </div>

        <div className={s.formGroup}>
          <label className={s.formLabel}>Timeout (ms)</label>
          <input className={s.formInputShort} type="number" value={data.timeout_ms} onChange={e => setData({ ...data, timeout_ms: Number(e.target.value) })} />
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
