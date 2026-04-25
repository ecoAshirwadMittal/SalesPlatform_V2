'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import {
  listQualifiedBuyerCodes,
  qualifyBuyerCode,
  type QualifiedBuyerCodePageResponse,
  type QualifiedBuyerCodeRow,
} from '@/lib/qbcAdmin';
import styles from './qualifiedBuyerCodes.module.css';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;

interface Filters {
  schedulingAuctionId: string;
  buyerCodeId: string;
}

const EMPTY_FILTERS: Filters = { schedulingAuctionId: '', buyerCodeId: '' };

export default function QualifiedBuyerCodesPage() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState<QualifiedBuyerCodePageResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [input, setInput] = useState<Filters>(EMPTY_FILTERS);
  const [applied, setApplied] = useState<Filters>(EMPTY_FILTERS);
  const [qualifyingId, setQualifyingId] = useState<number | null>(null);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setApplied(input);
      setPage(0);
    }, FILTER_DELAY);
    return () => { if (debounceRef.current) clearTimeout(debounceRef.current); };
  }, [input]);

  const refresh = useCallback(async () => {
    const saId = applied.schedulingAuctionId ? Number(applied.schedulingAuctionId) : undefined;
    const bcId = applied.buyerCodeId ? Number(applied.buyerCodeId) : undefined;
    const result = await listQualifiedBuyerCodes({
      schedulingAuctionId: Number.isFinite(saId) ? saId : undefined,
      buyerCodeId: Number.isFinite(bcId) ? bcId : undefined,
      page,
      pageSize: PAGE_SIZE,
    });
    setData(result);
  }, [applied, page]);

  useEffect(() => {
    let ignore = false;
    refresh().catch(() => { if (!ignore) setError('Failed to load qualified buyer codes'); });
    return () => { ignore = true; };
  }, [refresh]);

  const handleQualify = async (id: number) => {
    setQualifyingId(id);
    setError(null);
    try {
      const updated = await qualifyBuyerCode(id);
      setData((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          content: prev.content.map((r) => (r.id === updated.id ? updated : r)),
        };
      });
    } catch {
      setError(`Failed to qualify buyer code ${id}`);
    } finally {
      setQualifyingId(null);
    }
  };

  const updateFilter = <K extends keyof Filters>(k: K, v: string) =>
    setInput((prev) => ({ ...prev, [k]: v }));

  const total = data?.totalElements ?? 0;
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Qualified Buyer Codes</h2>
        <button
          type="button"
          className={`${styles.button} ${styles.buttonGhost}`}
          onClick={() => { setError(null); refresh().catch(() => setError('Failed to load qualified buyer codes')); }}
        >
          Refresh
        </button>
        {error && <span className={styles.error}>{error}</span>}
      </header>

      <div className={styles.filterRow}>
        <div className={styles.field}>
          <label className={styles.label} htmlFor="saIdFilter">
            Scheduling Auction ID
          </label>
          <input
            id="saIdFilter"
            className={styles.filterInput}
            value={input.schedulingAuctionId}
            placeholder="="
            inputMode="numeric"
            onChange={(e) => updateFilter('schedulingAuctionId', e.target.value.replace(/\D/g, ''))}
          />
        </div>
        <div className={styles.field}>
          <label className={styles.label} htmlFor="buyerCodeIdFilter">
            Buyer Code ID
          </label>
          <input
            id="buyerCodeIdFilter"
            className={styles.filterInput}
            value={input.buyerCodeId}
            placeholder="="
            inputMode="numeric"
            onChange={(e) => updateFilter('buyerCodeId', e.target.value.replace(/\D/g, ''))}
          />
        </div>
      </div>

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Scheduling Auction ID</th>
              <th>Buyer Code ID</th>
              <th>Qualification Type</th>
              <th>Included</th>
              <th>Submitted</th>
              <th>Special Treatment</th>
              <th>Changed Date</th>
              <th aria-label="actions" />
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map((row) => (
              <QbcRow
                key={row.id}
                row={row}
                qualifying={qualifyingId === row.id}
                onQualify={() => handleQualify(row.id)}
              />
            ))}
            {data && data.content.length === 0 && (
              <tr>
                <td className={styles.empty} colSpan={9}>
                  No qualified buyer codes match the current filters.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        <div className={styles.pagination}>
          <button type="button" onClick={() => setPage(0)} disabled={page === 0}>«</button>
          <button type="button" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>‹</button>
          <span>Currently showing {startIdx} to {endIdx} of {total}</span>
          <button type="button" onClick={() => setPage((p) => p + 1)} disabled={data ? page + 1 >= data.totalPages : true}>›</button>
          <button type="button" onClick={() => data && setPage(data.totalPages - 1)} disabled={data ? page + 1 >= data.totalPages : true}>»</button>
        </div>
      </div>
    </div>
  );
}

function QbcRow({
  row,
  qualifying,
  onQualify,
}: {
  row: QualifiedBuyerCodeRow;
  qualifying: boolean;
  onQualify: () => void;
}) {
  const qtClass =
    row.qualificationType === 'Manual'
      ? styles.badgeManual
      : row.qualificationType === 'Qualified'
        ? styles.badgeQualified
        : styles.badgeNotQualified;

  return (
    <tr>
      <td>{row.id}</td>
      <td>{row.schedulingAuctionId}</td>
      <td>{row.buyerCodeId}</td>
      <td>
        <span className={qtClass}>{row.qualificationType}</span>
      </td>
      <td>{row.included ? 'Yes' : 'No'}</td>
      <td>{row.submitted ? 'Yes' : 'No'}</td>
      <td>{row.specialTreatment ? 'Yes' : 'No'}</td>
      <td>{formatDate(row.changedDate)}</td>
      <td>
        {row.qualificationType !== 'Manual' && (
          <button
            type="button"
            className={styles.qualifyBtn}
            disabled={qualifying}
            aria-label={`Manually qualify QBC ${row.id}`}
            onClick={onQualify}
          >
            {qualifying ? '…' : 'Set Manual'}
          </button>
        )}
      </td>
    </tr>
  );
}

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '—';
  return d.toLocaleString();
}
