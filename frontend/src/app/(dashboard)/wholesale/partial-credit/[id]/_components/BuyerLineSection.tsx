import type { CreditRequestDetail, SystemStatus } from '@/lib/partialCreditClient';
import styles from '../detail.module.css';

export type ReasonKind = 'MISSING' | 'WRONG' | 'ENCUMBERED';

/**
 * Per-tab decision filter. Group 4 (Figma `Toggle - Partial Credit`)
 * gates the visible rows when a request has multiple reasons; the
 * single-reason layout always renders both. Pass `null` to render
 * every row regardless of decision.
 */
export type DecisionFilter = 'APPROVED' | 'DECLINED' | null;

interface BuyerLineSectionProps {
  kind: ReasonKind;
  detail: CreditRequestDetail;
  /**
   * Pluralised count badge in the heading ("Missing Devices (12)").
   * Independent from `detail.*Lines.length` so the multi-reason variant
   * can show the unfiltered count even while a toggle hides some rows.
   */
  headingCountOverride?: number;
  /**
   * When set, only rows whose `reviewDecision` matches the filter are
   * rendered. Pass `null` (the default) to render every line.
   */
  decisionFilter?: DecisionFilter;
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
 *
 * Group 4 (Figma parity 2026-05-12): plural headings + count badge,
 * column shapes rewritten to match Figma for all three reasons.
 */
export function BuyerLineSection({
  kind,
  detail,
  headingCountOverride,
  decisionFilter = null,
}: BuyerLineSectionProps) {
  if (!sectionApplies(kind, detail)) return null;
  const decisionsVisible = isFinal(detail.systemStatus);
  const totalCount = headingCountOverride ?? unfilteredCount(kind, detail);

  return (
    <section
      className={styles.reasonSection}
      aria-label={sectionLabel(kind)}
      id={`reason-panel-${kind.toLowerCase()}`}
    >
      <h2 className={styles.reasonHeading}>
        {sectionLabel(kind)} <span className={styles.reasonCountBadge}>({totalCount})</span>
      </h2>
      <table className={styles.reasonTable}>
        <thead>
          <tr>
            {headerCells(kind).map((c) => (
              <th key={c}>{c}</th>
            ))}
            {decisionsVisible && <th>Decision</th>}
          </tr>
        </thead>
        <tbody>{renderRows(kind, detail, decisionsVisible, decisionFilter)}</tbody>
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

function unfilteredCount(kind: ReasonKind, detail: CreditRequestDetail): number {
  switch (kind) {
    case 'MISSING':
      return detail.missingLines.length;
    case 'WRONG':
      return detail.wrongLines.length;
    case 'ENCUMBERED':
      return detail.encumberedLines.length;
  }
}

/**
 * Plural form per Figma `534:11349` (buyer-detail surface). The admin
 * surface uses the singular form — see Group 3 for the matching change.
 */
function sectionLabel(kind: ReasonKind): string {
  switch (kind) {
    case 'MISSING':
      return 'Missing Devices';
    case 'WRONG':
      return 'Wrong Devices';
    case 'ENCUMBERED':
      return 'Encumbered Devices';
  }
}

function headerCells(kind: ReasonKind): string[] {
  switch (kind) {
    case 'MISSING':
      // Figma: Barcode | Brand | Model Description | Box Number | Amount Paid.
      // Grade column dropped; "Model" relabelled to "Model Description";
      // Box Number sourced from MissingDeviceLineDto.boxNumber (already
      // on the DTO since V89).
      return ['Barcode', 'Brand', 'Model Description', 'Box Number', 'Amount Paid'];
    case 'WRONG':
      // Figma: Expected Device Barcode | Expected Device Description |
      //        Received Device IMEI/Serial | Received Device Description |
      //        Photos | Amount Paid. "Latest Price" dropped — not in Figma.
      // Photos column shows the per-line photo count; per-line uploader
      // is opened from the table cell (see notes in BuyerLineSectionProps).
      return [
        'Expected Device Barcode',
        'Expected Device Description',
        'Received Device IMEI/Serial',
        'Received Device Description',
        'Photos',
        'Amount Paid',
      ];
    case 'ENCUMBERED':
      // Figma: Barcode | Device Description | Credit Due. Brand+Model
      // collapse into a single concat'd description cell; Amount Paid
      // + Actual Value collapse to "Credit Due" using the server-side
      // amountToCredit (falls back to amountPaid - actualValue when the
      // request is still pre-review and the credit hasn't been computed).
      return ['Barcode', 'Device Description', 'Credit Due'];
  }
}

function renderRows(
  kind: ReasonKind,
  detail: CreditRequestDetail,
  decisionsVisible: boolean,
  decisionFilter: DecisionFilter,
): React.ReactNode {
  switch (kind) {
    case 'MISSING':
      return detail.missingLines
        .filter((line) => passesDecisionFilter(line.reviewDecision, decisionFilter))
        .map((line) => (
          <tr key={`missing-${line.id}`}>
            <td>{line.barcodeSubmitted}</td>
            <td>{line.brand ?? '—'}</td>
            <td>{line.model ?? '—'}</td>
            <td>{line.boxNumber ?? '—'}</td>
            <td>{formatMoney(line.amountPaid)}</td>
            {decisionsVisible && <td>{decisionPill(line.reviewDecision)}</td>}
          </tr>
        ));
    case 'WRONG':
      return detail.wrongLines
        .filter((line) => passesDecisionFilter(line.reviewDecision, decisionFilter))
        .map((line) => (
          <tr key={`wrong-${line.id}`}>
            <td>{line.expectedBarcode}</td>
            <td>{line.expectedModel ?? '—'}</td>
            {/* DTO has actualImeiOrModel (combined). A dedicated `receivedImei`
                field is a backend dependency; until then we surface the
                combined field and render `—` as the placeholder when null.
                Recorded as a blocker in the Group 4 output. */}
            <td>{line.actualImeiOrModel ?? '—'}</td>
            <td>{line.actualModel ?? '—'}</td>
            {/* Photos column — backend doesn't yet thread per-line photo
                counts into the detail DTO. Render `—` placeholder; the
                request-level Photos section below the tables still works. */}
            <td>—</td>
            <td>{formatMoney(line.expectedAmountPaid)}</td>
            {decisionsVisible && <td>{decisionPill(line.reviewDecision)}</td>}
          </tr>
        ));
    case 'ENCUMBERED':
      return detail.encumberedLines
        .filter((line) => passesDecisionFilter(line.reviewDecision, decisionFilter))
        .map((line) => (
          <tr key={`enc-${line.id}`}>
            <td>{line.barcodeSubmitted}</td>
            <td>{formatDeviceDescription(line.brand, line.model)}</td>
            <td>{formatMoney(computeCreditDue(line.amountToCredit, line.amountPaid, line.actualValue))}</td>
            {decisionsVisible && <td>{decisionPill(line.reviewDecision)}</td>}
          </tr>
        ));
  }
}

/**
 * Concat brand + model into the single "Device Description" cell. Either
 * value being null renders the other half (or `—` when both are missing).
 */
function formatDeviceDescription(brand: string | null, model: string | null): string {
  const parts = [brand, model].filter((p): p is string => p !== null && p.length > 0);
  return parts.length === 0 ? '—' : parts.join(' ');
}

/**
 * Credit Due preference order: server-side `amountToCredit` (set after
 * review), else fall back to `amountPaid - actualValue` (pre-review
 * preview), else `amountPaid` alone, else null.
 */
function computeCreditDue(
  amountToCredit: number | null,
  amountPaid: number | null,
  actualValue: number | null,
): number | null {
  if (amountToCredit !== null) return amountToCredit;
  if (amountPaid !== null && actualValue !== null) return amountPaid - actualValue;
  return amountPaid;
}

function passesDecisionFilter(
  decision: string | null,
  filter: DecisionFilter,
): boolean {
  if (filter === null) return true;
  if (filter === 'APPROVED') return decision === 'ACCEPTED';
  return decision === 'DECLINED';
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
