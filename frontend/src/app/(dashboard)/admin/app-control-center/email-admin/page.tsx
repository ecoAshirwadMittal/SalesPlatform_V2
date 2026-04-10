'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import e from './emailAdmin.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API = '/api/v1/admin/email';

async function jsonOrThrow<T = unknown>(res: Response): Promise<T> {
  const text = await res.text();
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}${text ? ': ' + text.slice(0, 200) : ''}`);
  if (!text) return {} as T;
  try { return JSON.parse(text) as T; } catch { throw new Error(text.slice(0, 200)); }
}

// ── Types ──

interface SmtpConfig {
  id: number;
  server_host: string;
  server_port: number;
  protocol: string;
  username: string;
  from_address: string;
  from_display_name: string;
  reply_to: string;
  use_ssl: boolean;
  use_tls: boolean;
  enabled: boolean;
  max_retry_attempts: number;
  timeout_ms: number;
}

interface EmailTemplate {
  id: number;
  template_name: string;
  from_address: string | null;
  from_display_name: string | null;
  reply_to: string | null;
  to_default: string | null;
  cc_default: string | null;
  bcc_default: string | null;
  subject: string | null;
  content_html: string | null;
  content_plain: string | null;
  has_attachment: boolean;
  created_date: string;
  updated_date: string;
}

interface EmailLogEntry {
  id: number;
  template_name: string | null;
  from_address: string | null;
  to_address: string;
  cc: string | null;
  bcc: string | null;
  subject: string | null;
  status: string;
  sent_date: string | null;
  error_message: string | null;
  retry_count: number;
  created_date: string;
}

interface Banner {
  type: 'success' | 'error';
  message: string;
}

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

// ══════════════════════════════════════════════════════════════════
// TAB 1: SMTP Configuration
// ══════════════════════════════════════════════════════════════════

function SmtpConfigTab({ onBanner }: { onBanner: (b: Banner) => void }) {
  const [cfg, setCfg] = useState<Partial<SmtpConfig>>({});
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const res = await apiFetch(`${API}/smtp`);
        setCfg(await jsonOrThrow(res));
      } catch (err) {
        onBanner({ type: 'error', message: String(err) });
      } finally {
        setLoading(false);
      }
    })();
  }, [onBanner]);

  async function handleSave() {
    setSaving(true);
    try {
      const res = await apiFetch(`${API}/smtp`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          serverHost: cfg.server_host || '',
          serverPort: cfg.server_port || 587,
          protocol: cfg.protocol || 'SMTP',
          username: cfg.username || '',
          encryptedPassword: password || '',
          fromAddress: cfg.from_address || '',
          fromDisplayName: cfg.from_display_name || '',
          replyTo: cfg.reply_to || '',
          useSsl: cfg.use_ssl || false,
          useTls: cfg.use_tls ?? true,
          enabled: cfg.enabled || false,
          maxRetryAttempts: cfg.max_retry_attempts || 3,
          timeoutMs: cfg.timeout_ms || 10000,
        }),
      });
      await jsonOrThrow(res);
      setPassword('');
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
      const res = await apiFetch(`${API}/smtp/test`, { method: 'POST' });
      const data = await jsonOrThrow<{ success: boolean; message: string }>(res);
      onBanner({ type: data.success ? 'success' : 'error', message: data.message });
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setTesting(false);
    }
  }

  if (loading) return <div className={s.loading}>Loading SMTP configuration...</div>;

  function updateCfg(field: string, value: string | number | boolean) {
    setCfg((prev) => ({ ...prev, [field]: value }));
  }

  return (
    <div className={s.formCard} style={{ maxWidth: '100%' }}>
      <h3 className={s.sectionTitle}>Server Settings</h3>
      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Server Host</label>
          <input className={s.formInput} value={cfg.server_host || ''} onChange={(ev) => updateCfg('server_host', ev.target.value)} placeholder="smtp.example.com" />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Port</label>
          <input className={s.formInputShort} type="number" value={cfg.server_port || 587} onChange={(ev) => updateCfg('server_port', parseInt(ev.target.value) || 587)} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Protocol</label>
          <input className={s.formInputShort} value={cfg.protocol || 'SMTP'} onChange={(ev) => updateCfg('protocol', ev.target.value)} />
        </div>
      </div>

      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Username</label>
          <input className={s.formInput} value={cfg.username || ''} onChange={(ev) => updateCfg('username', ev.target.value)} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Password</label>
          <input className={s.formInput} type="password" value={password} onChange={(ev) => setPassword(ev.target.value)} placeholder="(unchanged if empty)" />
        </div>
      </div>

      <h3 className={s.sectionTitle}>Email Identity</h3>
      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>From Address</label>
          <input className={s.formInput} value={cfg.from_address || ''} onChange={(ev) => updateCfg('from_address', ev.target.value)} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>From Display Name</label>
          <input className={s.formInput} value={cfg.from_display_name || ''} onChange={(ev) => updateCfg('from_display_name', ev.target.value)} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Reply-To</label>
          <input className={s.formInput} value={cfg.reply_to || ''} onChange={(ev) => updateCfg('reply_to', ev.target.value)} />
        </div>
      </div>

      <h3 className={s.sectionTitle}>Security &amp; Behavior</h3>
      <div className={e.toggleGrid}>
        <div className={s.toggleRow}>
          <button type="button" className={`${s.toggleSwitch} ${cfg.use_ssl ? s.toggleSwitchOn : ''}`} onClick={() => updateCfg('use_ssl', !cfg.use_ssl)} />
          <span className={s.toggleLabel}>Use SSL</span>
        </div>
        <div className={s.toggleRow}>
          <button type="button" className={`${s.toggleSwitch} ${cfg.use_tls ? s.toggleSwitchOn : ''}`} onClick={() => updateCfg('use_tls', !cfg.use_tls)} />
          <span className={s.toggleLabel}>Use TLS</span>
        </div>
        <div className={s.toggleRow}>
          <button type="button" className={`${s.toggleSwitch} ${cfg.enabled ? s.toggleSwitchOn : ''}`} onClick={() => updateCfg('enabled', !cfg.enabled)} />
          <span className={s.toggleLabel}>Email Sending Enabled</span>
        </div>
      </div>

      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Max Retry Attempts</label>
          <input className={s.formInputShort} type="number" value={cfg.max_retry_attempts || 3} onChange={(ev) => updateCfg('max_retry_attempts', parseInt(ev.target.value) || 3)} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Timeout (ms)</label>
          <input className={s.formInputShort} type="number" value={cfg.timeout_ms || 10000} onChange={(ev) => updateCfg('timeout_ms', parseInt(ev.target.value) || 10000)} />
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

// ══════════════════════════════════════════════════════════════════
// TAB 2: Email Templates
// ══════════════════════════════════════════════════════════════════

function TemplatesTab({ onBanner }: { onBanner: (b: Banner) => void }) {
  const [templates, setTemplates] = useState<EmailTemplate[]>([]);
  const [loading, setLoading] = useState(true);
  const [editId, setEditId] = useState<number | null>(null);
  const [editData, setEditData] = useState<Partial<EmailTemplate>>({});
  const [adding, setAdding] = useState(false);
  const [newTpl, setNewTpl] = useState<Partial<EmailTemplate>>({ template_name: '', subject: '', content_html: '', content_plain: '' });

  const load = useCallback(async () => {
    try {
      const res = await apiFetch(`${API}/templates`);
      setTemplates(await jsonOrThrow<EmailTemplate[]>(res));
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }, [onBanner]);

  useEffect(() => { load(); }, [load]);

  async function handleSave(id: number) {
    try {
      const res = await apiFetch(`${API}/templates/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          templateName: editData.template_name,
          fromAddress: editData.from_address,
          fromDisplayName: editData.from_display_name,
          replyTo: editData.reply_to,
          toDefault: editData.to_default,
          ccDefault: editData.cc_default,
          bccDefault: editData.bcc_default,
          subject: editData.subject,
          contentHtml: editData.content_html,
          contentPlain: editData.content_plain,
          hasAttachment: editData.has_attachment || false,
        }),
      });
      await jsonOrThrow(res);
      setEditId(null);
      onBanner({ type: 'success', message: 'Template updated.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  async function handleCreate() {
    try {
      const res = await apiFetch(`${API}/templates`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          templateName: newTpl.template_name,
          fromAddress: newTpl.from_address,
          fromDisplayName: newTpl.from_display_name,
          replyTo: newTpl.reply_to,
          toDefault: newTpl.to_default,
          ccDefault: newTpl.cc_default,
          bccDefault: newTpl.bcc_default,
          subject: newTpl.subject,
          contentHtml: newTpl.content_html,
          contentPlain: newTpl.content_plain,
          hasAttachment: false,
        }),
      });
      await jsonOrThrow(res);
      setAdding(false);
      setNewTpl({ template_name: '', subject: '', content_html: '', content_plain: '' });
      onBanner({ type: 'success', message: 'Template created.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  async function handleDelete(id: number) {
    if (!confirm('Delete this email template?')) return;
    try {
      const res = await apiFetch(`${API}/templates/${id}`, { method: 'DELETE' });
      await jsonOrThrow(res);
      onBanner({ type: 'success', message: 'Template deleted.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  if (loading) return <div className={s.loading}>Loading templates...</div>;

  return (
    <>
      <div className={s.toolbar}>
        <button className={s.addBtn} onClick={() => setAdding(true)}>+ New Template</button>
      </div>

      <table className={s.table}>
        <thead>
          <tr>
            <th style={{ width: '18%' }}>Template Name</th>
            <th style={{ width: '17%' }}>From Name</th>
            <th style={{ width: '35%' }}>Subject</th>
            <th style={{ width: '10%' }}>Attachment</th>
            <th style={{ width: '12%' }}>Created</th>
            <th style={{ width: '8%' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {adding && (
            <tr>
              <td><input className={s.cellInput} value={newTpl.template_name || ''} onChange={(ev) => setNewTpl({ ...newTpl, template_name: ev.target.value })} placeholder="Template Name" /></td>
              <td><input className={s.cellInput} value={newTpl.from_display_name || ''} onChange={(ev) => setNewTpl({ ...newTpl, from_display_name: ev.target.value })} placeholder="From Name" /></td>
              <td><input className={s.cellInput} value={newTpl.subject || ''} onChange={(ev) => setNewTpl({ ...newTpl, subject: ev.target.value })} placeholder="Subject" /></td>
              <td>—</td>
              <td>—</td>
              <td className={s.actionsCell}>
                <button className={s.saveRowBtn} onClick={handleCreate}>Save</button>
                <button className={s.actionBtn} onClick={() => setAdding(false)}>Cancel</button>
              </td>
            </tr>
          )}
          {templates.map((tpl) => (
            <tr key={tpl.id}>
              {editId === tpl.id ? (
                <>
                  <td><input className={s.cellInput} value={editData.template_name || ''} onChange={(ev) => setEditData({ ...editData, template_name: ev.target.value })} /></td>
                  <td><input className={s.cellInput} value={editData.from_display_name || ''} onChange={(ev) => setEditData({ ...editData, from_display_name: ev.target.value })} /></td>
                  <td><input className={s.cellInput} value={editData.subject || ''} onChange={(ev) => setEditData({ ...editData, subject: ev.target.value })} /></td>
                  <td>{tpl.has_attachment ? 'Yes' : 'No'}</td>
                  <td>{formatDate(tpl.created_date)}</td>
                  <td className={s.actionsCell}>
                    <button className={s.saveRowBtn} onClick={() => handleSave(tpl.id)}>Save</button>
                    <button className={s.actionBtn} onClick={() => setEditId(null)}>Cancel</button>
                  </td>
                </>
              ) : (
                <>
                  <td>
                    <button className={e.templateLink} onClick={() => { setEditId(tpl.id); setEditData({ ...tpl }); }}>
                      {tpl.template_name}
                    </button>
                  </td>
                  <td>{tpl.from_display_name || '—'}</td>
                  <td>{tpl.subject || '—'}</td>
                  <td>{tpl.has_attachment ? 'Yes' : 'No'}</td>
                  <td>{formatDate(tpl.created_date)}</td>
                  <td className={s.actionsCell}>
                    <button className={s.actionBtn} onClick={() => { setEditId(tpl.id); setEditData({ ...tpl }); }}>Edit</button>
                    <button className={s.deleteBtn} onClick={() => handleDelete(tpl.id)}>Delete</button>
                  </td>
                </>
              )}
            </tr>
          ))}
          {templates.length === 0 && !adding && (
            <tr><td colSpan={6} className={s.emptyState}>No email templates found.</td></tr>
          )}
        </tbody>
      </table>

      {editId !== null && (
        <TemplateDetailEditor
          data={editData}
          onChange={setEditData}
          onSave={() => handleSave(editId)}
          onCancel={() => setEditId(null)}
        />
      )}
    </>
  );
}

function TemplateDetailEditor({
  data, onChange, onSave, onCancel,
}: {
  data: Partial<EmailTemplate>;
  onChange: (d: Partial<EmailTemplate>) => void;
  onSave: () => void;
  onCancel: () => void;
}) {
  const [previewTab, setPreviewTab] = useState<'html' | 'plain'>('html');

  return (
    <div className={e.detailEditor}>
      <h3 className={s.sectionTitle}>Edit Template: {data.template_name}</h3>
      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>From Address</label>
          <input className={s.formInput} value={data.from_address || ''} onChange={(ev) => onChange({ ...data, from_address: ev.target.value })} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>Reply-To</label>
          <input className={s.formInput} value={data.reply_to || ''} onChange={(ev) => onChange({ ...data, reply_to: ev.target.value })} />
        </div>
      </div>
      <div className={e.formGrid}>
        <div className={s.formGroup}>
          <label className={s.formLabel}>To (default)</label>
          <input className={s.formInput} value={data.to_default || ''} onChange={(ev) => onChange({ ...data, to_default: ev.target.value })} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>CC (default)</label>
          <input className={s.formInput} value={data.cc_default || ''} onChange={(ev) => onChange({ ...data, cc_default: ev.target.value })} />
        </div>
        <div className={s.formGroup}>
          <label className={s.formLabel}>BCC (default)</label>
          <input className={s.formInput} value={data.bcc_default || ''} onChange={(ev) => onChange({ ...data, bcc_default: ev.target.value })} />
        </div>
      </div>
      <div className={s.formGroup}>
        <label className={s.formLabel}>Subject</label>
        <input className={s.formInput} value={data.subject || ''} onChange={(ev) => onChange({ ...data, subject: ev.target.value })} />
        <div className={s.formHint}>Use {'{'} placeholders {'}'} like {'{offerNumber}'}, {'{buyerCode}'}, etc.</div>
      </div>

      <div className={e.contentTabs}>
        <button className={`${e.contentTab} ${previewTab === 'html' ? e.contentTabActive : ''}`} onClick={() => setPreviewTab('html')}>HTML Content</button>
        <button className={`${e.contentTab} ${previewTab === 'plain' ? e.contentTabActive : ''}`} onClick={() => setPreviewTab('plain')}>Plain Text</button>
      </div>

      {previewTab === 'html' ? (
        <div className={s.formGroup}>
          <textarea className={s.formTextarea} style={{ minHeight: 200 }} value={data.content_html || ''} onChange={(ev) => onChange({ ...data, content_html: ev.target.value })} placeholder="HTML email body..." />
        </div>
      ) : (
        <div className={s.formGroup}>
          <textarea className={s.formTextarea} style={{ minHeight: 200 }} value={data.content_plain || ''} onChange={(ev) => onChange({ ...data, content_plain: ev.target.value })} placeholder="Plain text email body..." />
        </div>
      )}

      <div className={s.actionsRow}>
        <button className={s.saveBtn} onClick={onSave}>Save Template</button>
        <button className={s.actionBtn} onClick={onCancel}>Cancel</button>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════════════════
// TAB 3: Email Log
// ══════════════════════════════════════════════════════════════════

function EmailLogTab({ onBanner }: { onBanner: (b: Banner) => void }) {
  const [entries, setEntries] = useState<EmailLogEntry[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [detailId, setDetailId] = useState<number | null>(null);
  const [detailData, setDetailData] = useState<EmailLogEntry & { content_html?: string | null } | null>(null);
  const pageSize = 50;

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({ page: String(page), size: String(pageSize) });
      if (statusFilter) params.set('status', statusFilter);
      const res = await apiFetch(`${API}/log?${params}`);
      const data = await jsonOrThrow<{ data: EmailLogEntry[]; total: number }>(res);
      setEntries(data.data || []);
      setTotal(data.total || 0);
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter, onBanner]);

  useEffect(() => { load(); }, [load]);

  async function handleRetry(id: number) {
    try {
      const res = await apiFetch(`${API}/log/${id}/retry`, { method: 'POST' });
      const data = await jsonOrThrow<{ message?: string }>(res);
      onBanner({ type: 'success', message: data.message || 'Re-queued.' });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  async function showDetail(id: number) {
    try {
      const res = await apiFetch(`${API}/log/${id}`);
      setDetailData(await jsonOrThrow(res));
      setDetailId(id);
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  const totalPages = Math.ceil(total / pageSize);

  return (
    <>
      <div className={s.toolbar}>
        <div className={e.filterRow}>
          <label className={s.formLabel} style={{ margin: 0 }}>Status:</label>
          <select className={e.filterSelect} value={statusFilter} onChange={(ev) => { setStatusFilter(ev.target.value); setPage(0); }}>
            <option value="">All</option>
            <option value="SENT">Sent</option>
            <option value="QUEUED">Queued</option>
            <option value="FAILED">Failed</option>
            <option value="ERROR">Error</option>
          </select>
          <span className={e.countLabel}>{total} email(s)</span>
        </div>
      </div>

      {loading ? (
        <div className={s.loading}>Loading email log...</div>
      ) : (
        <>
          <table className={s.table}>
            <thead>
              <tr>
                <th style={{ width: '12%' }}>Status</th>
                <th style={{ width: '18%' }}>To</th>
                <th style={{ width: '30%' }}>Subject</th>
                <th style={{ width: '14%' }}>Template</th>
                <th style={{ width: '14%' }}>Date</th>
                <th style={{ width: '12%' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {entries.map((entry) => (
                <tr key={entry.id}>
                  <td><span className={`${e.statusBadge} ${e[`status${entry.status}`] || ''}`}>{entry.status}</span></td>
                  <td className={e.truncate}>{entry.to_address}</td>
                  <td className={e.truncate}>{entry.subject || '—'}</td>
                  <td>{entry.template_name || '—'}</td>
                  <td>{formatDate(entry.sent_date || entry.created_date)}</td>
                  <td className={s.actionsCell}>
                    <button className={s.actionBtn} onClick={() => showDetail(entry.id)}>View</button>
                    {(entry.status === 'FAILED' || entry.status === 'ERROR') && (
                      <button className={s.actionBtn} onClick={() => handleRetry(entry.id)}>Retry</button>
                    )}
                  </td>
                </tr>
              ))}
              {entries.length === 0 && (
                <tr><td colSpan={6} className={s.emptyState}>No email log entries found.</td></tr>
              )}
            </tbody>
          </table>

          {totalPages > 1 && (
            <div className={e.pagination}>
              <button className={s.actionBtn} disabled={page === 0} onClick={() => setPage(page - 1)}>← Prev</button>
              <span className={e.pageInfo}>Page {page + 1} of {totalPages}</span>
              <button className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>Next →</button>
            </div>
          )}
        </>
      )}

      {detailId !== null && detailData && (
        <div className={e.overlay} onClick={() => setDetailId(null)}>
          <div className={e.modal} onClick={(ev) => ev.stopPropagation()}>
            <div className={e.modalHeader}>
              <h3>Email Detail #{detailId}</h3>
              <button className={e.modalClose} onClick={() => setDetailId(null)}>×</button>
            </div>
            <div className={e.modalBody}>
              <div className={e.detailRow}><strong>From:</strong> {String(detailData.from_address || '—')}</div>
              <div className={e.detailRow}><strong>To:</strong> {String(detailData.to_address || '—')}</div>
              {detailData.cc && <div className={e.detailRow}><strong>CC:</strong> {String(detailData.cc)}</div>}
              {detailData.bcc && <div className={e.detailRow}><strong>BCC:</strong> {String(detailData.bcc)}</div>}
              <div className={e.detailRow}><strong>Subject:</strong> {String(detailData.subject || '—')}</div>
              <div className={e.detailRow}><strong>Status:</strong> {String(detailData.status)}</div>
              {detailData.error_message && <div className={e.detailRow}><strong>Error:</strong> <span className={e.errorText}>{String(detailData.error_message)}</span></div>}
              <div className={e.detailRow}><strong>Template:</strong> {String(detailData.template_name || '—')}</div>
              <div className={e.detailRow}><strong>Retry Count:</strong> {String(detailData.retry_count)}</div>
              <div className={e.detailRow}><strong>Created:</strong> {formatDate(String(detailData.created_date))}</div>
              {detailData.sent_date && <div className={e.detailRow}><strong>Sent:</strong> {formatDate(String(detailData.sent_date))}</div>}
              {detailData.content_html && (
                <>
                  <h4 style={{ marginTop: 16, marginBottom: 8 }}>HTML Body</h4>
                  <div className={e.htmlPreview} dangerouslySetInnerHTML={{ __html: String(detailData.content_html) }} />
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}

// ── Helpers ──

function formatDate(dateStr: string | null): string {
  if (!dateStr) return '—';
  try {
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  } catch {
    return dateStr;
  }
}
