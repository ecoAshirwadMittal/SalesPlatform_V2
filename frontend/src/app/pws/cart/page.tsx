'use client';

import { useEffect, useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import styles from './cart.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { getBuyerCodeId, getUserId } from '@/lib/session';
import { API_BASE } from '@/lib/apiRoutes';
import { getErrorMessage } from '@/lib/errors';

const DEBOUNCE_MS = 300;

interface CartItem {
  id: number;
  sku: string;
  deviceId: number;
  quantity: number;
  price: number;
  totalPrice: number;
  categoryName: string;
  brandName: string;
  modelName: string;
  carrierName: string;
  capacityName: string;
  colorName: string;
  gradeName: string;
  listPrice: number;
  availableQty: number;
}

interface CartData {
  offerId: number;
  totalSkus: number;
  totalQty: number;
  totalPrice: number;
  items: CartItem[];
}

interface SubmitResult {
  success: boolean;
  requiresSalesReview: boolean;
  alreadySubmitted: boolean;
  validationErrors: string[];
  exceedingItemSkus: string[];
  belowListPriceCount: number;
  offerId: number;
  offerNumber: string;
  orderNumber: string;
  title: string;
  message: string;
}

function authParams(): string {
  const buyerCodeId = getBuyerCodeId();
  const userId = getUserId();
  let params = `buyerCodeId=${buyerCodeId}`;
  if (userId) params += `&userId=${userId}`;
  return params;
}

export default function CartPage() {
  const router = useRouter();
  const [cart, setCart] = useState<CartData | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [validationErrors, setValidationErrors] = useState<string[]>([]);
  const [exceedingSkus, setExceedingSkus] = useState<string[]>([]);
  const [showResetConfirm, setShowResetConfirm] = useState(false);
  const [moreMenuOpen, setMoreMenuOpen] = useState(false);

  // Modal states for all 5 submission scenarios
  const [showAlreadySubmitted, setShowAlreadySubmitted] = useState(false);
  const [showSalesReview, setShowSalesReview] = useState(false);
  const [showOfferSubmitted, setShowOfferSubmitted] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [showError, setShowError] = useState(false);

  // Data for modals
  const [belowListPriceCount, setBelowListPriceCount] = useState(0);
  const [submittedOfferId, setSubmittedOfferId] = useState<number | null>(null);
  const [submittedOfferNumber, setSubmittedOfferNumber] = useState('');
  const [orderNumber, setOrderNumber] = useState('');
  const [errorTitle, setErrorTitle] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const moreMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    loadCart();
  }, []);

  // Close more menu on outside click
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (moreMenuRef.current && !moreMenuRef.current.contains(e.target as Node)) {
        setMoreMenuOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  async function loadCart() {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) { setLoading(false); return; }
    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart?${authParams()}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      data.items = (data.items || []).filter((i: CartItem) => i.quantity > 0);
      setCart(data);
    } catch (err) {
      setSaveError(getErrorMessage(err, 'Failed to load cart.'));
    } finally {
      setLoading(false);
    }
  }

  const [saveError, setSaveError] = useState<string | null>(null);

  function saveItemToApi(sku: string, deviceId: number, offerPrice: number, quantity: number) {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    setSaveError(null);
    apiFetch(`${API_BASE}/pws/offers/cart/items?${authParams()}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sku, deviceId, offerPrice, quantity }),
    })
      .then(res => res.json())
      .then(data => {
        data.items = (data.items || []).filter((i: CartItem) => i.quantity > 0);
        setCart(data);
      })
      .catch(() => setSaveError('Failed to save item. Your changes may not be persisted.'));
  }

  function handleItemChange(item: CartItem, field: 'price' | 'quantity', value: string) {
    const parsed = parseFloat(value);
    const numVal = isNaN(parsed) ? 0 : parsed;

    // Clear exceeding highlight when user adjusts qty
    if (field === 'quantity' && exceedingSkus.includes(item.sku)) {
      setExceedingSkus(prev => prev.filter(s => s !== item.sku));
    }

    setCart(prev => {
      if (!prev) return prev;
      const updatedItems = prev.items.map(i => {
        if (i.sku !== item.sku) return i;
        const updated = { ...i, [field]: numVal };
        updated.totalPrice = updated.price * updated.quantity;
        return updated;
      });
      const activeItems = updatedItems.filter(i => i.quantity > 0);
      return {
        ...prev,
        items: updatedItems,
        totalSkus: activeItems.length,
        totalQty: activeItems.reduce((s, i) => s + i.quantity, 0),
        totalPrice: activeItems.reduce((s, i) => s + i.totalPrice, 0),
      };
    });

    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      const updatedPrice = field === 'price' ? numVal : item.price;
      const updatedQty = field === 'quantity' ? numVal : item.quantity;
      saveItemToApi(item.sku, item.deviceId, updatedPrice, updatedQty);
    }, DEBOUNCE_MS);
  }

  async function handleRemoveItem(sku: string) {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart/items/${encodeURIComponent(sku)}?${authParams()}`, { method: 'DELETE' });
      const data = await res.json();
      data.items = (data.items || []).filter((i: CartItem) => i.quantity > 0);
      setCart(data);
      setExceedingSkus(prev => prev.filter(s => s !== sku));
    } catch (err) {
      setSaveError(getErrorMessage(err, 'Failed to remove item.'));
    }
  }

  async function handleResetCart() {
    setShowResetConfirm(false);
    setMoreMenuOpen(false);
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    try {
      await apiFetch(`${API_BASE}/pws/offers/cart?${authParams()}`, { method: 'DELETE' });
      router.push('/pws/order');
    } catch (err) {
      setSaveError(getErrorMessage(err, 'Failed to reset cart.'));
    }
  }

  async function handleExport() {
    setMoreMenuOpen(false);
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) return;
    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart/export?${authParams()}`);
      if (!res.ok) throw new Error('Export failed');
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'cart_export.csv';
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      setSaveError(getErrorMessage(err, 'Export failed.'));
    }
  }

  /**
   * Main submit handler — handles all 4 scenarios from ACT_SubmitCart:
   *   1. Already submitted → show "Cart Submitted!" popup
   *   2. Exceeding ATP qty → highlight rows + error banner
   *   3. Below list price → show "Almost Done!" popup
   *   4. Direct order → show "Thank you for your order!" popup
   */
  async function handleSubmit() {
    setValidationErrors([]);
    setExceedingSkus([]);
    setSubmitting(true);
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) { setSubmitting(false); return; }

    try {
      const res = await apiFetch(`${API_BASE}/pws/offers/cart/submit?${authParams()}`, { method: 'POST' });
      const data: SubmitResult = await res.json();

      // Scenario 1: Already submitted in another window
      if (data.alreadySubmitted) {
        setShowAlreadySubmitted(true);
        setSubmitting(false);
        return;
      }

      // Scenario 2: Items exceed ATP quantity
      if (data.exceedingItemSkus && data.exceedingItemSkus.length > 0) {
        setExceedingSkus(data.exceedingItemSkus);
        setValidationErrors([data.message || `${data.exceedingItemSkus.length} item(s) exceed available quantity.`]);
        setSubmitting(false);
        return;
      }

      // Validation errors (empty cart, etc.)
      if (!data.success && data.validationErrors && data.validationErrors.length > 0) {
        setValidationErrors(data.validationErrors);
        setSubmitting(false);
        return;
      }

      // Scenario 3: Below list price → "Almost Done!" modal
      if (data.requiresSalesReview) {
        setBelowListPriceCount(data.belowListPriceCount);
        setSubmittedOfferId(data.offerId);
        setShowSalesReview(true);
        setSubmitting(false);
        return;
      }

      // Scenario 4: Direct order success
      if (data.success && data.orderNumber) {
        setOrderNumber(data.orderNumber);
        setShowSuccess(true);
        setSubmitting(false);
        return;
      }

      // Fallback error
      setErrorTitle(data.title || 'Submit order failed');
      setErrorMessage(data.message || 'Order is not placed. Please try Re-Submitting the Order');
      setShowError(true);
    } catch {
      setErrorTitle('Submit order failed');
      setErrorMessage('Network error. Please try again.');
      setShowError(true);
    } finally {
      setSubmitting(false);
    }
  }

  /**
   * "Submit Offer" handler — called from "Almost Done" modal.
   * Triggers ACT_Offer_SubmitOffer: sets offer to SALES_REVIEW status.
   */
  async function handleSubmitOffer() {
    if (!submittedOfferId) return;
    setSubmitting(true);

    try {
      const userId = getUserId();
      let params = '';
      if (userId) params = `?userId=${userId}`;

      const res = await apiFetch(`${API_BASE}/pws/offers/${submittedOfferId}/submit-offer${params}`, { method: 'POST' });
      const data: SubmitResult = await res.json();

      setShowSalesReview(false);

      if (data.success) {
        setSubmittedOfferNumber(data.offerNumber || data.message || '');
        setShowOfferSubmitted(true);
      } else if (data.alreadySubmitted) {
        setShowAlreadySubmitted(true);
      } else {
        setErrorTitle(data.title || 'Submit offer failed');
        setErrorMessage(data.message || 'Failed to submit offer. Please try again.');
        setShowError(true);
      }
    } catch {
      setShowSalesReview(false);
      setErrorTitle('Submit offer failed');
      setErrorMessage('Network error. Please try again.');
      setShowError(true);
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) {
    return <div className={styles.pageWrapper}>Loading cart...</div>;
  }

  const items = cart?.items || [];
  const totalSkus = items.filter(i => i.quantity > 0).length;
  const totalQty = items.reduce((s, i) => s + i.quantity, 0);
  const totalPrice = items.reduce((s, i) => s + i.price * i.quantity, 0);
  const hasExceedingItems = items.some(i => i.quantity > i.availableQty);

  return (
    <div className={styles.pageWrapper}>
      <div className={styles.contentArea}>
        {/* Header — QA layout: back link, title, cart summary pill + submit + more actions */}
        <div className={styles.pageHeader}>
          <button className={styles.backBtn} onClick={() => router.push('/pws/order')}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="15 18 9 12 15 6"/></svg>
            Back to Shop
          </button>
          <h2 className={styles.pageTitle}>My Offer</h2>
          <div className={styles.cartSummary}>
            <div className={styles.cartStat}>
              <span className={styles.cartStatLabel}>SKUs</span>
              <span className={styles.cartStatValue}>{totalSkus}</span>
            </div>
            <div className={styles.cartStat}>
              <span className={styles.cartStatLabel}>Qty</span>
              <span className={styles.cartStatValue}>{totalQty}</span>
            </div>
            <div className={styles.cartStat}>
              <span className={styles.cartStatLabel}>Total</span>
              <span className={styles.cartStatValue}>${totalPrice.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
            </div>
          </div>
          <button className={styles.submitBtnHeader} onClick={handleSubmit} disabled={submitting || items.length === 0 || hasExceedingItems}>
            {submitting ? 'Submitting...' : 'Submit'}
          </button>
          <div ref={moreMenuRef} style={{ position: 'relative' }}>
            <button className={styles.moreActionsBtn} onClick={() => setMoreMenuOpen(p => !p)}>&#8942;</button>
            {moreMenuOpen && (
              <div className={styles.moreActionsDropdown}>
                <button className={styles.moreActionsItem} onClick={handleExport}>Export Excel</button>
                <button className={styles.moreActionsItem} onClick={() => { setMoreMenuOpen(false); setShowResetConfirm(true); }}>Reset Cart</button>
              </div>
            )}
          </div>
        </div>

        {/* Save error banner */}
        {saveError && (
          <div className={styles.errorBanner} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{saveError}</span>
            <button type="button" onClick={() => setSaveError(null)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'inherit', fontWeight: 'bold' }}>×</button>
          </div>
        )}

        {/* Client-side qty exceed warning */}
        {hasExceedingItems && (
          <div className={styles.errorBanner}>
            {items.filter(i => i.quantity > i.availableQty).length} item(s) exceed available quantity. Please adjust quantities.
          </div>
        )}

        {/* Validation errors / exceeding qty banner */}
        {validationErrors.length > 0 && (
          <div className={styles.errorBanner}>
            {validationErrors.map((err, i) => <div key={i}>{err}</div>)}
          </div>
        )}

        {/* Offer card — QA: .pws-my-offer-card with rounded corners + shadow */}
        <div className={styles.offerCard}>
          {items.length === 0 ? (
            <div className={styles.emptyCart}>
              <h3>There are no items in your cart</h3>
              <p>Return to the Inventory page and enter a quantity to add items to your offer.</p>
              <button className={styles.shopBtn} onClick={() => router.push('/pws/order')}>Go to Shop</button>
            </div>
          ) : (
            <div className={styles.gridContainer}>
              <table className={styles.datagrid}>
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
                    <th>Unit Price</th>
                    <th className={styles.thEditable}>Offer Price</th>
                    <th className={styles.thEditable}>Qty</th>
                    <th className={styles.thEditable}>Total</th>
                    <th className={styles.thRemove}></th>
                  </tr>
                </thead>
                <tbody>
                  {items.map(item => {
                    const isBelowList = item.price < item.listPrice;
                    const isExceeding = exceedingSkus.includes(item.sku);
                    let rowClass = '';
                    if (isExceeding) rowClass = styles.exceedQtyRow;
                    else if (isBelowList) rowClass = styles.belowListPrice;

                    return (
                      <tr key={item.sku} className={rowClass}>
                        <td>{item.sku}</td>
                        <td>{item.brandName || '-'}</td>
                        <td>{item.modelName || '-'}</td>
                        <td>{item.carrierName || '-'}</td>
                        <td>{item.capacityName || '-'}</td>
                        <td>{item.colorName || '-'}</td>
                        <td>{item.gradeName || '-'}</td>
                        <td>{item.availableQty}</td>
                        <td>${item.listPrice?.toFixed(2)}</td>
                        <td className={styles.tdEditable}>
                          <input
                            type="number"
                            className={styles.numberInput}
                            value={item.price || ''}
                            onChange={e => handleItemChange(item, 'price', e.target.value)}
                          />
                        </td>
                        <td className={styles.tdEditable}>
                          <input
                            type="number"
                            className={`${styles.numberInput} ${isExceeding ? styles.inputExceeding : ''}`}
                            value={item.quantity || ''}
                            onChange={e => handleItemChange(item, 'quantity', e.target.value)}
                            max={item.availableQty}
                          />
                        </td>
                        <td className={styles.tdEditable}><strong>${(item.price * item.quantity).toFixed(2)}</strong></td>
                        <td className={styles.tdRemove}>
                          <button className={styles.removeBtn} onClick={() => handleRemoveItem(item.sku)} title="Remove">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* ===== MODALS ===== */}

      {/* Reset Confirm Modal */}
      {showResetConfirm && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalDialog}>
            <h3>Reset Cart</h3>
            <p>Are you sure you want to clear all items from your cart?</p>
            <div className={styles.modalActions}>
              <button className={styles.modalCancel} onClick={() => setShowResetConfirm(false)}>Cancel</button>
              <button className={styles.modalOk} onClick={handleResetCart}>Reset</button>
            </div>
          </div>
        </div>
      )}

      {/* Scenario 1: "Cart Already Submitted" popup (PWS_OfferAlreadySubmitted) */}
      {showAlreadySubmitted && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalDialog}>
            <div className={styles.modalHeaderPws}>
              <h3>Cart Submitted!</h3>
            </div>
            <p>The cart you are attempting to submit has already been submitted in another window.</p>
            <div className={styles.modalActions}>
              <button className={styles.modalCancel} onClick={() => { setShowAlreadySubmitted(false); router.push('/pws/order'); }}>
                Continue Shopping
              </button>
              <button className={styles.modalOk} onClick={() => { setShowAlreadySubmitted(false); router.push('/pws/order'); }}>
                OK
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Scenario 3: "Almost Done!" popup (PWS_AlmostDone) */}
      {showSalesReview && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalDialog}>
            <div className={styles.modalHeaderPws}>
              <h3>Almost Done!</h3>
            </div>
            <p>
              {belowListPriceCount === 1
                ? `${belowListPriceCount} SKU will need to be reviewed by our sales team.`
                : `${belowListPriceCount} SKUs will need to be reviewed by our sales team.`}
            </p>
            <div className={styles.modalActions}>
              <button className={styles.modalCancel} onClick={() => setShowSalesReview(false)}>
                Cancel
              </button>
              <button className={styles.modalOk} onClick={handleSubmitOffer} disabled={submitting}>
                {submitting ? 'Submitting...' : 'Submit Offer'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Scenario 3b: "Offer Submitted" success popup (after ACT_Offer_SubmitOffer) */}
      {showOfferSubmitted && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalDialog}>
            <div className={styles.modalHeaderSuccess}>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#166534" strokeWidth="2"><circle cx="12" cy="12" r="10"/><polyline points="9 12 11.5 14.5 15.5 9.5"/></svg>
              <h3>Offer submitted</h3>
            </div>
            <p>Your offer has been submitted{submittedOfferNumber ? `, offer number: ${submittedOfferNumber}` : ''}. A sales representative will review your offer.</p>
            <div className={styles.modalActions}>
              <button className={styles.modalOk} onClick={() => { setShowOfferSubmitted(false); router.push('/pws/order'); }}>
                OK
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Scenario 4: "Thank you for your order!" success popup (PWS_Message_View success) */}
      {showSuccess && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalDialog}>
            <div className={styles.modalHeaderSuccess}>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#166534" strokeWidth="2"><circle cx="12" cy="12" r="10"/><polyline points="9 12 11.5 14.5 15.5 9.5"/></svg>
              <h3>Thank you for your order!</h3>
            </div>
            <p>Order Number: <strong>{orderNumber}</strong></p>
            <div className={styles.modalActions}>
              <button className={styles.modalOk} onClick={() => { setShowSuccess(false); router.push('/pws/order'); }}>
                Continue Shopping
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Error popup (PWS_Message_View failure) */}
      {showError && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalDialog}>
            <div className={styles.modalHeaderError}>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#991b1b" strokeWidth="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
              <h3>{errorTitle}</h3>
            </div>
            <p>{errorMessage}</p>
            <div className={styles.modalActions}>
              <button className={styles.modalOk} onClick={() => setShowError(false)}>
                OK
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
