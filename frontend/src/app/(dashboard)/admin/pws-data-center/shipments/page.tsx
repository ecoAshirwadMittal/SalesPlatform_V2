'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import s from '../../../settings/pws-control-center/admin.module.css';
import { apiFetch } from '@/lib/apiFetch';

/**
 * Shipments & Sync — admin read-only overview (Phase 3, pws-data-center-port.md).
 * Surfaces recent inventory sync runs from integration.sync_run_log so ops can
 * see last run, duration, row counts, and failures without leaving the modern app.
 * Consolidates the legacy "PWS Full Inventory Sync Report" and
 * "PWS Integration (Deposco config)" read-outs.
 */

const API_SYNC_LOGS = '/api/v1/inventory/sync/logs';
const API_SYNC_RUN = '/api/v1/inventory/sync/full';

interface SyncLogRow {
  id: number;
  syncType: string;
  status: string;
  startTime: string | null;
  endTime: string | null;
  totalItemsReceived: number | null;
  devicesUpdated: number | null;
  devicesMissing: number | null;
  errorMessage: string | null;
  triggeredBy: string | null;
  durationMs: number | null;
}

interface SyncLogsPage {
  content: SyncLogRow[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

function formatDate(value: string | null): string {
  if (!value) return '—';
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? value : d.toLocaleString();
}

function formatDuration(ms: number | null): string {
  if (ms == null) return '—';
  if (ms < 1000) return `${ms} ms`;
  const sec = ms / 1000;
  if (sec < 60) return `${sec.toFixed(1)}s`;
  const min = sec / 60;
  return `${min.toFixed(1)}m`;
}

function statusColor(status: string): string {
  switch (status) {
    case 'SUCCESS': return '#2CB34A';
    case 'FAILED': return '#d9534f';
    case 'RUNNING': return '#407874';
    default: return '#888';
  }
}

export default function ShipmentsPage() {
  const [rows, setRows] = useState<SyncLogRow[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [running, setRunning] = useState(false);

  const load = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch(`${API_SYNC_LOGS}?page=${page}&size=25`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data: SyncLogsPage = await res.json();
      setRows(Array.isArray(data.content) ? data.content : []);
      setTotalPages(Math.max(1, data.totalPages ?? 1));
      setTotalElements(data.totalElements ?? 0);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load sync logs');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { void load(); }, [load]);

  const triggerFullSync = async (): Promise<void> => {
    if (!confirm('Trigger a full Deposco inventory sync now? This is a long-running batch job.')) return;
    setRunning(true);
    setError(null);
    try {
      const res = await apiFetch(API_SYNC_RUN, { method: 'POST' });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      await load();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Sync failed');
    } finally {
      setRunning(false);
    }
  };

  const latest = rows[0];

  return (
    <div className={s.pageContainer}>
      <div className={s.pageHeader}>
        <Link href="/admin/pws-data-center" className={s.backLink}>← PWS Data Center</Link>
        <h2 className={s.pageTitle}>Shipments &amp; Sync</h2>
      </div>

      {error && <div className={`${s.banner} ${s.bannerError}`}>{error}</div>}

      {latest && (
        <div className={s.card} style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', gap: 32, padding: 16, flexWrap: 'wrap' }}>
            <div>
              <div style={{ fontSize: 12, color: '#888' }}>Last run</div>
              <div style={{ fontSize: 15, fontWeight: 600 }}>{formatDate(latest.startTime)}</div>
            </div>
            <div>
              <div style={{ fontSize: 12, color: '#888' }}>Status</div>
              <div style={{ fontSize: 15, fontWeight: 600, color: statusColor(latest.status) }}>
                {latest.status}
              </div>
            </div>
            <div>
              <div style={{ fontSize: 12, color: '#888' }}>Duration</div>
              <div style={{ fontSize: 15, fontWeight: 600 }}>{formatDuration(latest.durationMs)}</div>
            </div>
            <div>
              <div style={{ fontSize: 12, color: '#888' }}>Items received</div>
              <div style={{ fontSize: 15, fontWeight: 600 }}>{latest.totalItemsReceived ?? 0}</div>
            </div>
            <div>
              <div style={{ fontSize: 12, color: '#888' }}>Devices updated</div>
              <div style={{ fontSize: 15, fontWeight: 600 }}>{latest.devicesUpdated ?? 0}</div>
            </div>
            <div>
              <div style={{ fontSize: 12, color: '#888' }}>Missing SKUs</div>
              <div style={{ fontSize: 15, fontWeight: 600 }}>{latest.devicesMissing ?? 0}</div>
            </div>
          </div>
        </div>
      )}

      <div className={s.card}>
        <div className={s.toolbar}>
          <button
            type="button"
            className={s.actionBtn}
            onClick={triggerFullSync}
            disabled={running}
          >
            {running ? 'Running…' : 'Run Full Sync'}
          </button>
          <span style={{ marginLeft: 'auto', color: '#666', fontSize: 13 }}>
            {loading ? 'Loading…' : `${totalElements} run${totalElements === 1 ? '' : 's'}`}
          </span>
        </div>

        <table className={s.table}>
          <thead>
            <tr>
              <th>Started</th>
              <th>Ended</th>
              <th>Type</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Duration</th>
              <th style={{ textAlign: 'right' }}>Items</th>
              <th style={{ textAlign: 'right' }}>Updated</th>
              <th style={{ textAlign: 'right' }}>Missing</th>
              <th>Triggered by</th>
              <th>Error</th>
            </tr>
          </thead>
          <tbody>
            {rows.length === 0 && !loading && (
              <tr>
                <td colSpan={10} style={{ textAlign: 'center', color: '#888', padding: 24 }}>
                  No sync runs recorded yet.
                </td>
              </tr>
            )}
            {rows.map((r) => (
              <tr key={r.id}>
                <td>{formatDate(r.startTime)}</td>
                <td>{formatDate(r.endTime)}</td>
                <td>{r.syncType}</td>
                <td style={{ color: statusColor(r.status), fontWeight: 600 }}>{r.status}</td>
                <td style={{ textAlign: 'right' }}>{formatDuration(r.durationMs)}</td>
                <td style={{ textAlign: 'right' }}>{r.totalItemsReceived ?? 0}</td>
                <td style={{ textAlign: 'right' }}>{r.devicesUpdated ?? 0}</td>
                <td style={{ textAlign: 'right' }}>{r.devicesMissing ?? 0}</td>
                <td>{r.triggeredBy ?? '—'}</td>
                <td title={r.errorMessage ?? ''} style={{ maxWidth: 240, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {r.errorMessage ?? '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', padding: 12, alignItems: 'center' }}>
          <button type="button" className={s.actionBtn} disabled={page === 0} onClick={() => setPage(0)}>« First</button>
          <button type="button" className={s.actionBtn} disabled={page === 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>‹ Prev</button>
          <span style={{ fontSize: 13, color: '#555' }}>Page {page + 1} of {totalPages}</span>
          <button type="button" className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next ›</button>
          <button type="button" className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage(totalPages - 1)}>Last »</button>
        </div>
      </div>
    </div>
  );
}
