import type { CreditRequestDetail, SystemStatus } from '@/lib/partialCreditClient';
import styles from '../detail.module.css';

export type ReasonKind = 'MISSING' | 'WRONG' | 'ENCUMBERED';

interface BuyerLineSectionProps {
  kind: ReasonKind;
  detail: CreditRequestDetail;
}

/**
 * Read-only mirror of the admin {@code ReasonSection}. Three Sprint-4
 * differences from the admin variant:
 *  1. No per-line Action dropdown (the admin owns decisions).
 *  2. No bulk "Approve All" / "Decline All" buttons.
 *  3. Per-line decision pill is hidden until the parent request is in a
 *     final state (APPROVED / DECLINED) per Sprint 4 §11.Q2 — the buyer
 *     should never see mid-review thrash.
 *
 * Each section renders only when its corresponding reason flag is set on
 * the request header.
 */
export function BuyerLineSection({ kind, detail }: BuyerLineSectionProps) {
  if (!sectionApplies(kind, detail)) return null;
  const decisionsVisible = isFinal(detail.systemStatus);

  return (
    <section className={styles.reasonSection} aria-label={sectionLabel(kind)}>
      <h2 className={styles.reasonHeading}>{sectionLabel(kind)}</h2>
      <table className={styles.reasonTable}>
        <thead>
          <tr>
            {headerCells(kind).map((c) => (
              <th key={c}>{c}</th>
            ))}
            {decisionsVisible && <th>Decision</th>}
          </tr>
        </thead>
        <tbody>
          {renderRows(kind, detail, decisionsVisible)}
        </tbody>
      </table>
    </section>
  );
}

function sectionApplies(kind: ReasonKind, detail: CreditRequestDetail): boolean {
  switch (kind) {
    case 'MISSING':
      return detail.hasMissingDevice && detail.missingLines.length > 0;
    case 'WRONG':
      return detail.hasWrongDevice && detail.wrongLines.length > 0;
    case 'ENCUMBERED':
      return detail.hasEncumberedDevice && detail.encumberedLines.length > 0;
  }
}

function sectionLabel(kind: ReasonKind): string {
  switch (kind) {
    case 'MISSING':
      return 'Missing Device';
    case 'WRONG':
      return 'Wrong Device';
    case 'ENCUMBERED':
      return 'Encumbered Device';
  }
}

function headerCells(kind: ReasonKind): string[] {
  switch (kind) {
    case 'MISSING':
      return ['Barcode', 'Brand', 'Model', 'Grade', 'Amount Paid'];
    case 'WRONG':
      return ['Expected Barcode', 'Expected Model', 'Actual Model', 'Amount Paid', 'Latest Price'];
    case 'ENCUMBERED':
      return ['Barcode', 'Brand', 'Model', 'Amount Paid', 'Actual Value'];
  }
}

function renderRows(
  kind: ReasonKind,
  detail: CreditRequestDetail,
  decisionsVisible: boolean,
): React.ReactNode {
  switch (kind) {
    case 'MISSING':
      return detail.missingLines.map((line) => (
        <tr key={`missing-${line.id}`}>
          <td>{line.barcodeSubmitted}</td>
          <td>{line.brand ?? '—'}</td>
          <td>{line.model ?? '—'}</td>
          <td>{line.grade ?? '—'}</td>
          <td>{formatMoney(line.amountPaid)}</td>
          {decisionsVisible && <td>{decisionPill(line.reviewDecision)}</td>}
        </tr>
      ));
    case 'WRONG':
      return detail.wrongLines.map((line) => (
        <tr key={`wrong-${line.id}`}>
          <td>{line.expectedBarcode}</td>
          <td>{line.expectedModel ?? '—'}</td>
          <td>{line.actualModel ?? '—'}</td>
          <td>{formatMoney(line.expectedAmountPaid)}</td>
          <td>{formatMoney(line.latestPrice)}</td>
          {decisionsVisible && <td>{decisionPill(line.reviewDecision)}</td>}
        </tr>
      ));
    case 'ENCUMBERED':
      return detail.encumberedLines.map((line) => (
        <tr key={`enc-${line.id}`}>
          <td>{line.barcodeSubmitted}</td>
          <td>{line.brand ?? '—'}</td>
          <td>{line.model ?? '—'}</td>
          <td>{formatMoney(line.amountPaid)}</td>
          <td>{formatMoney(line.actualValue)}</td>
          {decisionsVisible && <td>{decisionPill(line.reviewDecision)}</td>}
        </tr>
      ));
  }
}

function decisionPill(decision: string | null): React.ReactNode {
  if (!decision || decision === 'PENDING') {
    // Falls through to a neutral dash even when the request is final and
    // a line was somehow left PENDING — defensive: never reveal "we
    // forgot a line" to the buyer as a literal PENDING label.
    return <span aria-label="No decision">—</span>;
  }
  return (
    <span
      className={
        decision === 'ACCEPTED' ? styles.decisionAccepted : styles.decisionDeclined
      }
    >
      {decision === 'ACCEPTED' ? 'Accepted' : 'Declined'}
    </span>
  );
}

function formatMoney(v: number | null): string {
  if (v === null || v === undefined) return '—';
  return `$${v.toFixed(2)}`;
}

function isFinal(status: SystemStatus): boolean {
  return status === 'APPROVED' || status === 'DECLINED';
}
