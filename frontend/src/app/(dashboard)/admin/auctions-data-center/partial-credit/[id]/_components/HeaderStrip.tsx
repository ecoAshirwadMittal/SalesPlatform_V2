import type { CreditRequestDetail } from '@/lib/partialCreditClient';
import styles from '../../admin.module.css';
import { StatusPill } from './StatusPill';

interface HeaderStripProps {
  detail: CreditRequestDetail;
  /**
   * Live colour for the status pill — preferred source is
   * `detail.statusColorHex` (DTO blocker — see TODO below). The caller
   * passes a fallback grey while the DTO field is still missing.
   */
  statusColorHex: string;
  /**
   * Whether the request is currently locked to this reviewer
   * (UNDER_REVIEW). The header doesn't change behaviour today, but the
   * caller already knows this from `detail.systemStatus` — kept here as
   * a hook for the future "Reviewed by: ..." indicator (§8.10).
   */
  onCompleteReview: () => void;
  /** Whether the Complete Review button should be disabled. */
  completeDisabled: boolean;
}

/**
 * Top-of-detail header with metadata + the Complete Review button.
 *
 * Per Figma §3.2 (design notes), the "Buyer" field is the human contact
 * name (e.g. `Jonathan Wildermeyer`) and the "Company" field is the
 * legal entity (e.g. `Acme Inc.`). The current `CreditRequestDetail`
 * DTO only carries `partyName`; until the backend exposes a contact
 * name field (`buyerName` / `requestedByName`), Buyer falls back to
 * `partyName` with a TODO marker (blocker surfaced in Group 3 output).
 *
 * Per §11.Q2 the admin detail should surface the INTERNAL status text
 * (`Under Review`) so the reviewer can distinguish their own lock from
 * the external label (`Pending Approval`). The detail DTO does not yet
 * carry `internalStatusText` — fall back to `displayStatus` and flag
 * the dependency as a blocker.
 *
 * Per §11.Q5 the status pill colour is server-driven from
 * `credit_request_statuses.color_hex`. The detail DTO does not yet
 * carry `statusColorHex` (the landing DTO does); fall back to the grey
 * passed by the caller and flag the dependency.
 */
export function HeaderStrip({
  detail,
  statusColorHex,
  onCompleteReview,
  completeDisabled,
}: HeaderStripProps) {
  // V91 — buyer contact name (#2), internal status text (#3) and
  // status pill colour (#4) all flow through CreditRequestDetail now.
  // Legacy rows + the backwards-compatible from() overload may leave
  // them null; fall back gracefully in each case.
  const buyerName = detail.buyerName ?? '—';
  const companyName = detail.partyName ?? '—';
  const statusLabel = detail.internalStatusText ?? detail.displayStatus;
  const pillColor = detail.statusColorHex ?? statusColorHex;

  return (
    <div className={styles.headerStrip}>
      <Field label="Request Date" value={formatDate(detail.orderCreatedDate)} />
      <Field label="Buyer" value={buyerName} />
      <Field label="Company" value={companyName} />
      <Field label="Order Number" value={detail.orderNumber} />
      <Field label="Request Reason" value={formatReasons(detail)} />
      <Field
        label="Status"
        value={
          <StatusPill
            colorHex={pillColor}
            label={statusLabel}
            systemStatus={detail.systemStatus}
          />
        }
      />
      {detail.reviewCompletedOn && (
        <Field label="Approved Date" value={formatDate(detail.reviewCompletedOn)} />
      )}
      <div className={styles.detailActions}>
        <button
          type="button"
          className={styles.buttonPrimary}
          onClick={onCompleteReview}
          disabled={completeDisabled}
        >
          Complete Review
        </button>
      </div>
    </div>
  );
}

interface FieldProps {
  label: string;
  value: React.ReactNode;
}

function Field({ label, value }: FieldProps) {
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
