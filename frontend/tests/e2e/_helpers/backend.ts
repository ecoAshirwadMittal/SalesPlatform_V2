/**
 * Probe the Spring Boot actuator endpoint. Live tests gate on this so the
 * suite skips quietly when no backend is up. Caches the result after the
 * first call so N tests don't each pay the HTTP round-trip.
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

export function resetBackendCache(): void {
  cached = null;
}
