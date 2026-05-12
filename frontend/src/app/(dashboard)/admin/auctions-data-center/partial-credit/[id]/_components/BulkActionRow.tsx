import styles from '../../admin.module.css';

interface BulkActionRowProps {
  onApproveAll: () => void;
  onDeclineAll: () => void;
  disabled?: boolean;
}

/**
 * Right-aligned cluster of per-section Approve All / Decline All
 * buttons. Per §4.3 the bulk verbs are `Approve All` / `Decline All`
 * (note: `Approve`, not `Accept` — the line-level verb).
 *
 * The legacy "Lines: N" count chip was dropped per Figma parity review
 * (§3.5/§3.6/§3.7 — no count chip is rendered next to the section
 * heading). Section line counts continue to be derivable from the
 * underlying data; the chip just isn't part of the visual chrome.
 */
export function BulkActionRow({
  onApproveAll,
  onDeclineAll,
  disabled,
}: BulkActionRowProps) {
  return (
    <div className={styles.sectionActions}>
      <button
        type="button"
        className={styles.buttonAcceptSmall}
        disabled={disabled}
        onClick={(e) => {
          e.stopPropagation();
          onApproveAll();
        }}
      >
        Approve All
      </button>
      <button
        type="button"
        className={styles.buttonDanger}
        disabled={disabled}
        onClick={(e) => {
          e.stopPropagation();
          onDeclineAll();
        }}
      >
        Decline All
      </button>
    </div>
  );
}
