'use client';

/**
 * SidebarToggle — chevron button that sits at the top edge of the sidebar.
 *
 * Props are explicit (collapsed / onToggle) so the component can be used
 * independently of SidebarContext in tests. The auction/PWS shell layouts
 * will typically wire it via the useSidebar() hook in Phase 4.
 *
 * Chevron points right when collapsed (→ expand), left when expanded (← collapse).
 * QA reference: qa-07-sidebar-collapsed.png — ~28px tall button, minimal chrome.
 */

import styles from './chrome.module.css';

interface SidebarToggleProps {
  collapsed: boolean;
  onToggle: () => void;
}

export default function SidebarToggle({ collapsed, onToggle }: SidebarToggleProps) {
  const label = collapsed ? 'Expand sidebar' : 'Collapse sidebar';

  return (
    <button
      type="button"
      className={styles.sidebarToggle}
      onClick={onToggle}
      aria-label={label}
      aria-expanded={!collapsed}
    >
      {/* Chevron SVG — rotated via CSS when expanded */}
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        width="16"
        height="16"
        fill="none"
        stroke="currentColor"
        strokeWidth="2.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
        focusable="false"
        className={collapsed ? styles.chevronRight : styles.chevronLeft}
      >
        {collapsed ? (
          // Right-pointing chevron
          <polyline points="9 18 15 12 9 6" />
        ) : (
          // Left-pointing chevron
          <polyline points="15 18 9 12 15 6" />
        )}
      </svg>
    </button>
  );
}
