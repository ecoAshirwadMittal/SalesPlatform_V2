'use client';

import styles from './endOfBiddingPanel.module.css';

interface EndOfBiddingAction {
  label: string;
  onClick: () => void;
}

interface EndOfBiddingPanelProps {
  subtitle: string;
  /**
   * Present only for the ended-auction state (legacy
   * `BidDownloadOnBuyerCodeSelect`): renders the "Auction {year} / Wk{week}"
   * heading above a bordered panel. Absent for ERROR_AUCTION_NOT_FOUND /
   * ALL_ROUNDS_DONE, which fall back to bare centered text.
   */
  auctionTitle?: string | null;
  actions?: EndOfBiddingAction[];
}

/**
 * End-of-bidding panel.
 *
 * - **Ended auction** (`auctionTitle` present) — pixel-clone of legacy
 *   `BidDownloadOnBuyerCodeSelect`: the auction heading, then a bordered panel
 *   with "Bidding has ended." + the subtitle + one "Download your Round N Bids"
 *   pill button per round. Styling values are measured from the H1 evidence
 *   capture and `migration_context/styling/EcoAtm.css` (`.confirmationheader`).
 * - **Truly-empty / all-rounds-done** (`auctionTitle` absent) — bare centered
 *   text, matching legacy's plain `Error_Auction_Not_Found` page. Preserved
 *   from the original component so the kept truly-empty branch is unchanged.
 */
export function EndOfBiddingPanel({ subtitle, auctionTitle, actions }: EndOfBiddingPanelProps) {
  const endedHeading = (
    <h1 className={styles.endedHeading}>
      <span className={styles.endedHeadingAccent}>Bidding</span> has ended.
    </h1>
  );

  // Truly-empty / all-rounds-done: unchanged bare centered layout.
  if (!auctionTitle) {
    return (
      <div
        style={{
          fontFamily: 'var(--font-family-primary)',
          color: 'var(--color-text-body)',
        }}
        className="flex min-h-[60vh] flex-col items-center justify-center gap-4 p-6 text-center"
      >
        <h1 className="text-2xl font-bold" style={{ color: 'var(--color-brand-teal-dark)' }}>
          <span style={{ color: 'var(--color-brand-green)' }}>Bidding</span> has ended.
        </h1>
        <p style={{ color: 'var(--color-text-muted)' }}>{subtitle}</p>
        {actions?.map((action) => (
          <button
            key={action.label}
            type="button"
            onClick={action.onClick}
            className="btn-primary-green"
            style={{ marginTop: 8 }}
          >
            {action.label}
          </button>
        ))}
      </div>
    );
  }

  // Ended auction: legacy heading + bordered download panel.
  return (
    <div className={styles.endedRoot}>
      <div className={styles.headingRow}>
        <h2 className={styles.auctionTitle}>{auctionTitle}</h2>
        <span className={styles.headingDivider} aria-hidden="true" />
      </div>
      <div className={styles.panel}>
        {endedHeading}
        {subtitle ? <p className={styles.subtitle}>{subtitle}</p> : null}
        {actions && actions.length > 0 ? (
          <div className={styles.actionRow}>
            {actions.map((action) => (
              <button
                key={action.label}
                type="button"
                className={styles.downloadButton}
                onClick={action.onClick}
              >
                {action.label}
              </button>
            ))}
          </div>
        ) : null}
      </div>
    </div>
  );
}
