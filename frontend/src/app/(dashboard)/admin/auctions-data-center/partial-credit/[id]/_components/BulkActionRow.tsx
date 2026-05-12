import styles from '../../admin.module.css';

interface BulkActionRowProps {
  label: string;
  count: number;
  onApproveAll: () => void;
  onDeclineAll: () => void;
  disabled?: boolean;
}

/**
 * Right-aligned cluster of per-section Approve All / Decline All
 * buttons. Per §4.3 the bulk verbs are `Approve All` / `Decline All`
 * (note: `Approve`, not `Accept` — the line-level verb).
 */
export function BulkActionRow({
  label,
  count,
  onApproveAll,
  onDeclineAll,
  disabled,
}: BulkActionRowProps) {
  return (
    <>
      <span className={styles.sectionCount}>
        {label}: {count}
      </span>
      <div className={styles.sectionActions}>
        <button
          type="button"
          className={styles.buttonAcceptSmall}
          disabled={disabled || count === 0}
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
          disabled={disabled || count === 0}
          onClick={(e) => {
            e.stopPropagation();
            onDeclineAll();
          }}
        >
          Decline All
        </button>
      </div>
    </>
  );
}
