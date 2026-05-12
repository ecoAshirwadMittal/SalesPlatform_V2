'use client';

import { useState } from 'react';
import styles from '../../admin.module.css';

export type CompleteReviewOutcome = 'APPROVED' | 'DECLINED';

interface CompleteReviewModalProps {
  /** Pre-selection of the outcome radio — per §11.Q4. */
  defaultOutcome: CompleteReviewOutcome;
  /** Optional inline error banner — populated by 400 validation issues. */
  errorMessage: string | null;
  /** While confirm is in-flight, disable both buttons + the radios. */
  submitting: boolean;
  onCancel: () => void;
  onConfirm: (outcome: CompleteReviewOutcome) => void;
}

/**
 * Confirmation modal for Complete Review. Per Sprint 3 plan §11.Q1
 * resolved copy:
 *   heading: "Complete review?"
 *   body:    "This will finalise the review and send the buyer a
 *             notification email. You cannot undo this action."
 * Outcome radios default to whichever the parent picked (Approved when
 * any line is ACCEPTED, else Declined — see §11.Q4).
 *
 * The component owns no async lifecycle of its own — the parent runs
 * the network call and re-renders with either an error or by closing
 * the modal entirely.
 */
export function CompleteReviewModal({
  defaultOutcome,
  errorMessage,
  submitting,
  onCancel,
  onConfirm,
}: CompleteReviewModalProps) {
  const [outcome, setOutcome] = useState<CompleteReviewOutcome>(defaultOutcome);

  return (
    <div className={styles.modalScrim} role="dialog" aria-modal="true" aria-labelledby="complete-review-heading">
      <div className={styles.modalCard}>
        <h2 id="complete-review-heading" className={styles.modalHeading}>
          Complete review?
        </h2>
        <p className={styles.modalBody}>
          This will finalise the review and send the buyer a notification email. You cannot undo
          this action.
        </p>

        {errorMessage && <div className={styles.modalErrorBanner}>{errorMessage}</div>}

        <div className={styles.modalOutcomeGroup}>
          <label className={styles.modalOutcomeOption}>
            <input
              type="radio"
              name="outcome"
              value="APPROVED"
              checked={outcome === 'APPROVED'}
              disabled={submitting}
              onChange={() => setOutcome('APPROVED')}
            />
            Approved
          </label>
          <label className={styles.modalOutcomeOption}>
            <input
              type="radio"
              name="outcome"
              value="DECLINED"
              checked={outcome === 'DECLINED'}
              disabled={submitting}
              onChange={() => setOutcome('DECLINED')}
            />
            Declined
          </label>
        </div>

        <div className={styles.modalActions}>
          <button
            type="button"
            className={styles.buttonGhost}
            disabled={submitting}
            onClick={onCancel}
          >
            Cancel
          </button>
          <button
            type="button"
            className={styles.buttonPrimary}
            disabled={submitting}
            onClick={() => onConfirm(outcome)}
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  );
}
