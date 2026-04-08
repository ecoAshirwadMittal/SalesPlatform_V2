# Domain Model

## Entities

### 📦 OrderRequest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OriginSystemOrderId` | StringAttribute | 200 | - | - |
| `OrderType` | StringAttribute | 200 | - | - |
| `OrderDate` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `OriginationSystemUser` | StringAttribute | 200 | - | - |
| `ShippingInstructions` | StringAttribute | 200 | - | - |
| `FreeFormOrd01` | StringAttribute | 200 | - | - |
| `FreeFormOrd02` | StringAttribute | 200 | - | - |
| `FreeFormOrd03` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OrderLineItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Item_number` | StringAttribute | 200 | - | - |
| `Quantity` | StringAttribute | 200 | - | - |
| `UnitSellingPrice` | StringAttribute | 200 | - | - |
| `OriginSystemLineId` | StringAttribute | 200 | - | - |
| `FreeFormLine01` | StringAttribute | 200 | - | - |
| `FreeFormLine02` | StringAttribute | 200 | - | - |
| `FreeFormLine03` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OracleResponse
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ReturnCode` | StringAttribute | 200 | - | - |
| `ReturnMessage` | StringAttribute | - | - | - |
| `OrderId` | StringAttribute | 50 | - | - |
| `OrderNumber` | StringAttribute | 50 | - | - |
| `HTTPCode` | IntegerAttribute | - | - | - |
| `JSONResponse` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.Bidder, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PWSConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OracleUsername` | StringAttribute | 200 | - | Corresponding to Oracle username - Used for buildi |
| `OraclePassword` | StringAttribute | 200 | - | Corresponding to password - Used for building the  |
| `OracleAPIPathToken` | StringAttribute | 400 | - | Entry point dev: https://norelocodb01.ecoatm.com:8 |
| `OracleAPIPathCreateOrder` | StringAttribute | 400 | - | - |
| `OracleAPIPathCreateRMA` | StringAttribute | 400 | - | - |
| `OracleHTTPRequestTimeoutToken` | IntegerAttribute | - | 300 | in milliseconds |
| `OracleHTTPRequestTimeoutCreateOrder` | IntegerAttribute | - | 300 | - |
| `OracleHTTPRequestTimeoutCreateRMA` | IntegerAttribute | - | 300 | - |
| `IsOracleCreateOrderAPIOn` | BooleanAttribute | - | false | Per default, Oracle API is off. This parameter is  |
| `IsOracleCreateRMAAPIOn` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSIntegration.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSIntegration.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 TokenReponse
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `access_token` | StringAttribute | 200 | - | - |
| `token_type` | StringAttribute | 200 | - | - |
| `expires_in` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 VW_FACT_INVENTORY_PWS_CURRENT
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RESULTJSON` | StringAttribute | - | - | - |

### 📦 PWSResponseConfig
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SourceSystem` | StringAttribute | 200 | - | - |
| `SourceErrorCode` | StringAttribute | 200 | - | - |
| `SourceErrorType` | StringAttribute | 200 | - | WARNING, ERROR, INFO |
| `SourceErrorMessage` | StringAttribute | - | - | - |
| `UserErrorCode` | StringAttribute | 200 | - | - |
| `UserErrorMessage` | StringAttribute | 500 | - | - |
| `ByPassForUser` | BooleanAttribute | - | false | If ByPassForUser is true, this message will be ign |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSIntegration.Bidder, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SFDeviceTemp
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `INVENTORY_ITEM_ID` | LongAttribute | - | 0 | - |
| `SKU` | StringAttribute | 128 | - | - |
| `ITEM_DESCRIPTION` | StringAttribute | 512 | - | - |
| `CATEGORY` | StringAttribute | 128 | - | - |
| `BRAND` | StringAttribute | 128 | - | - |
| `DEVICE_MODEL` | StringAttribute | 128 | - | - |
| `AVAILABLE_QUANTITY` | LongAttribute | - | 0 | - |
| `CARRIER` | StringAttribute | 128 | - | - |
| `DEVICE_CAPACITY` | StringAttribute | 128 | - | - |
| `DEVICE_COLOR` | StringAttribute | 384 | - | - |
| `GRADE` | StringAttribute | 128 | - | - |
| `CONDITION` | StringAttribute | 128 | - | - |
| `FILTER` | StringAttribute | 1800 | - | - |

### 📦 Item
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Sku` | StringAttribute | - | - | - |
| `Title` | StringAttribute | - | - | - |
| `Brand` | StringAttribute | - | - | - |
| `Category` | StringAttribute | - | - | - |
| `Model` | StringAttribute | - | - | - |
| `ModelYear` | StringAttribute | - | - | - |
| `Carrier` | StringAttribute | - | - | - |
| `Capacity` | StringAttribute | - | - | - |
| `Color` | StringAttribute | - | - | - |
| `Condition` | StringAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `Weight` | DecimalAttribute | - | 0 | - |
| `ItemType` | StringAttribute | - | - | - |
| `IsActive` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.WebServices | ✅ | ✅ | ✅ | ✅ | - |

### 📦 StoreProcedureArgument
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ARG_NAME` | StringAttribute | 200 | - | - |
| `AGR_CONTENT` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.Bidder, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep, EcoATM_PWSIntegration.WebServices | ✅ | ✅ | ✅ | ✅ | - |

### 📦 QueryResult
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DeposcoConfig
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BaseURL` | StringAttribute | - | - | - |
| `Username` | StringAttribute | 500 | - | - |
| `Password` | StringAttribute | 500 | - | - |
| `TestString` | StringAttribute | - | - | - |
| `LastSyncTime` | DateTimeAttribute | - | - | - |
| `PageCount` | IntegerAttribute | - | 0 | - |
| `ReportAttr` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DesposcoAPIs
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ServiceUrl` | StringAttribute | - | - | - |
| `ServiceName` | Enum(`EcoATM_PWSIntegration.ENUM_DeposcoServices`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PWSInventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DeviceCount` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ItemInventoryItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ItemNumber` | StringAttribute | - | - | - |
| `PageNo` | IntegerAttribute | - | - | - |

### 📦 FacilityInventoryItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Facility` | StringAttribute | - | - | - |
| `_parameter_` | IntegerAttribute | - | 0 | - |

### 📦 Inventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Total` | StringAttribute | - | - | - |
| `AvailableToPromise` | StringAttribute | - | - | - |
| `Unallocated` | StringAttribute | - | - | - |
| `Allocated` | StringAttribute | - | - | - |

### 📦 Integration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `URL` | StringAttribute | - | - | - |
| `Method` | Enum(`EcoATM_PWSIntegration.ENUM_Method`) | - | - | - |
| `Request` | StringAttribute | - | - | - |
| `StartTime` | DateTimeAttribute | - | - | - |
| `EndTime` | DateTimeAttribute | - | - | - |
| `Response` | StringAttribute | - | - | - |
| `ResponseCode` | StringAttribute | 200 | - | - |
| `ErrorType` | StringAttribute | 200 | - | - |
| `ErrorMessage` | StringAttribute | - | - | - |
| `StackTrace` | StringAttribute | - | - | - |
| `IsSuccessful` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep, EcoATM_PWSIntegration.WebServices | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OrderHistory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Number` | StringAttribute | - | - | - |
| `CustomerOrderNumber` | StringAttribute | 200 | - | - |
| `ActualShipDate` | DateTimeAttribute | - | - | - |
| `ShipVia` | StringAttribute | - | - | - |
| `ShipVendor` | StringAttribute | - | - | - |
| `OrderStatus` | StringAttribute | - | - | - |

### 📦 ShipToContact
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FirstName` | StringAttribute | - | - | - |
| `Phone` | StringAttribute | - | - | - |

### 📦 ShipToAddress
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Line1` | StringAttribute | - | - | - |
| `Line2` | StringAttribute | - | - | - |
| `Line3` | StringAttribute | - | - | - |
| `Line4` | StringAttribute | - | - | - |
| `City` | StringAttribute | - | - | - |
| `StateProvince` | StringAttribute | - | - | - |
| `PostalCode` | StringAttribute | - | - | - |
| `Country` | StringAttribute | - | - | - |

### 📦 Shipmentdata
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | IntegerAttribute | - | 0 | - |

### 📦 Shipment
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TrackingUrl` | StringAttribute | - | - | - |
| `TrackingNumber` | StringAttribute | 200 | - | - |

### 📦 Line
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ItemNumber` | StringAttribute | - | - | - |
| `IMEINumber` | StringAttribute | - | - | - |
| `TrackingNumber` | StringAttribute | 200 | - | - |
| `SerialNumber` | StringAttribute | - | - | - |
| `BoxLPNNumber` | StringAttribute | 200 | - | - |

### 📦 Container
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Lpn` | StringAttribute | - | - | - |
| `TrackingNumber` | StringAttribute | 200 | - | - |

### 📦 AccessToken
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Access_token` | StringAttribute | - | - | - |

### 📦 ItemInventoryItem_Test
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ItemNumber` | StringAttribute | - | - | - |
| `PageNo` | IntegerAttribute | - | - | - |
| `_parameter_` | IntegerAttribute | - | 0 | - |

### 📦 FacilityInventoryItem_Test
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Facility` | StringAttribute | - | - | - |
| `_parameter_` | IntegerAttribute | - | 0 | - |

### 📦 Inventory_Test
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Total` | StringAttribute | - | - | - |
| `AvailableToPromise` | StringAttribute | - | - | - |
| `Unallocated` | StringAttribute | - | - | - |
| `Allocated` | StringAttribute | - | - | - |
| `_parameter_` | IntegerAttribute | - | 0 | - |

### 📦 RMARequest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OriginSystemOrderId` | StringAttribute | 200 | - | - |
| `OrderType` | StringAttribute | 200 | - | - |
| `OrderDate` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `OriginSystemUser` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RMALineItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ItemNumber` | StringAttribute | 200 | - | - |
| `UnitSellingPrice` | StringAttribute | 200 | - | - |
| `RMAOriginalOrder` | StringAttribute | 200 | - | - |
| `RMAIMEI` | StringAttribute | 200 | - | - |
| `RMAReason` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 StockUnitItems
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Quantity` | StringAttribute | - | - | - |
| `LpnNumber` | StringAttribute | - | - | - |
| `ItemNumber` | StringAttribute | - | - | - |
| `AllocatedOrderNumber` | StringAttribute | - | - | - |

### 📦 GroupedCaseLots
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ItemNumber` | StringAttribute | 200 | - | - |
| `CaseLotID` | StringAttribute | 200 | - | - |
| `CaseLotCount` | IntegerAttribute | - | 0 | - |
| `CaseLotSize` | IntegerAttribute | - | 0 | - |

### 📦 SKUQuantity
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderPackQuantity` | DecimalAttribute | - | 0 | - |
| `CanceledPackQuantity` | DecimalAttribute | - | 0 | - |

### 📦 Links
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Href` | StringAttribute | - | - | - |
| `Rel` | StringAttribute | - | - | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `OrderLineItem_OrderRequest` | 022096cf-b2b6-49c2-859a-a3614dd472bb | 34ab3ae8-485b-4895-ae78-48148b525c61 | Reference | Default | DeleteMeButKeepReferences |
| `DesposcoAPIs_DeposcoConfig` | 3653f4e9-8f5b-486a-b9d7-759f67c6a77c | d35f24a9-9045-4172-babe-2dbdf2601fa3 | Reference | Default | DeleteMeButKeepReferences |
| `FacilityInventoryItem_ItemInventoryItem` | efeda9c7-2f3d-40d6-97ee-85b55a8be57d | d3ae24d4-fcd0-427a-bb24-932a5d7bee39 | Reference | Default | DeleteMeButKeepReferences |
| `Inventory_FacilityInventoryItem` | 675be49d-bdd9-4b28-9518-1b92c4482929 | efeda9c7-2f3d-40d6-97ee-85b55a8be57d | Reference | Both | DeleteMeButKeepReferences |
| `ShipToContact_OrderHistory` | 98b73a15-d94c-40fc-b6fa-21ea50047e93 | 7e396a58-a801-4765-a3a3-eb4c98a0c8ac | Reference | Both | DeleteMeButKeepReferences |
| `ShipToAddress_OrderHistory` | c52df106-45c8-4cb7-8e98-1577fd1e7f67 | 7e396a58-a801-4765-a3a3-eb4c98a0c8ac | Reference | Both | DeleteMeButKeepReferences |
| `Shipmentdata_OrderHistory` | 292d2dbb-e7ad-46ff-aad8-f9ab949c8228 | 7e396a58-a801-4765-a3a3-eb4c98a0c8ac | Reference | Default | DeleteMeButKeepReferences |
| `Line_Shipment` | 5f8be074-e878-4c21-83dd-b9be7708dc47 | 759883fa-00f8-49d8-a49b-2df69e82bebe | ReferenceSet | Both | DeleteMeButKeepReferences |
| `Container_Line` | 563e5291-1a14-4800-837a-5bec2d91dffd | 5f8be074-e878-4c21-83dd-b9be7708dc47 | Reference | Both | DeleteMeButKeepReferences |
| `FacilityInventoryItem_Test_ItemInventoryItem_Test` | 0ae9b0d1-0617-422d-82f0-a38c1bee6a46 | bf35653f-a8c4-4ab2-b6c7-31c8d546ed05 | ReferenceSet | Both | DeleteMeButKeepReferences |
| `Inventory_Test_FacilityInventoryItem_Test` | f8ab1fed-458d-4e18-95f7-6ff6652e3ebd | 0ae9b0d1-0617-422d-82f0-a38c1bee6a46 | Reference | Both | DeleteMeButKeepReferences |
| `RMALineItem_RMARequest` | a207a336-5cb5-4e52-a743-f793004b7406 | 25425c3b-2464-406d-8a20-6366567d26ad | Reference | Default | DeleteMeButKeepReferences |
| `SKUQuantity_OrderHistory` | 70d3710d-a1f3-4416-8696-47cd5520dab2 | 7e396a58-a801-4765-a3a3-eb4c98a0c8ac | Reference | Default | DeleteMeButKeepReferences |
| `Links_OrderHistory` | 00ed9adc-4be8-484e-9794-55e2cfaf3e7d | 7e396a58-a801-4765-a3a3-eb4c98a0c8ac | Reference | Default | DeleteMeButKeepReferences |
