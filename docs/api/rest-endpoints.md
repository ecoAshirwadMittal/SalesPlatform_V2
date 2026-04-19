# REST API Endpoints

Base URL: `http://localhost:8080/api/v1`

All endpoints require `Authorization: Bearer <JWT>` header unless noted.

---

## Pricing

### GET /pws/pricing/devices

List pricing devices with server-side pagination and optional filters.

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| page | int | 0 | Page number (zero-based) |
| size | int | 20 | Page size |
| sku | string | - | SKU contains filter |
| category | string | - | Exact category name |
| brand | string | - | Exact brand name |
| model | string | - | Exact model name |
| carrier | string | - | Exact carrier name |
| capacity | string | - | Exact capacity name |
| color | string | - | Exact color name |
| grade | string | - | Exact grade name |
| currentListPrice | BigDecimal | - | Exact current list price |
| futureListPrice | BigDecimal | - | Exact future list price |
| currentMinPrice | BigDecimal | - | Exact current min price |
| futureMinPrice | BigDecimal | - | Exact future min price |

**Response**: Spring `Page<PricingDeviceResponse>` JSON with `content`, `totalElements`, `totalPages`, `number`, `size`.

### PUT /pws/pricing/devices/{id}

Update future prices for a single device.

**Request body**:
```json
{
  "futureListPrice": 120.00,
  "futureMinPrice": 95.00
}
```

**Response**: `PricingDeviceResponse` with updated values.

**Error**: 400 if device not found.

### PUT /pws/pricing/devices/bulk

Bulk update future prices for multiple devices.

**Request body**:
```json
[
  { "deviceId": 1, "futureListPrice": 120.00, "futureMinPrice": 95.00 },
  { "deviceId": 2, "futureListPrice": 60.00, "futureMinPrice": 45.00 }
]
```

**Response**: `List<PricingDeviceResponse>` with updated values.

**Error**: 400 if any device not found.

### GET /pws/pricing/devices/{id}/history

Get price history for a device, ordered by date descending. Returns derived previous prices by comparing adjacent rows.

**Response**: `List<PriceHistoryResponse>` with `id`, `listPrice`, `minPrice`, `previousListPrice`, `previousMinPrice`, `expirationDate`, `createdDate`.

### POST /pws/pricing/devices/upload

Upload a CSV file to bulk update future prices. Format: `sku,futureListPrice,futureMinPrice`.

**Request**: `multipart/form-data` with `file` field (`.csv`).

**Response**: `CsvUploadResult` with `totalRows`, `updatedCount`, `errorCount`, `errors[]`.

### GET /pws/pricing/config

Get the future price activation date configuration (singleton).

**Response**:
```json
{ "id": 1, "futurePriceDate": "2026-05-01T00:00" }
```

### PUT /pws/pricing/config

Update the future price activation date.

**Request body**:
```json
{ "futurePriceDate": "2026-06-15T00:00:00" }
```

**Response**: Same as GET.

---

## Order History

### GET /pws/orders

Paginated order history list, scoped by the user's buyer codes.

| Param | Type | Default | Description |
|-------|------|---------|-------------|
| tab | string | `all` | Tab filter: `recent`, `inProcess`, `complete`, `all` |
| userId | long | - | User ID (required) |
| buyerCodeId | long | - | Filter to a specific buyer code (optional; must belong to user) |
| page | int | 0 | Page number (zero-based) |
| size | int | 20 | Page size |

**Response**: Spring `Page<OrderHistoryResponse>` JSON with `content`, `totalElements`, `totalPages`, `number`, `size`.

**Content item shape**:
```json
{
  "id": 123,
  "orderNumber": "ORD-001",
  "offerDate": "2026-03-15T10:00:00",
  "orderDate": "2026-03-16T14:00:00",
  "orderStatus": "In_Process",
  "shipDate": null,
  "shipMethod": null,
  "skuCount": 5,
  "totalQuantity": 50,
  "totalPrice": 1250.00,
  "buyer": "Acme Corp",
  "company": "Acme Corp",
  "lastUpdateDate": "2026-03-17T09:00:00",
  "offerOrderType": "Order",
  "offerId": 42
}
```

**Tab filter logic**:
| Tab | Filter |
|-----|--------|
| recent | `lastUpdateDate >= (now - 7 days)` |
| inProcess | `orderStatus IN ('In_Process', 'Pending_Order', 'Awaiting_Carrier_Pickup')` |
| complete | `orderStatus NOT IN ('In_Process', 'Pending_Order', 'Awaiting_Carrier_Pickup')` |
| all | no filter |

**Error**: 400 if `userId` is null or `tab` is unknown.

### GET /pws/orders/counts

Tab counts for the user's scoped orders.

| Param | Type | Description |
|-------|------|-------------|
| userId | long | User ID (required) |
| buyerCodeId | long | Filter to a specific buyer code (optional; must belong to user) |

**Response**:
```json
{ "recent": 12, "inProcess": 5, "complete": 230, "all": 247 }
```

**Error**: 400 if `userId` is null.

### GET /pws/orders/{offerId}/details/by-sku

Get order detail items aggregated by SKU for a given offer.

**Response**: `List<OrderDetailBySkuResponse>`

```json
[
  {
    "offerItemId": 1,
    "sku": "SKU-001",
    "description": "iPhone 14 Pro 128GB",
    "orderedQty": 5,
    "shippedQty": 3,
    "unitPrice": 25.00,
    "totalPrice": 125.00
  }
]
```

### GET /pws/orders/{offerId}/details/by-device

Get order detail items at the individual device (IMEI) level for a given offer.

**Response**: `List<OrderDetailByDeviceResponse>`

```json
[
  {
    "imeiDetailId": 100,
    "imei": "353456789012345",
    "sku": "SKU-001",
    "description": "iPhone 14 Pro 128GB",
    "unitPrice": 25.00,
    "serialNumber": "SN-001",
    "boxNumber": "BOX-A",
    "trackingNumber": "1Z999AA",
    "trackingUrl": "https://ups.com/track/1Z999AA"
  }
]
```

**Note**: Device-level data (IMEI, serial, box) is populated by the Deposco shipment sync. The By Device grid will show empty until shipment data exists.

---

## Inventory

### GET /inventory/devices

List active devices with optional filters.

| Param | Type | Description |
|-------|------|-------------|
| itemType | string | Filter by item type (e.g., "PWS") |
| excludeGrade | string | Exclude devices with this grade |
| minAtpQty | int | Minimum ATP quantity |

### POST /inventory/devices

Create a single device.

### POST /inventory/devices/bulk

Bulk create or upsert devices.

### GET /inventory/case-lots

List all case lots.

---

## Aggregated Inventory (Admin)

Backs `/admin/auctions-data-center/inventory`. Mirrors Mendix `AuctionUI.PG_AggregatedInventory`.

### GET /admin/inventory/weeks

Return current and past weeks (`week_start_datetime <= now()`), ordered by start datetime descending. Future weeks are excluded because they have no Snowflake data to render. Used to populate the week selector.

**Response**: `WeekOption[]` — `{ id, weekDisplay, weekStartDateTime, weekEndDateTime }`.

### GET /admin/inventory

Paginated rows for the selected week. Requires `Administrator` or `SalesOps`.

| Param | Type | Default | Description |
|---|---|---|---|
| weekId | long | - | FK into `mdm.week` |
| productId | string | - | Exact match on `ecoid2` |
| grades | string | - | Contains match on `merged_grade` |
| brand | string | - | Contains on `brand` |
| model | string | - | Contains on `model` |
| modelName | string | - | Contains on `name` |
| carrier | string | - | Contains on `carrier` |
| page | int | 0 | Page number |
| pageSize | int | 20 | Page size |

**Response**: `AggregatedInventoryPageResponse` — `{ content: AggregatedInventoryRow[], page, pageSize, totalElements, totalPages }`. Each row exposes `datawipe` alongside grades/quantities/prices so the admin edit modal can pre-fill the Data Wipe radio.

### GET /admin/inventory/totals

Per-week KPI totals (sum + weighted average). Rows with `is_deprecated = true` are excluded.

**Response**: `{ totalQuantity, totalPayout, averageTargetPrice, dwTotalQuantity, dwTotalPayout, dwAverageTargetPrice, lastSyncedAt, hasInventory, hasAuction, isCurrentWeek, syncStatus }`.

The four trailing helper flags replace the Mendix `AggInventoryHelper` microflow so the page can drive the Snowflake sync banner and the "Create Auction" button without a separate call:

| Field | Type | Description |
|---|---|---|
| `hasInventory` | boolean | `EXISTS` against `auctions.aggregated_inventory` for the week — true even when all quantities are zero, so the UI distinguishes "no rows yet" from "rows present but empty". |
| `hasAuction` | boolean | `EXISTS` against `auctions.auctions` for the week — used to suppress the "Create Auction" button once an auction already exists. |
| `isCurrentWeek` | boolean | True when the selected week's `week_end_datetime > now()` (Mendix-parity filter). |
| `syncStatus` | string | Latest `integration.snowflake_sync_log.status` for `SNOWFLAKE_AGG_INVENTORY` + this week (`STARTED`, `COMPLETED`, `FAILED`, `SKIPPED_UP_TO_DATE`, `SKIPPED_LOCKED`), or `"NONE"` if no run exists yet. |

When `weekId` is omitted, the helper flags return safe defaults (`hasInventory=false`, `hasAuction=false`, `isCurrentWeek=false`, `syncStatus="NONE"`).

### PUT /admin/inventory/{id}

Admin row edit. Requires `Administrator`. Flips `is_total_quantity_modified = true` on save so subsequent sync runs preserve the override.

**Request body**: `{ mergedGrade, datawipe, totalQuantity, dwTotalQuantity }`.

**Response**: `AggregatedInventoryRow`.

### POST /admin/inventory/weeks/{weekId}/sync

Trigger a Snowflake sync for the given week. Requires `Administrator` or `SalesOps`. Returns `403` for bidder roles.

Publishes `AggInventorySyncRequestedEvent`; the actual sync runs on the `snowflakeExecutor` after the HTTP commit (post-commit event bridge, mirrors the PWS email pattern). The response returns immediately — poll `GET /admin/inventory/weeks/{weekId}/sync/status` for progress.

**Response**: `202 Accepted` with `SyncTriggerResponse`:

```json
{ "status": "ACCEPTED", "source": "SNOWFLAKE_AGG_INVENTORY" }
```

When `snowflake.enabled=false`, no event is published and the body is:

```json
{ "status": "SKIPPED_DISABLED", "source": "SNOWFLAKE_AGG_INVENTORY" }
```

### GET /admin/inventory/weeks/{weekId}/sync/status

Latest sync log entry for the week. Frontend polls this endpoint every 3s while `status ∈ {PENDING, STARTED}` (up to 90s).

**Response**: `200 OK` with `SyncStatusResponse`:

```json
{
  "status": "COMPLETED",
  "startedAt": "2026-04-18T14:00:00",
  "finishedAt": "2026-04-18T14:00:07",
  "rowsUpserted": 1234,
  "errorMessage": null
}
```

When no log row exists for the week (no sync has ever been triggered), returns:

```json
{ "status": "NONE", "startedAt": null, "finishedAt": null, "rowsUpserted": null, "errorMessage": null }
```

`status` values: `NONE`, `STARTED`, `COMPLETED`, `FAILED`, `SKIPPED_UP_TO_DATE`, `SKIPPED_DISABLED`, `SKIPPED_LOCKED`.

### GET /admin/inventory/export

Streams an `.xlsx` of the current filter set (same query params as list). Not paginated.

**Response**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (binary).

---

## Auth

### POST /auth/login

Authenticate with email/password. Returns JWT token.

### GET /auth/buyer-codes?userId={id}

List buyer codes for a user.
