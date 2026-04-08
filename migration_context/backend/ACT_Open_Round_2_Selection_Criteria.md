# Microflow Detailed Specification: ACT_Open_Round_2_Selection_Criteria

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round = 2]` (Result: **$BidRoundSelectionFilter**)**
2. 🔀 **DECISION:** `$BidRoundSelectionFilter != empty`
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.Round2DefaultCriteria****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **AuctionUI.BidRoundSelectionFilter** (Result: **$NewBidRoundSelectionFilter**)
      - Set **Round** = `2`**
      2. **Maps to Page: **AuctionUI.Round2DefaultCriteria****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.