# Microflow Analysis: ACT_OpenBidSubmitConfirmtio_ExcelImport

### Requirements (Inputs):
- **$BidUploadPageHelper** (A record of type: AuctionUI.BidUploadPageHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$AuctionTimerHelper** (A record of type: AuctionUI.AuctionTimerHelper)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Close Form**
2. **Run another process: "AuctionUI.ACT_OpenBidSubmitConfirmation"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
