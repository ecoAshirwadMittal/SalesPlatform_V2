# Microflow Detailed Specification: ACT_AddCarryOverBidData

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$LastWeek_BidDataList** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$ScheduleAuction**)**
3. **Retrieve related **SchedulingAuction_Auction** via Association from **$ScheduleAuction** (Result: **$Auction**)**
4. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorLastWeekBidData** in **$LastWeek_BidDataList**
   └─ **End Loop**
7. **Commit/Save **$BidDataList_Updates** to Database**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.