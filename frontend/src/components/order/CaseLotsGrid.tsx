'use client';

import styles from '@/app/pws/order/pwsOrder.module.css';
import MultiSelectFilter from '@/app/pws/order/MultiSelectFilter';

export interface CaseLotItem {
  id: number;
  deviceId: number;
  sku: string;
  model: string;
  carrier: string;
  capacity: string;
  color: string;
  grade: string;
  caseLotSize: number;
  caseLotAtpQty: number;
  unitPrice: number;
  caseLotPrice: number;
}

export interface CLColumnFilters {
  sku: string;
  model: string[];
  carrier: string[];
  capacity: string[];
  color: string[];
  grade: string[];
  caseLotSize: string;
  caseLotAtpQty: string;
  unitPrice: string;
  caseLotPrice: string;
}

export interface CLCartEntry {
  numCases: number;
  unitOffer: number;
}

type SortDir = 'asc' | 'desc';
type CLSortField = keyof CaseLotItem | null;
type CLNumericFilter = 'sku' | 'caseLotSize' | 'caseLotAtpQty' | 'unitPrice' | 'caseLotPrice';
type CLMultiField = 'model' | 'carrier' | 'capacity' | 'color' | 'grade';

interface Props {
  pageRows: CaseLotItem[];
  filters: CLColumnFilters;
  distinctValues: Record<string, string[]>;
  sortField: CLSortField;
  sortDir: SortDir;
  cartItems: Record<number, CLCartEntry>;
  animatingSkus: Set<string>;
  page: number;
  totalPages: number;
  rangeStart: number;
  rangeEnd: number;
  totalRows: number;
  onSort: (field: keyof CaseLotItem) => void;
  onFilterChange: (field: CLNumericFilter, value: string) => void;
  onMultiFilterChange: (field: CLMultiField, value: string[]) => void;
  onCartChange: (cl: CaseLotItem, field: 'numCases' | 'unitOffer', value: string) => void;
  onPageChange: (page: number) => void;
}

export function CaseLotsGrid({
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
  const sortIcon = (field: keyof CaseLotItem) => (
    <span className={styles.sortIcon}>
      <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
        <path d="M5 0L9.33 5H0.67L5 0Z" fill={sortField === field && sortDir === 'asc' ? '#1a2e35' : '#bbb'} />
        <path d="M5 14L0.67 9H9.33L5 14Z" fill={sortField === field && sortDir === 'desc' ? '#1a2e35' : '#bbb'} />
      </svg>
    </span>
  );

  return (
    <>
      <div className={styles.caseLotDisclaimer}>
        Case lots are sold AS IS. All sales are final.
      </div>
      <table className={styles.datagrid}>
        <thead>
          <tr>
            <th className={styles.colSku} onClick={() => onSort('sku')}>SKU {sortIcon('sku')}</th>
            <th className={styles.colModel} onClick={() => onSort('model')}>Model Family {sortIcon('model')}</th>
            <th onClick={() => onSort('carrier')}>Carrier {sortIcon('carrier')}</th>
            <th onClick={() => onSort('capacity')}>Capacity {sortIcon('capacity')}</th>
            <th onClick={() => onSort('color')}>Color {sortIcon('color')}</th>
            <th onClick={() => onSort('grade')}>Grade {sortIcon('grade')}</th>
            <th onClick={() => onSort('caseLotSize')}>Case Pack Qty {sortIcon('caseLotSize')}</th>
            <th onClick={() => onSort('caseLotAtpQty')}>Avl. Cases {sortIcon('caseLotAtpQty')}</th>
            <th onClick={() => onSort('unitPrice')}>Unit Price {sortIcon('unitPrice')}</th>
            <th onClick={() => onSort('caseLotPrice')}>Case Price {sortIcon('caseLotPrice')}</th>
            <th>No. Cases</th>
            <th>Unit Offer</th>
            <th>Case Offer</th>
            <th>Total</th>
          </tr>
          <tr className={styles.filterRow}>
            <th><input className={styles.filterInput} type="text" value={filters.sku} onChange={e => onFilterChange('sku', e.target.value)} /></th>
            <th><MultiSelectFilter options={distinctValues.model || []} selected={filters.model} onChange={v => onMultiFilterChange('model', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.carrier || []} selected={filters.carrier} onChange={v => onMultiFilterChange('carrier', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.capacity || []} selected={filters.capacity} onChange={v => onMultiFilterChange('capacity', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.color || []} selected={filters.color} onChange={v => onMultiFilterChange('color', v)} /></th>
            <th><MultiSelectFilter options={distinctValues.grade || []} selected={filters.grade} onChange={v => onMultiFilterChange('grade', v)} /></th>
            <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.caseLotSize} onChange={e => onFilterChange('caseLotSize', e.target.value)} /></th>
            <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.caseLotAtpQty} onChange={e => onFilterChange('caseLotAtpQty', e.target.value)} /></th>
            <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.unitPrice} onChange={e => onFilterChange('unitPrice', e.target.value)} /></th>
            <th><input className={styles.filterInputNumber} type="number" placeholder="=" value={filters.caseLotPrice} onChange={e => onFilterChange('caseLotPrice', e.target.value)} /></th>
            <th></th>
            <th></th>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {pageRows.map(cl => {
            const entry = cartItems[cl.id];
            const caseOffer = entry ? entry.unitOffer * cl.caseLotSize : 0;
            const totalOffer = entry ? entry.unitOffer * cl.caseLotSize * entry.numCases : 0;
            const hasNumCases = entry && entry.numCases > 0;
            const hasUnitOffer = entry && entry.unitOffer > 0;
            const isExceedCL = hasNumCases && cl.caseLotAtpQty <= 100 && entry.numCases > cl.caseLotAtpQty;
            const isAnimatingCL = animatingSkus.has(`cl-${cl.id}`);

            let clPriceClass = '';
            if (hasUnitOffer && hasNumCases && cl.unitPrice > 0) {
              clPriceClass = entry.unitOffer < cl.unitPrice
                ? styles.offerPriceOrange
                : styles.offerPriceGreen;
            }

            return (
              <tr key={cl.id} className={isAnimatingCL ? styles.animatePulse : ''}>
                <td>{cl.sku}</td>
                <td>{cl.model}</td>
                <td>{cl.carrier}</td>
                <td>{cl.capacity}</td>
                <td>{cl.color}</td>
                <td>{cl.grade}</td>
                <td>{cl.caseLotSize}</td>
                <td>{cl.caseLotAtpQty}</td>
                <td>${cl.unitPrice.toLocaleString()}</td>
                <td>${cl.caseLotPrice.toLocaleString()}</td>
                <td className={`${styles.userDataCell} ${hasNumCases ? styles.cartQtyActive : ''}`}>
                  <input
                    type="number"
                    className={`${styles.numberInput} ${isExceedCL ? styles.exceedQtyInput : ''}`}
                    value={entry?.numCases || ''}
                    onChange={e => onCartChange(cl, 'numCases', e.target.value)}
                    placeholder=""
                    min={0}
                    step="1"
                    max={cl.caseLotAtpQty}
                  />
                  {isExceedCL && <div className={styles.exceedQtyHint}>Exceeds available</div>}
                </td>
                <td className={`${styles.userDataCell} ${clPriceClass}`}>
                  {hasNumCases ? (
                    <div className={styles.textDollar}>
                      <input
                        type="number"
                        className={styles.numberInput}
                        value={entry?.unitOffer || ''}
                        onChange={e => onCartChange(cl, 'unitOffer', e.target.value)}
                        placeholder=""
                        min={0}
                        step="0.01"
                      />
                    </div>
                  ) : (
                    <span className={styles.offerPriceDefault}>${cl.unitPrice.toLocaleString()}</span>
                  )}
                </td>
                <td className={styles.userDataCell}>{hasNumCases ? `$${caseOffer.toLocaleString()}` : ''}</td>
                <td className={`${styles.userDataCell} ${styles.colTotal}`}>{hasNumCases ? `$${totalOffer.toLocaleString()}` : ''}</td>
              </tr>
            );
          })}
          {pageRows.length === 0 && (
            <tr><td colSpan={14} style={{ textAlign: 'center', padding: '40px', fontWeight: 500 }}>There are currently no case lots to purchase</td></tr>
          )}
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
