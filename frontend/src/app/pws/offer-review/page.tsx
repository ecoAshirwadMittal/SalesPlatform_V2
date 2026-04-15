'use client';

import { useState, useEffect, useCallback, useMemo, useRef } from 'react';
import { useRouter } from 'next/navigation';
import styles from './offerReview.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

const BASE = `${API_BASE}/pws/offer-review`;
const PAGE_SIZE = 50;

interface OfferSummary {
  status: string;
  displayLabel: string;
  offerCount: number;
  totalSkus: number;
  totalQty: number;
  totalPrice: number;
}

interface OfferListItem {
  offerId: number;
  offerNumber: string | null;
  status: string;
  orderNumber: string | null;
  buyerName: string | null;
  buyerCode: string | null;
  salesRepName: string | null;
  totalSkus: number;
  totalQty: number;
  totalPrice: number;
  submissionDate: string | null;
  updatedDate: string | null;
}

interface ColumnFilters {
  offerId: string;
  orderNumber: string;
  status: string;
  buyerName: string;
  buyerCode: string;
  salesRep: string;
  skus: string;
  qty: string;
  price: string;
  offerDate: string;
  lastUpdated: string;
}

type SortField = 'offerId' | 'orderNumber' | 'status' | 'buyerName' | 'buyerCode' | 'salesRep' | 'skus' | 'qty' | 'price' | 'offerDate' | 'lastUpdated';
type SortDir = 'asc' | 'desc';

const EMPTY_FILTERS: ColumnFilters = {
  offerId: '', orderNumber: '', status: '', buyerName: '', buyerCode: '',
  salesRep: '', skus: '', qty: '', price: '', offerDate: '', lastUpdated: '',
};

const STATUS_CLASSES: Record<string, string> = {
  Sales_Review: styles.tabSalesReview,
  SALES_REVIEW: styles.tabSalesReview,
  Buyer_Acceptance: styles.tabBuyerAcceptance,
  BUYER_ACCEPTANCE: styles.tabBuyerAcceptance,
  Ordered: styles.tabOrdered,
  ORDERED: styles.tabOrdered,
  Pending_Order: styles.tabPendingOrder,
  PENDING_ORDER: styles.tabPendingOrder,
  Declined: styles.tabDeclined,
  DECLINED: styles.tabDeclined,
  Total: styles.tabTotal,
};

function formatDate(iso: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
}

function formatStatus(status: string | null): string {
  if (!status) return '';
  return status.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase()).replace(/\B\w+/g, (w) => w.toLowerCase());
}

function formatCurrency(val: number | null): string {
  if (val == null) return '$0.00';
  return '$' + val.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatCurrencyShort(val: number | null): string {
  if (val == null) return '$0';
  return '$' + val.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

function matchesText(value: string | null | undefined, filter: string): boolean {
  if (!filter) return true;
  return (value || '').toLowerCase().includes(filter.toLowerCase());
}

function matchesNumber(value: number | null | undefined, filter: string): boolean {
  if (!filter) return true;
  if (value == null) return false;
  return String(value).includes(filter);
}

function matchesDate(iso: string | null | undefined, filter: string): boolean {
  if (!filter) return true;
  if (!iso) return false;
  const formatted = formatDate(iso);
  return formatted.toLowerCase().includes(filter.toLowerCase());
}

function downloadCsv(offers: OfferListItem[]): void {
  const headers = ['Offer ID', 'Order Number', 'Offer Status', 'Buyer Name', 'Buyer Code',
    'Sales Rep', 'SKUs', 'Qty', 'Offer Price', 'Offer Date', 'Last Updated'];
  const rows = offers.map(o => [
    o.offerNumber || o.offerId,
    o.orderNumber || '',
    formatStatus(o.status),
    o.buyerName || '',
    o.buyerCode || '',
    o.salesRepName || '',
    o.totalSkus,
    o.totalQty,
    o.totalPrice ?? 0,
    formatDate(o.submissionDate),
    formatDate(o.updatedDate),
  ]);
  const csv = [headers.join(','), ...rows.map(r => r.map(v => `"${v}"`).join(','))].join('\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `offers-export-${new Date().toISOString().slice(0, 10)}.csv`;
  a.click();
  URL.revokeObjectURL(url);
}

export default function OfferReviewPage() {
  const router = useRouter();
  const [summaries, setSummaries] = useState<OfferSummary[]>([]);
  const [offers, setOffers] = useState<OfferListItem[]>([]);
  const [selectedStatus, setSelectedStatus] = useState<string>('Total');
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<ColumnFilters>(EMPTY_FILTERS);
  const [currentPage, setCurrentPage] = useState(1);
  const [sortField, setSortField] = useState<SortField | null>(null);
  const [sortDir, setSortDir] = useState<SortDir>('asc');
  const filterTimers = useRef<Record<string, ReturnType<typeof setTimeout>>>({});

  const loadData = useCallback(async (status: string) => {
    setLoading(true);
    try {
      const [sumRes, listRes] = await Promise.all([
        apiFetch(`${BASE}/summary`),
        apiFetch(`${BASE}?status=${encodeURIComponent(status)}`),
      ]);
      if (sumRes.ok) setSummaries(await sumRes.json());
      if (listRes.ok) setOffers(await listRes.json());
    } catch (err) {
      console.error('Failed to load offers:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData(selectedStatus);
  }, [selectedStatus, loadData]);

  function handleTabClick(status: string) {
    setSelectedStatus(status);
    setFilters(EMPTY_FILTERS);
    setCurrentPage(1);
    setSortField(null);
    setSortDir('asc');
  }

  function handleSort(field: SortField) {
    if (sortField === field) {
      setSortDir(prev => prev === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDir('asc');
    }
    setCurrentPage(1);
  }

  function handleFilterChange(field: keyof ColumnFilters, value: string) {
    if (filterTimers.current[field]) clearTimeout(filterTimers.current[field]);
    filterTimers.current[field] = setTimeout(() => {
      setFilters(prev => ({ ...prev, [field]: value }));
      setCurrentPage(1);
    }, 300);
  }

  const filteredOffers = useMemo(() => {
    const filtered = offers.filter(o =>
      matchesText(o.offerNumber || String(o.offerId), filters.offerId) &&
      matchesText(o.orderNumber, filters.orderNumber) &&
      matchesText(formatStatus(o.status), filters.status) &&
      matchesText(o.buyerName, filters.buyerName) &&
      matchesText(o.buyerCode, filters.buyerCode) &&
      matchesText(o.salesRepName, filters.salesRep) &&
      matchesNumber(o.totalSkus, filters.skus) &&
      matchesNumber(o.totalQty, filters.qty) &&
      matchesNumber(o.totalPrice, filters.price) &&
      matchesDate(o.submissionDate, filters.offerDate) &&
      matchesDate(o.updatedDate, filters.lastUpdated)
    );
    if (!sortField) return filtered;

    const getValue = (o: OfferListItem): string | number | null => {
      switch (sortField) {
        case 'offerId': return o.offerNumber || String(o.offerId);
        case 'orderNumber': return o.orderNumber;
        case 'status': return o.status;
        case 'buyerName': return o.buyerName;
        case 'buyerCode': return o.buyerCode;
        case 'salesRep': return o.salesRepName;
        case 'skus': return o.totalSkus;
        case 'qty': return o.totalQty;
        case 'price': return o.totalPrice;
        case 'offerDate': return o.submissionDate;
        case 'lastUpdated': return o.updatedDate;
        default: return null;
      }
    };

    return [...filtered].sort((a, b) => {
      const va = getValue(a);
      const vb = getValue(b);
      if (va == null && vb == null) return 0;
      if (va == null) return 1;
      if (vb == null) return -1;
      const cmp = typeof va === 'number' && typeof vb === 'number'
        ? va - vb
        : String(va).localeCompare(String(vb));
      return sortDir === 'asc' ? cmp : -cmp;
    });
  }, [offers, filters, sortField, sortDir]);

  const totalPages = Math.max(1, Math.ceil(filteredOffers.length / PAGE_SIZE));
  const pageStart = (currentPage - 1) * PAGE_SIZE;
  const pageEnd = Math.min(pageStart + PAGE_SIZE, filteredOffers.length);
  const pagedOffers = filteredOffers.slice(pageStart, pageEnd);

  return (
    <div className={styles.pageContainer}>
      {/* Page heading + status summary tabs */}
      <div className={styles.pageHeader}>
        <h2 className={styles.pageTitle}>Offers</h2>
        <div className={styles.statusTabs}>
          {summaries.map((s) => (
            <button
              key={s.status}
              className={`${styles.statusTab} ${STATUS_CLASSES[s.status] || ''} ${
                selectedStatus === s.status ? styles.statusTabActive : ''
              }`}
              onClick={() => handleTabClick(s.status)}
            >
              <div className={styles.statusTabTitle}>
                {s.displayLabel}: <strong>{s.offerCount}</strong>
              </div>
              <div className={styles.statusTabStats}>
                <table className={styles.statusTabTable}>
                  <thead><tr><td>SKUs</td><td>Qty</td><td>Price</td></tr></thead>
                  <tbody><tr>
                    <td>{s.totalSkus.toLocaleString()}</td>
                    <td>{s.totalQty.toLocaleString()}</td>
                    <td>{formatCurrency(s.totalPrice)}</td>
                  </tr></tbody>
                </table>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Download button */}
      <div className={styles.downloadRow}>
        <button className={styles.downloadButton} onClick={() => downloadCsv(filteredOffers)}>
          &#8681; Download
        </button>
      </div>

      {/* Offers data grid */}
      {loading ? (
        <div className={styles.loading}>Loading offers...</div>
      ) : offers.length === 0 ? (
        <div className={styles.emptyState}>No offers found for this status.</div>
      ) : (
        <>
          <table className={styles.dataGrid}>
            <thead>
              <tr>
                {([
                  ['Offer ID', 'offerId'],
                  ['Order Number', 'orderNumber'],
                  ['Offer Status', 'status'],
                  ['Buyer Name', 'buyerName'],
                  ['Buyer Code', 'buyerCode'],
                  ['Sales Rep', 'salesRep'],
                  ['SKUs', 'skus'],
                  ['Qty', 'qty'],
                  ['Offer Price', 'price'],
                  ['Offer Date', 'offerDate'],
                  ['Last Updated', 'lastUpdated'],
                ] as [string, SortField][]).map(([label, field]) => (
                  <th key={field}>
                    <div className={styles.sortableHeader} onClick={() => handleSort(field)}>
                      {label}
                      <span className={`${styles.sortIcon} ${sortField === field ? styles.sortIconActive : ''}`}>
                        {sortField === field ? (sortDir === 'asc' ? '\u25B2' : '\u25BC') : '\u21C5'}
                      </span>
                    </div>
                  </th>
                ))}
              </tr>
              <tr className={styles.filterRow}>
                <td><input className={styles.filterInput} placeholder="" defaultValue="" onChange={e => handleFilterChange('offerId', e.target.value)} /></td>
                <td><input className={styles.filterInput} placeholder="" defaultValue="" onChange={e => handleFilterChange('orderNumber', e.target.value)} /></td>
                <td><input className={styles.filterInput} placeholder="" defaultValue="" onChange={e => handleFilterChange('status', e.target.value)} /></td>
                <td><input className={styles.filterInput} placeholder="" defaultValue="" onChange={e => handleFilterChange('buyerName', e.target.value)} /></td>
                <td><input className={styles.filterInput} placeholder="" defaultValue="" onChange={e => handleFilterChange('buyerCode', e.target.value)} /></td>
                <td><input className={styles.filterInput} placeholder="" defaultValue="" onChange={e => handleFilterChange('salesRep', e.target.value)} /></td>
                <td><input className={styles.filterInputNumeric} placeholder="=" defaultValue="" onChange={e => handleFilterChange('skus', e.target.value)} /></td>
                <td><input className={styles.filterInputNumeric} placeholder="=" defaultValue="" onChange={e => handleFilterChange('qty', e.target.value)} /></td>
                <td><input className={styles.filterInputNumeric} placeholder="=" defaultValue="" onChange={e => handleFilterChange('price', e.target.value)} /></td>
                <td><input className={styles.filterInputDate} placeholder="&#128197;" defaultValue="" onChange={e => handleFilterChange('offerDate', e.target.value)} /></td>
                <td><input className={styles.filterInputDate} placeholder="&#128197;" defaultValue="" onChange={e => handleFilterChange('lastUpdated', e.target.value)} /></td>
              </tr>
            </thead>
            <tbody>
              {pagedOffers.map((offer) => (
                <tr key={offer.offerId}>
                  <td>
                    <span
                      className={styles.offerIdLink}
                      onClick={() => router.push(`/pws/offer-review/${offer.offerId}`)}
                    >
                      {offer.offerNumber || offer.offerId}
                    </span>
                  </td>
                  <td>{offer.orderNumber || ''}</td>
                  <td>{formatStatus(offer.status)}</td>
                  <td>{offer.buyerName || ''}</td>
                  <td>{offer.buyerCode || ''}</td>
                  <td>{offer.salesRepName || ''}</td>
                  <td>{offer.totalSkus}</td>
                  <td>{offer.totalQty}</td>
                  <td className={styles.priceCell}>{formatCurrencyShort(offer.totalPrice)}</td>
                  <td>{formatDate(offer.submissionDate)}</td>
                  <td>{formatDate(offer.updatedDate)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Pagination */}
          <div className={styles.pagination}>
            <button className={styles.paginationBtn} disabled={currentPage <= 1}
              onClick={() => setCurrentPage(1)}>&#9664;&#9664;</button>
            <button className={styles.paginationBtn} disabled={currentPage <= 1}
              onClick={() => setCurrentPage(p => p - 1)}>&#9664;</button>
            <span className={styles.paginationInfo}>
              {pageStart + 1} to {pageEnd} of {filteredOffers.length}
            </span>
            <button className={styles.paginationBtn} disabled={currentPage >= totalPages}
              onClick={() => setCurrentPage(p => p + 1)}>&#9654;</button>
            <button className={styles.paginationBtn} disabled={currentPage >= totalPages}
              onClick={() => setCurrentPage(totalPages)}>&#9654;&#9654;</button>
          </div>
        </>
      )}
    </div>
  );
}
