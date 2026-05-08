'use client';

/**
 * R2 Qualified Buyer Codes — admin read-only result view (gap H10).
 *
 * The R2 criteria editor (Round 2 Selection Rules) lets the admin
 * configure WHICH inventory + buyer-code rules apply. This page surfaces
 * the RESULT of running the R2 buyer-assignment service against those
 * rules for a specific auction — sortable / filterable list of
 * `qualified_buyer_codes` rows joined to the buyer code + company.
 *
 * Mendix had no equivalent dedicated page (gap-analysis #4); the legacy
 * admin had to inspect the database to audit qualification outcomes.
 */

import { useEffect, useMemo, useState } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import {
  fetchR2QualifiedBuyers,
  type R2QualifiedBuyerRow,
  type R2QualifiedBuyersResponse,
} from '@/lib/admin/r2QualifiedBuyers';

type Filter = 'all' | 'qualified' | 'special' | 'not-qualified';

export default function R2QualifiedBuyersPage() {
  const params = useParams<{ auctionId: string }>();
  const auctionId = Number(params?.auctionId);

  const [data, setData] = useState<R2QualifiedBuyersResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<Filter>('all');
  const [search, setSearch] = useState('');

  useEffect(() => {
    if (!Number.isFinite(auctionId)) {
      setError('Invalid auctionId');
      setLoading(false);
      return;
    }
    let cancelled = false;
    setLoading(true);
    setError(null);
    (async () => {
      try {
        const res = await fetchR2QualifiedBuyers(auctionId);
        if (!cancelled) setData(res);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : 'Failed to load');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [auctionId]);

  const filtered = useMemo(() => {
    if (!data) return [];
    const term = search.trim().toLowerCase();
    return data.rows.filter((r) => {
      if (filter === 'qualified' && !(r.qualificationType === 'Qualified' && !r.isSpecialTreatment)) {
        return false;
      }
      if (filter === 'special' && !r.isSpecialTreatment) {
        return false;
      }
      if (filter === 'not-qualified' && r.qualificationType !== 'Not_Qualified') {
        return false;
      }
      if (term && !`${r.code} ${r.companyName}`.toLowerCase().includes(term)) {
        return false;
      }
      return true;
    });
  }, [data, filter, search]);

  return (
    <div style={pageStyle}>
      <Link
        href={`/admin/auctions-data-center/auctions/${auctionId}/schedule`}
        style={backLinkStyle}
      >
        ← Back to schedule
      </Link>
      <h1 style={titleStyle}>R2 Qualified Buyer Codes</h1>
      {data && (
        <p style={subtitleStyle}>
          {data.auctionTitle} · R2 SA {data.r2SchedulingAuctionId}
          {data.r2InitStatus && (
            <>
              {' · '}
              <span style={statusPillStyle(data.r2InitStatus)}>{data.r2InitStatus}</span>
            </>
          )}
        </p>
      )}

      {loading && <p>Loading…</p>}
      {error && <p style={errorStyle}>Error: {error}</p>}

      {data && (
        <>
          <div style={summaryRowStyle}>
            <SummaryCard label="Total" value={data.totalRows} />
            <SummaryCard label="Qualified (regular)" value={data.qualifiedCount} accent="#16785f" />
            <SummaryCard label="Special-treatment" value={data.specialTreatmentCount} accent="#bf6c05" />
            <SummaryCard label="Not Qualified" value={data.notQualifiedCount} accent="#7a7a7a" />
          </div>

          <div style={controlsRowStyle}>
            <input
              type="search"
              placeholder="Filter by code or company…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={searchStyle}
            />
            <FilterButton current={filter} value="all" onClick={setFilter}>
              All
            </FilterButton>
            <FilterButton current={filter} value="qualified" onClick={setFilter}>
              Qualified
            </FilterButton>
            <FilterButton current={filter} value="special" onClick={setFilter}>
              Special
            </FilterButton>
            <FilterButton current={filter} value="not-qualified" onClick={setFilter}>
              Not Qualified
            </FilterButton>
            <span style={{ marginLeft: 'auto', color: '#7a7a7a', fontSize: 13 }}>
              Showing {filtered.length} of {data.totalRows}
            </span>
          </div>

          <div style={tableWrapStyle}>
            <table style={tableStyle}>
              <thead>
                <tr>
                  <th style={thStyle}>Code</th>
                  <th style={thStyle}>Type</th>
                  <th style={thStyle}>Company</th>
                  <th style={thStyle}>Qualification</th>
                  <th style={thStyle}>Included</th>
                  <th style={thStyle}>Special Treatment</th>
                </tr>
              </thead>
              <tbody>
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={6} style={{ padding: '1rem', textAlign: 'center', color: '#7a7a7a' }}>
                      No buyer codes match the current filter.
                    </td>
                  </tr>
                )}
                {filtered.map((row) => (
                  <Row key={row.buyerCodeId} row={row} />
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}

function Row({ row }: { row: R2QualifiedBuyerRow }) {
  return (
    <tr style={{ borderTop: '1px solid #eee' }}>
      <td style={tdStyle}>{row.code}</td>
      <td style={tdStyle}>{row.buyerCodeType}</td>
      <td style={tdStyle}>{row.companyName || <em style={{ color: '#999' }}>—</em>}</td>
      <td style={tdStyle}>
        <Pill kind={row.qualificationType === 'Qualified' ? 'good' : 'muted'}>
          {row.qualificationType}
        </Pill>
      </td>
      <td style={tdStyle}>{row.included ? '✓' : '—'}</td>
      <td style={tdStyle}>{row.isSpecialTreatment ? <Pill kind="warn">STB</Pill> : '—'}</td>
    </tr>
  );
}

function SummaryCard({
  label,
  value,
  accent = '#112d32',
}: {
  label: string;
  value: number;
  accent?: string;
}) {
  return (
    <div style={summaryCardStyle}>
      <div style={{ fontSize: 12, color: '#7a7a7a' }}>{label}</div>
      <div style={{ fontSize: 22, fontWeight: 600, color: accent }}>
        {value.toLocaleString()}
      </div>
    </div>
  );
}

function FilterButton({
  current,
  value,
  onClick,
  children,
}: {
  current: Filter;
  value: Filter;
  onClick: (v: Filter) => void;
  children: React.ReactNode;
}) {
  const active = current === value;
  return (
    <button
      type="button"
      onClick={() => onClick(value)}
      style={{
        padding: '0.35rem 0.75rem',
        borderRadius: 4,
        border: '1px solid ' + (active ? '#407874' : '#cfd1d3'),
        background: active ? '#407874' : '#fff',
        color: active ? '#fff' : '#112d32',
        fontSize: 13,
        cursor: 'pointer',
      }}
    >
      {children}
    </button>
  );
}

function Pill({
  kind,
  children,
}: {
  kind: 'good' | 'warn' | 'muted';
  children: React.ReactNode;
}) {
  const palette: Record<typeof kind, { bg: string; fg: string }> = {
    good: { bg: '#e6f0ee', fg: '#16785f' },
    warn: { bg: '#fff1de', fg: '#bf6c05' },
    muted: { bg: '#f0f0f0', fg: '#7a7a7a' },
  };
  const p = palette[kind];
  return (
    <span
      style={{
        background: p.bg,
        color: p.fg,
        padding: '0.15rem 0.5rem',
        borderRadius: 3,
        fontSize: 11,
        fontWeight: 600,
        letterSpacing: '0.02em',
      }}
    >
      {children}
    </span>
  );
}

function statusPillStyle(status: string): React.CSSProperties {
  const colors =
    status === 'SUCCESS'
      ? { bg: '#e6f0ee', fg: '#16785f' }
      : status === 'FAILED'
      ? { bg: '#fde7e7', fg: '#a31b1b' }
      : status === 'RUNNING'
      ? { bg: '#fff1de', fg: '#bf6c05' }
      : { bg: '#f0f0f0', fg: '#7a7a7a' };
  return {
    background: colors.bg,
    color: colors.fg,
    padding: '0.15rem 0.5rem',
    borderRadius: 3,
    fontSize: 11,
    fontWeight: 600,
  };
}

const pageStyle: React.CSSProperties = { padding: '1.5rem 2rem', maxWidth: 1280 };
const backLinkStyle: React.CSSProperties = { color: '#407874', fontSize: 13, textDecoration: 'none' };
const titleStyle: React.CSSProperties = { margin: '0.5rem 0 0.25rem', fontSize: 24, color: '#112d32' };
const subtitleStyle: React.CSSProperties = { color: '#514F4E', fontSize: 14, marginBottom: '1rem' };
const summaryRowStyle: React.CSSProperties = { display: 'flex', gap: '0.75rem', flexWrap: 'wrap', marginBottom: '1rem' };
const summaryCardStyle: React.CSSProperties = {
  border: '1px solid #e3e6e8', borderRadius: 6, padding: '0.5rem 0.875rem', minWidth: 130, background: '#fff',
};
const controlsRowStyle: React.CSSProperties = { display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '0.5rem' };
const searchStyle: React.CSSProperties = {
  padding: '0.4rem 0.6rem', border: '1px solid #B7B5B5', borderRadius: 4, font: 'inherit', minWidth: 240,
};
const tableWrapStyle: React.CSSProperties = { border: '1px solid #e3e6e8', borderRadius: 6, overflow: 'auto', background: '#fff' };
const tableStyle: React.CSSProperties = { width: '100%', borderCollapse: 'collapse', fontSize: 14 };
const thStyle: React.CSSProperties = {
  textAlign: 'left', padding: '0.5rem 0.75rem', background: '#f5f7f8', color: '#514F4E', fontSize: 12, fontWeight: 600,
  borderBottom: '1px solid #e3e6e8',
};
const tdStyle: React.CSSProperties = { padding: '0.5rem 0.75rem', color: '#112d32' };
const errorStyle: React.CSSProperties = { color: '#a31b1b' };
