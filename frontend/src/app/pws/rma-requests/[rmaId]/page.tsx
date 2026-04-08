'use client';

import { useState, useEffect, useCallback, useMemo } from 'react';
import { useRouter, useParams } from 'next/navigation';
import styles from './rmaDetails.module.css';
import { apiFetch } from '@/lib/apiFetch';

const API_BASE = '/api/v1/pws/rma';
const PAGE_SIZE = 20;

interface RmaInfo {
  rmaId: number;
  number: string | null;
  systemStatus: string | null;
  internalStatusText: string | null;
  externalStatusText: string | null;
  statusGroupedTo: string | null;
  requestSkus: number;
  requestQty: number;
  requestSalesTotal: number;
  approvedCount: number;
  declinedCount: number;
  submittedDate: string | null;
  approvalDate: string | null;
}

interface RmaItemData {
  id: number;
  imei: string | null;
  orderNumber: string | null;
  shipDate: string | null;
  salePrice: number | null;
  returnReason: string | null;
  status: string | null;
  statusDisplay: string | null;
  sku: string | null;
  deviceDescription: string | null;
  grade: string | null;
  itemType: string | null;
}

interface RmaDetail {
  rma: RmaInfo;
  items: RmaItemData[];
}

function formatDate(iso: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
}

function formatCurrency(val: number | null): string {
  if (val == null) return '$0';
  return '$' + val.toLocaleString('en-US');
}

export default function RmaDetailsBuyerPage() {
  const router = useRouter();
  const params = useParams();
  const rmaId = params.rmaId as string;

  const [detail, setDetail] = useState<RmaDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [showMoreMenu, setShowMoreMenu] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiFetch(`${API_BASE}/${rmaId}`);
      if (res.ok) setDetail(await res.json());
    } catch (err) {
      console.error('Failed to load RMA detail:', err);
    } finally {
      setLoading(false);
    }
  }, [rmaId]);

  useEffect(() => { loadData(); }, [loadData]);

  // Declined items shown in separate grid (per Mendix layout)
  const declinedItems = useMemo(() => {
    if (!detail || (detail.rma.declinedCount || 0) === 0) return [];
    return detail.items.filter(i => i.status === 'Decline');
  }, [detail]);

  const approvedItems = useMemo(() => {
    if (!detail) return [] as RmaItemData[];
    return detail.items.filter(i => i.status !== 'Decline');
  }, [detail]);

  function handleExportCsv() {
    if (!detail) return;
    const headers = ['IMEI/Serial', 'Order Number', 'Ship Date', 'SKU', 'Description', 'Original Price', 'Return Reason'];
    const rows = detail.items.map(i => [
      i.imei || '', i.orderNumber || '', formatDate(i.shipDate),
      i.sku || '', i.deviceDescription || '', i.salePrice ?? 0, i.returnReason || '',
    ]);
    const csv = [headers.join(','), ...rows.map(r => r.map(v => `"${v}"`).join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `rma-${detail.rma.number || rmaId}-details.csv`;
    a.click();
    URL.revokeObjectURL(url);
    setShowMoreMenu(false);
  }

  if (loading) return <div className={styles.pageContainer}><div className={styles.loadingOverlay}>Loading...</div></div>;
  if (!detail) return <div className={styles.pageContainer}><div className={styles.loadingOverlay}>RMA not found</div></div>;

  const { rma } = detail;

  return (
    <div className={styles.pageContainer}>
      <button className={styles.backNav} onClick={() => router.push('/pws/rma-requests')}>
        <span className={styles.backArrow}>&#8592;</span> RMA Requests
      </button>

      <div className={styles.rmaHeader}>
        <div className={styles.rmaInfo}>
          <h1 className={styles.rmaNumber}>RMA {rma.number || rmaId}</h1>
          <div className={styles.rmaMeta}>
            Status: {rma.externalStatusText || rma.systemStatus || '—'}
            {rma.submittedDate && ` | Submitted: ${formatDate(rma.submittedDate)}`}
          </div>
        </div>

        <div className={styles.rmaSummaryCards}>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>SKUs</div>
            <div className={styles.summaryValue}>{rma.requestSkus}</div>
          </div>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Qty</div>
            <div className={styles.summaryValue}>{rma.requestQty}</div>
          </div>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Total</div>
            <div className={styles.summaryValue}>{formatCurrency(rma.requestSalesTotal)}</div>
          </div>
        </div>

        <div className={styles.headerActions}>
          <div className={styles.moreActionsContainer}>
            <button className={styles.moreActionsBtn} onClick={() => setShowMoreMenu(!showMoreMenu)}>
              &#8943;
            </button>
            {showMoreMenu && (
              <div className={styles.moreActionsMenu}>
                <button className={styles.menuItem} onClick={handleExportCsv}>Export</button>
                <button className={styles.menuItem} onClick={() => setShowMoreMenu(false)}>
                  RMA Instructions
                </button>
                <button className={styles.menuItem} onClick={() => setShowMoreMenu(false)}>
                  Download RMA Label
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Main items grid */}
      <div className={styles.tableContainer}>
        <table className={styles.dataGrid}>
          <thead>
            <tr>
              <th>IMEI/Serial</th>
              <th>Order Number</th>
              <th>Ship Date</th>
              <th>SKU</th>
              <th>Description</th>
              <th className={styles.alignRight}>Original Price</th>
              <th>Return Reason</th>
            </tr>
          </thead>
          <tbody>
            {approvedItems.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE).map(item => (
              <tr key={item.id}>
                <td>{item.imei || ''}</td>
                <td>{item.orderNumber || ''}</td>
                <td>{formatDate(item.shipDate)}</td>
                <td>{item.sku || ''}</td>
                <td>{item.deviceDescription || ''}</td>
                <td className={styles.alignRight}>{formatCurrency(item.salePrice)}</td>
                <td>{item.returnReason || ''}</td>
              </tr>
            ))}
            {approvedItems.length === 0 && (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '40px', color: '#666' }}>No items</td></tr>
            )}
          </tbody>
        </table>
        {approvedItems.length > PAGE_SIZE && (
          <div className={styles.pagination}>
            <button className={styles.pageBtn} disabled={currentPage <= 1}
                    onClick={() => setCurrentPage(p => p - 1)}>&lt;</button>
            {Array.from({ length: Math.ceil(approvedItems.length / PAGE_SIZE) }, (_, i) => i + 1)
              .slice(Math.max(0, currentPage - 3), Math.min(Math.ceil(approvedItems.length / PAGE_SIZE), currentPage + 2))
              .map(p => (
                <button key={p}
                        className={`${styles.pageBtn} ${p === currentPage ? styles.pageBtnActive : ''}`}
                        onClick={() => setCurrentPage(p)}>
                  {p}
                </button>
              ))}
            <button className={styles.pageBtn} disabled={currentPage >= Math.ceil(approvedItems.length / PAGE_SIZE)}
                    onClick={() => setCurrentPage(p => p + 1)}>&gt;</button>
          </div>
        )}
      </div>

      {/* Declined items grid — shown only when there are declined items */}
      {declinedItems.length > 0 && (
        <>
          <h3 className={styles.sectionTitle}>Declined Items ({declinedItems.length})</h3>
          <div className={styles.tableContainer}>
            <table className={styles.dataGrid}>
              <thead>
                <tr>
                  <th>IMEI/Serial</th>
                  <th>Order Number</th>
                  <th>Ship Date</th>
                  <th>SKU</th>
                  <th>Description</th>
                  <th className={styles.alignRight}>Original Price</th>
                  <th>Return Reason</th>
                </tr>
              </thead>
              <tbody>
                {declinedItems.map(item => (
                  <tr key={item.id}>
                    <td>{item.imei || ''}</td>
                    <td>{item.orderNumber || ''}</td>
                    <td>{formatDate(item.shipDate)}</td>
                    <td>{item.sku || ''}</td>
                    <td>{item.deviceDescription || ''}</td>
                    <td className={styles.alignRight}>{formatCurrency(item.salePrice)}</td>
                    <td>{item.returnReason || ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
