"use client";

import ComparatorMenu from "./ComparatorMenu";
import DatePopoverInput from "./DatePopoverInput";
import {
  type ColumnFilter,
  type ColumnKind,
  type FilterOp,
  OPS_FOR_KIND,
  isValueless,
} from "./filterModel";
import styles from "./datagrid.module.css";

interface Props {
  /** User-facing column label, used to build aria-label fallbacks. */
  label: string;
  kind: ColumnKind;
  /** Current filter state for the column. */
  filter: ColumnFilter;
  onChange: (next: ColumnFilter) => void;
  /** Override the default op menu. Use for pages whose backend supports
   *  a narrower set than {@link OPS_FOR_KIND}. */
  availableOps?: FilterOp[];
  /** Override the value input type (defaults: numeric=number,
   *  text=text, date=date). */
  inputType?: "text" | "number" | "date";
  /** HTML inputmode hint passed to the input (defaults align with kind). */
  inputMode?: "text" | "numeric" | "decimal" | "none";
  /** Placeholder shown in the value input. */
  placeholder?: string;
}

/**
 * Combined filter cell — comparator dropdown + value input — used inside
 * a column header. The host owns state; this component is purely
 * presentational and forwards changes upward.
 */
export default function FilterCell({
  label,
  kind,
  filter,
  onChange,
  availableOps,
  inputType,
  inputMode,
  placeholder,
}: Props) {
  const ops = availableOps ?? OPS_FOR_KIND[kind];
  const valueless = isValueless(filter.op);

  const resolvedInputType: "text" | "number" | "date" =
    inputType ?? (kind === "numeric" ? "number" : kind === "date" ? "date" : "text");
  const resolvedInputMode: Props["inputMode"] =
    inputMode ?? (kind === "numeric" ? "decimal" : "text");

  // Date columns (with a value-bearing op) get the custom calendar
  // popover so the visual matches QA's Mendix DataGrid 2 calendar
  // trigger. Valueless ops (Empty / Not empty) still hide the input.
  const useDatePopover = kind === "date" && !valueless;

  return (
    <div className={styles.filterCell}>
      <ComparatorMenu
        selected={filter.op}
        available={ops}
        onChange={(op) => onChange({ op, value: isValueless(op) ? "" : filter.value })}
        ariaLabel={`${label} comparator`}
      />
      {useDatePopover ? (
        <DatePopoverInput
          value={filter.value}
          onChange={(next) => onChange({ op: filter.op, value: next })}
          ariaLabel={`Filter ${label}`}
          placeholder={placeholder ?? "mm/dd/yyyy"}
        />
      ) : (
        <input
          className={`${styles.filterInput} ${valueless ? styles.filterInputDisabled : ""}`}
          type={resolvedInputType}
          inputMode={valueless ? "none" : resolvedInputMode}
          value={valueless ? "" : filter.value}
          disabled={valueless}
          placeholder={valueless ? "" : (placeholder ?? label)}
          aria-label={`Filter ${label}`}
          onChange={(e) => onChange({ op: filter.op, value: e.target.value })}
        />
      )}
    </div>
  );
}
