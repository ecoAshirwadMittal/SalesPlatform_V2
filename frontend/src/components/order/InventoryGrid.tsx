'use client';

import styles from '@/app/pws/order/pwsOrder.module.css';
import MultiSelectFilter from '@/app/pws/order/MultiSelectFilter';

export interface DeviceItem {
  id: number;
  sku: string;
  category: string;
  brand: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  availableQty: number;
  listPrice: number;
}

export interface ColumnFilters {
  sku: string;
  category: string[];
  brand: string[];
  model: string[];
  carrier: string[];
  capacity: string[];
  color: string[];
  grade: string[];
  availableQty: string;
  price: string;
}

export interface CartEntry {
  offerPrice: number;
  cartQty: number;
}

type SortDir = 'asc' | 'desc';
type SortField = keyof DeviceItem | null;
type MultiSelectField = 'category' | 'brand' | 'model' | 'carrier' | 'capacity' | 'color' | 'grade';
type NumericFilter = 'sku' | 'availableQty' | 'price';

interface Props {
  pageRows: DeviceItem[];
  filters: ColumnFilters;
  distinctValues: Record<string, string[]>;
  sortField: SortField;
  sortDir: SortDir;
  cartItems: Record<string, CartEntry>;
  animatingSkus: Set<string>;
  page: number;
  totalPages: number;
  rangeStart: number;
  rangeEnd: number;
  totalRows: number;
  onSort: (field: keyof DeviceItem) => void;
  onFilterChange: (field: NumericFilter, value: string) => void;
  onMultiFilterChange: (field: MultiSelectField, value: string[]) => void;
  onCartChange: (device: DeviceItem, field: 'offerPrice' | 'cartQty', value: string) => void;
  onPageChange: (page: number) => void;
}

export function InventoryGrid({
  pageRows,
  filters,
  distinctValues,
  sortField,
  sortDir,
  cartItems,
  animatingSkus,
  page,
  totalPages,
  rangeStart,
  rangeEnd,
  totalRows,
  onSort,
  onFilterChange,
  onMultiFilterChange,
  onCartChange,
  onPageChange,
}: Props) {
  const sortIcon = (field: keyof DeviceItem) => (
    <span className={styles.sortIcon}>
      <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
        <path d="M5 0L9.33 5H0.67L5 0Z" fill={sortField === field && sortDir === 'asc' ? '#1a2e35' : '#bbb'} />
        <path d="M5 14L0.67 9H9.33L5 14Z" fill={sortField === field && sortDir === 'desc' ? '#1a2e35' : '#bbb'} />
      </svg>
    </span>
  );

  const calculateTotal = (sku: string) => {
    const item = cartItems[sku];
    if (!item) return 0;
    return item.offerPrice * item.cartQty;
  };

  return (
    <>
      <table className={styles.datagrid}>
        <thead>
          <tr>
            <th className={styles.colSku} onClick={() => onSort('sku')}>SKU {sortIcon('sku')}</th>
            <th onClick={() => onSort('category')}>Category {sortIcon('category')}</th>
            <th onClick={() => onSort('brand')}>Brand {sortIcon('brand')}</th>
            <th className={styles.colModel} onClick={() => onSort('model')}>Model Family {sortIcon('model')}</th>
            <th onClick={() => onSort('carrier')}>Carrier {sortIcon('carrier')}</th>
            <th onClick={() => onSort('capacity')}>Capacity {sortIcon('capacity')}</th>
            <th onClick={() => onSort('color')}>Color {sortIcon('color')}</th>
            <th onClick={() => onSort('grade')}>Grade {sortIcon('grade')}</th>
            <th onClick={() => onSort('availableQty')}>Avl. Qty {sortIcon('availableQty')}</th>
            <th onClick={() => onSort('listPrice')}>Price {sortIcon('listPrice')}</th>
            <th className={styles.colOfferPrice}>Offer Price</th>
            <th className={styles.colCartQty}>Cart Qty</th>
            <th className={styles.colTotal}>Total</th>
          </tr>
          <tr className={styles.filterRow}>
            <th><input className={styles.filterInput} type="text" value={filters.sku} onChange={e => onFilterChange('sku', e.target.value)} /></th>
            <th><MultiSelectFilter options={distinctValues.category || []} selected={filters.category} onChange={v => onMultiFilterChange('category', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.brand || []} selected={filters.brand} onChange={v => onMultiFilterChange('brand', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.model || []} selected={filters.model} onChange={v => onMultiFilterChange('model', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.carrier || []} selected={filters.carrier} onChange={v => onMultiFilterChange('carrier', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.capacity || []} selected={filters.capacity} onChange={v => onMultiFilterChange('capacity', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.color || []} selected={filters.color} onChange={v => onMultiFilterChange('color', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.grade || []} selected={filters.grade} onChange={v => onMultiFilterChange('grade', v)} /></th>
            <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.availableQty} onChange={e => onFilterChange('availableQty', e.target.value)} /></th>
            <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.price} onChange={e => onFilterChange('price', e.target.value)} /></th>
            <th></th>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {pageRows.map(device => {
            const cart = cartItems[device.sku];
            const hasQty = cart && cart.cartQty > 0;
            const hasPrice = cart && cart.offerPrice > 0;
            const isExceedQty = hasQty && device.availableQty <= 100 && cart.cartQty > device.availableQty;
            const isAnimating = animatingSkus.has(device.sku);

            let priceClass = '';
            if (hasPrice && hasQty && device.listPrice > 0) {
              priceClass = cart.offerPrice < device.listPrice
                ? styles.offerPriceOrange
                : styles.offerPriceGreen;
            }

            return (
              <tr key={device.sku} className={isAnimating ? styles.animatePulse : ''}>
                <td>{device.sku}</td>
                <td>{device.category}</td>
                <td>{device.brand}</td>
                <td>{device.model}</td>
                <td>{device.carrier}</td>
                <td>{device.capacity}</td>
                <td>{device.color}</td>
                <td>{device.grade}</td>
                <td>{device.availableQty > 100 ? '100+' : device.availableQty}</td>
                <td>${device.listPrice.toLocaleString()}</td>
                <td className={`${styles.userDataCell} ${priceClass}`}>
                  {hasQty ? (
                    <div className={styles.textDollar}>
                      <input
                        type="number"
                        className={styles.numberInput}
                        value={cart?.offerPrice || ''}
                        onChange={e => onCartChange(device, 'offerPrice', e.target.value)}
                        placeholder=""
                        min={0}
                        step="0.01"
                      />
                    </div>
                  ) : (
                    <span className={styles.offerPriceDefault}>${device.listPrice.toLocaleString()}</span>
                  )}
                </td>
                <td className={`${styles.userDataCell} ${hasQty ? styles.cartQtyActive : ''}`}>
                  <input
                    type="number"
                    className={`${styles.numberInput} ${isExceedQty ? styles.exceedQtyInput : ''}`}
                    value={cart?.cartQty || ''}
                    onChange={e => onCartChange(device, 'cartQty', e.target.value)}
                    placeholder=""
                    min={0}
                    step="1"
                    max={device.availableQty}
                  />
                  {isExceedQty && <div className={styles.exceedQtyHint}>Exceeds available</div>}
                </td>
                <td className={styles.colTotal}>{hasQty ? `$${calculateTotal(device.sku).toLocaleString()}` : ''}</td>
              </tr>
            );
          })}
        </tbody>
      </table>

      <div className={styles.pagination}>
        <button className={styles.pageBtn} disabled={page <= 1} onClick={() => onPageChange(1)} title="First">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="11 17 6 12 11 7"/><polyline points="18 17 13 12 18 7"/></svg>
        </button>
        <button className={styles.pageBtn} disabled={page <= 1} onClick={() => onPageChange(page - 1)} title="Previous">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6"/></svg>
        </button>
        <span className={styles.pageInfo}>{rangeStart} to {rangeEnd} of {totalRows}</span>
        <button className={styles.pageBtn} disabled={page >= totalPages} onClick={() => onPageChange(page + 1)} title="Next">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="9 18 15 12 9 6"/></svg>
        </button>
        <button className={styles.pageBtn} disabled={page >= totalPages} onClick={() => onPageChange(totalPages)} title="Last">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="13 17 18 12 13 7"/><polyline points="6 17 11 12 6 7"/></svg>
        </button>
      </div>
    </>
  );
}
