# Microflow Detailed Specification: ACT_Open_Round_3_Selection_Criteria

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round = 3]` (Result: **$BidRoundSelectionFilter**)**
2. 🔀 **DECISION:** `$BidRoundSelectionFilter != empty`
   ➔ **If [true]:**
      1. **Maps to Page: **AuctionUI.PG_Round3Criteria****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **AuctionUI.BidRoundSelectionFilter** (Result: **$NewBidRoundSelectionFilter**)
      - Set **Round** = `3`**
      2. **Maps to Page: **AuctionUI.PG_Round3Criteria****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.