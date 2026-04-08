# Microflow Detailed Specification: ACT_ShowBidderDashboardPage

### 📥 Inputs (Parameters)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.BuyerCodeSelect_Helper)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$AuctionTimerHelper** (Type: AuctionUI.AuctionTimerHelper)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BidRound != empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning** (Result: **$Log**)**
      2. **Show Message (Error): `Bidround is empty. ACT_ShowBidderDashboardPage.`**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.