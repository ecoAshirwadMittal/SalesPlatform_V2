/**
 * a11y.ts — axe-core / WCAG assertion helper for the E2E suite.
 *
 * Usage:
 *   import { checkA11y } from './_helpers/a11y';
 *   await checkA11y(page);
 *
 * Rules targeted: wcag2a, wcag2aa, wcag21a, wcag21aa.
 *
 * When a violation is genuinely un-fixable in < 5 min, disable the specific
 * rule via the `disable` parameter and add a TODO comment at the call site.
 */

import AxeBuilder from '@axe-core/playwright';
import type { Page } from '@playwright/test';
import { expect } from '@playwright/test';

export interface A11yOptions {
  /** axe rule IDs to skip. Add a TODO comment at every call site that uses this. */
  disable?: string[];
}

export async function checkA11y(page: Page, opts: A11yOptions = {}): Promise<void> {
  const builder = new AxeBuilder({ page }).withTags([
    'wcag2a',
    'wcag2aa',
    'wcag21a',
    'wcag21aa',
  ]);

  if (opts.disable && opts.disable.length > 0) {
    builder.disableRules(opts.disable);
  }

  const results = await builder.analyze();
  expect(results.violations).toEqual([]);
}
