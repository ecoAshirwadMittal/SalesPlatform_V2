'use client';

import s from '@/app/pws/pricing/pricing.module.css';

export interface PricingDevice {
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

export interface Filters {
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  currentListPrice: string;
  futureListPrice: string;
  currentMinPrice: string;
  futureMinPrice: string;
}

export interface PendingEdit {
  futureListPrice: string;
  futureMinPrice: string;
}

type SortDir = 'asc' | 'desc';

interface PricingGridProps {
  rows: PricingDevice[];
  filters: Filters;
  distinctValues: Record<string, string[]>;
  sortField: string | null;
  sortDir: SortDir;
  pendingEdits: Record<number, PendingEdit>;
  page: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onSort: (field: string) => void;
  onFilterChange: (field: keyof Filters, value: string) => void;
  onEditChange: (deviceId: number, field: 'futureListPrice' | 'futureMinPrice', value: string) => void;
  onRowClick: (device: PricingDevice) => void;
  onPageChange: (page: number) => void;
}

const columns = [
  { key: 'sku', header: 'SKU', type: 'text' as const, className: s.colSku },
  { key: 'categoryName', header: 'Category', type: 'dropdown' as const, filterField: 'category' },
  { key: 'brandName', header: 'Brand', type: 'dropdown' as const, filterField: 'brand' },
  { key: 'modelName', header: 'Model Family', type: 'dropdown' as const, filterField: 'model', className: s.colModel },
  { key: 'carrierName', header: 'Carrier', type: 'dropdown' as const, filterField: 'carrier' },
  { key: 'capacityName', header: 'Capacity', type: 'dropdown' as const, filterField: 'capacity' },
  { key: 'colorName', header: 'Color', type: 'dropdown' as const, filterField: 'color' },
  { key: 'gradeName', header: 'Grade', type: 'dropdown' as const, filterField: 'grade' },
  { key: 'currentListPrice', header: 'Current List Price', type: 'number' as const, filterField: 'currentListPrice' },
  { key: 'futureListPrice', header: 'New List Price', type: 'number' as const, filterField: 'futureListPrice' },
  { key: 'currentMinPrice', header: 'Current Min Price', type: 'number' as const, filterField: 'currentMinPrice' },
  { key: 'futureMinPrice', header: 'New Min Price', type: 'number' as const, filterField: 'futureMinPrice' },
];

const fmtPrice = (v: number | null) => (v != null ? `$${v.toFixed(2)}` : '');

function getEditValue(
  pendingEdits: Record<number, PendingEdit>,
  deviceId: number,
  field: 'futureListPrice' | 'futureMinPrice',
  original: number | null,
): string {
  const edit = pendingEdits[deviceId];
  if (edit) return edit[field];
  return original != null ? original.toFixed(2) : '';
}

export function PricingGrid({
  rows,
  filters,
  distinctValues,
  sortField,
  sortDir,
  pendingEdits,
  page,
  totalPages,
  totalElements,
  pageSize,
  onSort,
  onFilterChange,
  onEditChange,
  onRowClick,
  onPageChange,
}: PricingGridProps) {
  const startRow = page * pageSize + 1;
  const endRow = Math.min((page + 1) * pageSize, totalElements);

  const sortIcon = (field: string) => (
    <span className={s.sortIcon}>
      <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
        <path d="M5 0L9.33 5H0.67L5 0Z" fill={sortField === field && sortDir === 'asc' ? '#1a2e35' : '#bbb'} />
        <path d="M5 14L0.67 9H9.33L5 14Z" fill={sortField === field && sortDir === 'desc' ? '#1a2e35' : '#bbb'} />
      </svg>
    </span>
  );

  return (
    <div className={s.gridWrapper}>
      <div className={s.gridContainer}>
        <table className={s.datagrid}>
          <thead>
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={col.className || undefined}
                  onClick={() => onSort(col.key)}
                >
                  {col.header}
                  {sortIcon(col.key)}
                </th>
              ))}
            </tr>
            <tr className={s.filterRow}>
              {columns.map((col) => (
                <th key={`f-${col.key}`}>
                  {col.type === 'text' && (
                    <input
                      type="text"
                      className={s.filterInput}
                      value={filters[col.key as keyof Filters] || ''}
                      onChange={(e) => onFilterChange(col.key as keyof Filters, e.target.value)}
                    />
                  )}
                  {col.type === 'dropdown' && (
                    <select
                      className={s.filterSelect}
                      value={filters[col.filterField as keyof Filters] || ''}
                      onChange={(e) => onFilterChange(col.filterField as keyof Filters, e.target.value)}
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
                      value={filters[col.filterField as keyof Filters] || ''}
                      onChange={(e) => onFilterChange(col.filterField as keyof Filters, e.target.value)}
                    />
                  )}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className={s.emptyState}>
                  No pricing data found
                </td>
              </tr>
            ) : (
              rows.map((row) => (
                <tr key={row.id} onClick={() => onRowClick(row)}>
                  <td>{row.sku}</td>
                  <td>{row.categoryName}</td>
                  <td>{row.brandName}</td>
                  <td>{row.modelName}</td>
                  <td>{row.carrierName}</td>
                  <td>{row.capacityName}</td>
                  <td>{row.colorName}</td>
                  <td>{row.gradeName}</td>
                  <td>{fmtPrice(row.currentListPrice)}</td>
                  <td className={s.inputCell}>
                    <input
                      type="text"
                      className={s.priceInput}
                      value={getEditValue(pendingEdits, row.id, 'futureListPrice', row.futureListPrice)}
                      onChange={(e) => onEditChange(row.id, 'futureListPrice', e.target.value)}
                    />
                  </td>
                  <td>{fmtPrice(row.currentMinPrice)}</td>
                  <td className={s.inputCell}>
                    <input
                      type="text"
                      className={s.priceInput}
                      value={getEditValue(pendingEdits, row.id, 'futureMinPrice', row.futureMinPrice)}
                      onChange={(e) => onEditChange(row.id, 'futureMinPrice', e.target.value)}
                    />
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className={s.pagination}>
        <span className={s.pageInfo}>
          {totalElements > 0 ? `${startRow} to ${endRow} of ${totalElements}` : '0 of 0'}
        </span>
        <button className={s.pageBtn} disabled={page === 0} onClick={() => onPageChange(0)} title="First">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="11 17 6 12 11 7" /><polyline points="18 17 13 12 18 7" /></svg>
        </button>
        <button className={s.pageBtn} disabled={page === 0} onClick={() => onPageChange(page - 1)} title="Previous">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6" /></svg>
        </button>
        <button className={s.pageBtn} disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)} title="Next">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6" /></svg>
        </button>
        <button className={s.pageBtn} disabled={page >= totalPages - 1} onClick={() => onPageChange(totalPages - 1)} title="Last">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="13 17 18 12 13 7" /><polyline points="6 17 11 12 6 7" /></svg>
        </button>
      </div>
    </div>
  );
}
