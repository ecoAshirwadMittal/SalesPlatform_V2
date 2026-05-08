'use client';

import { useEffect, useRef } from 'react';
import styles from './list.module.css';

interface NoActiveAuctionModalProps {
  onAcknowledge: () => void;
}

/**
 * "No active auction" information modal — Mendix parity (M11b).
 *
 * QA shows this dialog when the admin clicks "Auction Scheduling" in the
 * sidebar and there are no scheduled rounds. The local list previously
 * rendered an empty grid silently; SalesOps had no signal that they needed
 * to create an auction first. The modal must be acknowledged ("OK") and
 * routes the user to the inventory page where the Create Auction button
 * lives.
 */
export function NoActiveAuctionModal({ onAcknowledge }: NoActiveAuctionModalProps): React.ReactElement {
  const okBtnRef = useRef<HTMLButtonElement | null>(null);
  const previouslyFocusedRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    previouslyFocusedRef.current = document.activeElement as HTMLElement | null;
    okBtnRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' || e.key === 'Enter') {
        e.preventDefault();
        onAcknowledge();
      }
    };
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('keydown', onKey);
      previouslyFocusedRef.current?.focus?.();
    };
  }, [onAcknowledge]);

  return (
    <div
      className={styles.modalBackdrop}
      role="dialog"
      aria-modal="true"
      aria-labelledby="no-active-auction-title"
    >
      <div className={styles.modal}>
        <h3 id="no-active-auction-title" className={styles.modalTitle}>
          Information
        </h3>
        <p className={styles.modalSubtitle}>
          No active auction. Please create an auction from the Inventory page.
        </p>
        <div className={styles.modalActions}>
          <button
            type="button"
            className={styles.button}
            onClick={onAcknowledge}
            ref={okBtnRef}
          >
            OK
          </button>
        </div>
      </div>
    </div>
  );
}
