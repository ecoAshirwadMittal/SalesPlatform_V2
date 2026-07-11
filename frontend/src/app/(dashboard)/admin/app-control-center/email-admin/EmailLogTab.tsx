'use client';

import { useState, useEffect, useCallback } from 'react';
import DOMPurify from 'dompurify';
import s from '../../../settings/pws-control-center/admin.module.css';
import e from './emailAdmin.module.css';
import { listEmailLog, getEmailLog, resendEmailLog, type EmailLogView } from '@/lib/adminEmailClient';
import { formatDate, type Banner } from './shared';

const PAGE_SIZE = 50;

export function EmailLogTab({ onBanner }: { onBanner: (b: Banner) => void }) {
  const [entries, setEntries] = useState<EmailLogView[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');
  const [templateKeyFilter, setTemplateKeyFilter] = useState('');
  const [fromFilter, setFromFilter] = useState('');
  const [toFilter, setToFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [detailData, setDetailData] = useState<EmailLogView | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listEmailLog({
        status: statusFilter || undefined,
        templateKey: templateKeyFilter || undefined,
        from: fromFilter ? new Date(fromFilter).toISOString() : undefined,
        to: toFilter ? new Date(toFilter).toISOString() : undefined,
        page,
        size: PAGE_SIZE,
      });
      setEntries(data.content || []);
      setTotal(data.totalElements || 0);
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter, templateKeyFilter, fromFilter, toFilter, onBanner]);

  useEffect(() => { load(); }, [load]);

  function applyFilter(setter: (v: string) => void, value: string) {
    setter(value);
    setPage(0);
  }

  async function handleResend(id: number) {
    try {
      const updated = await resendEmailLog(id);
      onBanner({ type: 'success', message: `Resent — status: ${updated.status}.` });
      load();
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  async function showDetail(id: number) {
    try {
      setDetailData(await getEmailLog(id));
    } catch (err) {
      onBanner({ type: 'error', message: String(err) });
    }
  }

  const totalPages = Math.ceil(total / PAGE_SIZE);

  return (
    <>
      <div className={s.toolbar}>
        <div className={e.filterRow}>
          <label className={s.formLabel} style={{ margin: 0 }} htmlFor="log-status-filter">Status:</label>
          <select
            id="log-status-filter"
            className={e.filterSelect}
            value={statusFilter}
            onChange={(ev) => applyFilter(setStatusFilter, ev.target.value)}
          >
            <option value="">All</option>
            <option value="PENDING">Pending</option>
            <option value="SENT">Sent</option>
            <option value="FAILED">Failed</option>
          </select>
          <label className={s.formLabel} style={{ margin: 0 }} htmlFor="log-template-key-filter">Template:</label>
          <input
            id="log-template-key-filter"
            className={e.filterInput}
            placeholder="Template key"
            value={templateKeyFilter}
            onChange={(ev) => applyFilter(setTemplateKeyFilter, ev.target.value)}
          />
          <label className={s.formLabel} style={{ margin: 0 }} htmlFor="log-from-filter">From:</label>
          <input
            id="log-from-filter"
            className={e.filterInput}
            type="date"
            value={fromFilter}
            onChange={(ev) => applyFilter(setFromFilter, ev.target.value)}
          />
          <label className={s.formLabel} style={{ margin: 0 }} htmlFor="log-to-filter">To:</label>
          <input
            id="log-to-filter"
            className={e.filterInput}
            type="date"
            value={toFilter}
            onChange={(ev) => applyFilter(setToFilter, ev.target.value)}
          />
          <span className={e.countLabel}>{total} email(s)</span>
        </div>
      </div>

      {loading ? (
        <div className={s.loading}>Loading email log...</div>
      ) : (
        <>
          <table className={s.table}>
            <thead>
              <tr>
                <th style={{ width: '10%' }}>Status</th>
                <th style={{ width: '18%' }}>To</th>
                <th style={{ width: '28%' }}>Subject</th>
                <th style={{ width: '14%' }}>Template Key</th>
                <th style={{ width: '16%' }}>Date</th>
                <th style={{ width: '14%' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {entries.map((entry) => (
                <tr key={entry.id}>
                  <td><span className={`${e.statusBadge} ${e[`status${entry.status}`] || ''}`}>{entry.status}</span></td>
                  <td className={e.truncate}>{entry.toAddress}</td>
                  <td className={e.truncate}>{entry.subject || '—'}</td>
                  <td>{entry.templateKey || '—'}</td>
                  <td>{formatDate(entry.sentDate || entry.createdDate)}</td>
                  <td className={s.actionsCell}>
                    <button className={s.actionBtn} onClick={() => showDetail(entry.id)}>View</button>
                    {entry.status === 'FAILED' && (
                      <button className={s.actionBtn} onClick={() => handleResend(entry.id)}>Resend</button>
                    )}
                  </td>
                </tr>
              ))}
              {entries.length === 0 && (
                <tr><td colSpan={6} className={s.emptyState}>No email log entries found.</td></tr>
              )}
            </tbody>
          </table>

          {totalPages > 1 && (
            <div className={e.pagination}>
              <button className={s.actionBtn} disabled={page === 0} onClick={() => setPage(page - 1)}>← Prev</button>
              <span className={e.pageInfo}>Page {page + 1} of {totalPages}</span>
              <button className={s.actionBtn} disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>Next →</button>
            </div>
          )}
        </>
      )}

      {detailData && (
        <div className={e.overlay} onClick={() => setDetailData(null)}>
          <div className={e.modal} onClick={(ev) => ev.stopPropagation()}>
            <div className={e.modalHeader}>
              <h3>Email Detail #{detailData.id}</h3>
              <button className={e.modalClose} onClick={() => setDetailData(null)}>×</button>
            </div>
            <div className={e.modalBody}>
              <div className={e.detailRow}><strong>From:</strong> {detailData.fromAddress || '—'}</div>
              <div className={e.detailRow}><strong>To:</strong> {detailData.toAddress || '—'}</div>
              {detailData.cc && <div className={e.detailRow}><strong>CC:</strong> {detailData.cc}</div>}
              {detailData.bcc && <div className={e.detailRow}><strong>BCC:</strong> {detailData.bcc}</div>}
              <div className={e.detailRow}><strong>Subject:</strong> {detailData.subject || '—'}</div>
              <div className={e.detailRow}><strong>Status:</strong> {detailData.status}</div>
              {detailData.errorMessage && (
                <div className={e.detailRow}><strong>Error:</strong> <span className={e.errorText}>{detailData.errorMessage}</span></div>
              )}
              <div className={e.detailRow}><strong>Template:</strong> {detailData.templateKey || '—'}</div>
              <div className={e.detailRow}><strong>Retry Count:</strong> {detailData.retryCount}</div>
              {detailData.sourceModule && (
                <div className={e.detailRow}>
                  <strong>Source:</strong> {detailData.sourceModule}{detailData.sourceId ? ` #${detailData.sourceId}` : ''}
                </div>
              )}
              <div className={e.detailRow}><strong>Created:</strong> {formatDate(detailData.createdDate)}</div>
              {detailData.sentDate && <div className={e.detailRow}><strong>Sent:</strong> {formatDate(detailData.sentDate)}</div>}
              {detailData.nextAttemptAt && (
                <div className={e.detailRow}><strong>Next Attempt:</strong> {formatDate(detailData.nextAttemptAt)}</div>
              )}
              {detailData.contentHtml && (
                <>
                  <h4 style={{ marginTop: 16, marginBottom: 8 }}>HTML Body</h4>
                  {/* M-3 (security review 2026-07-10): real sanitization via DOMPurify
                      before injecting server-derived HTML — replaces the old regex
                      stopgap `sanitizeEmailHtml`. */}
                  <div
                    className={e.htmlPreview}
                    dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(detailData.contentHtml) }}
                  />
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
