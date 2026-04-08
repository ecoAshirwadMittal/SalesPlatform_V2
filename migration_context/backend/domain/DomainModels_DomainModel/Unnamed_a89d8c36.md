# Domain Model

## Entities

### 📦 PurchaseOrder
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Weekrange` | StringAttribute | 200 | - | - |
| `ValidYearWeek` | BooleanAttribute | - | false | - |
| `PORefreshTimeStamp` | DateTimeAttribute | - | - | - |
| `TotalRecords` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PO.Administrator, EcoATM_PO.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PODetail
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductID` | IntegerAttribute | - | - | - |
| `Grade` | StringAttribute | 200 | - | - |
| `ModelName` | StringAttribute | 200 | - | - |
| `Price` | DecimalAttribute | - | - | - |
| `QtyCap` | IntegerAttribute | - | - | - |
| `PriceFulfilled` | DecimalAttribute | - | - | - |
| `QtyFullfiled` | IntegerAttribute | - | - | - |
| `TempBuyerCode` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PO.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PO.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 POHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ENUM_ActionType` | Enum(`EcoATM_PO.ENUM_POActionType`) | - | - | - |
| `EnablePOUpdate` | BooleanAttribute | - | false | - |
| `MissingBuyerCodeValidation` | BooleanAttribute | - | false | - |
| `MissingBuyerCodeList` | StringAttribute | - | - | - |
| `InvalidFileValidation` | BooleanAttribute | - | false | - |
| `InValidPOPeriod` | BooleanAttribute | - | false | - |
| `FileName` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PO.Administrator, EcoATM_PO.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PODetails_NP
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductID` | IntegerAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `ModelName` | StringAttribute | - | - | - |
| `BuyerCode` | StringAttribute | - | - | - |
| `Price` | DecimalAttribute | - | 0 | - |
| `QtyCap` | StringAttribute | 200 | - | - |

### 📦 WeeklyPO
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Qty` | IntegerAttribute | - | - | - |
| `Price` | DecimalAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PO.Administrator, EcoATM_PO.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PurchaseOrderDoc
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PO.Administrator, EcoATM_PO.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 WeekPeriod
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PO.Administrator, EcoATM_PO.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `PODetail_PurchaseOrder` | 8182da74-0fc2-4a7a-a413-2555f415089e | 7f2f0c07-610f-4ede-83bd-c62a27c73d1d | Reference | Default | DeleteMeButKeepReferences |
| `POHelper_PurchaseOrder` | 5bc6f649-2a19-4c47-bbcb-81e407f1f4c2 | 7f2f0c07-610f-4ede-83bd-c62a27c73d1d | Reference | Default | DeleteMeButKeepReferences |
| `WeeklyPO_PODetail` | e36b52ce-ab08-4bd0-a7b7-26970a27754b | 8182da74-0fc2-4a7a-a413-2555f415089e | Reference | Default | DeleteMeButKeepReferences |
| `WeeklyPO_PurchaseOrder` | e36b52ce-ab08-4bd0-a7b7-26970a27754b | 7f2f0c07-610f-4ede-83bd-c62a27c73d1d | Reference | Default | DeleteMeButKeepReferences |
| `PurchaseOrder_PurchaseOrderDoc` | 7f2f0c07-610f-4ede-83bd-c62a27c73d1d | 2070bdd3-91c2-4b7e-bceb-715e58b2e0d1 | Reference | Both | DeleteMeButKeepReferences |
| `WeekPeriod_PurchaseOrder` | ed8a4e96-2c38-4d06-af58-22d644a44083 | 7f2f0c07-610f-4ede-83bd-c62a27c73d1d | Reference | Default | DeleteMeButKeepReferences |
