# Microflow Detailed Specification: SUB_BuildSummaryReportObject

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidDataList** (Type: EcoATM_Buyer.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.Week** Filter: `[AuctionUI.Auction_Week/AuctionUI.Auction/AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/AuctionUI.BidRound_SchedulingAuction = $BidRound or AuctionUI.AggregatedInventory_Week/AuctionUI.AggregatedInventory/EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound = $BidRound]` (Result: **$Week**)**
2. **Retrieve related **BuyerBidSummaryReport_Week** via Association from **$Week** (Result: **$BuyerBidSummaryReportList**)**
3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode/AuctionUI.BuyerCode/Code` (Result: **$ExistingMatch**)**
4. 🔀 **DECISION:** `$ExistingMatch = empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BuyerBidSummaryReport** (Result: **$NewBuyerBidSummaryReport**)
      - Set **BuyerBidSummaryReport_Week** = `$Week`
      - Set **Submitted** = `true`
      - Set **BuyerCode** = `$BidRound/AuctionUI.BidRound_BuyerCode/AuctionUI.BuyerCode/Code`
      - Set **BuyerName** = `$BidRound/AuctionUI.BidRound_BuyerCode/AuctionUI.BuyerCode/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **LotsBid1** = `0`
      - Set **LotsBid2** = `0`
      - Set **UnitsBid1** = `0`
      - Set **UnitsBid2** = `0`**
      2. **Commit/Save **$NewBuyerBidSummaryReport** to Database**
      3. 🏁 **END:** Return `$NewBuyerBidSummaryReport`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$ExistingMatch`

**Final Result:** This process concludes by returning a [Object] value.