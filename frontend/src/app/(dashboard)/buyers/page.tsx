'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import styles from './buyers.module.css';
import { apiFetch } from '@/lib/apiFetch';

interface BuyerRow {
  id: number;
  companyName: string;
  buyerCodesDisplay: string;
  status: string;
}

interface PageResponse {
  content: BuyerRow[];
  page: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

// Mendix parity: 500 ms debounce on the text-filter inputs
const FILTER_DELAY = 500;

export default function BuyersOverviewPage() {
  const router = useRouter();
  const [data, setData] = useState<PageResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const pageSize = 20;

  const [companyNameFilter, setCompanyNameFilter] = useState('');
  const [buyerCodesFilter, setBuyerCodesFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const [syncing, setSyncing] = useState(false);
  const [sortDir, setSortDir] = useState<'asc' | 'desc' | null>(null);

  const [debouncedFilters, setDebouncedFilters] = useState({
    companyName: '',
    buyerCodes: '',
    status: '',
  });

  const debounceTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    if (debounceTimer.current) clearTimeout(debounceTimer.current);
    debounceTimer.current = setTimeout(() => {
      setDebouncedFilters({
        companyName: companyNameFilter,
        buyerCodes: buyerCodesFilter,
        status: statusFilter,
      });
      setPage(0);
    }, FILTER_DELAY);
    return () => {
      if (debounceTimer.current) clearTimeout(debounceTimer.current);
    };
  }, [companyNameFilter, buyerCodesFilter, statusFilter]);

  const fetchBuyers = useCallback(async () => {
    setLoading(true);
    const params = new URLSearchParams();
    params.set('page', String(page));
    params.set('pageSize', String(pageSize));
    if (debouncedFilters.companyName) params.set('companyName', debouncedFilters.companyName);
    if (debouncedFilters.buyerCodes) params.set('buyerCodes', debouncedFilters.buyerCodes);
    if (debouncedFilters.status) params.set('status', debouncedFilters.status);

    try {
      const res = await apiFetch(`/api/v1/admin/buyers?${params}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = (await res.json()) as PageResponse;
      setData(json);
    } catch (err) {
      // Non-fatal: surface empty state. Errors bubble up through network layer logs.
      setData({ content: [], page: 0, pageSize, totalElements: 0, totalPages: 0 });
    } finally {
      setLoading(false);
    }
  }, [page, debouncedFilters]);

  useEffect(() => {
    fetchBuyers();
  }, [fetchBuyers]);

  const renderStatusIcon = (status: string) => {
    if (status === 'Active') {
      return (
        <span className={styles.statusIcon} title="Active">
          <svg width="20" height="20" viewBox="0 0 20 20">
            <circle cx="10" cy="10" r="9" fill="#14AC36" stroke="#0e8f2b" strokeWidth="1" />
            <path
              d="M6 10l3 3 5-5"
              stroke="#fff"
              strokeWidth="2"
              fill="none"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </span>
      );
    }
    // Disabled / null / empty — Mendix shows Disabled_Rollover (gray minus circle)
    return (
      <span className={styles.statusIcon} title="Disabled">
        <svg width="20" height="20" viewBox="0 0 20 20">
          <circle cx="10" cy="10" r="9" fill="#999" stroke="#888" strokeWidth="1" />
          <path d="M6 10h8" stroke="#fff" strokeWidth="2" strokeLinecap="round" />
        </svg>
      </span>
    );
  };

  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;
  const startItem = totalElements === 0 ? 0 : page * pageSize + 1;
  const endItem = Math.min((page + 1) * pageSize, totalElements);

  return (
    <div className={styles.pageContainer}>
      <div className={styles.pageHeader}>
        <h2 className={styles.pageTitle}>Buyer Management</h2>
        <div style={{ display: 'flex', gap: 8 }}>
          <button
            className={styles.createBtn}
            disabled={syncing}
            onClick={async () => {
              setSyncing(true);
              try {
                await apiFetch('/api/v1/admin/buyers/snowflake-sync', { method: 'POST' });
              } catch {
                // Sync is fire-and-forget; errors are non-fatal
              } finally {
                setSyncing(false);
              }
            }}
          >
            {syncing ? 'Syncing...' : 'Update Buyers to Snowflake'}
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <path d="M2 12h20M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
            </svg>
          </button>
          <button
            className={styles.createBtn}
            onClick={() => router.push('/buyers/new')}
          >
            Create
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <path d="M2 12h20M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
            </svg>
          </button>
        </div>
      </div>

      <div className={styles.gridWrapper}>
        <table className={styles.datagrid}>
          <colgroup>
            <col style={{ width: '34%' }} />
            <col style={{ width: '34%' }} />
            <col style={{ width: '24%' }} />
            <col style={{ width: '8%' }} />
          </colgroup>
          <thead>
            <tr>
              <th>
                <div className={styles.columnHeaderRow}>
                  <span className={styles.columnHeader}>Buyer Name</span>
                  <button
                    className={styles.sortBtn}
                    onClick={() => setSortDir(d => d === 'asc' ? 'desc' : 'asc')}
                    title="Sort"
                  >
                    <span className={styles.sortIcon}>
                      <span className={sortDir === 'asc' ? styles.sortActive : styles.sortArrow}>&#9650;</span>
                      <span className={sortDir === 'desc' ? styles.sortActive : styles.sortArrow}>&#9660;</span>
                    </span>
                  </button>
                </div>
                <input
                  type="text"
                  className={styles.filterInput}
                  placeholder="Ab"
                  value={companyNameFilter}
                  onChange={(e) => setCompanyNameFilter(e.target.value)}
                />
              </th>
              <th>
                <div className={styles.columnHeaderRow}>
                  <span className={styles.columnHeader}>Buyer Codes</span>
                </div>
                <input
                  type="text"
                  className={styles.filterInput}
                  placeholder="Ab"
                  value={buyerCodesFilter}
                  onChange={(e) => setBuyerCodesFilter(e.target.value)}
                />
              </th>
              <th>
                <div className={styles.columnHeaderRow}>
                  <span className={styles.columnHeader}>Status</span>
                </div>
                <div style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
                  <select
                    className={styles.filterSelect}
                    value={statusFilter}
                    onChange={(e) => {
                      setStatusFilter(e.target.value);
                      setPage(0);
                    }}
                  >
                    <option value="">Select</option>
                    <option value="Active">Active</option>
                    <option value="Disabled">Disabled</option>
                  </select>
                  <button className={styles.columnPickerBtn} title="Column selector" type="button">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  </button>
                </div>
              </th>
              <th className={styles.actionsHeader} />
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className={styles.loadingCell}>
                  Loading...
                </td>
              </tr>
            ) : data?.content.length === 0 ? (
              <tr>
                <td colSpan={4} className={styles.loadingCell}>
                  No buyers found
                </td>
              </tr>
            ) : (
              data?.content.map((buyer) => (
                <tr key={buyer.id} onClick={() => router.push(`/buyers/${buyer.id}`)}>
                  <td>
                    <div className={styles.nameCell}>
                      <span className={styles.avatar}>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <path d="M3 21h18M5 21V7l7-4 7 4v14M9 9h1M9 13h1M9 17h1M14 9h1M14 13h1M14 17h1" />
                        </svg>
                      </span>
                      <span>{buyer.companyName}</span>
                    </div>
                  </td>
                  <td>{buyer.buyerCodesDisplay}</td>
                  <td className={styles.statusCell}>{renderStatusIcon(buyer.status)}</td>
                  <td className={styles.actionsCell}>
                    <button
                      className={styles.editBtn}
                      onClick={(e) => { e.stopPropagation(); router.push(`/buyers/${buyer.id}`); }}
                      title="Edit"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                      </svg>
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 0 && (
        <div className={styles.pagination}>
          <button
            className={styles.pageBtn}
            disabled={page === 0}
            onClick={() => setPage(0)}
            title="Go to first page"
          >
            &laquo;
          </button>
          <button
            className={styles.pageBtn}
            disabled={page === 0}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            title="Go to previous page"
          >
            &lsaquo;
          </button>
          <span className={styles.pageInfo}>
            Currently showing {startItem} to {endItem} of {totalElements}
          </span>
          <button
            className={styles.pageBtn}
            disabled={page >= totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
            title="Go to next page"
          >
            &rsaquo;
          </button>
          <button
            className={styles.pageBtn}
            disabled={page >= totalPages - 1}
            onClick={() => setPage(totalPages - 1)}
            title="Go to last page"
          >
            &raquo;
          </button>
        </div>
      )}
    </div>
  );
}
