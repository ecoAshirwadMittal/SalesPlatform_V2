# Microflow Detailed Specification: SUB_Auction_CleanUp

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekEndDateTime<'[%CurrentDateTime%]'] [AuctionUI.Auction_Week/AuctionUI.Auction/AuctionStatus='Closed']` (Result: **$WeekList**)**
      3. 🔄 **LOOP:** For each **$IteratorWeek** in **$WeekList**
         │ 1. **Call Microflow **AuctionUI.SUB_Purge_Auction****
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.