# Microflow Detailed Specification: SUB_InitializeRound3

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_ChangeSavedBidsToPreviouslySubmitted****
2. **Call Microflow **AuctionUI.ACT_CalculateTargetPrice****
3. **Call Microflow **AuctionUI.ACT_CalculateHighestBids****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.