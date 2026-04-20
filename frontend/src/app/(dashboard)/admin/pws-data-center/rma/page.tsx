'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

/**
 * RMA — admin read-only overview (Phase 3, pws-data-center-port.md).
 * Reuses GET /api/v1/pws/rma. Client-side status filter + pagination;
 * the list endpoint returns the full admin-scope collection.
 */

const API_RMAS = '/api/v1/pws/rma';

interface RmaRow {
  rmaId: number;
  number: string | null;
  systemStatus: string | null;
  internalStatusText: string | null;
  statusGroupedTo: string | null;
  buyerCodeId: number | null;
  buyerName: string | null;
  companyName: string | null;
  requestSkus: number | null;
  requestQty: number | null;
  requestSalesTotal: number | null;
  approvedSkus: number | null;
  approvedQty: number | null;
  approvedSalesTotal: number | null;
  submittedDate: string | null;
  reviewCompletedOn: string | null;
  oracleNumber: string | null;
}

const PAGE_SIZE = 25;

function formatMoney(value: number | null): string {
  if (value == null) return '—';
  return `$${Number(value).toFixed(2)}`;
}

function formatDate(value: string | null): string {
  if (!value) return '—';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return value;
  return d.toLocaleDateString();
}

export default function RmaAdminPage() {
  const [rows, setRows] = useState<RmaRow[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [numberFilter, setNumberFilter] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      if (statusFilter !== 'ALL') params.set('status', statusFilter);
      const qs = params.toString();
      const res = await apiFetch(`${API_RMAS}${qs ? `?${qs}` : ''}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: RmaRow[] = await res.json();
      setRows(Array.isArray(data) ? data : []);
      setPage(0);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load RMAs');
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { void load(); }, [load]);

  const filtered = numberFilter.trim()
    ? rows.filter((r) => (r.number ?? '').toLowerCase().includes(numberFilter.trim().toLowerCase()))
    : rows;
  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const pageRows = filtered.slice(page * PAGE_SIZE, page * PAGE_SIZE + PAGE_SIZE);

  const statusOptions = ['Pending', 'In Review', 'Approved', 'Declined', 'Complete'];

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <Link href="/admin/pws-data-center" className={s.backLink}>← PWS Data Center</Link>
        <h2 className={s.pageTitle}>RMA</h2>
      </div>

      {error && <div className={`${s.banner} ${s.bannerError}`}>{error}</div>}

      <div className={s.card}>
        <div className={s.toolbar}>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className={s.cellInput}
            style={{ maxWidth: 200 }}
          >
            <option value="ALL">All statuses</option>
            {statusOptions.map((st) => (
              <option key={st} value={st}>{st}</option>
            ))}
          </select>
          <input
            type="text"
            placeholder="RMA # contains…"
            value={numberFilter}
            onChange={(e) => { setNumberFilter(e.target.value); setPage(0); }}
            className={s.cellInput}
            style={{ maxWidth: 240, marginLeft: 8 }}
          />
          <span style={{ marginLeft: 'auto', color: '#666', fontSize: 13 }}>
            {loading ? 'Loading…' : `${filtered.length} RMA${filtered.length === 1 ? '' : 's'}`}
          </span>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>RMA #</th>
              <th>Status</th>
              <th>Buyer</th>
              <th>Company</th>
              <th style={{ textAlign: 'right' }}>Req SKUs</th>
              <th style={{ textAlign: 'right' }}>Req Qty</th>
              <th style={{ textAlign: 'right' }}>Req Total</th>
              <th style={{ textAlign: 'right' }}>Appr SKUs</th>
              <th style={{ textAlign: 'right' }}>Appr Qty</th>
              <th style={{ textAlign: 'right' }}>Appr Total</th>
              <th>Submitted</th>
              <th>Reviewed</th>
              <th>Oracle #</th>
            </tr>
          </thead>
          <tbody>
            {pageRows.length === 0 && !loading && (
              <tr><td colSpan={13} style={{ textAlign: 'center', color: '#888', padding: 24 }}>No RMAs found.</td></tr>
            )}
            {pageRows.map((r) => (
              <tr key={r.rmaId}>
                <td>{r.number ?? r.rmaId}</td>
                <td>{r.internalStatusText ?? r.systemStatus ?? '—'}</td>
                <td>{r.buyerName ?? '—'}</td>
                <td>{r.companyName ?? '—'}</td>
                <td style={{ textAlign: 'right' }}>{r.requestSkus ?? 0}</td>
                <td style={{ textAlign: 'right' }}>{r.requestQty ?? 0}</td>
                <td style={{ textAlign: 'right' }}>{formatMoney(r.requestSalesTotal)}</td>
                <td style={{ textAlign: 'right' }}>{r.approvedSkus ?? 0}</td>
                <td style={{ textAlign: 'right' }}>{r.approvedQty ?? 0}</td>
                <td style={{ textAlign: 'right' }}>{formatMoney(r.approvedSalesTotal)}</td>
                <td>{formatDate(r.submittedDate)}</td>
                <td>{formatDate(r.reviewCompletedOn)}</td>
                <td>{r.oracleNumber ?? '—'}</td>
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
    </div>
  );
}
