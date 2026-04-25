'use client';

/**
 * P8 Lane 3A — admin Bid Data view + remove.
 *
 * Mirrors qa-playwright-salesplatform's ACC_BidDataPage scope: admin selects
 * a (round, buyerCode) pair, optionally narrows to submitted-only rows, and
 * can soft-delete a row via the per-row "Remove" action. The Remove call
 * goes through the backend's audit-writing service so each admin action is
 * traceable per row.
 */

import { useCallback, useState } from 'react';
import {
  bidDataAdminClient,
  type BidDataAdminRow,
} from '@/lib/bidDataAdminClient';

export default function BidDataAdminPage() {
  const [bidRoundIdInput, setBidRoundIdInput] = useState<string>('');
  const [buyerCodeIdInput, setBuyerCodeIdInput] = useState<string>('');
  const [submittedOnly, setSubmittedOnly] = useState<boolean>(false);

  const [rows, setRows] = useState<BidDataAdminRow[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [hasLoaded, setHasLoaded] = useState<boolean>(false);

  const apply = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const bidRoundId = bidRoundIdInput.trim() ? Number(bidRoundIdInput.trim()) : undefined;
      const buyerCodeId = buyerCodeIdInput.trim() ? Number(buyerCodeIdInput.trim()) : undefined;
      const resp = await bidDataAdminClient.list({
        bidRoundId,
        buyerCodeId,
        submittedBidAmountGt0: submittedOnly,
      });
      setRows(resp.rows);
      setHasLoaded(true);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load bid data');
    } finally {
      setLoading(false);
    }
  }, [bidRoundIdInput, buyerCodeIdInput, submittedOnly]);

  const handleRemove = useCallback(
    async (id: number) => {
      // Optimistic-removal would let the row disappear before the audit write
      // confirms; we keep it server-truth-first instead. Re-list after the
      // DELETE so any concurrent admin's edits are also picked up.
      try {
        await bidDataAdminClient.remove(id);
        await apply();
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Remove failed');
      }
    },
    [apply],
  );

  return (
    <div style={{ padding: '1.5rem' }}>
      <h1>Bid Data</h1>
      <p style={{ color: '#666' }}>
        View bid_data rows by (round, buyerCode). Removed rows are
        soft-deleted and recorded in <code>auctions.bid_data_audit</code>.
      </p>

      <div
        style={{
          display: 'flex',
          gap: '1rem',
          alignItems: 'flex-end',
          margin: '1rem 0',
          flexWrap: 'wrap',
        }}
      >
        <label style={{ display: 'flex', flexDirection: 'column' }}>
          Bid Round Id
          <input
            aria-label="Bid Round Id"
            type="number"
            value={bidRoundIdInput}
            onChange={(e) => setBidRoundIdInput(e.target.value)}
          />
        </label>
        <label style={{ display: 'flex', flexDirection: 'column' }}>
          Buyer Code Id
          <input
            aria-label="Buyer Code Id"
            type="number"
            value={buyerCodeIdInput}
            onChange={(e) => setBuyerCodeIdInput(e.target.value)}
          />
        </label>
        <label
          style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}
        >
          <input
            aria-label="Submitted only"
            type="checkbox"
            checked={submittedOnly}
            onChange={(e) => setSubmittedOnly(e.target.checked)}
          />
          Submitted only
        </label>
        <button type="button" onClick={apply} disabled={loading}>
          Apply
        </button>
      </div>

      {error && (
        <div role="alert" style={{ color: 'crimson', margin: '0.5rem 0' }}>
          {error}
        </div>
      )}

      {loading && <div>Loading…</div>}

      {hasLoaded && rows.length === 0 && !loading && (
        <div role="status">No bid data matched the filters.</div>
      )}

      {rows.length > 0 && (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th align="left">Id</th>
              <th align="left">Round</th>
              <th align="left">Buyer</th>
              <th align="left">Ecoid</th>
              <th align="left">Grade</th>
              <th align="right">Bid Qty</th>
              <th align="right">Bid Amount</th>
              <th align="right">Submitted Qty</th>
              <th align="right">Submitted Amount</th>
              <th align="left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} data-testid={`bid-data-row-${r.id}`}>
                <td>{r.id}</td>
                <td>{r.bidRoundId}</td>
                <td>{r.buyerCodeId}</td>
                <td>{r.ecoid}</td>
                <td>{r.mergedGrade}</td>
                <td align="right">{r.bidQuantity ?? '—'}</td>
                <td align="right">{r.bidAmount ?? '—'}</td>
                <td align="right">{r.submittedBidQuantity ?? '—'}</td>
                <td align="right">{r.submittedBidAmount ?? '—'}</td>
                <td>
                  <button type="button" onClick={() => handleRemove(r.id)}>
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
