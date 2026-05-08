'use client';

/**
 * Inventory preview table embedded under the round inputs on the schedule
 * editor (gap H7). QA's Mendix scheduling page rendered the auction's
 * inventory in a paginated grid below the round date pickers so SalesOps
 * could double-check what was being scheduled before clicking Confirm.
 *
 * Reuses the existing /api/v1/admin/inventory endpoint scoped by week_id —
 * the auction's week is the one the rounds operate on, so no new backend
 * surface is needed. Read-only, no filter, no edit; the full Inventory
 * page remains the editor.
 */

import { useEffect, useState } from 'react';
import {
  fetchInventoryPage,
  type InventoryPageResponse,
  type InventoryRow,
} from '@/lib/aggregatedInventory';

const PAGE_SIZE = 20;

const usd = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 2,
});
const num = new Intl.NumberFormat('en-US');

interface InventoryPreviewProps {
  weekId: number;
}

export function InventoryPreview({ weekId }: InventoryPreviewProps) {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<InventoryPageResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    (async () => {
      try {
        const res = await fetchInventoryPage({ weekId, page, pageSize: PAGE_SIZE });
        if (!cancelled) setData(res);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : 'Failed to load inventory');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [weekId, page]);

  return (
    <section style={cardStyle} aria-labelledby="inv-preview-title">
      <header style={headerStyle}>
        <h2 id="inv-preview-title" style={titleStyle}>Inventory preview</h2>
        {data && (
          <span style={countStyle}>
            {data.totalElements === 0
              ? 'No rows'
              : `${num.format(page * PAGE_SIZE + 1)}–${num.format(
                  Math.min((page + 1) * PAGE_SIZE, data.totalElements)
                )} of ${num.format(data.totalElements)}`}
          </span>
        )}
      </header>

      {loading && !data && <p style={muted}>Loading inventory…</p>}
      {error && <p style={errorStyle}>Inventory preview failed: {error}</p>}

      {data && data.content.length > 0 && (
        <div style={tableWrap}>
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>Product ID</th>
                <th style={thStyle}>Grades</th>
                <th style={thStyle}>Brand</th>
                <th style={thStyle}>Model</th>
                <th style={thStyle}>Model Name</th>
                <th style={thStyle}>Carrier</th>
                <th style={thRight}>DW Qty</th>
                <th style={thRight}>DW Tgt Price</th>
                <th style={thRight}>Total Qty</th>
                <th style={thRight}>Tgt Price</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((r) => (
                <Row key={r.id} row={r} />
              ))}
            </tbody>
          </table>
        </div>
      )}

      {data && data.content.length === 0 && (
        <p style={muted}>No inventory rows for this week.</p>
      )}

      {data && data.totalPages > 1 && (
        <div style={paginationStyle}>
          <PageButton disabled={page === 0} onClick={() => setPage(0)}>«</PageButton>
          <PageButton disabled={page === 0} onClick={() => setPage(page - 1)}>‹</PageButton>
          <span style={{ fontSize: 13, color: '#514F4E' }}>
            Page {page + 1} of {data.totalPages}
          </span>
          <PageButton disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>›</PageButton>
          <PageButton disabled={page + 1 >= data.totalPages} onClick={() => setPage(data.totalPages - 1)}>»</PageButton>
        </div>
      )}
    </section>
  );
}

function Row({ row }: { row: InventoryRow }) {
  return (
    <tr style={{ borderTop: '1px solid #eee' }}>
      <td style={tdStyle}>{row.ecoid2}</td>
      <td style={tdStyle}>{row.mergedGrade ?? <em style={muted}>—</em>}</td>
      <td style={tdStyle}>{row.brand ?? '—'}</td>
      <td style={tdStyle}>{row.model ?? '—'}</td>
      <td style={tdStyle}>{row.name ?? '—'}</td>
      <td style={tdStyle}>{row.carrier ?? '—'}</td>
      <td style={tdRight}>{num.format(row.dwTotalQuantity)}</td>
      <td style={tdRight}>{usd.format(row.dwAvgTargetPrice)}</td>
      <td style={tdRight}>{num.format(row.totalQuantity)}</td>
      <td style={tdRight}>{usd.format(row.avgTargetPrice)}</td>
    </tr>
  );
}

function PageButton({
  disabled,
  onClick,
  children,
}: {
  disabled: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      style={{
        width: 28,
        height: 26,
        border: '1px solid #cfd1d3',
        background: disabled ? '#f5f7f8' : '#fff',
        color: disabled ? '#bdbdbd' : '#112d32',
        borderRadius: 4,
        cursor: disabled ? 'default' : 'pointer',
        font: 'inherit',
      }}
    >
      {children}
    </button>
  );
}

const cardStyle: React.CSSProperties = {
  border: '1px solid #e3e6e8',
  borderRadius: 6,
  background: '#fff',
  padding: '0.75rem 1rem',
  marginTop: '1rem',
};
const headerStyle: React.CSSProperties = {
  display: 'flex',
  alignItems: 'baseline',
  justifyContent: 'space-between',
  marginBottom: '0.5rem',
};
const titleStyle: React.CSSProperties = { margin: 0, fontSize: 14, color: '#514F4E', fontWeight: 600 };
const countStyle: React.CSSProperties = { fontSize: 12, color: '#7a7a7a' };
const tableWrap: React.CSSProperties = { overflow: 'auto', borderTop: '1px solid #eee' };
const tableStyle: React.CSSProperties = { width: '100%', borderCollapse: 'collapse', fontSize: 13 };
const thStyle: React.CSSProperties = {
  textAlign: 'left',
  padding: '0.4rem 0.5rem',
  background: '#f5f7f8',
  color: '#514F4E',
  fontSize: 11,
  fontWeight: 600,
  borderBottom: '1px solid #e3e6e8',
  whiteSpace: 'nowrap',
};
const thRight: React.CSSProperties = { ...thStyle, textAlign: 'right' };
const tdStyle: React.CSSProperties = { padding: '0.4rem 0.5rem', color: '#112d32', whiteSpace: 'nowrap' };
const tdRight: React.CSSProperties = { ...tdStyle, textAlign: 'right' };
const paginationStyle: React.CSSProperties = {
  display: 'flex',
  alignItems: 'center',
  gap: '0.4rem',
  marginTop: '0.5rem',
};
const muted: React.CSSProperties = { color: '#7a7a7a', fontStyle: 'italic' };
const errorStyle: React.CSSProperties = { color: '#a31b1b', fontSize: 13 };
