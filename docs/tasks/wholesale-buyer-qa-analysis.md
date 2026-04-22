# Wholesale Buyer Experience — QA Analysis (Parity Baseline)

**Source:** `https://buy-qa.ecoatmdirect.com/p/login/web` (Mendix QA)
**Test account:** `akshay+4@learnfastthinkslow.com` — two **auction-only** buyer codes (`DDWS`, `AD`) under CHS Technology (HK) Ltd. **No PWS buyer code attached.**
**Captured:** 2026-04-22
**Purpose:** establish the ground-truth surface, interactions, and visual tokens that the Next.js port must clone as-is.

### Scope caveat — this doc covers the auction-code-selected view only

A Buyer in the Mendix app can hold **auction buyer codes** (`AD`, `DDWS`, etc.), **PWS buyer codes** (for the Phone Wholesale Store flows), or both. Menu and landing-page gating is **per *selected* buyer code**, not per role and not per portfolio. The buyer-code picker lets the user choose which code's context they want to enter, and the entire shell switches based on that selection:

| Selected code type | Landing page | Sidebar nav |
|---|---|---|
| **Auction** (`AD`, `DDWS`, …) | **Bidder Dashboard** — the grid described in this doc | Auction + Buyer User Guide |
| **PWS** | **Shop** page (direct deep-link; not captured in this doc) | PWS menus (Orders / Offers / Pricing / Profile / Notifications) — **no Auction / Buyer User Guide item** |

A buyer with codes of both types **does not** see a merged menu. They see one context at a time, and use the **Switch Buyer Code** link in the top bar to return to the picker and pivot between contexts. The two shells are effectively two sibling apps gated by the active code.

Everything below describes the **auction-code-selected** shell. A second walkthrough with a PWS code selected is required to close parity on the Shop / Orders / Offers / Pricing / Profile / Notifications surfaces — those Mendix pages are already sliced in `migration_context/frontend/Pages_*.md`, but the PWS shell's live layout, nav order, and transitions still need a live capture pass.

Screenshots referenced in this document are stored under `.playwright-mcp/qa-01-*.png` through `qa-11-*.png` at the repo root. The Mendix source flows for each surface are already sliced in `migration_context/frontend/` and `migration_context/backend/`; this doc is the observational layer on top of that slice.

---

## 1. Total Surface

The entire wholesale bidder persona only has **five distinct screens/modals** and one PDF link:

| # | Surface | Route / Trigger | Notes |
|---|---|---|---|
| 1 | Login | `/p/login/web` | Shared with the whole buyer portal |
| 2 | Buyer-code picker | `/` (post-login landing when > 1 code) | "Welcome {firstName}!" + list of wholesale buyer codes |
| 3 | Bidder dashboard | `/` (after picking a code) | The only working surface — grid of 10,404 inventory rows, editable bid + qty cap per row |
| 4 | Carryover modal | **Carryover** button | Copies last week's bids forward; empty state = "You don't have bids from last week to carry over." |
| 5 | Import Your Bids modal | **Import** button | `.xlsx` upload, green "Import" CTA |
| — | Buyer User Guide | Sidebar link → PDF in new tab | Static `Buyer Guide (R1).pdf` download; not a first-class page |

**Crucial parity finding (auction-code-selected):** once an auction buyer code is selected in the picker, the shell shows only Auction + Buyer User Guide — the PWS menus (Orders / Offers / Pricing / Profile / Notifications) are absent regardless of whether the user also holds PWS codes. Those PWS menus belong to the **PWS shell**, which is reachable only by switching to a PWS code from the picker. The user-avatar popover in the auction shell has exactly two actions: **Submit Feedback** and **Logout**.

**Port implication:** the shell's nav is gated on the **active buyer code's type** (held in session / URL param), not on the user's code portfolio. The auction shell renders Auction + Buyer User Guide and nothing else. The PWS shell is a **separate layout** with its own sidebar — not a superset of the auction layout. The two shells share the top bar (logo, Switch Buyer Code link, avatar) and the login/picker flow; everything below the top bar differs.

Landing-page routing after picker selection:

- Pick an auction code → redirect to **Bidder Dashboard** (the auction shell's default route).
- Pick a PWS code → redirect to **Shop** (the PWS shell's default route).

---

## 2. Login (`/p/login/web`)

Matches the card-with-photo pattern on QA today.

- Left half: full-bleed hero photo (gloved hand holding a phone, industrial backdrop)
- Right half: cream card (`#F7F7F7`-family) with:
  - `ecoATM` wordmark (teal)
  - Headline: **"Premium Wholesale & Weekly Auctions"** (dark, large, bold)
  - Email textbox
  - Password textbox + eye toggle (right-inset icon button)
  - Row: `☐ Remember me?` (left) / `Forgot Password?` link (right, teal)
  - Primary pill: **Login** (dark teal `#112d32`, full-width, white text)
  - Secondary link: **Employee Login** (teal text, underline on hover)
  - Divider, then:
    - "Interested but don't have an account?"
    - Dark pill: **Contact Us**
- Footer: `Privacy Policy` button + `© 2026 ecoATM, LLC. All Rights Reserved.`
- Page background: solid ecoATM teal (top: slightly lighter, bottom: deeper teal)

The password eye toggle uses two stacked `<img>` elements (show vs. hide state). Replicate with SVG + aria-pressed for accessibility — the Mendix original relies on image swap.

---

## 3. Buyer-Code Picker (`/` post-login)

Rendered when the user has > 1 active wholesale buyer code.

**Layout:**
- Top bar: dark teal (`#112d32`) with ecoATM wordmark (white) on the left, user avatar on the right
- Centered content column:
  - H1: **"Welcome {firstName}!"**
  - Sub: **"Choose a buyer code to get started:"**
  - Hero image: black-and-white photo of a phone warehouse (warehouse shelves with phones)
  - Section title: **"Weekly Wholesale Auction"** (dark teal, bold)
  - Sub: "Bid on devices ready for uplift, repair, & resale."
  - List of buyer-code cards:
    - Dark navy/teal pill, rounded, with:
      - Left: green circular avatar with a briefcase icon
      - Bold 2-3 letter code on top (e.g. `DDWS`, `AD`)
      - Company name below (e.g. `CHS Technology (HK) Ltd`)
      - Right arrow `→` on the far right
      - Entire pill is a button — whole-card click target

**Known Mendix widget error visible in the accessibility tree** but not on-screen:
`Could not render widget 'AuctionUI.Buyer_Code_Select.activeMenuSelector1'`

This is a Mendix rendering bug — the active-code menu fails when there's no selection yet. Our port should simply not have this bug. The picker copy and layout stay identical.

**When the user has only 1 active code:** Mendix skips this screen and deep-links to the bidder dashboard directly. Our port should do the same.

---

## 4. Bidder Dashboard (the only real screen)

**Page title:** `ecoATM Direct - Bidder Dashboard` (document `<title>`).

### 4.1 Page chrome

| Region | Detail |
|---|---|
| Left sidebar | Gradient `linear-gradient(155.66deg, #14AC36 -12.99%, #00969F 83.48%)` (ecoATM green → teal). Collapsible via toggle at the top. Icon-only when collapsed (`54px` wide), icon + label when expanded. |
| Sidebar items | (1) **Auction** (gavel icon) (2) **Buyer User Guide** (book icon). That's it. |
| Top bar (flat white/cream) | Left: `ecoATM DIRECT` logo, "Switch Buyer Code" link, **active buyer-code chip** (framed, rounded, showing code + company). Right: **user name** text + circular avatar button with initials. Both visible at all times. |
| User avatar popover | Floating card with only two vertically stacked dark pills: **Submit Feedback**, **Logout**. |

### 4.2 Auction header row

Below the top bar, in the same cream background:

- Left: H2 **"Auction {YYYY} / Wk{NN}"** + H2 **"Round {n}"** — two side-by-side headings (Mendix's idiom: two `<h2>` elements instead of a single heading with a sub).
  - Example observed: `Auction 2026 / Wk16 Round 1`
- Next to the title block: two tertiary buttons `↓ Export` and `↑ Import` (outlined, light grey, 14px text, 500 weight). Arrow icons on the left of each label.
- Right side (same row):
  - H3 timer string **"Ends in | {D}D. | {H}hrs. | {M}min. |"** — observed `Ends in | 0D. | 5hrs. | 40min. |`. Pipe-delimited, human-readable, with trailing pipe. Same font family (Brandon Grotesque), 18px, 500.
  - Pill CTA **Submit Bids**. Default enabled state: bright green `#14AC36` fill, white text, 44px border-radius (full pill), 152×43 box, 18px label. A dark-teal variant is observed on fresh load/scroll — likely a CSS transition artifact, not a real second state. Clone the green as the canonical.

### 4.3 Sub-header row (above grid)

Spans the full width:

- Left: red warning copy `Minimum starting bid - $2.50` (style: body-size, 500 weight, deep red accent)
- Right: secondary pill **Carryover** (translucent teal fill `rgba(0, 150, 159, 0.18)`, dark text `#3C3C3C`)

### 4.4 The grid

Mendix datagrid with custom cell templates. 11 columns (Model Name hidden at smaller widths):

| # | Column | Type | Align | Filter type | Sortable |
|---|---|---|---|---|---|
| 1 | Product Id | integer | right | numeric input | ✓ |
| 2 | Brand | string | left | combobox (Search + options) | ✓ |
| 3 | Model | string | left | text input (Search) | ✓ |
| 4 | Model Name | string | left | text input (Search) | ✓ (hidden on narrow viewports) |
| 5 | Grade | string | left | text input | ✓ |
| 6 | Carrier | string | left | combobox (Search + options) | ✓ |
| 7 | Added | date | left | date-picker (with calendar icon button) | ✓ |
| 8 | Avail. Qty | integer | center | numeric input | ✓ |
| 9 | Target Price | currency | center | numeric input | ✓ |
| 10 | **Price** | currency **editable** | center | numeric input (also acts as filter) | ✓ |
| 11 | **Qty. Cap** | integer **editable** | center | numeric input | ✓ |

**Grid behavior:**
- **Infinite scroll**, not paginated — footer text says `Currently showing {loaded} of {total}` (observed `20 of 10404` / `40 of 10404` after scroll). Load-on-demand triggers on internal scroll container.
- Footer label exact string: `Showing{loaded} of {total}rows` (note: concatenated without spaces — Mendix idiom).
- Sort arrows in header (`↑` / `↓`), click cycles asc → desc → clear.
- Zebra stripes: first row `rgba(0,0,0,0)` (no striping observed at this inventory depth — row bg is transparent, the cream body shows through).
- Grid header text color: `rgb(102, 103, 102)` ≈ `#666766`.
- Cells have top/bottom borders (class `td-borders`); first row has no top border.

**Editable cells:**
- **Price** uses Mendix `mx-name-textBox1` with classes `text-dollar` (auto-prefixes `$` and formats to 2 dp), `textbox-select-all` (clicking pre-selects content). Default value rendered as `0.00`. Type=text, no `inputmode`, no `min`, no `step`.
- **Qty. Cap** uses `mx-name-textBox2` with class `only-numbers` (integer-only), same `textbox-select-all`. Default value empty. **Empty = no-cap sentinel** — matches the backend ADR (null `bid_quantity` means "accept any quantity up to `maximumQuantity`"). A `0` entry is valid and distinct — means "accept zero units at this price", rare but legal.
- Both inputs live inside `auctions-input auction-datagrid-customtext` wrappers — no borders (borderless inputs that visually merge with the cell).

### 4.5 Footer / totals

No totals row visible in QA's current implementation. The pagination block shows the `Currently showing X of Y` count only. **Our port should match** — no top/bottom totals bar, no "selected rows" line.

### 4.6 Carryover modal

- Centered card, cream background, thin border
- `×` close button top-right (plain, no background)
- Title slot present but empty in the empty-state
- Body copy: **"You don't have bids from last week to carry over."** (centered, single line, no primary CTA)
- When carryover *is* available (not observed here), behavior per Mendix source: copies last week's `submitted_*` values into the current week's `bid_*` fields for every row in the slice.

### 4.7 Import Your Bids modal

- Centered card, cream, same close button pattern
- Title: **"Import Your Bids"** (left-aligned, dark, bold)
- Body:
  - Sub-heading: **"To bulk import your bids:"**
  - Numbered list:
    1. Export your bid sheet
    2. Update your bids and qty caps in the excel sheet
    3. Upload your file here
    4. Please review your updated bids and quantity caps before final submission
  - File input + outlined **Browse...** button (grey outline, not a primary CTA)
  - Helper text: **"Supported format: .xlsx"** (12px, muted grey)
  - Bottom: primary pill **Import** — bright green `#14AC36`, full pill, 18px

**Important:** the green on the Import modal's primary button is **the same green as Submit Bids** (`#14AC36`). The Mendix idiom is: green pill = commit/submit action; dark teal pill = top-bar primary (login, contact us, etc.). Our port should use the same dichotomy.

### 4.8 Submit Bids flow — captured 2026-04-22

Exercised end-to-end on the AD auction. Behavior:

- **No pre-submit confirmation dialog.** Clicking **Submit Bids** POSTs immediately. The only pre-commit guard is the client-side "no bids entered" check.
- **Empty-state modal** (no bids entered) — Mendix shows an informational modal titled **"No Bids to Submit"** with a numbered list of how to add bids. Purely informational; Close dismisses. Screenshot: `qa-12-submit-bids-dialog.png`.
- **Post-submit success modal** — fires on both first submit and resubmit with identical copy:
  - Heading: `Your Bids have been Submitted!` (middle word `Bids` styled in green `#14AC36`, double-space between `Bids` and `have` per Mendix source)
  - Body: `Please review your updated bids, quantity caps and resubmit for any changes.`
  - Close CTA (green pill)
  - Screenshots: `qa-13-submit-bids-confirm-1.png`, `qa-14-submit-bids-resubmit.png`
- **Resubmit path shows the same modal** — no "already submitted" variant.
- **Min-bid rule is advisory, not hard.** A $2.50 bid was accepted without rejection. The red "Minimum starting bid - $2.50" label guides the user but does not block submit client-side.

Behavior per `migration_context/backend/ACT_SubmitBidRound.md` and our own 2026-04-23 ADR:
- Snapshots `bid_*` → `submitted_*` for every row in `(bid_round_id, buyer_code_id)` slice
- Prior `submitted_*` → `last_valid_*` (one level of history)
- Round stays re-submittable until lifecycle cron closes it
- REST endpoint already defined: `POST /api/v1/bidder/bid-rounds/{id}/submit?buyerCodeId={id}`

The frontend port of Submit Bids needs:
1. Client-side empty-bids guard: if every row has `bidAmount === 0`, open the empty-state modal and do **not** POST.
2. Single-click commit — no confirmation step.
3. Success modal with verbatim Mendix copy (preserve double-space and green `Bids` span).
4. On 429: surface the existing `RateLimitedError` with backoff copy.
5. On 409 `ROUND_CLOSED`: refetch + error surface (modal copy not yet captured — schedule with DOWNLOAD-mode walkthrough).

### 4.9 Both buyer codes render the same template

DDWS and AD both land on the identical bidder dashboard template — same 10,404-row inventory on QA. In production these differ by buyer-code **type** filter (DDWS is Data-Wipe-eligible; AD is non-DW), but the UI is a single shared page. No code-type badge or "DW only" ribbon is shown — the user learns which code they're on purely from the buyer-code chip in the top bar.

---

## 5. Visual Tokens (verified via computed styles)

Baseline to drive our Next.js port's `tokens.css` / theme:

| Token | Value | Usage |
|---|---|---|
| `--color-bg-body` | `#F7F7F7` (rgb 247,247,247) | Dashboard background |
| `--color-text-body` | `#3C3C3C` (rgb 60,60,60) | Default text |
| `--color-text-muted` | `#666766` (rgb 102,103,102) | Grid column headers |
| `--color-brand-teal-dark` | `#112d32` | Login primary, top-bar chip bg, dark pills |
| `--color-brand-teal` | `#407874` | Secondary teal (Forgot Password link, Employee Login) |
| `--color-brand-teal-mid` | `#00969F` | Sidebar gradient endpoint (bottom) |
| `--color-brand-green` | `#14AC36` | Primary CTA (Submit Bids, Import), sidebar gradient start |
| `--color-brand-green-alpha18` | `rgba(0, 150, 159, 0.18)` | Carryover button background (tint of teal, not green — worth noting) |
| `--color-warning-red` | deep red (approx `#C21E1E` from screenshot — exact hex TBD via eyedropper) | "Minimum starting bid" label |
| `--font-family-primary` | `"Brandon Grotesque", sans-serif` | **All text** — no secondary font observed |
| `--font-size-body` | `16px / 400` | Body text |
| `--font-size-h2` | large (observed page headings — `Auction 2026 / Wk16`, `Round 1`) | Two-line section headers |
| `--font-size-h3-timer` | `18px / 500` | Timer, Submit Bids label |
| `--font-size-button-sm` | `14px / 500` | Export/Import outline buttons |
| `--radius-pill` | `44px` | Submit Bids, Login, Contact Us, buyer-code cards |
| `--radius-input` | observed ~4px | Text inputs, filter search boxes |

**Sidebar gradient (copy-paste):**
```css
background: linear-gradient(155.66deg, #14AC36 -12.99%, #00969F 83.48%);
```

**Outline button (Export / Import):**
```css
background: #F7F7F7;
border: 1px solid #D0D0D0;
color: #3C3C3C;
font: 500 14px "Brandon Grotesque", sans-serif;
```

**Primary green pill (Submit Bids / modal Import CTA):**
```css
background: #14AC36;
border: 1px solid #14AC36;
color: #FFFFFF;
font: 400 18px "Brandon Grotesque", sans-serif;
border-radius: 44px;
padding: ~0 28px; /* visual match at 152×43 */
```

**Font loading:** `Brandon Grotesque` is a paid Monotype family. The Mendix app likely ships it via a licensed web-font endpoint. Confirm our ECS/Vercel license before embedding. If license is ambiguous, the closest free alternative used in ecoATM marketing is `Museo Sans` or `Proxima Nova` — get design sign-off before swapping.

---

## 6. Mendix Widget → Next.js Port Mapping

For the bidder dashboard, Mendix IDs observed in the DOM (useful when cross-referencing `migration_context/frontend/Pages_*.md`):

| Mendix widget | Element | Our port component |
|---|---|---|
| `AuctionUI.Buyer_Code_Select.activeMenuSelector1` | Top-bar active buyer chip (errored on QA) | `<BuyerCodeChip />` in bidder-layout |
| `AuctionUI.PG_Bidder_Dashboard_DG2` | Main datagrid | `<BidDataGrid />` — 11 columns, infinite-scroll |
| `AuctionUI.PG_Bidder_Dashboard_DG2.textBox1` (`mx-name-textBox1`, `text-dollar`) | Price cell editor | `<PriceCell />` with dollar mask |
| `AuctionUI.PG_Bidder_Dashboard_DG2.textBox2` (`mx-name-textBox2`, `only-numbers`) | Qty Cap cell editor | `<QtyCapCell />` integer-only |
| `AuctionUI.ecoAtm_Atlas_Default.sidebarToggle3` | Sidebar collapse toggle | `<SidebarToggle />` |
| `actionButton3` (`btn-primary`, Submit Bids) | Primary CTA | `<SubmitBidsButton />` |
| `actionButton7` (`carry-over-btn`) | Carryover trigger | `<CarryoverButton />` |
| Brand filter (`mx-name-textFilter2 auctions-filter`) | Brand combobox filter | `<BrandFilter />` — text + dropdown options |
| Date filter (`mx-name-dateFilter1`, React DatePicker) | Added date filter | `<AddedDateFilter />` — calendar picker |

The `.auctions-filter`, `.auctions-input`, `.auction-price`, `.auction-qtycap`, `.auction-grade`, `.auction-carrier`, `.auction-added`, `.auction-availqt`, `.auction-targetp`, `.auctions-datagrid-display-cell` classes are the Mendix app's custom CSS layer on top of datagrid primitives. Port these as CSS module classes with the **exact same names**, not renamed — makes the QA diff cheaper to review.

---

## 7. Parity Checklist (what "100% clone" actually means)

### Must-match behavior
- [ ] Single-code users skip the picker; multi-code users see it
- [ ] Only "Auction" and "Buyer User Guide" in the sidebar — no other nav
- [ ] User avatar menu: only "Submit Feedback" + "Logout"
- [ ] "Switch Buyer Code" link returns to picker without requiring re-auth
- [ ] Infinite scroll grid, no pagination controls
- [ ] Footer string: `Currently showing {N} of {Total}` — no ellipsis, no "rows" suffix in the primary line; "Showing{N} of {Total}rows" as the screen-reader duplicate
- [ ] 11 columns in the order listed in §4.4 (Model Name may hide at widths < ~1100px)
- [ ] Price input: `0.00` default, `text-dollar` auto-format, select-all on focus
- [ ] Qty Cap input: empty default, integer-only, select-all on focus, **empty ≠ zero** (no-cap vs. zero-unit bid)
- [ ] Carryover empty state copy verbatim: "You don't have bids from last week to carry over."
- [ ] Import modal copy verbatim, with step 3 showing concatenated "3.Upload your file here" (no space after the period — it's a Mendix typo worth preserving for pixel parity, or worth fixing with design sign-off)
- [ ] `.xlsx` only for import
- [ ] Red "Minimum starting bid - $2.50" label above the grid, left-aligned
- [ ] Timer format exact: `Ends in | {D}D. | {H}hrs. | {M}min. |` (trailing pipe included)
- [ ] Buyer User Guide opens a new tab with the PDF — **does not** render inside the app
- [ ] Green primary CTAs for commit actions; dark teal for brand/navigation primaries
- [ ] Sidebar gradient applied on the `.region-sidebar` container, not on individual menu items

### Must-NOT add (common divergences to avoid)

**Always:**
- [ ] ❌ No totals footer row on the grid
- [ ] ❌ No "selected rows" count
- [ ] ❌ No dark/light theme toggle
- [ ] ❌ No modern aesthetic overlays (glassmorphism, gradients inside cards, etc.)
- [ ] ❌ No pagination controls (it's infinite scroll)
- [ ] ❌ No inline sparkline / chart columns in the grid
- [ ] ❌ No hover-expand row detail panel (cells are flat)

**Auction shell (any auction buyer code selected, regardless of portfolio):**
- [ ] ❌ Do not render Orders / Offers / Pricing menu items
- [ ] ❌ Do not render Profile / Password / Notifications menu items
- [ ] ❌ User-avatar popover must contain only Submit Feedback + Logout

These exclusions are gated on the **active selected code's type**, not on the user's portfolio. A buyer who also holds PWS codes still sees the stripped auction shell while an auction code is selected; the PWS menus only appear after switching to a PWS code via the picker.

### Deferred / needs second QA walkthrough
- [ ] **Walk a PWS-code selection** — capture the PWS shell end to end: Shop landing page, sidebar menu (Orders / Offers / Pricing / Profile / Notifications), top-bar behavior, and the "Switch Buyer Code" round-trip back to the auction shell. The PWS shell is a separate layout, not an extension of the auction shell.
- [ ] Submit Bids confirmation dialog copy + primary-button label
- [ ] Success toast / inline confirmation after submit
- [ ] "Carryover available" non-empty state (layout + CTA label)
- [ ] DOWNLOAD mode (Round 1 closed, Round 2 not yet open) — what the page shows
- [ ] ALL_ROUNDS_DONE mode — final-state page copy
- [ ] ERROR_AUCTION_NOT_FOUND state — what the page shows vs. 404 redirect
- [ ] Round 2 / Round 3 grid variants — what columns change, if any (per the `bid_meets_threshold` filter)
- [ ] Round 3 labeled "Upsell Round" in-page (per the 2026-04-20 ADR) — verify header text at runtime
- [ ] Exact red hex for the minimum-bid warning label
- [ ] Exact teal hexes for top-bar, login button (eyeballed `#112d32` needs confirmation)

---

## 8. Implementation Ordering for Our Port

Our backend already has the contracts (bidder dashboard, bid_data create, submit, rate limiter — all documented in `docs/api/rest-endpoints.md` under "Bidder Dashboard" and in the 2026-04-23 ADR). The remaining work is purely frontend:

1. **Shell** — `BidderLayout` with the gradient sidebar, top bar, user avatar menu, buyer-code chip, sidebar-toggle.
2. **Buyer-code picker page** — hero image, code list, two-line copy. Skip for single-code users.
3. **Bidder dashboard page** — fetch `GET /api/v1/bidder/dashboard?buyerCodeId=…`, branch on `mode`.
4. **`BidDataGrid`** — 11 columns, infinite scroll, column-specific filter inputs, sort arrows. Use an existing virtualized grid lib (TanStack Table + react-virtuoso) rather than rolling our own.
5. **`PriceCell` / `QtyCapCell`** — controlled inputs wired to `PUT /api/v1/bidder/bid-data/{id}` with debounced save + `VersionConflictError` handling.
6. **Carryover modal** — empty state + success state.
7. **Import Your Bids modal** — `.xlsx` upload, server endpoint TBD (not yet in `rest-endpoints.md` — needs its own backend slice).
8. **Submit Bids button** — post to `POST /api/v1/bidder/bid-rounds/{id}/submit?buyerCodeId=…`, handle 409 `ROUND_CLOSED` and 429 `RateLimitedError`.
9. **Timer** — client-side countdown from `bidRound.endDatetime - now()`, format `Ends in | D.D | H.hrs | M.min |`.
10. **Minimum-bid validator** — client reject `< 2.50`, server authority still runs.

The Import `.xlsx` endpoint is the one piece without a backend slice today — flag for capability planning before the frontend hits that modal.

---

## 9. Open Questions for Product / Design

1. **Font license** — Brandon Grotesque paid Monotype. Clone as-is or substitute? Needs a yes/no before shipping.
2. **Mendix widget-render error** (`Buyer_Code_Select.activeMenuSelector1`) — intentional to preserve, or silently drop? Recommend: drop. It's a Mendix bug, not a feature.
3. **"3.Upload your file here"** typo in Import modal — preserve for pixel parity, or quietly fix? Recommend: fix with a copy cleanup pass.
4. **Round 3 label** — our 2026-04-20 ADR says display name is `"Upsell Round"`, not `"Round 3"`. Verify this surfaces on the bidder dashboard during Round 3 (not observed in QA since it's currently Round 1).
5. **Submit confirmation copy** — not captured in this pass; needs a disposable-auction walkthrough or design spec.
