'use client';

import styles from '../detail.module.css';

export type DecisionFilter = 'APPROVED' | 'DECLINED';

interface ApprovedDeclinedToggleProps {
  value: DecisionFilter;
  onChange: (next: DecisionFilter) => void;
  approvedCount: number;
  declinedCount: number;
}

/**
 * Figma component `Toggle - Partial Credit` (`656:3480`). Segmented
 * control rendered inside each multi-reason tab; filters the visible
 * lines to either Approved or Declined. Default is Approved (the
 * caller seeds the state with `'APPROVED'`).
 */
export function ApprovedDeclinedToggle({
  value,
  onChange,
  approvedCount,
  declinedCount,
}: ApprovedDeclinedToggleProps) {
  return (
    <div
      role="group"
      aria-label="Filter by decision"
      className={styles.decisionToggleGroup}
    >
      <button
        type="button"
        aria-pressed={value === 'APPROVED'}
        className={
          value === 'APPROVED'
            ? styles.decisionToggleActive
            : styles.decisionToggle
        }
        onClick={() => onChange('APPROVED')}
      >
        Approved ({approvedCount})
      </button>
      <button
        type="button"
        aria-pressed={value === 'DECLINED'}
        className={
          value === 'DECLINED'
            ? styles.decisionToggleActive
            : styles.decisionToggle
        }
        onClick={() => onChange('DECLINED')}
      >
        Declined ({declinedCount})
      </button>
    </div>
  );
}
