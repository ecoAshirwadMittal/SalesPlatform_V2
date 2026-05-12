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
 * Field set: Request Date, Buyer / Company (party_name), Order Number,
 * Request Reason, Status. The admin's "Complete Review" CTA is
 * intentionally absent — buyers never finalise a request themselves.
 */
export function BuyerHeaderStrip({
  detail,
  statusColorHex,
  submittedByLine,
}: BuyerHeaderStripProps) {
  return (
    <div className={styles.headerStrip}>
      <Field label="Request Date" value={formatDate(detail.orderCreatedDate)} />
      <Field label="Company" value={detail.partyName ?? '—'} />
      <Field label="Order Number" value={detail.orderNumber} />
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
      {submittedByLine && (
        <div className={styles.submittedByLine}>{submittedByLine}</div>
      )}
    </div>
  );
}

function Field({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className={styles.headerField}>
      <span className={styles.headerLabel}>{label}</span>
      <span className={styles.headerValue}>{value}</span>
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
