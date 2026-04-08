/**
 * Guard test: ensures no page uses raw `fetch()` for API calls.
 *
 * All API calls must go through `apiFetch()` from `@/lib/apiFetch`
 * so the JWT Authorization header is automatically attached.
 *
 * Allowed exceptions:
 *   - LoginForm.tsx (the login call itself gets the token)
 *   - apiFetch.ts  (the wrapper itself calls raw fetch)
 */
import { describe, it, expect } from 'vitest';
import * as fs from 'fs';
import * as path from 'path';

const SRC_DIR = path.resolve(__dirname, '..');

/** Files that are allowed to call raw fetch() */
const ALLOWED_RAW_FETCH = [
  'lib/apiFetch.ts',
  '(auth)/login/LoginForm.tsx',
  '__tests__/apiFetch-guard.test.ts',
];

function collectTsxFiles(dir: string, files: string[] = []): string[] {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      collectTsxFiles(full, files);
    } else if (/\.(tsx?|jsx?)$/.test(entry.name)) {
      files.push(full);
    }
  }
  return files;
}

function isAllowed(filePath: string): boolean {
  const rel = path.relative(SRC_DIR, filePath).replace(/\\/g, '/');
  return ALLOWED_RAW_FETCH.some(a => rel.includes(a));
}

describe('apiFetch guard', () => {
  it('no page should use raw fetch() instead of apiFetch() for API calls', () => {
    const allFiles = collectTsxFiles(SRC_DIR);
    const violations: string[] = [];

    for (const file of allFiles) {
      if (isAllowed(file)) continue;

      const content = fs.readFileSync(file, 'utf-8');

      // Match `await fetch(` or `= fetch(` but not `await apiFetch(` or `= apiFetch(`
      // Also skip lines that are comments
      const lines = content.split('\n');
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        if (line.startsWith('//') || line.startsWith('*')) continue;

        // Detect raw fetch usage: `await fetch(` or `fetch(`  as a function call
        // but not `apiFetch(`
        if (/(?<!api)fetch\s*\(/.test(line) && !line.includes('apiFetch')) {
          const rel = path.relative(SRC_DIR, file).replace(/\\/g, '/');
          violations.push(`${rel}:${i + 1}: ${line.substring(0, 100)}`);
        }
      }
    }

    expect(
      violations,
      `Found raw fetch() calls that should use apiFetch() instead:\n${violations.join('\n')}`
    ).toHaveLength(0);
  });

  it('all non-login pages that make API calls should import apiFetch', () => {
    const allFiles = collectTsxFiles(SRC_DIR);
    const violations: string[] = [];

    for (const file of allFiles) {
      if (isAllowed(file)) continue;

      const content = fs.readFileSync(file, 'utf-8');

      // If file contains apiFetch calls but no import, that's a bug
      if (content.includes('apiFetch(') && !content.includes("from '@/lib/apiFetch'") && !content.includes("from \"@/lib/apiFetch\"")) {
        const rel = path.relative(SRC_DIR, file).replace(/\\/g, '/');
        violations.push(rel);
      }
    }

    expect(
      violations,
      `Files use apiFetch() but don't import it:\n${violations.join('\n')}`
    ).toHaveLength(0);
  });
});
