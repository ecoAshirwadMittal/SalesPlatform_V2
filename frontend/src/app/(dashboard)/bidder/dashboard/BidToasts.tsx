'use client';

// ---------------------------------------------------------------------------
// BidToast — minimal aria-live toast banner for auto-save error feedback.
//
// WHY no toast library: the surface is simple (one message at a time,
// auto-dismiss managed by the parent, no queuing), and we want zero extra
// dependencies. The parent (BidderDashboardClient) owns the lifecycle:
//   1. set toast string on error
//   2. clear after 5s via setTimeout
// This component is purely presentational — it renders the message when
// non-null and uses aria-live="assertive" so screen readers announce it.
// ---------------------------------------------------------------------------

export interface BidToastProps {
  message: string | null;
}

export function BidToast({ message }: BidToastProps) {
  return (
    <div
      role="status"
      aria-live="assertive"
      aria-atomic="true"
      style={{
        position: 'fixed',
        bottom: '24px',
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 9999,
        pointerEvents: 'none',
        minWidth: '280px',
        maxWidth: '480px',
        transition: 'opacity 0.2s ease',
        opacity: message ? 1 : 0,
      }}
    >
      {message && (
        <div
          style={{
            background: '#112d32',
            color: '#ffffff',
            padding: '10px 20px',
            borderRadius: '6px',
            fontSize: '14px',
            fontWeight: 500,
            textAlign: 'center',
            boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
          }}
        >
          {message}
        </div>
      )}
    </div>
  );
}
