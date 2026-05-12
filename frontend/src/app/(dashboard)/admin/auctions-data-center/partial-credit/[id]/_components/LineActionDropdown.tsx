'use client';

import { useCallback, useEffect, useId, useRef, useState } from 'react';
import type { ReviewDecision } from '@/lib/adminPartialCreditClient';
import styles from '../../admin.module.css';

interface LineActionDropdownProps {
  /** Current persisted decision. Null is rendered as PENDING. */
  value: ReviewDecision | null;
  onChange: (decision: ReviewDecision) => void;
  disabled?: boolean;
}

interface Option {
  value: ReviewDecision;
  label: string;
  icon: 'thumbs-up' | 'thumbs-down' | null;
}

const OPTIONS: ReadonlyArray<Option> = [
  { value: 'PENDING', label: 'Select', icon: null },
  { value: 'ACCEPTED', label: 'Accept', icon: 'thumbs-up' },
  { value: 'DECLINED', label: 'Decline', icon: 'thumbs-down' },
];

/**
 * Per-row Accept / Decline / Pending dropdown.
 *
 * Per Figma §6.3 of the Sprint-3 design notes, the dropdown shows a
 * green thumbs-up icon for Accept (#1F8B3D) and a red thumbs-down icon
 * for Decline (#B3261E) alongside the verb. The native `<select>` can't
 * render icons, so this is a custom dropdown with explicit keyboard
 * a11y: arrow keys move focus, Enter / Space selects, Escape closes,
 * Tab dismisses.
 *
 * The verb in the design is `Accept` (singular), not `Approve` — the
 * latter is reserved for the per-section bulk button per §4.3. The
 * backend treats both verbs as the same ACCEPTED enum value.
 */
export function LineActionDropdown({ value, onChange, disabled }: LineActionDropdownProps) {
  const [open, setOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState<number>(() =>
    Math.max(0, OPTIONS.findIndex((o) => o.value === (value ?? 'PENDING'))),
  );
  const rootRef = useRef<HTMLDivElement>(null);
  const listboxId = useId();

  const selected = OPTIONS.find((o) => o.value === (value ?? 'PENDING')) ?? OPTIONS[0];

  // Close when focus / pointer leaves the dropdown.
  useEffect(() => {
    if (!open) return;
    const handlePointerDown = (e: MouseEvent | TouchEvent): void => {
      if (rootRef.current && !rootRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handlePointerDown);
    document.addEventListener('touchstart', handlePointerDown);
    return () => {
      document.removeEventListener('mousedown', handlePointerDown);
      document.removeEventListener('touchstart', handlePointerDown);
    };
  }, [open]);

  const commit = useCallback(
    (next: ReviewDecision) => {
      // The PENDING "Select" placeholder is non-committal — clicking it
      // simply closes the menu without calling onChange.
      if (next !== 'PENDING' && next !== value) {
        onChange(next);
      }
      setOpen(false);
    },
    [onChange, value],
  );

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>): void => {
    if (disabled) return;
    if (!open) {
      if (e.key === 'ArrowDown' || e.key === 'ArrowUp' || e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        setOpen(true);
      }
      return;
    }
    if (e.key === 'Escape') {
      e.preventDefault();
      setOpen(false);
      return;
    }
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setActiveIndex((i) => Math.min(OPTIONS.length - 1, i + 1));
      return;
    }
    if (e.key === 'ArrowUp') {
      e.preventDefault();
      setActiveIndex((i) => Math.max(0, i - 1));
      return;
    }
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      const option = OPTIONS[activeIndex];
      if (option) commit(option.value);
      return;
    }
    if (e.key === 'Tab') {
      setOpen(false);
    }
  };

  return (
    <div className={styles.lineActionRoot} ref={rootRef} onKeyDown={handleKeyDown}>
      <button
        type="button"
        className={styles.lineActionTrigger}
        disabled={disabled}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={listboxId}
        onClick={() => setOpen((o) => !o)}
      >
        <span className={styles.lineActionLabel}>
          <DecisionIcon icon={selected.icon} />
          <span>{selected.label}</span>
        </span>
        <ChevronDown />
      </button>
      {open && (
        <ul
          id={listboxId}
          role="listbox"
          className={styles.lineActionMenu}
          tabIndex={-1}
        >
          {OPTIONS.map((option, idx) => {
            const isSelected = option.value === selected.value;
            const isActive = idx === activeIndex;
            return (
              <li
                key={option.value}
                role="option"
                aria-selected={isSelected}
                className={`${styles.lineActionOption} ${
                  isActive ? styles.lineActionOptionActive : ''
                }`}
                onMouseEnter={() => setActiveIndex(idx)}
                onMouseDown={(e) => {
                  // Prevent the trigger from losing focus before the
                  // click handler runs.
                  e.preventDefault();
                }}
                onClick={() => commit(option.value)}
              >
                <DecisionIcon icon={option.icon} />
                <span>{option.label}</span>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}

interface DecisionIconProps {
  icon: 'thumbs-up' | 'thumbs-down' | null;
}

function DecisionIcon({ icon }: DecisionIconProps): React.ReactNode {
  if (icon === 'thumbs-up') {
    return (
      <svg
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="none"
        stroke="#1F8B3D"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <path d="M7 10v12" />
        <path d="M15 5.88 14 10h5.83a2 2 0 0 1 1.92 2.56l-2.33 8A2 2 0 0 1 17.5 22H7V10l4.36-8.36a1.5 1.5 0 0 1 2.6 1.5L13 6.5" />
      </svg>
    );
  }
  if (icon === 'thumbs-down') {
    return (
      <svg
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="none"
        stroke="#B3261E"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <path d="M17 14V2" />
        <path d="M9 18.12 10 14H4.17a2 2 0 0 1-1.92-2.56l2.33-8A2 2 0 0 1 6.5 2H17v12l-4.36 8.36a1.5 1.5 0 0 1-2.6-1.5L11 17.5" />
      </svg>
    );
  }
  return null;
}

function ChevronDown(): React.ReactNode {
  return (
    <svg
      width="12"
      height="12"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <polyline points="6 9 12 15 18 9" />
    </svg>
  );
}
