'use client';

import { useState, useEffect, useCallback, useMemo } from 'react';
import { useRouter, useParams } from 'next/navigation';
import styles from './rmaReviewDetails.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

const BASE = `${API_BASE}/pws/rma`;
const PAGE_SIZE = 20;

interface RmaInfo {
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
  approvedSkus: number;
  approvedQty: number;
  approvedSalesTotal: number;
  approvedCount: number;
  declinedCount: number;
  submittedDate: string | null;
  approvalDate: string | null;
  reviewCompletedOn: string | null;
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
  declineReason: string | null;
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

function shouldHighlightSku(item: RmaItemData): boolean {
  return item.grade === 'A_YYY' || item.itemType === 'SPB';
}

export default function RmaReviewDetailsPage() {
  const router = useRouter();
  const params = useParams();
  const rmaId = params.rmaId as string;

  const [detail, setDetail] = useState<RmaDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [showMoreMenu, setShowMoreMenu] = useState(false);
  const [pendingPage, setPendingPage] = useState(1);
  const [reviewedPage, setReviewedPage] = useState(1);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${rmaId}`);
      if (res.ok) setDetail(await res.json());
    } catch (err) {
      console.error('Failed to load RMA detail:', err);
    } finally {
      setLoading(false);
    }
  }, [rmaId]);

  useEffect(() => { loadData(); }, [loadData]);

  const pendingItems = useMemo(() => {
    if (!detail) return [];
    return detail.items.filter(i => !i.status || (i.status !== 'Approve' && i.status !== 'Decline'));
  }, [detail]);

  const reviewedItems = useMemo(() => {
    if (!detail) return [];
    return detail.items.filter(i => i.status === 'Approve' || i.status === 'Decline');
  }, [detail]);

  const pendingTotalPages = Math.max(1, Math.ceil(pendingItems.length / PAGE_SIZE));
  const reviewedTotalPages = Math.max(1, Math.ceil(reviewedItems.length / PAGE_SIZE));
  const pagePending = pendingItems.slice((pendingPage - 1) * PAGE_SIZE, pendingPage * PAGE_SIZE);
  const pageReviewed = reviewedItems.slice((reviewedPage - 1) * PAGE_SIZE, reviewedPage * PAGE_SIZE);

  async function handleItemAction(itemId: number, status: string) {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/items/${itemId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status }),
      });
      if (res.ok) await loadData();
    } catch (err) {
      console.error('Failed to update item status:', err);
    } finally {
      setActionLoading(false);
    }
  }

  async function handleApproveAll() {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${rmaId}/items/approve-all`, { method: 'PUT' });
      if (res.ok) await loadData();
    } catch (err) {
      console.error('Failed to approve all:', err);
    } finally {
      setActionLoading(false);
    }
  }

  async function handleDeclineAll() {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${rmaId}/items/decline-all`, { method: 'PUT' });
      if (res.ok) await loadData();
    } catch (err) {
      console.error('Failed to decline all:', err);
    } finally {
      setActionLoading(false);
    }
  }

  async function handleCompleteReview() {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${rmaId}/complete-review`, { method: 'PUT' });
      if (res.ok) await loadData();
    } catch (err) {
      console.error('Failed to complete review:', err);
    } finally {
      setActionLoading(false);
    }
  }

  function handleExportCsv() {
    if (!detail) return;
    const headers = ['IMEI/Serial', 'Order Number', 'Ship Date', 'SKU', 'Description', 'Grade', 'Original Price', 'Return Reason', 'Status'];
    const rows = detail.items.map(i => [
      i.imei || '', i.orderNumber || '', formatDate(i.shipDate),
      i.sku || '', i.deviceDescription || '', i.grade || '',
      i.salePrice ?? 0, i.returnReason || '', i.status || '',
    ]);
    const csv = [headers.join(','), ...rows.map(r => r.map(v => `"${v}"`).join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `rma-${detail.rma.number || rmaId}-sales-details.csv`;
    a.click();
    URL.revokeObjectURL(url);
    setShowMoreMenu(false);
  }

  function handleExportPendingApproval() {
    if (!detail) return;
    const headers = ['IMEI/Serial', 'Order Number', 'Ship Date', 'SKU', 'Description', 'Grade', 'Original Price', 'Return Reason'];
    const pending = detail.items.filter(i => !i.status || (i.status !== 'Approve' && i.status !== 'Decline'));
    const rows = pending.map(i => [
      i.imei || '', i.orderNumber || '', formatDate(i.shipDate),
      i.sku || '', i.deviceDescription || '', i.grade || '',
      i.salePrice ?? 0, i.returnReason || '',
    ]);
    const csv = [headers.join(','), ...rows.map(r => r.map(v => `"${v}"`).join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `rma-${detail.rma.number || rmaId}-pending-approval.csv`;
    a.click();
    URL.revokeObjectURL(url);
    setShowMoreMenu(false);
  }

  function renderPagination(currentPage: number, totalPages: number, setPage: (p: number) => void) {
    if (totalPages <= 1) return null;
    return (
      <div className={styles.pagination}>
        <button className={styles.pageBtn} disabled={currentPage <= 1}
                onClick={() => setPage(currentPage - 1)}>&lt;</button>
        {Array.from({ length: totalPages }, (_, i) => i + 1)
          .slice(Math.max(0, currentPage - 3), Math.min(totalPages, currentPage + 2))
          .map(p => (
            <button key={p}
                    className={`${styles.pageBtn} ${p === currentPage ? styles.pageBtnActive : ''}`}
                    onClick={() => setPage(p)}>
              {p}
            </button>
          ))}
        <button className={styles.pageBtn} disabled={currentPage >= totalPages}
                onClick={() => setPage(currentPage + 1)}>&gt;</button>
      </div>
    );
  }

  if (loading) return <div className={styles.pageContainer}><div className={styles.loadingOverlay}>Loading...</div></div>;
  if (!detail) return <div className={styles.pageContainer}><div className={styles.loadingOverlay}>RMA not found</div></div>;

  const { rma } = detail;
  const isReviewComplete = rma.statusGroupedTo === 'Closed' || rma.statusGroupedTo === 'Declined' || rma.reviewCompletedOn != null;

  return (
    <div className={styles.pageContainer}>
      <button className={styles.backNav} onClick={() => router.push('/pws/rma-review')}>
        <span className={styles.backArrow}>&#8592;</span> RMA Review
      </button>

      <div className={styles.rmaHeader}>
        <div className={styles.rmaInfo}>
          <h1 className={styles.rmaNumber}>RMA {rma.number || rmaId}</h1>
          <div className={styles.rmaMeta}>
            Status: {rma.internalStatusText || rma.systemStatus || '\u2014'}
            {rma.submittedDate && ` | Submitted: ${formatDate(rma.submittedDate)}`}
            {rma.buyerName && ` | Buyer: ${rma.buyerName}`}
            {rma.companyName && ` | Company: ${rma.companyName}`}
          </div>
        </div>

        <div className={styles.rmaSummaryCards}>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Request SKUs</div>
            <div className={styles.summaryValue}>{rma.requestSkus}</div>
          </div>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Request Qty</div>
            <div className={styles.summaryValue}>{rma.requestQty}</div>
          </div>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Request Total</div>
            <div className={styles.summaryValue}>{formatCurrency(rma.requestSalesTotal)}</div>
          </div>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Approved</div>
            <div className={styles.summaryValue}>{rma.approvedCount || 0}</div>
          </div>
          <div className={styles.summaryCard}>
            <div className={styles.summaryLabel}>Declined</div>
            <div className={styles.summaryValue}>{rma.declinedCount || 0}</div>
          </div>
        </div>

        <div className={styles.headerActions}>
          {!isReviewComplete && (
            <>
              <button className={styles.actionBtn} onClick={handleApproveAll} disabled={actionLoading || pendingItems.length === 0}>
                Approve All
              </button>
              <button className={styles.actionBtn} onClick={handleDeclineAll} disabled={actionLoading || pendingItems.length === 0}>
                Decline All
              </button>
              <button className={styles.completeReviewBtn} onClick={handleCompleteReview} disabled={actionLoading}>
                Complete Review
              </button>
            </>
          )}
          <div className={styles.moreActionsContainer}>
            <button className={styles.moreActionsBtn} onClick={() => setShowMoreMenu(!showMoreMenu)}>
              &#8943;
            </button>
            {showMoreMenu && (
              <div className={styles.moreActionsMenu}>
                <button className={styles.menuItem} onClick={handleExportCsv}>Export</button>
                <button className={styles.menuItem} onClick={handleExportPendingApproval}>Export Pending Approval</button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Pending items grid — with action dropdown */}
      {pendingItems.length > 0 && (
        <>
          <h3 className={styles.sectionTitle}>Pending Review ({pendingItems.length})</h3>
          <div className={styles.tableContainer}>
            <table className={styles.dataGrid}>
              <thead>
                <tr>
                  <th>IMEI/Serial</th>
                  <th>Order Number</th>
                  <th>Ship Date</th>
                  <th>SKU</th>
                  <th>Description</th>
                  <th>Grade</th>
                  <th className={styles.alignRight}>Original Price</th>
                  <th>Return Reason</th>
                  <th className={styles.actionColumnHeader}>Action</th>
                </tr>
              </thead>
              <tbody>
                {pagePending.map(item => (
                  <tr key={item.id}>
                    <td>{item.imei || ''}</td>
                    <td>{item.orderNumber || ''}</td>
                    <td>{formatDate(item.shipDate)}</td>
                    <td className={shouldHighlightSku(item) ? styles.skuHighlight : ''}>{item.sku || ''}</td>
                    <td>{item.deviceDescription || ''}</td>
                    <td>{item.grade || ''}</td>
                    <td className={styles.alignRight}>{formatCurrency(item.salePrice)}</td>
                    <td>{item.returnReason || ''}</td>
                    <td className={styles.actionColumn}>
                      <select
                        className={`${styles.actionDropdown} ${item.status === 'Approve' ? styles.actionApprove : item.status === 'Decline' ? styles.actionDecline : ''}`}
                        value={item.status || ''}
                        disabled={actionLoading}
                        onChange={e => handleItemAction(item.id, e.target.value)}
                      >
                        <option value="">-- Select --</option>
                        <option value="Approve">Approve</option>
                        <option value="Decline">Decline</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {renderPagination(pendingPage, pendingTotalPages, setPendingPage)}
          </div>
        </>
      )}

      {/* Reviewed items grid — read-only */}
      {reviewedItems.length > 0 && (
        <>
          <h3 className={styles.sectionTitle}>Reviewed Items ({reviewedItems.length})</h3>
          <div className={styles.tableContainer}>
            <table className={styles.dataGrid}>
              <thead>
                <tr>
                  <th>IMEI/Serial</th>
                  <th>Order Number</th>
                  <th>Ship Date</th>
                  <th>SKU</th>
                  <th>Description</th>
                  <th>Grade</th>
                  <th className={styles.alignRight}>Original Price</th>
                  <th>Return Reason</th>
                  <th>Decision</th>
                </tr>
              </thead>
              <tbody>
                {pageReviewed.map(item => (
                  <tr key={item.id}>
                    <td>{item.imei || ''}</td>
                    <td>{item.orderNumber || ''}</td>
                    <td>{formatDate(item.shipDate)}</td>
                    <td className={shouldHighlightSku(item) ? styles.skuHighlight : ''}>{item.sku || ''}</td>
                    <td>{item.deviceDescription || ''}</td>
                    <td>{item.grade || ''}</td>
                    <td className={styles.alignRight}>{formatCurrency(item.salePrice)}</td>
                    <td>{item.returnReason || ''}</td>
                    <td>
                      <span className={`${styles.decisionBadge} ${item.status === 'Approve' ? styles.decisionApprove : styles.decisionDecline}`}>
                        {item.status === 'Approve' ? '\u{1F44D}' : '\u2716'} {item.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {renderPagination(reviewedPage, reviewedTotalPages, setReviewedPage)}
          </div>
        </>
      )}

      {pendingItems.length === 0 && reviewedItems.length === 0 && (
        <div className={styles.emptyState}>No items found for this RMA</div>
      )}
    </div>
  );
}
