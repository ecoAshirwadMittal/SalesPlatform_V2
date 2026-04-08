# Domain Model

## Entities

### 📦 ImportFile
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ErrorMessage` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Custom_Excel_Import.Adminstrator, Custom_Excel_Import.Bidder, Custom_Excel_Import.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidDataExport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | StringAttribute | - | - | - |
| `Brand` | StringAttribute | - | - | - |
| `Model` | StringAttribute | - | - | - |
| `Model_Name` | StringAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `Carrier` | StringAttribute | - | - | - |
| `Added` | StringAttribute | - | - | - |
| `Avail_Qty` | StringAttribute | - | - | - |
| `Target_Price` | StringAttribute | - | - | - |
| `Price` | StringAttribute | - | - | - |
| `Qty_Cap` | StringAttribute | - | - | - |

### 📦 BidDataQuantityHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Eco_ID` | IntegerAttribute | - | 0 | - |
| `Grade` | StringAttribute | - | - | - |
| `Data_Wipe_Quantity` | DecimalAttribute | - | 0 | - |
| `Additional_non_DW_Quantity` | DecimalAttribute | - | 0 | - |

### 📦 BidDataRankRound2Export
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | StringAttribute | - | - | - |
| `Brand` | StringAttribute | - | - | - |
| `Model` | StringAttribute | - | - | - |
| `Model_Name` | StringAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `Carrier` | StringAttribute | - | - | - |
| `Added` | StringAttribute | - | - | - |
| `Avail_Qty` | StringAttribute | - | - | - |
| `Target_Price` | StringAttribute | - | - | - |
| `Price` | StringAttribute | - | - | - |
| `Qty_Cap` | StringAttribute | - | - | - |
| `Rank` | StringAttribute | - | - | - |

### 📦 BidDataRankRound3Export
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | StringAttribute | - | - | - |
| `Brand` | StringAttribute | - | - | - |
| `Model` | StringAttribute | - | - | - |
| `Model_Name` | StringAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `Carrier` | StringAttribute | - | - | - |
| `Added` | StringAttribute | - | - | - |
| `Avail_Qty` | StringAttribute | - | - | - |
| `Target_Price` | StringAttribute | - | - | - |
| `Price` | StringAttribute | - | - | - |
| `Qty_Cap` | StringAttribute | - | - | - |
| `Rank` | StringAttribute | - | - | - |

