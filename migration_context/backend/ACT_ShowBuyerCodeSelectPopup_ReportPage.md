# Microflow Detailed Specification: ACT_ShowBuyerCodeSelectPopup_ReportPage

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **UserRoles** via Association from **$currentUser** (Result: **$UserRoleList**)**
2. **List Operation: **Filter** on **$undefined** where `'Bidder'` (Result: **$BidderUserRole**)**
3. 🔀 **DECISION:** `$BidderUserRole != empty`
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.Buyer_Code_Select_Page_Report_POPUP****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **AuctionUI.Buyer_Code_Select_Page_Report_POPUP****
      2. **Maps to Page: **AuctionUI.Buyer_Code_Select_Search_Popup_Report****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.