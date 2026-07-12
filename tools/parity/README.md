# Parity Harness (`tools/parity`)

Live A/B comparison of the legacy Mendix app (local `:8082`, snapshot-pinned) against the new
Next.js app (`:3000`) — one pinned Chromium, identical normalization on both sides, manifest
driven. Program docs: `docs/tasks/parity-program-plan-2026-07-11.md` · findings ledger:
`docs/tasks/parity/findings.md` · schema-aware data gate: `docs/tasks/parity/schema-map.md`.

## Run

```bash
node tools/parity/bin/run.mjs                    # all manifest pages -> capture + diff + scoreboard
node tools/parity/bin/run.mjs --page auth-login  # one page (fast iteration loop)
node tools/parity/bin/run.mjs --skip-capture     # re-diff existing PNGs
```

Prereqs: backend `:8080` + frontend `:3000` running (schedulers frozen for capture sessions:
`AUCTIONS_LIFECYCLE_ENABLED=false`), local Mendix `:8082` on `qa-0327_mendix`, and
`tools/parity/auth/creds.local.json` (gitignored — see below).

Outputs (gitignored): `out/new/`, `out/legacy-local/`, `out/diff/`, `out/report.html`
(side-by-side slider), `out/parity-scoreboard.json`. Persist anything durable to
`docs/tasks/parity/evidence/` and log findings in `findings.md`.

## Manifests

Pages live in `docs/tasks/parity/pages/*.yaml` (one file per page; schema in `schema.ts`,
validated on load). Global bases/viewport/fixed-time in `docs/tasks/parity/config.yaml`.
Masks are declared per page **per side** and applied at capture time; every mask needs a
reason + note (registry discipline per plan §3).

## Auth

`auth/creds.local.json` (gitignored):

```json
{ "legacy": { "admin": {"user":"…","pass":"…","loginPath":"/login.html"},
              "buyer": {"user":"…","pass":"…","loginPath":"/p/login/web"} },
  "new":    { "admin": {"user":"…","pass":"…"}, "bidder": {"user":"…","pass":"…"} } }
```

Login happens once per (side, role) per run; storageState cached 20 min under `auth/`.

## Notes / gotchas

- `reg-cli` pinned to **0.17.7** — the 6.x WASM build panics on Windows.
- Specs resolve deps from `frontend/node_modules` via `NODE_PATH` (bin/run.mjs sets it).
- `clock.setFixedTime` (not `clock.install`) — install pauses timers and can stall the
  Mendix client boot.
- Legacy journeys click **sidebar text labels** (e.g. `text="Reserved Bids (EB)"`), never
  `mx-name-*` (CLAUDE.md rule); Mendix structural classes (`.widget-datagrid`, `.mx-page`)
  are fine.
- H0 one-off spec (`specs/h0-login.spec.ts`) is excluded by `testMatch`; run it explicitly
  with `--grep` + a testMatch override if ever needed again.
