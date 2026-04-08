# Microflow Detailed Specification: DS_GetOrCreateAggregatedInventoryHelper

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
2. **Retrieve related **AggInventoryHelper_Account** via Association from **$Account** (Result: **$AggInventoryHelper**)**
3. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
4. 🔀 **DECISION:** `$AggInventoryHelper!= empty`
   ➔ **If [true]:**
      1. **Update **$AggInventoryHelper**
      - Set **isTotalQuantityModified** = `if $BidDataTotalQuantityConfigList!=empty then true else false`**
      2. 🏁 **END:** Return `$AggInventoryHelper`
   ➔ **If [false]:**
      1. **Create **AuctionUI.AggInventoryHelper** (Result: **$NewAggInventoryHelper**)
      - Set **AggInventoryHelper_Account** = `$Account`
      - Set **isTotalQuantityModified** = `if $BidDataTotalQuantityConfigList!=empty then true else false`**
      2. 🏁 **END:** Return `$NewAggInventoryHelper`

**Final Result:** This process concludes by returning a [Object] value.