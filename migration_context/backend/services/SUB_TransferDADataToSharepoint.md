# Microflow Detailed Specification: SUB_TransferDADataToSharepoint

### 📥 Inputs (Parameters)
- **$DeviceBuyerList** (Type: EcoATM_DA.DeviceBuyer)
- **$BidDataList** (Type: AuctionUI.BidData)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorDeviceBuyer** in **$DeviceBuyerList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorDeviceBuyer/BuyerCode` (Result: **$BuyerCode_Existing**)**
   │ 2. 🔀 **DECISION:** `$BuyerCode_Existing = empty`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Code=$IteratorDeviceBuyer/BuyerCode]` (Result: **$BuyerCode**)**
   │       2. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
   └─ **End Loop**
4. **CreateList**
5. **Create **EcoATM_BuyerManagement.BuyerCode** (Result: **$EBBuyerCode**)
      - Set **Code** = `'EB'`
      - Set **Status** = `AuctionUI.enum_BuyerCodeStatus.Active`**
6. **Add **$$EBBuyerCode
** to/from list **$BuyerCodeList****
7. **Call Microflow **EcoATM_DA.SUB_CreateEBBidDataList****
8. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code = $IteratorBuyerCode/Code` (Result: **$BidDataList_IteratorBuyer**)**
   │ 2. **CreateList**
   │ 3. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList_IteratorBuyer**
   │    │ 1. **Retrieve related **BidData_AggregatedInventory** via Association from **$IteratorBidData** (Result: **$AggregatedInventory**)**
   │    │ 2. **Add **$$AggregatedInventory** to/from list **$AggregatedInventoryList****
   │    │ 3. 🔀 **DECISION:** `$IteratorBidData/IsChanged`
   │    │    ➔ **If [false]:**
   │    │    ➔ **If [true]:**
   │    │       1. **Retrieve related **DeviceBuyer_BidData** via Association from **$IteratorBidData** (Result: **$DeviceBuyer**)**
   │    │       2. **Create Variable **$NewBidAmount** = `if $DeviceBuyer/Reject then 0.00 else $DeviceBuyer/Bid`**
   │    │       3. **Update **$IteratorBidData**
      - Set **TempDABidAmount** = `$IteratorBidData/BidAmount`
      - Set **BidAmount** = `$NewBidAmount`**
   │    └─ **End Loop**
   │ 4. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW****
   │ 5. **CreateList**
   │ 6. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
   │ 7. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
   │ 8. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$AllBidsZipTempList**)**
   │ 9. **Delete**
   └─ **End Loop**
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.