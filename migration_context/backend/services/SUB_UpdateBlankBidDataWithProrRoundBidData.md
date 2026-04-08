# Microflow Detailed Specification: SUB_UpdateBlankBidDataWithProrRoundBidData

### 📥 Inputs (Parameters)
- **$BidData_PriorRound** (Type: AuctionUI.BidData)
- **$CurrentRound_BidDataList** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. 🔄 **LOOP:** For each **$IteratorPriorRoundBidData** in **$BidData_PriorRound**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorPriorRoundBidData/AuctionUI.BidData_AggregatedInventory` (Result: **$BidData_CurrentRound**)**
   │ 2. 🔀 **DECISION:** `$BidData_CurrentRound != empty`
   │    ➔ **If [true]:**
   │       1. **Update **$BidData_CurrentRound**
      - Set **BidAmount** = `$IteratorPriorRoundBidData/BidAmount`
      - Set **BidQuantity** = `$IteratorPriorRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$IteratorPriorRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$IteratorPriorRoundBidData/BidQuantity`**
   │    ➔ **If [false]:**
   └─ **End Loop**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.