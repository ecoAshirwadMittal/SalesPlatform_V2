# Microflow Detailed Specification: ACT_ShowBuyerCodeSelectPopup

### 📥 Inputs (Parameters)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **UserRoles** via Association from **$currentUser** (Result: **$UserRoleList**)**
3. **List Operation: **Filter** on **$undefined** where `'Bidder'` (Result: **$BidderUserRole**)**
4. 🔀 **DECISION:** `$BidderUserRole != empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **Maps to Page: **AuctionUI.Buyer_Code_Select_Popup****
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **Maps to Page: **AuctionUI.Buyer_Code_Select_Search_Popup****
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.