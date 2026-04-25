'use client';

/**
 * BidderShell layout — wraps all routes under /bidder/** with the
 * auction-buyer-specific chrome (gradient sidebar + buyer portal top-bar).
 *
 * Layout strategy: OPTION B (visual override)
 *
 * This file lives at app/(dashboard)/bidder/layout.tsx, which makes it a
 * child of (dashboard)/layout.tsx. In Next.js 16 App Router, nested layouts
 * always compose — the child layout's output is rendered as the `children`
 * prop of the parent. The admin shell (sidebar + top-bar from the parent
 * layout) therefore renders in the DOM behind this shell.
 *
 * Option A (moving bidder/ into a separate (buyer-portal) route group) would
 * cleanly isolate layouts but requires removing the existing files from
 * (dashboard)/bidder/dashboard/ to avoid duplicate URL conflicts in the
 * Next.js build. Given the number of component files that would need to move
 * (BidderDashboardClient, BidGrid, BidGridRow, DashboardHeader, EndOfBiddingPanel,
 * SubmitBar and their CSS modules), Option B is chosen here as the lower-risk
 * path for Phase 4. A future refactor can move the route tree when the full
 * dashboard component surface stabilises.
 *
 * The BidderShell renders `position: fixed; inset: 0` so it occupies the
 * entire viewport regardless of the admin-shell container. The admin shell
 * DOM is present but visually covered. Scroll position is owned by this
 * shell's inner content area.
 *
 * Hook contracts:
 *   - useActiveBuyerCode: validates the active code, redirects to /buyer-select
 *     if absent or invalid. Returns { active, loading, error }.
 *   - useSidebar (via SidebarProvider): collapsed state persisted to
 *     localStorage under 'bidder.sidebarCollapsed'.
 */

import { Suspense, useCallback, useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { SidebarProvider } from '@/components/chrome/SidebarContext';
import BuyerPortalChrome from '@/components/chrome/BuyerPortalChrome';
import BidderSidebar from '@/components/bidder/BidderSidebar';
import { useActiveBuyerCode } from '@/hooks/useActiveBuyerCode';
import { getAuthUser, type AuthUser } from '@/lib/session';
import { clearActiveBuyerCode } from '@/lib/activeBuyerCode';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';
import type { AvatarMenuItem } from '@/components/chrome/UserAvatarPopover';
import styles from './bidderShell.module.css';

/**
 * Inner shell component — separated so it can call hooks after
 * SidebarProvider is in the tree.
 */
function BidderShellInner({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const { active, loading } = useActiveBuyerCode();

  // Auth user is read from localStorage; we initialise null to avoid SSR
  // mismatch and hydrate after mount.
  const [authUser, setAuthUser] = useState<AuthUser | null>(null);

  useEffect(() => {
    // Reading localStorage is synchronous; calling setState inside useEffect
    // after mount is the standard Next.js SSR-safe pattern for hydrating from
    // client storage. The admin layout uses the same pattern at (dashboard)/layout.tsx:129.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setAuthUser(getAuthUser());
  }, []);

  const handleSwitchBuyerCode = useCallback(() => {
    router.push('/buyer-select');
  }, [router]);

  const handleLogout = useCallback(async () => {
    try {
      await apiFetch(`${API_BASE}/auth/logout`, { method: 'POST' });
    } catch {
      // Best-effort: clear local state and redirect regardless.
    }
    clearActiveBuyerCode();
    localStorage.removeItem('auth_user');
    try {
      new BroadcastChannel('auth').postMessage('logout');
    } catch {
      // BroadcastChannel is not supported in all environments.
    }
    router.push('/login');
  }, [router]);

  const avatarPopoverItems: AvatarMenuItem[] = [
    {
      label: 'Submit Feedback',
      // TODO Phase N: wire to mailto: or in-app feedback form
      onClick: () => {
        window.location.href = 'mailto:feedback@ecoatm.com?subject=Buyer%20Portal%20Feedback';
      },
    },
    {
      label: 'Logout',
      onClick: handleLogout,
    },
  ];

  // During loading or while useActiveBuyerCode validates the code,
  // return a minimal placeholder. The hook handles the redirect to
  // /buyer-select internally if the code is missing or invalid.
  if (loading || !authUser) {
    return (
      <div className={styles.shellRoot}>
        <div className={styles.shellLoading} aria-live="polite" aria-label="Loading…" />
      </div>
    );
  }

  return (
    <div className={styles.shellRoot}>
      {/* Gradient sidebar */}
      <BidderSidebar />

      {/* Main content area: top-bar + page content */}
      <div className={styles.shellMain}>
        <BuyerPortalChrome
          activeBuyerCode={active}
          user={authUser}
          onSwitchBuyerCode={handleSwitchBuyerCode}
          avatarPopoverItems={avatarPopoverItems}
        />

        {/* Page content rendered below the chrome top-bar */}
        <main className={styles.shellContent}>
          {children}
        </main>
      </div>
    </div>
  );
}

/**
 * BidderLayout — the exported layout component.
 *
 * Wraps BidderShellInner in a SidebarProvider so useSidebar() is available
 * throughout the sidebar and any nested components.
 */
export default function BidderLayout({ children }: { children: React.ReactNode }) {
  return (
    <SidebarProvider storageKey="bidder.sidebarCollapsed" defaultCollapsed={false}>
      {/* Suspense must sit above BidderShellInner because useActiveBuyerCode
          calls useSearchParams(); without this boundary Next 16 bails out of
          static prerender for every /bidder/** page. */}
      <Suspense fallback={<div />}>
        <BidderShellInner>
          {children}
        </BidderShellInner>
      </Suspense>
    </SidebarProvider>
  );
}
