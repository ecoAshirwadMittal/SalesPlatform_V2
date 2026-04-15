'use client';

import { useEffect, useState, useCallback, useRef } from 'react';
import { useRouter, useParams } from 'next/navigation';
import styles from '../counterOffers.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

const PAGE_SIZE = 20;

interface OfferItemData {
  id: number;
  sku: string;
  deviceId: number;
  quantity: number;
  price: number;
  totalPrice: number;
  itemStatus: string;
  offerDrawerStatus: string;
  buyerCounterStatus: string;
  counterQty: number;
  counterPrice: number;
  counterTotal: number;
  finalOfferPrice: number;
  finalOfferQuantity: number;
  finalOfferTotalPrice: number;
  counterCasePriceTotal: number;
  itemType: string;
  caseLotSize: number;
  categoryName: string;
  brandName: string;
  modelName: string;
  carrierName: string;
  capacityName: string;
  colorName: string;
  gradeName: string;
  listPrice: number;
  minPrice: number;
  availableQty: number;
}

interface OfferData {
  offerId: number;
  offerNumber: string;
  status: string;
  buyerCodeId: number;
  totalSkus: number;
  totalQty: number;
  totalPrice: number;
  items: OfferItemData[];
  counterOfferTotalSkus: number;
  counterOfferTotalQty: number;
  counterOfferTotalPrice: number;
  finalOfferTotalSkus: number;
  finalOfferTotalQty: number;
  finalOfferTotalPrice: number;
}

function fmt(n: number | null | undefined): string {
  if (n == null) return '$0.00';
  return '$' + Number(n).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatStatus(status: string): string {
  return status.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase()).replace(/\B\w+/g, w => w.toLowerCase());
}

function getStatusBadgeClass(status: string): string {
  if (status === 'Buyer_Acceptance') return styles.statusBuyerAcceptance;
  if (status === 'Ordered') return styles.statusOrdered;
  if (status === 'Declined') return styles.statusDeclined;
  if (status === 'Canceled') return styles.statusCanceled;
  return styles.statusBuyerAcceptance;
}

export default function CounterOfferDetailPage() {
  const router = useRouter();
  const params = useParams();
  const offerId = params?.offerId as string;

  const [offer, setOffer] = useState<OfferData | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [moreMenuOpen, setMoreMenuOpen] = useState(false);
  const [showConfirm, setShowConfirm] = useState<'cancel' | 'submit' | null>(null);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const moreMenuRef = useRef<HTMLDivElement>(null);

  const [funcPage, setFuncPage] = useState(1);
  const [clPage, setCLPage] = useState(1);
  const [untPage, setUntPage] = useState(1);

  const fetchOffer = useCallback(() => {
    apiFetch(`${API_BASE}/pws/counter-offers/${offerId}`)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((data: OfferData) => {
        setOffer(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [offerId]);

  useEffect(() => {
    if (offerId) fetchOffer();
  }, [offerId, fetchOffer]);

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (moreMenuRef.current && !moreMenuRef.current.contains(e.target as Node)) {
        setMoreMenuOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const handleItemAction = async (itemId: number, buyerCounterStatus: string) => {
    const res = await apiFetch(`${API_BASE}/pws/counter-offers/${offerId}/items/${itemId}/action`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ buyerCounterStatus }),
    });
    if (res.ok) {
      const data: OfferData = await res.json();
      setOffer(data);
    }
  };

  const handleAcceptAll = async () => {
    setMoreMenuOpen(false);
    const res = await apiFetch(`${API_BASE}/pws/counter-offers/${offerId}/accept-all`, { method: 'POST' });
    if (res.ok) {
      const data: OfferData = await res.json();
      setOffer(data);
      setMessage({ type: 'success', text: 'All counter offers accepted.' });
    }
  };

  const handleSubmit = async () => {
    setShowConfirm(null);
    setSubmitting(true);
    try {
      const res = await apiFetch(`${API_BASE}/pws/counter-offers/${offerId}/submit`, { method: 'POST' });
      const data = await res.json();
      if (data.orderNumber || data.offerNumber) {
        setMessage({ type: 'success', text: `Order submitted successfully. Order #${data.orderNumber || data.offerNumber || data.offerId}` });
        setTimeout(() => router.push('/pws/order'), 2000);
      } else if (data.validationErrors?.length) {
        setMessage({ type: 'error', text: data.validationErrors.join('; ') });
      } else if (data.message && !data.success) {
        setMessage({ type: 'error', text: data.message });
      }
    } catch {
      setMessage({ type: 'error', text: 'Failed to submit counter response.' });
    }
    setSubmitting(false);
  };

  const handleCancel = async () => {
    setShowConfirm(null);
    setSubmitting(true);
    try {
      await apiFetch(`${API_BASE}/pws/counter-offers/${offerId}/cancel`, { method: 'POST' });
      setMessage({ type: 'success', text: 'Offer has been canceled.' });
      setTimeout(() => router.push('/pws/order'), 2000);
    } catch {
      setMessage({ type: 'error', text: 'Failed to cancel offer.' });
    }
    setSubmitting(false);
  };

  if (loading) {
    return <div className={styles.pageContainer}><div className={styles.loading}>Loading counter offer details...</div></div>;
  }

  if (!offer) {
    return (
      <div className={styles.pageContainer}>
        <div className={styles.emptyState}>
          <h2>Offer not found</h2>
          <button className={styles.shopLink} onClick={() => router.push('/pws/counter-offers')}>
            Back to Counter Offers
          </button>
        </div>
      </div>
    );
  }

  const items = offer.items || [];
  const functionalItems = items.filter(i => i.itemType !== 'SPB' && i.gradeName !== 'A_YYY');
  const caseLotItems = items.filter(i => i.itemType === 'SPB');
  const untestedItems = items.filter(i => i.itemType !== 'SPB' && i.gradeName === 'A_YYY');

  const getStatusIcon = (status: string) => {
    if (status === 'Accept') return <span className={`${styles.statusIcon} ${styles.iconAccept}`}>&#x2713;</span>;
    if (status === 'Counter') return <span className={`${styles.statusIcon} ${styles.iconCounter}`}>&#x21C4;</span>;
    if (status === 'Decline') return <span className={`${styles.statusIcon} ${styles.iconDecline}`}>&#x2717;</span>;
    return null;
  };

  const getStatusTextClass = (status: string) => {
    if (status === 'Accept') return styles.statusTextAccept;
    if (status === 'Counter') return styles.statusTextCounter;
    if (status === 'Decline') return styles.statusTextDecline;
    return '';
  };

  const getRowCellClass = (status: string) => {
    if (status === 'Counter') return styles.counterCell;
    if (status === 'Decline') return styles.declineCell;
    return '';
  };

  const renderGrid = (gridItems: OfferItemData[], isCaseLot: boolean, currentPage: number, setPage: (p: number) => void) => {
    const totalPages = Math.ceil(gridItems.length / PAGE_SIZE);
    const paged = gridItems.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

    return (
      <>
        <div className={styles.gridContainer}>
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
                <th>List Price</th>
                <th>Offer Qty</th>
                <th>Offer Price</th>
                <th>Offer Total</th>
                <th>Status</th>
                <th>Action</th>
                {isCaseLot && <th>Case Pack</th>}
                <th>Qty</th>
                <th>Price</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {paged.map(item => {
                const isCounter = item.itemStatus === 'Counter';
                const counterCellClass = getRowCellClass(item.itemStatus);

                return (
                  <tr key={item.id}>
                    <td>{item.sku}</td>
                    <td>{item.brandName}</td>
                    <td>{item.modelName}</td>
                    <td>{item.carrierName}</td>
                    <td>{item.capacityName}</td>
                    <td>{item.colorName}</td>
                    <td style={{ textAlign: 'center' }}>{item.gradeName}</td>
                    <td>{fmt(item.listPrice)}</td>
                    <td style={{ textAlign: 'center' }}>{item.quantity}</td>
                    <td>{fmt(item.price)}</td>
                    <td>{fmt(item.totalPrice)}</td>
                    <td>
                      <span className={`${styles.statusText} ${getStatusTextClass(item.itemStatus)}`}>
                        {getStatusIcon(item.itemStatus)} {item.itemStatus}
                      </span>
                    </td>
                    <td>
                      {isCounter ? (
                        <select
                          className={styles.itemActionSelect}
                          value={item.buyerCounterStatus || ''}
                          onChange={e => handleItemAction(item.id, e.target.value)}
                        >
                          <option value="">-- Select --</option>
                          <option value="Accept">Accept</option>
                          <option value="Decline">Decline</option>
                        </select>
                      ) : item.itemStatus === 'Accept' ? (
                        <span className={`${styles.actionDisplay} ${styles.actionAccepted}`}>&#x2713; Accepted</span>
                      ) : item.itemStatus === 'Decline' ? (
                        <span className={`${styles.actionDisplay} ${styles.actionDeclined}`}>&#x2717; Declined</span>
                      ) : null}
                    </td>
                    {isCaseLot && <td style={{ textAlign: 'center' }}>{item.caseLotSize}</td>}
                    <td className={counterCellClass} style={{ textAlign: 'center' }}>{item.counterQty}</td>
                    <td className={counterCellClass}>{fmt(item.counterPrice)}</td>
                    <td className={counterCellClass}>{fmt(item.counterTotal)}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        {totalPages > 1 && (
          <div className={styles.pagination}>
            <button className={styles.paginationBtn} disabled={currentPage <= 1} onClick={() => setPage(1)}>&laquo;</button>
            <button className={styles.paginationBtn} disabled={currentPage <= 1} onClick={() => setPage(currentPage - 1)}>&lsaquo;</button>
            <span className={styles.paginationInfo}>{currentPage} of {totalPages}</span>
            <button className={styles.paginationBtn} disabled={currentPage >= totalPages} onClick={() => setPage(currentPage + 1)}>&rsaquo;</button>
            <button className={styles.paginationBtn} disabled={currentPage >= totalPages} onClick={() => setPage(totalPages)}>&raquo;</button>
          </div>
        )}
      </>
    );
  };

  return (
    <div className={styles.pageContainer}>
      {/* Back link */}
      <button className={styles.backLink} onClick={() => router.push('/pws/counter-offers')}>
        &#x2190; Back to Counter Offers
      </button>

      {/* Header */}
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>
          Offer {offer.offerNumber || offer.offerId}
        </h1>
        <div className={styles.headerActions}>
          <span className={`${styles.statusBadge} ${getStatusBadgeClass(offer.status)}`}>
            {formatStatus(offer.status)}
          </span>
          <button
            className={styles.submitBtn}
            disabled={submitting}
            onClick={() => setShowConfirm('submit')}
          >
            Submit Response
          </button>
          <div ref={moreMenuRef} className={styles.moreMenuWrapper}>
            <button className={styles.moreMenuButton} onClick={() => setMoreMenuOpen(!moreMenuOpen)}>
              &#x22EE;
            </button>
            {moreMenuOpen && (
              <div className={styles.moreMenuDropdown}>
                <button onClick={handleAcceptAll}>Accept All Counters</button>
                <button onClick={() => { setMoreMenuOpen(false); setShowConfirm('cancel'); }}>Cancel Offer</button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Message banner */}
      {message && (
        <div className={`${styles.messageBanner} ${message.type === 'success' ? styles.messageBannerSuccess : styles.messageBannerError}`}>
          {message.text}
        </div>
      )}

      {/* Summary Cards */}
      <div className={styles.summaryCardsRow}>
        <div className={styles.summaryCard}>
          <div className={styles.summaryCardTitle}>Original Offer</div>
          <table className={styles.summaryTable}>
            <thead><tr><td>SKUs</td><td>Qty</td><td>Total</td></tr></thead>
            <tbody><tr><td>{offer.totalSkus ?? 0}</td><td>{offer.totalQty ?? 0}</td><td>{fmt(offer.totalPrice)}</td></tr></tbody>
          </table>
        </div>
        <div className={styles.summaryCard}>
          <div className={styles.summaryCardTitle}>EcoATM&apos;s Counter Offer</div>
          <table className={styles.summaryTable}>
            <thead><tr><td>SKUs</td><td>Qty</td><td>Total</td></tr></thead>
            <tbody><tr><td>{offer.counterOfferTotalSkus ?? 0}</td><td>{offer.counterOfferTotalQty ?? 0}</td><td>{fmt(offer.counterOfferTotalPrice)}</td></tr></tbody>
          </table>
        </div>
        <div className={styles.summaryCard}>
          <div className={styles.summaryCardTitle}>Final Offer (Your Response)</div>
          <table className={styles.summaryTable}>
            <thead><tr><td>SKUs</td><td>Qty</td><td>Total</td></tr></thead>
            <tbody><tr><td>{offer.finalOfferTotalSkus ?? 0}</td><td>{offer.finalOfferTotalQty ?? 0}</td><td>{fmt(offer.finalOfferTotalPrice)}</td></tr></tbody>
          </table>
        </div>
      </div>

      {/* Functional Devices Grid */}
      {functionalItems.length > 0 && (
        <div className={styles.gridSection}>
          <div className={styles.sectionLabel}>Functional Devices</div>
          {renderGrid(functionalItems, false, funcPage, setFuncPage)}
        </div>
      )}

      {/* Case Lots Grid */}
      {caseLotItems.length > 0 && (
        <div className={styles.gridSection}>
          <div className={styles.sectionLabel}>Case Lots</div>
          {renderGrid(caseLotItems, true, clPage, setCLPage)}
        </div>
      )}

      {/* Untested Devices Grid */}
      {untestedItems.length > 0 && (
        <div className={styles.gridSection}>
          <div className={styles.sectionLabel}>Untested Devices</div>
          {renderGrid(untestedItems, false, untPage, setUntPage)}
        </div>
      )}

      {/* Confirm Modal */}
      {showConfirm && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <div className={styles.modalHeader}>
              {showConfirm === 'submit' ? 'Submit Counter Response' : 'Cancel Offer'}
            </div>
            <div className={styles.modalBody}>
              {showConfirm === 'submit'
                ? 'Are you sure you want to submit your counter response? This action cannot be undone.'
                : 'Are you sure you want to cancel this offer? This action cannot be undone.'}
            </div>
            <div className={styles.modalFooter}>
              <button className={styles.modalCancel} onClick={() => setShowConfirm(null)}>No</button>
              <button className={styles.modalOk} onClick={showConfirm === 'submit' ? handleSubmit : handleCancel}>Yes</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
