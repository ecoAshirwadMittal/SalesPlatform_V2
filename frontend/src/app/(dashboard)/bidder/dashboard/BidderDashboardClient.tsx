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
import { BidToast } from './BidToasts';
import { DashboardHeader } from './DashboardHeader';
import { EndOfBiddingPanel } from './EndOfBiddingPanel';
import { SubmitBidsEmptyStateModal } from './SubmitBidsEmptyStateModal';
import { BidsSubmittedModal } from './BidsSubmittedModal';

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
  const [gridDisabled, setGridDisabled] = useState<boolean>(false);
  // 'none' — no modal open
  // 'no-bids' — empty-state guard (no non-zero bids; no POST made)
  // 'success' — post-submit confirmation (first submit and resubmit)
  const [submitModal, setSubmitModal] = useState<'none' | 'no-bids' | 'success'>('none');
  const [lastSubmitWasResubmit, setLastSubmitWasResubmit] = useState<boolean>(false);
  // Toast state for auto-save errors. Auto-clears after 5s.
  const [toast, setToast] = useState<string | null>(null);

  useEffect(() => {
    if (!toast) return;
    const id = setTimeout(() => setToast(null), 5000);
    return () => clearTimeout(id);
  }, [toast]);
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

  // WHY: Auto-save errors from individual bid cells (RateLimitedError,
  // RoundClosedError, VersionConflictError) surface here as toasts so the
  // bidder doesn't lose context of which row they were editing. RoundClosed
  // also disables the grid and triggers a refetch so the state is consistent.
  const handleRowError = useCallback(async (err: unknown): Promise<void> => {
    if (err instanceof RateLimitedError) {
      setToast('Saving paused — please slow down.');
    } else if (err instanceof RoundClosedError) {
      setToast('This round has closed.');
      setGridDisabled(true);
      try {
        const fresh = await loadDashboard(buyerCodeRef.current);
        applyResponse(fresh);
      } catch (refetchErr: unknown) {
        console.error('refetch after ROUND_CLOSED (cell save) failed', refetchErr);
      }
    } else if (err instanceof VersionConflictError) {
      setToast('Another save collided with yours.');
      try {
        const fresh = await loadDashboard(buyerCodeRef.current);
        applyResponse(fresh);
      } catch (refetchErr: unknown) {
        console.error('refetch after VERSION_CONFLICT (cell save) failed', refetchErr);
      }
    } else {
      console.error('cell auto-save failed', err);
    }
  }, [applyResponse]);

  const handleSubmit = useCallback(async (): Promise<void> => {
    if (submitInFlight.current || !grid) return;

    // WHY: Client-side no-bids guard mirrors Mendix behaviour — if no row
    // has a bidAmount > 0, show the informational modal and do NOT POST.
    const hasBids = Array.from(grid.rowsById.values()).some((r) => r.bidAmount > 0);
    if (!hasBids) {
      setSubmitModal('no-bids');
      return;
    }

    submitInFlight.current = true;
    const roundId = grid.bidRound.id;
    setSubmitting(true);
    setErrorMessage(null);
    try {
      const result = await submitBidRound(roundId, buyerCodeRef.current);
      // Refetch to pick up flipped `submitted` flags + updated totals.
      const fresh = await loadDashboard(buyerCodeRef.current);
      applyResponse(fresh);
      // Show success modal; track resubmit flag for the prop.
      setLastSubmitWasResubmit(result.resubmit);
      setSubmitModal('success');
    } catch (err: unknown) {
      if (err instanceof RateLimitedError) {
        setErrorMessage('Too many requests — please slow down and try again.');
      } else if (err instanceof RoundClosedError) {
        setErrorMessage('This round has closed. Refreshing the view…');
        setGridDisabled(true);
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
      <BidToast message={toast} />
      <BidGrid
        rows={rows}
        onRowSaved={handleRowSaved}
        onRowError={handleRowError}
        disabled={gridDisabled}
        totalRowCount={rows.length}
      />

      {submitModal === 'no-bids' && (
        <SubmitBidsEmptyStateModal onClose={() => setSubmitModal('none')} />
      )}
      {submitModal === 'success' && (
        <BidsSubmittedModal
          onClose={() => setSubmitModal('none')}
          resubmit={lastSubmitWasResubmit}
        />
      )}
    </div>
  );
}
