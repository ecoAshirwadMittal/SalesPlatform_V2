'use client';

import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useRef, useState } from 'react';
import styles from './inventory.module.css';
import {
  fetchWeeks,
  fetchInventoryPage,
  fetchInventoryTotals,
  fetchSyncStatus,
  triggerWeekSync,
  updateInventoryRow,
  type WeekOption,
  type InventoryPageResponse,
  type InventoryTotals,
  type InventoryRow,
} from '@/lib/aggregatedInventory';
import {
  type ColumnFilter,
  type FilterOp,
  FilterCell,
} from '@/components/datagrid';
import { CreateAuctionModal } from './CreateAuctionModal';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;
// Snowflake sync poll cadence: 30 ticks × 3s = 90s ceiling, matching Phase 8
// plan. Anything longer is a silent failure we surface only via the log row.
const SYNC_POLL_INTERVAL_MS = 3_000;
const SYNC_POLL_MAX_TICKS = 30;
const SYNC_PENDING_STATUSES = new Set(['STARTED']);

/** Inventory filter state. The backend supports a 2-op model
 *  (contains / equals) for text columns and exact-match-only for the
 *  numeric productId, so the shared FilterCell is constrained via
 *  availableOps below. The page-state shape uses the shared
 *  {@link ColumnFilter} record so the migration to a richer backend
 *  (parity with reserve-bids) is a backend-only change later. */
type InventoryFilterKey =
  | 'productId' | 'grades' | 'brand' | 'model' | 'modelName' | 'carrier';

type InventoryFilterState = Record<InventoryFilterKey, ColumnFilter>;

const TEXT_OPS_INVENTORY: FilterOp[] = ['contains', 'eq'];
const NUMERIC_OPS_INVENTORY: FilterOp[] = ['eq'];

function emptyInventoryFilters(): InventoryFilterState {
  return {
    productId: { op: 'eq',       value: '' },
    grades:    { op: 'contains', value: '' },
    brand:     { op: 'contains', value: '' },
    model:     { op: 'contains', value: '' },
    modelName: { op: 'contains', value: '' },
    carrier:   { op: 'contains', value: '' },
  };
}

/** Translate a {@link ColumnFilter} op to the legacy
 *  {@code gradesMode}/{@code brandMode}/etc. wire token the inventory
 *  backend currently expects. The shared FilterCell speaks
 *  contains/eq; the legacy wire speaks contains/equals. */
function modeForOp(op: FilterOp): 'contains' | 'equals' {
  return op === 'eq' ? 'equals' : 'contains';
}

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

export default function AggregatedInventoryPage() {
  const router = useRouter();
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekId, setWeekId] = useState<number | null>(null);
  const [page, setPage] = useState(0);
  const [data, setData] = useState<InventoryPageResponse | null>(null);
  const [totals, setTotals] = useState<InventoryTotals | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [input, setInput] = useState<InventoryFilterState>(() => emptyInventoryFilters());
  const [applied, setApplied] = useState<InventoryFilterState>(() => emptyInventoryFilters());
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const [editRow, setEditRow] = useState<InventoryRow | null>(null);
  const [syncPending, setSyncPending] = useState(false);
  const [showCreateAuction, setShowCreateAuction] = useState(false);

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
      .catch(() => {
        if (ignore) return;
        setError('Failed to load weeks');
      });
    return () => {
      ignore = true;
    };
  }, []);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setApplied(input);
      setPage(0);
    }, FILTER_DELAY);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [input]);

  const refresh = useCallback(async () => {
    if (!weekId) return;
    const [grid, kpi] = await Promise.all([
      fetchInventoryPage({
        weekId,
        page,
        pageSize: PAGE_SIZE,
        productId: applied.productId.value || undefined,
        grades:    applied.grades.value    || undefined,
        brand:     applied.brand.value     || undefined,
        model:     applied.model.value     || undefined,
        modelName: applied.modelName.value || undefined,
        carrier:   applied.carrier.value   || undefined,
        gradesMode:    modeForOp(applied.grades.op),
        brandMode:     modeForOp(applied.brand.op),
        modelMode:     modeForOp(applied.model.op),
        modelNameMode: modeForOp(applied.modelName.op),
        carrierMode:   modeForOp(applied.carrier.op),
      }),
      fetchInventoryTotals(weekId),
    ]);
    setData(grid);
    setTotals(kpi);
  }, [weekId, page, applied]);

  useEffect(() => {
    let ignore = false;
    refresh().catch(() => {
      if (ignore) return;
      setError('Failed to load inventory');
    });
    return () => {
      ignore = true;
    };
  }, [refresh]);

  // Fire-and-forget Snowflake sync when the selected week changes. The backend
  // gates Administrator/SalesOps via @PreAuthorize and returns 403 for Bidders
  // — intentionally swallow the rejection so the read-only view still renders.
  useEffect(() => {
    if (!weekId) return;
    triggerWeekSync(weekId).catch(() => {
      /* fire-and-forget; backend enforces role + feature flag */
    });
  }, [weekId]);

  // Poll sync status for up to 90s while the latest log row is STARTED.
  // On completion, do a single final grid refetch so KPIs and rows reflect
  // the freshly upserted numbers.
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
          refresh().catch(() => {
            if (!cancelled) setError('Failed to load inventory');
          });
          return;
        }
        if (ticks >= SYNC_POLL_MAX_TICKS) {
          setSyncPending(false);
          return;
        }
        scheduleNext();
      } catch {
        // Transient network error — keep polling until ticks run out.
        if (cancelled) return;
        if (ticks >= SYNC_POLL_MAX_TICKS) {
          setSyncPending(false);
          return;
        }
        scheduleNext();
      }
    };

    void tick();

    return () => {
      cancelled = true;
      if (handle) clearTimeout(handle);
      setSyncPending(false);
    };
  }, [weekId, refresh]);

  const updateFilter = (key: InventoryFilterKey, next: ColumnFilter) =>
    setInput(prev => ({ ...prev, [key]: next }));

  const total = data?.totalElements ?? 0;
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

  const selectedWeek = weeks.find(w => w.id === weekId) ?? null;
  const canCreateAuction = Boolean(
    totals?.hasInventory && totals?.isCurrentWeek && !totals?.hasAuction,
  );

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Inventory</h2>
        <label htmlFor="week-select" className={styles.weekLabel}>Week:</label>
        <select
          id="week-select"
          className={styles.weekSelect}
          value={weekId ?? ''}
          onChange={e => {
            setWeekId(Number(e.target.value));
            setPage(0);
          }}
        >
          {weeks.map(w => (
            <option key={w.id} value={w.id}>
              {w.weekDisplay}
            </option>
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
            refresh().catch(() => setError('Failed to load inventory'));
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

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <th>
                <div>Product ID</div>
                <FilterCell label="Product ID" kind="numeric"
                  filter={input.productId}
                  onChange={(next) => updateFilter('productId', next)}
                  availableOps={NUMERIC_OPS_INVENTORY}
                  inputType="text"
                  inputMode="numeric" />
              </th>
              <th>
                <div>Grades</div>
                <FilterCell label="Grades" kind="text"
                  filter={input.grades}
                  onChange={(next) => updateFilter('grades', next)}
                  availableOps={TEXT_OPS_INVENTORY} />
              </th>
              <th>
                <div>Brand</div>
                <FilterCell label="Brand" kind="text"
                  filter={input.brand}
                  onChange={(next) => updateFilter('brand', next)}
                  availableOps={TEXT_OPS_INVENTORY} />
              </th>
              <th>
                <div>Model</div>
                <FilterCell label="Model" kind="text"
                  filter={input.model}
                  onChange={(next) => updateFilter('model', next)}
                  availableOps={TEXT_OPS_INVENTORY} />
              </th>
              <th>
                <div>Model Name</div>
                <FilterCell label="Model Name" kind="text"
                  filter={input.modelName}
                  onChange={(next) => updateFilter('modelName', next)}
                  availableOps={TEXT_OPS_INVENTORY} />
              </th>
              <th>
                <div>Carrier</div>
                <FilterCell label="Carrier" kind="text"
                  filter={input.carrier}
                  onChange={(next) => updateFilter('carrier', next)}
                  availableOps={TEXT_OPS_INVENTORY} />
              </th>
              <th>DW Qty</th>
              <th>DW Target Price</th>
              <th>Total Qty</th>
              <th>Target Price</th>
              <th aria-label="actions" />
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map(r => (
              <tr key={r.id}>
                <td>{r.ecoid2}</td>
                <td>{r.mergedGrade}</td>
                <td>{r.brand}</td>
                <td>{r.model}</td>
                <td>{r.name}</td>
                <td>{r.carrier}</td>
                <td>{formatInt(r.dwTotalQuantity)}</td>
                <td>{formatUsd(Number(r.dwAvgTargetPrice))}</td>
                <td>{formatInt(r.totalQuantity)}</td>
                <td>{formatUsd(Number(r.avgTargetPrice))}</td>
                <td>
                  <button
                    type="button"
                    className={styles.editLink}
                    aria-label={`Edit ${r.ecoid2} ${r.mergedGrade ?? ''}`.trim()}
                    onClick={() => setEditRow(r)}
                  >
                    Edit
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className={styles.pagination}>
          <button type="button" onClick={() => setPage(0)} disabled={page === 0}>«</button>
          <button type="button" onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>‹</button>
          <span>Currently showing {startIdx} to {endIdx} of {total}</span>
          <button
            type="button"
            onClick={() => setPage(p => p + 1)}
            disabled={data ? page + 1 >= data.totalPages : true}
          >
            ›
          </button>
          <button
            type="button"
            onClick={() => data && setPage(data.totalPages - 1)}
            disabled={data ? page + 1 >= data.totalPages : true}
          >
            »
          </button>
        </div>

        <div className={styles.downloadBar}>
          <button
            type="button"
            className={styles.button}
            disabled={!weekId}
            onClick={() => {
              if (!weekId) return;
              const qs = new URLSearchParams({ weekId: String(weekId) });
              if (applied.productId.value) qs.set('productId', applied.productId.value);
              if (applied.grades.value)    qs.set('grades', applied.grades.value);
              if (applied.brand.value)     qs.set('brand', applied.brand.value);
              if (applied.model.value)     qs.set('model', applied.model.value);
              if (applied.modelName.value) qs.set('modelName', applied.modelName.value);
              if (applied.carrier.value)   qs.set('carrier', applied.carrier.value);
              window.location.href = `/api/v1/admin/inventory/export?${qs}`;
            }}
          >
            Download
          </button>
        </div>
      </div>
      {editRow && (
        <EditModal
          row={editRow}
          onClose={() => setEditRow(null)}
          onSaved={() => {
            setEditRow(null);
            setError(null);
            refresh().catch(() => setError('Failed to load inventory'));
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
