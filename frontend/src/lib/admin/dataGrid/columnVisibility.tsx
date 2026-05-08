"use client";

/**
 * Reusable column-visibility hook + popover. Extracted from the PO list
 * (gap PO-9) when the same need surfaced on the PO line-items grid.
 *
 * Usage:
 *   const ALL_COLS = ["a", "b", "c"] as const;
 *   const LABELS = { a: "Foo", b: "Bar", c: "Baz" };
 *   const vis = useColumnVisibility(ALL_COLS, LABELS, "myGrid.cols.v1");
 *   ...
 *   <ColumnVisibilityMenu state={vis} />
 *   {vis.visible.has("a") && <td>...</td>}
 *
 * Storage: per-key in localStorage. SSR-safe (initial render = all
 * visible; useEffect upgrades after hydration). Storage failures
 * (private mode etc.) degrade to in-session-only.
 */

import { useEffect, useRef, useState } from "react";

export interface ColumnVisibilityState<K extends string> {
  visible: Set<K>;
  toggle: (col: K) => void;
  cols: readonly K[];
  labels: Record<K, string>;
}

export function useColumnVisibility<K extends string>(
  allCols: readonly K[],
  labels: Record<K, string>,
  storageKey: string,
): ColumnVisibilityState<K> {
  const [visible, setVisible] = useState<Set<K>>(() => new Set(allCols));

  useEffect(() => {
    try {
      const raw = window.localStorage.getItem(storageKey);
      if (!raw) return;
      const parsed = JSON.parse(raw) as string[];
      const valid = parsed.filter((k): k is K => (allCols as readonly string[]).includes(k));
      setVisible(new Set(valid));
    } catch { /* corrupt storage → fall back to all-visible */ }
    // allCols is treated as stable; passing a new array each render
    // would re-run this and clobber the user's saved set, so callers
    // should hoist allCols to a module-level const.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [storageKey]);

  function toggle(col: K) {
    setVisible(prev => {
      const next = new Set(prev);
      if (next.has(col)) next.delete(col);
      else next.add(col);
      try {
        window.localStorage.setItem(storageKey, JSON.stringify([...next]));
      } catch { /* in-session toggle still works */ }
      return next;
    });
  }

  return { visible, toggle, cols: allCols, labels };
}

interface MenuProps<K extends string> {
  state: ColumnVisibilityState<K>;
  /**
   * Optional override for the trigger button label. Default: "Columns".
   */
  buttonLabel?: string;
}

export function ColumnVisibilityMenu<K extends string>({
  state,
  buttonLabel = "Columns",
}: MenuProps<K>) {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    function onDoc(e: MouseEvent) {
      if (!ref.current) return;
      if (!ref.current.contains(e.target as Node)) setOpen(false);
    }
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape") setOpen(false);
    }
    document.addEventListener("mousedown", onDoc);
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("mousedown", onDoc);
      document.removeEventListener("keydown", onKey);
    };
  }, [open]);

  return (
    <div ref={ref} style={{ position: "relative" }}>
      <button
        type="button"
        onClick={() => setOpen(o => !o)}
        aria-haspopup="menu"
        aria-expanded={open}
        title="Show / hide columns"
        style={{
          padding: "0.4rem 0.6rem",
          background: "#F7F7F7",
          color: "#3C3C3C",
          border: "1px solid #D0D0D0",
          borderRadius: 4,
          cursor: "pointer",
          fontSize: 14,
          fontFamily: "inherit",
        }}
      >
        <span aria-hidden="true">👁</span>
        <span style={{ marginLeft: 6 }}>{buttonLabel}</span>
      </button>
      {open && (
        <div
          role="menu"
          style={{
            position: "absolute",
            top: "calc(100% + 4px)",
            right: 0,
            background: "#fff",
            border: "1px solid #D0D0D0",
            borderRadius: 4,
            boxShadow: "0 4px 12px rgba(0,0,0,0.12)",
            padding: "0.5rem 0",
            minWidth: 180,
            zIndex: 50,
          }}
        >
          {state.cols.map(col => (
            <label
              key={col}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "0.5rem",
                padding: "0.35rem 0.75rem",
                cursor: "pointer",
                fontSize: 14,
                color: "#3C3C3C",
              }}
            >
              <input
                type="checkbox"
                checked={state.visible.has(col)}
                onChange={() => state.toggle(col)}
              />
              {state.labels[col]}
            </label>
          ))}
        </div>
      )}
    </div>
  );
}
