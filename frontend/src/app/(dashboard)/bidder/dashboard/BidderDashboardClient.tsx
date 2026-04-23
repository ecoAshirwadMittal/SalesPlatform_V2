'use client';
import { useCallback, useEffect, useRef, useState } from 'react';
import {
  loadDashboard,
  submitBidRound,
  carryoverBidRound,
  exportBidRound,
  importBidRound,
  RateLimitedError,
  RoundClosedError,
  VersionConflictError,
} from '@/lib/bidder';
import type {
  BidderDashboardResponse,
  BidDataRow,
  BidImportResult,
  BidRoundSummary,
  CarryoverResult,
  RoundTimerState,
  SchedulingAuctionSummary,
} from '@/lib/bidder';
import { BidGrid } from './BidGrid';
import { BidToast } from './BidToasts';
import { DashboardHeader } from './DashboardHeader';
import { EndOfBiddingPanel } from './EndOfBiddingPanel';
import { SubmitBidsEmptyStateModal } from './SubmitBidsEmptyStateModal';
import { BidsSubmittedModal } from './BidsSubmittedModal';
import { CarryoverResultModal } from './CarryoverResultModal';
import { ImportBidsModal } from './ImportBidsModal';

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
  // Carryover result: null = modal closed, non-null = show CarryoverResultModal
  const [carryoverResult, setCarryoverResult] = useState<CarryoverResult | null>(null);
  // Import modal: false = closed, true = open
  const [importModalOpen, setImportModalOpen] = useState<boolean>(false);
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

  const handleCarryover = useCallback(async (): Promise<void> => {
    if (!grid) return;
    const roundId = grid.bidRound.id;
    try {
      const result = await carryoverBidRound(roundId, buyerCodeRef.current);
      // Refetch so bid_* values updated by carryover are reflected in the grid.
      if (result.copied > 0) {
        try {
          const fresh = await loadDashboard(buyerCodeRef.current);
          applyResponse(fresh);
        } catch (refetchErr: unknown) {
          console.error('refetch after carryover failed', refetchErr);
        }
      }
      setCarryoverResult(result);
    } catch (err: unknown) {
      if (err instanceof RoundClosedError) {
        setErrorMessage('This round has closed. Refreshing the view…');
        setGridDisabled(true);
        try {
          const fresh = await loadDashboard(buyerCodeRef.current);
          applyResponse(fresh);
        } catch (refetchErr: unknown) {
          console.error('refetch after ROUND_CLOSED (carryover) failed', refetchErr);
        }
      } else {
        console.error('carryover failed', err);
        setErrorMessage('Carryover failed. Please try again.');
      }
    }
  }, [grid, applyResponse]);

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
    // TODO(Phase 11 follow-up): add `GET /bidder/download-round-1?buyerCodeId=X`
    // backend endpoint that finds the closed Round 1 for the active week and
    // streams the xlsx. The existing `/bid-rounds/{id}/export` endpoint needs
    // a round id, which the DOWNLOAD response does not provide. Until that
    // endpoint exists the button no-ops. Copy is deferred to a live QA
    // walkthrough when the state is reachable — see wholesale-buyer-parity
    // plan §Phase 11 and Q4.
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

  const handleExport = (): void => {
    exportBidRound(grid.bidRound.id, buyerCodeRef.current);
  };

  const handleImport = (): void => {
    setImportModalOpen(true);
  };

  const handleImportSubmit = async (file: File): Promise<BidImportResult> => {
    return importBidRound(grid.bidRound.id, buyerCodeRef.current, file);
  };

  const handleImportComplete = async (): Promise<void> => {
    // Refetch the grid so any updated bid values are reflected immediately.
    try {
      const fresh = await loadDashboard(buyerCodeRef.current);
      applyResponse(fresh);
    } catch (err: unknown) {
      console.error('refetch after import failed', err);
    }
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
      {carryoverResult !== null && (
        <CarryoverResultModal
          result={carryoverResult}
          onClose={() => setCarryoverResult(null)}
        />
      )}
      <ImportBidsModal
        isOpen={importModalOpen}
        onClose={() => setImportModalOpen(false)}
        onSubmit={handleImportSubmit}
        onComplete={handleImportComplete}
      />
    </div>
  );
}
