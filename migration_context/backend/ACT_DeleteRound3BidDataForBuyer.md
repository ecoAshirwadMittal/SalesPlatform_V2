# Microflow Detailed Specification: ACT_DeleteRound3BidDataForBuyer

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Call Microflow **AuctionUI.SUB_BidData_BatchDelete****
3. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction and AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode[EcoATM_BuyerManagement.BuyerCode_Buyer = $Buyer]]` (Result: **$BidRoundList**)**
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.