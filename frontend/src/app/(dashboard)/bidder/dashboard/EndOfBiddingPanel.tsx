'use client';

interface EndOfBiddingPanelProps {
  subtitle: string;
  action?: { label: string; onClick: () => void };
}

/**
 * End-of-bidding placeholder panel — DOWNLOAD / ALL_ROUNDS_DONE /
 * ERROR_AUCTION_NOT_FOUND modes.
 *
 * Per the 2026-04-22 plan Q4 decision, exact QA-verified copy for these
 * states is deferred to a post-R1 walkthrough when the live states can
 * be captured. The current strings are placeholders — match to QA copy
 * in Phase 13 pixel-QA.
 */
export function EndOfBiddingPanel({ subtitle, action }: EndOfBiddingPanelProps) {
  return (
    <div
      style={{
        fontFamily: 'var(--font-family-primary)',
        color: 'var(--color-text-body)',
      }}
      className="flex min-h-[60vh] flex-col items-center justify-center gap-4 p-6 text-center"
    >
      <h1
        className="text-2xl font-bold"
        style={{ color: 'var(--color-brand-teal-dark)' }}
      >
        <span style={{ color: 'var(--color-brand-green)' }}>Bidding</span> has ended.
      </h1>
      <p style={{ color: 'var(--color-text-muted)' }}>{subtitle}</p>
      {action && (
        <button
          type="button"
          onClick={action.onClick}
          className="btn-primary-green"
          style={{ marginTop: 8 }}
        >
          {action.label}
        </button>
      )}
    </div>
  );
}
