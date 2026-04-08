# Microflow Detailed Specification: DS_GetRoundThreeBuyers

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **Find** on **$undefined** where `3` (Result: **$SchedulingAuction**)**
3. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
   ➔ **If [true]:**
      1. **Retrieve related **SchedulingAuction_QualifiedBuyers** via Association from **$SchedulingAuction** (Result: **$BuyerCodeList_Round3**)**
      2. 🏁 **END:** Return `$BuyerCodeList_Round3`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.