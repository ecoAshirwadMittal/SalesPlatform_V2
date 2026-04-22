'use client';
import { useCallback, useRef, useState } from 'react';
import {
  loadDashboard,
  submitBidRound,
  RateLimitedError,
  RoundClosedError,
  VersionConflictError,
} from '@/lib/bidder';
import type {
  BidderDashboardResponse,
  BidDataRow,
  BidDataTotals,
  BidRoundSummary,
  RoundTimerState,
  SchedulingAuctionSummary,
} from '@/lib/bidder';
import { BidGrid } from './BidGrid';
import { DashboardHeader } from './DashboardHeader';
import { SubmitBar } from './SubmitBar';

interface BidderDashboardClientProps {
  initial: BidderDashboardResponse;
  buyerCodeId: number;
}

interface GridState {
  auction: SchedulingAuctionSummary;
  bidRound: BidRoundSummary;
  rowsById: Map<number, BidDataRow>;
  order: number[];
  totals: BidDataTotals | null;
  timer: RoundTimerState | null;
}

function toGridState(response: BidderDashboardResponse): GridState | null {
  // Only GRID mode carries auction + bidRound. Other modes render
  // standalone copy and don't need this shape.
  if (response.mode !== 'GRID' || !response.auction || !response.bidRound) {
    return null;
  }
  const rowsById = new Map<number, BidDataRow>();
  const order: number[] = [];
  for (const row of response.rows) {
    rowsById.set(row.id, row);
    order.push(row.id);
  }
  return {
    auction: response.auction,
    bidRound: response.bidRound,
    rowsById,
    order,
    totals: response.totals,
    timer: response.timer,
  };
}

const FONT_STACK = "'Trebuchet MS', sans-serif";

export function BidderDashboardClient({
  initial,
  buyerCodeId,
}: BidderDashboardClientProps) {
  const [mode, setMode] = useState<BidderDashboardResponse['mode']>(initial.mode);
  const [grid, setGrid] = useState<GridState | null>(() => toGridState(initial));
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  // Latest buyerCodeId snapshot so callbacks stay stable without
  // re-registering on every render.
  const buyerCodeRef = useRef<number>(buyerCodeId);
  buyerCodeRef.current = buyerCodeId;
  // Synchronous guard against double-submit: two rapid clicks can land
  // in the same React batch before `setSubmitting(true)` flushes. The
  // ref check rejects the second call immediately.
  const submitInFlight = useRef<boolean>(false);

  const applyResponse = useCallback((response: BidderDashboardResponse): void => {
    setMode(response.mode);
    setGrid(toGridState(response));
  }, []);

  const handleRowSaved = useCallback((row: BidDataRow): void => {
    setGrid((prev) => {
      if (!prev) return prev;
      // Preserve insertion order; Map#set overwrites in place.
      const rowsById = new Map(prev.rowsById);
      rowsById.set(row.id, row);
      return { ...prev, rowsById };
    });
  }, []);

  const handleSubmit = useCallback(async (): Promise<void> => {
    if (submitInFlight.current || !grid) return;
    submitInFlight.current = true;
    const roundId = grid.bidRound.id;
    setSubmitting(true);
    setErrorMessage(null);
    try {
      await submitBidRound(roundId, buyerCodeRef.current);
      // submitBidRound returns a submission summary, not a full dashboard.
      // Refetch to pick up flipped `submitted` flags + updated totals.
      const fresh = await loadDashboard(buyerCodeRef.current);
      applyResponse(fresh);
    } catch (err: unknown) {
      if (err instanceof RateLimitedError) {
        setErrorMessage('Too many requests — please slow down and try again.');
      } else if (err instanceof RoundClosedError) {
        setErrorMessage('This round has closed. Refreshing the view…');
        try {
          const fresh = await loadDashboard(buyerCodeRef.current);
          applyResponse(fresh);
        } catch (refetchErr: unknown) {
          console.error('refetch after ROUND_CLOSED failed', refetchErr);
        }
      } else if (err instanceof VersionConflictError) {
        setErrorMessage('Another save collided with yours. Refreshing…');
        try {
          const fresh = await loadDashboard(buyerCodeRef.current);
          applyResponse(fresh);
        } catch (refetchErr: unknown) {
          console.error('refetch after VERSION_CONFLICT failed', refetchErr);
        }
      } else {
        console.error('submit failed', err);
        setErrorMessage('Submit failed. Please try again.');
      }
    } finally {
      submitInFlight.current = false;
      setSubmitting(false);
    }
  }, [grid, applyResponse]);

  if (mode === 'ERROR_AUCTION_NOT_FOUND') {
    return (
      <div style={{ fontFamily: FONT_STACK }} className="p-6 text-[#112d32]">
        No scheduled auction.
      </div>
    );
  }

  if (mode === 'ALL_ROUNDS_DONE') {
    return (
      <div style={{ fontFamily: FONT_STACK }} className="p-6 text-[#112d32]">
        All rounds have closed.
      </div>
    );
  }

  if (mode === 'DOWNLOAD') {
    return (
      <div style={{ fontFamily: FONT_STACK }} className="p-6 text-[#112d32]">
        Round 2: download inventory (coming soon).
      </div>
    );
  }

  if (!grid) {
    // GRID mode with missing auction/bidRound payload — treat as an
    // empty state rather than throwing.
    return (
      <div style={{ fontFamily: FONT_STACK }} className="p-6 text-[#112d32]">
        No bid data available.
      </div>
    );
  }

  const rows: BidDataRow[] = grid.order
    .map((id) => grid.rowsById.get(id))
    .filter((r): r is BidDataRow => r !== undefined);

  const canSubmit =
    !submitting &&
    grid.bidRound.roundStatus === 'Started' &&
    (grid.timer === null || grid.timer.active);

  return (
    <div style={{ fontFamily: FONT_STACK }} className="p-6 text-[#112d32]">
      <DashboardHeader
        auction={grid.auction}
        bidRound={grid.bidRound}
        timer={grid.timer}
      />
      <BidGrid rows={rows} onRowSaved={handleRowSaved} />
      <SubmitBar
        totals={grid.totals}
        canSubmit={canSubmit}
        onSubmit={handleSubmit}
        submitting={submitting}
        errorMessage={errorMessage}
      />
    </div>
  );
}
