# Microflow Detailed Specification: ACT_GetBuyerCodes_Report

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[RoundStatus='Started']` (Result: **$StartedSchedulingAuction**)**
2. 🔀 **DECISION:** `$StartedSchedulingAuction/Round = 2`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active']` (Result: **$AllActiveBuyerCodeList**)**
      2. 🏁 **END:** Return `$AllActiveBuyerCodeList`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_Round2BuyerCodes_SalesRep** (Result: **$Round2BuyerCodeList**)**
      2. 🏁 **END:** Return `$Round2BuyerCodeList`

**Final Result:** This process concludes by returning a [List] value.