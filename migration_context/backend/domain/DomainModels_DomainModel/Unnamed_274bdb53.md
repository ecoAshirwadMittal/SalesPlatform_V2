# Domain Model

## Entities

### 📦 MasterDeviceInventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ECOATM_CODE` | LongAttribute | - | 0 | - |
| `DEVICE_ID` | StringAttribute | 100 | - | - |
| `NAME` | StringAttribute | 512 | - | - |
| `DEVICE_BRAND` | StringAttribute | 255 | - | - |
| `DEVICE_CATEGORY` | StringAttribute | 255 | - | - |
| `DEVICE_FAMILY` | StringAttribute | 255 | - | - |
| `DEVICE_CARRIER` | StringAttribute | 255 | - | - |
| `CARRIER_DISPLAY_NAME` | StringAttribute | 255 | - | - |
| `CATEGORY_DISPLAY_NAME` | StringAttribute | 100 | - | - |
| `DEVICE_MODEL` | StringAttribute | 30 | - | - |
| `DESCRIPTION` | StringAttribute | 240 | - | - |
| `RELEASE_DATE` | DateTimeAttribute | - | - | - |
| `DB_CREATE_DATE` | DateTimeAttribute | - | - | - |
| `DB_UPDATE_DATE` | DateTimeAttribute | - | - | - |
| `CREATED_AT` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.Anonymous, EcoATM_MDM.Bidder, EcoATM_MDM.ecoAtmDirectAdmin, EcoATM_MDM.Executive, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 UserHelperGuide
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Active` | BooleanAttribute | - | false | - |
| `Version` | StringAttribute | 200 | - | - |
| `GuideType` | Enum(`AuctionUI.ENUM_DocumentType`) | - | Auctions | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.ecoAtmDirectAdmin, EcoATM_MDM.Executive, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Anonymous, EcoATM_MDM.Bidder, EcoATM_MDM.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Week
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `WeekID` | AutoNumberAttribute | - | 1 | - |
| `Year` | IntegerAttribute | - | 0 | - |
| `WeekNumber` | IntegerAttribute | - | 0 | - |
| `WeekStartDateTime` | DateTimeAttribute | - | - | - |
| `WeekEndDateTime` | DateTimeAttribute | - | - | - |
| `WeekDisplay` | StringAttribute | 200 | - | - |
| `WeekDisplayShort` | StringAttribute | 200 | - | - |
| `WeekNumberString` | StringAttribute | 200 | - | - |
| `AuctionDataPurged` | BooleanAttribute | - | false | - |

#### Indexes

- **Index**: `8fb835a0-fafb-4ab8-9e46-bdb06cd8848b` (Ascending)
- **Index**: `73259db6-98d9-4f79-8df0-1645335592e7` (Ascending)
- **Index**: `40a047ec-e3c3-46d6-955c-7d21b7697423` (Ascending)
- **Index**: `3e615e7b-3456-4f35-80d7-7f7d9670e491` (Ascending)
- **Index**: `68835f5d-1084-480f-819e-e3dfa051681d` (Ascending)
- **Index**: `9e680208-c5e5-49d3-9a89-dbbcf1b7e670` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CompanyHoliday
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HolidayDate` | DateTimeAttribute | - | - | - |
| `HolidayDescription` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Model
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Model` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |
| `BuyercodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Bidder, EcoATM_MDM.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Brand
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Brand` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |
| `BuyercodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Bidder, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ModelName
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ModelName` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |
| `BuyercodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Carrier
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Carrier` | StringAttribute | 200 | - | - |
| `DisplayName` | StringAttribute | 200 | - | - |
| `IsEnabledForFilter` | BooleanAttribute | - | false | - |
| `Rank` | IntegerAttribute | - | 0 | - |
| `BuyercodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_MDM.Administrator, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_MDM.Bidder | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BrandHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Brand` | StringAttribute | 200 | - | - |

### 📦 ModelHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Model` | StringAttribute | 200 | - | - |

### 📦 ModelNameHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ModelName` | StringAttribute | 200 | - | - |

### 📦 CarrierHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Carrier` | StringAttribute | 200 | - | - |

