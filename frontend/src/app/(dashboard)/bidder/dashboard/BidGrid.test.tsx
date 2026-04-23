// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { BidGrid } from './BidGrid';
import type { BidDataRow } from '@/lib/bidder';

// Mock only `saveBid` so the debounced autosave path resolves without
// hitting the network. Preserve every other export (typed error classes,
// schemas, helpers) so anything the component graph pulls in continues
// to behave normally.
vi.mock('@/lib/bidder', async () => {
  const actual = await vi.importActual<typeof import('@/lib/bidder')>('@/lib/bidder');
  return {
    ...actual,
    saveBid: vi.fn(async (_id: number, payload: { bidQuantity: number | null; bidAmount: number }) => ({
      id: 1,
      bidRoundId: 100,
      ecoid: 'AAA1',
      mergedGrade: 'A',
      buyerCodeType: 'Wholesale',
      bidQuantity: payload.bidQuantity,
      bidAmount: payload.bidAmount,
      targetPrice: 25,
      maximumQuantity: 10,
      payout: 0,
      submittedBidQuantity: null,
      submittedBidAmount: null,
      lastValidBidQuantity: null,
      lastValidBidAmount: null,
      submittedDatetime: null,
      changedDate: '2026-04-23',
    })),
  };
});

// ---------------------------------------------------------------------------
// Shared fixtures
// ---------------------------------------------------------------------------

const makeRow = (overrides: Partial<BidDataRow> = {}): BidDataRow => ({
  id: 1,
  bidRoundId: 100,
  ecoid: 'AAA1',
  mergedGrade: 'A',
  buyerCodeType: 'Wholesale',
  bidQuantity: 5,
  bidAmount: 0,
  targetPrice: 25,
  maximumQuantity: 10,
  payout: 0,
  submittedBidQuantity: null,
  submittedBidAmount: null,
  lastValidBidQuantity: null,
  lastValidBidAmount: null,
  submittedDatetime: null,
  changedDate: '2026-04-23',
  ...overrides,
});

const mockRow = makeRow();

const secondRow: BidDataRow = makeRow({
  id: 2,
  ecoid: 'BBB2',
  mergedGrade: 'B',
  targetPrice: 40,
  maximumQuantity: 20,
  bidQuantity: 3,
  bidAmount: 10,
  payout: 30,
});

// ---------------------------------------------------------------------------
// Original tests (unchanged contract)
// ---------------------------------------------------------------------------

describe('BidGrid', () => {
  // Deviation from verbatim plan: the plan snippet used `bidQuantity: null`,
  // which makes the input render with value="" on mount. React 19 skips
  // onChange when the input value doesn't actually change, so firing
  // `change` to '' on an already-empty input was a no-op and the test
  // timed out. Start with a filled quantity so "clear to blank" exercises
  // the real empty-string → null mapping path the test name describes.

  it('renders one row per bid-data item', () => {
    render(<BidGrid rows={[mockRow]} onRowSaved={() => {}} />);
    expect(screen.getByText('AAA1')).toBeInTheDocument();
    expect(screen.getByText('A')).toBeInTheDocument();
  });

  it('blank quantity input maps to null payload', async () => {
    const onSaved = vi.fn();
    render(<BidGrid rows={[mockRow]} onRowSaved={onSaved} />);
    const qtyInput = screen.getByLabelText(/Quantity for AAA1/i);
    fireEvent.change(qtyInput, { target: { value: '' } });
    // 500ms debounce elapses
    await waitFor(() => expect(onSaved).toHaveBeenCalled(), { timeout: 1000 });
  });

  // ---------------------------------------------------------------------------
  // Sort tests
  // ---------------------------------------------------------------------------

  describe('sort', () => {
    it('renders sort buttons for all 7 columns', () => {
      render(<BidGrid rows={[mockRow]} onRowSaved={() => {}} />);
      const sortButtons = [
        'Sort by Device',
        'Sort by Grade',
        'Sort by Target Price',
        'Sort by Max Qty',
        'Sort by Bid Qty',
        'Sort by Bid Amount',
        'Sort by Payout',
      ];
      for (const label of sortButtons) {
        expect(screen.getByRole('button', { name: label })).toBeInTheDocument();
      }
    });

    it('initial arrow glyph is neutral (↕)', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      expect(btn).toHaveTextContent('↕');
    });

    it('first click sets ascending sort (↑)', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      fireEvent.click(btn);
      expect(btn).toHaveTextContent('↑');
    });

    it('second click sets descending sort (↓)', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      fireEvent.click(btn);
      fireEvent.click(btn);
      expect(btn).toHaveTextContent('↓');
    });

    it('third click clears sort (back to ↕)', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      fireEvent.click(btn);
      fireEvent.click(btn);
      fireEvent.click(btn);
      expect(btn).toHaveTextContent('↕');
    });

    it('ascending sort by Device renders AAA1 before BBB2', () => {
      render(<BidGrid rows={[secondRow, mockRow]} onRowSaved={() => {}} />);
      fireEvent.click(screen.getByRole('button', { name: 'Sort by Device' }));
      const cells = screen.getAllByRole('cell');
      const ecoids = cells
        .map((c) => c.textContent ?? '')
        .filter((t) => t === 'AAA1' || t === 'BBB2');
      expect(ecoids[0]).toBe('AAA1');
      expect(ecoids[1]).toBe('BBB2');
    });

    it('descending sort by Device renders BBB2 before AAA1', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      fireEvent.click(btn);
      fireEvent.click(btn);
      const cells = screen.getAllByRole('cell');
      const ecoids = cells
        .map((c) => c.textContent ?? '')
        .filter((t) => t === 'AAA1' || t === 'BBB2');
      expect(ecoids[0]).toBe('BBB2');
      expect(ecoids[1]).toBe('AAA1');
    });

    it('sets aria-sort="ascending" on the active column header', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      fireEvent.click(screen.getByRole('button', { name: 'Sort by Device' }));
      // The th containing "Sort by Device" button should carry aria-sort
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      expect(btn.closest('th')).toHaveAttribute('aria-sort', 'ascending');
    });

    it('sets aria-sort="descending" after second click', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const btn = screen.getByRole('button', { name: 'Sort by Device' });
      fireEvent.click(btn);
      fireEvent.click(btn);
      expect(btn.closest('th')).toHaveAttribute('aria-sort', 'descending');
    });
  });

  // ---------------------------------------------------------------------------
  // Filter tests
  // ---------------------------------------------------------------------------

  describe('filter', () => {
    it('renders filter inputs for all 7 columns', () => {
      render(<BidGrid rows={[mockRow]} onRowSaved={() => {}} />);
      expect(screen.getByLabelText('Filter by Device')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by Grade')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by Target Price')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by Max Qty')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by Bid Qty')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by Bid Amount')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by Payout')).toBeInTheDocument();
    });

    it('text filter on Device hides non-matching rows', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      fireEvent.change(screen.getByLabelText('Filter by Device'), {
        target: { value: 'AAA' },
      });
      expect(screen.getByText('AAA1')).toBeInTheDocument();
      expect(screen.queryByText('BBB2')).not.toBeInTheDocument();
    });

    it('text filter on Grade hides non-matching rows', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      fireEvent.change(screen.getByLabelText('Filter by Grade'), {
        target: { value: 'B' },
      });
      expect(screen.queryByText('AAA1')).not.toBeInTheDocument();
      expect(screen.getByText('BBB2')).toBeInTheDocument();
    });

    it('clearing filter brings back all rows', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      const input = screen.getByLabelText('Filter by Device');
      fireEvent.change(input, { target: { value: 'AAA' } });
      expect(screen.queryByText('BBB2')).not.toBeInTheDocument();
      fireEvent.change(input, { target: { value: '' } });
      expect(screen.getByText('BBB2')).toBeInTheDocument();
    });

    it('numeric filter on Target Price hides non-matching rows', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      // mockRow.targetPrice = 25; secondRow.targetPrice = 40
      fireEvent.change(screen.getByLabelText('Filter by Target Price'), {
        target: { value: '25' },
      });
      expect(screen.getByText('AAA1')).toBeInTheDocument();
      expect(screen.queryByText('BBB2')).not.toBeInTheDocument();
    });
  });

  // ---------------------------------------------------------------------------
  // Footer tests
  // ---------------------------------------------------------------------------

  describe('footer', () => {
    it('shows total count when no filter is active', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} totalRowCount={2} />);
      expect(screen.getByText('Currently showing 2 of 2')).toBeInTheDocument();
    });

    it('updates matched count when a filter is applied', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} totalRowCount={2} />);
      fireEvent.change(screen.getByLabelText('Filter by Device'), {
        target: { value: 'AAA' },
      });
      expect(screen.getByText('Currently showing 1 of 2')).toBeInTheDocument();
    });

    it('uses rows.length as fallback when totalRowCount is omitted', () => {
      render(<BidGrid rows={[mockRow, secondRow]} onRowSaved={() => {}} />);
      expect(screen.getByText('Currently showing 2 of 2')).toBeInTheDocument();
    });
  });
});
