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

## Auctions (Admin)

Backs the "Create Auction" button on `/admin/auctions-data-center/inventory`. Mirrors Mendix `AuctionUI.ACT_Create_Auction`.

### GET /admin/auctions

Paginated, filterable list backing `/admin/auctions-data-center/auctions` (QA parity with `Mx_Admin.Pages.Auctions`). Each row carries the parent week's `weekDisplay` plus a scalar `roundCount` (COUNT(*) from `auctions.scheduling_auctions`) so the grid can render the "Rounds" column without a join fan-out.

**Roles**: `Administrator` or `SalesOps`.

| Param | Type | Default | Description |
|---|---|---|---|
| `title` | string | - | Case-insensitive contains on `auction_title`. |
| `weekId` | long | - | Exact match on `week_id`. |
| `status` | string | - | Exact match on `auction_status` (`Unscheduled` / `Scheduled` / `Started` / `Closed`). |
| `page` | int | 0 | Zero-based page number. |
| `pageSize` | int | 20 | Page size. |

Rows are ordered by `created_date DESC, id DESC` so the most recently created auction lands first.

**Response**: `200 OK` with `AuctionListPageResponse`:

```json
{
  "content": [
    {
      "id": 101,
      "auctionTitle": "Auction 2026 / Wk17",
      "auctionStatus": "Scheduled",
      "weekId": 42,
      "weekDisplay": "2026 / Wk17",
      "createdDate": "2026-04-20T14:05:00Z",
      "changedDate": "2026-04-20T14:05:00Z",
      "createdBy": "ashirwadmittal",
      "updatedBy": "ashirwadmittal",
      "roundCount": 3
    }
  ],
  "page": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### GET /admin/scheduling-auctions

Paginated, filterable list backing `/admin/auctions-data-center/schedule-auction` (QA parity with `Mx_Admin.Pages.Scheduling_Auctions`). Each row is one round of a parent auction — three rounds per scheduled auction — and joins to `auctions.auctions` for the parent title.

**Roles**: `Administrator` or `SalesOps`. An explicit matcher at `/api/v1/admin/scheduling-auctions/**` in `SecurityConfig` is required; without it the catch-all `/api/v1/admin/**` matcher would reject `SalesOps` at the filter chain before `@PreAuthorize` runs.

| Param | Type | Default | Description |
|---|---|---|---|
| `auctionId` | long | - | Exact match on parent `auction_id`. |
| `status` | string | - | Exact match on `round_status`. |
| `weekDisplay` | string | - | Case-insensitive contains on the denormalized `auction_week_year` column (e.g. `"2026 / Wk17"`). |
| `page` | int | 0 | Zero-based page number. |
| `pageSize` | int | 20 | Page size. |

Rows are ordered by `start_datetime DESC NULLS LAST, auction_id DESC, round ASC` so the soonest active round lands first and unscheduled rounds (null `start_datetime`) sink to the bottom.

**Response**: `200 OK` with `SchedulingAuctionListPageResponse`:

```json
{
  "content": [
    {
      "id": 301,
      "auctionId": 101,
      "auctionTitle": "Auction 2026 / Wk17",
      "auctionWeekYear": "2026 / Wk17",
      "round": 1,
      "name": "Round 1",
      "startDatetime": "2026-04-21T16:00:00Z",
      "endDatetime": "2026-04-25T07:00:00Z",
      "roundStatus": "Scheduled",
      "hasRound": true
    }
  ],
  "page": 0,
  "pageSize": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

### POST /admin/auctions

Create a new auction for a week. Writes a single `auctions.auctions` row. Per the 2026-04-20 ADR amendment, the three `auctions.scheduling_auctions` rounds are persisted later by the Schedule endpoint (`PUT /admin/auctions/{id}/schedule`, Phase C) — an `Unscheduled` auction legitimately has zero rounds until the admin confirms the schedule.

**Roles**: `Administrator` or `SalesOps`. Bidder and other roles → `403`.

**Request body**:

```json
{ "weekId": 42, "customSuffix": "Pilot" }
```

| Field | Type | Required | Description |
|---|---|---|---|
| `weekId` | long | yes | FK into `mdm.week`. Must resolve to an existing week. |
| `customSuffix` | string | no | Optional label appended to the auto-generated title. Trimmed server-side; blank/whitespace is treated as absent. |

**Title composition**: `"Auction " + week.weekDisplay` (e.g. `"Auction 2026 / Wk17"`), optionally suffixed by a single space + the trimmed `customSuffix` (e.g. `"Auction 2026 / Wk17 Pilot"`). Titles are case-insensitively unique across `auctions.auctions`.

**Response**: `201 Created` with `Location: /api/v1/admin/auctions/{id}` and body:

```json
{
  "id": 101,
  "auctionTitle": "Auction 2026 / Wk17",
  "auctionStatus": "Unscheduled",
  "weekId": 42,
  "weekDisplay": "2026 / Wk17"
}
```

**Errors**:

| Status | Cause | Body `message` |
|---|---|---|
| `400` | `weekId` missing/null | `"weekId is required"` |
| `403` | Non-Administrator/SalesOps role | `"Access denied"` |
| `404` | `weekId` doesn't resolve | `"Week not found: id=…"` |
| `409` | Week already has an auction | `"An auction already exists for week id=…"` |
| `409` | Title collides (case-insensitive) | `"An auction with this name already exists: …"` |

The frontend disambiguates the two 409 bodies by substring-matching `"name already exists"` — duplicate title becomes an inline field error; auction-already-exists surfaces as a banner.

### GET /admin/auctions/{id}

Fetch a single auction with its rounds. Backs the Auction Scheduling page header and, after Phase D lands, the auction overview screen.

**Roles**: `Administrator` or `SalesOps`.

**Response**: `200 OK` with `AuctionDetailResponse`:

```json
{
  "id": 42,
  "auctionTitle": "Auction 2026 / Wk17",
  "auctionStatus": "Scheduled",
  "weekId": 100,
  "weekDisplay": "2026 / Wk17",
  "rounds": [
    { "id": 301, "round": 1, "name": "Round 1", "startDatetime": "2026-04-21T16:00:00Z", "endDatetime": "2026-04-25T07:00:00Z", "roundStatus": "Scheduled", "hasRound": true },
    { "id": 302, "round": 2, "name": "Round 2", "startDatetime": "2026-04-25T13:00:00Z", "endDatetime": "2026-04-26T08:00:00Z", "roundStatus": "Scheduled", "hasRound": true },
    { "id": 303, "round": 3, "name": "Upsell Round", "startDatetime": "2026-04-26T11:00:00Z", "endDatetime": "2026-04-27T12:00:00Z", "roundStatus": "Scheduled", "hasRound": true }
  ]
}
```

`rounds` is an empty list when the auction is `Unscheduled`. Round 3's `name` is the literal `"Upsell Round"` — it is the customer-facing label inherited from the Mendix source.

**Errors**: `404` when the id does not resolve.

### GET /admin/auctions/{id}/schedule-defaults

Return the default (or current) round start/end datetimes for the Auction Scheduling page. Ports Mendix `ACT_LoadScheduleAuction_Helper` plus `ACT_Create_SchedulingAuction_Helper_Default`.

**Roles**: `Administrator` or `SalesOps`.

**Response**: `200 OK` with `ScheduleDefaultsResponse`:

```json
{
  "round1Start": "2026-04-21T16:00:00Z",
  "round1End": "2026-04-25T07:00:00Z",
  "round2Start": "2026-04-25T13:00:00Z",
  "round2End": "2026-04-26T08:00:00Z",
  "round3Start": "2026-04-26T11:00:00Z",
  "round3End": "2026-04-27T12:00:00Z",
  "round2Active": true,
  "round3Active": true,
  "round2MinutesOffset": 360,
  "round3MinutesOffset": 180
}
```

When the auction has no rounds yet, values are derived from `week.week_start_datetime` using offsets 16h (R1 start), 103h (R1 end), 128h (R2 end), 156h (R3 end), plus the config-driven `round2MinutesOffset` / `round3MinutesOffset` gap between rounds. When rounds already exist (reschedule), the stored start/end times and `hasRound`/`roundStatus` flags are used instead. The two offset fields are returned so the frontend can recompute later rounds' `From` values locally when an admin tweaks an earlier round's `End`.

### PUT /admin/auctions/{id}/schedule

Save or reschedule an auction. Mirrors Mendix `ACT_SaveScheduleAuction` + `VAL_Schedule_Auction`. Deletes the auction's existing rounds and writes three fresh `auctions.scheduling_auctions` rows in a single pessimistic-locked transaction. The parent `auctions.auctions.auction_status` flips to `Scheduled`. A post-commit `AuctionScheduledEvent` is published for audit (Phase F will wire the Snowflake push).

**Roles**: `Administrator` or `SalesOps`.

**Request body**:

```json
{
  "round1Start": "2026-04-21T16:00:00Z",
  "round1End":   "2026-04-25T07:00:00Z",
  "round2Start": "2026-04-25T13:00:00Z",
  "round2End":   "2026-04-26T08:00:00Z",
  "round2Active": true,
  "round3Start": "2026-04-26T11:00:00Z",
  "round3End":   "2026-04-27T12:00:00Z",
  "round3Active": true
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `round1Start`, `round1End` | `Instant` | yes | Round 1 is always active. |
| `round2Start`, `round2End` | `Instant` | yes when `round2Active=true` | Persisted even when inactive so the round-edit modal can reveal them later. |
| `round2Active` | boolean | yes | Maps to `has_round` on the persisted row; also drives `round_status` (`Scheduled` vs `Unscheduled`). |
| `round3Start`, `round3End` | `Instant` | yes when `round3Active=true` | Same semantics as R2. |
| `round3Active` | boolean | yes | Upsell-round toggle — mirrors Mendix. |

**Response**: `200 OK` with the same `AuctionDetailResponse` as `GET /admin/auctions/{id}`.

**Errors**:

| Status | Cause | Body `message` |
|---|---|---|
| `400` | Any active round with `end <= start` | Comma-joined list: `"Round 1 end date must be later than start date, Round 2 end date must be later than start date"`. Per-round errors are also surfaced in the `details[]` array so the UI can pin inline errors. |
| `404` | `id` does not resolve, or week missing | `"Auction not found: id=…"` |
| `409` | Any round already in `Started` status | `"Auction has started; this action is not available."` |
| `409` | Existing bids recorded for any round | `"Bids have already been submitted; reschedule is not available."` |

### POST /admin/auctions/{id}/unschedule

Flip an auction from `Scheduled` back to `Unscheduled`. Every round is moved to `Unscheduled`; no rows are deleted (bids, if any, remain intact for audit). Publishes `AuctionUnscheduledEvent` post-commit.

**Roles**: `Administrator` or `SalesOps`.

**Response**: `200 OK` with `AuctionDetailResponse` (`auctionStatus = "Unscheduled"`).

**Errors**:

| Status | Cause | Body `message` |
|---|---|---|
| `404` | `id` does not resolve | `"Auction not found: id=…"` |
| `409` | Any round already in `Started` status | `"Auction has started. Unscheduling the auction is not available."` |

### DELETE /admin/auctions/{id}

Delete an auction and all its scheduling / bid rows. Relies on the `ON DELETE CASCADE` on `auctions.scheduling_auctions.auction_id` (V58) and `auctions.bid_rounds.scheduling_auction_id` (V59).

**Roles**: `Administrator` only — `SalesOps` is denied (`403`). Matches Mendix parity where the legacy admin UI restricted delete to Admins even though the schedule flow also permitted SalesOps.

**Response**: `204 No Content`.

**Errors**:

| Status | Cause | Body `message` |
|---|---|---|
| `403` | Role is not Administrator | `"Access denied"` |
| `404` | `id` does not resolve | `"Auction not found: id=…"` |
| `409` | Any round already in `Started` status | `"Auction has started. Deleting the auction is not available."` |

### GET /admin/auctions/round-filters/{round}

Fetch the Round 2 or Round 3 Selection Rules (qualification filters) row. Backs the `acc_RoundTwoCriteriaPage` / `PG_Round3Criteria` port at `/admin/auctions-data-center/auctions/round-filters/[round]`. Exactly one row per round is stored in `auctions.bid_round_selection_filters`.

**Roles**: `Administrator` or `SalesOps`. Both roles can read; only `Administrator` can write via PUT.

**Path params**:

| Name | Type | Description |
|---|---|---|
| `round` | int | Must be `2` or `3`. Any other value returns `400`. |

**Response**: `200 OK` with `BidRoundSelectionFilterResponse`:

```json
{
  "id": 1,
  "legacyId": 101,
  "round": 2,
  "targetPercent": 85.0,
  "targetValue": 50.0,
  "totalValueFloor": 25.0,
  "mergedGrade1": "Good",
  "mergedGrade2": "Fair",
  "mergedGrade3": null,
  "stbAllowAllBuyersOverride": true,
  "stbIncludeAllInventory": false,
  "regularBuyerQualification": "Only_Qualified",
  "regularBuyerInventoryOptions": "InventoryRound1QualifiedBids",
  "createdDate": "2026-04-20T12:00:00Z",
  "changedDate": "2026-04-20T12:00:00Z"
}
```

`regularBuyerQualification` is one of `"Only_Qualified" | "All_Buyers"`. `regularBuyerInventoryOptions` is one of `"InventoryRound1QualifiedBids" | "ShowAllInventory"`. The three numeric columns (`targetPercent`, `targetValue`, `totalValueFloor`) are plain JSON numbers, not strings — the backend serializes `BigDecimal` via Jackson with numeric output, and the three columns are nullable.

**Errors**:

| Status | Cause | Body `message` |
|---|---|---|
| `400` | `round` is not `2` or `3` | `"Round must be 2 or 3, got: …"` |
| `403` | Role is not Administrator/SalesOps | `"Access denied"` |
| `404` | No row exists for the requested round | `"BidRoundSelectionFilter not found for round=…"` |

### PUT /admin/auctions/round-filters/{round}

Update the Round 2 / Round 3 Selection Rules. Writes all editable columns in a single transaction and bumps `changed_date`.

**Roles**: `Administrator` only. `SalesOps` is denied at the filter chain with `403` — matches the 2026-04-19 ADR pattern, because the existing `/api/v1/admin/auctions/**` matcher permits `Administrator` or `SalesOps`, and the PUT matcher is declared **before** it in `SecurityConfig` to restrict writes.

**Path params**: same as GET.

**Request body** (`BidRoundSelectionFilterRequest`):

```json
{
  "targetPercent": 85.0,
  "targetValue": 50.0,
  "totalValueFloor": 25.0,
  "mergedGrade1": "Good",
  "mergedGrade2": "Fair",
  "mergedGrade3": null,
  "stbAllowAllBuyersOverride": true,
  "stbIncludeAllInventory": false,
  "regularBuyerQualification": "Only_Qualified",
  "regularBuyerInventoryOptions": "InventoryRound1QualifiedBids"
}
```

| Field | Type | Notes |
|---|---|---|
| `targetPercent` | `BigDecimal` (nullable) | Target qualification percent, e.g. `85` for 85%. |
| `targetValue` | `BigDecimal` (nullable) | Dollar threshold for qualification. |
| `totalValueFloor` | `BigDecimal` (nullable) | Floor applied to the buyer's total order value. |
| `mergedGrade1`, `mergedGrade2`, `mergedGrade3` | string (nullable) | Grade codes merged into a combined tier. Max 30 chars each (matches V59 column). Blank strings are coerced to `null` server-side. |
| `stbAllowAllBuyersOverride` | boolean | Special-treatment buyers: allow All-buyers override. Required; `null` is coerced to `false` so the NOT NULL column is never violated. |
| `stbIncludeAllInventory` | boolean | Special-treatment buyers: include all inventory regardless of Round 1 qualification. Same `null → false` default. |
| `regularBuyerQualification` | enum | `"Only_Qualified" \| "All_Buyers"`. |
| `regularBuyerInventoryOptions` | enum | `"InventoryRound1QualifiedBids" \| "ShowAllInventory"`. |

**Response**: `200 OK` with the updated `BidRoundSelectionFilterResponse` (same shape as GET).

**Errors**:

| Status | Cause | Body `message` |
|---|---|---|
| `400` | `round` is not `2` or `3` | `"Round must be 2 or 3, got: …"` |
| `400` | Enum value outside the allowed set | Bean-validation / deserialization message |
| `403` | Role is not Administrator | `"Access denied"` |
| `404` | No row exists for the requested round | `"BidRoundSelectionFilter not found for round=…"` |

### POST /admin/auctions/{auctionId}/rounds/1/init

Manually (re)initialize Round 1 for the given auction. Ports Mendix
`SUB_InitializeRound1` — clamps aggregated-inventory target prices to
the configured `minimum_allowed_bid` floor (both non-DW and DW variants)
and rewrites the Qualified Buyer Codes (QBC) set. Idempotent: running
twice in a row produces identical state.

Normally triggered automatically by the `auctionLifecycle` cron when a
Round 1 `SchedulingAuction` transitions `Scheduled → Started`. This
endpoint is the admin recovery lever — use it when the listener was
disabled at the time of transition, or to force a re-initialization.

**Roles**: `Administrator` only. `SalesOps` and all other roles → `403`.
An explicit matcher at `POST /api/v1/admin/auctions/*/rounds/1/init` in
`SecurityConfig` is required to restrict writes, declared **before** the
broader `/api/v1/admin/auctions/**` matcher that also permits SalesOps.

**Response**: `200 OK` with `Round1InitializationResult`:

```json
{
  "schedulingAuctionId": 301,
  "auctionId": 101,
  "weekId": 42,
  "clampedNonDw": 3,
  "clampedDw": 1,
  "qbcsCreated": 579,
  "durationMs": 187
}
```

**Errors**:

| Status | Cause |
|---|---|
| `403` | Role is not Administrator |
| `404` | Auction has no Round 1 scheduling auction |
| `500` | Unexpected runtime error — tx is rolled back; check logs for `r1-init failed ...` |

> **Out-of-band status changes:** the `auctionLifecycle` cron job
> (see `docs/app-metadata/scheduled-events.md`) transitions
> `auction_status` and `round_status` automatically every minute when a
> round's start or end time has passed. API consumers should treat these
> fields as eventually consistent — a `Scheduled` auction may flip to
> `Started` without any HTTP call having been made.

---

## Bidder Dashboard

Backs `/bidder/dashboard`. Ports Mendix `ACT_OpenBidderDashboard` +
`ACT_CreateBidData` + `ACT_SubmitBidRound`. All three endpoints live under
`/api/v1/bidder/**` and require `Bidder` or `Administrator` roles; both the
`SecurityConfig` matcher and the class-level `@PreAuthorize` on
`BidderDashboardController` must agree, or the filter chain will reject
requests before method security runs.

### GET /api/v1/bidder/dashboard?buyerCodeId={id}

Landing-route + grid load. Resolves the auction state and, when a Started
round is active, synchronously materializes any missing `auctions.bid_data`
rows for the `(buyer_code, bid_round)` pair via a single
`INSERT ... SELECT` guarded by a Postgres advisory lock. Re-entrant: a
second call with rows already present is a no-op on the creation path.

**Roles**: `Bidder` (own buyer codes) or `Administrator`.

**Query params**:

| Name | Type | Description |
|---|---|---|
| `buyerCodeId` | long | FK into `buyer_mgmt.buyer_codes`. Bidder callers must own this buyer code; Administrator may pass any. |

**Response**: `200 OK` with `BidderDashboardResponse`:

```json
{
  "mode": "GRID",
  "auction": {
    "id": 301,
    "auctionId": 101,
    "auctionTitle": "Auction 2026 / Wk17",
    "round": 1,
    "roundName": "Round 1",
    "status": "Started"
  },
  "bidRound": {
    "id": 9001,
    "schedulingAuctionId": 301,
    "round": 1,
    "roundStatus": "Started",
    "startDatetime": "2026-04-21T16:00:00Z",
    "endDatetime":   "2026-04-25T07:00:00Z",
    "submitted": false,
    "submittedDatetime": null
  },
  "rows": [
    {
      "id": 555001,
      "bidRoundId": 9001,
      "ecoid": "SKU-12345",
      "mergedGrade": "Good",
      "buyerCodeType": "Wholesale",
      "bidQuantity": null,
      "bidAmount": "0.00",
      "targetPrice": "42.17",
      "maximumQuantity": 120,
      "payout": "0.00",
      "submittedBidQuantity": null,
      "submittedBidAmount": null,
      "lastValidBidQuantity": null,
      "lastValidBidAmount": null,
      "submittedDatetime": null,
      "changedDate": "2026-04-22T14:00:00Z"
    }
  ],
  "totals": {
    "rowCount": 582,
    "totalBidAmount": "0.00",
    "totalPayout": "0.00",
    "totalBidQuantity": 0
  },
  "timer": {
    "now": "2026-04-22T14:00:00Z",
    "startsAt": "2026-04-21T16:00:00Z",
    "endsAt":   "2026-04-25T07:00:00Z",
    "secondsUntilStart": 0,
    "secondsUntilEnd":   234000,
    "active": true
  }
}
```

`mode` is one of:

| Value | Meaning | Other fields |
|---|---|---|
| `GRID` | A Started round is active; `rows[]` populated, grid is editable. | All fields present. |
| `DOWNLOAD` | Round 1 is Closed but Round 2 hasn't opened — bidder can download their Round 1 bids. | `auction`, `bidRound`, `totals`, `timer` are `null`; `rows=[]`. |
| `ALL_ROUNDS_DONE` | Every round of the week's auction is `Closed`. Returns 200. | Same null-slot shape as `DOWNLOAD`. |
| `ERROR_AUCTION_NOT_FOUND` | No auction for the current week. Returns `404 Not Found` so the frontend can distinguish from a network error. | Same null-slot shape. |

`bidQuantity` of `null` is the **no-cap sentinel** — the bidder accepts any
quantity up to `maximumQuantity`. Zero means "I'll bid this price but I
want zero units" (valid but unusual).

**Errors**:

| Status | Cause |
|---|---|
| `403` | Bidder caller doesn't own the requested `buyerCodeId`. |
| `404` | `mode=ERROR_AUCTION_NOT_FOUND` — the body still shapes as `BidderDashboardResponse`. |

### PUT /api/v1/bidder/bid-data/{id}

Save edits to one `bid_data` row. Rate-limited to **60 requests/minute** per
`(user_id, bid_round_id)` bucket via `BidRateLimiter`. The limiter runs
before the service layer, so a denied request never touches the database.

**Request body** (`SaveBidRequest`):

```json
{ "bidQuantity": 12, "bidAmount": 40.00 }
```

| Field | Type | Notes |
|---|---|---|
| `bidQuantity` | int (nullable) | `null` = no-cap; `0` = zero-unit bid; negative rejected with `INVALID_QUANTITY`. |
| `bidAmount` | decimal | Must be `>= 0`. Negative or `null` rejected with `INVALID_AMOUNT`. |

**Response**: `200 OK` with the full refreshed `BidDataRow` (same shape as
the grid's `rows[]` entry), including the newly bumped `changedDate`.

**Errors**:

| Status | Error code | Cause |
|---|---|---|
| `400` | `INVALID_QUANTITY` | `bidQuantity < 0`. |
| `400` | `INVALID_AMOUNT` | `bidAmount` is null or negative. |
| `403` | `NOT_YOUR_BID_DATA` | The row's `user_id` doesn't match the caller. |
| `404` | `BID_DATA_NOT_FOUND` | No `bid_data` row with the given id. |
| `409` | `ROUND_CLOSED` | The owning bid round has transitioned to `Closed`. |
| `429` | — | Rate limit exhausted for this `(user, round)` bucket. Empty body. |

### POST /api/v1/bidder/bid-rounds/{id}/submit?buyerCodeId={id}

Re-callable submit. Copies `bid_quantity`/`bid_amount` →
`submitted_bid_quantity`/`submitted_bid_amount`, and prior
`submitted_*` → `last_valid_*` for every row of the given round +
buyer code. The buyer-code scoping on the server-side UPDATE is critical:
a bid round contains rows for every qualified buyer code of the week, and
without the `buyer_code_id` predicate a submit by buyer A would silently
flip rows belonging to buyer B. The `buyerCodeId` query param feeds this
predicate.

**Roles**: `Bidder` (own buyer codes) or `Administrator`.

**Query params**:

| Name | Type | Description |
|---|---|---|
| `buyerCodeId` | long | Scopes the UPDATE; caller must own it (Administrator exempt). |

**Request body**: (none)

**Response**: `200 OK` with `BidSubmissionResult`:

```json
{
  "bidRoundId": 9001,
  "rowCount": 582,
  "submittedDatetime": "2026-04-22T14:05:00Z",
  "resubmit": false
}
```

`resubmit=true` indicates the bid round was already in a submitted state —
the call still succeeded and the row count was rewritten. `rowCount` is
the number of rows flipped (= number of rows for the `(round, buyer_code)`
slice).

**Errors**:

| Status | Error code | Cause |
|---|---|---|
| `403` | `NOT_YOUR_BID_ROUND` | Caller doesn't own the requested `buyerCodeId`. |
| `404` | `BID_ROUND_NOT_FOUND` | No `bid_round` with the given id. |
| `409` | `ROUND_CLOSED` | The round has transitioned to `Closed`; resubmit is not permitted. |

---

## Auth

### POST /auth/login

Authenticate with email/password. Returns JWT token.

### GET /auth/buyer-codes?userId={id}

List buyer codes for a user.
