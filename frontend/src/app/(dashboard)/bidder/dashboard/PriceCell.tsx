'use client';
import { useState } from 'react';

// ---------------------------------------------------------------------------
// PriceCell — dollar-formatted text input matching Mendix `text-dollar` +
// `textbox-select-all` + `auction-price` + `auction-datagrid-customtext`
// behavior.
//
// WHY text, not number: Mendix renders type=text for dollar fields so that
// the `$` prefix can be shown without browser number-input interference.
//
// WHY call onSave on every change: The parent hook (useAutoSaveBid) owns
// the 500ms debounce — calling onSave eagerly lets the hook coalesce rapid
// keystrokes into a single PUT, matching the Mendix auto-save behavior.
// ---------------------------------------------------------------------------

export interface PriceCellProps {
  rowId: number;
  initialValue: number;
  onSave: (rowId: number, value: number) => void;
  disabled?: boolean;
}

function formatDollar(value: number): string {
  return `$ ${value.toFixed(2)}`;
}

function parseDollar(raw: string): number {
  // Strip `$`, commas, and whitespace; fall back to 0 for unparseable input.
  const cleaned = raw.replace(/[$,\s]/g, '');
  const parsed = parseFloat(cleaned);
  if (isNaN(parsed)) return 0;
  // Clamp negative values to 0 per the ADR (bidAmount must be >= 0).
  return Math.max(0, parsed);
}

export function PriceCell({ rowId, initialValue, onSave, disabled = false }: PriceCellProps) {
  // Display state owns the string the user sees while editing.
  // When not focused, it holds the formatted "$  X.XX" string.
  const [display, setDisplay] = useState<string>(formatDollar(initialValue));
  const [editing, setEditing] = useState<boolean>(false);

  const handleFocus = (e: React.FocusEvent<HTMLInputElement>): void => {
    setEditing(true);
    // On focus: strip `$` so the user edits just the numeric portion.
    const numericOnly = display.replace(/[$\s]/g, '');
    setDisplay(numericOnly);
    // Select-all after React's state flush.
    requestAnimationFrame(() => {
      e.target.select();
    });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const raw = e.target.value;
    // Allow digits, `.`, `-` (negative gets clamped on save/blur).
    // Allow empty string (user cleared the field).
    if (raw !== '' && !/^-?[\d.]*$/.test(raw)) return;
    setDisplay(raw);
    // Fire onSave on every keystroke so the parent hook can debounce.
    onSave(rowId, parseDollar(raw));
  };

  const handleBlur = (): void => {
    setEditing(false);
    const numeric = parseDollar(display);
    setDisplay(formatDollar(numeric));
    // Final authoritative save on blur.
    onSave(rowId, numeric);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>): void => {
    if (e.key === 'Enter') {
      e.currentTarget.blur();
    }
  };

  return (
    <input
      type="text"
      // Mendix parity class names — kept verbatim for CSS grep / QA matching.
      className="auction-price textbox-select-all text-dollar auction-datagrid-customtext"
      aria-label={`Price for row ${rowId}`}
      value={editing ? display : display}
      onChange={handleChange}
      onFocus={handleFocus}
      onBlur={handleBlur}
      onKeyDown={handleKeyDown}
      disabled={disabled}
    />
  );
}
