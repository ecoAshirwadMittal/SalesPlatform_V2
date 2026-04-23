'use client';
import { BidderModal } from './BidderModal';
import styles from './bidderModal.module.css';

/**
 * SubmitBidsEmptyStateModal — shown when the bidder clicks "Submit Bids"
 * with no non-zero bid amounts. Ports the Mendix "No Bids to Submit" guard
 * modal; no POST is made when this modal is triggered.
 *
 * QA reference: docs/qa-reference/qa-10-submit-no-bids-modal.png
 *
 * Copy is preserved verbatim from the Mendix HTML source, including the
 * "3.Use" spacing artifact (no space after the period in item 3).
 */

interface SubmitBidsEmptyStateModalProps {
  onClose: () => void;
}

export function SubmitBidsEmptyStateModal({ onClose }: SubmitBidsEmptyStateModalProps) {
  return (
    <BidderModal title="No Bids to Submit" onClose={onClose}>
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
        No{' '}
        <span style={{ color: 'var(--color-brand-green)' }}>Bids</span>
        {' '}to Submit
      </h2>

      <p
        style={{
          fontFamily: 'var(--font-family-primary)',
          fontSize: '14px',
          fontWeight: 700,
          color: 'var(--color-text-body)',
          margin: '0 0 8px',
        }}
      >
        Please add Bids by
      </p>

      <ol
        style={{
          fontFamily: 'var(--font-family-primary)',
          fontSize: '14px',
          color: 'var(--color-text-body)',
          margin: '0',
          paddingLeft: '20px',
          lineHeight: 1.6,
        }}
      >
        <li>Entering bids in the screen</li>
        <li>Use Export, add bids in the downloaded excel and import the file</li>
        {/* WHY: Mendix HTML source has "3.Use" with no space after the period — preserved verbatim */}
        <li>Use Carryover function to carry bids from last week and make necessary changes</li>
      </ol>

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
