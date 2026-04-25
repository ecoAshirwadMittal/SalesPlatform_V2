'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  filterAuctionCodes,
  listBuyerCodes,
  type BuyerCodeSummary,
} from '@/lib/admin/buyerCodes';
import { BuyerCodePicker } from './BuyerCodePicker';

/**
 * BidAsBidder — admin impersonation entry point.
 *
 * Mendix parity: NavMenuPage.BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin.
 * The admin picks an auction-side buyer code from a searchable dropdown;
 * selecting routes to `/bidder/dashboard?buyerCodeId=…`. Backend already
 * authorises Administrator on the bidder dashboard endpoint
 * (BidderDashboardController @PreAuthorize hasAnyRole Bidder/Administrator),
 * so no new backend wiring is required.
 *
 * Shows only AUCTION-side codes — PWS impersonation lives in a separate
 * admin flow that is out of P8 scope.
 */
export default function BidAsBidderPage() {
  const router = useRouter();
  const [codes, setCodes] = useState<BuyerCodeSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const all = await listBuyerCodes();
        if (cancelled) return;
        setCodes(filterAuctionCodes(all));
      } catch (e) {
        if (cancelled) return;
        setError(e instanceof Error ? e.message : 'Failed to load buyer codes');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const handleSelect = (code: BuyerCodeSummary) => {
    // Navigate to the bidder dashboard for the chosen code. The dashboard
    // endpoint is admin-aware and will render the picked buyer's grid.
    router.push(`/bidder/dashboard?buyerCodeId=${code.id}`);
  };

  return (
    <div style={{ padding: '24px', maxWidth: 720 }}>
      <h2
        style={{
          fontSize: 28,
          fontWeight: 300,
          color: '#3c3c3c',
          margin: '0 0 8px',
          fontFamily: "'Brandon Grotesque', 'Open Sans', Arial, sans-serif",
        }}
      >
        Bid as Bidder
      </h2>
      <p
        style={{
          color: '#666',
          margin: '0 0 24px',
          fontFamily: "'Brandon Grotesque', 'Open Sans', Arial, sans-serif",
        }}
      >
        Pick a buyer code to view and submit bids on their behalf.
      </p>

      {error && (
        <div
          role="alert"
          style={{
            padding: '12px 16px',
            background: '#fef2f2',
            color: '#991b1b',
            border: '1px solid #fecaca',
            borderRadius: 4,
            marginBottom: 16,
          }}
        >
          {error}
        </div>
      )}

      {loading ? (
        <div style={{ color: '#666' }}>Loading buyer codes…</div>
      ) : codes.length === 0 ? (
        <div role="status" style={{ color: '#666' }}>
          No auction-side buyer codes available.
        </div>
      ) : (
        <BuyerCodePicker codes={codes} onSelect={handleSelect} />
      )}
    </div>
  );
}
