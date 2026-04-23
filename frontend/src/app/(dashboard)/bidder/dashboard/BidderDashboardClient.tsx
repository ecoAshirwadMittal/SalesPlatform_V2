'use client';
import { useCallback, useEffect, useRef, useState } from 'react';
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
  BidRoundSummary,
  RoundTimerState,
  SchedulingAuctionSummary,
} from '@/lib/bidder';
import { BidGrid } from './BidGrid';
import { DashboardHeader } from './DashboardHeader';
import { EndOfBiddingPanel } from './EndOfBiddingPanel';

interface BidderDashboardClientProps {
  // `initial` is optional so the component can also own its first fetch
  // when the page is rendered entirely client-side (Next 16 SSR fetch of
  // a relative URL fails in Node; the app's other dashboard pages also
  // fetch in the browser).
  initial?: BidderDashboardResponse;
  buyerCodeId: number;
}

interface GridState {
  auction: SchedulingAuctionSummary;
  bidRound: BidRoundSummary;
  rowsById: Map<number, BidDataRow>;
  order: number[];
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
    timer: response.timer,
  };
}

export function BidderDashboardClient({
  initial,
  buyerCodeId,
}: BidderDashboardClientProps) {
  const [mode, setMode] = useState<BidderDashboardResponse['mode'] | 'LOADING'>(
    initial ? initial.mode : 'LOADING',
  );
  const [grid, setGrid] = useState<GridState | null>(() =>
    initial ? toGridState(initial) : null,
  );
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

  // First-load fetch when the parent didn't pre-hydrate. Runs once per
  // buyerCodeId change.
  useEffect(() => {
    if (initial) return;
    let cancelled = false;
    setMode('LOADING');
    loadDashboard(buyerCodeId)
      .then((response) => {
        if (!cancelled) applyResponse(response);
      })
      .catch((err: unknown) => {
        if (cancelled) return;
        console.error('dashboard load failed', err);
        setErrorMessage('Dashboard failed to load. Please refresh.');
      });
    return () => {
      cancelled = true;
    };
  }, [initial, buyerCodeId, applyResponse]);

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
      // Phase 8 will replace this with the "Your Bids have been Submitted!"
      // modal + BidsSubmittedModal component. For now the header flashes no
      // confirmation — the refetch is the visible state change.
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

  if (mode === 'LOADING') {
    return (
      <div className="p-6 text-[#112d32]">
        Loading dashboard…
      </div>
    );
  }

  if (mode === 'ERROR_AUCTION_NOT_FOUND') {
    return (
      <EndOfBiddingPanel subtitle="No scheduled auction is available." />
    );
  }

  if (mode === 'ALL_ROUNDS_DONE') {
    return (
      <EndOfBiddingPanel subtitle="Please come back next week." />
    );
  }

  if (mode === 'DOWNLOAD') {
    return (
      <EndOfBiddingPanel
        subtitle="Your bids from round 1 can be found below."
        action={{ label: 'Download your Round 1 Bids', onClick: () => {} }}
      />
    );
  }

  if (!grid) {
    // GRID mode with missing auction/bidRound payload — treat as an
    // empty state rather than throwing.
    return (
      <div className="p-6 text-[#112d32]">
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

  // Phase 9/10 will replace these stubs with modal openers.
  const handleCarryover = (): void => {
    // TODO(Phase 9): open CarryoverModal and POST /bidder/bid-rounds/{id}/carryover
  };
  const handleExport = (): void => {
    // TODO(Phase 10): GET /bidder/bid-rounds/{id}/export as .xlsx download
  };
  const handleImport = (): void => {
    // TODO(Phase 10): open ImportBidsModal
  };

  return (
    <div className="p-6">
      <DashboardHeader
        auction={grid.auction}
        bidRound={grid.bidRound}
        timer={grid.timer}
        canSubmit={canSubmit}
        submitting={submitting}
        errorMessage={errorMessage}
        onSubmit={handleSubmit}
        onCarryover={handleCarryover}
        onExport={handleExport}
        onImport={handleImport}
      />
      <BidGrid rows={rows} onRowSaved={handleRowSaved} />
    </div>
  );
}
