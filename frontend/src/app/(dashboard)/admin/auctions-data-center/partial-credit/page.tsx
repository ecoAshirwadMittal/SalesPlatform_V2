'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  type AdminCreditRequestRow,
  type AdminLandingResponse,
  type AdminListFilter,
  type AdminSystemStatus,
  ExportTooLargeError,
  exportXlsx,
  type LineKind,
  listAdmin,
} from '@/lib/adminPartialCreditClient';
import styles from './admin.module.css';

/**
 * Admin landing — `/admin/auctions-data-center/partial-credit`.
 *
 * Why this is a client component: the page owns interactive filter +
 * pagination state. Server-side rendering would require an
 * authenticated server fetch with cookie forwarding, which the existing
 * admin pages (reserve-bids, purchase-orders) deliberately avoid in
 * favour of client-side `apiFetch`-backed loads. We follow the same
 * pattern to keep the auth model uniform across `/admin/**`.
 *
 * Status counter chips, filter chips, table layout + empty-state copy
 * are pulled verbatim from `partial-credit-sprint3-design-notes.md` §2.
 * Per §11.Q5 the status pill colour is sourced live from the backend
 * (`row.statusColorHex`) and applied inline so the SPKB-3664 status-
 * config page can mutate it without a redeploy.
 */

const STATUS_CHIPS: ReadonlyArray<{
  label: string;
  // `null` means "no status filter" (the All chip). When the chip is
  // selected, we drop the `status` query param entirely.
  value: AdminSystemStatus | null;
  counterKey: 'pendingApproval' | 'approved' | 'declined' | 'all';
}> = [
  { label: 'Pending Approval', value: 'PENDING_APPROVAL', counterKey: 'pendingApproval' },
  { label: 'Approved', value: 'APPROVED', counterKey: 'approved' },
  { label: 'Declined', value: 'DECLINED', counterKey: 'declined' },
  { label: 'All', value: null, counterKey: 'all' },
];

const PAGE_SIZE = 25;

export default function AdminPartialCreditLandingPage() {
  const router = useRouter();
  const [data, setData] = useState<AdminLandingResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // Filter + paging state. Pending Approval is the Figma default-selected
  // chip (§2.2). Falling through to that filter mirrors the way SalesOps
  // actually uses the landing — pending requests first.
  const [statusFilter, setStatusFilter] = useState<AdminSystemStatus | null>(
    'PENDING_APPROVAL',
  );
  const [buyerCodeId, setBuyerCodeId] = useState<string>('');
  const [orderNumber, setOrderNumber] = useState<string>('');
  const [reason, setReason] = useState<LineKind | ''>('');
  const [dateFrom, setDateFrom] = useState<string>('');
  const [dateTo, setDateTo] = useState<string>('');
  const [page, setPage] = useState<number>(0);
  const [exporting, setExporting] = useState(false);
  const [exportToast, setExportToast] = useState<string | null>(null);

  const filter = useMemo<AdminListFilter>(() => {
    const f: AdminListFilter = { page, size: PAGE_SIZE };
    if (statusFilter) f.status = statusFilter;
    if (buyerCodeId.trim()) {
      const parsed = Number.parseInt(buyerCodeId, 10);
      if (!Number.isNaN(parsed)) f.buyerCodeId = parsed;
    }
    if (orderNumber.trim()) f.orderNumber = orderNumber.trim();
    if (reason) f.reason = reason;
    if (dateFrom) f.dateFrom = dateFrom;
    if (dateTo) f.dateTo = dateTo;
    return f;
  }, [statusFilter, buyerCodeId, orderNumber, reason, dateFrom, dateTo, page]);

  const handleDownload = useCallback(async () => {
    setExporting(true);
    setExportToast(null);
    try {
      // The exporter respects the same filter the table is showing —
      // don't paginate the download. Strip page/size so the service
      // sees "all rows matching this filter".
      const exportFilter: AdminListFilter = { ...filter };
      delete exportFilter.page;
      delete exportFilter.size;
      const blob = await exportXlsx(exportFilter);
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      const today = new Date().toISOString().slice(0, 10);
      a.download = `partial-credit-${today}.xlsx`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (err) {
      if (err instanceof ExportTooLargeError) {
        setExportToast(
          `Too many rows for one export — your filter matches ${err.matched} requests, the limit is ${err.limit}. Narrow your filters and try again.`,
        );
      } else {
        setExportToast(
          err instanceof Error ? err.message : 'Export failed',
        );
      }
    } finally {
      setExporting(false);
    }
  }, [filter]);

  useEffect(() => {
    setLoading(true);
    setError(null);
    let cancelled = false;
    listAdmin(filter)
      .then((res) => {
        if (cancelled) return;
        setData(res);
      })
      .catch((e: unknown) => {
        if (cancelled) return;
        setError(e instanceof Error ? e.message : 'Failed to load requests');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [filter]);

  const counterValue = useCallback(
    (key: (typeof STATUS_CHIPS)[number]['counterKey']) => {
      if (!data) return 0;
      if (key === 'all') {
        const { pendingApproval, underReview, approved, declined } = data.counters;
        // The chip total counts every reviewable request — PENDING_APPROVAL,
        // UNDER_REVIEW (visible only on detail per Q2), APPROVED, DECLINED.
        return pendingApproval + underReview + approved + declined;
      }
      return data.counters[key];
    },
    [data],
  );

  const totalPages = data ? Math.max(1, Math.ceil(data.total / PAGE_SIZE)) : 1;

  const onClearFilters = useCallback(() => {
    setBuyerCodeId('');
    setOrderNumber('');
    setReason('');
    setDateFrom('');
    setDateTo('');
    setPage(0);
  }, []);

  return (
    <div className={`pg-partial-credit ${styles.page}`}>
      <div className={styles.breadcrumb}>
        <Link href="/admin">Admin</Link> &nbsp;›&nbsp; Auctions Data Center &nbsp;›&nbsp; Partial
        Credit
      </div>

      <div className={styles.headingRow}>
        <h1 className={styles.heading}>Partial Credit Requests</h1>

        <div className={styles.headingRowEnd}>
          <div className={styles.chipRow}>
            {STATUS_CHIPS.map((chip) => {
              const active = statusFilter === chip.value;
              return (
                <button
                  type="button"
                  key={chip.label}
                  className={`${styles.chip} ${active ? styles.chipActive : ''}`}
                  onClick={() => {
                    setStatusFilter(chip.value);
                    setPage(0);
                  }}
                >
                  <span>{chip.label}:</span>
                  <span className={styles.chipCount}>{counterValue(chip.counterKey)}</span>
                </button>
              );
            })}
          </div>
          {/* Sprint 4 additive: Download xlsx export. Not in Figma — kept per
              Group 2 brief. Token swap: --color-pc-green (was teal #407874 fallback). */}
          <button
            type="button"
            className={styles.downloadButton}
            onClick={handleDownload}
            disabled={exporting}
            data-testid="export-xlsx"
          >
            {exporting ? 'Preparing…' : 'Download'}
          </button>
        </div>
      </div>
      {/* Sprint 4 additive: dismissible over-cap toast (Export413 branch). Not in Figma. */}
      {exportToast && (
        <div className={styles.exportToast} role="alert">
          {exportToast}
          <button
            type="button"
            className={styles.exportToastClose}
            onClick={() => setExportToast(null)}
            aria-label="Dismiss"
          >
            ✕
          </button>
        </div>
      )}

      {/* Sprint 4 additive: standalone filter row. Figma puts the filter
          affordances inline per-column (Ab buttons) — we keep this UX-driven
          banner per Group 2 brief. */}
      <div className={styles.filterRow}>
        <span className={styles.filterLabel}>Filters</span>
        <input
          className={styles.filterInput}
          placeholder="Buyer code id"
          value={buyerCodeId}
          onChange={(e) => {
            setBuyerCodeId(e.target.value);
            setPage(0);
          }}
        />
        <input
          className={styles.filterInput}
          placeholder="Order number"
          value={orderNumber}
          onChange={(e) => {
            setOrderNumber(e.target.value);
            setPage(0);
          }}
        />
        <select
          className={styles.filterInput}
          value={reason}
          onChange={(e) => {
            setReason(e.target.value as LineKind | '');
            setPage(0);
          }}
        >
          <option value="">All reasons</option>
          <option value="MISSING">Missing Device</option>
          <option value="WRONG">Wrong Device</option>
          <option value="ENCUMBERED">Encumbered Device</option>
        </select>
        <input
          type="date"
          className={`${styles.filterInput} ${styles.filterDate}`}
          value={dateFrom}
          onChange={(e) => {
            setDateFrom(e.target.value);
            setPage(0);
          }}
          aria-label="Date from"
        />
        <input
          type="date"
          className={`${styles.filterInput} ${styles.filterDate}`}
          value={dateTo}
          onChange={(e) => {
            setDateTo(e.target.value);
            setPage(0);
          }}
          aria-label="Date to"
        />
        <button type="button" className={styles.filterClear} onClick={onClearFilters}>
          Clear filters
        </button>
      </div>

      {error && <div className={styles.errorBanner}>{error}</div>}

      {loading && !data && <p>Loading…</p>}
      {data && (
        <div className={styles.tableCard}>
          <table className={styles.gridTable}>
            <thead>
              {/* Figma 7-column shape (design-notes §2.4):
                  Date Submitted | Buyer | Company | Order Number | Request Reason | Status | (eye) */}
              <tr>
                <th>Date Submitted</th>
                <th>Buyer</th>
                <th>Company</th>
                <th>Order Number</th>
                <th>Request Reason</th>
                <th>Status</th>
                <th className={styles.iconActionCell} aria-label="Open" />
              </tr>
            </thead>
            <tbody>
              {data.rows.length === 0 ? (
                <tr>
                  {/* Figma empty-state copy (design-notes §2.5 line 728).
                      Rendered as a single full-row <td> per Group 2 spec. */}
                  <td colSpan={7} className={styles.emptyStateCell}>
                    There are currently no Partial Credit Requests to approve
                  </td>
                </tr>
              ) : (
                data.rows.map((row) => (
                  <AdminRow key={row.id} row={row} onOpen={() => router.push(detailHref(row.id))} />
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {data && data.total > PAGE_SIZE && (
        <div className={styles.pagination}>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            type="button"
            className={styles.pageButton}
            disabled={page === 0}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
          >
            Previous
          </button>
          <button
            type="button"
            className={styles.pageButton}
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

interface AdminRowProps {
  row: AdminCreditRequestRow;
  onOpen: () => void;
}

function AdminRow({ row, onOpen }: AdminRowProps) {
  return (
    <tr>
      {/* Figma 7-column shape (design-notes §2.4 / §2.5):
          Date Submitted | Buyer | Company | Order Number | Request Reason | Status | (eye). */}
      <td>{formatDate(row.submittedDate)}</td>
      {/* V91 — buyer contact name now lives on AdminCreditRequestRow.buyerName.
          Legacy rows where no contact has been entered fall back to "—". */}
      <td>{row.buyerName ?? '—'}</td>
      <td>{row.partyName ?? '—'}</td>
      <td>{row.orderNumber}</td>
      <td>{formatReasons(row)}</td>
      <td>
        {/* R6/R8: Figma uses neutral pill chrome (transparent background) with
            colored text driven by the DTO `colorHex`. The status pill renders
            the colorHex as `color` rather than `backgroundColor`. */}
        <span
          className={styles.statusPill}
          style={{ color: row.statusColorHex }}
          title={row.systemStatus}
        >
          {row.displayStatus}
        </span>
      </td>
      <td className={styles.iconActionCell}>
        {/* Eye-icon action column — Figma layout_TM4B6H. Navigates to the
            review detail at /admin/auctions-data-center/partial-credit/{id}. */}
        <button
          type="button"
          className={styles.eyeButton}
          onClick={onOpen}
          aria-label={`Open ${row.requestNumber}`}
        >
          {/* Inline SVG — we don't ship the Font Awesome eye runtime to the
              admin bundle just for one icon. */}
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z" />
            <circle cx="12" cy="12" r="3" />
          </svg>
        </button>
      </td>
    </tr>
  );
}

function detailHref(id: number): string {
  return `/admin/auctions-data-center/partial-credit/${id}`;
}

function formatReasons(row: AdminCreditRequestRow): string {
  const reasons: string[] = [];
  if (row.hasMissingDevice) reasons.push('Missing Device');
  if (row.hasWrongDevice) reasons.push('Wrong Device');
  if (row.hasEncumberedDevice) reasons.push('Encumbered Device');
  return reasons.length === 0 ? '—' : reasons.join(', ');
}

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  const d = new Date(iso);
  return Number.isNaN(d.getTime()) ? '—' : d.toLocaleDateString();
}
