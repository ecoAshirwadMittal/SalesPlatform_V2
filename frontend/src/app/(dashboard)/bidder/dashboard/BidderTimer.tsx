'use client';
import { useEffect, useState } from 'react';
import type { RoundTimerState } from '@/lib/bidder';

interface BidderTimerProps {
  timer: RoundTimerState | null;
}

interface TimerParts {
  label: string;
  days: number;
  hours: number;
  minutes: number;
}

/**
 * Derive timer display parts from an ISO end/start timestamp and the current
 * wall-clock time. Returns `null` when the duration is zero or negative.
 */
function computeParts(
  isoTarget: string,
  label: string,
): TimerParts | null {
  const totalSeconds = Math.max(
    0,
    Math.floor((new Date(isoTarget).getTime() - Date.now()) / 1000),
  );
  if (totalSeconds <= 0) return null;

  const days = Math.floor(totalSeconds / 86400);
  const hours = Math.floor((totalSeconds % 86400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);

  return { label, days, hours, minutes };
}

/**
 * Format timer parts into the QA-verified pipe-delimited string:
 * `Ends in | {D}D. | {H}hrs. | {M}min. |`
 */
function formatParts(parts: TimerParts): string {
  return `${parts.label} | ${parts.days}D. | ${parts.hours}hrs. | ${parts.minutes}min. |`;
}

/**
 * Renders the round countdown timer in the QA-verified format:
 *   Active round:     `Ends in | {D}D. | {H}hrs. | {M}min. |`
 *   Pre-round:        `Starts in | {D}D. | {H}hrs. | {M}min. |`
 *   Null timer / done: empty
 *
 * Re-evaluates every 60 s by reading `timer.endsAt` / `timer.startsAt`
 * against `Date.now()` — avoids accumulated drift from a decrement counter.
 */
export function BidderTimer({ timer }: BidderTimerProps) {
  // Tick state is just a counter; the real time is always re-read from
  // Date.now() inside computeParts so drift never accumulates.
  const [tick, setTick] = useState<number>(0);

  useEffect(() => {
    if (!timer) return;
    const id = setInterval(() => setTick((t) => t + 1), 60_000);
    return () => clearInterval(id);
  }, [timer]);

  if (!timer) return null;

  const parts: TimerParts | null = timer.active && timer.endsAt
    ? computeParts(timer.endsAt, 'Ends in')
    : !timer.active && timer.secondsUntilStart > 0 && timer.startsAt
      ? computeParts(timer.startsAt, 'Starts in')
      : null;

  if (!parts) return null;

  // `tick` is read inside the render path (not just inside effects) so React
  // knows this component depends on the counter and re-renders when it fires.
  void tick;

  return <span>{formatParts(parts)}</span>;
}
