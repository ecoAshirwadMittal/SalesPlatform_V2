# Domain Model

## Entities

### 📦 SchedulingAuction
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Auction_Week_Year` | StringAttribute | 200 | - | - |
| `Round` | IntegerAttribute | - | 0 | - |
| `Name` | StringAttribute | 200 | - | - |
| `Start_DateTime` | DateTimeAttribute | - | - | - |
| `End_DateTime` | DateTimeAttribute | - | - | - |
| `isStartNotificationSent` | BooleanAttribute | - | false | - |
| `isEndNotificationSent` | BooleanAttribute | - | false | - |
| `isReminderNotificationSent` | BooleanAttribute | - | false | - |
| `RoundStatus` | Enum(`AuctionUI.enum_SchedulingAuctionStatus`) | - | - | - |
| `HasRound` | BooleanAttribute | - | true | - |
| `EmailReminders` | Enum(`AuctionUI.ENUM_ReminderEmails`) | - | NoneSent | - |
| `CreatedBy` | StringAttribute | 200 | - | - |
| `UpdatedBy` | StringAttribute | 200 | - | - |
| `NotificationsEnabled` | BooleanAttribute | - | true | - |
| `Round3InitStatus` | Enum(`AuctionUI.Enum_ScheduleAuctionInitStatus`) | - | Pending | - |
| `SnowflakeJson` | StringAttribute | - | - | - |

#### Indexes

- **Index**: `9803f1fa-b6ef-436a-91aa-3861c34b49d2` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesLeader, AuctionUI.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Auction
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `AuctionTitle` | StringAttribute | 200 | - | - |
| `AuctionStatus` | Enum(`AuctionUI.enum_SchedulingAuctionStatus`) | - | - | - |
| `CreatedBy` | StringAttribute | 200 | - | - |
| `UpdatedBy` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder, AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesLeader, AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SchedulingAuction_Helper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Auction_Week_Year` | StringAttribute | 200 | - | - |
| `Round1_Start_DateTime` | DateTimeAttribute | - | - | - |
| `Round1_End_DateTime` | DateTimeAttribute | - | - | - |
| `Round1_Status` | Enum(`AuctionUI.enum_SchedulingAuctionStatus`) | - | - | - |
| `Round2_Start_DateTime` | DateTimeAttribute | - | - | - |
| `Round2_End_DateTime` | DateTimeAttribute | - | - | - |
| `isRound2Active` | Enum(`AuctionUI.enum_SchedulingAuctionStatus`) | - | - | - |
| `Round2_isActive` | BooleanAttribute | - | true | - |
| `Round3_Start_DateTime` | DateTimeAttribute | - | - | - |
| `Round3_End_Datetime` | DateTimeAttribute | - | - | - |
| `Round3_Status` | Enum(`AuctionUI.enum_SchedulingAuctionStatus`) | - | - | - |
| `isRound3Active` | BooleanAttribute | - | true | - |
| `Auction_Name` | StringAttribute | 200 | - | - |
| `Name_Unique` | BooleanAttribute | - | true | - |
| `Buyers` | StringAttribute | 200 | All | - |
| `BuyersTotal` | IntegerAttribute | - | 0 | - |
| `BuyersDWOnly` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BidData_Helper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EcoID` | IntegerAttribute | - | 0 | - |
| `BidQuantity` | IntegerAttribute | - | 0 | - |
| `BidAmount` | DecimalAttribute | - | 0 | - |
| `TargetPrice` | DecimalAttribute | - | 0 | - |
| `MaximumQuantity` | IntegerAttribute | - | 0 | - |
| `ecoGrade` | StringAttribute | 200 | - | - |
| `Code` | StringAttribute | 200 | - | - |
| `WeekId` | IntegerAttribute | - | 0 | - |
| `CompanyName` | StringAttribute | 200 | - | - |
| `User` | StringAttribute | 200 | - | - |
| `BuyerCodeType` | StringAttribute | 200 | - | - |
| `Data_Wipe_Quantity` | LongAttribute | - | 0 | - |
| `BidRound` | IntegerAttribute | - | 0 | The current bid round for this auction |
| `PreviousRoundBidQuantity` | IntegerAttribute | - | 0 | - |
| `PreviousRoundBidAmount` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidRound
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BidRoundId` | AutoNumberAttribute | - | 1 | - |
| `Submitted` | BooleanAttribute | - | false | - |
| `SubmittedDatatime` | DateTimeAttribute | - | - | - |
| `Note` | StringAttribute | 2000 | - | Note left by sales rep to the buyercode bidder |
| `UploadedToSharepoint` | BooleanAttribute | - | false | - |
| `UploadToSharepointDateTime` | DateTimeAttribute | - | - | - |
| `IsDeprecated` | BooleanAttribute | - | false | Indicator used by the purge activity. |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesLeader, AuctionUI.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EmailNotification
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `PageURL` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 200 | - | - |
| `Auction` | StringAttribute | 200 | - | - |
| `Round` | IntegerAttribute | - | 0 | - |
| `RefresherURL` | StringAttribute | 200 | - | - |
| `DateTime` | StringAttribute | 200 | - | - |
| `ConcatBuyerCodes` | StringAttribute | 200 | - | - |
| `BidSheet_Url` | StringAttribute | - | - | - |
| `Note` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidDataDoc
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ReminderNotificationHelper
> Used to create a list of emails to sent an auction reminder notification to

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Email` | StringAttribute | 200 | - | - |
| `FirstName` | StringAttribute | 200 | - | - |

### 📦 BidderRouterHelper
> This is used as a helper to route a bidder user

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Round1Status` | StringAttribute | 200 | - | - |
| `Round2Status` | StringAttribute | 200 | - | - |
| `Round1BidSubmitted` | BooleanAttribute | - | false | - |
| `Round2BidSubmitted` | BooleanAttribute | - | false | - |
| `CurrYearWeek` | StringAttribute | 200 | - | - |
| `CurrentRound` | StringAttribute | 200 | - | - |
| `Round3Status` | StringAttribute | 200 | - | - |
| `Round3BidSubmitted` | BooleanAttribute | - | false | - |
| `R2isActive` | BooleanAttribute | - | false | - |
| `R3isActive` | BooleanAttribute | - | false | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `isSpecialTreatmentBuyer` | BooleanAttribute | - | false | - |
| `Round1BidCount` | IntegerAttribute | - | 0 | - |
| `Round2BidCount` | IntegerAttribute | - | 0 | - |
| `Round3BidCount` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesLeader, AuctionUI.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 AllBidDownload
> Created as a helper for processing all bids by buyer code after each round

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ecoATMCode` | IntegerAttribute | - | 0 | - |
| `Category` | StringAttribute | 200 | - | - |
| `DeviceId` | StringAttribute | 200 | - | - |
| `BrandName` | StringAttribute | 200 | - | - |
| `PartNumber` | StringAttribute | 200 | - | - |
| `PartName` | StringAttribute | 200 | - | - |
| `Added` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `A_YYY` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeA_YYY` | StringAttribute | 200 | $0.00 | - |
| `A_YYYEstimatedQuantity` | DecimalAttribute | - | 0 | - |
| `A_YYYQuantityCap` | IntegerAttribute | - | - | - |
| `B_NYY_D_NNY` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeB_NYY_D_NNY` | StringAttribute | 200 | $0.00 | - |
| `B_NYY_D_NNYEstimatedQuantity` | DecimalAttribute | - | 0 | - |
| `B_NYY_D_NNYQuantityCap` | IntegerAttribute | - | - | - |
| `C_YNY_G_YNN` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeC_YNY_G_YNN` | StringAttribute | 200 | $0.00 | - |
| `C_YNY_G_YNNEstimatedQuantity` | DecimalAttribute | - | 0 | - |
| `C_YNY_G_YNNQuantityCap` | IntegerAttribute | - | - | - |
| `E_YYN` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeE_YYN` | StringAttribute | 200 | $0.00 | - |
| `E_YYNEstimatedQuantity` | DecimalAttribute | - | 0 | - |
| `E_YYNQuantityCap` | IntegerAttribute | - | - | - |
| `F_NYN_H_NNN` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeF_NYN_H_NNN` | StringAttribute | 200 | $0.00 | - |
| `F_NYN_H_NNNEstimatedQuantity` | DecimalAttribute | - | 0 | - |
| `F_NYN_H_NNNQuantityCap` | IntegerAttribute | - | - | - |
| `Auction_Week_Year` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AllBidsDoc
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AllBidsZipTempList
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AllBidsZipped
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AllBidDownload_ScreenHelper
> Added as an indicator for when zip files are generated on the report overview screen

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `R1Caption` | StringAttribute | 200 | - | - |
| `R2Caption` | StringAttribute | 200 | - | - |
| `UpsellCaption` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder, AuctionUI.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BidRoundSelectionFilter
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SubmissionID` | AutoNumberAttribute | - | 1 | - |
| `Round` | IntegerAttribute | - | 0 | - |
| `TargetPercent` | DecimalAttribute | - | 0 | - |
| `TargetValue` | IntegerAttribute | - | 0 | - |
| `TotalValueFloor` | IntegerAttribute | - | 0 | - |
| `MergedGrade1` | StringAttribute | 200 | - | - |
| `MergedGrade2` | StringAttribute | 200 | - | - |
| `MergedGrade3` | StringAttribute | 200 | - | - |
| `RegularBuyerQualification` | Enum(`EcoATM_BuyerManagement.Enum_RegularBuyerQualification`) | - | - | - |
| `RegularBuyerInventoryOptions` | Enum(`EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption`) | - | - | - |
| `STBAllowAllBuyersOverride` | BooleanAttribute | - | false | Allows all special treatment buyers to participate |
| `STBIncludeAllInventory` | BooleanAttribute | - | false | - |
| `BidPercentageVariation` | IntegerAttribute | - | - | - |
| `BidAmountVariation` | IntegerAttribute | - | - | - |
| `RankQualificationLimit` | IntegerAttribute | - | - | - |
| `R3ErrorMessage` | StringAttribute | 500 | - | - |
| `R2InventoryErrorMessage` | StringAttribute | 500 | - | - |
| `R2TargetErrorMessage` | StringAttribute | 500 | - | - |
| `R2TargetFactorErrorMessage` | StringAttribute | 500 | - | - |
| `R3TargetFactorErrorMessage` | StringAttribute | 500 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 AuctionTimerHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Hot1Text` | StringAttribute | 200 | - | - |
| `Hot2Text` | StringAttribute | 200 | - | - |
| `Hot3Text` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AggregatedInventory
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EcoId` | IntegerAttribute | - | 0 | - |
| `MergedGrade` | StringAttribute | 200 | - | - |
| `DataWipe` | StringAttribute | 200 | - | - |
| `AvgTargetPrice` | DecimalAttribute | - | 0 | - |
| `AvgPayout` | DecimalAttribute | - | 0 | - |
| `TotalPayout` | DecimalAttribute | - | 0 | - |
| `TotalQuantity` | IntegerAttribute | - | 0 | - |
| `DWAvgTargetPrice` | DecimalAttribute | - | 0 | - |
| `DWAvgPayout` | DecimalAttribute | - | 0 | - |
| `DWTotalPayout` | DecimalAttribute | - | 0 | - |
| `DWTotalQuantity` | IntegerAttribute | - | 0 | - |
| `Name` | StringAttribute | 200 | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `Carrier` | StringAttribute | 200 | - | - |
| `CreatedAt` | DateTimeAttribute | - | - | - |
| `Category` | StringAttribute | 200 | - | - |
| `DeviceId` | StringAttribute | 200 | - | - |
| `Round1TargetPrice` | DecimalAttribute | - | - | - |
| `Round1TargetPrice_DW` | DecimalAttribute | - | - | - |
| `Round1MaxBid` | DecimalAttribute | - | - | - |
| `Round1MaxBidBuyerCode` | StringAttribute | - | - | - |
| `Round2EBForTarget` | DecimalAttribute | - | - | - |
| `Round2TargetPrice` | DecimalAttribute | - | - | - |
| `R2TargetPriceFactor` | DecimalAttribute | - | - | - |
| `R2TargetPriceFactorType` | Enum(`AuctionUI.ENUM_TargetPriceFactorType`) | - | - | - |
| `Round2MaxBid` | DecimalAttribute | - | - | - |
| `Round2MaxBidBuyerCode` | StringAttribute | - | - | - |
| `Round3EBForTarget` | DecimalAttribute | - | - | - |
| `Round3TargetPrice` | DecimalAttribute | - | - | - |
| `R3TargetPriceFactor` | DecimalAttribute | - | - | - |
| `R3TargetPriceFactorType` | Enum(`AuctionUI.ENUM_TargetPriceFactorType`) | - | - | - |
| `R2POMaxBid` | DecimalAttribute | - | 0 | - |
| `R3POMaxBid` | DecimalAttribute | - | 0 | - |
| `R2POMaxBuyerCode` | StringAttribute | - | - | - |
| `R3POMaxBuyerCode` | StringAttribute | - | - | - |
| `isTotalQuantityModified` | BooleanAttribute | - | false | - |

#### Indexes

- **Index**: `08baa450-b59c-4b98-b56e-13cd78fc27fe` (Ascending)
- **Index**: `bbff0d23-5df7-41e2-9878-f9bda1322723` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesLeader, AuctionUI.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AggreegatedInventoryTotals
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `AvgTargetPrice` | DecimalAttribute | - | 0 | - |
| `AvgPayout` | DecimalAttribute | - | 0 | - |
| `TotalPayout` | DecimalAttribute | - | 0 | - |
| `TotalQuantity` | LongAttribute | - | 0 | - |
| `DWAvgTargetPrice` | DecimalAttribute | - | 0 | - |
| `DWAvgPayout` | DecimalAttribute | - | 0 | - |
| `DWTotalPayout` | DecimalAttribute | - | 0 | - |
| `DWTotalQuantity` | LongAttribute | - | 0 | - |
| `MaxUploadTime` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AggInventoryHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HasAuction` | BooleanAttribute | - | false | - |
| `HasInventory` | BooleanAttribute | - | false | - |
| `HasAuctionBeenTriggered` | BooleanAttribute | - | false | - |
| `WasTitleEmpty` | BooleanAttribute | - | false | - |
| `HasSchedule` | BooleanAttribute | - | false | - |
| `isCurrentWeek` | BooleanAttribute | - | false | - |
| `AuctionName` | StringAttribute | 200 | - | - |
| `isTotalQuantityModified` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidDataTotalQuantityConfig
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EcoID` | IntegerAttribute | - | - | - |
| `Grade` | Enum(`AuctionUI.ENUM_MergedGrades`) | - | - | - |
| `NonDWQuantity` | IntegerAttribute | - | - | - |
| `DataWipeQuantity` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BidDataId` | AutoNumberAttribute | - | 1 | - |
| `BidAmount` | DecimalAttribute | - | 0 | - |
| `BidQuantity` | IntegerAttribute | - | 0 | - |
| `BidRound` | IntegerAttribute | - | 0 | - |
| `BuyerCodeType` | StringAttribute | 200 | - | - |
| `Data_Wipe_Quantity` | IntegerAttribute | - | 0 | - |
| `Code` | StringAttribute | 200 | - | - |
| `CompanyName` | StringAttribute | 200 | - | - |
| `EcoID` | IntegerAttribute | - | 0 | - |
| `MaximumQuantity` | IntegerAttribute | - | 0 | - |
| `Merged_Grade` | StringAttribute | 200 | - | - |
| `PreviousRoundBidAmount` | DecimalAttribute | - | 0 | - |
| `PreviousRoundBidQuantity` | IntegerAttribute | - | 0 | - |
| `TargetPrice` | DecimalAttribute | - | 0 | - |
| `User` | StringAttribute | 200 | - | - |
| `WeekID` | IntegerAttribute | - | 0 | - |
| `Payout` | DecimalAttribute | - | 0 | - |
| `HighestBid` | BooleanAttribute | - | false | - |
| `Margin` | DecimalAttribute | - | 0 | - |
| `SubmitDateTime` | DateTimeAttribute | - | - | Used to identify where a bid is a winning bid if t |
| `SubmittedBidAmount` | DecimalAttribute | - | - | Used to allow buyers to resubmit bids. Keeps lates |
| `SubmittedBidQuantity` | IntegerAttribute | - | - | Used to allow buyers to resubmit bids. Keeps lates |
| `SubmittedDateTime` | DateTimeAttribute | - | - | Used to allow buyers to resubmit bids. Keeps lates |
| `Rejected` | BooleanAttribute | - | false | - |
| `AcceptReason` | StringAttribute | 200 | - | - |
| `RejectReason` | StringAttribute | - | - | - |
| `IsChanged` | BooleanAttribute | - | false | - |
| `TempDABidAmount` | DecimalAttribute | - | 0 | - |
| `MergedGrade` | StringAttribute | - | - | - |
| `LastValidBidAmount` | DecimalAttribute | - | 0 | - |
| `LastValidBidQuantity` | IntegerAttribute | - | 0 | - |
| `Round2BidRank` | IntegerAttribute | - | - | - |
| `DisplayRound2BidRank` | StringAttribute | 200 | - | - |
| `Round3BidRank` | IntegerAttribute | - | - | - |
| `DisplayRound3BidRank` | StringAttribute | 200 | - | - |

#### Indexes

- **Index**: `7b44c9f2-e8ca-4c0c-9a82-e66bccf591d1` (Ascending)
- **Index**: `37d6a8e9-8c04-4898-9982-a45979cf0f1c` (Ascending)
- **Index**: `attr` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder | ✅ | ✅ | ✅ | ✅ | `[AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| AuctionUI.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesLeader, AuctionUI.SalesOps | ✅ | ✅ | ✅ | ✅ | - |

### 📦 TargetPriceFactor
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MinimumValue` | DecimalAttribute | - | 0 | - |
| `MaximumValue` | DecimalAttribute | - | 0 | - |
| `FactorType` | Enum(`AuctionUI.ENUM_TargetPriceFactorType`) | - | Percentage_Factor | - |
| `FactorAmount` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder, AuctionUI.SalesLeader, AuctionUI.SalesOps | ❌ | ✅ | ✅ | ❌ | - |

### 📦 MaxLotBid
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProductId` | IntegerAttribute | - | 0 | - |
| `Grade` | StringAttribute | 200 | - | - |
| `MaxBid` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RoundThreeBuyersDataReport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompanyName` | StringAttribute | 200 | - | - |
| `BuyerCodes` | StringAttribute | - | - | - |
| `SubmittedBy` | StringAttribute | 200 | - | - |
| `SubmittedOn` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.SalesLeader, AuctionUI.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RoundThreeBidDataReport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CodeGrade` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `PartName` | StringAttribute | 200 | - | - |
| `EstimatedUnitCount` | IntegerAttribute | - | 0 | - |
| `BuyerQtyCap` | StringAttribute | 200 | - | - |
| `Bid` | StringAttribute | 200 | - | - |
| `MaxBid` | StringAttribute | 200 | - | - |
| `AcceptMaxBid` | StringAttribute | 200 | - | - |
| `BuyerName` | StringAttribute | 2000 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `BidRank` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RoundThreeBidDataExcelExport
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ErrorMessage` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidUploadPageHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FileName` | StringAttribute | 200 | - | - |
| `AuctionTitle` | StringAttribute | 200 | - | - |
| `ProcessingMessage` | StringAttribute | 200 | Processing | - |
| `ProgressValue` | IntegerAttribute | - | 0 | - |
| `Status` | Enum(`EcoATM_BidData.enum_BidUploadStatus`) | - | - | - |
| `FileUploadMessage` | StringAttribute | 200 | - | - |
| `RowCount` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidData_ImportSettings
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TemplateName` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 RoundThreeBuyersDataReport_NP
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompanyName` | StringAttribute | 200 | - | - |
| `BuyerCodes` | StringAttribute | - | - | - |
| `SubmittedBy` | StringAttribute | 200 | - | - |
| `SubmittedOn` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidDataQueryHelper
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 UiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DateTime` | DateTimeAttribute | - | - | - |
| `String` | StringAttribute | - | - | - |
| `Bool` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder, AuctionUI.Compliance, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidSubmitLog
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SubmittedBy` | StringAttribute | 200 | - | - |
| `SubmitDateTime` | DateTimeAttribute | - | - | - |
| `SubmitAction` | Enum(`AuctionUI.enum_SubmitBidsLogAction`) | - | - | - |
| `RetryCount` | IntegerAttribute | - | - | - |
| `Status` | Enum(`AuctionUI.enum_BidSubmitLogStatus`) | - | - | - |
| `Message` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DataDogTest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RoleList` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.Compliance, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BidDataDeleteHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `StartDate` | DateTimeAttribute | - | - | - |
| `EndDate` | DateTimeAttribute | - | - | - |
| `ScriptToRun` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CarryOverBidsNP
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FromWeek` | IntegerAttribute | - | 0 | - |
| `FromYear` | IntegerAttribute | - | 0 | - |
| `FromBuyerCode` | StringAttribute | 200 | - | - |
| `ToYear` | IntegerAttribute | - | 0 | - |
| `ToWeek` | IntegerAttribute | - | 0 | - |
| `ToBuyerCode` | StringAttribute | 200 | - | - |
| `OverwriteExisting` | BooleanAttribute | - | false | - |
| `DryRun` | BooleanAttribute | - | false | - |
| `BatchSize` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.Compliance, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidDataTemp
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EcoID` | IntegerAttribute | - | - | - |
| `Merged_Grade` | StringAttribute | 200 | - | - |
| `Quantity` | IntegerAttribute | - | - | - |
| `Amount` | DecimalAttribute | - | - | - |
| `TargetPrice` | DecimalAttribute | - | - | - |
| `MaximumQuantity` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Anonymous, AuctionUI.Bidder, AuctionUI.Compliance, AuctionUI.ecoAtmDirectAdmin, AuctionUI.Executive, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep, AuctionUI.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BidRanking
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DisplayRank` | BooleanAttribute | - | false | - |
| `IncludeEBForRanking` | BooleanAttribute | - | false | - |
| `MinBid` | DecimalAttribute | - | 0.10 | - |
| `MaxRank` | IntegerAttribute | - | 10 | - |
| `StartingRank` | IntegerAttribute | - | 1 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| AuctionUI.Bidder | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerCodeSelectUiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IsWholesaleUser` | BooleanAttribute | - | false | - |
| `IsPWSUser` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 SharePointMethod
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Method` | Enum(`AuctionUI.ENUM_SharepointMethod`) | - | OLD | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 AllBidDownload_2
> Created as a helper for processing all bids by buyer code after each round

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ecoATMCode` | IntegerAttribute | - | 0 | - |
| `Category` | StringAttribute | 200 | - | - |
| `DeviceId` | StringAttribute | 200 | - | - |
| `BrandName` | StringAttribute | 200 | - | - |
| `PartNumber` | StringAttribute | 200 | - | - |
| `PartName` | StringAttribute | 200 | - | - |
| `Added` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `A_YYY` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeA_YYY` | StringAttribute | 200 | $0.00 | - |
| `A_YYYEstimatedQuantity` | IntegerAttribute | - | 0 | - |
| `A_YYYQuantityCap` | IntegerAttribute | - | - | - |
| `B_NYY_D_NNY` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeB_NYY_D_NNY` | StringAttribute | 200 | $0.00 | - |
| `B_NYY_D_NNYEstimatedQuantity` | IntegerAttribute | - | 0 | - |
| `B_NYY_D_NNYQuantityCap` | IntegerAttribute | - | - | - |
| `C_YNY_G_YNN` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeC_YNY_G_YNN` | StringAttribute | 200 | $0.00 | - |
| `C_YNY_G_YNNEstimatedQuantity` | IntegerAttribute | - | 0 | - |
| `C_YNY_G_YNNQuantityCap` | IntegerAttribute | - | - | - |
| `E_YYN` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeE_YYN` | StringAttribute | 200 | $0.00 | - |
| `E_YYNEstimatedQuantity` | IntegerAttribute | - | 0 | - |
| `E_YYNQuantityCap` | IntegerAttribute | - | - | - |
| `F_NYN_H_NNN` | StringAttribute | 200 | $0.00 | - |
| `MAXofGradeF_NYN_H_NNN` | StringAttribute | 200 | $0.00 | - |
| `F_NYN_H_NNNEstimatedQuantity` | IntegerAttribute | - | 0 | - |
| `F_NYN_H_NNNQuantityCap` | IntegerAttribute | - | - | - |
| `Auction_Week_Year` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ZeroBidDownload
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ecoATMCode` | IntegerAttribute | - | - | - |
| `BrandName` | StringAttribute | 200 | - | - |
| `PartName` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `A_YYY` | IntegerAttribute | - | - | - |
| `B_NYY` | IntegerAttribute | - | - | - |
| `C_YNY` | IntegerAttribute | - | - | - |
| `D_NNY` | IntegerAttribute | - | - | - |
| `E_YYN` | IntegerAttribute | - | - | - |
| `F_NYN` | IntegerAttribute | - | - | - |
| `G_YNN` | IntegerAttribute | - | - | - |
| `H_NNN` | IntegerAttribute | - | - | - |
| `Inactive` | StringAttribute | 200 | 'FALSE | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| AuctionUI.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BidDataImport_Round3
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Code_Grade` | StringAttribute | - | - | - |
| `Brand` | StringAttribute | - | - | - |
| `Part_Name` | StringAttribute | - | - | - |
| `Estimated_Unit_Count` | DecimalAttribute | - | 0 | - |
| `Your_Quantity_Cap` | StringAttribute | - | - | - |
| `Bid` | StringAttribute | - | - | - |
| `Max_Bid` | StringAttribute | - | - | - |
| `Accept_Max_Bid_YN` | StringAttribute | - | - | - |
| `Buyer_Name` | StringAttribute | - | - | - |
| `Code` | StringAttribute | - | - | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `SchedulingAuction_Auction` | de6018ad-23e1-4367-a386-0e23c2ed7c00 | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Default | DeleteMeButKeepReferences |
| `SchedulingAuction_Helper_Auction` | 7a355b77-4693-4a33-b7eb-9d057f29e88f | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Default | DeleteMeButKeepReferences |
| `BidData_Helper_BidRound` | 46e7d4af-41b1-41af-a567-d6082b2fc066 | 18d18d40-6cb3-48ef-8aba-44e01235c6ae | Reference | Default | DeleteMeButKeepReferences |
| `BidRound_SchedulingAuction` | 18d18d40-6cb3-48ef-8aba-44e01235c6ae | de6018ad-23e1-4367-a386-0e23c2ed7c00 | Reference | Default | DeleteMeButKeepReferences |
| `AllBidDownload_AllBidsDoc` | 24968a8a-f750-4507-a95a-b6eea1fae758 | 11272d34-f1b5-4f17-b387-7ed73c5188ad | Reference | Default | DeleteMeButKeepReferences |
| `AllBidsDocDW_Auction` | 11272d34-f1b5-4f17-b387-7ed73c5188ad | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Both | DeleteMeButKeepReferences |
| `AllBidsDocAll_Auction` | 11272d34-f1b5-4f17-b387-7ed73c5188ad | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Both | DeleteMeButKeepReferences |
| `BidRound_BidDataDoc` | 18d18d40-6cb3-48ef-8aba-44e01235c6ae | 36d0a0ab-fffe-4cb2-82ba-c290b880fb74 | Reference | Both | DeleteMeButKeepReferences |
| `BidData_Helper_AggregatedInventory` | 46e7d4af-41b1-41af-a567-d6082b2fc066 | 2f06a43c-6b85-4d60-9bf8-b47ae27281b7 | Reference | Default | DeleteMeButKeepReferences |
| `AggInventoryHelper_Auction` | da90b803-2d7c-4d9d-a2d5-33e3c810f591 | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Default | DeleteMeButKeepReferences |
| `SchedulingAuction_Helper_AggInventoryHelper` | 7a355b77-4693-4a33-b7eb-9d057f29e88f | da90b803-2d7c-4d9d-a2d5-33e3c810f591 | Reference | Default | DeleteMeButKeepReferences |
| `BidData_AggregatedInventory` | ce0a95f8-b8a4-4f81-9160-7269e5a86de4 | 2f06a43c-6b85-4d60-9bf8-b47ae27281b7 | Reference | Default | DeleteMeButKeepReferences |
| `BidData_BidRound` | ce0a95f8-b8a4-4f81-9160-7269e5a86de4 | 18d18d40-6cb3-48ef-8aba-44e01235c6ae | Reference | Default | DeleteMeButKeepReferences |
| `BidData_BidDataDoc` | ce0a95f8-b8a4-4f81-9160-7269e5a86de4 | 36d0a0ab-fffe-4cb2-82ba-c290b880fb74 | Reference | Default | DeleteMeButKeepReferences |
| `TargetPriceFactor_BidRoundSelectionFilter` | a830d754-6759-4412-8b25-628604850af7 | da2e6b11-830d-46f5-ba80-45c283f4cffa | Reference | Default | DeleteMeButKeepReferences |
| `RoundThreeBidDataExcelExport_RoundThreeBidDataReport` | 65178341-6e26-4db3-8e4f-2126abf9a38b | c3bee757-d658-4b14-805d-df0910e5a155 | ReferenceSet | Both | DeleteMeAndReferences |
| `RoundThreeBuyersDataReport_Auction` | 5c9d5fc8-c94e-47d0-8b84-957431d20369 | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Default | DeleteMeButKeepReferences |
| `RoundThreeBuyersDataReport_RoundThreeBidDataExcelExport` | 5c9d5fc8-c94e-47d0-8b84-957431d20369 | 65178341-6e26-4db3-8e4f-2126abf9a38b | Reference | Both | DeleteMeButKeepReferences |
| `BidSubmitLog_BidRound` | 3ed0be7e-6e63-40f7-8c06-48e3d7888bd7 | 18d18d40-6cb3-48ef-8aba-44e01235c6ae | Reference | Default | DeleteMeButKeepReferences |
| `BidDataQueryHelper_SchedulingAuction` | 7c5bff33-f46d-4c5b-91b4-84c7c3c70b5e | de6018ad-23e1-4367-a386-0e23c2ed7c00 | Reference | Both | DeleteMeButKeepReferences |
| `BidDataQueryHelper_Auction` | 7c5bff33-f46d-4c5b-91b4-84c7c3c70b5e | 05e02ca4-a0ed-43c8-84ef-b4f3ec803965 | Reference | Both | DeleteMeButKeepReferences |
| `AllBidDownload_2_AllBidsDoc` | 87f336d8-ae70-4736-8751-a8dc5f0c5d69 | 11272d34-f1b5-4f17-b387-7ed73c5188ad | Reference | Default | DeleteMeButKeepReferences |
| `ZeroBidDownload_AllBidsDoc` | ad6a8db8-7ad9-4af1-b3b0-21e15c1f96d0 | 11272d34-f1b5-4f17-b387-7ed73c5188ad | Reference | Default | DeleteMeButKeepReferences |
