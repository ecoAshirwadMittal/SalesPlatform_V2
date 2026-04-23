'use client';

interface CarryoverButtonProps {
  onClick: () => void;
  disabled?: boolean;
}

/**
 * Carryover pill — copies last week's bids forward. Phase 9 wires the real
 * endpoint; Phase 5 ships the button wiring only.
 */
export function CarryoverButton({ onClick, disabled = false }: CarryoverButtonProps) {
  return (
    <button
      type="button"
      className="btn-carryover"
      onClick={onClick}
      disabled={disabled}
    >
      Carryover
    </button>
  );
}
