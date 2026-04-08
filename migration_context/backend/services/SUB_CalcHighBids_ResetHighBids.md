# Microflow Detailed Specification: SUB_CalcHighBids_ResetHighBids

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound[AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction=$Auction] and HighestBid = true]` (Result: **$BidDataList_AuctionHighBids**)**
2. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList_AuctionHighBids**
   │ 1. **Update **$IteratorBidData**
      - Set **HighestBid** = `false`**
   └─ **End Loop**
3. **Commit/Save **$BidDataList_AuctionHighBids** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.