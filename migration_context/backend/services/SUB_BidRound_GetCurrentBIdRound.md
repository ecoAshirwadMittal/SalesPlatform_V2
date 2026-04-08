# Microflow Detailed Specification: SUB_BidRound_GetCurrentBIdRound

### 📥 Inputs (Parameters)
- **$CurrentSchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **BidRound_SchedulingAuction** via Association from **$CurrentSchedulingAuction** (Result: **$BidRoundList**)**
3. **List Operation: **Filter** on **$undefined** where `$BuyerCode` (Result: **$CurrentBidRoundList**)**
4. **List Operation: **Head** on **$undefined** (Result: **$NewBidRound**)**
5. **Call Microflow **Custom_Logging.SUB_Log_Info****
6. 🏁 **END:** Return `$NewBidRound`

**Final Result:** This process concludes by returning a [Object] value.