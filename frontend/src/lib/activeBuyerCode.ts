/**
 * Active buyer-code helpers — single source of truth for reading, writing,
 * and clearing the buyer code that is currently selected by the user.
 *
 * Storage contract (Phase 2 → Phase 3+):
 *   - Primary:  localStorage['activeBuyerCode']  (canonical, used by useActiveBuyerCode hook)
 *   - Compat:   sessionStorage['selectedBuyerCode'] (legacy PWS pages still read this key;
 *               will be removed when all PWS pages migrate to the URL-param pattern in Phase 4+)
 *
 * Routing contract:
 *   - AUCTION codes → /bidder/dashboard?buyerCodeId={id}
 *   - PWS codes     → /pws/order?buyerCodeId={id}
 *
 * Extracted from buyer-select/page.tsx (Phase 3) so the useActiveBuyerCode hook
 * and any other consumer can share the same logic without duplicating it.
 */

export interface ActiveBuyerCode {
  id: number;
  code: string;
  buyerName: string;
  buyerCodeType: string;  // raw Mendix enum value (e.g. "Wholesale", "DataWipe")
  codeType: 'AUCTION' | 'PWS';
}

const STORAGE_KEY = 'activeBuyerCode';
const COMPAT_KEY = 'selectedBuyerCode';  // legacy sessionStorage key still read by some PWS pages

/**
 * Write the active buyer code to both storage locations.
 * Silently no-ops on the server (SSR context).
 */
export function setActiveBuyerCode(code: ActiveBuyerCode): void {
  if (typeof window === 'undefined') return;
  const serialized = JSON.stringify(code);
  localStorage.setItem(STORAGE_KEY, serialized);
  // Maintain legacy compat key until all PWS pages migrate to URL params.
  sessionStorage.setItem(COMPAT_KEY, serialized);
}

/**
 * Read the active buyer code from localStorage.
 * Returns null on the server, on parse errors, or when nothing is stored.
 */
export function getActiveBuyerCode(): ActiveBuyerCode | null {
  if (typeof window === 'undefined') return null;
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    return JSON.parse(raw) as ActiveBuyerCode;
  } catch {
    return null;
  }
}

/**
 * Clear the active buyer code from both storage locations.
 * Called on logout or when the code is no longer valid.
 */
export function clearActiveBuyerCode(): void {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(STORAGE_KEY);
  sessionStorage.removeItem(COMPAT_KEY);
}

/**
 * Derive the shell route for a given buyer code.
 * PWS codes route to the PWS order page; all others go to the bidder dashboard.
 */
export function resolveShellRoute(code: Pick<ActiveBuyerCode, 'id' | 'codeType'>): string {
  if (code.codeType === 'PWS') {
    return `/pws/order?buyerCodeId=${code.id}`;
  }
  return `/bidder/dashboard?buyerCodeId=${code.id}`;
}
