import type { CreditRequestDetail } from '@/lib/partialCreditClient';
import styles from '../detail.module.css';

interface BuyerHeaderStripProps {
  detail: CreditRequestDetail;
  /** Server-rendered status pill colour from credit_request_statuses. */
  statusColorHex: string;
  /** Optional sub-line "Submitted by …" for the on-behalf flow (Sprint 4 chunk 6). */
  submittedByLine?: string;
}

/**
 * Read-only header strip — buyer-side mirror of the admin
 * {@code HeaderStrip} with no Complete Review button and the EXTERNAL
 * status label (per Sprint 4 §11.Q2 the admin sees the internal label,
 * the buyer sees the external one).
 *
 * Group 4 (Figma parity 2026-05-12) — fields are now:
 *   Order Number | Request Date | Request Reason | Status | Approved Date
 *
 * "Company" (party_name) is dropped — Figma treats the buyer as the
 * company so the duplicate field is removed. "Approved Date" is added
 * and reuses `reviewCompletedOn` (null pre-review renders as `—`).
 * The request number sits under the Order Number field as a sub-label
 * per Figma `534:11349`.
 */
export function BuyerHeaderStrip({
  detail,
  statusColorHex,
  submittedByLine,
}: BuyerHeaderStripProps) {
  return (
    <div className={styles.headerStrip}>
      <Field
        label="Order Number"
        value={detail.orderNumber}
        subLabel={detail.requestNumber}
      />
      <Field label="Request Date" value={formatDate(detail.orderCreatedDate)} />
      <Field label="Request Reason" value={formatReasons(detail)} />
      <Field
        label="Status"
        value={
          <span
            className={styles.statusPill}
            style={{ backgroundColor: statusColorHex }}
            data-status={detail.systemStatus}
          >
            {detail.displayStatus}
          </span>
        }
      />
      <Field label="Approved Date" value={formatDate(detail.reviewCompletedOn)} />
      {submittedByLine && (
        <div className={styles.submittedByLine}>{submittedByLine}</div>
      )}
    </div>
  );
}

interface FieldProps {
  label: string;
  value: React.ReactNode;
  subLabel?: string;
}

function Field({ label, value, subLabel }: FieldProps) {
  return (
    <div className={styles.headerField}>
      <span className={styles.headerLabel}>{label}</span>
      <span className={styles.headerValue}>{value}</span>
      {subLabel && <span className={styles.headerSubLabel}>{subLabel}</span>}
    </div>
  );
}

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  const d = new Date(iso);
  return Number.isNaN(d.getTime()) ? '—' : d.toLocaleDateString();
}

function formatReasons(detail: CreditRequestDetail): string {
  const reasons: string[] = [];
  if (detail.hasMissingDevice) reasons.push('Missing Device');
  if (detail.hasWrongDevice) reasons.push('Wrong Device');
  if (detail.hasEncumberedDevice) reasons.push('Encumbered Device');
  return reasons.length === 0 ? '—' : reasons.join(', ');
}
