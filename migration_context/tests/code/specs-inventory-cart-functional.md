# Workflow Spec: inventory-cart-functional.md

- **Path**: `specs\inventory-cart-functional.md`
- **Category**: Workflow Spec
- **Lines**: 115
- **Size**: 3,848 bytes

## Source Code

```markdown
# Workflow Spec: Inventory & Cart Functional Tests

## 1. Business Context

| Field | Value |
|-------|-------|
| **User Goal** | Create offers on inventory, download/upload offer files, verify cart functionality |
| **Actor Role** | `Buyer` / `Admin (SalesRep)` |
| **Platform Area** | `PWS` (Premium Wholesale) |
| **Jira Reference** | SPKB-528, SPKB-648, SPKB-965, SPKB-1082, SPKB-1279, SPKB-1280, SPKB-1281, SPKB-1282, SPKB-1283, SPKB-1296, SPKB-1297, SPKB-1298, SPKB-1315, SPKB-1316, SPKB-1317 |
| **Test Priority** | `Smoke` / `Regression` |

---

## 2. Test Data Requirements

### User Credentials

| Role | User Key | Buyer Code | Purpose |
|------|----------|------------|---------|
| PWS Buyer | `PWS_UserOne` | `21839` | Create & download offers |
| PWS Buyer | `PWS_UserTwo` | `25199` | Cart removal tests |
| Admin | `ADMIN` | `AA23WHL` | Upload offer files |

---

## 3. Page Objects Required

| Page Object | Role | Primary Actions |
|-------------|------|-----------------|
| `LoginPage` | Both | `loginAs()`, `logout()` |
| `PWS_ShopPage` | Buyer | `enterOfferData()`, `getRowData()`, `selectMoreActionOption()`, `sortAvlQty()` |
| `PWS_CartPage` | Buyer | `getSummaryOffer()`, `clickSubmitButton()` |
| `NavMenuPage` | Admin | `chooseMainNav(navTabs.BidAsBider)`, `BidAsBidderPage_chooseBuyerCodeDropDownAsAdmin()` |

---

## 4. UI Step-by-Step Flow

### Inventory Page Tests

| Test ID | Scenario | Steps |
|---------|----------|-------|
| SPKB-1082 | Create offer, verify total calculation | Enter price × qty for 3 SKUs, verify Total = Price × Qty |
| SPKB-965 | Validate Offer ID format | Submit offer, verify format: `{BuyerCode}{YY}{SeqXXX}` |
| SPKB-648 | Download offer file | Enter offers, click "Download Offer", verify Excel saved |
| SPKB-528 | Upload offer file (Admin) | Login as Admin, select buyer, upload Excel, verify data |

### Cart Page Tests

| Test ID | Scenario | Expected Result |
|---------|----------|-----------------|
| SPKB-1279 | Remove item via X icon | SKU removed from cart |
| SPKB-1280 | Remove item via qty=0 | SKU removed from cart |
| SPKB-1281 | Removed items don't reappear on reload | Cart persists without item |
| SPKB-1282 | Summary updates on removal | SKU count, qty, total decrease |
| SPKB-1296 | Can't submit over avail qty | Submit blocked |
| SPKB-1297 | Can submit under avail qty | Submit succeeds |
| SPKB-1298 | Submit over avail qty > 100 | "Almost Done" modal |

---

## 5. Critical Checks ⚠️

1. **Total calculation**: `Total = Price × Qty` for each row
2. **Offer ID validation**: Format `{BuyerCode}{YY}{SeqNumber}`
3. **File upload sync**: Downloaded offer data matches uploaded data
4. **Cart state persistence**: Removed items stay removed after reload

---

## 6. File Downloads/Uploads

| Operation | Actor | Method |
|-----------|-------|--------|
| Download Offer | Buyer | `selectMoreActionOption('Download Offer')` + `page.waitForEvent('download')` |
| Upload Offer | Admin | `uploadFile(page, inputXpath, fileName)` |

---

## 7. Data Flow Between Tests

```
Test 1 (Download):
├── Enter 3 SKUs with offer data
├── Store expectedRowsData[]
├── Download Excel file
└── Save downloadedFileName

Test 2 (Upload):
├── Skip if downloadedFileName empty
├── Login as Admin
├── Upload the downloaded file
└── Verify rows match expectedRowsData[]
```

---

## 8. Test Configuration

```typescript
test.describe('Inventory Page Functional Tests', () => {
    test.describe.configure({ mode: 'serial' });
    let downloadedFileName: string;
    let expectedRowsData: Array<{ price: number, qty: number, total: number }>;
    // Shared state between download and upload tests
});
```

---

## 9. Spec File Location

**Path**: `src/tests/PremiumWholesales/PWS_FunctionalTests/InventoryAndCartFunctionalTests.spec.ts`

```
