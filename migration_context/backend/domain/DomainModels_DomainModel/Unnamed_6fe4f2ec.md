# Domain Model

## Entities

### 📦 RMA
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Number` | StringAttribute | - | - | - |
| `RequestSKUs` | IntegerAttribute | - | - | - |
| `RequestQty` | IntegerAttribute | - | - | - |
| `RequestSalesTotal` | IntegerAttribute | - | - | - |
| `ApprovalDate` | DateTimeAttribute | - | - | - |
| `ApprovedSKUs` | IntegerAttribute | - | - | - |
| `ApprovedQty` | IntegerAttribute | - | - | - |
| `ApprovedSalesTotal` | IntegerAttribute | - | - | - |
| `SubmittedDate` | DateTimeAttribute | - | - | - |
| `ReviewCompletedOn` | DateTimeAttribute | - | - | - |
| `AllRMAItemsValid` | BooleanAttribute | - | false | - |
| `ApprovedCount` | IntegerAttribute | - | - | - |
| `DeclinedCount` | IntegerAttribute | - | - | - |
| `EntityOwner` | StringAttribute | 200 | - | - |
| `EntityChanger` | StringAttribute | 200 | - | - |
| `SystemStatus` | StringAttribute | 200 | - | - |
| `OracleRMAStatus` | StringAttribute | - | - | - |
| `JSONContent` | StringAttribute | - | - | - |
| `OracleHTTPCode` | IntegerAttribute | - | - | - |
| `OracleJSONResponse` | StringAttribute | - | - | - |
| `IsSuccessful` | BooleanAttribute | - | false | - |
| `OracleNumber` | StringAttribute | 200 | - | - |
| `OracleId` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.Bidder | ✅ | ✅ | ✅ | ❌ | `[EcoATM_RMA.RMA_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_RMA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMAStatus
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SystemStatus` | StringAttribute | - | - | - |
| `IsDefaultStatus` | BooleanAttribute | - | false | - |
| `InternalStatusText` | StringAttribute | 200 | - | - |
| `InternalStatusHexCode` | Enum(`EcoATM_Direct_Theme.ENUM_PWSBgColor`) | - | - | - |
| `ExternalStatusText` | StringAttribute | 200 | - | - |
| `ExternalStatusHexCode` | Enum(`EcoATM_Direct_Theme.ENUM_PWSBgColor`) | - | - | - |
| `Desciption` | StringAttribute | - | - | - |
| `SalesStatusHeaderHexCode` | Enum(`EcoATM_Direct_Theme.ENUM_PWSBgColor`) | - | - | - |
| `SalesTableHoverHexCode` | Enum(`EcoATM_Direct_Theme.ENUM_PWSBgColor`) | - | - | - |
| `SortOrder` | IntegerAttribute | - | - | - |
| `StatusGroupedTo` | Enum(`EcoATM_RMA.ENUM_StatusGroup`) | - | - | - |
| `StatusVerbiageBidder` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_RMA.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMAItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IMEI` | StringAttribute | - | - | - |
| `ShipDate` | DateTimeAttribute | - | - | - |
| `ReturnReason` | StringAttribute | - | - | - |
| `Status` | Enum(`EcoATM_RMA.ENUM_RMAItemStatus`) | - | - | - |
| `OrderNumber` | StringAttribute | 200 | - | - |
| `SalePrice` | IntegerAttribute | - | - | - |
| `DeclineReason` | StringAttribute | - | - | - |
| `StatusDisplay` | StringAttribute | 200 | - | - |
| `EntityOwner` | StringAttribute | 200 | - | - |
| `EntityChanger` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.Bidder | ✅ | ✅ | ✅ | ❌ | `[EcoATM_RMA.RMAItem_RMA/EcoATM_RMA.RMA/EcoATM_RMA.RMA_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMATemplate
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TemplateName` | StringAttribute | 200 | - | - |
| `IsActive` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMADetailsExport
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMAFile
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IsValid` | BooleanAttribute | - | false | - |
| `InvalidReason` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_RMA.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_RMA.RMAFile_RMA/EcoATM_RMA.RMA/EcoATM_RMA.RMA_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMARequest_ImportHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IMEISerial_Number` | StringAttribute | - | - | - |
| `Return_Reason` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 RMAId
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MaxRMAId` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_RMA.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_RMA.RMAId_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMAExcelDocument
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMAFilterHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FilterStatus` | Enum(`EcoATM_RMA.ENUM_RMAItemStatus`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 FileUploadProcessHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CurrentPercentage` | IntegerAttribute | - | 0 | - |
| `FileName` | StringAttribute | - | - | - |
| `Message` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMAMasterHelper
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMAUiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HeaderLabel` | Enum(`EcoATM_RMA.ENUM_StatusGroup`) | - | - | Header label can be use as an alternative title fo |
| `HeaderCSSClass` | StringAttribute | 200 | - | - |
| `DataGridHoverCSSClass` | StringAttribute | 200 | - | - |
| `RMASystemStatus` | StringAttribute | 200 | - | - |
| `RMACount` | IntegerAttribute | - | 0 | - |
| `TotalSKUs` | IntegerAttribute | - | 0 | - |
| `TotalQty` | IntegerAttribute | - | 0 | - |
| `TotalPrice` | IntegerAttribute | - | 0 | - |
| `SortOrder` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMAReturnLabel
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMAReasons
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ValidReasons` | StringAttribute | 200 | - | - |
| `IsActive` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RMAStatusFromDeposco
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderStatus` | StringAttribute | - | - | - |

### 📦 InvalidRMAItem_UiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `InvalidIMEI` | StringAttribute | - | - | - |
| `DuplicateIMEI` | StringAttribute | - | - | - |
| `InvalidReason` | StringAttribute | - | - | - |
| `InvalidIMEICount` | IntegerAttribute | - | 0 | - |
| `DuplicateIMEICount` | IntegerAttribute | - | 0 | - |
| `InvalidReasonCount` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 InvalidIMEI_ExportHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IMEI` | StringAttribute | 200 | - | - |
| `Reason` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps | ✅ | ✅ | ✅ | ❌ | - |

### 📦 InvalidRMAFileExport
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps | ✅ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `RMA_RMAStatus` | f18aa395-92d1-4632-b78c-1623c592842c | 39b9f6e9-0b67-4113-80f4-c95d5acd2067 | Reference | Default | DeleteMeButKeepReferences |
| `RMAItem_RMA` | 9f21e376-7c67-4a05-ab4b-8d71cf98a707 | f18aa395-92d1-4632-b78c-1623c592842c | Reference | Default | DeleteMeButKeepReferences |
| `RMAItem_RMADetailsExport` | 9f21e376-7c67-4a05-ab4b-8d71cf98a707 | e2bdbf20-490e-4259-872a-2b7019317e94 | Reference | Default | DeleteMeButKeepReferences |
| `RMAFile_RMA` | 82e3d3e6-b772-49e2-9160-0306d9b12af3 | f18aa395-92d1-4632-b78c-1623c592842c | Reference | Both | DeleteMeButKeepReferences |
| `RMA_RMAExcelDocument` | f18aa395-92d1-4632-b78c-1623c592842c | 54f5e00e-d1c2-4fa7-a2b3-486e88ddd145 | Reference | Default | DeleteMeButKeepReferences |
| `RMAUiHelper_RMAMasterHelper` | b2d02335-13da-4a26-9424-ea1ec1aaa445 | c062337b-ef83-4b6a-8be2-22844ba1dcb3 | Reference | Default | DeleteMeButKeepReferences |
| `RMAUiHelper_RMAMasterHelper_Selected` | b2d02335-13da-4a26-9424-ea1ec1aaa445 | c062337b-ef83-4b6a-8be2-22844ba1dcb3 | Reference | Both | DeleteMeButKeepReferences |
| `InvalidRMAItem_UiHelper_RMA` | 881d242f-30a2-4cad-a63b-e567a751e09a | f18aa395-92d1-4632-b78c-1623c592842c | Reference | Default | DeleteMeButKeepReferences |
| `RMAUiHelper_RMAStatus` | b2d02335-13da-4a26-9424-ea1ec1aaa445 | 39b9f6e9-0b67-4113-80f4-c95d5acd2067 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `InvalidIMEI_ExportHelper_InvalidRMAFileExport` | c6ded2ef-17d0-41ff-9d58-907d301c61e1 | 15341dd2-b53f-45d6-82b4-cfdc93c85ae0 | Reference | Default | DeleteMeButKeepReferences |
| `InvalidRMAItem_UiHelper_InvalidIMEI_ExportHelper` | 881d242f-30a2-4cad-a63b-e567a751e09a | c6ded2ef-17d0-41ff-9d58-907d301c61e1 | ReferenceSet | Default | DeleteMeAndReferences |
