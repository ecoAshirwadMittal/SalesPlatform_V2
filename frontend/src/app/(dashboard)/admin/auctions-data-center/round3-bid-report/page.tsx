'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/apiFetch';
import {
  fetchRound3ReportByWeek,
  type Round3ReportResponse,
} from '@/lib/admin/round3Report';

/**
 * Round 3 Bid Report by Buyer — admin read-only view.
 *
 * Mendix parity: {@code RoundThreeBidReportPage}. Pure browse:
 *   1. Pick a week from the dropdown (loaded from
 *      {@code GET /api/v1/admin/inventory/weeks} — same dataset
 *      AggregatedInventoryPage uses, so weeks stay consistent across
 *      admin surfaces).
 *   2. The grid loads all R3 report rows for auctions in that week.
 *   3. Empty week → "No data" empty state, NOT an error.
 *
 * Decimals come back as JSON numbers; we format defensively against null.
 */

interface WeekOption {
  id: number;
  weekDisplay: string;
}

export default function Round3BidReportPage() {
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [selectedWeek, setSelectedWeek] = useState<number | null>(null);
  const [report, setReport] = useState<Round3ReportResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load week dropdown options on mount.
  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const res = await apiFetch('/api/v1/admin/inventory/weeks');
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = (await res.json()) as WeekOption[];
        if (cancelled) return;
        setWeeks(data);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : 'Failed to load weeks');
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  // Load the report whenever the selected week changes.
  useEffect(() => {
    if (selectedWeek == null) {
      setReport(null);
      return;
    }
    let cancelled = false;
    setLoading(true);
    setError(null);
    (async () => {
      try {
        const data = await fetchRound3ReportByWeek(selectedWeek);
        if (!cancelled) setReport(data);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : 'Failed to load report');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [selectedWeek]);

  return (
    <div style={{ padding: '24px' }}>
      <h2
        style={{
          fontSize: 28,
          fontWeight: 300,
          color: '#3c3c3c',
          margin: '0 0 8px',
          fontFamily: "'Brandon Grotesque', 'Open Sans', Arial, sans-serif",
        }}
      >
        Round Three Bid Report by Buyer
      </h2>
      <p
        style={{
          color: '#666',
          margin: '0 0 24px',
          fontFamily: "'Brandon Grotesque', 'Open Sans', Arial, sans-serif",
        }}
      >
        View the per-buyer R3 submission audit for a given week.
      </p>

      <div style={{ marginBottom: 16, display: 'flex', gap: 12, alignItems: 'center' }}>
        <label htmlFor="week-select" style={{ fontWeight: 500 }}>
          Week
        </label>
        <select
          id="week-select"
          aria-label="Week"
          value={selectedWeek ?? ''}
          onChange={(e) => setSelectedWeek(e.target.value ? Number(e.target.value) : null)}
          style={{
            padding: '8px 10px',
            fontSize: 14,
            border: '1px solid #ccc',
            borderRadius: 4,
            minWidth: 220,
          }}
        >
          <option value="">-- Choose a week --</option>
          {weeks.map((w) => (
            <option key={w.id} value={w.id}>
              {w.weekDisplay}
            </option>
          ))}
        </select>
      </div>

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

      {loading && <div style={{ color: '#666' }}>Loading report…</div>}

      {report && !loading && (
        <ReportGrid report={report} />
      )}

      {selectedWeek == null && !loading && !error && (
        <div role="status" style={{ color: '#666' }}>
          Pick a week to load its R3 report.
        </div>
      )}
    </div>
  );
}

function ReportGrid({ report }: { report: Round3ReportResponse }) {
  if (report.count === 0) {
    return (
      <div role="status" data-testid="r3-empty-state" style={{ color: '#666' }}>
        No data for the selected week.
      </div>
    );
  }

  return (
    <table
      aria-label="R3 buyer report"
      style={{ width: '100%', borderCollapse: 'collapse', background: '#fff' }}
    >
      <thead>
        <tr style={{ background: '#f7f5f1', textAlign: 'left' }}>
          <th style={cellStyle}>Buyer Code</th>
          <th style={cellStyle}>Company</th>
          <th style={cellStyleRight}>Total Quantity</th>
          <th style={cellStyleRight}>Total Payout</th>
          <th style={cellStyle}>Submitted</th>
        </tr>
      </thead>
      <tbody>
        {report.rows.map((r) => (
          <tr key={r.id}>
            <td style={cellStyle}>{r.buyerCode ?? '—'}</td>
            <td style={cellStyle}>{r.companyName ?? '—'}</td>
            <td style={cellStyleRight}>{r.totalQuantity ?? '—'}</td>
            <td style={cellStyleRight}>
              {r.totalPayout != null ? `$${r.totalPayout.toFixed(2)}` : '—'}
            </td>
            <td style={cellStyle}>
              {r.submittedDatetime
                ? new Date(r.submittedDatetime).toLocaleString()
                : '—'}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

const cellStyle: React.CSSProperties = {
  padding: '10px 12px',
  borderBottom: '1px solid #eee',
  fontSize: 14,
};

const cellStyleRight: React.CSSProperties = {
  ...cellStyle,
  textAlign: 'right',
};
