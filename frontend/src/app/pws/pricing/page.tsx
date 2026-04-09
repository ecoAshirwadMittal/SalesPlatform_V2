'use client';

import { useEffect, useState, useCallback, useRef } from 'react';
import s from './pricing.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API_BASE = '/api/v1';
const PAGE_SIZE = 20;
const DEBOUNCE_MS = 500;

// ── Types ──

interface PricingDevice {
  id: number;
  sku: string;
  categoryName: string | null;
  brandName: string | null;
  modelName: string | null;
  carrierName: string | null;
  capacityName: string | null;
  colorName: string | null;
  gradeName: string | null;
  currentListPrice: number | null;
  futureListPrice: number | null;
  currentMinPrice: number | null;
  futureMinPrice: number | null;
}

interface PageResponse {
  content: PricingDevice[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

interface Filters {
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
}

type SortField = string | null;
type SortDir = 'asc' | 'desc';

const emptyFilters: Filters = {
  sku: '', category: '', brand: '', model: '',
  carrier: '', capacity: '', color: '', grade: '',
};

const dropdownFields = ['category', 'brand', 'model', 'carrier', 'capacity', 'color', 'grade'] as const;

// ── Main Component ──

export default function PricingPage() {
  const [data, setData] = useState<PricingDevice[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  const [filters, setFilters] = useState<Filters>({ ...emptyFilters });
  const [debouncedFilters, setDebouncedFilters] = useState<Filters>({ ...emptyFilters });
  const [sortField, setSortField] = useState<SortField>(null);
  const [sortDir, setSortDir] = useState<SortDir>('asc');

  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const searchTimer = useRef<ReturnType<typeof setTimeout>>(undefined);
  const filterTimer = useRef<ReturnType<typeof setTimeout>>(undefined);

  // Distinct values for dropdown filters
  const [distinctValues, setDistinctValues] = useState<Record<string, string[]>>({});

  // ── Debounce search ──
  useEffect(() => {
    if (searchTimer.current) clearTimeout(searchTimer.current);
    searchTimer.current = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPage(0);
    }, DEBOUNCE_MS);
    return () => { if (searchTimer.current) clearTimeout(searchTimer.current); };
  }, [searchTerm]);

  // ── Debounce filters ──
  useEffect(() => {
    if (filterTimer.current) clearTimeout(filterTimer.current);
    filterTimer.current = setTimeout(() => {
      setDebouncedFilters({ ...filters });
      setPage(0);
    }, DEBOUNCE_MS);
    return () => { if (filterTimer.current) clearTimeout(filterTimer.current); };
  }, [filters]);

  // ── Fetch data from server ──
  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.set('page', String(page));
      params.set('size', String(PAGE_SIZE));

      // Apply debounced filters
      if (debouncedFilters.sku || debouncedSearch) {
        const skuSearch = debouncedFilters.sku || debouncedSearch;
        params.set('sku', skuSearch);
      }
      for (const field of dropdownFields) {
        if (debouncedFilters[field]) params.set(field, debouncedFilters[field]);
      }

      const res = await apiFetch(`${API_BASE}/pws/pricing/devices?${params.toString()}`);
      if (res.ok) {
        const json: PageResponse = await res.json();
        setData(json.content);
        setTotalElements(json.totalElements);
        setTotalPages(json.totalPages);
      }
    } catch (err) {
      console.error('Failed to load pricing data:', err);
    } finally {
      setLoading(false);
    }
  }, [page, debouncedFilters, debouncedSearch]);

  useEffect(() => { fetchData(); }, [fetchData]);

  // ── Fetch distinct values for dropdowns ──
  useEffect(() => {
    (async () => {
      try {
        // Fetch all active devices to build distinct values (using a large page)
        // In future phases this can be a dedicated endpoint
        const res = await apiFetch(`${API_BASE}/pws/pricing/devices?page=0&size=1`);
        if (res.ok) {
          // For now, we'll populate distinct values from the loaded data
          // A dedicated /distinct endpoint would be better for 23K rows
        }
      } catch { /* not critical */ }
    })();
  }, []);

  // Build distinct values from currently loaded data (basic approach for Phase 1)
  // In production, a dedicated endpoint should provide these
  useEffect(() => {
    const vals: Record<string, Set<string>> = {};
    for (const field of dropdownFields) {
      vals[field] = new Set();
    }
    // We need all data for distinct values, so fetch without pagination
    (async () => {
      try {
        const res = await apiFetch(`${API_BASE}/pws/pricing/devices?page=0&size=10000`);
        if (res.ok) {
          const json: PageResponse = await res.json();
          for (const d of json.content) {
            if (d.categoryName) vals['category'].add(d.categoryName);
            if (d.brandName) vals['brand'].add(d.brandName);
            if (d.modelName) vals['model'].add(d.modelName);
            if (d.carrierName) vals['carrier'].add(d.carrierName);
            if (d.capacityName) vals['capacity'].add(d.capacityName);
            if (d.colorName) vals['color'].add(d.colorName);
            if (d.gradeName) vals['grade'].add(d.gradeName);
          }
          const result: Record<string, string[]> = {};
          for (const field of dropdownFields) {
            result[field] = Array.from(vals[field]).sort();
          }
          setDistinctValues(result);
        }
      } catch { /* not critical */ }
    })();
  }, []);

  // ── Sort handler ──
  const handleSort = useCallback((field: string) => {
    if (sortField === field) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  }, [sortField]);

  // Client-side sort of the current page data
  const sorted = [...data].sort((a, b) => {
    if (!sortField) return 0;
    const aVal = a[sortField as keyof PricingDevice];
    const bVal = b[sortField as keyof PricingDevice];
    if (aVal == null && bVal == null) return 0;
    if (aVal == null) return 1;
    if (bVal == null) return -1;
    if (typeof aVal === 'number' && typeof bVal === 'number') {
      return sortDir === 'asc' ? aVal - bVal : bVal - aVal;
    }
    const cmp = String(aVal).localeCompare(String(bVal));
    return sortDir === 'asc' ? cmp : -cmp;
  });

  // ── Filter change handler ──
  const updateFilter = useCallback((field: keyof Filters, value: string) => {
    setFilters((prev) => ({ ...prev, [field]: value }));
  }, []);

  // ── Sort icon ──
  function sortIcon(field: string) {
    return (
      <span className={s.sortIcon}>
        <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
          <path d="M5 0L9.33 5H0.67L5 0Z" fill={sortField === field && sortDir === 'asc' ? '#1a2e35' : '#bbb'} />
          <path d="M5 14L0.67 9H9.33L5 14Z" fill={sortField === field && sortDir === 'desc' ? '#1a2e35' : '#bbb'} />
        </svg>
      </span>
    );
  }

  // ── Pagination helpers ──
  const startRow = page * PAGE_SIZE + 1;
  const endRow = Math.min((page + 1) * PAGE_SIZE, totalElements);

  // ── Price formatter ──
  const fmtPrice = (v: number | null) => (v != null ? `$${v.toFixed(2)}` : '');

  // ── Columns config ──
  const columns = [
    { key: 'sku', header: 'SKU', type: 'text' as const, className: s.colSku },
    { key: 'categoryName', header: 'Category', type: 'dropdown' as const, filterField: 'category' },
    { key: 'brandName', header: 'Brand', type: 'dropdown' as const, filterField: 'brand' },
    { key: 'modelName', header: 'Model Family', type: 'dropdown' as const, filterField: 'model', className: s.colModel },
    { key: 'carrierName', header: 'Carrier', type: 'dropdown' as const, filterField: 'carrier' },
    { key: 'capacityName', header: 'Capacity', type: 'dropdown' as const, filterField: 'capacity' },
    { key: 'colorName', header: 'Color', type: 'dropdown' as const, filterField: 'color' },
    { key: 'gradeName', header: 'Grade', type: 'dropdown' as const, filterField: 'grade' },
    { key: 'currentListPrice', header: 'Current List Price', type: 'number' as const },
    { key: 'futureListPrice', header: 'New List Price', type: 'number' as const, inputCell: true },
    { key: 'currentMinPrice', header: 'Current Min Price', type: 'number' as const },
    { key: 'futureMinPrice', header: 'New Min Price', type: 'number' as const, inputCell: true },
  ];

  return (
    <div className={s.pageWrapper}>
      <div className={s.contentArea}>
        {/* ── Header ── */}
        <div className={s.pageHeader}>
          <h1 className={s.pageTitle}>Pricing</h1>
          <div className={s.searchBar}>
            <input
              type="text"
              className={s.searchInput}
              placeholder="Search"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <svg className={s.searchIconSvg} width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </div>
          <div className={s.headerActions}>
            <button className={s.actionBtn} type="button">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="17 8 12 3 7 8" />
                <line x1="12" y1="3" x2="12" y2="15" />
              </svg>
              Upload Data
            </button>
            <button className={s.actionBtn} type="button">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
              Export
            </button>
          </div>
        </div>

        {/* ── Grid ── */}
        {loading && data.length === 0 ? (
          <div className={s.loadingState}>Loading pricing data…</div>
        ) : (
          <div className={s.gridWrapper}>
            <div className={s.gridContainer}>
              <table className={s.datagrid}>
                <thead>
                  {/* Header row */}
                  <tr>
                    {columns.map((col) => (
                      <th
                        key={col.key}
                        className={col.className || undefined}
                        onClick={() => handleSort(col.key)}
                      >
                        {col.header}
                        {sortIcon(col.key)}
                      </th>
                    ))}
                  </tr>
                  {/* Filter row */}
                  <tr className={s.filterRow}>
                    {columns.map((col) => (
                      <th key={`f-${col.key}`}>
                        {col.type === 'text' && (
                          <input
                            type="text"
                            className={s.filterInput}
                            value={filters[col.key as keyof Filters] || ''}
                            onChange={(e) => updateFilter(col.key as keyof Filters, e.target.value)}
                          />
                        )}
                        {col.type === 'dropdown' && (
                          <select
                            className={s.filterSelect}
                            value={filters[col.filterField as keyof Filters] || ''}
                            onChange={(e) => updateFilter(col.filterField as keyof Filters, e.target.value)}
                          >
                            <option value="">Search</option>
                            {(distinctValues[col.filterField!] || []).map((v) => (
                              <option key={v} value={v}>{v}</option>
                            ))}
                          </select>
                        )}
                        {col.type === 'number' && (
                          <input
                            type="text"
                            className={s.filterInputNumber}
                            placeholder="="
                          />
                        )}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {sorted.length === 0 ? (
                    <tr>
                      <td colSpan={columns.length} className={s.emptyState}>
                        No pricing data found
                      </td>
                    </tr>
                  ) : (
                    sorted.map((row) => (
                      <tr key={row.id}>
                        <td>{row.sku}</td>
                        <td>{row.categoryName}</td>
                        <td>{row.brandName}</td>
                        <td>{row.modelName}</td>
                        <td>{row.carrierName}</td>
                        <td>{row.capacityName}</td>
                        <td>{row.colorName}</td>
                        <td>{row.gradeName}</td>
                        <td>{fmtPrice(row.currentListPrice)}</td>
                        <td className={s.inputCell}>{fmtPrice(row.futureListPrice)}</td>
                        <td>{fmtPrice(row.currentMinPrice)}</td>
                        <td className={s.inputCell}>{fmtPrice(row.futureMinPrice)}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* ── Pagination ── */}
            <div className={s.pagination}>
              <span className={s.pageInfo}>
                {totalElements > 0 ? `${startRow} to ${endRow} of ${totalElements}` : '0 of 0'}
              </span>
              <button className={s.pageBtn} disabled={page === 0} onClick={() => setPage(0)} title="First">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="11 17 6 12 11 7" /><polyline points="18 17 13 12 18 7" /></svg>
              </button>
              <button className={s.pageBtn} disabled={page === 0} onClick={() => setPage((p) => p - 1)} title="Previous">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6" /></svg>
              </button>
              <button className={s.pageBtn} disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)} title="Next">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6" /></svg>
              </button>
              <button className={s.pageBtn} disabled={page >= totalPages - 1} onClick={() => setPage(totalPages - 1)} title="Last">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="13 17 18 12 13 7" /><polyline points="6 17 11 12 6 7" /></svg>
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
