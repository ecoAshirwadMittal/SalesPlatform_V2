# Microflow Detailed Specification: SUB_BidData_BatchDeleteUnSubmittedUploadedBids

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidData** Filter: `[ AuctionUI.BidData_BidRound/AuctionUI.BidRound[ Submitted = false and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction] ]` (Result: **$BidDataList_All**)**
2. **AggregateList**
3. **Create Variable **$Amount** = `@EcoATM_Direct_Sharepoint.CONST_EndOfRoundBidDataDeleteAmount`**
4. **Create Variable **$TotalProcessed** = `0`**
5. **Create Variable **$Offset** = `0`**
6. **JavaCallAction**
7. **DB Retrieve **AuctionUI.BidData** Filter: `[ AuctionUI.BidData_BidRound/AuctionUI.BidRound[ Submitted = false and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction] ]` (Result: **$BidDataList**)**
8. **AggregateList**
9. **Update Variable **$TotalProcessed** = `$TotalProcessed + $RetrievedBidDataCount`**
10. **Delete**
11. **JavaCallAction**
12. **LogMessage**
13. 🔀 **DECISION:** `$TotalProcessed >= $TotalItems`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.