# Microflow Detailed Specification: DS_ListQualifiedBuyerCodes

### 📥 Inputs (Parameters)
- **$QualifiedBuyerCodesQueryHelper** (Type: EcoATM_BuyerManagement.QualifiedBuyerCodesQueryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$QualifiedBuyerCodesQueryHelper/EcoATM_BuyerManagement.QualifiedBuyerCodesQueryHelper_Auction/AuctionUI.Auction != empty and $QualifiedBuyerCodesQueryHelper/EcoATM_BuyerManagement.QualifiedBuyerCodesQueryHelper_SchedulingAuction/AuctionUI.SchedulingAuction != empty`
   ➔ **If [true]:**
      1. **Retrieve related **QualifiedBuyerCodesQueryHelper_SchedulingAuction** via Association from **$QualifiedBuyerCodesQueryHelper** (Result: **$SchedulingAuction**)**
      2. **DB Retrieve **EcoATM_BuyerManagement.QualifiedBuyerCodes** Filter: `[ ( EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction = $SchedulingAuction ) ]` (Result: **$QualifiedBuyerCodesList**)**
      3. 🏁 **END:** Return `$QualifiedBuyerCodesList`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.