'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import s from './orders.module.css';
import { apiFetch } from '@/lib/apiFetch';
import {
  formatDate,
  formatCurrency,
  formatStatus,
  statusClassKey,
  sortOrders,
  paginationLabel,
  parseUserId,
  type OrderHistoryResponse,
  type SortField,
  type SortDir,
} from './orders-helpers';

const API_BASE = '/api/v1/pws';
const PAGE_SIZE = 20;

// ── Types local to the component ──

interface OrderHistoryTabCounts {
  recent: number;
  inProcess: number;
  complete: number;
  all: number;
}

interface PageResponse {
  content: OrderHistoryResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

type TabKey = 'recent' | 'inProcess' | 'complete' | 'all';

const TABS: { key: TabKey; label: string }[] = [
  { key: 'all', label: 'All' },
  { key: 'recent', label: 'Recent' },
  { key: 'inProcess', label: 'In Process' },
  { key: 'complete', label: 'Complete' },
];

// ── Main Component ──

export default function OrderHistoryPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState<TabKey>('all');
  const [tabCounts, setTabCounts] = useState<OrderHistoryTabCounts | null>(null);
  const [orders, setOrders] = useState<OrderHistoryResponse[]>([]);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [sortField, setSortField] = useState<SortField | null>(null);
  const [sortDir, setSortDir] = useState<SortDir>('asc');

  // Read userId from localStorage (set by login flow)
  const getUserId = useCallback((): number | null => {
    if (typeof window === 'undefined') return null;
    return parseUserId(localStorage.getItem('auth_user'));
  }, []);

  // Read selected buyer code from sessionStorage (set by PWS layout)
  const getBuyerCodeId = useCallback((): number | null => {
    if (typeof window === 'undefined') return null;
    const stored = sessionStorage.getItem('selectedBuyerCode');
    if (!stored) return null;
    try {
      const parsed = JSON.parse(stored);
      return parsed?.id ?? null;
    } catch { return null; }
  }, []);

  // ── Fetch orders for current tab + page ──
  const fetchOrders = useCallback(async (tab: TabKey, pg: number) => {
    const userId = getUserId();
    if (!userId) return;
    setLoading(true);
    try {
      const params = new URLSearchParams({
        tab,
        userId: String(userId),
        page: String(pg),
        size: String(PAGE_SIZE),
      });
      const buyerCodeId = getBuyerCodeId();
      if (buyerCodeId) {
        params.set('buyerCodeId', String(buyerCodeId));
      }
      const res = await apiFetch(`${API_BASE}/orders?${params.toString()}`);
      if (res.ok) {
        const json: PageResponse = await res.json();
        setOrders(json.content);
        setTotalElements(json.totalElements);
        setTotalPages(json.totalPages);
      }
    } catch (err) {
      console.error('Failed to load orders:', err);
    } finally {
      setLoading(false);
    }
  }, [getUserId]);

  // ── Fetch tab counts ──
  const fetchCounts = useCallback(async () => {
    const userId = getUserId();
    if (!userId) return;
    try {
      const params = new URLSearchParams({ userId: String(userId) });
      const buyerCodeId = getBuyerCodeId();
      if (buyerCodeId) {
        params.set('buyerCodeId', String(buyerCodeId));
      }
      const res = await apiFetch(`${API_BASE}/orders/counts?${params.toString()}`);
      if (res.ok) {
        setTabCounts(await res.json());
      }
    } catch (err) {
      console.error('Failed to load tab counts:', err);
    }
  }, [getUserId]);

  // On mount: fetch counts + orders in parallel
  useEffect(() => {
    fetchCounts();
    fetchOrders(activeTab, page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Re-fetch when buyer code changes (user selects a different code in the top bar)
  useEffect(() => {
    function handleBuyerCodeChanged() {
      setPage(0);
      fetchOrders(activeTab, 0);
      fetchCounts();
    }
    window.addEventListener('buyerCodeChanged', handleBuyerCodeChanged);
    return () => window.removeEventListener('buyerCodeChanged', handleBuyerCodeChanged);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab, fetchOrders, fetchCounts]);

  // On tab change: reset page, fetch new data
  const handleTabClick = useCallback((tab: TabKey) => {
    setActiveTab(tab);
    setPage(0);
    setSortField(null);
    setSortDir('asc');
    fetchOrders(tab, 0);
    fetchCounts();
  }, [fetchOrders, fetchCounts]);

  // On page change
  useEffect(() => {
    fetchOrders(activeTab, page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  // ── Sort handler (client-side on current page) ──
  const handleSort = useCallback((field: SortField) => {
    if (sortField === field) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortDir('asc');
    }
  }, [sortField]);

  const sorted = sortOrders(orders, sortField, sortDir);

  // ── Row click → Order Detail page ──
  const handleRowClick = useCallback((offerId: number) => {
    router.push(`/pws/orders/${offerId}`);
  }, [router]);

  // ── Sort icon ──
  function sortIcon(field: SortField) {
    const isActive = sortField === field;
    const char = isActive ? (sortDir === 'asc' ? '\u25B2' : '\u25BC') : '\u21C5';
    return (
      <span className={`${s.sortIcon} ${isActive ? s.sortIconActive : ''}`}>
        {char}
      </span>
    );
  }

  // ── Column config ──
  const columns: { key: SortField; header: string; align?: string; render: (row: OrderHistoryResponse) => React.ReactNode }[] = [
    { key: 'orderNumber', header: 'Order Number', render: (r) => r.orderNumber || '---' },
    { key: 'offerDate', header: 'Offer Date', render: (r) => formatDate(r.offerDate) || '---' },
    { key: 'orderDate', header: 'Order Date', render: (r) => formatDate(r.orderDate) || '---' },
    { key: 'buyer', header: 'Buyer', render: (r) => r.buyer || '---' },
    { key: 'company', header: 'Company', render: (r) => r.company || '---' },
    {
      key: 'orderStatus', header: 'Order Status',
      render: (r) => (
        <span className={`${s.statusBadge} ${s[statusClassKey(r.orderStatus)]}`}>
          {formatStatus(r.orderStatus)}
        </span>
      ),
    },
    { key: 'shipDate', header: 'Ship Date', render: (r) => formatDate(r.shipDate) || '---' },
    { key: 'shipMethod', header: 'Ship Method', render: (r) => r.shipMethod || '---' },
    { key: 'skuCount', header: 'SKUs', align: 'center', render: (r) => r.skuCount },
    { key: 'totalQuantity', header: 'Qty', align: 'center', render: (r) => r.totalQuantity },
    { key: 'totalPrice', header: 'Total Price', align: 'right', render: (r) => formatCurrency(r.totalPrice) },
  ];

  const pageLabel = paginationLabel(page, PAGE_SIZE, totalElements);

  return (
    <div className={s.pageContainer}>
      {/* ── Header ── */}
      <div className={s.pageHeader}>
        <h1 className={s.pageTitle}>Order History</h1>
        <div className={s.headerActions}>
          {/* Export button placeholder for Phase 4 */}
        </div>
      </div>

      {/* ── Tab bar ── */}
      <div className={s.tabBar}>
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`${s.tab} ${activeTab === tab.key ? s.tabActive : ''}`}
            onClick={() => handleTabClick(tab.key)}
          >
            {tab.label}
            <span className={s.tabCount}>
              ({tabCounts ? tabCounts[tab.key] : '...'})
            </span>
          </button>
        ))}
      </div>

      {/* ── Grid ── */}
      {loading && orders.length === 0 ? (
        <div className={s.loadingState}>Loading orders...</div>
      ) : (
        <div className={s.gridWrapper}>
          <table className={s.dataGrid}>
            <thead>
              <tr>
                {columns.map((col) => (
                  <th
                    key={col.key}
                    className={col.align === 'center' ? s.colCenter : col.align === 'right' ? s.colRight : undefined}
                    onClick={() => handleSort(col.key)}
                  >
                    <div className={s.sortableHeader}>
                      {col.header}
                      {sortIcon(col.key)}
                    </div>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {sorted.length === 0 ? (
                <tr>
                  <td colSpan={columns.length} className={s.emptyState}>
                    There are currently no orders in this stage.
                  </td>
                </tr>
              ) : (
                sorted.map((row) => (
                  <tr key={row.id} onClick={() => handleRowClick(row.offerId)}>
                    {columns.map((col) => (
                      <td
                        key={col.key}
                        className={col.align === 'center' ? s.colCenter : col.align === 'right' ? s.colRight : undefined}
                      >
                        {col.render(row)}
                      </td>
                    ))}
                  </tr>
                ))
              )}
            </tbody>
          </table>

          {/* ── Pagination ── */}
          <div className={s.pagination}>
            <span className={s.pageInfo}>{pageLabel}</span>
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
  );
}
