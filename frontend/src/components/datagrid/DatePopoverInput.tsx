"use client";

import { useEffect, useId, useMemo, useRef, useState } from "react";
import { CalendarIcon } from "./icons";
import styles from "./datagrid.module.css";

interface Props {
  /** YYYY-MM-DD (or empty). The host passes its raw filter value here
   *  and updates state via {@code onChange}. */
  value: string;
  onChange: (next: string) => void;
  ariaLabel: string;
  placeholder?: string;
}

/**
 * Date input + calendar popover. Replaces the native
 * {@code <input type="date">} on the Last Updated filter so the visual
 * matches QA's Mendix DataGrid 2 calendar trigger (a custom button next
 * to a text input that opens a month-grid popover).
 *
 * Dates are exchanged in YYYY-MM-DD ISO form, matching what the rest of
 * the filter pipeline expects (FilterSpecParser coerces to UTC start
 * of day on the backend).
 */
export default function DatePopoverInput({
  value,
  onChange,
  ariaLabel,
  placeholder = "mm/dd/yyyy",
}: Props) {
  const [open, setOpen] = useState(false);
  // Month/year shown in the popover header. Defaults to the current
  // date or the parsed value if one is set.
  const initial = parseISO(value) ?? new Date();
  const [viewYear, setViewYear] = useState(initial.getFullYear());
  const [viewMonth, setViewMonth] = useState(initial.getMonth());
  const wrapRef = useRef<HTMLDivElement>(null);
  const popoverId = useId();

  useEffect(() => {
    if (!open) return;
    const onClickOutside = (e: MouseEvent) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target as Node)) setOpen(false);
    };
    const onKey = (e: KeyboardEvent) => { if (e.key === "Escape") setOpen(false); };
    window.addEventListener("mousedown", onClickOutside);
    window.addEventListener("keydown", onKey);
    return () => {
      window.removeEventListener("mousedown", onClickOutside);
      window.removeEventListener("keydown", onKey);
    };
  }, [open]);

  // Keep the popover header in sync if the host rewrites value
  // externally (e.g. clear-all).
  useEffect(() => {
    const d = parseISO(value);
    if (d) {
      setViewYear(d.getFullYear());
      setViewMonth(d.getMonth());
    }
  }, [value]);

  const days = useMemo(() => buildMonthGrid(viewYear, viewMonth), [viewYear, viewMonth]);

  const selectDay = (iso: string) => {
    onChange(iso);
    setOpen(false);
  };

  const shiftMonth = (delta: number) => {
    let m = viewMonth + delta;
    let y = viewYear;
    while (m < 0)  { m += 12; y -= 1; }
    while (m > 11) { m -= 12; y += 1; }
    setViewMonth(m);
    setViewYear(y);
  };

  const display = formatDisplay(value);

  return (
    <div className={styles.dateWrap} ref={wrapRef}>
      <input
        type="text"
        className={styles.filterInput}
        value={display}
        placeholder={placeholder}
        aria-label={ariaLabel}
        onChange={(e) => onChange(parseDisplay(e.target.value))}
      />
      <button
        type="button"
        className={styles.dateTrigger}
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-controls={open ? popoverId : undefined}
        aria-label={`${ariaLabel} calendar`}
        onClick={() => setOpen((v) => !v)}
      >
        <CalendarIcon />
      </button>
      {open && (
        <div className={styles.calendarPopover} role="dialog" aria-modal="false" id={popoverId}>
          <div className={styles.calendarHeader}>
            <button type="button" className={styles.calendarNav} aria-label="Previous month" onClick={() => shiftMonth(-1)}>‹</button>
            <span className={styles.calendarTitle}>
              {MONTH_LABELS[viewMonth]} {viewYear}
            </span>
            <button type="button" className={styles.calendarNav} aria-label="Next month" onClick={() => shiftMonth(1)}>›</button>
          </div>
          <div className={styles.calendarWeekdays}>
            {WEEKDAY_LABELS.map((d) => <span key={d}>{d}</span>)}
          </div>
          <div className={styles.calendarGrid}>
            {days.map((d) => (
              <button
                key={d.iso}
                type="button"
                className={`${styles.calendarDay} ${d.outsideMonth ? styles.calendarDayMuted : ""} ${d.iso === value ? styles.calendarDaySelected : ""} ${d.isToday ? styles.calendarDayToday : ""}`}
                onClick={() => selectDay(d.iso)}
                aria-label={d.iso}
                aria-pressed={d.iso === value}
              >
                {d.day}
              </button>
            ))}
          </div>
          <div className={styles.calendarFooter}>
            <button
              type="button"
              className={styles.calendarFooterAction}
              onClick={() => { onChange(""); setOpen(false); }}
            >
              Clear
            </button>
            <button
              type="button"
              className={styles.calendarFooterAction}
              onClick={() => selectDay(toISO(new Date()))}
            >
              Today
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

// ── Date helpers ─────────────────────────────────────────────────

const MONTH_LABELS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];
const WEEKDAY_LABELS = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];

interface DayCell {
  iso: string;
  day: number;
  outsideMonth: boolean;
  isToday: boolean;
}

/** Build the 6-row × 7-col grid for a month, including leading + trailing
 *  days from neighbouring months so the layout is uniform. */
function buildMonthGrid(year: number, month: number): DayCell[] {
  const todayIso = toISO(new Date());
  const firstDay = new Date(Date.UTC(year, month, 1));
  // JS getDay() returns 0=Sun..6=Sat — matches our WEEKDAY_LABELS order.
  const leadingBlank = firstDay.getUTCDay();
  const cells: DayCell[] = [];

  // Leading days from previous month
  const prevMonthDays = new Date(Date.UTC(year, month, 0)).getUTCDate();
  for (let i = leadingBlank - 1; i >= 0; i--) {
    const d = prevMonthDays - i;
    const date = new Date(Date.UTC(year, month - 1, d));
    cells.push({
      iso: toISO(date),
      day: d,
      outsideMonth: true,
      isToday: toISO(date) === todayIso,
    });
  }

  // Current month
  const daysInMonth = new Date(Date.UTC(year, month + 1, 0)).getUTCDate();
  for (let d = 1; d <= daysInMonth; d++) {
    const date = new Date(Date.UTC(year, month, d));
    cells.push({
      iso: toISO(date),
      day: d,
      outsideMonth: false,
      isToday: toISO(date) === todayIso,
    });
  }

  // Trailing days to fill 6 rows
  let trailingDay = 1;
  while (cells.length < 42) {
    const date = new Date(Date.UTC(year, month + 1, trailingDay));
    cells.push({
      iso: toISO(date),
      day: trailingDay,
      outsideMonth: true,
      isToday: toISO(date) === todayIso,
    });
    trailingDay++;
  }
  return cells;
}

function toISO(d: Date): string {
  const y = d.getUTCFullYear();
  const m = String(d.getUTCMonth() + 1).padStart(2, "0");
  const day = String(d.getUTCDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

function parseISO(value: string): Date | null {
  if (!value) return null;
  const m = value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
  if (!m) return null;
  const d = new Date(Date.UTC(Number(m[1]), Number(m[2]) - 1, Number(m[3])));
  return Number.isNaN(d.getTime()) ? null : d;
}

/** Format ISO YYYY-MM-DD as the user-facing MM/DD/YYYY shown in the
 *  text input. Falls back to the raw value if parse fails so the user
 *  can keep typing without losing characters. */
function formatDisplay(iso: string): string {
  const d = parseISO(iso);
  if (!d) return iso;
  const m = String(d.getUTCMonth() + 1).padStart(2, "0");
  const day = String(d.getUTCDate()).padStart(2, "0");
  return `${m}/${day}/${d.getUTCFullYear()}`;
}

/** Inverse of formatDisplay: parse the user-typed MM/DD/YYYY back to
 *  ISO. Returns the raw string unchanged when the format is incomplete
 *  so React's controlled input doesn't fight typing. */
function parseDisplay(input: string): string {
  const m = input.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
  if (!m) return input;
  return `${m[3]}-${m[1]}-${m[2]}`;
}
