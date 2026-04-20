'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

/**
 * PWS Offers & Orders — admin overview.
 *
 * Phase 2 (pws-data-center-port.md): moved to the paginated
 * /pws/offer-review/paged endpoint and surfaces the Mendix-parity
 * "Changed By" column populated from the JWT principal on mutation.
 */

const API_PAGED = '/api/v1/pws/offer-review/paged';
const API_SUMMARY = '/api/v1/pws/offer-review/summary';

interface OfferListItem {
  offerId: number;
  offerNumber: string | null;
  status: string | null;
  buyerName: string | null;
  buyerCode: string | null;
  salesRepName: string | null;
  totalSkus: number | null;
  totalQty: number | null;
  totalPrice: number | null;
  submissionDate: string | null;
  updatedDate: string | null;
  changedBy: string | null;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

interface OfferSummary {
  status: string;
  label: string;
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

export default function PwsOffersAdminPage() {
  const [items, setItems] = useState<OfferListItem[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [buyerCodeFilter, setBuyerCodeFilter] = useState('');
  const [statuses, setStatuses] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const res = await apiFetch(API_SUMMARY);
        if (!res.ok) return;
        const data: OfferSummary[] = await res.json();
        if (!cancelled) {
          setStatuses(data.map((d) => d.status).filter((st) => st && st !== 'Total'));
        }
      } catch {
        // Summary is best-effort; the dropdown falls back to ALL.
      }
    })();
    return () => { cancelled = true; };
  }, []);

  const load = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams({ page: String(page), size: String(PAGE_SIZE) });
      if (statusFilter !== 'ALL') params.set('status', statusFilter);
      if (buyerCodeFilter.trim()) params.set('buyerCode', buyerCodeFilter.trim());
      const res = await apiFetch(`${API_PAGED}?${params.toString()}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: PageResponse<OfferListItem> = await res.json();
      setItems(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setTotalElements(data.totalElements ?? 0);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load offers');
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter, buyerCodeFilter]);

  useEffect(() => { void load(); }, [load]);

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <Link href="/admin/pws-data-center" className={s.backLink}>← PWS Data Center</Link>
        <h2 className={s.pageTitle}>PWS Offers & Orders</h2>
      </div>

      {error && <div className={`${s.banner} ${s.bannerError}`}>{error}</div>}

      <div className={s.card}>
        <div className={s.toolbar}>
          <select
            value={statusFilter}
            onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}
            className={s.cellInput}
            style={{ maxWidth: 200 }}
          >
            <option value="ALL">All statuses</option>
            {statuses.map((st) => (
              <option key={st} value={st}>{st}</option>
            ))}
          </select>
          <input
            type="text"
            placeholder="Buyer code contains…"
            value={buyerCodeFilter}
            onChange={(e) => { setBuyerCodeFilter(e.target.value); setPage(0); }}
            className={s.cellInput}
            style={{ maxWidth: 240, marginLeft: 8 }}
          />
          <span style={{ marginLeft: 'auto', color: '#666', fontSize: 13 }}>
            {loading ? 'Loading…' : `${totalElements} offer${totalElements === 1 ? '' : 's'}`}
          </span>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>Offer #</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>SKUs</th>
              <th style={{ textAlign: 'right' }}>Qty</th>
              <th style={{ textAlign: 'right' }}>Total</th>
              <th>Offer Date</th>
              <th>Sales Rep</th>
              <th>Buyer Code</th>
              <th>Updated</th>
              <th>Changed By</th>
            </tr>
          </thead>
          <tbody>
            {items.length === 0 && !loading && (
              <tr><td colSpan={10} style={{ textAlign: 'center', color: '#888', padding: 24 }}>No offers found.</td></tr>
            )}
            {items.map((o) => {
              const changedBy = o.changedBy;
              const displayName = changedBy ? changedBy.split('@')[0] : '—';
              return (
                <tr key={o.offerId}>
                  <td>{o.offerNumber ?? o.offerId}</td>
                  <td>{o.status ?? '—'}</td>
                  <td style={{ textAlign: 'right' }}>{o.totalSkus ?? 0}</td>
                  <td style={{ textAlign: 'right' }}>{o.totalQty ?? 0}</td>
                  <td style={{ textAlign: 'right' }}>{formatMoney(o.totalPrice)}</td>
                  <td>{formatDate(o.submissionDate)}</td>
                  <td>{o.salesRepName ?? '—'}</td>
                  <td>{o.buyerCode ?? '—'}</td>
                  <td>{formatDate(o.updatedDate)}</td>
                  <td title={changedBy ?? undefined}>{displayName}</td>
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
    </div>
  );
}
