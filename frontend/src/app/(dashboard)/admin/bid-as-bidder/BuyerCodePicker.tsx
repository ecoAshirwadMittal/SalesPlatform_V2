'use client';

import { useEffect, useMemo, useRef, useState } from 'react';
import type { BuyerCodeSummary } from '@/lib/admin/buyerCodes';

interface BuyerCodePickerProps {
  codes: BuyerCodeSummary[];
  onSelect: (code: BuyerCodeSummary) => void;
  /** Test hook — pre-populated search value. */
  initialQuery?: string;
}

/**
 * Searchable buyer-code dropdown for the BidAsBidder admin page.
 *
 * Why a custom combobox instead of <select>: the picker must filter on
 * substring across both `code` and `buyerName`, and the QA flow expects a
 * type-as-you-go UX. A native <select> is keyboard-friendly but lacks the
 * filter-as-you-type behaviour, and the existing /buyer-select picker
 * also uses a custom render. ARIA-combobox roles preserve a11y.
 */
export function BuyerCodePicker({ codes, onSelect, initialQuery = '' }: BuyerCodePickerProps) {
  const [query, setQuery] = useState(initialQuery);
  const [open, setOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return codes;
    return codes.filter(
      (c) =>
        c.code.toLowerCase().includes(q) || c.buyerName.toLowerCase().includes(q),
    );
  }, [codes, query]);

  // Close on outside click — keeps the picker behaving like a real combobox.
  useEffect(() => {
    function onDocClick(e: MouseEvent) {
      if (!inputRef.current) return;
      const root = inputRef.current.closest('[data-buyer-code-picker]');
      if (root && !root.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener('mousedown', onDocClick);
    return () => document.removeEventListener('mousedown', onDocClick);
  }, []);

  return (
    <div data-buyer-code-picker style={{ position: 'relative', maxWidth: 480 }}>
      <input
        ref={inputRef}
        type="text"
        role="combobox"
        aria-expanded={open}
        aria-controls="buyer-code-listbox"
        aria-autocomplete="list"
        placeholder="Search by buyer code or company name..."
        value={query}
        onChange={(e) => {
          setQuery(e.target.value);
          setOpen(true);
        }}
        onFocus={() => setOpen(true)}
        style={{
          width: '100%',
          padding: '10px 12px',
          fontSize: 15,
          border: '1px solid #ccc',
          borderRadius: 4,
          fontFamily: "'Brandon Grotesque', 'Open Sans', Arial, sans-serif",
        }}
      />

      {open && (
        <ul
          id="buyer-code-listbox"
          role="listbox"
          aria-label="Buyer codes"
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            margin: 0,
            padding: 0,
            listStyle: 'none',
            background: '#fff',
            border: '1px solid #ccc',
            borderTop: 'none',
            maxHeight: 320,
            overflowY: 'auto',
            zIndex: 10,
            boxShadow: '0 4px 8px rgba(0,0,0,0.08)',
          }}
        >
          {filtered.length === 0 ? (
            <li
              role="option"
              aria-selected={false}
              aria-disabled
              style={{ padding: '10px 12px', color: '#777' }}
            >
              No matching buyer codes
            </li>
          ) : (
            filtered.map((c) => (
              <li
                key={c.id}
                role="option"
                aria-selected={false}
                style={{
                  padding: '10px 12px',
                  cursor: 'pointer',
                  borderTop: '1px solid #eee',
                }}
                onMouseDown={(e) => {
                  // Use mousedown so blur on the input does not race the click.
                  e.preventDefault();
                  setQuery(c.code);
                  setOpen(false);
                  onSelect(c);
                }}
              >
                <strong>{c.code}</strong>
                <span style={{ color: '#666', marginLeft: 8 }}>{c.buyerName}</span>
              </li>
            ))
          )}
        </ul>
      )}
    </div>
  );
}
