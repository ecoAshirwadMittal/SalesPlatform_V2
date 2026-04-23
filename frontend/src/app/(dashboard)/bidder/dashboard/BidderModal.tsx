'use client';
import { useEffect, useCallback, type ReactNode } from 'react';
import styles from './bidderModal.module.css';

/**
 * BidderModal — shared overlay + card primitive.
 *
 * WHY a shared primitive: Phases 8 (submit modals), 9 (carryover), and 10
 * (import) all need the same overlay/card/close-X pattern. Keeping the chrome
 * here avoids copy-pasting three near-identical overlays. Each phase slots in
 * its own content via `children`.
 *
 * Accessibility:
 *  - role="dialog" + aria-modal="true" so AT treats it as a modal.
 *  - aria-label from the `title` prop (accessible name without visible heading
 *    duplication — caller controls the visible heading independently).
 *  - Escape key closes the modal.
 *  - Click-outside the card closes the modal.
 *  - Scroll is not locked (the grid behind is small; locking adds complexity
 *    for a non-critical UX at this stage — revisit if QA requests it).
 */
export interface BidderModalProps {
  /** Accessible title surfaced via aria-label on the dialog. */
  title: string;
  onClose: () => void;
  children: ReactNode;
}

export function BidderModal({ title, onClose, children }: BidderModalProps) {
  // Close on Escape key
  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    },
    [onClose],
  );

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [handleKeyDown]);

  // Click-outside: the overlay div is the backdrop; clicks that land directly
  // on it (not on the card child) propagate to this handler.
  const handleOverlayClick = useCallback(
    (e: React.MouseEvent<HTMLDivElement>) => {
      if (e.target === e.currentTarget) onClose();
    },
    [onClose],
  );

  return (
    <div
      className={styles.overlay}
      onClick={handleOverlayClick}
      aria-hidden="false"
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-label={title}
        className={styles.card}
      >
        <button
          type="button"
          className={styles.closeBtn}
          onClick={onClose}
          aria-label="Close dialog"
        >
          ×
        </button>
        {children}
      </div>
    </div>
  );
}
