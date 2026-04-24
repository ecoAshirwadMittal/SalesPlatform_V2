/**
 * Probe the Spring Boot actuator endpoint. CI runs with mocks only and
 * skips backend-dependent tests; local runs with `mvn spring-boot:run`
 * active auto-enable them.
 *
 * Caches the result after the first call so N tests don't each pay the
 * HTTP round-trip.
 */
let cached: boolean | null = null;

export async function isBackendAvailable(baseUrl = 'http://localhost:8080'): Promise<boolean> {
  if (cached !== null) return cached;
  try {
    const res = await fetch(`${baseUrl}/actuator/health`, {
      signal: AbortSignal.timeout(1500),
    });
    cached = res.ok;
  } catch {
    cached = false;
  }
  return cached;
}

/**
 * Reset the cache — call from `beforeAll` if you want to re-probe. Most
 * runs don't need this.
 */
export function resetBackendCache(): void {
  cached = null;
}
