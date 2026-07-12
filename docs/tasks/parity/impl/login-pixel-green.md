# Login page → pixel-green (LOGIN-P1 / P2 / P3)

Implementation record for parity task #1 (`docs/tasks/parity/findings.md`
→ "Proposed implementation task #1"). Drives the new `/login` page to
pixel-parity with the legacy Mendix buyer login (`/p/login/web` ·
`EcoATM_UserManagement.Login_New`).

**Status: pass-2 converged — every element box matches the harness legacy
capture to ±1px** (see § Pass 2 at the bottom for the per-element
before/after table). Residual strict-diff is 0.63% of the frame, all
cross-rasterizer glyph antialiasing. Final `reg-cli` sign-off belongs to
the orchestrator's harness (same pinned Chromium both sides).

Fix order per the task: **P2 (fonts, cross-cutting) → P1 (geometry) → P3
(footer)**.

---

## 0. Where the legacy truth came from

The buyer login is a Mendix SPA route (`/#/login/web`) — `curl` returns
only the empty shell, so values were pulled from three authoritative
sources (the Playwright MCP browser was locked by parallel agents the
whole session, so live `getComputedStyle` wasn't available — everything
below is from static sources + a pixel-sample of the H0 capture, then
cross-checked against a headless render of the finished page):

1. **Live compiled theme** — `curl http://localhost:8082/theme.compiled.css`
   → the real `.newlogincard` / `.mainheadertext` / `.loginbutton` /
   `.policy-footer` / `.policy-text` / `.copyright-text` rules.
2. **Live font CSS** — `curl .../resources/fonts/Brandon_Grotesque.css`
   and `.../FoundersGrotesk/founders-grotesk-font.css` → exact
   file→weight/style mappings.
3. **Compiled page model** — `Auctions UI-Release10/deployment/web/pages/
   en_US/EcoATM_UserManagement/Login_New.page.xml` → the Privacy Policy
   `openLink` href, the copyright expression, the widget tree / grid
   split, and the exact caption strings. (The redesigned buyer login is
   newer than the `ai_knowledge_base_Release10` snapshot — it is **not**
   in the KB; the running app's deployment folder is.)
4. **Pixel sample** of `evidence/h0-2026-07-11/legacy-local-login-buyer.png`
   (PIL) → the heading/footer/gradient colours + the card edge coords.

---

## 1. LOGIN-P2 — fonts  ⚠️ finding premise was inverted

**Finding said:** heading should be **Brandon Grotesque**; the new app
falls back because it ships only 3 Brandon weights.

**Ground truth (theme.compiled.css):** the entire legacy buyer-login card
+ footer is **Founders Grotesk**, *not* Brandon. The new app was the one
rendering the heading in Brandon (via `--font-family-primary`), which is
why it looked wrong. Verbatim legacy rules:

```
.newlogincard   { font-family: Founders Grotesk; ... }
.mainheadertext { font-family: Founders Grotesk; font-weight: 500;
                  font-size: 30px; line-height: 100%; }
.logininput     { font-family: Founders Grotesk; ... }
.loginbutton    { font-family: Founders Grotesk; font-weight: 500; ... }
.policy-footer  { font-family: Founders Grotesk; }
```

So the pixel-correct heading fix is **Founders Grotesk 500 / 30px**, and
the whole card + footer moves to Founders. Founders `Medium` (500) already
ships in the new app (`public/fonts/FoundersGrotesk/FoundersGrotesk-Medium.otf`),
so no new Founders file was needed.

### What changed
- Added a `--font-family-pws` token (Founders-first, the exact legacy PWS
  stack) and pointed the whole login card + footer at it.
- **Still shipped the full Brandon set** (the finding's cross-cutting ask,
  and it is the correct Atlas `$font-family-base` for the rest of the app):
  copied all 12 Brandon OTFs into `public/fonts/brandon-grotesque/` and
  rewrote the `@font-face` block to mirror the legacy
  `Brandon_Grotesque.css` mapping exactly.
- Aligned `--font-family-primary` to legacy's declared base
  (`'Brandon Grotesque', 'Open Sans', sans-serif`; dropped the stray `Arial`).

### Brandon Grotesque font table (verbatim from legacy `Brandon_Grotesque.css`)

| File (public/fonts/brandon-grotesque/) | `font-weight` | `font-style` | In legacy CSS? |
|---|---|---|---|
| `Brandon_thin.otf`     | 100 | normal | yes |
| `Brandon_thin_it.otf`  | 100 | italic | yes |
| `Brandon_light.otf`    | **200** | normal | yes (legacy maps light→200, not 300) |
| `Brandon_light_it.otf` | **200** | italic | yes |
| `Brandon_reg.otf`      | 400 | normal | yes |
| `Brandon_reg_it.otf`   | 400 | italic | yes |
| `Brandon_med.otf`      | 500 | normal | yes |
| `Brandon_med_it.otf`   | 500 | italic | yes |
| `Brandon_bld.otf`      | 700 | normal | yes |
| `Brandon_bld_it.otf`   | 700 | italic | yes |
| `Brandon_blk.otf`      | 900 | normal | **no** — file present, not wired by legacy; exposed at 900 for completeness |
| `Brandon_blk_it.otf`   | 900 | italic | **no** — same |

Founders Grotesk (already present in the new app; 400 + 500 cover the login):
`FoundersGrotesk-Regular.otf`→400/normal, `FoundersGrotesk-Medium.otf`→500/normal.

---

## 2. LOGIN-P1 — card geometry (all values verbatim from `theme.compiled.css`)

| Property | Legacy value | Source class |
|---|---|---|
| Card width | `900px` | `.newlogincard` |
| Card height | `609px` (fixed, was `min-height`) | `.newlogincard` |
| Card border-radius | `22px` | `.newlogincard` |
| Card background | `#EFEBE4` | `.newlogincard` |
| Card margin | `100px` top / `10px` bottom | `.newlogincard` |
| Card box-shadow | **none** (new app had `--shadow-card`; removed) | `.newlogincard` |
| Card top edge @1080p | **124px** (container `padding-top:24px` + card `margin-top:100px`) | measured |
| Photo\|form split | `col-lg-6 / col-lg-6` = **50 / 50** (each 450px) | page model |
| Page background | `linear-gradient(180deg, #417974 0%, #102E33 100%)` | `.newauctiongradient` |
| Heading font/size/weight | Founders Grotesk / `30px` / `500`, line-height `100%` | `.mainheadertext` |
| Heading colour | `#3C3C3C` (pixel-sampled) | inherited Atlas `$font-color-header` |
| Heading→first-input gap | `24px` (`spacing-outer-top-large` @≥992px) | page model |
| Input width / radius / border | `420px` / `5px` / `1px #534F4C` | `.logininput`, `.mx-textbox-input` |
| Input font / placeholder | Founders `18px` / placeholder `#1C1B1C` / focus `2px #407874` | `.logininput` |
| Login/Contact button | `250×45`, radius `32.99px`, bg `#102E33`, text `#F0EBE3`, Founders 500 18px | `.loginbutton` |
| Employee button | `250×40`, Founders 500 18px, `#407874`, cream bg | `.loginbuttonemployee` |
| Remember-me text | Founders 400, `16px`, `#1C1B1C` | `.remember-me-text` |
| Forgot Password | Founders 500, `17px`, `#407874` | `.forgotpassword` |
| Contact divider | `border-top: 1px solid #A8A7A6`, 20px above | `.contact-us-container` |
| Sub-header | Founders 500, `20px`, line-height `33px` | `.subheadertext` |

**Root cause of the old displacement:** the new `.container` used
`align-items/justify-content: center` (card vertically centred → top ≈220px)
and both panes used `flex: 1` (the form pane's larger min-content width
squeezed the photo pane narrower than 50%). Fix: `.container` is now a
top-anchored flex **column** (`padding-top:24px`), and each pane is
`flex: 0 0 50%; min-width: 0`.

---

## 3. LOGIN-P3 — footer

Legacy footer is a **sibling below** the card (900px, on the gradient) —
the `AuctionUI.Contact_CW` snippet, `.policy-footer`.

| Element | Legacy | Source |
|---|---|---|
| Row | `.policy-footer` — `width:900px; margin-top:20px;` Founders Grotesk, Privacy left / copyright right | `theme.compiled.css` |
| Privacy link | caption **"Privacy Policy"**, `openLink` web `https://www.ecoatm.com/pages/privacy-policy`; `.policy-text` = `#FBFAF8`, weight 400, 18px, **underline** | `Login_New.page.xml` (`AuctionUI.Contact_CW.actionButton1`) |
| Copyright | `.copyright-text` = `#FBFAF8`, 400, 18px, right-aligned | `theme.compiled.css` |
| Copyright text | **`"© " + formatDateTime(currentDateTime, "yyyy") + " ecoATM, LLC. All Rights Reserved."`** → **dynamic current year** | `Login_New.page.xml` (`AuctionUI.Contact_CW.text2`) |

Mirrored with `© {new Date().getFullYear()} ecoATM, LLC. All Rights
Reserved.` inside the client `LoginForm` (so it is the browser's current
year, exactly like the legacy client-side `formatDateTime`). Rendered
output at capture time: **"© 2026 ecoATM, LLC. All Rights Reserved."**

---

## 4. Bonus parity fixes found while in here (documented, low-risk)

- **Contact Us was a dead button** ("Contact URL TBD"). Legacy is an
  `openLink` to `https://www.ecoatmb2b.com/wholesale-devices#contact-us`
  (from the page model). Wired it (opens in a new tab).
- **Gradient** was a brand-token approximation (`#407874 → #112d32`);
  set to the exact legacy `#417974 → #102E33` / `180deg`.
- **Sub-header apostrophe:** legacy uses a curly `’` (`don’t`); switched
  `&apos;` → `&rsquo;` to match the exact glyph.
- **Card drop-shadow removed** — the legacy `.newlogincard` declares none.

---

## 5. Files changed (all under this worktree's `frontend/`)

| File | Change |
|---|---|
| `public/fonts/brandon-grotesque/*.otf` | +10 OTFs (now the full 12-file set) |
| `src/app/globals.css` | full Brandon `@font-face` set (12 faces, legacy mapping); `--font-family-primary` fallback aligned; new `--font-family-pws` (Founders) token |
| `src/app/(auth)/login/login.module.css` | rewrite: top-anchored container + exact gradient, 900×609 card / 50-50 split / no shadow, Founders throughout, exact input/button/heading metrics, new `.policyFooter` / `.policyText` / `.copyrightText` |
| `src/app/(auth)/login/LoginForm.tsx` | returns card **+ footer** (fragment); footer = Privacy Policy link (real href, `target="_blank"`) + dynamic-year copyright; Contact Us wired; curly apostrophe |
| `tests/e2e/wholesale-buyer-login.spec.ts` | +1 test asserting the Privacy Policy href + current-year copyright |

`src/app/(auth)/login/page.tsx` — unchanged (already wraps `.container`).

---

## 6. Verification

- **`npx tsc --noEmit`:** clean for every touched file. (31 total errors
  remain in the repo, all in two E2E specs I never touched —
  `admin-purchase-orders.spec.ts`, `wholesale-submit-bids.spec.ts` — i.e.
  pre-existing/unrelated.)
- **Headless render** (my worktree dev server on a throwaway `:13000`, so
  the main-worktree `:3000` is untouched; standalone `@playwright/test`
  chromium, not a CDP attach) — `getBoundingClientRect` / `getComputedStyle`
  vs legacy:

  | Metric | Legacy (measured) | New (measured) |
  |---|---|---|
  | Card box | x=510 y=124 w=900 h=609 (→1409,733) | **x=510 y=124 w=900 h=609 (→1410,733)** |
  | Split (form pane left) | ≈960 | **960** (panes 450/450) |
  | Footer | y≈763, x 510→1410, 900px | **y=763, x 510→1410, 900px** |
  | Heading font | Founders Grotesk | **Founders Grotesk 500 / 30px / rgb(60,60,60)=#3C3C3C** |
  | Copyright | © {year} … | **© 2026 ecoATM, LLC. All Rights Reserved.** |
  | Privacy href | ecoatm.com/pages/privacy-policy | **exact match, target=_blank** |

  Screenshot saved (session temp) — visually indistinguishable from
  `legacy-local-login-buyer.png` (card higher, 50/50 split, Founders
  heading, white underlined Privacy Policy + right-aligned copyright).
- **E2E spec** (`wholesale-buyer-login.spec.ts`) not executed here: the
  Playwright config auto-starts `npm run dev` on `:3000`, which is the
  **main** worktree's server (off-limits). The added footer assertions
  are written to pass against this build; the orchestrator's harness run
  is the sign-off gate.

---

## 7. Ambiguities / assumptions (explicit — nothing guessed silently)

1. **Finding LOGIN-P2 is inverted.** Legacy heading is **Founders
   Grotesk 500**, not Brandon. Implemented the pixel-correct Founders fix
   *and* still shipped the full Brandon set (the finding's cross-cutting
   ask). Flag for the register: LOGIN-P2's "expected = Brandon" text is
   wrong; the delta was new-app-Brandon vs legacy-Founders.
2. **Card top = 124px** decomposed as container `padding-top:24px` + card
   `margin-top:100px`. The `margin-top:100px` is exact-legacy; the 24px is
   the inferred Mendix layout-region offset that reproduces the measured
   124px top (confirmed by the headless render landing at exactly 124).
3. **Brandon Black (900)** — the two `Brandon_blk*` OTFs ship in the set
   but legacy's own `Brandon_Grotesque.css` does **not** wire them; exposed
   at 900 for completeness. `light` is mapped to **200** (not the usual
   300) to match legacy exactly.
4. **Privacy Policy opens in a new tab** (`target="_blank" rel="noopener
   noreferrer"`). Mendix `openLink` (web) target wasn't recoverable from
   the model; new-tab is the safer/conventional choice for an external
   policy link and keeps the login page alive. One-attribute change if the
   orchestrator wants same-tab.
5. **Copyright year is dynamic** (`new Date().getFullYear()`), mirroring
   the legacy `formatDateTime(currentDateTime,"yyyy")` expression — not a
   hardcoded 2026. (Note: the ecoATM *email* templates hardcode a stale
   "Copyright 2025"; the login footer is the dynamic one.)
6. **Card drop-shadow removed** to match the `.newlogincard` class (no
   shadow declared). If the strict diff shows the legacy has a faint
   shadow from some Mendix default, it is a one-line re-add.
7. **Internal vertical gaps** (field-to-field, button spacing) use the
   Atlas desktop spacing values read from `theme.compiled.css`
   (`-large`=24px, `-medium`=13px). Any sub-pixel residue here is what the
   orchestrator's strict `reg-cli` pass will surface, if anything.

---

# Pass 2 (2026-07-12) — per-element convergence after the first harness re-run

The first harness re-run (post-merge 46161e3c) showed the card frame /
50-50 split / footer presence aligned but element-level ghosting inside
the form pane, the footer, and the photo. Pass 2 measured every element
off the harness PNG pair with a PIL band profiler
(`tools`: scratchpad `measure_login.py` — row-profile of non-background
ink inside the form pane + footer white-text clusters + photo-pane
shift correlation), fixed root causes, and re-rendered headlessly on a
throwaway `:13000` until every band matched.

## Root cause #1 — fonts NEVER loaded anywhere (the real LOGIN-P2)

`document.fonts` diagnostics on the rendered page showed every custom
FontFace in `status: error` and all `/fonts/**` requests answering
**HTTP 307**. `src/proxy.ts`'s auth matcher excluded `images/`, `qa_*`,
`favicon.ico` — but **not `fonts/`** — so on any unauthenticated page
(the login page itself, every harness capture) each @font-face fetch
redirected to `/login`, failed decode, and the page fell back to
**Arial** (4th in the stack — that is what "thinner fallback face" in
the original finding actually was; it was never a missing-weight issue).
Fix: `fonts/` added to the matcher exclusion (public static assets,
exactly parallel to `images/`). One line; cross-cutting — every
unauthenticated surface now renders the branded fonts.

With fonts loading, canvas metrics confirmed identity with legacy:
"Premium Wholesale &" @ Founders 500 30px = 271.9px (legacy ink 271);
"© 2026 …Reserved." @ Founders 400 18px = 303.4px (legacy ink 303).

## Root cause #2 — logo rendered at 180×60 (natural is 200×40)

The legacy asset is byte-identical (md5) to `public/qa_logo.png`,
natural 200×40; legacy renders it at natural size (glyph box 195×34 @
y174). The component passed `width={180} height={60}` → scaled 176×31.
Fixed to 200×40 → glyph box lands exactly at legacy's 1087..1281 ×
174..207.

## Root cause #3 — measured-vs-theme geometry deltas

| Item | Legacy (measured off harness PNG) | Was | Fix |
|---|---|---|---|
| Input border box | **390px** wide (x 990..1379) — the 420px `.logininput` formgroup carries 15px side padding | 420 | `.formGroup`/`.actions` width 390 |
| Input radius | ~3px (Atlas `$border-radius-default` on the `.form-control`; the 5px sits on the invisible formgroup) | 5px | 3px |
| Password box height | **43px** (legacy fractional layout rounds it 1px shorter than the 44px email box — bottom border rows 416..417) | 44 | `.inputWithToggle` height 43px (+1 compensation in `.actions` margin-top) |
| Buttons' x | pills at **1063..1312** = pane center +3px (Mendix `.btn` side margin) | 1060..1309 | `margin-left: 6px` on `.loginbutton`/`.loginbuttonemployee` |
| Remember row | checkbox top = password border +3px; Login top = password border +48px; row is 22px (forgot's 22px line-height dominates); label line-height 100% | −12/13 margins, label lh ~1.5 | `.actions` margin `-13px/11px`; `.checkboxLabel` line-height 1 |
| Checkbox | **20×20, 1px #898787 border, 3px radius, transparent interior** (pixel-sampled) | native 16px white | appearance:none custom box (+ a checked style, not in capture) |
| Employee label y | glyph top = button top + 12 (padding-top 9 + 18px line-height-1 cap inset) | flex-center, default lh | `align-items:flex-start; padding-top:9px; line-height:1` |
| Divider span | full 450px pane (x 960..1410) | 420 (formBody width) | formBody → width:100%, rows self-size at 390 |
| Contact gap | subheader 5px bottom margin COLLAPSES with contact's 13px top margin in legacy block flow (net 13) | 5+13=18 in flex | subheader margin-bottom 0 |
| Eye icon | legacy `font_awesome_icon_2.svg` (23×20, fill #7D7B7A), same asset for BOTH toggle states, at top 11px / right edge 10px inside the input border | hand-drawn 20px SVG, centered | exact legacy path inlined, `.passwordToggle` top:11px right:10px |
| Photo | top-anchored width-fit (747×1013 → 450×610.2, ~1px bottom clip) | cover centered (dy −1 ghost) | `object-position: top` |

## Per-element before/after (harness legacy PNG vs headless render)

All coordinates at 1920×1080; "before" = first harness re-run capture.

| Element | Legacy | Before (pass 1) | After (pass 2) |
|---|---|---|---|
| Logo glyphs | y174..207 x1087..1281 | y167..197 x1097..1272 | **y174..207 x1087..1281** ✓ |
| Heading L1 | y238..256 x1050..1320 | y223..244 x1038..1332 | **y238..256 x1050..1320** ✓ |
| Heading L2 | y268..291 x1081..1288 | y253..280 x1075..1293 | **y268..291 x1081..1288** ✓ |
| Email box | y315..358 x990..1379 | y304..347 x975..1394 | **y315..358 x990..1379** ✓ |
| Password box | y375..417 x990..1379 | y364..407 x975..1394 | **y375..417 x990..1379** ✓ |
| Eye icon band | y387..404 | y390..405 (own SVG) | **y387..404** ✓ (exact legacy SVG) |
| Checkbox | y422..423 x990..1009, #898787/3px/transparent | white native 16px | **identical box + style** ✓ |
| Remember/Forgot | y428..441 | y421..436 | y427..441 (±1 glyph AA) |
| Login pill | y467..511 x1063..1312 | y468..512 x1060..1309 | **y467..511 x1063..1312** ✓ |
| Employee label | y537..551 x1129..1246 | y538..554 x1122..1248 | **y537..551 x1129..1246** ✓ |
| Divider | y590 x≥965..1404 | y591 x975..1394 | **y590 x≥965..1404** ✓ |
| "Interested…" | y617..629 x1030..1339 | y615..629 x1019..1351 | y617..629 x1030..1340 (±1) |
| Contact pill | y652..696 x1063..1312 | y658..702 x1060..1309 | **y652..696 x1063..1312** ✓ |
| Footer texts | y766..780; privacy x510..608; © x1107..1409 | y765..781; x510..621; x1066..1407 | y766..780; **x510..608** ✓; © x1108..1408 (±1) |
| Photo shift | — | dy=1 ghost (contour) | **dy=0** ✓ |

Strict masked pixel diff (tol 16/channel, ENV-1 demo-user region masked):
13,164 px = **0.63%** of the frame — thin hollow glyph outlines and photo
resampling speckle only, i.e. rasterizer-level AA between my local
Chromium and the harness's pinned build. Zero solid/doubled ghosts
remain; the harness's same-Chromium-both-sides compare is expected to
land far lower.

## Files changed in pass 2

| File | Change |
|---|---|
| `src/proxy.ts` | `fonts/` added to the matcher exclusions (root cause #1) |
| `src/app/(auth)/login/LoginForm.tsx` | logo 200×40; both hand-drawn eye SVGs replaced by the exact legacy `font_awesome_icon_2.svg` path (single icon, both states — matching legacy) |
| `src/app/(auth)/login/login.module.css` | all root-cause-#3 rows above |
| `src/app/globals.css` | Founders family completed with the legacy-served Light(200)/RegularItalic(400i)/Bold(700) faces |
| `public/fonts/FoundersGrotesk/` | +3 OTFs (Bold/Light/RegularItalic — md5-identical to the files :8082 serves) |

## Pass-2 ambiguities (explicit)

1. The `.actions` margins (−13/11) and the +6px button `margin-left` are
   **empirical** — the rendered legacy page is the spec; plain Atlas
   spacing-class arithmetic does not reproduce it (Mendix widget default
   margins account for the residue).
2. The password input is deliberately 1px shorter (43px) than the email
   input — mirroring legacy's own fractional-layout rounding, not a
   design token.
3. The checkbox **checked** state is not in the capture; unchecked is
   pixel-matched, checked got a sensible midnight-green fill + check.
4. `src/proxy.ts` also lacks a `reset-password` exclusion (unauthenticated
   reset links bounce to /login). Out of scope here — flagged for the
   auth owner; not changed.

---

# Pass 3 (2026-07-12) — control-level convergence (final)

Pass 3 targets the last residue the pass-2 overlay showed **inside** the
otherwise-aligned card: the placeholder/label text ghosted while the boxes
matched, the input frame a shade off, and the photo-side corner arcs. Method
is the same loop as pass 2 — PIL band-profile the harness legacy PNG for exact
ink extents / border pixel colours, render the build headlessly on a throwaway
`:13000`, iterate until the bands match. Every value below is a **PIL sample of
the harness legacy capture**, not a theme-CSS re-read (pass-1/2 had inverted two
of them from the theme source; the render is the spec).

## 0. Baseline note — this worktree did not contain pass 1/2

The agent worktree branch (`worktree-agent-…`, an RMA line) was **not** branched
from the pass-2 tip — it had the pass-0 login (Brandon font, `12px 16px`
padding, native checkbox) and neither the parity doc nor the Founders/Brandon
font assets. `main` (`dcbd139e`) carries the complete pass-1/2 work. Pass 3
first imported the exact pass-2 state of the login files from `main`
(`git checkout main -- <login.module.css, LoginForm.tsx, globals.css, proxy.ts,
public/fonts/**, wholesale-buyer-login.spec.ts, this doc>`), verified `proxy.ts`
and `globals.css` diverged from `main` **only** by the login-parity additions
(no RMA-branch collateral), then applied the pass-3 deltas on top. The commit on
this branch therefore contains pass 1 + 2 + 3.

## 1. Root causes fixed (all PIL-sampled off the harness legacy PNG)

| # | Symptom (pass-2 overlay) | Legacy sample | Was (pass 2) | Fix |
|---|---|---|---|---|
| 1 | "Email"/"Password" placeholder ink doubled | ink x-start **1001** | `padding: 8px 12px` → ink x-start 1005 (+4px) | input `padding: 8px 8px` (the Atlas `.form-control` value; pass-2's 12px left was wrong) |
| 1 | "Remember me?" label doubled | ink x-start **1020** | checkbox `margin-right:13px` → 1024 (+4px) | checkbox `margin-right: 9px` (13px was an Atlas-class guess, not measured) |
| 2 | Input frame red on all four sides | border pixel **(137,135,135) = #898787**, solid 1px, no spill | `border:1px #534F4C` → rendered (83,79,76) | `border: 1px solid var(--color-input-border-dark)` (#898787). The theme's `.logininput .mx-textbox-input {#534F4C}` override does **not** take effect in the captured page — the frame is the plain Atlas `.form-control` #898787 |
| 3 | Card **left** (photo) corner arcs red | left photo arc reaches the straight edge **12px** below the card top; right cream arc **16px** (asymmetric — legacy clips the photo ~4px tighter than the cream) | uniform `border-radius:22px` → left arc 16px (4px too round) | `border-radius: 17px 22px 22px 17px` (left corners tighter). Reproduces the legacy arc within ±1px, no cream sliver (card cream clips to the same left radius as the photo) |

Button labels (Login / Employee Login / Contact Us) were **already** matched in
pass 2 (dX 0–1) — their overlay "ghosting" was cross-rasterizer glyph AA, not a
position error. Confirmed by band-profile: Login dX+1, Employee/Contact dX0.

## 2. Per-element before/after (harness legacy PNG vs headless `:13000` render, 1920×1080)

| Element | Legacy | Pass 2 (before) | Pass 3 (after) |
|---|---|---|---|
| Email placeholder ink | x1001..1037 | x1005..1041 (**dX +4**) | **x1001..1037 (dX 0)** ✓ |
| Password placeholder ink | x1001..1067 | x1005..1071 (**dX +4**) | **x1001..1067 (dX 0)** ✓ |
| Input border pixel | (137,135,135) | (83,79,76) #534F4C | **(137,135,135) #898787** ✓ |
| Remember-me label ink | x1020 | x1024 (**dX +4**) | x1021 (dX +1, AA) |
| Forgot Password ink | x1255..1379 | x1255..1379 | x1255..1379 (dX 0) ✓ |
| Login / Employee / Contact | matched | dX 0–1 | dX 0–1 ✓ (AA) |
| Top-left photo arc (x per y124..136) | 522→510 | 526→510 (**+4px rounder**) | 522→510 (**±1px**) ✓ |
| Bottom-left photo arc | (17px radius) | 22px (too round) | 17px ✓ |
| Top-right cream arc | 1392→1409 | 1392→1409 | 1392→1409 (identical) ✓ |

## 3. Residuals left faithful (sub-pixel / cross-rasterizer — not chased)

These remain because they are at/below the rasterizer noise floor **and** the
render here is a different Chromium build than the harness's pinned one (the
pass-2 caveat). Tuning them to my-Chromium-vs-harness-legacy risks *de-tuning*
the harness's same-Chromium-both-sides compare, and the CSS is already faithful:

1. **Eye icon ±1px** — legacy ink x1347..1368 y388..404; render x1348..1368
   y387..404. The **right and bottom edges coincide**; only the faint top-left AA
   pixel differs by 1. The toggle CSS (`top:11px`, `right:10px` from the input
   edge) is the exact legacy `.passwordicon .toggle-password-button {top:11px;
   right:25px}` translated for the 390px control — not adjusted.
2. **Photo bottom edge 1px** — the photo fills to y732 vs legacy y733 at x700
   (card-bottom sub-pixel). Forcing it would mean a fractional card height /
   photo overflow that shifts the footer; left at the faithful 609px.
3. **Glyph AA** — every text run shows a thin hollow red outline in the
   local-vs-harness overlay (0.6%-class, same as pass 2). Zero solid/doubled
   ghosts remain; the harness's pinned-Chromium-both-sides compare erases this.

## 4. Files changed in pass 3

| File | Change |
|---|---|
| `src/app/(auth)/login/login.module.css` | `.input` padding `8px 12px`→`8px 8px` + border `#534F4C`→`var(--color-input-border-dark)` (#898787); `.checkbox` margin-right `13px`→`9px`; `.loginCard` border-radius `22px`→`17px 22px 22px 17px` (asymmetric — tighter photo-side corners) |
| (imported from `main`) | `LoginForm.tsx`, `globals.css`, `proxy.ts`, `public/fonts/**` (13 OTFs), `wholesale-buyer-login.spec.ts`, this doc — the pass-1/2 baseline this worktree lacked |

## 5. Verification

- **`npx tsc --noEmit`:** 31 errors, **all pre-existing** and none in a login
  file (partial-credit `*.test.tsx` + the two known e2e specs
  `admin-purchase-orders.spec.ts` / `wholesale-submit-bids.spec.ts`). Pass-3
  touches only `login.module.css` (a CSS module — no TS surface), so **zero new
  errors**.
- **Headless render** (`next dev -p 13000`, standalone `@playwright/test`
  chromium at deviceScaleFactor 1 — the main `:3000`/`:8080`/`:8082` untouched):
  band profiles above; `document.fonts` shows Founders 400/500 loaded, all
  `/fonts/**` HTTP 200 (proxy `fonts/` exclusion intact), input computed
  `padding:8px` / `border rgb(137,135,135)`.
- **Local diff overlay** (my-Chromium render vs harness legacy PNG, tol 24, photo
  interior masked): the pass-2 red input-frame boxes and top-left corner arc are
  **gone**; remaining red is glyph-edge AA (expected, environmental) + the two
  ≤1px residuals in §3. Final `reg-cli` sign-off belongs to the orchestrator's
  same-Chromium harness.
