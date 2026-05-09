"use client";

import { useEffect, useId, useRef, useState } from "react";
import {
  type FilterOp,
  OP_LABEL,
} from "./filterModel";
import { COMPARATOR_ICON } from "./icons";
import Popover from "./Popover";
import styles from "./datagrid.module.css";

interface Props {
  selected: FilterOp;
  available: FilterOp[];
  onChange: (op: FilterOp) => void;
  /** Aria label for the trigger button. Shape: "Bid comparator". */
  ariaLabel: string;
}

/**
 * Comparator dropdown anchored next to a filter input. Mendix-style: the
 * trigger renders the current op's glyph; clicking opens a small listbox
 * of legal ops for the column. Keyboard / outside-click / Esc close.
 *
 * The listbox is rendered via {@link Popover} so it portals into
 * {@code document.body} and escapes ancestor {@code overflow: hidden}
 * (datagrid wrappers clip on the no-rows / "No matches" state).
 *
 * Selecting an op calls onChange immediately and closes the menu — same
 * commit shape as a `<select>`. The host owns op state.
 */
export default function ComparatorMenu({
  selected,
  available,
  onChange,
  ariaLabel,
}: Props) {
  const [open, setOpen] = useState(false);
  const triggerRef = useRef<HTMLButtonElement>(null);
  const menuRef = useRef<HTMLUListElement>(null);
  const menuId = useId();

  useEffect(() => {
    if (!open) return;
    // The menu lives in document.body via the portal — the trigger
    // wrapper alone can't gate click-outside any more. Check both the
    // trigger button and the menu element for inclusion.
    const onPointerDown = (e: PointerEvent) => {
      const target = e.target as Node;
      if (triggerRef.current?.contains(target)) return;
      if (menuRef.current?.contains(target)) return;
      setOpen(false);
    };
    const onKey = (e: KeyboardEvent) => { if (e.key === "Escape") setOpen(false); };
    window.addEventListener("pointerdown", onPointerDown);
    window.addEventListener("keydown", onKey);
    return () => {
      window.removeEventListener("pointerdown", onPointerDown);
      window.removeEventListener("keydown", onKey);
    };
  }, [open]);

  const SelectedIcon = COMPARATOR_ICON[selected];
  return (
    <div className={styles.comparatorWrap}>
      <button
        ref={triggerRef}
        type="button"
        className={styles.comparatorButton}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={open ? menuId : undefined}
        aria-label={ariaLabel}
        onClick={() => setOpen((v) => !v)}
      >
        <SelectedIcon />
      </button>
      <Popover anchorRef={triggerRef} open={open}>
        <ul
          ref={menuRef}
          id={menuId}
          className={styles.menu}
          role="listbox"
          aria-label={ariaLabel}
        >
          {available.map((op) => {
            const Icon = COMPARATOR_ICON[op];
            return (
              <li
                key={op}
                role="option"
                aria-selected={op === selected}
                className={`${styles.menuItem} ${op === selected ? styles.menuItemSelected : ""}`}
                onClick={() => {
                  onChange(op);
                  setOpen(false);
                }}
              >
                <span className={styles.menuGlyph}><Icon /></span>
                <span className={styles.menuLabel}>{OP_LABEL[op]}</span>
              </li>
            );
          })}
        </ul>
      </Popover>
    </div>
  );
}
