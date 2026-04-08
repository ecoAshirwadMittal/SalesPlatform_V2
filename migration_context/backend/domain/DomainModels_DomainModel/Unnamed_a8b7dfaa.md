# Domain Model

## Entities

### 📦 BuyerBidDetailReportHelperOb
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CurrentAuctionName` | StringAttribute | 200 | - | - |
| `PreviousAuctionName` | StringAttribute | 200 | - | - |
| `LotsBid1` | IntegerAttribute | - | 0 | - |
| `LotsBid2` | IntegerAttribute | - | 0 | - |
| `UnitsBid1` | IntegerAttribute | - | 0 | - |
| `UnitsBid2` | IntegerAttribute | - | 0 | - |
| `UpOrDownLots` | StringAttribute | 200 | - | - |
| `UpOrDownBids` | StringAttribute | 200 | - | - |
| `ShowTotals` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps, EcoATM_Reports.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerBidDetailReport_NP
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | IntegerAttribute | - | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `ModelName` | StringAttribute | 200 | - | - |
| `Grade` | StringAttribute | 200 | - | - |
| `Quantity1` | IntegerAttribute | - | - | - |
| `Bid1` | DecimalAttribute | - | - | - |
| `Quantity2` | IntegerAttribute | - | - | - |
| `Bid2` | DecimalAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps, EcoATM_Reports.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerAwardSummaryTotals
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SalesQty` | IntegerAttribute | - | 0 | - |
| `Amount` | DecimalAttribute | - | 0 | - |
| `WeeklyBudget` | DecimalAttribute | - | 0 | - |
| `PreviousWeekSalesQty` | IntegerAttribute | - | 0 | - |
| `PreviousWeekAmount` | DecimalAttribute | - | 0 | - |
| `PreviousWeekWeeklyBudget` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerBidSummaryReportHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CurrentAuctionName` | StringAttribute | 1000 | - | - |
| `PreviousAuctionName` | StringAttribute | 1000 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Reports.ecoAtmDirectAdmin | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Reports.SalesLeader | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_Reports.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Reports.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerBidSummaryReport_NP
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LotsBid1` | IntegerAttribute | - | 0 | - |
| `UnitsBid1` | IntegerAttribute | - | 0 | - |
| `LotsBid2` | IntegerAttribute | - | 0 | - |
| `UnitsBid2` | IntegerAttribute | - | 0 | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `BuyerName` | StringAttribute | 200 | - | - |
| `UpOrDownLots` | StringAttribute | 200 | - | - |
| `UpOrDownUnits` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_Reports.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_Reports.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_Reports.ecoAtmDirectAdmin | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_Reports.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 CohortMapping
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CurrentEcoID` | IntegerAttribute | - | - | - |
| `CurrentModelName` | StringAttribute | 2000 | - | - |
| `CohortEcoID` | IntegerAttribute | - | - | - |
| `CohortModelName` | StringAttribute | 2000 | - | - |
| `AvgSellingPrice` | DecimalAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_Reports.ecoAtmDirectAdmin, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 CohortMappingDoc
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EBCalibrationQueryHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DisplayReport` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CohortMappingFileImport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Current_EcoID` | DecimalAttribute | - | 0 | - |
| `Current_Model_Name` | StringAttribute | - | - | - |
| `Cohort_EcoID` | DecimalAttribute | - | 0 | - |
| `Cohort_Model_Name` | StringAttribute | - | - | - |
| `ASP` | DecimalAttribute | - | 0 | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `BuyerBidDetailReportHelperOb_BuyerBidDetailReport_NP` | adef9290-5ce7-407d-ad88-46d401d697a8 | 687ae107-0b36-442b-9f15-a48ddb809dde | ReferenceSet | Both | DeleteMeButKeepReferences |
| `BuyerBidSummaryReport_NP_BuyerBidSummaryReportHelper` | cd55856c-c100-4dc7-a2dc-ff3cdeaf329f | 08d44bdd-e5c0-4dec-a3b5-4cc675cb5d5b | ReferenceSet | Both | DeleteMeButKeepReferences |
