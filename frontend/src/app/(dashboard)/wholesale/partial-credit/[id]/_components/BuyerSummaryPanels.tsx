import styles from '../detail.module.css';

interface BuyerSummaryPanelsProps {
  requestedSkus: number;
  requestedQty: number;
  requestedTotal: number;
  /** Null until reviewCompletedOn is set — gates rendering of the
   *  approved-side panel so it does not appear pre-review with zeros. */
  approvedSkus: number | null;
  approvedQty: number | null;
  approvedTotal: number | null;
}

/**
 * Two side-by-side mini-tables: Requested Credit (always rendered) and
 * Approved Credit (rendered only after the admin finalises the review).
 * Mirrors the admin {@code SummaryPanels} but the buyer never sees the
 * approved values mid-review (Sprint 4 §11.Q2).
 */
export function BuyerSummaryPanels({
  requestedSkus,
  requestedQty,
  requestedTotal,
  approvedSkus,
  approvedQty,
  approvedTotal,
}: BuyerSummaryPanelsProps) {
  const approvedVisible =
    approvedSkus !== null && approvedQty !== null && approvedTotal !== null;
  return (
    <div className={styles.summaryRow}>
      <Panel
        label="Requested Credit"
        skus={requestedSkus}
        qty={requestedQty}
        total={requestedTotal}
      />
      {approvedVisible && (
        <Panel
          label="Approved Credit"
          skus={approvedSkus}
          qty={approvedQty}
          total={approvedTotal}
        />
      )}
    </div>
  );
}

interface PanelProps {
  label: string;
  skus: number;
  qty: number;
  total: number;
}

function Panel({ label, skus, qty, total }: PanelProps) {
  return (
    <div className={styles.summaryPanel}>
      <span className={styles.summaryPanelLabel}>{label}</span>
      <div className={styles.summaryPanelTable}>
        <span className={styles.summaryCellHeader}>SKUs</span>
        <span className={styles.summaryCellHeader}>Qty</span>
        <span className={styles.summaryCellHeader}>Total</span>
        <div className={styles.summaryDivider} />
        <span className={styles.summaryCellValue}>{skus}</span>
        <span className={styles.summaryCellValue}>{qty}</span>
        <span className={styles.summaryCellValue}>{formatCurrency(total)}</span>
      </div>
    </div>
  );
}

function formatCurrency(value: number): string {
  return `$${value.toFixed(2)}`;
}
