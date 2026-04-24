// @vitest-environment jsdom
import { act, renderHook, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { useActiveBuyerCode } from './useActiveBuyerCode';

// Mock Next router + search params so the hook can read them without a real
// App Router. Each test rebuilds these to simulate different URL states.
const routerReplace = vi.fn();
let urlParams: URLSearchParams;

vi.mock('next/navigation', () => ({
  useRouter: () => ({ replace: routerReplace }),
  useSearchParams: () => urlParams,
}));

vi.mock('@/lib/apiFetch', () => ({
  apiFetch: (url: string) => global.fetch(url),
}));

vi.mock('@/lib/apiRoutes', () => ({
  API_BASE: '/api/v1',
}));

const authUser = {
  userId: 42,
  firstName: 'Test',
  lastName: 'User',
  fullName: 'Test User',
  email: 'test@example.com',
  initials: 'TU',
};

function primeLocalStorage(authUserPresent: boolean, activeCode: { id: number; code: string; buyerName: string; buyerCodeType: string; codeType: 'AUCTION' | 'PWS' } | null) {
  window.localStorage.clear();
  window.sessionStorage.clear();
  if (authUserPresent) {
    window.localStorage.setItem('auth_user', JSON.stringify(authUser));
  }
  if (activeCode) {
    window.localStorage.setItem('activeBuyerCode', JSON.stringify(activeCode));
  }
}

function primeFetch(codes: Array<{ id: number; code: string; buyerName: string; buyerCodeType: string; codeType: 'AUCTION' | 'PWS' }>) {
  global.fetch = vi.fn().mockResolvedValue({
    ok: true,
    status: 200,
    json: async () => codes,
  }) as unknown as typeof fetch;
}

describe('useActiveBuyerCode', () => {
  beforeEach(() => {
    routerReplace.mockReset();
    urlParams = new URLSearchParams();
  });

  afterEach(() => {
    window.localStorage.clear();
    window.sessionStorage.clear();
  });

  it('redirects to /login when no auth_user in storage', async () => {
    primeLocalStorage(false, null);
    primeFetch([]);

    const { result } = renderHook(() => useActiveBuyerCode());
    await waitFor(() => expect(routerReplace).toHaveBeenCalled());
    expect(routerReplace).toHaveBeenCalledWith('/login');
    expect(result.current.active).toBeNull();
  });

  it('redirects to /buyer-select when no candidate id in url or storage', async () => {
    primeLocalStorage(true, null);
    primeFetch([]);

    renderHook(() => useActiveBuyerCode());
    await waitFor(() => expect(routerReplace).toHaveBeenCalled());
    expect(routerReplace).toHaveBeenCalledWith('/buyer-select');
  });

  it('validates and returns the stored active code when API confirms it', async () => {
    const code = { id: 501, code: 'AD', buyerName: 'CHS', buyerCodeType: 'Wholesale', codeType: 'AUCTION' as const };
    primeLocalStorage(true, code);
    primeFetch([code]);

    const { result } = renderHook(() => useActiveBuyerCode());
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.active).toEqual(code);
    expect(routerReplace).not.toHaveBeenCalled();
  });

  it('URL param overrides localStorage when both are present', async () => {
    const storedCode = { id: 501, code: 'AD', buyerName: 'CHS', buyerCodeType: 'Wholesale', codeType: 'AUCTION' as const };
    const urlCode = { id: 502, code: 'DDWS', buyerName: 'CHS', buyerCodeType: 'Wholesale', codeType: 'AUCTION' as const };
    primeLocalStorage(true, storedCode);
    primeFetch([storedCode, urlCode]);
    urlParams = new URLSearchParams('buyerCodeId=502');

    const { result } = renderHook(() => useActiveBuyerCode());
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.active?.id).toBe(502);
    expect(result.current.active?.code).toBe('DDWS');
  });

  it('redirects to /buyer-select when the stored code is no longer in the API response', async () => {
    const staleCode = { id: 999, code: 'OLD', buyerName: 'Gone', buyerCodeType: 'Wholesale', codeType: 'AUCTION' as const };
    primeLocalStorage(true, staleCode);
    primeFetch([]);   // user no longer has that code assigned

    renderHook(() => useActiveBuyerCode());
    await waitFor(() => expect(routerReplace).toHaveBeenCalled());
    expect(routerReplace).toHaveBeenCalledWith('/buyer-select');
  });

  it('surfaces an error when the API fetch fails', async () => {
    const code = { id: 501, code: 'AD', buyerName: 'CHS', buyerCodeType: 'Wholesale', codeType: 'AUCTION' as const };
    primeLocalStorage(true, code);
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 500,
    }) as unknown as typeof fetch;

    const { result } = renderHook(() => useActiveBuyerCode());
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.error).toBeInstanceOf(Error);
    expect(result.current.error?.message).toMatch(/500/);
  });

  it('ensures loading flips to false in the happy path', async () => {
    const code = { id: 501, code: 'AD', buyerName: 'CHS', buyerCodeType: 'Wholesale', codeType: 'AUCTION' as const };
    primeLocalStorage(true, code);
    primeFetch([code]);

    const { result } = renderHook(() => useActiveBuyerCode());
    expect(result.current.loading).toBe(true);
    await act(async () => {});
    await waitFor(() => expect(result.current.loading).toBe(false));
  });
});
