# Microflow Detailed Specification: ACT_GetBidReportsOverview

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$week** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`**
2. **Create Variable **$year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`**
3. **DB Retrieve **AuctionUI.Week** Filter: `[(WeekNumber = $week and Year = $year)]` (Result: **$ExistingWeek**)**
4. 🔀 **DECISION:** `$ExistingWeek = empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.Week** (Result: **$CurrentWeek**)
      - Set **WeekNumber** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`
      - Set **WeekStartDateTime** = `[%BeginOfCurrentWeek%]`
      - Set **WeekEndDateTime** = `[%EndOfCurrentWeek%]`
      - Set **WeekDisplay** = `formatDateTime([%CurrentDateTime%],'yyyy') + ' / WK' + formatDateTime([%CurrentDateTime%],'ww')`
      - Set **Year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`
      - Set **WeekDisplayShort** = `'WK' + formatDateTime([%CurrentDateTime%],'ww')`**
      2. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_NewWeek**)
      - Set **HasAuction** = `false`
      - Set **HasInventory** = `false`
      - Set **InventoryOverview_Helper_Week** = `$CurrentWeek`
      - Set **WasTitleEmpty** = `false`
      - Set **HasAuctionBeenTriggered** = `false`**
      3. **Maps to Page: **AuctionUI.Inventory_Overview****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create Variable **$HasAuction** = `$ExistingWeek/AuctionUI.Auction_Week/AuctionUI.Auction != empty`**
      2. **Retrieve related **Inventory_Week** via Association from **$ExistingWeek** (Result: **$InventoryList**)**
      3. **AggregateList**
      4. **Create Variable **$HasInventory** = `$InventoryListCount > 0`**
      5. **Retrieve related **InventoryOverview_Helper_Week** via Association from **$ExistingWeek** (Result: **$InventoryOverview_HelperList**)**
      6. 🔀 **DECISION:** `$InventoryOverview_HelperList != empty`
         ➔ **If [false]:**
            1. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_CurrentWeek**)
      - Set **HasAuction** = `$HasAuction`
      - Set **HasInventory** = `$HasInventory`
      - Set **InventoryOverview_Helper_Week** = `$ExistingWeek`
      - Set **HasAuctionBeenTriggered** = `false`
      - Set **WasTitleEmpty** = `false`**
            2. 🔀 **DECISION:** `$HasInventory`
               ➔ **If [false]:**
                  1. **Call Microflow **AuctionUI.ACT_StartInventoryUpload****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Maps to Page: **AuctionUI.Inventory_Overview****
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **List Operation: **Head** on **$undefined** (Result: **$NewInventoryOverview_Helper**)**
            2. 🔀 **DECISION:** `$HasInventory`
               ➔ **If [false]:**
                  1. **Call Microflow **AuctionUI.ACT_StartInventoryUpload****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Create **AuctionUI.InventoryOverview_Helper** (Result: **$InventoryOverview_Helper_CurrentWeek_1**)
      - Set **HasAuction** = `$NewInventoryOverview_Helper/HasAuction`
      - Set **HasInventory** = `$NewInventoryOverview_Helper/HasInventory`
      - Set **WasTitleEmpty** = `$NewInventoryOverview_Helper/WasTitleEmpty`
      - Set **InventoryOverview_Helper_Week** = `$NewInventoryOverview_Helper/AuctionUI.InventoryOverview_Helper_Week`
      - Set **HasAuctionBeenTriggered** = `$NewInventoryOverview_Helper/HasAuctionBeenTriggered`**
                  2. **Maps to Page: **AuctionUI.Inventory_Overview****
                  3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.