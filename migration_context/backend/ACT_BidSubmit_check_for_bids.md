# Microflow Detailed Specification: ACT_BidSubmit_check_for_bids

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList**)**
2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BidAmount != empty and $currentObject/BidAmount > 0` (Result: **$BidDataList_GreaterThanZero**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$Count > 0`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList_1**)**
      3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BidAmount != empty and $currentObject/BidAmount > 0` (Result: **$BidDataList_GreaterThanZero_1**)**
      4. **AggregateList**
      5. 🔀 **DECISION:** `$Count_Delay > 0`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.