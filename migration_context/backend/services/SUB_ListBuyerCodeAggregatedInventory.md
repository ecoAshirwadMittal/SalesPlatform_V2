# Microflow Detailed Specification: SUB_ListBuyerCodeAggregatedInventory

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (Type: AuctionUI.Auction)
- **$QualifiedBuyerCodes** (Type: EcoATM_BuyerManagement.QualifiedBuyerCodes)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round=2]` (Result: **$BidRoundSelectionFilter**)**
2. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerInventoryOptions = EcoATM_BuyerManagement.enum_RegularBuyerInventoryOption.ShowAllINventory or ($QualifiedBuyerCodes/Included and $QualifiedBuyerCodes/Qualificationtype = EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Manual)`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[ AuctionUI.BidData_BuyerCode = $BuyerCode and (BidAmount > 0)]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]` (Result: **$AggregatedInventoryList_Round1Bids**)**
      2. 🏁 **END:** Return `$AggregatedInventoryList_Round1Bids`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week = $Auction]` (Result: **$AggregatedInventoryList_All**)**
            2. 🏁 **END:** Return `$AggregatedInventoryList_All`
         ➔ **If [false]:**
            1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week = $Auction] [DWTotalQuantity > 0]` (Result: **$AggregatedInventoryList_All_DW**)**
            2. 🏁 **END:** Return `$AggregatedInventoryList_All_DW`

**Final Result:** This process concludes by returning a [List] value.