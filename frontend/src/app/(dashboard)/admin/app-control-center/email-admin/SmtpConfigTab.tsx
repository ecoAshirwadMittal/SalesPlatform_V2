'use client';

import { useState, useEffect } from 'react';
import s from '../../../settings/pws-control-center/admin.module.css';
import e from './emailAdmin.module.css';
import { getSmtpConfig, updateSmtpConfig, testSmtpConnection, type SmtpConfigView } from '@/lib/adminEmailClient';
import { formatDate, type Banner } from './shared';

/**
 * Task 10 — SMTP Settings tab.
 *
 * `SmtpConfigView`/`SmtpConfigUpdate` have no password (or username) field
 * by design (D2 — `docs/tasks/email-management-design-2026-07-10.md` §6):
 * the SMTP password is env-only (`spring.mail.password`) and can never be
 * read or written through this endpoint, so this form intentionally has
 * no Username/Password inputs — rendering one would silently do nothing.
 */
export function SmtpConfigTab({ onBanner }: { onBanner: (b: Banner) => void }) {
  const [cfg, setCfg] = useState<Partial<SmtpConfigView>>({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        setCfg(await getSmtpConfig());
      } catch (err) {
        onBanner({ type: 'error', message: String(err) });
      } finally {
        setLoading(false);
      }
    })();
  }, [onBanner]);

  function set<K extends keyof SmtpConfigView>(field: K, value: SmtpConfigView[K]) {
    setCfg((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSave() {
    setSaving(true);
    try {
      const updated = await updateSmtpConfig({
        serverHost: cfg.serverHost || '',
        serverPort: cfg.serverPort || 587,
        protocol: cfg.protocol || 'SMTP',
        fromAddress: cfg.fromAddress || '',
        fromDisplayName: cfg.fromDisplayName || '',
        replyTo: cfg.replyTo || '',
        useSsl: cfg.useSsl || false,
        useTls: cfg.useTls ?? true,
        enabled: cfg.enabled || false,
        maxRetryAttempts: cfg.maxRetryAttempts || 3,
        timeoutMs: cfg.timeoutMs || 10000,
      });
      setCfg(updated);
      onBanner({ type: 'success', message: 'SMTP configuration saved.' });
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setSaving(false);
    }
  }

  async function handleTest() {
    setTesting(true);
    try {
      const result = await testSmtpConnection();
      onBanner({ type: result.success ? 'success' : 'error', message: result.message });
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setTesting(false);
    }
  }

  if (loading) return <div className={s.loading}>Loading SMTP configuration...</div>;

  return (
    <div className={s.formCard} style={{ maxWidth: '100%' }}>
      {cfg.changedDate && (
        <p className={e.countLabel} style={{ marginBottom: 12 }}>Last updated {formatDate(cfg.changedDate)}</p>
      )}

      <h3 className={s.sectionTitle}>Server Settings</h3>
      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-server-host">Server Host</label>
          <input
            id="smtp-server-host"
            className={s.formInput}
            value={cfg.serverHost || ''}
            onChange={(ev) => set('serverHost', ev.target.value)}
            placeholder="smtp.example.com"
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-server-port">Port</label>
          <input
            id="smtp-server-port"
            className={s.formInputShort}
            type="number"
            value={cfg.serverPort || 587}
            onChange={(ev) => set('serverPort', parseInt(ev.target.value, 10) || 587)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-protocol">Protocol</label>
          <input
            id="smtp-protocol"
            className={s.formInputShort}
            value={cfg.protocol || 'SMTP'}
            onChange={(ev) => set('protocol', ev.target.value)}
          />
        </div>
      </div>

      <h3 className={s.sectionTitle}>Email Identity</h3>
      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-from-address">From Address</label>
          <input
            id="smtp-from-address"
            className={s.formInput}
            value={cfg.fromAddress || ''}
            onChange={(ev) => set('fromAddress', ev.target.value)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-from-display-name">From Display Name</label>
          <input
            id="smtp-from-display-name"
            className={s.formInput}
            value={cfg.fromDisplayName || ''}
            onChange={(ev) => set('fromDisplayName', ev.target.value)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-reply-to">Reply-To</label>
          <input
            id="smtp-reply-to"
            className={s.formInput}
            value={cfg.replyTo || ''}
            onChange={(ev) => set('replyTo', ev.target.value)}
          />
        </div>
      </div>

      <h3 className={s.sectionTitle}>Security &amp; Behavior</h3>
      <div className={e.toggleGrid}>
        <div className={s.toggleRow}>
          <button
            type="button"
            aria-label="Use SSL"
            className={`${s.toggleSwitch} ${cfg.useSsl ? s.toggleSwitchOn : ''}`}
            onClick={() => set('useSsl', !cfg.useSsl)}
          />
          <span className={s.toggleLabel}>Use SSL</span>
        </div>
        <div className={s.toggleRow}>
          <button
            type="button"
            aria-label="Use TLS"
            className={`${s.toggleSwitch} ${cfg.useTls ? s.toggleSwitchOn : ''}`}
            onClick={() => set('useTls', !cfg.useTls)}
          />
          <span className={s.toggleLabel}>Use TLS</span>
        </div>
        <div className={s.toggleRow}>
          <button
            type="button"
            aria-label="Email Sending Enabled"
            className={`${s.toggleSwitch} ${cfg.enabled ? s.toggleSwitchOn : ''}`}
            onClick={() => set('enabled', !cfg.enabled)}
          />
          <span className={s.toggleLabel}>Email Sending Enabled</span>
        </div>
      </div>

      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-max-retry">Max Retry Attempts</label>
          <input
            id="smtp-max-retry"
            className={s.formInputShort}
            type="number"
            value={cfg.maxRetryAttempts || 3}
            onChange={(ev) => set('maxRetryAttempts', parseInt(ev.target.value, 10) || 3)}
          />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel} htmlFor="smtp-timeout">Timeout (ms)</label>
          <input
            id="smtp-timeout"
            className={s.formInputShort}
            type="number"
            value={cfg.timeoutMs || 10000}
            onChange={(ev) => set('timeoutMs', parseInt(ev.target.value, 10) || 10000)}
          />
        </div>
      </div>

      <div className={s.actionsRow}>
        <button className={s.saveBtn} disabled={saving} onClick={handleSave}>
          {saving ? 'Saving...' : 'Save Configuration'}
        </button>
        <button className={`${s.actionBtn} ${e.testBtn}`} disabled={testing} onClick={handleTest}>
          {testing ? 'Testing...' : 'Test Connection'}
        </button>
      </div>
    </div>
  );
}
