# Microflow Detailed Specification: ACT_ChangeBuyerCodeSavedBidsToPreviouslySubmitted

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. **DB Retrieve **AuctionUI.BidData** Filter: `[ ( AuctionUI.BidData_BidRound = $BidRound and SubmittedBidAmount != empty and SubmittedDateTime < changedDate ) ]` (Result: **$BidDataList**)**
3. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │ 1. **Update **$IteratorBidData**
      - Set **BidAmount** = `$IteratorBidData/SubmittedBidAmount`
      - Set **BidQuantity** = `$IteratorBidData/SubmittedBidQuantity`**
   │ 2. **Add **$$IteratorBidData
** to/from list **$BidDataList_ToCommit****
   └─ **End Loop**
4. **Commit/Save **$BidDataList_ToCommit** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.