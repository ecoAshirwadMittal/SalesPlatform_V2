/**
 * Narrow an `unknown` caught value to a displayable string. Phase 2 of
 * the repo simplification replaces silent `console.error` swallowing
 * with user-visible error banners — call sites feed this helper into
 * the error-state setter so messages are consistent.
 */
export function getErrorMessage(err: unknown, fallback = 'Something went wrong. Please try again.'): string {
  if (err instanceof Error && err.message) return err.message;
  if (typeof err === 'string' && err.length > 0) return err;
  return fallback;
}
