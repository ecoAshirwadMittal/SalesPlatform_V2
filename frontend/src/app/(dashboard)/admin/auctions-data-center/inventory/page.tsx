'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import styles from './inventory.module.css';
import {
  fetchWeeks,
  fetchInventoryPage,
  fetchInventoryTotals,
  updateInventoryRow,
  type WeekOption,
  type InventoryPageResponse,
  type InventoryTotals,
  type InventoryRow,
} from '@/lib/aggregatedInventory';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;

interface Filters {
  productId: string;
  grades: string;
  brand: string;
  model: string;
  modelName: string;
  carrier: string;
}

const EMPTY_FILTERS: Filters = {
  productId: '',
  grades: '',
  brand: '',
  model: '',
  modelName: '',
  carrier: '',
};

const usdFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 2,
});
const intFormatter = new Intl.NumberFormat('en-US');

const formatUsd = (n: number) => usdFormatter.format(n);
const formatInt = (n: number) => intFormatter.format(n);

export default function AggregatedInventoryPage() {
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekId, setWeekId] = useState<number | null>(null);
  const [page, setPage] = useState(0);
  const [data, setData] = useState<InventoryPageResponse | null>(null);
  const [totals, setTotals] = useState<InventoryTotals | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [input, setInput] = useState<Filters>(EMPTY_FILTERS);
  const [applied, setApplied] = useState<Filters>(EMPTY_FILTERS);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const [editRow, setEditRow] = useState<InventoryRow | null>(null);

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
        productId: applied.productId || undefined,
        grades: applied.grades || undefined,
        brand: applied.brand || undefined,
        model: applied.model || undefined,
        modelName: applied.modelName || undefined,
        carrier: applied.carrier || undefined,
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

  const updateFilter = <K extends keyof Filters>(k: K, v: string) =>
    setInput(prev => ({ ...prev, [k]: v }));

  const total = data?.totalElements ?? 0;
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

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
        <button className={styles.button} type="button">
          Create Auction
        </button>
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
        <Kpi label="Total Payout" value={formatUsd(Number(totals?.totalPayout ?? 0))} />
        <Kpi label="Average Target Price" value={formatUsd(Number(totals?.averageTargetPrice ?? 0))} />
        <Kpi label="DW Total Quantity" value={formatInt(totals?.dwTotalQuantity ?? 0)} />
        <Kpi label="DW Total Payout" value={formatUsd(Number(totals?.dwTotalPayout ?? 0))} />
        <Kpi label="DW Average Target Price" value={formatUsd(Number(totals?.dwAverageTargetPrice ?? 0))} />
      </section>

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <HeaderCell label="Product ID" filter={input.productId} onChange={v => updateFilter('productId', v)} kind="equal" />
              <HeaderCell label="Grades" filter={input.grades} onChange={v => updateFilter('grades', v)} />
              <HeaderCell label="Brand" filter={input.brand} onChange={v => updateFilter('brand', v)} />
              <HeaderCell label="Model" filter={input.model} onChange={v => updateFilter('model', v)} />
              <HeaderCell label="Model Name" filter={input.modelName} onChange={v => updateFilter('modelName', v)} />
              <HeaderCell label="Carrier" filter={input.carrier} onChange={v => updateFilter('carrier', v)} />
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

interface HeaderCellProps {
  label: string;
  filter: string;
  onChange: (v: string) => void;
  kind?: 'contains' | 'equal';
}

function HeaderCell({ label, filter, onChange, kind = 'contains' }: HeaderCellProps) {
  return (
    <th>
      <div>{label}</div>
      <input
        className={styles.filterInput}
        value={filter}
        placeholder={kind === 'equal' ? '=' : 'Ab'}
        onChange={e => onChange(e.target.value)}
        inputMode={kind === 'equal' ? 'numeric' : 'text'}
      />
    </th>
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
