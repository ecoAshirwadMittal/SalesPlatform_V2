"use client";

import { useEffect, useState, type CSSProperties, type ReactNode, type RefObject } from "react";
import { createPortal } from "react-dom";

interface Props {
  /** Element the popover anchors to (typically the trigger button). */
  anchorRef: RefObject<HTMLElement | null>;
  open: boolean;
  /** Which edge of the anchor the popover left/right snaps to. */
  align?: "left" | "right";
  /** Pixels of vertical gap between anchor and popover. */
  offset?: number;
  /** z-index for stacking above modals. Default 1000. */
  zIndex?: number;
  className?: string;
  children: ReactNode;
}

/**
 * Portals its children to `document.body` and pins them to the
 * anchor's bounding rect via {@code position: fixed}. Solves the
 * "dropdown gets clipped by ancestor overflow: hidden" issue that
 * datagrid wrappers hit when the table is short (no rows / "No
 * matches" state) and the menu would extend past the table's box.
 *
 * Position recomputes on window resize + scroll so the popover stays
 * glued to the trigger as the user pans the page. The host is still
 * responsible for click-outside / Esc handling — pass refs to both
 * the anchor and the popover-rendered menu element to its
 * pointerdown listener so clicks inside the menu don't dismiss it.
 */
export default function Popover({
  anchorRef,
  open,
  align = "left",
  offset = 2,
  zIndex = 1000,
  className,
  children,
}: Props) {
  const [rect, setRect] = useState<DOMRect | null>(null);

  useEffect(() => {
    if (!open) {
      setRect(null);
      return;
    }
    const update = () => {
      const r = anchorRef.current?.getBoundingClientRect();
      setRect(r ?? null);
    };
    update();
    window.addEventListener("resize", update);
    // capture: catch scroll on any ancestor (the popover lives in
    // document.body and would otherwise miss inner scroll containers).
    window.addEventListener("scroll", update, true);
    return () => {
      window.removeEventListener("resize", update);
      window.removeEventListener("scroll", update, true);
    };
  }, [open, anchorRef]);

  if (!open || !rect) return null;

  // SSR safety — react-dom's createPortal requires a real DOM target.
  if (typeof document === "undefined") return null;

  const style: CSSProperties = {
    position: "fixed",
    top: rect.bottom + offset,
    zIndex,
  };
  if (align === "right") {
    style.right = document.documentElement.clientWidth - rect.right;
  } else {
    style.left = rect.left;
  }

  return createPortal(
    <div className={className} style={style}>{children}</div>,
    document.body,
  );
}
