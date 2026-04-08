# Microflow Detailed Specification: ACT_BidIMport_Cancel_2

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (Type: AuctionUI.BuyerCodeSelect_Helper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_Caching.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Close current page/popup**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
3. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
4. **Maps to Page: **AuctionUI.Bidder_Dashboard_HOT****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.