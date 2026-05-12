'use client';

import { useEffect, useState, type ReactNode } from 'react';
import type {
  LineKind,
  PrologResult,
  ReviewDecision,
} from '@/lib/adminPartialCreditClient';
import type { CreditRequestDetail } from '@/lib/partialCreditClient';
import styles from '../../admin.module.css';
import { BulkActionRow } from './BulkActionRow';
import { LineActionDropdown } from './LineActionDropdown';

type MissingLine = CreditRequestDetail['missingLines'][number];
type WrongLine = CreditRequestDetail['wrongLines'][number];
type EncumberedLine = CreditRequestDetail['encumberedLines'][number];

interface BaseSectionProps {
  title: string;
  kind: LineKind;
  /** When true, every dropdown / button on the section is disabled. */
  busy: boolean;
  onSectionBulk: (kind: LineKind, decision: ReviewDecision) => void;
}

interface MissingSectionProps extends BaseSectionProps {
  kind: 'MISSING';
  lines: MissingLine[];
  onLineDecision: (lineId: number, decision: ReviewDecision) => void;
}

interface WrongSectionProps extends BaseSectionProps {
  kind: 'WRONG';
  lines: WrongLine[];
  onLineDecision: (lineId: number, decision: ReviewDecision) => void;
}

interface EncumberedSectionProps extends BaseSectionProps {
  kind: 'ENCUMBERED';
  lines: EncumberedLine[];
  onLineDecision: (lineId: number, decision: ReviewDecision) => void;
  onEncumberedFields: (
    lineId: number,
    prologResult: PrologResult,
    actualValue: number | null,
  ) => void;
}

type ReasonSectionProps = MissingSectionProps | WrongSectionProps | EncumberedSectionProps;

/**
 * Collapsible reason section. Renders one table whose columns vary by
 * {@link kind}. The section owns its own open / closed state — every
 * section defaults to open so reviewers see all flagged lines on first
 * load. Decision + Prolog Result changes call back into the parent
 * which fires the network request.
 */
export function ReasonSection(props: ReasonSectionProps) {
  const [open, setOpen] = useState(true);

  return (
    <section className={styles.sectionCard}>
      <div
        className={styles.sectionHeader}
        onClick={() => setOpen((o) => !o)}
        role="button"
        aria-expanded={open}
        tabIndex={0}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            setOpen((o) => !o);
          }
        }}
      >
        <h2 className={styles.sectionTitle}>{props.title}</h2>
        <BulkActionRow
          onApproveAll={() => props.onSectionBulk(props.kind, 'ACCEPTED')}
          onDeclineAll={() => props.onSectionBulk(props.kind, 'DECLINED')}
          disabled={props.busy || props.lines.length === 0}
        />
        <span
          className={`${styles.chevron} ${open ? styles.chevronOpen : ''}`}
          aria-hidden="true"
        >
          <ChevronDown />
        </span>
      </div>

      {open && (
        <div className={styles.sectionBody}>
          {props.lines.length === 0 ? (
            <p className={styles.sectionEmpty}>No lines in this section.</p>
          ) : (
            renderTable(props)
          )}
        </div>
      )}
    </section>
  );
}

function renderTable(props: ReasonSectionProps): ReactNode {
  if (props.kind === 'MISSING') return <MissingTable {...props} />;
  if (props.kind === 'WRONG') return <WrongTable {...props} />;
  return <EncumberedTable {...props} />;
}

// ----------------------------------------------------------------------------
// Missing table — design notes §3.5.2
// Column order: Barcode | Brand | Model Description | Grade | Box Number
//   | Amount Paid | Ship Status | Action | Amount to Credit
// ----------------------------------------------------------------------------

function MissingTable({ lines, onLineDecision, busy }: MissingSectionProps) {
  return (
    <table className={styles.lineTable}>
      <thead>
        <tr>
          <th>Barcode</th>
          <th>Brand</th>
          <th>Model Description</th>
          <th>Grade</th>
          <th>Box Number</th>
          <th>Amount Paid</th>
          <th>Ship Status</th>
          <th className={styles.actionCell}>Action</th>
          <th className={styles.amountCreditCell}>Amount to Credit</th>
        </tr>
      </thead>
      <tbody>
        {lines.map((line) => (
          <tr key={line.id}>
            <td>{line.barcodeSubmitted}</td>
            <td>{line.brand ?? '—'}</td>
            <td>{line.model ?? '—'}</td>
            <td>{line.grade ?? '—'}</td>
            <td>{line.boxNumber ?? '—'}</td>
            <td>{formatCurrency(line.amountPaid)}</td>
            <td>{line.shipStatus ?? '—'}</td>
            <td className={styles.actionCell}>
              <LineActionDropdown
                value={line.reviewDecision as ReviewDecision | null}
                disabled={busy}
                onChange={(d) => onLineDecision(line.id, d)}
              />
            </td>
            <td className={styles.amountCreditCell}>{formatCurrency(line.amountToCredit)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// ----------------------------------------------------------------------------
// Wrong table — design notes §3.6.2 + parity-fix R8/§8.6
// Column order (10 cells, NO `Recommendation` column):
//   Expected Barcode | Expected Device | Grade | Box No. | Amount Paid
//   | Actual IMEI | Actual Device | Latest Price | Action | Amount to Credit
//
// The legacy `Recommendation` column was dropped per §8.6 — its value
// now (a) defaults the per-line Action dropdown when reviewer hasn't
// chosen and (b) surfaces as a hover tooltip on the Action `<th>` so
// reviewers can still see the engine's recommendation rationale.
// ----------------------------------------------------------------------------

function WrongTable({ lines, onLineDecision, busy }: WrongSectionProps) {
  return (
    <table className={styles.lineTable}>
      <thead>
        <tr>
          <th>Expected Barcode</th>
          <th>Expected Device</th>
          <th>Grade</th>
          <th>Box No.</th>
          <th>Amount Paid</th>
          <th>Actual IMEI</th>
          <th>Actual Device</th>
          <th>Latest Price</th>
          <th
            className={styles.actionCell}
            title="Default selection comes from the action recommendation engine."
          >
            Action
          </th>
          <th className={styles.amountCreditCell}>Amount to Credit</th>
        </tr>
      </thead>
      <tbody>
        {lines.map((line) => (
          <WrongRow
            key={line.id}
            line={line}
            busy={busy}
            onLineDecision={onLineDecision}
          />
        ))}
      </tbody>
    </table>
  );
}

interface WrongRowProps {
  line: WrongLine;
  busy: boolean;
  onLineDecision: (lineId: number, decision: ReviewDecision) => void;
}

function WrongRow({ line, busy, onLineDecision }: WrongRowProps) {
  // Default-from-recommendation: when the reviewer hasn't decided yet
  // (reviewDecision is null), surface the engine's recommendation as
  // the dropdown's pre-selected value. Recommendation strings from the
  // backend are 'ACCEPT' / 'DECLINE' / null — normalise to the
  // ReviewDecision enum the dropdown understands.
  const defaultDecision = recommendationToDecision(line.actionRecommendation);
  const effectiveValue: ReviewDecision | null =
    (line.reviewDecision as ReviewDecision | null) ?? defaultDecision;

  // TODO(DTO): WrongDeviceLineDto needs to surface `expectedBoxNumber`
  // (the column exists on the entity — see `WrongDeviceLine.java:32`).
  // Until then the cell renders an em-dash. Tracked as a Group-3
  // blocker.
  const boxNumber: string | null = null;
  return (
    <tr>
      <td>{line.expectedBarcode}</td>
      <td>{formatBrandModel(line.expectedBrand, line.expectedModel)}</td>
      <td>{line.expectedGrade ?? '—'}</td>
      <td>{boxNumber ?? '—'}</td>
      <td>{formatCurrency(line.expectedAmountPaid)}</td>
      <td>{line.actualImeiOrModel ?? '—'}</td>
      <td>{formatBrandModel(line.actualBrand, line.actualModel)}</td>
      <td>{formatCurrency(line.latestPrice)}</td>
      <td
        className={styles.actionCell}
        title={
          line.actionRecommendation
            ? `Recommended: ${formatRecommendation(line.actionRecommendation)}`
            : undefined
        }
      >
        <LineActionDropdown
          value={effectiveValue}
          disabled={busy}
          onChange={(d) => onLineDecision(line.id, d)}
        />
      </td>
      <td className={styles.amountCreditCell}>{formatCurrency(line.amountToCredit)}</td>
    </tr>
  );
}

function recommendationToDecision(rec: string | null | undefined): ReviewDecision | null {
  if (!rec) return null;
  const upper = rec.toUpperCase();
  if (upper === 'ACCEPT' || upper === 'ACCEPTED') return 'ACCEPTED';
  if (upper === 'DECLINE' || upper === 'DECLINED') return 'DECLINED';
  return null;
}

function formatRecommendation(rec: string): string {
  const upper = rec.toUpperCase();
  if (upper === 'ACCEPT' || upper === 'ACCEPTED') return 'Accept';
  if (upper === 'DECLINE' || upper === 'DECLINED') return 'Decline';
  return rec;
}

// ----------------------------------------------------------------------------
// Encumbered table — design notes §3.7 + plan §11.Q3
// Column order: Barcode | Brand/Model | Grade | Box | Paid | Prolog Result
//   | Actual Value | Action | Amount to Credit
//
// Per §11.Q3 the `Actual Value` column always renders; empty cells use
// the "Enter value" placeholder + green-tinted background.
// ----------------------------------------------------------------------------

function EncumberedTable({
  lines,
  onLineDecision,
  onEncumberedFields,
  busy,
}: EncumberedSectionProps) {
  return (
    <table className={styles.lineTable}>
      <thead>
        <tr>
          <th>Barcode</th>
          <th>Brand / Model</th>
          <th>Grade</th>
          <th>Box</th>
          <th>Paid</th>
          <th>Prolog Result</th>
          <th>Actual Value</th>
          <th className={styles.actionCell}>Action</th>
          <th className={styles.amountCreditCell}>Amount to Credit</th>
        </tr>
      </thead>
      <tbody>
        {lines.map((line) => (
          <EncumberedRow
            key={line.id}
            line={line}
            busy={busy}
            onLineDecision={onLineDecision}
            onEncumberedFields={onEncumberedFields}
          />
        ))}
      </tbody>
    </table>
  );
}

interface EncumberedRowProps {
  line: EncumberedLine;
  busy: boolean;
  onLineDecision: (lineId: number, decision: ReviewDecision) => void;
  onEncumberedFields: (
    lineId: number,
    prologResult: PrologResult,
    actualValue: number | null,
  ) => void;
}

function EncumberedRow({
  line,
  busy,
  onLineDecision,
  onEncumberedFields,
}: EncumberedRowProps) {
  // Track Actual Value locally so the field is editable mid-type without
  // round-tripping every keystroke. The blur handler is the commit point.
  const initial =
    line.actualValue === null || line.actualValue === undefined ? '' : String(line.actualValue);
  const [actualValueDraft, setActualValueDraft] = useState<string>(initial);

  // Keep the local draft in sync if the parent replaces the line
  // (e.g. after a section bulk operation re-fetches the detail).
  useEffect(() => {
    setActualValueDraft(initial);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initial]);

  const commitFields = (prolog: PrologResult, draft: string): void => {
    const parsed = draft.trim() === '' ? null : Number.parseFloat(draft);
    onEncumberedFields(
      line.id,
      prolog,
      Number.isFinite(parsed as number) ? (parsed as number) : null,
    );
  };

  return (
    <tr>
      <td>{line.barcodeSubmitted}</td>
      <td>{formatBrandModel(line.brand, line.model)}</td>
      <td>{line.grade ?? '—'}</td>
      <td>{line.boxNumber ?? '—'}</td>
      <td>{formatCurrency(line.amountPaid)}</td>
      <td>
        <select
          className={styles.lineDropdown}
          disabled={busy}
          value={line.prologResult ?? 'PENDING'}
          onChange={(e) =>
            commitFields(e.target.value as PrologResult, actualValueDraft)
          }
        >
          <option value="PENDING">Pending</option>
          <option value="ENCUMBERED">Encumbered</option>
          <option value="NOT_ENCUMBERED">Not Encumbered</option>
        </select>
      </td>
      <td>
        <input
          type="number"
          step="0.01"
          className={`${styles.actualValueInput} ${
            actualValueDraft.trim() === '' ? styles.actualValueInputEmpty : ''
          }`}
          placeholder="Enter value"
          value={actualValueDraft}
          disabled={busy}
          onChange={(e) => setActualValueDraft(e.target.value)}
          onBlur={() =>
            commitFields(
              (line.prologResult as PrologResult | null) ?? 'PENDING',
              actualValueDraft,
            )
          }
        />
      </td>
      <td className={styles.actionCell}>
        <LineActionDropdown
          value={line.reviewDecision as ReviewDecision | null}
          disabled={busy}
          onChange={(d) => onLineDecision(line.id, d)}
        />
      </td>
      <td className={styles.amountCreditCell}>{formatCurrency(line.amountToCredit)}</td>
    </tr>
  );
}

// ----------------------------------------------------------------------------
// Shared formatters + chevron icon
// ----------------------------------------------------------------------------

function formatCurrency(value: number | null | undefined): string {
  if (value === null || value === undefined) return '—';
  return `$${value.toFixed(2)}`;
}

function formatBrandModel(brand: string | null, model: string | null): string {
  if (!brand && !model) return '—';
  return [brand, model].filter(Boolean).join(' ');
}

function ChevronDown(): React.ReactNode {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <polyline points="6 9 12 15 18 9" />
    </svg>
  );
}
