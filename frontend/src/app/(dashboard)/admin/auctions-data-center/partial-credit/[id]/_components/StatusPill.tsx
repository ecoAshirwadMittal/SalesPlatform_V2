import styles from '../../admin.module.css';

interface StatusPillProps {
  /** Inline-applied background colour, sourced live from the API. */
  colorHex: string;
  /** External or internal status text — depends on caller context. */
  label: string;
  /** Optional system status (rendered to the title attribute for debugging). */
  systemStatus?: string;
}

/**
 * Generic pill renderer used on the admin landing rows AND the detail-
 * page header strip. The colour is always sourced live from the
 * backend's `credit_request_statuses.color_hex` so SPKB-3664 admin edits
 * land immediately without redeploying the frontend (see Sprint 3
 * plan §11.Q5).
 */
export function StatusPill({ colorHex, label, systemStatus }: StatusPillProps) {
  return (
    <span
      className={styles.statusPill}
      style={{ backgroundColor: colorHex }}
      title={systemStatus}
    >
      {label}
    </span>
  );
}
