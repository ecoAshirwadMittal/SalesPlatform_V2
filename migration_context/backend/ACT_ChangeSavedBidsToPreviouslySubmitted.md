# Microflow Detailed Specification: ACT_ChangeSavedBidsToPreviouslySubmitted

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
2. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList**)**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRoundList**
   │ 1. **DB Retrieve **AuctionUI.BidData** Filter: `[ ( AuctionUI.BidData_BidRound = $IteratorBidRound and SubmittedBidAmount != empty and SubmittedDateTime < changedDate ) ]` (Result: **$BidDataList**)**
   │ 2. **AggregateList**
   │ 3. 🔀 **DECISION:** `$Count > 0`
   │    ➔ **If [true]:**
   │       1. **Call Microflow **Custom_Logging.SUB_Log_Info** (Result: **$Log**)**
   │       2. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │          │ 1. **Update **$IteratorBidData**
      - Set **BidAmount** = `$IteratorBidData/SubmittedBidAmount`
      - Set **BidQuantity** = `$IteratorBidData/SubmittedBidQuantity`**
   │          │ 2. **Add **$$IteratorBidData
** to/from list **$BidDataList_ToCommit****
   │          └─ **End Loop**
   │    ➔ **If [false]:**
   │       1. **Call Microflow **Custom_Logging.SUB_Log_Info** (Result: **$Log**)**
   └─ **End Loop**
5. **Commit/Save **$BidDataList_ToCommit** to Database**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.