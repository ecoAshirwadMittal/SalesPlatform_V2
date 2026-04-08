# Workflow Spec: offer-review-flow.md

- **Path**: `specs\offer-review-flow.md`
- **Category**: Workflow Spec
- **Lines**: 112
- **Size**: 4,100 bytes

## Source Code

```markdown
# Workflow Spec: Offer Review Flow

## 1. Business Context

| Field | Value |
|-------|-------|
| **User Goal** | Submit offers as Buyer, review and process as Sales Rep (Accept/Decline/Counter/Finalize) |
| **Actor Role** | `Buyer` → `Admin/Sales Rep` → `Buyer` (multi-role workflow) |
| **Platform Area** | `PWS` (Premium Wholesale) |
| **Jira Reference** | Offer Flow Tests |
| **Test Priority** | `Regression` |

---

## 2. Test Data Requirements

### User Credentials (Paired Buyer + Admin)

| Scenario | Buyer | Buyer Code | Admin |
|----------|-------|------------|-------|
| Accept All | `PWS_UserOne` | `21839` | `ADMIN_One` |
| Decline All | `PWS_UserFour` | `APWS04` | `ADMIN_Four` |
| Finalize All | `PWS_UserTwo` | `25199` | `ADMIN_Two` |
| Counter → Accept | `PWS_UserThree` | `24763` | `ADMIN_Three` |
| Counter → Decline | `PWS_UserSix` | `APWS06` | `ADMIN_Six` |
| Counter → Cancel | `PWS_UserSeven` | `APWS07` | `ADMIN_Seven` |
| Counter → Mixed | `PWS_UserFive` | `21999` | `ADMIN_Five` |

---

## 3. Page Objects Required

| Page Object | Role | Primary Actions |
|-------------|------|-----------------|
| `LoginPage` | Both | `loginAs()`, `logoutFromPWS()` |
| `PWS_CartPage` | Buyer | `submitOfferBelowListPrice(skus, price, qty)` |
| `PWS_NavMenu_AsAdmin` | Admin | `chooseNavMenu("Offer Review")` |
| `PWS_OfferQueuePages` | Admin | `chooseOfferStatusTab()`, `findAndClickOfferByID()`, `isOfferIdExistUnderAnyTab()` |
| `PWS_OfferDetailsPage` | Admin | `moreActionOption()`, `clickCompleteReviewButton()`, `salesActionEachSKU()` |
| `PWS_CounterOfferPage` | Buyer | `selectCounterActionByRowIndex()`, `clickSubmitResponseButton()` |

---

## 4. UI Step-by-Step Flow

### Flow 1: Sales Rep Accepts Offer

| Step | Actor | Action | Expected Result |
|------|-------|--------|-----------------|
| 1 | Buyer | Login, submit offer below list price | Offer ID generated |
| 2 | Buyer | Logout | Session cleared |
| 3 | Admin | Login, navigate to Offer Review | Offer Queue visible |
| 4 | Admin | Select "Sales Review" tab | Offer appears in queue |
| 5 | Admin | Click offer, select "Accept All" | All SKUs marked Accept |
| 6 | Admin | Click "Complete Review" | Confirmation modal |
| 7 | Admin | Navigate to "Ordered" tab | Accepted offer appears |

### Flow 2: Sales Rep Counteroffers → Buyer Accepts

| Step | Actor | Action | Expected Result |
|------|-------|--------|-----------------|
| 1-4 | Buyer/Admin | Same as Accept flow | Offer in Sales Review |
| 5 | Admin | Select "Counter" for each SKU | Counter fields enabled |
| 6 | Admin | Enter counter price/qty | Values saved |
| 7 | Admin | Complete Review | Offer moves to Buyer Acceptance |
| 8 | Buyer | Login, navigate to "Counters" | Counteroffer visible |
| 9 | Buyer | Accept counteroffer | Status updated |
| 10 | Admin | Check "Ordered" tab | Accepted counter appears |

---

## 5. Critical Checks ⚠️

1. **Offer ID tracking**: Same offer ID throughout workflow
2. **Tab transitions**: Offer moves to correct tab after each action
3. **SKU status**: Each SKU shows correct action (Accept/Decline/Counter/Finalize)
4. **Logout between roles**: Clean session switch between Buyer and Admin

---

## 6. Test Variants

| Describe Block | Buyer | Admin | Final Tab |
|---------------|-------|-------|-----------|
| Accept All | PWS_UserOne | ADMIN_One | Ordered |
| Decline All | PWS_UserFour | ADMIN_Four | Declined |
| Finalize All | PWS_UserTwo | ADMIN_Two | Ordered |
| Counter → Accept | PWS_UserThree | ADMIN_Three | Ordered |
| Counter → Decline | PWS_UserSix | ADMIN_Six | Declined |
| Counter → Cancel | PWS_UserSeven | ADMIN_Seven | Declined |
| Counter → Mixed | PWS_UserFive | ADMIN_Five | Ordered |

---

## 7. Test Configuration

```typescript
// Single browser, shared state across role switches
let base: BaseTest;
test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    base = new BaseTest(page);
    await base.setup();
});
```

---

## 8. Spec File Location

**Path**: `src/tests/PremiumWholesales/PWS_WorkflowsTests/OfferFlowTests.spec.ts`

```
