'use client';

/**
 * P8 Lane 3B — admin Qualified Buyer Codes view + manual qualify/unqualify.
 *
 * Mirrors qa-playwright-salesplatform's ACC_QualifiedBuyerCodePage scope:
 * admin selects a scheduling auction, sees one row per qualified buyer code
 * with the included flag + qualification_type, and toggles the included
 * checkbox to qualify/unqualify. Side-effect: PATCH forces
 * `qualification_type='Manual'` so subsequent auto-recompute jobs preserve
 * the admin's choice.
 */

import { useCallback, useState } from 'react';
import {
  qualifiedBuyerCodesClient,
  type QualifiedBuyerCodeAdminRow,
} from '@/lib/qualifiedBuyerCodesClient';

export default function QualifiedBuyerCodesAdminPage() {
  const [schedulingAuctionIdInput, setSchedulingAuctionIdInput] = useState<string>('');
  const [rows, setRows] = useState<QualifiedBuyerCodeAdminRow[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [hasLoaded, setHasLoaded] = useState<boolean>(false);

  const apply = useCallback(async () => {
    const trimmed = schedulingAuctionIdInput.trim();
    if (!trimmed) {
      setError('Scheduling Auction Id is required');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const resp = await qualifiedBuyerCodesClient.list(Number(trimmed));
      setRows(resp.rows);
      setHasLoaded(true);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load qualified buyer codes');
    } finally {
      setLoading(false);
    }
  }, [schedulingAuctionIdInput]);

  const handleToggle = useCallback(
    async (id: number, nextIncluded: boolean) => {
      try {
        const updated = await qualifiedBuyerCodesClient.updateIncluded(id, nextIncluded);
        // Replace the affected row with the server-truth result so the
        // qualification_type cell reflects the forced 'Manual' transition.
        setRows((prev) =>
          prev.map((r) => (r.id === id ? { ...r, ...updated } : r)),
        );
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Update failed');
      }
    },
    [],
  );

  return (
    <div style={{ padding: '1.5rem' }}>
      <h1>Qualified Buyer Codes</h1>
      <p style={{ color: '#666' }}>
        Toggle the <em>Included</em> flag to qualify or unqualify a buyer for
        the chosen scheduling auction. The qualification_type column flips to{' '}
        <code>Manual</code> automatically — auto-recompute jobs skip Manual rows.
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
          Scheduling Auction Id
          <input
            aria-label="Scheduling Auction Id"
            type="number"
            value={schedulingAuctionIdInput}
            onChange={(e) => setSchedulingAuctionIdInput(e.target.value)}
          />
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
        <div role="status">No qualified buyer codes for that auction.</div>
      )}

      {rows.length > 0 && (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th align="left">Buyer Code</th>
              <th align="left">Qualification Type</th>
              <th align="left">Special Treatment</th>
              <th align="left">Included</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} data-testid={`qbc-row-${r.id}`}>
                <td data-testid="buyer-code">{r.buyerCode ?? '—'}</td>
                <td data-testid="qualification-type">{r.qualificationType}</td>
                <td>{r.specialTreatment ? 'Yes' : 'No'}</td>
                <td>
                  <label
                    style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}
                  >
                    <input
                      type="checkbox"
                      aria-label="Included"
                      checked={r.included}
                      onChange={(e) => handleToggle(r.id, e.target.checked)}
                    />
                    {r.included ? 'Included' : 'Excluded'}
                  </label>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
