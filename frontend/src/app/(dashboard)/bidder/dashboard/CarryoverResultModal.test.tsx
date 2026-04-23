// @vitest-environment jsdom
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { CarryoverResultModal } from './CarryoverResultModal';
import type { CarryoverResult } from '@/lib/bidder';

describe('CarryoverResultModal', () => {
  // -------------------------------------------------------------------------
  // Empty state (copied === 0)
  // -------------------------------------------------------------------------

  describe('empty state (copied === 0)', () => {
    const emptyResult: CarryoverResult = { copied: 0, notFound: 0, prevWeek: null };

    it('renders the empty-state body copy verbatim', () => {
      render(<CarryoverResultModal result={emptyResult} onClose={() => {}} />);
      expect(
        screen.getByText("You don't have bids from last week to carry over."),
      ).toBeInTheDocument();
    });

    it('does NOT render a visible heading element', () => {
      render(<CarryoverResultModal result={emptyResult} onClose={() => {}} />);
      // BidderModal sets aria-label on the dialog but does not render a <h2>
      // inside the modal body for the empty-state (Mendix parity: no heading).
      const headings = screen.queryAllByRole('heading');
      expect(headings).toHaveLength(0);
    });

    it('Escape key closes the modal', () => {
      const onClose = vi.fn();
      render(<CarryoverResultModal result={emptyResult} onClose={onClose} />);
      fireEvent.keyDown(document, { key: 'Escape' });
      expect(onClose).toHaveBeenCalledTimes(1);
    });

    it('close (×) button closes the modal', () => {
      const onClose = vi.fn();
      render(<CarryoverResultModal result={emptyResult} onClose={onClose} />);
      fireEvent.click(screen.getByRole('button', { name: 'Close dialog' }));
      expect(onClose).toHaveBeenCalledTimes(1);
    });
  });

  // -------------------------------------------------------------------------
  // Success state (copied > 0)
  // -------------------------------------------------------------------------

  describe('success state (copied > 0)', () => {
    it('renders success copy with count and prevWeek', () => {
      const result: CarryoverResult = { copied: 42, notFound: 8, prevWeek: '2026 / Wk15' };
      render(<CarryoverResultModal result={result} onClose={() => {}} />);
      expect(
        screen.getByText('Carried over 42 bids from Week 2026 / Wk15.'),
      ).toBeInTheDocument();
    });

    it('falls back to "from last week" when prevWeek is null', () => {
      const result: CarryoverResult = { copied: 5, notFound: 2, prevWeek: null };
      render(<CarryoverResultModal result={result} onClose={() => {}} />);
      expect(
        screen.getByText('Carried over 5 bids from last week.'),
      ).toBeInTheDocument();
    });

    it('uses singular "bid" when copied === 1', () => {
      const result: CarryoverResult = { copied: 1, notFound: 0, prevWeek: '2026 / Wk16' };
      render(<CarryoverResultModal result={result} onClose={() => {}} />);
      expect(
        screen.getByText('Carried over 1 bid from Week 2026 / Wk16.'),
      ).toBeInTheDocument();
    });

    it('Escape key closes the modal', () => {
      const onClose = vi.fn();
      const result: CarryoverResult = { copied: 10, notFound: 0, prevWeek: '2026 / Wk15' };
      render(<CarryoverResultModal result={result} onClose={onClose} />);
      fireEvent.keyDown(document, { key: 'Escape' });
      expect(onClose).toHaveBeenCalledTimes(1);
    });
  });
});
