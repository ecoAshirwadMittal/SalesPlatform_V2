# Microflow Detailed Specification: ACT_SaveBidData_deprecated

### 📥 Inputs (Parameters)
- **$BidData_Helper** (Type: AuctionUI.BidData_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BidData_Helper_BidRound** via Association from **$BidData_Helper** (Result: **$BidRound**)**
2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound=$BidRound and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/ecoID=$BidData_Helper/EcoID and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Merged_Grade=$BidData_Helper/ecoGrade ]` (Result: **$BidData**)**
3. 🔀 **DECISION:** `$BidData=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BidData** (Result: **$NewBidData**)
      - Set **EcoID** = `$BidData_Helper/EcoID`
      - Set **BidQuantity** = `$BidData_Helper/BidQuantity`
      - Set **BidData_AggregatedInventory** = `$BidData_Helper/AuctionUI.BidData_Helper_AggregatedInventory`
      - Set **BidAmount** = `$BidData_Helper/BidAmount`
      - Set **BidData_BidRound** = `$BidRound`**
      2. **ValidationFeedback**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BidData** (and Save to DB)
      - Set **BidQuantity** = `$BidData_Helper/BidQuantity`
      - Set **BidAmount** = `$BidData_Helper/BidAmount`**
      2. **ValidationFeedback**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.