'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

/**
 * Master Data — admin tabbed CRUD over MDM lookup tables
 * (Phase 3, pws-data-center-port.md). Consolidates the legacy
 * Device Brands/Colors/Categories/Capacities/Carriers/Grades/Models grids
 * into a single tabbed screen backed by /api/v1/admin/master-data/{type}.
 * Delete is a soft disable (is_enabled=false) — hard deletes would break
 * device FK integrity.
 */

const API_BASE = '/api/v1/admin/master-data';

type TabKey = 'brands' | 'colors' | 'categories' | 'capacities' | 'carriers' | 'grades' | 'models';

const TABS: Array<{ key: TabKey; label: string }> = [
  { key: 'brands',     label: 'Brands' },
  { key: 'models',     label: 'Models' },
  { key: 'categories', label: 'Categories' },
  { key: 'capacities', label: 'Capacities' },
  { key: 'carriers',   label: 'Carriers' },
  { key: 'colors',     label: 'Colors' },
  { key: 'grades',     label: 'Grades' },
];

interface MasterDataItem {
  id: number;
  name: string;
  displayName: string | null;
  isEnabled: boolean;
  sortRank: number | null;
}

interface PageResponse {
  content: MasterDataItem[];
  totalElements: number;
  totalPages: number;
  number: number;
}

interface DraftState {
  id: number | null;
  name: string;
  displayName: string;
  sortRank: string;
  isEnabled: boolean;
}

const EMPTY_DRAFT: DraftState = {
  id: null,
  name: '',
  displayName: '',
  sortRank: '',
  isEnabled: true,
};

export default function MasterDataPage() {
  const [activeTab, setActiveTab] = useState<TabKey>('brands');
  const [rows, setRows] = useState<MasterDataItem[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [nameFilter, setNameFilter] = useState('');
  const [draft, setDraft] = useState<DraftState | null>(null);
  const [saving, setSaving] = useState(false);

  const load = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch(`${API_BASE}/${activeTab}?page=${page}&size=50`);
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || `HTTP ${res.status}`);
      }
      const data: PageResponse = await res.json();
      setRows(Array.isArray(data.content) ? data.content : []);
      setTotalPages(Math.max(1, data.totalPages ?? 1));
      setTotalElements(data.totalElements ?? 0);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load items');
    } finally {
      setLoading(false);
    }
  }, [activeTab, page]);

  useEffect(() => { void load(); }, [load]);

  const openCreate = (): void => setDraft({ ...EMPTY_DRAFT });
  const openEdit = (row: MasterDataItem): void => setDraft({
    id: row.id,
    name: row.name ?? '',
    displayName: row.displayName ?? '',
    sortRank: row.sortRank != null ? String(row.sortRank) : '',
    isEnabled: row.isEnabled,
  });
  const closeDraft = (): void => setDraft(null);

  const saveDraft = async (): Promise<void> => {
    if (!draft) return;
    if (!draft.name.trim()) {
      setError('Name is required');
      return;
    }
    setSaving(true);
    setError(null);
    try {
      const body = JSON.stringify({
        name: draft.name.trim(),
        displayName: draft.displayName.trim() || null,
        sortRank: draft.sortRank.trim() ? Number(draft.sortRank) : null,
        isEnabled: draft.isEnabled,
      });
      const url = draft.id == null ? `${API_BASE}/${activeTab}` : `${API_BASE}/${activeTab}/${draft.id}`;
      const res = await apiFetch(url, {
        method: draft.id == null ? 'POST' : 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body,
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || `HTTP ${res.status}`);
      }
      setDraft(null);
      await load();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  const softDelete = async (row: MasterDataItem): Promise<void> => {
    if (!confirm(`Disable ${row.name}? Devices referencing it will keep the FK but it will be hidden from new selections.`)) return;
    setError(null);
    try {
      const res = await apiFetch(`${API_BASE}/${activeTab}/${row.id}`, { method: 'DELETE' });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || `HTTP ${res.status}`);
      }
      await load();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Delete failed');
    }
  };

  const switchTab = (tab: TabKey): void => {
    setActiveTab(tab);
    setPage(0);
    setNameFilter('');
    setDraft(null);
    setError(null);
  };

  const filtered = nameFilter.trim()
    ? rows.filter((r) => (r.name ?? '').toLowerCase().includes(nameFilter.trim().toLowerCase()))
    : rows;

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <Link href="/admin/pws-data-center" className={s.backLink}>← PWS Data Center</Link>
        <h2 className={s.pageTitle}>Master Data</h2>
      </div>

      <div className={s.card} style={{ marginBottom: 12 }}>
        <div style={{ display: 'flex', gap: 4, padding: 8, borderBottom: '1px solid #e0e0e0', flexWrap: 'wrap' }}>
          {TABS.map((t) => (
            <button
              key={t.key}
              type="button"
              onClick={() => switchTab(t.key)}
              style={{
                padding: '8px 16px',
                border: 'none',
                background: activeTab === t.key ? '#407874' : 'transparent',
                color: activeTab === t.key ? '#fff' : '#333',
                fontWeight: activeTab === t.key ? 600 : 400,
                borderRadius: 4,
                cursor: 'pointer',
              }}
            >
              {t.label}
            </button>
          ))}
        </div>
      </div>

      {error && <div className={`${s.banner} ${s.bannerError}`}>{error}</div>}

      <div className={s.card}>
        <div className={s.toolbar}>
          <input
            type="text"
            placeholder="Name contains…"
            value={nameFilter}
            onChange={(e) => setNameFilter(e.target.value)}
            className={s.cellInput}
            style={{ maxWidth: 240 }}
          />
          <button type="button" className={s.actionBtn} onClick={openCreate} style={{ marginLeft: 8 }}>
            + New
          </button>
          <span style={{ marginLeft: 'auto', color: '#666', fontSize: 13 }}>
            {loading ? 'Loading…' : `${totalElements} item${totalElements === 1 ? '' : 's'}`}
          </span>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Display Name</th>
              <th style={{ textAlign: 'right' }}>Sort Rank</th>
              <th>Enabled</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 && !loading && (
              <tr><td colSpan={6} style={{ textAlign: 'center', color: '#888', padding: 24 }}>No items found.</td></tr>
            )}
            {filtered.map((r) => (
              <tr key={r.id} style={{ opacity: r.isEnabled ? 1 : 0.55 }}>
                <td>{r.id}</td>
                <td>{r.name}</td>
                <td>{r.displayName ?? '—'}</td>
                <td style={{ textAlign: 'right' }}>{r.sortRank ?? '—'}</td>
                <td>{r.isEnabled ? 'Yes' : 'No'}</td>
                <td style={{ textAlign: 'right' }}>
                  <button type="button" className={s.actionBtn} onClick={() => openEdit(r)}>Edit</button>
                  {r.isEnabled && (
                    <button
                      type="button"
                      className={s.actionBtn}
                      onClick={() => void softDelete(r)}
                      style={{ marginLeft: 4 }}
                    >
                      Disable
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', padding: 12, alignItems: 'center' }}>
          <button type="button" className={s.actionBtn} disabled={page === 0} onClick={() => setPage(0)}>« First</button>
          <button type="button" className={s.actionBtn} disabled={page === 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>‹ Prev</button>
          <span style={{ fontSize: 13, color: '#555' }}>Page {page + 1} of {totalPages}</span>
          <button type="button" className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next ›</button>
          <button type="button" className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage(totalPages - 1)}>Last »</button>
        </div>
      </div>

      {draft && (
        <div
          style={{
            position: 'fixed',
            top: 0, left: 0, right: 0, bottom: 0,
            background: 'rgba(0,0,0,0.4)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
          }}
          onClick={closeDraft}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              background: '#fff',
              borderRadius: 8,
              padding: 24,
              minWidth: 400,
              maxWidth: 500,
              boxShadow: '0 8px 24px rgba(0,0,0,0.2)',
            }}
          >
            <h3 style={{ marginTop: 0, color: '#112d32' }}>
              {draft.id == null ? 'New' : 'Edit'} {TABS.find((t) => t.key === activeTab)?.label.replace(/s$/, '')}
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <label style={{ fontSize: 13, color: '#555' }}>
                Name *
                <input
                  type="text"
                  value={draft.name}
                  onChange={(e) => setDraft({ ...draft, name: e.target.value })}
                  className={s.cellInput}
                  style={{ width: '100%', marginTop: 4 }}
                  autoFocus
                />
              </label>
              <label style={{ fontSize: 13, color: '#555' }}>
                Display Name
                <input
                  type="text"
                  value={draft.displayName}
                  onChange={(e) => setDraft({ ...draft, displayName: e.target.value })}
                  className={s.cellInput}
                  style={{ width: '100%', marginTop: 4 }}
                />
              </label>
              <label style={{ fontSize: 13, color: '#555' }}>
                Sort Rank
                <input
                  type="number"
                  value={draft.sortRank}
                  onChange={(e) => setDraft({ ...draft, sortRank: e.target.value })}
                  className={s.cellInput}
                  style={{ width: '100%', marginTop: 4 }}
                />
              </label>
              <label style={{ fontSize: 13, color: '#555', display: 'flex', alignItems: 'center', gap: 8 }}>
                <input
                  type="checkbox"
                  checked={draft.isEnabled}
                  onChange={(e) => setDraft({ ...draft, isEnabled: e.target.checked })}
                />
                Enabled
              </label>
            </div>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 20 }}>
              <button type="button" className={s.actionBtn} onClick={closeDraft} disabled={saving}>Cancel</button>
              <button
                type="button"
                className={s.actionBtn}
                onClick={() => void saveDraft()}
                disabled={saving}
                style={{ background: '#407874', color: '#fff' }}
              >
                {saving ? 'Saving…' : 'Save'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
