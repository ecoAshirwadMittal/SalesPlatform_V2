'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import s from './orderDetails.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { formatDate, formatCurrency, formatStatus } from '../orders-helpers';

const API_BASE = '/api/v1/pws';

// ── Types ──

interface OrderDetailBySkuResponse {
  offerItemId: number;
  sku: string | null;
  description: string | null;
  orderedQty: number | null;
  shippedQty: number | null;
  unitPrice: number | null;
  totalPrice: number | null;
}

interface OrderDetailByDeviceResponse {
  imeiDetailId: number;
  imei: string | null;
  sku: string | null;
  description: string | null;
  unitPrice: number | null;
  serialNumber: string | null;
  boxNumber: string | null;
  trackingNumber: string | null;
  trackingUrl: string | null;
}

interface OrderSummary {
  orderNumber: string | null;
  orderStatus: string | null;
  offerDate: string | null;
  orderDate: string | null;
  offerOrderType: string | null;
  buyer: string | null;
  company: string | null;
  shipDate: string | null;
  shipMethod: string | null;
  skuCount: number;
  totalQuantity: number;
  totalPrice: number;
}

type ViewMode = 'BySKU' | 'ByDevice';

// ── Status badge class mapping ──

function statusClass(status: string | null): string {
  if (!status) return s.statusNone;
  const display = formatStatus(status);
  switch (display) {
    case 'Offer Pending':
      return s.statusOfferPending;
    case 'Shipped':
      return s.statusShipped;
    case 'Order Cancelled':
      return s.statusCancelled;
    default:
      return s.statusNone;
  }
}

// ── Main Component ──

export default function OrderDetailPage() {
  const router = useRouter();
  const params = useParams();
  const offerId = params.id as string;

  const [activeView, setActiveView] = useState<ViewMode>('BySKU');
  const [orderSummary, setOrderSummary] = useState<OrderSummary | null>(null);
  const [skuData, setSkuData] = useState<OrderDetailBySkuResponse[]>([]);
  const [deviceData, setDeviceData] = useState<OrderDetailByDeviceResponse[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch order summary from the list endpoint (find this order)
  const fetchSummary = useCallback(async () => {
    try {
      const stored = localStorage.getItem('auth_user');
      if (!stored) return;
      const userId = JSON.parse(stored).userId;
      if (!userId) return;

      const res = await apiFetch(
        `${API_BASE}/orders?tab=all&userId=${userId}&page=0&size=1000`
      );
      if (res.ok) {
        const json = await res.json();
        const match = json.content?.find(
          (o: { offerId: number }) => String(o.offerId) === offerId
        );
        if (match) {
          setOrderSummary({
            orderNumber: match.orderNumber,
            orderStatus: match.orderStatus,
            offerDate: match.offerDate,
            orderDate: match.orderDate,
            offerOrderType: match.offerOrderType,
            buyer: match.buyer,
            company: match.company,
            shipDate: match.shipDate,
            shipMethod: match.shipMethod,
            skuCount: match.skuCount ?? 0,
            totalQuantity: match.totalQuantity ?? 0,
            totalPrice: match.totalPrice ?? 0,
          });
        }
      }
    } catch (err) {
      console.error('Failed to load order summary:', err);
    }
  }, [offerId]);

  // Fetch By SKU data
  const fetchSkuData = useCallback(async () => {
    try {
      const res = await apiFetch(`${API_BASE}/orders/${offerId}/details/by-sku`);
      if (res.ok) {
        setSkuData(await res.json());
      }
    } catch (err) {
      console.error('Failed to load SKU details:', err);
    }
  }, [offerId]);

  // Fetch By Device data
  const fetchDeviceData = useCallback(async () => {
    try {
      const res = await apiFetch(`${API_BASE}/orders/${offerId}/details/by-device`);
      if (res.ok) {
        setDeviceData(await res.json());
      }
    } catch (err) {
      console.error('Failed to load device details:', err);
    }
  }, [offerId]);

  // On mount: fetch all data
  useEffect(() => {
    setLoading(true);
    Promise.all([fetchSummary(), fetchSkuData(), fetchDeviceData()])
      .finally(() => setLoading(false));
  }, [fetchSummary, fetchSkuData, fetchDeviceData]);

  // Strikethrough for declined/cancelled (check external status text)
  const externalStatus = formatStatus(orderSummary?.orderStatus);
  const isStrikethrough =
    externalStatus === 'Offer Declined' ||
    externalStatus === 'Order Cancelled';

  const priceClass = isStrikethrough
    ? `${s.colRight} ${s.strikethrough}`
    : s.colRight;
  const qtyClass = isStrikethrough
    ? `${s.colCenter} ${s.strikethrough}`
    : s.colCenter;

  if (loading) {
    return <div className={s.loadingState}>Loading order details...</div>;
  }

  return (
    <div className={s.pageContainer}>
      {/* ── Back link ── */}
      <button className={s.backLink} onClick={() => router.push('/pws/orders')}>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polyline points="15 18 9 12 15 6" />
        </svg>
        Order History
      </button>

      {/* ── Details card ── */}
      <div className={s.detailsCard}>
        <h1 className={s.cardTitle}>Order Details</h1>

        {/* ── Metadata row 1 ── */}
        {orderSummary && (
          <>
            <div className={s.metaRow}>
              <div className={s.metaFields}>
                <div className={s.metaField}>
                  <div className={s.metaLabel}>Order Number</div>
                  <div className={s.metaValue}>{orderSummary.orderNumber || '---'}</div>
                </div>
                <div className={s.metaField}>
                  <div className={s.metaLabel}>Order Date</div>
                  <div className={s.metaValue}>{formatDate(orderSummary.orderDate) || '---'}</div>
                </div>
                <div className={s.metaField}>
                  <div className={s.metaLabel}>Buyer</div>
                  <div className={s.metaValue}>{orderSummary.buyer || '---'}</div>
                </div>
                <div className={s.metaField}>
                  <div className={s.metaLabel}>Company</div>
                  <div className={s.metaValue}>{orderSummary.company || '---'}</div>
                </div>
              </div>

              {/* ── Order Summary box ── */}
              <div className={s.orderSummaryBox}>
                <div className={s.summaryTitle}>Order Summary</div>
                <table className={s.summaryTable}>
                  <thead>
                    <tr>
                      <th>SKUs</th>
                      <th>Qty</th>
                      <th>Price</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>{orderSummary.skuCount}</td>
                      <td>{orderSummary.totalQuantity}</td>
                      <td>{formatCurrency(orderSummary.totalPrice)}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            {/* ── Metadata row 2 ── */}
            <div className={s.metaFields}>
              <div className={s.metaField}>
                <div className={s.metaLabel}>Status</div>
                <span className={`${s.statusBadge} ${statusClass(orderSummary.orderStatus)}`}>
                  {formatStatus(orderSummary.orderStatus)}
                </span>
              </div>
              <div className={s.metaField}>
                <div className={s.metaLabel}>Ship Date</div>
                <div className={s.metaValue}>{formatDate(orderSummary.shipDate) || '---'}</div>
              </div>
              <div className={s.metaField}>
                <div className={s.metaLabel}>Ship Method</div>
                <div className={s.metaValue}>{orderSummary.shipMethod || '---'}</div>
              </div>
              <div className={s.metaField}>
                <div className={s.metaLabel}>Ship To</div>
                <div className={s.metaValue}>---</div>
              </div>
            </div>
          </>
        )}

        {/* ── View toggle + Download ── */}
        <div className={s.actionBar}>
          <div className={s.viewToggle}>
            <button
              className={`${s.viewBtn} ${activeView === 'BySKU' ? s.viewBtnActive : ''}`}
              onClick={() => setActiveView('BySKU')}
            >
              By SKU
            </button>
            <button
              className={`${s.viewBtn} ${activeView === 'ByDevice' ? s.viewBtnActive : ''}`}
              onClick={() => setActiveView('ByDevice')}
            >
              By Device
            </button>
          </div>
          <button className={s.downloadBtn}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
              <polyline points="7 10 12 15 17 10" />
              <line x1="12" y1="15" x2="12" y2="3" />
            </svg>
            Download
          </button>
        </div>

        {/* ── By SKU Grid ── */}
        {activeView === 'BySKU' && (
          <>
            <table className={s.dataGrid}>
              <thead>
                <tr>
                  <th>SKU</th>
                  <th>Description</th>
                  <th className={s.colCenter}>Ordered Qty</th>
                  <th className={s.colCenter}>Shipped Qty</th>
                  <th className={s.colRight}>Unit Price</th>
                  <th className={s.colRight}>Total Price</th>
                </tr>
              </thead>
              <tbody>
                {skuData.length === 0 ? (
                  <tr>
                    <td colSpan={6} className={s.emptyState}>
                      No items found for this order.
                    </td>
                  </tr>
                ) : (
                  skuData.map((row) => (
                    <tr key={row.offerItemId}>
                      <td>{row.sku || '---'}</td>
                      <td>{row.description || '---'}</td>
                      <td className={qtyClass}>{row.orderedQty ?? 0}</td>
                      <td className={qtyClass}>{row.shippedQty != null ? row.shippedQty : '--'}</td>
                      <td className={priceClass}>{formatCurrency(row.unitPrice)}</td>
                      <td className={priceClass}>{formatCurrency(row.totalPrice)}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            {/* ── Pagination ── */}
            {skuData.length > 0 && (
              <div className={s.pagination}>
                <span className={s.pageInfo}>1 to {skuData.length} of {skuData.length}</span>
              </div>
            )}
          </>
        )}

        {/* ── By Device Grid ── */}
        {activeView === 'ByDevice' && (
          <>
            <table className={s.dataGrid}>
              <thead>
                <tr>
                  <th>IMEI</th>
                  <th>SKU</th>
                  <th>Description</th>
                  <th className={s.colRight}>Unit Price</th>
                  <th>Serial Number</th>
                  <th>Box Number</th>
                  <th>Tracking Number</th>
                </tr>
              </thead>
              <tbody>
                {deviceData.length === 0 ? (
                  <tr>
                    <td colSpan={7} className={s.emptyState}>
                      No device details found for this order.
                    </td>
                  </tr>
                ) : (
                  deviceData.map((row) => (
                    <tr key={row.imeiDetailId}>
                      <td>{row.imei || '---'}</td>
                      <td>{row.sku || '---'}</td>
                      <td>{row.description || '---'}</td>
                      <td className={isStrikethrough ? `${s.colRight} ${s.strikethrough}` : s.colRight}>
                        {formatCurrency(row.unitPrice)}
                      </td>
                      <td>{row.serialNumber || '---'}</td>
                      <td>{row.boxNumber || '---'}</td>
                      <td>
                        {row.trackingUrl ? (
                          <a
                            href={row.trackingUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className={s.trackingLink}
                          >
                            {row.trackingNumber || 'Track'}
                          </a>
                        ) : (
                          row.trackingNumber || '---'
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            {deviceData.length > 0 && (
              <div className={s.pagination}>
                <span className={s.pageInfo}>1 to {deviceData.length} of {deviceData.length}</span>
              </div>
            )}
          </>
        )}

        {/* ── Invoice footer ── */}
        <div className={s.invoiceFooter}>
          <em>This is not an invoice. Your invoice will be emailed.</em>
        </div>
      </div>
    </div>
  );
}
