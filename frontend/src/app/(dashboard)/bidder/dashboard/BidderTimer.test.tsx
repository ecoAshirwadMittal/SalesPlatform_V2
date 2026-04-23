// @vitest-environment jsdom
import { render } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { BidderTimer } from './BidderTimer';
import type { RoundTimerState } from '@/lib/bidder';

function timerAt(endsAt: string, active = true, secondsUntilStart = 0, secondsUntilEnd = 0): RoundTimerState {
  return {
    now: new Date().toISOString(),
    startsAt: new Date(Date.now() - 1_000_000).toISOString(),
    endsAt,
    secondsUntilStart,
    secondsUntilEnd,
    active,
  };
}

describe('BidderTimer', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-04-22T12:00:00Z'));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('renders null when timer is null', () => {
    const { container } = render(<BidderTimer timer={null} />);
    expect(container.textContent).toBe('');
  });

  it('formats 5 hours remaining as "Ends in | 0D. | 5hrs. | 0min. |"', () => {
    const endsAt = new Date('2026-04-22T17:00:00Z').toISOString();
    const { container } = render(<BidderTimer timer={timerAt(endsAt, true, 0, 5 * 3600)} />);
    expect(container.textContent).toBe('Ends in | 0D. | 5hrs. | 0min. |');
  });

  it('formats 1 day 2 hours 30 minutes as "Ends in | 1D. | 2hrs. | 30min. |"', () => {
    const endsAt = new Date('2026-04-23T14:30:00Z').toISOString();
    const { container } = render(
      <BidderTimer timer={timerAt(endsAt, true, 0, (24 + 2) * 3600 + 30 * 60)} />,
    );
    expect(container.textContent).toBe('Ends in | 1D. | 2hrs. | 30min. |');
  });

  it('pre-round timer formats "Starts in" label', () => {
    const startsAt = new Date('2026-04-22T12:30:00Z').toISOString();
    const endsAt = new Date('2026-04-22T18:00:00Z').toISOString();
    const timer: RoundTimerState = {
      now: new Date().toISOString(),
      startsAt,
      endsAt,
      secondsUntilStart: 30 * 60,
      secondsUntilEnd: 6 * 3600,
      active: false,
    };
    const { container } = render(<BidderTimer timer={timer} />);
    expect(container.textContent).toBe('Starts in | 0D. | 0hrs. | 30min. |');
  });

  it('renders null when round is active but already past end', () => {
    const endsAt = new Date('2026-04-22T11:00:00Z').toISOString();
    const { container } = render(<BidderTimer timer={timerAt(endsAt, true, 0, 0)} />);
    expect(container.textContent).toBe('');
  });
});
