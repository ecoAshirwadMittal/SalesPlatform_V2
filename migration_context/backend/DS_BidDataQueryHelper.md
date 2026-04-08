# Microflow Detailed Specification: DS_BidDataQueryHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BidDataQueryHelper_Session** via Association from **$currentSession** (Result: **$BidDataQueryHelperList**)**
2. 🔀 **DECISION:** `$BidDataQueryHelperList !=empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$BidDataQueryHelper**)**
      2. **Update **$BidDataQueryHelper**
      - Set **BidDataQueryHelper_SchedulingAuction** = `empty`
      - Set **BidDataQueryHelper_Auction** = `empty`
      - Set **BidDataQueryHelper_BuyerCode** = `empty`**
      3. 🏁 **END:** Return `$BidDataQueryHelper`
   ➔ **If [false]:**
      1. **Create **AuctionUI.BidDataQueryHelper** (Result: **$NewBidDataQueryHelper**)
      - Set **BidDataQueryHelper_Session** = `$currentSession`**
      2. 🏁 **END:** Return `$NewBidDataQueryHelper`

**Final Result:** This process concludes by returning a [Object] value.