# Domain Model

## Entities

### 📦 AuctionsFeature
> This entity is used to hold config settings for the Buyer Code submit process.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CreateExcelBidExport` | BooleanAttribute | - | true | - |
| `SendAuctionDataToSnowflake` | BooleanAttribute | - | true | - |
| `SendBuyerToSnowflake` | BooleanAttribute | - | true | - |
| `SendBidDataToSnowflake` | BooleanAttribute | - | true | - |
| `AuctionRound2MinutesOffset` | IntegerAttribute | - | 360 | - |
| `AuctionRound3MinutesOffset` | IntegerAttribute | - | 360 | - |
| `GenerateRound3Files` | BooleanAttribute | - | true | - |
| `CalculateRound2BuyerParticipation` | BooleanAttribute | - | true | - |
| `SPRetryCount` | IntegerAttribute | - | 3 | - |
| `SendFilesToSharepointOnSubmit` | BooleanAttribute | - | true | - |
| `Round2CriteriaActive` | BooleanAttribute | - | true | - |
| `RequireWholesaleUserAgreement` | BooleanAttribute | - | false | - |
| `MinimumAllowedBid` | DecimalAttribute | - | 0 | - |
| `SendBidRankingToSnowFlake` | BooleanAttribute | - | false | - |
| `LegacyRoundThree` | BooleanAttribute | - | false | - |
| `LegacyTargetPrice` | BooleanAttribute | - | false | - |
| `LegacyBidDataCreation` | BooleanAttribute | - | true | - |
| `LegacyManualQualification` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 Buyer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SubmissionID` | AutoNumberAttribute | - | 1 | - |
| `CompanyName` | StringAttribute | 200 | - | - |
| `Status` | Enum(`AuctionUI.enum_BuyerStatus`) | - | - | - |
| `BuyerCodesDisplay` | StringAttribute | 1000 | - | - |
| `isFailedBuyerDisable` | BooleanAttribute | - | false | - |
| `EntityOwner` | StringAttribute | 200 | - | - |
| `EntityChanger` | StringAttribute | 200 | - | - |
| `isSpecialBuyer` | BooleanAttribute | - | false | - |
| `BuyerEmptyValidationMessage` | StringAttribute | 200 | - | - |
| `BuyerUniqueValidationMessage` | StringAttribute | 200 | - | - |
| `buyerCodeInvalidMessage_empty` | StringAttribute | 200 | - | - |
| `buyerCodeInvalidMessage_Unique` | StringAttribute | 200 | - | - |
| `buyerCodeTypeInvalidMessage` | StringAttribute | 200 | - | - |
| `BuyerCodesEmptyValidationMessage` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_BuyerManagement.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.Compliance | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerCode
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BuyerCodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |
| `Code` | StringAttribute | 200 | - | - |
| `Status` | Enum(`AuctionUI.enum_BuyerCodeStatus`) | - | - | - |
| `SubmissionID` | AutoNumberAttribute | - | 1 | - |
| `Budget` | IntegerAttribute | - | - | - |
| `EntityOwner` | StringAttribute | 200 | - | - |
| `EntityChanger` | StringAttribute | 200 | - | - |
| `softDelete` | BooleanAttribute | - | false | - |
| `codeEmptyValid` | BooleanAttribute | - | true | - |
| `codeUniqueValid` | BooleanAttribute | - | true | - |
| `typevalid` | BooleanAttribute | - | true | - |
| `ShowSubmitOfferBtn` | BooleanAttribute | - | true | - |

#### Indexes

- **Index**: `47da39b3-f3ce-4791-8fa0-9e831ca5fe61` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_BuyerManagement.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.Compliance | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerCode_Helper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Code` | StringAttribute | 200 | - | - |
| `BuyerCodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |
| `Status` | Enum(`AuctionUI.enum_BuyerCodeStatus`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerCodeSelect_Helper
> Created for the buyer select screen

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompanyName` | StringAttribute | 200 | - | - |
| `Code` | StringAttribute | 200 | - | - |
| `isUpsellRound` | BooleanAttribute | - | false | - |
| `Note` | StringAttribute | 2000 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 NewBuyerHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompanyName` | StringAttribute | 200 | - | - |
| `Code_DW` | StringAttribute | 200 | - | - |
| `Code_PO` | StringAttribute | 200 | - | - |
| `Code_WH` | StringAttribute | 200 | - | - |
| `SourcePage` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 NewBuyerCodeHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Code` | StringAttribute | 200 | - | - |
| `BuyerCodeType` | Enum(`AuctionUI.enum_BuyerCodeType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerCodeSelectSearchHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SelectedCode` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.SalesLeader | ✅ | ✅ | ✅ | ❌ | - |

### 📦 NP_BuyerCodeSelect_Helper
> Created for the buyer select screen

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompanyName` | StringAttribute | 200 | - | - |
| `Code` | StringAttribute | 200 | - | - |
| `isUpsellRound` | BooleanAttribute | - | false | - |
| `Note` | StringAttribute | 200 | - | - |
| `comboBoxSearchHelper` | StringAttribute | 200 | - | - |
| `IsSelected` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Anonymous, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.ecoAtmDirectAdmin, EcoATM_BuyerManagement.Executive, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep, EcoATM_BuyerManagement.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Parent_NPBuyerCodeSelectHelper
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Anonymous, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.ecoAtmDirectAdmin, EcoATM_BuyerManagement.Executive, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep, EcoATM_BuyerManagement.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 SalesRepresentative
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SalesRepresentativeId` | AutoNumberAttribute | - | 1 | 20250729 BORNPA - Add ID to be able to synchronize |
| `SalesRepFirstName` | StringAttribute | 200 | - | - |
| `SalesRepLastName` | StringAttribute | 200 | - | - |
| `Active` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.ecoAtmDirectAdmin, EcoATM_BuyerManagement.Executive, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep, EcoATM_BuyerManagement.User | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_BuyerManagement.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_BuyerManagement.Compliance | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerCodeChangeLog
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OldBuyerCodeType` | StringAttribute | 200 | - | - |
| `NewBuyerCodeType` | StringAttribute | 200 | - | - |
| `EditedBy` | StringAttribute | 200 | - | - |
| `EditedOn` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Compliance | ✅ | ✅ | ✅ | ✅ | - |

### 📦 QualifiedBuyerCodes
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Qualificationtype` | Enum(`EcoATM_BuyerManagement.enum_BuyerCodeQualificationType`) | - | - | - |
| `Included` | BooleanAttribute | - | false | - |
| `Submitted` | BooleanAttribute | - | false | - |
| `SubmittedDateTime` | DateTimeAttribute | - | - | - |
| `OpenedDashboard` | BooleanAttribute | - | false | - |
| `OpenedDashboardDateTime` | DateTimeAttribute | - | - | - |
| `isSpecialTreatment` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 QualifiedBuyerCodesQueryHelper
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 DG2_UiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IsBidDataLoaded` | BooleanAttribute | - | false | - |
| `DisplayBidRankColumn` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 BidSubmitConfirmationHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BidsBelowMinimumBid` | IntegerAttribute | - | 0 | - |
| `MinBidAmount` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerCode_SessionAndTabHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TabId` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep | ✅ | ✅ | ✅ | ✅ | `[System.owner='[%CurrentUser%]']` |

### 📦 Round3NoSalesRepHelper
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerCodeQueryHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Code` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_BuyerManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `BuyerCode_Buyer` | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | c5ada766-d71c-4373-81cf-f88bcd93b560 | Reference | Default | DeleteMeButKeepReferences |
| `BuyerCode_Helper_BuyerCode` | 75edc1e2-2d28-4af8-b47e-4a732272dcc7 | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `BuyerCodeSelect_Helper_BuyerCode` | 35ea9293-c549-4f04-af61-a7f278a264c4 | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | Reference | Default | DeleteMeButKeepReferences |
| `NewBuyerCodeHelper_NewBuyerHelper` | b35f1421-8253-4e7e-875a-e4e59defdede | 7f636aa1-9f5c-46fa-92d6-44dfca568101 | Reference | Default | DeleteMeButKeepReferences |
| `BuyerCodeSelectSearchHelper_BuyerCode` | 6653bca2-b7fc-48e6-ac42-ea2bade9243d | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | Reference | Default | DeleteMeButKeepReferences |
| `NP_BuyerCodeSelect_Helper_BuyerCode` | 27d0c5ac-9b51-4daf-bd08-92a6f4560884 | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | Reference | Default | DeleteMeButKeepReferences |
| `NP_BuyerCodeSelect_Helper_BuyerCodeSelectSearch_Helper` | 27d0c5ac-9b51-4daf-bd08-92a6f4560884 | 6653bca2-b7fc-48e6-ac42-ea2bade9243d | Reference | Both | DeleteMeButKeepReferences |
| `NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper` | 27d0c5ac-9b51-4daf-bd08-92a6f4560884 | 5ebd8f88-6974-4f5e-929a-f6534222b73f | Reference | Default | DeleteMeButKeepReferences |
| `Buyer_SalesRepresentative` | c5ada766-d71c-4373-81cf-f88bcd93b560 | e83a5438-df76-49e5-bb61-a3e25f1510f9 | Reference | Default | DeleteMeButKeepReferences |
| `BuyerCodeChangeLog_BuyerCode` | 26bc1a1c-9606-4311-b399-07b45981e3a9 | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | Reference | Default | DeleteMeButKeepReferences |
| `QualifiedBuyerCodes_BuyerCode` | a03443fa-17db-4e7b-bb8d-028d9b2ad13a | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | Reference | Default | DeleteMeButKeepReferences |
| `BuyerCode_SessionAndTabHelper_BuyerCode` | dcdce696-57ba-4550-906c-8dbde3708353 | d16c40b8-9483-4e54-8e2e-27f32f6d5cb7 | Reference | Default | DeleteMeButKeepReferences |
| `Buyer_Round3NoSalesRepHelper` | c5ada766-d71c-4373-81cf-f88bcd93b560 | 8344f7cd-ae34-448b-9e4d-2e25e13f5c50 | Reference | Default | DeleteMeButKeepReferences |
