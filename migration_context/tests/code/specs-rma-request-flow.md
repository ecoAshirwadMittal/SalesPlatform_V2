# Workflow Spec: rma-request-flow.md

- **Path**: `specs\rma-request-flow.md`
- **Category**: Workflow Spec
- **Lines**: 108
- **Size**: 3,470 bytes

## Source Code

```markdown
# Workflow Spec: RMA Request Flow

## 1. Business Context

| Field | Value |
|-------|-------|
| **User Goal** | Submit a Return Merchandise Authorization (RMA) for a purchased device |
| **Actor Role** | `Buyer` (PWS) |
| **Platform Area** | `PWS` (Premium Wholesale) |
| **Jira Reference** | RMA-001 through RMA-006 |
| **Test Priority** | `Regression` |

---

## 2. Test Data Requirements

### User Credentials

| Role | User Key | Buyer Code | Notes |
|------|----------|------------|-------|
| PWS Buyer | `Nadia_GmailOne` | `22379` | Multi-buyer code account, requires buyer selection |

### Dynamic Data

| Data Type | Source | Method |
|-----------|--------|--------|
| IMEI | Shipped Order | `pws_RMAPage.getIMEIFromShippedOrder()` |
| Return Reason | Static | `"Incorrect Device"` (valid dropdown value) |

---

## 3. Page Objects Required

| Page Object | Location | Primary Actions |
|-------------|----------|-----------------|
| `LoginPage` | `CommonPages/LoginPage` | `loginAs()`, `logout()` |
| `PWS_NavMenu_AsBuyer` | `PWS/BuyerPages/PWS_NavMenu_AsBuyer` | `chooseNavMenu("RMAs")` |
| `PWS_RMAPage` | `PWS/BuyerPages/PWS_RMAPage` | `downloadRMATemplate()`, `fillRMATemplate()`, `uploadRMAFile()`, `submitRMA()` |

---

## 4. UI Step-by-Step Flow

### Pre-conditions
- [x] User logged in as `Nadia_GmailOne`
- [x] Buyer code `22379` selected via Welcome Page
- [x] Navigation to RMAs tab complete

### Test Steps

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Navigate to RMAs via `pws_navMenuAsBuyer.chooseNavMenu("RMAs")` | RMAs page loads, datagrid visible |
| 2 | Get initial RMA count | Store count for verification |
| 3 | Click "Request RMA" button | Modal opens |
| 4 | Download RMA template | Excel file downloaded |
| 5 | Fill template with IMEI and return reason | Modified Excel saved |
| 6 | Upload filled template | File accepted |
| 7 | Click Submit RMA | Success message displayed |
| 8 | Verify RMA count increased | New row in RMAs table |
| 9 | Download RMA Details Excel | Excel contains submitted IMEI |

### Critical Checks ⚠️

1. **IMEI in first row**: Submitted IMEI appears in first row of RMA table
2. **Excel validation**: Downloaded RMA Details Excel contains the IMEI
3. **Status correct**: RMA shows correct status (pending/submitted)

---

## 5. Test Variants

| Test ID | Scenario | User | Status |
|---------|----------|------|--------|
| RMA-002 | Column visibility toggle | Nadia_GmailOne | `skip` |
| RMA-003 | Download RMA data from 3-dot menu | Nadia_GmailOne | `skip` |
| RMA-004 | RMA Instructions modal | Nadia_GmailOne | `skip` |
| RMA-005 | RMA Policy drawer | Nadia_GmailOne | Active |
| RMA-006 | Full RMA upload/submit flow | Nadia_GmailOne | Active |

---

## 6. File Downloads/Uploads

| Operation | File Type | Helper Method |
|-----------|-----------|---------------|
| Download Template | Excel (.xlsx) | `pws_RMAPage.downloadRMATemplate()` |
| Fill Template | Excel (.xlsx) | `pws_RMAPage.fillRMATemplate(path, imei, reason)` |
| Upload File | Excel (.xlsx) | `pws_RMAPage.uploadRMAFile(filePath)` |
| Parse Results | Excel (.xlsx) | `readRMAExcel(filePath)` |

---

## 7. Test Configuration

```typescript
test.describe.serial("RMA Tests", () => {
    // Tests run in sequence - shared browser state
    // Login once in beforeAll, navigate to RMAs
});
```

---

## 8. Spec File Location

**Path**: `src/tests/PremiumWholesales/PWS_WorkflowsTests/RMATests.spec.ts`

```
