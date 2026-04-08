# Microflow Detailed Specification: NAV_DashboardNavigationLogic

### ⚙️ Execution Flow (Logic Steps)
1. **Close current page/popup**
2. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[ ( Name = $currentUser/Name ) ]` (Result: **$EcoATMDirectUser**)**
3. **Call Microflow **EcoATM_UserManagement.SUB_SendUserLoginToSnowflake****
4. 🔀 **DECISION:** `$EcoATMDirectUser/LandingPagePreference`
   ➔ **If [Premium_Wholesale]:**
      1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
      2. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$BidderUserRoleExists**)**
      3. 🔀 **DECISION:** `$BidderUserRoleExists!=empty`
         ➔ **If [false]:**
            1. **Maps to Page: **EcoATM_PWS.PWSOrder_PE_Dashboard****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_SelectFirstPWSBuyerCode** (Result: **$BuyerCode**)**
            2. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
            3. 🏁 **END:** Return empty
   ➔ **If [Wholesale_Auction]:**
      1. **Call Microflow **AuctionUI.SUB_NavigateToAggregatedInventoryPage****
      2. **Maps to Page: **AuctionUI.Buyer_Code_Select_Search****
      3. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. **Call Microflow **AuctionUI.SUB_NavigateToAggregatedInventoryPage****
      2. **Maps to Page: **AuctionUI.Buyer_Code_Select_Search****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.