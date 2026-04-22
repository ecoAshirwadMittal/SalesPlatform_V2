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
  BidDataTotals,
  BidRoundSummary,
  RoundTimerState,
  SchedulingAuctionSummary,
} from '@/lib/bidder';
import { BidGrid } from './BidGrid';
import { DashboardHeader } from './DashboardHeader';
import { EndOfBiddingPanel } from './EndOfBiddingPanel';
import { SubmitBar } from './SubmitBar';

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

// Pull the first non-null `submittedDatetime` off the initial payload so a
// fresh page load shows "Submitted at <stamp>" when the user already
// submitted earlier in this round. Mendix shows the same stamp on revisit.
function initialSubmittedAt(response: BidderDashboardResponse): Date | null {
  for (const row of response.rows) {
    if (row.submittedDatetime) {
      return new Date(row.submittedDatetime);
    }
  }
  return null;
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
  const [lastSubmittedAt, setLastSubmittedAt] = useState<Date | null>(() =>
    initial ? initialSubmittedAt(initial) : null,
  );
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
    // Keep the post-submit stamp in sync with server state on refetch,
    // so revisiting a submitted round shows the persisted timestamp.
    setLastSubmittedAt(initialSubmittedAt(response));
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
      // Stamp the post-submit confirmation line. We use `new Date()` rather
      // than the server's `submittedDatetime` so the user sees an immediate
      // local-clock confirmation; the resubmit path overwrites it on the
      // next click.
      setLastSubmittedAt(new Date());
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
      <div style={{ fontFamily: FONT_STACK }} className="p-6 text-[#112d32]">
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
        lastSubmittedAt={lastSubmittedAt}
      />
    </div>
  );
}
