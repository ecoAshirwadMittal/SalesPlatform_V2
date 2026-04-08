# Microflow Detailed Specification: ACT_SendBidstoSharepoint_perBuyerCode_Admin

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Call Microflow **AuctionUI.ACT_CreateBidSubmitLog** (Result: **$BidSubmitLog**)**
3. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
4. **LogMessage**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.