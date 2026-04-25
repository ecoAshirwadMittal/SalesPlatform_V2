'use client';

import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useRef, useState } from 'react';
import {
  closeRound,
  listSchedulingAuctions,
  RoundAlreadyTransitionedError,
  startRound,
  type SchedulingAuctionListPageResponse,
  type SchedulingAuctionListRow,
} from '@/lib/auctions';
import styles from './list.module.css';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;

const STATUSES = ['Unscheduled', 'Scheduled', 'Started', 'Closed'] as const;

interface Filters {
  auctionId: string;
  status: string;
  weekDisplay: string;
}

const EMPTY_FILTERS: Filters = { auctionId: '', status: '', weekDisplay: '' };

const statusClass: Record<string, string> = {
  Unscheduled: styles.statusUnscheduled,
  Scheduled: styles.statusScheduled,
  Started: styles.statusStarted,
  Closed: styles.statusClosed,
};

export default function SchedulingAuctionListPage() {
  const router = useRouter();
  const [page, setPage] = useState(0);
  const [data, setData] = useState<SchedulingAuctionListPageResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [transitionError, setTransitionError] = useState<string | null>(null);
  const [transitioning, setTransitioning] = useState<number | null>(null);

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
    const parsedAuctionId = applied.auctionId ? Number(applied.auctionId) : undefined;
    const res = await listSchedulingAuctions({
      auctionId: Number.isFinite(parsedAuctionId) ? parsedAuctionId : undefined,
      status: applied.status || undefined,
      weekDisplay: applied.weekDisplay || undefined,
      page,
      pageSize: PAGE_SIZE,
    });
    setData(res);
  }, [applied, page]);

  useEffect(() => {
    let ignore = false;
    refresh().catch(() => {
      if (!ignore) setError('Failed to load scheduling auctions');
    });
    return () => {
      ignore = true;
    };
  }, [refresh]);

  const handleTransition = async (id: number, action: 'start' | 'close') => {
    setTransitionError(null);
    setTransitioning(id);
    try {
      if (action === 'start') {
        await startRound(id);
      } else {
        await closeRound(id);
      }
      await refresh();
    } catch (err) {
      const msg =
        err instanceof RoundAlreadyTransitionedError
          ? err.message
          : err instanceof Error
            ? err.message
            : 'Round transition failed';
      setTransitionError(msg);
    } finally {
      setTransitioning(null);
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
        <h2 className={styles.title}>Scheduling Auctions</h2>
        <button
          className={styles.buttonGhost}
          type="button"
          onClick={() => {
            setError(null);
            refresh().catch(() => setError('Failed to load scheduling auctions'));
          }}
        >
          Refresh
        </button>
        {error && <span className={styles.error}>{error}</span>}
        {transitionError && (
          <span className={styles.error} role="alert">
            {transitionError}
          </span>
        )}
      </header>

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <th>
                <div>Auction ID</div>
                <input
                  className={styles.filterInput}
                  value={input.auctionId}
                  placeholder="="
                  inputMode="numeric"
                  onChange={(e) => updateFilter('auctionId', e.target.value.replace(/\D/g, ''))}
                />
              </th>
              <th>
                <div>Auction Title</div>
              </th>
              <th>
                <div>Auction Week</div>
                <input
                  className={styles.filterInput}
                  value={input.weekDisplay}
                  placeholder="Ab"
                  onChange={(e) => updateFilter('weekDisplay', e.target.value)}
                />
              </th>
              <th>Round</th>
              <th>Name</th>
              <th>Start</th>
              <th>End</th>
              <th>
                <div>Status</div>
                <select
                  className={styles.filterSelect}
                  value={input.status}
                  onChange={(e) => updateFilter('status', e.target.value)}
                >
                  <option value="">All</option>
                  {STATUSES.map((s) => (
                    <option key={s} value={s}>
                      {s}
                    </option>
                  ))}
                </select>
              </th>
              <th aria-label="actions" />
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map((r) => (
              <SchedulingRow
                key={r.id}
                row={r}
                isTransitioning={transitioning === r.id}
                onEdit={() => {
                  if (r.auctionId) {
                    router.push(`/admin/auctions-data-center/auctions/${r.auctionId}/schedule`);
                  }
                }}
                onStart={() => void handleTransition(r.id, 'start')}
                onClose={() => void handleTransition(r.id, 'close')}
              />
            ))}
            {data && data.content.length === 0 && (
              <tr>
                <td className={styles.empty} colSpan={9}>
                  No scheduling rows match the current filters.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        <div className={styles.pagination}>
          <button type="button" onClick={() => setPage(0)} disabled={page === 0}>
            «
          </button>
          <button
            type="button"
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
          >
            ‹
          </button>
          <span>
            Currently showing {startIdx} to {endIdx} of {total}
          </span>
          <button
            type="button"
            onClick={() => setPage((p) => p + 1)}
            disabled={data ? page + 1 >= data.totalPages : true}
          >
            ›
          </button>
          <button
            type="button"
            onClick={() => data && setPage(data.totalPages - 1)}
            disabled={data ? page + 1 >= data.totalPages : true}
          >
            »
          </button>
        </div>
      </div>
    </div>
  );
}

function SchedulingRow({
  row,
  isTransitioning,
  onEdit,
  onStart,
  onClose,
}: {
  row: SchedulingAuctionListRow;
  isTransitioning: boolean;
  onEdit: () => void;
  onStart: () => void;
  onClose: () => void;
}) {
  return (
    <tr>
      <td>{row.auctionId ?? '—'}</td>
      <td>{row.auctionTitle ?? '—'}</td>
      <td>{row.auctionWeekYear ?? '—'}</td>
      <td>{row.round}</td>
      <td>{row.name ?? '—'}</td>
      <td>{formatDate(row.startDatetime)}</td>
      <td>{formatDate(row.endDatetime)}</td>
      <td>
        <span className={`${styles.statusBadge} ${statusClass[row.roundStatus] ?? ''}`}>
          {row.roundStatus}
        </span>
      </td>
      <td>
        <div style={{ display: 'flex', gap: 4, flexWrap: 'wrap' }}>
          {row.auctionId && (
            <button
              type="button"
              className={styles.editLink}
              aria-label={`View auction ${row.auctionId}`}
              onClick={onEdit}
            >
              View
            </button>
          )}
          {row.roundStatus === 'Scheduled' && (
            <button
              type="button"
              className={styles.editLink}
              aria-label={`Start round ${row.id}`}
              disabled={isTransitioning}
              onClick={onStart}
            >
              {isTransitioning ? '…' : 'Start'}
            </button>
          )}
          {row.roundStatus === 'Started' && (
            <button
              type="button"
              className={styles.editLink}
              aria-label={`Close round ${row.id}`}
              disabled={isTransitioning}
              onClick={onClose}
            >
              {isTransitioning ? '…' : 'Close'}
            </button>
          )}
        </div>
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
