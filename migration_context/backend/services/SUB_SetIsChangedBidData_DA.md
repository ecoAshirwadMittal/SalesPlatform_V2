# Microflow Detailed Specification: SUB_SetIsChangedBidData_DA

### 📥 Inputs (Parameters)
- **$DeviceBuyerList** (Type: EcoATM_DA.DeviceBuyer)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorDeviceBuyer** in **$DeviceBuyerList**
   │ 1. **Retrieve related **DeviceBuyer_BidData** via Association from **$IteratorDeviceBuyer** (Result: **$BidData**)**
   │ 2. 🔀 **DECISION:** `$BidData != empty`
   │    ➔ **If [true]:**
   │       1. **Update **$BidData**
      - Set **IsChanged** = `true`
      - Set **Rejected** = `$IteratorDeviceBuyer/Reject`
      - Set **RejectReason** = `$IteratorDeviceBuyer/RejectReason`
      - Set **AcceptReason** = `$IteratorDeviceBuyer/AcceptReason`**
   │       2. **Add **$$BidData
** to/from list **$BidDataList_Changed****
   │       3. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
   │       4. **DB Retrieve **AuctionUI.BidData** Filter: `[EcoID = $BidData/EcoID and AuctionUI.BidData_BuyerCode = $BidData/AuctionUI.BidData_BuyerCode and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week and id != $BidData ]` (Result: **$BidDataList_AllGrades**)**
   │       5. **Add **$$BidDataList_AllGrades** to/from list **$BidDataList_Changed****
   │    ➔ **If [false]:**
   └─ **End Loop**
3. **Commit/Save **$BidDataList_Changed** to Database**
4. 🏁 **END:** Return `$BidDataList_Changed`

**Final Result:** This process concludes by returning a [List] value.