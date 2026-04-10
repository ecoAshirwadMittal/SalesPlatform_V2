# PWS Pixel-Perfect QA Comparison Plan

## QA Credentials

### Admin (PWS Layout with SALES/BUYERS sections)
- URL: `https://buy-qa.ecoatmdirect.com/login.html`
- Username: `ashirwadmittal`
- Password: `Password123#`
- After login, navigate to `/p/inventory` to enter PWS layout

### Buyer (PWS Layout — buyer-only nav)
- URL: `https://buy-qa.ecoatmdirect.com/p/login/web`
- Email: `nadia.ecoatm@gmail.com`
- Password: `Test100%`
- Buyer code: `NB_PWS`

### Local Dev
- URL: `http://localhost:3000`
- Login: `bidder@buyerco.com` / `Bidder123!`
- Admin: `salesops@test.com` / `SalesOps123!`

---

## Execution Workflow (Per Page)

1. **Screenshot QA** — Playwright `browser_navigate` + `browser_take_screenshot` at 1440x900
2. **Screenshot Local** — Same viewport, same page
3. **Extract CSS** — `browser_evaluate(getComputedStyle)` on QA elements
4. **Catalog diffs** — List discrepancies by visual impact
5. **Fix CSS** — Apply exact computed values to `.module.css`
6. **Re-verify** — Fresh screenshot, side-by-side compare
7. **Commit** — One commit per page: `fix: pixel-match /pws/{page} styling to QA Mendix`

---

## QA-to-Local Page URL Mapping

| # | QA URL Path (post-login) | Local URL | CSS File |
|---|--------------------------|-----------|----------|
| 0 | (layout shell) | `/pws/*` | `src/app/pws/pws.module.css` |
| 1 | `/p/inventory` (Shop tab) | `/pws/order` | `src/app/pws/order/pwsOrder.module.css` |
| 2 | (click Cart from Shop) | `/pws/cart` | `src/app/pws/cart/cart.module.css` |
| 3 | Offer Review sidebar | `/pws/offer-review` | `src/app/pws/offer-review/offerReview.module.css` |
| 4 | Click offer row | `/pws/offer-review/[offerId]` | (shares offerReview.module.css) |
| 5 | Counters sidebar | `/pws/counter-offers` | `src/app/pws/counter-offers/counterOffers.module.css` |
| 6 | Click counter row | `/pws/counter-offers/[offerId]` | (shares counterOffers.module.css) |
| 7 | RMAs sidebar | `/pws/rma-requests` | `src/app/pws/rma-requests/rmaRequests.module.css` |
| 8 | Click RMA row | `/pws/rma-requests/[rmaId]` | `src/app/pws/rma-requests/[rmaId]/rmaDetails.module.css` |
| 9 | RMA Review sidebar | `/pws/rma-review` | `src/app/pws/rma-review/rmaReview.module.css` |
| 10 | Click RMA review row | `/pws/rma-review/[rmaId]` | `src/app/pws/rma-review/[rmaId]/rmaReviewDetails.module.css` |
| 11 | Pricing sidebar | `/pws/pricing` | `src/app/pws/pricing/pricing.module.css` |
| 12 | Inventory sidebar | `/pws/inventory` | `src/app/pws/inventory/inventory.module.css` |

---

## Design Tokens (Extracted from QA)

| Token | Value | Usage |
|-------|-------|-------|
| Teal primary | `#407874` / `rgb(64,120,116)` | Active nav, links, focus, "View As" label bg |
| Dark | `#112d32` / `rgb(17,45,50)` | Sidebar bg, topbar bg |
| Page content bg | `#efebe4` / `rgb(239,235,228)` | Content area beige, active nav bg |
| Card/header bg | `#F7F7F7` / `rgb(247,247,247)` | Datagrid headers, modals, body bg |
| Text primary | `#3c3c3c` / `rgb(60,60,60)` | Body text |
| Text secondary | `#666766` | Column headers, labels |
| Border gray | `#ced0d3` | Datagrid cell borders |
| Input border | `#D0D0D0` | Form controls |
| Green accent | `#14AC36` | Submit buttons, success |
| Price green text | `rgb(64,120,116)` | Offer prices at/above list |
| Price green bg | `rgb(215,228,217)` | Offer price green background |
| Price orange text | `rgb(225,115,56)` | Offer prices below list |
| Price orange bg | `rgb(244,229,219)` | Offer price orange background |
| Cart qty red | `rgb(235,51,0)` | Cart qty when > 0 |
| Yellow highlight | `#fdf488` | Active status tab bg |
| Cart summary bg | `#d8e5d9` | Cart summary pill |
| Sidebar nav text | `#fbfaf8` / `rgb(251,250,248)` | Nav labels, section headers |
| Branding text | `#d6fddb` / `rgb(214,253,219)` | "Premium Wholesale" in topbar |
| Buyer code selector border | `#898787` / `rgb(137,135,135)` | Combobox border |

---

## Phase 0: Layout Shell — COMPLETED

### Changes Applied

| Element | Before | After (Matches QA) |
|---------|--------|---------------------|
| Topbar height | 48px | **62px** |
| Topbar padding | `0 8px 0 16px` | **`0 8px`** |
| Topbar position | static | **relative** (for centered buyer code) |
| Section headers | `display: none` | **Visible: 11px/500, #fbfaf8, uppercase** |
| Sidebar order | Buyers first | **Sales first, then Buyers** |
| Nav item font | 10px | **12px** |
| Nav item padding | 8px | **13px** (67px total height) |
| Active nav bg | transparent | **#efebe4 beige** |
| User icon | 34px, transparent, teal border | **28px, solid #407874, white icon** |
| Buyer code selector | Semi-transparent button, left-aligned | **"View As" teal label + combobox, centered** |
| Logo size | 100x24 | **143x29** |
| Content bg | `#F7F7F7` | **`#efebe4` beige** |
| Container bg | `#F7F7F7` | **`#efebe4` beige** |
| Nav icons | 20x20 SVG | **18x18 SVG** |
| User icon SVG | 18x18, strokeWidth 2 | **14x14, strokeWidth 2.5** |

### Files Modified
- `src/app/pws/pws.module.css`
- `src/app/pws/layout.tsx`

---

## Phase 1: Buyer-Facing Pages (Priority Order)

### 1.1 /pws/order (Shop Page) — DONE
- **File**: `src/app/pws/order/pwsOrder.module.css` (1129 lines)
- **Status**: Fixed — search input border, th font-weight 600, td padding 5.25px
- **Previous fixes**: Datagrid borders (border-collapse separate, border-left), offer price colors (green/orange RGB), dollar sign `::before`, cart qty red, spinner removal, toLocaleString, font weights
- **Action**: Screenshot QA vs local, catalog remaining discrepancies
- **Key elements to check**:
  - Datagrid borders (border-collapse separate, border-left on cells, no border-right)
  - Header row: bg `#F7F7F7`, font `13px/200`, color `#666766`, padding `2px`
  - Body cells: padding `19px left, 6px top/right, 5.25px bottom`
  - Filter row: input height `30px`, border `#D0D0D0`
  - Offer Price: green `rgb(64,120,116)` on `rgb(215,228,217)`, orange `rgb(225,115,56)` on `rgb(244,229,219)`
  - Dollar sign: `::before` pseudo-element with `content: "$"`
  - Cart Qty: red `rgb(235,51,0)` when > 0
  - Tabs: active tab with teal underline
  - Search bar: pill shape styling
  - Cart summary pill: bg `#d8e5d9`, border-radius `33px 0 0 33px`

### 1.2 /pws/cart (Cart Page) — SKIPPED (500 error)
- **File**: `src/app/pws/cart/cart.module.css` (590 lines) — td padding fixed to 5.25px
- **QA nav**: Click "My Offer" or cart icon from Shop
- **Key elements**:
  - Back button: underline, color `#102e33`
  - Page title "My Offer": `40px`, weight `500`
  - Cart summary pill: bg `#d8e5d9`, border-radius `33px 0 0 33px`
  - Datagrid: same border pattern as Shop
  - Remove column: trash icon styling
  - Submit button: green pill (`.pws-cart-submit`)
  - Offer Price / Ext. Price: dollar sign prefix, green/orange colors
  - Qty input: red when active, spinner removal
  - Empty state message

### 1.3 /pws/offer-review (Offer Review List) — DONE
- **File**: `src/app/pws/offer-review/offerReview.module.css`
- **Fixes**: pageTitle 40px/500/#1c1b1c, th font-weight 600, td padding 5.25px

### 1.4 /pws/offer-review/[offerId] (Offer Detail) — SKIPPED (500 error)
- **File**: shares `offerReview.module.css` — CSS fixes applied via 1.3
- **Note**: Page returns 500 from Next.js SSR; backend API works fine

### 1.5 /pws/counter-offers (Counter Offers List) — DONE
- **File**: `src/app/pws/counter-offers/counterOffers.module.css`
- **Fixes**: pageTitle 40px/500/#1c1b1c, dataGrid td padding 5.25px, offerListGrid td padding 5.25px

### 1.6 /pws/counter-offers/[offerId] (Counter Offer Detail) — DONE (shared CSS)
- **File**: shares `counterOffers.module.css` — fixes applied via 1.5

### 1.7 /pws/rma-requests (RMA Requests List) — DONE
- **File**: `src/app/pws/rma-requests/rmaRequests.module.css`
- **Fixes**: pageTitle 40px/500/#112d32, Request RMA button → dark pill (#102e33, 50px radius), tableContainer transparent, dataGrid th/td → beige #efebe4 with correct padding/weight

### 1.8 /pws/rma-requests/[rmaId] (RMA Detail) — DONE
- **File**: `src/app/pws/rma-requests/[rmaId]/rmaDetails.module.css`
- **Fixes**: rmaNumber weight 500/#112d32, dataGrid th/td → beige #efebe4 with correct padding/weight

---

## Phase 2: Admin Pages

### 2.1 /pws/rma-review (RMA Review List) — DONE
- **File**: `src/app/pws/rma-review/rmaReview.module.css`
- **Fixes**: pageTitle 40px/500/#112d32, dataGrid th/td → beige #efebe4 with correct padding/weight

### 2.2 /pws/rma-review/[rmaId] (RMA Review Detail) — DONE
- **File**: `src/app/pws/rma-review/[rmaId]/rmaReviewDetails.module.css`
- **Fixes**: rmaNumber weight 500/#112d32, dataGrid th/td → beige #efebe4 with correct padding/weight

### 2.3 /pws/pricing (Pricing Page)
- **File**: `src/app/pws/pricing/pricing.module.css` (762 lines)
- **Note**: Admin-only page
- **Key elements**:
  - Search bar (pill shape)
  - Datagrid with editable price cells
  - CSV upload button
  - Future price date picker modal
  - Price columns: current vs future, dollar formatting
  - Save / Upload button styles

### 2.3 /pws/pricing — DONE
- **Fix**: td padding 5px → 5.25px (title already correct)

### 2.4 /pws/inventory (Inventory Page) — DONE
- **File**: `src/app/pws/inventory/inventory.module.css`
- **Fix**: td padding 5px → 5.25px (title already correct)

---

## Phase 3: Cross-Page Consistency Audit

### 3.1 Shared Pattern Audit
- Side-by-side ALL page screenshots
- Verify consistency:
  - Page title sizing (40px/500wt vs 36px/300wt — both may be correct per QA)
  - Datagrid border patterns identical
  - Status badge colors consistent across offer-review, counter-offers, RMA
  - Button styles (pill radius, padding, font-size)
  - Background color `#efebe4` everywhere

### 3.2 Responsive Spot-Check (1024px)
- Screenshot 2-3 key pages at 1024px on both QA and local
- No pixel-perfect at 1024px — just no major breakage

---

## Common CSS Properties Checklist (Per Page)

| Element | Properties to Extract |
|---------|----------------------|
| Page background | `background-color` |
| Page title | `font-size`, `font-weight`, `color`, `font-family`, `line-height`, `margin` |
| Container padding | all 4 sides |
| Datagrid header | `background-color`, `font-size`, `font-weight`, `color`, `padding`, `border-bottom` |
| Datagrid filter inputs | `height`, `border`, `border-radius`, `background`, `font-size` |
| Datagrid body cell | `background-color`, `font-size`, `color`, `padding` (all 4), all 4 borders |
| Datagrid container | `border-collapse`, `border-spacing` |
| Status badges | `background-color`, `color`, `padding`, `border-radius`, `font-size`, `font-weight` |
| Buttons (primary) | `background-color`, `color`, `border`, `border-radius`, `padding`, `font-size`, `font-weight`, `height` |
| Input fields | `height`, `border`, `border-radius`, `background`, `font-size`, `padding`, `color` |
| Tabs (active/inactive) | `background-color`, `border-bottom`, `color`, `font-weight` |
| Dollar sign cells | `::before` content, color |
| Pagination bar | `margin`, `font-size`, `color` |

---

## Known Mendix CSS Patterns (from `migration_context/styling/EcoAtm.css`)

1. **Datagrid header**: bg `#F7F7F7`, padding `2px` all sides
2. **Datagrid cells**: padding `19px left, 6px top/right, 5.25px bottom`
3. **Form controls**: min-height `30px`, height `30px`, border `#D0D0D0`, padding `2px`
4. **Filter selector button**: height/width `30px`
5. **Pagination**: margin `16px` left/right
6. **Modal**: bg `#F7F7F7`, header `35px/500wt`
7. **Column header label**: `13px/200wt`, color `#666766`
8. **Sidebar width**: `56px` (confirmed via computed styles)
9. **Datagrid borders**: `border-collapse: separate`, `border-spacing: 0`, `border-left` on cells (no `border-right`)

---

## Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| QA session expires | Re-login with credentials above |
| QA has data local doesn't | Focus on CSS, not data. Seed test data if needed |
| Mendix CSS minified | Use `getComputedStyle()` not class names |
| Font "Founders Grotesk" differs | Verify `@font-face` in both environments |
| Admin pages need different creds | Use admin QA login (`ashirwadmittal`) |
| Windows HMR caching | Delete `.next/`, restart `npm run dev` |

---

## Success Criteria

- [ ] Phase 0: Layout shell matches QA — **DONE**
- [ ] Phase 1.1: Shop page validated
- [ ] Phase 1.2: Cart page matched
- [ ] Phase 1.3: Offer Review list matched
- [ ] Phase 1.4: Offer Detail matched
- [ ] Phase 1.5: Counter Offers list matched
- [ ] Phase 1.6: Counter Offer Detail matched
- [ ] Phase 1.7: RMA Requests list matched
- [ ] Phase 1.8: RMA Detail matched
- [ ] Phase 2.1: RMA Review list matched
- [ ] Phase 2.2: RMA Review Detail matched
- [ ] Phase 2.3: Pricing page matched
- [ ] Phase 2.4: Inventory page matched
- [ ] Phase 3: Cross-page consistency verified
- [ ] Each page committed separately
