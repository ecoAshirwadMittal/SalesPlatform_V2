import { useMemo } from 'react';

export type SortDir = 'asc' | 'desc';

interface UseListingOptions<T, F extends string> {
  rows: T[];
  multiFields: readonly F[];
  /** True if `row` should appear in the dataset. When `excludeMultiField` is set,
   *  the predicate must skip that single multi-select filter so distinctValues can
   *  show the full set of values still reachable for that column. */
  passes: (row: T, excludeMultiField: F | null) => boolean;
  sortField: keyof T | null;
  sortDir: SortDir;
  page: number;
  pageSize: number;
}

interface UseListingResult<T> {
  distinctValues: Record<string, string[]>;
  filteredRows: T[];
  pageRows: T[];
  totalPages: number;
  rangeStart: number;
  rangeEnd: number;
}

export function useListing<T, F extends string>(
  opts: UseListingOptions<T, F>,
): UseListingResult<T> {
  const { rows, multiFields, passes, sortField, sortDir, page, pageSize } = opts;

  const distinctValues = useMemo(() => {
    const result: Record<string, string[]> = {};
    for (const field of multiFields) {
      const vals = new Set<string>();
      for (const row of rows) {
        if (!passes(row, field)) continue;
        const v = row[field as unknown as keyof T];
        if (v && v !== '-') vals.add(String(v));
      }
      result[field] = Array.from(vals).sort();
    }
    return result;
  }, [rows, multiFields, passes]);

  const filteredRows = useMemo(() => {
    const filtered = rows.filter(row => passes(row, null));
    if (!sortField) return filtered;
    return [...filtered].sort((a, b) => {
      const aVal = a[sortField];
      const bVal = b[sortField];
      if (typeof aVal === 'number' && typeof bVal === 'number') {
        return sortDir === 'asc' ? aVal - bVal : bVal - aVal;
      }
      const cmp = String(aVal).toLowerCase().localeCompare(String(bVal).toLowerCase());
      return sortDir === 'asc' ? cmp : -cmp;
    });
  }, [rows, passes, sortField, sortDir]);

  const totalPages = Math.max(1, Math.ceil(filteredRows.length / pageSize));
  const pageRows = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filteredRows.slice(start, start + pageSize);
  }, [filteredRows, page, pageSize]);

  const rangeStart = filteredRows.length === 0 ? 0 : (page - 1) * pageSize + 1;
  const rangeEnd = Math.min(page * pageSize, filteredRows.length);

  return { distinctValues, filteredRows, pageRows, totalPages, rangeStart, rangeEnd };
}

function parseNum(value: string, parser: (s: string) => number): number | null {
  if (!value) return null;
  const n = parser(value);
  return isNaN(n) ? null : n;
}

export function matchesIntFilter(rowVal: number, filterVal: string): boolean {
  const n = parseNum(filterVal, s => parseInt(s, 10));
  return n === null || rowVal === n;
}

export function matchesFloatFilter(rowVal: number, filterVal: string): boolean {
  const n = parseNum(filterVal, parseFloat);
  return n === null || rowVal === n;
}

export function matchesContains(rowVal: string, filterVal: string): boolean {
  if (!filterVal) return true;
  return rowVal.toLowerCase().includes(filterVal.toLowerCase());
}

export function matchesMultiSelect<T extends string>(rowVal: T, selected: T[]): boolean {
  return selected.length === 0 || selected.includes(rowVal);
}
