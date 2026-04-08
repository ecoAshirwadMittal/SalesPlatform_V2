# Microflow Detailed Specification: ACT_NavigateToBidderDashboardOneBuyerCode

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
2. **Close current page/popup**
3. **Call Microflow **AuctionUI.ACT_BidderDashboardNavigationHelper****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.