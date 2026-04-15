/**
 * Wrapper around fetch that forwards the HttpOnly `auth_token` cookie on
 * every request via `credentials: 'include'`. The token is never accessible
 * to JS — this wrapper exists so we always opt into cookie forwarding and
 * to keep call sites uniform.
 *
 * Drop-in replacement: apiFetch(url, init?) has the same signature as fetch().
 */
export function apiFetch(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
  return fetch(input, {
    ...init,
    credentials: 'include',
  });
}
