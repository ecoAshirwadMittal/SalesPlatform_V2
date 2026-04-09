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
