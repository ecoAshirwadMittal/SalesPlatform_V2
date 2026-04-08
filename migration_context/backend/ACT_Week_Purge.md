# Microflow Detailed Specification: ACT_Week_Purge

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. **Show Message (Warning): `Action can be processed because the corresponding feature flag [PurgeWeek] is off.`**
      3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.Auction_Week=$Week] [AuctionStatus!='Closed']` (Result: **$AuctionList**)**
      3. 🔀 **DECISION:** `$AuctionList=empty`
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.SUB_Purge_Auction****
            2. **Call Microflow **AuctionUI.ACT_Week_DeleteByAdmin****
            3. **Call Microflow **Custom_Logging.SUB_Log_Info****
            4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Show Message (Warning): `This week can't be deleted because related unclosed auctions exists.`**
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.