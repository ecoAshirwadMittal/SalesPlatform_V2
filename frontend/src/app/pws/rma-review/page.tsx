'use client';

import { useState, useEffect, useCallback, useMemo, useRef } from 'react';
import { useRouter } from 'next/navigation';
import styles from './rmaReview.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

const BASE = `${API_BASE}/pws/rma`;
const PAGE_SIZE = 20;

interface RmaSummary {
  status: string;
  displayLabel: string;
  rmaCount: number;
  totalPrice: number;
  totalSkus: number;
  totalQty: number;
}

interface RmaListItem {
  rmaId: number;
  number: string | null;
  systemStatus: string | null;
  internalStatusText: string | null;
  externalStatusText: string | null;
  statusGroupedTo: string | null;
  buyerName: string | null;
  companyName: string | null;
  requestSkus: number;
  requestQty: number;
  requestSalesTotal: number;
  submittedDate: string | null;
}

interface ColumnFilters {
  number: string;
  status: string;
  buyer: string;
  company: string;
  skus: string;
}

type SortField = 'number' | 'status' | 'submittedDate' | 'buyer' | 'company' | 'skus' | 'qty' | 'total';
type SortDir = 'asc' | 'desc';

const EMPTY_FILTERS: ColumnFilters = { number: '', status: '', buyer: '', company: '', skus: '' };

function formatDate(iso: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
}

function formatCurrency(val: number | null): string {
  if (val == null) return '$0';
  return '$' + val.toLocaleString('en-US');
}

function matchesText(value: string | null | undefined, filter: string): boolean {
  if (!filter) return true;
  return (value || '').toLowerCase().includes(filter.toLowerCase());
}

function getStatusClass(grouped: string | null): string {
  switch (grouped) {
    case 'Pending_Approval': return styles.statusPendingApproval;
    case 'Open': return styles.statusOpen;
    case 'Declined': return styles.statusDeclined;
    case 'Closed': return styles.statusClosed;
    default: return styles.statusDefault;
  }
}

export default function RmaReviewPage() {
  const router = useRouter();
  const [summaries, setSummaries] = useState<RmaSummary[]>([]);
  const [rmas, setRmas] = useState<RmaListItem[]>([]);
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
      const statusParam = status !== 'Total' ? `&status=${encodeURIComponent(status)}` : '';
      const [sumRes, listRes] = await Promise.all([
        apiFetch(`${BASE}/summary`),
        apiFetch(`${BASE}?${statusParam}`),
      ]);
      if (sumRes.ok) setSummaries(await sumRes.json());
      if (listRes.ok) setRmas(await listRes.json());
    } catch (err) {
      console.error('Failed to load RMAs:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadData(selectedStatus); }, [selectedStatus, loadData]);

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

  function sortIcon(field: SortField): string {
    if (sortField !== field) return '↕';
    return sortDir === 'asc' ? '↑' : '↓';
  }

  const filteredRmas = useMemo(() => {
    const filtered = rmas.filter(r =>
      matchesText(r.number, filters.number) &&
      matchesText(r.internalStatusText, filters.status) &&
      matchesText(r.buyerName, filters.buyer) &&
      matchesText(r.companyName, filters.company) &&
      matchesText(String(r.requestSkus), filters.skus)
    );

    if (sortField) {
      filtered.sort((a, b) => {
        let cmp = 0;
        switch (sortField) {
          case 'number': cmp = (a.number || '').localeCompare(b.number || ''); break;
          case 'status': cmp = (a.internalStatusText || '').localeCompare(b.internalStatusText || ''); break;
          case 'submittedDate': cmp = (a.submittedDate || '').localeCompare(b.submittedDate || ''); break;
          case 'buyer': cmp = (a.buyerName || '').localeCompare(b.buyerName || ''); break;
          case 'company': cmp = (a.companyName || '').localeCompare(b.companyName || ''); break;
          case 'skus': cmp = a.requestSkus - b.requestSkus; break;
          case 'qty': cmp = a.requestQty - b.requestQty; break;
          case 'total': cmp = a.requestSalesTotal - b.requestSalesTotal; break;
        }
        return sortDir === 'desc' ? -cmp : cmp;
      });
    }

    return filtered;
  }, [rmas, filters, sortField, sortDir]);

  const totalPages = Math.max(1, Math.ceil(filteredRmas.length / PAGE_SIZE));
  const pageRmas = filteredRmas.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

  return (
    <div className={styles.pageContainer}>
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>RMA Review</h1>

        <div className={styles.statusTabs}>
          {summaries.map(s => (
            <button key={s.status}
                    className={`${styles.statusTab} ${selectedStatus === s.status ? styles.statusTabActive : ''}`}
                    onClick={() => handleTabClick(s.status)}>
              <div className={styles.statusTabTitle}>
                <strong>{s.displayLabel}</strong>: {s.rmaCount}
              </div>
              <div className={styles.statusTabStats}>
                {formatCurrency(s.totalPrice)} | {s.totalSkus} SKUs | {s.totalQty} Qty
              </div>
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className={styles.loadingOverlay}>Loading...</div>
      ) : filteredRmas.length === 0 ? (
        <div className={styles.emptyState}>There are currently no open RMA&apos;s</div>
      ) : (
        <div className={styles.tableContainer}>
          <table className={styles.dataGrid}>
            <thead>
              <tr>
                <th onClick={() => handleSort('number')}>
                  RMA Number <span className={`${styles.sortIcon} ${sortField === 'number' ? styles.sortActive : ''}`}>{sortIcon('number')}</span>
                </th>
                <th onClick={() => handleSort('status')}>
                  RMA Status <span className={`${styles.sortIcon} ${sortField === 'status' ? styles.sortActive : ''}`}>{sortIcon('status')}</span>
                </th>
                <th onClick={() => handleSort('submittedDate')}>
                  Request Date <span className={`${styles.sortIcon} ${sortField === 'submittedDate' ? styles.sortActive : ''}`}>{sortIcon('submittedDate')}</span>
                </th>
                <th onClick={() => handleSort('buyer')}>
                  Buyer <span className={`${styles.sortIcon} ${sortField === 'buyer' ? styles.sortActive : ''}`}>{sortIcon('buyer')}</span>
                </th>
                <th onClick={() => handleSort('company')}>
                  Company <span className={`${styles.sortIcon} ${sortField === 'company' ? styles.sortActive : ''}`}>{sortIcon('company')}</span>
                </th>
                <th className={styles.alignCenter} onClick={() => handleSort('skus')}>
                  SKUs <span className={`${styles.sortIcon} ${sortField === 'skus' ? styles.sortActive : ''}`}>{sortIcon('skus')}</span>
                </th>
                <th className={styles.alignCenter} onClick={() => handleSort('qty')}>
                  Qty <span className={`${styles.sortIcon} ${sortField === 'qty' ? styles.sortActive : ''}`}>{sortIcon('qty')}</span>
                </th>
                <th className={styles.alignRight} onClick={() => handleSort('total')}>
                  Request Total <span className={`${styles.sortIcon} ${sortField === 'total' ? styles.sortActive : ''}`}>{sortIcon('total')}</span>
                </th>
              </tr>
              <tr>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('number', e.target.value)} /></th>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('status', e.target.value)} /></th>
                <th></th>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('buyer', e.target.value)} /></th>
                <th><input className={styles.columnFilter} placeholder="Filter..." onChange={e => handleFilterChange('company', e.target.value)} /></th>
                <th><input className={`${styles.columnFilter} ${styles.alignCenter}`} placeholder="Filter..." onChange={e => handleFilterChange('skus', e.target.value)} /></th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {pageRmas.map(r => (
                <tr key={r.rmaId} className={styles.clickableRow}
                    onClick={() => router.push(`/pws/rma-review/${r.rmaId}`)}>
                  <td>{r.number || r.rmaId}</td>
                  <td>
                    <span className={`${styles.statusBadge} ${getStatusClass(r.statusGroupedTo)}`}>
                      {r.internalStatusText || r.systemStatus || ''}
                    </span>
                  </td>
                  <td>{formatDate(r.submittedDate)}</td>
                  <td>{r.buyerName || ''}</td>
                  <td>{r.companyName || ''}</td>
                  <td className={styles.alignCenter}>{r.requestSkus}</td>
                  <td className={styles.alignCenter}>{r.requestQty}</td>
                  <td className={styles.alignRight}>{formatCurrency(r.requestSalesTotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className={styles.pagination}>
            <button className={styles.pageBtn} disabled={currentPage <= 1}
                    onClick={() => setCurrentPage(p => p - 1)}>&lt;</button>
            {Array.from({ length: totalPages }, (_, i) => i + 1)
              .slice(Math.max(0, currentPage - 3), Math.min(totalPages, currentPage + 2))
              .map(p => (
                <button key={p}
                        className={`${styles.pageBtn} ${p === currentPage ? styles.pageBtnActive : ''}`}
                        onClick={() => setCurrentPage(p)}>
                  {p}
                </button>
              ))}
            <button className={styles.pageBtn} disabled={currentPage >= totalPages}
                    onClick={() => setCurrentPage(p => p + 1)}>&gt;</button>
          </div>
        </div>
      )}
    </div>
  );
}
