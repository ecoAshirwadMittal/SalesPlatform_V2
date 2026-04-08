# Microflow Detailed Specification: DS_BuyerCodeBySession

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerCode_Session** via Association from **$currentSession** (Result: **$BuyerCodeList**)**
2. **List Operation: **Filter** on **$undefined** where `AuctionUI.enum_BuyerCodeType.Premium_Wholesale` (Result: **$PWSBuyerCodeList**)**
3. **List Operation: **Head** on **$undefined** (Result: **$BuyerCode**)**
4. 🔀 **DECISION:** `$BuyerCode != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BuyerCode`
   ➔ **If [false]:**
      1. **Call Microflow **AuctionUI.ACT_GetCurrentUser** (Result: **$EcoATMDirectUser**)**
      2. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
      3. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$BidderUserRoleExists**)**
      4. 🔀 **DECISION:** `$BidderUserRoleExists!=empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_SelectFirstPWSBuyerCode** (Result: **$BuyerCode_New**)**
            2. 🏁 **END:** Return `$BuyerCode_New`
         ➔ **If [false]:**
            1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Status='Active'] [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'] [BuyerCodeType = 'Premium_Wholesale'] [not(EcoATM_BuyerManagement.BuyerCode_Session/System.Session)]` (Result: **$BuyerCode_FirstActive**)**
            2. **Update **$BuyerCode_FirstActive** (and Save to DB)
      - Set **BuyerCode_Session** = `$currentSession`**
            3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.