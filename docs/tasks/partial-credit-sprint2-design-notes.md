---
Source: Figma file rYKB9vBqlJOFUuGN7GAgQS, node 173-600 ("Submit Credit Request" CANVAS)
File metadata name: "2026 Auction: Partial Credit"
Pulled on: 2026-05-11 via Figma MCP (mcp__figma__get_figma_data)
Purpose: Reference for Sprint 2 Next.js wizard implementation
Source dump: ~/.claude/projects/.../tool-results/mcp-figma-get_figma_data-1778541181379.txt (43,481 lines)
---

# Partial Credit Sprint 2 — Figma Design Notes

This document captures every load-bearing piece of copy, layout signal, and
design token surfaced by node 173-600 ("Submit Credit Request") of the
Partial Credit Figma file. It is intended as a paste-ready reference for the
wizard pages — quotes are verbatim from the Figma export.

---

## 1. Frame Inventory

### 1.1 Top-level SECTIONs (canvas children, in document order)

The CANVAS `Submit Credit Request` (id `173:600`) has **7 named SECTIONs**:

| # | SECTION name | Approx. line range | Purpose |
|---|---|---|---|
| 1 | `Start Request (R2 Certified)` | 171–1369 | Wizard entry — choose order + reasons |
| 2 | `Summary` | 1370–11154 | Step 5 summary (multiple variants: view + edit per reason) + Confirmation overlay |
| 3 | `Credit Requests Landing` | 12246–13544 | Buyer-facing list page (Filled + Empty variants) |
| 4 | `Credit Request Detail Page` | 13545–18978 | Single request read-only detail (Wrong / Missing / Approved variants) |
| 5 | `Encumbered` _(SECTION title text in file: "Encumbered Device")_ | 18979–21889 | Encumbered Devices step (entry, file uploaded, barcodes entered, summary) |
| 6 | `Wrong Device` | 21890–31307 | Wrong Devices step (barcodes entry, parsed table, photo modals) |
| 7 | `Missing Devices` | 31308–43480 | Missing Devices step (start, errors, file uploaded, photo required, barcodes, damage Yes/No, photo added, barcode list, add-more) |

The Title labels written into the canvas (the big PURPLE banner labels on
each section) are:
- `Start Request` (line 1366)
- `Summary` (line 1757 — also embedded title on Summary frames)
- `Credit Request Summary` (line 11153) — on the post-submit screen
- `Credit Requests Landing Page` (line 13541)
- `Credit Request Detail Page` (line 18975)
- `Encumbered Devices` (line 20312 / 21322)
- `Wrong Device` (line 23120)
- `Missing Device` (line 31330)
- `Missing Device Error States` (line 31348)

### 1.2 All named screen frames (`name:` at indent 12 — one per visual screen)

Verbatim names, in document order:

1. `Start Credit Request` (line 179) — empty/initial state, "Next" disabled
2. `Start Credit Request` (line 735) — populated state with order # filled in, reasons selected, "Next" active
3. `Request Credit - Summary` (line 1378) — Step 5 default summary view (all three reason groups collapsed/listed)
4. `Request Credit - Summary - Edit Missing Devices` (line 2867)
5. `Request Credit - Summary Wrong Devices` (line 4946) — wrong-devices detail block expanded
6. `Request Credit - Summary - Edit Wrong Devices` (line 6422)
7. `Request Credit - Summary Encumbered Devices` (line 8696) — encumbered detail expanded
8. `Request Credit Confirmation` (line 9591) — pre-submit "all sections expanded" view with Submit Request modal overlay
9. `Request Credit - Summary - Edit Encumbered Devices` (line 11157)
10. `Credit Requests Landing (Filled)` (line 12254)
11. `Credit Requests Landing (Empty)` (line 12990)
12. `Credit Request Details (Wrong Device, Pending Approval)` (line 13553)
13. `Credit Request Details (Missing Device, Pending Approval)` (line 15481)
14. `Credit Request Details (Missing Device, Pending Approval)` (line 18251) — **duplicate name**; second instance is actually the Approved/multi-reason variant
15. `Encumbered Device` (line 18987) — Step 4 entry (textarea + dropzone)
16. `Encumbered Device - File Uploaded` (line 19601)
17. `Encumbered Device - Barcodes Entered` (line 20316)
18. `Encumbered Device Summary` (line 20931) — parsed-list table with removal counts
19. `Wrong Device` (line 21898) — Step 3 entry
20. `Wrong Device - Barcodes Entered` (line 22502)
21. `Wrong Device - Actual Device Details` (line 23124) — first occurrence
22. `Wrong Device - Actual Device Details` (line 25071) — **duplicate name**; populated/edited state
23. `Wrong Device - Add Photos` (line 27068) — open Add-Photos modal
24. `Wrong Device - Edit Photos` (line 29122)
25. `Missing Device - Start` (line 31352) — Step 2 entry
26. `Missing Device - Start Errors` (line 32024) — same step with inline validation errors
27. `Missing Device - File Uploaded` (line 32715)
28. `Missing Device - Photo Required Error` (line 33465)
29. `Missing Device - Barcodes Entered` (line 34199)
30. `Missing Device - Yes Damage` (line 34866) — Yes radio + photos dropzone
31. `Missing Device - No Damage` (line 35593) — No radio selected, no photo zone
32. `Missing Device - Yes Damage - Photo Added` (line 36272)
33. `Missing Device - Barcode List` (line 37065) — parsed/listed barcodes after Next
34. `Add Missing Devices` (line 38947) — open Add-More-Devices modal

### 1.3 Apparent wizard flow order

The Step Horizontal indicator labels (extracted verbatim from instance text
nodes — see "Step indicator" below) show the 5 wizard steps:

```
Step 1: Overview
Step 2: Missing Device      (only when Missing Device reason was selected)
Step 3: Wrong Device        (only when Wrong Device reason was selected)
Step 4: Encumbered Device   (only when Encumbered reason was selected)
Step 5: Summary             (always)
```

End-to-end happy path (all three reasons selected):

```
Start Credit Request                                   (Step 1 — Overview)
  → Missing Device - Start                             (Step 2)
  → Missing Device - File Uploaded / Barcodes Entered  (Step 2 cont.)
  → Missing Device - Yes/No Damage [+ Photo Added]     (Step 2 cont.)
  → Missing Device - Barcode List                      (Step 2 review/parsed)
  → Wrong Device                                       (Step 3)
  → Wrong Device - Barcodes Entered                    (Step 3 cont.)
  → Wrong Device - Actual Device Details               (Step 3 parsed table)
  → Wrong Device - Add Photos / Edit Photos modal      (overlay over Step 3)
  → Encumbered Device                                  (Step 4)
  → Encumbered Device - File Uploaded                  (Step 4 cont.)
  → Encumbered Device - Barcodes Entered               (Step 4 cont.)
  → Encumbered Device Summary                          (Step 4 parsed)
  → Request Credit - Summary                           (Step 5)
  → [optional] Request Credit - Summary - Edit *       (inline edits)
  → Request Credit Confirmation [Submit Request modal] (overlay)
  → Confirmation modal: "Request submitted!"           (success)
```

If a reason checkbox is **unchecked**, that section's step disappears and
numbering compresses (e.g. only Wrong Device selected → Step 2 = Wrong
Device, Step 3 = Summary). The Step Horizontal Done state (green circle
with check icon) replaces the numeric label as the user advances.

---

## 2. Wizard Steps — Per-Step Content

### 2.1 Step 1 — Overview (`Start Credit Request`)

**Section title (purple Title banner):** `Start Request`

**Breadcrumb:** `All Credit Requests` (link with chevron icon)

**Page heading:** `Submit a Credit Request` (style `Display/lg`, fontSize 40,
Founders Grotesk Medium, color `#1C1B1C`)

**Step indicator state:**
- Step 1 `Overview` — Active (green circle `#14AC36` + white "1")
- Step 2 `Device Details` — Inactive (gray outline circle, gray "2")
- Step 3 `Summary` — Inactive

> Note: when this initial wizard renders (before reasons are checked), the
> indicator collapses Missing/Wrong/Encumbered into a single "Device Details"
> placeholder step. After reasons are selected, the indicator expands to the
> 5-step layout described above.

#### Card 1: Order Number

- Card heading: `What order is the request for?` (style `Display/sm`,
  fontSize 24)
- Field label: `Order Number` (style `Text/base (strong)`)
- Field placeholder (populated state): `XX-XXXX`
- Helper text (under field, populated state only — line 1076):
  `Partial credit requests must be made within 30 days of the order shipment date.`

#### Card 2: Reasons (checkbox group)

- Card heading: `Why are you requesting credit?` (style `Text/lg (strong)`)
- Subheading: `Select all that apply` (style `Text/sm`)
- Checkbox 1 label: `Missing Device`
- Checkbox 2 label: `Wrong Device (model, carrier, or capacity)`
- Checkbox 3 label: `Encumbered (iCloud locked, MDM locked, or blocklisted)`

(Checkboxes use Figma component "Checkbox / New". Default state has white
fill with `#5E656F` stroke, 1.5px; selected uses the green check icon.)

#### Buttons

- Primary CTA (right-aligned): `Next` (filled green `#14AC36`, opacity 0.4
  when disabled — empty state has no order # so it's grayed)

#### Header (Auction Header component)

- `Switch Buyer Code` (link text, color `#3C3C3C`)
- Buyer code chip: `ABCM` (large, Display/sm) + `ABC Mobile` (subdued
  Text/sm)
- User avatar block: `John Doe` + initials `JD` (white-on-color circle)

---

### 2.2 Step 2 — Missing Device

#### 2.2.1 Entry: `Missing Device - Start` (line 31352)

- Step indicator: `Overview` ✓ done · `Missing Device` ACTIVE · `Wrong
  Device` inactive · `Encumbered Device` inactive · `Summary` inactive
- Page heading: `Which devices are you missing?` (`Display/sm`)
- Subtitle: `Copy and paste the barcodes into the text field below or upload
  a file listing the barcodes.` (`Text/base`)
- Textarea placeholder: `Enter barcodes`
- Divider label: `**OR**` (bold, between the textarea and dropzone)
- File dropzone:
  - Icon: arrow-up-from-line
  - Primary copy: `Click or drag and drop file here to upload`
  - Helper: `Accepted formats: .xlsx, .csv, .docx`
- Question label: `Was the shipment damaged?` (`Text/base (strong)`)
- Radio options: `Yes` / `No`
- Buttons (right-aligned cluster): `Back` (white, gray border) + `Next`
  (green primary, disabled until valid)

#### 2.2.2 Validation errors: `Missing Device - Start Errors` (line 32024)

Same layout as above, plus inline error messages (red `#EB3300`,
`Text/sm`):
- Under barcode block: `Enter or upload the missing device barcodes`
- Under damage question: `Select an answer`

#### 2.2.3 File uploaded: `Missing Device - File Uploaded` (line 32715)

Replaces dropzone with file chip:
- Filename: `missingdevices.xlsx`
- Progress: `100%`
- Icon: `file-spreadsheet` + `xmark` to remove

#### 2.2.4 Photo required: `Missing Device - Photo Required Error` (line 33465)

After answering Yes to damage with no photo uploaded:
- New section heading: `Add photos of the damaged shipment`
- Dropzone: same `Click or drag and drop file here to upload`
- Inline red error: `Add a photo of the damaged shipment`

#### 2.2.5 Damage Yes path

- `Missing Device - Yes Damage` (line 34866) — radio Yes selected, photo
  dropzone visible (`Add photos of the damaged shipment` + drop zone)
- `Missing Device - Yes Damage - Photo Added` (line 36272) — adds photo chip
  - Filename: `img_98765.jpg` (verbatim — escaped underscore in source)
  - Progress: `100%`
  - `xmark` to remove

#### 2.2.6 Damage No path

- `Missing Device - No Damage` (line 35593) — radio No selected, no photo
  zone rendered, just Back/Next

#### 2.2.7 Parsed barcode list: `Missing Device - Barcodes Entered` (line 34199)

Same form, textarea pre-filled with comma-separated barcodes:
```
987654321, 987654321, 987654321, 987654321, 987654321, 987654321, 987654321,
987654321, 987654321, 987654321, 987654321, 987654321, 987654321, 987654321,
987654321, 987654321, 987654321
```

#### 2.2.8 Review table: `Missing Device - Barcode List` (line 37065)

After clicking Next, the parsed barcode list:
- Section heading: `Missing Devices` + count badge `(25)`
- Warning banner (line 37462): `Removed 1 duplicate and 2 barcodes that were not in the original order`
- Add-more button: `Plus` icon + `Add More Devices`
- Table column: `Missing Device Barcode` (single column with sort arrows)
- Per-row content example: `345678901 - Samsung Galaxy S21, 128 GB` with
  `xmark` icon to remove

#### 2.2.9 Add-more modal: `Add Missing Devices` (line 38947)

- Modal heading: `Add Missing Devices` (`Display/sm`)
- Subtitle: `Copy and paste the barcodes into the text field below or upload
  a file listing the barcodes.`
- Close: `xmark` icon
- Body: same textarea + OR + dropzone as the step entry
- CTA: `Add` (green primary)

---

### 2.3 Step 3 — Wrong Device

#### 2.3.1 Entry: `Wrong Device` (line 21898)

- Step indicator: `Overview` ✓ · `Missing Device` ✓ · `Wrong Device` ACTIVE
  · `Encumbered Device` 4 · `Summary` 5
- Heading: `Which devices were you expecting?` (`Display/sm`)
- Subtitle: `Copy and paste the barcodes into the text field below or upload
  a file listing the barcodes.`
- Textarea placeholder: `Enter barcodes`
- `**OR**` divider
- Dropzone: `Click or drag and drop file here to upload` /
  `Accepted formats: .xlsx, .csv, .docx`
- Buttons: `Back` / `Next`

#### 2.3.2 Barcodes entered: `Wrong Device - Barcodes Entered` (line 22502)

Pre-filled textarea (same comma-joined `987654321` placeholder string).

#### 2.3.3 Parsed table: `Wrong Device - Actual Device Details` (line 23124)

After Next, the user is shown a per-row form for each expected barcode to
identify the received device:

- Section heading: `Wrong Devices` + count badge `(16)`
- Table columns (with sort affordances `arrows-up-down`):
  - `Expected Device`
  - `Received Device` + helper `(IMEI or model name)`
  - `Photos` + helper `(optional)`
- Default per-row cells:
  - Expected Device: e.g. `345678901 - Samsung Galaxy S21, 128 GB`
  - Received Device cell: free-text input (no pre-fill) — `Enter IMEI or model name` is implied; some rows show `356938035643809` (an IMEI) or `Apple iPhone 12` (model)
  - Photos cell: action button `plus` icon + `Add Photos`, with row-delete
    `xmark` on the far right
- Once photos are attached, the Photos cell shows:
  - `pen` icon + `Edit Photos`
  - `circle-check` icon + `2 photos` (status)

The populated variant `Wrong Device - Actual Device Details` (line 25071)
shows a mix of:
- Row with IMEI `356938035643809` + `2 photos`
- Row with model name `Samsung Galaxy S20` + `Add Photos`
- Other rows: `Add Photos`

Buttons: `Back` / `Next`

#### 2.3.4 Add Photos modal: `Wrong Device - Add Photos` (line 27068)

Modal copy:
- Heading: `Add Photos` (`Display/sm`)
- Subheading: `Received Device: Samsung Galaxy S20` (varies per row)
- Close: `xmark`
- Dropzone: `Click or drag and drop file here to upload`
- CTA: `Add` (green primary)

#### 2.3.5 Edit Photos modal: `Wrong Device - Edit Photos` (line 29122)

Modal copy:
- Heading: `Edit Photos`
- Subheading: `Received Device: 356938035643809` (IMEI of the row)
- Close: `xmark`
- Dropzone (same)
- File thumbnails listed (uploaded photos):
  - `img_98765.jpg` · 100%
  - `img_98766.jpg` · 100%
  - Each with `xmark` to remove
- CTA: `Save` (green primary)

---

### 2.4 Step 4 — Encumbered Device

#### 2.4.1 Entry: `Encumbered Device` (line 18987)

- Step indicator: `Overview` ✓ · `Missing Device` ✓ · `Wrong Device` ✓ ·
  `Encumbered Device` ACTIVE (number `4`) · `Summary` 5
- Heading: `Which devices are encumbered?` (`Display/sm`)
- Subtitle: `Copy and paste the barcodes into the text field below or upload
  a file listing the barcodes.`
- Textarea placeholder: `Enter barcodes`
- `**OR**` divider
- Dropzone (same as Missing): `Click or drag and drop file here to upload`
  / `Accepted formats: .xlsx, .csv, .docx`
- Buttons: `Back` / `Next`

> Note: there is **no** "if you are not R-2 certified, submit an RMA instead"
> warning inside this wizard step. That copy appears on the Landing page
> instead (see §5).

#### 2.4.2 File uploaded: `Encumbered Device - File Uploaded` (line 19601)

- Filename chip: `encumbereddevices.xlsx` · `100%` · `file-spreadsheet` icon
  · `xmark` to remove

#### 2.4.3 Barcodes entered: `Encumbered Device - Barcodes Entered` (line 20316)

Pre-filled textarea (same comma string).

#### 2.4.4 Summary table: `Encumbered Device Summary` (line 20931)

- Section heading: `Encumbered Devices` + count `(8)`
- Warning banner: `Removed 1 duplicate and 2 barcodes that were not in the original order`
- Add-more button: `Plus` icon + `Add More Devices`
- Table column: `Encumbered Device Barcode` (single column)
- Per-row content: `345678901 - Samsung Galaxy S21, 128 GB` with `xmark` to
  delete (rows include Samsung Galaxy S21, Apple iPhone 13, Google Pixel 6,
  Xiaomi Mi 11, Sony Xperia 5 III, Oppo Find X3 Pro, Motorola Edge 20,
  Huawei P40 Pro)
- Buttons: `Back` / `Next`

---

### 2.5 Step 5 — Summary

#### 2.5.1 Default view: `Request Credit - Summary` (line 1378)

- Section title (purple banner): `Summary`
- Step indicator: all four prior steps `✓` done, Step 5 `Summary` ACTIVE
- Summary card (top, white) — three labeled metadata rows:
  - `Order Number` → `XXXXXX-XX`
  - `Request Reasons` → `Missing Device, Wrong Device, Encumbered Device`
  - `Total Devices` → `49`
- Submit row buttons (right-aligned): `Submit Request` (green primary)

Below the summary card, **collapsed group cards** for each selected reason:
- `Missing Devices` (collapsed)
- `Wrong Devices` (collapsed)
- `Encumbered Devices` (collapsed)

#### 2.5.2 Group expansions (used in Edit / Detail variants)

When a group is expanded:
- Group header: `Missing Devices` + `(25)` count
- Edit button: `Pen` icon + `Edit`
- Show/hide toggle: `Show Details` (`Chevron-down`) / `Hide Details` (`Chevron-up`)

Missing Devices table columns:
- `Barcode` (sort)
- `Device Description` (sort)
- `Amount Paid` (sort — only in detail/summary view)

Sample rows: `3020250619182236283058` · `Samsung Galaxy S26` / `Samsung
Galaxy S26, 256 GB` · `$XX`

Wrong Devices table columns (in summary expanded):
- `Expected Device`
- `Received Device` + `(IMEI or model name)`
- `Photos` + `(optional)`

Sample rows: `345678901 - Samsung Galaxy S21, 128 GB` · `356938035643809` ·
`2 photos`

Encumbered Devices table columns: `Barcode` · `Device Description`

#### 2.5.3 Edit variants

- `Request Credit - Summary - Edit Missing Devices` (line 2867)
- `Request Credit - Summary - Edit Wrong Devices` (line 6422)
- `Request Credit - Summary - Edit Encumbered Devices` (line 11157)

Each Edit variant:
- Replaces the `Pen + Edit` button with `Plus` icon + `Add More Devices`
- Adds a row-delete `xmark` to every row
- Footer for the edited block: `Cancel` (gray) + `Save` (green primary)

#### 2.5.4 Confirmation view: `Request Credit Confirmation` (line 9591)

Pre-submit screen: all three groups expanded with full tables; bottom row
shows `Edit` (gray with `Pen` icon) · `Cancel` (gray) · `Submit Request`
(green primary).

#### 2.5.5 Submit confirmation modal (overlay, line 11097)

- Triggered by Submit Request
- Modal width: 520px
- Background scrim: `rgba(28, 27, 28, 0.5)` (`fill_6LXOV6`)
- Modal panel background: `#F0F6EF` (`fill_VYR7YF`) — light green
- Modal border: 12px stroke, `#F7F5F1`
- Centered content:
  - Icon: `circle-check` (Font Awesome solid, fontSize 32, green `#14AC36`)
  - Heading: `Request submitted!` (`fontFamily: Founders Grotesk`, Medium
    500, fontSize 26)
- No explicit buttons captured in this overlay — it appears to be an
  auto-dismiss / "Done" message; clicking the scrim or waiting routes back
  to the landing page.

---

## 3. Visual Hierarchy / Layout Signals

### 3.1 Step indicator

- Component name (from Figma library): `Step Horizontal` (componentSet
  `244:1736`)
- Three states:
  - `State=Active` — filled green circle (`#14AC36`), white numeral, label
    in `Text/sm (strong)` color `#1C1B1C`
  - `State=Inactive` — white circle with gray border (`#A8A7A6`),
    numeral color `#7D7B7A`, label color `#534F4C`, weight `Text/sm`
  - `State=Done` — green filled circle with white `check` icon (Font
    Awesome) replacing the numeral, label in `Text/sm (strong)` (active
    color treatment)
- Layout: row, `justify-content: space-between`, width 800px, with a
  horizontal divider line (`Line`, stroke `#A8A7A6`) running behind the
  circles to connect them
- Steps are rendered horizontally and centered above the form card

### 3.2 Layout structure

- Two-zone layout: a **fixed left rail** (50px wide, gradient
  `linear-gradient(136deg, #14AC36 0%, #00969F 84%)`, containing the
  hamburger / book-open / turn-down-left icons) AND a top-aligned
  **horizontal header** (Auction Header component, 1390px wide, padding
  24px, white background)
- Main content area: column layout, gap 32px, padding 24px, max width 1390px
- Form cards within a step: white background `#FFFFFF`, padding `24px 32px`,
  gap 16px, radius 8px, box-shadow `0px 1px 2px -1px rgba(0,0,0,0.1),
  0px 1px 3px 0px rgba(0,0,0,0.1)` (`shadow` token)
- Button group container: `flex-end` aligned, fixed width 800px, gap 8px
- Page-level background: `#F7F7F7` (`fill_82WWSQ`)

### 3.3 Sidebar vs full-width

The submit wizard pages are **full-width** with only the narrow 50px green
gradient rail on the left (icons only — no labeled nav links). There is no
labeled sidebar in any wizard frame. The landing and detail pages also use
the same 50px icon rail.

### 3.4 Where `Cancel` lives

- **Edit variants on Summary**: `Cancel` sits **bottom-right**, immediately
  left of `Save` — both in the right-aligned button cluster (line 11061
  reference; same pattern for Edit Missing / Wrong / Encumbered).
- **Pre-submit Confirmation view**: `Cancel` sits between `Edit` and
  `Submit Request` in the bottom-right cluster (line 11074).
- There is **no top-right "Cancel" link** in any of the screens — the
  Auction Header has no Cancel control.
- Modal-style overlays (Add Photos / Edit Photos / Add Missing Devices) use
  `xmark` (close icon) in the top-right of the modal panel — these are the
  modal close affordance, functionally equivalent to Cancel.

### 3.5 Color tokens (hex codes extracted)

| Token / role | Hex | Source name |
|---|---|---|
| Page background | `#F7F7F7` | `fill_82WWSQ` |
| White / card / row | `#FFFFFF` | `fill_HR391A`, `Primary/White`, `White` |
| Primary text / Blackish | `#1C1B1C` | `fill_Z5L1IT`, `Blackish` |
| Primary CTA (green) | `#14AC36` | `fill_CRTE1J`, `Eco Green` |
| Step-active accent dark green | `#348033` | `fill_AE64UT` (gradient overlay used in left rail framing) |
| Auction gradient start | `rgba(20,172,54,1)` | gradient/Auction Gradient |
| Auction gradient end | `rgba(0,150,159,1)` (#00969F) | gradient/Auction Gradient |
| Secondary text gray | `#3C3C3C` | `fill_RIPGWA` |
| Tertiary muted text | `#534F4C` | `fill_DFFO4N` |
| Step inactive numeral | `#7D7B7A` | `fill_OTEC8F` |
| Inactive border / divider | `#A8A7A6` | `fill_TEY4IP` |
| Field outline / checkbox | `#5E656F` | `fill_7RS4YY` |
| Error red (inline + State=Error) | `#EB3300` | `fill_CDVCMD` |
| Warning orange | `#F26B21` | `fill_3AGKW1` |
| Light-button background | `#EFECE4` | `Light Gray` |
| Disabled overlay | `#505050` (50% black scrim variant) | `fill_DCTDB6` (section bg) |
| Modal scrim | `rgba(28,27,28,0.5)` | `fill_6LXOV6` |
| Modal success panel | `#F0F6EF` | `fill_VYR7YF` |
| Modal success border | `#F7F5F1` | `fill_1DOFH5` |
| Title banner (purple) | `#E3D6FF` | `fill_7JF292` |
| Subtle off-white surface | `#FBFAF8` | `fill_RZJGFK` |
| Pure black | `#000000` | `fill_0PU20L` (used for checkbox labels) |

### 3.6 Typography

Primary font: **Founders Grotesk** (Regular 400 / Medium 500).
Display title (purple Title banner only): **Inter Bold 700**, 80px, -5%
letter-spacing.

| Token | Family | Style | Weight | Size | Notes |
|---|---|---|---|---|---|
| `Display/lg` | Founders Grotesk | Medium | 500 | 40 | Page headings ("Submit a Credit Request") |
| `Display/sm` | Founders Grotesk | Medium | 500 | 24 | Card headings ("What order is the request for?", modal headings) |
| `Text/lg (strong)` | Founders Grotesk | Medium | 500 | 24 | Card titles in reason card |
| `Text/base` | Founders Grotesk | Regular | 400 | 18 | Body copy, subtitles |
| `Text/base (strong)` | Founders Grotesk | Medium | 500 | 18 | Field labels, "Photo of damage", "Was the shipment damaged?" |
| `Text/sm` | Founders Grotesk | Regular | 400 | 16 | Helper text, checkbox labels, error messages |
| `Text/sm (strong)` | Founders Grotesk | Medium | 500 | 16 | Step labels (when active/done), button text |
| `Text/sm (strong) (189:520)` | Founders Grotesk | Medium | 500 | 16 | Button text variant (CENTER + TOP) |
| `Text/xs` | _(not explicitly defined in the slice; inferred fontSize 14)_ | Regular | 400 | 14 | "Accepted formats" helper |
| `Desktop Web/P` | Founders Grotesk | Regular | 400 | 18 | Placeholder text in inputs |
| `style_9HNP7J` (Title banner) | Inter | Bold | 700 | 80 | Big purple page section titles only |
| `style_CAKW1V` (modal heading) | Founders Grotesk | Medium | 500 | 26 | "Request submitted!" |

### 3.7 Spacing scale

Observed from layout tokens:
- Container padding: 24px (header zone), `24px 32px` (form cards)
- Stack gap between cards: 16px
- Stack gap between sections of page: 32px
- Button height: 40px
- Button width (typical): 200px
- Button padding: `8px 16px`
- Field input padding: `0px 16px`
- Modal panel: 520px wide, 164px tall (success modal)
- Form card max width: 800px
- Step indicator block: width 800px, height 68px
- Wizard rail icon column: 50px wide, full height
- Section content max width: 1390px (after the 50px left rail)

### 3.8 Effects

- Card shadow: `box-shadow: 0px 1px 2px -1px rgba(0,0,0,0.1), 0px 1px 3px 0px rgba(0,0,0,0.1)`
- Modal border: 12px solid `#F7F5F1` (gives a "halo" framing effect)

---

## 4. Submit Confirmation Modal

(See §2.5.5 for full visual treatment.)

Verbatim copy:
- Icon: `circle-check`
- Heading: `Request submitted!`

There are **no buttons rendered in the modal** (no OK / Done / Close).
Implementation note: treat as a transient confirmation toast/modal — close
the dialog programmatically after a short delay or on overlay click and
route the user back to `Credit Requests Landing`.

Pre-submit Submit-Request affordance lives in the bottom bar:
- `Edit` (gray secondary, with `Pen` icon) — collapses summary back to view
- `Cancel` (gray secondary) — exits the wizard / discards request
- `Submit Request` (green primary) — opens the confirmation modal

Both `Cancel` and `Submit Request` buttons share the same `200px × 40px`
chip dimensions used everywhere else.

---

## 5. Buyer Landing / List Page — `Credit Requests Landing`

Two frames captured: `Credit Requests Landing (Filled)` (line 12254) and
`Credit Requests Landing (Empty)` (line 12990).

### 5.1 Page chrome

- Section title (purple Title banner): `Credit Requests Landing Page`
- Page heading: `Credit Requests` (`Display/lg` 40px)
- Top-right of page heading row:
  - Link: `Credit Request Policy`
  - Primary button: `Submit a Credit Request` (green primary, leads to
    Step 1)
- **Banner text directly below the heading row** (`Text/base`):
  > "Have an issue with your order? You may be eligible for a credit. We’ll
  > review claims on missing, incorrect, or encumbered devices. If you are
  > not R-2 certified and received an encumbered device, you must submit an
  > RMA instead."

(This is the only place where the R-2 not-certified copy appears in the
slice.)

### 5.2 Table columns (Filled variant)

In order, with their associated icons / sort affordances:

| Column header | Sort | Extra |
|---|---|---|
| `Date Submitted` | `arrows-up-down` | `calendar-days` icon picker / filter |
| `Order Number` | `arrows-up-down` | `Ab` filter chip |
| `Request Reasons` | `arrows-up-down` | `Ab` filter chip |
| `Status` | `arrows-up-down` | `Ab` filter chip |
| _(action)_ | — | `eye` view icon |

> Note: the Empty variant uses the singular column name `Request Reason`
> (not `Request Reasons`). The Filled variant uses the plural — kept as is
> in the export, treat the plural as canonical for buyer-facing copy.

### 5.3 Row data

Three sample rows in Filled:

| Date | Order # | Reasons | Status |
|---|---|---|---|
| `mm/dd/yy` | `XX-XXXXXX` | `Missing Device` | `Pending Approval` |
| `mm/dd/yy` | `XX-XXXXXX` | `Missing Device, Wrong Device` | `Declined` |
| `mm/dd/yy` | `XX-XXXXXX` | `Wrong Device, Encumbered Device` | `Approved` |

### 5.4 Status badge styling

Status values appear as plain text inside framed pills (`Frame 14606684`)
with a 0.81px stroke `#A8A7A6`. In this slice all three statuses are
rendered with the **same neutral light pill treatment** — no distinct
color tokens per status are present in the Figma slice itself. (Status
colors are presumably handled by the live `_Status Chip` component variants
referenced as `496:3761` in the componentSet, but those variants aren't
expanded inline in this dump.) Implementation should map:

- `Pending Approval` → neutral / warning (orange `#F26B21` is available)
- `Declined` → error (red `#EB3300`)
- `Approved` → success (green `#14AC36`)

### 5.5 Filter chip pattern

Column-header filter chips are rendered as small `Ab` text inside a
rectangular frame with 0.81px stroke `#A8A7A6` — they sit immediately to
the right of the sort arrows for `Order Number`, `Request Reasons`, and
`Status`. The Date Submitted column uses a `calendar-days` icon picker
instead.

### 5.6 Empty state

Frame: `Credit Requests Landing (Empty)`. Same table chrome, but body
content is a single full-row message:
- Copy: `There are currently no Partial Credit Requests`

### 5.7 Detail page table columns

(For reference — `Credit Request Detail Page` frames show how requested
items are displayed when reading an existing request.)

**Wrong Devices detail table** (`Credit Request Details (Wrong Device, Pending Approval)`):

| Column | Notes |
|---|---|
| `Expected Device Barcode` | |
| `Expected Device Description` | |
| `Received Device IMEI/Serial` | |
| `Received Device Description (if IMEI unknown)` | label has parenthetical helper |
| `Photos` | shows photo count |
| `Amount Paid` | `$XX` |

Header strip on the detail page:
- `Order Number` `XXXXXX-XX`
- `Request Date` `mm/dd/yy`
- `Request Reason` `Wrong Device` _(or comma-joined list)_
- `Status` `Pending Approval` _(or `Approved` + `Approved Date mm/dd/yy`)_
- `Download` button (`arrow-down-to-line` icon)

**Missing Devices detail table:**

| Column |
|---|
| `Box Number` (e.g. `XX-2458`) |
| `Barcode` |
| `Brand` (e.g. `Apple`) |
| `Model Description` (e.g. `iPhone XR 64GB Other A1984/A2105/A2106/A2108`) |
| `Amount Paid` (`$XX`) |

Below the missing devices table the detail page repeats the damage prompt:
- `Shipment damaged?` `Yes`
- `Photo of damage` `1 photo`

This is the rendered/saved equivalent of the in-wizard "Was the shipment
damaged?" question.

---

## 6. Anomalies / Notes for Implementation

1. **Duplicate frame names.** Three pairs of frames share names:
   - Two `Start Credit Request` frames (empty vs filled state).
   - Two `Credit Request Details (Missing Device, Pending Approval)` frames
     (the second at line 18251 is actually the multi-reason **Approved**
     variant; rename when porting).
   - Two `Wrong Device - Actual Device Details` frames (the second at line
     25071 is the populated/edited state with mixed IMEI / model rows).
   Treat each pair as before/after states of the same screen.
2. **No R-2 warning in the Encumbered wizard step.** The "if you are not
   R-2 certified ... must submit an RMA instead" copy lives only on the
   Landing page banner (§5.1), not inside the Step 4 Encumbered Devices
   form. If product wants an inline warning during the wizard, that copy
   needs to be added (or sourced from the legacy Mendix page).
3. **"30-day window" copy** appears only on the populated/active variant of
   `Start Credit Request` (line 1076), not on the empty-state variant. It
   should be rendered as helper text under the Order Number field on
   render.
4. **Singular vs plural — Reasons column.** Filled landing says
   `Request Reasons`, Empty landing says `Request Reason`. Use plural
   everywhere.
5. **"Removed N duplicates, M not in order" banner** is consistently
   worded as `Removed 1 duplicate and 2 barcodes that were not in the
   original order` (lines 21348, 37462, 39344). The grammar handles 1 vs N
   with "duplicate"/"duplicates" and "barcode"/"barcodes" — productionize
   this with i18n pluralization.
6. **Modal close consistency.** Add Photos / Edit Photos / Add Missing
   Devices all close via top-right `xmark`. No labeled Cancel button inside
   the modals. The CTA varies by modal: `Add` for Add Photos / Add Missing
   Devices, `Save` for Edit Photos.
7. **Submit confirmation modal has no explicit dismiss control.** The Mendix
   legacy probably auto-dismisses after a few seconds and routes to the
   landing page; honor that behavior.
8. **Step Horizontal indicator collapses pre-reason-selection.** Initial
   `Start Credit Request` (line 179) shows just 3 generic steps
   (`Overview` / `Device Details` / `Summary`) until reasons are chosen.
   After reasons are selected, the populated variant (line 735) and every
   subsequent step show all 5 numbered steps. Implementation must compute
   the indicator state from the selected reasons.
9. **`Total Devices` count is the sum across all selected reasons** (the
   Summary card shows `49` when `25 + 16 + 8 = 49`).
10. **Filename / progress chips** for uploaded files contain escaped
    underscores in the source (`img\_98765.jpg`, `img\_98766.jpg`).
    Don't ship the backslash — that's a YAML-escape artifact.
11. **The Auction Header is a shared component** (`Auction Header`,
    componentId `584:2721`) — pull it from the existing shell, don't
    rebuild. It contains: EcoAtm logo, Switch Buyer Code link, buyer code
    chip (e.g. `ABCM` / `ABC Mobile`), user name + avatar initials.
12. **No table is virtualized or paginated in the Figma slice.** The
    Wrong/Missing detail tables show 20+ rows inline — real implementation
    should plan virtualization or pagination for large batches.
13. **Order Number input placeholder** is `XX-XXXX` in the populated
    variant but the field is empty in the initial variant — render as
    placeholder text (not a default value).
14. **All buttons are 200px × 40px** in the wizard footers — this is wider
    than typical and should be respected for visual parity with Mendix.
15. **The wizard `Cancel` only appears on Summary**, not on intermediate
    steps. On Steps 1–4, the only "back-out" affordance is the `Back`
    button (which retreats one step) — there is no Cancel button. To
    abandon the wizard, the user must navigate via the breadcrumb
    `All Credit Requests`.

---

## 7. Quick-reference copy block (for paste-in)

```text
// Step 1
heading: "Submit a Credit Request"
orderCardHeading: "What order is the request for?"
orderFieldLabel: "Order Number"
orderPlaceholder: "XX-XXXX"
orderHelper: "Partial credit requests must be made within 30 days of the order shipment date."
reasonsCardHeading: "Why are you requesting credit?"
reasonsSubheading: "Select all that apply"
reasonMissing: "Missing Device"
reasonWrong: "Wrong Device (model, carrier, or capacity)"
reasonEncumbered: "Encumbered (iCloud locked, MDM locked, or blocklisted)"

// Step 2 — Missing
missingHeading: "Which devices are you missing?"
barcodeSubtitle: "Copy and paste the barcodes into the text field below or upload a file listing the barcodes."
textareaPlaceholder: "Enter barcodes"
orDivider: "OR"
dropzonePrimary: "Click or drag and drop file here to upload"
dropzoneHelper: "Accepted formats: .xlsx, .csv, .docx"
damageQuestion: "Was the shipment damaged?"
damageYes: "Yes"
damageNo: "No"
damagePhotoLabel: "Add photos of the damaged shipment"
errorBarcodeRequired: "Enter or upload the missing device barcodes"
errorDamageRequired: "Select an answer"
errorDamagePhoto: "Add a photo of the damaged shipment"
addMoreModalHeading: "Add Missing Devices"
addMoreCta: "Add"

// Step 3 — Wrong
wrongHeading: "Which devices were you expecting?"
wrongTableExpected: "Expected Device"
wrongTableReceived: "Received Device"
wrongTableReceivedHelper: "(IMEI or model name)"
wrongTablePhotos: "Photos"
wrongTablePhotosHelper: "(optional)"
addPhotosModalHeading: "Add Photos"
editPhotosModalHeading: "Edit Photos"

// Step 4 — Encumbered
encumberedHeading: "Which devices are encumbered?"

// Step 5 — Summary
summaryOrderLabel: "Order Number"
summaryReasonsLabel: "Request Reasons"
summaryTotalLabel: "Total Devices"
groupMissing: "Missing Devices"
groupWrong: "Wrong Devices"
groupEncumbered: "Encumbered Devices"
toggleShow: "Show Details"
toggleHide: "Hide Details"
btnEdit: "Edit"
btnAddMore: "Add More Devices"
btnCancel: "Cancel"
btnSave: "Save"
btnSubmit: "Submit Request"
btnBack: "Back"
btnNext: "Next"
parsedBanner: "Removed {duplicates} duplicate{plural1} and {invalid} barcode{plural2} that were not in the original order"

// Confirmation
confirmationHeading: "Request submitted!"

// Landing
landingHeading: "Credit Requests"
landingPolicyLink: "Credit Request Policy"
landingCta: "Submit a Credit Request"
landingBanner: "Have an issue with your order? You may be eligible for a credit.  We’ll review claims on missing, incorrect, or encumbered devices. If you are not R-2 certified and received an encumbered device, you must submit an RMA instead."
landingEmpty: "There are currently no Partial Credit Requests"
colDate: "Date Submitted"
colOrder: "Order Number"
colReasons: "Request Reasons"
colStatus: "Status"
statusPending: "Pending Approval"
statusDeclined: "Declined"
statusApproved: "Approved"

// Detail page
detailHeading: "Credit Request Details"
detailBack: "All Credit Requests"
detailLabelOrder: "Order Number"
detailLabelDate: "Request Date"
detailLabelReason: "Request Reason"
detailLabelStatus: "Status"
detailLabelApprovedDate: "Approved Date"
detailDownload: "Download"
detailShipmentDamaged: "Shipment damaged?"
detailPhotoOfDamage: "Photo of damage"

// Wrong devices detail columns
wd_col_expBarcode: "Expected Device Barcode"
wd_col_expDesc: "Expected Device Description"
wd_col_rxImei: "Received Device IMEI/Serial"
wd_col_rxDesc: "Received Device Description (if IMEI unknown)"
wd_col_photos: "Photos"
wd_col_amount: "Amount Paid"

// Missing devices detail columns
md_col_box: "Box Number"
md_col_barcode: "Barcode"
md_col_brand: "Brand"
md_col_modelDesc: "Model Description"
md_col_amount: "Amount Paid"
```
