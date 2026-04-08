/**
 * Wrapper around fetch that automatically attaches the JWT auth token
 * from localStorage to every request as a Bearer token.
 *
 * Drop-in replacement: apiFetch(url, init?) has the same signature as fetch().
 */
export function apiFetch(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
  const token = typeof window !== 'undefined' ? localStorage.getItem('auth_token') : null;

  const headers = new Headers(init?.headers);
  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  return fetch(input, { ...init, headers });
}
