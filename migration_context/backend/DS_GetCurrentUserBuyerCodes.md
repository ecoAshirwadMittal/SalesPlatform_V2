# Microflow Detailed Specification: DS_GetCurrentUserBuyerCodes

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetCurrentUser** (Result: **$EcoATMDirectUser**)**
2. **Retrieve related **BuyerCode_Session** via Association from **$currentSession** (Result: **$CurrentSessionBuyerCodes**)**
3. **List Operation: **Head** on **$undefined** (Result: **$CurrentBuyerCode**)**
4. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
5. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$BidderUserRoleExists**)**
6. 🔀 **DECISION:** `$BidderUserRoleExists!=empty`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$CurrentSessionBuyerCodes = empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. **List Operation: **Head** on **$undefined** (Result: **$CurrentBuyerCode_1**)**
            2. 🏁 **END:** Return `$CurrentBuyerCode_1`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser = $currentUser] [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active']` (Result: **$BuyerCodeList**)**
      2. 🔀 **DECISION:** `length($BuyerCodeList) > 1`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$CurrentBuyerCode`

**Final Result:** This process concludes by returning a [Object] value.