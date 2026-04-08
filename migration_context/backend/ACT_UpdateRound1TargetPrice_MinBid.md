# Microflow Detailed Specification: ACT_UpdateRound1TargetPrice_MinBid

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week = $Auction and AvgTargetPrice < $BuyerCodeSubmitConfig/MinimumAllowedBid and TotalQuantity > 0]` (Result: **$AggregatedInventoryList_NonDW_TargetPriceBelowMinBidConfig**)**
3. 🔄 **LOOP:** For each **$IteratorAggregatedInventory_NonDW_BelowMin** in **$AggregatedInventoryList_NonDW_TargetPriceBelowMinBidConfig**
   │ 1. **Update **$IteratorAggregatedInventory_NonDW_BelowMin**
      - Set **AvgTargetPrice** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
   └─ **End Loop**
4. **Commit/Save **$AggregatedInventoryList_NonDW_TargetPriceBelowMinBidConfig** to Database**
5. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week = $Auction and DWAvgTargetPrice < $BuyerCodeSubmitConfig/MinimumAllowedBid and DWTotalQuantity > 0]` (Result: **$AggregatedInventoryList_DW_TargetPriceBelowMinBidConfig**)**
6. 🔄 **LOOP:** For each **$IteratorAggregatedInventory_DW_BelowMin** in **$AggregatedInventoryList_DW_TargetPriceBelowMinBidConfig**
   │ 1. **Update **$IteratorAggregatedInventory_DW_BelowMin**
      - Set **DWAvgTargetPrice** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
   └─ **End Loop**
7. **Commit/Save **$AggregatedInventoryList_DW_TargetPriceBelowMinBidConfig** to Database**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.