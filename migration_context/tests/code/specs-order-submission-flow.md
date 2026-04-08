# Workflow Spec: order-submission-flow.md

- **Path**: `specs\order-submission-flow.md`
- **Category**: Workflow Spec
- **Lines**: 109
- **Size**: 3,938 bytes

## Source Code

```markdown
# Workflow Spec: Order Submission Flow

## 1. Business Context

| Field | Value |
|-------|-------|
| **User Goal** | Submit a PWS order, verify confirmation, and validate email notification |
| **Actor Role** | `Buyer` → `Admin` |
| **Platform Area** | `PWS` (Premium Wholesale) |
| **Jira Reference** | SPKB-645, SPKB-699, SPKB-700, SPKB-1255, SPKB-1256, SPKB-1259, SPKB-1295, SPKB-1296, SPKB-1298, SPKB-1450, SPKB-1451, SPKB-1556 |
| **Test Priority** | `Smoke` / `Regression` |

---

## 2. Test Data Requirements

### User Credentials

| Role | User Key | Buyer Code | Email Domain |
|------|----------|------------|--------------|
| PWS Buyer | `PWS_UserSix` | `APWS06` | `ecopws6@fexpost.com` |
| Admin | `ADMIN_Six` | `AAD6` | - |

### Email Verification

| Provider | Account | Purpose |
|----------|---------|---------|
| TempMail | `ecopws6` | Verify order confirmation email |

---

## 3. Page Objects Required

| Page Object | Role | Primary Actions |
|-------------|------|-----------------|
| `LoginPage` | Both | `loginAs()`, `logoutFromPWS()` |
| `PWS_ShopPage` | Buyer | `enterOfferData()`, `sortAvlQty()`, `selectMoreActionOption("Reset Offer")` |
| `PWS_CartPage` | Buyer | `clickSubmitButton()`, `getSummaryOffer()`, `isSubmitButtonEnabled()` |
| `PWS_NavMenu_AsAdmin` | Admin | `chooseNavMenu("Offer Review")` |
| `PWS_OfferQueuePages` | Admin | `chooseOfferStatusTab("Ordered")`, `isOrderIdExistUnderOrderedTab()` |
| `TempMailPage` | - | `getEmailSubjectForCurrentUser()`, `getTotalAmountFromEmail()` |

---

## 4. UI Step-by-Step Flow

### Happy Path: Standard Order Submission

| Step | Action | Expected Result | Jira |
|------|--------|-----------------|------|
| 1 | Reset offer to clear cart | Cart empty | - |
| 2 | Verify submit button disabled (empty cart) | Button disabled | SPKB-1255 |
| 3 | Sort by Avl Qty descending | In-stock items first | - |
| 4 | Enter offer (1 SKU, $1500, Qty 1) | Offer entered | - |
| 5 | Click cart button | Cart page loads | - |
| 6 | Verify submit button enabled | Button enabled | SPKB-1255 |
| 7 | Verify summary: SKUs=1, Qty=1, Total=1500 | Summary correct | SPKB-1556 |
| 8 | Click Submit | Confirmation modal appears | SPKB-1256 |
| 9 | Verify cart cleared after submission | Submit button disabled | SPKB-700 |
| 10 | Admin: Check Ordered tab | Order visible | SPKB-699 |
| 11 | Verify email sent | Subject contains "order is being processed" | SPKB-1259 |
| 12 | Verify email total | Total = $1500 | SPKB-1450 |

### Edge Case: Exceed Available Quantity

| Step | Action | Expected Result | Jira |
|------|--------|-----------------|------|
| 1 | Sort by Avl Qty ascending | Low-stock items first | - |
| 2 | Enter qty exceeding available | Red highlight appears | SPKB-1295 |
| 3 | Verify submit blocked | Button disabled | SPKB-1296 |
| 4 | Enter qty 100+ exceeding available | "Almost Done" modal | SPKB-1298 |
| 5 | Submit with adjusted qty | Order accepted | - |
| 6 | Verify adjusted qty email | Email mentions adjustment | SPKB-1451 |

---

## 5. Critical Checks ⚠️

1. **Submit button state**: Disabled when cart empty, enabled when has items
2. **Summary accuracy**: SKU count, quantity, and total calculated correctly
3. **Exceed qty handling**: Red highlight, submit blocked for < 100, modal for 100+
4. **Email verification**: Correct subject and amount in confirmation email

---

## 6. Test Configuration

```typescript
test.describe('PWS Order Submission Tests', () => {
    test.describe.configure({ mode: 'serial' });
    // Tests share state - order submission depends on cart state
});
```

---

## 7. Integration Points

| Integration | Type | Verification |
|-------------|------|--------------|
| TempMail | Email | `tempMailPage.getEmailSubjectForCurrentUser()` |
| Offer Queue | DB/UI | Admin checks "Ordered" tab |

---

## 8. Spec File Location

**Path**: `src/tests/PremiumWholesales/PWS_WorkflowsTests/OrderSubmissionTests.spec.ts`

```
