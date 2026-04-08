# Microflow Detailed Specification: SUB_CreateBuyerCodeSelectHelper

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_HelperList** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_BuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **AggregateList**
2. 🔀 **DECISION:** `$Count_BuyerCodes=1`
   ➔ **If [true]:**
      1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Code != empty` (Result: **$oneBuyerCode**)**
      2. **Call Microflow **AuctionUI.ACT_NavigateToBidderDashboardOneBuyerCode****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **AuctionUI.Buyer_Code_Select****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.