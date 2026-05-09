'use client';

import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import styles from './inventory.module.css';
import {
  fetchWeeks,
  fetchInventoryPage,
  fetchInventoryTotals,
  fetchSyncStatus,
  triggerWeekSync,
  updateInventoryRow,
  type WeekOption,
  type InventoryTotals,
  type InventoryRow,
} from '@/lib/aggregatedInventory';
import {
  type ColumnDef,
  type FetcherArgs,
  DataGrid,
} from '@/components/datagrid';
import { CreateAuctionModal } from './CreateAuctionModal';

const PAGE_SIZE = 20;
// Snowflake sync poll cadence: 30 ticks × 3s = 90s ceiling, matching Phase 8
// plan. Anything longer is a silent failure we surface only via the log row.
const SYNC_POLL_INTERVAL_MS = 3_000;
const SYNC_POLL_MAX_TICKS = 30;
const SYNC_PENDING_STATUSES = new Set(['STARTED']);

const usdFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 2,
});
// QA renders six-figure payout totals as integer dollars (e.g. "$1,980,410").
// Cents on six-figure aggregates is just visual noise — kept for per-unit
// prices like Average Target Price + per-row Target Price columns where the
// cents actually carry signal.
const usdIntegerFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 0,
});
const intFormatter = new Intl.NumberFormat('en-US');

const formatUsd = (n: number) => usdFormatter.format(n);
const formatUsdInteger = (n: number) => usdIntegerFormatter.format(n);
const formatInt = (n: number) => intFormatter.format(n);

/** Inventory backend now speaks the new wire format directly (see
 *  InventoryFilterRequest.parse on the backend). The frontend forwards
 *  each column's filter value as-is — no client-side translation. */

export default function AggregatedInventoryPage() {
  const router = useRouter();
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekId, setWeekId] = useState<number | null>(null);
  const [totals, setTotals] = useState<InventoryTotals | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [editRow, setEditRow] = useState<InventoryRow | null>(null);
  const [syncPending, setSyncPending] = useState(false);
  const [showCreateAuction, setShowCreateAuction] = useState(false);
  const [refreshNonce, setRefreshNonce] = useState(0);

  // Mirror of the DataGrid's applied filter wire-format. Cached so the
  // Download button URL can include the same filters the user sees.
  const [appliedFilters, setAppliedFilters] = useState<Record<string, string>>({});

  // Bootstrap weeks
  useEffect(() => {
    let ignore = false;
    fetchWeeks()
      .then(list => {
        if (ignore) return;
        setWeeks(list);
        const now = Date.now();
        const current = list.find(w => new Date(w.weekEndDateTime).getTime() > now);
        setWeekId((current ?? list[0])?.id ?? null);
      })
      .catch(() => { if (!ignore) setError('Failed to load weeks'); });
    return () => { ignore = true; };
  }, []);

  // Fetch totals (KPIs) for the selected week. Independent of the grid's
  // filter state — totals are week-scoped only. Re-runs on refreshNonce
  // so the post-sync auto-refresh + the manual Refresh button both pick
  // up new numbers.
  useEffect(() => {
    if (!weekId) return;
    let ignore = false;
    fetchInventoryTotals(weekId)
      .then(t => { if (!ignore) setTotals(t); })
      .catch(() => { if (!ignore) setError('Failed to load inventory totals'); });
    return () => { ignore = true; };
  }, [weekId, refreshNonce]);

  // Fire-and-forget Snowflake sync trigger when the selected week changes.
  // Backend gates Administrator/SalesOps via @PreAuthorize and 403s for
  // Bidders — swallow so the read-only view still renders.
  useEffect(() => {
    if (!weekId) return;
    triggerWeekSync(weekId).catch(() => { /* fire-and-forget */ });
  }, [weekId]);

  // Poll sync status for up to 90s while the latest log row is STARTED.
  // On completion, bump refreshNonce so the grid + totals re-fetch.
  useEffect(() => {
    if (!weekId) return;
    let cancelled = false;
    let ticks = 0;
    let handle: ReturnType<typeof setTimeout> | null = null;

    const scheduleNext = () => {
      if (cancelled) return;
      handle = setTimeout(tick, SYNC_POLL_INTERVAL_MS);
    };

    const tick = async () => {
      if (cancelled) return;
      ticks += 1;
      try {
        const status = await fetchSyncStatus(weekId);
        if (cancelled) return;
        const isPending = SYNC_PENDING_STATUSES.has(status.status);
        setSyncPending(isPending);
        if (!isPending) {
          setRefreshNonce(n => n + 1);
          return;
        }
        if (ticks >= SYNC_POLL_MAX_TICKS) { setSyncPending(false); return; }
        scheduleNext();
      } catch {
        if (cancelled) return;
        if (ticks >= SYNC_POLL_MAX_TICKS) { setSyncPending(false); return; }
        scheduleNext();
      }
    };

    void tick();

    return () => {
      cancelled = true;
      if (handle) clearTimeout(handle);
      setSyncPending(false);
    };
  }, [weekId]);

  // Fetcher closes over weekId + refreshNonce so the grid re-fetches when
  // either changes. Filter values flow through to the backend as-is —
  // InventoryFilterRequest.parse on the backend handles both the new
  // "op,value" wire format and the legacy bare-value shape.
  const fetcher = useCallback(
    async ({ filters, page, size, signal }: FetcherArgs) => {
      void signal;
      if (!weekId) return { rows: [], total: 0 };
      const res = await fetchInventoryPage({
        weekId,
        page,
        pageSize: size,
        productId: filters.productId,
        grades:    filters.grades,
        brand:     filters.brand,
        model:     filters.model,
        modelName: filters.modelName,
        carrier:   filters.carrier,
      });
      return { rows: res.content, total: res.totalElements };
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [weekId, refreshNonce],
  );

  const columns = useMemo<ColumnDef<InventoryRow>[]>(() => [
    // ecoid2 is VARCHAR(64) on the backend; backend now exposes the full
    // 11-op text comparator menu so the dropdown matches every other
    // admin grid. Numeric ops on this column do lexicographic
    // comparison (10 < 9), but the column is rendered + thought of as
    // numeric — keep the kind=text so case-insensitive ops apply, and
    // surface only the safe ops to keep the menu honest.
    {
      key: 'productId', label: 'Product ID', accessor: r => r.ecoid2,
      filter: {
        kind: 'text', filterKey: 'productId',
        availableOps: ['eq', 'neq', 'contains', 'startsWith', 'endsWith', 'empty', 'notEmpty'],
        placeholder: 'Product ID',
      },
    },
    {
      key: 'grades', label: 'Grades', accessor: r => r.mergedGrade ?? '—',
      filter: { kind: 'text', filterKey: 'grades', placeholder: 'Grades' },
    },
    {
      key: 'brand', label: 'Brand', accessor: r => r.brand ?? '—',
      filter: { kind: 'text', filterKey: 'brand', placeholder: 'Brand' },
    },
    {
      key: 'model', label: 'Model', accessor: r => r.model ?? '—',
      filter: { kind: 'text', filterKey: 'model', placeholder: 'Model' },
    },
    {
      key: 'modelName', label: 'Model Name', accessor: r => r.name ?? '—',
      filter: { kind: 'text', filterKey: 'modelName', placeholder: 'Model Name' },
    },
    {
      key: 'carrier', label: 'Carrier', accessor: r => r.carrier ?? '—',
      filter: { kind: 'text', filterKey: 'carrier', placeholder: 'Carrier' },
    },
    { key: 'dwQty',    label: 'DW Qty',          numeric: true, accessor: r => formatInt(r.dwTotalQuantity) },
    { key: 'dwTp',     label: 'DW Target Price', numeric: true, accessor: r => formatUsd(Number(r.dwAvgTargetPrice)) },
    { key: 'totalQty', label: 'Total Qty',       numeric: true, accessor: r => formatInt(r.totalQuantity) },
    { key: 'tp',       label: 'Target Price',    numeric: true, accessor: r => formatUsd(Number(r.avgTargetPrice)) },
  ], []);

  const rowActions = useCallback(
    (r: InventoryRow) => (
      <button
        type="button"
        className={styles.editLink}
        aria-label={`Edit ${r.ecoid2} ${r.mergedGrade ?? ''}`.trim()}
        onClick={() => setEditRow(r)}
      >
        Edit
      </button>
    ),
    [],
  );

  const selectedWeek = weeks.find(w => w.id === weekId) ?? null;
  const canCreateAuction = Boolean(
    totals?.hasInventory && totals?.isCurrentWeek && !totals?.hasAuction,
  );

  const downloadHref = useMemo(() => {
    if (!weekId) return null;
    const qs = new URLSearchParams({ weekId: String(weekId) });
    // Pass each filter wire-format value straight through; the export
    // endpoint understands both the new "op,value" shape and the
    // legacy bare-value shape via the same InventoryFilterRequest
    // parser used by the list endpoint.
    for (const key of ['productId', 'grades', 'brand', 'model', 'modelName', 'carrier'] as const) {
      const wire = appliedFilters[key];
      if (wire) qs.set(key, wire);
    }
    return `/api/v1/admin/inventory/export?${qs}`;
  }, [weekId, appliedFilters]);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Inventory</h2>
        <label htmlFor="week-select" className={styles.weekLabel}>Week:</label>
        <select
          id="week-select"
          className={styles.weekSelect}
          value={weekId ?? ''}
          onChange={e => setWeekId(Number(e.target.value))}
        >
          {weeks.map(w => (
            <option key={w.id} value={w.id}>{w.weekDisplay}</option>
          ))}
        </select>
        {canCreateAuction && (
          <button
            className={styles.button}
            type="button"
            onClick={() => setShowCreateAuction(true)}
          >
            Create Auction
            <GavelIcon />
          </button>
        )}
        <button
          className={styles.buttonGhost}
          type="button"
          onClick={() => {
            setError(null);
            setRefreshNonce(n => n + 1);
          }}
        >
          Refresh
        </button>
        <span className={styles.lastSynced}>
          Last synced: {totals?.lastSyncedAt ? new Date(totals.lastSyncedAt).toLocaleString() : '—'}
        </span>
        {error && <span className={styles.error}>{error}</span>}
      </header>

      <section className={styles.kpiStrip} aria-label="Inventory totals">
        <Kpi label="Total Quantity" value={formatInt(totals?.totalQuantity ?? 0)} />
        <Kpi label="Total Payout" value={formatUsdInteger(Number(totals?.totalPayout ?? 0))} />
        <Kpi label="Average Target Price" value={formatUsd(Number(totals?.averageTargetPrice ?? 0))} />
        <Kpi label="DW Total Quantity" value={formatInt(totals?.dwTotalQuantity ?? 0)} />
        <Kpi label="DW Total Payout" value={formatUsdInteger(Number(totals?.dwTotalPayout ?? 0))} />
        <Kpi label="DW Average Target Price" value={formatUsd(Number(totals?.dwAverageTargetPrice ?? 0))} />
      </section>

      {syncPending && (
        <div className={styles.syncBanner} role="status" aria-live="polite">
          Syncing from Snowflake…
        </div>
      )}

      <DataGrid<InventoryRow>
        columns={columns}
        fetcher={fetcher}
        rowKey={r => r.id}
        rowActions={rowActions}
        rowActionsLabel="Actions"
        pageSize={PAGE_SIZE}
        emptyMessage="No inventory rows match the current filters."
        topBarSlot={
          <a
            href={downloadHref ?? '#'}
            aria-disabled={!downloadHref}
            tabIndex={downloadHref ? 0 : -1}
            onClick={e => { if (!downloadHref) e.preventDefault(); }}
          >
            <button
              type="button"
              className={styles.button}
              disabled={!weekId}
            >
              Download
            </button>
          </a>
        }
        onAppliedFiltersChange={setAppliedFilters}
      />

      {editRow && (
        <EditModal
          row={editRow}
          onClose={() => setEditRow(null)}
          onSaved={() => {
            setEditRow(null);
            setError(null);
            setRefreshNonce(n => n + 1);
          }}
        />
      )}
      {showCreateAuction && weekId && selectedWeek && (
        <CreateAuctionModal
          weekId={weekId}
          weekDisplay={selectedWeek.weekDisplay}
          onClose={() => setShowCreateAuction(false)}
          onCreated={(newAuctionId) => {
            // Mendix parity: the admin lands on the Auction Scheduling page
            // immediately after creating the auction. See
            // docs/tasks/auction-scheduling-plan.md section 1.1 (user flow).
            setShowCreateAuction(false);
            setError(null);
            router.push(`/admin/auctions-data-center/auctions/${newAuctionId}/schedule`);
          }}
        />
      )}
    </div>
  );
}

function Kpi({ label, value }: { label: string; value: string }) {
  return (
    <div className={styles.kpiCard}>
      <p className={styles.kpiLabel}>{label}</p>
      <p className={styles.kpiValue}>{value}</p>
    </div>
  );
}

/**
 * L19 — inline gavel/hammer icon on the Create Auction button. Mirrors
 * QA's Mendix admin button which renders an auction-hammer SVG to the right
 * of the label. Inlined (not lucide-react) to avoid adding a dependency
 * for a single glyph; uses `currentColor` so it inherits the button text
 * color across primary + ghost variants.
 */
function GavelIcon(): React.ReactElement {
  return (
    <svg
      aria-hidden="true"
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      style={{ marginLeft: '0.4rem', verticalAlign: 'middle', flexShrink: 0 }}
    >
      <path d="m14.5 12.5-8 8a2.119 2.119 0 1 1-3-3l8-8" />
      <path d="m16 16 6-6" />
      <path d="m8 8 6-6" />
      <path d="m9 7 8 8" />
      <path d="m21 11-8-8" />
    </svg>
  );
}

function EditModal({
  row,
  onClose,
  onSaved,
}: {
  row: InventoryRow;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [mergedGrade, setMergedGrade] = useState(row.mergedGrade ?? '');
  const [datawipe, setDatawipe] = useState(row.datawipe);
  const [totalQuantity, setTotalQuantity] = useState(row.totalQuantity);
  const [dwTotalQuantity, setDwTotalQuantity] = useState(row.dwTotalQuantity);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const firstFieldRef = useRef<HTMLInputElement | null>(null);
  const previouslyFocusedRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    previouslyFocusedRef.current = document.activeElement as HTMLElement | null;
    firstFieldRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('keydown', onKey);
      previouslyFocusedRef.current?.focus?.();
    };
  }, [onClose]);

  const onSave = async () => {
    setSaving(true);
    setSaveError(null);
    try {
      await updateInventoryRow(row.id, { mergedGrade, datawipe, totalQuantity, dwTotalQuantity });
      onSaved();
    } catch (e) {
      setSaveError(e instanceof Error ? e.message : 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div
      className={styles.modalBackdrop}
      role="dialog"
      aria-modal="true"
      aria-labelledby="edit-modal-title"
      onMouseDown={e => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className={styles.modal}>
        <h3 id="edit-modal-title" className={styles.modalTitle}>Edit Aggregated Inventory</h3>
        <fieldset className={styles.field}>
          <legend>Merged Grade</legend>
          <div className={styles.radioGroup}>
            {['A_YYY', 'C_YNY/G_YNN', 'E_YYN'].map((g, idx) => (
              <label key={g}>
                <input
                  ref={idx === 0 ? firstFieldRef : undefined}
                  type="radio"
                  name="grade"
                  value={g}
                  checked={mergedGrade === g}
                  onChange={() => setMergedGrade(g)}
                />
                {' '}{g}
              </label>
            ))}
          </div>
        </fieldset>
        <fieldset className={styles.field}>
          <legend>Data Wipe</legend>
          <div className={styles.radioGroup}>
            <label>
              <input type="radio" name="dw" checked={datawipe} onChange={() => setDatawipe(true)} /> Yes
            </label>
            <label>
              <input type="radio" name="dw" checked={!datawipe} onChange={() => setDatawipe(false)} /> No
            </label>
          </div>
        </fieldset>
        <div className={styles.field}>
          <label htmlFor="edit-total-qty">Total Quantity</label>
          <input
            id="edit-total-qty"
            type="number"
            value={totalQuantity}
            onChange={e => setTotalQuantity(Number(e.target.value))}
          />
        </div>
        <div className={styles.field}>
          <label htmlFor="edit-dw-total-qty">DW Total Quantity</label>
          <input
            id="edit-dw-total-qty"
            type="number"
            value={dwTotalQuantity}
            onChange={e => setDwTotalQuantity(Number(e.target.value))}
          />
        </div>
        {saveError && <span className={styles.error}>{saveError}</span>}
        <div className={styles.modalActions}>
          <button type="button" className={styles.buttonGhost} onClick={onClose} disabled={saving}>Cancel</button>
          <button type="button" className={styles.button} onClick={onSave} disabled={saving}>
            {saving ? 'Saving…' : 'Save'}
          </button>
        </div>
      </div>
    </div>
  );
}
