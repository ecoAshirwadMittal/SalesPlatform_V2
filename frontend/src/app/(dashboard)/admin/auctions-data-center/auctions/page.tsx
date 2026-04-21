'use client';

import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useRef, useState } from 'react';
import { fetchWeeks, type WeekOption } from '@/lib/aggregatedInventory';
import {
  listAuctions,
  type AuctionListPageResponse,
  type AuctionListRow,
} from '@/lib/auctions';
import styles from './list.module.css';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;

const STATUSES = ['Unscheduled', 'Scheduled', 'Started', 'Closed'] as const;

interface Filters {
  title: string;
  weekId: string;
  status: string;
}

const EMPTY_FILTERS: Filters = { title: '', weekId: '', status: '' };

const statusClass: Record<string, string> = {
  Unscheduled: styles.statusUnscheduled,
  Scheduled: styles.statusScheduled,
  Started: styles.statusStarted,
  Closed: styles.statusClosed,
};

export default function AuctionsListPage() {
  const router = useRouter();
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [page, setPage] = useState(0);
  const [data, setData] = useState<AuctionListPageResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [input, setInput] = useState<Filters>(EMPTY_FILTERS);
  const [applied, setApplied] = useState<Filters>(EMPTY_FILTERS);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    let ignore = false;
    fetchWeeks()
      .then((list) => {
        if (!ignore) setWeeks(list);
      })
      .catch(() => {
        if (!ignore) setError('Failed to load weeks');
      });
    return () => {
      ignore = true;
    };
  }, []);

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
    const res = await listAuctions({
      title: applied.title || undefined,
      weekId: applied.weekId ? Number(applied.weekId) : undefined,
      status: applied.status || undefined,
      page,
      pageSize: PAGE_SIZE,
    });
    setData(res);
  }, [applied, page]);

  useEffect(() => {
    let ignore = false;
    refresh().catch(() => {
      if (!ignore) setError('Failed to load auctions');
    });
    return () => {
      ignore = true;
    };
  }, [refresh]);

  const updateFilter = <K extends keyof Filters>(k: K, v: string) =>
    setInput((prev) => ({ ...prev, [k]: v }));

  const total = data?.totalElements ?? 0;
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Auctions</h2>
        <button
          className={styles.buttonGhost}
          type="button"
          onClick={() => {
            setError(null);
            refresh().catch(() => setError('Failed to load auctions'));
          }}
        >
          Refresh
        </button>
        {error && <span className={styles.error}>{error}</span>}
      </header>

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <th>
                <div>Title</div>
                <input
                  className={styles.filterInput}
                  value={input.title}
                  placeholder="Ab"
                  onChange={(e) => updateFilter('title', e.target.value)}
                />
              </th>
              <th>
                <div>Week</div>
                <select
                  className={styles.filterSelect}
                  value={input.weekId}
                  onChange={(e) => updateFilter('weekId', e.target.value)}
                >
                  <option value="">All</option>
                  {weeks.map((w) => (
                    <option key={w.id} value={w.id}>
                      {w.weekDisplay}
                    </option>
                  ))}
                </select>
              </th>
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
              <th>Rounds</th>
              <th>Created</th>
              <th>Changed</th>
              <th>Created By</th>
              <th aria-label="actions" />
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map((r) => (
              <AuctionRow
                key={r.id}
                row={r}
                onEdit={() =>
                  router.push(`/admin/auctions-data-center/auctions/${r.id}/schedule`)
                }
              />
            ))}
            {data && data.content.length === 0 && (
              <tr>
                <td className={styles.empty} colSpan={8}>
                  No auctions match the current filters.
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

function AuctionRow({ row, onEdit }: { row: AuctionListRow; onEdit: () => void }) {
  return (
    <tr>
      <td>{row.auctionTitle}</td>
      <td>{row.weekDisplay ?? '—'}</td>
      <td>
        <span className={`${styles.statusBadge} ${statusClass[row.auctionStatus] ?? ''}`}>
          {row.auctionStatus}
        </span>
      </td>
      <td>{row.roundCount}</td>
      <td>{formatDate(row.createdDate)}</td>
      <td>{formatDate(row.changedDate)}</td>
      <td>{row.createdBy ?? '—'}</td>
      <td>
        <button
          type="button"
          className={styles.editLink}
          aria-label={`Schedule ${row.auctionTitle}`}
          onClick={onEdit}
        >
          {row.auctionStatus === 'Unscheduled' ? 'Schedule' : 'View'}
        </button>
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
