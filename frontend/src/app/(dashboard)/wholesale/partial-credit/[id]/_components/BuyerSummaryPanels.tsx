import styles from '../detail.module.css';

interface BuyerSummaryPanelsProps {
  requestedQty: number;
  requestedTotal: number;
  /** Null until reviewCompletedOn is set — gates rendering of the
   *  approved-side panel so it does not appear pre-review with zeros. */
  approvedQty: number | null;
  approvedTotal: number | null;
}

/**
 * Two side-by-side mini-tables: Requested Credit (always rendered) and
 * Approved Credit (rendered only after the admin finalises the review).
 * Mirrors the admin {@code SummaryPanels} but the buyer never sees the
 * approved values mid-review (Sprint 4 §11.Q2).
 *
 * Group 4 (Figma parity 2026-05-12) — 2-column layout (Qty / Total).
 * SKUs column dropped per Figma `534:11349`. The Approved-Credit panel
 * still lacks the Figma highlight fill — `fill_NH6QXG` hex is
 * unresolved (parity report R10). TODO(design) below tracks it.
 */
export function BuyerSummaryPanels({
  requestedQty,
  requestedTotal,
  approvedQty,
  approvedTotal,
}: BuyerSummaryPanelsProps) {
  const approvedVisible = approvedQty !== null && approvedTotal !== null;
  return (
    <div className={styles.summaryRow}>
      <Panel
        label="Requested Credit"
        qty={requestedQty}
        total={requestedTotal}
      />
      {approvedVisible && (
        // TODO(design): Approved Credit panel header highlight
        // (Figma `fill_NH6QXG`) — hex unresolved in the Mendix
        // stylesheet. Ship without highlight until design confirms.
        <Panel label="Approved Credit" qty={approvedQty} total={approvedTotal} />
      )}
    </div>
  );
}

interface PanelProps {
  label: string;
  qty: number;
  total: number;
}

function Panel({ label, qty, total }: PanelProps) {
  return (
    <div className={styles.summaryPanel}>
      <span className={styles.summaryPanelLabel}>{label}</span>
      <div className={styles.summaryPanelTable}>
        <span className={styles.summaryCellHeader}>Qty</span>
        <span className={styles.summaryCellHeader}>Total</span>
        <div className={styles.summaryDivider} />
        <span className={styles.summaryCellValue}>{qty}</span>
        <span className={styles.summaryCellValue}>{formatCurrency(total)}</span>
      </div>
    </div>
  );
}

function formatCurrency(value: number): string {
  return `$${value.toFixed(2)}`;
}
