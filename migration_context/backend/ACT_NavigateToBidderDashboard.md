# Microflow Detailed Specification: ACT_NavigateToBidderDashboard

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
2. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Premium_Wholesale`
   ➔ **If [true]:**
      1. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
      2. **Close current page/popup**
      3. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$BuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
      4. **Call Microflow **AuctionUI.ACT_BidderDashboardNavigationHelper****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$BuyerCode/EcoATM_BuyerManagement.BuyerCode_Session = empty or $BuyerCode/EcoATM_BuyerManagement.BuyerCode_Session = $currentSession`
         ➔ **If [false]:**
            1. **Maps to Page: **EcoATM_PWS.PWS_BuyerCodeInUse****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
            2. **Close current page/popup**
            3. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$BuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
            4. **Call Microflow **AuctionUI.ACT_BidderDashboardNavigationHelper****
            5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.