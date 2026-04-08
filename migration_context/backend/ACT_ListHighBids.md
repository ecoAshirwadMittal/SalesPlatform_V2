# Microflow Detailed Specification: ACT_ListHighBids

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
4. **Create Variable **$OQLQuery_MaxBidByLotForScheduledAuction** = `'select EcoID as ProductId, Merged_Grade as Grade, Max(BidAmount) as MaxBid from AuctionUI."BidData" where AuctionUI.BidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted = true and BidAmount > 0 and AuctionUI.BidData/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' group by EcoID , Merged_Grade'`**
5. **JavaCallAction**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return `$MaxLotBidList`

**Final Result:** This process concludes by returning a [List] value.