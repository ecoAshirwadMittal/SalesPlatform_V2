'use client';

/**
 * Small dismissible error banner used by PWS pages to surface fetch
 * and save failures instead of swallowing them with `console.error`.
 * Kept style-inline so it drops into pages that don't already import
 * a shared CSS module. Callers own the error-state lifecycle and
 * should null out the message via `onDismiss`.
 */
interface ErrorBannerProps {
  message: string | null;
  onDismiss: () => void;
}

export function ErrorBanner({ message, onDismiss }: ErrorBannerProps) {
  if (!message) return null;
  return (
    <div
      role="alert"
      style={{
        padding: '8px 16px',
        background: '#fef2f2',
        color: '#991b1b',
        borderBottom: '1px solid #fecaca',
        fontSize: '14px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
      }}
    >
      <span>{message}</span>
      <button
        type="button"
        onClick={onDismiss}
        aria-label="Dismiss error"
        style={{
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          color: 'inherit',
          fontWeight: 'bold',
          fontSize: '18px',
          lineHeight: 1,
        }}
      >
        ×
      </button>
    </div>
  );
}
