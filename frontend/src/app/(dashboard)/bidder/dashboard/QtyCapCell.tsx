'use client';
import { useState } from 'react';

// ---------------------------------------------------------------------------
// QtyCapCell — integer-only input matching Mendix `only-numbers` +
// `textbox-select-all` + `auction-qtycap` + `auction-datagrid-customtext`.
//
// WHY empty ≠ zero: Per the 2026-04-23 ADR, `null` bid_quantity is the
// "no-cap" sentinel (accept any qty up to maximumQuantity). An empty input
// maps to null; `0` maps to a zero-unit bid (valid, unusual). These are
// distinct states and must stay distinct.
//
// WHY call onSave on every change: The parent hook (useAutoSaveBid) owns
// the 500ms debounce — calling onSave eagerly lets the hook coalesce rapid
// keystrokes into a single PUT, matching the Mendix auto-save behavior.
// ---------------------------------------------------------------------------

export interface QtyCapCellProps {
  rowId: number;
  initialValue: number | null;
  onSave: (rowId: number, value: number | null) => void;
  disabled?: boolean;
}

function parseQty(raw: string): number | null {
  if (raw === '') return null;
  const n = parseInt(raw, 10);
  return isNaN(n) ? null : n;
}

export function QtyCapCell({ rowId, initialValue, onSave, disabled = false }: QtyCapCellProps) {
  // Empty string = no-cap sentinel (null on save); any digit string = qty.
  const [display, setDisplay] = useState<string>(
    initialValue === null ? '' : String(initialValue),
  );

  const handleFocus = (e: React.FocusEvent<HTMLInputElement>): void => {
    // Select-all on focus so the user's first keystroke replaces the value.
    requestAnimationFrame(() => {
      e.target.select();
    });
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>): void => {
    // Allow: digits, Backspace, Delete, ArrowLeft, ArrowRight, Tab, Home, End.
    const allowed = /^\d$/.test(e.key) ||
      ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End', 'Enter'].includes(e.key);
    if (!allowed) {
      e.preventDefault();
      return;
    }
    if (e.key === 'Enter') {
      e.currentTarget.blur();
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const raw = e.target.value;
    // Extra guard: strip anything that is not a digit (handles paste).
    const digitsOnly = raw.replace(/\D/g, '');
    setDisplay(digitsOnly);
    // Fire onSave on every keystroke so the parent hook can debounce.
    onSave(rowId, parseQty(digitsOnly));
  };

  const handleBlur = (): void => {
    // Fire immediately on blur to flush any pending debounce.
    onSave(rowId, parseQty(display));
  };

  return (
    <input
      type="text"
      inputMode="numeric"
      // Mendix parity class names — kept verbatim for CSS grep / QA matching.
      className="auction-qtycap only-numbers textbox-select-all auction-datagrid-customtext"
      aria-label={`Qty Cap for row ${rowId}`}
      value={display}
      onChange={handleChange}
      onFocus={handleFocus}
      onKeyDown={handleKeyDown}
      onBlur={handleBlur}
      disabled={disabled}
    />
  );
}
