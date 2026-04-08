# Microflow Detailed Specification: DS_DisplayViewAs_Helper

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetCurrentUser** (Result: **$EcoATMDirectUser**)**
2. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
3. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$BidderUserRoleExists**)**
4. 🔀 **DECISION:** `$BidderUserRoleExists!=empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser = $currentUser] [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'] [BuyerCodeType='Premium_Wholesale']` (Result: **$BuyerCodeList**)**
      2. 🔀 **DECISION:** `length($BuyerCodeList) > 1`
         ➔ **If [true]:**
            1. **Create **AuctionUI.UiHelper** (Result: **$NewUiHelper_1**)
      - Set **Bool** = `true`**
            2. 🏁 **END:** Return `$NewUiHelper_1`
         ➔ **If [false]:**
            1. **Create **AuctionUI.UiHelper** (Result: **$NewUiHelper_2**)
      - Set **Bool** = `false`**
            2. 🏁 **END:** Return `$NewUiHelper_2`
   ➔ **If [false]:**
      1. **Create **AuctionUI.UiHelper** (Result: **$NewUiHelper**)
      - Set **Bool** = `true`**
      2. 🏁 **END:** Return `$NewUiHelper`

**Final Result:** This process concludes by returning a [Object] value.