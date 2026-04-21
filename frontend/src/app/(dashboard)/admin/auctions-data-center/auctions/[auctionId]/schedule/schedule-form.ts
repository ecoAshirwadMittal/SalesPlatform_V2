/**
 * Form model + date helpers shared by the Auction Scheduling page and its
 * modal sub-components. Extracted so the main page component stays under
 * the 800-line guideline.
 *
 * Admin inputs are collected in local time via native <input type="date">
 * + <input type="time">; we round-trip through ISO for the API on submit.
 */

import type { ScheduleDefaultsResponse } from '@/lib/auctions';

export interface RoundFields {
  fromDate: string; // yyyy-MM-dd
  fromTime: string; // HH:mm
  toDate: string;
  toTime: string;
}

export interface FormState {
  round1: RoundFields;
  round2: RoundFields;
  round2Active: boolean;
  round3: RoundFields;
  round3Active: boolean;
}

export interface RoundErrors {
  round1?: string;
  round2?: string;
  round3?: string;
}

// Mendix parity: customer-facing display names. Round 3 is "Upsell Round".
export const ROUND_LABELS = {
  1: 'Round 1',
  2: 'Round 2',
  3: 'Upsell Round',
} as const;

/**
 * Split an ISO timestamp into local-time date + time strings for the
 * native controls. Admins edit in their local timezone (matches how
 * Mendix surfaced these); we convert back to UTC on submit.
 */
export function splitIso(iso: string): { date: string; time: string } {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return { date: '', time: '' };
  const yyyy = String(d.getFullYear()).padStart(4, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  const hh = String(d.getHours()).padStart(2, '0');
  const mi = String(d.getMinutes()).padStart(2, '0');
  return { date: `${yyyy}-${mm}-${dd}`, time: `${hh}:${mi}` };
}

/**
 * Combine local-time date + time fields back to a millisecond-precision
 * UTC ISO string for the API.
 */
export function combineIso(date: string, time: string): string | null {
  if (!date || !time) return null;
  const [y, mo, d] = date.split('-').map(Number);
  const [h, mi] = time.split(':').map(Number);
  if ([y, mo, d, h, mi].some((n) => Number.isNaN(n))) return null;
  const local = new Date(y, mo - 1, d, h, mi, 0, 0);
  if (Number.isNaN(local.getTime())) return null;
  return local.toISOString();
}

export function addMinutes(iso: string | null, minutes: number): string | null {
  if (!iso) return null;
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return null;
  return new Date(d.getTime() + minutes * 60_000).toISOString();
}

export function isoFromFields(f: RoundFields): string | null {
  return combineIso(f.fromDate, f.fromTime);
}

export function isoToFields(f: RoundFields): string | null {
  return combineIso(f.toDate, f.toTime);
}

/**
 * Human-readable timeline for the confirm modal. Uses the user's locale
 * because all inputs were collected in local time — keeps the modal
 * visually aligned with what the admin typed.
 */
export function formatRange(startIso: string | null, endIso: string | null): string {
  if (!startIso || !endIso) return '—';
  const fmt = new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  });
  return `${fmt.format(new Date(startIso))} — ${fmt.format(new Date(endIso))}`;
}

export function initialStateFromDefaults(defaults: ScheduleDefaultsResponse): FormState {
  const r1Start = splitIso(defaults.round1Start);
  const r1End = splitIso(defaults.round1End);
  const r2Start = splitIso(defaults.round2Start);
  const r2End = splitIso(defaults.round2End);
  const r3Start = splitIso(defaults.round3Start);
  const r3End = splitIso(defaults.round3End);
  return {
    round1: {
      fromDate: r1Start.date,
      fromTime: r1Start.time,
      toDate: r1End.date,
      toTime: r1End.time,
    },
    round2: {
      fromDate: r2Start.date,
      fromTime: r2Start.time,
      toDate: r2End.date,
      toTime: r2End.time,
    },
    round2Active: defaults.round2Active,
    round3: {
      fromDate: r3Start.date,
      fromTime: r3Start.time,
      toDate: r3End.date,
      toTime: r3End.time,
    },
    round3Active: defaults.round3Active,
  };
}

/**
 * Client-side `end > start` validation per active round. Matches the
 * backend `VAL_Schedule_Auction`. Backend still re-validates.
 */
export function validateForm(state: FormState): RoundErrors {
  const errors: RoundErrors = {};
  const check = (key: 'round1' | 'round2' | 'round3', r: RoundFields) => {
    const start = isoFromFields(r);
    const end = isoToFields(r);
    if (!start || !end) {
      errors[key] = 'Both From and To are required.';
      return;
    }
    if (new Date(end).getTime() <= new Date(start).getTime()) {
      errors[key] = 'To must be after From.';
    }
  };
  check('round1', state.round1);
  if (state.round2Active) check('round2', state.round2);
  if (state.round3Active) check('round3', state.round3);
  return errors;
}

/**
 * Map comma-joined backend validation details to per-round slots.
 * Examples:
 *   "End must be after Start on Round 1"
 *   "Both Start and End are required on Round 2"
 * Matches by substring for resilience.
 */
export function mapDetailsToRoundErrors(details: string[]): RoundErrors {
  const next: RoundErrors = {};
  let unassigned = details.slice();
  const assignTo = (
    key: 'round1' | 'round2' | 'round3',
    matcher: (s: string) => boolean,
  ) => {
    const idx = unassigned.findIndex(matcher);
    if (idx >= 0) {
      next[key] = unassigned[idx];
      unassigned = unassigned.filter((_, i) => i !== idx);
    }
  };
  assignTo('round1', (s) => /round\s*1/i.test(s));
  assignTo('round2', (s) => /round\s*2/i.test(s));
  assignTo('round3', (s) => /round\s*3|upsell/i.test(s));
  if (unassigned.length > 0 && !next.round1) {
    next.round1 = unassigned.join(', ');
  }
  return next;
}
