// Manifest-driven parity capture. One test per page × state — failures isolate.
// Filter with PARITY_PAGE=<pageId>. Diffing happens afterwards (bin/run.mjs → reg-cli).
import { test } from '@playwright/test';
import { loadPages } from '../lib/manifest';
import { captureSide } from '../lib/capture';

const pages = loadPages(process.env.PARITY_PAGE);

if (pages.length === 0) {
  test('manifest has pages', () => {
    throw new Error(`No manifest pages matched PARITY_PAGE=${process.env.PARITY_PAGE ?? '(all)'}`);
  });
}

for (const p of pages) {
  for (const state of p.states) {
    test(`capture ${p.pageId} [${state.id}]`, async ({ browser }) => {
      test.skip(!!p.skip, p.skip ?? '');
      // New side first (fast, known-good) so legacy journey issues don't hide new-side breaks.
      const newShot = await captureSide(browser, p, state.id, 'new');
      console.log(`ok new     ${newShot}`);
      const legacyShot = await captureSide(browser, p, state.id, 'legacy');
      console.log(`ok legacy  ${legacyShot}`);
    });
  }
}
