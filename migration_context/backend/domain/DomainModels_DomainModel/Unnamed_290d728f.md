# Domain Model

> Reusable entity used for generic message display.

## Entities

### 📦 OfferMasterHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `StatusSelected` | Enum(`EcoATM_PWS.ENUM_PWSOrderStatus`) | - | - | - |
| `HasOrderedItems` | BooleanAttribute | - | false | - |
| `HasSalesReviewItems` | BooleanAttribute | - | false | - |
| `HasBuyerAcceptanceItems` | BooleanAttribute | - | false | - |
| `HasDeclinedItems` | BooleanAttribute | - | false | - |
| `HasTotalItems` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ManageFileDocument
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Message` | StringAttribute | 500 | - | - |
| `ProcessPercentage` | IntegerAttribute | - | 0 | CurrentPercentage in process advancement. Used for |
| `HasProcessFailed` | BooleanAttribute | - | false | Indicator if the process has failed or not. |
| `DetailledMessage` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BuyerOffer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OfferID` | AutoNumberAttribute | - | 1001 | - |
| `OfferStatus` | Enum(`EcoATM_PWS.ENUM_PWSOrderStatus`) | - | - | - |
| `OfferTotal` | IntegerAttribute | - | 0 | - |
| `OfferSKUs` | IntegerAttribute | - | 0 | - |
| `OfferQuantity` | IntegerAttribute | - | 0 | - |
| `IsExceedingQty` | BooleanAttribute | - | false | - |
| `CSSClass` | StringAttribute | 200 | - | - |
| `SubmitOffer` | BooleanAttribute | - | false | - |
| `IsFunctionalDevicesExist` | BooleanAttribute | - | false | - |
| `IsCaseLotsExist` | BooleanAttribute | - | false | - |
| `IsUntestedDeviceExist` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ✅ | `[EcoATM_PWS.BuyerOffer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 BuyerOfferItem
> 2025-02-20 BORNPA - Transform the calculated field by a non calculated field. Determination of the CSSClass will be done when modification are applies on the BuyerOfferItem (cf. Naresh).

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Quantity` | IntegerAttribute | - | 0 | - |
| `OfferPrice` | IntegerAttribute | - | 0 | - |
| `TotalPrice` | IntegerAttribute | - | 0 | - |
| `CaseOfferTotalPrice` | IntegerAttribute | - | 0 | - |
| `CSSClass` | StringAttribute | 200 | - | - |
| `IsExceedQty` | BooleanAttribute | - | false | - |
| `IsLowerthenCurrentPrice` | BooleanAttribute | - | false | - |
| `IsModified` | BooleanAttribute | - | false | - |
| `_Type` | Enum(`EcoATM_PWS.ENUM_BuyerOfferItemType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_PWS.BuyerOfferItem_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_PWS.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OrderExcelDocument
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OrderDataExport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DeviceCode` | StringAttribute | 200 | - | - |
| `Category` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `Carrier` | StringAttribute | 200 | - | - |
| `Capacity` | StringAttribute | 200 | - | - |
| `Color` | StringAttribute | 200 | - | - |
| `Grade` | StringAttribute | 200 | - | - |
| `ListPrice` | IntegerAttribute | - | - | - |
| `AvailableQty` | StringAttribute | 200 | - | - |
| `OfferPrice` | IntegerAttribute | - | - | - |
| `Quantity` | IntegerAttribute | - | 0 | - |
| `TotalPrice` | LongAttribute | - | - | - |
| `SheetName` | StringAttribute | 200 | - | - |
| `CasePrice` | DecimalAttribute | - | - | - |
| `CasePackQty` | IntegerAttribute | - | - | - |
| `CaseOffer` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PWSUserPersonalization
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DataGrid2Personalization` | StringAttribute | - | - | - |
| `DataGrid2PersonalizationReviewOrder` | StringAttribute | - | - | - |
| `EnableCaseLots` | BooleanAttribute | - | false | - |
| `EnableAYYY` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 MDMFuturePriceHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FuturePWSPriceDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 PricingUpdateFile
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PricingDataImportHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | - | - | - |
| `CurrentListPrice` | IntegerAttribute | - | 0 | - |
| `NewListPrice` | IntegerAttribute | - | 0 | - |
| `CurrentMinPrice` | IntegerAttribute | - | 0 | - |
| `NewMinPrice` | IntegerAttribute | - | 0 | - |

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
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PricingExcelDocument
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PricingDataExport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |
| `Category` | StringAttribute | 200 | - | - |
| `Brand` | StringAttribute | 200 | - | - |
| `Model` | StringAttribute | 200 | - | - |
| `Carrier` | StringAttribute | 200 | - | - |
| `Capacity` | StringAttribute | 200 | - | - |
| `Color` | StringAttribute | 200 | - | - |
| `Grade` | StringAttribute | 200 | - | - |
| `Lookup` | StringAttribute | - | - | - |
| `CurrentListPrice` | StringAttribute | 200 | - | - |
| `NewListPrice` | StringAttribute | 200 | - | - |
| `CurrentMinPrice` | StringAttribute | 200 | - | - |
| `NewMinPrice` | StringAttribute | 200 | - | - |
| `SKUStatus` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PricingDataExcelImporter
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | - | - | - |
| `Current_List_Price` | StringAttribute | 200 | - | - |
| `New_List_Price` | StringAttribute | 200 | - | - |
| `Current_Min_Price` | StringAttribute | 200 | - | - |
| `New_Min_Price` | StringAttribute | 200 | - | - |

### 📦 Order
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderNumber` | StringAttribute | 200 | - | - |
| `OrderLine` | StringAttribute | 200 | - | - |
| `OrderDate` | DateTimeAttribute | - | - | - |
| `OracleOrderStatus` | StringAttribute | - | - | 20250407 BORNPA - Statuses from Oracle are not def |
| `JSONContent` | StringAttribute | - | - | - |
| `OracleHTTPCode` | IntegerAttribute | - | - | Retourned HTTP Code after Oracle call. |
| `OracleJSONResponse` | StringAttribute | - | - | Original response from Oracle |
| `IsSuccessful` | BooleanAttribute | - | false | - |
| `ShipDate` | DateTimeAttribute | - | - | - |
| `ShipMethod` | StringAttribute | 200 | - | - |
| `ShipToAddress` | StringAttribute | 2000 | - | - |
| `HasShipmentDetails` | BooleanAttribute | - | true | - |
| `ShippedTotalSKU` | IntegerAttribute | - | - | - |
| `ShippedTotalQuantity` | IntegerAttribute | - | - | - |
| `ShippedTotalPrice` | IntegerAttribute | - | - | - |
| `LegacyOrder` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_PWS.Order_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |

### 📦 Offer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OfferID` | StringAttribute | 200 | - | - |
| `OfferStatus` | Enum(`EcoATM_PWS.ENUM_PWSOrderStatus`) | - | - | - |
| `OfferSKUCount` | IntegerAttribute | - | - | - |
| `OfferTotalQuantity` | IntegerAttribute | - | - | Total quantities |
| `OfferTotalPrice` | IntegerAttribute | - | - | - |
| `OfferSubmissionDate` | DateTimeAttribute | - | - | - |
| `OfferAvgPrice` | DecimalAttribute | - | - | - |
| `OfferMinPercentageVariance` | DecimalAttribute | - | - | - |
| `UpdateDate` | DateTimeAttribute | - | - | - |
| `CounterOfferTotalSKU` | IntegerAttribute | - | - | - |
| `CounterOfferTotalQty` | IntegerAttribute | - | - | - |
| `CounterOfferTotalPrice` | IntegerAttribute | - | - | - |
| `CounterOfferAvgPrice` | DecimalAttribute | - | - | - |
| `CounterOfferMinPercentageVariance` | DecimalAttribute | - | - | - |
| `CounterResponseSubmittedOn` | DateTimeAttribute | - | - | - |
| `FinalOfferTotalSKU` | IntegerAttribute | - | - | - |
| `FinalOfferTotalQty` | IntegerAttribute | - | - | - |
| `FinalOfferTotalPrice` | IntegerAttribute | - | - | - |
| `FinalOfferSubmittedOn` | DateTimeAttribute | - | - | - |
| `isValidOffer` | BooleanAttribute | - | false | - |
| `SalesReviewCompletedOn` | DateTimeAttribute | - | - | - |
| `OfferBeyondSLA` | BooleanAttribute | - | false | - |
| `SameSKUOffer` | BooleanAttribute | - | false | - |
| `OfferRevertedDate` | DateTimeAttribute | - | - | - |
| `BuyerOfferCancelled` | BooleanAttribute | - | false | - |
| `SellerOfferCancelled` | BooleanAttribute | - | false | - |
| `OfferCancelledOn` | DateTimeAttribute | - | - | - |
| `OfferType` | Enum(`EcoATM_PWS.ENUM_OfferType`) | - | OfferFlow | - |
| `AllSKUsWithValidOffer` | BooleanAttribute | - | true | - |
| `FirstReminderSent` | BooleanAttribute | - | false | - |
| `SecondReminderSent` | BooleanAttribute | - | false | - |
| `IsFunctionalDeviceExist` | BooleanAttribute | - | false | - |
| `IsCaseLotsExist` | BooleanAttribute | - | false | - |
| `IsUntestedDeviceExist` | BooleanAttribute | - | false | - |
| `OrderPackQuantity` | IntegerAttribute | - | 0 | - |
| `CanceledQuantity` | IntegerAttribute | - | 0 | - |
| `ShippedQuantity` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ✅ | `[EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_PWS.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OfferItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OfferQuantity` | IntegerAttribute | - | - | - |
| `OfferPrice` | IntegerAttribute | - | - | - |
| `CounterCasePriceTotal` | IntegerAttribute | - | - | - |
| `OfferTotalPrice` | IntegerAttribute | - | - | - |
| `SalesOfferItemStatus` | Enum(`EcoATM_PWS.ENUM_OfferItemStatus`) | - | - | 20250421 BORNPA - Add Finalize option. |
| `CounterPrice` | IntegerAttribute | - | - | - |
| `CounterQuantity` | IntegerAttribute | - | - | - |
| `CounterTotal` | IntegerAttribute | - | - | - |
| `FinalOfferPrice` | IntegerAttribute | - | - | - |
| `FinalOfferQuantity` | IntegerAttribute | - | - | - |
| `FinalOfferTotalPrice` | IntegerAttribute | - | - | - |
| `BuyerCounterStatus` | Enum(`EcoATM_PWS.ENUM_CounterStatus`) | - | - | - |
| `MinPercentage` | DecimalAttribute | - | - | - |
| `ListPercentage` | DecimalAttribute | - | 0 | - |
| `SameSKUOfferAvailable` | BooleanAttribute | - | false | - |
| `QuantityCSSStyle` | StringAttribute | 200 | - | 20250429 BORNPA - Only used for the display cf SPK |
| `OfferDrawerStatus` | Enum(`EcoATM_PWS.ENUM_OfferDrawerStatus`) | - | - | - |
| `Reserved` | BooleanAttribute | - | false | - |
| `ReservedOn` | DateTimeAttribute | - | - | - |
| `OrderSynced` | BooleanAttribute | - | false | - |
| `ValidQty` | BooleanAttribute | - | true | - |
| `ShippedQty` | IntegerAttribute | - | 0 | - |
| `ShippedPrice` | IntegerAttribute | - | 0 | - |
| `SortOrder` | IntegerAttribute | - | 99 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ❌ | ✅ | ✅ | ❌ | `[EcoATM_PWS.OfferItem_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer='[%CurrentUser%]']` |
| EcoATM_PWS.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 PWSConstants
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SLADays` | IntegerAttribute | - | 2 | - |
| `SalesEmail` | StringAttribute | 200 | wholesalesupport@ecoatm.com | - |
| `HoursFirstCounterReminderEmail` | IntegerAttribute | - | 24 | - |
| `HoursSecondCounterReminderEmail` | IntegerAttribute | - | 48 | - |
| `SendFirstReminder` | BooleanAttribute | - | true | - |
| `SendSecondReminder` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 UserMessage
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Title` | StringAttribute | 200 | - | - |
| `PrimaryMessage` | StringAttribute | 200 | - | - |
| `Message` | StringAttribute | - | - | - |
| `CSSClass` | StringAttribute | 200 | - | - |
| `IsSuccess` | BooleanAttribute | - | false | 20250324 BORNPA - Adding this indicator. Depending |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PWSSearch
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SearchStr1` | StringAttribute | 200 | - | - |
| `SearchStr2` | StringAttribute | 200 | - | - |
| `SearchStr3` | StringAttribute | 200 | - | - |
| `SearchStr4` | StringAttribute | 200 | - | - |
| `SearchStr5` | StringAttribute | 200 | - | - |
| `SearchStr6` | StringAttribute | 200 | - | - |
| `SearchStr7` | StringAttribute | 200 | - | - |
| `SearchStr8` | StringAttribute | 200 | - | - |
| `SearchStr9` | StringAttribute | 200 | - | - |
| `SearchStr10` | StringAttribute | 200 | - | - |
| `SearchString` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.Bidder | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesLeader | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesOps | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OfferDataExcelImporter
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | - | - | - |
| `Category` | StringAttribute | - | - | - |
| `Brand` | StringAttribute | - | - | - |
| `Model` | StringAttribute | - | - | - |
| `Carrier` | StringAttribute | - | - | - |
| `Capacity` | StringAttribute | - | - | - |
| `Color` | StringAttribute | - | - | - |
| `Grade` | StringAttribute | - | - | - |
| `ListPrice` | DecimalAttribute | - | - | - |
| `AvailableQuantity` | StringAttribute | - | - | - |
| `OfferPrice` | DecimalAttribute | - | 0 | - |
| `Quantity` | DecimalAttribute | - | - | - |
| `TotalPrice` | DecimalAttribute | - | - | - |

### 📦 OffersUiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HeaderLabel` | StringAttribute | 200 | - | Header label can be use as an alternative title fo |
| `HeaderCSSClass` | StringAttribute | 200 | - | - |
| `OfferStatus` | Enum(`EcoATM_PWS.ENUM_PWSOrderStatus`) | - | - | - |
| `OfferCount` | IntegerAttribute | - | 0 | - |
| `TotalSKUs` | IntegerAttribute | - | 0 | - |
| `TotalQty` | IntegerAttribute | - | 0 | - |
| `TotalPrice` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PWSEmailNotification
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `BuyerName` | StringAttribute | 200 | - | - |
| `OfferID` | StringAttribute | 200 | - | - |
| `TableHTML` | StringAttribute | - | - | - |
| `Total` | IntegerAttribute | - | 0 | - |
| `TotalHTML` | StringAttribute | - | - | - |
| `FooterHTML` | StringAttribute | - | - | - |
| `CounterButtonHTML` | StringAttribute | - | - | - |
| `CompanyName` | StringAttribute | 200 | - | - |
| `RMANumber` | StringAttribute | 200 | - | - |
| `RMAButtonHTML` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OrderListSelection
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SelectedCount` | LongAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferDrawerHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DataGridSource` | Enum(`EcoATM_PWS.ENUM_DrawerDataGridSource`) | - | ThisSKU | - |
| `Last90Days` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferExcelDocument
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferDataExport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OfferID` | StringAttribute | 200 | - | - |
| `OfferStatus` | StringAttribute | 200 | - | - |
| `BuyerName` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `SalesRep` | StringAttribute | 200 | - | - |
| `SKUs` | IntegerAttribute | - | 0 | - |
| `Qty` | IntegerAttribute | - | 0 | - |
| `OfferPrice` | DecimalAttribute | - | 0 | - |
| `OfferDate` | DateTimeAttribute | - | - | - |
| `LastUpdated` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferDetailsExport
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferAndOrdersView
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderNumber` | StringAttribute | 200 | - | - |
| `OfferDate` | DateTimeAttribute | - | - | - |
| `OrderDate` | DateTimeAttribute | - | - | - |
| `OrderStatus` | StringAttribute | 200 | - | - |
| `ShipDate` | DateTimeAttribute | - | - | - |
| `ShipMethod` | StringAttribute | 200 | - | - |
| `ShipTo` | StringAttribute | 2000 | - | - |
| `SKUCount` | IntegerAttribute | - | - | - |
| `TotalQuantity` | IntegerAttribute | - | - | - |
| `TotalPrice` | IntegerAttribute | - | - | - |
| `Buyer` | StringAttribute | 200 | - | - |
| `Company` | StringAttribute | 200 | - | - |
| `LastUpdateDate` | DateTimeAttribute | - | - | - |
| `OfferOrderType` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OrderHistoryHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RecentDateCutoff` | DateTimeAttribute | - | - | - |
| `CurrentTab` | Enum(`EcoATM_PWS.ENUM_OrderHistoryTab`) | - | Recent | - |
| `CountAll` | IntegerAttribute | - | 0 | - |
| `CountComplete` | IntegerAttribute | - | 0 | - |
| `CountRecent` | IntegerAttribute | - | 0 | - |
| `CountInProcess` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 OrderHistoyDownload
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderNumber` | StringAttribute | 200 | - | - |
| `OfferDate` | DateTimeAttribute | - | - | - |
| `OrderDate` | DateTimeAttribute | - | - | - |
| `OrderStatus` | StringAttribute | 200 | - | - |
| `SKUcount` | IntegerAttribute | - | 0 | - |
| `TotalQuantity` | IntegerAttribute | - | 0 | - |
| `TotalPrice` | IntegerAttribute | - | 0 | - |
| `Buyer` | StringAttribute | 200 | - | - |
| `BuyerCode` | StringAttribute | 200 | - | - |
| `Company` | StringAttribute | 200 | - | - |
| `LastUpdateDate` | DateTimeAttribute | - | - | - |
| `OfferOrderType` | StringAttribute | 200 | - | - |
| `ShipDate` | DateTimeAttribute | - | - | - |
| `ShipMethod` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ❌ | - |

### 📦 OrderHistoryExport
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OrderDetailsExport
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OfferItemExcelDocument
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OfferItemDataExport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OfferDate` | StringAttribute | 200 | - | - |
| `Status` | StringAttribute | 200 | - | - |
| `Customer` | StringAttribute | 200 | - | - |
| `Qty` | IntegerAttribute | - | - | - |
| `ListPrice` | StringAttribute | 200 | - | - |
| `OfferPrice` | StringAttribute | 200 | - | - |
| `Color` | StringAttribute | 200 | - | - |
| `SKU` | StringAttribute | 200 | - | - |

### 📦 OrderStatus
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SystemStatus` | StringAttribute | 200 | - | - |
| `InternalStatusText` | StringAttribute | 200 | - | - |
| `ExternalStatusText` | StringAttribute | 200 | - | - |
| `InterStatusHexCode` | Enum(`EcoATM_Direct_Theme.ENUM_PWSBgColor`) | - | - | - |
| `ExternalStatusHexCode` | Enum(`EcoATM_Direct_Theme.ENUM_PWSBgColor`) | - | - | - |
| `SystemStatusDescription` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ShipmentDetail
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TrackingNumber` | StringAttribute | 1000 | - | - |
| `TrackingUrl` | StringAttribute | 2000 | - | - |
| `SKUCount` | IntegerAttribute | - | - | - |
| `Quantity` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 IMEIDetail
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IMEINumber` | StringAttribute | 200 | - | - |
| `SerialNumber` | StringAttribute | 200 | - | - |
| `BoxLPNNumber` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 MaintenanceMode
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IsEnabled` | BooleanAttribute | - | false | - |
| `BannerStartTime` | DateTimeAttribute | - | - | - |
| `StartTime` | DateTimeAttribute | - | - | - |
| `EndTime` | DateTimeAttribute | - | - | - |
| `BannerTitle` | StringAttribute | - | - | - |
| `BannerMessage` | StringAttribute | - | - | - |
| `PageTitle` | StringAttribute | - | - | - |
| `PageHeader` | StringAttribute | - | - | - |
| `PageMessage` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 PWSInventorySyncReport
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ReportDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 LegacyOrderDateHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LegacyDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SKUSyncDetail
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |
| `PreviousSyncTime` | DateTimeAttribute | - | - | - |
| `PreviousSyncQty` | IntegerAttribute | - | 0 | - |
| `SyncTime` | DateTimeAttribute | - | - | - |
| `SyncQty` | IntegerAttribute | - | 0 | - |
| `DeltaQty` | IntegerAttribute | - | 0 | - |
| `NotFound` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ChangeOfferStatusHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FromOfferStatus` | Enum(`EcoATM_PWS.ENUM_PWSOrderStatus`) | - | - | - |
| `AllPeriod` | BooleanAttribute | - | false | - |
| `StartingDate` | DateTimeAttribute | - | - | - |
| `EndingDate` | DateTimeAttribute | - | - | - |
| `NotOrderStatusChange` | BooleanAttribute | - | false | - |
| `HasShipmentDetails` | BooleanAttribute | - | false | - |
| `LegacyOrder` | BooleanAttribute | - | false | - |
| `SelectManuallyOrders` | BooleanAttribute | - | false | - |
| `ToOrderStatus` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OrderDetailHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderDetailDataGridSource` | Enum(`EcoATM_PWS.ENUM_OrderDetailsDataGridSource`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 IMEIData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OrderNum` | StringAttribute | 200 | - | - |
| `SKU` | StringAttribute | 200 | - | - |
| `IMEI` | StringAttribute | 200 | - | - |

### 📦 OrderDetailsExportByDevice
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesLeader | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesOps | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 NavigationMenu
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `IsActive` | BooleanAttribute | - | false | - |
| `Order` | IntegerAttribute | - | 0 | - |
| `MicroflowName` | StringAttribute | 200 | - | - |
| `IconCSSClass` | StringAttribute | 200 | - | - |
| `UserGroup` | Enum(`EcoATM_PWS.ENUM_CurrentNavigationView`) | - | - | - |
| `LoadingMessage` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SKUHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SKU` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep | ✅ | ✅ | ✅ | ✅ | - |

### 📦 UpdateSnowflakeHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FromDate` | DateTimeAttribute | - | - | - |
| `ToDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_PWS.Administrator | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `OffersUiHelper_OfferMasterHelper` | cbca54f7-2b8e-4b98-8284-ddc29bfd5376 | a1f05b62-559e-4abd-8b50-79076f6d1b15 | Reference | Default | DeleteMeButKeepReferences |
| `BuyerOfferItem_BuyerOffer` | b7a77285-22af-4f62-bcc9-0416f29e7f4f | af808ea3-e52d-44ca-bb9f-6fc8b24e6261 | Reference | Default | DeleteMeButKeepReferences |
| `OrderDataExport_Devices` | bf08f3b6-29a2-427c-ba87-16fdc4e7a22d | 45db2fa4-6dba-4770-aba0-15840f0306d5 | Reference | Default | DeleteMeButKeepReferences |
| `PricingUpdateFile_MDMFuturePriceHelper` | 7e48fd9a-707e-4e3f-9499-6642cfc15a33 | cc5d5da9-9e3f-417f-81c9-1f6d6dbcc6d1 | Reference | Both | DeleteMeButKeepReferences |
| `PricingDataExport_PricingExcelDocument` | 0d18b2da-4821-4de9-a53f-1cda35dd0bb0 | c49a32f1-15e7-47ba-b9e4-77632e386b3e | Reference | Default | DeleteMeButKeepReferences |
| `OfferItem_Offer` | 60853902-0e79-432a-9946-6f8f078b6d67 | bc47a23d-f8cf-49a6-a1c7-ee89cdb9e82a | Reference | Default | DeleteMeButKeepReferences |
| `OfferItem_Order` | 60853902-0e79-432a-9946-6f8f078b6d67 | 3d02f01b-3238-4365-a63e-754a34ef7827 | Reference | Default | DeleteMeButKeepReferences |
| `Offer_Order` | bc47a23d-f8cf-49a6-a1c7-ee89cdb9e82a | 3d02f01b-3238-4365-a63e-754a34ef7827 | Reference | Both | DeleteMeButKeepReferences |
| `OfferDataExport_OfferExcelDocument` | 0d3ff844-ce7e-4869-901b-f80fc07ff873 | 653f7f89-b0dd-478c-92ed-09aa0b480280 | Reference | Default | DeleteMeButKeepReferences |
| `OfferItem_OfferDetailsExport` | 60853902-0e79-432a-9946-6f8f078b6d67 | 5eba963c-6233-43e9-bbab-a625178b56e5 | ReferenceSet | Both | DeleteMeButKeepReferences |
| `OrderHistoyDownload_OrderHistoryExport` | 701b1db9-79df-42fc-8d0f-879c324ac543 | d953dafd-77d2-404c-a3cd-39bde9ed0ee9 | Reference | Default | DeleteMeButKeepReferences |
| `OfferItem_OrderDetailsExport` | 60853902-0e79-432a-9946-6f8f078b6d67 | 7e02182b-64d8-4b3e-9b46-edac6cbb15a3 | Reference | Default | DeleteMeButKeepReferences |
| `OfferItemDataExport_OfferItemExcelDocument` | 6e454399-c091-43f0-ac74-0abaec8d5201 | 95cf4b2f-6e3d-4b9d-b4f3-c282fc4f0e44 | Reference | Default | DeleteMeButKeepReferences |
| `ShipmentDetail_Order` | 38719185-6617-43ad-95ba-e4419cc8ad98 | 3d02f01b-3238-4365-a63e-754a34ef7827 | Reference | Default | DeleteMeButKeepReferences |
| `IMEIDetail_OfferItem` | 18eef71d-e0a7-4fee-8777-5541dd919096 | 60853902-0e79-432a-9946-6f8f078b6d67 | Reference | Default | DeleteMeButKeepReferences |
| `SKUSyncDetail_PWSInventorySyncReport` | 5a5251ce-5e6c-4cd4-a0ed-099ee878b019 | 006546e1-a299-4125-a1c0-b67698307471 | ReferenceSet | Both | DeleteMeButKeepReferences |
| `Offer_OrderStatus` | bc47a23d-f8cf-49a6-a1c7-ee89cdb9e82a | d58695d5-01d3-4509-8928-2c8dc865d1fa | Reference | Default | DeleteMeButKeepReferences |
| `ChangeOfferStatusHelper_OrderStatus` | 5bb2bd0e-12ff-401c-b758-8ea7334679f8 | d58695d5-01d3-4509-8928-2c8dc865d1fa | Reference | Default | DeleteMeButKeepReferences |
| `ChangeOfferStatusHelper_Order` | 5bb2bd0e-12ff-401c-b758-8ea7334679f8 | 3d02f01b-3238-4365-a63e-754a34ef7827 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `OfferItem_ShipmentDetail` | 38719185-6617-43ad-95ba-e4419cc8ad98 | 60853902-0e79-432a-9946-6f8f078b6d67 | ReferenceSet | Both | DeleteMeButKeepReferences |
| `IMEIDetail_ShipmentDetail` | 18eef71d-e0a7-4fee-8777-5541dd919096 | 38719185-6617-43ad-95ba-e4419cc8ad98 | Reference | Default | DeleteMeButKeepReferences |
| `OfferItem_OrderStatus` | 60853902-0e79-432a-9946-6f8f078b6d67 | d58695d5-01d3-4509-8928-2c8dc865d1fa | Reference | Default | DeleteMeButKeepReferences |
| `IMEIDetail_OrderDetailsExportByDevice` | 18eef71d-e0a7-4fee-8777-5541dd919096 | ec2f8c97-095f-4aa4-8101-387ee84d9cd7 | Reference | Default | DeleteMeButKeepReferences |
| `OrderDataExport_CaseLots` | bf08f3b6-29a2-427c-ba87-16fdc4e7a22d | 45db2fa4-6dba-4770-aba0-15840f0306d5 | Reference | Default | DeleteMeButKeepReferences |
