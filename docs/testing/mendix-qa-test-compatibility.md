# Mendix QA Playwright Tests vs Modern SalesPlatform Compatibility Report

**Date:** 2026-04-10
**Source:** `qa-playwright-salesplatform/src/tests/PremiumWholesales/`
**Target:** `SalesPlatform_Modern/` (Next.js + Spring Boot)

---

## Executive Summary

| Metric | Count |
|--------|-------|
| Total test files | 11 |
| Total test cases (defined) | **131** |
| Tests that would pass as-is | **0** |
| Tests fully commented out / dead | **47** |
| Tests skipped/fixme by the QA suite itself | **10** |
| Active tests (would execute but fail) | **74** |
| Reusable test logic (after rewrite) | **~65** |

**Zero tests can run against the modern app without modification.** Every test targets the Mendix QA environment with Mendix-specific selectors, credentials, and navigation patterns.

---

## Root Causes (Affecting ALL Tests)

### 1. Target Environment Mismatch
- Tests point to `buy-qa.ecoatmdirect.com` (Mendix), not `localhost:3000` (modern app)
- `user_data.json` `baseURL` is the Mendix QA URL

### 2. Mendix-Specific DOM Selectors
All page objects use selectors that only exist in Mendix's rendered DOM:
- `.mx-name-actionButton20`, `.mx-name-actionButton1`
- `.widget-datagrid-grid-body`, `.widget-datagrid-column-header`
- `//input[@id='usernameInput']`, `//button[@id='loginButton']`
- `.mx-link`, `.mx-icon-lined`, `.mx-icon-remove`
- `//a[@class='mx-link mx-name-actionButton3 circlebase usericon_settings']`

The modern app uses standard React/Next.js components with different class names and data attributes.

### 3. Authentication Flow Difference
- Mendix: `#usernameInput` / `#passwordInput` / `#loginButton`
- Modern: `POST /api/v1/auth/login` with JWT, different login page layout

### 4. User Credentials
- Tests use Mendix QA users (`ecopws1@anything.com`, `nadia.ecoatm@gmail.com`, etc.)
- Modern app has seeded dev users (`admin@test.com`, `salesops@test.com`, etc.)
- Role system and buyer code associations differ

### 5. Navigation Structure
- Mendix: `pws_navMenuAsAdmin.chooseNavMenu("Offer Review")` with Mendix nav widgets
- Modern: Next.js sidebar with `<Link>` components, different route structure

### 6. External Service Dependencies
- Deposco API integration (real API calls in tests)
- Snowflake data warehouse connectivity
- TempMail service for email verification
- These external services exist regardless of app platform but test wiring differs

---

## Per-File Analysis

### File 1: `PWS_FunctionalTests/ATP_Tests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| Verify ATP on Sales Platform Match ATP on Deposco | FAIL | Mendix admin Data Center page selectors; Deposco API page object |
| Verify Deposco API Return Code 200 after Order Created | FAIL | Mendix login/nav; `submitOrderbySKU` uses Mendix shop selectors |
| Verify Deposco Order Status is in NEW | FAIL | Depends on prior test creating order in Mendix |
| Verify ATP on Deposco Decreased after Order Placed | FAIL | Same dependency chain |
| Verify ATP on Sales Platform Sync with Deposco | FAIL | Mendix Data Center page selectors |

**Tests: 5 | Active: 5 | Would pass: 0**
**Reuse potential: LOW** -- Logic requires Deposco API integration not yet built in modern app. ATP sync service exists (`AtpSyncController`) but Data Center UI doesn't exist.

---

### File 2: `PWS_FunctionalTests/InventoryAndCartFunctionalTests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| SPKB-1082,965: Create Offer and Total Calculated | FAIL | Mendix `widget-datagrid` selectors, `enterOfferData()` targets Mendix grid |
| SPKB-648: Download offer file | FAIL | `.mx-name-actionButton20` Mendix selector |
| SPKB-528: SalesRep upload valid offer file | FAIL | `//input[@type='file']` + Mendix-specific upload flow |
| SPKB-648: Download Offer (Cart tests) | FAIL | `.mx-name-actionButton20`, Mendix shop page |
| SPKB-1296: Cannot Submit Offer Above Avail-Qty | FAIL | Mendix gridcell selectors, `isQtyExceedMessageVisible()` |
| SPKB-1297: Submit Offer Below Avail-Qty | FAIL | `submitAndCaptureOrderID()` targets Mendix modal |
| SPKB-1298: Submit Offer Above Avail-Qty > 100 | FAIL | Mendix gridcell "100+" text parsing |
| SPKB-1279: Remove Item by Clicking X Icon | FAIL | Mendix cart page selectors |
| SPKB-1280: Remove Item by Update Qty to Zero | EMPTY | No test body |
| SPKB-1281: Removed Items Not Reappear after Reload | EMPTY | No test body |
| SPKB-1282: Summary Updating when Removing Item | EMPTY | No test body |
| SPKB-1283: Buyer Can Submit Order Successful | EMPTY | No test body |
| SPKB-1315: SalesRep Finalize Order Above List | EMPTY | No test body |
| SPKB-1316: SalesRep Finalize Order Below List | EMPTY | No test body |
| SPKB-1317: SalesRep Finalize Some SKU Below List | EMPTY | No test body |

**Tests: 15 | Active: 8 | Empty stubs: 7 | Would pass: 0**
**Reuse potential: HIGH** -- Modern app has inventory page + cart page. Test *logic* (offer creation, total calculation, qty validation) is directly applicable. Page objects need full rewrite.

---

### File 3: `PWS_FunctionalTests/OfferDetailFunctionalTests.spec.ts`

**ALL TESTS COMMENTED OUT** (header note: "NOT UP TO DATE - NEED TO BE REVISED LATER")

Covers 3 describe blocks:
- Sale Review Detail Page: Finalize Tests (7 tests)
- Sale Review Page: More-Action Options Tests (4 tests)
- Sale Review Page: Sales Action Functionality Tests (10 tests)
- Counter Offer Page: Buyer Action Tests (8 tests)

**Tests: ~29 | Active: 0 (all commented) | Would pass: 0**
**Reuse potential: HIGH** -- Modern app has `offer-review/[offerId]` and `counter-offers/[offerId]` pages. Business logic is directly applicable once page objects are rewritten.

---

### File 4: `PWS_FunctionalTests/PWS_EmailCommunicationTests.spec.ts`

**ALL TESTS COMMENTED OUT** (header note: "NEED TO REVIEW AND FIX -- BUYER CODE NOT AVAILABLE ISSUE")

Covers:
- SPKB-919: Offer-Submitted email
- SPKB-920/1052: Counter Offer email
- SPKB-1053: Action Link on Counter Offer email
- SPKB-921/1054: Order-Submitted email (all SKUs accepted)
- SPKB-922 through SPKB-1056: Various email scenarios (8 empty stubs)

**Tests: 12 | Active: 0 (all commented) | Would pass: 0**
**Reuse potential: LOW** -- Email system not yet implemented in modern app. TempMailPage dependency is external.

---

### File 5: `PWS_WorkflowsTests/ATPInventoryTests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| ATP-001: SKU-level inventory quantity match (UI <> Snowflake) | FAIL | Mendix shop page selectors; Snowflake client dependency |
| ATP-002: Total row count parity | FAIL | Mendix datagrid selectors |
| ATP-003: Case Lot SKU-level match | FAIL | Mendix second datagrid selector; Case Lots tab not in modern app |
| ATP-004: Case Lot row count parity | FAIL | Same as above |
| ATP-005: Data freshness (Snowflake-only) | FAIL | Snowflake env vars / connectivity not configured for modern |
| ATP-006: ATP quantity invariants (Snowflake-only) | FAIL | Same Snowflake dependency |

**Tests: 6 | Active: 6 | Would pass: 0**
**Reuse potential: MEDIUM** -- ATP-005 and ATP-006 are pure Snowflake checks (no UI). Could be extracted as standalone data quality tests. UI tests need full rewrite.

---

### File 6: `PWS_WorkflowsTests/CaseLotTests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| CL-001: Navigate to Case Lots tab | FAIL | `selectInventoryTab()` targets Mendix tabs |
| CL-002: View Case Lot inventory grid | FAIL | `getCaseLotRowData()` targets Mendix grid |
| CL-003: Purchase 1 case (happy path) | SKIPPED | `test.skip(true, ...)` |
| CL-004: Qty exceed warning on Cart | FIXME | `test.fixme(true, ...)` |
| CL-005: Total calculation (Cases x Price) | FAIL | Mendix case lot grid selectors |
| CL-006: Submit Order with Case Lots | SKIPPED | `test.skip(true, ...)` |
| CL-007: Mixed Cart submission | SKIPPED | `test.skip(true, ...)` |
| CL-008: Download Case Lot Excel | FIXME | `test.fixme(true, ...)` |
| CL-009: Search by valid SKU | FAIL | `filterCaseLotBySKU()` Mendix selectors |
| CL-010: Filter by Model Family | FAIL | Mendix datagrid column selectors |
| CL-011: Pagination controls | FAIL | Mendix pagination button selectors |
| CL-012: Non-existent SKU search | FAIL | Same as CL-009 |
| CL-013: Cart empty state | FAIL | Mendix shop/cart selectors |
| CL-014: Non-existent Model Family filter | FAIL | Same as CL-010 |
| CL-015: Clear filter restores inventory | FAIL | Same as CL-009 |
| CL-016: More Actions dropdown options | FAIL | Mendix dropdown selectors |
| CL-017: Cart consistency under add/remove | FAIL | Mendix shop/cart selectors |
| CL-018: Grid responsiveness | FAIL | Mendix datagrid extraction |
| CL-019: Sort + filter coherence | FAIL | Mendix column header sort selectors |

**Tests: 19 | Active: 14 | Skipped: 3 | Fixme: 2 | Would pass: 0**
**Reuse potential: LOW** -- Case Lot tab does not exist in modern inventory page. Feature needs to be built first.

---

### File 7: `PWS_WorkflowsTests/InventoryExcelDownloadTests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| INV-001: Quick smoke - Download Excel, verify first 2 pages | FAIL | `downloadExcel()`, `getAllVisibleTableData()` target Mendix |
| INV-002: Full test - Download Excel, verify ALL pages | FAIL | Same + `getAllTableDataAcrossPages()` Mendix-specific |

**Tests: 2 | Active: 2 | Would pass: 0**
**Reuse potential: MEDIUM** -- Excel download/verification logic is reusable. Modern inventory page exists but Excel export isn't built yet.

---

### File 8: `PWS_WorkflowsTests/OfferFlowTests.spec.ts`

5 describe blocks, each testing a complete offer lifecycle:

**Block 1: Accept Flow (5 tests)**
| Test | Failure Reason |
|------|----------------|
| Buyer Submit Offer | Mendix login + `submitOfferBelowListPrice()` |
| SalesRep Find Offer in Sales Review | Mendix offer queue selectors |
| SalesRep Accept All SKUs | `moreActionOption("Accept All")` Mendix selectors |
| SalesRep Complete Review | Mendix confirmation modal |
| Accepted Offer in Ordered Tab | Mendix offer queue tab selectors |

**Block 2: Decline Flow (5 tests)** -- Same pattern, Decline path
**Block 3: Finalize Flow (5 tests)** -- Same pattern, Finalize path
**Block 4: Counter > Buyer Accept (8 tests)** -- Full counter-offer cycle
**Block 5: Counter > Buyer Decline (8 tests)** -- Counter-offer decline
**Block 6: Counter > Buyer Cancel (8 tests)** -- Counter-offer cancel
**Block 7: Counter > Accept+Decline SKUs (8 tests)** -- Mixed counter response

**Tests: 30+ (across all blocks) | Active: 30+ | Would pass: 0**
**Reuse potential: VERY HIGH** -- This is the core PWS workflow. Modern app has all corresponding pages: `offer-review/`, `counter-offers/`, `orders/`. The test scenarios and business logic are directly applicable. Only page objects need rewriting.

---

### File 9: `PWS_WorkflowsTests/OrderSubmissionTests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| SPKB-1255: Submit Disabled When Cart Empty | FAIL | Mendix cart page selectors |
| SPKB-1255: Submit Enabled When Cart Has Items | FAIL | Mendix shop/cart selectors |
| SPKB-1556: In-Cart Summary | FAIL | `getSummaryOffer()` Mendix selectors |
| SPKB-1224: Buyer Code Display on Cart | FAIL | `getBuyerFromViewAs()` Mendix selector |
| SPKB-1256: Order Confirmation Modal | FAIL | Mendix modal selectors |
| SPKB-700: Cart Cleared after Submission | FAIL | Mendix page state |
| SPKB-699: Order Shows under Ordered Tab | FAIL | Mendix offer queue selectors |
| SPKB-1259: Order-Submitted Email | FAIL | TempMailPage + Mendix dependency chain |
| SPKB-1450: Validate Email Contents | FAIL | Same |
| SPKB-1295: Red Highlight on Exceed Qty | FAIL | Mendix cart styling |
| SPKB-1296: Submission Blocked on Exceed Qty | FAIL | Mendix cart page |
| SPKB-1298: Submit with Exceed Avail Qty 100+ | FAIL | Mendix shop/cart flow |
| SPKB-1451: Adjusted-Qty Email to Buyer | FAIL | TempMailPage dependency |

**Tests: 13 | Active: 13 | Would pass: 0**
**Reuse potential: HIGH** -- Modern app has cart page + order submission. Test scenarios are directly applicable.

---

### File 10: `PWS_WorkflowsTests/RMATests.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| RMA-002: Toggle column visibility | SKIPPED | `test.skip()` |
| RMA-003: Download RMA data | SKIPPED | `test.skip()` |
| RMA-004: RMA Instructions modal | SKIPPED | `test.skip()` |
| RMA-005: RMA Policy from 3-dot menu | FAIL | `.rma-instructiondrawer-closebtn` Mendix class |
| RMA-006: Request RMA with IMEI upload | FAIL | Mendix RMA page selectors, `.pws-rma-returns-datagrid` |
| RMA-007: Dynamic IMEI from shipped order | SKIPPED | `test.skip()` |
| RMA-008: Validation errors for invalid IMEI | FAIL | `.rma-file-upload-error` Mendix class |

**Tests: 7 | Active: 3 | Skipped: 4 | Would pass: 0**
**Reuse potential: HIGH** -- Modern app has `rma-requests/` and `rma-review/` pages. RMA workflow is built. Page objects need rewrite.

---

### File 11: `PWS_WorkflowsTests/SnowflakeConnectivityTest.spec.ts`

| Test | Status | Failure Reason |
|------|--------|----------------|
| SFCONN-001: Verify Snowflake env vars loaded | FAIL* | Only fails if Snowflake env vars not configured |

**Tests: 1 | Active: 1 | Would pass: 0-1 (depends on environment setup)**
**Reuse potential: FULL** -- This is a pure infrastructure check with no UI dependency. Could run as-is if Snowflake credentials are configured.

---

## Feature Coverage Gap Analysis

| Feature Area | Mendix QA Tests | Modern App Status | Gap |
|-------------|-----------------|-------------------|-----|
| **Login/Auth** | Used by all tests | Built (JWT auth) | Page objects need rewrite |
| **Inventory/Shop** | 8+ active tests | Built (`/pws/inventory`) | Selectors differ completely |
| **Case Lots** | 14 active tests | **NOT BUILT** | Feature missing entirely |
| **Cart** | 8+ active tests | Built (`/pws/cart`) | Selectors differ |
| **Offer Submission** | 30+ tests | Built (`/pws/order`) | Selectors differ |
| **Offer Review (Admin)** | 15+ tests | Built (`/pws/offer-review`) | Selectors differ |
| **Counter Offers** | 24+ tests | Built (`/pws/counter-offers`) | Selectors differ |
| **Order History** | Referenced in tests | Built (`/pws/orders`) | Selectors differ |
| **RMA** | 3 active tests | Built (`/pws/rma-requests`) | Selectors differ |
| **Pricing (Admin)** | 0 tests | Built (`/pws/pricing`) | No QA tests exist |
| **ATP/Deposco Sync** | 5 tests | Backend built (`AtpSyncController`) | No Data Center UI |
| **Snowflake ATP Verify** | 6 tests | Not applicable | External infrastructure |
| **Email Notifications** | 0 active (all commented) | Not built | Feature + tests both missing |
| **Excel Download** | 2 tests | Not built in modern | Feature missing |

---

## Recommendations

### Phase 1: Rewrite Page Objects (Immediate Value)
Create new page objects for the modern app matching the same API surface as the Mendix page objects:
- `LoginPage` -> Modern JWT login flow
- `PWS_ShopPage` -> Modern inventory grid
- `PWS_CartPage` -> Modern cart page
- `PWS_OfferDetailsPage` -> Modern offer review detail
- `PWS_CounterOfferPage` -> Modern counter offers
- `PWS_RMAPage` -> Modern RMA requests

### Phase 2: Port High-Value Test Scenarios
Priority order by business value:
1. **OfferFlowTests** (30+ tests) -- Core PWS workflow, all pages built
2. **InventoryAndCartFunctionalTests** (8 tests) -- Buyer-facing fundamentals
3. **OrderSubmissionTests** (13 tests) -- Order creation validation
4. **RMATests** (3 tests) -- RMA workflow

### Phase 3: Build Missing Features Then Port Tests
1. **Case Lots** -- 14 tests waiting, feature not built
2. **Excel Download** -- 2 tests, feature not built
3. **Email Notifications** -- 12 tests (all currently dead), feature not built

### Phase 4: Infrastructure Tests
1. **Snowflake connectivity** -- Can reuse as-is
2. **ATP data quality** -- Pure Snowflake queries, reusable
3. **Deposco integration** -- Needs backend API bridge

---

## Test Case ID Cross-Reference

| SPKB ID | Test Name | File | Status |
|---------|-----------|------|--------|
| SPKB-1082 | Create Offer and Total Calculated | InventoryAndCart | FAIL (selectors) |
| SPKB-965 | OfferId validation | InventoryAndCart | FAIL (selectors) |
| SPKB-648 | Download offer file | InventoryAndCart | FAIL (selectors) |
| SPKB-528 | SalesRep upload valid offer | InventoryAndCart | FAIL (selectors) |
| SPKB-1296 | Cannot Submit Above Avail-Qty | InventoryAndCart | FAIL (selectors) |
| SPKB-1297 | Submit Below Avail-Qty | InventoryAndCart | FAIL (selectors) |
| SPKB-1298 | Submit Above Avail-Qty > 100 | InventoryAndCart | FAIL (selectors) |
| SPKB-1279 | Remove Item by X Icon | InventoryAndCart | FAIL (selectors) |
| SPKB-966 | Finalize Under MoreAction | OfferDetail | COMMENTED OUT |
| SPKB-970 | Error Missing Finalized Price/Qty | OfferDetail | COMMENTED OUT |
| SPKB-971 | Error Accepted + Finalized SKUs | OfferDetail | COMMENTED OUT |
| SPKB-967 | SalesRep Enter Price and Qty | OfferDetail | COMMENTED OUT |
| SPKB-972 | Validate Summary Final Offer | OfferDetail | COMMENTED OUT |
| SPKB-973 | Finalized Offer to Ordered Stage | OfferDetail | COMMENTED OUT |
| SPKB-1277 | Accept-All set Items to Accept | OfferDetail | COMMENTED OUT |
| SPKB-1118 | Decline-All set Items to Decline | OfferDetail | COMMENTED OUT |
| SPKB-968 | Finalize-All set Items to Finalize | OfferDetail | COMMENTED OUT |
| SPKB-1167 | Download from More-Action | OfferDetail | COMMENTED OUT |
| SPKB-788 | Sales Review Default Status | OfferDetail | COMMENTED OUT |
| SPKB-1199 | Accept/Reject/Counter at SKU Level | OfferDetail | COMMENTED OUT |
| SPKB-791 | Accept Not Allows Edit Price/Qty | OfferDetail | COMMENTED OUT |
| SPKB-795 | Complete-Review Block Missing Counter | OfferDetail | COMMENTED OUT |
| SPKB-790 | Counter Allows Edit Price/Qty | OfferDetail | COMMENTED OUT |
| SPKB-794 | Counter Inline Total Calculation | OfferDetail | COMMENTED OUT |
| SPKB-793 | Counter Summary Box Updating | OfferDetail | COMMENTED OUT |
| SPKB-792 | Original Offer Summary Unchanged | OfferDetail | COMMENTED OUT |
| SPKB-843 | Buyer Open Counter Offer Detail | OfferDetail | COMMENTED OUT |
| SPKB-844 | Buyer Cannot Edit Accepted SKU | OfferDetail | COMMENTED OUT |
| SPKB-845 | Buyer Select Accept/Reject Countered | OfferDetail | COMMENTED OUT |
| SPKB-850 | Cannot Submit Blank Counter Action | OfferDetail | COMMENTED OUT |
| SPKB-846 | Original Offer Summary Box | OfferDetail | COMMENTED OUT |
| SPKB-847 | Counter Offer Summary Box | OfferDetail | COMMENTED OUT |
| SPKB-848 | Final Offer Summary Box | OfferDetail | COMMENTED OUT |
| SPKB-849 | Cancel Order Functionality | OfferDetail | COMMENTED OUT |
| SPKB-919 | Offer-Submitted Email | EmailComm | COMMENTED OUT |
| SPKB-920 | Counter Offer Email | EmailComm | COMMENTED OUT |
| SPKB-1052 | Counter Offer Email (dup) | EmailComm | COMMENTED OUT |
| SPKB-1053 | Action Link on Counter Email | EmailComm | COMMENTED OUT |
| SPKB-921 | Order-Submitted Email (all accepted) | EmailComm | COMMENTED OUT |
| SPKB-1054 | Order-Submitted Email (dup) | EmailComm | COMMENTED OUT |
| SPKB-1255 | Submit Disabled When Cart Empty | OrderSubmission | FAIL (selectors) |
| SPKB-1556 | In-Cart Summary | OrderSubmission | FAIL (selectors) |
| SPKB-1224 | Buyer Code Display on Cart | OrderSubmission | FAIL (selectors) |
| SPKB-1256 | Order Confirmation Modal | OrderSubmission | FAIL (selectors) |
| SPKB-700 | Cart Cleared after Submission | OrderSubmission | FAIL (selectors) |
| SPKB-699 | Order Shows under Ordered Tab | OrderSubmission | FAIL (selectors) |
| SPKB-1259 | Order-Submitted Email Sent | OrderSubmission | FAIL (email dep) |
| SPKB-1450 | Validate Email SKUs/Qty/Total | OrderSubmission | FAIL (email dep) |
| SPKB-1295 | Red Highlight Exceed Qty | OrderSubmission | FAIL (selectors) |
| SPKB-1451 | Adjusted-Qty Email | OrderSubmission | FAIL (email dep) |
| SPKB-1991 | ATP Inventory Data Verification | ATPInventory | FAIL (Snowflake) |
