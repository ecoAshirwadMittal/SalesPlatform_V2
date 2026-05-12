'use client';

import styles from '../detail.module.css';

export type ReasonTabKey = 'MISSING' | 'WRONG' | 'ENCUMBERED';

interface TabSpec {
  key: ReasonTabKey;
  label: string;
  count: number;
}

interface MultiReasonTabsProps {
  active: ReasonTabKey;
  onChange: (next: ReasonTabKey) => void;
  tabs: readonly TabSpec[];
}

/**
 * Figma component `Tabs - Partial Credit` (`651:2878`). Renders one tab
 * per active reason on a multi-reason credit request. Page-level state
 * owns the active tab so the visible section + the per-tab toggle stay
 * in sync.
 *
 * Real `[role="tablist"]` + `role="tab"` + `aria-selected` pattern so
 * the control is accessible to screen readers and keyboard users (arrow
 * keys aren't wired here because the parent renders one section at a
 * time — Phase 2 can add full APG roving-tab-index if needed).
 */
export function MultiReasonTabs({ active, onChange, tabs }: MultiReasonTabsProps) {
  return (
    <div role="tablist" aria-label="Reason sections" className={styles.reasonTabs}>
      {tabs.map((tab) => {
        const selected = tab.key === active;
        return (
          <button
            key={tab.key}
            type="button"
            role="tab"
            id={`reason-tab-${tab.key.toLowerCase()}`}
            aria-selected={selected}
            aria-controls={`reason-panel-${tab.key.toLowerCase()}`}
            tabIndex={selected ? 0 : -1}
            className={selected ? styles.reasonTabActive : styles.reasonTab}
            onClick={() => onChange(tab.key)}
          >
            {tab.label} ({tab.count})
          </button>
        );
      })}
    </div>
  );
}
