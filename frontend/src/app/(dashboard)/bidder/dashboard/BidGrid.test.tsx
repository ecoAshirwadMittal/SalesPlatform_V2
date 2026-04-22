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

describe('BidGrid', () => {
  // Deviation from verbatim plan: the plan snippet used `bidQuantity: null`,
  // which makes the input render with value="" on mount. React 19 skips
  // onChange when the input value doesn't actually change, so firing
  // `change` to '' on an already-empty input was a no-op and the test
  // timed out. Start with a filled quantity so "clear to blank" exercises
  // the real empty-string → null mapping path the test name describes.
  const mockRow: BidDataRow = {
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
  };

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
});
