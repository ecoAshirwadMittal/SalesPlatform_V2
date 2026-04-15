# TypeScript Code Review Report

**Date:** 2026-04-11
**Reviewer:** typescript-reviewer agent
**Scope:** All modified/new TypeScript files on `main` branch
**Verdict:** BLOCK -- Security issues must be fixed before deploy

---

## Summary

| Severity | Count | Action Required |
|----------|-------|-----------------|
| CRITICAL | 0 | -- |
| HIGH | 7 | Must fix before merge |
| MEDIUM | 10 | Should fix |
| LOW | 3 | Optional |

---

## HIGH

### H-1 -- `any` type in `order/page.tsx`

**Files:** `frontend/src/app/pws/order/page.tsx` lines ~189, ~215

`loadInventory` and `loadCaseLots` map over API response arrays with `(d: any)` and `(cl: any)`. ESLint's `@typescript-eslint/no-explicit-any` flags both. Any typo in field access silently produces `undefined` without a compile-time error.

**Fix:** Define interfaces for the raw API shape and parse into them, or use `unknown` with narrowing.

---

### H-2 -- `setState` called synchronously inside `useEffect` (3 files)

**Files:**
- `frontend/src/app/(dashboard)/layout.tsx` line 139
- `frontend/src/app/pws/layout.tsx` line 174
- `frontend/src/app/pws/counter-offers/page.tsx` line 50

Pattern:
```ts
useEffect(() => {
  const stored = localStorage.getItem('auth_user');
  if (stored) {
    try { setUser(JSON.parse(stored)); } catch { /* ignore */ }
  }
}, []);
```

This causes an immediate extra render cycle on mount.

**Fix:** Use a lazy `useState` initializer:
```ts
const [user, setUser] = useState<AuthUser | null>(() => {
  if (typeof window === 'undefined') return null;
  try { return JSON.parse(localStorage.getItem('auth_user') ?? ''); }
  catch { return null; }
});
```

---

### H-3 -- Floating / fire-and-forget promises with no error handling

**Files:** `frontend/src/app/pws/order/page.tsx`, `frontend/src/app/pws/cart/page.tsx`

`saveItemToApi` and `saveCaseLotItemToApi` call `apiFetch(...).catch(err => console.error(...))`. The `catch` only logs -- it does not update UI state. In a transactional UI (offer/cart), a silent failure means the user believes their change was persisted when it was not.

In `cart/page.tsx` (lines 139-149), `saveItemToApi` is a fire-and-forget `.then().catch()` chain. If the PUT fails, in-memory cart state is updated but the backend is not.

**Fix:** Set an error state that surfaces to the user when save fails.

---

### H-4 -- `returnTo` open redirect in `LoginForm.tsx`

**File:** `frontend/src/app/(auth)/login/LoginForm.tsx` lines 52-55

```ts
const returnTo = params.get('returnTo');
if (returnTo) {
  router.push(returnTo);
}
```

`returnTo` comes directly from the URL query string with no validation. An attacker can craft `/login?returnTo=https://evil.com` and after authentication the user navigates off-site.

**Fix:**
```ts
if (returnTo && returnTo.startsWith('/') && !returnTo.startsWith('//')) {
  router.push(returnTo);
}
```

---

### H-5 -- JWT stored in `localStorage` and cookie with no `HttpOnly` flag

**File:** `frontend/src/app/(auth)/login/LoginForm.tsx` lines 44-47

```ts
localStorage.setItem('auth_token', data.token);
document.cookie = `auth_token=${data.token}; path=/; SameSite=Lax`;
```

Token is stored in `localStorage` (accessible to any JS on the page, vulnerable to XSS) and in a client-set cookie without `HttpOnly` (also script-accessible). A `document.cookie` call from the browser can never set `HttpOnly`.

**Fix:** The Spring Boot backend should return a `Set-Cookie` response header with `HttpOnly; Secure; SameSite=Strict`. The frontend should not handle token storage directly.

---

### H-6 -- Swallowed errors across layouts and effects

**Files:** `frontend/src/app/pws/layout.tsx` lines 174, 179, 204; `frontend/src/app/(dashboard)/layout.tsx` line 139

All `JSON.parse` calls on storage values are wrapped in `try/catch { /* ignore */ }`. The `loadBuyerCodes()` function (pws/layout.tsx line 204) also swallows network fetch errors silently. If the buyer-code fetch fails, the user sees no indication.

**Fix:** Log the error or set an error state visible in the UI.

---

### H-7 -- `useEffect` calls non-stable function references without listing them as deps

**File:** `frontend/src/app/pws/order/page.tsx` lines 154-159

```ts
useEffect(() => {
  loadInventory();
  loadCaseLots();
  loadCart();
}, []);
```

`loadInventory`, `loadCaseLots`, and `loadCart` are declared as plain `async function` inside the component body. They are recreated on every render yet not listed in the dependency array. If any of these functions close over state that changes, they will silently use stale closures.

**Fix:** Wrap the functions in `useCallback` with proper deps or inline them into the effect.

---

## MEDIUM

### M-1 -- `proxy.ts` named export mismatch with Next.js middleware convention

**File:** `frontend/src/proxy.ts`

Exports `proxy` and `config` but Next.js middleware must be in `middleware.ts` at the root of `src/` and must export `default`. Verify a separate `src/middleware.ts` imports and re-exports this.

---

### M-2 -- `next.config.ts` hardcodes `localhost:8080` as proxy destination

**File:** `frontend/next.config.ts` line 12

```ts
destination: 'http://localhost:8080/api/v1/:path*',
```

In staging/production, the rewrite will proxy to a non-existent host.

**Fix:**
```ts
destination: `${process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'}/api/v1/:path*`,
```

---

### M-3 -- `router` dependency in effect

**File:** `frontend/src/app/pws/counter-offers/page.tsx` line 69

`router` is in the dep array which is correct, but if `router` changes identity on every render the effect will re-fire. In practice with Next.js App Router `useRouter()` returns a stable reference, but this should be documented.

---

### M-4 -- `key={index}` in rendered error lists

**Files:** `frontend/src/app/pws/cart/page.tsx` line 399, `frontend/src/app/pws/offer-review/[offerId]/page.tsx` line 413

Using array index as `key` causes incorrect reconciliation if errors are added/removed from the middle.

**Fix:** Use the error string itself as the key.

---

### M-5 -- `console.error` left in production code

**Files:** `cart/page.tsx` (lines 130, 149, 198), `order/page.tsx` (lines 204, 232, 252, 263, 273), `offer-review/[offerId]/page.tsx` (lines 107, 143)

**Fix:** Replace with a structured logger or surface via UI state.

---

### M-6 -- `handleItemAction` has no error handling

**File:** `frontend/src/app/pws/counter-offers/[offerId]/page.tsx` lines 120-130

```ts
const handleItemAction = async (itemId: number, buyerCounterStatus: string) => {
  const res = await apiFetch(...);
  if (res.ok) { ... }
};
```

No `try/catch`. If the network request throws, the unhandled rejection is silently swallowed. Same for `handleAcceptAll` (lines 132-140).

**Fix:** Add try/catch and surface errors to the `message` state.

---

### M-7 -- Shared mutable `page` variable in E2E tests

**File:** `frontend/tests/pws/offer-flow.spec.ts` lines 15-22

A module-level `page` instance is shared across seven `test.describe` blocks. If any test leaves the page in a broken state, all subsequent flows fail.

**Fix:** Each `describe` block should have its own `beforeAll`/`afterAll` or use Playwright fixtures for per-describe isolation.

---

### M-8 -- Unused `err` binding in `LoginForm.tsx`

**File:** `frontend/src/app/(auth)/login/LoginForm.tsx` line 65

ESLint warns: `'err' is defined but never used`.

**Fix:** Change to `catch { ... }` (no binding).

---

### M-9 -- `auth_token` cookie cleared without `Secure` flag on logout

**Files:** `frontend/src/app/(dashboard)/layout.tsx` line 183, `frontend/src/app/pws/layout.tsx` line 262

```ts
document.cookie = 'auth_token=; path=/; max-age=0';
```

To delete a cookie, you must match all attributes from creation. If the cookie was set with `Secure`, omitting it may prevent deletion in some browsers.

**Fix:** Mirror the creation attributes from `LoginForm.tsx`.

---

### M-10 -- `backLink` implemented as a `div` instead of a `button`

**File:** `frontend/src/app/pws/offer-review/[offerId]/page.tsx` line 205

```tsx
<div className={styles.backLink} onClick={() => router.push('/pws/offer-review')}>
```

A `div` with `onClick` is not keyboard-accessible and not semantically correct. Will fail accessibility audits.

**Fix:** Use `<button type="button">` or `<a href="/pws/offer-review">`.

---

## LOW

### L-1 -- E2E retries set to 1 in CI only

**File:** `frontend/playwright.config.ts` line 7

`retries: process.env.CI ? 1 : 0` -- single retry in CI is generally too low for E2E tests against a real backend. Consider 2 retries.

---

### L-2 -- `navigateTo` fixture has an unsafe `as any` cast

**File:** `frontend/tests/fixtures/auth.ts` line 73

```ts
await use(async (label: string) => navigateTo(page, label as any));
```

**Fix:** Update the fixture type to use the union type from the function signature.

---

### L-3 -- Magic number `PAGE_SIZE` duplicated

**Files:** `frontend/src/app/pws/counter-offers/[offerId]/page.tsx` line 9 (`PAGE_SIZE = 20`), `frontend/src/app/pws/order/page.tsx` line 9 (`PAGE_SIZE = 50`)

Different values with the same name in separate files. Fine but should be documented since they will diverge from any future shared pagination constant.

---

## Key Files with Most Issues

| File | Issues |
|------|--------|
| `frontend/src/app/(auth)/login/LoginForm.tsx` | H-4, H-5, M-8 |
| `frontend/src/app/pws/layout.tsx` | H-2, H-6, M-9 |
| `frontend/src/app/(dashboard)/layout.tsx` | H-2, M-9 |
| `frontend/src/app/pws/order/page.tsx` | H-1, H-3, H-7, M-5 |
| `frontend/src/app/pws/cart/page.tsx` | H-3, M-4, M-5 |
| `frontend/tests/pws/offer-flow.spec.ts` | M-7 |

---

## Recommended Fix Order

1. **H-4** (open redirect) -- security, quick fix
2. **H-5** (JWT storage) -- security, requires backend changes
3. **H-1** (any types) -- type safety
4. **H-2** (setState in effects) -- correctness
5. **H-3** (silent failures) -- user experience
6. **H-6** (swallowed errors) -- observability
7. **H-7** (stale closures) -- correctness
8. All MEDIUM issues
