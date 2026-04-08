# Microflow Detailed Specification: NAV_DashboardNavigationLogic_Bidder

### ⚙️ Execution Flow (Logic Steps)
1. **Close current page/popup**
2. **Create Variable **$Message** = `'Dashboard Navigation Logic for Bidders'`**
3. **Create Variable **$Timername** = `'NAV_DashboardNavigationLogic_Bidder'`**
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[ ( Name = $currentUser/Name ) ]` (Result: **$EcoATMDirectUser**)**
6. **DB Retrieve **EcoATM_BuyerManagement.AuctionsFeature**  (Result: **$AuctionsFeature**)**
7. **Call Microflow **AuctionUI.SUB_HasWholesaleBuyerCodesAssigned** (Result: **$HasWholesaleBuyerCode**)**
8. 🔀 **DECISION:** `$EcoATMDirectUser/Acknowledgement = true or $AuctionsFeature/RequireWholesaleUserAgreement = false or $HasWholesaleBuyerCode = false`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_UserManagement.SUB_SendUserLoginToSnowflake****
      2. **Call Microflow **AuctionUI.ACT_CreateBuyerCodeSelectHelper****
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **AuctionUI.PG_UserAgreement****
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.