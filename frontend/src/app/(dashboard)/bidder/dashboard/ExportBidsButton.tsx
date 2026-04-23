'use client';

interface ExportBidsButtonProps {
  onClick: () => void;
  disabled?: boolean;
}

/**
 * Export button — downloads the current bid slice as .xlsx. Phase 10 wires
 * the backend endpoint; Phase 5 ships the visual + click wiring only.
 */
export function ExportBidsButton({ onClick, disabled = false }: ExportBidsButtonProps) {
  return (
    <button
      type="button"
      className="btn-outline"
      onClick={onClick}
      disabled={disabled}
      aria-label="Export bids as xlsx"
    >
      <span aria-hidden="true">↓</span>
      <span>Export</span>
    </button>
  );
}
