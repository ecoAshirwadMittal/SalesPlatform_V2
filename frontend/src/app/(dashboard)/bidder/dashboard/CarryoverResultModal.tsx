'use client';
import { BidderModal } from './BidderModal';
import type { CarryoverResult } from '@/lib/bidder';

/**
 * CarryoverResultModal — shown after the Carryover button is clicked.
 *
 * Two states driven by the `result.copied` count:
 *
 * EMPTY STATE (copied === 0)
 *   - No visible title/heading (Mendix renders no heading in this modal)
 *   - Body text centered, larger font:
 *     "You don't have bids from last week to carry over."
 *   - Close (×) top-right only — no footer CTA button
 *   QA reference: docs/qa-reference/qa-04-carryover-empty-modal.png
 *
 * SUCCESS STATE (copied > 0)
 *   - Body: "Carried over {N} bids from Week {prevWeek}." (or "from last
 *     week" when prevWeek is null)
 *   - Close (×) top-right only — no footer CTA button
 *
 * NEEDS REVIEW: success-state copy is provisional ("Carried over N bids
 * from Week X."). Verify against a future QA walkthrough when carryover
 * has live data.
 */

interface CarryoverResultModalProps {
  result: CarryoverResult;
  onClose: () => void;
}

export function CarryoverResultModal({ result, onClose }: CarryoverResultModalProps) {
  const isEmptyState = result.copied === 0;

  const bodyText = isEmptyState
    ? "You don't have bids from last week to carry over."
    : buildSuccessText(result.copied, result.prevWeek);

  return (
    <BidderModal
      title={isEmptyState ? 'No bids to carry over' : 'Carryover complete'}
      onClose={onClose}
    >
      <p
        style={{
          fontFamily: 'var(--font-family-primary)',
          fontSize: '16px',
          color: 'var(--color-text-body)',
          margin: '8px 0 0',
          textAlign: 'center',
          lineHeight: 1.5,
        }}
      >
        {bodyText}
      </p>
    </BidderModal>
  );
}

function buildSuccessText(copied: number, prevWeek: string | null): string {
  const weekLabel = prevWeek ? `Week ${prevWeek}` : 'last week';
  return `Carried over ${copied} bid${copied === 1 ? '' : 's'} from ${weekLabel}.`;
}
