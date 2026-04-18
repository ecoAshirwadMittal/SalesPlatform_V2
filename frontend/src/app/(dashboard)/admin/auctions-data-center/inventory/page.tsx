'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import styles from './inventory.module.css';
import {
  fetchWeeks,
  fetchInventoryPage,
  fetchInventoryTotals,
  type WeekOption,
  type InventoryPageResponse,
  type InventoryTotals,
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
                <td />
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
