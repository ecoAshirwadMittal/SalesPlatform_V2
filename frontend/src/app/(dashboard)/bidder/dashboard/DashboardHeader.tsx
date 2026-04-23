'use client';
import type {
  SchedulingAuctionSummary,
  BidRoundSummary,
  RoundTimerState,
} from '@/lib/bidder';
import { BidderTimer } from './BidderTimer';
import { MinimumStartingBidLabel } from './MinimumStartingBidLabel';
import { CarryoverButton } from './CarryoverButton';
import { ExportBidsButton } from './ExportBidsButton';
import { ImportBidsButton } from './ImportBidsButton';
import styles from './dashboardHeader.module.css';

interface DashboardHeaderProps {
  auction: SchedulingAuctionSummary;
  bidRound: BidRoundSummary;
  timer: RoundTimerState | null;
  canSubmit: boolean;
  submitting: boolean;
  errorMessage: string | null;
  onSubmit: () => void;
  onCarryover: () => void;
  onExport: () => void;
  onImport: () => void;
}

/**
 * Bidder dashboard header — Mendix parity per
 * `docs/qa-reference/qa-03-bidder-dashboard-ad.png`:
 *
 * Row 1: title (Auction YYYY / WkNN + Round N) | Export / Import | timer | Submit Bids
 * Row 2: "Minimum starting bid - $2.50" (red) | Carryover
 *
 * Per the 2026-04-22 QA capture + decisions table, Round 3's label is
 * `Round 3` (not "Upsell Round" — the backend's name field is ignored in
 * favor of the numeric `round` value).
 */
export function DashboardHeader({
  auction,
  bidRound,
  timer,
  canSubmit,
  submitting,
  errorMessage,
  onSubmit,
  onCarryover,
  onExport,
  onImport,
}: DashboardHeaderProps) {
  const disabled = !canSubmit || submitting;
  const roundLabel = `Round ${bidRound.round}`;

  return (
    <header className={styles.header}>
      <div className={styles.titleRow}>
        <div className={styles.titleGroup}>
          <h2 className={styles.auctionTitle}>{auction.auctionTitle}</h2>
          <h2 className={styles.roundTitle}>{roundLabel}</h2>
        </div>
        <div className={styles.headerActions}>
          <ExportBidsButton onClick={onExport} />
          <ImportBidsButton onClick={onImport} />
        </div>
        <div className={styles.headerRight}>
          <span className={styles.timer}>
            <BidderTimer timer={timer} />
          </span>
          <button
            type="button"
            className={`btn-primary-green ${styles.submitButton}`}
            onClick={onSubmit}
            disabled={disabled}
          >
            {submitting ? 'Submitting…' : 'Submit Bids'}
          </button>
        </div>
      </div>

      <div className={styles.subHeaderRow}>
        <MinimumStartingBidLabel />
        <CarryoverButton onClick={onCarryover} />
      </div>

      {errorMessage ? (
        <div role="alert" className={styles.errorBanner}>
          {errorMessage}
        </div>
      ) : null}
    </header>
  );
}
