"use client";

import { useEffect, useId, useRef, useState } from "react";
import {
  type FilterOp,
  OP_GLYPH,
  OP_LABEL,
} from "./filterModel";
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
  const wrapRef = useRef<HTMLDivElement>(null);
  const menuId = useId();

  useEffect(() => {
    if (!open) return;
    const onClickOutside = (e: MouseEvent) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target as Node)) setOpen(false);
    };
    const onKey = (e: KeyboardEvent) => { if (e.key === "Escape") setOpen(false); };
    window.addEventListener("mousedown", onClickOutside);
    window.addEventListener("keydown", onKey);
    return () => {
      window.removeEventListener("mousedown", onClickOutside);
      window.removeEventListener("keydown", onKey);
    };
  }, [open]);

  return (
    <div className={styles.comparatorWrap} ref={wrapRef}>
      <button
        type="button"
        className={styles.comparatorButton}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={open ? menuId : undefined}
        aria-label={ariaLabel}
        onClick={() => setOpen((v) => !v)}
      >
        {OP_GLYPH[selected]}
      </button>
      {open && (
        <ul
          id={menuId}
          className={styles.menu}
          role="listbox"
          aria-label={ariaLabel}
        >
          {available.map((op) => (
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
              <span className={styles.menuGlyph}>{OP_GLYPH[op]}</span>
              <span className={styles.menuLabel}>{OP_LABEL[op]}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
