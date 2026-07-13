'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { getActiveBuyerCode } from '@/lib/activeBuyerCode';
import {
  type CreditRequestSummary,
  deleteRequest,
  listRequests,
  type SystemStatus,
} from '@/lib/partialCreditClient';
import { getAuthUser } from '@/lib/session';
import { OnBehalfModal } from './OnBehalfModal';
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
  const [isSalesRep, setIsSalesRep] = useState(false);
  const [onBehalfOpen, setOnBehalfOpen] = useState(false);

  useEffect(() => {
    const user = getAuthUser();
    setIsSalesRep((user?.roles ?? []).includes('SalesRep'));
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

  // Hard-delete a DRAFT the buyer owns. The trash affordance is only rendered
  // on DRAFT rows, but the server independently enforces ownership + DRAFT-only
  // (403 / 409) — this is a UX shortcut, not the security boundary.
  async function handleDelete(request: CreditRequestSummary) {
    if (buyerCodeId === null) return;
    if (
      !window.confirm(
        `Delete draft credit request for order ${request.orderNumber}? This cannot be undone.`,
      )
    ) {
      return;
    }
    setError(null);
    try {
      await deleteRequest(request.id);
      const data = await listRequests(buyerCodeId);
      setRows(data);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to delete request');
    }
  }

  return (
    <div className={`pg-partial-credit ${styles.page}`}>
      {/* Group 7 — Figma has no breadcrumb on top-level pages. The legacy
          "All Buyer Codes › Credit Requests" breadcrumb was removed. */}
      <div className={styles.landingHeadingRow}>
        <h1 className={styles.heading}>Credit Requests</h1>
        <div className={styles.landingHeadingActions}>
          {/* Sprint 4 stub — policy page does not exist yet but the link is
              part of the Figma landing chrome and must be visible now. */}
          <Link href="/wholesale/partial-credit/policy" className={styles.landingPolicyLink}>
            Credit Request Policy
          </Link>
          {isSalesRep && (
            <button
              type="button"
              className={styles.buttonGhost}
              onClick={() => setOnBehalfOpen(true)}
              data-testid="on-behalf-trigger"
            >
              Submit on behalf
            </button>
          )}
          <button
            type="button"
            className={styles.buttonPrimary}
            onClick={() => router.push('/wholesale/partial-credit/new')}
            disabled={!buyerCodeId}
          >
            Submit a Credit Request
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
          icon picker for Date Submitted (see design notes §5.5).

          Group 7: empty-state is rendered INSIDE the table chrome as a
          full-row colspan message rather than a standalone card. The 5th
          (eye-icon) action column routes to the detail page. */}
      {rows === null ? (
        <p>Loading…</p>
      ) : (
        <div className={styles.card} style={{ padding: 0 }}>
          <table className={styles.gridTable}>
            <thead>
              <tr>
                <th>Date Submitted</th>
                <th>Order Number</th>
                <th>Request Reasons</th>
                <th>Status</th>
                <th className={styles.landingActionCol} aria-label="View" />
              </tr>
            </thead>
            <tbody>
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={5} className={styles.landingEmptyCell}>
                    There are currently no Partial Credit Requests
                  </td>
                </tr>
              ) : (
                rows.map((r) => (
                  <tr key={r.id}>
                    <td>
                      {r.submittedDate ? new Date(r.submittedDate).toLocaleDateString() : '—'}
                    </td>
                    <td>{r.orderNumber}</td>
                    <td>{formatReasons(r)}</td>
                    <td>{renderStatusPill(r.systemStatus, r.displayStatus)}</td>
                    <td className={styles.landingActionCol}>
                      <span className={styles.landingActionGroup}>
                        {r.systemStatus === 'DRAFT' && (
                          <button
                            type="button"
                            className={styles.landingDeleteButton}
                            onClick={() => handleDelete(r)}
                            aria-label={`Delete draft credit request ${r.requestNumber}`}
                          >
                            <TrashIcon />
                          </button>
                        )}
                        <Link
                          href={`/wholesale/partial-credit/${r.id}`}
                          className={styles.landingViewLink}
                          aria-label={`View credit request ${r.requestNumber}`}
                        >
                          <EyeIcon />
                        </Link>
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
      <OnBehalfModal
        open={onBehalfOpen}
        onClose={() => setOnBehalfOpen(false)}
        onDraftCreated={(draftId) => {
          // Sprint 4 chunk 8 — wizard step 1 now reads ?draftId=X and
          // resumes the existing draft instead of creating a new one,
          // so the rep can drive every step from this entry point.
          router.push(`/wholesale/partial-credit/new?draftId=${draftId}`);
        }}
      />
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

/**
 * Inline SVG eye glyph — Group 7 action column on the landing table.
 * Sized to ~18px to sit centered inside the action cell.
 */
function EyeIcon() {
  return (
    <svg
      viewBox="0 0 20 20"
      width="18"
      height="18"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path
        d="M1.667 10S4.167 4.167 10 4.167 18.333 10 18.333 10 15.833 15.833 10 15.833 1.667 10 1.667 10z"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle
        cx="10"
        cy="10"
        r="2.5"
        stroke="currentColor"
        strokeWidth="1.5"
      />
    </svg>
  );
}

/**
 * Inline SVG trash glyph — Group 7 action column, rendered only on DRAFT
 * rows. Sized to match {@link EyeIcon} so the two controls sit level.
 */
function TrashIcon() {
  return (
    <svg
      viewBox="0 0 20 20"
      width="18"
      height="18"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path
        d="M2.5 5h15M7.5 5V3.75A1.25 1.25 0 0 1 8.75 2.5h2.5A1.25 1.25 0 0 1 12.5 3.75V5m2.083 0-.52 9.36A1.25 1.25 0 0 1 12.816 15.5H7.184a1.25 1.25 0 0 1-1.247-1.14L5.417 5M8.333 8.333v4.167M11.667 8.333v4.167"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}
