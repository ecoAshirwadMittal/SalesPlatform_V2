# Microflow Detailed Specification: DS_RetrieveRoundTwoBuyers

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **Find** on **$undefined** where `2` (Result: **$SchedulingAuction**)**
3. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
   ➔ **If [true]:**
      1. **Retrieve related **SchedulingAuction_QualifiedBuyers** via Association from **$SchedulingAuction** (Result: **$BuyerCodeList_Round2**)**
      2. 🏁 **END:** Return `$BuyerCodeList_Round2`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.