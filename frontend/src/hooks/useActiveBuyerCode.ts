/**
 * useActiveBuyerCode — validates the active buyer code on mount.
 *
 * Resolution priority (highest → lowest):
 *   1. URL param  ?buyerCodeId=<id>   (deep-link wins)
 *   2. localStorage['activeBuyerCode']
 *
 * After resolving a candidate id, the hook validates it against
 * GET /auth/buyer-codes?userId=<userId>. If the code is absent from the
 * response (removed, expired, or wrong user) it redirects to /buyer-select.
 *
 * Designed for use in the bidder and PWS layout shells (Phase 4+).
 * Phase 3 ships the hook; Phase 4 wires it into the layout providers.
 */

import { useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import type { ActiveBuyerCode } from '@/lib/activeBuyerCode';
import {
  getActiveBuyerCode,
  setActiveBuyerCode,
} from '@/lib/activeBuyerCode';
import { getAuthUser } from '@/lib/session';
import { apiFetch } from '@/lib/apiFetch';
import { API_BASE } from '@/lib/apiRoutes';

interface UseActiveBuyerCodeResult {
  active: ActiveBuyerCode | null;
  loading: boolean;
  error: Error | null;
}

/**
 * Raw shape returned by GET /auth/buyer-codes.
 * Must match the BuyerCodeResponse DTO on the backend.
 */
interface BuyerCodeApiResponse {
  id: number;
  code: string;
  buyerName: string;
  buyerCodeType: string;
  codeType: 'AUCTION' | 'PWS';
}

export function useActiveBuyerCode(): UseActiveBuyerCodeResult {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [active, setActive] = useState<ActiveBuyerCode | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function validate() {
      try {
        const authUser = getAuthUser();
        if (!authUser) {
          router.replace('/login');
          return;
        }

        // Resolve candidate: URL param takes precedence over localStorage.
        const urlParam = searchParams.get('buyerCodeId');
        const candidateId = urlParam ? parseInt(urlParam, 10) : null;
        const stored = getActiveBuyerCode();

        // If neither source has a code, send to the picker.
        if (!candidateId && !stored) {
          router.replace('/buyer-select');
          return;
        }

        // Fetch the user's buyer codes to validate the candidate.
        const res = await apiFetch(
          `${API_BASE}/auth/buyer-codes?userId=${authUser.userId}`
        );
        if (!res.ok) throw new Error(`Failed to load buyer codes: HTTP ${res.status}`);
        const codes: BuyerCodeApiResponse[] = await res.json();

        // Prefer the URL-param id; fall back to the stored code's id.
        const resolvedId = candidateId ?? stored!.id;
        const matched = codes.find((c) => c.id === resolvedId);

        if (!matched) {
          // Code is no longer valid for this user — redirect to picker.
          router.replace('/buyer-select');
          return;
        }

        const resolved: ActiveBuyerCode = {
          id: matched.id,
          code: matched.code,
          buyerName: matched.buyerName,
          buyerCodeType: matched.buyerCodeType,
          codeType: matched.codeType,
        };

        // Persist so subsequent navigations (that don't carry the URL param) still work.
        setActiveBuyerCode(resolved);

        if (!cancelled) {
          setActive(resolved);
        }
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err : new Error(String(err)));
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    validate();
    return () => {
      cancelled = true;
    };
    // router and searchParams are stable across renders; include them to satisfy
    // linting rules, but this effect intentionally runs once on mount.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return { active, loading, error };
}
