# Microflow Detailed Specification: ACT_BidderDashboardNavigationHelper

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
2. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Premium_Wholesale`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_OpenBidderDashboard****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **EcoATM_PWS.ACT_Open_PWS_Order****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.