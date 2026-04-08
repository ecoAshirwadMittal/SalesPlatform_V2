# Microflow Detailed Specification: SUB_GetUniqueBidDataList

### 📥 Inputs (Parameters)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidData** Filter: `[ $DeviceAllocation/Grade = Merged_Grade and $DeviceAllocation/ProductID = EcoID and $DAWeek/EcoATM_DA.DAWeek_Week = AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/id and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted ]` (Result: **$BidDataList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $IteratorBidData/AuctionUI.BidData_BuyerCode and $currentObject/Merged_Grade = $IteratorBidData/Merged_Grade and $currentObject/EcoID = $IteratorBidData/EcoID` (Result: **$BidData_Existing**)**
   │ 2. 🔀 **DECISION:** `$BidData_Existing = empty`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BuyerCode = $IteratorBidData/AuctionUI.BidData_BuyerCode` (Result: **$BidDataList_Buyer**)**
   │       2. **List Operation: **Sort** on **$undefined** sorted by: SubmittedDateTime (Descending) (Result: **$BidDataList_Buyer_Sorted**)**
   │       3. **List Operation: **Head** on **$undefined** (Result: **$BidData_Buyer**)**
   │       4. **Add **$$BidData_Buyer** to/from list **$BidDataList_Unique****
   └─ **End Loop**
4. 🏁 **END:** Return `$BidDataList_Unique`

**Final Result:** This process concludes by returning a [List] value.