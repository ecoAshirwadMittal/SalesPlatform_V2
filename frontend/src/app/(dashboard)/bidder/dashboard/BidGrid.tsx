'use client';
import { useMemo, useState } from 'react';
import type { BidDataRow } from '@/lib/bidder';
import { BidGridRow } from './BidGridRow';
import styles from './bidGrid.module.css';

// ---------------------------------------------------------------------------
// Types — Phase 6B: 11 QA-parity columns (was 7 in Phase 6A)
// ---------------------------------------------------------------------------

type SortColumn =
  | 'ecoid'
  | 'brand'
  | 'model'
  | 'modelName'
  | 'mergedGrade'
  | 'carrier'
  | 'added'
  | 'targetPrice'
  | 'maximumQuantity'
  | 'bidQuantity'
  | 'bidAmount';

type SortDirection = 'asc' | 'desc';

interface SortState {
  column: SortColumn | null;
  direction: SortDirection | null;
}

interface FilterState {
  ecoid: string;
  brand: string;
  model: string;
  modelName: string;
  mergedGrade: string;
  carrier: string;
  added: string;
  maximumQuantity: string;
  targetPrice: string;
  bidAmount: string;
  bidQuantity: string;
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
// Helpers
// ---------------------------------------------------------------------------

const EMPTY_FILTERS: FilterState = {
  ecoid: '',
  brand: '',
  model: '',
  modelName: '',
  mergedGrade: '',
  carrier: '',
  added: '',
  maximumQuantity: '',
  targetPrice: '',
  bidAmount: '',
  bidQuantity: '',
};

function nextSort(current: SortState, column: SortColumn): SortState {
  if (current.column !== column) return { column, direction: 'asc' };
  if (current.direction === 'asc') return { column, direction: 'desc' };
  return { column: null, direction: null };
}

function sortGlyph(sort: SortState, column: SortColumn): string {
  if (sort.column !== column) return '↕';
  return sort.direction === 'asc' ? '↑' : '↓';
}

function ariaSort(sort: SortState, column: SortColumn): 'ascending' | 'descending' | 'none' {
  if (sort.column !== column) return 'none';
  return sort.direction === 'asc' ? 'ascending' : 'descending';
}

function numVal(v: number | null | undefined): number {
  return v ?? -Infinity;
}

function strVal(v: string | null | undefined): string {
  return v ?? '';
}

function includesLC(haystack: string | null | undefined, needle: string): boolean {
  if (!needle) return true;
  if (!haystack) return false;
  return haystack.toLowerCase().includes(needle.toLowerCase());
}

function numberMatchesExact(field: number | null | undefined, filter: string): boolean {
  if (filter === '') return true;
  const f = parseFloat(filter);
  if (isNaN(f)) return true;
  return numVal(field) === f;
}

/**
 * Renders an ISO-8601 timestamp as `M/D/YYYY` — matches QA's short date
 * format (e.g. `10/10/2013`, `9/12/2019`). Null returns empty string.
 */
function formatAdded(iso: string | null | undefined): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return '';
  return `${d.getMonth() + 1}/${d.getDate()}/${d.getFullYear()}`;
}

function applyFilters(rows: BidDataRow[], filters: FilterState): BidDataRow[] {
  return rows.filter((row) => {
    if (!includesLC(row.ecoid, filters.ecoid)) return false;
    if (!includesLC(row.brand, filters.brand)) return false;
    if (!includesLC(row.model, filters.model)) return false;
    if (!includesLC(row.modelName, filters.modelName)) return false;
    if (!includesLC(row.mergedGrade, filters.mergedGrade)) return false;
    if (!includesLC(row.carrier, filters.carrier)) return false;
    // Date filter: substring match on formatted date OR raw ISO — keeps the
    // implementation trivial; upgrade to a picker if QA needs one later.
    if (filters.added !== '') {
      const formatted = formatAdded(row.added);
      const iso = row.added ?? '';
      if (!formatted.includes(filters.added) && !iso.includes(filters.added)) return false;
    }
    if (!numberMatchesExact(row.targetPrice, filters.targetPrice)) return false;
    if (!numberMatchesExact(row.maximumQuantity, filters.maximumQuantity)) return false;
    if (!numberMatchesExact(row.bidQuantity, filters.bidQuantity)) return false;
    if (!numberMatchesExact(row.bidAmount, filters.bidAmount)) return false;
    return true;
  });
}

function applySort(rows: BidDataRow[], sort: SortState): BidDataRow[] {
  if (!sort.column || !sort.direction) return rows;
  const col = sort.column;
  const dir = sort.direction === 'asc' ? 1 : -1;

  return [...rows].sort((a, b) => {
    let cmp = 0;
    switch (col) {
      case 'ecoid': cmp = a.ecoid.localeCompare(b.ecoid); break;
      case 'brand': cmp = strVal(a.brand).localeCompare(strVal(b.brand)); break;
      case 'model': cmp = strVal(a.model).localeCompare(strVal(b.model)); break;
      case 'modelName': cmp = strVal(a.modelName).localeCompare(strVal(b.modelName)); break;
      case 'mergedGrade': cmp = a.mergedGrade.localeCompare(b.mergedGrade); break;
      case 'carrier': cmp = strVal(a.carrier).localeCompare(strVal(b.carrier)); break;
      case 'added': cmp = strVal(a.added).localeCompare(strVal(b.added)); break;
      case 'targetPrice': cmp = a.targetPrice - b.targetPrice; break;
      case 'maximumQuantity': cmp = numVal(a.maximumQuantity) - numVal(b.maximumQuantity); break;
      case 'bidQuantity': cmp = numVal(a.bidQuantity) - numVal(b.bidQuantity); break;
      case 'bidAmount': cmp = a.bidAmount - b.bidAmount; break;
    }
    return cmp * dir;
  });
}

// ---------------------------------------------------------------------------
// Column header cell
// ---------------------------------------------------------------------------

interface HeaderCellProps {
  column: SortColumn;
  label: string;
  align?: 'left' | 'right';
  hideOnNarrow?: boolean;
  sort: SortState;
  onSort: (c: SortColumn) => void;
}

function HeaderCell({ column, label, align = 'left', hideOnNarrow, sort, onSort }: HeaderCellProps) {
  const thClass = hideOnNarrow ? styles.colModelName : undefined;
  return (
    <th aria-sort={ariaSort(sort, column)} style={{ textAlign: align }} className={thClass}>
      <span>{label}</span>
      <button type="button" aria-label={`Sort by ${label}`} onClick={() => onSort(column)} className={styles.sortBtn}>
        {sortGlyph(sort, column)}
      </button>
    </th>
  );
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

  const handleSort = (column: SortColumn): void => setSort((prev) => nextSort(prev, column));

  return (
    <div>
      <div className={styles.wrapper}>
        <table className={styles.table}>
          <thead className={styles.thead}>
            <tr className={styles.thLabelRow}>
              <HeaderCell column="ecoid" label="Product Id" sort={sort} onSort={handleSort} />
              <HeaderCell column="brand" label="Brand" sort={sort} onSort={handleSort} />
              <HeaderCell column="model" label="Model" sort={sort} onSort={handleSort} />
              <HeaderCell column="modelName" label="Model Name" hideOnNarrow sort={sort} onSort={handleSort} />
              <HeaderCell column="mergedGrade" label="Grade" sort={sort} onSort={handleSort} />
              <HeaderCell column="carrier" label="Carrier" sort={sort} onSort={handleSort} />
              <HeaderCell column="added" label="Added" sort={sort} onSort={handleSort} />
              <HeaderCell column="maximumQuantity" label="Avail. Qty" align="right" sort={sort} onSort={handleSort} />
              <HeaderCell column="targetPrice" label="Target Price" align="right" sort={sort} onSort={handleSort} />
              <HeaderCell column="bidAmount" label="Price" align="right" sort={sort} onSort={handleSort} />
              <HeaderCell column="bidQuantity" label="Qty. Cap" align="right" sort={sort} onSort={handleSort} />
            </tr>
            <tr className={styles.thFilterRow}>
              {([
                ['ecoid', 'text'],
                ['brand', 'text'],
                ['model', 'text'],
                ['modelName', 'text', styles.colModelName],
                ['mergedGrade', 'text'],
                ['carrier', 'text'],
                ['added', 'text'],
                ['maximumQuantity', 'number'],
                ['targetPrice', 'number'],
                ['bidAmount', 'number'],
                ['bidQuantity', 'number'],
              ] as const).map(([key, type, colCls]) => (
                <th key={key} className={colCls}>
                  <input
                    type={type === 'number' ? 'number' : 'text'}
                    aria-label={`Filter by ${key}`}
                    placeholder={type === 'number' ? '=' : 'Filter…'}
                    value={filters[key as keyof FilterState]}
                    onChange={(e) => setFilter(key as keyof FilterState, e.target.value)}
                    className={`${styles.filterInput} ${type === 'number' ? styles.filterInputRight : ''}`}
                  />
                </th>
              ))}
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
