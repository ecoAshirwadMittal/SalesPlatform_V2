'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

/**
 * PWS Devices — admin overview.
 *
 * Phase 2 (pws-data-center-port.md): adds inline future-price editing
 * and soft-delete with a required-reason modal. Mutations hit the
 * existing PUT /pws/pricing/devices/{id} and the new Phase 2
 * DELETE /pws/pricing/devices/{id} → pws.admin_audit_log.
 */

const API = '/api/v1/pws/pricing/devices';

interface PricingDevice {
  id: number;
  sku: string;
  categoryName: string | null;
  brandName: string | null;
  modelName: string | null;
  carrierName: string | null;
  capacityName: string | null;
  colorName: string | null;
  gradeName: string | null;
  currentListPrice: number | null;
  currentMinPrice: number | null;
  futureListPrice: number | null;
  futureMinPrice: number | null;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

interface EditDraft {
  futureListPrice: string;
  futureMinPrice: string;
}

const PAGE_SIZE = 25;

function parsePrice(value: string): number | null {
  const trimmed = value.trim();
  if (!trimmed) return null;
  const parsed = Number(trimmed);
  return Number.isFinite(parsed) ? parsed : null;
}

function formatPrice(value: number | null): string {
  if (value == null) return '—';
  return `$${Number(value).toFixed(2)}`;
}

export default function PwsDevicesAdminPage() {
  const [items, setItems] = useState<PricingDevice[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [skuFilter, setSkuFilter] = useState('');
  const [brandFilter, setBrandFilter] = useState('');
  const [modelFilter, setModelFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [banner, setBanner] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [draft, setDraft] = useState<EditDraft>({ futureListPrice: '', futureMinPrice: '' });
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<PricingDevice | null>(null);
  const [deleteReason, setDeleteReason] = useState('');
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams({ page: String(page), size: String(PAGE_SIZE) });
      if (skuFilter.trim()) params.set('sku', skuFilter.trim());
      if (brandFilter.trim()) params.set('brand', brandFilter.trim());
      if (modelFilter.trim()) params.set('model', modelFilter.trim());

      const res = await apiFetch(`${API}?${params.toString()}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: PageResponse<PricingDevice> = await res.json();
      setItems(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setTotalElements(data.totalElements ?? 0);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load devices');
    } finally {
      setLoading(false);
    }
  }, [page, skuFilter, brandFilter, modelFilter]);

  useEffect(() => { void load(); }, [load]);

  function beginEdit(d: PricingDevice): void {
    setEditingId(d.id);
    setDraft({
      futureListPrice: d.futureListPrice != null ? String(d.futureListPrice) : '',
      futureMinPrice: d.futureMinPrice != null ? String(d.futureMinPrice) : '',
    });
  }

  function cancelEdit(): void {
    setEditingId(null);
    setDraft({ futureListPrice: '', futureMinPrice: '' });
  }

  async function saveEdit(id: number): Promise<void> {
    const futureListPrice = parsePrice(draft.futureListPrice);
    const futureMinPrice = parsePrice(draft.futureMinPrice);
    if (futureListPrice == null || futureMinPrice == null) {
      setBanner('Enter numeric values for both future prices.');
      return;
    }
    if (futureListPrice < futureMinPrice) {
      setBanner('Future list price must be ≥ future min price.');
      return;
    }
    setSaving(true);
    setBanner(null);
    try {
      const res = await apiFetch(`${API}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ futureListPrice, futureMinPrice }),
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      cancelEdit();
      setBanner('Future prices updated.');
      await load();
    } catch (err: unknown) {
      setBanner(err instanceof Error ? err.message : 'Failed to update device');
    } finally {
      setSaving(false);
    }
  }

  async function confirmDelete(): Promise<void> {
    if (!deleteTarget) return;
    const reason = deleteReason.trim();
    if (!reason) {
      setBanner('Reason is required for device deletion.');
      return;
    }
    setDeleting(true);
    setBanner(null);
    try {
      const res = await apiFetch(`${API}/${deleteTarget.id}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ reason }),
      });
      if (!res.ok) {
        const data = await res.json().catch(() => ({ error: `HTTP ${res.status}` }));
        throw new Error(data.error ?? `HTTP ${res.status}`);
      }
      setDeleteTarget(null);
      setDeleteReason('');
      setBanner(`Device ${deleteTarget.sku} soft-deleted.`);
      await load();
    } catch (err: unknown) {
      setBanner(err instanceof Error ? err.message : 'Failed to delete device');
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <Link href="/admin/pws-data-center" className={s.backLink}>← PWS Data Center</Link>
        <h2 className={s.pageTitle}>PWS Devices</h2>
      </div>

      {error && <div className={`${s.banner} ${s.bannerError}`}>{error}</div>}
      {banner && <div className={`${s.banner} ${s.bannerSuccess}`}>{banner}</div>}

      <div className={s.card}>
        <div className={s.toolbar}>
          <input
            type="text"
            placeholder="SKU contains…"
            value={skuFilter}
            onChange={(e) => { setSkuFilter(e.target.value); setPage(0); }}
            className={s.cellInput}
            style={{ maxWidth: 200 }}
          />
          <input
            type="text"
            placeholder="Brand contains…"
            value={brandFilter}
            onChange={(e) => { setBrandFilter(e.target.value); setPage(0); }}
            className={s.cellInput}
            style={{ maxWidth: 180, marginLeft: 8 }}
          />
          <input
            type="text"
            placeholder="Model contains…"
            value={modelFilter}
            onChange={(e) => { setModelFilter(e.target.value); setPage(0); }}
            className={s.cellInput}
            style={{ maxWidth: 180, marginLeft: 8 }}
          />
          <span style={{ marginLeft: 'auto', color: '#666', fontSize: 13 }}>
            {loading ? 'Loading…' : `${totalElements} device${totalElements === 1 ? '' : 's'}`}
          </span>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>SKU</th>
              <th>Brand</th>
              <th>Model</th>
              <th>Carrier</th>
              <th>Capacity</th>
              <th>Color</th>
              <th>Grade</th>
              <th style={{ textAlign: 'right' }}>List Price</th>
              <th style={{ textAlign: 'right' }}>Min Price</th>
              <th style={{ textAlign: 'right' }}>Future List</th>
              <th style={{ textAlign: 'right' }}>Future Min</th>
              <th style={{ width: 180 }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.length === 0 && !loading && (
              <tr><td colSpan={12} style={{ textAlign: 'center', color: '#888', padding: 24 }}>No devices found.</td></tr>
            )}
            {items.map((d) => {
              const isEditing = editingId === d.id;
              return (
                <tr key={d.id}>
                  <td>{d.sku}</td>
                  <td>{d.brandName ?? '—'}</td>
                  <td>{d.modelName ?? '—'}</td>
                  <td>{d.carrierName ?? '—'}</td>
                  <td>{d.capacityName ?? '—'}</td>
                  <td>{d.colorName ?? '—'}</td>
                  <td>{d.gradeName ?? '—'}</td>
                  <td style={{ textAlign: 'right' }}>{formatPrice(d.currentListPrice)}</td>
                  <td style={{ textAlign: 'right' }}>{formatPrice(d.currentMinPrice)}</td>
                  <td style={{ textAlign: 'right' }}>
                    {isEditing ? (
                      <input
                        type="number"
                        step="0.01"
                        className={s.cellInput}
                        value={draft.futureListPrice}
                        onChange={(e) => setDraft((prev) => ({ ...prev, futureListPrice: e.target.value }))}
                        style={{ width: 100 }}
                      />
                    ) : formatPrice(d.futureListPrice)}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    {isEditing ? (
                      <input
                        type="number"
                        step="0.01"
                        className={s.cellInput}
                        value={draft.futureMinPrice}
                        onChange={(e) => setDraft((prev) => ({ ...prev, futureMinPrice: e.target.value }))}
                        style={{ width: 100 }}
                      />
                    ) : formatPrice(d.futureMinPrice)}
                  </td>
                  <td>
                    {isEditing ? (
                      <>
                        <button type="button" className={s.actionBtn} disabled={saving} onClick={() => void saveEdit(d.id)}>
                          {saving ? 'Saving…' : 'Save'}
                        </button>
                        <button type="button" className={s.actionBtn} disabled={saving} onClick={cancelEdit} style={{ marginLeft: 4 }}>
                          Cancel
                        </button>
                      </>
                    ) : (
                      <>
                        <button type="button" className={s.actionBtn} onClick={() => beginEdit(d)}>Edit</button>
                        <button
                          type="button"
                          className={s.actionBtn}
                          onClick={() => { setDeleteTarget(d); setDeleteReason(''); }}
                          style={{ marginLeft: 4 }}
                        >
                          Delete
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>

        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', padding: 12, alignItems: 'center' }}>
          <button type="button" className={s.actionBtn} disabled={page === 0} onClick={() => setPage(0)}>« First</button>
          <button type="button" className={s.actionBtn} disabled={page === 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>‹ Prev</button>
          <span style={{ fontSize: 13, color: '#555' }}>Page {page + 1} of {Math.max(1, totalPages)}</span>
          <button type="button" className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next ›</button>
          <button type="button" className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage(totalPages - 1)}>Last »</button>
        </div>
      </div>

      {deleteTarget && (
        <div
          role="dialog"
          aria-modal="true"
          style={{
            position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000,
          }}
        >
          <div style={{ background: '#fff', borderRadius: 6, padding: 24, width: 440, maxWidth: '90vw' }}>
            <h3 style={{ marginTop: 0, fontSize: 18, color: '#112d32' }}>Delete device</h3>
            <p style={{ fontSize: 14, color: '#444' }}>
              Soft-delete <strong>{deleteTarget.sku}</strong>? The device will be flagged
              inactive and an audit row will be written to <code>pws.admin_audit_log</code>.
            </p>
            <label style={{ fontSize: 13, display: 'block', marginBottom: 4, color: '#333' }}>
              Reason (required)
            </label>
            <textarea
              value={deleteReason}
              onChange={(e) => setDeleteReason(e.target.value)}
              rows={3}
              className={s.cellInput}
              style={{ width: '100%' }}
              placeholder="e.g. Duplicate SKU; master record kept as SKU-123"
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8, marginTop: 16 }}>
              <button
                type="button"
                className={s.actionBtn}
                disabled={deleting}
                onClick={() => { setDeleteTarget(null); setDeleteReason(''); }}
              >
                Cancel
              </button>
              <button
                type="button"
                className={s.actionBtn}
                disabled={deleting || !deleteReason.trim()}
                onClick={() => void confirmDelete()}
              >
                {deleting ? 'Deleting…' : 'Confirm delete'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
