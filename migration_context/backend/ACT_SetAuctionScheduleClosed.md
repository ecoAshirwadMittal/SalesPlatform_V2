# Microflow Detailed Specification: ACT_SetAuctionScheduleClosed

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeZoneOffset**)**
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[RoundStatus = 'Started']` (Result: **$SchedulingAuctionList_NotClosed**)**
3. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
4. 🔄 **LOOP:** For each **$IteratorSchedulingAuction** in **$SchedulingAuctionList_NotClosed**
   │ 1. **Create Variable **$End_DateTime** = `$IteratorSchedulingAuction/End_DateTime`**
   │ 2. **Create Variable **$CurrentDT** = `subtractHours([%CurrentDateTime%],$TimeZoneOffset)`**
   │ 3. 🔀 **DECISION:** `$End_DateTime < $CurrentDT and $IteratorSchedulingAuction/RoundStatus!=AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`
   │    ➔ **If [true]:**
   │       1. **LogMessage**
   │       2. **Update **$IteratorSchedulingAuction** (and Save to DB)
      - Set **RoundStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Closed`**
   │       3. **LogMessage**
   │       4. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
   │          ➔ **If [true]:**
   │             1. **Retrieve related **SchedulingAuction_Auction** via Association from **$IteratorSchedulingAuction** (Result: **$Auction**)**
   │             2. **Call Microflow **AuctionUI.SUB_SetAuctionStatus****
   │             3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
   │             4. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
   │             5. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round = 1`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round = 2`
   │                      ➔ **If [false]:**
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation_TryCatch** (Result: **$Variable**)**
   │                         2. **Call Microflow **AuctionUI.SUB_Round3_PreProcessRoundData****
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **AuctionUI.SUB_InitializeRound2****
   │                   2. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation_TryCatch** (Result: **$Variable**)**
   │                   3. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round = 2`
   │                      ➔ **If [false]:**
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation_TryCatch** (Result: **$Variable**)**
   │                         2. **Call Microflow **AuctionUI.SUB_Round3_PreProcessRoundData****
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round = 1`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round = 2`
   │                      ➔ **If [false]:**
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation_TryCatch** (Result: **$Variable**)**
   │                         2. **Call Microflow **AuctionUI.SUB_Round3_PreProcessRoundData****
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **AuctionUI.SUB_InitializeRound2****
   │                   2. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation_TryCatch** (Result: **$Variable**)**
   │                   3. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round = 2`
   │                      ➔ **If [false]:**
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **AuctionUI.ACT_TriggerBidRankingCalculation_TryCatch** (Result: **$Variable**)**
   │                         2. **Call Microflow **AuctionUI.SUB_Round3_PreProcessRoundData****
   │    ➔ **If [false]:**
   └─ **End Loop**
5. **Commit/Save **$SchedulingAuctionList_NotClosed** to Database**
6. **LogMessage**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.