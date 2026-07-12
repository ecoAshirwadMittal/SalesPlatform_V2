# Legacy ↔ New Parity Program — Plan (2026-07-11)

**Goal:** run the legacy Mendix SalesPlatform and the new Next.js/Spring rebuild side-by-side on
localhost and drive every page to *customer-indistinguishable* parity — 100% pixel match plus
functional/UX equivalence — iteratively, page by page, with an auditable definition of "done."

Synthesized from four parallel design workstreams (inventory, runtime topology, diff harness,
coverage/process). Companion prior art: `docs/tasks/qa-vs-local-page-audit-playbook.md` and the
reserve-bids / PO walkthroughs — this plan scales that manual process, it does not replace it.

---

## 0. The one-paragraph answer

Pin **one database snapshot** and derive both apps from it (legacy Mendix runtime locally on a
restored copy; new app re-seeded via `extract_qa_data.py` from the *same* snapshot), freeze both
clocks, then run a **live A/B capture harness**: one pinned Chromium, two browser contexts (one
logged into each app), identical normalization (srgb color profile, DPR 1, animations off, fonts
injected identically, time frozen), screenshot both sides per page/state, diff with
`reg-cli`/`pixelmatch`, and gate on **zero unexplained pixels** — every non-matching pixel is
either fixed or covered by a justified, registered mask. A manifest
(`docs/tasks/parity/pages/*.yaml`) is the single source of truth: it drives the harness, tracks
per-page status/burn-down, and later becomes the production URL-redirect table. Because the two
apps hold the same data in **different schemas** (the migration is the bridge), every run is gated
by **schema-aware data validation first**: paired queries — legacy Mendix dialect vs new-schema
dialect, matched on business keys — prove both DBs hold the same data before any pixel is diffed
(see `docs/tasks/parity/schema-map.md`).

---

## 1. Why the naive approach is already known to fail (banked lesson)

A previous pixel-parity sprint compared new-app screenshots against **stored PNGs captured earlier
from live Mendix** and scored **0/8**. Documented root causes
(`docs/TODO/pixel-compare-strategy-plan.md`, `docs/qa-reference/README.md`):

1. Baseline PNGs were stale snapshots of a **live, drifting database** — row data never matched.
2. Baselines were captured on a **different machine/color space** (macOS P3 vs sRGB) — persistent color deltas.
3. GPU gradient rendering, `font-display: swap` flicker, browser version skew.

Every one of these is an artifact of *stored cross-machine baselines vs a moving target*. The
design below removes all three **by construction**:

- **No stored baseline.** The legacy app *is* the baseline, re-rendered live on every run.
- **Same pinned Chromium binary, same host, same second, both sides** — OS/GPU/color-space/AA
  deltas cancel; `--force-color-profile=srgb` kills the P3-vs-sRGB class outright.
- **Same data on both sides** — both apps derive from one snapshot (§2), so row-level content
  matches instead of being masked away.

This is also why SaaS visual tools (Percy/Chromatic/Argos/Lost Pixel) are the wrong shape: their
whole value is managing a *stored approved baseline of the same app*. We have a live cross-app A/B
comparison — nothing to store or approve.

---

## 2. Runtime topology — Option C hybrid (recommended)

### Options considered

| | A: hosted QA vs local new | B: local Mendix vs local new, same snapshot | C: hybrid (B primary, A fallback) |
|---|---|---|---|
| Data determinism | Weak — QA drifts the moment after the dump; IDs never align | Strongest — both sides derive from one instant | Strong where it matters |
| Clock/round state | Two independent clocks; rounds rarely align | Self-aligning (one wall clock; freeze schedulers) | Aligned on the primary lane |
| Env noise | Highest (TLS, CDN recompression, remote fonts, latency) | Lowest (both `http://localhost`, authentic Atlas theme + fonts) | Low |
| Coverage risk | None (QA is the full app) | Local Mendix project may not contain buyer/PWS surfaces | Covered by fallback |
| Effort | Lowest setup, highest per-comparison friction | Highest one-time setup | Medium |

**Recommendation: Option C.**
- **Primary reference = local Mendix runtime** (`http://localhost:8082/index.html?profile=Responsive`)
  pointed at a restored snapshot DB — deterministic data, noise-free, clock-aligned, and it serves
  the *authentic* Atlas theme + Brandon Grotesque files (a more faithful pixel reference than any
  font extraction). **User-confirmed (2026-07-11): the local Mendix app serves BOTH the PWS and
  buyer/auction surfaces**, so this lane covers the full app — the coverage risk is retired.
- **Certification + fallback = hosted QA** (`buy-qa.ecoatmdirect.com`) — once per surface, verify
  the local `:8082` render matches real QA (fonts/layout/model version) before trusting it; use QA
  directly only if a specific surface turns out to misbehave locally.
- **New app is always re-seeded from the exact snapshot the Mendix runtime uses.**

### Setup runbook

1. **Snapshot of record:** restore the Mendix QA dump as local Postgres `qa-0327` (prefer QA over
   prod — no real-PII restore). Record the snapshot datetime. Keep this DB **pristine** (ETL source).
2. **Mendix-owned copy:** `createdb qa-0327_mendix`, restore the same dump into it (Mendix writes
   sessions/locks and may schema-sync — it must not touch the ETL source).
3. **Point Studio Pro at the copy**; confirm the model version matches the dump; boot `:8082`.
4. **Certify:** for one covered page, screenshot `:8082` vs hosted QA and confirm they match
   (fonts, layout, spacing). This validates the stand-in and surfaces model-version drift early.
5. **Seed the new app from the same snapshot:** `python migration_scripts/extract_qa_data.py
   --source-db qa-0327` → wipe DB via `bootstrap.sql` → `mvn spring-boot:run` (Flyway V1–V92).
   Never edit an applied V-file in place (checksum); a re-sync is always wipe + regenerate + fresh
   migrate. Note V39/V77/V81 seeds are outside the generator's scope and refresh separately.
6. **Freeze clocks/state:** new app `AUCTIONS_LIFECYCLE_ENABLED=false`; disable/ignore Mendix
   scheduled events during audits; pin and record an "audit date." Auction pages are
   fixture-driven (scheduling_auctions are not in the ETL) — build shared per-round fixtures.
7. **Log in per side:** new `admin@test.com` (:3000); local Mendix admin from the snapshot's user
   set (:8082); hosted QA creds (CLAUDE.md) for the fallback lane.

### Data-parity gate — schema-aware validation (layer 0)

The two apps hold the same data in **different schemas**; re-running the migration
(wipe → `extract_qa_data.py --source-db <snapshot>` → fresh Flyway migrate) re-establishes data
equality on demand. Every comparison agent/harness run is therefore **schema-aware per side**:
the legacy side queries Mendix `"module$entity"` tables (junction-table FKs, lowercase columns),
the new side queries the normalized schemas (`identity`, `buyer_mgmt`, `pws`, `mdm`, …), and
results are normalized and matched on **business keys**, never PKs (IDs are re-assigned):
`buyer_codes.code`, `device.sku`, `order.order_number`, `account.email`, plus the persisted
`legacy_id` columns (device, price_history, offer, offer_item, order, shipment_detail — note
`pws.rma` has none; its key is `number`).

**The table-by-table map, expected-deltas catalogue, paired validation queries, and the
schema-card briefing template for dispatched agents live in
[`parity/schema-map.md`](parity/schema-map.md)** (derived from `extract_qa_data.py`; regenerate
the map when the script changes). Gate order per comparison run:

1. Re-run migration if data changed → **whole-DB gate**: row-count pack + field-level checksums
   (schema-map §5 Q1/Q2) must pass up to the catalogued expected deltas (dev seed users
   9001–9006, SKIP_TABLES, orphan skips, offer unification).
2. **Per-page `dataChecks`**: each manifest page carries paired legacy/new SQL scoped to what the
   page renders; its pixel/text diff is only actionable once dataChecks pass — if the DB layer
   differs, fix the data before touching CSS.
3. Then DOM-text diff → pixel diff (§3).

Caveat: `auctions.*`, `partial_credit.*`, and `email.*` are **not** generator-derived
(fixture-seeded or greenfield) — their validation contract is fixture-driven, not
snapshot-equality (schema-map §3). QBC validation only holds post-migrate/pre-lifecycle because
the R2/R3 services rewrite that table at runtime.

### URL-mapping manifest (audit worklist now, redirect table later)

One committed manifest maps every legacy surface (`/p/<page>`, `/link/<deeplink>`, `/login.html`)
to its Next.js route, with an `id_strategy` (`none` | `business-key:<field>` | `legacy-id-bridge`).
It serves as **both** the parity worklist **and** the eventual production redirect table (edge
middleware at the legacy hostname; keep in-the-wild email links like `/p/counter-offer` and
reset-password as explicit permanent rules). `frontend/src/proxy.ts` is the insertion point.

---

## 3. The comparison harness — live A/B capture (`tools/parity/`)

### Stack

- **Runner:** existing `@playwright/test` 1.59 — separate config
  `tools/parity/playwright.parity.config.ts` (do **not** extend `frontend/playwright.config.ts`;
  its `webServer` auto-start and 2% diff ratio are for app-vs-self drift, a different contract).
- **Diff + report:** **`reg-cli`** (one devDep) diffing `out/legacy/` vs `out/new/` — built for
  dir-vs-dir A/B; gives the side-by-side HTML report (legacy | new | slider overlay, % mismatch)
  and machine-readable JSON for free. **`pixelmatch` + `pngjs`** in-process for the strict gate
  math and scoreboard. `odiff` is the perf escape hatch if volume demands it. BackstopJS rejected
  (second runner + config dialect); SaaS tools rejected (baseline-model mismatch + QA-screenshot
  egress).

### Capture model

One `browser` → **two `BrowserContext`s with identical options**: legacy context
(`baseURL` = `:8082` or `buy-qa`, Mendix session storageState) and new context
(`baseURL` = `:3000`, JWT storageState per role). For each manifest page × state: navigate both →
normalize both → await readiness on both → run the state's interaction on both (per-side selector
maps — Mendix DOM ≠ new DOM; never target `mx-name-*`) → `screenshot({ mask })` both → diff.

### Determinism checklist (each knob maps to a failure it prevents)

| Knob | How | Prevents |
|---|---|---|
| Pinned Chromium | one Playwright install drives both contexts | version skew |
| Color profile | `--force-color-profile=srgb` | the prior P3-vs-sRGB 0/8 failure |
| Text AA | `--disable-lcd-text`, `--font-render-hinting=none` | subpixel color fringing that shifts with x-offset |
| DPR/viewport | `deviceScaleFactor: 1`, fixed viewport per project | retina resampling; layout-at-width drift |
| Animations | CSS kill-all inject + `reducedMotion` + PW `animations:'disabled'` | mid-animation frames, swap flicker |
| Time | `page.clock.install({ time: FIXED })` both sides, before nav | "3 min ago", countdowns, today-defaulting pickers |
| Fonts | capture-time identical `@font-face` inject (one self-hosted Brandon set) + `document.fonts.check` assert on both | font fallback divergence (new app ships only 3 of 12 legacy Brandon weights — tracked as its own defect, not per-page noise) |
| Waits | `networkidle` → `fonts.ready` → per-page `readySelector` → data-settled probe | pre-hydration/pre-data frames |
| Scrollbars/caret | `--hide-scrollbars` + CSS; `caret-color: transparent` | 15–17px gutter shift; blinking caret |
| Masks | per-page manifest, per-side locators, applied **at capture time** | volatile data/timestamps |
| Full-page | default viewport-clip; `fullPage` only for genuine long scrolls; scroll-prime lazy content | sticky-header duplication, lazy blanks |
| Locale/TZ | `locale: 'en-US'`, `timezoneId: 'America/New_York'` both sides | date/number formatting drift |

### Acceptance policy — what "100% pixel match" means operationally

A page-state is **GREEN** iff:

> mismatched pixels **outside all masks** == 0, **and** every mask has a
> `masks.registry.json` entry (reason enum: `DATA | TIMESTAMP | CDN-IMAGE | NATIVE-CONTROL |
> IRREDUCIBLE-AA | KNOWN-DEFERRED`, plus justification + owner + date), **and** total masked area
> ≤ the page's `maskAreaBudgetPct`.

Threshold forgives only sub-pixel anti-aliasing (pixelmatch ≈ 0.1, AA-ignore) — never a percent
ratio. Unregistered masks fail the page. Net effect: "100%" = **zero unexplained pixels**, an
auditable claim.

### Functional layer ("each function", beyond pixels)

0. **Paired DB `dataChecks`** (schema-aware, layer 0 — see §2 and
   `docs/tasks/parity/schema-map.md`): each side's agent runs the query written for *its* schema,
   results are normalized and matched on business keys. Runs before all UI layers.
1. **Normalized DOM text-diff** of the content region — catches wrong-data-same-layout bugs that
   masks would hide, plus copy drift.
2. **Network field-compare** — capture matched API responses on both sides; compare shape always,
   values where snapshot parity is expected.
3. **Interaction-feel parity** — hover/active/focus captured as extra screenshot states; Tab-walk
   focus-order sequence diff; Esc-closes-modal / Enter-submits end-state equivalence; reuse the
   existing axe helper for a11y-tree extraction.
4. **Journeys** — the same scripted flow (login → navigate → filter → sort → paginate → modal →
   submit) executed on both apps with one input set and per-side locator maps.

### Layout

```
tools/parity/
  playwright.parity.config.ts   # separate PW projects (one per viewport); no webServer
  schema.ts                     # Zod validation of the manifest
  lib/  capture.ts normalize.ts clock.ts fonts.ts masks.ts ready.ts auth.ts
        textdiff.ts netcapture.ts focusorder.ts scoreboard.ts classify.ts
  bin/  capture.mjs (--page <id> | --all)   report.mjs   parity.mjs
  masks.registry.json           # committed exception registry
  auth/  out/                   # gitignored (storageState secrets; PNGs/reports)
```

npm scripts: `parity` (full run) and `parity:page <id>` (single-page loop). Skips cleanly when
either side is unreachable (mirrors `isBackendAvailable()`), so CI-safe as a non-blocking
burn-down report first, tightening to per-page blocking gates as pages go green.

---

## 4. Coverage model & operating process

### Manifest = tracker = harness config

`docs/tasks/parity/` with `roles.yaml` (role reconciliation), `config.yaml` (bases, breakpoints,
thresholds, global masks), and **one YAML per logical page** in `pages/*.yaml` (~55–65 files after
grouping list+detail). One file per page avoids merge conflicts and matches the small-files rule.
Key fields (full schema in the process workstream output, to be committed as
`docs/tasks/parity/schema.md`):

- `pageId, title, phase, surface (buyer|admin|auth|shell), module`
- `legacyUrl | legacy deep-link | scripted nav journey` + `newUrl` + `routeParams.fixture`
  (mandatory for `[id]` routes)
- `legacyEquivalence: same-page | modal-vs-route | merged | net-new-local | removed-from-legacy`
  — names the architectural-divergence class the audits kept finding
- `roles[]` + `ownerRole`; `breakpoints[]`; `states[]` (id, setup steps per side, waitFor)
- `masks[]` **paired with** `dynamicData[]` (masked for pixels ⇒ still format/copy-asserted —
  e.g. `MM/DD/YY at hh:mm A z`; money is always `$#,##0.00` per the 2026-07-11 currency ADR)
- `dataChecks[]` — paired legacy/new SQL scoped to what the page renders (`legacySql`, `newSql`,
  `key`, `expect: identical | subset | catalogued-delta:<ref>`); pattern in schema-map §5
- `deepLinks[]`, `emails[]`, `downloads[]` (structural compare: sheets, column set+order, header
  labels, formats — not byte-for-byte)
- `status: not-started | diffing | green | signed-off`, `dod{12 gates}`, `discrepancies[]`,
  `acceptedDivergences[]` (each ADR-backed), `owner`, `signedOffBy`

### Role rule (contains the matrix explosion)

- `ownerRole` gets the **full states × breakpoints matrix**.
- Every other role in `roles[]` gets a **frame-only** capture (default state, 1920) — proves nav
  frame + reachability, not a full re-diff.
- Roles **not** in `roles[]` get a **negative check** — server-side 403/redirect (client gates are
  UX-only per security rules).
- Prerequisite: reconcile runtime roles (Administrator, Co-Admin, SalesOps, SalesRep, Bidder,
  ecoAtmDirectAdmin) against the legacy `.mmd` flow roles (administrator, salesops, salesrep,
  bidder, compliance, salesleader, anonymous) in `roles.yaml` — they do **not** line up 1:1.

### States per page archetype

List/DataGrid (default, loading, populated, empty-result, filtered numeric+text, sorted asc/desc,
paginated, column-selector, row-hover, error), Detail (populated, 404, tabs), Form (pristine,
valid, per-rule validation, in-flight, success, server-error, dirty-guard), Wizard (per-step +
back-retains-state), Modal-host (open, validation, submit, cancel, focus-trap), Dashboard
(pre/active/post/terminal), Auth, Shell-per-role.

### Breakpoints — desktop-first, evidence-based

Admin/shell/auth: **1920 (sign-off), 1440, 1366**. Buyer surfaces: same three, **plus 768/390 only
where a one-time probe proves the legacy page actually reflows** (existing audits were explicitly
desktop-only; capturing mobile where legacy never reflowed generates noise — and "improving" it is
itself a tell).

### Definition of done — 12 gates per page

1 pixel (zero unexplained) · 2 copy/labels (incl. no PK leakage — the `#73` class of defect) ·
3 number/date formats (currency convention decided once, app-wide) · 4 interaction outcomes (modal
stays modal unless ADR'd) · 5 validation text/timing/placement · 6 focus/keyboard (tab order, Esc,
Enter, focus-trap) · 7 load-feel (≤ +500ms perceived on identical fixtures) · 8 URL/deep-link
redirects · 9 flow emails (subject/body/recipients/trigger) · 10 downloads (structural xlsx
equivalence) · 11 role access (positive + negative, server-enforced) · 12 zero unowned divergences
(every residual delta fixed or ADR'd).

### Findings ledger

**Every finding is persisted in [`parity/findings.md`](parity/findings.md)** (id, layer,
severity, classification, expected-vs-actual, evidence path, status) with durable screenshot
evidence under `parity/evidence/`. Implementation tasks are built by lifting register entries
(grouped per page or per root cause); once per-page manifests exist, their `discrepancies[]`
reference register ids and the register stays the roll-up.

### Pipeline, classification, burn-down

`not-started → diffing → green → signed-off` (named reviewer; regression auto-reverts to diffing
if a later change breaks the harness result). Every diff classified and routed:
**style-fix** (frontend, batchable) · **data-fix** (backend; follows backend DoD) ·
**feature-gap** (graduates to its own planned task; blocks sign-off, not style-green) ·
**accepted** (ADR entry; harness auto-passes). CRITICAL/HIGH block sign-off. Weekly generated
burn-down (`docs/tasks/parity/reports/`): surface-weighted signed-off % (buyer pages ×2) + open
CRITICAL/HIGH count are the two numbers that matter.

### Page phase order

- **Phase 0 — shell/auth/nav** (login, forgot/reset, buyer-select, per-role shell): frames every
  other screenshot; known shell-level defects (broken sidebar link, missing menu items) would
  poison every page's diff. `roles.yaml` reconciliation happens here.
- **Phase 1 — buyer core:** `bidder/dashboard` (clear BD-24 first), PWS spine
  (`pws/inventory → pricing → cart → order → orders(/[id])`). External customers judge here.
- **Phase 2 — buyer secondary:** offer-review, counter-offers, RMA, partial-credit wizard.
- **Phase 3 — admin core:** auctions data center + reserve-bids + purchase-orders (walkthroughs
  already banked — fastest to green).
- **Phase 4 — admin long-tail:** control centers, settings, users/buyers, MDM. Many are
  `net-new-local` (no legacy equivalent → DoD is internal consistency, not a diff).

---

## 5. Harness build plan (effort)

| Phase | Scope | Effort |
|---|---|---|
| **H0 — prove the thesis** | Scaffold `tools/parity/`; auth fixtures both sides; normalize+clock+fonts inject; hard-code **login**; reg-cli report. **DONE 2026-07-11 — thesis PROVEN:** `login.html` rendered **pixel-identical** between local Mendix `:8082` and hosted QA in one pinned Chromium (zero environment noise — the 0/8 failure class is dead). Buyer login identical except the Studio Pro demo-user floating widget on the local side (mask or disable demo users). Parity diffs vs the new `/login` show real findings: card geometry/vertical offset, heading font (Brandon fallback), missing Privacy-Policy/© footer. Notes: `reg-cli` pinned to **0.17.7** (the 6.x WASM build panics on Windows); specs run via `cd frontend && NODE_PATH="$(pwd)/node_modules" npx playwright test -c ../tools/parity/playwright.parity.config.ts`; used `clock.setFixedTime` not `clock.install` (install pauses timers and can stall the Mendix client boot) | 0.5–1 d |
| **H1 — manifest + engine** | Zod schema; two-context driver; per-side masks; ready signals; deep-link ∪ journey nav; seed manifest from the 10 QA-reference surfaces | 1–2 d |
| **H2 — diff/report/scoreboard** | reg-cli wiring; scoreboard + history; masks registry + area budget; classify; `--page`/`--all` | 1 d |
| **H3 — functional layer** | text-diff, network field-compare, focus-order + hover/focus/active states | 2–3 d |
| **H4 — scale-out** | all ~77 routes; 1440/1920 (+ probed mobile) projects; CI wiring | ongoing |

H0 is the go/no-go: login has no backing-data problem, so if live same-Chromium A/B doesn't
collapse to ~zero diff there, stop and rethink before scaling. It directly retires the 0/8 blocker.

---

## 6. Open decisions & risks

| # | Item | Owner/action |
|---|---|---|
| 1 | ~~Does the local Mendix project cover the full legacy app?~~ **RESOLVED 2026-07-11: user confirms local Mendix serves both PWS and buyer surfaces.** Remaining: verify the model version matches the chosen dump (runbook steps 3–4 certification) | Certify `:8082` vs hosted QA once per surface |
| 2 | ~~Currency convention~~ **RESOLVED 2026-07-11: 2 decimals app-wide (`$#,##0.00`)** — ADR in `docs/architecture/decisions.md`; legacy pages provably rendering integer dollars get escalated per the working agreement | Done |
| 3 | **Role reconciliation** (runtime ↔ `.mmd` flows) | Phase 0, `roles.yaml` |
| 4 | **Mask discipline** — under-masking = all false positives; over-masking hides drift | Every mask needs a registry entry + a paired `dynamicData` format assert; mask-area budget |
| 5 | **Auction clock/state** — rounds advance by wall clock; scheduling_auctions aren't snapshot-aligned | Freeze schedulers; shared per-round fixtures; pinned audit date |
| 6 | **Hygiene:** `migration_scripts/extract_qa_data.py` hardcodes the source-DB password; untracked `Brandon_Grotesque.css` / `computed-styles.txt` / dom-tree dumps at repo root | Move cred to env; file or delete the strays |
| 7 | **Font gap:** new app ships 3 of 12 Brandon weights + different fallback stack | Ship the full legacy set (files in `migration_context/styling/`); tracked as its own defect line |

## 7. Immediate next steps

1. ~~Runbook steps 1–5~~ **DONE 2026-07-11:** `qa-0327_mendix` cloned; new app re-seeded from the
   same snapshot; whole-DB data gate 18/18 PASS (see R-6). Remaining from the runbook: the
   user-driven Studio Pro step (R-3) + scheduled-events-off (R-5), then the one-page
   certification of `:8082` vs hosted QA.
2. ~~H0 proof~~ **DONE 2026-07-11 — GO.** Zero-noise A/B proven (see §5 H0 row); certification of
   `:8082` vs hosted QA passed (one dev-widget artifact). Artifacts:
   `tools/parity/out/report-cert.html` + `report-parity.html`.
3. **H1 — BUILT 2026-07-12, new side fully validated; legacy half blocked on `:8082` being
   restarted.** Engine: `tools/parity/{schema.ts, lib/{manifest,context,auth,capture}.ts,
   specs/run.spec.ts, bin/run.mjs}` — manifest-driven (Zod-validated
   `docs/tasks/parity/{config,roles,pages/*}.yaml`), per-side auth → cached storageState
   (hydration-safe fill on the Next login — values filled pre-hydration get wiped by React,
   fixed with settle+verify+retry), per-side masks at capture time, legacy-local-only hide-CSS
   for the ENV-1 dev widget, journeys via sidebar text labels, reg-cli diff + scoreboard JSON.
   Run: `node tools/parity/bin/run.mjs [--page <id>] [--skip-capture]`.
   Seeded manifests: auth-login, admin-reserve-bids-list, admin-purchase-orders-list,
   bidder-dashboard (skipped pending a legacy account with an AUCTION code — roles.yaml).
   Validated end-to-end on the new side (admin login + both admin grids captured, storageState
   reuse confirmed). Untested until `:8082` returns: Mendix admin login selectors + the two
   sidebar journeys. Then scaffold remaining page YAMLs + `schema.md`.
4. Optional legacy-side hygiene: disable Studio Pro demo users (removes the floating dev widget
   from every legacy-local capture) — else it becomes a standing legacy-side mask.

## 8. Prerequisites checklist — what we need before starting (added 2026-07-11)

Working agreement: **when blocked during testing, ASK the user — never assume, never silently
drop the item.** The user can operate/adjust the legacy Mendix app (Studio Pro, accounts, config)
to unblock. User has confirmed local Mendix serves both PWS and buyer surfaces.

**Legacy side (user provides / confirms)**
- [x] R-1 **RESOLVED 2026-07-11:** project = `Auctions UI-Release10`
      (`C:\Users\Ashirwad.Mittal\Mendix\Auctions UI-Release10`) — user confirms it is the full
      app (PWS + buyer + admin surfaces)
- [x] R-2 **RESOLVED + VERIFIED 2026-07-11:** snapshot of record = existing local `qa-0327`.
      Verified present with key counts: 653 buyer codes, 486 users, 22,476 devices,
      1,168 + 417 = 1,585 offers (matches V22's unified count), 353 RMAs. Copies to make:
      pristine `qa-0327` (ETL source, untouched) + `qa-0327_mendix` (Mendix runtime DB).
      Note: fresher snapshots exist locally (`qa-0625` newest) if we ever want to re-baseline —
      same runbook applies
- [ ] R-3 Studio Pro pointed at `qa-0327_mendix`; boots at `:8082`; **no destructive model/schema
      sync prompt** (if Studio Pro insists on syncing, we stop and ask)
- [x] R-4 **RESOLVED 2026-07-11 (verify at boot):** use the QA accounts shipped inside the dump —
      `ashirwadmittal` (admin) + `nadia.ecoatm@gmail.com` (buyer) with the known QA passwords;
      fall back to a password-hash reset in the `_mendix` copy only if they turn out SSO-only
- [ ] R-5 Mendix scheduled events (round transitions) disabled/ignored during audit sessions

**New app side**
- [x] R-6 **EXECUTED 2026-07-11:** `salesplatform_dev` wiped + re-seeded from `qa-0327`; fresh
      chain V1–V92 + `R__apply_triggers` applied; whole-DB gate **18/18 PASS** (schema-map §5).
      Provenance check first proved committed V16–V24 byte-identical to a qa-0327 regeneration.
      Three fresh-chain defects found + fixed en route (uncommitted, pending review):
      **V34** regenerated — old file had user FKs shifted by one (wrong submitted-by/reviewed-by
      on every RMA); **V33** now drops V29's placeholder rma_status/rma_reason (shape collision
      broke the seed); **R__apply_triggers** no longer references the V92-renamed email tables.
      `qa-0327_mendix` clone created for the Mendix runtime (pristine `qa-0327` untouched)
- [ ] R-7 Backend `:8080` + frontend `:3000` running; `AUCTIONS_LIFECYCLE_ENABLED=false` during
      captures. ⚠ Confirmed a boot-time writer exists (the SPB e2e device seeder touched 5 rows
      at startup) — capture sessions must restart the backend with schedulers frozen and re-run
      the Q1 gate afterwards to prove the data didn't move
- [ ] R-8 Postgres `:5432` up with both DBs reachable

**Harness/tooling**
- [ ] R-9 One new devDep: `reg-cli` (+ optional `pixelmatch`/`pngjs`) — npm install approval
- [ ] R-10 Playwright 1.59 Chromium present (already in repo); creds materialized as gitignored
      storageState under `tools/parity/auth/`
- [ ] R-11 Full Brandon Grotesque set from `migration_context/styling/` for capture-time font
      injection

**Decisions (user)**
- [x] R-12 **RESOLVED 2026-07-11:** `qa-0327` (see R-2)
- [x] R-13 **RESOLVED 2026-07-11:** currency = **2 decimals app-wide** (`$#,##0.00`) — ADR in
      `docs/architecture/decisions.md` (2026-07-11 entry)
- [ ] R-14 Role set to compare + runtime↔legacy role reconciliation sign-off (`roles.yaml`)
- [ ] R-15 Viewport policy sign-off (desktop-first 1920/1440/1366; mobile only where legacy
      actually reflows)

---

**Key references:** `docs/TODO/pixel-compare-strategy-plan.md` (prior failure post-mortem) ·
`docs/qa-reference/README.md` · `docs/tasks/qa-vs-local-page-audit-playbook.md` ·
`migration_context/frontend/*_flow_*.mmd` (legacy per-role page inventory) ·
`migration_context/styling/EcoAtm.css` + Brandon OTFs · `migration_scripts/extract_qa_data.py` ·
`frontend/playwright.config.ts` + `frontend/tests/e2e/_helpers/backend.ts` (conventions to mirror).
