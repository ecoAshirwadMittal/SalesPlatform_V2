# PWS Order History Migration Plan

> Migrating Mendix `PWS_OrderHistory` + `PWS_OrderDetails` pages to Next.js + Spring Boot

## 1. Database Layer

### Decision: PostgreSQL VIEW via Flyway Migration

The Mendix `OfferAndOrdersView` is a computed virtual entity joining offer, order, buyer, and buyer_code data. A SQL VIEW is the correct approach because:

1. The join is complex (5 tables, aggregates for SKU count / total qty / total price) and must be paginated server-side — a VIEW lets PostgreSQL plan the query optimally.
2. Tab counts require 4 separate `COUNT(*)` queries against the same row set — a VIEW avoids repeating the join logic.
3. The Excel export queries the full filtered set without pagination — reusing the same VIEW keeps it DRY.

**Alternatives considered:**

| Option | Pros | Cons | Decision |
|--------|------|------|----------|
| DB VIEW | Optimal query plan, single source of truth, reusable | Adds a Flyway migration | **Selected** |
| JPQL query | No migration needed | Complex multi-table join, hard to paginate, repeated for each tab | Rejected |
| JPA Specification | Consistent with Pricing page | Designed for single-entity filtering, not multi-table joins with aggregates | Rejected |

### Flyway Migration: `V37__order_history_view.sql`

```sql
CREATE OR REPLACE VIEW pws.offer_and_orders_view AS
SELECT
    COALESCE(o.id, -ofe.id)                          AS id,
    COALESCE(o.order_number, ofe.offer_number)        AS order_number,
    ofe.submission_date                                AS offer_date,
    o.order_date                                       AS order_date,
    COALESCE(o.order_status, ofe.status)               AS order_status,
    o.ship_date                                        AS ship_date,
    o.ship_method                                      AS ship_method,
    (SELECT COUNT(DISTINCT oi.sku)
       FROM pws.offer_item oi
      WHERE oi.offer_id = ofe.id)::int                AS sku_count,
    COALESCE(ofe.total_qty, 0)                         AS total_quantity,
    COALESCE(ofe.total_price, 0)                       AS total_price,
    (SELECT b.company_name
       FROM buyer_mgmt.buyer_code_buyers bcb
       JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
      WHERE bcb.buyer_code_id = ofe.buyer_code_id
      LIMIT 1)                                         AS buyer,
    (SELECT b.company_name
       FROM buyer_mgmt.buyer_code_buyers bcb
       JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
      WHERE bcb.buyer_code_id = ofe.buyer_code_id
      LIMIT 1)                                         AS company,
    GREATEST(ofe.updated_date, o.updated_date)         AS last_update_date,
    CASE WHEN o.id IS NOT NULL THEN 'Order' ELSE 'Offer' END AS offer_order_type,
    ofe.buyer_code_id                                  AS buyer_code_id,
    ofe.id                                             AS offer_id
FROM pws.offer ofe
LEFT JOIN pws."order" o ON o.offer_id = ofe.id;
```

**Notes:**
- `buyer_code_id` enables row-level security filtering in the service layer.
- `COALESCE(o.id, -ofe.id)` provides a unique ID for JPA's `@Id` requirement (orders positive, offer-only rows negative).
- LEFT JOIN ensures offers without orders still appear.
- No WHERE clause — tab filtering is done in the service layer via JPA Specifications, keeping the VIEW generic and reusable.
- `buyer` and `company` both resolve to `company_name` (buyers table has no first_name/last_name — those exist on sales_representatives only).

---

## 2. Backend API Design

### 2a. JPA Entity (Read-Only)

**File:** `backend/src/main/java/com/ecoatm/salesplatform/model/pws/OrderHistoryView.java`

```java
@Entity
@Immutable
@Table(name = "offer_and_orders_view", schema = "pws")
public class OrderHistoryView {
    @Id
    private Long id;
    private String orderNumber;
    private LocalDateTime offerDate;
    private LocalDateTime orderDate;
    private String orderStatus;
    private LocalDateTime shipDate;
    private String shipMethod;
    private Integer skuCount;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
    private String buyer;
    private String company;
    private LocalDateTime lastUpdateDate;
    private String offerOrderType;
    private Long buyerCodeId;
    private Long offerId;
    // getters only, no setters (immutable)
}
```

### 2b. Repository

**File:** `backend/src/main/java/com/ecoatm/salesplatform/repository/pws/OrderHistoryViewRepository.java`

```java
public interface OrderHistoryViewRepository
        extends JpaRepository<OrderHistoryView, Long>,
                JpaSpecificationExecutor<OrderHistoryView> {
}
```

`JpaSpecificationExecutor` enables dynamic composition of buyer-code scoping + tab filtering via `Specification`. No custom query methods needed — all counting and filtering is done through composed Specifications in the service layer, consistent with the Pricing page pattern.

### 2c. DTOs

**`OrderHistoryResponse.java`** (record)

```java
public record OrderHistoryResponse(
    Long id, String orderNumber, LocalDateTime offerDate, LocalDateTime orderDate,
    String orderStatus, LocalDateTime shipDate, String shipMethod,
    Integer skuCount, Integer totalQuantity, BigDecimal totalPrice,
    String buyer, String company, LocalDateTime lastUpdateDate,
    String offerOrderType, Long offerId
) {
    public static OrderHistoryResponse from(OrderHistoryView v) { ... }
}
```

**`OrderHistoryTabCounts.java`** (record)

```java
public record OrderHistoryTabCounts(long recent, long inProcess, long complete, long all) {}
```

**`OrderDetailBySkuResponse.java`** (record)

```java
public record OrderDetailBySkuResponse(
    String sku, String description, Integer orderedQty,
    Integer shippedQty, BigDecimal unitPrice, BigDecimal totalPrice
) {}
```

**`OrderDetailByDeviceResponse.java`** (record)

```java
public record OrderDetailByDeviceResponse(
    String imei, String sku, String description, BigDecimal unitPrice,
    String serialNumber, String boxNumber, String trackingNumber, String trackingUrl
) {}
```

### 2d. Service Layer

**File:** `backend/src/main/java/com/ecoatm/salesplatform/service/OrderHistoryService.java`

```java
@Service
public class OrderHistoryService {

    private static final List<String> IN_PROCESS_STATUSES =
        List.of("In_Process", "Pending_Order", "Awaiting_Carrier_Pickup");

    // --- Tab counts (scoped by buyer codes the user can see) ---
    public OrderHistoryTabCounts getTabCounts(Long userId) { ... }

    // --- Paginated list for a given tab ---
    public Page<OrderHistoryResponse> listOrders(String tab, Long userId, Pageable pageable) { ... }

    // --- Order details by SKU ---
    public List<OrderDetailBySkuResponse> getDetailsBySku(Long offerId) { ... }

    // --- Order details by Device ---
    public List<OrderDetailByDeviceResponse> getDetailsByDevice(Long orderId) { ... }

    // --- Export Excel (returns byte[] of .xlsx) ---
    public byte[] exportExcel(String tab, Long userId) { ... }
}
```

**Buyer-code scoping logic** (critical for security):

```java
private Specification<OrderHistoryView> scopeByUser(Long userId) {
    List<BuyerCodeResponse> codes = buyerCodeService.getBuyerCodesForUser(userId);
    List<Long> codeIds = codes.stream().map(BuyerCodeResponse::getId).toList();
    return (root, query, cb) -> root.get("buyerCodeId").in(codeIds);
}
```

- **Admin/SalesOps/SalesRep**: sees all buyer codes
- **Bidder**: sees only their linked buyer codes

Tab filtering composed: `Specification.where(scopeByUser(userId)).and(tabFilter(tab))`

### 2e. Controller

**File:** `backend/src/main/java/com/ecoatm/salesplatform/controller/OrderHistoryController.java`

### 2f. Endpoint Summary

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/v1/pws/orders?tab=&page=&size=&userId=` | Paginated order history list |
| GET | `/api/v1/pws/orders/counts?userId=` | Tab counts (recent, inProcess, complete, all) |
| GET | `/api/v1/pws/orders/{offerId}/details/by-sku` | Order detail items by SKU |
| GET | `/api/v1/pws/orders/{orderId}/details/by-device` | Order detail items by device |
| GET | `/api/v1/pws/orders/export?tab=&userId=` | Excel download for current tab |

### Tab Filter Logic

| Tab | Filter |
|-----|--------|
| Recent | `lastUpdateDate >= (now - 7 days)` |
| In Process | `orderStatus IN ('In_Process', 'Pending_Order', 'Awaiting_Carrier_Pickup')` |
| Complete | `orderStatus NOT IN ('In_Process', 'Pending_Order', 'Awaiting_Carrier_Pickup')` |
| All | no filter |

### Excel Export Columns

OrderNumber, OfferDate, OrderDate, OrderStatus, SKUcount, TotalQuantity, TotalPrice, Buyer, Company, LastUpdateDate, OfferOrderType, ShipDate, ShipMethod

**Filename:** `PWS OrderHistory_{tab}_{yyyyMMdd}.xlsx`

---

## 3. Frontend Architecture

### 3a. Route and File Structure

```
frontend/src/app/pws/orders/
  page.tsx                         # Order History list with tabs
  orders.module.css                # CSS module (Mendix pws-orderhistory-datagrid styling)

frontend/src/app/pws/orders/[id]/
  page.tsx                         # Order Details page
  orderDetails.module.css          # CSS module for detail view
```

**Decision: Order Details as a separate page, not a modal.**
- Mendix navigates to a separate page with back-navigation
- Detail page has two sub-views (By SKU, By Device) with their own grids and export
- URL-addressable (`/pws/orders/123`) enables bookmarking

### 3b. Orders List Page Structure

```
OrdersPage (client component)
  ├── Tab bar: Recent (N) | In Process (N) | Complete (N) | All (N)
  ├── Export button
  ├── DataGrid table (same columns for all tabs)
  │     ├── OrderNumber, OfferDate, OrderDate, Buyer, Company
  │     ├── OrderStatus (badge), ShipDate, ShipMethod
  │     ├── SKUs (center), Qty (center), TotalPrice (right, $formatted)
  │     └── Row click → router.push(`/pws/orders/${row.offerId}`)
  ├── Pagination (button-style, page size 20)
  └── Empty state: "There are currently no orders in this stage."
```

**State:**
- `activeTab`: `'recent' | 'inProcess' | 'complete' | 'all'` (default: `'all'`)
- `page`: number (zero-based)
- `tabCounts`: `{ recent, inProcess, complete, all }`
- `data`: `OrderHistoryResponse[]`
- `totalElements`, `totalPages`

**Data fetching:** `useCallback` + `useEffect` on tab/page change, `apiFetch` from `@/lib/apiFetch`.

### 3c. Order Details Page Structure

```
OrderDetailPage (client component)
  ├── Back button → router.push('/pws/orders')
  ├── Order header (order number, status, dates)
  ├── View toggle: "By SKU" | "By Device"
  ├── DataGrid (By SKU): SKU, Description, Ordered Qty, Shipped Qty, Unit Price, Total Price
  ├── DataGrid (By Device): IMEI, SKU, Description, Unit Price, Serial Number, Box Number, Tracking Number (link)
  ├── Export button
  └── Conditional strikethrough on Declined/Cancelled
```

### 3d. Datagrid Columns (Order History List)

| Column | Alignment | Format |
|--------|-----------|--------|
| Order Number | left | text |
| Offer Date | left | date |
| Order Date | left | date |
| Buyer | left | text |
| Company | left | text |
| Order Status | left | status badge |
| Ship Date | left | date |
| Ship Method | left | text |
| SKUs | center | number |
| Qty | center | number |
| Total Price | right | `$X.XX` |

---

## 4. Implementation Phases

### Phase 1: Database View + Backend Core

**Scope:**
1. `V37__order_history_view.sql` Flyway migration
2. `OrderHistoryView` entity (immutable)
3. `OrderHistoryViewRepository` with `JpaSpecificationExecutor`
4. DTOs: `OrderHistoryResponse`, `OrderHistoryTabCounts`
5. `OrderHistoryService` with `listOrders()` and `getTabCounts()`
6. `OrderHistoryController` with `GET /orders` and `GET /orders/counts`
7. Unit tests for `OrderHistoryService`
8. Integration test for the repository

**Files created:**
- `backend/src/main/resources/db/migration/V37__order_history_view.sql`
- `backend/src/main/java/.../model/pws/OrderHistoryView.java`
- `backend/src/main/java/.../repository/pws/OrderHistoryViewRepository.java`
- `backend/src/main/java/.../dto/OrderHistoryResponse.java`
- `backend/src/main/java/.../dto/OrderHistoryTabCounts.java`
- `backend/src/main/java/.../service/OrderHistoryService.java`
- `backend/src/main/java/.../controller/OrderHistoryController.java`
- `backend/src/test/java/.../service/OrderHistoryServiceTest.java`
- `backend/src/test/java/.../controller/OrderHistoryControllerTest.java`

**Depends on:** Nothing — uses existing `BuyerCodeService`, `OrderRepository`, `OfferItemRepository`.

### Phase 2: Frontend Orders List Page

**Scope:**
1. `frontend/src/app/pws/orders/page.tsx` with tab bar, datagrid, pagination
2. `frontend/src/app/pws/orders/orders.module.css` matching Mendix styling
3. Wire up to `GET /api/v1/pws/orders` and `GET /api/v1/pws/orders/counts`
4. Tab switching, page reset, empty state
5. Row click navigates to order details
6. QA visual comparison against `https://buy-qa.ecoatmdirect.com`

**Files created:**
- `frontend/src/app/pws/orders/page.tsx`
- `frontend/src/app/pws/orders/orders.module.css`

**Depends on:** Phase 1

### Phase 3: Order Details Page

**Scope:**
1. `OrderDetailBySkuResponse` and `OrderDetailByDeviceResponse` DTOs
2. `getDetailsBySku()` and `getDetailsByDevice()` in `OrderHistoryService`
3. Detail endpoints in `OrderHistoryController`
4. `frontend/src/app/pws/orders/[id]/page.tsx` with By SKU / By Device toggle
5. `frontend/src/app/pws/orders/[id]/orderDetails.module.css`
6. Conditional strikethrough for Declined/Cancelled
7. Tracking number links
8. Unit tests for detail service methods

**Files created:**
- `backend/src/main/java/.../dto/OrderDetailBySkuResponse.java`
- `backend/src/main/java/.../dto/OrderDetailByDeviceResponse.java`
- `frontend/src/app/pws/orders/[id]/page.tsx`
- `frontend/src/app/pws/orders/[id]/orderDetails.module.css`

**Depends on:** Phase 1, Phase 2

### Phase 4: Excel Export

**Scope:**
1. Apache POI dependency (`poi-ooxml`) in `backend/pom.xml`
2. `exportExcel()` in `OrderHistoryService`
3. `GET /api/v1/pws/orders/export` endpoint
4. Frontend Export button wired to trigger download
5. Order Details export (By SKU and By Device)
6. Tests for export

**Files modified:**
- `backend/pom.xml`
- `OrderHistoryService.java`
- `OrderHistoryController.java`
- `frontend/src/app/pws/orders/page.tsx`

**Depends on:** Phase 1

### Phase 5: Sidebar Navigation + Documentation

**Scope:**
1. "Orders" link in PWS sidebar navigation
2. Update `docs/api/rest-endpoints.md`
3. Update `docs/business-logic/` with order history rules
4. Update `docs/testing/coverage.md`

**Depends on:** All prior phases

---

## 5. Risks and Decisions

### RISK 1: Offers WITHOUT Orders

The VIEW uses LEFT JOIN so offers without orders (status='Pending_Order', 'Sales_Review', etc.) still appear. Rows without an order have `null` for order_date, ship_date, ship_method, and order_number falls back to offer_number.

**Decision:** Confirmed by Mendix behavior — the Order History page shows both offers and orders.

### RISK 2: Buyer/Company Resolution

The VIEW's correlated subquery returns one buyer per buyer_code (`LIMIT 1`). If a buyer_code has multiple buyers, only one appears. This matches Mendix behavior.

**Decision:** Accept `LIMIT 1`. Revisit if business needs all buyers shown.

### RISK 3: "Awaiting Carrier Pickup" Inconsistency

Mendix bug: `SUB_CalculateOrderHistoryTabTotals` includes "Awaiting Carrier Pickup" in In Process counts, but `ACT_OrderHistory_ExportExcel` does not include it in the In Process export filter.

**Decision:** Include "Awaiting Carrier Pickup" in both list AND export for In Process tab. Document as deliberate fix.

### RISK 4: OfferItem-to-Order Linkage

`pws.offer_item` has only `offer_id` (FK to offer), no direct FK to order. Linkage is `offer_item → offer → order`.

**Decision:** Existing schema is sufficient. No additional junction table needed.

### RISK 5: By Device Detail Data

The "By Device" grid needs IMEI, serial number, box number from Deposco sync. These may live in `order.json_content` (JSON payload) rather than denormalized columns.

**Decision:** Parse JSON content in Phase 3. If fragile, add a V38 migration to denormalize. Should not block Phases 1-2.

**Action required:** Spike before Phase 3 — query `pws."order".json_content` from the dev DB to document the exact JSON schema (IMEI, serial, box number, tracking fields). Define parsing strategy and fallback behavior for missing fields.

### RISK 6: Apache POI Dependency

POI-OOXML adds ~15MB to the JAR. Standard approach for XLSX in Spring Boot.

**Decision:** Accept. Add `poi-ooxml` 5.3.0 to `backend/pom.xml`. Use `SXSSFWorkbook` (streaming API) instead of `XSSFWorkbook` to avoid memory pressure on large exports (1000+ rows).

### RISK 7: userId vs JWT

Existing controllers pass `userId` as a query parameter (JWT auth not fully wired yet).

**Decision:** Use `@RequestParam(required = false) Long userId` for now, matching `OfferController`. Add TODO for future JWT extraction.

---

## 6. Mendix Source Files Referenced

| File | Content |
|------|---------|
| `migration_context/frontend/components/Pages_Page/PWS_OrderHistory.md` | Page widget tree, datagrid columns, tabs |
| `migration_context/frontend/components/Pages_Page/PWS_OrderHistory_OQL.md` | OQL variant of the page |
| `migration_context/frontend/components/Pages_Page/PWS_OrderDetails.md` | Detail page (By SKU, By Device grids) |
| `migration_context/frontend/ACT_UpdateOrderHistoryTabHelper.md` | Tab switching nanoflow |
| `migration_context/backend/ACT_OrderHistory_ExportExcel.md` | Excel export microflow |
| `migration_context/backend/DS_GetOrCreateOrderHistoryHelper.md` | Helper initialization (default tab, 7-day cutoff) |
| `migration_context/backend/DS_OrderHistoryDetails.md` | Detail retrieval by offer/order number |
| `migration_context/backend/services/SUB_CalculateOrderHistoryTabTotals.md` | Tab count calculation |
| `migration_context/backend/services/Sub_SyncOrderHistory.md` | Deposco order sync |
| `migration_context/backend/domain/.../Unnamed_290d728f.md` (line 569) | OfferAndOrdersView entity definition |
| `migration_context/backend/domain/.../ENUM_OrderHistoryTab.md` | Tab enum: Recent, InProcess, Complete, All |

## 7. Existing Codebase Patterns to Follow

| Pattern Source | What to Reuse |
|----------------|---------------|
| `PricingController.java` | Pagination, query params, `Page<T>` response |
| `PricingService.java` | `Specification` pattern, DTO mapping |
| `BuyerCodeService.java` | Buyer-code scoping, role-based logic |
| `OfferController.java` | `userId` param authorization pattern |
| `frontend/.../pricing/page.tsx` | Data-fetching, pagination, CSS module pattern |
| `@/lib/apiFetch.ts` | Auth-bearing fetch wrapper |
