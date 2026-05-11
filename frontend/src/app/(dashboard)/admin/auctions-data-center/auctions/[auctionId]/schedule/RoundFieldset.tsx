'use client';

import styles from './schedule.module.css';
import type { RoundStatsView } from '@/lib/auctions';
import type { RoundFields } from './schedule-form';

/**
 * QA's Mendix page hard-codes "EST" because the eco team operates in
 * Eastern. We match the label for visual parity even though
 * `<input type="time">` records browser-local wall-clock — true
 * Eastern-zone conversion on submit is a separate effort. See
 * `docs/tasks/auction-scheduling-final-three-plan.md`.
 */
const TIMEZONE_LABEL = 'EST';

interface RoundFieldsetProps {
  title: string;
  fields: RoundFields;
  onChange: (field: keyof RoundFields, value: string) => void;
  disabled?: boolean;
  fromReadOnly?: boolean;
  error?: string;
  toggle?: {
    label: string;
    checked: boolean;
    onChange: (v: boolean) => void;
    disabled?: boolean;
  };
  selectionRulesHref?: string;
  /** Per-round Buyers / Total / DW-Only counts (gap H5). null when unknown. */
  stats?: RoundStatsView | null;
}

/**
 * Inline chat-bubble glyph that prefixes the "Selection Rules" link
 * (lucide MessageCircle path). Inlined to avoid pulling lucide-react
 * for one icon; `currentColor` makes it inherit the link color.
 */
function ChatBubbleIcon(): React.ReactElement {
  return (
    <svg
      aria-hidden="true"
      width="14"
      height="14"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M7.9 20A9 9 0 1 0 4 16.1L2 22Z" />
    </svg>
  );
}

/**
 * Per-round form section: From Date/Time + To Date/Time rendered as two
 * horizontal rows (QA visual parity). When {@code fromReadOnly} is set
 * (R2/R3), the From row is greyed and disabled because it is derived
 * from the previous round's End on the parent. Optional {@code toggle}
 * renders the active checkbox in the section header.
 */
export function RoundFieldset({
  title,
  fields,
  onChange,
  disabled,
  fromReadOnly,
  error,
  toggle,
  selectionRulesHref,
  stats,
}: RoundFieldsetProps) {
  const id = title.toLowerCase().replace(/\s+/g, '-');
  return (
    <section className={styles.roundCard} aria-labelledby={`${id}-title`}>
      <div className={styles.roundHeader}>
        <div className={styles.roundHeaderLeft}>
          <h2 id={`${id}-title`} className={styles.roundTitle}>{title}</h2>
          {stats && (
            <div className={styles.roundStats} aria-label="Round summary">
              <span>
                <span className={styles.roundStatsLabel}>Buyers</span>{' '}
                <span className={styles.roundStatsValue}>
                  {stats.buyerCount === null ? 'All' : stats.buyerCount.toLocaleString()}
                </span>
              </span>
              <span>
                <span className={styles.roundStatsLabel}>Total</span>{' '}
                <span className={styles.roundStatsValue}>
                  {stats.totalQuantity.toLocaleString()}
                </span>
              </span>
              <span>
                <span className={styles.roundStatsLabel}>DW Only</span>{' '}
                <span className={styles.roundStatsValue}>
                  {stats.dwTotalQuantity.toLocaleString()}
                </span>
              </span>
            </div>
          )}
        </div>
        <div className={styles.roundHeaderRight}>
          {selectionRulesHref && (
            <a
              href={selectionRulesHref}
              target="_blank"
              rel="noopener noreferrer"
              className={styles.selectionRulesLink}
            >
              <ChatBubbleIcon />
              Selection Rules
            </a>
          )}
          {toggle && (
            <label className={styles.activeToggle}>
              <input
                type="checkbox"
                checked={toggle.checked}
                onChange={(e) => toggle.onChange(e.target.checked)}
                disabled={toggle.disabled}
              />
              {toggle.label}
            </label>
          )}
        </div>
      </div>
      <div
        className={`${styles.timeRow} ${fromReadOnly ? styles.timeRowReadOnly : ''}`}
      >
        <span className={styles.timeRowLabel}>From:</span>
        <input
          id={`${id}-from-date`}
          type="date"
          aria-label={`${title} from date`}
          value={fields.fromDate}
          onChange={(e) => onChange('fromDate', e.target.value)}
          disabled={disabled || fromReadOnly}
          aria-readonly={fromReadOnly}
        />
        <input
          id={`${id}-from-time`}
          type="time"
          aria-label={`${title} from time`}
          value={fields.fromTime}
          onChange={(e) => onChange('fromTime', e.target.value)}
          disabled={disabled || fromReadOnly}
          aria-readonly={fromReadOnly}
        />
        <span className={styles.tzTag}>{TIMEZONE_LABEL}</span>
      </div>
      <div className={styles.timeRow}>
        <span className={styles.timeRowLabel}>To:</span>
        <input
          id={`${id}-to-date`}
          type="date"
          aria-label={`${title} to date`}
          value={fields.toDate}
          onChange={(e) => onChange('toDate', e.target.value)}
          disabled={disabled}
        />
        <input
          id={`${id}-to-time`}
          type="time"
          aria-label={`${title} to time`}
          value={fields.toTime}
          onChange={(e) => onChange('toTime', e.target.value)}
          disabled={disabled}
        />
        <span className={styles.tzTag}>{TIMEZONE_LABEL}</span>
      </div>
      {error && <span className={styles.inlineError}>{error}</span>}
    </section>
  );
}
