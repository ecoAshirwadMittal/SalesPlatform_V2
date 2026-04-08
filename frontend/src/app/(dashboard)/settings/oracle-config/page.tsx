'use client';

import { useState, useEffect, useCallback } from 'react';
import styles from './oracleConfig.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API_BASE = '/api/v1/admin/oracle-config';

interface OracleConfigData {
  id: number | null;
  username: string;
  password: string | null;
  hasPassword: boolean;
  authPath: string;
  createOrderPath: string;
  createRmaPath: string;
  timeoutMs: number;
  isCreateOrderApiOn: boolean;
  updatedDate: string | null;
}

type TabKey = 'security' | 'api';

interface Banner {
  type: 'success' | 'error';
  title: string;
  message: string;
}

export default function OracleConfigPage() {
  const [config, setConfig] = useState<OracleConfigData | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);
  const [activeTab, setActiveTab] = useState<TabKey>('security');
  const [banner, setBanner] = useState<Banner | null>(null);

  // Form state
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [authPath, setAuthPath] = useState('');
  const [createOrderPath, setCreateOrderPath] = useState('');
  const [createRmaPath, setCreateRmaPath] = useState('');
  const [timeoutMs, setTimeoutMs] = useState(5000);
  const [isCreateOrderApiOn, setIsCreateOrderApiOn] = useState(false);

  const loadConfig = useCallback(async () => {
    try {
      const res = await apiFetch(API_BASE);
      if (res.ok) {
        const data: OracleConfigData = await res.json();
        setConfig(data);
        setUsername(data.username || '');
        setAuthPath(data.authPath || '');
        setCreateOrderPath(data.createOrderPath || '');
        setCreateRmaPath(data.createRmaPath || '');
        setTimeoutMs(data.timeoutMs || 5000);
        setIsCreateOrderApiOn(data.isCreateOrderApiOn || false);
      }
    } catch (err) {
      console.error('Failed to load Oracle config:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadConfig(); }, [loadConfig]);

  async function handleSave() {
    setSaving(true);
    setBanner(null);
    try {
      const res = await apiFetch(API_BASE, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username,
          password: password || null,
          authPath,
          createOrderPath,
          createRmaPath,
          timeoutMs,
          isCreateOrderApiOn,
        }),
      });
      if (res.ok) {
        const data: OracleConfigData = await res.json();
        setConfig(data);
        setPassword('');
        setBanner({ type: 'success', title: 'Saved', message: 'Oracle configuration updated successfully.' });
      } else {
        setBanner({ type: 'error', title: 'Save failed', message: 'Could not save configuration.' });
      }
    } catch (err) {
      setBanner({ type: 'error', title: 'Save failed', message: String(err) });
    } finally {
      setSaving(false);
    }
  }

  async function handleTestAuth() {
    setTesting(true);
    setBanner(null);
    try {
      const res = await apiFetch(`${API_BASE}/test-auth`, { method: 'POST' });
      const data = await res.json();
      setBanner({
        type: data.success ? 'success' : 'error',
        title: data.title,
        message: data.message,
      });
    } catch (err) {
      setBanner({ type: 'error', title: 'Connection failed', message: String(err) });
    } finally {
      setTesting(false);
    }
  }

  function formatDate(iso: string | null): string {
    if (!iso) return '-';
    const d = new Date(iso);
    return d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' })
      + ' ' + d.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' });
  }

  if (loading) return <div style={{ padding: '48px', color: '#888', textAlign: 'center' }}>Loading configuration...</div>;

  return (
    <div className={styles.pageContainer}>
      <div className={styles.pageHeader}>
        <h2 className={styles.pageTitle}>Oracle Configuration</h2>
        {config?.updatedDate && (
          <span className={styles.lastUpdated}>Last updated: {formatDate(config.updatedDate)}</span>
        )}
      </div>

      {/* Banner */}
      {banner && (
        <div className={`${styles.banner} ${banner.type === 'success' ? styles.bannerSuccess : styles.bannerError}`}>
          <strong>{banner.title}</strong> — {banner.message}
        </div>
      )}

      {/* Tab bar — mirrors Mendix TabContainer with Security + API tabs */}
      <div className={styles.tabBar}>
        <button
          className={`${styles.tab} ${activeTab === 'security' ? styles.tabActive : ''}`}
          onClick={() => setActiveTab('security')}
        >
          Security
        </button>
        <button
          className={`${styles.tab} ${activeTab === 'api' ? styles.tabActive : ''}`}
          onClick={() => setActiveTab('api')}
        >
          API
        </button>
      </div>

      <div className={styles.formCard}>
        {/* ── Security Tab ── */}
        {activeTab === 'security' && (
          <>
            <div className={styles.formGroup}>
              <label className={styles.formLabel}>Oracle Username</label>
              <input
                className={styles.formInput}
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Oracle API username"
              />
            </div>

            <div className={styles.formGroup}>
              <label className={styles.formLabel}>Oracle Password</label>
              <div className={styles.passwordWrapper}>
                <input
                  className={styles.formInput}
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder={config?.hasPassword ? '••••••••  (leave blank to keep current)' : 'Enter password'}
                />
                {config?.hasPassword && !password && (
                  <div className={styles.passwordMask}>Password is set</div>
                )}
              </div>
            </div>

            {/* Mendix: IsOracleCreateOrderAPIOn checkbox */}
            <div className={styles.toggleRow}>
              <span className={styles.toggleLabel}>Create Order API</span>
              <button
                type="button"
                className={`${styles.toggleSwitch} ${isCreateOrderApiOn ? styles.toggleOn : ''}`}
                onClick={() => setIsCreateOrderApiOn(!isCreateOrderApiOn)}
                aria-label="Toggle Create Order API"
              />
              <span className={styles.toggleStatusText}>
                {isCreateOrderApiOn ? 'Enabled' : 'Disabled'}
              </span>
            </div>

            <div className={styles.actionsRow}>
              <button className={styles.testBtn} onClick={handleTestAuth} disabled={testing}>
                {testing ? 'Testing...' : 'Test Authentication'}
              </button>
            </div>
          </>
        )}

        {/* ── API Tab ── */}
        {activeTab === 'api' && (
          <>
            <div className={styles.formGroup}>
              <label className={styles.formLabel}>Auth Token Path</label>
              <input
                className={styles.formInput}
                type="text"
                value={authPath}
                onChange={(e) => setAuthPath(e.target.value)}
                placeholder="https://api-dev.ecoatm.com/..."
              />
              <div className={styles.formHint}>Oracle OAuth token endpoint URL</div>
            </div>

            <div className={styles.formGroup}>
              <label className={styles.formLabel}>Create Order Path</label>
              <input
                className={styles.formInput}
                type="text"
                value={createOrderPath}
                onChange={(e) => setCreateOrderPath(e.target.value)}
                placeholder="https://api-dev.ecoatm.com/..."
              />
              <div className={styles.formHint}>Oracle Create Order REST endpoint</div>
            </div>

            <div className={styles.formGroup}>
              <label className={styles.formLabel}>Create RMA Path</label>
              <input
                className={styles.formInput}
                type="text"
                value={createRmaPath}
                onChange={(e) => setCreateRmaPath(e.target.value)}
                placeholder="https://api-dev.ecoatm.com/..."
              />
              <div className={styles.formHint}>Oracle Create RMA REST endpoint</div>
            </div>

            <div className={styles.formGroup}>
              <label className={styles.formLabel}>HTTP Timeout (ms)</label>
              <input
                className={styles.formInputShort}
                type="number"
                value={timeoutMs}
                onChange={(e) => setTimeoutMs(Number(e.target.value))}
                min={1000}
                max={300000}
                step={1000}
              />
              <div className={styles.formHint}>Timeout for Oracle API calls (default: 5000ms)</div>
            </div>
          </>
        )}

        {/* Save button — shown on both tabs */}
        <div className={styles.actionsRow}>
          <button className={styles.saveBtn} onClick={handleSave} disabled={saving}>
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </div>
    </div>
  );
}
