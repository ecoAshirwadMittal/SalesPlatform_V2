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
 *
 * Column set, banner copy and empty-state copy are pulled verbatim from
 * `docs/tasks/partial-credit-sprint2-design-notes.md` §5. The fifth
 * (action / eye) column ships in Sprint 4 along with the detail page —
 * row click navigation is intentionally omitted here so we don't 404 the
 * buyer onto a route that does not exist yet.
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
      <div className={styles.landingHeadingRow}>
        <h1 className={styles.heading}>Credit Requests</h1>
        <div className={styles.landingHeadingActions}>
          {/* Sprint 4 stub — policy page does not exist yet but the link is
              part of the Figma landing chrome and must be visible now. */}
          <Link href="/wholesale/partial-credit/policy" className={styles.landingPolicyLink}>
            Credit Request Policy
          </Link>
          <button
            type="button"
            className={styles.buttonPrimary}
            onClick={() => router.push('/wholesale/partial-credit/new')}
            disabled={!buyerCodeId}
          >
            + Submit a Credit Request
          </button>
        </div>
      </div>

      {/* Landing banner — verbatim from design notes line 855. The R-2 / RMA
          callout lives ONLY on the landing page (see anomaly note #2). */}
      <div className={styles.landingBanner}>
        Have an issue with your order? You may be eligible for a credit. We&rsquo;ll review claims
        on missing, incorrect, or encumbered devices. If you are not R-2 certified and received an
        encumbered device, you must submit an RMA instead.
      </div>

      {error && <div className={styles.warningBanner}>{error}</div>}

      {/* TODO(Sprint 3): add column-header `Ab` filter chips + `calendar-days`
          icon picker for Date Submitted (see design notes §5.5). */}
      {rows === null ? (
        <p>Loading…</p>
      ) : rows.length === 0 ? (
        <div className={styles.card}>
          <p style={{ margin: 0 }}>There are currently no Partial Credit Requests</p>
        </div>
      ) : (
        <div className={styles.card} style={{ padding: 0 }}>
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th>Date Submitted</th>
                <th>Order Number</th>
                <th>Request Reasons</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id}>
                  <td>
                    {r.submittedDate ? new Date(r.submittedDate).toLocaleDateString() : '—'}
                  </td>
                  <td>{r.orderNumber}</td>
                  <td>{formatReasons(r)}</td>
                  <td>{renderStatusPill(r.systemStatus, r.displayStatus)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

/**
 * Joins reason names with commas to match the Figma sample rows
 * (e.g. "Missing Device, Wrong Device, Encumbered Device").
 */
function formatReasons(r: CreditRequestSummary): string {
  const reasons: string[] = [];
  if (r.hasMissingDevice) reasons.push('Missing Device');
  if (r.hasWrongDevice) reasons.push('Wrong Device');
  if (r.hasEncumberedDevice) reasons.push('Encumbered Device');
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
