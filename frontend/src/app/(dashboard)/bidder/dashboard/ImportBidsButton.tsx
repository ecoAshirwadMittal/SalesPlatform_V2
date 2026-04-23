'use client';

interface ImportBidsButtonProps {
  onClick: () => void;
  disabled?: boolean;
}

/**
 * Import button — opens the Import Your Bids modal. Phase 10 wires the
 * backend endpoint + modal body; Phase 5 ships the visual + click wiring only.
 */
export function ImportBidsButton({ onClick, disabled = false }: ImportBidsButtonProps) {
  return (
    <button
      type="button"
      className="btn-outline"
      onClick={onClick}
      disabled={disabled}
      aria-label="Import bids from xlsx"
    >
      <span aria-hidden="true">↑</span>
      <span>Import</span>
    </button>
  );
}
