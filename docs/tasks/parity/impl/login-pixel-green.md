# Login page → pixel-green (LOGIN-P1 / P2 / P3)

Implementation record for parity task #1 (`docs/tasks/parity/findings.md`
→ "Proposed implementation task #1"). Drives the new `/login` page to
pixel-parity with the legacy Mendix buyer login (`/p/login/web` ·
`EcoATM_UserManagement.Login_New`).

**Status: implemented + self-verified green** (headless capture on a
throwaway port matched every legacy metric to the pixel — see
§ Verification). Final strict `reg-cli` sign-off still belongs to the
orchestrator's H-capture harness.

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
