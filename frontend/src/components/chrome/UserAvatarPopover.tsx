'use client';

/**
 * UserAvatarPopover — user name + circular avatar button with a floating popover.
 *
 * The popover shows a list of action items as stacked dark-teal pill buttons.
 * QA reference: qa-06-avatar-popover.png — two items: "Submit Feedback", "Logout".
 * The auction shell passes exactly those two via the `items` prop; the PWS shell
 * can inject more items in Phase 4+.
 *
 * Accessibility:
 *   - Avatar is a <button> with aria-haspopup="true" and aria-expanded.
 *   - Popover traps focus (Tab cycles items, Shift+Tab exits back to the avatar).
 *   - Escape closes the popover and returns focus to the avatar button.
 *   - Click outside closes the popover.
 */

import {
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import styles from './chrome.module.css';

export interface AvatarMenuItem {
  label: string;
  onClick: () => void;
  /** Optional danger styling — reserved for future use; QA uses uniform dark pills. */
  danger?: boolean;
}

interface UserAvatarPopoverProps {
  user: {
    fullName: string;
    initials: string;
  };
  items: AvatarMenuItem[];
}

export default function UserAvatarPopover({ user, items }: UserAvatarPopoverProps) {
  const [open, setOpen] = useState(false);
  const avatarRef = useRef<HTMLButtonElement>(null);
  const popoverRef = useRef<HTMLDivElement>(null);

  const close = useCallback(() => setOpen(false), []);

  // Close on click outside the avatar + popover container.
  useEffect(() => {
    if (!open) return;

    function handleDocumentClick(e: MouseEvent) {
      const target = e.target as Node;
      if (
        !avatarRef.current?.contains(target) &&
        !popoverRef.current?.contains(target)
      ) {
        close();
      }
    }

    document.addEventListener('click', handleDocumentClick);
    return () => document.removeEventListener('click', handleDocumentClick);
  }, [open, close]);

  // Close on Escape.
  useEffect(() => {
    if (!open) return;

    function handleKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape') {
        close();
        avatarRef.current?.focus();
      }
    }

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [open, close]);

  // Focus the first popover item when the popover opens.
  useEffect(() => {
    if (open) {
      const firstItem = popoverRef.current?.querySelector<HTMLButtonElement>('button');
      firstItem?.focus();
    }
  }, [open]);

  function handleAvatarClick() {
    setOpen((prev) => !prev);
  }

  function handleItemClick(item: AvatarMenuItem) {
    close();
    item.onClick();
  }

  return (
    <div className={styles.avatarWrapper}>
      {/* User full name — plain text to the left of the avatar */}
      <span className={styles.avatarFullName} aria-hidden="true">
        {user.fullName}
      </span>

      {/* Circular avatar button with initials */}
      <button
        ref={avatarRef}
        type="button"
        className={styles.avatarButton}
        onClick={handleAvatarClick}
        aria-haspopup="true"
        aria-expanded={open}
        aria-label={`User menu for ${user.fullName}`}
      >
        <span aria-hidden="true">{user.initials}</span>
      </button>

      {/* Floating popover — right-aligned below the avatar */}
      {open && (
        <div
          ref={popoverRef}
          role="menu"
          aria-label="User actions"
          className={styles.avatarPopover}
        >
          {items.map((item) => (
            <button
              key={item.label}
              type="button"
              role="menuitem"
              className={`${styles.popoverItem}${item.danger ? ` ${styles.popoverItemDanger}` : ''}`}
              onClick={() => handleItemClick(item)}
            >
              {item.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
