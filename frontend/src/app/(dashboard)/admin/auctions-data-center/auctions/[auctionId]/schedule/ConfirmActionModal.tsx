'use client';

import { useEffect, useRef } from 'react';
import styles from './schedule.module.css';

interface ConfirmActionModalProps {
  title: string;
  body: string;
  warning?: string;
  confirmLabel: string;
  confirmVariant?: 'danger' | 'primary';
  onCancel: () => void;
  onConfirm: () => void;
  submitting: boolean;
}

/**
 * Generic "are you sure?" modal used by Unschedule and Delete. Destructive
 * actions take the danger variant (red outline); the primary button focus
 * defaults to Cancel so a stray Enter keypress doesn't accidentally
 * delete or unschedule.
 */
export function ConfirmActionModal({
  title,
  body,
  warning,
  confirmLabel,
  confirmVariant = 'primary',
  onCancel,
  onConfirm,
  submitting,
}: ConfirmActionModalProps) {
  const cancelBtnRef = useRef<HTMLButtonElement | null>(null);

  useEffect(() => {
    cancelBtnRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !submitting) onCancel();
    };
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [onCancel, submitting]);

  return (
    <div
      className={styles.modalBackdrop}
      role="dialog"
      aria-modal="true"
      aria-labelledby="action-confirm-title"
      onMouseDown={(e) => {
        if (e.target === e.currentTarget && !submitting) onCancel();
      }}
    >
      <div className={styles.modal}>
        <h3 id="action-confirm-title" className={styles.modalTitle}>{title}</h3>
        <p className={styles.modalSubtitle}>{body}</p>
        {warning && <div className={styles.confirmWarning}>{warning}</div>}
        <div className={styles.modalActions}>
          <button
            type="button"
            className={styles.buttonGhost}
            onClick={onCancel}
            ref={cancelBtnRef}
            disabled={submitting}
          >
            Cancel
          </button>
          <button
            type="button"
            className={confirmVariant === 'danger' ? styles.buttonDanger : styles.button}
            onClick={onConfirm}
            disabled={submitting}
          >
            {submitting ? 'Working…' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
