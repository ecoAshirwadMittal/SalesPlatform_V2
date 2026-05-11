'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { getActiveBuyerCode } from '@/lib/activeBuyerCode';
import {
  type CreditRequestSummary,
  listRequests,
  type SystemStatus,
} from '@/lib/partialCreditClient';
import styles from './wizard.module.css';

/**
 * Buyer landing page — Figma frame "Credit Requests Landing (Filled/Empty)".
 * Renders the buyer's own list of credit requests with status pills and a
 * primary CTA to start a new submission.
 */
export default function PartialCreditLandingPage() {
  const router = useRouter();
  const [rows, setRows] = useState<CreditRequestSummary[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [buyerCodeId, setBuyerCodeId] = useState<number | null>(null);

  useEffect(() => {
    const active = getActiveBuyerCode();
    if (!active) {
      router.replace('/buyer-select');
      return;
    }
    setBuyerCodeId(active.id);
    listRequests(active.id)
      .then((data) => setRows(data))
      .catch((e) => setError(e instanceof Error ? e.message : 'Failed to load requests'));
  }, [router]);

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>
        <Link href="/buyer-select">All Buyer Codes</Link> &nbsp;›&nbsp; Credit Requests
      </div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
        <h1 className={styles.heading}>Credit Requests</h1>
        <button
          type="button"
          className={styles.buttonPrimary}
          onClick={() => router.push('/wholesale/partial-credit/new')}
          disabled={!buyerCodeId}
        >
          + Submit a Credit Request
        </button>
      </div>

      {error && <div className={styles.warningBanner}>{error}</div>}

      {rows === null ? (
        <p>Loading…</p>
      ) : rows.length === 0 ? (
        <div className={styles.card}>
          <p style={{ margin: 0 }}>
            You have not submitted any partial credit requests yet. Click <strong>Submit a Credit
            Request</strong> to start.
          </p>
        </div>
      ) : (
        <div className={styles.card} style={{ padding: 0 }}>
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th>Request #</th>
                <th>Order #</th>
                <th>Request Reasons</th>
                <th>Devices</th>
                <th>Status</th>
                <th>Submitted</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr
                  key={r.id}
                  style={{ cursor: 'pointer' }}
                  onClick={() => router.push(`/wholesale/partial-credit/${r.id}`)}
                >
                  <td>{r.requestNumber}</td>
                  <td>{r.orderNumber}</td>
                  <td>{formatReasons(r)}</td>
                  <td>{r.totalDevices ?? '—'}</td>
                  <td>{renderStatusPill(r.systemStatus, r.displayStatus)}</td>
                  <td>{r.submittedDate ? new Date(r.submittedDate).toLocaleDateString() : '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function formatReasons(r: CreditRequestSummary): string {
  const reasons: string[] = [];
  if (r.hasMissingDevice) reasons.push('Missing');
  if (r.hasWrongDevice) reasons.push('Wrong');
  if (r.hasEncumberedDevice) reasons.push('Encumbered');
  return reasons.length === 0 ? '—' : reasons.join(', ');
}

function renderStatusPill(status: SystemStatus, label: string) {
  const className =
    status === 'APPROVED'
      ? styles.statusApproved
      : status === 'DECLINED'
      ? styles.statusDeclined
      : status === 'DRAFT'
      ? styles.statusDraft
      : styles.statusPending;
  return <span className={`${styles.statusPill} ${className}`}>{label}</span>;
}
