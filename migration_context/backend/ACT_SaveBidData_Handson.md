# Microflow Detailed Specification: ACT_SaveBidData_Handson

### 📥 Inputs (Parameters)
- **$BidData_HelperList** (Type: AuctionUI.BidData_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$BidData_HelperList != empty`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
      2. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
      3. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
      4. **Call Microflow **AuctionUI.ACT_GetActiveSchedulingAuction** (Result: **$CurrentSchedulingAuction**)**
      5. **List Operation: **Filter** on **$undefined** where `AuctionUI.enum_SchedulingAuctionStatus.Started` (Result: **$NewSchedulingAuctionList**)**
      6. **List Operation: **Head** on **$undefined** (Result: **$CurrentSchedulingAuction_1**)**
      7. **List Operation: **Head** on **$undefined** (Result: **$FirstBidData**)**
      8. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction=$CurrentSchedulingAuction and AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$FirstBidData/Code]` (Result: **$BidRound**)**
      9. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Code=$FirstBidData/Code]` (Result: **$BuyerCode**)**
      10. **DB Retrieve **AuctionUI.BidData** Filter: `[Code = $BuyerCode/Code] [AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList**)**
      11. **CreateList**
      12. **Create Variable **$CurrentDateTime** = `[%CurrentDateTime%]`**
      13. 🔄 **LOOP:** For each **$IteratorBidData_Helper** in **$BidData_HelperList**
         │ 1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[EcoId=$IteratorBidData_Helper/EcoID and MergedGrade=$IteratorBidData_Helper/ecoGrade and AuctionUI.AggregatedInventory_Week=$Week]` (Result: **$AgregatedInventory**)**
         │ 2. **List Operation: **Find** on **$undefined** where `$AgregatedInventory` (Result: **$BidData**)**
         │ 3. 🔀 **DECISION:** `$BidData=empty`
         │    ➔ **If [true]:**
         │       1. **Create **AuctionUI.BidData** (Result: **$NewBidData**)
      - Set **EcoID** = `$IteratorBidData_Helper/EcoID`
      - Set **BidQuantity** = `if $IteratorBidData_Helper/BidQuantity = -1 then empty else $IteratorBidData_Helper/BidQuantity`
      - Set **BidData_AggregatedInventory** = `$AgregatedInventory`
      - Set **BidAmount** = `$IteratorBidData_Helper/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **Merged_Grade** = `$IteratorBidData_Helper/ecoGrade`
      - Set **TargetPrice** = `$AgregatedInventory/AvgTargetPrice`**
         │       2. **Add **$$BidData** to/from list **$BidDataList_ToCOmmit****
         │    ➔ **If [false]:**
         │       1. **Update **$BidData**
      - Set **BidQuantity** = `if $IteratorBidData_Helper/BidQuantity = -1 then empty else $IteratorBidData_Helper/BidQuantity`
      - Set **BidAmount** = `$IteratorBidData_Helper/BidAmount`
      - Set **SubmitDateTime** = `$CurrentDateTime`**
         │       2. **Add **$$BidData** to/from list **$BidDataList_ToCOmmit****
         └─ **End Loop**
      14. **Commit/Save **$BidDataList_ToCOmmit** to Database**
      15. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.