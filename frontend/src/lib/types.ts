/**
 * Shared TypeScript types. Keep this file thin — only put shapes here
 * that are actually reused across pages. One-off page-local shapes
 * should stay in the page file.
 */

import type { ReactNode } from 'react';

/**
 * Spring `Page<T>` envelope as serialized by the backend. Used by
 * every paginated list endpoint under `/api/v1`.
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface SubNavItem {
  label: string;
  href: string;
}

/**
 * Union shape used by both `(dashboard)/layout.tsx` and
 * `pws/layout.tsx`. PWS nav items are flat; dashboard items may
 * be expandable with sub-items — the optional fields cover both.
 */
export interface NavItem {
  label: string;
  href: string;
  icon: ReactNode;
  expandable?: boolean;
  children?: SubNavItem[];
}
