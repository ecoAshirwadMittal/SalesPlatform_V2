'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import styles from './counterOffers.module.css';
import { apiFetch } from '@/lib/apiFetch';
import { getBuyerCodeId } from '@/lib/session';
import { API_BASE } from '@/lib/apiRoutes';


interface OfferListItem {
  offerId: number;
  offerNumber: string;
  status: string;
  buyerCode: string;
  totalSkus: number;
  totalQty: number;
  totalPrice: number;
  submissionDate: string;
  updatedDate: string;
}

function formatDate(iso: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', { month: 'numeric', day: 'numeric', year: 'numeric' });
}

function fmt(n: number | null | undefined): string {
  if (n == null) return '$0.00';
  return '$' + Number(n).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

export default function CounterOffersPage() {
  const router = useRouter();
  const [offers, setOffers] = useState<OfferListItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const buyerCodeId = getBuyerCodeId();
    if (!buyerCodeId) {
      setLoading(false);
      return;
    }

    apiFetch(`${API_BASE}/pws/counter-offers?buyerCodeId=${buyerCodeId}`)
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((data: OfferListItem[]) => {
        if (!Array.isArray(data)) { setLoading(false); return; }
        setOffers(data);
        if (data.length === 1) {
          router.replace(`/pws/counter-offers/${data[0].offerId}`);
          return;
        }
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [router]);

  if (loading) {
    return <div className={styles.pageContainer}><div className={styles.loading}>Loading counter offers...</div></div>;
  }

  if (offers.length === 0) {
    return (
      <div className={styles.pageContainer}>
        <div className={styles.pageHeader}>
          <h1 className={styles.pageTitle}>Counter Offers</h1>
        </div>
        <div className={styles.emptyState}>
          <h2>No pending counter offers</h2>
          <p>There are no counter offers available for your buyer code.</p>
          <button className={styles.shopLink} onClick={() => router.push('/pws/order')}>
            Go to Shop
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.pageContainer}>
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>Counter Offers</h1>
      </div>
      <div className={styles.gridContainer}>
        <table className={styles.offerListGrid}>
          <thead>
            <tr>
              <th>Offer #</th>
              <th>Status</th>
              <th>Buyer Code</th>
              <th>SKUs</th>
              <th>Qty</th>
              <th>Total Price</th>
              <th>Submitted</th>
              <th>Last Updated</th>
            </tr>
          </thead>
          <tbody>
            {offers.map(offer => (
              <tr key={offer.offerId} data-offer-id={offer.offerId} onClick={() => router.push(`/pws/counter-offers/${offer.offerId}`)}>
                <td><span className={styles.offerIdLink}>{offer.offerNumber || offer.offerId}</span></td>
                <td>{offer.status?.replace(/_/g, ' ')}</td>
                <td>{offer.buyerCode}</td>
                <td>{offer.totalSkus}</td>
                <td>{offer.totalQty}</td>
                <td>{fmt(offer.totalPrice)}</td>
                <td>{formatDate(offer.submissionDate)}</td>
                <td>{formatDate(offer.updatedDate)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
