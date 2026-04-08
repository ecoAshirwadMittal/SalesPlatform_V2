# Microflow Detailed Specification: SUB_Round2_DeleteUnsubmittedBids

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BidRound2** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction]` (Result: **$BidRound**)**
3. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRound**
   │ 1. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $IteratorBidRound] [BidAmount = 0 or BidAmount = empty]` (Result: **$BidDataList**)**
   │ 2. **Add **$$BidDataList** to/from list **$BidData_ToDelete****
   └─ **End Loop**
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.