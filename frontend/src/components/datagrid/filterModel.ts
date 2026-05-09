/**
 * Shared filter model for admin datagrids. Mirrors the backend's
 * {@link FilterOp} / {@link FilterColumn} enums in
 * `backend/.../service/auctions/reservebid/filter/`. Hosts the type
 * definitions, default ops per column kind, and the kind→ops menu mapping
 * used by the comparator dropdown.
 */

/** All comparator ops the backend understands. Tokens match the wire
 *  format the FilterSpecParser accepts (e.g. `productId=eq,73`). */
export type FilterOp =
  | "eq"
  | "neq"
  | "gt"
  | "gte"
  | "lt"
  | "lte"
  | "empty"
  | "notEmpty"
  | "contains"
  | "startsWith"
  | "endsWith";

export type ColumnKind = "numeric" | "text" | "date";

/** Per-cell state: which op the user picked + the value they typed.
 *  When `op` is in {@link VALUELESS_OPS}, `value` is ignored on the wire. */
export interface ColumnFilter {
  op: FilterOp;
  value: string;
}

export const VALUELESS_OPS = new Set<FilterOp>(["empty", "notEmpty"]);

export function isValueless(op: FilterOp): boolean {
  return VALUELESS_OPS.has(op);
}

/** Full label catalogue for the dropdown menu. */
export const OP_LABEL: Record<FilterOp, string> = {
  eq:         "Equal",
  neq:        "Not equal",
  gt:         "Greater than",
  gte:        "Greater than or equal",
  lt:         "Smaller than",
  lte:        "Smaller than or equal",
  empty:      "Empty",
  notEmpty:   "Not empty",
  contains:   "Contains",
  startsWith: "Starts with",
  endsWith:   "Ends with",
};

/** Glyph rendered as the comparator-trigger label. ASCII / unicode
 *  fallbacks; the icon-font sprint replaces these with SVG components. */
export const OP_GLYPH: Record<FilterOp, string> = {
  eq:         "=",
  neq:        "≠",
  gt:         ">",
  gte:        "≥",
  lt:         "<",
  lte:        "≤",
  empty:      "∅",
  notEmpty:   "⊘",
  contains:   "Ab",
  startsWith: "Ab→",
  endsWith:   "→Ab",
};

/** Default op when a column first renders. Matches QA Mendix DataGrid 2:
 *  numeric / date columns default to Equal, text to Contains. */
export const DEFAULT_OP_FOR_KIND: Record<ColumnKind, FilterOp> = {
  numeric: "eq",
  text:    "contains",
  date:    "eq",
};

/** Full op menu per kind. Pages can pass a narrower {@code availableOps}
 *  prop to restrict the dropdown to a subset (e.g. inventory ships
 *  contains/equals only since its backend hasn't migrated to FilterSpec
 *  yet). */
export const OPS_FOR_KIND: Record<ColumnKind, FilterOp[]> = {
  numeric: ["gt", "gte", "eq", "neq", "lt", "lte", "empty", "notEmpty"],
  text:    ["contains", "startsWith", "endsWith",
            "gt", "gte", "eq", "neq", "lt", "lte", "empty", "notEmpty"],
  date:    ["gt", "gte", "eq", "neq", "lt", "lte", "empty", "notEmpty"],
};

/** Serialise one `{op, value}` pair to the wire format the backend's
 *  FilterSpecParser expects: `op,value` for value-bearing ops, `op` for
 *  valueless. Returns null when the filter is empty (no op set or empty
 *  value on a value-bearing op) so the caller can skip the param. */
export function serializeFilter(filter: ColumnFilter | undefined): string | null {
  if (!filter) return null;
  if (isValueless(filter.op)) return filter.op;
  if (filter.value === "" || filter.value == null) return null;
  return `${filter.op},${filter.value}`;
}
