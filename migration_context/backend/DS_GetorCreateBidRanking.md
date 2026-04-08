# Microflow Detailed Specification: DS_GetorCreateBidRanking

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidRanking**  (Result: **$BidRanking**)**
2. 🔀 **DECISION:** `$BidRanking!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BidRanking`
   ➔ **If [false]:**
      1. **Create **AuctionUI.BidRanking** (Result: **$NewBidRanking**)**
      2. 🏁 **END:** Return `$NewBidRanking`

**Final Result:** This process concludes by returning a [Object] value.