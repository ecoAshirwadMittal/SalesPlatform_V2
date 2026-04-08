# Microflow Detailed Specification: SUB_GetCurrentWeek

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetMostRecentAuction** (Result: **$Auction**)**
2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
3. 🔀 **DECISION:** `$Week!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Week`
   ➔ **If [false]:**
      1. **Create Variable **$week** = `parseInteger(formatDateTime([%CurrentDateTime%],'w'))`**
      2. **Create Variable **$year** = `parseInteger(formatDateTime([%CurrentDateTime%],'YYYY'))`**
      3. **DB Retrieve **EcoATM_MDM.Week** Filter: `[(WeekNumber = $week and Year = $year)]` (Result: **$DefaultCurrentWeek**)**
      4. 🏁 **END:** Return `$DefaultCurrentWeek`

**Final Result:** This process concludes by returning a [Object] value.