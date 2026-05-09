import type { ComponentType, SVGProps } from "react";
import type { FilterOp } from "../filterModel";

/**
 * License-clean glyph reproductions for the admin-grid comparator menu.
 *
 * Authored to mirror Mendix's `datagrid-filters` icon font (the visual
 * signature of every QA grid) without redistributing the proprietary font
 * itself. Each SVG is drawn on a 24×24 grid with 1.6 px stroke weight and
 * uses {@code currentColor} so the host's text colour drives the glyph
 * tint. Default render size is 14 × 14, suitable for both the
 * comparator-trigger button (38 × 28) and the menu-item leading slot
 * (24-wide); host can override via `width`/`height` props.
 *
 * QA reference captures:
 * - `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-03-bid-comparator-menu.png`
 * - `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-04-grade-comparator-menu.png`
 *
 * Spec: `docs/tasks/datagrid-filters-icon-font-plan.md` §2 (glyph
 * inventory + Option A justification).
 */

type IconProps = SVGProps<SVGSVGElement>;

const baseProps = {
  viewBox: "0 0 24 24",
  width: 14,
  height: 14,
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.6,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
  "aria-hidden": true,
};

// ── Math comparator glyphs (8 ops) ──────────────────────────────

export function GreaterThanIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M9 6 L16 12 L9 18" />
    </svg>
  );
}

export function GreaterThanOrEqualIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M9 4 L16 10 L9 16" />
      <path d="M7 20 H17" />
    </svg>
  );
}

export function EqualIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M5 9 H19" />
      <path d="M5 15 H19" />
    </svg>
  );
}

export function NotEqualIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M5 9 H19" />
      <path d="M5 15 H19" />
      <path d="M17 5 L7 19" />
    </svg>
  );
}

export function SmallerThanIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M15 6 L8 12 L15 18" />
    </svg>
  );
}

export function SmallerThanOrEqualIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M15 4 L8 10 L15 16" />
      <path d="M7 20 H17" />
    </svg>
  );
}

/**
 * Empty — Mendix renders this as an `=` glyph with an overbar, signalling
 * "the equal-to-empty case". We mirror that with three horizontal lines
 * rather than the Unicode `∅` (which reads as a different concept in
 * non-Mendix UI traditions).
 */
export function EmptyIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M5 6 H19" />
      <path d="M5 12 H19" />
      <path d="M5 18 H19" />
    </svg>
  );
}

/** Not empty — the Empty bar+equal motif crossed out with a slash. */
export function NotEmptyIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M5 6 H19" />
      <path d="M5 12 H19" />
      <path d="M5 18 H19" />
      <path d="M18 4 L6 20" />
    </svg>
  );
}

// ── Text comparator glyphs (3 ops) ──────────────────────────────
//
// The "Ab" letterforms render via SVG `<text>` so they inherit the host
// font stack. Helvetica / Arial fallbacks are deliberate — Brandon
// Grotesque has no italic / hairline weight; the system sans-serif
// reads cleaner at 9 px than a ligature would.

const TEXT_STYLE = {
  fontFamily: "Helvetica, Arial, sans-serif",
  fontSize: 11,
  fontWeight: 600,
  fill: "currentColor",
  stroke: "none",
} as const;

export function ContainsIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <text x="12" y="16" textAnchor="middle" {...TEXT_STYLE}>Ab</text>
    </svg>
  );
}

/** Starts with — a vertical anchor bar to the LEFT of "Ab", indicating
 *  the match is anchored at the start of the string. */
export function StartsWithIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <path d="M5 5 V19" />
      <text x="14" y="16" textAnchor="middle" {...TEXT_STYLE}>Ab</text>
    </svg>
  );
}

/** Ends with — anchor bar to the RIGHT. */
export function EndsWithIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <text x="10" y="16" textAnchor="middle" {...TEXT_STYLE}>Ab</text>
      <path d="M19 5 V19" />
    </svg>
  );
}

// ── Calendar (date-comparator trigger) ──────────────────────────

export function CalendarIcon(props: IconProps) {
  return (
    <svg {...baseProps} {...props}>
      <rect x="3.5" y="5" width="17" height="15" rx="1.5" />
      <path d="M3.5 10 H20.5" />
      <path d="M8 3 V7" />
      <path d="M16 3 V7" />
    </svg>
  );
}

// ── Op → component map ──────────────────────────────────────────

export const COMPARATOR_ICON: Record<FilterOp, ComponentType<IconProps>> = {
  eq:         EqualIcon,
  neq:        NotEqualIcon,
  gt:         GreaterThanIcon,
  gte:        GreaterThanOrEqualIcon,
  lt:         SmallerThanIcon,
  lte:        SmallerThanOrEqualIcon,
  empty:      EmptyIcon,
  notEmpty:   NotEmptyIcon,
  contains:   ContainsIcon,
  startsWith: StartsWithIcon,
  endsWith:   EndsWithIcon,
};
