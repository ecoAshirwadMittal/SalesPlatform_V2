import type { CreditRequestDetail } from '@/lib/partialCreditClient';
import styles from '../../admin.module.css';
import { StatusPill } from './StatusPill';

interface HeaderStripProps {
  detail: CreditRequestDetail;
  /** Live colour for the status pill — sourced from the landing response. */
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
 * Top-of-detail header with two columns of metadata + the Complete
 * Review button. Per §11.Q2 the admin detail surfaces the INTERNAL
 * status text (`Under Review`) so the reviewer knows someone has the
 * request open — landing rows continue to render the external label.
 */
export function HeaderStrip({
  detail,
  statusColorHex,
  onCompleteReview,
  completeDisabled,
}: HeaderStripProps) {
  return (
    <div className={styles.headerStrip}>
      <Field label="Request Date" value={formatDate(detail.orderCreatedDate)} />
      <Field label="Buyer" value={detail.partyName ?? '—'} />
      <Field label="Company" value={detail.partyName ?? '—'} />
      <Field label="Order Number" value={detail.orderNumber} />
      <Field label="Request Reason" value={formatReasons(detail)} />
      <Field
        label="Status"
        value={
          <StatusPill
            colorHex={statusColorHex}
            label={detail.displayStatus}
            systemStatus={detail.systemStatus}
          />
        }
      />
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
