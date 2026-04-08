# Domain Model

## Entities

### 📦 AgregateRevenueTotal
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TotalPayout` | DecimalAttribute | - | - | - |
| `TotalAvailableQty` | LongAttribute | - | - | - |
| `TotalSold` | LongAttribute | - | - | - |
| `TotalRevenue` | DecimalAttribute | - | - | - |
| `TotalMargin` | DecimalAttribute | - | - | - |
| `MarginPercentage` | DecimalAttribute | - | - | - |
| `AverageSellingPrice` | DecimalAttribute | - | - | - |
| `AveragePurchasePrice` | DecimalAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 DAWeek
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LastUploadTime` | DateTimeAttribute | - | - | - |
| `LastRefreshTime` | DateTimeAttribute | - | - | - |
| `IsFinalized` | BooleanAttribute | - | false | - |
| `FinalizeTimeStamp` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DeviceAllocation
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductID` | IntegerAttribute | - | 0 | - |
| `Brand` | StringAttribute | 200 | - | - |
| `ModelName` | StringAttribute | 200 | - | - |
| `Grade` | StringAttribute | 200 | - | - |
| `AvailableQty` | IntegerAttribute | - | - | - |
| `Payout` | DecimalAttribute | - | - | - |
| `EB` | DecimalAttribute | - | - | - |
| `TargetPrice` | DecimalAttribute | - | - | - |
| `SalesQty` | IntegerAttribute | - | - | - |
| `AvgSalesPrice` | DecimalAttribute | - | - | - |
| `Revenue` | DecimalAttribute | - | - | - |
| `MinEB` | DecimalAttribute | - | - | - |
| `MinEBWeekEnding` | DateTimeAttribute | - | - | - |
| `Margin` | DecimalAttribute | - | - | - |
| `MarginPercentage` | DecimalAttribute | - | - | - |
| `DataWipeQty` | IntegerAttribute | - | - | - |
| `DataWipePayout` | DecimalAttribute | - | - | - |
| `DataWipeTargetPrice` | DecimalAttribute | - | - | - |
| `NonDataWipeQty` | IntegerAttribute | - | - | - |
| `NonDataWipePayout` | DecimalAttribute | - | - | - |
| `NonDataWipeTargetPrice` | DecimalAttribute | - | - | - |
| `Review` | StringAttribute | 200 | - | - |
| `IsChanged` | BooleanAttribute | - | false | - |
| `RowType` | StringAttribute | 200 | - | - |
| `BuyerJSON` | StringAttribute | - | - | - |

#### Indexes

- **Index**: `a52af71f-8757-4efe-911a-8deb5cdd35a5` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DeviceBuyer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BuyerCode` | StringAttribute | 200 | - | - |
| `BuyerName` | StringAttribute | 200 | - | - |
| `Bid` | DecimalAttribute | - | 0 | - |
| `QtyCap` | IntegerAttribute | - | - | - |
| `AwardedQty` | IntegerAttribute | - | 0 | - |
| `Reject` | BooleanAttribute | - | false | - |
| `RejectReason` | StringAttribute | - | - | - |
| `AcceptReason` | StringAttribute | - | - | - |
| `ClearingBid` | BooleanAttribute | - | false | - |
| `EB` | BooleanAttribute | - | false | - |
| `IsChanged` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DAHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DisplayDA_DataGrid` | BooleanAttribute | - | false | - |
| `DAGridPersonlization` | StringAttribute | - | - | - |
| `AuctionEndDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Root
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 JsonObject
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Week` | IntegerAttribute | - | 0 | - |
| `Year` | IntegerAttribute | - | 0 | - |
| `EB` | DecimalAttribute | - | 0 | - |
| `ProductID` | StringAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `BuyerCode` | StringAttribute | - | - | - |
| `QuantityAllocated` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Buyercode
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyercodeItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 NPE_ClearingBid
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `PageHeader` | BooleanAttribute | - | false | - |
| `ClearingBid` | StringAttribute | 200 | - | - |
| `LastAwardedDate` | StringAttribute | 200 | - | - |
| `LastAwardedMinimumPrice` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerSummary
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BuyerCode` | StringAttribute | 200 | - | - |
| `BuyerName` | StringAttribute | 200 | - | - |
| `SalesQty` | LongAttribute | - | 0 | - |
| `Amount` | DecimalAttribute | - | 0 | - |
| `WeeklyBudget` | DecimalAttribute | - | - | - |
| `PreviousWeekSalesQty` | LongAttribute | - | 0 | - |
| `PreviousWeekAmount` | DecimalAttribute | - | 0 | - |
| `PreviousWeekWeeklyBudget` | DecimalAttribute | - | - | - |
| `CurrentEcoATMGradeDetails` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerDetail
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | IntegerAttribute | - | 0 | - |
| `Grade` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `AvgSalesPrice` | DecimalAttribute | - | 0 | - |
| `SalesQty` | IntegerAttribute | - | 0 | - |
| `Amount` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerDetailTotals
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SalesQty` | IntegerAttribute | - | 0 | - |
| `Amount` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 StringHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Key` | StringAttribute | 200 | - | - |
| `Value` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 NP_DeviceBuyer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BuyerCode` | StringAttribute | 200 | - | - |
| `QuantityAllocated` | IntegerAttribute | - | 0 | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `AgregateRevenueTotal_DAWeek` | e9bb6f9b-b334-4383-9434-8a0f8d57e725 | accbf8d6-c4fe-4173-b6a1-590f0ac5df5b | Reference | Both | DeleteMeButKeepReferences |
| `DeviceAllocation_DAWeek` | a3469a18-039c-4e05-bb37-854e6ebb2c17 | accbf8d6-c4fe-4173-b6a1-590f0ac5df5b | Reference | Default | DeleteMeButKeepReferences |
| `DAHelper_DAWeek` | e2361851-aa63-44c0-95e6-15070eb1afee | accbf8d6-c4fe-4173-b6a1-590f0ac5df5b | Reference | Default | DeleteMeButKeepReferences |
| `DeviceBuyer_DAWeek` | 62a5297c-831f-45d2-88e2-0bc0cf7a3929 | accbf8d6-c4fe-4173-b6a1-590f0ac5df5b | Reference | Default | DeleteMeButKeepReferences |
| `JsonObject_Root` | b6d61893-7e0b-44fa-b37a-fca728d9da91 | 38f8dcf1-1bd7-4f2b-ab83-9edf386d84fc | Reference | Default | DeleteMeButKeepReferences |
| `Buyercode_JsonObject` | 2170376d-d438-4385-ac42-e66e37808219 | b6d61893-7e0b-44fa-b37a-fca728d9da91 | Reference | Both | DeleteMeButKeepReferences |
| `BuyercodeItem_Buyercode` | a012c68f-c371-4088-a793-ae92f088cb00 | 2170376d-d438-4385-ac42-e66e37808219 | Reference | Default | DeleteMeButKeepReferences |
| `DeviceBuyer_DeviceAllocation` | 62a5297c-831f-45d2-88e2-0bc0cf7a3929 | a3469a18-039c-4e05-bb37-854e6ebb2c17 | Reference | Default | DeleteMeButKeepReferences |
| `DeviceAllocation_DAHelper` | a3469a18-039c-4e05-bb37-854e6ebb2c17 | e2361851-aa63-44c0-95e6-15070eb1afee | ReferenceSet | Both | DeleteMeButKeepReferences |
| `BuyerDetail_BuyerSummary` | 7d5335db-e122-4691-be34-e11c26052cd8 | dbbae46e-8f2b-48a7-860e-234b94b12856 | Reference | Default | DeleteMeButKeepReferences |
| `BuyerDetailTotals_BuyerSummary` | b085b339-3a24-4cf4-af76-7afd798a3051 | dbbae46e-8f2b-48a7-860e-234b94b12856 | Reference | Both | DeleteMeButKeepReferences |
| `NP_DeviceBuyer_Root` | 41468851-8b06-4c88-bc57-939fded2301e | 38f8dcf1-1bd7-4f2b-ab83-9edf386d84fc | Reference | Default | DeleteMeButKeepReferences |
