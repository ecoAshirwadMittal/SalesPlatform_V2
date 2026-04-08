# Microflow Detailed Specification: ACT_UnscheduleAuction

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$CurrentWeek**)**
2. **Retrieve related **AggregatedInventory_Week** via Association from **$CurrentWeek** (Result: **$AggregatedInventoryList**)**
3. **Retrieve related **Auction_Week** via Association from **$CurrentWeek** (Result: **$Auction**)**
4. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/HasRound = true and $currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Started` (Result: **$StartedSchedulingAuctionList**)**
6. 🔀 **DECISION:** `$StartedSchedulingAuctionList = empty`
   ➔ **If [true]:**
      1. **Update **$Auction** (and Save to DB)
      - Set **AuctionStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`**
      2. 🔄 **LOOP:** For each **$IteratorSchedulingAuction** in **$SchedulingAuctionList**
         │ 1. **Update **$IteratorSchedulingAuction**
      - Set **RoundStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`**
         └─ **End Loop**
      3. **Commit/Save **$SchedulingAuctionList** to Database**
      4. **Update **$AggInventoryHelper**
      - Set **HasAuction** = `$Auction!=empty`
      - Set **HasInventory** = `$AggregatedInventoryList!=empty`
      - Set **HasSchedule** = `$StartedSchedulingAuctionList!=empty`
      - Set **HasAuctionBeenTriggered** = `$Auction!=empty`**
      5. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
      6. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
            2. **Maps to Page: **AuctionUI.Inventory_Auction_Overview****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Maps to Page: **AuctionUI.Inventory_Auction_Overview****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Auction has started. Unscheduling the auction is not available.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.