# Microflow Detailed Specification: SUB_InitializeRound2

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'InitializeRound2'`**
2. **Create Variable **$Description** = `'Initialize Round 2 data'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
5. **List Operation: **Find** on **$undefined** where `2` (Result: **$SchedulingAuction**)**
6. **Call Microflow **AuctionUI.ACT_ChangeSavedBidsToPreviouslySubmitted****
7. **Call Microflow **AuctionUI.ACT_CalculateTargetPrice****
8. **Call Microflow **AuctionUI.SUB_AssignRoundTwoBuyers****
9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.