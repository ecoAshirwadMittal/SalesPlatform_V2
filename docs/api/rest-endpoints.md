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

## Auth

### POST /auth/login

Authenticate with email/password. Returns JWT token.

### GET /auth/buyer-codes?userId={id}

List buyer codes for a user.
