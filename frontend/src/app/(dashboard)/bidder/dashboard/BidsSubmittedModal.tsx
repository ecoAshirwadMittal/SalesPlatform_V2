'use client';
import { BidderModal } from './BidderModal';
import styles from './bidderModal.module.css';

/**
 * BidsSubmittedModal — shown after a successful submit (first submit AND
 * resubmit show the same modal; `resubmit` prop drives no visual difference
 * per QA parity).
 *
 * QA reference: docs/qa-reference/qa-08-submit-success-modal.png
 *
 * Heading copy is verbatim from Mendix HTML:
 *   "Your Bids  have been Submitted!"
 * The double-space between "Bids" and "have" is intentional per QA analysis.
 * We use a JSX space + non-breaking space literal to preserve it reliably
 * across whitespace-collapsing contexts: {' '} after "Bids".
 */

interface BidsSubmittedModalProps {
  onClose: () => void;
  /** Passed through from BidSubmissionResult.resubmit — currently unused
   *  visually but kept in props so Phase 9+ can branch if needed. */
  resubmit?: boolean;
}

export function BidsSubmittedModal({ onClose }: BidsSubmittedModalProps) {
  return (
    <BidderModal title="Bids submitted" onClose={onClose}>
      <h2
        style={{
          fontFamily: 'var(--font-family-primary)',
          fontSize: '22px',
          fontWeight: 700,
          color: 'var(--color-text-body)',
          margin: '0 0 16px',
          textAlign: 'center',
        }}
      >
        {/* WHY: Mendix HTML has "Your Bids  have been Submitted!" with a
            double-space between "Bids" and "have". Preserved using a
            normal space + U+00A0 non-breaking space so HTML rendering
            cannot collapse the gap. */}
        Your{' '}
        <span style={{ color: 'var(--color-brand-green)' }}>Bids</span>
        {' '}
        {' '}have been Submitted!
      </h2>

      <p
        style={{
          fontFamily: 'var(--font-family-primary)',
          fontSize: '14px',
          color: 'var(--color-text-body)',
          margin: '0',
          textAlign: 'center',
          lineHeight: 1.5,
        }}
      >
        Please review your updated bids, quantity caps and resubmit for any changes.
      </p>

      <div className={styles.footer}>
        <button
          type="button"
          className={`btn-primary-green ${styles.footerClose}`}
          onClick={onClose}
        >
          Close
        </button>
      </div>
    </BidderModal>
  );
}
