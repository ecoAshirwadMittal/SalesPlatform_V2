'use client';

import { useEffect, useState, useMemo, useCallback, useRef } from 'react';
import styles from './inventory.module.css';
import { apiFetch } from '@/lib/apiFetch';
import {
  parseDevices,
  parseCaseLots,
  matchesSearch,
  matchesCLSearch,
  emptyFilters,
  type DeviceItem,
  type CaseLotItem,
  type ColumnFilters,
  type SortField,
  type SortDir,
  type TabId,
} from './inventory-helpers';

const API_BASE = '/api/v1';
const PAGE_SIZE = 50;
const DEBOUNCE_MS = 300;

// ── Main Component ──

export default function InventoryPage() {
  const [activeTab, setActiveTab] = useState<TabId>('functional');
  const [functionalDevices, setFunctionalDevices] = useState<DeviceItem[]>([]);
  const [caseLots, setCaseLots] = useState<CaseLotItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);

  // Search
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const searchTimer = useRef<ReturnType<typeof setTimeout>>(undefined);

  // Filters, sort, page per tab
  const [fdFilters, setFdFilters] = useState<ColumnFilters>({ ...emptyFilters });
  const [fdSort, setFdSort] = useState<SortField>(null);
  const [fdSortDir, setFdSortDir] = useState<SortDir>('asc');
  const [fdPage, setFdPage] = useState(0);

  const [clFilters, setClFilters] = useState<ColumnFilters>({ ...emptyFilters });
  const [clSort, setClSort] = useState<SortField>(null);
  const [clSortDir, setClSortDir] = useState<SortDir>('asc');
  const [clPage, setClPage] = useState(0);


  // ── Load data ──
  useEffect(() => {
    (async () => {
      try {
        const [fdRes, clRes] = await Promise.all([
          apiFetch(`${API_BASE}/inventory/devices?itemType=PWS&minAtpQty=0&excludeGrade=A_YYY`),
          apiFetch(`${API_BASE}/inventory/case-lots`),
        ]);
        if (fdRes.ok) setFunctionalDevices(parseDevices(await fdRes.json()));
        if (clRes.ok) setCaseLots(parseCaseLots(await clRes.json()));
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error('Failed to load inventory:', err);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  // ── Debounced search ──
  useEffect(() => {
    if (searchTimer.current) clearTimeout(searchTimer.current);
    searchTimer.current = setTimeout(() => setDebouncedSearch(searchTerm), DEBOUNCE_MS);
    return () => { if (searchTimer.current) clearTimeout(searchTimer.current); };
  }, [searchTerm]);

  const searchTokens = useMemo(() =>
    debouncedSearch.trim().toLowerCase().split(/\s+/).filter(Boolean),
    [debouncedSearch]
  );

  // ── Filter + sort + paginate for Functional Devices ──
  const fdFiltered = useMemo(() => {
    return functionalDevices
      .filter((d) => matchesSearch(d, searchTokens))
      .filter((d) => {
        if (fdFilters.sku && !d.sku.toLowerCase().includes(fdFilters.sku.toLowerCase())) return false;
        if (fdFilters.category && d.category !== fdFilters.category) return false;
        if (fdFilters.brand && d.brand !== fdFilters.brand) return false;
        if (fdFilters.model && d.model !== fdFilters.model) return false;
        if (fdFilters.carrier && d.carrier !== fdFilters.carrier) return false;
        if (fdFilters.capacity && d.capacity !== fdFilters.capacity) return false;
        if (fdFilters.color && d.color !== fdFilters.color) return false;
        if (fdFilters.grade && d.grade !== fdFilters.grade) return false;
        if (fdFilters.price && d.listPrice < parseFloat(fdFilters.price)) return false;
        if (fdFilters.qty && d.atpQty < parseInt(fdFilters.qty)) return false;
        return true;
      });
  }, [functionalDevices, searchTokens, fdFilters]);

  const fdSorted = useMemo(() => {
    if (!fdSort) return fdFiltered;
    return [...fdFiltered].sort((a, b) => {
      const av = (a as unknown as Record<string, unknown>)[fdSort];
      const bv = (b as unknown as Record<string, unknown>)[fdSort];
      if (typeof av === 'number' && typeof bv === 'number') return fdSortDir === 'asc' ? av - bv : bv - av;
      return fdSortDir === 'asc' ? String(av || '').localeCompare(String(bv || '')) : String(bv || '').localeCompare(String(av || ''));
    });
  }, [fdFiltered, fdSort, fdSortDir]);

  const fdPageData = useMemo(() => fdSorted.slice(fdPage * PAGE_SIZE, (fdPage + 1) * PAGE_SIZE), [fdSorted, fdPage]);
  const fdTotalPages = Math.ceil(fdSorted.length / PAGE_SIZE);

  // ── Filter + sort + paginate for Case Lots ──
  const clFiltered = useMemo(() => {
    return caseLots
      .filter((c) => matchesCLSearch(c, searchTokens))
      .filter((c) => {
        if (clFilters.sku && !c.sku.toLowerCase().includes(clFilters.sku.toLowerCase())) return false;
        if (clFilters.category && c.category !== clFilters.category) return false;
        if (clFilters.brand && c.brand !== clFilters.brand) return false;
        if (clFilters.model && c.model !== clFilters.model) return false;
        if (clFilters.carrier && c.carrier !== clFilters.carrier) return false;
        if (clFilters.capacity && c.capacity !== clFilters.capacity) return false;
        if (clFilters.color && c.color !== clFilters.color) return false;
        if (clFilters.grade && c.grade !== clFilters.grade) return false;
        return true;
      });
  }, [caseLots, searchTokens, clFilters]);

  const clSorted = useMemo(() => {
    if (!clSort) return clFiltered;
    return [...clFiltered].sort((a, b) => {
      const av = (a as unknown as Record<string, unknown>)[clSort];
      const bv = (b as unknown as Record<string, unknown>)[clSort];
      if (typeof av === 'number' && typeof bv === 'number') return clSortDir === 'asc' ? av - bv : bv - av;
      return clSortDir === 'asc' ? String(av || '').localeCompare(String(bv || '')) : String(bv || '').localeCompare(String(av || ''));
    });
  }, [clFiltered, clSort, clSortDir]);

  const clPageData = useMemo(() => clSorted.slice(clPage * PAGE_SIZE, (clPage + 1) * PAGE_SIZE), [clSorted, clPage]);
  const clTotalPages = Math.ceil(clSorted.length / PAGE_SIZE);

  // ── Cascading dropdown distinct values (matches Shop page pattern) ──
  // Each dropdown shows only values that exist after applying all OTHER active filters
  const fdDistinct = useMemo(() => {
    const fields = ['category', 'brand', 'model', 'carrier', 'capacity', 'color', 'grade'] as const;
    const result: Record<string, string[]> = {};
    for (const field of fields) {
      const filtered = functionalDevices.filter((d) => {
        if (!matchesSearch(d, searchTokens)) return false;
        for (const f of fields) {
          if (f === field) continue;
          if (fdFilters[f] && d[f as keyof DeviceItem] !== fdFilters[f]) return false;
        }
        if (fdFilters.sku && !d.sku.toLowerCase().includes(fdFilters.sku.toLowerCase())) return false;
        return true;
      });
      const vals = new Set<string>();
      filtered.forEach((d) => { const v = d[field as keyof DeviceItem] as string; if (v) vals.add(v); });
      result[field] = Array.from(vals).sort();
    }
    return result;
  }, [functionalDevices, fdFilters, searchTokens]);

  const clDistinct = useMemo(() => {
    const fields = ['category', 'brand', 'model', 'carrier', 'capacity', 'color', 'grade'] as const;
    const result: Record<string, string[]> = {};
    for (const field of fields) {
      const filtered = caseLots.filter((c) => {
        if (!matchesCLSearch(c, searchTokens)) return false;
        for (const f of fields) {
          if (f === field) continue;
          if (clFilters[f] && c[f as keyof CaseLotItem] !== clFilters[f]) return false;
        }
        if (clFilters.sku && !c.sku.toLowerCase().includes(clFilters.sku.toLowerCase())) return false;
        return true;
      });
      const vals = new Set<string>();
      filtered.forEach((c) => { const v = c[field as keyof CaseLotItem] as string; if (v) vals.add(v); });
      result[field] = Array.from(vals).sort();
    }
    return result;
  }, [caseLots, clFilters, searchTokens]);

  // ── Export handler — client-side CSV export (matches Shop page pattern) ──
  const handleExport = useCallback(() => {
    setDownloading(true);
    try {
      let csv = '';
      let filename = '';

      if (activeTab === 'functional') {
        csv = 'SKU,Category,Brand,Model Family,Carrier,Capacity,Color,Grade,Price,Avl. Qty\n';
        for (const d of fdSorted) {
          csv += [d.sku, d.category, d.brand, d.model, d.carrier, d.capacity, d.color, d.grade,
                  d.listPrice, d.atpQty].join(',') + '\n';
        }
        filename = `CONFIDENTIAL_PWS_Inventory_Functional_${new Date().toISOString().slice(0, 10).replace(/-/g, '')}.csv`;
      } else {
        csv = 'SKU,Category,Brand,Model Family,Carrier,Capacity,Color,Grade,Case Pack Qty,Avl. Cases,Unit Price,Case Price\n';
        for (const c of clSorted) {
          csv += [c.sku, c.category, c.brand, c.model, c.carrier, c.capacity, c.color, c.grade,
                  c.caseLotSize, c.caseLotAtpQty, c.unitPrice, c.caseLotPrice].join(',') + '\n';
        }
        filename = `CONFIDENTIAL_PWS_Inventory_CaseLots_${new Date().toISOString().slice(0, 10).replace(/-/g, '')}.csv`;
      }

      const blob = new Blob([csv], { type: 'text/csv' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();
      URL.revokeObjectURL(url);
    } finally {
      setDownloading(false);
    }
  }, [activeTab, fdSorted, clSorted]);

  // ── Sort toggle helper ──
  function toggleSort(current: SortField, dir: SortDir, field: string, setField: (f: SortField) => void, setDir: (d: SortDir) => void) {
    if (current === field) {
      setDir(dir === 'asc' ? 'desc' : 'asc');
    } else {
      setField(field);
      setDir('asc');
    }
  }


  // ── Render ──

  if (loading) {
    return (
      <div className={styles.pageWrapper}>
        <div className={styles.contentArea}>
          <div className={styles.loadingState}>Loading inventory...</div>
        </div>
      </div>
    );
  }

  const tabs: { id: TabId; label: string; visible: boolean }[] = [
    { id: 'functional', label: 'Functional Devices', visible: true },
    { id: 'caseLots', label: 'Functional Case Lots', visible: true },
  ];

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.contentArea}>
        {/* Header */}
        <div className={styles.pageHeader}>
          <h1 className={styles.pageTitle}>Inventory</h1>
          <div className={styles.searchBar}>
            <input
              className={styles.searchInput}
              placeholder="Search inventory..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setFdPage(0); setClPage(0); }}
            />
            <svg className={styles.searchIconSvg} width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </div>
          <button className={styles.downloadBtn} disabled={downloading} onClick={handleExport}>
            {downloading ? 'Exporting...' : 'Export'}
          </button>
        </div>

        {/* Tabs */}
        <div className={styles.tabsContainer}>
          <div className={styles.tabList}>
            {tabs.filter((t) => t.visible).map((tab) => (
              <button
                key={tab.id}
                className={`${styles.tabItem} ${activeTab === tab.id ? styles.tabItemActive : ''}`}
                onClick={() => setActiveTab(tab.id)}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* Tab content */}
        <div className={styles.gridWrapper}>
          {activeTab === 'functional' && (
            <DeviceGrid
              data={fdPageData}
              filters={fdFilters}
              setFilters={(f) => { setFdFilters(f); setFdPage(0); }}
              sortField={fdSort}
              sortDir={fdSortDir}
              onSort={(f) => toggleSort(fdSort, fdSortDir, f, setFdSort, setFdSortDir)}
              distinctValues={fdDistinct}
              page={fdPage}
              totalPages={fdTotalPages}
              totalCount={fdSorted.length}
              setPage={setFdPage}
            />
          )}
          {activeTab === 'caseLots' && (
            <CaseLotGrid
              data={clPageData}
              filters={clFilters}
              setFilters={(f) => { setClFilters(f); setClPage(0); }}
              sortField={clSort}
              sortDir={clSortDir}
              onSort={(f) => toggleSort(clSort, clSortDir, f, setClSort, setClSortDir)}
              distinctValues={clDistinct}
              page={clPage}
              totalPages={clTotalPages}
              totalCount={clSorted.length}
              setPage={setClPage}
            />
          )}
        </div>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════════════════
// Device Grid (Functional Devices tab)
// ══════════════════════════════════════════════════════════════════

interface DeviceGridProps {
  data: DeviceItem[];
  filters: ColumnFilters;
  setFilters: (f: ColumnFilters) => void;
  sortField: SortField;
  sortDir: SortDir;
  onSort: (field: string) => void;
  distinctValues: Record<string, string[]>;
  page: number;
  totalPages: number;
  totalCount: number;
  setPage: (p: number) => void;
}

function DeviceGrid({ data, filters, setFilters, sortField, sortDir, onSort, distinctValues, page, totalPages, totalCount, setPage }: DeviceGridProps) {
  const s = styles;

  const columns: { key: string; label: string; filterType: 'text' | 'dropdown' | 'number'; className?: string }[] = [
    { key: 'sku', label: 'SKU', filterType: 'text', className: s.colSku },
    { key: 'category', label: 'Category', filterType: 'dropdown' },
    { key: 'brand', label: 'Brand', filterType: 'dropdown' },
    { key: 'model', label: 'Model Family', filterType: 'dropdown', className: s.colModel },
    { key: 'carrier', label: 'Carrier', filterType: 'dropdown' },
    { key: 'capacity', label: 'Capacity', filterType: 'dropdown' },
    { key: 'color', label: 'Color', filterType: 'dropdown' },
    { key: 'grade', label: 'Grade', filterType: 'dropdown' },
    { key: 'listPrice', label: 'Price', filterType: 'number' },
    { key: 'atpQty', label: 'Avl. Qty', filterType: 'number' },
  ];

  const filterKey = (key: string) => key === 'listPrice' ? 'price' : key === 'atpQty' ? 'qty' : key;

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

  return (
    <>
      <div className={s.gridContainer}>
        <table className={s.datagrid}>
          <thead>
            <tr>
              {columns.map((col) => (
                <th key={col.key} className={col.className} onClick={() => onSort(col.key)}>
                  {col.label} {sortIcon(col.key)}
                </th>
              ))}
            </tr>
            <tr className={s.filterRow}>
              {columns.map((col) => (
                <th key={col.key}>
                  {col.filterType === 'text' && (
                    <input className={s.filterInput} value={filters[filterKey(col.key) as keyof ColumnFilters]} onChange={(e) => setFilters({ ...filters, [filterKey(col.key)]: e.target.value })} />
                  )}
                  {col.filterType === 'dropdown' && (
                    <select className={s.filterSelect} value={filters[col.key as keyof ColumnFilters]} onChange={(e) => setFilters({ ...filters, [col.key]: e.target.value })}>
                      <option value="">Search</option>
                      {(distinctValues[col.key] || []).map((v) => <option key={v} value={v}>{v}</option>)}
                    </select>
                  )}
                  {col.filterType === 'number' && (
                    <input className={s.filterInputNumber} type="number" placeholder="=" value={filters[filterKey(col.key) as keyof ColumnFilters]} onChange={(e) => setFilters({ ...filters, [filterKey(col.key)]: e.target.value })} />
                  )}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((d) => (
              <tr key={d.id}>
                <td>{d.sku}</td>
                <td>{d.category}</td>
                <td>{d.brand}</td>
                <td>{d.model}</td>
                <td>{d.carrier}</td>
                <td>{d.capacity}</td>
                <td>{d.color}</td>
                <td>{d.grade}</td>
                <td>${d.listPrice.toFixed(2)}</td>
                <td>{d.atpQty > 100 ? '100+' : d.atpQty}</td>
              </tr>
            ))}
            {data.length === 0 && (
              <tr><td colSpan={10} className={s.emptyState}>No devices match the current filters.</td></tr>
            )}
          </tbody>
        </table>
      </div>
      <Pagination page={page} totalPages={totalPages} totalCount={totalCount} setPage={setPage} />
    </>
  );
}

// ══════════════════════════════════════════════════════════════════
// Case Lot Grid
// ══════════════════════════════════════════════════════════════════

interface CaseLotGridProps {
  data: CaseLotItem[];
  filters: ColumnFilters;
  setFilters: (f: ColumnFilters) => void;
  sortField: SortField;
  sortDir: SortDir;
  onSort: (field: string) => void;
  distinctValues: Record<string, string[]>;
  page: number;
  totalPages: number;
  totalCount: number;
  setPage: (p: number) => void;
}

function CaseLotGrid({ data, filters, setFilters, sortField, sortDir, onSort, distinctValues, page, totalPages, totalCount, setPage }: CaseLotGridProps) {
  const s = styles;

  const columns: { key: string; label: string; filterType: 'text' | 'dropdown' | 'number' }[] = [
    { key: 'sku', label: 'SKU', filterType: 'text' },
    { key: 'category', label: 'Category', filterType: 'dropdown' },
    { key: 'brand', label: 'Brand', filterType: 'dropdown' },
    { key: 'model', label: 'Model Family', filterType: 'dropdown' },
    { key: 'carrier', label: 'Carrier', filterType: 'dropdown' },
    { key: 'capacity', label: 'Capacity', filterType: 'dropdown' },
    { key: 'color', label: 'Color', filterType: 'dropdown' },
    { key: 'grade', label: 'Grade', filterType: 'dropdown' },
    { key: 'caseLotSize', label: 'Case Pack Qty', filterType: 'number' },
    { key: 'caseLotAtpQty', label: 'Avl. Cases', filterType: 'number' },
    { key: 'unitPrice', label: 'Unit Price', filterType: 'number' },
    { key: 'caseLotPrice', label: 'Case Price', filterType: 'number' },
  ];

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

  return (
    <>
      <div className={s.gridContainer}>
        <table className={s.datagrid}>
          <thead>
            <tr>
              {columns.map((col) => (
                <th key={col.key} onClick={() => onSort(col.key)}>
                  {col.label} {sortIcon(col.key)}
                </th>
              ))}
            </tr>
            <tr className={s.filterRow}>
              {columns.map((col) => (
                <th key={col.key}>
                  {col.filterType === 'text' && (
                    <input className={s.filterInput} value={col.key === 'sku' ? filters.sku : ''} onChange={(e) => setFilters({ ...filters, [col.key]: e.target.value })} />
                  )}
                  {col.filterType === 'dropdown' && (
                    <select className={s.filterSelect} value={filters[col.key as keyof ColumnFilters] || ''} onChange={(e) => setFilters({ ...filters, [col.key]: e.target.value })}>
                      <option value="">Search</option>
                      {(distinctValues[col.key] || []).map((v) => <option key={v} value={v}>{v}</option>)}
                    </select>
                  )}
                  {col.filterType === 'number' && (
                    <input className={s.filterInputNumber} type="number" placeholder="=" />
                  )}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((c) => (
              <tr key={c.id}>
                <td>{c.sku}</td>
                <td>{c.category}</td>
                <td>{c.brand}</td>
                <td>{c.model}</td>
                <td>{c.carrier}</td>
                <td>{c.capacity}</td>
                <td>{c.color}</td>
                <td>{c.grade}</td>
                <td>{c.caseLotSize}</td>
                <td>{c.caseLotAtpQty}</td>
                <td>${c.unitPrice.toFixed(2)}</td>
                <td>${c.caseLotPrice.toFixed(2)}</td>
              </tr>
            ))}
            {data.length === 0 && (
              <tr><td colSpan={12} className={s.emptyState}>There are currently no case lots to purchase</td></tr>
            )}
          </tbody>
        </table>
      </div>
      <Pagination page={page} totalPages={totalPages} totalCount={totalCount} setPage={setPage} />
    </>
  );
}

// ══════════════════════════════════════════════════════════════════
// Pagination
// ══════════════════════════════════════════════════════════════════

function Pagination({ page, totalPages, totalCount, setPage }: { page: number; totalPages: number; totalCount: number; setPage: (p: number) => void }) {
  if (totalPages <= 1) return null;
  return (
    <div className={styles.pagination}>
      <button className={styles.pageBtn} disabled={page === 0} onClick={() => setPage(0)}>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="11 17 6 12 11 7" /><polyline points="18 17 13 12 18 7" /></svg>
      </button>
      <button className={styles.pageBtn} disabled={page === 0} onClick={() => setPage(page - 1)}>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6" /></svg>
      </button>
      <span className={styles.pageInfo}>{page * PAGE_SIZE + 1} to {Math.min((page + 1) * PAGE_SIZE, totalCount)} of {totalCount}</span>
      <button className={styles.pageBtn} disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6" /></svg>
      </button>
      <button className={styles.pageBtn} disabled={page >= totalPages - 1} onClick={() => setPage(totalPages - 1)}>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="13 17 18 12 13 7" /><polyline points="6 17 11 12 6 7" /></svg>
      </button>
    </div>
  );
}
