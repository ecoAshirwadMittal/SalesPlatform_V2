# Domain Model

## Entities

### 📦 ReserveBidSync
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LastSyncDateTime` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_EB.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_EB.SalesLeader | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_EB.SalesOps | ✅ | ✅ | ✅ | ❌ | - |

### 📦 ReserveBid
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | IntegerAttribute | - | 0 | - |
| `Grade` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `Bid` | DecimalAttribute | - | 0 | - |
| `LastUpdateDateTime` | DateTimeAttribute | - | - | - |
| `LastAwardedMinPrice` | DecimalAttribute | - | 0 | - |
| `LastAwardedWeek` | StringAttribute | 200 | - | - |
| `BidValidWeekDate` | StringAttribute | 200 | - | - |

#### Indexes

- **Index**: `8300bd24-cf65-45f4-85fa-26b831c64ce1` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_EB.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_EB.SalesLeader | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_EB.SalesOps | ✅ | ✅ | ✅ | ❌ | - |

### 📦 ReserveBidFile
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_EB.Administrator, EcoATM_EB.SalesLeader, EcoATM_EB.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EB_Pricing
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductID` | DecimalAttribute | - | 0 | - |
| `Grade` | StringAttribute | - | - | - |
| `ModelName` | StringAttribute | - | - | - |
| `Price` | DecimalAttribute | - | 0 | - |

### 📦 ReservedBidAudit
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OldPrice` | DecimalAttribute | - | 0 | - |
| `NewPrice` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_EB.Administrator, EcoATM_EB.SalesLeader, EcoATM_EB.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `ReserveBidFile_ReserveBid` | b0ad58fe-70c9-489d-aed3-66cd55d0bc7f | c3efb6ae-ce61-4ed4-854d-b58ffbb945be | ReferenceSet | Both | DeleteMeButKeepReferences |
| `ReservedBidAudit_ReserveBid` | b5effba4-b73d-449a-a9be-c1644271e4c4 | c3efb6ae-ce61-4ed4-854d-b58ffbb945be | Reference | Default | DeleteMeButKeepReferences |
