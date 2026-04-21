'use client';

import styles from './schedule.module.css';
import type { RoundFields } from './schedule-form';

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
}

/**
 * Per-round form section: From Date/Time + To Date/Time. When
 * {@code fromReadOnly} is set (R2/R3), the From inputs are greyed and
 * disabled because they are derived from the previous round's End on the
 * parent. Optional {@code toggle} renders the active checkbox in the
 * section header.
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
}: RoundFieldsetProps) {
  const id = title.toLowerCase().replace(/\s+/g, '-');
  return (
    <section className={styles.roundCard} aria-labelledby={`${id}-title`}>
      <div className={styles.roundHeader}>
        <h2 id={`${id}-title`} className={styles.roundTitle}>{title}</h2>
        <div className={styles.roundHeaderRight}>
          {selectionRulesHref && (
            <a
              href={selectionRulesHref}
              target="_blank"
              rel="noopener noreferrer"
              className={styles.selectionRulesLink}
            >
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
      <div className={styles.fieldGrid}>
        <div className={`${styles.field} ${fromReadOnly ? styles.fieldReadOnly : ''}`}>
          <label htmlFor={`${id}-from-date`}>From Date</label>
          <input
            id={`${id}-from-date`}
            type="date"
            value={fields.fromDate}
            onChange={(e) => onChange('fromDate', e.target.value)}
            disabled={disabled || fromReadOnly}
            aria-readonly={fromReadOnly}
          />
        </div>
        <div className={`${styles.field} ${fromReadOnly ? styles.fieldReadOnly : ''}`}>
          <label htmlFor={`${id}-from-time`}>From Time</label>
          <input
            id={`${id}-from-time`}
            type="time"
            value={fields.fromTime}
            onChange={(e) => onChange('fromTime', e.target.value)}
            disabled={disabled || fromReadOnly}
            aria-readonly={fromReadOnly}
          />
        </div>
        <div className={styles.field}>
          <label htmlFor={`${id}-to-date`}>To Date</label>
          <input
            id={`${id}-to-date`}
            type="date"
            value={fields.toDate}
            onChange={(e) => onChange('toDate', e.target.value)}
            disabled={disabled}
          />
        </div>
        <div className={styles.field}>
          <label htmlFor={`${id}-to-time`}>To Time</label>
          <input
            id={`${id}-to-time`}
            type="time"
            value={fields.toTime}
            onChange={(e) => onChange('toTime', e.target.value)}
            disabled={disabled}
          />
        </div>
      </div>
      {error && <span className={styles.inlineError}>{error}</span>}
    </section>
  );
}
