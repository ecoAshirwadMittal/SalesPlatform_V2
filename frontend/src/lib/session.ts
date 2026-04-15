/**
 * Shared session helpers — single source of truth for reading the selected
 * buyer code and the authenticated user out of browser storage. Duplicated
 * copies previously lived in cart/, counter-offers/, order/, and orders/.
 */

export interface SelectedBuyerCode {
  id: number;
  code?: string;
  [key: string]: unknown;
}

export interface AuthUser {
  userId: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  initials: string;
  roles?: string[];
}

function readJson<T>(storage: Storage | null, key: string): T | null {
  if (!storage) return null;
  try {
    const raw = storage.getItem(key);
    if (!raw) return null;
    return JSON.parse(raw) as T;
  } catch {
    return null;
  }
}

export function getSelectedBuyerCode(): SelectedBuyerCode | null {
  if (typeof window === 'undefined') return null;
  return readJson<SelectedBuyerCode>(window.sessionStorage, 'selectedBuyerCode');
}

export function getBuyerCodeId(): number | null {
  const selected = getSelectedBuyerCode();
  return selected?.id ?? null;
}

export function getAuthUser(): AuthUser | null {
  if (typeof window === 'undefined') return null;
  return readJson<AuthUser>(window.localStorage, 'auth_user');
}

export function getUserId(): number | null {
  return getAuthUser()?.userId ?? null;
}
