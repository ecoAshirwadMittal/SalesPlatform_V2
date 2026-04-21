'use client';

import { useEffect, useRef } from 'react';
import styles from './schedule.module.css';
import {
  ROUND_LABELS,
  formatRange,
  isoFromFields,
  isoToFields,
  type FormState,
} from './schedule-form';

interface ConfirmModalProps {
  form: FormState;
  onCancel: () => void;
  onConfirm: () => void;
  submitting: boolean;
}

/**
 * Confirm Schedule modal — Mendix parity with `ScheduleAuction_Confirm`.
 * Shows the computed R1/R2/R3 timeline summary. Primary CTA reads
 * "Looks good, Schedule!" verbatim from `acc_AuctionSchedulePage`.
 *
 * Minimal focus-trap: two buttons (Cancel, Confirm) — Tab ping-pongs
 * between them. Escape cancels unless submitting.
 */
export function ConfirmModal({ form, onCancel, onConfirm, submitting }: ConfirmModalProps) {
  const dialogRef = useRef<HTMLDivElement | null>(null);
  const confirmBtnRef = useRef<HTMLButtonElement | null>(null);
  const previouslyFocusedRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    previouslyFocusedRef.current = document.activeElement as HTMLElement | null;
    confirmBtnRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !submitting) onCancel();
      if (e.key === 'Tab') {
        const focusable = dialogRef.current?.querySelectorAll<HTMLElement>(
          'button:not([disabled])',
        );
        if (!focusable || focusable.length === 0) return;
        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('keydown', onKey);
      previouslyFocusedRef.current?.focus?.();
    };
  }, [onCancel, submitting]);

  const r1 = formatRange(isoFromFields(form.round1), isoToFields(form.round1));
  const r2 = form.round2Active
    ? formatRange(isoFromFields(form.round2), isoToFields(form.round2))
    : null;
  const r3 = form.round3Active
    ? formatRange(isoFromFields(form.round3), isoToFields(form.round3))
    : null;

  return (
    <div
      className={styles.modalBackdrop}
      role="dialog"
      aria-modal="true"
      aria-labelledby="confirm-schedule-title"
      onMouseDown={(e) => {
        if (e.target === e.currentTarget && !submitting) onCancel();
      }}
    >
      <div className={styles.modal} ref={dialogRef}>
        <h3 id="confirm-schedule-title" className={styles.modalTitle}>
          Confirm auction schedule
        </h3>
        <p className={styles.modalSubtitle}>
          Review the rounds below before scheduling. Once confirmed, the auction
          will move to <strong>Scheduled</strong>.
        </p>
        <ul className={styles.summaryList}>
          <li className={styles.summaryItem}>
            <span className={styles.summaryRound}>{ROUND_LABELS[1]}</span>
            <span className={styles.summaryTime}>{r1}</span>
          </li>
          <li className={styles.summaryItem}>
            <span className={styles.summaryRound}>{ROUND_LABELS[2]}</span>
            {r2 ? (
              <span className={styles.summaryTime}>{r2}</span>
            ) : (
              <span className={`${styles.summaryTime} ${styles.summaryInactive}`}>Inactive</span>
            )}
          </li>
          <li className={styles.summaryItem}>
            <span className={styles.summaryRound}>{ROUND_LABELS[3]}</span>
            {r3 ? (
              <span className={styles.summaryTime}>{r3}</span>
            ) : (
              <span className={`${styles.summaryTime} ${styles.summaryInactive}`}>Inactive</span>
            )}
          </li>
        </ul>
        <div className={styles.modalActions}>
          <button
            type="button"
            className={styles.buttonGhost}
            onClick={onCancel}
            disabled={submitting}
          >
            Cancel
          </button>
          <button
            type="button"
            className={styles.button}
            onClick={onConfirm}
            disabled={submitting}
            ref={confirmBtnRef}
          >
            {submitting ? 'Scheduling…' : 'Looks good, Schedule!'}
          </button>
        </div>
      </div>
    </div>
  );
}
