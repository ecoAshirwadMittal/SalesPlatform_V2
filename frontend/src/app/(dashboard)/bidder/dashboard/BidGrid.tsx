'use client';
import { useMemo, useState } from 'react';
import type { BidDataRow } from '@/lib/bidder';
import { BidGridRow } from './BidGridRow';
import styles from './bidGrid.module.css';

// ---------------------------------------------------------------------------
// Types
// ---------------------------------------------------------------------------

type SortColumn =
  | 'ecoid'
  | 'mergedGrade'
  | 'targetPrice'
  | 'maximumQuantity'
  | 'bidQuantity'
  | 'bidAmount'
  | 'payout';

type SortDirection = 'asc' | 'desc';

interface SortState {
  column: SortColumn | null;
  direction: SortDirection | null;
}

interface FilterState {
  ecoid: string;
  mergedGrade: string;
  targetPrice: string;
  maximumQuantity: string;
  bidQuantity: string;
  bidAmount: string;
  payout: string;
}

export interface BidGridProps {
  rows: BidDataRow[];
  onRowSaved: (row: BidDataRow) => void;
  onRowError?: (err: unknown) => void;
  /** Disables all bid cell inputs — set when the round has closed. */
  disabled?: boolean;
  /** Total row count before client-side filtering, for the footer. */
  totalRowCount?: number;
}

// ---------------------------------------------------------------------------
// Helpers (module-level — stable references, no re-render issues)
// ---------------------------------------------------------------------------

const EMPTY_FILTERS: FilterState = {
  ecoid: '',
  mergedGrade: '',
  targetPrice: '',
  maximumQuantity: '',
  bidQuantity: '',
  bidAmount: '',
  payout: '',
};

/** Advance sort state: asc → desc → clear. */
function nextSort(current: SortState, column: SortColumn): SortState {
  if (current.column !== column) return { column, direction: 'asc' };
  if (current.direction === 'asc') return { column, direction: 'desc' };
  return { column: null, direction: null };
}

/** Arrow glyph for the column header button. */
function sortGlyph(sort: SortState, column: SortColumn): string {
  if (sort.column !== column) return '↕';
  return sort.direction === 'asc' ? '↑' : '↓';
}

/** aria-sort value for <th>. */
function ariaSort(sort: SortState, column: SortColumn): 'ascending' | 'descending' | 'none' {
  if (sort.column !== column) return 'none';
  return sort.direction === 'asc' ? 'ascending' : 'descending';
}

/** Numeric value helper — null / undefined treated as -Infinity for sorting. */
function numVal(v: number | null | undefined): number {
  return v ?? -Infinity;
}

function applyFilters(rows: BidDataRow[], filters: FilterState): BidDataRow[] {
  return rows.filter((row) => {
    if (filters.ecoid && !row.ecoid.toLowerCase().includes(filters.ecoid.toLowerCase())) return false;
    if (filters.mergedGrade && !row.mergedGrade.toLowerCase().includes(filters.mergedGrade.toLowerCase())) return false;

    if (filters.targetPrice !== '') {
      const f = parseFloat(filters.targetPrice);
      if (!isNaN(f) && row.targetPrice !== f) return false;
    }
    if (filters.maximumQuantity !== '') {
      const f = parseFloat(filters.maximumQuantity);
      if (!isNaN(f) && numVal(row.maximumQuantity) !== f) return false;
    }
    if (filters.bidQuantity !== '') {
      const f = parseFloat(filters.bidQuantity);
      if (!isNaN(f) && numVal(row.bidQuantity) !== f) return false;
    }
    if (filters.bidAmount !== '') {
      const f = parseFloat(filters.bidAmount);
      if (!isNaN(f) && row.bidAmount !== f) return false;
    }
    if (filters.payout !== '') {
      const f = parseFloat(filters.payout);
      if (!isNaN(f) && numVal(row.payout) !== f) return false;
    }

    return true;
  });
}

function applySort(rows: BidDataRow[], sort: SortState): BidDataRow[] {
  if (!sort.column || !sort.direction) return rows;
  const col = sort.column;
  const dir = sort.direction === 'asc' ? 1 : -1;

  return [...rows].sort((a, b) => {
    let cmp = 0;
    if (col === 'ecoid') {
      cmp = a.ecoid.localeCompare(b.ecoid);
    } else if (col === 'mergedGrade') {
      cmp = a.mergedGrade.localeCompare(b.mergedGrade);
    } else if (col === 'targetPrice') {
      cmp = a.targetPrice - b.targetPrice;
    } else if (col === 'maximumQuantity') {
      cmp = numVal(a.maximumQuantity) - numVal(b.maximumQuantity);
    } else if (col === 'bidQuantity') {
      cmp = numVal(a.bidQuantity) - numVal(b.bidQuantity);
    } else if (col === 'bidAmount') {
      cmp = a.bidAmount - b.bidAmount;
    } else if (col === 'payout') {
      cmp = numVal(a.payout) - numVal(b.payout);
    }
    return cmp * dir;
  });
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

export function BidGrid({ rows, onRowSaved, onRowError, disabled = false, totalRowCount }: BidGridProps) {
  const [sort, setSort] = useState<SortState>({ column: null, direction: null });
  const [filters, setFilters] = useState<FilterState>(EMPTY_FILTERS);

  const setFilter = (key: keyof FilterState, value: string): void => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const filtered = useMemo(() => applyFilters(rows, filters), [rows, filters]);
  const sorted = useMemo(() => applySort(filtered, sort), [filtered, sort]);

  const total = totalRowCount ?? rows.length;
  const matchCount = filtered.length;

  const handleSort = (column: SortColumn): void => {
    setSort((prev) => nextSort(prev, column));
  };

  return (
    <div>
      <div className={styles.wrapper}>
        <table className={styles.table}>
          <thead className={styles.thead}>
            {/* Row 1: column labels + sort arrows */}
            <tr className={styles.thLabelRow}>
              <th aria-sort={ariaSort(sort, 'ecoid')} style={{ textAlign: 'left' }} className="px-2">
                <span>Device</span>
                <button type="button" aria-label="Sort by Device" onClick={() => handleSort('ecoid')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'ecoid')}
                </button>
              </th>
              <th aria-sort={ariaSort(sort, 'mergedGrade')} style={{ textAlign: 'left' }} className="px-2">
                <span>Grade</span>
                <button type="button" aria-label="Sort by Grade" onClick={() => handleSort('mergedGrade')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'mergedGrade')}
                </button>
              </th>
              <th aria-sort={ariaSort(sort, 'targetPrice')} style={{ textAlign: 'right' }} className="px-2">
                <span>Target Price</span>
                <button type="button" aria-label="Sort by Target Price" onClick={() => handleSort('targetPrice')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'targetPrice')}
                </button>
              </th>
              <th aria-sort={ariaSort(sort, 'maximumQuantity')} style={{ textAlign: 'right' }} className="px-2">
                <span>Max Qty</span>
                <button type="button" aria-label="Sort by Max Qty" onClick={() => handleSort('maximumQuantity')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'maximumQuantity')}
                </button>
              </th>
              <th aria-sort={ariaSort(sort, 'bidQuantity')} style={{ textAlign: 'right' }} className="px-2">
                <span>Bid Qty</span>
                <button type="button" aria-label="Sort by Bid Qty" onClick={() => handleSort('bidQuantity')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'bidQuantity')}
                </button>
              </th>
              <th aria-sort={ariaSort(sort, 'bidAmount')} style={{ textAlign: 'right' }} className="px-2">
                <span>Bid Amount</span>
                <button type="button" aria-label="Sort by Bid Amount" onClick={() => handleSort('bidAmount')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'bidAmount')}
                </button>
              </th>
              <th aria-sort={ariaSort(sort, 'payout')} style={{ textAlign: 'right' }} className="px-2">
                <span>Payout</span>
                <button type="button" aria-label="Sort by Payout" onClick={() => handleSort('payout')} className={styles.sortBtn}>
                  {sortGlyph(sort, 'payout')}
                </button>
              </th>
            </tr>
            {/* Row 2: filter inputs */}
            <tr className={styles.thFilterRow}>
              <th>
                <input
                  type="text"
                  aria-label="Filter by Device"
                  placeholder="Filter…"
                  value={filters.ecoid}
                  onChange={(e) => setFilter('ecoid', e.target.value)}
                  className={styles.filterInput}
                />
              </th>
              <th>
                <input
                  type="text"
                  aria-label="Filter by Grade"
                  placeholder="Filter…"
                  value={filters.mergedGrade}
                  onChange={(e) => setFilter('mergedGrade', e.target.value)}
                  className={styles.filterInput}
                />
              </th>
              <th>
                <input
                  type="number"
                  aria-label="Filter by Target Price"
                  placeholder="="
                  value={filters.targetPrice}
                  onChange={(e) => setFilter('targetPrice', e.target.value)}
                  className={`${styles.filterInput} ${styles.filterInputRight}`}
                />
              </th>
              <th>
                <input
                  type="number"
                  aria-label="Filter by Max Qty"
                  placeholder="="
                  value={filters.maximumQuantity}
                  onChange={(e) => setFilter('maximumQuantity', e.target.value)}
                  className={`${styles.filterInput} ${styles.filterInputRight}`}
                />
              </th>
              <th>
                <input
                  type="number"
                  aria-label="Filter by Bid Qty"
                  placeholder="="
                  value={filters.bidQuantity}
                  onChange={(e) => setFilter('bidQuantity', e.target.value)}
                  className={`${styles.filterInput} ${styles.filterInputRight}`}
                />
              </th>
              <th>
                <input
                  type="number"
                  aria-label="Filter by Bid Amount"
                  placeholder="="
                  value={filters.bidAmount}
                  onChange={(e) => setFilter('bidAmount', e.target.value)}
                  className={`${styles.filterInput} ${styles.filterInputRight}`}
                />
              </th>
              <th>
                <input
                  type="number"
                  aria-label="Filter by Payout"
                  placeholder="="
                  value={filters.payout}
                  onChange={(e) => setFilter('payout', e.target.value)}
                  className={`${styles.filterInput} ${styles.filterInputRight}`}
                />
              </th>
            </tr>
          </thead>
          <tbody>
            {sorted.map((row, i) => (
              <BidGridRow
                key={row.id}
                row={row}
                striped={i % 2 === 1}
                disabled={disabled}
                onSaved={onRowSaved}
                onError={onRowError}
              />
            ))}
          </tbody>
        </table>
      </div>
      <div className={styles.footer} aria-live="polite">
        {`Currently showing ${matchCount.toLocaleString()} of ${total.toLocaleString()}`}
      </div>
    </div>
  );
}
