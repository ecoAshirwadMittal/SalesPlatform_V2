# Workflow Spec: case-lot-workflow.md

- **Path**: `specs\case-lot-workflow.md`
- **Category**: Workflow Spec
- **Lines**: 147
- **Size**: 5,830 bytes

## Source Code

```markdown
# Workflow Spec: Case Lot Purchasing

## 1. Business Context

| Field | Value |
|-------|-------|
| **User Goal** | Purchase devices by case lot (entire cases, not individual units) |
| **Actor Role** | `Buyer` |
| **Platform Area** | `PWS` (Premium Wholesale) |
| **Jira Reference** | _TBD - Please provide bug ticket for missing validation_ |
| **Test Priority** | `Regression` |

---

## 2. Functional Overview

### What is Case Lot Purchasing?
- Buyers view case inventory under the **"Functional Case Lots"** tab
- Each line item represents a **case size** (case pack quantity)
- Buyers purchase **entire cases**, not individual units
- Example: entering "1" in qty = 1 case (e.g., 21 devices per case)

### Key Terminology
| Current Term | Suggested Term | Description |
|--------------|----------------|-------------|
| Case Pack Quantity | **Units per Case** | Number of devices in a single case |

---

## 3. Test Data Requirements

### User Credentials
| Role | User Key | Buyer Code |
|------|----------|------------|
| PWS Buyer | `PWS_UserOne` | `21839` |
| PWS Buyer | `PWS_UserTwo` | `25199` |

---

## 4. Page Objects Required

| Page Object | Location | Primary Actions |
|-------------|----------|-----------------|
| `LoginPage` | `CommonPages/LoginPage` | `loginAs()` |
| `PWS_ShopPage` | `PWS/BuyerPages/PWS_ShopPage` | `selectInventoryTab()`, `enterCaseOfferData()` |
| `PWS_CartPage` | `PWS/BuyerPages/PWS_CartPage` | `clickSubmitButton()` |

---

## 5. UI Step-by-Step Flow

### Pre-conditions
- [x] User is logged in as PWS Buyer
- [x] Navigation to Shop page is complete
- [x] "Functional Case Lots" tab is selected

### Test Scenarios

#### Core Tests (CL-001 – CL-008)

| Test ID | Scenario | Expected Result |
|---------|----------|-----------------|
| CL-001 | Navigate to Case Lots tab | Tab selected, "Case lots are sold AS IS" disclaimer visible |
| CL-002 | View Case Lot inventory grid | Grid displays: SKU, Case Pack Qty, Avl Cases, Price/Case |
| CL-003 | Purchase 1 case (happy path) | Order submitted successfully, qty = units per case |
| CL-004 | Enter qty > available cases | 🐛 **BUG**: System should display error but currently allows |
| CL-005 | Verify total calculation | Total = (Cases Requested) × (Price per Case) |
| CL-006 | Submit Order with Case Lots only | Cart → Submit → Order ID captured and verified on Orders page |
| CL-007 | Mixed Cart (Standard + Case Lot) | Both item types in Cart → Submit → Order verified |
| CL-008 | Download Excel and verify data | Excel matches UI grid for all SKUs, pricing, and availability |

#### Positive Tests (CL-009 – CL-011)

| Test ID | Scenario | Expected Result |
|---------|----------|-----------------|
| CL-009 | Search for product by valid SKU | Grid filters to show only matching SKU(s), result count updates |
| CL-010 | Filter by Model Family and verify grid updates | Grid displays only products matching the selected model family |
| CL-011 | Pagination controls navigate pages | Next/Previous/First/Last buttons work; row data changes correctly |

#### Negative Tests (CL-012 – CL-016)

| Test ID | Scenario | Expected Result |
|---------|----------|-----------------|
| CL-012 | Search with non-existent SKU | Grid shows zero results with no errors |
| CL-013 | Submit cart with no quantity entered | Cart is empty (0 SKUs, 0 Qty, $0); no order created |
| CL-014 | Apply Model Family filter with no matching products | Grid shows zero rows; UI handles gracefully (no crash) |
| CL-015 | Clear search box and verify grid restores | Grid returns to full inventory after clearing SKU filter |
| CL-016 | Verify "More Actions" dropdown exposes all options | Dropdown shows: Reset Offer, Download Offer, Download Current View, Download All Items |

#### Creative / Stability Tests (CL-017 – CL-019)

| Test ID | Scenario | Expected Result |
|---------|----------|-----------------|
| CL-017 | Add and remove items repeatedly → cart consistency | Cart summary stays accurate (SKUs, Qty, Total) after multiple add/reset cycles |
| CL-018 | Verify grid responsiveness with all visible data | Grid renders all rows without timeouts; data is extractable |
| CL-019 | Sort while filter is applied → coherent results | Grid maintains filter criteria after sort; data order changes correctly |

### Critical Checks ⚠️
1. **Case qty validation**: Cannot order more cases than available (MISSING - BUG)
2. **Total calculation**: Total = Cases × Price per Case
3. **Disclaimer visibility**: "Case lots are sold AS IS" must be visible
4. **Search/Filter**: Grid correctly responds to SKU search and Model Family filters
5. **Cart consistency**: Summary (SKUs, Qty, Total) accurately reflects entered offers

---

## 6. Known Issues / Bugs

> [!CAUTION]
> **Missing Validation (Bug Logged)**  
> If a buyer tries to purchase more cases than available, the system should display an error.  
> Currently, this validation is missing and needs to be verified when fixed.

---

## 7. Test Annotations

```typescript
test.describe('Case Lot Inventory Tests @pws-regression', () => {
    test.describe.configure({ mode: 'serial' });
    // Tests for case lot purchasing flow
});
```

| Annotation | Value |
|------------|-------|
| `@pws-regression` | Yes |
| `@serial` | Yes (state-dependent) |

---

## 8. Spec File Location

**Target Path**: `src/tests/PremiumWholesales/PWS_WorkflowsTests/CaseLotTests.spec.ts`

### Pattern to Follow
Reference: `PWS_FunctionalTests/InventoryAndCartFunctionalTests.spec.ts`

---

## 9. Change Log

| Date | Author | Change Description |
|------|--------|-------------------|
| 2026-02-06 | Automation | Initial spec created |
| 2026-02-08 | Automation | Added CL-006 through CL-019: order submission, Excel verification, search/filter, pagination, negative, and creative tests |

```
