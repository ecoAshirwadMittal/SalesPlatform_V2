# Nanoflow: ACT_ShowBidSubmitPage_2

**Allowed Roles:** EcoATM_Buyer.Administrator, EcoATM_Buyer.Bidder, EcoATM_Buyer.SalesRep

## 📥 Inputs

- **$BuyerCodeSelect_Helper_NP** (EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_Caching.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (EcoATM_Caching.Parent_NPBuyerCodeSelectHelper)
- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$SchedulingAuction** (AuctionUI.SchedulingAuction)
- **$AuctionTimerHelper** (AuctionUI.AuctionTimerHelper)

## ⚙️ Execution Flow

1. **Call Microflow **AuctionUI.ACT_OpenBidSubmitConfirmation****
2. **Open Page: **EcoATM_Buyer.PG_Bidder_Dashboard_Submit****
3. **Call Microflow **AuctionUI.ACT_SubmitBidData****
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
