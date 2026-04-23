'use client';

/**
 * SidebarContext — shared collapsed/expanded state for the buyer portal shells.
 *
 * Phase 3 ships the provider + hook. Phase 4 wraps the actual shell layouts
 * with <SidebarProvider> and wires <SidebarToggle> as the trigger.
 *
 * Persistence: sidebar state is persisted to localStorage under the key
 * provided via the `storageKey` prop so each shell can maintain its own
 * preference (bidder shell: 'bidder.sidebarCollapsed',
 * PWS shell: 'pws.sidebarCollapsed').
 */

import {
  createContext,
  useCallback,
  useContext,
  useState,
  type ReactNode,
} from 'react';

interface SidebarContextValue {
  collapsed: boolean;
  toggle: () => void;
}

const SidebarContext = createContext<SidebarContextValue | null>(null);

interface SidebarProviderProps {
  children: ReactNode;
  /** localStorage key for persisting the collapsed preference. */
  storageKey?: string;
  /** Initial collapsed state (used only if no persisted value exists). */
  defaultCollapsed?: boolean;
}

export function SidebarProvider({
  children,
  storageKey = 'bidder.sidebarCollapsed',
  defaultCollapsed = false,
}: SidebarProviderProps) {
  // Use a lazy initialiser to hydrate from localStorage without an effect.
  // On the server (SSR) we always start with defaultCollapsed — there is no
  // localStorage. On the client the initialiser runs once synchronously before
  // the first render, so the component never flickers from the default value to
  // the stored value (no state-inside-effect problem, no extra render cycle).
  const [collapsed, setCollapsed] = useState<boolean>(() => {
    if (typeof window === 'undefined') return defaultCollapsed;
    const stored = localStorage.getItem(storageKey);
    return stored !== null ? stored === 'true' : defaultCollapsed;
  });

  const toggle = useCallback(() => {
    setCollapsed((prev) => {
      const next = !prev;
      if (typeof window !== 'undefined') {
        localStorage.setItem(storageKey, String(next));
      }
      return next;
    });
  }, [storageKey]);

  return (
    <SidebarContext.Provider value={{ collapsed, toggle }}>
      {children}
    </SidebarContext.Provider>
  );
}

/**
 * useSidebar — consume the sidebar collapsed state and toggle fn.
 * Must be used inside a <SidebarProvider>.
 */
export function useSidebar(): SidebarContextValue {
  const ctx = useContext(SidebarContext);
  if (!ctx) {
    throw new Error('useSidebar must be used within a <SidebarProvider>');
  }
  return ctx;
}
