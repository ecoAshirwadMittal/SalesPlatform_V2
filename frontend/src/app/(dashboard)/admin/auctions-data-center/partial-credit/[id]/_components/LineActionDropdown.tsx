import type { ReviewDecision } from '@/lib/adminPartialCreditClient';
import styles from '../../admin.module.css';

interface LineActionDropdownProps {
  /** Current persisted decision. Null is rendered as PENDING. */
  value: ReviewDecision | null;
  onChange: (decision: ReviewDecision) => void;
  disabled?: boolean;
}

/**
 * Per-row Accept / Decline / Pending dropdown. The dropdown is the
 * single mutation point for one line's `review_decision` — the parent
 * row then re-renders the Amount-to-Credit cell from the response.
 *
 * The verb in the design is `Accept` (singular), not `Approve` — the
 * latter is reserved for the per-section bulk button per §4.3. The
 * backend treats both verbs as the same ACCEPTED enum value.
 */
export function LineActionDropdown({ value, onChange, disabled }: LineActionDropdownProps) {
  return (
    <select
      className={styles.lineDropdown}
      value={value ?? 'PENDING'}
      disabled={disabled}
      onChange={(e) => onChange(e.target.value as ReviewDecision)}
    >
      <option value="PENDING">Select</option>
      <option value="ACCEPTED">Accept</option>
      <option value="DECLINED">Decline</option>
    </select>
  );
}
