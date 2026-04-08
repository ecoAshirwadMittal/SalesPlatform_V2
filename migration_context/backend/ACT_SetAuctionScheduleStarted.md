# Microflow Detailed Specification: ACT_SetAuctionScheduleStarted

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[(RoundStatus = 'Scheduled')]` (Result: **$SchedulingAuctionList_ToBeStarted**)**
2. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeZoneOffset**)**
3. **Create Variable **$Current_DT** = `subtractHours([%CurrentDateTime%],$TimeZoneOffset)`**
4. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
5. 🔄 **LOOP:** For each **$IteratorSchedulingAuction** in **$SchedulingAuctionList_ToBeStarted**
   │ 1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Start_DateTime <= $Current_DT`
   │    ➔ **If [true]:**
   │       1. **LogMessage**
   │       2. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round=3`
   │          ➔ **If [false]:**
   │             1. **Update **$IteratorSchedulingAuction**
      - Set **RoundStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Started`**
   │             2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
   │                ➔ **If [true]:**
   │                   1. **Retrieve related **SchedulingAuction_Auction** via Association from **$IteratorSchedulingAuction** (Result: **$Auction**)**
   │                   2. **Call Microflow **AuctionUI.SUB_SetAuctionStatus****
   │                   3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
   │                   4. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
   │                   5. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round=1`
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **AuctionUI.SUB_InitializeRound1****
   │                         2. **DB Retrieve **AuctionUI.AllBidsZipped**  (Result: **$AllBidsZippedList**)**
   │                         3. **Delete**
   │                         4. **DB Retrieve **AuctionUI.AllBidDownload_ScreenHelper**  (Result: **$AllBidDownload_ScreenHelper**)**
   │                         5. **Delete**
   │                         6. **Create **AuctionUI.AllBidDownload_ScreenHelper** (Result: **$NewAllBidDownload_ScreenHelper**)
      - Set **R1Caption** = `empty`
      - Set **R2Caption** = `empty`
      - Set **UpsellCaption** = `empty`**
   │                      ➔ **If [false]:**
   │                         1. **Call Microflow **AuctionUI.ACT_ChangeSavedBidsToPreviouslySubmitted****
   │                         2. **Call Microflow **AuctionUI.ACT_CalculateTargetPrice****
   │                         3. **Call Microflow **AuctionUI.Sub_ProcessSpecialBuyers****
   │                         4. **Call Microflow **AuctionUI.SUB_AssignRoundTwoBuyers****
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round=1`
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **AuctionUI.SUB_InitializeRound1****
   │                         2. **DB Retrieve **AuctionUI.AllBidsZipped**  (Result: **$AllBidsZippedList**)**
   │                         3. **Delete**
   │                         4. **DB Retrieve **AuctionUI.AllBidDownload_ScreenHelper**  (Result: **$AllBidDownload_ScreenHelper**)**
   │                         5. **Delete**
   │                         6. **Create **AuctionUI.AllBidDownload_ScreenHelper** (Result: **$NewAllBidDownload_ScreenHelper**)
      - Set **R1Caption** = `empty`
      - Set **R2Caption** = `empty`
      - Set **UpsellCaption** = `empty`**
   │                      ➔ **If [false]:**
   │                         1. **Call Microflow **AuctionUI.ACT_ChangeSavedBidsToPreviouslySubmitted****
   │                         2. **Call Microflow **AuctionUI.ACT_CalculateTargetPrice****
   │                         3. **Call Microflow **AuctionUI.Sub_ProcessSpecialBuyers****
   │                         4. **Call Microflow **AuctionUI.SUB_AssignRoundTwoBuyers****
   │          ➔ **If [true]:**
   │             1. **Call Microflow **AuctionUI.ACT_Round3_SetStarted****
   │    ➔ **If [false]:**
   └─ **End Loop**
6. **Commit/Save **$SchedulingAuctionList_ToBeStarted** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.