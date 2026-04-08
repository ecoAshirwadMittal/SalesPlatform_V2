# Microflow Detailed Specification: SUB_BidData_BatchDeleteAllBidsByAuction

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.BidData** Filter: `[ AuctionUI.BidData_BidRound/AuctionUI.BidRound[ AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week] ]` (Result: **$BidDataList_All**)**
3. **AggregateList**
4. **Create Variable **$Amount** = `@EcoATM_Direct_Sharepoint.CONST_EndOfRoundBidDataDeleteAmount`**
5. **Create Variable **$TotalProcessed** = `0`**
6. **Create Variable **$Offset** = `0`**
7. **JavaCallAction**
8. **DB Retrieve **AuctionUI.BidData** Filter: `[ AuctionUI.BidData_BidRound/AuctionUI.BidRound[ AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week] ]` (Result: **$BidDataList**)**
9. **AggregateList**
10. **Update Variable **$TotalProcessed** = `$TotalProcessed + $RetrievedBidDataCount`**
11. **Delete**
12. **JavaCallAction**
13. **Call Microflow **Custom_Logging.SUB_Log_Info****
14. 🔀 **DECISION:** `$TotalProcessed >= $TotalItems`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.