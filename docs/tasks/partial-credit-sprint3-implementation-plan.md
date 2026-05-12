# Partial Credit — Sprint 3 Implementation Plan

> **Status:** Drafted 2026-05-11. Source of truth for Sprint 3 admin-review work.
> **Owner:** Sprint 3 implementer (next session)
> **Jira epic:** [SPKB-3653](https://gazelle.atlassian.net/browse/SPKB-3653) — stories: SPKB-3658 (admin review), SPKB-3660 (credit calc), SPKB-3661 (recommendation engine), SPKB-3663 (Complete Review + email), SPKB-3664 (status config).
> **Related:**
>  - `docs/tasks/partial-credit-modern-implementation-plan.md` §3 (Sprint 3 scope)
>  - `docs/tasks/partial-credit-sprint3-design-notes.md` (Figma frame inventory — sibling file, same date)
>  - `docs/tasks/partial-credit-sprint2-closeout-plan.md` (chunk shape + effort sizing model)
>  - `../docs/tasks/partial-credit-implementation-plan.md` §8 (credit calc), §9 (recommendation engine), §10 (sales-ops microflows), §16 (Sprint 3 task list)
>  - `../docs/tasks/partial-credit-confluence.md` §"Acceptance criteria — Admin review"

This document is durable — Sprint 3 commits should tick its boxes inline (do
not rewrite the file from scratch).

---

## 1. Sprint goal + scope statement

> **Sprint 3 — Admin review + credit calc + action recommendation + bulk
> actions + Complete Review.** Sales ops can review any pending request,
> see the three reason sections, get a per-line recommendation,
> accept/decline (per line or in bulk per section), enter manual Prolog
> Result + Actual Value on encumbered lines, click Complete Review to flip
> the request to Approved or Declined.
>
> — `partial-credit-modern-implementation-plan.md` §3, lines 70-77

Sprint 3 adds **two engines** (credit calc + action recommendation), **one
new admin controller surface** (~7 endpoints), **two Next.js routes**
(landing + detail), and **one status-config admin route** (SPKB-3664).
No new schema migration unless verification (§2 below) surfaces a gap.
The 4 admin-only services + 2 helpers are designed for full test
coverage at 80%+ on the new code.

---

## 2. Schema check (V89 readiness)

V89 already ships with every column Sprint 3 will write to. Cross-reference
against §8 (credit calc), §9 (recommendation), §3.2-3.4 of the design notes
(admin review UI):

### 2.1 Columns used by credit calc

| Engine reads/writes | Column | V89 location | Status |
|---|---|---|---|
| Read: amount paid | `missing_device_lines.amount_paid` | V89:109 | ✅ present |
| Read: amount paid | `wrong_device_lines.expected_amount_paid` | V89:144 | ✅ present |
| Read: amount paid | `encumbered_device_lines.amount_paid` | V89:189 | ✅ present |
| Read: latest price | `wrong_device_lines.latest_price` | V89:155 | ✅ present (cache target) |
| Read: actual value | `encumbered_device_lines.actual_value` | V89:193 | ✅ present |
| Read: review decision | `*.review_decision` | V89:114, 162, 195 | ✅ present (default `PENDING`) |
| Write: amount to credit | `*.amount_to_credit` | V89:116, 164, 197 | ✅ present |
| Write: header summary | `credit_requests.{requested,approved}_{skus,qty,total}` | V89:66-71 | ✅ present |

### 2.2 Columns used by recommendation engine

| Engine reads/writes | Column | V89 location | Status |
|---|---|---|---|
| Read: expected brand | `wrong_device_lines.expected_brand` | V89:140 | ✅ present |
| Read: expected ecoatm code + grade | `wrong_device_lines.expected_ecoatm_code`, `expected_grade` | V89:145, 142 | ✅ present |
| Read: actual ecoatm code + grade | `wrong_device_lines.actual_ecoatm_code`, `actual_grade` | V89:150, 151 | ✅ present |
| Read: expected_week_id | `wrong_device_lines.expected_week_id` | V89:146 | ✅ present (FK → mdm.week) |
| Read: latest_price | `wrong_device_lines.latest_price` | V89:155 | ✅ present |
| Write: action_recommendation | `wrong_device_lines.action_recommendation` | V89:158 | ✅ present (`ACCEPT` / `DECLINE` CHECK) |

### 2.3 Columns used by admin review surface

| UI reads | Column | V89 location | Status |
|---|---|---|---|
| Header strip | `credit_requests.{party_name,order_number,order_created_date,order_shipped_date,buyer_code_id,reviewed_by_id}` | V89:53-79 | ✅ |
| Status pill | `credit_request_statuses.{system_status,external_status_text,internal_status_text,color_hex}` | V89:18-30 | ✅ |
| Ship Status col | `missing_device_lines.ship_status` | V89:111 | ✅ |
| Prolog Result dropdown | `encumbered_device_lines.prolog_result` | V89:191 | ✅ |
| Actual Value input | `encumbered_device_lines.actual_value` | V89:193 | ✅ |
| Reviewer audit | `credit_requests.{reviewed_by_id,review_completed_on,changed_by_id,changed_date}` | V89:73-84 | ✅ |

### 2.4 V90 needed?

**No new columns required.** Two index adds would be nice-to-have but not
required for correctness. Sprint 3 ships **without a new migration**.
Optional V90 (defer to Sprint 4 polish if not needed):

```sql
-- Speed up "load all lines for one request" — JPA already covers this via
-- @ManyToOne FK index (V89:124, 172, 207 already index credit_request_id).
-- No additional indexes needed.

-- Speed up "list pending requests for status filter" — V89:90
-- (idx_cr_status_request_date) already covers this.
```

**Decision:** ship Sprint 3 with no schema migration. If Chunk 6 admin
landing performance is unacceptable on the dev dataset, add a covering
index as a follow-up.

---

## 3. New backend services

All Java sources live under
`backend/src/main/java/com/ecoatm/salesplatform/service/partialcredit/`.

### 3.1 `CreditCalculationService` (interface) + `CreditCalculationServiceImpl` (default impl)

**File paths:**
- `service/partialcredit/CreditCalculationService.java`
- `service/partialcredit/CreditCalculationServiceImpl.java`

**Purpose:** §8 of impl-plan. Compute `amount_to_credit` per line + recompute header summary.

**Public method signatures:**

```java
public interface CreditCalculationService {

    /** Compute amount_to_credit for one missing-device line.
     *  Missing: AmountToCredit = AmountPaid (full credit). */
    BigDecimal computeMissingDeviceCredit(MissingDeviceLine line);

    /** Compute amount_to_credit for one wrong-device line.
     *  Wrong: AmountToCredit = max(0, ExpectedAmountPaid - LatestPrice).
     *  If latest_price is null, throw IllegalStateException — caller must
     *  ensure latest_price is computed before this method runs. */
    BigDecimal computeWrongDeviceCredit(WrongDeviceLine line);

    /** Compute amount_to_credit for one encumbered-device line.
     *  Encumbered: AmountToCredit = ActualValue if present, else AmountPaid. */
    BigDecimal computeEncumberedDeviceCredit(EncumberedDeviceLine line);

    /** Recompute and persist the header summary fields on a request.
     *  Runs after any per-line decision change. */
    HeaderSummary recomputeAndPersistHeaderSummary(Long creditRequestId);

    record HeaderSummary(int requestedSkus, int requestedQty,
                         BigDecimal requestedTotal,
                         int approvedSkus, int approvedQty,
                         BigDecimal approvedTotal) {}
}
```

**Key business logic / SQL:**

```java
// recomputeAndPersistHeaderSummary — uses repository queries:
SELECT COUNT(DISTINCT expected_ecoatm_code) FROM wrong_device_lines WHERE credit_request_id = :id
SELECT COUNT(DISTINCT ecoatm_code) FROM missing_device_lines WHERE credit_request_id = :id
SELECT COUNT(DISTINCT ecoatm_code) FROM encumbered_device_lines WHERE credit_request_id = :id
SELECT SUM(amount_to_credit) FROM ... WHERE credit_request_id = :id AND review_decision = 'ACCEPTED'
```

Implement these as `@Query` methods on the respective repositories
(see §4 below).

**Error model:** throws `IllegalStateException` when wrong-device line
has `latest_price = null` (caller's contract violation); throws
`EntityNotFoundException` when credit request ID doesn't exist.

### 3.2 `ActionRecommendationService` (interface) + `ActionRecommendationServiceImpl`

**File paths:**
- `service/partialcredit/ActionRecommendationService.java`
- `service/partialcredit/ActionRecommendationServiceImpl.java`

**Purpose:** §9 of impl-plan. Brand-keyed device-diff classifier that
recommends Accept or Decline on each wrong-device line.

**Public method signatures:**

```java
public interface ActionRecommendationService {

    /** Returns ACCEPT or DECLINE for a wrong-device line. Pure function;
     *  no DB writes. Caller persists the result to
     *  WrongDeviceLine.actionRecommendation. */
    ActionRecommendation recommend(WrongDeviceLine line);

    /** Bulk variant — runs recommend() on every wrong-device line for a
     *  request and persists action_recommendation. Runs once at
     *  review-open. */
    void recomputeAndPersistRecommendations(Long creditRequestId);
}
```

**Key business logic:**

```java
// From impl-plan §9 — strict ports of the brand-keyed diff rules.
public ActionRecommendation recommend(WrongDeviceLine line) {
    String expectedBrand = line.getExpectedBrand();
    Set<String> approvedBrands = Set.of("Apple", "Samsung", "Motorola");

    if (expectedBrand == null || !approvedBrands.contains(expectedBrand)) {
        return ActionRecommendation.DECLINE;
    }
    if (isNoPower(line.getExpectedModel())) {  // helper — matches "no power" string
        return ActionRecommendation.DECLINE;
    }
    DeviceDiff diff = compareDevices(line);
    if (diff.isCapacityOrModelOnly()) {
        return ActionRecommendation.ACCEPT;
    }
    if (diff.isGradeOrColor()) {
        return ActionRecommendation.DECLINE;
    }
    BigDecimal latest = line.getLatestPrice();
    if (latest != null && line.getExpectedAmountPaid() != null
            && latest.compareTo(line.getExpectedAmountPaid()) > 0) {
        return ActionRecommendation.DECLINE;  // received worth more than paid
    }
    return ActionRecommendation.DECLINE;  // safe default
}
```

`DeviceDiff` is a private inner type that compares `(expected_ecoatm_code,
expected_grade, expected_model, ...)` against `(actual_ecoatm_code, ...)`.
Initial implementation: simple string equality on a few key dimensions;
can be refined as test cases drive corner-case discovery.

**Error model:** throws `IllegalStateException` if `expected_brand` is
null and no diff can be computed. Never returns null.

### 3.3 `MaxSubmittedBidLookup` (helper)

**File path:** `service/partialcredit/MaxSubmittedBidLookup.java`

**Purpose:** SUB_PartialCredit_GetMaxSubmittedBid equivalent. Queries
`auctions.bid_data` for the max submitted bid amount filtered by
(week_id ∈ all bid_rounds of week, ecoid, merged_grade).

**Public method signature:**

```java
@Service
public class MaxSubmittedBidLookup {
    private final BidDataRepository bidDataRepository;

    /** Returns max(submitted_bid_amount) across all bid_rounds for the
     *  given week + (ecoid, merged_grade) tuple. Returns null if no
     *  bid_data rows match. */
    public BigDecimal getMaxSubmittedBid(Long weekId, String ecoid, String mergedGrade);
}
```

**SQL (added to BidDataRepository):**

```sql
SELECT MAX(bd.submitted_bid_amount)
FROM auctions.bid_data bd
JOIN auctions.bid_round br ON bd.bid_round_id = br.id
JOIN auctions.scheduling_auctions sa ON br.scheduling_auction_id = sa.id
WHERE sa.week_id = :weekId
  AND bd.ecoid = :ecoid
  AND bd.merged_grade = :mergedGrade
  AND bd.submitted_bid_amount IS NOT NULL
```

(Adjust JPQL to the actual repo conventions — verify exact JPA field
names on `BidRound` and `SchedulingAuction` before writing.)

**Error model:** returns `null` when no rows match — caller decides
how to treat (caller should not crash; recommendation engine should
treat as `latest_price = 0` for the "received worth more than paid"
check).

### 3.4 `ResolveReceivedDeviceService` (helper)

**File path:** `service/partialcredit/ResolveReceivedDeviceService.java`

**Purpose:** Take a buyer-entered string (IMEI or model name) and
resolve to a `(ecoatm_code, grade)` tuple via `mdm.device`. Phase 1
implementation is a thin lookup; Phase 2 may add fuzzy match.

**Public method signature:**

```java
public interface ResolveReceivedDeviceService {

    /** Resolve a buyer-entered IMEI/model string to the ecoatm_code +
     *  grade of the device they likely received. Returns empty when
     *  no match. */
    Optional<ResolvedDevice> resolve(String imeiOrModel);

    record ResolvedDevice(String ecoatmCode, String grade, String brand,
                          String model) {}
}
```

**Lookup strategy:**
1. If string is 15 digits → treat as IMEI; query `mdm.device` by IMEI prefix or
   a TAC lookup table (out of scope — Phase 2 adds the TAC table).
   Phase 1: return `Optional.empty()` for IMEI-style inputs.
2. Else → treat as model name; query `mdm.device` by `device_code` or
   model fuzzy-match (`LIKE %...%`).
3. Cache results in a per-request `Map<String, ResolvedDevice>` to avoid
   repeated lookups.

**Error model:** never throws; returns `Optional.empty()` for unmatched
inputs. Caller (`AdminCreditRequestService.openReview`) handles the empty
case by leaving `actual_ecoatm_code` + `actual_grade` null on the line.

### 3.5 `WeekResolver` — extend existing `WeekRepository`

**File path:** `repository/mdm/WeekRepository.java` (add a method)

**New method:**

```java
/** Resolves a date to the auction Week it falls within
 *  (weekStartDateTime <= date < weekEndDateTime). */
@Query("SELECT w FROM Week w WHERE w.weekStartDateTime <= :date AND w.weekEndDateTime > :date")
Optional<Week> findByDate(@Param("date") Instant date);
```

No new file needed. Reuse `WeekRepository`.

### 3.6 `AdminCreditRequestService` (NEW)

**File path:** `service/partialcredit/AdminCreditRequestService.java`

**Decision: new dedicated service vs. extend `CreditRequestService`?**

**Recommendation: NEW dedicated `AdminCreditRequestService` class.**

Rationale:
- `CreditRequestService` is the buyer-facing service — it carries
  buyer-code scoping logic and treats every operation as "owned by
  current user's buyer_code". Adding admin methods that bypass that
  scoping would muddle the contract.
- Sprint 3 admin code paths exercise different invariants: status
  transitions are `PENDING_APPROVAL → UNDER_REVIEW → APPROVED|DECLINED`
  (admin-only); buyer service handles `DRAFT → PENDING_APPROVAL`.
- Separate class makes role-based testing cleaner (`@PreAuthorize` for
  admin role is enforced at the controller; the service can assume
  authentication has already passed and just trust the input).
- Same pattern as the codebase already uses: `BuyerOverviewController`
  + `AdminMasterDataController` are two surfaces; `RmaController` (buyer)
  vs admin RMA surface (if it exists). The split is idiomatic.

**Public method signatures:**

```java
@Service
@Transactional
public class AdminCreditRequestService {

    /** Lists requests filtered by status + (optional) date / buyer /
     *  company / order# / reason. Status counts returned alongside the
     *  paged list. */
    AdminListResult list(AdminListFilters filters, Pageable pageable);

    /** Opens a request for review. Transitions PENDING_APPROVAL →
     *  UNDER_REVIEW on first open. Computes latest_price + action
     *  recommendation for each wrong-device line. Returns the full detail. */
    CreditRequestDetail openReview(Long creditRequestId, Long reviewerUserId);

    /** Sets the review_decision on one line. Recomputes amount_to_credit
     *  on that line + the header summary. */
    LineDecisionResult setLineDecision(Long creditRequestId, Long lineId,
                                        ReasonKind reason,
                                        ReviewDecision decision,
                                        Long reviewerUserId);

    /** Bulk: sets every PENDING line in the section to the same decision.
     *  Skips lines that already have a non-PENDING decision. */
    SectionDecisionResult setSectionDecision(Long creditRequestId,
                                              ReasonKind reason,
                                              ReviewDecision decision,
                                              Long reviewerUserId);

    /** Sets prolog_result + actual_value on one encumbered line.
     *  Recomputes amount_to_credit on that line. */
    EncumberedUpdateResult setEncumberedDetails(Long creditRequestId,
                                                 Long lineId,
                                                 PrologResult prolog,
                                                 BigDecimal actualValue,
                                                 Long reviewerUserId);

    /** Finalises review. Validates every line has a decision !=
     *  PENDING. Status flips to APPROVED or DECLINED based on the
     *  outcome parameter. Fires the review-completed email. */
    CompleteReviewResult completeReview(Long creditRequestId,
                                          ReviewOutcome outcome,
                                          Long reviewerUserId);

    enum ReasonKind { MISSING, WRONG, ENCUMBERED }
    enum ReviewOutcome { APPROVE, DECLINE }
}
```

**Filter record:**

```java
public record AdminListFilters(
    SystemStatus status,      // null = all
    LocalDate dateFrom,
    LocalDate dateTo,
    String buyer,             // contains-match on buyer name
    String company,           // contains-match on party_name
    String orderNumber,       // exact match
    ReasonKind reason         // null = all
) {}
```

**Error model:**
- Throws `EntityNotFoundException` for unknown credit_request_id.
- Throws `IllegalStateException` for status-transition violations
  (e.g. `completeReview` called when status is not `UNDER_REVIEW`).
- Throws `IllegalArgumentException` if `completeReview` is called when
  any line still has `review_decision = PENDING`.
- Throws `IllegalArgumentException` if the request reason doesn't match
  the section kind (e.g. setting a Wrong decision on a Missing-only request).

### 3.7 Repository additions (new query methods)

| Repository | New method | Purpose |
|---|---|---|
| `CreditRequestRepository` | `Page<CreditRequest> findByFiltersAdmin(AdminListFilters f, Pageable p)` | Admin landing list |
| `CreditRequestRepository` | `long countByStatusId(Long statusId)` | Status counter chips |
| `CreditRequestRepository` | `Optional<CreditRequest> findByIdForAdminUpdate(Long id)` | Pessimistic lock for `openReview` first-reviewer race |
| `MissingDeviceLineRepository` | `List<MissingDeviceLine> findByCreditRequestId(Long id)` | already exists from Sprint 2 |
| `WrongDeviceLineRepository` | same | already exists |
| `EncumberedDeviceLineRepository` | same | already exists |
| `BidDataRepository` | `BigDecimal findMaxSubmittedBid(Long weekId, String ecoid, String mergedGrade)` | §3.3 helper SQL |
| `WeekRepository` | `Optional<Week> findByDate(Instant date)` | §3.5 |

### 3.8 Service-layer event for email

`AdminCreditRequestService.completeReview` publishes a Spring event
`PartialCreditReviewCompletedEvent(creditRequestId, outcome,
reviewerUserId)`. A listener `PartialCreditReviewEmailListener`
consumes it asynchronously and sends the buyer email via the existing
`EmailSender` infrastructure. **The Sprint 3 implementation publishes
the event; the listener is gated behind a feature flag and ships as a
log-only stub for Sprint 3 (real email sending lands in Sprint 4) —
see §6.**

---

## 4. New REST endpoints

All admin endpoints live under `/api/v1/admin/partial-credit/**` in a
new controller class.

**File path:** `controller/admin/AdminPartialCreditController.java`

**Class-level annotations:**

```java
@RestController
@RequestMapping("/api/v1/admin/partial-credit")
@PreAuthorize("hasAnyRole('PartialCredit_SalesOps','PartialCredit_Admin',"
            + "'SalesOps','SalesRep','Administrator','CoAdministrator')")
public class AdminPartialCreditController { ... }
```

(Same liberal allowlist Sprint 2's `BuyerPartialCreditController` uses
for `Bidder` — accepts both the new `PartialCredit_*` roles and the
existing dev-seeded role names so the surface is reachable before
SPKB-3659 wires the role mapping.)

### 4.1 `GET /api/v1/admin/partial-credit`

**Purpose:** Landing list with filters + status counters.

| Field | Value |
|---|---|
| HTTP method + path | `GET /api/v1/admin/partial-credit` |
| Query params | `status`, `dateFrom`, `dateTo`, `buyer`, `company`, `orderNumber`, `reason`, `page`, `size`, `sort` |
| Response DTO | `AdminListResponse { content: AdminListRow[], totalElements, totalPages, counters: {pendingApproval, approved, declined, all} }` |
| Success status | `200 OK` |
| Error cases | `400 Bad Request` on malformed date strings; `500` on unexpected |

`AdminListRow` shape (per design notes §2.5):

```java
public record AdminListRow(
    Long id,
    String requestNumber,
    Instant dateSubmitted,
    String buyerName,
    String partyName,
    String orderNumber,
    List<String> reasons,        // e.g. ["Missing Device", "Wrong Device"]
    String externalStatusText,   // from credit_request_statuses
    String statusColorHex
) {}
```

### 4.2 `GET /api/v1/admin/partial-credit/{id}/review`

**Purpose:** Open the review page. **Side effect:** transitions
`PENDING_APPROVAL → UNDER_REVIEW` on first call (per impl-plan §12).
Computes `latest_price` + `action_recommendation` for each wrong-device
line if not yet cached.

| Field | Value |
|---|---|
| Path param | `id` (Long) |
| Response DTO | `AdminReviewDetail` — same shape as `CreditRequestDetail` from Sprint 2 with admin-only fields added (reviewer info, recommendations) |
| Success status | `200 OK` |
| Error cases | `404 Not Found`; `409 Conflict` if status is `DRAFT` |

### 4.3 `POST /api/v1/admin/partial-credit/{id}/lines/{lineId}/decision`

**Purpose:** Per-line accept/decline.

| Field | Value |
|---|---|
| Request DTO | `LineDecisionRequest { reason: "MISSING"\|"WRONG"\|"ENCUMBERED", decision: "ACCEPTED"\|"DECLINED" }` |
| Response DTO | `LineDecisionResponse { updatedLine, headerSummary }` |
| Success status | `200 OK` |
| Error cases | `404` (unknown id/lineId); `409` (wrong reason); `403` if review is no longer in `UNDER_REVIEW` |

### 4.4 `POST /api/v1/admin/partial-credit/{id}/sections/{kind}/decision`

**Purpose:** Per-section bulk decision. Skips lines that already have a
non-PENDING decision.

| Field | Value |
|---|---|
| Path params | `id`, `kind` (`missing`/`wrong`/`encumbered`) |
| Request DTO | `SectionDecisionRequest { decision: "ACCEPTED"\|"DECLINED" }` |
| Response DTO | `SectionDecisionResponse { updatedCount, skippedCount, headerSummary }` |
| Success status | `200 OK` |

### 4.5 `POST /api/v1/admin/partial-credit/{id}/lines/{lineId}/encumbered`

**Purpose:** Set Prolog Result + Actual Value on an encumbered line.

| Field | Value |
|---|---|
| Request DTO | `EncumberedDetailsRequest { prologResult: "ENCUMBERED"\|"NOT_ENCUMBERED"\|"PENDING", actualValue: BigDecimal }` |
| Response DTO | `EncumberedDetailsResponse { updatedLine, headerSummary }` |
| Success status | `200 OK` |
| Error cases | `400` if line isn't an encumbered line; `404` for unknown id |

### 4.6 `POST /api/v1/admin/partial-credit/{id}/complete-review`

**Purpose:** Final transition.

| Field | Value |
|---|---|
| Request DTO | `CompleteReviewRequest { outcome: "APPROVE"\|"DECLINE" }` |
| Response DTO | `CompleteReviewResponse { id, requestNumber, finalStatus, emailQueued: boolean }` |
| Success status | `200 OK` |
| Error cases | `409` if not in `UNDER_REVIEW`; `422 Unprocessable Entity` if any line is still PENDING |

### 4.7 Status-config admin endpoints (SPKB-3664)

For the dedicated status-config page:

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/v1/admin/partial-credit/statuses` | List all 5 status rows |
| `PATCH` | `/api/v1/admin/partial-credit/statuses/{id}` | Update text/color/sort_order on one row |

`PATCH` request DTO: `StatusUpdateRequest { internalStatusText, externalStatusText, colorHex, sortOrder, showInUserCounters }`.

`system_status` is **NOT** editable — it's a stable enum keyed by code.
Only the cosmetic fields update.

### 4.8 Security path matchers

Add to `SecurityConfig` (existing file —
`config/SecurityConfig.java`):

```java
.requestMatchers("/api/v1/admin/partial-credit/**").authenticated()
```

The `@PreAuthorize` annotation on the controller does the role gating;
the `SecurityConfig` change only ensures the path requires authentication
(not anonymous).

---

## 5. New Next.js admin routes

### 5.1 Route prefix verification

Existing admin routes live under
`frontend/src/app/(dashboard)/admin/auctions-data-center/`:
- `auctions/` (scheduling)
- `bid-data/`
- `inventory/`
- `purchase-orders/`
- `reserve-bids/`
- `round3-bid-report/`
- `schedule-auction/`
- `scheduling-auctions/`

**Confirmed prefix:** `/admin/auctions-data-center/partial-credit/...`

This matches CLAUDE.md examples for the surface and the modern plan
§3 line 76. Use it verbatim.

### 5.2 Landing page

**Route:** `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/page.tsx`

**Components needed:**
- `<StatusChipRow>` — 4 chips (Pending Approval, Approved, Declined, All). Reuse the chip pattern from Sprint 2 buyer landing.
- `<AdminTable>` — 7 columns per design notes §2.4. Reuse the existing `<DataGrid>` or build a custom table.
- `<EmptyState>` — single-line text matching Figma verbatim.
- `<DownloadButton>` — disabled placeholder (Sprint 4 export work).
- `<FilterRow>` — column-level filter chip inputs. Sprint 3 ships a **minimal** filter UI (status dropdown only). Per-column filtering deferred per design notes §8.13.

**Data fetching:** Server component → call `GET /api/v1/admin/partial-credit?status=...` via the existing `adminPartialCreditClient` (new file).

**Frontend client:** `frontend/src/lib/adminPartialCreditClient.ts` —
mirrors `frontend/src/lib/partialCreditClient.ts` (Sprint 2 pattern). 7
exported functions matching the 7 endpoints from §4.

### 5.3 Review detail page

**Route:** `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/[id]/page.tsx`

**Components needed:**
- `<Breadcrumb>` — "All Partial Credit Requests" → request number
- `<RequestHeaderStrip>` — 6 fields (Request Date, Buyer, Company, Order Number, Request Reason, Status). Per design notes §3.2.
- `<SummaryPanels>` — 2 mini-tables (Requested Credit, Total Credit). Per design notes §3.3.
- `<CompleteReviewButton>` — top-right of header strip. Opens modal.
- `<ReasonSection>` — generic section card. Props: `title`, `lines`, `columns`, `onLineDecisionChange`, `onSectionBulkAction`. Used 3x (one per reason).
- `<LineActionDropdown>` — per-row Action dropdown. Renders default selection from `action_recommendation` (Wrong only); reviewer override updates `review_decision`.
- `<PrologResultDropdown>` + `<ActualValueInput>` — encumbered-only per-row inputs.
- `<BulkActionRow>` — "Approve All" button per section.
- `<CompleteReviewModal>` — `Confirm` / `Cancel` buttons. Body copy from §5.2 of design notes (placeholder, awaiting design follow-up).
- `<RecommendationTooltip>` — small `circle-info` icon next to the
  default selection, hover shows the reasoning ("Recommended: Decline —
  Latest Price $50 exceeds Amount Paid $40").

**Per-section component variant per reason:**

| Section | Variant component file |
|---|---|
| `MissingDeviceSection` | `partial-credit/[id]/MissingDeviceSection.tsx` |
| `WrongDeviceSection` | `partial-credit/[id]/WrongDeviceSection.tsx` |
| `EncumberedDeviceSection` | `partial-credit/[id]/EncumberedDeviceSection.tsx` |

These wrap the shared `<ReasonSection>` with reason-specific column
definitions + per-row cell renderers.

### 5.4 Status config page (SPKB-3664)

**Route:** `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/statuses/page.tsx`

**Components:**
- Simple grid: one row per status (5 rows from V89 seed).
- Editable cells for `internal_status_text`, `external_status_text`,
  `color_hex` (color picker), `sort_order`, `show_in_user_counters` (toggle).
- `system_status` is read-only.
- Save-on-blur → calls `PATCH /api/v1/admin/partial-credit/statuses/{id}`.

### 5.5 Shared CSS module

**File:** `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/admin.module.css`

Reuse Sprint 2's `wizard.module.css` patterns where they apply (status
pills, chip colors, table cell borders, font sizes). New tokens:
- `.actionCell` — green-tint `#D8E5D9` background, sticky-right
- `.amountCreditCell` — same tint
- `.sectionCard` — white fill, 8px radius, shadow per design notes §6
- `.bulkActionButton` — green primary with `Approve All` styling
- `.headerStripField` — column flex with 8px gap, 13px label + 18px value

**Decision: NEW `admin.module.css` (not extending wizard).** The admin
surface has distinct interaction patterns (sticky action columns, bulk
buttons, mini-tables) that don't reuse the wizard's step-indicator /
file-drop / barcode-textarea patterns. Separate module is cleaner.

---

## 6. Email — "Review Completed"

### 6.1 Existing email infrastructure (verified)

The codebase already has:
- `service/email/EmailSender.java` — interface
- `service/email/EmailMessage.java` — value class
- `service/email/LoggingEmailSender.java` — dev/log impl
- `service/email/SmtpEmailSender.java` — prod impl
- `service/email/PwsOfferEmailEvent.java` + `PwsOfferEmailListener.java`
  — Spring event pattern for async send

No `EmailTemplate` table is in the schema today. The existing senders
take fully-rendered subject + body strings.

### 6.2 Decision: hardcode email copy in Sprint 3

Reuse the existing `EmailSender` infrastructure. Hardcode the subject +
body for `PartialCredit_ReviewCompleted_Approved` and
`PartialCredit_ReviewCompleted_Declined` in a new
`PartialCreditReviewEmailListener` class.

**Sprint 4 / SPKB-3664** can introduce the admin-editable `email_templates`
table + `EmailTemplateService` if/when the team wants admin-tunable copy
without redeploy. Out of scope for Sprint 3.

**File path:** `service/email/PartialCreditReviewEmailListener.java`

```java
@Component
public class PartialCreditReviewEmailListener {

    private final EmailSender emailSender;
    private final CreditRequestRepository repo;
    private final UserRepository userRepo;

    @Async
    @EventListener
    public void handle(PartialCreditReviewCompletedEvent event) {
        CreditRequest cr = repo.findById(event.creditRequestId()).orElseThrow();
        // Look up buyer email via buyer_code → user_buyer junction
        String buyerEmail = lookupBuyerEmail(cr.getBuyerCodeId());
        String subject = event.outcome() == APPROVE
            ? "Your partial credit request " + cr.getRequestNumber() + " has been approved"
            : "Your partial credit request " + cr.getRequestNumber() + " has been declined";
        String body = renderBody(cr, event.outcome());
        emailSender.send(new EmailMessage(buyerEmail, subject, body));
    }
}
```

**Sprint 3 ships:** the listener wired but gated behind a property
`partial-credit.review-completed-email.enabled` (default `false`). In
dev, the email isn't actually sent; the listener logs the intent.
**Sprint 4** flips the flag + adds the template machinery.

---

## 7. Status config page (SPKB-3664)

### 7.1 Verification: does buyer landing render color/text live from DB?

**Verified yes.** Sprint 2's `BuyerPartialCreditController.toSummary` reads
the `CreditRequestStatus` entity and surfaces `externalStatusText` +
`colorHex` to the frontend. Updating the V89 seed rows at runtime via the
new SPKB-3664 endpoints will immediately reflect on the buyer landing
on next reload — no app restart needed.

(See `controller/BuyerPartialCreditController.java#toSummary` lines
196-220.)

### 7.2 Effort estimate

| Sub-task | Effort |
|---|---|
| Backend: `GET /statuses` + `PATCH /statuses/{id}` endpoints | 2h |
| Frontend: status-config page with 5-row editable grid | 3h |
| Integration test: PATCH then re-GET on buyer landing reflects change | 1h |
| Playwright: navigate to status-config, change color, navigate to buyer landing, assert pill color changed | 1h |
| Total | **~7h** |

---

## 8. Chunk breakdown

8 bisectable chunks, each one a single commit, sized S/M/L. Total
estimate **~38-50 hours** (~5-6 working days for one engineer).

| # | Chunk | Effort | Depends on |
|---|---|---|---|
| 1 | Credit calc engine + repository queries | M ~5-7h | — |
| 2 | Recommendation engine + helper services | M ~5-7h | 1 |
| 3 | `AdminCreditRequestService` + repository additions | L ~6-8h | 1, 2 |
| 4 | `AdminPartialCreditController` + 7 REST endpoints | M ~5-6h | 3 |
| 5 | Admin landing Next.js page | M ~5-6h | 4 |
| 6 | Admin review detail Next.js page (3 reason sections + Complete Review modal) | L ~8-10h | 4, 5 |
| 7 | Status-config endpoints + page (SPKB-3664) | M ~5-7h | 4 |
| 8 | Playwright E2E + email listener wiring | M ~4-5h | 6, 7 |

### Chunk 1 — Credit calc engine (M, ~5-7h)

**Files:**
- `service/partialcredit/CreditCalculationService.java` (interface, ~30 lines)
- `service/partialcredit/CreditCalculationServiceImpl.java` (~200 lines)
- `repository/auctions/BidDataRepository.java` (add `findMaxSubmittedBid` JPQL method)
- `repository/mdm/WeekRepository.java` (add `findByDate` JPQL method)
- `service/partialcredit/MaxSubmittedBidLookup.java` (~60 lines)
- `service/partialcredit/ResolveReceivedDeviceService.java` (~80 lines, Phase-1 lookup only)

**Deliverables:**
- Pure functions implementing the §8 credit-math formulas
- `recomputeAndPersistHeaderSummary` recomputes `requested_*` + `approved_*` fields from line decisions

**Tests:**
- `CreditCalculationServiceTest` — JUnit unit, ~12 cases:
  - Missing accepted → amount_to_credit = amount_paid
  - Missing declined → 0
  - Wrong accepted, latest_price < paid → paid - latest_price
  - Wrong accepted, latest_price > paid → 0 (max-zero floor)
  - Wrong accepted, latest_price = null → throws
  - Encumbered accepted, actual_value present → actual_value
  - Encumbered accepted, actual_value null → amount_paid
  - Header summary: 3 missing lines (2 accepted) → correct skus/qty/total
- `MaxSubmittedBidLookupIT` — Testcontainers Postgres IT, ~3 cases:
  - Returns max over multiple bid rounds
  - Returns null when no rows match
  - Returns null when submitted_bid_amount is null on all matching rows
- `ResolveReceivedDeviceServiceTest` — JUnit unit, ~5 cases:
  - 15-digit IMEI → empty (Phase 1 stub)
  - Model name with exact match → resolved
  - Model name with no match → empty
  - Null input → empty
  - Empty string → empty

**Commit:** `feat(partial-credit): credit calculation engine for admin review`

### Chunk 2 — Recommendation engine + helper services (M, ~5-7h)

**Files:**
- `service/partialcredit/ActionRecommendationService.java` (interface, ~25 lines)
- `service/partialcredit/ActionRecommendationServiceImpl.java` (~150 lines)

**Deliverables:**
- Brand-keyed device-diff classifier
- Bulk recompute method (called by `AdminCreditRequestService.openReview`)
- Persists `wrong_device_lines.action_recommendation`

**Tests:**
- `ActionRecommendationServiceTest` — JUnit unit, ~10 cases (matching the §9 rule branches):
  - Expected brand "Apple" + capacity diff → ACCEPT
  - Expected brand "Apple" + grade diff → DECLINE
  - Expected brand "Sony" (not in approved list) → DECLINE
  - Expected brand "Apple" + "no power" model → DECLINE
  - Expected brand "Samsung" + latest_price > paid → DECLINE
  - Expected brand "Motorola" + latest_price = null + capacity diff → ACCEPT
  - Null expected brand → DECLINE (safe default)
  - Null actual ecoatm code → DECLINE
  - Color diff (e.g. iPhone Black → iPhone White) → DECLINE
  - Model diff (e.g. iPhone XR → iPhone 11) → ACCEPT

**Commit:** `feat(partial-credit): action recommendation engine for wrong-device lines`

### Chunk 3 — `AdminCreditRequestService` + repository additions (L, ~6-8h)

**Files:**
- `service/partialcredit/AdminCreditRequestService.java` (~300 lines)
- `repository/partialcredit/CreditRequestRepository.java` (add 3 query methods)
- `service/partialcredit/AdminListResult.java` + `AdminListFilters.java` + result records
- `event/PartialCreditReviewCompletedEvent.java` (Spring event record)

**Deliverables:**
- All 6 admin service methods implemented per §3.6
- Atomic `openReview` flips status via pessimistic-update (or `@Version` optimistic-lock — pick one, document)
- Header-summary recompute called after every line decision change
- `completeReview` publishes the event for the email listener

**Tests:**
- `AdminCreditRequestServiceTest` — JUnit unit, ~20 cases:
  - `list` filters by status correctly
  - `list` returns correct counters
  - `openReview` flips PENDING_APPROVAL → UNDER_REVIEW + computes latest_price
  - `openReview` is idempotent on second call (already UNDER_REVIEW)
  - `setLineDecision` updates row + recomputes header summary
  - `setLineDecision` rejects wrong reason kind
  - `setSectionDecision` skips already-decided lines
  - `setEncumberedDetails` recomputes amount_to_credit
  - `completeReview` rejects when any line is PENDING
  - `completeReview` flips to APPROVED on outcome=APPROVE
  - `completeReview` publishes the event
  - `completeReview` rejects when status is not UNDER_REVIEW
  - ... etc

**Commit:** `feat(partial-credit): admin review orchestration service`

### Chunk 4 — `AdminPartialCreditController` + 7 endpoints (M, ~5-6h)

**Files:**
- `controller/admin/AdminPartialCreditController.java` (~200 lines)
- `dto/partialcredit/AdminListRow.java` + `AdminListResponse.java` + 6 other request/response DTOs
- `config/SecurityConfig.java` (add path matcher — already permitted under `authenticated()` likely, but verify)

**Deliverables:**
- 7 REST endpoints from §4 fully wired
- OpenAPI annotations on each endpoint for Swagger UI

**Tests:**
- `AdminPartialCreditControllerIT` — Spring MVC slice IT, ~12 cases:
  - `GET /` returns the right shape + paging metadata
  - `GET /` 401 when unauthenticated
  - `GET /` 403 when authenticated but wrong role
  - `GET /{id}/review` flips status on first call
  - `POST /{id}/lines/{lineId}/decision` updates the line
  - `POST /{id}/sections/wrong/decision` skips already-decided lines
  - `POST /{id}/lines/{lineId}/encumbered` sets prolog + actual_value
  - `POST /{id}/complete-review` rejects PENDING lines (422)
  - `POST /{id}/complete-review` succeeds when all decided (200)
  - ... etc

**Commit:** `feat(partial-credit): admin REST surface for review workflow`

### Chunk 5 — Admin landing Next.js page (M, ~5-6h)

**Files:**
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/page.tsx`
- `frontend/src/lib/adminPartialCreditClient.ts` (~120 lines, Zod schemas + 7 functions)
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/admin.module.css` (~150 lines)
- `frontend/src/components/admin/StatusChipRow.tsx` (~60 lines)

**Deliverables:**
- Landing with 7 columns + 4 status chips + empty state + Download placeholder
- Status filter chip interaction (clicking a chip refilters the list)
- Row eye-icon navigates to `/admin/auctions-data-center/partial-credit/{id}`

**Tests:**
- Frontend unit: `StatusChipRow.test.tsx` — snapshot + click handler
- Visual regression: Playwright screenshot at 1440px (admin user logged in)

**Commit:** `feat(partial-credit): admin landing with status counters and filters`

### Chunk 6 — Admin review detail Next.js page (L, ~8-10h)

**Files:**
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/[id]/page.tsx` (~80 lines, server component data-fetch)
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/[id]/ReviewClient.tsx` (~300 lines, client component with the modal state)
- `MissingDeviceSection.tsx` (~120 lines)
- `WrongDeviceSection.tsx` (~140 lines)
- `EncumberedDeviceSection.tsx` (~150 lines)
- `LineActionDropdown.tsx` (~80 lines)
- `BulkActionRow.tsx` (~40 lines)
- `CompleteReviewModal.tsx` (~80 lines)
- `RequestHeaderStrip.tsx` (~100 lines)
- `SummaryPanels.tsx` (~80 lines)
- `RecommendationTooltip.tsx` (~50 lines)

**Deliverables:**
- All 3 reason sections render with the right columns
- Per-line dropdown updates row state + recomputes summary panels
- Bulk Approve All / Decline All works per section
- Encumbered section: Prolog Result dropdown + Actual Value input wired
- Complete Review modal: Confirm/Cancel flow, fires API, navigates back on success
- Default Action dropdown selection driven by `action_recommendation`
- Wrong-device recommendation tooltip on hover

**Tests:**
- Frontend unit: `LineActionDropdown.test.tsx`, `SummaryPanels.test.tsx`, `CompleteReviewModal.test.tsx`
- Playwright E2E (drafted, fully shipped in Chunk 8)

**Commit:** `feat(partial-credit): admin review detail page with three reason sections`

### Chunk 7 — Status-config endpoints + page (M, ~5-7h)

**Files:**
- Add 2 endpoints to `AdminPartialCreditController` (or a new
  `AdminPartialCreditStatusController` — recommend keeping with main
  admin controller for fewer files)
- `frontend/src/app/(dashboard)/admin/auctions-data-center/partial-credit/statuses/page.tsx`

**Deliverables:**
- Per §7 above

**Tests:**
- IT: PATCH a status row, GET on buyer landing, assert pill color reflects the change
- Playwright: navigate to status-config, change color, navigate back to buyer landing, assert visual diff

**Commit:** `feat(partial-credit): admin status configuration page`

### Chunk 8 — Playwright E2E + email listener wiring (M, ~4-5h)

**Files:**
- `frontend/tests/e2e/admin-partial-credit-review.spec.ts` (~200 lines)
- `service/email/PartialCreditReviewEmailListener.java` (~80 lines)
- `service/email/PartialCreditReviewEmailListenerTest.java` (~60 lines)
- `application.yml`: add `partial-credit.review-completed-email.enabled: false`

**E2E test cases:**
- Admin login (admin@test.com)
- Navigate to landing, see N pending requests
- Click eye on first row, assert URL is `/admin/.../partial-credit/{id}`
- Assert status transitions to "Under Review" (header strip)
- Click Accept on one Missing line, assert summary panel updates
- Click "Approve All" on Wrong section, assert all wrong lines flip
- Click "Complete Review", confirm modal, assert status pill is "Approved" on landing
- Validation: try Complete Review with one PENDING line → 422 error toast

**Listener test:**
- `PartialCreditReviewEmailListenerTest` — Spring slice with mocked `EmailSender`, fires the event, asserts the listener calls `send()` with correct subject prefix.

**Commit:** `test(partial-credit): e2e admin review + email listener wiring`

---

## 9. Test plan (per chunk)

### 9.1 Unit tests (Mockito-style)

| Chunk | Test file | LOC est. | Mocks |
|---|---|---|---|
| 1 | `CreditCalculationServiceTest` | 250 | None (pure functions) |
| 1 | `MaxSubmittedBidLookupIT` | 100 | Testcontainers |
| 1 | `ResolveReceivedDeviceServiceTest` | 100 | `DeviceRepository` |
| 2 | `ActionRecommendationServiceTest` | 200 | None (pure) |
| 3 | `AdminCreditRequestServiceTest` | 400 | `CreditRequestRepository`, `MissingDeviceLineRepository`, `CreditCalculationService`, `ActionRecommendationService`, `ApplicationEventPublisher` |
| 4 | `AdminPartialCreditControllerIT` | 300 | `MockMvc` slice |
| 7 | `AdminPartialCreditStatusUpdateIT` | 100 | `MockMvc` slice |
| 8 | `PartialCreditReviewEmailListenerTest` | 80 | `EmailSender` |

### 9.2 Integration tests (Spring MVC + Postgres Testcontainers)

| Chunk | Test file | What it covers |
|---|---|---|
| 1 | `BidDataRepositoryIT` (extend existing) | `findMaxSubmittedBid` JPQL works on real PG |
| 3 | `AdminCreditRequestServiceIT` | Full transactional flow: create draft → submit → openReview → setLineDecision → completeReview |
| 7 | `StatusConfigUpdatePropagationIT` | PATCH status, fetch via buyer endpoint, assert reflected |

### 9.3 Playwright E2E (in Chunk 8)

`frontend/tests/e2e/admin-partial-credit-review.spec.ts` — see Chunk 8
specification above.

### 9.4 Coverage target

- Per-chunk: each new service class targets 90%+ line coverage on its
  own test file.
- Aggregate across Sprint 3: 85%+ on the new code (matches Sprint 2
  closeout target). Verify via JaCoCo on the
  `service.partialcredit.{CreditCalculationService,
  ActionRecommendationService, AdminCreditRequestService,
  MaxSubmittedBidLookup, ResolveReceivedDeviceService}` packages.

---

## 10. Out-of-scope (Sprint 4 / Phase 2)

These are NOT close-out items; they belong to later sprints by design.

| Item | Deferred to | Source |
|---|---|---|
| Buyer detail page (`/wholesale/partial-credit/[id]`) | Sprint 4 / SPKB-3669 | modern plan §3 line 78 |
| Sales-rep on-behalf submission | Sprint 4 / SPKB-3659 | modern plan §3 |
| Permissions role mapping (`Bidder` → `PartialCredit_Buyer`, etc.) | Sprint 4 / SPKB-3659 | modern plan §6 |
| Post-submit photo uploads on wrong-device lines | Sprint 4 / SPKB-3662 | modern plan §3 + 2026-05-11 decision log #4 |
| File-drop xlsx/csv/docx parsing for barcodes | Sprint 4 | Sprint 2 closeout §5.2 |
| Admin Excel export (Download button on landing) | Sprint 4 | Sprint 2 closeout §3 |
| Email template admin UI (DB-backed `email_templates` table) | Sprint 4 | §6 above |
| Reason-pivoted admin landings (Missing-only, Wrong-only) | Phase 2 | impl-plan §16 line 462 + 2026-05-11 decision |
| Per-column filter pop-overs on admin landing | Sprint 4 polish | design notes §8.13 |
| "Reviewed By: ..." indicator on landing rows | Sprint 4 polish | design notes §8.12 |
| RMA auto-creation for accepted encumbered lines | Phase 2 | impl-plan §18 Q8 |
| Prolog automated encumbrance check (replace manual entry) | Phase 2 | impl-plan §18 Q9 / decision 2026-05-11 |
| Oracle write-back of approved credits | Phase 2 | impl-plan §7 / out-of-scope |
| R-2 certification gating in wizard Step 1 | Phase 2 | impl-plan §16 line 482 |
| Datadog APM dashboards | Phase 2 | impl-plan §15 |

---

## 11. Open questions — RESOLVED 2026-05-11

### Q1 — Complete Review confirmation modal copy — **SHIP PROPOSED**

- Heading: `Complete review?`
- Body: `This will finalise the review and send the buyer a notification email. You cannot undo this action.`
- Buttons: `Cancel` (white, gray border) + `Confirm` (green primary)
- Outcome selector: radio group — `Approved` / `Declined`, defaulted per Q4 below.

### Q2 — Header strip Status: internal vs. external — **INTERNAL ON DETAIL, EXTERNAL ON LIST**

- Admin detail page: internal status text (`Under Review`).
- Admin landing list: external status text (`Pending Approval` for both PENDING_APPROVAL and UNDER_REVIEW).

### Q3 — Encumbered `Actual Value` column always visible? — **ALWAYS RENDER**

Always render the column. Empty / null cell shows placeholder "Enter value" with a green-tinted background. Treat the single-row Figma frame as a design omission.

### Q4 — Default outcome on mixed line decisions — **DEFAULT APPROVED IF ANY LINE ACCEPTED**

Pre-select `Approved` in the Complete Review modal whenever ≥1 line has `review_decision = ACCEPTED`. If every line is DECLINED, default to `Declined`. Reviewer can override the default before Confirm. Modal CTA enabled by default once the modal renders.

### Q5 — Status color hex tokens — V89 seed vs. CSS — **LIVE FROM DB**

Frontend reads `colorHex` from the API response and applies it inline (`style={{ backgroundColor: row.statusColorHex }}`). The SPKB-3664 admin status-config page edits these in-place; no redeploy needed. Sprint 2 buyer landing already follows this pattern.

---

## 12. Verification (Sprint 3)

Manual smoke checks once Chunk 8 is committed:

1. `mvn flyway:migrate` does NOT add a new migration (V89 still latest).
2. Login as `admin@test.com` → navigate to
   `/admin/auctions-data-center/partial-credit` → see at least one
   PENDING_APPROVAL row (assumes Sprint 2 has been used to seed a
   request).
3. Click the eye icon → request status flips to UNDER_REVIEW (verify
   via `psql` query `SELECT status_id, internal_status_text FROM
   partial_credit.credit_requests CR JOIN
   partial_credit.credit_request_statuses CRS ON CR.status_id = CRS.id`).
4. Per-line Accept on a Missing line → summary panel updates instantly.
5. Bulk Approve All on Wrong section → all Wrong lines flip to
   ACCEPTED in one network round-trip.
6. Enter Prolog Result = `Encumbered` + Actual Value = $25 on an
   Encumbered line → `amount_to_credit` is recomputed.
7. Click Complete Review → modal opens → confirm → status flips to
   APPROVED → buyer landing reflects new status pill color.
8. Email listener logs the intent (since flag is off in dev) — verify
   in backend stdout.
9. Navigate to `/admin/.../partial-credit/statuses` → edit
   `APPROVED.color_hex` to `#00C853` → save → navigate to buyer
   landing → confirm the green pill color is now `#00C853`.
10. `mvn verify` runs all tests, JaCoCo report shows 85%+ on the new
    packages.
