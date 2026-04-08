# Microflow Detailed Specification: ACT_OpenBidSubmitConfirmtio_ExcelImport

### 📥 Inputs (Parameters)
- **$BidUploadPageHelper** (Type: AuctionUI.BidUploadPageHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$AuctionTimerHelper** (Type: AuctionUI.AuctionTimerHelper)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Close current page/popup**
2. **Call Microflow **AuctionUI.ACT_OpenBidSubmitConfirmation****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.