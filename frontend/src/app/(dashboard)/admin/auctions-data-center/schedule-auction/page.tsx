'use client';

import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useRef, useState } from 'react';
import {
  closeRound,
  listSchedulingAuctions,
  reRank,
  recalculateTargetPrice,
  RecalcAlreadyRunningError,
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

/**
 * M30 (2026-05-07): live "Next transition in Xm Ys" indicator. SalesOps
 * cannot tell from the static list whether a transition is "stuck" or
 * pending the next cron tick (~60s lag). The hook drives a 1Hz wall
 * clock so each row's countdown re-renders without touching the API.
 *
 * Returning a millisecond-precision number lets the row format with
 * `Math.floor` for stable display while keeping the hook itself
 * trivially testable.
 */
function useNowTick(intervalMs = 1000): number {
  const [now, setNow] = useState<number>(() => Date.now());
  useEffect(() => {
    const id = setInterval(() => setNow(Date.now()), intervalMs);
    return () => clearInterval(id);
  }, [intervalMs]);
  return now;
}

/**
 * Format the difference between `targetMs` and `nowMs` as `Xm Ys`.
 * Negative deltas (target already passed but cron hasn't fired yet)
 * collapse to "due now" — the cron tick fires within ~60s of the
 * boundary, so a static "due now" is accurate without special-casing
 * each second past zero.
 */
function formatTransitionCountdown(nowMs: number, targetMs: number): string {
  const delta = targetMs - nowMs;
  if (delta <= 0) return 'due now';
  const totalSeconds = Math.floor(delta / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}m ${seconds}s`;
}

export default function SchedulingAuctionListPage() {
  const router = useRouter();
  // M30: 1Hz wall clock so the per-row "Next transition in Xm Ys" tag
  // recomputes without re-fetching the list. Cheap — no network, no
  // state copy.
  const now = useNowTick();
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

  const handleRecalc = async (id: number, action: 're-rank' | 'recalculate-target-price') => {
    setTransitionError(null);
    setTransitioning(id);
    try {
      if (action === 're-rank') {
        await reRank(id);
      } else {
        await recalculateTargetPrice(id);
      }
      await refresh();
    } catch (err) {
      const msg =
        err instanceof RecalcAlreadyRunningError
          ? err.message
          : err instanceof Error
            ? err.message
            : 'Recalculation failed';
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
                now={now}
                isTransitioning={transitioning === r.id}
                onEdit={() => {
                  if (r.auctionId) {
                    router.push(`/admin/auctions-data-center/auctions/${r.auctionId}/schedule`);
                  }
                }}
                onStart={() => void handleTransition(r.id, 'start')}
                onClose={() => void handleTransition(r.id, 'close')}
                onRerank={() => void handleRecalc(r.id, 're-rank')}
                onRecalcTargetPrice={() => void handleRecalc(r.id, 'recalculate-target-price')}
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
  now,
  isTransitioning,
  onEdit,
  onStart,
  onClose,
  onRerank,
  onRecalcTargetPrice,
}: {
  row: SchedulingAuctionListRow;
  now: number;
  isTransitioning: boolean;
  onEdit: () => void;
  onStart: () => void;
  onClose: () => void;
  onRerank: () => void;
  onRecalcTargetPrice: () => void;
}) {
  // M30: which boundary are we counting down to?
  //   Scheduled → Started fires at startDatetime
  //   Started   → Closed  fires at endDatetime
  // Closed / Unscheduled rows have no upcoming transition.
  const transitionTargetIso =
    row.roundStatus === 'Scheduled'
      ? row.startDatetime
      : row.roundStatus === 'Started'
        ? row.endDatetime
        : null;
  const transitionTargetMs =
    transitionTargetIso != null ? new Date(transitionTargetIso).getTime() : NaN;
  const showCountdown = transitionTargetIso != null && Number.isFinite(transitionTargetMs);
  const countdownLabel = showCountdown ? formatTransitionCountdown(now, transitionTargetMs) : '';
  // The transition fires off the row's relevant boundary, so we attach
  // the tag under the corresponding date column for visual proximity.
  const showCountdownOnStartCell = row.roundStatus === 'Scheduled';
  const showCountdownOnEndCell = row.roundStatus === 'Started';

  return (
    <tr>
      <td>{row.auctionId ?? '—'}</td>
      <td>{row.auctionTitle ?? '—'}</td>
      <td>{row.auctionWeekYear ?? '—'}</td>
      <td>{row.round}</td>
      <td>{row.name ?? '—'}</td>
      <td>
        {formatDate(row.startDatetime)}
        {showCountdown && showCountdownOnStartCell && (
          <div className={styles.transitionTag} aria-label="Next transition">
            Next transition: {countdownLabel}
          </div>
        )}
      </td>
      <td>
        {formatDate(row.endDatetime)}
        {showCountdown && showCountdownOnEndCell && (
          <div className={styles.transitionTag} aria-label="Next transition">
            Next transition: {countdownLabel}
          </div>
        )}
      </td>
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
          {row.roundStatus === 'Closed' && (row.round === 1 || row.round === 2) && (
            <>
              <button
                type="button"
                className={styles.editLink}
                aria-label={`Re-rank round ${row.id}`}
                disabled={isTransitioning}
                onClick={onRerank}
              >
                {isTransitioning ? '…' : 'Re-rank'}
              </button>
              <button
                type="button"
                className={styles.editLink}
                aria-label={`Recalc target price round ${row.id}`}
                disabled={isTransitioning}
                onClick={onRecalcTargetPrice}
              >
                {isTransitioning ? '…' : 'Recalc TP'}
              </button>
            </>
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
