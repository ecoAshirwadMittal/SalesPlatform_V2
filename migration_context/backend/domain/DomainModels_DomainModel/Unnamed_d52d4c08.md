# Domain Model

## Entities

### 📦 Device
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |
| `DeviceCode` | StringAttribute | 200 | - | - |
| `DeviceDescription` | StringAttribute | 200 | - | - |
| `AvailableQty` | IntegerAttribute | - | 0 | - |
| `CurrentListPrice` | IntegerAttribute | - | 0 | - |
| `CurrentMinPrice` | IntegerAttribute | - | 0 | - |
| `FutureMinPrice` | IntegerAttribute | - | 0 | - |
| `FutureListPrice` | IntegerAttribute | - | 0 | - |
| `SearchAttr` | StringAttribute | - | - | - |
| `IsActive` | BooleanAttribute | - | true | - |
| `Weight` | DecimalAttribute | - | - | 20250514 BORNPA - Added. data coming from the Publ |
| `ModelYear` | StringAttribute | 50 | - | 20250514 BORNPA - New data coming from Published R |
| `ItemType` | StringAttribute | 200 | - | 20250514 BORNPA - New data coming from Published R |
| `LastUpdateDate` | DateTimeAttribute | - | - | - |
| `ATPQty` | IntegerAttribute | - | - | - |
| `ReservedQty` | IntegerAttribute | - | - | - |
| `DeposcoPageNo` | IntegerAttribute | - | - | - |
| `LastSyncTime` | DateTimeAttribute | - | - | - |

#### Indexes

- **Index**: `ac1afb9c-421c-418a-9b22-d3f558179d9a` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Model
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Model` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Capacity
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Capacity` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Color
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Color` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Carrier
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Carrier` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Grade
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Grade` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Category
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Category` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 PriceHistory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ListPrice` | IntegerAttribute | - | - | - |
| `MinPrice` | IntegerAttribute | - | - | - |
| `ExpirationDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator, EcoATM_PWSMDM.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder, EcoATM_PWSMDM.SalesOps, EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Brand
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Brand` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Condition
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Condition` | StringAttribute | 200 | - | - |
| `Rank` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Note
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Notes` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator, EcoATM_PWSMDM.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder, EcoATM_PWSMDM.SalesOps, EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 DeviceTemp
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |
| `DeviceCode` | LongAttribute | - | 0 | - |
| `DeviceDescription` | StringAttribute | 200 | - | - |
| `DeviceCarrier` | StringAttribute | 200 | - | - |
| `AvailableQty` | IntegerAttribute | - | 0 | - |
| `SearchAttr` | StringAttribute | 500 | - | - |
| `DeviceColor` | StringAttribute | 200 | - | - |
| `DeviceCapacity` | StringAttribute | 200 | - | - |
| `DeviceModel` | StringAttribute | 200 | - | - |
| `DeviceCondition` | StringAttribute | 200 | - | - |
| `DeviceCategory` | StringAttribute | 200 | - | - |
| `DeviceBrand` | StringAttribute | 200 | - | - |
| `DeviceGrade` | StringAttribute | 200 | - | - |
| `isNew` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator, EcoATM_PWSMDM.SalesLeader, EcoATM_PWSMDM.SalesOps, EcoATM_PWSMDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferID
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MaxOfferID` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator, EcoATM_PWSMDM.SalesLeader, EcoATM_PWSMDM.SalesOps, EcoATM_PWSMDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |

### 📦 PropertiesUtility
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UpdateModel` | BooleanAttribute | - | false | - |
| `UpdateBrand` | BooleanAttribute | - | false | - |
| `UpdateCategory` | BooleanAttribute | - | false | - |
| `UpdateGrade` | BooleanAttribute | - | false | - |
| `UpdateColor` | BooleanAttribute | - | false | - |
| `UpdateCapacity` | BooleanAttribute | - | false | - |
| `UpdateCarrier` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CaseLot
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CaseLotID` | StringAttribute | 200 | - | - |
| `CaseLotSize` | IntegerAttribute | - | 0 | - |
| `CaseLotPrice` | IntegerAttribute | - | 0 | - |
| `CaseLotAvlQty` | IntegerAttribute | - | 0 | - |
| `CaseLotReservedQty` | IntegerAttribute | - | 0 | - |
| `CaseLotATPQty` | IntegerAttribute | - | 0 | - |
| `CreatedBy` | StringAttribute | 200 | - | - |
| `UpdatedBy` | StringAttribute | 200 | - | - |
| `IsActive` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.Bidder, EcoATM_PWSMDM.SalesLeader, EcoATM_PWSMDM.SalesOps, EcoATM_PWSMDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWSMDM.SalesOps, EcoATM_PWSMDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWSMDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |

### 📦 DeviceSFTemp
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |
| `AvlQty` | IntegerAttribute | - | 0 | - |
| `ATPQty` | IntegerAttribute | - | 0 | - |
| `ReservedQty` | IntegerAttribute | - | 0 | - |

### 📦 InventoryExport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `Capacity` | StringAttribute | 200 | - | - |
| `Color` | StringAttribute | 200 | - | - |
| `Carrier` | StringAttribute | 200 | - | - |
| `Grade` | StringAttribute | 200 | - | - |
| `Category` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `AvailableQty` | IntegerAttribute | - | 0 | - |
| `CurrentListPrice` | DecimalAttribute | - | - | - |
| `ItemType` | StringAttribute | 200 | - | - |
| `ATPQty` | IntegerAttribute | - | 0 | - |
| `ReservedQty` | IntegerAttribute | - | 0 | - |
| `CaseLotSize` | IntegerAttribute | - | 0 | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `Device_Model` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 47531b26-d365-45d2-97f8-adce31843de5 | Reference | Default | DeleteMeButKeepReferences |
| `Device_Capacity` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 02376c02-fe32-463f-9840-e8f15622f4f1 | Reference | Default | DeleteMeButKeepReferences |
| `Device_Color` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | bd8e2758-0759-4d7e-94d8-f4415d8510ca | Reference | Default | DeleteMeButKeepReferences |
| `Device_Carrier` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 6638df84-192a-45a9-b54c-36e28b100282 | Reference | Default | DeleteMeButKeepReferences |
| `Device_Grade` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 41e8dda9-c1a6-41f7-b967-627338aca8c5 | Reference | Default | DeleteMeButKeepReferences |
| `Device_Category` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 09aa63bb-0717-4bc4-8242-ac068debffeb | Reference | Default | DeleteMeButKeepReferences |
| `PriceHistory_DeviceList` | c5e2ba55-6bf2-4e58-805b-b4fe8e00aeb1 | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | Reference | Default | DeleteMeButKeepReferences |
| `Device_Brand` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 4146b657-12c0-47a4-a0d5-d00be046ddce | Reference | Default | DeleteMeButKeepReferences |
| `Device_Condition` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 5e4c1016-e766-4bbe-97d7-372320c2a87b | Reference | Default | DeleteMeButKeepReferences |
| `Device_Note` | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | 927c9271-2406-47d4-b7ca-9f44af708a7a | Reference | Default | DeleteMeAndReferences |
| `PropertiesUtility_Category_New` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 09aa63bb-0717-4bc4-8242-ac068debffeb | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Grade_Existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 41e8dda9-c1a6-41f7-b967-627338aca8c5 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Carrier_Existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 6638df84-192a-45a9-b54c-36e28b100282 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Color_Existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | bd8e2758-0759-4d7e-94d8-f4415d8510ca | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Capacity_Existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 02376c02-fe32-463f-9840-e8f15622f4f1 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Capacity_New` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 02376c02-fe32-463f-9840-e8f15622f4f1 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Color_New` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | bd8e2758-0759-4d7e-94d8-f4415d8510ca | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Carrier_new` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 6638df84-192a-45a9-b54c-36e28b100282 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Grade_New` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 41e8dda9-c1a6-41f7-b967-627338aca8c5 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Category_existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 09aa63bb-0717-4bc4-8242-ac068debffeb | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Brand_Existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 4146b657-12c0-47a4-a0d5-d00be046ddce | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Brand_New` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 4146b657-12c0-47a4-a0d5-d00be046ddce | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Model_Existing` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 47531b26-d365-45d2-97f8-adce31843de5 | Reference | Default | DeleteMeButKeepReferences |
| `PropertiesUtility_Model_New` | 4ad4d184-5e31-4f5d-9928-dcc8b74fbec5 | 47531b26-d365-45d2-97f8-adce31843de5 | Reference | Default | DeleteMeButKeepReferences |
| `CaseLot_Device` | 5d1d63de-7038-4d22-80ab-af139810a52d | 3c8b7c45-5713-4b40-81e0-ec9b18653252 | Reference | Default | DeleteMeButKeepReferences |
