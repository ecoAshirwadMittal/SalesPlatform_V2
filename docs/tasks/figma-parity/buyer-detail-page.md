# Figma parity — Buyer detail page

## Surface 7: Buyer detail (/wholesale/partial-credit/[id])

**Figma node:** `534-11895` (the id supplied by the caller) **did NOT
resolve** — the Figma API returned `Node 534:11895 was not found`. The
nearest matching SECTION in the "2026 Auction: Partial Credit" file is
`534:11349` "Credit Request Detail Page", which contains four child
variants:

| Child id | Variant name |
|---|---|
| `534:11463` | Credit Request Details (Wrong Device, Pending Approval) |
| `534:11820` | Credit Request Details (Missing Device, Pending Approval) |
| `651:2421` (≈) | Credit Request Details (Multi Reasons, Partially Approved) |
| `656:3735` (≈) | Credit Request Details (Multi Reasons, Partially Approved) — alt |

This review uses `534:11349` and its children as the canonical reference
for the buyer-side detail screen.

**Local files:**
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/page.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/detail.module.css`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/_components/BuyerHeaderStrip.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/_components/BuyerSummaryPanels.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/_components/BuyerLineSection.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/_components/PhotoUploadDropzone.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/_components/PhotoGallery.tsx`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/_components/ReviewSummaryPanel.tsx`

**Variants reviewed:**
- Wrong Device, Pending Approval (Figma child `534:11463`)
- Missing Device, Pending Approval (Figma child `534:11820`)
- Multi-Reasons, Partially Approved (Figma child `651:2421` / `656:3735`)

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Copy / Layout | Header strip field set is wrong. Figma has 5 fields (Order Number, Request Date, Request Reason, Status pill, Approved Date). Code has 5 (Request Date, Company, Order Number, Request Reason, Status) — "Company" is invented, "Approved Date" is missing. | `Order Number / Request Date / Request Reason / Status / Approved Date` | `Request Date / Company / Order Number / Request Reason / Status` | Drop "Company" from `BuyerHeaderStrip.tsx`. Add an "Approved Date" field that renders `reviewCompletedOn` when present and `'—'` otherwise (or hide the field pre-review). |
| CRITICAL | Copy / Layout | Missing Device table columns disagree. Figma: `Box Number / Barcode / Brand / Model Description / Amount Paid`. Code: `Barcode / Brand / Model / Grade / Amount Paid`. Code is missing **Box Number** and has an unspec'd **Grade** column. | 5 cols incl. Box Number | 5 cols incl. Grade, no Box Number | Update `BuyerLineSection.tsx` MISSING branch: replace `Grade` with `Box Number` (sourced from `line.boxNumber` — confirm DTO field name with admin variant). Rename column header from "Model" to "Model Description". |
| CRITICAL | Copy / Layout | Wrong Device table columns disagree heavily. Figma: `Expected Device Barcode / Expected Device Description / Received Device IMEI/Serial / Received Device Description / Photos / Amount Paid` (6 cols). Code: `Expected Barcode / Expected Model / Actual Model / Amount Paid / Latest Price` (5 cols). Code is missing a **Photos** column and a **Received Device IMEI/Serial** column; "Latest Price" is invented (not in Figma); column labels diverge. | 6 cols incl. Photos, IMEI/Serial | 5 cols, no Photos column, includes Latest Price | Update `BuyerLineSection.tsx` WRONG branch: replace `Expected Barcode → Expected Device Barcode`, `Expected Model → Expected Device Description`, `Actual Model → Received Device Description`, add `Received Device IMEI/Serial` between them, add per-line `Photos` count cell, remove `Latest Price`. |
| CRITICAL | Copy / Layout | Encumbered Device table columns disagree. Figma columns include `Barcode / Device Description / Credit Due`. Code: `Barcode / Brand / Model / Amount Paid / Actual Value`. "Brand" + "Model" should collapse to "Device Description"; "Amount Paid" + "Actual Value" should collapse to "Credit Due". | `Barcode / Device Description / Credit Due` | `Barcode / Brand / Model / Amount Paid / Actual Value` | Replace `Brand`+`Model` with a single concatenated "Device Description" cell. Replace `Amount Paid`+`Actual Value` with a single "Credit Due" cell (server should expose `amountPaid - actualValue` or similar — confirm with admin variant). |
| CRITICAL | Layout | Summary panel column count is wrong. Figma shows `Qty / Total` (2 columns). Code shows `SKUs / Qty / Total` (3 columns). The buyer-side summary is intentionally narrower in Figma. | 2 cols `Qty / Total` | 3 cols `SKUs / Qty / Total` | Drop the SKUs column from `BuyerSummaryPanels.tsx` and from `detail.module.css` `.summaryPanelTable { grid-template-columns: repeat(3, …) }` (change to 2). The `BuyerSummaryPanelsProps.requestedSkus / approvedSkus` props become unused → also drop the count helpers in `page.tsx`. |
| CRITICAL | Layout / Behaviour | Missing "Download" CTA. Figma shows a "Download" button (with `arrow-down-to-line` icon, `Text/sm (strong)` label) anchored to the summary row in every variant. Code has no Download control anywhere on the buyer page. | Download button rendered alongside summary panels | Absent | Add a Download button to the page (export a per-request PDF / CSV). If the export is out of scope for Sprint 4, raise a follow-up ticket — but the Figma absence is conspicuous enough to be flagged here. |
| CRITICAL | Layout / Behaviour | Multi-reasons variant uses **tabs**, code uses **stacked sections**. Figma "Multi-Reasons" variant renders one section at a time with tab navigation: `Missing Devices` / `Wrong Devices` / `Encumbered Devices` (component `Tabs - Partial Credit`, `651:2878`). It also renders an Approved/Declined toggle under each tab ("Approved (10)" / "Declined (6)") — component `Toggle - Partial Credit`, `656:3480`. Code stacks all three sections vertically and never separates Approved vs Declined lines. | Tabs + Approved/Declined toggle | Three stacked sections, decisions intermixed | Introduce a tab control between summary and reason sections for finalised requests with > 1 reason. Add an Approved/Declined toggle inside each tab section once the request is final. |
| HIGH | Typography | Wrong font family. Figma uses **Founders Grotesk** for every text style (`Text/base`, `Display/lg`, `Display/sm`, `style_3ZMRX8` etc.). Code's `detail.module.css` uses `var(--font-family-primary)` which resolves to **Brandon Grotesque**. `globals.css` declares a `.pws` Founders Grotesk variable but this page doesn't opt into it. | `Founders Grotesk` (400 / 500) | `Brandon Grotesque` (via `--font-family-primary`) | Add a Founders Grotesk variable (e.g. `--font-family-pws`) and apply it at `.page` or scope this page under the PWS layout that already loads Founders Grotesk. Same fix is owed by the other buyer surfaces. |
| HIGH | Typography | Page heading is too small and uses the wrong weight stack. Figma "Credit Request Details" uses `Display/lg` (40px / 500). Code `.pageHeading` is 28px / 500. | 40px, weight 500, line-height 110% | 28px, weight 500 | Bump `.pageHeading` font-size to 40px and add `line-height: 1.1`. |
| HIGH | Copy | Page heading text is wrong. Figma title is **"Credit Request Details"**. Code renders `detail.requestNumber` (e.g. `PC-001234`). | `Credit Request Details` | `{requestNumber}` literal | Render `<h1>Credit Request Details</h1>` and move the request number into the header strip (Figma puts the number behind the "Order Number" field). |
| HIGH | Copy | Reason-section heading is singular but Figma is plural. Figma uses `Missing Devices`, `Wrong Devices`, `Encumbered Devices` plus a count `(N)`. Code uses `Missing Device`, `Wrong Device`, `Encumbered Device` and shows no count. | `Missing Devices (30)` | `Missing Device` | Update `sectionLabel()` in `BuyerLineSection.tsx` to pluralise; append `(N)` where N = `detail.missingLines.length` etc. |
| HIGH | Copy | Request-Reason value uses pluralised noun phrases in Figma but the singular form in code. Figma: `Missing Device, Wrong Device, Encumbered Device`. Code: `Missing Device, Wrong Device, Encumbered Device` — same content, but Figma joins with `, ` and code joins with `, ` (matches). Drop this. | n/a | n/a | n/a — confirmed OK. |
| HIGH | Color | Approved Credit panel background highlight is missing. Figma's Approved Credit panel top row uses `fill_NH6QXG` (a teal/highlight fill — visible in node `692:2866 → Frame 14607098`). Code paints both panels with the same `white` background. | Approved panel header tinted | Plain white | Add a subtle fill to the Approved Credit panel's header strip; match `fill_NH6QXG` once you've resolved its hex. |
| HIGH | Color | Status pill colour is hardcoded client-side, not server-driven. Spec §3 requires the pill colour to flow from `credit_request_statuses.colorHex`. Code falls back to a local switch on `systemStatus` (`fallbackStatusColor` in `page.tsx`) because the GET DTO doesn't include `colorHex`. Comment in code acknowledges this is Phase-2 work. | Server-driven `colorHex` | Hardcoded `#D08214 / #407874 / #14AC36 / #B3261E / #888888` | Add `statusColorHex` to the `CreditRequestDetail` payload (`GET /api/v1/partial-credit/requests/{id}`), or join `credit_request_statuses` server-side. Remove `fallbackStatusColor()` once wired. |
| HIGH | Behaviour | Photo upload is a `<input type=file>` with no drag-and-drop. The class is named `.dropzone` but no `onDragOver/onDrop` handlers exist; Figma's "Add Photos" button + plus icon suggests a simple primary button, not a dropzone-shaped area. | Primary button "Add Photos" with `+` icon | Dashed-border file picker labelled "Choose photo(s) to upload" | Either implement the drop handlers (match the visual cue) or replace the dropzone visual with a primary button to match Figma. Recommend: button to match Figma exactly. |
| MEDIUM | Copy | Photos column header label in Wrong Device table. Figma exposes per-line "Photos" as a column; code only renders a request-level photos section at the bottom. Buyers cannot see which line a photo is attached to from the table view. | Per-line `Photos` cell | No per-line photo affordance in the table | Wire the per-line `wrongDeviceLineId` chip in the table → opens the line-scoped uploader. This already exists as a `PhotoUploadDropzone` prop but is never set from the table row. |
| MEDIUM | Copy | Status pill label is "Submitted" in Figma for pre-review. Code uses `detail.displayStatus` which probably resolves to "Pending Approval" or similar. | `Submitted` | `displayStatus` (probably "Pending Approval") | Verify the external label mapping in `credit_request_statuses`. If the external label is genuinely "Pending Approval", reconcile with Figma copy — otherwise relabel. |
| MEDIUM | Color | Reason table header background. Figma uses `fill_AALVAV` = `#F7F5F1` (warm cream). Code uses `--color-surface-2` fallback `#f6f6f6` (cool grey). | `#F7F5F1` | `#f6f6f6` | Adjust `--color-surface-2` or set `.reasonTable th { background: #F7F5F1; }` directly. |
| MEDIUM | Color | Reason-table inner border. Figma uses `fill_ROJ3I9` = `#A8A7A6`. Code uses `--color-input-border` = `#D0D0D0`. | `#A8A7A6` | `#D0D0D0` | Replace border colour in `.reasonTable th, td` and `.summaryPanel` to the darker `#A8A7A6` for visual parity, or introduce a new token. |
| MEDIUM | Color | Page background. Figma section `534:11349` has `fill_6UXM6N` (dark/warm). Page wrapper has no explicit background; inherits white from body. | `fill_6UXM6N` (warm/dark) | white | Confirm the page-level fill and apply on `.page` if appropriate (or on the layout shell). |
| MEDIUM | Spacing | Summary panel cell row padding. Figma `layout_58FB5S` is `padding: 8px 4px` with `gap: 24px`. Code `.summaryPanelTable` uses `gap: 4px 12px` and no padding on cells. | 8px / 4px / 24px | 4px / 12px / 0 | Match the Figma padding and gap on the summary cells; the visual rhythm is currently tighter than spec. |
| MEDIUM | Spacing | Section page gap. Figma section-level layout is `layout_C0Z8XA` with section padding `24px 32px` and inter-section gap `24px`. Code `.page` uses responsive `clamp` padding and `gap: 20px`. | 24px / 32px / 24px | clamp(20px,2vw,32px) / clamp(24px,3vw,40px) / 20px | Tighten the gap to a flat `24px` to match Figma; the clamp on padding is fine for responsive but exceeds the Figma value at desktop. |
| MEDIUM | Behaviour | Reason sections render even when the request has zero lines of that reason. Code `sectionApplies()` guards on `detail.hasMissingDevice && detail.missingLines.length > 0` — correct. Figma multi-reasons variant also gates on tab selection, which code does not implement. | Tab + count gate | Length gate only | Already partially compliant; the tab gating is captured in the CRITICAL row above. |
| LOW | Copy | "Back to my requests" copy. Not in Figma — Figma uses an inline "All Credit Requests" breadcrumb link (`534:11489 → "All Credit Requests"`). | `All Credit Requests` (breadcrumb) | `← Back to my requests` | Rename and restyle as a breadcrumb. Keep the chevron only if Figma uses one (it doesn't in this section). |
| LOW | Copy | Photos hint text "JPEG, PNG, HEIC or WebP up to 5 MB each." is not in Figma. Figma's "Add Photos" button has no hint copy. Decide whether to keep the hint (helpful for buyers) or strip it. | No hint | Hint text rendered | Likely keep for usability; document as an intentional deviation in the chunk-5 plan. |
| LOW | Copy | Currency formatter prints `$0.00` for null values. Figma uses literal `$XXX` placeholders for unspecified, which is dummy data — but the formatter never returns `$0.00` for null in code (it returns `—`). Verified OK. | n/a | `—` for null | Confirmed OK. |
| LOW | Spacing | Status pill padding. Figma pill is hug-fit with horizontal gap `8px`. Code uses `padding: 4px 12px`. Close enough; flag only because the radius (Figma 999 vs code 999) and font sizes (Figma `Text/sm` 16px vs code 13px) don't fully match. | 16px text, hug padding | 13px text, 4×12 padding | Bump pill font-size to 14–16px to align with Figma's `Text/sm`. |
| LOW | Behaviour | Lightbox click-to-close. Code uses `onClick` on the outer div but doesn't trap focus or wire Escape. Not blocking parity; UX nicety. | n/a | Click-only | Add Escape key handler and focus trap in `PhotoGallery.tsx` lightbox. |

### Confirmed-OK summary
- Section ordering matches Figma: back link → page heading → header strip → summary panels → review summary (conditional) → reason sections → photos. Figma does not include the review summary panel pre-completion, which matches the code's `reviewCompletedOn !== null` gate.
- Decision pill gating (visible only when `systemStatus ∈ {APPROVED, DECLINED}`) matches Sprint 4 §11.Q2 (this is intentional, not a Figma mismatch).
- Photo upload + delete blocked once `finalised` is true (Sprint 4 §11.Q1). Confirmed OK in `page.tsx` and `PhotoGallery.tsx`.
- Approved-credit panel hidden until `reviewCompletedOn` is set. Confirmed OK in `BuyerSummaryPanels.tsx`.
- Review summary panel renders only when `reviewCompletedOn !== null`. Confirmed OK in `ReviewSummaryPanel.tsx`.
- No "Complete Review" CTA on the buyer side (admin-only). Confirmed OK — the comment in `BuyerHeaderStrip.tsx` calls this out explicitly.
- Photo MIME types (`image/jpeg,png,heic,webp`) match the dropzone label copy.
- Currency formatter renders `—` for null instead of `$0.00`. Confirmed OK.
- Submitted-by sub-line is reserved for the on-behalf flow (chunk 6); rendered conditionally.

### Figma node resolution notes
- The supplied node id `534-11895` does **not** exist in file `rYKB9vBqlJOFUuGN7GAgQS`. The Figma API returned `Node 534:11895 was not found`. This is the most important caveat in this review — the implementation plan citation is wrong by a few hundred nodes.
- The actual buyer-detail screen lives at SECTION node `534:11349` ("Credit Request Detail Page"), discovered by searching the file dump for nodes named `Credit Request Detail` or `Request Detail`.
- Children of `534:11349` include: `534:11463` (Wrong Device, Pending Approval), `534:11820` (Missing Device, Pending Approval), and two `Multi Reasons, Partially Approved` variants near `651:2421` and `656:3735`.
- Other "Request Detail" / "Request Details" frames exist elsewhere in the file (e.g. `250:1399` "Request Detail" — admin-side; `534:11888` and similar) but `534:11349` is the only SECTION named for the buyer-side credit-request detail flow.
- All textStyle measurements (font sizes, weights, line heights) used in this review came from the resolved `534:11349` subtree; pixel-perfect numbers are quoted verbatim from the textStyles map (lines 8383–8693 of the API dump).
