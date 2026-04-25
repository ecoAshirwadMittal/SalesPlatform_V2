import { execFileSync } from 'node:child_process';
import { existsSync } from 'node:fs';
import path from 'node:path';

/**
 * Apply an idempotent SQL fixture to the dev DB via psql.
 *
 * Resolves files under `frontend/tests/_fixtures/{name}` so callers pass a
 * bare basename (`'bid-data-admin-seed.sql'`).
 *
 * Connection params come from these env vars (with sensible local defaults
 * matching `bootstrap.sql`):
 *   E2E_PG_HOST     (default: localhost)
 *   E2E_PG_USER     (default: salesplatform)
 *   E2E_PG_PASSWORD (default: salesplatform)
 *   E2E_PG_DB       (default: salesplatform_dev)
 *
 * Requires `psql` on PATH. If absent, throws with a clear actionable
 * message rather than failing inside Playwright with a cryptic exec error.
 */
export function applyFixture(name: string): void {
  const sqlPath = path.resolve(process.cwd(), 'tests', '_fixtures', name);

  if (!existsSync(sqlPath)) {
    throw new Error(`SQL fixture not found: ${sqlPath}`);
  }

  const host = process.env.E2E_PG_HOST ?? 'localhost';
  const user = process.env.E2E_PG_USER ?? 'salesplatform';
  const pass = process.env.E2E_PG_PASSWORD ?? 'salesplatform';
  const db = process.env.E2E_PG_DB ?? 'salesplatform_dev';

  try {
    execFileSync(
      'psql',
      ['-h', host, '-U', user, '-d', db, '-v', 'ON_ERROR_STOP=1', '-f', sqlPath],
      {
        env: { ...process.env, PGPASSWORD: pass },
        stdio: ['ignore', 'pipe', 'pipe'],
        maxBuffer: 5 * 1024 * 1024,
      },
    );
  } catch (err: unknown) {
    if ((err as NodeJS.ErrnoException)?.code === 'ENOENT') {
      throw new Error(
        `psql not found on PATH. Install PostgreSQL client tools or set ` +
          `E2E_PG_HOST/USER/PASSWORD/DB to point at a reachable instance.`,
      );
    }
    throw err;
  }
}
