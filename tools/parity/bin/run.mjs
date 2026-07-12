#!/usr/bin/env node
// Parity harness orchestrator: capture (playwright) -> diff (reg-cli) -> scoreboard.
// Usage (from repo root or anywhere):
//   node tools/parity/bin/run.mjs                 # all manifest pages
//   node tools/parity/bin/run.mjs --page auth-login
//   node tools/parity/bin/run.mjs --skip-capture  # re-diff existing PNGs only
import { spawnSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const HERE = path.dirname(fileURLToPath(import.meta.url));
const PARITY = path.join(HERE, '..');
const REPO = path.join(PARITY, '..', '..');
const FRONTEND = path.join(REPO, 'frontend');
const OUT = path.join(PARITY, 'out');

const args = process.argv.slice(2);
const pageArg = args.includes('--page') ? args[args.indexOf('--page') + 1] : undefined;
const skipCapture = args.includes('--skip-capture');

function run(cmd, cmdArgs, extraEnv = {}) {
  const r = spawnSync(cmd, cmdArgs, {
    cwd: FRONTEND,
    stdio: 'inherit',
    shell: true,
    env: { ...process.env, NODE_PATH: path.join(FRONTEND, 'node_modules'), ...extraEnv },
  });
  return r.status ?? 1;
}

let captureExit = 0;
if (!skipCapture) {
  // Clean compare dirs on EVERY capture run — stale files from earlier runs
  // (even other pages) otherwise pollute the reg-cli report. Evidence worth
  // keeping is persisted under docs/tasks/parity/evidence/ instead.
  for (const d of ['new', 'legacy-local', 'diff', 'errors']) {
    fs.rmSync(path.join(OUT, d), { recursive: true, force: true });
  }
  captureExit = run('npx', ['playwright', 'test', '-c', '../tools/parity/playwright.parity.config.ts'], {
    ...(pageArg ? { PARITY_PAGE: pageArg } : {}),
  });
}

const newDir = path.join(OUT, 'new');
const legacyDir = path.join(OUT, 'legacy-local');
let reportExit = 0;
if (fs.existsSync(newDir) && fs.existsSync(legacyDir)) {
  reportExit = run('npx', [
    'reg-cli',
    JSON.stringify(newDir),
    JSON.stringify(legacyDir),
    JSON.stringify(path.join(OUT, 'diff')),
    '-R', JSON.stringify(path.join(OUT, 'report.html')),
    '-J', JSON.stringify(path.join(OUT, 'report.json')),
    '-M', '0.1',
  ]);
} else {
  console.error('No capture output to diff.');
}

// Scoreboard: merge reg-cli verdicts into a machine-readable burn-down artifact.
const reportPath = path.join(OUT, 'report.json');
if (fs.existsSync(reportPath)) {
  const rep = JSON.parse(fs.readFileSync(reportPath, 'utf8'));
  const name = (f) => String(f).replace(/\.png$/, '');
  const scoreboard = {
    generatedAt: new Date().toISOString(),
    captureExit,
    green: (rep.passedItems ?? []).map(name).sort(),
    diff: (rep.failedItems ?? []).map(name).sort(),
    onlyNew: (rep.newItems ?? []).map(name).sort(),
    onlyLegacy: (rep.deletedItems ?? []).map(name).sort(),
  };
  scoreboard.totals = {
    green: scoreboard.green.length,
    diff: scoreboard.diff.length,
    unpaired: scoreboard.onlyNew.length + scoreboard.onlyLegacy.length,
  };
  fs.writeFileSync(path.join(OUT, 'parity-scoreboard.json'), JSON.stringify(scoreboard, null, 2));
  console.log('\n=== parity scoreboard ===');
  console.log(JSON.stringify(scoreboard.totals));
  for (const g of scoreboard.green) console.log(`  GREEN ${g}`);
  for (const d of scoreboard.diff) console.log(`  DIFF  ${d}`);
  for (const u of [...scoreboard.onlyNew, ...scoreboard.onlyLegacy]) console.log(`  UNPAIRED ${u}`);
  console.log(`report: ${path.join(OUT, 'report.html')}`);
}

process.exit(captureExit || 0);
