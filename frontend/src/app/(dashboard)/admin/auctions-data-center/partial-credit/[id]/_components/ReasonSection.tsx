'use client';

import { useState, type ReactNode } from 'react';
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
          label="Lines"
          count={props.lines.length}
          onApproveAll={() => props.onSectionBulk(props.kind, 'ACCEPTED')}
          onDeclineAll={() => props.onSectionBulk(props.kind, 'DECLINED')}
          disabled={props.busy}
        />
        <span className={`${styles.chevron} ${open ? styles.chevronOpen : ''}`}>▾</span>
      </div>

      {open && (
        <div className={styles.sectionBody}>
          {props.lines.length === 0 ? (
            <p style={{ padding: '24px', margin: 0, color: '#534F4C' }}>No lines in this section.</p>
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
// Missing table — design notes §3.5
// ----------------------------------------------------------------------------

function MissingTable({ lines, onLineDecision, busy }: MissingSectionProps) {
  return (
    <table className={styles.lineTable}>
      <thead>
        <tr>
          <th>Ship Status</th>
          <th>Barcode</th>
          <th>Brand / Model</th>
          <th>Grade</th>
          <th>Box</th>
          <th>Paid</th>
          <th className={styles.actionCell}>Action</th>
          <th className={styles.amountCreditCell}>Credit</th>
        </tr>
      </thead>
      <tbody>
        {lines.map((line) => (
          <tr key={line.id}>
            <td>{line.shipStatus ?? '—'}</td>
            <td>{line.barcodeSubmitted}</td>
            <td>{formatBrandModel(line.brand, line.model)}</td>
            <td>{line.grade ?? '—'}</td>
            <td>{line.boxNumber ?? '—'}</td>
            <td>{formatCurrency(line.amountPaid)}</td>
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
// Wrong table — design notes §3.6
// ----------------------------------------------------------------------------

function WrongTable({ lines, onLineDecision, busy }: WrongSectionProps) {
  return (
    <table className={styles.lineTable}>
      <thead>
        <tr>
          <th>Expected Device</th>
          <th>Received Device</th>
          <th>Grade</th>
          <th>Latest Price</th>
          <th>Paid</th>
          <th>Recommendation</th>
          <th className={styles.actionCell}>Action</th>
          <th className={styles.amountCreditCell}>Credit</th>
        </tr>
      </thead>
      <tbody>
        {lines.map((line) => (
          <tr key={line.id}>
            <td>
              <div style={{ display: 'flex', flexDirection: 'column' }}>
                <span style={{ fontFamily: 'monospace', fontSize: 12 }}>
                  {line.expectedBarcode}
                </span>
                <span>{formatBrandModel(line.expectedBrand, line.expectedModel)}</span>
              </div>
            </td>
            <td>
              <div style={{ display: 'flex', flexDirection: 'column' }}>
                <span style={{ fontFamily: 'monospace', fontSize: 12 }}>
                  {line.actualImeiOrModel ?? '—'}
                </span>
                <span>{formatBrandModel(line.actualBrand, line.actualModel)}</span>
              </div>
            </td>
            <td>
              {/* Expected grade is the relevant cell — actual grade is
                  surfaced in the "received" device cell when present. */}
              {line.expectedGrade ?? '—'}
            </td>
            <td>{formatCurrency(line.latestPrice)}</td>
            <td>{formatCurrency(line.expectedAmountPaid)}</td>
            <td>{line.actionRecommendation ?? '—'}</td>
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
// Encumbered table — design notes §3.7 + plan §11.Q3
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
          <th className={styles.amountCreditCell}>Credit</th>
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

  const commitFields = (prolog: PrologResult, draft: string) => {
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
// Shared formatters
// ----------------------------------------------------------------------------

function formatCurrency(value: number | null | undefined): string {
  if (value === null || value === undefined) return '—';
  return `$${value.toFixed(2)}`;
}

function formatBrandModel(brand: string | null, model: string | null): string {
  if (!brand && !model) return '—';
  return [brand, model].filter(Boolean).join(' ');
}
