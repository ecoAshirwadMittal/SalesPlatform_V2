import styles from '../../admin.module.css';
import type { HeaderSummary } from '@/lib/adminPartialCreditClient';

interface SummaryPanelsProps {
  /** Recomputed on every mutating decision call — pass the latest. */
  summary: HeaderSummary;
}

/**
 * Two side-by-side mini-tables: Requested Credit + Total Credit. Per
 * Figma §3.3 each panel shows SKUs / Qty / Total in a 3-column grid
 * with a 0.5px divider between header and values. Per §8.2 we DO NOT
 * use the legacy "Requested RMA" / "Approved RMA" labels even on
 * encumbered-only requests — those are deferred to a possible Phase 2
 * RMA integration.
 */
export function SummaryPanels({ summary }: SummaryPanelsProps) {
  return (
    <div className={styles.summaryRow}>
      <Panel
        label="Requested Credit"
        skus={summary.requestedSkus}
        qty={summary.requestedQty}
        total={summary.requestedTotal}
      />
      <Panel
        label="Total Credit"
        skus={summary.approvedSkus}
        qty={summary.approvedQty}
        total={summary.approvedTotal}
      />
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
