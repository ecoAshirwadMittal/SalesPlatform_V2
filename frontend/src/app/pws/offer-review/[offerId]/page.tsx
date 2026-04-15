'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import styles from '../offerReview.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

const BASE = `${API_BASE}/pws/offer-review`;

interface OfferItemData {
  id: number;
  sku: string;
  deviceId: number;
  quantity: number;
  price: number;
  totalPrice: number;
  itemStatus: string;
  offerDrawerStatus: string | null;
  buyerCounterStatus: string | null;
  counterQty: number | null;
  counterPrice: number | null;
  counterTotal: number | null;
  categoryName: string | null;
  brandName: string | null;
  modelName: string | null;
  carrierName: string | null;
  capacityName: string | null;
  colorName: string | null;
  gradeName: string | null;
  listPrice: number | null;
  minPrice: number | null;
  availableQty: number | null;
}

interface OfferData {
  offerId: number;
  offerNumber: string | null;
  status: string;
  buyerCodeId: number;
  totalSkus: number;
  totalQty: number;
  totalPrice: number;
  items: OfferItemData[];
  createdDate: string;
  updatedDate: string;
}

interface SubmitResult {
  success: boolean;
  title: string;
  message: string;
  offerNumber?: string;
  orderNumber?: string;
  validationErrors?: string[];
}

const BADGE_CLASSES: Record<string, string> = {
  Sales_Review: styles.statusSalesReview,
  SALES_REVIEW: styles.statusSalesReview,
  Buyer_Acceptance: styles.statusBuyerAcceptance,
  BUYER_ACCEPTANCE: styles.statusBuyerAcceptance,
  Ordered: styles.statusOrdered,
  ORDERED: styles.statusOrdered,
  Pending_Order: styles.statusPendingOrder,
  PENDING_ORDER: styles.statusPendingOrder,
  Declined: styles.statusDeclined,
  DECLINED: styles.statusDeclined,
  SUBMITTED: styles.statusSubmitted,
  PENDING_REVIEW: styles.statusPendingReview,
};

function formatStatus(status: string | null): string {
  if (!status) return '';
  return status.replace(/_/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase()).replace(/\B\w+/g, (w) => w.toLowerCase());
}

function formatCurrency(val: number | null): string {
  if (val == null) return '$0.00';
  return '$' + val.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function pctVs(offerPrice: number | null, referencePrice: number | null): { text: string; cls: string } {
  if (!offerPrice || !referencePrice || referencePrice === 0) return { text: '-', cls: styles.pctNeutral };
  const pct = ((offerPrice - referencePrice) / referencePrice) * 100;
  const text = (pct >= 0 ? '+' : '') + pct.toFixed(1) + '%';
  if (pct > 0) return { text, cls: styles.pctPositive };
  if (pct < 0) return { text, cls: styles.pctNegative };
  return { text, cls: styles.pctNeutral };
}

export default function OfferDetailPage() {
  const params = useParams();
  const router = useRouter();
  const offerId = Number(params.offerId);

  const [offer, setOffer] = useState<OfferData | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [modal, setModal] = useState<SubmitResult | null>(null);
  const [moreMenuOpen, setMoreMenuOpen] = useState(false);

  const loadOffer = useCallback(async () => {
    try {
      const res = await apiFetch(`${BASE}/${offerId}`);
      if (res.ok) setOffer(await res.json());
    } catch (err) {
      console.error('Failed to load offer:', err);
    } finally {
      setLoading(false);
    }
  }, [offerId]);

  useEffect(() => { loadOffer(); }, [loadOffer]);

  const isEditable = offer?.status === 'Sales_Review' || offer?.status === 'SALES_REVIEW';

  async function handleItemAction(itemId: number, action: string) {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${offerId}/items/${itemId}/action`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action }),
      });
      if (res.ok) setOffer(await res.json());
    } finally {
      setActionLoading(false);
    }
  }

  async function handleCounterUpdate(itemId: number, field: 'counterQty' | 'counterPrice', value: string) {
    const body: Record<string, number | null> = {};
    body[field] = value ? Number(value) : null;
    try {
      const res = await apiFetch(`${BASE}/${offerId}/items/${itemId}/counter`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      if (res.ok) setOffer(await res.json());
    } catch (err) {
      console.error('Counter update failed:', err);
    }
  }

  async function handleBulkAction(action: string) {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${offerId}/${action}`, { method: 'POST' });
      if (res.ok) setOffer(await res.json());
    } finally {
      setActionLoading(false);
    }
  }

  function handleDownload() {
    if (!offer) return;
    const headers = [
      'SKU', 'Brand', 'Model', 'Carrier', 'Capacity', 'Color', 'Grade',
      'Avl Qty', 'Min Price', 'List Price', 'Offer Qty', 'Offer Price',
      'Offer Total', 'Status',
    ];
    const escapeCell = (val: unknown): string => {
      const str = String(val ?? '');
      return str.includes(',') || str.includes('"') || str.includes('\n')
        ? `"${str.replace(/"/g, '""')}"` : str;
    };
    const rows = offer.items.map((item) => [
      item.sku, item.brandName, item.modelName, item.carrierName,
      item.capacityName, item.colorName, item.gradeName, item.availableQty,
      item.minPrice, item.listPrice, item.quantity, item.price,
      item.totalPrice, item.itemStatus,
    ].map(escapeCell).join(','));

    const csv = [headers.join(','), ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `offer-${offer.offerNumber || offer.offerId}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  async function handleCompleteReview() {
    setActionLoading(true);
    try {
      const res = await apiFetch(`${BASE}/${offerId}/complete-review`, { method: 'POST' });
      const data: SubmitResult = await res.json();
      setModal(data);
      if (data.success) loadOffer();
    } finally {
      setActionLoading(false);
    }
  }

  if (loading) return <div className={styles.loading}>Loading offer...</div>;
  if (!offer) return <div className={styles.emptyState}>Offer not found.</div>;

  return (
    <div className={styles.pageContainer}>
      {/* Back link */}
      <div className={styles.backLink} onClick={() => router.push('/pws/offer-review')}>
        &lsaquo; All Offers
      </div>

      {/* Header info row */}
      <div className={styles.detailInfoRow}>
        <div className={styles.detailInfoField}>
          <div className={styles.detailInfoLabel}>Offer ID</div>
          <div className={styles.detailInfoValue}>{offer.offerNumber || offer.offerId}</div>
        </div>
        <div className={styles.detailInfoField}>
          <div className={styles.detailInfoLabel}>Buyer</div>
          <div className={styles.detailInfoValue}>{offer.items[0]?.brandName || '-'}</div>
        </div>
        <div className={styles.detailInfoField}>
          <div className={styles.detailInfoLabel}>Customer</div>
          <div className={styles.detailInfoValue}>-</div>
        </div>
        <div className={styles.detailInfoField}>
          <div className={styles.detailInfoLabel}>Sales Rep</div>
          <div className={styles.detailInfoValue}>-</div>
        </div>

        {/* Original Offer summary box */}
        <div className={styles.offerSummaryBox}>
          <div className={styles.offerSummaryTitle}>Original Offer</div>
          <table className={styles.offerSummaryTable}>
            <thead><tr><td>SKUs</td><td>Qty</td><td>Price</td><td>Avg. Price</td></tr></thead>
            <tbody><tr>
              <td>{offer.totalSkus}</td>
              <td>{offer.totalQty}</td>
              <td>{formatCurrency(offer.totalPrice)}</td>
              <td>{offer.totalQty > 0 ? formatCurrency(offer.totalPrice / offer.totalQty) : '$0.00'}</td>
            </tr></tbody>
          </table>
        </div>
      </div>

      {/* Status badge + actions row */}
      <div className={styles.detailActionsRow}>
        <span className={`${styles.statusBadgeLarge} ${BADGE_CLASSES[offer.status] || ''}`}>
          {formatStatus(offer.status)}
        </span>
        <div className={styles.detailActionsRight}>
          {isEditable && (
            <>
              <button className={styles.completeReviewButton}
                onClick={handleCompleteReview} disabled={actionLoading}>Complete Review</button>
              <div className={styles.moreMenuWrapper}>
                <button className={styles.moreMenuButton}
                  onClick={() => setMoreMenuOpen(!moreMenuOpen)}>&#8943;</button>
                {moreMenuOpen && (
                  <div className={styles.moreMenuDropdown}>
                    <button onClick={() => { handleBulkAction('accept-all'); setMoreMenuOpen(false); }}
                      disabled={actionLoading}>Accept All</button>
                    <button onClick={() => { handleBulkAction('finalize-all'); setMoreMenuOpen(false); }}
                      disabled={actionLoading}>Finalize All</button>
                    <button onClick={() => { handleBulkAction('decline-all'); setMoreMenuOpen(false); }}
                      disabled={actionLoading}>Decline All</button>
                    <button onClick={() => { handleDownload(); setMoreMenuOpen(false); }}>Download</button>
                  </div>
                )}
              </div>
            </>
          )}
        </div>
      </div>

      {/* Items table */}
      {offer.items.length === 0 ? (
        <div className={styles.emptyState}>No items in this offer.</div>
      ) : (
        <>
          <div className={styles.sectionLabel}>Functional Devices</div>
          <table className={styles.dataGrid}>
            <thead>
              <tr>
                <th>SKU</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Carrier</th>
                <th>Capacity</th>
                <th>Color</th>
                <th>Grade</th>
                <th>Avl. Qty</th>
                <th>Min Price</th>
                <th>List Price</th>
                <th>vs Min%</th>
                <th>vs List%</th>
                <th>Offer Qty</th>
                <th>Offer Price</th>
                <th>Offer Total</th>
                {isEditable && <th>Actions</th>}
                {isEditable && <th>Qty</th>}
                {isEditable && <th>Counter Price</th>}
                {isEditable && <th>Total</th>}
                {!isEditable && <th>Status</th>}
              </tr>
            </thead>
            <tbody>
              {offer.items.map((item) => {
                const vsMin = pctVs(item.price, item.minPrice);
                const vsList = pctVs(item.price, item.listPrice);
                const isCounter = item.itemStatus === 'Counter';
                const isDecline = item.itemStatus === 'Decline';

                return (
                  <tr key={item.id}>
                    <td>{item.sku}</td>
                    <td>{item.brandName || ''}</td>
                    <td>{item.modelName || ''}</td>
                    <td>{item.carrierName || ''}</td>
                    <td>{item.capacityName || ''}</td>
                    <td>{item.colorName || ''}</td>
                    <td>{item.gradeName || ''}</td>
                    <td>{item.availableQty ?? ''}</td>
                    <td className={styles.priceCell}>{formatCurrency(item.minPrice)}</td>
                    <td className={styles.priceCell}>{formatCurrency(item.listPrice)}</td>
                    <td className={vsMin.cls}>{vsMin.text}</td>
                    <td className={vsList.cls}>{vsList.text}</td>
                    <td>{item.quantity}</td>
                    <td className={styles.priceCell}>{formatCurrency(item.price)}</td>
                    <td className={styles.priceCell}>{formatCurrency(item.totalPrice)}</td>

                    {isEditable && (
                      <td>
                        <select
                          className={styles.itemActionSelect}
                          value={item.itemStatus || ''}
                          onChange={(e) => handleItemAction(item.id, e.target.value)}
                          disabled={actionLoading}
                        >
                          <option value="Accept">Accept</option>
                          <option value="Counter">Counter</option>
                          <option value="Decline">Decline</option>
                          <option value="Finalize">Finalize</option>
                        </select>
                      </td>
                    )}

                    {isEditable && (
                      <>
                        <td className={isCounter ? styles.counterCell : (isDecline ? styles.declineCell : styles.acceptCell)}>
                          {isCounter ? (
                            <input
                              type="number"
                              className={styles.counterInput}
                              value={item.counterQty ?? ''}
                              onChange={(e) => handleCounterUpdate(item.id, 'counterQty', e.target.value)}
                              min={0}
                            />
                          ) : null}
                        </td>
                        <td className={isCounter ? styles.counterCell : (isDecline ? styles.declineCell : styles.acceptCell)}>
                          {isCounter ? (
                            <input
                              type="number"
                              className={styles.counterInput}
                              value={item.counterPrice ?? ''}
                              onChange={(e) => handleCounterUpdate(item.id, 'counterPrice', e.target.value)}
                              min={0}
                              step="0.01"
                            />
                          ) : null}
                        </td>
                        <td className={isCounter ? styles.counterCell : (isDecline ? styles.declineCell : styles.acceptCell)}>
                          {isCounter ? formatCurrency(item.counterTotal) : null}
                        </td>
                      </>
                    )}

                    {!isEditable && (
                      <td>
                        {item.itemStatus && (
                          <span className={`${styles.statusIcon} ${
                            item.itemStatus === 'Accept' ? styles.iconAccept :
                            item.itemStatus === 'Counter' ? styles.iconCounter :
                            item.itemStatus === 'Decline' ? styles.iconDecline :
                            item.itemStatus === 'Finalize' ? styles.iconFinalize : ''
                          }`}>
                            {item.itemStatus === 'Accept' ? '\u2713' :
                             item.itemStatus === 'Counter' ? '\u21C4' :
                             item.itemStatus === 'Decline' ? '\u2717' :
                             item.itemStatus === 'Finalize' ? '\u2605' : ''}
                          </span>
                        )}
                        <span style={{ marginLeft: '6px', fontSize: '12px' }}>{item.itemStatus}</span>
                      </td>
                    )}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </>
      )}

      {/* Result modal */}
      {modal && (
        <div className={styles.modalOverlay} onClick={() => setModal(null)}>
          <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
            <div className={modal.success ? styles.modalHeaderSuccess : styles.modalHeaderError}>
              {modal.title}
            </div>
            <div className={styles.modalBody}>
              {modal.message}
              {modal.validationErrors && modal.validationErrors.length > 0 && (
                <ul style={{ marginTop: '8px', paddingLeft: '20px' }}>
                  {modal.validationErrors.map((e, i) => <li key={i}>{e}</li>)}
                </ul>
              )}
            </div>
            <div className={styles.modalFooter}>
              {modal.success ? (
                <button className={styles.modalOk} onClick={() => {
                  setModal(null);
                  router.push('/pws/offer-review');
                }}>OK</button>
              ) : (
                <button className={styles.modalOk} onClick={() => setModal(null)}>OK</button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
