# Partial Credit — Sprint 4 Implementation Plan

> **Status:** Drafted 2026-05-11. Source of truth for Sprint 4 "feature-complete Phase 1" work.
> **Owner:** Sprint 4 implementer (next session)
> **Jira epic:** [SPKB-3653](https://gazelle.atlassian.net/browse/SPKB-3653) — stories: SPKB-3659 (permissions + on-behalf), SPKB-3662 (post-submit photos), SPKB-3669 (buyer detail page), SPKB-3664 (email templates — admin UI half), Sprint 4 polish (Excel export, file-drop parsing).
> **Related:**
>  - `docs/tasks/partial-credit-modern-implementation-plan.md` §3 line 78 (Sprint 4 scope)
>  - `docs/tasks/partial-credit-sprint3-implementation-plan.md` §10 (out-of-scope list — every Sprint 4 item)
>  - `docs/tasks/partial-credit-sprint3-design-notes.md` (admin landing + review detail Figma frames — Sprint 4 reuses several)
>  - `../docs/tasks/partial-credit-implementation-plan.md` §3 (role model), §11 (on-behalf), §13 (photos)
>  - `../docs/tasks/partial-credit-confluence.md` §"Permissions" + §"On-behalf submission"

This document is durable — Sprint 4 commits should tick its boxes inline (do
not rewrite the file from scratch).

---

## 1. Sprint goal + scope statement

> **Sprint 4 — Feature-complete Phase 1.** Sales reps can submit on behalf of
> a buyer. Buyers can view their own request detail page (read-only mirror of
> the admin review). Sales-ops review of wrong-device lines can request
> post-submit photo uploads from buyers. The "Download" placeholder on the
> admin landing becomes a working xlsx export. Email templates move from
> hardcoded strings to a DB-backed table with an admin editor, and the
> `review-completed-email.enabled` flag flips to `true`. Permission posture
> stays on the existing global roles (`Bidder`, `SalesRep`, `SalesOps`,
> `Administrator`) — the four `PartialCredit_*` rows V89 seeded into
> `identity.user_roles` remain orphaned (no role_assignments) and are
> dropped from every controller allowlist.
>
> — `partial-credit-modern-implementation-plan.md` §3 line 78

Sprint 4 adds **one schema migration** (V90 — `email_templates` table +
`email_audit` table + on-behalf columns on `credit_requests`), **two new
services** (`EmailTemplateService`, `OnBehalfSubmissionService`), **~5 new
REST endpoints** (photo upload, photo list, photo delete, xlsx export,
sales-rep on-behalf buyer-code resolver), and **two new Next.js routes**
(`/wholesale/partial-credit/[id]`, admin email templates page). Target
test coverage 85%+ on new code.

---

## 2. Schema check + V90 migration

### 2.1 V90 — `email_templates` table

```sql
-- V90__partial_credit_email_templates_and_role_assignments.sql
CREATE TABLE partial_credit.email_templates (
    id              BIGSERIAL PRIMARY KEY,
    template_key    VARCHAR(80)  NOT NULL UNIQUE,    -- 'ReviewCompleted_Approved', 'ReviewCompleted_Declined', 'PhotoUploadRequested'
    subject         VARCHAR(255) NOT NULL,
    body_html       TEXT         NOT NULL,
    body_text       TEXT,                            -- nullable; falls back to stripped body_html
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    description     VARCHAR(500),
    created_date    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    changed_date    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by_id   BIGINT,
    changed_by_id   BIGINT,
    CONSTRAINT email_templates_key_chk CHECK (template_key ~ '^[A-Za-z0-9_]+$')
);

CREATE INDEX idx_pc_email_templates_key ON partial_credit.email_templates (template_key);
```

Seed three rows with the **current hardcoded copy** from `ReviewCompletedEmailListener`:
- `ReviewCompleted_Approved`
- `ReviewCompleted_Declined`
- `PhotoUploadRequested` (new — drafted in §6.2 below)

### 2.2 Role assignment seed — DROPPED

The previous draft proposed seeding `role_assignments` rows so every
`Bidder` user also held `PartialCredit_Buyer` (and the same for the other
three roles). **2026-05-11 decision: drop the role mapping entirely** and
use the existing global roles directly in every partial-credit allowlist.
The four `PartialCredit_*` rows V89 seeded into `identity.user_roles`
(ids 1101-1104) remain in place but stay orphaned — no `role_assignments`
ever points at them. They are not deleted (V89 is shipped) but are
ignored by the application. See §7 for the post-Sprint-4 allowlists.

### 2.3 Schema check (existing tables — what Sprint 4 reads/writes)

| Action | Column | V89 location | Status |
|---|---|---|---|
| Read existing photos | `credit_request_photos.*` | V89:215 | ✅ present |
| Write new photos | same | same | ✅ — Sprint 2 ships the table empty |
| Write upload metadata | `credit_request_uploads.*` | V89:238 | ✅ present |
| Read submitter | `credit_requests.submitted_by_id` | V89:60 | ✅ present (Sprint 2 added) |
| Read on-behalf flag | `credit_requests.is_on_behalf` | **MISSING** — see §2.4 |

### 2.4 V90 — `is_on_behalf` column on `credit_requests`

```sql
ALTER TABLE partial_credit.credit_requests
    ADD COLUMN is_on_behalf       BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN on_behalf_of_id    BIGINT REFERENCES identity.users(id),
    ADD COLUMN on_behalf_buyer_code_id BIGINT REFERENCES buyer_mgmt.buyer_codes(id);

CREATE INDEX idx_cr_on_behalf_of ON partial_credit.credit_requests (on_behalf_of_id)
    WHERE on_behalf_of_id IS NOT NULL;
```

`submitted_by_id` = the sales-rep user; `on_behalf_of_id` = the buyer user
the rep is acting for; `on_behalf_buyer_code_id` = which of that buyer's
codes the request is filed under. The buyer-detail-page query (§5.1) uses
`(submitted_by_id = me) OR (on_behalf_of_id = me)` so on-behalf requests
appear on the buyer's landing too.

### 2.5 V90 — `partial_credit.email_audit` (optional but recommended)

```sql
CREATE TABLE partial_credit.email_audit (
    id              BIGSERIAL PRIMARY KEY,
    template_key    VARCHAR(80)  NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    credit_request_id BIGINT REFERENCES partial_credit.credit_requests(id) ON DELETE SET NULL,
    sent_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    success         BOOLEAN      NOT NULL,
    error_message   TEXT
);
CREATE INDEX idx_pc_email_audit_request ON partial_credit.email_audit (credit_request_id);
CREATE INDEX idx_pc_email_audit_sent_at ON partial_credit.email_audit (sent_at DESC);
```

Lets us answer "did this buyer actually receive the approval email?" without
mining stdout. Sprint 4 writes one row per send attempt from
`ReviewCompletedEmailListener` + `PhotoUploadRequestListener`.

---

## 3. New / extended backend services

All Java sources live under
`backend/src/main/java/com/ecoatm/salesplatform/service/partialcredit/`
unless noted.

### 3.1 `EmailTemplateService` (NEW)

**File paths:**
- `service/partialcredit/EmailTemplateService.java` (interface)
- `service/partialcredit/EmailTemplateServiceImpl.java`
- `repository/partialcredit/EmailTemplateRepository.java`

**Public method signatures:**

```java
public interface EmailTemplateService {

    /** Loads a template by key. Throws if missing or disabled. */
    EmailTemplate get(String templateKey);

    /** Renders a template against the supplied variable map.
     *  Uses simple {{varName}} substitution — no Mustache/Freemarker.
     *  Variables not in the map render as empty string + warn-log. */
    RenderedEmail render(String templateKey, Map<String, Object> variables);

    /** Admin: list all templates for the editor UI. */
    List<EmailTemplate> listAll();

    /** Admin: update one template. Bumps changed_date + changed_by_id. */
    EmailTemplate update(Long id, EmailTemplateUpdate patch, Long changedByUserId);

    record RenderedEmail(String subject, String bodyHtml, String bodyText) {}
}
```

**Rendering rule:** straight `{{varName}}` substitution via `String.replace`.
HTML-escape variable values by default; opt-out via `{{!varName}}` (raw)
for fields the admin trusts (e.g. `{{!ctaButton}}` rendering a pre-built
HTML anchor). Keep it boring — no template engine dependency.

**Cache:** `@Cacheable("emailTemplates")` keyed on `template_key`; evict
on every `update` call via `@CacheEvict(allEntries = true)`. Same pattern
as `BuyerCodeQueryService` (existing).

### 3.2 `OnBehalfSubmissionService` (NEW)

**File path:** `service/partialcredit/OnBehalfSubmissionService.java`

**Purpose:** SUB_PartialCredit_SubmitOnBehalf equivalent. Sales-rep flow:
the rep picks a buyer code + buyer user, runs the same submission flow as
the buyer would, and the resulting `credit_requests` row carries
`is_on_behalf = TRUE`.

**Public method signatures:**

```java
@Service
@Transactional
public class OnBehalfSubmissionService {

    /** Lists buyer codes the calling sales-rep is allowed to file on behalf
     *  of. Scoping: rep.buyer_sales_reps junction must include the code. */
    List<BuyerCodeOption> listEligibleBuyerCodes(Long salesRepUserId);

    /** Lists users associated with a given buyer code so the rep can pick
     *  the specific buyer they're acting for. */
    List<BuyerUserOption> listBuyersForCode(Long salesRepUserId, Long buyerCodeId);

    /** Stamps is_on_behalf, on_behalf_of_id, on_behalf_buyer_code_id on a
     *  freshly-created draft. Called from the wizard step 1 once the rep
     *  has confirmed (buyer, buyer_code) pair. */
    void markAsOnBehalf(Long draftId, Long salesRepUserId,
                        Long onBehalfOfUserId, Long buyerCodeId);

    record BuyerCodeOption(Long id, String code, String buyerName) {}
    record BuyerUserOption(Long userId, String displayName, String email) {}
}
```

**Auth checks:** `markAsOnBehalf` throws `AccessDeniedException` if the
rep isn't on `buyer_sales_reps` for the buyer code. The rep drives the
rest of the wizard through the existing buyer endpoints, so Chunk 6 also
adds `SalesRep` to the `BuyerPartialCreditController` allowlist — final
state `hasAnyRole('Bidder','SalesRep','Administrator')`. The service
layer enforces that a `SalesRep` caller can only PATCH / submit drafts
where `submitted_by_id = me` (i.e. drafts the rep started themselves).

### 3.3 `CreditRequestPhotoService` (NEW)

**File path:** `service/partialcredit/CreditRequestPhotoService.java`

**Purpose:** ACT_PartialCredit_UploadWrongDevicePhoto equivalent. Buyer
uploads photos after the initial submit (admin sees them on the review
detail page). Max 5 photos per wrong-device line, 5 MB each, image/* MIME
only.

**Public method signatures:**

```java
@Service
public class CreditRequestPhotoService {

    record UploadInput(MultipartFile file, Long wrongDeviceLineId, Long uploaderUserId) {}

    /** Persists one photo row. Returns the saved entity ID. */
    Long uploadPhoto(Long creditRequestId, UploadInput in);

    /** Returns all photos for the request (admin) or only the
     *  caller-uploaded photos (buyer — Sprint 4 keeps it permissive,
     *  Phase 2 may tighten). */
    List<PhotoMetadata> listPhotos(Long creditRequestId, Long viewerUserId);

    /** Returns the bytea payload + content-type for the photo response. */
    PhotoBlob downloadPhoto(Long photoId, Long viewerUserId);

    /** Deletes a photo. Buyer can only delete their own; admin can delete any. */
    void deletePhoto(Long photoId, Long viewerUserId);

    record PhotoMetadata(Long id, String filename, String contentType,
                         long sizeBytes, Long wrongDeviceLineId,
                         Instant uploadedDate, Long uploadedByUserId) {}
    record PhotoBlob(byte[] data, String contentType, String filename) {}
}
```

**Validation:**
- Max file size: 5 MB (configurable: `partial-credit.photo.max-size-mb`, default 5).
- Max photos per line: 5 (configurable: `partial-credit.photo.max-per-line`, default 5).
- Allowed content types: `image/jpeg`, `image/png`, `image/heic`, `image/webp`.
- Rejects upload if the parent request status is `APPROVED` or `DECLINED` (review is finalised; no more evidence allowed).

### 3.4 `PartialCreditExcelExportService` (NEW)

**File path:** `service/partialcredit/PartialCreditExcelExportService.java`

**Purpose:** Admin landing Download button. Streams an xlsx file with the
filtered admin landing list (one sheet) plus a second sheet listing every
line in every selected request. Reuses the Apache POI pattern from the
existing PO export (`PurchaseOrderExcelExportService`).

**Public method signature:**

```java
@Service
public class PartialCreditExcelExportService {

    /** Builds the xlsx bytes for the given filter set. Caller streams as
     *  an HTTP attachment. */
    byte[] export(AdminListFilters filters);
}
```

**Sheets:**
1. **Requests** — same 7 columns as the landing table (id, request#, date, buyer, company, order#, reasons, status).
2. **Lines** — request#, reason kind, ecoatm_code, qty, amount_paid, amount_to_credit, review_decision, action_recommendation (wrong only).

Hard cap at **5,000 requests** per export to keep memory predictable; UI
banner if the filter would exceed.

### 3.5 `CreditRequestFileDropParser` (NEW)

**File path:** `service/partialcredit/CreditRequestFileDropParser.java`

**Purpose:** Buyer wizard Step 2 file-drop. Buyer drops an xlsx, csv, or
docx with barcodes; parser extracts a list of strings, dedupes them, and
hands them to the existing barcode-reconciliation pipeline (Sprint 2
chunk 5).

**Public method signature:**

```java
@Service
public class CreditRequestFileDropParser {

    record ParsedBarcodes(List<String> barcodes, List<String> warnings) {}

    /** Detects file type by content-type + sniff; extracts barcodes from
     *  every cell / line / paragraph; returns deduped list.
     *  Warnings include things like "skipped 3 cells that didn't look
     *  like barcodes" without blocking. */
    ParsedBarcodes parse(MultipartFile file);
}
```

**Format support:**
- **xlsx** — Apache POI, first sheet only, every non-empty cell value.
- **csv** — comma OR tab delimited, first column only.
- **docx** — Apache POI XWPF, every paragraph's plain text.

Lines containing letters mixed with digits are kept verbatim; pure-digit
runs ≥ 8 chars are kept; everything else is dropped with a warning.

---

## 4. New REST endpoints

### 4.1 Buyer — post-submit photo upload + list + download + delete

Add to existing `BuyerPartialCreditController.java`:

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/buyer/partial-credit/{id}/photos` | Multipart upload — fields: `file`, `wrongDeviceLineId` |
| `GET`  | `/api/v1/buyer/partial-credit/{id}/photos` | List all photo metadata for this request |
| `GET`  | `/api/v1/buyer/partial-credit/photos/{photoId}/blob` | Stream bytes — `Content-Disposition: inline` |
| `DELETE` | `/api/v1/buyer/partial-credit/photos/{photoId}` | Delete (own-photos only) |

**Auth:** existing class-level `@PreAuthorize("hasAnyRole('Bidder','Administrator')")` (post-Sprint-4 — see §7); service-layer enforces ownership.

**Error cases:**
- `413 Payload Too Large` if file > max-size-mb.
- `415 Unsupported Media Type` if MIME outside the allowlist.
- `409 Conflict` if request status is APPROVED / DECLINED.
- `403 Forbidden` if photo owner doesn't match caller (delete only).

### 4.2 Buyer — request detail page (read-only)

Add to existing `BuyerPartialCreditController.java`:

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/v1/buyer/partial-credit/{id}` | Already exists from Sprint 2 — Sprint 4 extends the response DTO to include `lines[]` (Missing / Wrong / Encumbered) + `headerSummary` + `reviewerNotes` (nullable) so the buyer detail page can render the read-only mirror |

**DTO extension:** add to existing `CreditRequestDetail` record:

```java
public record CreditRequestDetail(
    // ... existing Sprint 2 fields ...
    List<MissingLineView> missingLines,        // NEW
    List<WrongLineView> wrongLines,            // NEW
    List<EncumberedLineView> encumberedLines,  // NEW
    HeaderSummaryDto headerSummary,            // NEW — same shape as admin response
    String reviewerNotes,                      // NEW — null until review completed
    Instant reviewCompletedOn                  // NEW — null until completed
) {}
```

Buyer-side `MissingLineView` strips admin-only fields (no `review_decision`
detail per-line if the request is still PENDING — only show the final
decision after the request is APPROVED / DECLINED).

### 4.3 Sales-rep on-behalf flow

Add to a **new** controller `OnBehalfPartialCreditController.java` (under
`controller/partialcredit/`):

| Method | Path | Purpose |
|---|---|---|
| `GET`  | `/api/v1/salesrep/partial-credit/buyer-codes` | List buyer codes the rep can file on behalf of |
| `GET`  | `/api/v1/salesrep/partial-credit/buyer-codes/{codeId}/users` | List users for a code |
| `POST` | `/api/v1/salesrep/partial-credit/drafts/{draftId}/mark-on-behalf` | Stamp the on-behalf fields on an existing draft |

**Class-level `@PreAuthorize`:** `hasAnyRole('SalesRep', 'Administrator')`.

The rep then drives the rest of the wizard through the existing buyer
endpoints — same DTOs, same validators. The buyer-code resolver in
`CreditRequestService.createDraft` uses `on_behalf_buyer_code_id` when set,
otherwise the caller's own buyer code.

### 4.4 Admin — Excel export

Add to existing `AdminPartialCreditController.java`:

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/v1/admin/partial-credit/export.xlsx` | Same query params as the landing GET. Streams `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`. `Content-Disposition: attachment; filename="partial-credit-{yyyy-MM-dd}.xlsx"` |

**Cap behaviour:** if `filters` resolves > 5,000 rows, return `413 Payload Too Large` with body `{"error":"too_many_rows","limit":5000,"matched":<count>}` so the UI can show a toast asking the user to narrow filters.

### 4.5 Admin — email templates

Add to existing `AdminPartialCreditController.java` (or a new sibling controller — see §8 chunk 4 decision):

| Method | Path | Purpose |
|---|---|---|
| `GET`   | `/api/v1/admin/partial-credit/email-templates` | List all templates |
| `PATCH` | `/api/v1/admin/partial-credit/email-templates/{id}` | Update one — fields: `subject`, `bodyHtml`, `bodyText`, `enabled` |
| `POST`  | `/api/v1/admin/partial-credit/email-templates/{id}/preview` | Render with a stub variable set; returns rendered subject + body for preview |

`template_key` is **NOT** editable post-seed — the listener code references
it directly. Editing the key would break the listener silently.

---

## 5. New Next.js routes

### 5.1 Buyer detail page

**Route:** `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/page.tsx`

**Components:**
- `<RequestHeaderStrip>` — reuse from admin detail (move to a shared
  location: `frontend/src/components/partial-credit/RequestHeaderStrip.tsx`).
- `<SummaryPanels>` — reuse from admin detail (same shared move).
- `<BuyerLineSection>` — read-only variant of the admin section. No Action
  dropdown, no bulk button. Final per-line decision pill shown only after
  request is APPROVED / DECLINED.
- `<PhotoUploadDropzone>` — new component. Allows multi-file drop; hits
  POST `/photos`. Visible only when request status is PENDING_APPROVAL or
  UNDER_REVIEW; hidden once review is complete.
- `<PhotoGallery>` — thumbnails of uploaded photos; click → opens a modal
  with the full image. Delete (trash icon) visible only on the buyer's own
  photos.
- `<ReviewerNotesPanel>` — render only when `reviewCompletedOn != null`;
  shows the reviewer's name + completion date.

**Status pill behaviour:** matches the buyer landing (uses
`externalStatusText` + `colorHex` from the API — same DB-driven scheme as
Sprint 2 buyer landing).

### 5.2 Admin email-templates page

**Route:** `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/email-templates/page.tsx`

**Components:**
- Table of all templates: key (read-only), subject (editable), enabled (toggle), last-changed-by, last-changed-date.
- Click a row → expands to a side panel / drawer with a tab pair: **Edit** (rich-text editor for body_html + raw textarea for body_text) and **Preview** (renders with stub data, displays subject + body inline).
- Save calls `PATCH /email-templates/{id}` and shows a success toast.
- **No `New Template` button** — Sprint 4 ships with the 3 seeded keys only. Adding a 4th key requires a code change in the listener that references it, so it's intentionally a DB-seeded-only set.

### 5.3 Sales-rep on-behalf wizard entry

**Route:** add a top-level "On Behalf Of" affordance to the buyer wizard
landing at `/wholesale/partial-credit`. If the calling user has the
`SalesRep` role, show:
- A "Submit on behalf of a buyer" button at the top of the landing.
- Clicking it opens a 2-step modal: pick buyer code (chip search) → pick
  buyer user (auto-narrowed by code).
- On Confirm: creates a draft via existing buyer endpoint, then immediately
  calls `POST /salesrep/.../mark-on-behalf` with the chosen (buyer_user_id,
  buyer_code_id), then redirects to the wizard step 1.

Re-uses every existing wizard step verbatim — the only difference is the
banner at the top of the wizard says `Submitting on behalf of <buyer name>
for buyer code <code>`.

### 5.4 Admin landing — wire the Download button

**File:** `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/page.tsx` (extend existing)

Replace the disabled placeholder with a real button that hits
`GET /api/v1/admin/partial-credit/export.xlsx?...filters` with the
current filter set. Browser handles the download via standard
`Content-Disposition: attachment`. Toast on 413 ("Too many results — narrow
your filters").

---

## 6. Email templates — content + variables

### 6.1 Variable contract

Every template receives the same base set (extra keys are added per
event):

| Variable | Source |
|---|---|
| `{{buyerName}}` | `credit_requests.party_name` |
| `{{requestNumber}}` | `credit_requests.request_number` |
| `{{orderNumber}}` | `credit_requests.order_number` |
| `{{requestDate}}` | `credit_requests.request_date` (formatted `yyyy-MM-dd`) |
| `{{statusText}}` | `credit_request_statuses.external_status_text` |
| `{{appBaseUrl}}` | `application.yml` → `app.base-url` |
| `{{detailUrl}}` | `{{appBaseUrl}}/wholesale/partial-credit/{{requestId}}` |

Extra per-template:
- `ReviewCompleted_Approved` / `ReviewCompleted_Declined`:
  `{{approvedTotalDisplay}}`, `{{reviewerName}}`, `{{reviewCompletedDate}}`.
- `PhotoUploadRequested`: `{{wrongDeviceLineDescription}}`, `{{photoUploadDeadline}}` (admin-set; nullable → renders as "as soon as possible").

**Currency formatting:** `{{approvedTotalDisplay}}` carries the rendered
currency string (e.g. `$25.00`) — the dollar sign is part of the
substituted value, not adjacent to `{{` in the template. Flyway parses
`${...}` as its own placeholder syntax, so the seed migration cannot put
a literal `$` immediately before `{{`. Chunk 2's renderer formats
`credit_requests.approved_total` (BigDecimal) → `"$<amount>"` before
substitution.

### 6.2 Seed content (drafts — design follow-up may revise)

**`ReviewCompleted_Approved`** — subject: `Your partial credit request {{requestNumber}} has been approved`. Body: paragraph confirming the approval, lists `{{approvedTotalDisplay}}`, links to `{{detailUrl}}`.

**`ReviewCompleted_Declined`** — subject: `Your partial credit request {{requestNumber}} has been declined`. Body: paragraph explaining the decline + link to `{{detailUrl}}`.

**`PhotoUploadRequested`** (new in Sprint 4) — subject: `Please upload photos for partial credit request {{requestNumber}}`. Body: tells the buyer which line needs photos + link.

All three seeded in V90 with the existing hardcoded subject/body lifted
out of `ReviewCompletedEmailListener` so behaviour is identical post-flip.

### 6.3 Flip the flag

`application.yml`:

```yaml
partial-credit:
  review-completed-email:
    enabled: true  # was: false
```

Add a SMTP-required check in `EmailTemplateServiceImpl` constructor: if
the flag is true but `spring.mail.host` is empty, fail-fast at startup with
a clear message ("Email is enabled but no SMTP host configured"). Prevents
silent prod misconfig.

---

## 7. Permissions — keep existing global roles (SPKB-3659 on-behalf only)

**2026-05-11 decision: drop the `PartialCredit_*` role tier entirely.** The
buyer-facing controllers use `Bidder`; sales-rep flow uses `SalesRep`;
sales-ops uses `SalesOps`; admin overrides everything via `Administrator`.
The V89-seeded `PartialCredit_*` rows in `identity.user_roles` (1101-1104)
stay in place but no `role_assignments` is ever created for them — they
are deliberately orphaned. SPKB-3659 collapses to just the on-behalf
submission work; the permission half is closed by this decision (no V90
seed, no allowlist rewrite).

### 7.1 Allowlists per controller — final Sprint 4 state

| Controller | `@PreAuthorize` |
|---|---|
| `BuyerPartialCreditController` | `hasAnyRole('Bidder','SalesRep','Administrator')` — `SalesRep` is included so reps can drive the wizard for an on-behalf draft they created; service layer enforces `submitted_by_id = me` |
| `AdminPartialCreditController` | `hasAnyRole('SalesOps','SalesRep','Administrator','CoAdministrator')` |
| `OnBehalfPartialCreditController` (NEW Sprint 4) | `hasAnyRole('SalesRep','Administrator')` |

Sprint 3 currently has a wider transitional allowlist on the admin
controller (`'PartialCredit_SalesOps','PartialCredit_Admin','SalesOps','SalesRep','Administrator','CoAdministrator'`).
Chunk 8 narrows it to the row above by dropping the two `PartialCredit_*`
role names — same set of human users still authorized (no one is
assigned those roles anyway).

### 7.2 `SecurityConfig.java` path matchers

Unchanged from Sprint 3 — paths already use `.authenticated()` plus the
controller-level `@PreAuthorize` gating. No path-matcher edits needed
in Sprint 4.

### 7.3 Manual smoke test

Login as each of:
- `bidder@buyerco.com` — sees buyer landing + detail + can upload photos.
- `salesrep@test.com` — sees buyer landing + "Submit on behalf" button.
- `salesops@test.com` — sees admin landing + can review.
- `admin@test.com` — sees everything including the email-templates page.

---

## 8. Chunk breakdown

8 bisectable chunks, each a single commit, sized S/M/L. Total estimate
**~40-55 hours** (~5-7 working days for one engineer).

| # | Chunk | Effort | Depends on |
|---|---|---|---|
| 1 | V90 migration (email_templates + email_audit + on-behalf columns) + IT | S ~3-4h | — |
| 2 | `EmailTemplateService` + repo + flip listener to read from DB | M ~5-7h | 1 |
| 3 | Admin email-templates REST endpoints + Next.js page | M ~6-8h | 2 |
| 4 | `CreditRequestPhotoService` + buyer photo REST endpoints | M ~5-7h | 1 |
| 5 | Buyer detail page (`/wholesale/partial-credit/[id]`) + DTO extension + PhotoGallery | L ~7-9h | 4 |
| 6 | `OnBehalfSubmissionService` + sales-rep REST endpoints + on-behalf modal UI | L ~7-9h | 1 |
| 7 | `PartialCreditExcelExportService` + admin Download button wire-up | M ~4-5h | — (independent) |
| 8 | `CreditRequestFileDropParser` + wizard Step 2 file-drop UI + role allowlist tightening + email flag flip + E2E | M ~5-7h | 2, 4, 6 |

### Chunk 1 — V90 migration (M, ~4-5h)

**Files:**
- `backend/src/main/resources/db/migration/V90__partial_credit_phase1_finalize.sql` (~120 lines)
- `backend/src/test/java/.../partialcredit/V90MigrationIT.java` (~80 lines)

**Deliverables:**
- `email_templates` table + 3 seed rows lifted from the hardcoded listener strings.
- `email_audit` table.
- `is_on_behalf`, `on_behalf_of_id`, `on_behalf_buyer_code_id` columns on `credit_requests`.

**Tests:**
- `V90MigrationIT` — Testcontainers Postgres IT, ~3 cases:
  - `email_templates` has exactly 3 rows after seed
  - `email_audit` table exists with the indexes
  - `credit_requests.is_on_behalf` defaults to FALSE for legacy rows

**Commit:** `feat(partial-credit): V90 migration — email templates, on-behalf columns, email audit`

### Chunk 2 — `EmailTemplateService` + listener flip (M, ~5-7h)

**Files:**
- `service/partialcredit/EmailTemplateService.java` (interface, ~30 lines)
- `service/partialcredit/EmailTemplateServiceImpl.java` (~150 lines)
- `repository/partialcredit/EmailTemplateRepository.java` (~25 lines)
- `model/partialcredit/EmailTemplate.java` (~60 lines)
- `listener/partialcredit/ReviewCompletedEmailListener.java` (modify — swap hardcoded strings for `emailTemplateService.render(...)`)
- `service/partialcredit/EmailAuditService.java` (~60 lines, writes one row per send attempt)

**Tests:**
- `EmailTemplateServiceTest` — JUnit unit, ~10 cases:
  - `render` substitutes single variable
  - `render` HTML-escapes by default
  - `render` honours `{{!varName}}` raw escape
  - `render` empty-string-substitutes missing variables + warns
  - `update` evicts cache
  - `get` throws on missing key
  - `get` throws on disabled template
- `ReviewCompletedEmailListenerTest` (modify existing) — assert subject + body now come from DB; mock `EmailTemplateService`.
- `EmailAuditServiceIT` — Testcontainers, ~3 cases (success row, failure row, no-template row).

**Commit:** `feat(partial-credit): email templates via DB + audit log`

### Chunk 3 — Admin email-templates REST + Next.js page (M, ~6-8h)

**Files:**
- `controller/admin/AdminPartialCreditController.java` (add 3 endpoints from §4.5)
- `dto/partialcredit/EmailTemplateView.java`, `EmailTemplateUpdate.java`, `EmailTemplatePreview.java`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/email-templates/page.tsx`
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/email-templates/EmailTemplateEditor.tsx`
- `frontend/src/lib/adminPartialCreditClient.ts` (extend — 3 new functions)

**Tests:**
- `AdminPartialCreditControllerIT` (extend) — 5 new cases (GET list, PATCH subject, PATCH disabled, POST preview renders, 401 unauthenticated).
- Frontend unit: `EmailTemplateEditor.test.tsx` — save button disabled when no changes, preview tab renders rendered output.

**Commit:** `feat(partial-credit): admin email-templates editor + preview`

### Chunk 4 — Photo upload service + REST (M, ~5-7h)

**Files:**
- `service/partialcredit/CreditRequestPhotoService.java` (~200 lines)
- `controller/BuyerPartialCreditController.java` (add 4 endpoints from §4.1)
- `dto/partialcredit/PhotoMetadataView.java`
- `application.yml` (add `partial-credit.photo.max-size-mb: 5`, `partial-credit.photo.max-per-line: 5`, allowed MIME list)
- `config/MultipartConfig.java` (verify or add — max-file-size: 5MB; max-request-size: 30MB)

**Tests:**
- `CreditRequestPhotoServiceTest` — JUnit unit, ~12 cases:
  - Upload happy path → row written, bytea length matches
  - Upload > 5MB → throws PayloadTooLarge
  - Upload `image/gif` → throws UnsupportedMediaType
  - Upload when request is APPROVED → throws Conflict
  - Upload when line already has 5 photos → throws too-many
  - List returns metadata in upload order
  - Download returns correct content-type
  - Delete own → succeeds
  - Delete others' as buyer → throws Forbidden
  - Delete others' as admin → succeeds
- `BuyerPartialCreditControllerIT` (extend) — 4 new cases (multipart POST, multipart 413, GET blob, DELETE).

**Commit:** `feat(partial-credit): buyer photo upload + download + delete`

### Chunk 5 — Buyer detail page (L, ~7-9h)

**Files:**
- `controller/BuyerPartialCreditController.java` (extend `GET /{id}` response — add `lines[]`, `headerSummary`, `reviewerNotes`)
- `dto/partialcredit/CreditRequestDetail.java` (extend — see §4.2)
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/page.tsx` (~80 lines, server component data-fetch)
- `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/BuyerDetailClient.tsx` (~250 lines)
- `frontend/src/components/partial-credit/RequestHeaderStrip.tsx` (move from admin)
- `frontend/src/components/partial-credit/SummaryPanels.tsx` (move from admin)
- `frontend/src/components/partial-credit/BuyerLineSection.tsx` (~150 lines)
- `frontend/src/components/partial-credit/PhotoUploadDropzone.tsx` (~120 lines)
- `frontend/src/components/partial-credit/PhotoGallery.tsx` (~100 lines)
- `frontend/src/components/partial-credit/ReviewerNotesPanel.tsx` (~40 lines)

**Tests:**
- Frontend unit: `BuyerLineSection.test.tsx`, `PhotoUploadDropzone.test.tsx`, `PhotoGallery.test.tsx`.
- Playwright (drafted, fully shipped in Chunk 8) — buyer logs in, navigates to detail, uploads a photo, sees it in the gallery.

**Commit:** `feat(partial-credit): buyer request detail page with photo upload`

### Chunk 6 — On-behalf submission (L, ~7-9h)

**Files:**
- `service/partialcredit/OnBehalfSubmissionService.java` (~180 lines)
- `controller/partialcredit/OnBehalfPartialCreditController.java` (~120 lines)
- `dto/partialcredit/BuyerCodeOption.java`, `BuyerUserOption.java`, `MarkOnBehalfRequest.java`
- `frontend/src/app/(dashboard)/wholesale/partial-credit/page.tsx` (extend — add "Submit on behalf" button + modal)
- `frontend/src/app/(dashboard)/wholesale/partial-credit/OnBehalfModal.tsx` (~180 lines)
- `frontend/src/lib/onBehalfPartialCreditClient.ts` (~80 lines)
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/page.tsx` (extend — show on-behalf banner when present)
- `service/partialcredit/CreditRequestService.java` (extend — `createDraft` honours `on_behalf_buyer_code_id` if set)

**Tests:**
- `OnBehalfSubmissionServiceTest` — ~10 cases (eligibility filtering, scoping enforcement, mark idempotency, etc.)
- `OnBehalfPartialCreditControllerIT` — ~8 cases (200/403 matrix).
- Frontend unit: `OnBehalfModal.test.tsx`.
- Playwright (in Chunk 8): sales-rep login → submit on behalf of `bidder@buyerco.com` → request shows up on bidder's landing.

**Commit:** `feat(partial-credit): sales-rep on-behalf submission`

### Chunk 7 — Excel export (M, ~4-5h)

**Files:**
- `service/partialcredit/PartialCreditExcelExportService.java` (~180 lines)
- `controller/admin/AdminPartialCreditController.java` (add 1 endpoint from §4.4)
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/page.tsx` (wire the Download button)

**Tests:**
- `PartialCreditExcelExportServiceTest` — JUnit unit, ~6 cases (header row, body cells, two-sheet structure, empty-result handling, 5,000-row cap, mixed-reason rows).
- `AdminPartialCreditControllerIT` (extend) — 3 new cases (200 with attachment header, 413 over-limit, 401 unauth).
- Smoke: open the downloaded file in Excel and eyeball the two sheets.

**Commit:** `feat(partial-credit): admin xlsx export with two-sheet layout`

### Chunk 8 — File-drop parser + allowlist narrowing + email flip + E2E (M, ~5-7h)

**Files:**
- `service/partialcredit/CreditRequestFileDropParser.java` (~150 lines)
- `frontend/src/app/(dashboard)/wholesale/partial-credit/new/.../FileDropZone.tsx` (wire to the buyer wizard Step 2 — replace the existing manual-only barcode textarea with a hybrid)
- `controller/admin/AdminPartialCreditController.java` — narrow `@PreAuthorize` per §7.1 (drop the two `PartialCredit_*` role names from the existing allowlist; keep `SalesOps`, `SalesRep`, `Administrator`, `CoAdministrator`)
- `controller/BuyerPartialCreditController.java` — drop `PartialCredit_Buyer` from the allowlist. Final state `hasAnyRole('Bidder','SalesRep','Administrator')` — the `SalesRep` addition was already made in Chunk 6 so reps can drive the wizard for on-behalf drafts
- `application.yml` — flip `partial-credit.review-completed-email.enabled: true`
- `frontend/tests/e2e/partial-credit-sprint4.spec.ts` (~250 lines, see below)

**E2E test cases:**
- Buyer login → submit a request → navigate to detail → upload a photo → see it in the gallery
- Admin login → review the request → complete with APPROVE → assert email was logged in `email_audit` (DB query) → assert buyer detail page now shows the reviewer note + approved status pill
- Sales-rep login → click "Submit on behalf" → pick a buyer + buyer code → walk through wizard → assert request appears on the buyer's landing with the on-behalf banner
- Admin login → click Download on landing → assert the xlsx response has a valid content-type
- Admin login → email-templates page → edit the Approved subject → save → repeat the review flow → assert the new subject is logged

**Commit:** `feat(partial-credit): finalize Phase 1 — file-drop, allowlist narrow, email flip, E2E sweep`

---

## 9. Test plan (per chunk)

### 9.1 Unit tests (Mockito-style)

| Chunk | Test file | LOC est. | Mocks |
|---|---|---|---|
| 1 | `V90MigrationIT` | 100 | Testcontainers |
| 2 | `EmailTemplateServiceTest` | 200 | `EmailTemplateRepository` |
| 2 | `EmailAuditServiceIT` | 100 | Testcontainers |
| 2 | `ReviewCompletedEmailListenerTest` (modify) | +60 | `EmailTemplateService`, `EmailSender` |
| 3 | `AdminPartialCreditControllerIT` (extend) | +120 | `MockMvc` slice |
| 4 | `CreditRequestPhotoServiceTest` | 250 | `CreditRequestPhotoRepository`, `CreditRequestRepository` |
| 4 | `BuyerPartialCreditControllerIT` (extend) | +120 | `MockMvc` slice |
| 5 | `BuyerDetailClient.test.tsx`, line/section/gallery component tests | 300 | RTL |
| 6 | `OnBehalfSubmissionServiceTest` | 200 | `BuyerSalesRepRepository`, `UserBuyerRepository`, `CreditRequestRepository` |
| 6 | `OnBehalfPartialCreditControllerIT` | 200 | `MockMvc` slice |
| 7 | `PartialCreditExcelExportServiceTest` | 150 | `CreditRequestRepository`, all 3 line repos |
| 8 | `CreditRequestFileDropParserTest` | 200 | `MultipartFile` fixtures (xlsx, csv, docx) |

### 9.2 Integration tests

| Chunk | Test file | Covers |
|---|---|---|
| 1 | `V90MigrationIT` | Migration applies cleanly + idempotent |
| 2 | `EmailAuditServiceIT` | Audit rows write on success + failure |
| 4 | `BuyerPartialCreditControllerIT` (extend) | Multipart upload happy + size + MIME negatives |
| 6 | `OnBehalfPartialCreditControllerIT` | 200/403 matrix across 3 endpoints |

### 9.3 Playwright E2E (in Chunk 8)

Single file: `frontend/tests/e2e/partial-credit-sprint4.spec.ts`. 5 scenarios listed under Chunk 8.

### 9.4 Coverage target

- Per-chunk: each new service class targets 90%+ line coverage.
- Aggregate across Sprint 4: 85%+ on the new code (matches Sprints 2-3
  closeout target). Verify via JaCoCo on the
  `service.partialcredit.{EmailTemplateService, OnBehalfSubmissionService,
  CreditRequestPhotoService, PartialCreditExcelExportService,
  CreditRequestFileDropParser}` packages.

---

## 10. Out-of-scope (Phase 2)

These remain Phase 2 — Sprint 4 closes Phase 1 without touching them.

| Item | Source |
|---|---|
| Automated Prolog encumbrance check | impl-plan §18 Q9 |
| RMA auto-creation for accepted encumbered lines | impl-plan §18 Q8 |
| Oracle write-back of approved credits | impl-plan §7 |
| Datadog APM dashboards | impl-plan §15 |
| R-2 certification gating in wizard Step 1 | impl-plan §16 line 482 |
| Reason-pivoted admin landings (Missing-only, Wrong-only) | impl-plan §16 line 462 |
| Per-column filter pop-overs on admin landing | Sprint 3 design notes §8.13 |
| External object storage (S3) for photos | modern plan §2 ("revisit at scale") |
| Promote `PartialCredit_*` role tier (if/when wholesale-eligibility gating is wanted) | Sprint 4 §7 — orphan rows live in `identity.user_roles` ready to use; needs a future migration to seed `role_assignments` and a parallel allowlist update |
| Email template versioning + rollback UI | Sprint 4 ships with last-write-wins; Phase 2 may add `email_template_versions` |
| Buyer-side notification preferences (opt-out of emails) | Phase 2 — Sprint 4 sends to every approved request |

---

## 11. Open questions

These need a product decision before Chunk 5 / Chunk 6 ship. Default
behaviours marked **(SHIP DEFAULT)** are what the implementation lands
with unless overridden.

### Q1 — Photo upload window: PENDING_APPROVAL only, or also UNDER_REVIEW?

**(SHIP DEFAULT: both)** — buyer can upload until the review is
completed. Closing the window earlier (e.g. once an admin opens the
review) creates a race where the buyer's upload silently fails. Phase 2
can add a "freeze" toggle if reviewers want it.

### Q2 — Does the buyer detail page show per-line review_decision before completion?

**(SHIP DEFAULT: no)** — the buyer sees a single high-level pill
(Pending Approval / Under Review / Approved / Declined) and the
final per-line decisions only after the request is fully completed.
Avoids reviewer thrash being visible mid-review.

### Q3 — On-behalf banner copy

**(SHIP DEFAULT)** — `You are submitting on behalf of {{buyerName}} for buyer code {{code}}.` On the wizard pages, render in a yellow info banner above Step 1. On the buyer's own detail page, render `Submitted by {{salesRepName}} on your behalf` near the header strip.

### Q4 — Email template editor: rich-text or raw HTML?

**(SHIP DEFAULT: raw HTML + Preview tab)** — the editor is a plain `<textarea>` for `body_html`. The Preview tab renders it inline. Rich-text WYSIWYG is Phase 2 if admins ask. Keeps Sprint 4 small.

### Q5 — `email_audit` retention

**(SHIP DEFAULT: no auto-purge)** — Phase 2 may add a Flyway cleanup or a
scheduled job. Volume estimate: ~10 requests/day × ~2 emails each = ~7k
rows/year, negligible.

---

## 12. Verification (Sprint 4)

Manual smoke checks once Chunk 8 is committed:

1. `mvn flyway:migrate` applies V90 cleanly on a fresh `salesplatform_dev`.
2. `psql -c "SELECT COUNT(*) FROM partial_credit.email_templates"` returns `3`.
3. `psql -c "SELECT COUNT(*) FROM identity.user_role_assignments WHERE role_id IN (1101,1102,1103,1104)"` returns `0` — the `PartialCredit_*` rows stay orphaned by design (§7).
4. Login as `bidder@buyerco.com` → submit a request → navigate to detail page → upload a photo → see it in the gallery.
5. Login as `salesrep@test.com` → click "Submit on behalf" → pick `bidder@buyerco.com` + their `NB_PWS` code → walk through wizard → assert the new draft has `is_on_behalf = TRUE` and the buyer can see it.
6. Login as `salesops@test.com` → review the request → click Complete Review → confirm Approve → check backend stdout shows the email send + `psql` query on `email_audit` returns the row.
7. Login as `admin@test.com` → navigate to email-templates page → change `ReviewCompleted_Approved.subject` → save → submit + review another request → assert the new subject is logged.
8. Login as `admin@test.com` → admin landing → click Download → confirm xlsx downloads with the right filename pattern.
9. `mvn verify` passes, JaCoCo report shows 85%+ on the 5 new Sprint 4 packages.

---

## 13. Sprint 4 close-out checklist

When Chunk 8 is merged:

- [ ] V90 migration applied to QA and prod environments
- [ ] `partial-credit.review-completed-email.enabled: true` in QA + prod application.yml (env-specific overrides)
- [ ] SMTP credentials confirmed live in QA + prod via `spring.mail.*`
- [ ] Smoke checks 1-9 above all pass on QA
- [ ] `docs/business-logic/index.md` adds `[Partial credit — full flow](partial-credit.md)` entry
- [ ] `docs/api/rest-endpoints.md` lists all Sprint 4 endpoints
- [ ] `docs/testing/coverage.md` adds the Sprint 4 package row (target 85%+)
- [ ] `docs/app-metadata/modules.md` updates the Partial Credit module entry with Phase 1 done
- [ ] SPKB-3659, SPKB-3662, SPKB-3669, SPKB-3664 (admin half) moved to Done in Jira
