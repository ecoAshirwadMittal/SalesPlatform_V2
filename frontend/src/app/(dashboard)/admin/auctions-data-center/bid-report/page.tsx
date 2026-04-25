'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { getBidReportR3, type BidReportPageResponse, type BidReportRow } from '@/lib/bidReport';
import styles from './bidReport.module.css';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;

interface Filters {
  auctionId: string;
}

const EMPTY_FILTERS: Filters = { auctionId: '' };

export default function BidReportR3Page() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<BidReportPageResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [input, setInput] = useState<Filters>(EMPTY_FILTERS);
  const [applied, setApplied] = useState<Filters>(EMPTY_FILTERS);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setApplied(input);
      setPage(0);
    }, FILTER_DELAY);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [input]);

  const refresh = useCallback(async () => {
    const auctionIdNum = applied.auctionId ? Number(applied.auctionId) : undefined;
    const result = await getBidReportR3({
      auctionId: Number.isFinite(auctionIdNum) ? auctionIdNum : undefined,
      page,
      pageSize: PAGE_SIZE,
    });
    setData(result);
  }, [applied, page]);

  useEffect(() => {
    let ignore = false;
    refresh().catch(() => {
      if (!ignore) setError('Failed to load R3 bid report');
    });
    return () => { ignore = true; };
  }, [refresh]);

  const total = data?.totalElements ?? 0;
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>R3 Bid Report</h2>
        <button
          type="button"
          className={`${styles.button} ${styles.buttonGhost}`}
          onClick={() => {
            setError(null);
            refresh().catch(() => setError('Failed to load R3 bid report'));
          }}
        >
          Refresh
        </button>
        {error && <span className={styles.error}>{error}</span>}
      </header>

      <div className={styles.filterRow}>
        <div className={styles.field}>
          <label className={styles.label} htmlFor="auctionIdFilter">
            Auction ID
          </label>
          <input
            id="auctionIdFilter"
            className={styles.filterInput}
            value={input.auctionId}
            placeholder="="
            inputMode="numeric"
            onChange={(e) =>
              setInput((prev) => ({ ...prev, auctionId: e.target.value.replace(/\D/g, '') }))
            }
          />
        </div>
      </div>

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Auction ID</th>
              <th>Buyer Code ID</th>
              <th>ECOID</th>
              <th>Merged Grade</th>
              <th>Type</th>
              <th>Bid Qty</th>
              <th>Bid Amt</th>
              <th>Submitted Qty</th>
              <th>Submitted Amt</th>
              <th>Target Price</th>
              <th>Submitted At</th>
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map((row) => (
              <BidReportRowEl key={row.id} row={row} />
            ))}
            {data && data.content.length === 0 && (
              <tr>
                <td className={styles.empty} colSpan={12}>
                  No R3 bid data matches the current filters.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        <div className={styles.pagination}>
          <button type="button" onClick={() => setPage(0)} disabled={page === 0}>«</button>
          <button type="button" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>‹</button>
          <span>Currently showing {startIdx} to {endIdx} of {total}</span>
          <button
            type="button"
            onClick={() => setPage((p) => p + 1)}
            disabled={data ? page + 1 >= data.totalPages : true}
          >›</button>
          <button
            type="button"
            onClick={() => data && setPage(data.totalPages - 1)}
            disabled={data ? page + 1 >= data.totalPages : true}
          >»</button>
        </div>
      </div>
    </div>
  );
}

function BidReportRowEl({ row }: { row: BidReportRow }) {
  return (
    <tr>
      <td>{row.id}</td>
      <td>{row.auctionId ?? '—'}</td>
      <td>{row.buyerCodeId}</td>
      <td>{row.ecoid ?? '—'}</td>
      <td>{row.mergedGrade ?? '—'}</td>
      <td>{row.buyerCodeType ?? '—'}</td>
      <td>{row.bidQuantity ?? '—'}</td>
      <td>{row.bidAmount != null ? `$${Number(row.bidAmount).toFixed(2)}` : '—'}</td>
      <td>{row.submittedBidQuantity ?? '—'}</td>
      <td>{row.submittedBidAmount != null ? `$${Number(row.submittedBidAmount).toFixed(2)}` : '—'}</td>
      <td>{row.targetPrice != null ? `$${Number(row.targetPrice).toFixed(2)}` : '—'}</td>
      <td>{formatDate(row.submittedDatetime)}</td>
    </tr>
  );
}

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '—';
  return d.toLocaleString();
}
