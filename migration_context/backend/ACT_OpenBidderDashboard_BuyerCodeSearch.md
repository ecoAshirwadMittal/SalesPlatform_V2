# Microflow Detailed Specification: ACT_OpenBidderDashboard_BuyerCodeSearch

### 📥 Inputs (Parameters)
- **$BuyerCodeSelectSearch_Helper** (Type: EcoATM_BuyerManagement.BuyerCodeSelectSearchHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCodeSelectSearch_Helper** via Association from **$BuyerCodeSelectSearch_Helper** (Result: **$NP_BuyerCodeSelect_Helper**)**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$Parent_NPBuyerCodeSelectHelper**)**
3. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
4. **Call Microflow **AuctionUI.ACT_BidderDashboardNavigationHelper****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.