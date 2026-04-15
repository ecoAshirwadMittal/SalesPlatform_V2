'use client';

import { useEffect, useState, useCallback } from 'react';
import s from './pricing.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { useDebounce } from '@/lib/useDebounce';
import { API_BASE } from '@/lib/apiRoutes';
import type { PageResponse } from '@/lib/types';
import { getErrorMessage } from '@/lib/errors';
import { ErrorBanner } from '@/components/ErrorBanner';
import { PriceHistoryModal, type PriceHistoryEntry } from '@/components/pricing/PriceHistoryModal';
import { FutureDateModal } from '@/components/pricing/FutureDateModal';
import { UploadCsvModal, type UploadResult } from '@/components/pricing/UploadCsvModal';
import { PricingGrid, type PricingDevice, type Filters, type PendingEdit } from '@/components/pricing/PricingGrid';

const PAGE_SIZE = 20;
const DEBOUNCE_MS = 500;

type SortField = string | null;
type SortDir = 'asc' | 'desc';

const emptyFilters: Filters = {
  sku: '', category: '', brand: '', model: '',
  carrier: '', capacity: '', color: '', grade: '',
  currentListPrice: '', futureListPrice: '', currentMinPrice: '', futureMinPrice: '',
};

const dropdownFields = ['category', 'brand', 'model', 'carrier', 'capacity', 'color', 'grade'] as const;

// ── Main Component ──

export default function PricingPage() {
  const [data, setData] = useState<PricingDevice[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [uploadOpen, setUploadOpen] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState<UploadResult | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Future price date config
  const [futureDateOpen, setFutureDateOpen] = useState(false);
  const [futurePriceDate, setFuturePriceDate] = useState('');
  const [futureDateSaving, setFutureDateSaving] = useState(false);

  // Price history modal
  const [historyOpen, setHistoryOpen] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyDevice, setHistoryDevice] = useState<PricingDevice | null>(null);
  const [historyData, setHistoryData] = useState<PriceHistoryEntry[]>([]);

  const [filters, setFilters] = useState<Filters>({ ...emptyFilters });
  const debouncedFilters = useDebounce(filters, DEBOUNCE_MS);
  const [sortField, setSortField] = useState<SortField>(null);
  const [sortDir, setSortDir] = useState<SortDir>('asc');

  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, DEBOUNCE_MS);

  // Distinct values for dropdown filters
  const [distinctValues, setDistinctValues] = useState<Record<string, string[]>>({});

  // Inline editing state
  const [pendingEdits, setPendingEdits] = useState<Record<number, PendingEdit>>({});

  // Reset to first page whenever the debounced query inputs settle.
  useEffect(() => { setPage(0); }, [debouncedSearch, debouncedFilters]);

  // ── Fetch data from server ──
  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.set('page', String(page));
      params.set('size', String(PAGE_SIZE));

      if (debouncedFilters.sku || debouncedSearch) {
        params.set('sku', debouncedFilters.sku || debouncedSearch);
      }
      for (const field of dropdownFields) {
        if (debouncedFilters[field]) params.set(field, debouncedFilters[field]);
      }
      const priceFields = ['currentListPrice', 'futureListPrice', 'currentMinPrice', 'futureMinPrice'] as const;
      for (const pf of priceFields) {
        if (debouncedFilters[pf]) params.set(pf, debouncedFilters[pf]);
      }

      const res = await apiFetch(`${API_BASE}/pws/pricing/devices?${params.toString()}`);
      if (res.ok) {
        const json: PageResponse<PricingDevice> = await res.json();
        setData(json.content);
        setTotalElements(json.totalElements);
        setTotalPages(json.totalPages);
      }
    } catch (err) {
      setErrorMsg(getErrorMessage(err, 'Failed to load pricing data.'));
    } finally {
      setLoading(false);
    }
  }, [page, debouncedFilters, debouncedSearch]);

  useEffect(() => { fetchData(); }, [fetchData]);

  // ── Fetch future price config on mount ──
  useEffect(() => {
    (async () => {
      try {
        const res = await apiFetch(`${API_BASE}/pws/pricing/config`);
        if (res.ok) {
          const json = await res.json();
          if (json.futurePriceDate) {
            setFuturePriceDate(json.futurePriceDate.slice(0, 10));
          }
        }
      } catch { /* not critical */ }
    })();
  }, []);

  // ── Save future price date ──
  const handleFutureDateSave = useCallback(async () => {
    setFutureDateSaving(true);
    try {
      const dateValue = futurePriceDate ? futurePriceDate + 'T00:00:00' : '';
      const res = await apiFetch(`${API_BASE}/pws/pricing/config`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ futurePriceDate: dateValue }),
      });
      if (res.ok) {
        setFutureDateOpen(false);
      }
    } catch (err) {
      setErrorMsg(getErrorMessage(err, 'Failed to save future price date.'));
    } finally {
      setFutureDateSaving(false);
    }
  }, [futurePriceDate]);

  // ── Fetch distinct values for dropdowns (once on mount) ──
  useEffect(() => {
    (async () => {
      try {
        const res = await apiFetch(`${API_BASE}/pws/pricing/devices?page=0&size=10000`);
        if (!res.ok) return;
        const json: PageResponse<PricingDevice> = await res.json();
        const vals: Record<string, Set<string>> = {};
        for (const field of dropdownFields) vals[field] = new Set();
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

  const handleEditChange = useCallback((deviceId: number, field: 'futureListPrice' | 'futureMinPrice', value: string) => {
    setPendingEdits((prev) => {
      const existing = prev[deviceId];
      const row = data.find((d) => d.id === deviceId);
      const base: PendingEdit = existing || {
        futureListPrice: row?.futureListPrice != null ? row.futureListPrice.toFixed(2) : '',
        futureMinPrice: row?.futureMinPrice != null ? row.futureMinPrice.toFixed(2) : '',
      };
      return { ...prev, [deviceId]: { ...base, [field]: value } };
    });
  }, [data]);

  const hasEdits = Object.keys(pendingEdits).length > 0;

  // ── Save pending edits ──
  const handleSave = useCallback(async () => {
    const entries = Object.entries(pendingEdits);
    if (entries.length === 0) return;

    setSaving(true);
    try {
      const requests = entries.map(([id, edit]) => ({
        deviceId: Number(id),
        futureListPrice: edit.futureListPrice !== '' ? parseFloat(edit.futureListPrice) : null,
        futureMinPrice: edit.futureMinPrice !== '' ? parseFloat(edit.futureMinPrice) : null,
      }));

      const res = await apiFetch(`${API_BASE}/pws/pricing/devices/bulk`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requests),
      });

      if (res.ok) {
        setPendingEdits({});
        await fetchData();
      }
    } catch (err) {
      setErrorMsg(getErrorMessage(err, 'Failed to save pricing updates.'));
    } finally {
      setSaving(false);
    }
  }, [pendingEdits, fetchData]);

  // ── Discard edits ──
  const handleDiscard = useCallback(() => {
    setPendingEdits({});
  }, []);

  // ── Export ──
  const handleExport = useCallback(async () => {
    setExporting(true);
    try {
      // Fetch all data for export (respecting current filters)
      const params = new URLSearchParams();
      params.set('page', '0');
      params.set('size', '100000');
      if (debouncedFilters.sku || debouncedSearch) {
        params.set('sku', debouncedFilters.sku || debouncedSearch);
      }
      for (const field of dropdownFields) {
        if (debouncedFilters[field]) params.set(field, debouncedFilters[field]);
      }
      const priceFieldsExport = ['currentListPrice', 'futureListPrice', 'currentMinPrice', 'futureMinPrice'] as const;
      for (const pf of priceFieldsExport) {
        if (debouncedFilters[pf]) params.set(pf, debouncedFilters[pf]);
      }

      const res = await apiFetch(`${API_BASE}/pws/pricing/devices?${params.toString()}`);
      if (!res.ok) return;
      const json: PageResponse<PricingDevice> = await res.json();

      let csv = 'SKU,Category,Brand,Model Family,Carrier,Capacity,Color,Grade,Current List Price,New List Price,Current Min Price,New Min Price\n';
      for (const d of json.content) {
        csv += [
          d.sku,
          d.categoryName ?? '',
          d.brandName ?? '',
          d.modelName ?? '',
          d.carrierName ?? '',
          d.capacityName ?? '',
          d.colorName ?? '',
          d.gradeName ?? '',
          d.currentListPrice ?? '',
          d.futureListPrice ?? '',
          d.currentMinPrice ?? '',
          d.futureMinPrice ?? '',
        ].join(',') + '\n';
      }

      const blob = new Blob([csv], { type: 'text/csv' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `CONFIDENTIAL_PWS_Pricing_${new Date().toISOString().slice(0, 10).replace(/-/g, '')}.csv`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      setErrorMsg(getErrorMessage(err, 'Failed to export pricing data.'));
    } finally {
      setExporting(false);
    }
  }, [debouncedFilters, debouncedSearch]);

  // ── Upload handler ──
  const handleUpload = useCallback(async (file: File) => {
    setUploading(true);
    setUploadResult(null);
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await apiFetch(`${API_BASE}/pws/pricing/devices/upload`, {
        method: 'POST',
        body: formData,
      });
      if (res.ok) {
        const result = await res.json();
        setUploadResult(result);
        if (result.updatedCount > 0) {
          await fetchData();
        }
      }
    } catch (err) {
      setErrorMsg(getErrorMessage(err, 'Failed to upload pricing CSV.'));
    } finally {
      setUploading(false);
    }
  }, [fetchData]);

  // ── Row click → price history ──
  const handleRowClick = useCallback(async (device: PricingDevice) => {
    setHistoryDevice(device);
    setHistoryOpen(true);
    setHistoryLoading(true);
    setHistoryData([]);
    try {
      const res = await apiFetch(`${API_BASE}/pws/pricing/devices/${device.id}/history`);
      if (res.ok) {
        setHistoryData(await res.json());
      }
    } catch (err) {
      setErrorMsg(getErrorMessage(err, 'Failed to load price history.'));
    } finally {
      setHistoryLoading(false);
    }
  }, []);

  return (
    <div className={s.pageWrapper}>
      <div className={s.contentArea}>
        <ErrorBanner message={errorMsg} onDismiss={() => setErrorMsg(null)} />
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
            {hasEdits && (
              <>
                <button className={s.actionBtn} type="button" onClick={handleDiscard} disabled={saving}>
                  Discard
                </button>
                <button className={`${s.actionBtn} ${s.saveBtn}`} type="button" onClick={handleSave} disabled={saving}>
                  {saving ? 'Saving…' : `Save (${Object.keys(pendingEdits).length})`}
                </button>
              </>
            )}
            <button className={s.actionBtn} type="button" onClick={() => setFutureDateOpen(true)}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                <line x1="16" y1="2" x2="16" y2="6" />
                <line x1="8" y1="2" x2="8" y2="6" />
                <line x1="3" y1="10" x2="21" y2="10" />
              </svg>
              Set Future Date
            </button>
            <button className={s.actionBtn} type="button" onClick={() => { setUploadOpen(true); setUploadResult(null); }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="17 8 12 3 7 8" />
                <line x1="12" y1="3" x2="12" y2="15" />
              </svg>
              Upload Data
            </button>
            <button className={s.actionBtn} type="button" onClick={handleExport} disabled={exporting}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
              {exporting ? 'Exporting…' : 'Export'}
            </button>
          </div>
        </div>

        {loading && data.length === 0 ? (
          <div className={s.loadingState}>Loading pricing data…</div>
        ) : (
          <PricingGrid
            rows={sorted}
            filters={filters}
            distinctValues={distinctValues}
            sortField={sortField}
            sortDir={sortDir}
            pendingEdits={pendingEdits}
            page={page}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={PAGE_SIZE}
            onSort={handleSort}
            onFilterChange={updateFilter}
            onEditChange={handleEditChange}
            onRowClick={handleRowClick}
            onPageChange={setPage}
          />
        )}
      </div>

      {historyOpen && (
        <PriceHistoryModal
          device={historyDevice}
          data={historyData}
          loading={historyLoading}
          onClose={() => setHistoryOpen(false)}
        />
      )}

      {futureDateOpen && (
        <FutureDateModal
          value={futurePriceDate}
          saving={futureDateSaving}
          onChange={setFuturePriceDate}
          onSave={handleFutureDateSave}
          onClose={() => setFutureDateOpen(false)}
        />
      )}

      {uploadOpen && (
        <UploadCsvModal
          uploading={uploading}
          result={uploadResult}
          onUpload={handleUpload}
          onClose={() => setUploadOpen(false)}
        />
      )}
    </div>
  );
}
